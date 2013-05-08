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
 * $Id: VacationController.java 5440 2006-06-09 08:58:15Z imemruk $
 * $Log:$
 */
package org.intalio.tempo.workflow.task.xml.vacation;

import org.apache.axiom.om.OMElement;
import org.intalio.tempo.workflow.task.Vacation;
import org.intalio.tempo.workflow.task.xml.TaskXMLConstants;
import org.intalio.tempo.workflow.util.RequiredArgumentException;
import org.intalio.tempo.workflow.util.xml.InvalidInputFormatException;
import org.intalio.tempo.workflow.util.xml.OMElementQueue;
import org.intalio.tempo.workflow.util.xml.OMUnmarshaller;
import org.intalio.tempo.workflow.util.xml.XsdDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VacationUnmarshaller extends OMUnmarshaller {
	private static final Logger LOG = LoggerFactory.getLogger(VacationUnmarshaller.class);

	public VacationUnmarshaller() {
		super(TaskXMLConstants.TASK_NAMESPACE, TaskXMLConstants.TASK_NAMESPACE_PREFIX);
	}

	public Vacation unmarshalVacation(OMElement rootElement) throws InvalidInputFormatException {
		Vacation vacation = null;
		try {
			if (rootElement == null) {
				throw new RequiredArgumentException("rootElement");
			}
			LOG.debug("OMElement=" + rootElement);
			vacation = new Vacation();
			OMElementQueue rootQueue = new OMElementQueue(rootElement);
			int vacId = Integer.parseInt(this.expectElementValue(rootQueue, "vacId"));
			if (vacId != 0) {
				vacation.setId(vacId);
			}
			String fromDate = this.expectElementValue(rootQueue, "vacFrom");
			if ((fromDate != null) && !("".equals(fromDate))) {
				vacation.setFromDate(new XsdDateTime(fromDate).getTime());
			}
			String toDate = this.expectElementValue(rootQueue, "vacTo");
			if (toDate != null) {
				vacation.setToDate(new XsdDateTime(toDate).getTime());
			}

			String description = this.expectElementValue(rootQueue, "vacDesc");
			if (description != null) {
				vacation.setDescription(description);
			}
			String user = this.expectElementValue(rootQueue, "vacUser");
			if (user != null) {
				vacation.setUser(user);
			}
			String substitute = this.expectElementValue(rootQueue, "vacSubstitute");
			if (substitute != null) {
				vacation.setSubstitute(substitute);
			}
		} catch (Exception e) {
			LOG.error("Exception while unmarshalling vacation data", e);
		}
		return vacation;
	}
}