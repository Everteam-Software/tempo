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

import java.lang.ref.WeakReference;
import java.rmi.Remote;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MapRegistry: registry based on a map of weak references   
 */
public class MapRegistry implements Registry, Remote {
    private static final Logger LOG = LoggerFactory.getLogger(MapRegistry.class);

    // weak hash map used to avoid memory retention
    private final Map<String, WeakReference<Object>> _map = new HashMap<String, WeakReference<Object>>();
    
    private boolean _debug = false;
    
    /**
     * Required no-arg public constructor
     */
    public MapRegistry() {
        // nothing
    }

    /**
     * Bind a named object into the global registry.
     */
    public <T> void bind(String name, T object) {
        synchronized (_map) {
            _map.put(name, new WeakReference<Object>(object));
        }
        if (_debug) {
            LOG.debug("bind: {}", name);
            dump();
        }
    }

    /**
     * Lookup a named object in the global registry.
     * The returned object is proxied using the current context class loader.
     */
    @SuppressWarnings("unchecked")
    public <T> T lookup(String name) {
        return (T) lookup(name, Thread.currentThread().getContextClassLoader());
    }

    /**
     * Lookup a named object in the global registry, and create a proxy
     * using the given class loader.
     */
    @SuppressWarnings("unchecked")
    public <T> T lookup(String name, ClassLoader loader) {
        if (_debug) {
            LOG.debug("lookup: {}", name);
            dump();
        }
        T proxiedObject = null;
        synchronized (_map) {
            WeakReference<T> weak = (WeakReference<T>) _map.get(name);
            if (weak != null) {
                proxiedObject = (T) weak.get();
                if (proxiedObject == null) 
                    _map.remove(name);
            }
        }
        if (proxiedObject == null) return null;
        RemoteProxy<T> proxy = new RemoteProxy<T>(proxiedObject, loader, proxiedObject.getClass().getClassLoader());
        return proxy.newProxyInstance();
    }

    /**
     * Lookup a named object in the global registry, without any proxy.
     */
    @SuppressWarnings("unchecked")
    public <T> T lookupNonProxied(String name) {
        synchronized (_map) {
            WeakReference<T> weak = (WeakReference<T>) _map.get(name);
            T obj = (T) weak.get();
            if (obj == null) 
                _map.remove(name);
            return obj;
        }
    }
    
    private void dump() {
        StringBuffer buf = new StringBuffer("MapRegistry");
        buf.append(" "+this.toString());
        for (Map.Entry<String, WeakReference<Object>> entry : _map.entrySet()) {
            buf.append("\nKey: ");
            buf.append(entry.getKey());
            buf.append(" Value: ");
            String className = "null";
            WeakReference<Object> weak = entry.getValue();
            if (weak != null && weak.get() != null)
                className = weak.get().getClass().getName();
            buf.append(className);
        }
        LOG.debug(buf.toString());
    }

    public void init(Properties props) {
        _debug = props.getProperty("org.intalio.tempo.registry.debug", "false").equalsIgnoreCase("true");
    }

}
