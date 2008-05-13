package org.intalio.tempo.uiframework.forms;

import java.util.HashMap;
import java.util.Map;

import javax.management.Notification;

import org.intalio.tempo.workflow.task.PATask;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.Task;

public class GenericFormManager implements FormManager {
    public static final String NOTIFICATION = Notification.class.getSimpleName();
    public static final String PIPA = PIPATask.class.getSimpleName();
    public static final String PA = PATask.class.getSimpleName();
    public static final String DEFAULT = "default";

    Map<String, Map<String, String>> _mappings;

    public GenericFormManager() {

    }

    public void setMappings(Map<String, Map<String, String>> mappings) {
        _mappings = mappings;
    }

    public String getNotificationURL(Task t) {
        return getURL(t, NOTIFICATION);
    }

    private String getURL(final Task t, final String staticTaskType) {
        HashMap<String, String> map = (HashMap<String, String>) _mappings.get(staticTaskType);
        String formURL = t.getFormURLAsString();
        if(formURL!=null) {
            String key = formURL.subSequence(formURL.lastIndexOf(".") + 1, formURL.length()).toString();
            if(key!=null && map.containsKey(key)) {
                return map.get(key);    
            }
        }
        return map.get(DEFAULT);
    }

    public String getPeopleActivityURL(Task t) {
        return getURL(t, PA);
    }

    public String getPeopleInitiatedProcessURL(Task t) {
        return getURL(t, PIPA);
    }

}
