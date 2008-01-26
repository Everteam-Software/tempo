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

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.intalio.tempo.workflow.util.RequiredArgumentException;

public abstract class OMMarshaller {
    private OMFactory _omFactory;
    private OMNamespace _omNamespace;

    protected OMMarshaller(OMFactory omFactory, OMNamespace omNamespace) {
        if (omFactory == null) {
            throw new RequiredArgumentException("omFactory");
        }
        if (omNamespace == null) {
            throw new RequiredArgumentException("omNamespace");
        }
        _omFactory = omFactory;
        _omNamespace = omNamespace;
    }

    protected OMFactory getOMFactory() {
        return _omFactory;
    }

    protected OMNamespace getOMNamespace() {
        return _omNamespace;
    }

    protected OMElement createElement(String name) {
        return _omFactory.createOMElement(name, _omNamespace);
    }

    protected OMElement createElement(OMElement parent, String name) {
        return _omFactory.createOMElement(name, _omNamespace, parent);
    }

    protected OMElement createElement(OMElement parent, String name, String value) {
        OMElement element = this.createElement(parent, name);
        element.setText(value);
        return element;
    }
}
