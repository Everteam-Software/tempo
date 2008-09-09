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

import javax.naming.InitialContext;

/**
 * EmbeddedJNDIRegistry   
 */
public class JNDIMapRegistry implements Registry {

    public static final String DEFAULT_JNDI_LOOKUP  = "java:comp/env/registry/TempoRegistry";

    private String _jndiLookup = DEFAULT_JNDI_LOOKUP;
    
    /**
     * Required no-arg public constructor
     */
    public JNDIMapRegistry() {
        // nothing
    }

    /**
     * Bind a named object into the global registry.
     */
    public <T> void bind(String name, T object) {
        try {
            Registry registry = internalLookup(_jndiLookup, Thread.currentThread().getContextClassLoader());
            if (registry == null)
                throw new IllegalStateException("Name not found: "+_jndiLookup);
            registry.bind(name, object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Lookup a named object in the global registry.
     * The returned object is proxied using the current context class loader.
     */
    @SuppressWarnings("unchecked")
    public <T> T lookup(String name) {
        try {
            Registry registry = internalLookup(_jndiLookup, Thread.currentThread().getContextClassLoader());
            if (registry == null)
                throw new IllegalStateException("Name not found: "+_jndiLookup);
            return (T) registry.lookup(name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Lookup a named object in the global registry, and create a proxy
     * using the given class loader.
     */
    @SuppressWarnings("unchecked")
    public <T> T lookup(String name, ClassLoader loader) {
        try {
            Registry registry = internalLookup(_jndiLookup, loader);
            if (registry == null)
                throw new IllegalStateException("Name not found: "+_jndiLookup);
            return (T) registry.lookup(name, loader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Lookup a named object in the global registry, without any proxy.
     */
    @SuppressWarnings("unchecked")
    public <T> T lookupNonProxied(String name) {
        try {
            InitialContext context = new InitialContext();
            Registry registry = (Registry) context.lookup(_jndiLookup);
            if (registry == null)
                throw new IllegalStateException("Name not found: "+_jndiLookup);
            return (T) registry.lookup(name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public void init(Properties props) {
        _jndiLookup = props.getProperty("org.intalio.tempo.registry.jndi.lookup", DEFAULT_JNDI_LOOKUP);
    }

    @SuppressWarnings("unchecked")
    private <T> T internalLookup(String name, ClassLoader loader) {
        T proxiedObject = null;
        try {
            InitialContext context = new InitialContext();
            proxiedObject = (T) context.lookup(name);
            if (proxiedObject == null) return null;
            RemoteProxy<T> proxy = new RemoteProxy<T>(proxiedObject, loader, proxiedObject.getClass().getClassLoader());
            return proxy.newProxyInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
