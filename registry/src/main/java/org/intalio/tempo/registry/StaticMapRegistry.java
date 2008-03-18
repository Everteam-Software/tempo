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

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * StaticRegistry: registry based on a static weak hash map.   
 */
public class StaticMapRegistry implements Registry {
    private static final Logger LOG = LoggerFactory.getLogger(StaticMapRegistry.class);

    private static final MapRegistry MAP = new MapRegistry();

    static {
        try {
            MAP.init(System.getProperties());
        } catch (Exception e) {
            LOG.error("Error while initializing StaticMapRegistry", e);
        }
    }
    
    /**
     * Required no-arg public constructor
     */
    public StaticMapRegistry() {
        // nothing
    }

    /**
     * Bind a named object into the global registry.
     */
    public <T> void bind(String name, T object) {
        MAP.bind(name, object);
    }

    /**
     * Lookup a named object in the global registry.
     * The returned object is proxied using the current context class loader.
     */
    @SuppressWarnings("unchecked")
    public <T> T lookup(String name) {
        return (T) MAP.lookup(name);
    }

    /**
     * Lookup a named object in the global registry, and create a proxy
     * using the given class loader.
     */
    @SuppressWarnings("unchecked")
    public <T> T lookup(String name, ClassLoader loader) {
        return (T) MAP.lookup(name, loader);
    }

    /**
     * Lookup a named object in the global registry, without any proxy.
     */
    @SuppressWarnings("unchecked")
    public <T> T lookupNonProxied(String name) {
        return (T) MAP.lookupNonProxied(name);
    }
    
    public void init(Properties props) {
        // nothing; initialization happens in static block (above)
    }

}
