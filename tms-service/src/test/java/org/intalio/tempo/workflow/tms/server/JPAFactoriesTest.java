/**
 * Copyright (c) 2005-2007 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 */
package org.intalio.tempo.workflow.tms.server;

import java.net.URI;
import java.util.Random;

import junit.framework.Assert;
import junit.framework.JUnit4TestAdapter;

import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.task.Notification;
import org.intalio.tempo.workflow.task.PATask;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.task.TaskState;
import org.intalio.tempo.workflow.task.TaskType;
import org.intalio.tempo.workflow.task.xml.XmlTooling;
import org.intalio.tempo.workflow.tms.server.dao.ITaskDAOConnection;
import org.intalio.tempo.workflow.tms.server.dao.JPATaskDaoConnectionFactory;
import org.intalio.tempo.workflow.util.TaskEquality;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Confirm the proper usage of JPA factories for storing tasks
 */
public class JPAFactoriesTest {
  final static Logger log = LoggerFactory.getLogger(JPAFactoriesTest.class);
  final static String USER = "intalio/niko", ROLE = "role";
  final static UserRoles URS = new UserRoles(USER, new String[] { ROLE });
  final static Random random = new Random();
  final static XmlTooling xml = new XmlTooling();
  final static int TASK_COUNT = 50;

  enum FORCE_TASK_TYPE {
    RANDOM, PIPA, PA, NOTIFICATION
  }

  static final FORCE_TASK_TYPE type = FORCE_TASK_TYPE.PIPA;
  static int ID_COUNTER = 0;
  static ITaskDAOConnection connection;
  static JPATaskDaoConnectionFactory jtdcf;

  public static junit.framework.Test suite() throws Exception {
    // create the factory and check no left overs from previous run.
    jtdcf = new JPATaskDaoConnectionFactory();
    connection = jtdcf.openConnection();
    return new JUnit4TestAdapter(JPAFactoriesTest.class);
  }

  @Test
  public void userTaskListShouldBeEmpty() throws Exception {
    updateTasksAndCheckCount(USER, 0, "Database is propably not clean. Remaining tasks for user");
  }

  @Test
  public void userTaskListShouldBeEmptyWhenATaskWithNoUserIsAdded() throws Exception {
    // get a sample task and commit it. check it's not assigned already.
    Task t = getRandomSampleTask();
    connection.createTask(t);
    connection.commit();
    updateTasksAndCheckCount(USER, 0, "Got some tasks after creating a task with no assigned user.");
  }

  @Test
  public void userShouldGetATaskWhenUserOrRolesMatch() throws Exception {
    Task t = getRandomSampleTask();
    // add some user and roles to the task, update the persisted task and
    // check it's in the list
    t.getUserOwners().add(USER);
    t.getRoleOwners().add(ROLE);
    connection.createTask(t);
    connection.commit();
    updateTasksAndCheckCount(USER, 1, "Expecting one task for the current user");
  }

  @Test
  public void userShouldGetAllTheFiftyTasksAssignedToHim() throws Exception {
    // add quite a few random tasks
    for (int i = 0; i < TASK_COUNT; i++) {
      Task t2 = getRandomSampleTask();
      t2.getUserOwners().add(USER);
      t2.getRoleOwners().add(ROLE);
      connection.createTask(t2);
      connection.commit();
      Task t3 = connection.fetchTaskIfExists(t2.getID());
      TaskEquality.areTasksEquals(t2, t3);
    }
    updateTasksAndCheckCount(USER, TASK_COUNT + 1, "User does not have a proper task count in his inbox.");
  }

  @Test
  public void userShouldGetAnEmptyTaskListWhenEverythingIsDeleted() throws Exception {
    // delete the two tasks, check they are not in the user's list anymore
    Task[] _tasks = connection.fetchAllAvailableTasks(URS);
    for (Task task : _tasks) {
      connection.deleteTask(0, task.getID());
      connection.commit();
    }
    updateTasksAndCheckCount(USER, 0, "Expecting zero task left for the current user");
  }

  void updateTasksAndCheckCount(String user, int expectedCount, String message) throws Exception {
    Task[] tasks = connection.fetchAllAvailableTasks(URS);
    Assert.assertEquals(message, expectedCount, tasks.length);
  }

  Task getRandomSampleTask() throws Exception {
    String id = "id_" + (ID_COUNTER++);
    URI uri = new URI("http://hellonico.net");
    switch (type) {
      case NOTIFICATION:
        return new Notification(id, uri, xml.parseXML("<hello/>"));
      case PIPA:
        return new PIPATask(id, uri, uri, uri, "initOperationSOAPAction");
      case PA:
        return new PATask(id, new URI("http://hellonico.net"), "processId", "soap", xml.parseXML("<hello/>"));
      default:
        int rand = random.nextInt(3);
        switch (rand) {
          case 0:
            return new PATask(id, new URI("http://hellonico.net"), "processId", "soap", xml.parseXML("<hello/>"));
          case 1:
            return new PIPATask(id, uri, uri, uri, "initOperationSOAPAction");
          default:
            return new Notification(id, uri, xml.parseXML("<hello/>"));
        }
    }
  }

}
