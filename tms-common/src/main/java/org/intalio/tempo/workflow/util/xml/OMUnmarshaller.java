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

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.commons.lang.StringUtils;
import org.intalio.tempo.workflow.auth.AuthIdentifierSet;
import org.intalio.tempo.workflow.util.RequiredArgumentException;

public abstract class OMUnmarshaller {

    private String _namespaceURI;

    private String _namespacePrefix;

    protected OMUnmarshaller(String namespaceURI, String namespacePrefix) {
        if (namespaceURI == null) {
            throw new RequiredArgumentException("namespaceURI");
        }
        if (namespacePrefix == null) {
            throw new RequiredArgumentException("namespacePrefix");
        }
        _namespaceURI = namespaceURI;
        _namespacePrefix = namespacePrefix;
    }

    protected OMElement requireElement(OMElementQueue queue, String name)
            throws InvalidInputFormatException {
        QName qName = new QName(_namespaceURI, name, _namespacePrefix);
        OMElement element = queue.getNextElement();
        if (element == null) {
            throw new InvalidInputFormatException("Expected element " + qName + ", but not found");
        }
        if (! element.getQName().equals(qName)) {
            throw new InvalidInputFormatException("Expected element " + qName + ", encountered " + element.getQName()
                    + " instead.");
        }
        return element;
    }

    protected OMElement expectElement(OMElementQueue queue, String name) {
        OMElement result = null;

        QName qName = new QName(_namespaceURI, name, _namespacePrefix);
        OMElement element = queue.getNextElement();
        if (element != null) {
            if (element.getQName().equals(qName)) {
                result = element;
            } else {
                queue.pushElementBack(element);
            }
        }
        return result;
    }

    protected OMElement expectElementAnyNS(OMElementQueue queue, String name) {
        OMElement result = null;

        OMElement element = queue.getNextElement();
        if (element != null) {
            if (element.getQName().getLocalPart().equals(name)) {
                result = element;
            } else {
                queue.pushElementBack(element);
            }
        }
        return result;
    }

    protected String requireElementValue(OMElementQueue queue, String name)
            throws InvalidInputFormatException {
        OMElement element = this.requireElement(queue, name);
        return element.getText();
    }

    protected String expectElementValue(OMElementQueue queue, String name) {
        OMElement element = this.expectElementAnyNS(queue, name);
        String result = null;
        if (element != null) {
            result = element.getText();
        }
        return result;
    }
    
    @SuppressWarnings("unchecked")
	protected ArrayList expectListOfValues(OMElementQueue queue, String elementName) {
    	return (ArrayList) expectList(queue, elementName, new ArrayList());
    }
    
    protected Collection expectList(OMElementQueue queue, String elementName, Collection list) {
    	while (true) {
            String value = this.expectElementValue(queue, elementName);
            if(StringUtils.isNotEmpty(value)) 
                list.add(value);
            else 
                break;
        }
        return list;
    }

    protected AuthIdentifierSet expectAuthIdentifiers(OMElementQueue queue, String elementName) {
    	return (AuthIdentifierSet) expectList(queue, elementName, new AuthIdentifierSet());
    }

    protected void requireParameter(Object parameter, String name)
            throws InvalidInputFormatException {
        if (parameter == null) {
            throw new InvalidInputFormatException("Required parameter was not specified: " + name);
        }
    }

    protected void forbidParameter(Object parameter, String name)
            throws InvalidInputFormatException {
        if (parameter != null) {
            throw new InvalidInputFormatException("A forbidden parameter was specified: " + name);
        }
    }

}
