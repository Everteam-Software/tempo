/**
 * Copyright (c) 2005-2007 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 */
package org.intalio.tempo.workflow.dao;

import java.io.IOException;
import java.sql.Connection;
import java.util.Map;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;

import org.apache.openjpa.persistence.OpenJPAEntityManager;
import org.apache.openjpa.persistence.OpenJPAPersistence;
import org.intalio.tempo.web.SysPropApplicationContextLoader;
import org.intalio.tempo.workflow.SpringInit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common class for making JPA-based connection factories. Also takes care of
 * setting the proper spring context when loading in an axis environment
 */
public abstract class AbstractJPAConnectionFactory {

    final static Logger log = LoggerFactory.getLogger(AbstractJPAConnectionFactory.class);
    protected EntityManagerFactory factory;

    final static String JPA_FILE = "jpa.config.file";
    final static String JPA_DEFAULT_FILE = "jpa.properties";

    /**
     * Load the factorry using properties found in a jpa config file
     * @param entityManagerFactoryName
     * @throws IOException
     */
    public AbstractJPAConnectionFactory(String entityManagerFactoryName) {
        Properties map = new Properties();
        String jpaFile = System.getProperty(JPA_FILE);
        if(jpaFile==null) jpaFile =JPA_DEFAULT_FILE;
        log.info("Using jpa config file:"+jpaFile);
        try {
            map.load(AbstractJPAConnectionFactory.class.getResourceAsStream("/"+jpaFile));    
        } catch (Exception e) {
            throw new RuntimeException("Could not load jpa properies from file:"+jpaFile);
        }
        initEM(entityManagerFactoryName, map);
    }
    
    public AbstractJPAConnectionFactory(String entityManagerFactoryName, Map<String, Object> properties) {
        Thread thread = Thread.currentThread();
        ClassLoader oldClassLoader = thread.getContextClassLoader();
        String serviceName = (String) properties.get("serviceName");
        SysPropApplicationContextLoader context = SpringInit.CONTEXT_MAP.get(serviceName);
        try {
            // check if we are loading from spring, and if so, apply the workaround for
            // Axis and Spring class loading otherwise classes needed for dynamic loading are not found
            if (context != null) {
                log.info("Using context file:" + context.getApplicationContextFile());
                thread.setContextClassLoader(context.getClass().getClassLoader());
            }

            initDatasourceIfNeeded(properties);
            initEM(entityManagerFactoryName, properties);
        } catch (Exception e) {
            // prevent from going anywhere else without this configured properly
            throw new RuntimeException(e);
        } finally {
            if (context != null) {    
                thread.setContextClassLoader(oldClassLoader);
            }
        }
    }

    private void initEM(String entityManagerFactoryName, Map properties) {
        log.info("Factory:" + this.getClass().getName() + ": Using the following JPA properties:" + properties);
        factory = Persistence.createEntityManagerFactory(entityManagerFactoryName, properties);

        // the factory created can sometimes be null. Check against that
        if (factory == null)
            throw new RuntimeException("Factory not properly created");
    }
    
    public Connection getUnderlyingJDBCConnectionFromEntityManager() {
        OpenJPAEntityManager kem = OpenJPAPersistence.cast(factory.createEntityManager());
        Connection conn = (Connection) kem.getConnection();
        //conn.close();
        return conn;
    }
    
    /**
     * Clear the cache by accessing the cache that is associated
     * with the entity manager factory. 
     */
    public void clearCache(){
    	if(factory != null){
    		// evict everything from the cache 
    		factory.getCache().evictAll();  
    		log.debug("Cache cleared");    	
    	}
    }

    private void initDatasourceIfNeeded(Map<String,Object> properties) {
        try {
            if(properties.containsKey("tempo.datasourceURL")) {
                InitialContext initialContext = new InitialContext();
                String jndiPath = (String)properties.get("tempo.datasourceURL");
                if(log.isDebugEnabled()) log.debug("About to get hook for DataSource " + jndiPath);
                DataSource _dataSource = (DataSource) initialContext.lookup(jndiPath);
                properties.put("openjpa.ConnectionFactory", _dataSource);
            }    
        } catch (NamingException ne) {
            throw new IllegalStateException("Couldn't find datasource through jndi");
        }
        
    }

    public abstract Object openConnection();

}
