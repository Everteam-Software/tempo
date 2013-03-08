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
import org.intalio.tempo.workflow.util.SecurityProviderProperty;

public final class AuthIdentifierNormalizer {

    private AuthIdentifierNormalizer() {

    }

    public static String normalizeAuthIdentifier(String sourceId) {
        if (sourceId == null) {
            throw new RequiredArgumentException("Invalid user id");
        }
        sourceId = sourceId.trim();
        for (int i=0; i<sourceId.length(); i++) {
            if ("/|:".indexOf(sourceId.charAt(i)) >=0 )
                sourceId = sourceId.substring(0,i) + '\\' + sourceId.substring(i+1);
        }
        if (!SecurityProviderProperty.isCaseSensitive()) {
            sourceId = sourceId.toLowerCase();
        }
        return sourceId;
    }

    public static String[] normalizeAuthIdentifiers(String[] sourceIDs) {
        String[] normalizedIDs = new String[sourceIDs.length];
        
        for (int i = 0; i < sourceIDs.length; ++i) {
            normalizedIDs[i] = normalizeAuthIdentifier(sourceIDs[i]);
        }
        
        return normalizedIDs;
    }    
}
