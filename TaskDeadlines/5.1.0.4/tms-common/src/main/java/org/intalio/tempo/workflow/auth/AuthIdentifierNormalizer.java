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

package org.intalio.tempo.workflow.auth;

import org.intalio.tempo.workflow.util.RequiredArgumentException;

public final class AuthIdentifierNormalizer {

    private AuthIdentifierNormalizer() {

    }

    public static String normalizeAuthIdentifier(String sourceID) {
        if (sourceID == null) {
            throw new RequiredArgumentException("sourceID");
        }
        
        return sourceID.replace('/', '\\').replace('.', '\\').toLowerCase();
    }

    public static String[] normalizeAuthIdentifiers(String[] sourceIDs) {
        String[] normalizedIDs = new String[sourceIDs.length];
        
        for (int i = 0; i < sourceIDs.length; ++i) {
            normalizedIDs[i] = normalizeAuthIdentifier(sourceIDs[i]);
        }
        
        return normalizedIDs;
    }    
}
