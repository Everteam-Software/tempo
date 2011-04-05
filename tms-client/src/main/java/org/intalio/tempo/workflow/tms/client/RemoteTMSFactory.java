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

package org.intalio.tempo.workflow.tms.client;

import java.io.File;

import org.intalio.tempo.workflow.tms.ITaskManagementServiceFactory;
import org.intalio.tempo.workflow.tms.ITaskManagementService;
import org.intalio.tempo.workflow.util.RequiredArgumentException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public class RemoteTMSFactory implements ITaskManagementServiceFactory {

	private String _endpoint;
	private String _participantToken;
	private static boolean isTEMPOLOCAL = true;

	public RemoteTMSFactory(String endpoint, String participantToken) {
		if (endpoint == null) {
			throw new RequiredArgumentException("endpoint");
		}
		if (participantToken == null) {
			throw new RequiredArgumentException("participantToken");
		}
		_endpoint = endpoint;
		_participantToken = participantToken;
	}

	public ITaskManagementService getService() {
		if (isTEMPOLOCAL) {
			Resource xmlResource = new FileSystemResource(System
					.getProperty("org.intalio.deploy.configDirectory")
					+ File.separatorChar + "tempo-tms.xml");
			BeanFactory factory = new XmlBeanFactory(xmlResource);
			LocalTMSClient client = (LocalTMSClient) factory
					.getBean("tms.tmsclient");
			client.setParticipantToken(_participantToken);
			return client;
		} else
			return new RemoteTMSClient(_endpoint, _participantToken);
	}

}
