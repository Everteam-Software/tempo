package org.intalio.tempo.security;

import org.intalio.tempo.security.authentication.AuthenticationException;
import org.intalio.tempo.security.authentication.provider.AuthenticationProvider;
import org.intalio.tempo.security.provider.SecurityProvider;
import org.intalio.tempo.security.rbac.RBACException;
import org.intalio.tempo.security.rbac.provider.RBACProvider;

public class DummySecurityProvider implements SecurityProvider{

    public void dispose() throws RBACException {
        // TODO Auto-generated method stub
        
    }

    public AuthenticationProvider getAuthenticationProvider(String realm) throws AuthenticationException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    public RBACProvider getRBACProvider(String realm) throws RBACException {
        // TODO Auto-generated method stub
        return null;
    }

    public String[] getRealms() throws AuthenticationException, RBACException {
        String[] realms = new String[]{"test1", "test2"};
        return realms;
    }

    public void initialize(Object config) throws AuthenticationException, RBACException {
        // TODO Auto-generated method stub
        
    }

    public void setName(String name) {
        // TODO Auto-generated method stub
        
    }

}
