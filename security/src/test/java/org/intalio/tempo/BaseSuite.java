/**
* Copyright (C) 2001, Intalio Inc.
*
* The program(s) herein may be used and/or copied only with
* the written permission of Intalio Inc. or in accordance with
* the terms and conditions stipulated in the agreement/contract
* under which the program(s) have been supplied.
*
* $Id: BaseSuite.java,v 1.2 2003/09/26 18:32:50 boisvert Exp $
* Date        Author    Changes
*
* 2001/05/01  Mills     Created
*
*/

package org.intalio.tempo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.intalio.tempo.test.FuncTestSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The very basic suite. It provides not much other than log4j setup.
 */
public class BaseSuite extends FuncTestSuite {

    /**
     * Property name for the Log4J configuration file.
     */
    public static final String PROPERTY_LOG_FILE = "log4j.file";

    /**
     * Property name for refresh interval.
     */
    public static final String PROPERTY_LOG_REFRESH = "log4j.refresh";

    /**
     * Default Log4J configuration file name.
     */
    public static final String DEFAULT_LOG_FILE = "log4j.xml";

    /**
     * Default Log4J configuration file refresh interval (in milliseconds).
     */
    public static final long DEFAULT_LOG_REFRESH = 5000;
        
    protected Logger            log;
    
    public BaseSuite() {

        super();
    }
    
    public BaseSuite( Class cls ) {
        super(cls);
    }
    
    public BaseSuite( String name ) {
        super(name);
    }
    
    protected void setUp() throws Exception {
        log = LoggerFactory.getLogger( "tempo.main" );

        // read "system.properties" file
        Properties props = System.getProperties();
        File systemPropertyFile = new File( "system.properties" );
        if ( systemPropertyFile.exists() ) {
            try {
                props.load( new FileInputStream( systemPropertyFile ) );
            } catch ( IOException ioe ) {
                log.info( "Error while loading system.properties", ioe );
            }
        }
    }
}
