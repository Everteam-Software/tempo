package org.intalio.tempo.deployment.impl;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import javax.sql.DataSource;

public class SQLScript {

    BufferedReader _input;

    boolean _ignore;

    boolean _interactive;

    BufferedReader _console;

    static boolean _verbose = true;

    DataSource _ds;

    /**
     * Construct an SQLScript with necessary parameters.
     */
    public SQLScript(InputStream input, DataSource ds) throws IOException, SQLException, ClassNotFoundException {
        _console = new BufferedReader(new InputStreamReader(System.in));
        _ds = ds;
        _input = new BufferedReader(new InputStreamReader(input));
    }

    /**
     * Run the SQL Script. Which means: 1) Connect to database; 2) Execute the SQLScript statements; 3) Close the
     * database connection.
     */
    public void run() throws IOException, SQLException {
        executeScript();
    }

    /**
     * Set the interactive mode on/off.
     */
    public void setInteractive(boolean isInteractive) {
        _interactive = isInteractive;
    }

    /**
     * Set whether or not the execution stops upon an SQLException thrown by an executed statement.
     */
    public void setIgnoreErrors(boolean ignore) {
        _ignore = ignore;
    }

    public void setVerbose(boolean verbose) {
        _verbose = verbose;
    }

    /**
     * Executes statements from the SQLScript file.
     * 
     * If interactive mode is on, asks the user for confirmation on individual statements.
     */
    protected void executeScript() throws SQLException {
        Connection con = _ds.getConnection();
        try {
            Statement stmt = con.createStatement();
            try {
                while (true) {
                    String line = readNextLine();
                    if (line == null) {
                        // EOF detected
                        break;
                    }

                    if (line.startsWith(".") || line.startsWith("quit")) {
                        break;
                    }

                    System.out.println(line);

                    if (_interactive) {
                        boolean exec;

                        while (true) {
                            System.out.print("E)xecute, S)kip or A)bort: ");
                            System.out.flush();
                            String s = "";
                            try {
                                s = _console.readLine();
                            } catch (IOException ioe) {
                                ioe.printStackTrace();
                                continue;
                            }
                            if (s == null) {
                                continue;
                            }
                            if (s.equalsIgnoreCase("e")) {
                                exec = true;
                                break;
                            } else if (s.equalsIgnoreCase("s")) {
                                exec = false;
                                break;
                            } else if (s.equalsIgnoreCase("a")) {
                                return;
                            }
                        }
                        if (!exec) {
                            // skip this statement
                            continue;
                        }
                    }

                    try {
                        boolean hasResult = stmt.execute(line);

                        if (hasResult) {
                            ResultSet rs = stmt.getResultSet();
                            System.out.println();
                            System.out.print(resultSet2String(rs));
                            System.out.println();
                        }

                    } catch (SQLException sqle) {
                        if (!_ignore && !_interactive) {
                            sqle.printStackTrace();
                            break;
                        } else {
                            System.out.println(sqle.getMessage());
                        }
                    }

                }
            } finally {
                close(stmt);
            }
        } finally {
            close(con);
        }
    }

    /**
     * Convert a ResultSet into a display string.
     */
    public static String resultSet2String(ResultSet rs) throws java.sql.SQLException {
        StringBuffer buf = new StringBuffer();
        int columnCount = rs.getMetaData().getColumnCount();

        // print column titles
        for (int c = 1; c <= columnCount; c++) {
            buf.append("[");
            buf.append(rs.getMetaData().getColumnLabel(c));
            buf.append("]");

            if (c < columnCount) {
                buf.append("\t");
            }
        }
        buf.append("\n");
        buf.append("\n");

        // print column data
        while (rs.next()) {
            for (int c = 1; c <= columnCount; c++) {
                if ((rs.getMetaData().getColumnType(c) == Types.BLOB)
                        || (rs.getMetaData().getColumnType(c) == Types.BINARY)
                        || (rs.getMetaData().getColumnType(c) == Types.JAVA_OBJECT)
                        || (rs.getMetaData().getColumnType(c) == Types.LONGVARBINARY)
                        || (rs.getMetaData().getColumnType(c) == Types.VARBINARY)) {
                    buf.append("[BLOB]");
                } else {
                    buf.append(rs.getString(c));
                }

                if (c < columnCount) {
                    buf.append("\t");
                }
            }
            buf.append("\n");
        }

        return buf.toString();
    }

    /**
     * Read next statement from the SQLScript file.
     */
    protected String readNextLine() {
        try {
            StringBuffer buf = new StringBuffer();
            boolean gotData = false;

            while (true) {
                String s = _input.readLine();
                if (s == null) {
                    if (gotData) {
                        return buf.toString();
                    } else {
                        return null;
                    }
                }

                if (s.startsWith(".") || s.startsWith("quit")) {
                    return null;
                }

                if ((!s.trim().equals("")) && (!s.startsWith("#")) && (!s.startsWith("--"))) {
                    gotData = true;
                    buf.append(s);
                    buf.append("\n");
                    if (s.indexOf(";") >= 0) {
                        return buf.toString().substring(0, buf.toString().indexOf(";"));
                    }
                }
            }
        } catch (EOFException eofe) {
            return null;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
    }

    void close(Connection c) {
        if (c != null) {
            try {
                c.close();
            } catch (SQLException e) {
                // ignore
            }
        }
    }

    void close(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                // ignore
            }
        }
    }
}