/*
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
 * $Id: FormManagerBroker.java 5440 2006-06-09 08:58:15Z imemruk $
 */
package org.intalio.tempo.uiframework.forms;

import org.intalio.tempo.uiframework.forms.FormManager;


/**
 * This is the broker who decides which of Forms Manager implementation to use in order
 * to represent the selected task for user. Currently the only implementation is XForm Manager
 * based on XForms technology.
 * In principle the decision can be made dynamically, based, for example, on some task properties.
 * Though currently the decision made yet on deployment-phase: the Form Manager implementation is configured
 * in <code>faces-config.xml</code>.
 *
 * Last modified: $Date: 2007-07-10 16:05:40 -0700 (Tue, 10 Jul 2007) $
 * @version $Revision: 1124 $
 * @author Oleg Zenzin
 */
public class FormManagerBroker {
    private static final FormManagerBroker INSTANCE = new FormManagerBroker();

    private FormManager _formManager;

    private FormManagerBroker() {}

    /**
     * Provides the Form Manager implementation.
     * The implementation to be used is preset in configuration file <code>faces-config.xml</code>.
     */
    public FormManager getFormManager() {
        return _formManager;
    }

    /**
     * This setter provided to serve Managed Bean idiom of JSF: see file <code>faces-config.xml</code>.
     */
    public void setFormManager(FormManager formManager) {
        _formManager = formManager;
    }

    public static FormManagerBroker getInstance() {
        return INSTANCE;
    }
}
