package org.intalio.tempo.workflow.tms.server.dao;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;

public class JPATaskDaoConnectionFactory implements ITaskDAOConnectionFactory {
    final static Logger log = Logger.getLogger(JPATaskDaoConnectionFactory.class);
    EntityManagerFactory factory;    
    
    public JPATaskDaoConnectionFactory() {
        Properties p = new Properties();
        try {
            p.load(this.getClass().getResourceAsStream("/jpa-properties.txt"));
        }
        catch (Exception e) {
            log.info("Properties not found.");
        }
        System.getProperties().putAll(p);
        factory = Persistence.createEntityManagerFactory("org.intalio.tempo", System.getProperties());
    }

    public ITaskDAOConnection openConnection() {
        return new JPATaskDaoConnection(factory.createEntityManager());
    }

    @Override
    protected void finalize() throws Throwable {
        factory.close();
        super.finalize();
    }

}
