package org.intalio.tempo.web.controller;

import java.rmi.RemoteException;

import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.authentication.AuthenticationException;
import org.intalio.tempo.security.rbac.RBACException;
import org.intalio.tempo.security.ws.TokenClient;

public class FakeTokenClient extends TokenClient{
    

    /**
     * Create a token service client
     * 
     * @param endpointUrl
     *            endpoint of the token service
     */
    public FakeTokenClient(String endpointUrl) {
        super(endpointUrl);
    }
    
    public String authenticateUser(String user, String password) throws AuthenticationException, RBACException, RemoteException {
        if (user.equalsIgnoreCase("test1"))
            return "FakeToken";
        else
            throw new AuthenticationException("Invalid user");
    }
    
    public Property[] getTokenProperties(String token) throws AuthenticationException, RemoteException {
        Property name = new Property();
        name.setName("user");
        name.setValue("test1");
        Property roles = new Property();
        roles.setName("roles");
        roles.setValue("test/role1;test/role2");
        Property[] ret = new Property[2];
        ret[0] = name;
        ret[1] = roles;
        return ret;
    }
}
