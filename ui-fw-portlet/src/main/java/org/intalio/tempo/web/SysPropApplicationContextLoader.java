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
 *
 * $Id: TaskManagementServicesFacade.java 5440 2006-06-09 08:58:15Z imemruk $
 * $Log:$
 */
package org.intalio.tempo.web;

import java.io.IOException;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.SystemPropertyUtils;

/**
 * An application context loader that supports loading configuration files using system properties, e.g.,
 * <br/>
 * <code>
 * loader = new SysPropApplicationContextLoader("${com.example.application.home}/config.xml");
 * myBean = loader.getBean("myConfig");
 * </code>
 *
 * System properties must be enclosed within "${" prefix and "}" suffix strings.
 */
public class SysPropApplicationContextLoader {

    private BeanFactory _beanFactory;

    private String _appContextFile;

    private static final String FILE_PREFIX = "file:";

    /**
     * Construct an ApplicationContextLoader and load the application context file.
     * 
     * @param appContextFile
     *            application context file; supports ${systemProperty} dereferencing.
     * @throws IOException
     *             if exception occurs during loading of the application context
     */
    public SysPropApplicationContextLoader(String appContextFile) throws IOException {
        if (appContextFile == null) {
            throw new IllegalArgumentException("Argument 'contextFile' is null");
        }
        _appContextFile = SystemPropertyUtils.resolvePlaceholders(appContextFile);
        if (_appContextFile.startsWith(FILE_PREFIX)) {
            _appContextFile = _appContextFile.substring(FILE_PREFIX.length());
        }
        Resource configResource = new FileSystemResource(_appContextFile);
        _beanFactory = new XmlBeanFactory(configResource);
    }

    /**
     * Return the name of the application context file
     */
    public String getApplicationContextFile() {
        return _appContextFile;
    }

    /**
     * Get a named bean from the application context
     */
    public <T> T getBean(String beanName) {
        if (beanName == null) {
            throw new IllegalArgumentException("Argument 'beanName' is null");
        }
        return (T) _beanFactory.getBean(beanName);
    }

}
