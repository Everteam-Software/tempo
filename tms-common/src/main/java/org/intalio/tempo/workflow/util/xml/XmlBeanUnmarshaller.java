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

package org.intalio.tempo.workflow.util.xml;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.intalio.tempo.workflow.auth.AuthIdentifierSet;
import org.intalio.tempo.workflow.util.RequiredArgumentException;

public abstract class XmlBeanUnmarshaller {

    private static final Logger _logger = LoggerFactory.getLogger(XmlBeanUnmarshaller.class);
    
	private String _namespaceURI;

	private String _namespacePrefix;

	protected XmlBeanUnmarshaller(String namespaceURI, String namespacePrefix) {
		if (namespaceURI == null) {
			throw new RequiredArgumentException("namespaceURI");
		}
		if (namespacePrefix == null) {
			throw new RequiredArgumentException("namespacePrefix");
		}
		_namespaceURI = namespaceURI;
		_namespacePrefix = namespacePrefix;
	}

	protected XmlObject expectElement(XmlObject element, String name) {
		XmlObject result = null;
		XmlCursor elementCursor = element.newCursor();
		elementCursor.toStartDoc();
		boolean hasNext = true;
		QName qName = new QName(_namespaceURI, name);
		while (hasNext) {
			elementCursor.toNextToken();
			if (elementCursor.getName() != null && elementCursor.getName().getLocalPart().equals(qName.getLocalPart())) {
				result = elementCursor.getObject();
				break;
			}
			hasNext = elementCursor.hasNextToken();
		}
		return result;
	}

	protected String expectElementValue(XmlObject element, String name) {
		String result = null;
		XmlCursor elementCursor = element.newCursor();
		elementCursor.toStartDoc();
		boolean hasNext = true;
		QName qName = new QName(_namespaceURI, name);
		while (hasNext) {
			elementCursor.toNextToken();

			if (elementCursor.getName() != null && elementCursor.getName().equals(qName)) {
				result = elementCursor.getTextValue();
				break;
			}
			hasNext = elementCursor.hasNextToken();
		}
		return result;
	}

	protected XmlObject requireElement(XmlObject element, String name)
			throws InvalidInputFormatException {
		XmlObject result = expectElement(element, name);
		if (result == null) {
			throw new InvalidInputFormatException("Expected element " + name
					+ ", but not found");
		}
		return result;
	}

	protected AuthIdentifierSet expectAuthIdentifiers(XmlObject element,
			String elementName) {
		AuthIdentifierSet resultSet = new AuthIdentifierSet();
		XmlCursor elementCursor = element.newCursor();
		if (elementCursor.toChild(new QName(_namespaceURI, elementName,
				_namespacePrefix))) {
			String authID = elementCursor.getTextValue();
			if ((authID != null) && (!"".equals(authID.trim()))) {
				resultSet.add(authID);
			}
		}
		return resultSet;
	}

	protected void requireParameter(Object parameter, String name)
			throws InvalidInputFormatException {
		if (parameter == null) {
			throw new InvalidInputFormatException(
					"Required parameter was not specified: " + name);
		}
	}

	protected void forbidParameter(Object parameter, String name)
			throws InvalidInputFormatException {
		if (parameter != null) {
			log.debug("A forbidden parameter was specified: " + name);
		}
	}

}
