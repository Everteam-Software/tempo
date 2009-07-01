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
package org.intalio.tempo.workflow.wds.core;

import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * A JDBC implementation of item DAO connection factory.
 * <p />
 * Uses Proxool for JDBC pooling. See <a href="Proxool home page">http://proxool.sf.net/</a> for details about Proxool.
 * <p />
 * 
 * @author Iwan Memruk
 * @version $Revision: 486 $
 */
public class JdbcItemDaoConnectionFactory implements ItemDaoConnectionFactory {
    
    private DataSource _dataSource;

    /**
     * Instance constructor.
     *
     * @param dataSourceUrl JDBC URL of the datasource to use.
     */
    public JdbcItemDaoConnectionFactory(String dataSourceUrl) {
        try {
            InitialContext ctx = new InitialContext();
            _dataSource = (DataSource) ctx.lookup(dataSourceUrl);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized ItemDaoConnection getItemDaoConnection() {
        try {
            return new JdbcItemDaoConnection(_dataSource.getConnection());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
