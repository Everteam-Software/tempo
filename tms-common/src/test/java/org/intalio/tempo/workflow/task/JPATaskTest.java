package org.intalio.tempo.workflow.task;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.Properties;
import java.util.List;
import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import junit.framework.Assert;
import junit.framework.JUnit4TestAdapter;

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
    int uniqueID = 0;

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(JPATaskTest.class);
    }

    private synchronized String getUniqueTaskID() {
        uniqueID++;
        return "id_" + uniqueID;
    }

    @Before
    public void setUpEntityManager() throws Exception {
        Properties p = new Properties();
        p.load(this.getClass().getResourceAsStream("/jpa.properties"));
        System.getProperties().putAll(p);
        factory = Persistence.createEntityManagerFactory("org.intalio.tempo.tms", System.getProperties());
        em = factory.createEntityManager();
        jpa = em.getTransaction();
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
        jpa.begin();
        em.persist(o);
        jpa.commit();
        // this is to prevent caching at the entity manager level
        em.clear();
    }

    @Test
    public void faultyQueryWithOpenJPA1_2() throws Exception {

        String user = "niko";
        List<String> params = new ArrayList<String>();
        params.add(user);

        Task task1 = new Notification(getUniqueTaskID(), new URI("http://hellonico.net"), getXmlSampleDocument());
        task1.getUserOwners().add(user);
        persist(task1);
        em.clear(); // <--- this was causing problems as getSingleResult is
                    // optimized in openjpa12 and clear the entity manager
                    // somehow
        final TaskFetcher taskFetcher = new TaskFetcher(em);
        Notification task3 = (Notification) taskFetcher.fetchTaskIfExists(task1.getID());

        checkRemoved(task1);
    }

    @Test
    public void checkForDoubles() throws Exception {
        Task task1 = new Notification(getUniqueTaskID(), new URI("http://hellonico.net"), getXmlSampleDocument());
        task1.getRoleOwners().add("examples\\employee");
        task1.getRoleOwners().add("examples\\manager");
        persist(task1);

        final TaskFetcher taskFetcher = new TaskFetcher(em);
        UserRoles ur = new UserRoles("niko", new String[] { "examples\\employee", "examples\\manager" });
        Task[] list = taskFetcher.fetchAllAvailableTasks(ur);
        Assert.assertEquals(1, list.length);
    }

    @Test
    public void notificationOneRolePersist() throws Exception {
        Notification task1 = null, task2 = null;

        task1 = new Notification(getUniqueTaskID(), new URI("http://hellonico.net"), getXmlSampleDocument());
        task1.getRoleOwners().add("intalio\\manager");
        persist(task1);

        final TaskFetcher taskFetcher = new TaskFetcher(em);
        task2 = (Notification) taskFetcher.fetchTasksForRole("intalio\\manager")[0];
        TaskEquality.isEqual(task1, task2);

        checkRemoved(task2);
    }

    @Test
    public void notificationOneUserPersiste() throws Exception {
        Notification task1 = null, task2 = null;

        task1 = new Notification(getUniqueTaskID(), new URI("http://hellonico.net"), getXmlSampleDocument());
        task1.getUserOwners().add("intalio\\admin");
        persist(task1);

        final TaskFetcher taskFetcher = new TaskFetcher(em);
        task2 = (Notification) taskFetcher.fetchTasksForUser("intalio\\admin")[0];
        TaskEquality.isEqual(task1, task2);

        checkRemoved(task2);
    }

    @Test
    public void PIPATaskSearchFromURL() throws Exception {
        PIPATask task1 = new PIPATask(getUniqueTaskID(), new URI("http://hellonico2.net"), new URI("http://hellonico2.net"), new URI("http://hellonico2.net"),
                        "initOperationSOAPAction");
        persist(task1);
        Query q = em.createNamedQuery(PIPATask.FIND_BY_URL).setParameter(1, "http://hellonico2.net");
        PIPATask task2 = (PIPATask) (q.getSingleResult());
        TaskEquality.isEqual(task1, task2);
        checkRemoved(task2);
    }

    @Test
    public void PATaskPersistence() throws Exception {

        String id = getUniqueTaskID();
        PATask task1 = new PATask(id, new URI("http://hellonico.net"), "processId", "soap", getXmlSampleDocument());
        task1.setDeadline(new Date());

        persist(task1);

        Query q = em.createNamedQuery(Task.FIND_BY_ID).setParameter(1, id);
        PATask task2 = (PATask) (q.getResultList()).get(0);

        TaskEquality.isEqual(task1, task2);

        Assert.assertEquals(task1.getInputAsXmlString(), task2.getInputAsXmlString());
        Assert.assertEquals(task1.getInputAsXmlString(), task2.getInputAsXmlString());

        checkRemoved(task2);
    }

    @Test
    public void PIPATaskPersistence() throws Exception {
        String id = getUniqueTaskID();
        PIPATask task1 = new PIPATask(id, new URI("http://hellonico.net"), new URI("http://hellonico.net"), new URI("http://hellonico.net"),
                        "initOperationSOAPAction");

        persist(task1);

        Query q = em.createNamedQuery(Task.FIND_BY_ID).setParameter(1, id);

        PIPATask task2 = (PIPATask) (q.getResultList()).get(0);

        TaskEquality.isEqual(task1, task2);
        checkRemoved(task2);
    }

    /**
     * This tests that a user cannot get a task in his task list that is not
     * available to him use the  <code>isAvailableTo</code> method in the <code>Task</code> class.
     */
    @Test
    public void userWithStrangeLogin() throws Exception {
        String users[] = new String[] { "overdating\\pdv017@es\\japanesemaster\\com", "overdating\\pdv017@es.japanesemaster.com",
                        "overdating\\pdv017@es\\japanesemaster\\com ", "overdating\\pdv017@es.japanesemaster.com ", "nico", "alex", "assaf", "matthieu",
                        "nico " };
        for (String user : users)
            testLogins(user, users);
    }

    private void testLogins(String user, String[] users) throws URISyntaxException, Exception {
        String id = getUniqueTaskID();
        Notification task1 = null, task2 = null;
        task1 = new Notification(id, new URI("http://hellonico.net"), getXmlSampleDocument());
        task1.getUserOwners().add(user);
        persist(task1);

        for (String us : users) {
            _logger.info(user + ":" + us + ":");
            UserRoles ur = new UserRoles(us, new String[] {});
            Task[] tasks = new TaskFetcher(em).fetchAllAvailableTasks(ur);
            if (tasks.length > 0)
                Assert.assertEquals(tasks[0].isAvailableTo(ur), true);
        }

        checkRemoved(task1);
    }

    @Test
    public void authorizeActionForUser() throws Exception {
        String id = getUniqueTaskID();
        Notification task1 = null, task2 = null;

        task1 = new Notification(id, new URI("http://hellonico.net"), getXmlSampleDocument());
        task1.authorizeActionForUser("play", "niko");
        task1.authorizeActionForUser("go_home", "niko");
        task1.authorizeActionForUser("eat", "alex");
        task1.getRoleOwners().add("role1");
        task1.getUserOwners().add("user1");

        persist(task1);

        Query q = em.createNamedQuery(Task.FIND_BY_ID).setParameter(1, id);
        task2 = (Notification) (q.getResultList()).get(0);
        TaskEquality.areTasksEquals(task2, task1);

        TaskFetcher fetcher = new TaskFetcher(em);
        Assert.assertEquals(Notification.class, fetcher.fetchTasksForUser("user1")[0].getClass());
        Assert.assertEquals(Notification.class, fetcher.fetchTasksForRole("role1")[0].getClass());

        testFecthForUserRoles("user1", new String[] { "role2" }, 1);
        testFecthForUserRoles("user2", new String[] { "role1" }, 1);
        testFecthForUserRoles("user2", new String[] { "role2" }, 0);
        testFecthForUserRoles("user3", new String[] { "role3" }, 0);

        checkRemoved(task2);
    }

    @Test
    public void NotificationPersistence() throws Exception {

        String id = getUniqueTaskID();
        Notification task1 = new Notification(id, new URI("http://hellonico.net"), getXmlSampleDocument());

        persist(task1);

        Query q = em.createNamedQuery(Task.FIND_BY_ID).setParameter(1, id);
        Notification task2 = (Notification) (q.getResultList()).get(0);

        TaskEquality.areTasksEquals(task1, task2);

        checkRemoved(task2);
    }

    @Test
    public void PAWithInputOutput() throws Exception {
        PATask task2;
        PATask task1 = new PATask(getUniqueTaskID(), new URI("http://hellonico.net"), "processId", "soap", getXmlSampleDocument());
        task1.setInput(xml.getXmlDocument("/pa_input.xml"));
        task1.setOutput(xml.getXmlDocument("/pa_output.xml"));

        task1.getUserOwners().add("intalio\\admin");
        persist(task1);

        final TaskFetcher taskFetcher = new TaskFetcher(em);
        final UserRoles user = new UserRoles("intalio\\admin", new String[] { "examples\\manager", "examples\\employee" });
        task2 = (PATask) taskFetcher.fetchAllAvailableTasks(user)[0];
        TaskEquality.isEqual(task1, task2);

        checkRemoved(task2);

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

        checkRemoved(task2);
    }

    @Test
    public void authorizeUserRolesAndIsAvailable() throws Exception {
        String id = getUniqueTaskID();
        PATask task1 = new PATask(id, new URI("http://hellonico.net"), "processId", "soap", getXmlSampleDocument());
        task1.authorizeActionForUser("save", "examples\\manager");
        task1.authorizeActionForUser("save", "user1");
        task1.getUserOwners().add("user2");
        task1.getRoleOwners().add("role1");

        task1.setPriority(2);
        persist(task1);

        Query q = em.createNamedQuery(Task.FIND_BY_ID).setParameter(1, id);
        PATask task2 = (PATask) (q.getResultList()).get(0);

        TaskEquality.areTasksEquals(task1, task2);

        final UserRoles credentials = new UserRoles("user1", new String[] { "role1" });
        final UserRoles credentials2 = new UserRoles("user2", new String[] { "role2" });
        final UserRoles credentials3 = new UserRoles("user3", new String[] { "role3" });

        Assert.assertTrue(task2.isAvailableTo(credentials));
        Assert.assertTrue(task2.isAvailableTo(credentials2));
        Assert.assertFalse(task2.isAvailableTo(credentials3));

        Assert.assertTrue(task2.isAuthorizedAction(credentials, "save"));
        Assert.assertTrue(task2.isAuthorizedAction(credentials, "cook"));

        checkRemoved(task2);
    }

    @Test
    public void testFetchAvailabeTasksWithCriteriaPA() throws Exception {
        PATask task1 = getSampleTask(TaskState.FAILED);

        persist(task1);

        testFetchForUserRolesWithCriteria("user1", new String[] { "role1" }, PATask.class, "T._state = TaskState.FAILED", 1);
        testFetchForUserRolesWithCriteria("user1", new String[] { "role1" }, PATask.class, "T._state = TaskState.FAILED and T._priority = 2", 1);
        testFetchForUserRolesWithCriteria("user1", new String[] { "role1" }, PATask.class, "T._state = TaskState.FAILED and T._priority = 1", 0);
        testFetchForUserRolesWithCriteria("user1", new String[] { "role1" }, PATask.class, "T._state = TaskState.FAILED or T._priority = 1", 1);
        testFetchForUserRolesWithCriteria("user3", new String[] { "role3" }, PATask.class, "T._state = TaskState.FAILED", 0);
        testFetchForUserRolesWithCriteria("user1", new String[] { "role1" }, PATask.class, "T._state = TaskState.COMPLETED", 0);
        testFetchForUserRolesWithCriteria("user1", new String[] { "role1" }, Notification.class, null, 0);

        checkRemoved(task1.getID());
    }

    private PATask getSampleTask(TaskState state) throws URISyntaxException, Exception {
        String id = getUniqueTaskID();
        PATask task1 = new PATask(id, new URI("http://hellonico.net"), "processId", "soap", getXmlSampleDocument());
        task1.authorizeActionForUser("save", "examples\\manager");
        task1.setPriority(2);
        task1.setState(state);
        task1.getRoleOwners().add("role1");
        task1.getUserOwners().add("user1");
        return task1;
    }

    @Test
    public void testFetchWithCriteriaAndOrder() throws Exception {
        PATask task1 = getSampleTask(TaskState.READY);
        task1.setDescription("Ztarting with a Z");
        PATask task2 = getSampleTask(TaskState.CLAIMED);
        task2.setDescription("Arting with a A");
        PATask task3 = getSampleTask(TaskState.FAILED);

        persist(task1);
        persist(task2);
        persist(task3);

        String PA_QUERY_READY_OR_CLAIMED_ORDERED = "T._state = TaskState.READY or T._state = TaskState.CLAIMED ORDER BY t._description ASC";
        Task[] tasks = testFetchForUserRolesWithCriteria("user1", new String[] { "role1" }, PATask.class, PA_QUERY_READY_OR_CLAIMED_ORDERED, 2);
        // this way we can confirm the tasks are ordered
        Assert.assertTrue(tasks[0].getDescription().startsWith("A"));

        // check the query is working for other tasks as well
        String QUERY_READY_OR_CLAIMED_ORDERED = "ORDER BY t._creationDate ASC";
        testFetchForUserRolesWithCriteria("user1", new String[] { "role1" }, Task.class, QUERY_READY_OR_CLAIMED_ORDERED, 3);
        testFetchForUserRolesWithCriteria("user1", new String[] { "role1" }, Class.forName("org.intalio.tempo.workflow.task.Task"),
                        QUERY_READY_OR_CLAIMED_ORDERED, 3);

        checkRemoved(task1);
        checkRemoved(task2);
        checkRemoved(task3);
    }

    @Test
    public void testFetchAvailabeTasksWithCriteriaNOTI() throws Exception {
        String id = getUniqueTaskID();
        Notification task2 = new Notification(id, new URI("http://hellonico.net"), getXmlSampleDocument());
        task2.getRoleOwners().add("role1");
        task2.getUserOwners().add("user1");

        persist(task2);

        testFetchForUserRolesWithCriteria("user1", new String[] { "role1" }, Notification.class, null, 1);
        testFetchForUserRolesWithCriteria("user3", new String[] { "role3" }, Notification.class, null, 0);
        testFetchForUserRolesWithCriteria("user1", new String[] { "role1" }, PIPATask.class, null, 0);

        checkRemoved(task2.getID());
    }

    @Test
    public void testFetchAvailabeTasksWithCriteriaPIPA() throws Exception {
        String id = getUniqueTaskID();
        PIPATask task1 = new PIPATask(id, new URI("http://hellonico.net"), new URI("http://hellonico.net"), new URI("http://hellonico.net"),
                        "initOperationSOAPAction");

        task1.getRoleOwners().add("role1");
        task1.getUserOwners().add("user1");

        persist(task1);

        testFetchForUserRolesWithCriteria("user1", new String[] { "role1" }, PIPATask.class, null, 1);
        testFetchForUserRolesWithCriteria("user3", new String[] { "role3" }, PIPATask.class, null, 0);

        checkRemoved(task1.getID());

    }

    private Task[] testFetchForUserRolesWithCriteria(String userId, String[] roles, Class taskClass, String subQuery, int size) throws Exception {
        Task[] tasks = new TaskFetcher(em).fetchAvailableTasks(new UserRoles(userId, roles), taskClass, subQuery);
        Assert.assertEquals(size, tasks.length);
        return tasks;
    }

    private Task[] testFecthForUserRoles(String userId, String[] roles, int size) throws Exception {
        Task[] tasks = new TaskFetcher(em).fetchAllAvailableTasks(new UserRoles(userId, roles));
        Assert.assertEquals(size, tasks.length);
        return tasks;
    }

    private Document getXmlSampleDocument() throws Exception {
        return xml.getXmlDocument("/inputWithNamespace.xml");
    }

    private void checkRemoved(Task task2) {
        checkRemoved(task2.getID());
    }

    private void checkRemoved(String id) {
        final TaskFetcher taskFetcher = new TaskFetcher(em);
        jpa.begin();
        taskFetcher.deleteTasksWithID(id);
        jpa.commit();
        try {
            taskFetcher.fetchTaskIfExists(id);
            Assert.fail("No task should be left here");
        } catch (Exception expected) {
            // this is good.
        }
    }

}
