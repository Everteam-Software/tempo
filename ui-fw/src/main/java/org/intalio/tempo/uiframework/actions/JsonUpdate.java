package org.intalio.tempo.uiframework.actions;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.rmi.RemoteException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.intalio.tempo.uiframework.URIUtils;
import org.intalio.tempo.uiframework.forms.FormManager;
import org.intalio.tempo.uiframework.forms.FormManagerBroker;
import org.intalio.tempo.workflow.auth.AuthException;
import org.intalio.tempo.workflow.task.Notification;
import org.intalio.tempo.workflow.task.PATask;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.task.TaskState;
import org.intalio.tempo.workflow.tms.ITaskManagementService;
import org.intalio.tempo.workflow.tms.client.RemoteTMSFactory;

import atg.taglib.json.util.JSONArray;
import atg.taglib.json.util.JSONException;
import atg.taglib.json.util.JSONObject;

public class JsonUpdate extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		response.setContentType("application/x-json");
		String token = request.getParameter("token");
		String userName = request.getParameter("user");
		if (token != null && userName != null && token.length() > 0 && userName.length() > 0) {
			JSONObject jroot = new JSONObject();
			JSONArray jtasks = new JSONArray();

			JSONArray jprocesses = new JSONArray();

			ITaskManagementService taskManager = getTMS(token);
			try {
				Task[] tasks = taskManager.getTaskList();

				FormManager fmanager = FormManagerBroker.getInstance()
						.getFormManager();
				String peopleActivityUrl = resoleUrl(request, fmanager
						.getPeopleActivityURL());
				String notificationURL = resoleUrl(request, fmanager
						.getNotificationURL());
				String peopleInitiatedProcessURL = resoleUrl(request, fmanager
						.getPeopleInitiatedProcessURL());

				for (Object task : tasks) {
					if (task instanceof Notification) {
						Notification notification = (Notification) task;
						if (!TaskState.COMPLETED
								.equals(notification.getState())) {
							JSONObject jtask = new JSONObject();
							jtask.put("taskUrl", notificationURL + "?id="
									+ notification.getID() + "&url="
									+ notification.getFormURLAsString()
									+ "&token=" + token + "&user=" + userName);
							jtask.put("description", notification
									.getDescription());
							jtask.put("creationDate", notification
									.getCreationDate());
							jtasks.add(jtask);
						}
					} else if (task instanceof PATask) {
						PATask paTask = (PATask) task;
						if (!TaskState.COMPLETED.equals(paTask.getState())) {
							JSONObject jtask = new JSONObject();
							jtask.put("taskUrl", peopleActivityUrl + "?id="
									+ paTask.getID() + "&url="
									+ paTask.getFormURLAsString() + "&token="
									+ token + "&user=" + userName);
							jtask.put("description", paTask.getDescription());
							jtask.put("creationDate", paTask.getCreationDate());
							jtasks.add(jtask);
						}
					} else if (task instanceof PIPATask) {
						PIPATask pipaTask = (PIPATask) task;
						JSONObject jprocess = new JSONObject();
						jprocess.put("taskUrl", peopleInitiatedProcessURL
								+ "?id=" + pipaTask.getID() + "&url="
								+ pipaTask.getFormURLAsString() + "&token="
								+ token + "&user=" + userName);
						jprocess.put("description", pipaTask.getDescription());
						jprocess
								.put("creationDate", pipaTask.getCreationDate());
						jprocesses.add(jprocess);
					} else {

					}

				}
				jroot.put("tasks", jtasks);
				jroot.put("process", jprocesses);
			} catch (AuthException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			PrintWriter out = response.getWriter();

			out.print(jroot);
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		doGet(request, response);
	}

	private ITaskManagementService getTMS(String participantToken)
			throws RemoteException {
		String endpoint = "http://localhost:8080/axis2/services/TaskManagementServices";
		return new RemoteTMSFactory(endpoint, participantToken).getService();
	}

	private String resoleUrl(HttpServletRequest request, String url) {
		try {
			url = URIUtils.resolveHttpURI(request, url);

		} catch (URISyntaxException ex) {
		}
		return url;
	}
}
