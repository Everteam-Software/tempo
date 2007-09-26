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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.axiom.om.OMElement;
import org.intalio.tempo.workflow.util.RequiredArgumentException;

public class OMElementQueue {
    private Iterator<OMElement> _iterator;

    private Queue<OMElement> _queue = new LinkedList<OMElement>();

    public OMElementQueue(OMElement parent) {
        if (parent == null) {
            throw new RequiredArgumentException("parent");
        }
        if (!parent.isComplete()) {
            parent.build();
        }
        _iterator = parent.getChildElements();
    }

    public OMElement getNextElement() {
        OMElement result = null;
        if (_queue.peek() != null) {
            result = _queue.remove();
        } else if (_iterator.hasNext()) {
            result = (OMElement) _iterator.next();
        }
        return result;
    }

    public void pushElementBack(OMElement element) {
        if (element == null) {
            throw new RequiredArgumentException("element");
        }
        _queue.add(element);
    }
}
