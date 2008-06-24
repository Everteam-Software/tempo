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
 * @version $Revision: 42053 $ $Date: 2007-06-10 13:17:55 0000 (Sun, 10 Jun
 *          2007) $
 * @since 3.0
 */
public final class TempoUsernamePasswordAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {

  private TokenClient _tokenService;

  public TempoUsernamePasswordAuthenticationHandler() {
    log.warn(this.getClass().getName() + " is only to be used in a testing environment.  NEVER enable this in a production environment.");
  }

  public boolean authenticateUsernamePasswordInternal(final UsernamePasswordCredentials credentials) {
    final String username = credentials.getUsername();
    final String password = credentials.getPassword();
    try {
      String response = _tokenService.authenticateUser(username, password);
      log.debug(response);
      return true;
    } catch (Exception e) {
      log.debug("User [" + username + "] failed authentication");
      return false;
    }
  }

  public TokenClient get_tokenService() {
    return _tokenService;
  }

  public void set_tokenService(TokenClient service) {
    _tokenService = service;
  }

}
