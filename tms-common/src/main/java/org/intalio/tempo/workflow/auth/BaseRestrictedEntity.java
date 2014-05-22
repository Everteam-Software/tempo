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

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ElementCollection;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;

import org.apache.commons.collections.CollectionUtils;

@MappedSuperclass
@Embeddable
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class BaseRestrictedEntity implements IRestrictedEntity {
    
    /** Note: do not access this field directly, otherwise JPA cannot load them */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name="tempo_user", joinColumns={@JoinColumn(name="TASK_ID", referencedColumnName="ID")})
    @Column(name="element")
    private Collection<String> _userOwners;

    /** Note: do not access this field directly, otherwise JPA cannot load them */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name="tempo_role", joinColumns={@JoinColumn(name="TASK_ID", referencedColumnName="ID")})
    @Column(name="element")
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
        for (String userOwner : getUserOwners())
            if (userId.equals(userOwner)
                    || credentials.getVacationUsers().contains(userOwner))
                return true;
        if(getRoleOwners().contains("*"))  return true;
        return CollectionUtils.containsAny(getRoleOwners(), credentials.getAssignedRoles());
    }
    
}
