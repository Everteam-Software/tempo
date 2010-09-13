package org.intalio.tempo.uiframework.forms;

import junit.framework.TestCase;

public class XFormsManagerTest extends TestCase {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        junit.textui.TestRunner.run(XFormsManagerTest.class);
    }

    public void test() {
        XFormsManager manager = new XFormsManager();
        String notificationURL = "notificationURL";
        String peopleActivityURL = "peopleActivityURL";
        String peopleInitiatedProcessURL = "peopleInitiatedProcessURL";

        try {
            manager.setNotificationURL(notificationURL);
            manager.setPeopleActivityURL(peopleActivityURL);
            manager.setPeopleInitiatedProcessURL(peopleInitiatedProcessURL);
        } catch (Exception e) {
            assertTrue(false);
        }
        
        assertEquals(manager.getNotificationURL(null), notificationURL);
        assertEquals(manager.getPeopleActivityURL(null), peopleActivityURL);
        assertEquals(manager.getPeopleInitiatedProcessURL(null), peopleInitiatedProcessURL);
    }
}
