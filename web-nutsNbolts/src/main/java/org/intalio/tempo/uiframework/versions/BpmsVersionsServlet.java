/**
 * Copyright (c) 2005-2007 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 */

package org.intalio.tempo.uiframework.versions;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BpmsVersionsServlet extends HttpServlet {
    private static final long serialVersionUID = -76889544882620584L;

    private static final Logger LOGGER = LoggerFactory.getLogger(BpmsVersionsServlet.class);
	
	private static final String UNKNOWN = "unknown";
	
	private static final String VERSIONS_PROPERTIES= "versions.properties";

    public static final String BPMS_VERSION_PROP = "bpms-version";
    public static final String BPMS_BUILD_NUMBER_PROP = "bpms-build-number";

	private static Properties bpmsVersions = new Properties();
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		bpmsVersions = readConfig();
		
		Writer out = response.getWriter();
		out.write("<html><body>");		
		out.write("<ul>");
		out.write("<li>BPMS ");
		out.write(bpmsVersions.getProperty("bpms-version", UNKNOWN));
		out.write("</li>");
		out.write("<li>Console ");
		out.write(bpmsVersions.getProperty("console-version", UNKNOWN));
		out.write("</li>");
		out.write("<li>ODE ");
		out.write(bpmsVersions.getProperty("ode-version", UNKNOWN));
		out.write("</li>");
		out.write("<li>Workflow FDS ");
		out.write(bpmsVersions.getProperty("fds-version",UNKNOWN));
		out.write("</li>");
		out.write("<li>Workflow TAS ");
		out.write(bpmsVersions.getProperty("tas-version",UNKNOWN));
		out.write("</li>");
		out.write("<li>Workflow TMS ");
		out.write(bpmsVersions.getProperty("tms-version",UNKNOWN));
		out.write("</li>");
		out.write("<li>Workflow tms client ");
		out.write(bpmsVersions.getProperty("tms-client-version",UNKNOWN));
		out.write("</li>");
		out.write("<li>Workflow UI-FW ");
		out.write(bpmsVersions.getProperty("ui-fw-version",UNKNOWN));
		out.write("</li>");
		out.write("<li>Workflow WDS ");
		out.write(bpmsVersions.getProperty("wds-version", UNKNOWN));
		out.write("</li>");
		out.write("<li>Workflow X-forms manager ");
		out.write(bpmsVersions.getProperty("xforms-version", UNKNOWN));
		out.write("</li>");
		out.write("<li>Workflow XPath ");
		out.write(bpmsVersions.getProperty("xpath-version", UNKNOWN));
		out.write("</li>");
		out.write("<li>WSI ");
		out.write(bpmsVersions.getProperty("wsi-version", UNKNOWN));
		out.write("</li>");
		out.write("</ul>");
		out.write("</body></html>");
		out.flush();		
	}
	
    private static Properties readConfig() {
        try {
            InputStream is = BpmsVersionsServlet.class.getClassLoader().getResourceAsStream(VERSIONS_PROPERTIES);
            if (is == null) {
                throw new RuntimeException("Couldn't find " + VERSIONS_PROPERTIES + " in the default classpath.");
            }
            Properties properties = new Properties();
            try {
                properties.load(is);
            } catch (IOException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            } finally {
                try{if (is!=null) is.close();} catch (Exception e) {}
            }
            return properties;
        } catch(Exception ex) {
            LOGGER.warn("Unable to load " + VERSIONS_PROPERTIES + ":" + ex);
            return new Properties();
        }
    }

    public static Properties getBPMSVersionsProperties() {
        return bpmsVersions.isEmpty() ? readConfig() : bpmsVersions; 
    }
}
