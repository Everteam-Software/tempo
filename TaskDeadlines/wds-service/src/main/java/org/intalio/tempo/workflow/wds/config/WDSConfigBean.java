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
package org.intalio.tempo.workflow.wds.config;

/**
 * Workflow deployment service configuration. (Instantiated using Spring)
 */
public class WDSConfigBean {

    /** JDBC URL of WDS data source. */
    private String _wdsDataSource;

    /** JDBC URL of TMS data source. */
    private String _tmsDataSource;

    private String _wdsEndpoint;

    public WDSConfigBean() {
        // nothing
    }

    public String getWdsEndpoint() {
        return _wdsEndpoint;
    }

    public void setWdsEndpoint(String wdsEndpoint) {
        _wdsEndpoint = wdsEndpoint;
    }

    /**
     * Sets the JDBC URL of WDS data source.
     */
    public void setWdsDataSource(String dataSource) {
        if (dataSource == null) throw new NullPointerException("dataSource");
        _wdsDataSource = dataSource;
    }

    /**
     * Returns the JDBC URL of WDS data source.
     */
    public String getWdsDataSource() {
        return _wdsDataSource;
    }

    /**
     * Sets the JDBC URL of TMS data source.
     */
    public void setTmsDataSource(String dataSource) {
        if (dataSource == null) throw new NullPointerException("dataSource");
        _tmsDataSource = dataSource;
    }

    /**
     * Returns the JDBC URL of TMS data source.
     */
    public String getTmsDataSource() {
        return _tmsDataSource;
    }
}
