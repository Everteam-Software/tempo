package org.intalio.tempo.workflow.wds.core;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;

public class JpaItemDaoConnectionFactory implements ItemDaoConnectionFactory {

    final static Logger log = Logger.getLogger(JpaItemDaoConnectionFactory.class);
    EntityManagerFactory factory;    
    
    public JpaItemDaoConnectionFactory() {
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

    @Override
    protected void finalize() throws Throwable {
        factory.close();
        super.finalize();
    }
    
    public ItemDaoConnection getItemDaoConnection() {
        return new JpaItemDaoConnection(factory.createEntityManager());
    }

}
