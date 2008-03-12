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
 * $Id: FormManager.java 2764 2006-03-16 18:34:41Z ozenzin $
 * $Log:$
 */
package org.intalio.tempo.uiframework.forms;

/**
 * Represents Forms Manager entity: provides its attributes required to interact
 * with Forms Manager definit implementation.
 *
 * @version $Revision: 691 $
 */
public interface FormManager {

    /**
     * Used in building the URL to Forms Manager
     *
     * @return URL to form manager for building forms that are associated with ""People Initiated Process" Tasks
     * business processes.
     */
    String getPeopleInitiatedProcessURL();

    /**
     * Used in building the URL to Forms Manager
     *
     * @return URL to form manager for building forms that are associated with "People Activity" Tasks
     */
    String getPeopleActivityURL();
    
    String getNotificationURL();
}
