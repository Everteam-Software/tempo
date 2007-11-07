package org.intalio.tempo.jpa;

import java.net.URI;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.intalio.tempo.workflow.task.PATask;
import org.intalio.tempo.workflow.task.xml.XmlTooling;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

public class JPATaskTest {
    final static Logger log = Logger.getLogger(JPATaskTest.class);

    EntityManager em;
    EntityManagerFactory factory;
    XmlTooling xml = new XmlTooling();

    @Before
    public void setUpEntityManager() throws Exception {
        Properties p = new Properties();
        p.load(this.getClass().getResourceAsStream("/jpa-properties.txt"));
        System.getProperties().putAll(p);
        factory = Persistence.createEntityManagerFactory("org.intalio.tempo", System.getProperties());
        em = factory.createEntityManager();
    }

    @After
    public void closeFactory() throws Exception {
        factory.close();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testBasicPATaskPersistence() throws Exception {
        em.getTransaction().begin();

        String id = "My id" + System.currentTimeMillis();
        PATask task1 = new PATask(id, new URI("http://hellonico.net"), "processId", "soap", getXmlSampleDocument());
        task1.setOutput(getXmlSampleDocument());

        em.persist(task1);
        em.getTransaction().commit();

        Query q = em.createQuery("select m from PATask m");
        for (PATask m : (List<PATask>) q.getResultList()) {
            log.info(m.toString() + " (created on: " + m.getCreationDate() + ")");
        }

        PATask task2 = (PATask) em.find(PATask.class, id);
        em.close();

        Assert.assertEquals(task1, task2);
        Assert.assertEquals(task1.getInput(), task2.getInput());
        Assert.assertEquals(task1.getOutput(), task2.getOutput());
    }

    private Document getXmlSampleDocument() throws Exception {
        return xml.parseXml(getClass().getResourceAsStream("/employees.xml"));
    }

}
