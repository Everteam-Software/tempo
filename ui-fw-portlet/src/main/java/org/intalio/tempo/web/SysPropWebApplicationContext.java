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

package org.intalio.tempo.web;

import org.springframework.core.io.Resource;
import org.springframework.util.SystemPropertyUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * Custom XMLWebApplicationContext that allows loading configuration files using system properties, e.g.,
 * <br/>
 *     &lt;context-param&gt;<br/>
 *        &lt;param-name&gt;contextConfigLocation&lt;/param-name&gt;<br/>
 *        &lt;param-value&gt;${org.intalio.tempo.configDirectory}/tempo-tms.xml &lt;/param-value&gt;<br/>
 *     &lt;/context-param&gt;<br/><br/>
 *
 * System properties must be enclosed within "${" prefix and "}" suffix strings.
 */
public class SysPropWebApplicationContext extends XmlWebApplicationContext {

    @Override
    public Resource getResource(String location) {
        location = SystemPropertyUtils.resolvePlaceholders(location);
        return super.getResource(location);
    }
    
}
