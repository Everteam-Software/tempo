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
package org.intalio.tempo.workflow.tas.axis2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.jaxen.JaxenException;
import org.jaxen.NamespaceContext;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;

/**
 * Implements common high-level XPath operations.
 * <p />
 * Please note that this class treats any invalid queries as the developer's error and thus throws a
 * {@link java.lang.RuntimeException} which is not recommended to catch and process.
 * <p />
 * Based on Jaxen.
 */
class AxiomXPathHelper {

    /**
     * Caches parsed XPath queries to lessen performance loss from re-parsing the same query multiple times.
     */
    private Map<String, XPath> _queryCache = new HashMap<String, XPath>();

    /**
     * The common namespace context reused by all {@link XPath} instances managed by this instances.
     */
    private NamespaceContext _namespaceContext;

    /**
     * Instance constructor.
     * 
     * @param namespaceMap
     *            A prefix->namespace map for all necessary namespace prefix bindings.
     */
    public AxiomXPathHelper(Map<String, String> namespaceMap) {
        _namespaceContext = new SimpleNamespaceContext(namespaceMap);
    }

    /**
     * Returns a cached {@link XPath} instance for a specific query.
     * If there is no cached instance for the query, a new instance is created and stored in cache.
     */
    private synchronized XPath getXPathUsingCache(String query) {
        XPath result = _queryCache.get(query);
        if (result == null) {
            result = createXPath(query);
            _queryCache.put(query, result);
        }
        return result;
    }

    /**
     * Creates a new {@link XPath} instance for an XPath query.
     * The new instance will use the namespace context kept in {@link #_namespaceContext}.
     */
    private XPath createXPath(String query) {
        try {
            XPath xpath = new AXIOMXPath(query);
            xpath.setNamespaceContext(_namespaceContext);
            return xpath;
        } catch (JaxenException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Prepares an {@link OMElement} for usage as an XPath context root.
     */
    private static void prepareRoot(OMElement root) {
        if (!root.isComplete()) root.build();
    }

    /**
     * Evaluates an XPath query to a string.
     * In case the result is empty, an {@link InvalidMessageFormatException} is thrown.
     */
    public String getRequiredString(OMElement root, String query)
            throws InvalidMessageFormatException {
        String result = getString(root, query);
        if (result == null) {
            throw new InvalidMessageFormatException("A required value '" + query + "' not found in the message.");
        } else {
            return result;
        }
    }

    /**
     * Evaluates an XPath query to a string.
     * In case the result is empty, <code>null</code> is returned.
     */
    public String getString(OMElement root, String query) {
        prepareRoot(root);
        try {
            XPath xpath = getXPathUsingCache(query);
            OMNode node = (OMNode) xpath.selectSingleNode(root);
            String result = null;
            if (node instanceof OMElement) {
                OMElement element = (OMElement) node;
                result = element.getText();
            } else if (node instanceof OMAttribute) {
                OMAttribute attribute = (OMAttribute) node;
                result = attribute.getAttributeValue();
            }
            return result;
        } catch (JaxenException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Evaluates an XPath query and returns a single target element.
     * In case the query does not select an element, an {@link InvalidMessageFormatException} is thrown.
     */
    public OMElement getRequiredElement(OMElement root, String query)
            throws InvalidMessageFormatException {
        OMElement result = getElement(root, query);
        if (result == null) {
            throw new InvalidMessageFormatException("A required element '" + query + "' not found in the message");
        } else {
            return result;
        }
    }

    /**
     * Evaluates an XPath query and returns a single target element.
     * In case the query does not select an element, <code>null</code> is returned.
     */
    public OMElement getElement(OMElement root, String query) {
        prepareRoot(root);
        try {
            XPath xpath = getXPathUsingCache(query);
            OMNode node = (OMNode) xpath.selectSingleNode(root);
            OMElement result = null;
            if (node instanceof OMElement) {
                result = (OMElement) node;
            }
            return result;
        } catch (JaxenException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Evaluates an XPath query and returns all target elements.
     * In case the query does not select any element, an {@link InvalidMessageFormatException} is thrown.
     */
    public OMElement[] getRequiredElements(OMElement root, String query)
            throws InvalidMessageFormatException {
        OMElement[] result = getElements(root, query);
        if (result == null) {
            throw new InvalidMessageFormatException("A required element '" + query + "' not found in the message");
        } else {
            return result;
        }
    }

    /**
     * Evaluates an XPath query and returns all target elements.
     * In case the query does not select any element, <code>null</code> is returned.
     */
    public OMElement[] getElements(OMElement root, String query) {
        prepareRoot(root);
        try {
            XPath xpath = getXPathUsingCache(query);
            List nodes = xpath.selectNodes(root);
            OMElement[] result = new OMElement[nodes.size()];
            int i = 0;
            for (Object node : nodes) {
                if (node instanceof OMElement) {
                    result[i] = (OMElement) node;
                    i++;
                } else {
                    throw new RuntimeException("Non-element result set for element list query: '" + query + "'");
                }
            }
            return result;
        } catch (JaxenException e) {
            throw new RuntimeException(e);
        }
    }
}
