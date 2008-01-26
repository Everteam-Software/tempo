/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with the
 * written permission of Intalio Inc. or in accordance with the terms
 * and conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *
 * $Id: LDAPRoleSuite.java,v 1.2 2003/09/30 01:31:24 yip Exp $
 */
package org.intalio.tempo.security.ldap;

/**
 * LDAPRoleSuite
 * 
 * @author <a href="http://www.intalio.com">&copy; Intalio Inc.</a>
 */
public class LDAPRoleSuite extends AbstractSuite {

    /**
     * Constructor
     * 
     */
    public LDAPRoleSuite() {
        super();
    }

    /**
     * Constructor
     * @param cls
     */
    public LDAPRoleSuite(Class cls) {
        super(cls);
    }

    /**
     * Constructor
     * @param name
     */
    public LDAPRoleSuite(String name) {
        super(name);
    }

    /**
     * @see org.intalio.tempo.security.ldap.AbstractSuite#getConfigurationPath()
     */
    protected String getConfigurationPath() {
        return "org/intalio/tempo/security/ldap/test.roles.properties";
    }

}
