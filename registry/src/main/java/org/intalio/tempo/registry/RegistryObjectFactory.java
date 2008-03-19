/**
 * Copyright (c) 2008 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 */
package org.intalio.tempo.registry;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistryObjectFactory implements ObjectFactory {
    private static final Logger LOG = LoggerFactory.getLogger(RegistryObjectFactory.class);
    
    private StaticMapRegistry _registry = new StaticMapRegistry();

    public RegistryObjectFactory() {
        // nothing
        LOG.debug("RegistryObjectFactory constructor");
    }
    
    @SuppressWarnings("unchecked")
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable environment)
        throws NamingException
    {
        LOG.debug("RegistryObjectFactory getObjectInstance");
        return _registry;
    }

}