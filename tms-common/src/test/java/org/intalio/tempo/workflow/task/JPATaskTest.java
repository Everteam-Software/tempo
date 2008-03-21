package org.intalio.tempo.workflow.task;

import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import junit.framework.Assert;
import junit.framework.JUnit4TestAdapter;

import org.apache.openjpa.persistence.OpenJPAPersistence;
import org.apache.openjpa.persistence.OpenJPAQuery;
import org.apache.openjpa.persistence.jdbc.FetchMode;
import org.apache.openjpa.persistence.jdbc.JDBCFetchPlan;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.task.attachments.Attachment;
import org.intalio.tempo.workflow.task.attachments.AttachmentMetadata;
import org.intalio.tempo.workflow.task.xml.XmlTooling;
import org.intalio.tempo.workflow.util.TaskEquality;
import org.intalio.tempo.workflow.util.jpa.TaskFetcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class JPATaskTest {
    final static Logger _logger = LoggerFactory.getLogger(JPATaskTest.class);

    EntityManager em;
    EntityManagerFactory factory;
    EntityTransaction jpa;
    XmlTooling xml = new XmlTooling();

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(JPATaskTest.class);
    }

    @Before
    public void setUpEntityManager() throws Exception {
        Properties p = new Properties();
        p.load(this.getClass().getResourceAsStream("/jpa.properties"));
        System.getProperties().putAll(p);
        factory = Persistence.createEntityManagerFactory("org.intalio.tempo.tms", System.getProperties());
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
        // this is to prevent caching at the entity manager level
        em.clear();
    }

    @Test
    public void PATaskPersistence() throws Exception {

        String id = "My id" + System.currentTimeMillis();
        PATask task1 = new PATask(id, new URI("http://hellonico.net"), "processId", "soap", getXmlSampleDocument());
        task1.setDeadline(new Date());

        persist(task1);

        Query q = em.createNamedQuery(Task.FIND_BY_ID).setParameter(1, id);
        forceEagerFetching(q);
        PATask task2 = (PATask) (q.getResultList()).get(0);

        em.close();
        TaskEquality.isEqual(task1, task2);

        Assert.assertEquals(task1.getInputAsXmlString(), task2.getInputAsXmlString());
        Assert.assertEquals(task1.getInputAsXmlString(), task2.getInputAsXmlString());
    }

    @Test
    public void PIPATaskPersistence() throws Exception {
        String id = "id" + System.currentTimeMillis();
        PIPATask task1 = new PIPATask(id, new URI("http://hellonico.net"), new URI("http://hellonico.net"), new URI(
                "http://hellonico.net"), "initOperationSOAPAction");

        persist(task1);

        Query q = em.createNamedQuery(Task.FIND_BY_ID).setParameter(1, id);
        forceEagerFetching(q);

        PIPATask task2 = (PIPATask) (q.getResultList()).get(0);

        TaskEquality.isEqual(task1, task2);
    }

    @Test
    public void authorizeActionForUser() throws Exception {
        String id = "id" + System.currentTimeMillis();
        Notification task1 = new Notification(id, new URI("http://hellonico.net"), getXmlSampleDocument());
        task1.authorizeActionForUser("play", "niko");
        task1.authorizeActionForUser("go_home", "niko");
        task1.authorizeActionForUser("eat", "alex");
        task1.getRoleOwners().add("role1");
        task1.getUserOwners().add("user1");

        persist(task1);

        Query q = em.createNamedQuery(Task.FIND_BY_ID).setParameter(1, id);
        Notification task2 = (Notification) (q.getResultList()).get(0);
        TaskEquality.areTasksEquals(task1, task2);
    }

    private void forceEagerFetching(Query q) {
        OpenJPAQuery kq = OpenJPAPersistence.cast(q);
        JDBCFetchPlan fetch = (JDBCFetchPlan) kq.getFetchPlan();
        fetch.setEagerFetchMode(FetchMode.PARALLEL);
        fetch.setSubclassFetchMode(FetchMode.PARALLEL);
    }

    @Test
    public void NotificationPersistence() throws Exception {

        String id = "id" + System.currentTimeMillis();
        Notification task1 = new Notification(id, new URI("http://hellonico.net"), getXmlSampleDocument());

        persist(task1);

        Query q = em.createNamedQuery(Task.FIND_BY_ID).setParameter(1, id);
        forceEagerFetching(q);
        Notification task2 = (Notification) (q.getResultList()).get(0);

        TaskEquality.areTasksEquals(task1, task2);

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

        TaskEquality.areAttachmentsEqual(task1, task2);
    }
    
    @Test
    public void authorizeUserRoles() throws Exception {
        String id = "pa" + System.currentTimeMillis();
        PATask task1 = new PATask(id, new URI("http://hellonico.net"), "processId", "soap", getXmlSampleDocument());
        task1.authorizeActionForUser("save", "examples\\manager");
        task1.setPriority(2);
        persist(task1);

        Query q = em.createNamedQuery(Task.FIND_BY_ID).setParameter(1, id);
        PATask task2 = (PATask) (q.getResultList()).get(0);
        
        TaskEquality.areTasksEquals(task1, task2);

        em.close();
    }

    @Test
    public void searchQuery() throws Exception {
        TaskFetcher fetcher = new TaskFetcher(em);
        Assert.assertEquals(Notification.class, fetcher.fetchTasksForUser("user1")[0].getClass());
        Assert.assertEquals(Notification.class, fetcher.fetchTasksForRole("role1")[0].getClass());

        testFecthForUserRoles("user1", new String[] { "role2" }, 1);
        testFecthForUserRoles("user2", new String[] { "role1" }, 1);
        testFecthForUserRoles("user2", new String[] { "role2" }, 0);
        testFecthForUserRoles("user3", new String[] { "role3" }, 0);
    }

    private void testFecthForUserRoles(String userId, String[] roles, int size) throws Exception {
        Task[] tasks = new TaskFetcher(em).fetchAllAvailableTasks(new UserRoles(userId, roles));
        Assert.assertEquals(size, tasks.length);
    }

    private Document getXmlSampleDocument() throws Exception {
        return xml.getXmlDocument("/employees.xml");
    }

}
