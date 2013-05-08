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

/**
 *
 * @author bapiraju
 *
 */
@Entity
@Table(name = "tempo_prev_owners")
@NamedQueries({
    @NamedQuery(name = TaskPrevOwners.FIND_PREV_OWNERS_BY_ID, query =
    "select prevOwners from TaskPrevOwners prevOwners where prevOwners._id = ?1"
       ),
    })
public class TaskPrevOwners {
	
    /**
    * query to get TaskPrevOwners by Id.
    */
	private Query findId;

	/**
	 * Constant holds FIND_PREV_OWNERS_BY_ID.
	 */
	public static final String FIND_PREV_OWNERS_BY_ID
	                        = "find_prev_owners_by_id";
	/**
	 * entityManager.
	 */
	private EntityManager entityManager;

	/**
	 * empty constructor.
	 */
	public TaskPrevOwners() {
		
	}
	
	/**
	 * constructor with entityManager.
	 * @param em EntityManager
	 */
	public TaskPrevOwners(final EntityManager em) {
		entityManager = em;
		findId = entityManager.createNamedQuery(
		        TaskPrevOwners.FIND_PREV_OWNERS_BY_ID);
	}

	/**
	 * task id.
	 */
	@Id
	@Column(name = "task_id")
	@Persistent
	private String id;
	/**
	 * task previous users.
	 */
	@Column(name = "prev_users")
	@Persistent
	private String prevUsers;

	/**
	 * task previous roles.
	 */
	@Column(name = "prev_roles")
	@Persistent
	private String prevRoles;

	/**
	 * get task id.
	 * @return String
	 */
	public final String getId() {
		return id;
	}

	/**
	 * Set task Id.
	 * @param taskId String
	 */
	public final void setId(final String taskId) {
		this.id = taskId;
	}
	/**
	 * get task previous users.
	 * @return String
	 */
	public final String getPrevUsers() {
		return prevUsers;
	}
	/**
	 * set task previous users.
	 * @param taskPrevUsers String
	 */
	public final void setPrevUsers(final String taskPrevUsers) {
		this.prevUsers = taskPrevUsers;
	}
	/**
	 * get task previous roles.
	 * @return String
	 */
	public final String getPrevRoles() {
		return prevRoles;
	}
	/**
	 * set task previous roles.
	 * @param taskPrevRoles String
	 */
	public final void setPrevRoles(final String taskPrevRoles) {
		this.prevRoles = taskPrevRoles;
	}
	
	/**
     * to get task previous owners by id.
     * @param taskId String
     * @return TaskPrevOwners
     */
	public final TaskPrevOwners fetchPrevOwnersByID(final String taskId) {
        Query q = findId.setParameter(1, id);
        List<TaskPrevOwners> resultList = q.getResultList();
        return resultList.get(0);
    }

}
