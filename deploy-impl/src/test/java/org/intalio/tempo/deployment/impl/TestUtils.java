/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with
 * the written permission of Intalio Inc. or in accordance with
 * the terms and conditions stipulated in the agreement/contract
 * under which the program(s) have been supplied.
 *
 * $Id: SimpleSpringTest.java,v 1.5 2005/03/29 22:09:07 ssahuc Exp $
 */

package org.intalio.tempo.deployment.impl;

import java.io.File;
import java.io.IOException;

/**
 * Various test-related utility functions
 */
public class TestUtils {
    
    static File getTestBase() {
        try {
            String path = Utils.class.getResource("/test1.xml").toURI().getPath();
            return new File(path).getParentFile();
        } catch (Exception except) {
            throw new RuntimeException(except);
        }
    }
    
    static File getAssemblyDir(String assemblyName) {
        return new File(getTestBase(), assemblyName);
    }
    
    static File getTempDeployDir() {
        try {
            File temp = File.createTempFile("DeployServiceTest", "1");
            File dir = new File(temp.getParent(), "DeployServiceTest");
            return dir;
        } catch (IOException except) {
            throw new RuntimeException(except);
        }
    }
}
