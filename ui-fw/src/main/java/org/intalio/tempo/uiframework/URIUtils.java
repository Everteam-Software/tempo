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
package org.intalio.tempo.uiframework;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;

import org.intalio.tempo.uiframework.forms.FormManager;
import org.intalio.tempo.workflow.task.Task;

public class URIUtils {
    
    public static String resolveURI(HttpServletRequest request, String endpoint) 
        throws URISyntaxException
    {
        URI uri = new URI(endpoint);
        if (!uri.isAbsolute()) {
            uri = new URI(request.getScheme(), null, request.getServerName(), request.getServerPort(), 
                          null, null, null).resolve(uri);
        }
        return uri.toString();
    }
    
    public static String getFormURLForTask(FormManager fm, Task t, String ticket, String user) {
        Object[] params = new Object[] { fm.getURL(t), t.getID(), t.getClass().getSimpleName(), t.getFormURLAsString(), ticket, user };
        return MessageFormat.format("{0}?id={1}&type={2}&url={3}&token={4}&user={5}", params);
    }
    
    public static URI getResolvedTaskURL(HttpServletRequest request, FormManager fm, Task t, String ticket, String user) throws URISyntaxException {
        return URI.create(resolveURI(request, URLEncoder.encode(getFormURLForTask(fm, t, ticket, user))));
    }
    
    public static void main(String[] args) throws Exception {
        String url = "/xFormsManager/act?id=a22b1230966ea393:23eeb00d:11a9247675e:-7ff564.9.233.993202&type=PATask&url=oxf://PA/form.xform&token=VE9LRU4mJnVzZXI9PWludGFsaW9cYWRtaW4mJmlzc3VlZD09MTIxMzgzMDU0NzMxNSYmcm9sZXM9PWludGFsaW9ccHJvY2Vzc2FkbWluaXN0cmF0b3IsZXhhbXBsZXNcZW1wbG95ZWUsaW50YWxpb1xwcm9jZXNzbWFuYWdlcixleGFtcGxlc1xtYW5hZ2VyJiZmdWxsTmFtZT09QWRtaW5pbmlzdHJhdG9yJiZlbWFpbD09YWRtaW5AZXhhbXBsZS5jb20mJm5vbmNlPT03NjE4NTAyMjU5NjE0Nzg1MTg2JiZ0aW1lc3RhbXA9PTEyMTM4MzA1NDczMTYmJmRpZ2VzdD09UnlSYkJiMSttcEdqaWRmNStmQ3kzd0loek9VPSYmJiZUT0tFTg&user=intalio\\admin";
        new URI(URLEncoder.encode(url));
    }
}
