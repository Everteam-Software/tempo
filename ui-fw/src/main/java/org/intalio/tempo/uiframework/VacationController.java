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

package org.intalio.tempo.uiframework;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.ws.RBACAdminClient;
import org.intalio.tempo.security.ws.RBACQueryClient;
import org.intalio.tempo.web.ApplicationState;
import org.intalio.tempo.web.controller.LoginController;
import org.intalio.tempo.workflow.task.Vacation;
import org.intalio.tempo.workflow.tms.ITaskManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.json.JsonView;

/**
 * This java file acts as a controller to vacation management insert's ,selects
 * & delete's the vacation details of a particular user.
 */
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
	Set<String> amRoles = null;

	/**
	 * RBACAdmin service Client.
	 */
	private static RBACAdminClient rbacAdminClient;

    /**
     * RBACQuery service Client.
     */
	private static RBACQueryClient rbacQueryClient;

    /**
     * all users with properties.
     */
	private static Map<String, Property[]> users;

	/**
	 * constructor for VacationController.
	 * @param jsonView JsonView
	 * @param adminClient RBACAdminClient
	 * @param queryClient RBACQueryClient
	 */
    public VacationController(final JsonView jsonView,
                              final RBACAdminClient adminClient,
                              final RBACQueryClient queryClient) {
            this.json = jsonView;
            rbacAdminClient = adminClient;
            rbacQueryClient = queryClient;
	}

    /**
     * vacation controller request handler.
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return ModelAndView ModelAndView
     * @throws ServletException ServletException
     * @throws IOException IOException
     */
    public final ModelAndView handleRequest(final HttpServletRequest request,
            final HttpServletResponse response) throws ServletException,
            IOException {
        try {
            amRoles = conf.getAbsenceManagerRoles();
            users = rbacAdminClient.getUsers();
            String endpoint = URIUtils.resolveURI(request, _endpoint);
            String pToken = getParticipantToken(request);
            taskManager = Configuration.getInstance().getTmsFactory()
                    .getService(endpoint, pToken);
            String name = null;
            String[] userRoles = null;
            String action = request.getParameter("action");
            ApplicationState appState = ApplicationState
                    .getCurrentInstance(request);
            if (appState != null) {
                name = appState.getCurrentUser().getName();
                userRoles = appState.getCurrentUser().getRoles();
            }
            model = new LinkedHashMap<String, Object>();
            if (action != null && name != null) {
                if (action.equalsIgnoreCase("Validate")) {
                    model = getVacationDetails(name);
                } else if (action.equalsIgnoreCase("list")) {
                    boolean isAbsenceManager = false;
                    if (userRoles != null && amRoles != null) {
                        if (CollectionUtils.containsAny(
                                        Arrays.asList(userRoles),
                                        amRoles)) {
                            isAbsenceManager = true;
                        }
                    }
                    if (isAbsenceManager) {
                        model = getAllVacationDetails();
                    } else {
                        model = getUserVacationDetails(name);
                    }
                    model.put("isAbsenceManager", isAbsenceManager);
                    model.put("isSubstituteMandatory",
                            isSubstituteMandatory.booleanValue());
                } else if (action.equalsIgnoreCase("match")) {
                    String fromDate = request.getParameter("fromDate");
                    String toDate = request.getParameter("toDate");
                    if (fromDate != null && toDate != null) {
                        model = getMatchedVacations(fromDate, toDate);
                    }
                } else if (action.equalsIgnoreCase("endVacation")) {
                    if (request.getParameter("id") != null) {
                        String[] vacIds = request.getParameter("id").split(",");
                        if(vacIds != null && vacIds.length > 0) {
                            model = deleteVacationDetails(vacIds);
                        }
                    }
                } else if (action.equalsIgnoreCase("insertVacation")) {
                    String fromDate = request.getParameter("fromDate");
                    String toDate = request.getParameter("toDate");
                    String desc = request.getParameter("desc");
                    String substitute = request.getParameter("substitute");
                    String user = request.getParameter("user");
                    if (fromDate != null && toDate != null && desc != null
                            && user != null && substitute != null) {
                        if (isVacationDetailsValid(user, substitute, fromDate, toDate, false, "")) {
                            model = insertVacationDetails(fromDate, toDate,
                                    desc.trim(), user, substitute);
                        }
                    }
                } else if (action.equalsIgnoreCase("editVacation")) {
                    String id = request.getParameter("id");
                    String fromDate = request.getParameter("fromDate");
                    String toDate = request.getParameter("toDate");
                    String desc = request.getParameter("desc");
                    String substitute = request.getParameter("substitute");
                    String user = request.getParameter("user");
                    if (user != null && !"".equals(user)) {
                        name = user;
                    }
                    if (id != null && fromDate != null && toDate != null
                            && desc != null && substitute != null) {
                        if (isVacationDetailsValid(user, substitute, fromDate, toDate, true, id)) {
                            model = editVacationDetails(id, fromDate, toDate,
                                    desc.trim(), name, substitute);
                        }
                    }
                } else if (action.equalsIgnoreCase("getUsers")) {
                    model.put("users",
                            getAssignedUsers(Arrays.asList(userRoles)));
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
			updateUserNames(vac);
			model.put("vacs", vac);
		} catch (Exception e) {
			LOG.error("Exception while fetching vacation records. " + e.getMessage(), e);
		}
		return model;
	}

    /**
     * Gets the vacation details of all users.
     * @return Map<String, Object> Vacations
     */
    public final Map<String, Object> getAllVacationDetails() {
        try {
            List<Vacation> vac = taskManager.getVacationList();
            updateUserNames(vac);
            model.put("vacs", vac);
        } catch (Exception e) {
            LOG.error(
                    "Exception while fetching vacation records. "
                            + e.getMessage(), e);
        }
        return model;
    }

	public Map<String, Object> deleteVacationDetails(String[] ids) {
		taskManager.deleteVacation(ids);
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
		if (state == null) {
		    LOG.error("Not able to get ParticipantToken, ApplicationState null.");
		    return null;
		}
		return state.getCurrentUser().getToken();
	}
	
	/**
	 * gets matched Vacation Details for given dates 
	 */
	protected Map<String, Object> getMatchedVacations(String fromDate, String toDate){
		try {
			List<Vacation> vac = taskManager.getMatchedVacations(fromDate, toDate);
			updateUserNames(vac);
			model.put("vacs", vac);
		} catch (Exception e) {
			LOG.error("Exception while fetching vacation records. " + e.getMessage(), e);
		}
		return model;
	}

    /**
     * gets matched Vacation Details for given dates.
     * @param substitute String
     * @param fromDate String
     * @param toDate String
     * @return isSubstituteValid boolean
     */
    protected final boolean validateSubstitute(final String substitute,
            final String fromDate, final String toDate) {
        boolean isSubstituteValid = true;
        try {
            List<Vacation> vac = taskManager.getUserMatchedVacations(
                    substitute, fromDate, toDate);
            if (vac != null && vac.size() > 0) {
                isSubstituteValid = false;
            }
        } catch (Exception e) {
            LOG.error(
                    "Exception while fetching vacation records. "
                            + e.getMessage(), e);
        }
        return isSubstituteValid;
    }

    /**
     * gets matched Vacation Details for given dates.
     * @param user String
     * @param fromDate String
     * @param toDate String
     * @return isUserValid boolean
     */
    protected final boolean validateUser(final String user,
            final String fromDate, final String toDate, final boolean isUpdate,
            final String vacId) {
        boolean isUserValid = true;
        try {
            List<Vacation> vac = taskManager.getUserMatchedVacations(
                    user, fromDate, toDate);
            int count = 0;
            if (vac != null && isUpdate) {
                for(Vacation v : vac) {
                    if (vacId.equals(String.valueOf(v.getId()))) {
                        count = 1;
                        break;
                    }
                }
            }
            if (vac != null && vac.size() > count) {
                isUserValid = false;
            }
        } catch (Exception e) {
            LOG.error(
                    "Exception while fetching vacation records. "
                            + e.getMessage(), e);
        }
        return isUserValid;
    }

    /**
     * to validate given dates.
     * @param fromDate String
     * @param toDate String
     * @return isDatesValid boolean
     */
    protected final boolean validateDates(final String fromDate,
            final String toDate, final boolean isUpdate) {
        boolean isDatesValid = true;
        try {
            Date startDate = format.parse(fromDate);
            Date endDate = format.parse(toDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date today = cal.getTime();
            if (endDate.before(today) || endDate.before(startDate)) {
                isDatesValid = false;
            } else if (!isUpdate && startDate.before(today)) {
                isDatesValid = false;
            }
        } catch (ParseException e) {
            LOG.error("Exception while parsing dates. " + e.getMessage(), e);
        }
        return isDatesValid;
    }

    /**
     * to validate given dates.
     * @param fromDate String
     * @param toDate String
     * @return isDatesValid boolean
     */
    protected final boolean isVacationDetailsValid(final String user,
            final String substitute, final String fromDate,
            final String toDate, final boolean isUpdate, final String vacId) {
        boolean isVacationValid = false;
        boolean isDatesValid = true;
        boolean isUserValid = true;
        boolean isSubstituteValid = true;
        if (!validateDates(fromDate, toDate, isUpdate)) {
            message = "Invalid Date Range";
            isDatesValid = false;
        } else if (!validateUser(user, fromDate, toDate, isUpdate, vacId)) {
            message = "Invalid Vacation Dates";
            isUserValid = false;
        } else if (!validateSubstitute(substitute, fromDate, toDate)) {
            message = "Invalid Substitute";
            isSubstituteValid = false;
        }
        model.put("message", message);
        if (isDatesValid && isUserValid && isSubstituteValid) {
            isVacationValid = true;
        }
        return isVacationValid;
    }

    /**
     * get assigned user for login user roles.
     * @param userRoles List<String>
     * @return users List<Property>
     */
    private static List<Property> getAssignedUsers(
            final List<String> userRoles) {
        List<Property> assignedUsers = new ArrayList<Property>();
        Set<String> matchedUsers = new HashSet<String>();
        for (String role : userRoles) {
           String[] usrs = null;
                   try {
                       usrs = rbacQueryClient.getAssignedUsers(role);
                } catch (Exception e) {
                    LOG.error(
                            "Exception while getting Assigned Users "
                                    + e.getMessage(), e);
                }
           matchedUsers.addAll(Arrays.asList(usrs));
        }
        if (users != null) {
            for (String user : matchedUsers) {
                String name = getUserDisplayName(user);
                assignedUsers.add(new Property(user, name));
            }
        }
        return assignedUsers;
    }

    /**
     * update vacation list with users actual names.
     * @param vacs List<Vacation>
     */
    private static void updateUserNames(final List<Vacation> vacs) {
        if (vacs != null) {
            for (Vacation v:vacs) {
               v.setUserName(getUserDisplayName(v.getUser()));
               v.setSubstituteName(getUserDisplayName(v.getSubstitute()));
            }
         }
    }

    /**
     * get User Display Name.
     * @param user String
     * @return displayName String
     */
    private static String getUserDisplayName(final String user) {
        String name = user;
        if (users != null) {
            Property[] props = users.get(user);
            if (props != null) {
                name = LoginController
                        .extractUserDisplayName(users.get(user));
            }
        }
        if (name == null || "".equals(name)) { name = user; }
        return name;
    }

}
