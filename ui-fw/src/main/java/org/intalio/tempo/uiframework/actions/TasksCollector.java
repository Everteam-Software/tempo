/**
 * Copyright (c) 2005-2008 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 */
package org.intalio.tempo.uiframework.actions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequestWrapper;
import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.llom.util.AXIOMUtil;
import org.intalio.tempo.uiframework.Configuration;
import org.intalio.tempo.uiframework.URIUtils;
import org.intalio.tempo.uiframework.forms.FormManager;
import org.intalio.tempo.uiframework.forms.FormManagerBroker;
import org.intalio.tempo.uiframework.model.TaskHolder;
import org.intalio.tempo.workflow.auth.AuthException;
import org.intalio.tempo.workflow.task.Notification;
import org.intalio.tempo.workflow.task.PATask;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.tms.ITaskManagementService;
import org.intalio.tempo.workflow.tms.client.RemoteTMSFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible for collecting task and sending them back to the
 * user interface
 * 
 * @author niko
 * 
 */
public class TasksCollector {

	final String[] parameters = new String[] { "page", "rp", "sortname",
			"sortorder", "query", "qtype", "type", "full" };

	final String TAMANAGEMENT_URI = "http://www.intalio.com/gi/forms/TAmanagement.gi";

	/**
	 * Parameters that can come from the user interface
	 */
	final class ParameterMap extends HashMap<String, String> {
		public ParameterMap() {
			super(parameters.length);
			put("full", "false");
			put("page", "1");
			put("sortname", "_creationDate");
			put("sortorder", "ASC");
			put("query", null);
			put("qtype", null);
			put("type", "PATask");
			init();
		}

		/**
		 * Do not copy over null values and empty values for parameters
		 */
		public void init() {
			for (String param : parameters) {
				String value = _request.getParameter(param);
				if (value != null && !value.trim().equalsIgnoreCase("")
						&& !value.equalsIgnoreCase("undefined"))
					put(param, value);
			}
		}

		/**
		 * Tells whether a parameter is defined and has a non-null value
		 */
		public boolean isSet(String param) {
			return this.containsKey(param) && this.get(param) != null;
		}
	}

	private static final Logger _log = LoggerFactory
			.getLogger(TasksCollector.class);
	private final Configuration conf = Configuration.getInstance();

	private HttpServletRequestWrapper _request;
	private String _user;
	private String _endpoint;
	private String _token;

	public TasksCollector(HttpServletRequestWrapper request, String user,
			String token) {
		this._request = request;
		this._user = user;
		this._token = token;
		this._endpoint = conf.getServiceEndpoint();
	}

	protected ITaskManagementService getTaskManager(String endpoint,
			String token) {
		return new RemoteTMSFactory(endpoint, token).getService();
	}

	public ArrayList<TaskHolder<Task>> retrieveTasks() throws Exception {
		final FormManager fmanager = FormManagerBroker.getInstance()
				.getFormManager();
		final ArrayList<TaskHolder<Task>> _tasks = new ArrayList<TaskHolder<Task>>();
		final String endpoint = URIUtils.resolveURI(_request, _endpoint);
		final ITaskManagementService taskManager = getTaskManager(endpoint,
				_token);

		ParameterMap params = new ParameterMap();
		String type = params.get("type");

		if (type.equals(Notification.class.getSimpleName())
				|| type.equals(PATask.class.getSimpleName())) {
			StringBuffer baseQuery = new StringBuffer(
					"(T._state = TaskState.READY OR T._state = TaskState.CLAIMED) ");
			collect(fmanager, _tasks, taskManager, params, type, baseQuery);
		} else if (type.equals(PIPATask.class.getSimpleName())) {
			StringBuffer baseQuery = new StringBuffer("");
			collect(fmanager, _tasks, taskManager, params, type, baseQuery);
		} else {
			_log.error("Cannot collect task of type:" + type);
		}
		return _tasks;
	}

	private void collect(final FormManager fmanager,
			final ArrayList<TaskHolder<Task>> _tasks,
			final ITaskManagementService taskManager, ParameterMap params,
			String type, StringBuffer query) throws AuthException {
		// do we have a valid query
		boolean validQuery = params.isSet("qtype") && params.isSet("query");
		// if yes, append it
		if (validQuery) {
			// PIPA have no base query, since they have no state.
			// so we don't need the keyword AND here.
			if (query.length() != 0) {
				query.append(" AND ");
			}
			query.append(" T." + params.get("qtype") + " like '%"
					+ params.get("query") + "%'");
		}
		// keep this for counting total tasks
		String countQuery = query.toString();
		// set the order column
		if (params.isSet("sortname"))
			query.append(" ORDER BY T." + params.get("sortname"));
		// set the sort order
		if (params.isSet("sortorder"))
			query.append(" " + params.get("sortorder"));
		// count total and get those tasks
		collectTasks(_token, _user, fmanager, taskManager, type, countQuery,
				query, _tasks, params.get("rp"), params.get("page"), Boolean
						.valueOf(params.get("full")));
	}

	private void collectTasks(final String token, final String user,
			FormManager fmanager, ITaskManagementService taskManager,
			String taskType, String countQuery, StringBuffer query,
			List<TaskHolder<Task>> tasksHolder, final String taskPerPage,
			final String page, final boolean collectFull) throws AuthException {
		// get total number of tasks
		long total = taskManager.countAvailableTasks(taskType, countQuery);

		int itasksPerPage = 0;
		try {
			// itasksPerPage = Integer.parseInt(taskPerPage);
			itasksPerPage = (int) total;
		} catch (Exception e) {
		}
		// int ipage = 1;
		// try {
		// ipage = Integer.parseInt(page);
		// } catch (Exception e) {
		// }
		// int index = (ipage - 1) * itasksPerPage;
		// long toIndex = index + itasksPerPage;
		// if (toIndex > total)
		// toIndex = total;
		// _request.setAttribute("totalPage", total);
		// _request.setAttribute("currentPage", page);

		// Task[] tasks = taskManager.getAvailableTasks(taskType,
		// query.toString(), String.valueOf(index), String
		// .valueOf(itasksPerPage), collectFull);

		Task[] tasks = taskManager.getAvailableTasks(taskType,
				query.toString(), "0", String.valueOf(itasksPerPage),
				collectFull);

		ArrayList<Task> tempList = new ArrayList<Task>();

		for (Task task : tasks) {
			if (task instanceof PATask && collectFull
					&& !user.equals("intalio\\admin")) {
				PATask currentTA = (PATask) task;
				try {
					OMElement el = AXIOMUtil.stringToOM(currentTA
							.getOutputAsXmlString());
					OMElement ad = el.getFirstChildWithName(new QName(
							TAMANAGEMENT_URI, "ArrivalDeparture"));

					String date = "";

					String ActualDepartureDate = ad.getFirstChildWithName(
							new QName(TAMANAGEMENT_URI, "ActualDepartureDate"))
							.getText();

					if (!ActualDepartureDate.equals("1970-01-01")) {
						date = ActualDepartureDate
								+ " "
								+ ad.getFirstChildWithName(
										new QName(TAMANAGEMENT_URI, "ATD"))
										.getText();
					} else {
						date = ad.getFirstChildWithName(
								new QName(TAMANAGEMENT_URI,
										"ScheduledDepartureDate")).getText()
								+ " "
								+ ad.getFirstChildWithName(
										new QName(TAMANAGEMENT_URI, "STD"))
										.getText();
					}

					if (toDisplay(date))
						tempList.add(task);

				} catch (Exception e) {
					// Dunno: this would be a PATask with no data, which
					// shouldn't exist...
				}
			} else {
				tempList.add(task);
			}
		}

		total = tempList.size();

		_request.setAttribute("totalPage", total);
		_request.setAttribute("currentPage", "1");

		// for (Task task : tasks) {
		for (int i = 0; i < total; i++) {
			tasksHolder.add(new TaskHolder<Task>(tempList.get(i), URIUtils
					.getResolvedTaskURLAsString(_request, fmanager, tempList
							.get(i), token, user)));
		}

		if (_log.isDebugEnabled()) {
			_log.error("DEBUG\n" + taskType + "\n" + query + "\n"
					+ tasks.length);
			_log.error("(" + tasks.length
					+ ") tasks were retrieved for participant token " + _token);
		}
	}

	private static boolean toDisplay(String date) {

		Calendar today = Calendar.getInstance();

		today.add(Calendar.HOUR_OF_DAY, -8);

		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			Calendar TADate = Calendar.getInstance();
			TADate.setTime(f.parse(date.replace("T", " ").replace("Z", "")));

			if (TADate.after(today)) {
				return true;
			} else {
				return false;
			}

		} catch (ParseException e) {
			_log.error("Unparsable date");
		}

		// if the dateCheck fails, display the date
		return true;
	}

}
