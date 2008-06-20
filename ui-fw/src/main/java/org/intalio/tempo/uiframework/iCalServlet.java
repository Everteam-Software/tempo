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

package org.intalio.tempo.uiframework;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.ValidationException;
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
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.Task;

public class iCalServlet extends ExternalTasksServlet {
    private static final long serialVersionUID = -76889544882620584L;

    public void generateFile(HttpServletRequest request, String pToken, String user, Task[] tasks, FormManager fmanager, ServletOutputStream outputStream)
                    throws URISyntaxException, IOException, ValidationException {
        
        Calendar calendar = new Calendar();
        calendar.getProperties().add(new ProdId("-//Ben Fortuna//iCal4j 1.0//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);

        for (Task t : tasks) {
            if(!(t instanceof PIPATask)) { 
                VEvent task = new VEvent();
                task.getProperties().add(new DtStart(new DateTime(t.getCreationDate()), false));
                task.getProperties().add(new Description(t.getDescription()));
                task.getProperties().add(new Uid(t.getID()));
                task.getProperties().add(new Summary(t.getDescription()));
                Url url = new Url();
                url.setUri(URIUtils.getResolvedTaskURL(request, fmanager, t, pToken, user));
                task.getProperties().add(url);
                calendar.getComponents().add(task);
            }
            
        }
        new CalendarOutputter().output(calendar, outputStream);
    }

    public String getFileMimeType() {
        return "text/calendar";
    }

    public String getFileExt() {
        return ".ics";
    }

}
