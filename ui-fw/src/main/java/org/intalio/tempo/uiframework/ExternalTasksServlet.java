package org.intalio.tempo.uiframework;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.authentication.AuthenticationConstants;
import org.intalio.tempo.security.util.PropertyUtils;
import org.intalio.tempo.security.ws.TokenClient;
import org.intalio.tempo.uiframework.actions.TasksCollector;
import org.intalio.tempo.uiframework.model.TaskHolder;
import org.intalio.tempo.web.ApplicationState;
import org.intalio.tempo.web.User;
import org.intalio.tempo.workflow.task.Task;

@SuppressWarnings("serial")
public abstract class ExternalTasksServlet extends HttpServlet {
    
    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
    
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
            
            if(rtoken!=null) {
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
            ArrayList<TaskHolder<Task>> tasks = collector.retrieveTasks();
            ServletOutputStream outputStream = response.getOutputStream();
            String filename = "tasks for "+user+getFileExt();
            response.setContentType(getFileMimeType());
            response.addHeader("Content-disposition", "attachment; filename=\""      + filename + "\"");
            
            generateFile(request, pToken, user, tasks, outputStream);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    public abstract void generateFile(HttpServletRequest request, String pToken, String user, ArrayList<TaskHolder<Task>> tasks, ServletOutputStream outputStream) 
        throws Exception;

    public abstract String getFileExt();
    
    public abstract String getFileMimeType();

}