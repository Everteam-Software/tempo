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

package org.intalio.tempo.workflow.auth.n3;

import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.token.TokenService;
import org.intalio.tempo.security.util.PropertyUtils;
import org.intalio.tempo.security.util.StringArrayUtils;
import org.intalio.tempo.security.ws.TokenClient;
import org.intalio.tempo.workflow.auth.AuthException;
import org.intalio.tempo.workflow.auth.IAuthProvider;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class N3AuthProvider implements IAuthProvider {

    private static final Logger _logger = LoggerFactory.getLogger(N3AuthProvider.class);

    private TokenService _tokenService;

    private String _wsEndpoint;

    public N3AuthProvider() {
        // empty constructor for Spring
    }

    public void setWsEndpoint(String wsEndpoint) {
        _wsEndpoint = wsEndpoint;
    }

    public UserRoles authenticate(String participantToken) throws AuthException {
        assert participantToken != null : "Authentication with null token is called!";

        try {
            Property[] properties = connect2tokenService().getTokenProperties(participantToken);
            String invokerUser = (String) PropertyUtils.getProperty(properties, "user").getValue();
            if (_logger.isDebugEnabled()) {
                _logger.debug("Token '" + participantToken + "' is resolved to " + invokerUser);
            }
            Property roleProperty = PropertyUtils.getProperty(properties, "roles");
            String[] invokerRoles = StringArrayUtils.parseCommaDelimited((String) roleProperty.getValue());
            if (_logger.isDebugEnabled()) {
                String roles = "";
                for (int i = 0; i < invokerRoles.length; i++)
                    roles += (i == 0 ? "" : ",") + invokerRoles[i];
                _logger.debug("User " + invokerUser + " with roles " + roles);
            }
            UserRoles userRoles=new UserRoles(invokerUser, invokerRoles);

            Property isWorkFlowAdmin = PropertyUtils.getProperty(properties, "isWorkflowAdmin");
            if(isWorkFlowAdmin!=null)
            userRoles.setWorkflowAdmin(Boolean.parseBoolean(isWorkFlowAdmin.getValue().toString()));
            else
            	userRoles.setWorkflowAdmin(false);
            
			if (_logger.isDebugEnabled()){		
				_logger.debug("isWorkflowAdmin :" + userRoles.isWorkflowAdmin());
			}	
			
            return userRoles;
        } catch (Exception e) {
            throw new AuthException(e);
        }
    }

    /*private boolean isWorkflowAdmin(String user) throws Exception  {
        	return	connect2tokenService().isWorkflowAdmin(user);        
    }*/
    
    
    private TokenService connect2tokenService() throws Exception {
        if (_tokenService == null) {
            _logger.debug("Initialize connect to " + _wsEndpoint);
            _tokenService = getTokenClient();
        }
        return _tokenService;
    }
    
    protected TokenClient getTokenClient(){
        return new TokenClient(_wsEndpoint);
    }

}
