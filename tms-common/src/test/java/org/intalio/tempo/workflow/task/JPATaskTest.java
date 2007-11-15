package org.intalio.tempo.workflow.task;

import java.net.URI;
import java.net.URL;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.intalio.tempo.workflow.auth.AuthIdentifierSet;
import org.intalio.tempo.workflow.task.attachments.Attachment;
import org.intalio.tempo.workflow.task.attachments.AttachmentMetadata;
import org.intalio.tempo.workflow.task.xml.XmlTooling;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

public class JPATaskTest {
    final static Logger log = Logger.getLogger(JPATaskTest.class);

    EntityManager em;
    EntityManagerFactory factory;
    EntityTransaction jpa;

    XmlTooling xml = new XmlTooling();

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

    @SuppressWarnings("unchecked")
    @Test
    public void testBasicPATaskPersistence() throws Exception {

        String id = "My id" + System.currentTimeMillis();
        PATask task1 = new PATask(id, new URI("http://hellonico.net"), "processId", "soap", getXmlSampleDocument());

        persist(task1);

        Query q = em.createNamedQuery(Task.FIND_BY_ID).setParameter(1, id);
        PATask task2 = (PATask) (q.getResultList()).get(0);

        em.close();

        Assert.assertEquals(task1.getInputAsXmlString(), task2.getInputAsXmlString());
        Assert.assertEquals(task1.getInputAsXmlString(), task2.getInputAsXmlString());
    }

    @Test
    public void testBasicPIPATaskPersistence() throws Exception {
        String id = "id" + System.currentTimeMillis();
        PIPATask task1 = new PIPATask(id, new URI("http://hellonico.net"), new URI("http://hellonico.net"), new URI(
                "http://hellonico.net"), "initOperationSOAPAction");

        persist(task1);

        Query q = em.createNamedQuery(Task.FIND_BY_ID).setParameter(1, id);
        PIPATask task2 = (PIPATask) (q.getResultList()).get(0);

        Assert.assertEquals(task1, task2);
    }

    @Test
    public void testBasicNotificationPersistence() throws Exception {

        String id = "id" + System.currentTimeMillis();
        Notification task1 = new Notification(id, new URI("http://hellonico.net"), getXmlSampleDocument());

        persist(task1);

        Query q = em.createNamedQuery(Task.FIND_BY_ID).setParameter(1, id);
        Notification task2 = (Notification) (q.getResultList()).get(0);

        Assert.assertEquals(task1, task2);

        em.close();
    }

    @Test
    public void authorizeActionForUser() throws Exception {
        String id = "id" + System.currentTimeMillis();
        Notification task1 = new Notification(id, new URI("http://hellonico.net"), getXmlSampleDocument());
        task1.authorizeActionForUser("play", "niko");
        task1.authorizeActionForUser("go_home", "niko");
        task1.authorizeActionForUser("eat", "alex");
        log.info(task1.getAuthorizedUsers("play").toString());

        persist(task1);

        Query q = em.createNamedQuery(Task.FIND_BY_ID).setParameter(1, id);
        Notification task2 = (Notification) (q.getResultList()).get(0);

        AuthIdentifierSet players = task2.getAuthorizedUsers("play");
        log.info(players.toString());
        Assert.assertTrue(players.contains("niko"));
        Assert.assertFalse(players.contains("alex"));

        AuthIdentifierSet homers = task2.getAuthorizedUsers("go_home");
        Assert.assertTrue(homers.contains("niko"));
        Assert.assertFalse(homers.contains("alex"));

        AuthIdentifierSet eaters = task2.getAuthorizedUsers("eat");
        Assert.assertFalse(eaters.contains("niko"));
        Assert.assertTrue(eaters.contains("alex"));

        em.close();
    }

    @Test
    public void attachmentsPATask() throws Exception {
        AttachmentMetadata metadata = new AttachmentMetadata();
        Attachment att = new Attachment(metadata, new URL("http://hellonico.net"));
        String id = "pa" + System.currentTimeMillis();
        PATask task1 = new PATask(id, new URI("http://hellonico.net"), "processId", "soap", getXmlSampleDocument());
        task1.addAttachment(att);

        persist(task1);

        Query q = em.createNamedQuery(Task.FIND_BY_ID).setParameter(1, id);
        PATask task2 = (PATask) (q.getResultList()).get(0);

        Assert.assertEquals(task1, task2);
        Assert.assertEquals(1, task2.getAttachments().size());

    }

    private Document getXmlSampleDocument() throws Exception {
        return xml.parseXml(getClass().getResourceAsStream("/employees.xml"));
    }

}
