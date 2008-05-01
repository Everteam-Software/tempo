package org.intalio.tempo.persistence.delete;

import java.util.ArrayList;

import org.ho.yaml.Yaml;
import org.intalio.tempo.workflow.auth.AuthIdentifierSet;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.task.PATask;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.tms.server.dao.JPATaskDaoConnection;
import org.intalio.tempo.workflow.tms.server.dao.JPATaskDaoConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteTasksHelper implements Runnable {

    final static Logger log = LoggerFactory.getLogger(DeleteTasksHelper.class);
    private JPATaskDaoConnectionFactory jdcf;
    private JPATaskDaoConnection dao;
    private DeleteConfig config;

    public void run() {
        log.info("Deleting tasks for:" + config.username);
        UserRoles ur = new UserRoles(config.username, new AuthIdentifierSet());
        Task[] tasks = null;
        if (config.queries != null) {
            ArrayList<Query> queries = config.getQueries();
            Query q = queries.get(0);
            String query = q.subquery;
            Class subclass = PATask.class;
            try {
                subclass = Class.forName(q.klass);
            } catch (Exception e) {

            }
            tasks = dao.fetchAvailableTasks(ur, subclass, query);
        } else {
            tasks = dao.fetchAllAvailableTasks(ur);
        }

        for (Task t : tasks) {
            if (config.fakerun) {
                log.info("Fakerun. Candidate for deletion:" + t.getClass() + ":" + t.getID());
            } else {
                log.info("Deleting:" + t.getClass() + ":" + t.getID());
                dao.deleteTask(t.getInternalId(), t.getID());
            }
        }

    }

    public DeleteTasksHelper() throws Exception {
        jdcf = new JPATaskDaoConnectionFactory();
        dao = (JPATaskDaoConnection) jdcf.openConnection();
        config = (DeleteConfig) Yaml.loadType(this.getClass().getResourceAsStream("/delete.yml"), DeleteConfig.class);
    }

    public DeleteTasksHelper(JPATaskDaoConnectionFactory jdcf, DeleteConfig config) {
        this.jdcf = jdcf;
        this.config = config;
    }
}
