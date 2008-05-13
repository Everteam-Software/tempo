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
 *
 * $Id: FormManager.java 2764 2006-03-16 18:34:41Z ozenzin $
 * $Log:$
 */

package org.intalio.tempo.uiframework.forms;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.intalio.tempo.workflow.task.Notification;
import org.intalio.tempo.workflow.task.PIPATask;
import org.junit.Assert;
import org.junit.Test;

public class FormManagerTest {

    @Test
    public void testGenericFormManager() throws Exception {
        GenericFormManager gfm = new GenericFormManager();
        Map<String, Map<String, String>> mappings = new HashMap<String, Map<String, String>>();
        java.util.HashMap<String, String> notification = new HashMap<String, String>();
        notification.put(".*xform", "/xFormsManager/notification");
        java.util.HashMap<String, String> pipa = new HashMap<String, String>();
        pipa.put(".*xform", "/xFormsManager/init");

        mappings.put(GenericFormManager.NOTIFICATION, notification);
        mappings.put(GenericFormManager.PIPA, pipa);
        gfm.setMappings(mappings);

        PIPATask task1 = new PIPATask("1223", new URI("/AbsenceRequest/AbsenceRequest.xform"));
        Assert.assertSame(gfm.getPeopleInitiatedProcessURL(task1), "/xFormsManager/init");

        PIPATask task2 = new PIPATask("1223", new URI("/AbsenceRequest/AbsenceRequest.gi"));
        Assert.assertEquals(gfm.getPeopleInitiatedProcessURL(task2), "/AbsenceRequest/AbsenceRequest.gi");

        Notification task3 = new Notification("123", new URI("/AbsenceRequest/AbsenceRequest.xform"));
        Assert.assertSame(gfm.getNotificationURL(task3), "/xFormsManager/notification");
        Assert.assertSame(gfm.getURL(task3), "/xFormsManager/notification");
        
        String fullURL = "http://localhost:8080/giframework/task1";
        PIPATask taskWithFullFormURL = new PIPATask("123", new URI(fullURL));
        Assert.assertSame(gfm.getURL(taskWithFullFormURL), fullURL);
    }
}
