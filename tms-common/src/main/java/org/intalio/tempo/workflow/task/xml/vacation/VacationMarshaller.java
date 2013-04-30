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

import java.util.Calendar;

import org.apache.axiom.om.OMElement;
import org.apache.xmlbeans.XmlObject;
import org.intalio.tempo.workflow.task.Vacation;
import org.intalio.tempo.workflow.task.xml.TaskXMLConstants;
import org.intalio.tempo.workflow.task.xml.XmlTooling;

import com.intalio.bpms.workflow.taskManagementServices20051109.VacationMetadata;

public class VacationMarshaller {
	public OMElement marshalVacationData(Vacation vac) {
		OMElement om = XmlTooling.convertDocument(marshalXMLVacationData(vac));
		om.setLocalName(TaskXMLConstants.VACATION_LOCAL_NAME);
		om.setNamespace(TaskXMLConstants.TASK_OM_NAMESPACE);
		return om;
	}

	private XmlObject marshalXMLVacationData(Vacation vac) {
		VacationMetadata vacMetadataElement = VacationMetadata.Factory.newInstance();
		vacMetadataElement.setVacId(vac.getId());
		vacMetadataElement.setVacDesc(vac.getDescription());

		Calendar cal = Calendar.getInstance();
		cal.setTime(vac.getFromDate());
		vacMetadataElement.setVacFrom(cal);

		cal.setTime(vac.getToDate());
		vacMetadataElement.setVacTo(cal);

		vacMetadataElement.setVacUser(vac.getUser());
		vacMetadataElement.setVacSubstitute(vac.getSubstitute());
		return vacMetadataElement;
	}
}
