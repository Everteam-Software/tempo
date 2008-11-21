/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.apache.directory.intalio.embed.webapp;

import java.io.File;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.directory.server.configuration.MutableServerStartupConfiguration;
import org.apache.directory.server.core.configuration.MutablePartitionConfiguration;
import org.apache.directory.server.core.configuration.PartitionConfiguration;
import org.apache.directory.server.core.configuration.ShutdownConfiguration;
import org.apache.directory.server.ldap.LdapConfiguration;
import org.apache.directory.shared.ldap.constants.SchemaConstants;
import org.apache.directory.shared.ldap.message.AttributesImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Servlet context listener to start and stop ApacheDS.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory
 *         Project</a>
 */
public class StartStopListener implements ServletContextListener {
	
	private static final Logger LOG = LoggerFactory.getLogger(StartStopListener.class);

    /**
     * Startup ApacheDS embedded.
     */
    public void contextInitialized(ServletContextEvent evt) {

        try {
            // Create a default configuration
            MutableServerStartupConfiguration pcfg = new MutableServerStartupConfiguration();
            
            MutablePartitionConfiguration pc = new MutablePartitionConfiguration();
            pc.setId("intalio");
            pc.setSuffix("dc=intalio,dc=com");

            Attributes entry = new AttributesImpl();
            entry.put( SchemaConstants.OBJECT_CLASS_AT, SchemaConstants.TOP_OC );
            entry.get( SchemaConstants.OBJECT_CLASS_AT ).add( "domain" );
            entry.get( SchemaConstants.OBJECT_CLASS_AT ).add( SchemaConstants.EXTENSIBLE_OBJECT_OC );
            entry.put( "dc", "intalio" );
            pc.setContextEntry( entry );
            
            Set<PartitionConfiguration> pcs = new HashSet<PartitionConfiguration>();
            pcs.add( pc );
            pcfg.setPartitionConfigurations(pcs);
            
            // Determine an appropriate working directory
            ServletContext servletContext = evt.getServletContext();
            File workingDir = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
            pcfg.setWorkingDirectory(workingDir);

            // Set LDAP port to 10389
            LdapConfiguration ldapCfg = pcfg.getLdapConfiguration();
			int port = 10389;
			try {
			   String value = evt.getServletContext().getInitParameter("ldap.port");
			   port = Integer.parseInt(value);	
			} catch(Exception e) {
				LOG.error("Could not parse the port properly",e);
			}
            ldapCfg.setIpPort(port);

            // Start the Server
            Hashtable env = EnvHelper.createEnv();
            env.putAll(pcfg.toJndiEnvironment());
            new InitialDirContext(env);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Shutdown ApacheDS embedded.
     */
    public void contextDestroyed(ServletContextEvent evt) {
        try {
            Hashtable env = EnvHelper.createEnv();
            ShutdownConfiguration cfg = new ShutdownConfiguration();
            env.putAll(cfg.toJndiEnvironment());
            new InitialDirContext(env);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }
}
