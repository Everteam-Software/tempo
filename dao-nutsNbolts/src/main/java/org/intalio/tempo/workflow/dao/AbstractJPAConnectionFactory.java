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

import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;

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

    public AbstractJPAConnectionFactory(String entityManagerFactoryName, Map<String, Object> properties) {
        try {
            log.info("Factory:" + this.getClass().getName() + ": Using the following JPA properties:" + properties);
            checkClassLoader();
            initDatasourceIfNeeded(properties);
            factory = Persistence.createEntityManagerFactory(entityManagerFactoryName, properties);

            // the factory created can sometimes be null. Check against that
            boolean created = factory != null;
            if (!created)
                throw new RuntimeException("Factory not properly created");
        } catch (Exception e) {
            // prevent from going anywhere else without this configured properly
            throw new RuntimeException(e);
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

    /**
     * check if we are loading from spring, and if so, apply the workaround for
     * axis and spring class loading otherwise classes needed for dynamic
     * loading are not found
     */
    private void checkClassLoader() {
        try {
            SysPropApplicationContextLoader context = SpringInit.CONTEXT;
            if (context != null) {
                log.info("Using context file:" + context.getApplicationContextFile());
                Thread currentThread = Thread.currentThread();
                currentThread.setContextClassLoader(context.getClass().getClassLoader());
            }
        } catch (Error e) {
            log.debug("Not using spring context for classloading");
        }
    }

    public abstract Object openConnection();

}
