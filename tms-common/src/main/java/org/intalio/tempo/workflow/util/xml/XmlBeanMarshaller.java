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
import org.intalio.tempo.workflow.util.RequiredArgumentException;

public abstract class XmlBeanMarshaller {
    private String _namespace;
    
    private String _prefix;

    protected XmlBeanMarshaller( String namespace, String prefix) {
        if (namespace == null) {
            throw new RequiredArgumentException("namespace");
        }
        
        if (prefix == null){
        	throw new RequiredArgumentException("prefix");
        }
        _namespace = namespace;
        _prefix = prefix;
    }

    protected String getNamespace() {
        return _namespace;
    }

    protected String getPrefix() {
    	return _prefix;
    }
    protected XmlObject createElement(String name) {
		XmlObject newElement = XmlObject.Factory.newInstance();

		XmlCursor newElementCursor = newElement.newCursor();
		newElementCursor.toNextToken();

		newElementCursor.beginElement(new QName(_namespace, name, _prefix));
		return newElement;
    }

//    protected XmlObject createElement(String name, String value) {
//		XmlObject newElement = XmlObject.Factory.newInstance();
//
//		XmlCursor newElementCursor = newElement.newCursor();
//		newElementCursor.toNextToken();
//
//		newElementCursor.beginElement(new QName(_namespace, name, _prefix));
//		newElementCursor.insertChars(value);
//		return newElement;
//    }
    
    protected XmlObject createElement(XmlObject parent, String name) {
        XmlObject newElement = createElement(name);
        XmlCursor newElementCursor = newElement.newCursor();
        newElementCursor.toStartDoc();
        newElementCursor.toNextToken();
		
		XmlCursor parentCursor = parent.newCursor();
		parentCursor.toEndToken();
		newElementCursor.moveXml(parentCursor);
		return newElement;
    }

    protected XmlObject createElement(XmlObject parent, String name, String value) {
        final XmlObject createElement = createElement(parent, name);
        final XmlCursor newCursor = createElement.newCursor();
        newCursor.toFirstContentToken();
        newCursor.insertChars(value);
        return createElement;    	
    }

}
