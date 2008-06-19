/**
 * Copyright (c) 2005-2007 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 */

package org.intalio.tempo.uiframework;

import java.io.IOException;
import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Url;
import net.fortuna.ical4j.model.property.Version;

import org.intalio.tempo.uiframework.forms.FormManager;
import org.intalio.tempo.uiframework.forms.FormManagerBroker;
import org.intalio.tempo.web.ApplicationState;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.tms.ITaskManagementService;
import org.intalio.tempo.workflow.tms.client.RemoteTMSFactory;

public class iCalServlet extends HttpServlet {
    private static final long serialVersionUID = -76889544882620584L;

    private final Configuration conf = Configuration.getInstance();
        
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String pToken = getParticipantToken(request);
            ApplicationState state = ApplicationState.getCurrentInstance(request);
            String user = state.getCurrentUser().getName();
            ITaskManagementService taskManager = getTMS(request, pToken);
            Task[] tasks = taskManager.getAvailableTasks("Task", "ORDER BY T._creationDate");

            Calendar calendar = new Calendar();
            calendar.getProperties().add(new ProdId("-//Ben Fortuna//iCal4j 1.0//EN"));
            calendar.getProperties().add(Version.VERSION_2_0);
            calendar.getProperties().add(CalScale.GREGORIAN);
            
            FormManager fmanager = FormManagerBroker.getInstance().getFormManager();

            for(Task t : tasks) {
                VEvent task = new VEvent();
                task.getProperties().add(new DtStart(new DateTime(t.getCreationDate()),false));
                task.getProperties().add(new Description(t.getDescription()));
                task.getProperties().add(new Uid(t.getID()));
                task.getProperties().add(new Summary(t.getDescription()));
                Url url = new Url();
                url.setUri(URIUtils.getResolvedTaskURL(request, fmanager, t, pToken, user));
                task.getProperties().add(url);
                calendar.getComponents().add(task);
            }
                        
            
            String filename = "tasks for "+user+".ics";
            CalendarOutputter outputter = new CalendarOutputter();
            response.setContentType("text/calendar");
            response.addHeader("Content-disposition", "attachment; filename=\""      + filename + "\"");
            outputter.output(calendar, response.getOutputStream());
            
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
    
    protected String getParticipantToken(HttpServletRequest request) {
        ApplicationState state = ApplicationState.getCurrentInstance(request);
        return state.getCurrentUser().getToken();
    }
    
    protected ITaskManagementService getTMS(HttpServletRequest request, String participantToken) throws Exception {
        String endpoint = URIUtils.resolveURI(request, conf.getServiceEndpoint());
        return new RemoteTMSFactory(endpoint, participantToken).getService();
    }
    
}
