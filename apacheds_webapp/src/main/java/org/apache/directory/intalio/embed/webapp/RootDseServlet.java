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

import java.io.IOException;
import java.io.PrintWriter;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A servlet which displays the Root DSE of the embedded server.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class RootDseServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            resp.setContentType("text/plain");
            PrintWriter out = resp.getWriter();

            out.println("*** ApacheDS RootDSE ***\n");

            DirContext ctx = new InitialDirContext(EnvHelper.createEnv());

            SearchControls ctls = new SearchControls();
            ctls.setReturningAttributes(new String[] { "*", "+" });
            ctls.setSearchScope(SearchControls.OBJECT_SCOPE);

            NamingEnumeration result = ctx.search("", "(objectClass=*)", ctls);
            if (result.hasMore()) {
                SearchResult entry = (SearchResult) result.next();
                Attributes as = entry.getAttributes();

                NamingEnumeration ids = as.getIDs();
                while (ids.hasMore()) {
                    String id = (String) ids.next();
                    Attribute attr = as.get(id);
                    for (int i = 0; i < attr.size(); ++i) {
                        out.println(id + ": " + attr.get(i));
                    }
                }
            }
            ctx.close();

            out.flush();
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
