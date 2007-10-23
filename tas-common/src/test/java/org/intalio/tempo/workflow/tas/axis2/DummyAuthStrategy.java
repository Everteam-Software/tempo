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

import org.apache.log4j.Logger;

import org.intalio.tempo.workflow.tas.core.AuthCredentials;
import org.intalio.tempo.workflow.tas.core.AuthException;
import org.intalio.tempo.workflow.tas.core.AuthStrategy;

/**
 * A dummy implementation of {@link org.intalio.tempo.workflow.tas.core.AuthStrategy}. Always authenticates any provided
 * credentials successfully and outputs debug messages.
 */
public class DummyAuthStrategy implements AuthStrategy {
    private static final Logger _logger = Logger.getLogger(DummyAuthStrategy.class);

    public void authenticate(AuthCredentials credentials)
            throws AuthException {
        _logger.debug("Dummy authorization OK.");
        _logger.debug(credentials);
    }
}
