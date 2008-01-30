/**
 * Copyright (c) 2005-2007 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 */
package org.intalio.tempo.deployment.impl;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Localization utility class
 */
public class LocalizedMessages {
    private static final Logger LOG = LoggerFactory.getLogger(LocalizedMessages.class);

    static ResourceBundle _messages = null;
    
    static {
        try {
            _messages = ResourceBundle.getBundle("DeploymentService");
        } catch (Exception except) {
            LOG.error("Error initializing localized message bundle", except);
        }
    }
    
    public static String _(String pattern, Object... arguments) {
        if (_messages != null) {
            String localized = _messages.getString(pattern);
            if (localized != null) pattern = localized;
        }
        return MessageFormat.format(pattern, arguments);
    }
}
