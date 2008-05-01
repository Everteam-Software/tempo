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
package org.intalio.tempo.workflow.tas.core;

import org.intalio.tempo.security.Property;

/**
 * Defines the set of operations for the Authentication/Authorization strategy of
 * {@link org.intalio.tempo.workflow.tas.core.TaskAttachmentServiceImpl}. <br />
 * Authentication/Authorization strategy implementation must provide means to check a specified set of security
 * credentials for integrity (not being corrupt) and validity (representing a correctly authorized invocation).
 */
public interface AuthStrategy {

    /**
     * Checks a specific security credential set for authorizing an invocation.<br />
     * If the specified credential set does not authorize the invocation successfully, this method throws an
     * {@link AuthException}.
     */
    Property[] authenticate(AuthCredentials credentials)
            throws AuthException;
}
