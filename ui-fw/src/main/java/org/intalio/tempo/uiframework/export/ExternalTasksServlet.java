package org.intalio.tempo.uiframework.export;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.authentication.AuthenticationConstants;
import org.intalio.tempo.security.util.PropertyUtils;
import org.intalio.tempo.security.ws.TokenClient;
import org.intalio.tempo.uiframework.Configuration;
import org.intalio.tempo.uiframework.actions.TasksCollector;
import org.intalio.tempo.uiframework.model.TaskHolder;
import org.intalio.tempo.web.ApplicationState;
import org.intalio.tempo.web.User;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.task.traits.ICompleteReportingTask;
import org.intalio.tempo.workflow.task.traits.IProcessBoundTask;
import org.intalio.tempo.workflow.task.traits.ITaskWithDeadline;
import org.intalio.tempo.workflow.task.traits.ITaskWithPriority;
import org.intalio.tempo.workflow.task.traits.ITaskWithState;
import org.intalio.tempo.workflow.task.traits.InitTask;

@SuppressWarnings("serial")
public abstract class ExternalTasksServlet extends HttpServlet {

    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
    ArrayList<TaskHolder<Task>> tasks;

    public ExternalTasksServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            TokenClient tc = Configuration.getInstance().getTokenClient();
            String rtoken = request.getParameter("token");
            String pToken;
            String user;

            if (rtoken != null) {
                Property[] properties = tc.getTokenProperties(rtoken);
                user = PropertyUtils.getProperty(properties, AuthenticationConstants.PROPERTY_USER).getValue().toString();
                pToken = rtoken;
            } else {
                ApplicationState state = ApplicationState.getCurrentInstance(new HttpServletRequestWrapper(request));
                User currentUser = state.getCurrentUser();
                pToken = currentUser.getToken();
                user = currentUser.getName();
            }

            TasksCollector collector = new TasksCollector(new HttpServletRequestWrapper(request), user, pToken);
            tasks = collector.retrieveTasks();
            ServletOutputStream outputStream = response.getOutputStream();
            String filename = "tasks for " + user + getFileExt();
            response.setContentType(getFileMimeType());
            response.addHeader("Content-disposition", "attachment; filename=\"" + filename + "\"");

            generateFile(request, pToken, user, outputStream);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    public ArrayList<Map<ExportKey, String>> sortTasks() {
        ArrayList<Map<ExportKey, String>> sortedTaskList = new ArrayList<Map<ExportKey, String>>();
        for (TaskHolder<Task> t : tasks) {
            Task task = t.getTask();
            HashMap<ExportKey, String> map = new HashMap<ExportKey, String>();
            map.put(ExportKey.CREATION_DATE, simpleDateFormat.format(task.getCreationDate()));
            map.put(ExportKey.DESCRIPTION, task.getDescription());
            map.put(ExportKey.ID, task.getID());
            map.put(ExportKey.FORM_URL, task.getFormURLAsString());
            if (task instanceof ITaskWithPriority) {
                Integer priority = ((ITaskWithPriority) task).getPriority();
                if(priority!=null) {
                    map.put(ExportKey.PRIORITY, String.valueOf(priority));    
                } else {
                    map.put(ExportKey.PRIORITY, StringUtils.EMPTY);
                }
            }
            if (task instanceof ITaskWithState) {
                map.put(ExportKey.STATE, ((ITaskWithState) task).getState().getName());
            }
            if (task instanceof IProcessBoundTask) {
                map.put(ExportKey.PROCESS_ID, String.valueOf(((IProcessBoundTask) task).getProcessID()));
            }
            if (task instanceof InitTask) {
                map.put(ExportKey.PROCESS_ENDPOINT, String.valueOf(((InitTask) task).getProcessEndpoint()));
                map.put(ExportKey.INIT_MESSAGE_NS, String.valueOf(((InitTask) task).getInitMessageNamespaceURI()));
                map.put(ExportKey.INIT_OPERATION_ACTION, String.valueOf(((InitTask) task).getInitOperationSOAPAction()));
            }
            if (task instanceof ICompleteReportingTask)
                map.put(ExportKey.COMPLETE_URL, String.valueOf(((ICompleteReportingTask) task).getCompleteSOAPAction()));
            if (task instanceof ITaskWithDeadline) {
                ITaskWithDeadline deadlinedTask = (ITaskWithDeadline) task;
                Date deadline = deadlinedTask.getDeadline();
                if (deadline != null) {
                    map.put(ExportKey.DEADLINE, String.valueOf(simpleDateFormat.format(deadline)));
                } else {
                    map.put(ExportKey.DEADLINE, StringUtils.EMPTY);
                }

            }
            sortedTaskList.add(map);
        }
        return sortedTaskList;
    }

    public abstract void generateFile(HttpServletRequest request, String pToken, String user, ServletOutputStream outputStream) throws Exception;

    public abstract String getFileExt();

    public abstract String getFileMimeType();

}