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
package org.intalio.tempo.workflow.tms.server;

import java.net.URI;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.UUID;

import org.intalio.tempo.workflow.task.PIPATask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class Helper to load/create a PIPA class from either:
 * <ul>
 * <li>an http request and its content headers</li>
 * <li>a set of java <code>Properties</code></li>
 * </uL>
 *
 */
public class PIPALoader {
    private static final Logger logger = LoggerFactory.getLogger(PIPALoader.class);

    private static final String PROPERTY_TASK_ROLE_OWNERS = "task-role-owners";
    private static final String PROPERTY_TASK_USER_OWNERS = "task-user-owners";
    private static final String PROPERTY_USER_PROCESS_INIT_ACTION = "userProcessInitSOAPAction";
    private static final String PROPERTY_PROCESS_ENDPOINT = "processEndpoint";
    private static final String PROPERTY_FORM_NS = "formNamespace";
    private static final String PROPERTY_DESCRIPTION = "task-description";
    private static final String PROPERTY_FORM_URI = "formURI";

    public static PIPATask parsePipa(Properties prop) {
        logger.info("Parse PIPA using properties: " + prop.toString());

        PIPATask task = new PIPATask(UUID.randomUUID().toString(), prop.getProperty(PROPERTY_FORM_URI));
        task.setDescription(prop.getProperty(PROPERTY_DESCRIPTION));
        task.setInitMessageNamespaceURI(URI.create(prop.getProperty(PROPERTY_FORM_NS)));
        task.setProcessEndpointFromString(prop.getProperty(PROPERTY_PROCESS_ENDPOINT));
        task.setInitOperationSOAPAction(prop.getProperty(PROPERTY_USER_PROCESS_INIT_ACTION));

        String userOwnerHeader = prop.getProperty(PROPERTY_TASK_USER_OWNERS);
        String[] userOwners = split(userOwnerHeader);
        task.setUserOwners(userOwners);

        String roleOwnerHeader = prop.getProperty(PROPERTY_TASK_ROLE_OWNERS);
        String[] roleOwners = split(roleOwnerHeader);
        task.setRoleOwners(roleOwners);
        return task;
    }

    /**
     * Splits comma-delimited values into array
     */
    private static String[] split(String source) {
        if (source == null)
            return new String[0];
        ArrayList<String> list = new ArrayList<String>();
        StringTokenizer tok = new StringTokenizer(source, ",");
        while (tok.hasMoreTokens())
            list.add(tok.nextToken());
        return list.toArray(new String[list.size()]);
    }
}