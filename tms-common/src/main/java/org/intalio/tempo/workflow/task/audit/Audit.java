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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;


@Entity
@Table(name = "tempo_audit")
public class Audit {

    @Id
    @Column(name = "id")
    @TableGenerator(name="tg1" , table="OPENJPA_SEQUENCE_TABLE", pkColumnName="ID" , valueColumnName="SEQUENCE_VALUE" , pkColumnValue = "0", allocationSize=10)
    @GeneratedValue(strategy=GenerationType.TABLE , generator="tg1")
    private long id;

    @Column(name = "action_performed")
    private String actionPerformed;

    @Column(name = "audit_date")
    private Date auditDate;

    @Column(name = "task_id")
    private String taskId;

    @Column(name = "user_name")
    private String user;

    @Column(name = "updated_description")
    private String updatedDescription;

    @Column(name = "updated_priority")
    private String updatedPriority;

    @Column(name = "assigned_users")
    private String assignedUsers;

    @Column(name = "assigned_roles")
    private String assignedRoles;

    @Column(name = "updated_state")
    private String updatedState;

    @Column(name = "instance_id")
    private Long instanceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "audit_type")
    private AuditType auditType = AuditType.WORKFLOW;

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

    public AuditType getAuditType() {
        return auditType;
    }

    public void setAuditType(AuditType auditType) {
        this.auditType = auditType;
    }

}
