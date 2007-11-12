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
import java.util.Iterator;

import org.intalio.tempo.workflow.util.RequiredArgumentException;

public class AuthIdentifierSet implements Iterable<String> {
    private Collection<String> _backingSet = new HashSet<String>();

    public AuthIdentifierSet() {

    }

    public AuthIdentifierSet(Collection<String> strings) {
        _backingSet.addAll(strings);
    }
    
    public AuthIdentifierSet(AuthIdentifierSet instance) {
        _backingSet.addAll(instance._backingSet);
    }

    public AuthIdentifierSet(String[] idArray) {
        Collection<String> normalizedIDs = Arrays.asList(AuthIdentifierNormalizer.normalizeAuthIdentifiers(idArray));
        _backingSet.addAll(normalizedIDs);
    }

    @Override
    public boolean equals(Object rhs) {
        boolean result = false;

        if (rhs instanceof AuthIdentifierSet) {
            AuthIdentifierSet rhsSet = (AuthIdentifierSet) rhs;

            result = rhsSet._backingSet.equals(_backingSet);
        }

        return result;
    }
    
    public Collection<String> toCollection() {
        return _backingSet;
    }

    @Override
    public int hashCode() {
        return _backingSet.hashCode();
    }

    @Override
    public String toString() {
        return _backingSet.toString();
    }

    public boolean isEmpty() {
        return _backingSet.isEmpty();
    }

    public int size() {
        return _backingSet.size();
    }

    public boolean add(String authID) {
        if (authID == null) {
            throw new RequiredArgumentException("authID");
        }
        String normalizedID = AuthIdentifierNormalizer.normalizeAuthIdentifier(authID);
        return _backingSet.add(normalizedID);
    }

    public void addAll(AuthIdentifierSet rhs) {
        if (rhs == null) {
            throw new RequiredArgumentException("rhs");
        }

        for (String id : rhs) {
            _backingSet.add(id);
        }
    }

    public boolean contains(Object object) {
        boolean result = false;

        if (object instanceof String) {
            String normalizedID = AuthIdentifierNormalizer.normalizeAuthIdentifier((String) object);
            result = _backingSet.contains(normalizedID);
        }

        return result;
    }

    public boolean remove(Object object) {
        boolean result = false;

        if (object instanceof String) {
            String normalizedID = AuthIdentifierNormalizer.normalizeAuthIdentifier((String) object);
            result = _backingSet.remove(normalizedID);
        }

        return result;
    }

    public void clear() {
        _backingSet.clear();
    }

    public Iterator<String> iterator() {
        return _backingSet.iterator();
    }

    public boolean intersects(AuthIdentifierSet rhs) {
        if (rhs == null) {
            throw new RequiredArgumentException("rhs");
        }

        boolean intersects = false;
        for (String authID : this) {
            if (rhs.contains(authID)) {
                intersects = true;
                break;
            }
        }

        return intersects;
    }
}
