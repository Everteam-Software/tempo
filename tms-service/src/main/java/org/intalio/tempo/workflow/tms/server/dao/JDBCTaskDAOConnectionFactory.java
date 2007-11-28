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
 *
 * $Id: TaskManagementServicesFacade.java 5440 2006-06-09 08:58:15Z imemruk $
 * $Log:$
 */

package org.intalio.tempo.workflow.tms.server.dao;

import java.sql.Connection;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JDBCTaskDAOConnectionFactory implements ITaskDAOConnectionFactory {

    private static final Logger _logger = LoggerFactory.getLogger(JDBCTaskDAOConnectionFactory.class);

    private DataSource _dataSource;

    public JDBCTaskDAOConnectionFactory(String jndiPath) {
        if (jndiPath == null) throw new IllegalArgumentException("JNDI path is null");
        try {
            InitialContext initialContext = new InitialContext();
            _logger.debug("About to get hook for DataSource " + jndiPath);
            _dataSource = (DataSource) initialContext.lookup(jndiPath);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    public ITaskDAOConnection openConnection() {
        Connection con = null;
        try {
            _logger.debug("Getting connection to TMS DB");
            con = _dataSource.getConnection();
            return new JDBCTaskDAOConnection(_dataSource.getConnection());
        } catch (Exception e) {
            close(con);
            throw new RuntimeException(e);
        }
    }

    static void close(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (Throwable t2) {
                // ignore
            }
        }
    }

}
