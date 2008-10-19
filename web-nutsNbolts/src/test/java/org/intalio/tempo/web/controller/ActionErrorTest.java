package org.intalio.tempo.web.controller;

import java.util.Locale;

import junit.framework.TestCase;

import org.junit.Test;

public class ActionErrorTest extends TestCase{
    
    @Test
    public void testActionErrorSimple() throws Exception {
        ActionError ae = new ActionError("error msg", "error msg detail");
        assertEquals(ae.getMessage(),"error msg");
        assertEquals(ae.getDetails(), "error msg detail");
    }
    
    @Test
    public void testActionError() throws Exception {
        ActionError ae = new ActionError(-1, "target", "com_intalio_bpms_workflow_taskHolder_priority", new String[]{"message arg1"}, "detail", "com_intalio_bpms_workflow_taskHolder_taskState", new String[]{"detail keys1"});
        assertEquals(ae.getCode(), -1);
        assertEquals(ae.getDetails(), "detail");
        assertEquals(ae.getMessage(), "com_intalio_bpms_workflow_taskHolder_priority");
        assertEquals(ae.getTarget(), "target");
        assertTrue(ae.getFormattedDetail().contains("State"));
        assertTrue(ae.getFormattedDetail(Locale.FRANCE).contains("Statut"));
        assertTrue(ae.getFormattedMessage().contains("Priority"));
        assertTrue(ae.getFormattedMessage(Locale.FRANCE).contains("Urgence"));
        assertEquals(ae.getMessageArguments().length, 1);
        assertEquals(ae.getDetailArguments().length, 1);
        assertTrue(ActionError.getMessage("com_intalio_bpms_workflow_tab_processes", Locale.US).contains("Processes"));
    }
}
