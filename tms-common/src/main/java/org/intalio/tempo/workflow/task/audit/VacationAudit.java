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

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.openjpa.persistence.Persistent;

/**
 * The persistent class for the VACATION_AUDIT database table.
 * 
 */
@Entity
@TableGenerator(name = "tab", initialValue = 0, allocationSize = 1)
@Table(name = "vacation_audit")
public class VacationAudit {

    /**
     * id.
     */
    @GeneratedValue
    @Id
    @Column(name = "id")
    @Persistent
    private long id;

    /**
     * vacation id.
     */
    @Persistent
    @Column(name = "vacation_id")
    private long vacationId;

    /**
     * actionPerformed.
     */
    @Persistent
    @Column(name = "action_performed")
    private String actionPerformed;

    /**
     * auditDate.
     */
    @Persistent
    @Column(name = "audit_date")
    private Timestamp auditDate;

    /**
     * auditUserName.
     */
    @Persistent
    @Column(name = "audit_user_name")
    private String auditUserName;

    /**
     * updatedDescription.
     */
    @Persistent
    @Column(name = "updated_description")
    private String updatedDescription;

    /**
     * updatedFromDate.
     */
    @Temporal(TemporalType.DATE)
    @Persistent
    @Column(name = "updated_from_date")
    private Date updatedFromDate;

    /**
     * updatedIsActive.
     */
    @Persistent
    @Column(name = "updated_is_active")
    private short updatedIsActive;

    /**
     * updatedSubstitute.
     */
    @Persistent
    @Column(name = "updated_substitute")
    private String updatedSubstitute;

    /**
     * updatedToDate.
     */
    @Temporal(TemporalType.DATE)
    @Persistent
    @Column(name = "updated_to_date")
    private Date updatedToDate;

    /**
     * updatedUserName.
     */
    @Persistent
    @Column(name = "updated_user_name")
    private String updatedUserName;

    /**
     * new vacationAudit object.
     */
    public VacationAudit() {
    }

    /**
     *get id.
     *@return id long
     */
    public final long getId() {
        return this.id;
    }

    /**
     *set id.
     * @param auditId long
     */
    public final void setId(final long auditId) {
        this.id = auditId;
    }

    /**
     *set vacation id.
     * @param vacationID long
     */
    public final void setVacationId(final long vacationID) {
        this.vacationId = vacationID;
    }

    /**
     *get vacation id.
     *@return vacationId long
     */
    public final long getVacationId() {
        return this.vacationId;
    }

    /**
     *get actionPerformed.
     * @return  actionPerformed String
     */
    public final String getActionPerformed() {
        return this.actionPerformed;
    }

    /**
     * set actionPerformed.
     * @param action String
     */
    public final void setActionPerformed(final String action) {
        this.actionPerformed = action;
    }

    /**
     *get auditDate.
     * @return  auditDate Timestamp
     */
    public final Timestamp getAuditDate() {
        return this.auditDate;
    }

    /**
     *set auditDate.
     * @param  date Timestamp
     */
    public final void setAuditDate(final Timestamp date) {
        this.auditDate = date;
    }

    /**
     *get auditUserName.
     * @return  auditUserName String
     */
    public final String getAuditUserName() {
        return this.auditUserName;
    }

    /**
     *set auditUserName.
     * @param  userName String
     */
    public final void setAuditUserName(final String userName) {
        this.auditUserName = userName;
    }

    /**
     *get updatedDescription.
     * @return  updatedDescription String
     */
    public final String getUpdatedDescription() {
        return this.updatedDescription;
    }

    /**
     *set updatedDescription.
     *  @param  description String
     */
    public final void setUpdatedDescription(final String description) {
        this.updatedDescription = description;
    }

    /**
     *get updatedFromDate.
     * @return  updatedFromDate Date
     */
    public final Date getUpdatedFromDate() {
        return this.updatedFromDate;
    }

    /**
     *set updatedFromDate.
     * @param  fromDate Date
     */
    public final void setUpdatedFromDate(final Date fromDate) {
        this.updatedFromDate = fromDate;
    }

    /**
     *get updatedIsActive.
     * @return  updatedIsActive short
     */
    public final short getUpdatedIsActive() {
        return this.updatedIsActive;
    }

    /**
     *set updatedIsActive.
     * @param  isActive short
     */
    public final void setUpdatedIsActive(final short isActive) {
        this.updatedIsActive = isActive;
    }

    /**
     *get updatedSubstitute.
     * @return  updatedSubstitute String
     */
    public final String getUpdatedSubstitute() {
        return this.updatedSubstitute;
    }

    /**
     *set updatedSubstitute.
     * @param  substitute String
     */
    public final void setUpdatedSubstitute(final String substitute) {
        this.updatedSubstitute = substitute;
    }

    /**
     *get updatedToDate.
     * @return  updatedToDate Date
     */
    public final Date getUpdatedToDate() {
        return this.updatedToDate;
    }

    /**
     *set updatedToDate.
     * @param  toDate Date
     */
    public final void setUpdatedToDate(final Date toDate) {
        this.updatedToDate = toDate;
    }

    /**
     *get updatedUserName.
     * @return  updatedUserName String
     */
    public final String getUpdatedUserName() {
        return this.updatedUserName;
    }

    /**
     *set updatedUserName.
     * @param  userName String
     */
    public final void setUpdatedUserName(final String userName) {
        this.updatedUserName = userName;
    }

}
