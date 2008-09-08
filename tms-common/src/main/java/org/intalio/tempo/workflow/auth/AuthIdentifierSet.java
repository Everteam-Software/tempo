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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.persistence.MappedSuperclass;

import org.apache.commons.lang.StringUtils;

@MappedSuperclass
public class AuthIdentifierSet extends HashSet<String> {

    private static final long serialVersionUID = -6628743014089702581L;

    public AuthIdentifierSet() {

    }

    public AuthIdentifierSet(List<String> strings) {
        addAll(strings);
    }
    
    public AuthIdentifierSet(Collection<String> strings) {
        addAll(strings);
    }

    public AuthIdentifierSet(AuthIdentifierSet instance) {
        addAll(instance);
    }

    public AuthIdentifierSet(String[] idArray) {
        Collection<String> normalizedIDs = Arrays.asList(AuthIdentifierNormalizer.normalizeAuthIdentifiers(idArray));
        addAll(normalizedIDs);
    }

    public boolean add(String authID) {
        // ignore null parameters and empty strings
        if (StringUtils.isEmpty(authID)) return true; 
        String normalizedID = AuthIdentifierNormalizer.normalizeAuthIdentifier(authID);
        return super.add(normalizedID);
    }

    public boolean contains(Object object) {
        boolean result = false;

        if (object instanceof String) {
            String normalizedID = AuthIdentifierNormalizer.normalizeAuthIdentifier((String) object);
            result = super.contains(normalizedID);
        }

        return result;
    }

    public boolean remove(String object) {
        String normalizedID = AuthIdentifierNormalizer.normalizeAuthIdentifier((String) object);
        return super.remove(normalizedID);
    }
}
