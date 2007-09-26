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
 * $Id: XFormsManager.java 2764 2006-03-16 18:34:41Z ozenzin $
 * $Log:$
 */
package org.intalio.tempo.uiframework.forms;

import org.intalio.tempo.uiframework.forms.FormManager;

import java.net.URISyntaxException;

/**
 * Used to handle redirect to Form Manager component of Intalio|BPMS Workflow.
 * Represents XForm Manager: provides its attributes required to interact with it.
 * This class also conforms to Managed Beans idiom of JSF, that's why setters
 * provided: to support configuration via <code>faces-config.xml</code> 
 *
 * @version $Revision: 1124 $
 */
public class XFormsManager implements FormManager {

    private String _peopleInitiatedProcessURL;

    private String _peopleActivityURL;

    private String _notificationURL;

    /**
     * Responsible for url to form manager for XForms that are associated with "People Initiated Process" Tasks.
     * @return url to form manager for XForms that are associated with "People Initiated Process" Tasks.
     */
    public String getPeopleInitiatedProcessURL() {
        return _peopleInitiatedProcessURL;
    }

    /**
     * Responsible for url to form manager for XForms that are associated with "People Initiated Process" Tasks.
     * @param peopleInitiatedProcessURL - should be preset in faces-context.xml
     */
    public void setPeopleInitiatedProcessURL(String peopleInitiatedProcessURL) throws URISyntaxException {
        _peopleInitiatedProcessURL = peopleInitiatedProcessURL;
    }

    /**
     * Responsible for url to form manager for XForms that are associated with "People Activity" Tasks.
     * @return url to form manager for XForms that are associated with "People Activity" Tasks.
     */
    public String getPeopleActivityURL() {
        return _peopleActivityURL;
    }

    /**
     * Responsible for url to form manager for XForms that are associated with "People Activity" Tasks.
     * @param peopleActivityURL - should be preset in faces-config.xml
     */
    public void setPeopleActivityURL(String peopleActivityURL) throws URISyntaxException {
        _peopleActivityURL = peopleActivityURL;
    }

    public String getNotificationURL() {
        return _notificationURL;
    }
    
    public void setNotificationURL(String url) {
        _notificationURL = url;
    }
}
