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

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;

import org.apache.commons.collections.CollectionUtils;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;

@MappedSuperclass
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class BaseRestrictedEntity implements IRestrictedEntity {
    
    /** Note: do not access this field directly, otherwise JPA cannot load them */
    @PersistentCollection(elementType=String.class, elementCascade=CascadeType.ALL, elementEmbedded = false, fetch=FetchType.EAGER)
    @ContainerTable(name="tempo_user")
    private Collection<String> _userOwners;

    /** Note: do not access this field directly, otherwise JPA cannot load them */
    @PersistentCollection(elementType=String.class, elementCascade=CascadeType.ALL, elementEmbedded = false, fetch=FetchType.EAGER)
    @ContainerTable(name="tempo_role")
    private Collection<String> _roleOwners;

    public BaseRestrictedEntity() {
        _userOwners = new AuthIdentifierSet();
        _roleOwners = new AuthIdentifierSet();
    }

    public Collection<String> getUserOwners() {
        return _userOwners;
    }

    public Collection<String> getRoleOwners() {
        return _roleOwners;
    }
    
    public void setUserOwners(Collection<String> owners) {
        _userOwners = new AuthIdentifierSet(owners);
    }

    public void setRoleOwners(Collection<String> owners) {
        _roleOwners = new AuthIdentifierSet(owners);
    }

    public boolean isAvailableTo(UserRoles credentials) {
		String userId = credentials.getUserID();
        for (String userOwner : getUserOwners()) if (userId.equals(userOwner)) return true;
        if(getRoleOwners().contains("*"))  return true;
        return CollectionUtils.containsAny(getRoleOwners(), credentials.getAssignedRoles());
    }
    
}
