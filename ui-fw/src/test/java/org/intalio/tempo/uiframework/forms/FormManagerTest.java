package org.intalio.tempo.uiframework.forms;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.intalio.tempo.uiframework.forms.GenericFormManager;
import org.intalio.tempo.workflow.task.PIPATask;
import org.junit.Assert;
import org.junit.Test;

public class FormManagerTest {

    @Test
    public void testGenericFormManager() throws Exception {
        GenericFormManager gfm = new GenericFormManager();
        Map<String, Map<String, String>> mappings = new HashMap<String, Map<String, String>>();
        java.util.HashMap<String, String> notification = new HashMap<String, String>();
        notification.put("xform", "/xFormsManager/notification");
        notification.put("gi", "/giFormsManager/notification");
        notification.put("ruby", "/rubyFormsManager/notification");
        java.util.HashMap<String, String> pipa = new HashMap<String, String>();
        pipa.put("xform", "/xFormsManager/init");

        mappings.put(GenericFormManager.NOTIFICATION, notification);
        mappings.put(GenericFormManager.PIPA, pipa);
        gfm.setMappings(mappings);

        PIPATask task1 = new PIPATask("1223", new URI("/AbsenceRequest/AbsenceRequest.xform"));
        String url = gfm.getPeopleInitiatedProcessURL(task1);
        Assert.assertEquals(url, "/xFormsManager/init");
    }
}
