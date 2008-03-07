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
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;
import org.apache.openjpa.persistence.jdbc.XJoinColumn;
import org.intalio.tempo.workflow.util.RequiredArgumentException;

@Entity
@Table(name = "TEMPO_AUTH_SET")
public class AuthIdentifierSet extends HashSet<String> {

    private static final long serialVersionUID = -6628743014089702581L;
    
    @PersistentCollection(elementCascade = CascadeType.ALL)
    @ContainerTable(name="TEMPO_BACKING_SET",joinColumns=@XJoinColumn(name="SET_ID"))
    @ElementJoinColumn(name="AUTH_ID")
    private Collection<String> backingSet = new HashSet<String>();

    public AuthIdentifierSet() {

    }

    public AuthIdentifierSet(List<String> strings) {
        backingSet.addAll(strings);
    }
    
    public AuthIdentifierSet(Collection<String> strings) {
        backingSet.addAll(strings);
    }

    public AuthIdentifierSet(AuthIdentifierSet instance) {
        backingSet.addAll(instance.backingSet);
    }

    public AuthIdentifierSet(String[] idArray) {
        Collection<String> normalizedIDs = Arrays.asList(AuthIdentifierNormalizer.normalizeAuthIdentifiers(idArray));
        backingSet.addAll(normalizedIDs);
    }

    @Override
    public boolean equals(Object rhs) {
        throw new RuntimeException("Do not use me for testing");
    }

    public Collection<String> toCollection() {
        return backingSet;
    }

    @Override
    public int hashCode() {
        return backingSet.hashCode();
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("(");
        if(!backingSet.isEmpty()) {
            for(String s : backingSet) buffer.append("'"+s+"'"+",");
            buffer.setCharAt(buffer.length()-1, ')');    
        } else {
            buffer.append(")");
        }
        return buffer.toString();
    }

    public boolean isEmpty() {
        return backingSet.isEmpty();
    }

    public int size() {
        return backingSet.size();
    }

    public boolean add(String authID) {
        if (authID == null) {
            throw new RequiredArgumentException("authID");
        }
        String normalizedID = AuthIdentifierNormalizer.normalizeAuthIdentifier(authID);
        return backingSet.add(normalizedID);
    }

    public void addAll(AuthIdentifierSet rhs) {
        if (rhs == null) {
            throw new RequiredArgumentException("rhs");
        }

        for (String id : rhs) {
            backingSet.add(id);
        }
    }

    public boolean contains(Object object) {
        boolean result = false;

        if (object instanceof String) {
            String normalizedID = AuthIdentifierNormalizer.normalizeAuthIdentifier((String) object);
            result = backingSet.contains(normalizedID);
        }

        return result;
    }

    public boolean remove(Object object) {
        boolean result = false;

        if (object instanceof String) {
            String normalizedID = AuthIdentifierNormalizer.normalizeAuthIdentifier((String) object);
            result = backingSet.remove(normalizedID);
        }

        return result;
    }

    public void clear() {
        backingSet.clear();
    }

    public Iterator<String> iterator() {
        return backingSet.iterator();
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
