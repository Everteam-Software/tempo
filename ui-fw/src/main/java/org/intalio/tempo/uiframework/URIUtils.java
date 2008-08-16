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
 * $Id: XFormsManager.java 2764 2006-03-16 18:34:41Z ozenzin $
 * $Log:$
 */
package org.intalio.tempo.uiframework;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.intalio.tempo.uiframework.forms.FormManager;
import org.intalio.tempo.workflow.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class URIUtils {
    private static final String LOCALHOST_LOCAL = "hostname.intalio";
    static Logger _log = LoggerFactory.getLogger(URIUtils.class);

    public static String resolveURI(HttpServletRequest request, String endpoint) {
        return resolveURI(request.getScheme(), request.getServerName(), request.getServerPort(), endpoint);
    }

    public static String resolveURI(String scheme, String serverName, int serverPort, String endpoint) {
        try {
            URI uri = new URI(endpoint);
            if (!uri.isAbsolute()) {
                uri = new URI(scheme, null, LOCALHOST_LOCAL, serverPort, null, null, null).resolve(uri);
            }
            return uri.toString().replaceAll(LOCALHOST_LOCAL, serverName);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getFormURLForTask(FormManager fm, Task t, String ticket, String user) {
        try {
            Object[] params = new Object[] { fm.getURL(t), t.getID(), t.getClass().getSimpleName(), URLEncoder.encode(t.getFormURLAsString(), "UTF-8"), ticket,
                            URLEncoder.encode(user, "UTF-8") };
            return MessageFormat.format("{0}?id={1}&type={2}&url={3}&token={4}&user={5}", params);
        } catch (UnsupportedEncodingException e) {
            // if utf-8 isnot available we're bad :)
            throw new RuntimeException(e);
        }
    }

    public static URI getResolvedTaskURL(HttpServletRequestWrapper request, FormManager fm, Task t, String ticket, String user) {
        try {
            return URI.create(resolveURI(request, getFormURLForTask(fm, t, ticket, user)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getResolvedTaskURLAsString(HttpServletRequestWrapper request, FormManager fm, Task t, String ticket, String user) {
        return getResolvedTaskURL(request, fm, t, ticket, user).toString();
    }
}
