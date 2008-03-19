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

import java.rmi.Remote;
import java.util.Properties;


/**
 * Registry: A locator abstraction or JNDI alternative since not appservers have a global writable 
 * JNDI context.
 */
public interface Registry extends Remote {

    
    public void init(Properties props);
    
    /**
     * Bind a named object into the registry.
     */
    public <T> void bind(String name, T object);

    /**
     * Lookup a named object in the registry.
     * The returned object is proxied using the current context class loader.
     */
    public <T> T lookup(String name);

    /**
     * Lookup a named object in the registry, and create a proxy using the given class loader.
     */
    public <T> T lookup(String name, ClassLoader loader);

    /**
     * Lookup a named object in the registry, without any proxy.
     */
    public <T> T lookupNonProxied(String name);
     
}
