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

import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.token.TokenService;
import org.intalio.tempo.security.ws.TokenClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An n3-security based implementation of
 * {@link org.intalio.tempo.workflow.tas.core.AuthStrategy}.<br /> Uses a local
 * token service to validate and authorize provided security credentials.
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
     * An n3-security token service instance used to process security
     * credentials.
     */
    protected TokenService _tokenService;

    /**
     * Endpoint for Security WS. Initiated from config file with Spring
     * Framework XMLBeans technology.
     */
    private String _wsEndpoint;

    /**
     * Instance constructor (used in the Spring Bean instantiation).
     * 
     * Look for file {@code tempo-tas.xml}
     */
    public N3AuthStrategy() {
    }

    /**
     * Endpoint for Security WS. Initiated from config file with Spring
     * Framework XMLBeans technology.
     */
    public String getWsEndpoint() {
        return _wsEndpoint;
    }

    /**
     * Endpoint for Security WS. Initiated from config file with Spring
     * Framework XMLBeans technology.
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

    public Property[] authenticate(AuthCredentials credentials) throws AuthException {
        try {
            //TODO: TAS should be using the same code as TMS, or merge TAS with TMS altogether.
            String participantToken = credentials.getParticipantToken();
            Property[] properties = connect2tokenService().getTokenProperties(participantToken);
            return properties;
        } catch (Exception e) {
            throw new AuthException(e);
        }
    }
}