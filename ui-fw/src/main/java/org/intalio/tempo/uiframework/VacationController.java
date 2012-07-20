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
import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.intalio.tempo.web.ApplicationState;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.json.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.intalio.tempo.workflow.task.Vacation;
import org.intalio.tempo.workflow.tms.server.dao.VacationDAOConnection;
import org.intalio.tempo.workflow.tms.server.dao.VacationDAOConnectionFactory;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VacationController implements Controller {
	private static final Logger LOG = LoggerFactory.getLogger(VacationController.class);
	private VacationDAOConnectionFactory vacationDAOFactory;
	VacationDAOConnection dao = null;
	JsonView json = null;
	DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	@SuppressWarnings("rawtypes")
	Map model = null;
	String message = "Failure";

	public VacationController(EntityManager em) {
	}

	public VacationController(VacationDAOConnectionFactory vacationDAOFactory, JsonView json) {
		this.vacationDAOFactory = vacationDAOFactory;
		this.json = json;
	}

	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			model = new LinkedHashMap();
			dao = vacationDAOFactory.openConnection();
			if (request.getParameter("action") != null && request.getParameter("action").equalsIgnoreCase("Validate")) {
				model = getVacationDetails(ApplicationState.getCurrentInstance(request).getCurrentUser().getName());
			} else if (request.getParameter("action") != null
					&& request.getParameter("action").equalsIgnoreCase("endVacation")) {
				if (request.getParameter("id") != null)
					model = deleteVacationDetails(Integer.parseInt(request.getParameter("id")));
			} else if (request.getParameter("action") != null
					&& request.getParameter("action").equalsIgnoreCase("insertVacation")) {
				if (request.getParameter("fromDate") != null && request.getParameter("toDate") != null
						&& request.getParameter("desc") != null)
					model = insertVacationDetails((Date) df.parse(request.getParameter("fromDate")),
							(Date) df.parse(request.getParameter("toDate")), request.getParameter("desc").trim(),
							ApplicationState.getCurrentInstance(request).getCurrentUser().getName());
			}
		} catch (Exception e) {
			message = e.getMessage();
			LOG.error("Failed to execute action. " + e.getMessage(), e);
		}
		return new ModelAndView(json, model);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map getVacationDetails(String user) {
		LOG.debug("getDetails=" + user);
		List<Vacation> vacationObj = dao.getVacationDetails(user);
		LOG.debug("vacationObj=" + vacationObj.size());
		if (vacationObj.size() >= 1) {
			model.put("vacation", vacationObj);
		}
		return model;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map deleteVacationDetails(int id) {
		Boolean bol = dao.deleteVacationDetails(id);
		if (bol) {
			dao.commit();
			message = "Deleted";
		} else
			message = "Error";
		model.put("message", message);
		return model;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map insertVacationDetails(Date fromDate, Date toDate, String description, String user) {
		Vacation vacationObj = new Vacation();
		vacationObj.set_from_Date(fromDate);
		vacationObj.set_to_Date(toDate);
		vacationObj.set_description(description);
		vacationObj.set_user(user);
		dao.insertVacationDetails(vacationObj);
		dao.commit();
		message = "Inserted";
		model.put("message", message);
		return model;
	}

}