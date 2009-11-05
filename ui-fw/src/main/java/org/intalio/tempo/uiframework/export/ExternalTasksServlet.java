package org.intalio.tempo.uiframework.export;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import org.intalio.tempo.uiframework.export.ExportKey.ExportGLobalKey;
import org.intalio.tempo.uiframework.model.TaskHolder;
import org.intalio.tempo.web.ApplicationState;
import org.intalio.tempo.web.User;
import org.intalio.tempo.workflow.task.AssignedAvionics;
import org.intalio.tempo.workflow.task.AssignedCoords;
import org.intalio.tempo.workflow.task.AssignedMechanics;
import org.intalio.tempo.workflow.task.PATask;
import org.intalio.tempo.workflow.task.RTR;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.task.traits.ICompleteReportingTask;
import org.intalio.tempo.workflow.task.traits.IProcessBoundTask;
import org.intalio.tempo.workflow.task.traits.ITaskWithDeadline;
import org.intalio.tempo.workflow.task.traits.ITaskWithPriority;
import org.intalio.tempo.workflow.task.traits.ITaskWithState;
import org.intalio.tempo.workflow.task.traits.InitTask;

@SuppressWarnings("serial")
public abstract class ExternalTasksServlet extends HttpServlet {

    private static final String CVS_SEPARATOR = " ; ";
	final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
    final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
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
    public ArrayList<LinkedHashMap<String, String>> sortSITAIntalioTasks() {
    	 ArrayList<LinkedHashMap<String, String>> sortedTaskList = new ArrayList<LinkedHashMap<String, String>>();
         if(tasks.size()==0) return sortedTaskList;
         else{
        	 
        	 if (tasks.get(0).getTask() instanceof PATask){
        		 return sortSITATasks();
        	 }
        	 else{
        		 return sortIntalioTasks();
        		 
        	 }
         }
    }
    public ArrayList<Map<ExportKey.ExportGLobalKey, String>> sortTasks() {
        ArrayList<Map<ExportKey.ExportGLobalKey, String>> sortedTaskList = new ArrayList<Map<ExportKey.ExportGLobalKey, String>>();
        if(tasks==null) return sortedTaskList;
        for (TaskHolder<Task> t : tasks) {
            Task task = t.getTask();
            HashMap<ExportKey.ExportGLobalKey, String> map = new HashMap<ExportKey.ExportGLobalKey, String>();
            map.put(ExportKey.ExportGLobalKey.CREATION_DATE, simpleDateFormat.format(task.getCreationDate()));
            map.put(ExportKey.ExportGLobalKey.DESCRIPTION, task.getDescription());
            map.put(ExportKey.ExportGLobalKey.ID, task.getID());
            map.put(ExportKey.ExportGLobalKey.FORM_URL, task.getFormURLAsString());
            if (task instanceof ITaskWithPriority) {
                Integer priority = ((ITaskWithPriority) task).getPriority();
                if(priority!=null) {
                    map.put(ExportKey.ExportGLobalKey.PRIORITY, String.valueOf(priority));    
                } else {
                    map.put(ExportKey.ExportGLobalKey.PRIORITY, StringUtils.EMPTY);
                }
            }
            if (task instanceof ITaskWithState) {
                map.put(ExportKey.ExportGLobalKey.STATE, ((ITaskWithState) task).getState().getName());
            }
            if (task instanceof IProcessBoundTask) {
                map.put(ExportKey.ExportGLobalKey.PROCESS_ID, String.valueOf(((IProcessBoundTask) task).getProcessID()));
            }
            if (task instanceof InitTask) {
                map.put(ExportKey.ExportGLobalKey.PROCESS_ENDPOINT, String.valueOf(((InitTask) task).getProcessEndpoint()));
                map.put(ExportKey.ExportGLobalKey.INIT_MESSAGE_NS, String.valueOf(((InitTask) task).getInitMessageNamespaceURI()));
                map.put(ExportKey.ExportGLobalKey.INIT_OPERATION_ACTION, String.valueOf(((InitTask) task).getInitOperationSOAPAction()));
            }
            if (task instanceof ICompleteReportingTask)
                map.put(ExportKey.ExportGLobalKey.COMPLETE_URL, String.valueOf(((ICompleteReportingTask) task).getCompleteSOAPAction()));
            if (task instanceof ITaskWithDeadline) {
                ITaskWithDeadline deadlinedTask = (ITaskWithDeadline) task;
                Date deadline = deadlinedTask.getDeadline();
                if (deadline != null) {
                    map.put(ExportKey.ExportGLobalKey.DEADLINE, String.valueOf(simpleDateFormat.format(deadline)));
                } else {
                    map.put(ExportKey.ExportGLobalKey.DEADLINE, StringUtils.EMPTY);
                }

            }

            sortedTaskList.add(map);
        }
        return sortedTaskList;
    }
    public ArrayList<LinkedHashMap<String, String>> sortSITATasks() {
        ArrayList<LinkedHashMap<String, String>> sortedTaskList = new ArrayList<LinkedHashMap<String, String>>();
        if(tasks==null) return sortedTaskList;
        for (TaskHolder<Task> t : tasks) {
            PATask task=(PATask)t.getTask();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

            map.put(ExportKey.ExportSITAKey.UPDATE.ExportSITAKey(), String.valueOf(task.get_update()));
            map.put(ExportKey.ExportSITAKey.TAIL_NUMBER.ExportSITAKey(), task.get_AircraftID());
            map.put(ExportKey.ExportSITAKey.ARR_FL.ExportSITAKey(), task.get_ArrivalFlightNumber());
            map.put(ExportKey.ExportSITAKey.ARR_SCH_DATE.ExportSITAKey(), formatDate(task.get_ScheduledArrival()));
            map.put(ExportKey.ExportSITAKey.STA.ExportSITAKey(), formatTime(task.get_ScheduledArrival()));
            map.put(ExportKey.ExportSITAKey.ETA_ATA.ExportSITAKey(),  formatTime(task.get_ActualArrival()));
            // TODO map.put(ExportKey.ExportSITAKey.INS.ExportSITAKey(), task.get_InspectionType());
            map.put(ExportKey.ExportSITAKey.DEP_FL.ExportSITAKey(), task.get_DepartureFlightNumber());
            map.put(ExportKey.ExportSITAKey.DEP_SCH_DATE.ExportSITAKey(), formatDate(task.get_ScheduledDeparture()));
            map.put(ExportKey.ExportSITAKey.STD.ExportSITAKey(), formatTime(task.get_ScheduledDeparture()));
            map.put(ExportKey.ExportSITAKey.ETD_ATD.ExportSITAKey(), formatTime(task.get_ActualDeparture()));
            map.put(ExportKey.ExportSITAKey.STAND.ExportSITAKey(), task.get_Stand());
            map.put(ExportKey.ExportSITAKey.COORD.ExportSITAKey(), formatCoords(task.get_assignedCoord()));
            map.put(ExportKey.ExportSITAKey.MECHANICS.ExportSITAKey(), formatMechanics(task.get_assignedMechanics()));
            map.put(ExportKey.ExportSITAKey.AVIONICS.ExportSITAKey(), formatAvionics(task.get_assignedAvionics()));
            map.put(ExportKey.ExportSITAKey.HIL.ExportSITAKey(), "");
            // TODO map.put(ExportKey.ExportSITAKey.RTR.ExportSITAKey(), formatRTR(task.get_RTR()));
            map.put(ExportKey.ExportSITAKey.COMMENTS.ExportSITAKey(), task.get_comments());
            map.put(ExportKey.ExportSITAKey.STATE.ExportSITAKey(), task.get_state().name());          
            map.put(ExportKey.ExportSITAKey.HELP_REQUEST.ExportSITAKey(), task.get_resources());
            map.put(ExportKey.ExportSITAKey.START.ExportSITAKey(), formatTime(task.get_startTime()));
            map.put(ExportKey.ExportSITAKey.END.ExportSITAKey(), formatTime(task.get_startTime()));
            map.put(ExportKey.ExportSITAKey.RELEASE.ExportSITAKey(), formatTime(task.get_releaseTime()));
            map.put(ExportKey.ExportSITAKey.LATE.ExportSITAKey(), String.valueOf(task.get_late()));
            
            sortedTaskList.add(map);
        }
            
            
            
            return sortedTaskList;
    }
    public ArrayList<LinkedHashMap<String, String>> sortIntalioTasks() {
        ArrayList<LinkedHashMap<String, String>> sortedTaskList = new ArrayList<LinkedHashMap<String, String>>();
        if(tasks==null) return sortedTaskList;
        for (TaskHolder<Task> t : tasks) {
            Task task = t.getTask();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put(ExportKey.ExportGLobalKey.CREATION_DATE.ExportGlobalKey(), simpleDateFormat.format(task.getCreationDate()));
            map.put(ExportKey.ExportGLobalKey.DESCRIPTION.ExportGlobalKey(), task.getDescription());
            map.put(ExportKey.ExportGLobalKey.ID.ExportGlobalKey(), task.getID());
            map.put(ExportKey.ExportGLobalKey.FORM_URL.ExportGlobalKey(), task.getFormURLAsString());
            if (task instanceof ITaskWithPriority) {
                Integer priority = ((ITaskWithPriority) task).getPriority();
                if(priority!=null) {
                    map.put(ExportKey.ExportGLobalKey.PRIORITY.ExportGlobalKey(), String.valueOf(priority));    
                } else {
                    map.put(ExportKey.ExportGLobalKey.PRIORITY.ExportGlobalKey(), StringUtils.EMPTY);
                }
            }
            if (task instanceof ITaskWithState) {
                map.put(ExportKey.ExportGLobalKey.STATE.ExportGlobalKey(), ((ITaskWithState) task).getState().getName());
            }
            if (task instanceof IProcessBoundTask) {
                map.put(ExportKey.ExportGLobalKey.PROCESS_ID.ExportGlobalKey(), String.valueOf(((IProcessBoundTask) task).getProcessID()));
            }
            if (task instanceof InitTask) {
                map.put(ExportKey.ExportGLobalKey.PROCESS_ENDPOINT.ExportGlobalKey(), String.valueOf(((InitTask) task).getProcessEndpoint()));
                map.put(ExportKey.ExportGLobalKey.INIT_MESSAGE_NS.ExportGlobalKey(), String.valueOf(((InitTask) task).getInitMessageNamespaceURI()));
                map.put(ExportKey.ExportGLobalKey.INIT_OPERATION_ACTION.ExportGlobalKey(), String.valueOf(((InitTask) task).getInitOperationSOAPAction()));
            }
            if (task instanceof ICompleteReportingTask)
                map.put(ExportKey.ExportGLobalKey.COMPLETE_URL.ExportGlobalKey(), String.valueOf(((ICompleteReportingTask) task).getCompleteSOAPAction()));
            if (task instanceof ITaskWithDeadline) {
                ITaskWithDeadline deadlinedTask = (ITaskWithDeadline) task;
                Date deadline = deadlinedTask.getDeadline();
                if (deadline != null) {
                    map.put(ExportKey.ExportGLobalKey.DEADLINE.ExportGlobalKey(), String.valueOf(simpleDateFormat.format(deadline)));
                } else {
                    map.put(ExportKey.ExportGLobalKey.DEADLINE.ExportGlobalKey(), StringUtils.EMPTY);
                }

            }

            sortedTaskList.add(map);
        }
        return sortedTaskList;
    }

	private String formatRTR(Collection<RTR> RTRs) {
			String response="";
			for(RTR rtr:RTRs){
				response+=rtr.getRTRID()+CVS_SEPARATOR;
			}
			if(RTRs.size()>0)
				response=removeLastSeparator(response);
			return response;
	}

	private String formatAvionics(
			Collection<AssignedAvionics> avionics) {
		String response="";
		for(AssignedAvionics avionic:avionics){
			response+=avionic.getName()+CVS_SEPARATOR;
		}
		if(avionics.size()>0)
			response=removeLastSeparator(response);
		return response;
	}
	private String formatCoords(
			Collection<AssignedCoords> coords) {
		String response="";
		for(AssignedCoords coord:coords){
			response+=coord.getName()+CVS_SEPARATOR;
		}
		if(coords.size()>0)
			response=removeLastSeparator(response);
		return response;
	}

	private String formatTime(Date time) {
		return (time==null)?"":timeFormat.format(time);
	}

	private String formateDateTime(Date datetime) {
		return (datetime==null)?"":simpleDateFormat.format(datetime);
	}

	private String formatDate(Date date) {
		return (date==null)?"":dateFormat.format(date);
	}

	private String formatMechanics(
			Collection<AssignedMechanics> mechanics) {
		String response="";
		for(AssignedMechanics mechanic:mechanics){
			response+=mechanic.getName()+CVS_SEPARATOR;
		}
		if(mechanics.size()>0)
			response=removeLastSeparator(response);
		return response;
	}

	private String removeLastSeparator(String response) {
		return response.substring(0, response.length()-CVS_SEPARATOR.length());
	}

	public abstract void generateFile(HttpServletRequest request, String pToken, String user, ServletOutputStream outputStream) throws Exception;

    public abstract String getFileExt();

    public abstract String getFileMimeType();

}