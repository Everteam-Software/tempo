/**
 * Copyright (c) 2005-2006 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 */
package org.intalio.tempo.workflow.fds;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class holds the configuration of the FDS. <br>
 * It implements the <i>Singleton</i> pattern.
 * <p>
 * The configuration is loaded from a resource by the name of
 * <code>fds-config.xml</code>
 * <p>
 * Its format is straightforward and XML-based. See the default instance for
 * details.
 * <p>
 * If the configuration loading fails, an error is logged and defaults are used.
 * 
 * @author Iwan Memruk
 * @version $Revision: 842 $
 */
public final class FormDispatcherConfiguration {
    /**
     * Configuration file location property
     */
    public static final String CONFIG_DIR_PROPERTY = "org.intalio.tempo.configDirectory";

    /**
     * The name of the resource containing the configuration.
     */
    private static final String _CONFIG_RESOURCE_NAME = "fds-config.xml";

    /**
     * The Log4j logger for this class.
     */
    private static Logger _log = LoggerFactory.getLogger(FormDispatcherConfiguration.class);

    /**
     * The shared singleton instance of this class.
     */
    private static FormDispatcherConfiguration _instance = new FormDispatcherConfiguration();

    /**
     * The base URL of PXE deployment. <br>
     * The initialization value is the default.
     */
    private String _pxeBaseUrl = "http://localhost:8080/ode";

    /**
     * The FDS endpoint
     */
    private String _fdsUrl = "http://localhost:8080/fds/workflow/ib4p";

    /**
     * The URL of the Workflow Processes, relative to the base URL of PXE
     * deployment. <br>
     * The initialization value is the default.
     */
    private String _workflowProcessesRelativeUrl = "/workflow/ib4p";

    /**
     * Endpoint for the Task Management Service
     */
    private String _tmsUrl = "http://localhost:8080/axis2/services/TaskManagementServices";

    /**
     * Timeout when sending messages
     */
    private int _httpTimeout = 10000;

    /**
     * Returns the shared singleton instance of this class.
     * 
     * @return The shared singleton instance of this class.
     */
    public static FormDispatcherConfiguration getInstance() {
        return _instance;
    }

    /**
     * Returns the base URL of PXE deployment.
     * 
     * @return The base URL of PXE deployment.
     */
    public String getPxeBaseUrl() {
        return _pxeBaseUrl;
    }

    public int getHttpTimeout() {
        return _httpTimeout;
    }

    public void setHttpTimeout(int httpTimeout) {
        _httpTimeout = httpTimeout;
    }

    /**
     * Returns the endpoint for FDS
     * 
     * @return the FDS endpoint
     */
    public String getFdsUrl() {
        return _fdsUrl;
    }

    /**
     * Returns the URL of the Workflow Processes, relative to the base URL of
     * PXE deployment.
     * 
     * @return The URL of the Workflow Processes, relative to the base URL of
     *         PXE deployment.
     */
    public String getWorkflowProcessesRelativeUrl() {
        return _workflowProcessesRelativeUrl;
    }

    /**
     * Returns the TMS endpoint
     * 
     * @return the TMS endpoint
     */
    public String getTmsUrl() {
        return _tmsUrl;
    }

    /**
     * Instance constructor. <br>
     * Tries to load the configuration from the configuration resource. <br>
     * If the loading fails, logs an error and uses the defaults.
     */
    private FormDispatcherConfiguration() {
        try {
            String configDir = System.getProperty(CONFIG_DIR_PROPERTY);
            if (configDir == null) {
                _log.error("Property " + CONFIG_DIR_PROPERTY + " not set; using configuration defaults for FDS");
                return;
            }
            File f = new File(configDir, _CONFIG_RESOURCE_NAME);
            if (!f.exists()) {
                _log.error("Missing FDS configuration file: " + f.toString());
                return;
            }
            InputStream configInputStream = new FileInputStream(f);
            Document configDocument = new SAXReader().read(configInputStream);

            String pxeBaseUrl = configDocument.valueOf("/config/pxeBaseUrl");
            String workflowProcessesRelativeUrl = configDocument.valueOf("/config/workflowProcessesRelativeUrl");
            String tmsUrl = configDocument.valueOf("/config/tmsUrl");
            String fdsUrl = configDocument.valueOf("/config/fdsUrl");

            _pxeBaseUrl = pxeBaseUrl;
            _workflowProcessesRelativeUrl = workflowProcessesRelativeUrl;
            _tmsUrl = tmsUrl;
            _fdsUrl = fdsUrl;

            try {
                String timeout = configDocument.valueOf("/config/httpTimeout");
                if (timeout != null && !"".equalsIgnoreCase(timeout))
                    _httpTimeout = Integer.parseInt(timeout);
            } finally {
                _log.debug("Using following FDS timeout in ms:" + _httpTimeout);
            }

        } catch (Exception e) {
            _log.error("Failed to load the configuration: " + e.getMessage(), e);
        }
    }
}
