package org.intalio.tempo.uiframework.scheduler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.intalio.tempo.uiframework.Configuration;
import org.intalio.tempo.workflow.auth.AuthIdentifierSet;
import org.intalio.tempo.workflow.task.PATask;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.task.TaskState;
import org.intalio.tempo.workflow.task.Vacation;
import org.intalio.tempo.workflow.task.traits.ITaskWithState;
import org.intalio.tempo.workflow.tms.server.ITMSServer;
import org.intalio.tempo.workflow.tms.server.dao.ITaskDAOConnection;
import org.intalio.tempo.workflow.tms.server.dao.ITaskDAOConnectionFactory;
import org.intalio.tempo.workflow.tms.server.dao.VacationDAOConnection;
import org.intalio.tempo.workflow.tms.server.dao.VacationDAOConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Absence Management scheduler.
 * 
 * @author bapiraju
 * 
 */
public class AMScheduler {

    /**
     * scheduler thread pool to execute.
     */
    private final ScheduledExecutorService scheduler = Executors
            .newScheduledThreadPool(1);
    /**
     * configuration instance.
     */
    private static Configuration conf = Configuration.getInstance();

    /**
     * holds todays date in "dd/MM/yyyy" format.
     */
    private String today = new SimpleDateFormat("dd/MM/yyyy").format(Calendar
            .getInstance().getTime());
    /**
     * holds scheduler interval.
     */
    private int schedulerInterval = conf.getAmSchedulerInterval();
    /**
     * holds scheduler is active flag.
     */
    private Boolean schedulerActive = conf.isAmSchedulerActive();
    /**
     * reference to tms server.
     */
    private ITMSServer tmsServer;
    /**
     * reference to taskDAOFactory.
     */
    private ITaskDAOConnectionFactory taskDAOFactory;
    /**
     * reference to vacationDAOFactory.
     */
    private VacationDAOConnectionFactory vacationDAOFactory;
    /**
     * reference to logger.
     */
    private final Logger logger = LoggerFactory.getLogger(AMScheduler.class);

    /**
     * Absence Management scheduler constructor.
     */
    public AMScheduler() {
        if (schedulerActive) {
            scheduler.scheduleWithFixedDelay(new Runnable() {
                public void run() {
                    try {
                        AMScheduler.this.processSubstituteTasks();
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }, schedulerInterval, schedulerInterval, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * set taskDAOFactory.
     * 
     * @param taskDAOConnectionFactory
     *            ITaskDAOConnectionFactory
     */
    public final void setTaskDAOFactory(
            final ITaskDAOConnectionFactory taskDAOConnectionFactory) {
        this.taskDAOFactory = taskDAOConnectionFactory;
        logger.info("ITaskDAOConnectionFactory implementation : "
                + taskDAOFactory.getClass());
    }

    /**
     * 
     * @param vacationDAOConnectionFactory
     *            VacationDAOConnectionFactory
     */
    public final void setVacationDAOFactory(
            final VacationDAOConnectionFactory vacationDAOConnectionFactory) {
        this.vacationDAOFactory = vacationDAOConnectionFactory;
        logger.info("VacationDAOConnectionFactory implementation : "
                + vacationDAOFactory.getClass());
    }

    /**
     * 
     * @param server
     *            ITMSServer
     */
    public final void setServer(final ITMSServer server) {
        this.tmsServer = server;
    }

    /**
     * read vacations and assign user available tasks to substitutes.
     */
    public final void processSubstituteTasks() {
        try {
            Date todayDate = new SimpleDateFormat("dd/MM/yyyy").parse(today);
            Calendar yesterday = Calendar.getInstance();
            yesterday.add(Calendar.DATE, -1);
            Date yesterdayDate = yesterday.getTime();
            List<Vacation> vacations = null;
            VacationDAOConnection vdao = null;
            try {
                if (vacationDAOFactory != null) {
                    vdao = vacationDAOFactory.openConnection();
                    vacations = tmsServer.getVacationsByStartDate(vdao,
                            todayDate);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
                if (vdao != null) {
                    vdao.close();
                }
            }
            if (vacations == null) { vacations = Collections.emptyList(); }
            for (Vacation vacation : vacations) {

                List<String> users = new ArrayList<String>();
                users.add(vacation.getUser());
                ITaskDAOConnection dao = null;
                List<Task> tasks = null;
                try {
                    if (taskDAOFactory != null) {
                        dao = taskDAOFactory.openConnection();
                        tasks = tmsServer.getTaskList(dao, users);
                    }
                    if (tasks == null) { tasks = Collections.emptyList(); }
                    for (Task task : tasks) {
                        if (task instanceof PATask
                                && ((ITaskWithState) task).getState().equals(
                                        TaskState.READY)
                                && !task.getUserOwners().contains(
                                        vacation.getSubstitute())) {
                            AuthIdentifierSet userSet = new AuthIdentifierSet(
                                    task.getUserOwners());
                            userSet.add(vacation.getSubstitute());
                            task.setUserOwners(userSet);
                            dao.updateTask(task);
                            dao.commit();
                        }
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                } finally {
                    if (dao != null) {
                        dao.close();
                    }
                }
            }
            try {
                if (vacationDAOFactory != null) {
                    vdao = vacationDAOFactory.openConnection();
                    vacations = tmsServer.getVacationsByEndDate(vdao,
                            yesterdayDate);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
                if (vdao != null) {
                    vdao.close();
                }
            }
            if (vacations == null) { vacations = Collections.emptyList(); }
            for (Vacation vacation : vacations) {

                List<String> substitutes = new ArrayList<String>();
                substitutes.add(vacation.getSubstitute());
                ITaskDAOConnection dao = null;
                List<Task> tasks = null;
                try {
                    if (taskDAOFactory != null) {
                        dao = taskDAOFactory.openConnection();
                        tasks = tmsServer.getTaskList(dao, substitutes);
                    }
                    if (tasks == null) { tasks = Collections.emptyList(); }
                    for (Task task : tasks) {
                        if (task instanceof PATask
                                && ((ITaskWithState) task).getState().equals(
                                        TaskState.READY)
                                && task.getUserOwners().contains(
                                        vacation.getSubstitute())
                                && task.getUserOwners().contains(
                                        vacation.getUser())) {
                            AuthIdentifierSet userSet = new AuthIdentifierSet(
                                    task.getUserOwners());
                            userSet.remove(vacation.getSubstitute());
                            task.setUserOwners(userSet);
                            dao.updateTask(task);
                            dao.commit();
                        }
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                } finally {
                    if (dao != null) {
                        dao.close();
                    }
                }
            }
        } catch (ParseException e1) {
            logger.error(e1.getMessage(), e1);
        }
    }
}