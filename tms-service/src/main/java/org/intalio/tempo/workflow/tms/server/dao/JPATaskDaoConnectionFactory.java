package org.intalio.tempo.workflow.tms.server.dao;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.intalio.tempo.workflow.SpringInit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JPATaskDaoConnectionFactory implements ITaskDAOConnectionFactory {
    final static Logger log = LoggerFactory.getLogger(JPATaskDaoConnectionFactory.class);
    EntityManagerFactory factory;

    public JPATaskDaoConnectionFactory(Map properties) {
        log.info("Using the following JPA properties:"+properties);
        Thread.currentThread().setContextClassLoader(SpringInit.CONTEXT.getClass().getClassLoader());
        factory = Persistence.createEntityManagerFactory("org.intalio.tempo", properties);
        log.info("Factory was properly created:"+(factory!=null));
    }
    
    public ITaskDAOConnection openConnection() {
        log.info("Opening a new JPA connection");
        try {
            JPATaskDaoConnection taskDaoConnection = new JPATaskDaoConnection(factory.createEntityManager());
            log.info(taskDaoConnection.getClass().getName());
            return taskDaoConnection;
        } catch (Exception e) {
            log.error("Error while opening connection",e);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        factory.close();
        super.finalize();
    }

}
