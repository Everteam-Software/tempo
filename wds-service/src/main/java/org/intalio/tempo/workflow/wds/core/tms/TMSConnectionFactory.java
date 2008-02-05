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
package org.intalio.tempo.workflow.wds.core.tms;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * This class is a factory for end-users to get 
 * {@link org.intalio.tempo.workflow.wds.core.tms.TMSConnection} instances.
 */
public class TMSConnectionFactory implements TMSConnectionFactoryInterface {

    private DataSource _dataSource;

    /**
     * Instance constructor
     */
    public TMSConnectionFactory() {
  
    }

	/**
	 *
     * @param dataSourceUrlJDBC URL of TMS database.
     */
    public void setDataSourceUrl(String dataSourceUrl) {
        try {
            InitialContext ctx = new InitialContext();
            _dataSource = (DataSource) ctx.lookup(dataSourceUrl);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a new TMS connection instance. <br />
     * It is required that you use the <code>close()</code> method after using the connection instance.
     */
    public synchronized TMSConnectionInterface getTMSConnection() {
        try {
            Connection jdbcConnection = _dataSource.getConnection();
            TMSConnectionInterface tmsConnection = new TMSConnection(jdbcConnection);
            return tmsConnection;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
