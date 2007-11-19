package org.intalio.tempo.workflow.wds.core.tms;

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.intalio.tempo.workflow.wds.core.Item;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class PipaJpaTest {
    final static Logger log = Logger.getLogger(PipaJpaTest.class);

    EntityManager em;
    EntityManagerFactory factory;
    EntityTransaction jpa;


    @Before
    public void setUpEntityManager() throws Exception {
        Properties p = new Properties();
        p.load(this.getClass().getResourceAsStream("/jpa.properties"));
        System.getProperties().putAll(p);
        factory = Persistence.createEntityManagerFactory("org.intalio.tempo", System.getProperties());
        em = factory.createEntityManager();
        jpa = em.getTransaction();
        jpa.begin();
    }

    @After
    public void closeFactory() throws Exception {
        try {
            em.close();
        } catch (Exception e) {
        }
        try {
            factory.close();
        } catch (Exception e) {
        }
    }

    private void persist(Object o) throws Exception {
        em.persist(o);
        jpa.commit();
        em.clear();
    }
    
    @Test
    public void storeAPipaAndRetrieveIt() throws Exception {
        PipaTask task1 = new PipaTask();
        task1.setId("abc");
        task1.setFormNamespace("urn:ns");
        task1.setFormURL("http://localhost/");
        task1.setProcessEndpoint("http://localhost/process");
        task1.setInitSoapAction("initProcess");
        
        persist(task1);
        
        Query q = em.createNamedQuery(PipaTask.FIND_BY_ID).setParameter(1, "abc");
        PipaTask task2 = (PipaTask)(q.getResultList().get(0));
        Assert.assertEquals(task1,task2);
    }
    
    @Test 
    public void storeAnItemAndRetrieveIt() throws Exception {
        Item i1 = new Item("http://hellonico.net", "meta", new byte[]{1,2,3});
        persist(i1);

        Query q = em.createNamedQuery(Item.FIND_BY_URI).setParameter(1, "http://hellonico.net");
        Item i2 = (Item)(q.getResultList().get(0));
        Assert.assertEquals(i1,i2);
    }
}
