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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Nodes;
import nu.xom.XPathContext;

/**
 * Not thread-safe
 */
public class NamespaceConvertor {
    private String targetNamespace;
    private Set<String> excludeQueries = new HashSet<String>();
    private List<Nodes> excludedElements = new ArrayList<Nodes>();
    private XPathContext xpathContext = new XPathContext();

    public NamespaceConvertor(String targetNamespace, XPathContext xpathContext) {
        this.targetNamespace = targetNamespace;
        this.xpathContext = xpathContext;
    }

    public void addExcludeQuery(String query) {
        excludeQueries.add(query);
    }
    
    public void setXPathContext(XPathContext context) {
        xpathContext = context;
    }
    
    public XPathContext getXPathContext() {
        return xpathContext;
    }
    
    private void prepareExcludedElements(Document document) {
        for (String query : excludeQueries) {
            Nodes nodes = document.query(query, xpathContext);
            excludedElements.add(nodes);
        }
    }
    
    private void cleanupExcludedElements() {
        excludedElements.clear();
    }
    
    private boolean isExcluded(Element element) {
        for (Nodes nodes : excludedElements) {
            if (nodes.contains(element)) {
                return true;
            }
        }
        return false;
    }
    
    private void convertRecursive(Element element) {
        if (! isExcluded(element)) {
            element.setNamespaceURI(targetNamespace);
            Elements childElements = element.getChildElements();
            for (int i = 0; i < childElements.size(); ++i) {
                Element childElement = childElements.get(i);
                convertRecursive(childElement);
            }
        }
    }
    
    public void apply(Document document) {
        prepareExcludedElements(document);
        convertRecursive(document.getRootElement());
        cleanupExcludedElements();
    }
}
