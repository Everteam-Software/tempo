package org.intalio.tempo.cas;

import org.intalio.tempo.security.ws.TokenClient;
import org.jasig.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;

/**
 * Simple test implementation of a AuthenticationHandler that returns true if
 * the username and password match. This class should never be enabled in a
 * production environment and is only designed to facilitate unit testing and
 * load testing.
 * 
 * @author Scott Battaglia
 * @version $Revision: 42053 $ $Date: 2007-06-10 13:17:55 0000 (Sun, 10 Jun 2007) $
 * @since 3.0
 */
public final class TempoUsernamePasswordAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {

    public TempoUsernamePasswordAuthenticationHandler() {
        log.warn(this.getClass().getName() + " is only to be used in a testing environment.  NEVER enable this in a production environment.");
    }

    public boolean authenticateUsernamePasswordInternal(
        final UsernamePasswordCredentials credentials) {
        final String username = credentials.getUsername();
        final String password = credentials.getPassword();

        TokenClient tc = new TokenClient("http://localhost:8080/axis2/services/TokenService");
        try {
            String response = tc.authenticateUser(username, password);
            log.debug(response);
            return true;
        } catch (Exception e) {
            log.debug("User [" + username +  "] failed authentication");
            //return false;
			return false;
		}
    }
}
