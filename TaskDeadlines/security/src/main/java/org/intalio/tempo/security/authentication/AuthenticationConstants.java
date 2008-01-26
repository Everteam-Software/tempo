/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with the
 * written permission of Intalio Inc. or in accordance with the terms
 * and conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package org.intalio.tempo.security.authentication;

/**
 * Constants used in the authentication system, most notably for the
 * passing of credential properties.
 *
 * @author <a href="http://www.intalio.com">&copy; Intalio Inc.</a>
 */
public interface AuthenticationConstants
{

    /**
     * User id property
     */
    public static final String PROPERTY_USER = "user";

    /**
     * Password property
     */
    public static final String PROPERTY_PASSWORD = "password";
    
    /**
     * Timestamp property
     */
    public static final String PROPERTY_TIMESTAMP = "timestamp";
    
    /**
     * Nonce property
     */
    public static final String PROPERTY_NONCE = "nonce";
    
    /**
     * Digest property
     */
    public static final String PROPERTY_DIGEST = "digest";
    
    
    /**
     * Token issued time property
     */
    public static final String PROPERTY_ISSUED = "issued";

    /**
     * List of roles property
     */
    public static final String PROPERTY_ROLES = "roles";

    /**
     * X.509 certificate property
     */
    public static final String PROPERTY_X509_CERTIFICATE = "X509certificate";
    
}
