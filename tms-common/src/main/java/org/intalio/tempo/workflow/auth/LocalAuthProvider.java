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
 */
package org.intalio.tempo.workflow.auth;

import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.impl.TokenServiceImpl;
import org.intalio.tempo.security.util.PropertyUtils;
import org.intalio.tempo.security.util.StringArrayUtils;
import org.intalio.tempo.workflow.auth.n3.N3AuthProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalAuthProvider implements IAuthProvider {
	public TokenServiceImpl _tokenService;
	private static final Logger _logger = LoggerFactory
			.getLogger(LocalAuthProvider.class);

	public void setTokenService(TokenServiceImpl tokenService) {
		_tokenService = tokenService;
	}

	public UserRoles authenticate(String participantToken) throws AuthException {

		assert participantToken != null : "Authentication with null token is called!";

		try {
			Property[] properties = _tokenService
					.getTokenProperties(participantToken);
			String invokerUser = (String) PropertyUtils.getProperty(properties,
					"user").getValue();
			if (_logger.isDebugEnabled()) {
				_logger.debug("Token '" + participantToken
						+ "' is resolved to " + invokerUser);
			}
			Property roleProperty = PropertyUtils.getProperty(properties,
					"roles");
			String[] invokerRoles = StringArrayUtils
					.parseCommaDelimited((String) roleProperty.getValue());
			if (_logger.isDebugEnabled()) {
				String roles = "";
				for (int i = 0; i < invokerRoles.length; i++)
					roles += (i == 0 ? "" : ",") + invokerRoles[i];
				_logger.debug("User " + invokerUser + " with roles " + roles);
			}
			
			UserRoles userRoles=new UserRoles(invokerUser, invokerRoles);
			userRoles.setWorkflowAdmin(_tokenService.isWorkflowAdmin(invokerUser));
			
			if (_logger.isDebugEnabled()){		
				_logger.debug("isWorkflowAdmin :" + userRoles.isWorkflowAdmin());
			}
			
			return userRoles;
		} catch (Exception e) {
			_logger.error("Exception while Authenticating users", e);
			throw new AuthException(e);
		}
	}
}
