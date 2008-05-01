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
 */
package org.intalio.tempo.workflow.tas.axis2;

import org.intalio.tempo.security.Property;
import org.intalio.tempo.workflow.tas.core.AuthCredentials;
import org.intalio.tempo.workflow.tas.core.AuthException;
import org.intalio.tempo.workflow.tas.core.AuthStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A dummy implementation of {@link org.intalio.tempo.workflow.tas.core.AuthStrategy}. Always authenticates any provided
 * credentials successfully and outputs debug messages.
 */
public class DummyAuthStrategy implements AuthStrategy {
    private static final Logger _logger = LoggerFactory.getLogger(DummyAuthStrategy.class);

    public Property[] authenticate(AuthCredentials credentials)
            throws AuthException {
        _logger.debug("Dummy authorization OK."+credentials);
        return null;
    }
}
