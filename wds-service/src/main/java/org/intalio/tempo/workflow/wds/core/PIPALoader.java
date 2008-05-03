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
package org.intalio.tempo.workflow.wds.core;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.wds.servlets.InvalidRequestFormatException;
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

    public static final String HEADER_TASK_ROLE_OWNERS = "Task-RoleOwners";
    public static final String HEADER_TASK_USER_OWNERS = "Task-UserOwners";
    public static final String HEADER_PROCESS_INIT_ACTION = "Process-InitSOAPAction";
    public static final String HEADER_PROCESS_ENDPOINT = "Process-Endpoint";
    public static final String HEADER_FORM_NAMESPACE = "Form-Namespace";
    public static final String HEADER_TASK_DESCRIPTION = "Task-Description";
    public static final String HEADER_FORM_URL = "Form-URL";
    public static final String HEADER_TASK_ID = "Task-ID";
    
    public static final String PROPERTY_TASK_ROLE_OWNERS = "task-role-owners";
    public static final String PROPERTY_TASK_USER_OWNERS = "task-user-owners";
    public static final String PROPERTY_USER_PROCESS_INIT_ACTION = "userProcessInitSOAPAction";
    public static final String PROPERTY_PROCESS_ENDPOINT = "processEndpoint";
    public static final String PROPERTY_FORM_NS = "formNamespace";
    public static final String PROPERTY_DESCRIPTION = "task-description";
    public static final String PROPERTY_FORM_URI = "formURI";

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
     * Fetches PIPA task properties from HTTP request headers and builds a
     * PipaTask
     */
    public static PIPATask parsePipa(HttpServletRequest request) throws InvalidRequestFormatException {
        PIPATask task;
        try {
            task = new PIPATask(request.getHeader(HEADER_TASK_ID), new URI(request.getHeader(HEADER_FORM_URL)));

            task.setDescription(request.getHeader(HEADER_TASK_DESCRIPTION));
            task.setInitMessageNamespaceURI(URI.create(request.getHeader(HEADER_FORM_NAMESPACE)));
            task.setProcessEndpointFromString(request.getHeader(HEADER_PROCESS_ENDPOINT));
            task.setInitOperationSOAPAction(request.getHeader(HEADER_PROCESS_INIT_ACTION));

            String userOwnerHeader = request.getHeader(HEADER_TASK_USER_OWNERS);
            String[] userOwners = split(userOwnerHeader);
            task.setUserOwners(userOwners);

            String roleOwnerHeader = request.getHeader(HEADER_TASK_ROLE_OWNERS);
            String[] roleOwners = split(roleOwnerHeader);
            task.setRoleOwners(roleOwners);
            if (!task.isValid()) {
                throw new InvalidRequestFormatException("Invalid PIPA task:\n" + task);
            }
            return task;
        } catch (URISyntaxException e) {
            throw new InvalidRequestFormatException("Invalid PIPA task:",e);
        }
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