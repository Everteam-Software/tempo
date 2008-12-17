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
package org.intalio.tempo.uiframework.forms;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import javax.management.Notification;

import org.apache.commons.lang.StringUtils;
import org.intalio.tempo.workflow.task.PATask;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.Task;

/**
 * This form manager is initialized with a set of mappings.
 * When requested a form URL for a task it returns by default the one defined in the task
 * If it cannot find it, it returns the corresponding URL obtained from a matching regexp
 *
 */
public class GenericFormManager implements FormManager {
    public static final String NOTIFICATION = Notification.class.getSimpleName();
    public static final String PIPA = PIPATask.class.getSimpleName();
    public static final String PA = PATask.class.getSimpleName();
    public static final String DEFAULT = "default";

    private Map<String, Map<Pattern, String>> _mappings;

    public GenericFormManager() {

    }

    /**
     * Initialized all the regexp
     */
    public void setMappings(Map<String, Map<String, String>> mappings) {
        Map<String, Map<Pattern, String>> mappingsWithRegExp = new HashMap<String, Map<Pattern, String>>();
        Iterator<String> keys = mappings.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            Map<String, String> entry = mappings.get(key);
            Map<Pattern, String> entryWithRegexp = new HashMap<Pattern, String>(entry.size());
            Iterator<String> regexpEntries = entry.keySet().iterator();
            while (regexpEntries.hasNext()) {
                String regexp = regexpEntries.next();
                Pattern pattern = Pattern.compile(regexp);
                entryWithRegexp.put(pattern, entry.get(regexp));
            }
            mappingsWithRegExp.put(key, entryWithRegexp);
        }
        _mappings = mappingsWithRegExp;
    }

    public String getNotificationURL(Task t) {
        return getURL(t, NOTIFICATION);
    }
    
    public String getURL(Task t) {
        return getURL(t, t.getClass().getSimpleName());
    }

    private String getURL(final Task t, final String staticTaskType) {
        HashMap<Pattern, String> map = (HashMap<Pattern, String>) _mappings.get(staticTaskType);
        String formURL = t.getFormURLAsString();
        if (formURL == null) {
            return StringUtils.EMPTY;
        }
        Iterator<Pattern> patterns = map.keySet().iterator();
        while (patterns.hasNext()) {
            Pattern pattern = patterns.next();
            if (pattern.matcher(formURL).matches()) {
                return map.get(pattern);
            }
        }
        return formURL;
    }

    public String getPeopleActivityURL(Task t) {
        return getURL(t, PA);
    }

    public String getPeopleInitiatedProcessURL(Task t) {
        return getURL(t, PIPA);
    }

}
