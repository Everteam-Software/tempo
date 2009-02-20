/**
 * Copyright (c) 2007-2008 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 */

package org.intalio.tempo.deployment.impl;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.intalio.tempo.deployment.AssemblyId;
import org.intalio.tempo.deployment.ComponentId;
import org.intalio.tempo.deployment.DeployedAssembly;
import org.intalio.tempo.deployment.DeployedComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Maintain persistent state of deployed assemblies, components and resources.
 * <p>
 * Thread-safe
 */
public class Persistence {
    private static final Logger LOG = LoggerFactory.getLogger(Persistence.class);

    private final File _deployDir;
    
    private final DataSource _ds;
    
    // Connection held during transaction, if any.
    private ThreadLocal<Connection> _connection = new ThreadLocal<Connection>();
    
    public Persistence(File deployDir, DataSource ds) {
        _deployDir = deployDir;
        _ds = ds;
    }
   
    public void startTransaction() {
        if (_connection.get() != null) {
            rollback("Connection was already open");
            release();
            throw new IllegalStateException("Transaction already started");
        }
        try {
            _connection.set(_ds.getConnection());
            _connection.get().setAutoCommit(false);
        } catch (Exception e) {
            release();
            throw new PersistenceException(e);
        }
    }
    
    public void commitTransaction() {
        ensureTransaction();
        try {
            _connection.get().commit();
        } catch (Exception e) {
            rollback("Error during commit");
            throw new PersistenceException(e);
        } finally {
            release();
        }
    }

    public void rollbackTransaction(String reason) {
        rollback(reason);
        release();
    }
    
    /**
     * Add a deployed assembly
     */
    void add(DeployedAssembly assembly) throws PersistenceException {
        String name = assembly.getAssemblyId().getAssemblyName();
        int version = assembly.getAssemblyId().getAssemblyVersion();
        String adir = _deployDir.toURI().relativize((new File(assembly.getAssemblyDir())).toURI()).getPath();
        boolean active = true;
        Connection c = null;
        try {
            c = getConnection();
            // Insert assembly
            EasyStatement inserta = new EasyStatement(c, "INSERT INTO DEPLOY_ASSEMBLIES VALUES (?,?,?,?)");
            try {
                inserta.write(name);
                inserta.write(version);
                inserta.write(adir);
                inserta.write(active);
                inserta.execute();
            } finally {
                inserta.close();
            }
            // Insert components
            for (DeployedComponent dc: assembly.getDeployedComponents()) {
                File assemblyDir = new File(assembly.getAssemblyDir());
                String component = dc.getComponentId().getComponentName();
                String cdir = assemblyDir.toURI().relativize(new File(dc.getComponentDir()).toURI()).getPath();
                String manager = dc.getComponentManagerName();

                EasyStatement insertc = new EasyStatement(c, "INSERT INTO DEPLOY_COMPONENTS VALUES (?,?,?,?,?)");
                try {
                    insertc.write(name);
                    insertc.write(version);
                    insertc.write(component);
                    insertc.write(manager);
                    insertc.write(cdir);
                    insertc.execute();
                } finally {
                    insertc.close();
                }
            }
        } catch (SQLException e) {
            throw new PersistenceException(e);
        } finally {
            close(c);
        }
    }
    
    /**
     * Retire *any* currently active version of the current assembly
     */
    void retire(String assembly) throws PersistenceException {
        Connection c = null;
        try {
            c = getConnection();
            EasyStatement inserta = new EasyStatement(c, "UPDATE DEPLOY_ASSEMBLIES SET CACTIVE = 0 WHERE ASSEMBLY = ?");
            try {
                inserta.write(assembly);
                inserta.execute();
            } finally {
                inserta.close();
            }
        } catch (SQLException e) {
            throw new PersistenceException(e);
        } finally {
            close(c);
        }
    }

    /**
     * Load persistent deployed state
     */
    Map<AssemblyId, DeployedAssembly> load() {
        Map<AssemblyId, DeployedAssembly> map = new HashMap<AssemblyId, DeployedAssembly>();
        Connection c = null;
        try {
            c = getConnection();

            EasyStatement selecta = new EasyStatement(c, "SELECT * FROM DEPLOY_ASSEMBLIES");
            try {
                EasyResultSet rs = selecta.executeQuery();
                while (rs.next()) {
                    String assembly = rs.readString();
                    int version = rs.readInt();
                    String adir = rs.readString();
                    boolean active = rs.readBoolean();
                    AssemblyId aid = new AssemblyId(assembly, version);
                    adir = (new File(_deployDir, adir)).toString();
                    DeployedAssembly da = new DeployedAssembly(aid, adir, new ArrayList<DeployedComponent>(), active);
                    map.put(aid, da);
                }
            } finally {
                selecta.close();
            }

            EasyStatement selectb = new EasyStatement(c, "SELECT * FROM DEPLOY_COMPONENTS");
            try {
                EasyResultSet rs = selectb.executeQuery();
                while (rs.next()) {
                    String assembly = rs.readString();
                    int version = rs.readInt();
                    String component = rs.readString();
                    String manager = rs.readString();
                    String cdir = rs.readString();
                    AssemblyId aid = new AssemblyId(assembly, version);
                    ComponentId cid = new ComponentId(aid, component);
                    DeployedAssembly da = map.get(aid);
                    if (da == null) {
                        LOG.error("Deployed component entry is missing parent assembly: "+cid);
                    } else {
                        cdir = (new File(da.getAssemblyDir(), cdir)).toString();
                        DeployedComponent dc = new DeployedComponent(cid, cdir, manager);
                        da.getDeployedComponents().add(dc);
                    }
                } 
                rs.close();
            } finally {
                selectb.close();
            }
            return map;
        } catch (SQLException e) {
            throw new PersistenceException(e);
        } finally {
            close(c);
        }
    }

    /**
     * Remove assembly from persistent state
     */
    void remove(AssemblyId aid) {
        Connection c = null;
        EasyStatement stmt;
        try {
            c = getConnection();

            stmt = new EasyStatement(c, "DELETE FROM DEPLOY_RESOURCES WHERE ASSEMBLY = ? AND VERSION = ?");
            stmt.write(aid.getAssemblyName());
            stmt.write(aid.getAssemblyVersion());
            stmt.execute();
            stmt.close();
            
            stmt = new EasyStatement(c, "DELETE FROM DEPLOY_COMPONENTS WHERE ASSEMBLY = ? AND VERSION = ?");
            stmt.write(aid.getAssemblyName());
            stmt.write(aid.getAssemblyVersion());
            stmt.execute();
            stmt.close();

            stmt = new EasyStatement(c, "DELETE FROM DEPLOY_ASSEMBLIES WHERE ASSEMBLY = ? AND VERSION = ?");
            stmt.write(aid.getAssemblyName());
            stmt.write(aid.getAssemblyVersion());
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            throw new PersistenceException(e);
        } finally {
            close(c);
        }
    }

    private Connection getConnection() throws SQLException {
        Connection c = _connection.get();
        if (c != null) return c;
        else return _ds.getConnection();
    }
    
    private void ensureTransaction() {
        if (_connection.get() == null) 
            throw new IllegalStateException("No active transaction");
    }

    private void close(Connection c) {
        if (c != null && c != _connection.get()) {
            try {
                c.close();
            } catch (SQLException e) {
                // ignore
            }
        }
    }

    private void rollback(String reason) {
        Connection c = _connection.get();
        if (c != null) {
            LOG.warn("Deployment transaction rolled back: "+reason);
            try {
                c.rollback();
            } catch (SQLException e) {
                // ignore
            }
        }
    }

    private void release() {
        Connection c = _connection.get();
        if (c != null) {
            try {
                c.setAutoCommit(true);
            } catch (SQLException e) {
                // ignore
            }
            try {
                c.close();
            } catch (SQLException e) {
                // ignore
            }
            _connection.set(null);
        }
    }
    
}
