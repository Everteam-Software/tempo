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
 * JNDIRegistry: A JNDI-based registry
 */
public class JNDIRegistry implements Registry {

    /**
     * Required no-arg public constructor
     */
    public JNDIRegistry() {
        // nothing
    }

    public <T> void bind(String name, T object) {
        try {
            InitialContext context = new InitialContext();
            context.rebind(name, object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T lookup(String name) {
        return (T) lookup(name, Thread.currentThread().getContextClassLoader());
    }

    @SuppressWarnings("unchecked")
    public <T> T lookup(String name, ClassLoader loader) {
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

    @SuppressWarnings("unchecked")
    public <T> T lookupNonProxied(String name) {
        try {
            InitialContext context = new InitialContext();
            return (T) context.lookup(name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public void init(Properties props) {
        // nothing
    }

}
