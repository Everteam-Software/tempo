/**
 * Copyright (c) 2005-2008 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 */

package org.intalio.tempo.workflow.task;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Query;
import javax.persistence.Table;

import org.apache.openjpa.persistence.Persistent;

@Entity
@Table(name = "tempo_prev_owners")
@NamedQueries({
    @NamedQuery(name = TaskPrevOwners.FIND_PREV_OWNERS_BY_ID, query = "select prevOwners from TaskPrevOwners prevOwners where prevOwners._id = ?1"),
    })
public class TaskPrevOwners {
	
	private Query find_id;
	public static final String FIND_PREV_OWNERS_BY_ID = "find_prev_owners_by_id";
	private EntityManager _entityManager;
	
	public TaskPrevOwners(){
		
	}
	public TaskPrevOwners(EntityManager em) {
		_entityManager = em;
		find_id = _entityManager.createNamedQuery(TaskPrevOwners.FIND_PREV_OWNERS_BY_ID);
	}
	
	@Id
	@Column(name = "task_id")
	@Persistent
	private String _id;
	
	@Column(name = "prev_users")
	@Persistent
	private String _prevUsers;
	
	@Column(name = "prev_roles")
	@Persistent
	private String _prevRoles;

	public String getId() {
		return _id;
	}

	public void setId(String _id) {
		this._id = _id;
	}

	public String getPrevUsers() {
		return _prevUsers;
	}

	public void setPrevUsers(String _prev_users) {
		this._prevUsers = _prev_users;
	}

	public String getPrevRoles() {
		return _prevRoles;
	}

	public void setPrevRoles(String _prev_roles) {
		this._prevRoles = _prev_roles;
	}
	
	/**
	 * to get task previous owners by id
	 * @param id
	 * @return
	 */
	public TaskPrevOwners fetchPrevOwnersByID(String id) {
        Query q = find_id.setParameter(1, id);
        List<TaskPrevOwners> resultList = q.getResultList();
        return resultList.get(0);
    }

}
