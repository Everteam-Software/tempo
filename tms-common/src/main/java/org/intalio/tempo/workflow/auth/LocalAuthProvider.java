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
private static final Logger _logger = LoggerFactory.getLogger(N3AuthProvider.class);
public void setTokenService(TokenServiceImpl tokenService){
	_tokenService=tokenService;
}	
@Override
	public UserRoles authenticate(String participantToken) throws AuthException {
		
		assert participantToken != null : "Authentication with null token is called!";

        try {
            Property[] properties = _tokenService.getTokenProperties(participantToken);
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
            return new UserRoles(invokerUser, invokerRoles);
        } catch (Exception e) {
        	_logger.error(e.getMessage(), e);
            throw new AuthException(e);
        }
	}

}
