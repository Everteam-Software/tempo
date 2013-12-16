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
package org.intalio.tempo.workflow.tms.server.audit;

import java.sql.Timestamp;
import java.util.Date;

import org.aspectj.lang.JoinPoint;
import org.intalio.tempo.workflow.auth.AuthException;
import org.intalio.tempo.workflow.auth.IAuthProvider;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.task.Vacation;
import org.intalio.tempo.workflow.task.audit.VacationAudit;
import org.intalio.tempo.workflow.tms.server.dao.VacationDAOConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The persistent class for the VACATION_AUDIT database table.
 * 
 */
public class AuditVacation {

    /**
     * logger for AuditVacation.
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(AuditVacation.class);

    /**
     * authProvider to get properties from token.
     */
    private IAuthProvider authProvider;

    /**
     * set authProvider property.
     * @param authenticationProvider IAuthProvider
     */
    public final void setAuthProvider(
            final IAuthProvider authenticationProvider) {
        this.authProvider = authenticationProvider;
        LOGGER.info("IAuthProvider implementation : "
        + authProvider.getClass());
    }
    /**
     * Audit's creation of vacation.
     * 
     * @param joinPoint JoinPoint
     * @throws AuthException Exception
     */
    public final void auditCreateVacation(final JoinPoint joinPoint)
            throws AuthException {
        Object[] createArgs = joinPoint.getArgs();
        VacationDAOConnection dao = (VacationDAOConnection) createArgs[0];
        Vacation vacation = (Vacation) createArgs[1];
        String participantToken = (String) createArgs[2];
        String actionPerformed = joinPoint.getSignature().getName();
        LOGGER.debug("intercepting action : " + actionPerformed);
        LOGGER.debug("vacation id: " + vacation.getId());
        UserRoles credentials = authProvider.authenticate(participantToken);
        String userOwner = credentials.getUserID();
        VacationAudit vacationAudit = new VacationAudit();
        vacationAudit.setActionPerformed(actionPerformed);
        vacationAudit.setAuditDate(new Timestamp(new Date().getTime()));
        vacationAudit.setVacationId(vacation.getId());
        vacationAudit.setAuditUserName(userOwner);
        vacationAudit.setUpdatedDescription(vacation.getDescription());
        vacationAudit.setUpdatedFromDate(vacation.getFromDate());
        vacationAudit.setUpdatedToDate(vacation.getToDate());
        vacationAudit.setUpdatedUserName(vacation.getUser());
        vacationAudit.setUpdatedSubstitute(vacation.getSubstitute());
        vacationAudit.setUpdatedIsActive((short) vacation.getIs_active());
        dao.auditVacation(vacationAudit);
        dao.commit();
    }

    /**
     * Audit's update of vacation.
     * 
     * @param joinPoint JoinPoint
     * @throws AuthException Exception
     */
    public final void auditUpdateVacation(final JoinPoint joinPoint)
            throws AuthException {
        Object[] createArgs = joinPoint.getArgs();
        VacationDAOConnection dao = (VacationDAOConnection) createArgs[0];
        Vacation vacation = (Vacation) createArgs[1];
        String participantToken = (String) createArgs[2];
        UserRoles credentials = authProvider.authenticate(participantToken);
        String userOwner = credentials.getUserID();
        String actionPerformed = joinPoint.getSignature().getName();
        LOGGER.debug("intercepting action : " + actionPerformed);
        LOGGER.debug("vacation id: " + vacation.getId());
        VacationAudit vacationAudit = new VacationAudit();
        vacationAudit.setActionPerformed(actionPerformed);
        vacationAudit.setAuditDate(new Timestamp(new Date().getTime()));
        vacationAudit.setVacationId(vacation.getId());
        vacationAudit.setAuditUserName(userOwner);
        vacationAudit.setUpdatedDescription(vacation.getDescription());
        vacationAudit.setUpdatedFromDate(vacation.getFromDate());
        vacationAudit.setUpdatedToDate(vacation.getToDate());
        vacationAudit.setUpdatedUserName(vacation.getUser());
        vacationAudit.setUpdatedSubstitute(vacation.getSubstitute());
        vacationAudit.setUpdatedIsActive((short) vacation.getIs_active());
        dao.auditVacation(vacationAudit);
        dao.commit();
    }

    /**
     * Audit's deletion of vacation.
     * 
     * @param joinPoint JoinPoint
     * @throws AuthException Exception
     */
    public final void auditDeleteVacation(final JoinPoint joinPoint)
            throws AuthException {
        Object[] createArgs = joinPoint.getArgs();
        VacationDAOConnection dao = (VacationDAOConnection) createArgs[0];
        int vacationId = (Integer) createArgs[1];
        String participantToken = (String) createArgs[2];
        UserRoles credentials = authProvider.authenticate(participantToken);
        String userOwner = credentials.getUserID();
        String actionPerformed = joinPoint.getSignature().getName();
        LOGGER.debug("intercepting action : " + actionPerformed);
        LOGGER.debug("vacation id: " + vacationId);
        VacationAudit vacationAudit = new VacationAudit();
        vacationAudit.setActionPerformed(actionPerformed);
        vacationAudit.setAuditDate(new Timestamp(new Date().getTime()));
        vacationAudit.setVacationId(vacationId);
        vacationAudit.setAuditUserName(userOwner);
        vacationAudit.setUpdatedIsActive((short) 0);
        dao.auditVacation(vacationAudit);
        dao.commit();
    }
}
