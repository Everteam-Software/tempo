/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with the
 * written permission of Intalio Inc. or in accordance with the terms
 * and conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package org.intalio.tempo.security.provider;

import org.intalio.tempo.security.authentication.AuthenticationException;
import org.intalio.tempo.security.authentication.provider.AuthenticationProvider;
import org.intalio.tempo.security.rbac.RBACException;
import org.intalio.tempo.security.rbac.provider.RBACProvider;

/**
 * Security provider, a factory interface providing concrete 
 * implementations of the RBACProvider and AuthenticationProvider.
 * <p>
 * This factory exists to support the co-initialization of both providers 
 * using the shared configuration if necessary.
 * <p>
 * Implementations of this factory MUST have a public no-argument constructor.
 * The provider is first configured via the <code>initialize</code> method.  After,
 * a client may obtain and use the various security sub-system implementations
 * until the provider is shut down by calling <code>dispose</code>.
 *
 * @author <a href="http://www.intalio.com">&copy; Intalio Inc.</a>
 */
public interface SecurityProvider
{

    /**
     * Initialize the provider with an implementation-dependent configuration.
     * <p>
     * This method is called right after instantiation and prior to any use
     * of the provider.
     *
     * @param config Configuration of the security provider.
     */
    public void initialize( Object config )
        throws AuthenticationException, RBACException;
        

    /**
     * Return the unique name of this security provider.
     */
    public String getName();
    
	
	/**
	 * Set the unique name of this security provider.
	 */
	public void setName( String name );

    
    /**
     * Return the realms federated by this security provider.
     */
    public String[] getRealms()
        throws AuthenticationException, RBACException;

    
    /**
     * Return the RBACProvider.
     * <p>
     * The provider is assumed to be initialized.
     * <p>
     * An implementation MAY return <code>null</code> if these functions
     * are not available for this realm or in this provider.
     */
    public RBACProvider getRBACProvider( String realm )
        throws RBACException;
    

    /**
     * Return the AuthenticationProvider.
     * <p>
     * The provider is assumed to be initialized.
     * <p>
     * An implementation MAY return <code>null</code> if these functions
     * are not available for this realm or in this provider.
     */
    public AuthenticationProvider getAuthenticationProvider( String realm )
        throws AuthenticationException;
    

    /**
     * Shut down and dispose of any resources allocated by the provider.
     */
    public void dispose()
        throws RBACException;
    
}
