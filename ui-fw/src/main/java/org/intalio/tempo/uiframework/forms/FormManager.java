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

import org.intalio.tempo.workflow.task.Task;

/**
 * Represents Forms Manager entity: provides its attributes required to interact
 * with Forms Manager definit implementation.
 *
 * @version $Revision: 691 $
 */
public interface FormManager {

    String getPeopleInitiatedProcessURL(Task t);
    String getPeopleActivityURL(Task t);
    String getNotificationURL(Task t);
    
    String getURL(Task t);
}
