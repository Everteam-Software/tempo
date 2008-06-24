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

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registry Factory
 */
public class RegistryFactory {
    private static final Logger LOG = LoggerFactory.getLogger(RegistryFactory.class);

    public static final String DEFAULT_PROPERTY_FILE = "${org.intalio.tempo.configDirectory}/tempo-registry.properties";

    private String _propertyFile = DEFAULT_PROPERTY_FILE;

    private Properties _props;

    private Registry _registry;
    
    public RegistryFactory() {
        // nothing
    }

    public Properties getProperties() {
        return _props;
    }

    public void setProperties(Properties props) {
        _props = props;
    }
    
    public String getPropertyFile() {
        return _propertyFile;
    }
    
    public void setPropertyFile(String file) {
        _propertyFile = file;
    }
    
    /**
     * Initialize the factory from the supplied properties or from properties file (default).
     */
    @SuppressWarnings("unchecked")
    public synchronized void init() {
        if (_propertyFile != null) {
            _propertyFile = resolveSystemProperties(_propertyFile);
        }
        try {
            if (_props == null) {
                _props = new Properties();
                if (new File(_propertyFile).exists()) {
                    _props.load(new FileInputStream(_propertyFile));
                } else {
                    LOG.warn("Registry configuration file not available: "+_propertyFile);
                }
            }
            
            LOG.debug("Initializing RegistryFactory with properties: "+_props);
            
            String className = (String) _props.getProperty("org.intalio.tempo.registry.class", StaticMapRegistry.class.getName());
            boolean useContextClassLoader = _props.getProperty("org.intalio.tempo.registry.contextClassLoader", "true").equalsIgnoreCase("true");
            Class<Registry> clazz;
            if (useContextClassLoader) {
                try {
                    clazz = (Class<Registry>) Class.forName(className, true, Thread.currentThread().getContextClassLoader());
                } catch (ClassNotFoundException ex) {
                    // fallback to current classloader
                    clazz = (Class<Registry>) Class.forName(className);
                }
            } else {
                try {
                    clazz = (Class<Registry>) Class.forName(className);
                } catch (ClassNotFoundException ex) {
                    // fallback to context classloader
                    clazz = (Class<Registry>) Class.forName(className, true, Thread.currentThread().getContextClassLoader());
                }
            }                
            if (clazz == null)
                throw new IllegalStateException("Unable to load class: "+className);
            _registry = clazz.newInstance();
            _registry.init(_props);
        } catch (Exception except) {
            throw new RuntimeException("Exception while initializing RegistryFactory", except);
        }
    }

    public synchronized Registry getRegistry() {
        return _registry;
    }

    private static String resolveSystemProperties(String str) {
        int fromIndex = 0;
        while (true) {
            int start = str.indexOf("${", fromIndex);
            if (start < 0) break;
            int end = str.indexOf("}", start);
            if (end < 0) break;
            String replace = str.substring(start, end+1);
            String key = str.substring(start+2, end);
            String value = System.getProperty(key);
            if (value != null) {
                str = str.replace(replace, value);
            } else {
                fromIndex = start+2;
            }
        }
        return str;
    }
    
}
