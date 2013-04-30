/**
 * Copyright (c) 2005-2006 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 *
 * $Id: VacationController.java 5440 2006-06-09 08:58:15Z imemruk $
 * $Log:$
 */

/**
 *  This java file acts as a controller to vacation management insert's ,selects & delete's the vacation details of a particular user  
 */
package org.intalio.tempo.uiframework;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.intalio.tempo.web.ApplicationState;
import org.intalio.tempo.workflow.task.Vacation;
import org.intalio.tempo.workflow.tms.ITaskManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.json.JsonView;

public class VacationController implements Controller {
	private static final Logger LOG = LoggerFactory.getLogger(VacationController.class);
	JsonView json = null;
	Map<String, Object> model = null;
	String message = "Failure";
	Configuration conf = Configuration.getInstance();
	String _endpoint = conf.getServiceEndpoint();
	ITaskManagementService taskManager = null;
	SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
	SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
	Boolean  isSubstituteMandatory = conf.isSubstituteMandatory();
	Set<String> amRoles = conf.getAbsenceManagerRoles();
	public VacationController(JsonView json) {
		this.json = json;
	}

	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			String endpoint = URIUtils.resolveURI(request, _endpoint);
			String pToken = getParticipantToken(request);
			taskManager = Configuration.getInstance().getTmsFactory().getService(endpoint, pToken);
			String name = null;
			String[] userRoles = null;
			String action = request.getParameter("action");
			ApplicationState appState = ApplicationState.getCurrentInstance(request);
			if (appState != null){
				name = appState.getCurrentUser().getName();
				userRoles = appState.getCurrentUser().getRoles();
			}
			model = new LinkedHashMap<String, Object>();
			if(action != null && name!=null){
				if (action.equalsIgnoreCase("Validate")) {
						model = getVacationDetails(name);
				}else if (action.equalsIgnoreCase("list")) {
					model = getUserVacationDetails(name);
					if(amRoles != null && CollectionUtils.containsAny(amRoles,Arrays.asList(userRoles))){
						model.put("isAbsenceManager", "true");
					}
					model.put("isSubstituteMandatory", isSubstituteMandatory.booleanValue());
				} else if (action.equalsIgnoreCase("match")) {
					String fromDate = request.getParameter("fromDate");
					String toDate = request.getParameter("toDate");
					if (fromDate != null && toDate != null)
						model = getMatchedVacations(fromDate, toDate);
				} else if (action.equalsIgnoreCase("endVacation")) {
					if (request.getParameter("id") != null)
						model = deleteVacationDetails(request.getParameter("id"));
				} else if (action.equalsIgnoreCase("insertVacation")) {
					String fromDate = request.getParameter("fromDate");
					String toDate = request.getParameter("toDate");
					String desc = request.getParameter("desc");
					String substitute = request.getParameter("substitute");
					if (fromDate != null && toDate != null && desc != null && substitute != null)
						model = insertVacationDetails(fromDate,toDate,desc.trim(), name, substitute);
				}  else if (action.equalsIgnoreCase("insertProxyVacation")) {
					String fromDate = request.getParameter("fromDate");
					String toDate = request.getParameter("toDate");
					String desc = request.getParameter("desc");
					String substitute = request.getParameter("substitute");
					String user = request.getParameter("user");
					if (fromDate != null && toDate != null && desc != null && user != null && substitute != null)
						model = insertVacationDetails(fromDate,toDate,desc.trim(), user, substitute);
				} else if (action.equalsIgnoreCase("editVacation")) {
					String id = request.getParameter("id");
					String fromDate = request.getParameter("fromDate");
					String toDate = request.getParameter("toDate");
					String desc = request.getParameter("desc");
					String substitute = request.getParameter("substitute");
					if (id != null && fromDate != null && toDate != null && desc != null && substitute != null)
						model = editVacationDetails(id, fromDate, toDate, desc.trim(), name, substitute);
				}
			}
		} catch (Exception e) {
			message = e.getMessage();
			LOG.error("Failed to execute action. " + e.getMessage(), e);
		}
		return new ModelAndView(json, model);
	}

	public Map<String, Object> getVacationDetails(String user) {
		try {
			List<Vacation> vac = taskManager.getUserVacation(user);
			if (vac.size() >= 1) {
				for (int i = 0; i < vac.size(); i++) {
					model.put("vacId", vac.get(i).getId());
					model.put("vacDesc", vac.get(i).getDescription());
					model.put("vacFromdate", format.format(df.parse(vac.get(i).getFromDate().toString())));
					model.put("vacToDate", format.format(df.parse(vac.get(i).getToDate().toString())));
					model.put("vacUser", vac.get(i).getUser());
				}
			}
		} catch (ParseException e) {
			LOG.error("Failed to parse. " + e.getMessage(), e);
		} catch (Exception e) {
			LOG.error("Exception while fetching vacation record. " + e.getMessage(), e);
		}
		return model;
	}
	
	/**
	 * Gets the vacation details of a particular user
	 */
	public Map<String, Object> getUserVacationDetails(String user) {
		try {
			List<Vacation> vac = taskManager.getUserVacation(user);
			model.put("vacs", vac);
		} catch (Exception e) {
			LOG.error("Exception while fetching vacation records. " + e.getMessage(), e);
		}
		return model;
	}

	public Map<String, Object> deleteVacationDetails(String id) {
		taskManager.deleteVacation(id);
		message = "Deleted";
		model.put("message", message);
		return model;
	}

	public Map<String, Object> insertVacationDetails(String fromDate, String toDate, String description, String user) {
		
		return this.insertVacationDetails(fromDate, toDate, description, user, null);
	}
	
	/**
	 * Inserts Vacation Details 
	 */
	public Map<String, Object> insertVacationDetails(String fromDate, String toDate, String description, String user, String substitute) {
		taskManager.insertVacation(fromDate, toDate, description, user,substitute);
		message = "Inserted";
		model.put("message", message);
		return model;
	}
	
	/**
	 * updates Vacation Details 
	 */
	public Map<String, Object> editVacationDetails(String id, String fromDate, String toDate, String description, String user, String substitute) {
		try {
			Vacation vac = new Vacation();
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		
			vac.setFromDate(df.parse(fromDate));
			vac.setToDate(df.parse(toDate));
			vac.setDescription(description);
			vac.setUser(user);
			vac.setSubstitute(substitute);
			vac.setId(Integer.parseInt(id));
			taskManager.updateVacation(vac);
			message = "Updated";
			model.put("message", message);
			
			} catch (Exception e) {
				LOG.error("Exception :: ", e);
			}
		return model;
	}

	protected String getParticipantToken(HttpServletRequest request) {
		ApplicationState state = ApplicationState.getCurrentInstance(request);
		return state.getCurrentUser().getToken();
	}
	
	/**
	 * gets matched Vacation Details for given dates 
	 */
	protected Map<String, Object> getMatchedVacations(String fromDate, String toDate){
		try {
			List<Vacation> vac = taskManager.getMatchedVacations(fromDate, toDate);
			model.put("vacs", vac);
		} catch (Exception e) {
			LOG.error("Exception while fetching vacation records. " + e.getMessage(), e);
		}
		return model;
	}

}