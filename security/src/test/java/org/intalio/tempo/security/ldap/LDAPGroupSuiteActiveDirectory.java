/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with the
 * written permission of Intalio Inc. or in accordance with the terms
 * and conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *
 * $Id: LDAPGroupSuiteActiveDirectory.java,v 1.1 2004/05/07 04:01:23 yip Exp $
 */
package org.intalio.tempo.security.ldap;

/**
 * LDAPGroupSuite
 * 
 * @author <a href="http://www.intalio.com">&copy; Intalio Inc.</a>
 */
public class LDAPGroupSuiteActiveDirectory extends AbstractSuite {

    /**
     * Constructor
     * 
     */
    public LDAPGroupSuiteActiveDirectory() {
        super();
    }

    /**
     * Constructor
     * @param cls
     */
    public LDAPGroupSuiteActiveDirectory(Class cls) {
        super(cls);
    }

    /**
     * Constructor
     * @param name
     */
    public LDAPGroupSuiteActiveDirectory(String name) {
        super(name);
    }

    /**
     * @see org.intalio.tempo.security.ldap.AbstractSuite#getConfigurationPath()
     */
    protected String getConfigurationPath() {
        return "org/intalio/tempo/security/ldap/test.groups.properties.ad";
    }

}
