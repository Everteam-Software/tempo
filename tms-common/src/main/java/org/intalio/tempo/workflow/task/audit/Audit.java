/**
 * Copyright (c) 2005-2007 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 */
package org.intalio.tempo.workflow.task.audit;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.openjpa.persistence.Persistent;

@Entity
@TableGenerator(name = "auditGenerator", initialValue = 0, allocationSize = 1)
@Table(name = "tempo_audit")
public class Audit {

    @GeneratedValue
    @Id
    @Column(name = "id")
    private long id;

    @Persistent
    @Column(name = "action_performed")
    private String actionPerformed;

    @Persistent
    @Column(name = "audit_date")
    private Date auditDate;

    @Persistent
    @Column(name = "task_id")
    private String taskId;

    @Persistent
    @Column(name = "user_name")
    private String user;

    @Persistent
    @Column(name = "updated_description")
    private String updatedDescription;

    @Persistent
    @Column(name = "updated_priority")
    private String updatedPriority;

    @Persistent
    @Column(name = "assigned_users")
    private String assignedUsers;

    @Persistent
    @Column(name = "assigned_roles")
    private String assignedRoles;

    @Persistent
    @Column(name = "updated_state")
    private String updatedState;

    @Persistent
    @Column(name = "instance_id")
    private Long instanceId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getActionPerformed() {
        return actionPerformed;
    }

    public void setActionPerformed(String actionPerformed) {
        this.actionPerformed = actionPerformed;
    }

    public Date getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(Date auditDate) {
        this.auditDate = auditDate;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUpdatedDescription() {
        return updatedDescription;
    }

    public void setUpdatedDescription(String updatedDescription) {
        this.updatedDescription = updatedDescription;
    }

    public String getUpdatedPriority() {
        return updatedPriority;
    }

    public void setUpdatedPriority(String updatedPriority) {
        this.updatedPriority = updatedPriority;
    }

    public String getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(String assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    public String getAssignedRoles() {
        return assignedRoles;
    }

    public void setAssignedRoles(String assignedRoles) {
        this.assignedRoles = assignedRoles;
    }

    public String getUpdatedState() {
        return updatedState;
    }

    public void setUpdatedState(String updatedState) {
        this.updatedState = updatedState;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

}
