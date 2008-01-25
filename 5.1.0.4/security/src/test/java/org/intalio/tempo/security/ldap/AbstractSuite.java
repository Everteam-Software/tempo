/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with the
 * written permission of Intalio Inc. or in accordance with the terms
 * and conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *
 * $Id: AbstractSuite.java,v 1.1 2003/09/20 02:24:11 yip Exp $
 */
package org.intalio.tempo.security.ldap;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.intalio.tempo.BaseSuite;

/**
 * Abstract Suite
 * 
 * @author <a href="http://www.intalio.com">&copy; Intalio Inc.</a>
 */
public abstract class AbstractSuite extends BaseSuite {

    private Map configuration;
    /**
     * Constructor
     * 
     */
    public AbstractSuite() {
        super();
    }

    /**
     * Constructor
     * @param cls
     */
    public AbstractSuite(Class cls) {
        super(cls);
    }

    /**
     * Constructor
     * @param name
     */
    public AbstractSuite(String name) {
        super(name);
    }

    public Map getConfiguration() throws IOException {
        return configuration;
    }
    
    protected abstract String getConfigurationPath();
    
    public synchronized void initConfiguration() throws Exception {
        // note, we might not have Log4J ready here
        String path = getConfigurationPath();
        ClassLoader cl = LDAPRBACProviderTest.class.getClassLoader();
        InputStream is = cl.getResourceAsStream(path);
        if (is==null) {
            System.err.println("Properties file cannot be located: "+path);
            return;
        }
        Properties result = new Properties();
        result.load(is);
        System.out.println("Properties loaded: "+result);
        configuration =  result;
    }
    
    public void setUp() throws Exception {
        super.setUp();
        initConfiguration();
    }
    
    public void tearDown() throws Exception {
        super.tearDown();
    }
    
}
