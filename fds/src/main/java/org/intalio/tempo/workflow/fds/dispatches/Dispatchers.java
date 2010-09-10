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
 */
package org.intalio.tempo.workflow.fds.dispatches;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Dispatchers {
    private static Logger logger = LoggerFactory.getLogger(Dispatchers.class);
    private static Map<String, Class<? extends IDispatcher>> dispatcherMap
        = new HashMap<String, Class<? extends IDispatcher>>();
        
    private static void registerDispatcherClass(String rootElementName, 
            Class<? extends IDispatcher> dispatcherClass) {
        logger.debug("Registering dispatcher " + dispatcherClass.getCanonicalName() + " for message type "
                + rootElementName);
        dispatcherMap.put(rootElementName, dispatcherClass);
    }
    
    static {
        registerDispatcherClass("notifyRequest", NotifyDispatcher.class);        
        registerDispatcherClass("escalateRequest", EscalateDispatcher.class);        
    }       
    
    public static IDispatcher createDispatcher(String rootElementName) throws NoDispatcherException {
        try {
            Class<? extends IDispatcher> dispatcherClass = dispatcherMap.get(rootElementName);
            if (dispatcherClass == null) {
                throw new NoDispatcherException(rootElementName);
            }
            
            logger.debug("Creating a dispatcher of class " + dispatcherClass.getCanonicalName());
            IDispatcher dispatcher = null;
            IDispatcher baseDispatcher = dispatcherClass.newInstance();
            if (logger.isDebugEnabled()) {
                return new LoggerDispatcher(baseDispatcher);
            } else {
                dispatcher = baseDispatcher;
            }
                        
            return dispatcher;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Dispatchers() {

    }
}
