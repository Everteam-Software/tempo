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
package org.intalio.tempo.workflow.tas.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.authentication.AuthenticationConstants;
import org.intalio.tempo.security.token.TokenService;
import org.intalio.tempo.security.util.PropertyUtils;
import org.intalio.tempo.security.util.StringArrayUtils;
import org.intalio.tempo.security.ws.TokenClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An n3-security based implementation of {@link org.intalio.tempo.workflow.tas.core.AuthStrategy}.<br />
 * Uses a local token service to validate and authorize provided security credentials.
 * 
 * @author Iwan Memruk
 * @version $Revision: 722 $
 * @see org.intalio.tempo.workflow.tas.core.TaskAttachmentServiceImpl
 */
public class N3AuthStrategy implements AuthStrategy {

    /**
     * Log4J logger for this class.
     */
    private static final Logger _logger = LoggerFactory.getLogger(N3AuthStrategy.class);

    /**
     * An n3-security token service instance used to process security credentials.
     */
    protected TokenService _tokenService;

    /**
     * Endpoint for Security WS. Initiated from config file with Spring Framework XMLBeans technology.
     */
    private String _wsEndpoint;

    /**
     * Normalizes all allowed security indentifier delimiters ("/", "\", ":", "|") and
     * replaces them with the default <code>'\'</code> delimiter.
     * 
     * @param sourceId A possibly non-normalized security ID.
     * @return The idempotent normalized security ID.
     */
    private static String normalizeAuthIdentifier(String sourceId) {
        for (int i=0; i<sourceId.length(); i++) {
            if ("/|:".indexOf(sourceId.charAt(i)) >=0 )
                sourceId = sourceId.substring(0,i) + '\\' + sourceId.substring(i+1);
        }
        return sourceId;
    }
    

    /**
     * Instance constructor (used in the Spring Bean instantiation).
     *
     * Look for file {@code tempo-tas.xml}
     */
    public N3AuthStrategy() {
    }

    /**
     * Endpoint for Security WS. Initiated from config file with Spring Framework XMLBeans technology.
     */
    public String getWsEndpoint() {
        return _wsEndpoint;
    }

    /**
     * Endpoint for Security WS. Initiated from config file with Spring Framework XMLBeans technology.
     */
    public void setWsEndpoint(String wsEndpoint) {
        this._wsEndpoint = wsEndpoint;
    }

    protected TokenService connect2tokenService() throws Exception {
        if (_tokenService == null) {
            _tokenService = new TokenClient(_wsEndpoint);
        }
        return _tokenService;
    }

    
    public Property[] authenticate(AuthCredentials credentials)
            throws AuthException {
        String participantToken = credentials.getParticipantToken();
        try {
            Property[] properties = connect2tokenService().getTokenProperties(participantToken);

            String invokerUser = (String) PropertyUtils.getProperty(properties, AuthenticationConstants.PROPERTY_USER)
                    .getValue();
            invokerUser = N3AuthStrategy.normalizeAuthIdentifier(invokerUser);
            
            Property roleProperty = PropertyUtils.getProperty(properties, AuthenticationConstants.PROPERTY_ROLES);
            List<String> invokerRoles = new ArrayList<String>();
			for(String role: Arrays.asList(StringArrayUtils.parseCommaDelimited((String) roleProperty.getValue()))) {
				invokerRoles.add(normalizeAuthIdentifier(role));
			}
			if(_logger.isDebugEnabled()) {
				_logger.debug("Invoker user id: " + invokerUser);
				_logger.debug("Invoker roles: " + invokerRoles);
				_logger.debug("auth users: " + credentials.getAuthorizedUsers());
				_logger.debug("auth roles: " + credentials.getAuthorizedRoles());				
			}
                        
            boolean userAuthorized = false;
            
            for (String user : credentials.getAuthorizedUsers()) {
                if (user.equals(invokerUser)) {
                    userAuthorized = true;
                    break;
                }
            }
            
            if (! userAuthorized) {
                boolean roleAuthorized = false;

                for (String role : credentials.getAuthorizedRoles()) {
                    role = N3AuthStrategy.normalizeAuthIdentifier(role);
                    if (invokerRoles.contains(role)) {
                        roleAuthorized = true;
                        break;
                    }
                }
                
                if (! roleAuthorized) throw new AuthException("Access denied.");
            }
            
            return properties;
        } catch (Exception e) {
            throw new AuthException(e);
        }
    }

}
