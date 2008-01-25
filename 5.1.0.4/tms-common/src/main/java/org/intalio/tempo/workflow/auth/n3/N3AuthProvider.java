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

import org.apache.log4j.Logger;
import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.token.TokenService;
import org.intalio.tempo.security.util.PropertyUtils;
import org.intalio.tempo.security.util.StringArrayUtils;
import org.intalio.tempo.security.ws.TokenClient;
import org.intalio.tempo.workflow.auth.AuthException;
import org.intalio.tempo.workflow.auth.IAuthProvider;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class N3AuthProvider implements IAuthProvider {

    private static final Logger _logger = Logger.getLogger(N3AuthProvider.class);

    private static final String TOKEN_SERVICE_BEAN_NAME = "tokenService";

    private TokenService _tokenService;

    private String _wsEndpoint;

    public N3AuthProvider() {
        // empty constructor for Spring
    }
    
    /**
     * This constructor is left for temporary campatibility with UI-FW not migrated to Security WS
     * @param resourceName
     * @param resourceClassLoader
     * @throws Exception
     */
    public N3AuthProvider(String resourceName, ClassLoader resourceClassLoader) throws Exception {
        assert resourceName != null : "No file to read TOM authenticatio configuration!";

        ClassLoader previousClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(resourceClassLoader);

            Resource beanConfig = new ClassPathResource(resourceName);
            XmlBeanFactory beanFactory = new XmlBeanFactory(beanConfig);

            _tokenService = (TokenService) beanFactory.getBean(TOKEN_SERVICE_BEAN_NAME);
            _logger.debug("Spring Bean '" +
                    TOKEN_SERVICE_BEAN_NAME + "' is read from file " + resourceName + " as " + _tokenService);

        } catch (Exception e) {
            _logger.error("Cannot properly read Spring Bean " + TOKEN_SERVICE_BEAN_NAME, e);
        } finally {
            Thread.currentThread().setContextClassLoader(previousClassLoader);
        }
    }

    public void setWsEndpoint(String wsEndpoint) {
        _wsEndpoint = wsEndpoint;
    }

    public UserRoles authenticate(String participantToken) throws AuthException {
        assert participantToken != null : "Authentication with null token is called!";

        try {
            Property[] properties = connect2tokenService().getTokenProperties(participantToken);
            _logger.debug("Token '" + participantToken + "' is resolved to " + properties);

            String invokerUser = (String) PropertyUtils.getProperty(properties, "user").getValue();
            Property roleProperty = PropertyUtils.getProperty(properties, "roles");
            String[] invokerRoles = StringArrayUtils.parseCommaDelimited((String) roleProperty.getValue());
            if (_logger.isDebugEnabled()) {
                String roles = "";
                for (int i=0; i<invokerRoles.length; i++) roles += (i==0 ? "" : ",") + invokerRoles[i];
                _logger.debug("User " + invokerUser + " with roles " + roles);
            }
            return new UserRoles(invokerUser, invokerRoles);
        } catch (Exception e) {
            throw new AuthException(e);
        }
    }

    private TokenService connect2tokenService() throws Exception {
        if (_tokenService == null) {
            _logger.debug("Initialize connect to " + _wsEndpoint);
            _tokenService = new TokenClient(_wsEndpoint);
        }
        return _tokenService;
    }


}
