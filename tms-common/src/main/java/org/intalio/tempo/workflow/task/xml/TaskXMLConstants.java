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
 * $Id: TaskManagementServicesFacade.java 5440 2006-06-09 08:58:15Z imemruk $
 * $Log:$
 */

package org.intalio.tempo.workflow.task.xml;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMNamespace;

public final class TaskXMLConstants {

    public static final String TASK_NAMESPACE = "http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/";

    public static final String TASK_NAMESPACE_PREFIX = "tms";
    
    public static final String TASK_LOCAL_NAME = "task";
    
    public final static OMNamespace TASK_OM_NAMESPACE = OMAbstractFactory.getOMFactory().createOMNamespace(
            TASK_NAMESPACE, 
            TASK_NAMESPACE_PREFIX);
    
    public static final QName TASK_QNAME = new QName(TASK_NAMESPACE, TASK_LOCAL_NAME);

}
