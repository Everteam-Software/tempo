package org.intalio.tempo.workflow.tms.server.dao;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JPATaskDaoConnectionFactory implements ITaskDAOConnectionFactory {
    final static Logger log = LoggerFactory.getLogger(JPATaskDaoConnectionFactory.class);
    EntityManagerFactory factory;

    public JPATaskDaoConnectionFactory(String jndiPath) {
        Properties sysProp = System.getProperties();
        sysProp.put("jta-data-source", jndiPath);
        log.info("Setting datasource to:"+jndiPath);
        factory = Persistence.createEntityManagerFactory("org.intalio.tempo", sysProp);
    }
    
//
//    public JPATaskDaoConnectionFactory() {
//        try {
//            Properties p = new Properties();
//            p.load(this.getClass().getResourceAsStream("/jpa-properties.txt"));
//            System.getProperties().putAll(p);
//        } catch (Exception e) {
//            log.info("Properties not found.");
//        }
//        factory = Persistence.createEntityManagerFactory("org.intalio.tempo", System.getProperties());
//    }

    public ITaskDAOConnection openConnection() {
        return new JPATaskDaoConnection(factory.createEntityManager());
    }

    @Override
    protected void finalize() throws Throwable {
        factory.close();
        super.finalize();
    }

}
