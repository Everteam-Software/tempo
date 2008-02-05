/**
 * Copyright (c) 2005-2007 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 */
package org.intalio.tempo.workflow.dao;

import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.intalio.tempo.web.SysPropApplicationContextLoader;
import org.intalio.tempo.workflow.SpringInit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common class for making JPA-based connection factories.
 * Also takes care of setting the proper spring context when loading in an axis environment
 */
public abstract class AbstractJPAConnectionFactory {

    final static Logger log = LoggerFactory.getLogger(AbstractJPAConnectionFactory.class);
    protected EntityManagerFactory factory;
    
    public AbstractJPAConnectionFactory(Map<?, ?> properties) {
        log.info("Using the following JPA properties:" + properties);
        try {
            // check if we are loading from spring, and if so, 
            // apply the workaround for axis and spring class loading
        	try {
				SysPropApplicationContextLoader context = SpringInit.CONTEXT;
				if (context != null) {
					log.info("Using context file:"
							+ context.getApplicationContextFile());
					Thread currentThread = Thread.currentThread();
					currentThread.setContextClassLoader(context.getClass().getClassLoader());
				}
			} catch (Error e) {
				log.info("Not using spring context for classloading");
			}
            factory = Persistence.createEntityManagerFactory("org.intalio.tempo", properties);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.info("Factory was properly created:" + (factory != null));
    }
    
    public AbstractJPAConnectionFactory() {
    	Properties p = new Properties();
        try {
            p.load(this.getClass().getResourceAsStream("/jpa-properties.txt"));
        }
        catch (Exception e) {
            log.info("Properties not found.");
        }
        System.getProperties().putAll(p);
        factory = Persistence.createEntityManagerFactory("org.intalio.tempo", System.getProperties());
    }

    @Override
    protected void finalize() throws Throwable {
        factory.close();
        super.finalize();
    }

	public abstract Object openConnection();

}
