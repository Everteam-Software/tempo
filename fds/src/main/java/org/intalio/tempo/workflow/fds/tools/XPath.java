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
 */
package org.intalio.tempo.workflow.fds.tools;

import org.intalio.tempo.workflow.fds.dispatches.InvalidInputFormatException;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.XPathContext;

public class XPath {
    private XPathContext context;

    public XPath(XPathContext xpathContext) {
        this.context = xpathContext;
    }

    public Element requireElement(Node node, String query) throws InvalidInputFormatException {
        Nodes nodes = node.query(query, context);
        if ((nodes.size() < 1) || (! (nodes.get(0) instanceof Element))) {
            throw new InvalidInputFormatException("Required element not found: " + query);
        }
        return (Element) nodes.get(0);
    }

}
