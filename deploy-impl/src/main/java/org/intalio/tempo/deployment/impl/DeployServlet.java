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
package org.intalio.tempo.deployment.impl;

import static org.intalio.tempo.deployment.impl.LocalizedMessages._;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import javax.naming.AuthenticationException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.intalio.tempo.deployment.rmi.DeploymentServiceRMI;
import org.intalio.tempo.web.SysPropApplicationContextLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a REST web interface for deployment
 */
public class DeployServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(DeployServlet.class);

    private static final String DEFAULT_CONTEXT_FILE = "file:${org.intalio.tempo.configDirectory}/tempo-deploy.xml";

    DeploymentServiceImpl _deploy;

    final DeploymentServiceRMI _rmi = new DeploymentServiceRMI();

    boolean _rmiInit = true;

    @Override
    public void init() throws ServletException {
        SysPropApplicationContextLoader loader;
        String contextFile = getServletConfig().getInitParameter("contextConfigLocation");
        if (contextFile == null) {
            contextFile = DEFAULT_CONTEXT_FILE;
        }
        LOG.debug("Loading context: "+contextFile);
        try {
            loader = new SysPropApplicationContextLoader(contextFile);
        } catch (IOException except) {
            throw new ServletException(except);
        }
        _deploy = loader.getBean("deploymentService");
        
        _rmi.loadProperties();
        
        // _rmiInit = "TRUE".equalsIgnoreCase((String) props.get("rmi.init"));

        exportRMI();
        
        LOG.debug("Servlet initialized.");
    }

    @Override
    public void destroy() {
        unexportRMI();
    }
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException
    {
        if (LOG.isDebugEnabled()) LOG.debug("doDelete request="+request);
        String resourceUri = getResourceUri(request);
        try {
            authenticate(request);
            LOG.debug("Undeploying: '" + resourceUri + "'");
            
            // TODO: parse assembly version id
            // _deploy.undeployAssembly(resourceUri);
            
            LOG.debug("Undeployed: '" + resourceUri + "'");
        } catch (AuthenticationException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            warn("Authentication error", e);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            warn("Exception while undeploying package: '" + resourceUri + "'", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOG.debug("doGet request="+request);
        String resourceUri = getResourceUri(request);
        try {
            authenticate(request);
            LOG.debug("Listing: '" + resourceUri + "'");
            // _deploy.list();
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            warn("Exception while listing: '" + resourceUri + "'", e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException
    {
        if (LOG.isDebugEnabled()) {
            LOG.debug("doPut request="+request+" contentType="+request.getContentType()+
                      " contentLength="+request.getContentLength());
        }
        String resourceUri = getResourceUri(request);
        String contentType = request.getContentType();
        if (contentType == null) contentType = "application/octet-stream";
        int errorCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        InputStream payloadStream = request.getInputStream();
        try {
            authenticate(request);
            LOG.debug("Deploying: '" + resourceUri + "'");

            if (request.getContentLength() <= 0) {
                errorCode = HttpServletResponse.SC_NOT_ACCEPTABLE;
                throw new RuntimeException("Content length must be >= 0");
            }
            if (!contentType.equals("application/zip")) {
                errorCode = HttpServletResponse.SC_NOT_ACCEPTABLE;
                throw new RuntimeException("Content type must be 'application/zip'");
            }

            boolean replaceExistingAssemblies = "true".equalsIgnoreCase(request.getParameter("replaceExistingAssemblies"));
             
            _deploy.deployAssembly(resourceUri, payloadStream, replaceExistingAssemblies);

            response.setStatus(HttpServletResponse.SC_OK);
        } catch (AuthenticationException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(e.getMessage());
            LOG.warn("Authentication error", e);
        } catch (Exception e) {
            response.sendError(errorCode);
            response.getWriter().write(e.getMessage());
            warn("Exception while deploying: '" + resourceUri + "'", e);
        } finally {
            if (payloadStream != null) {
                payloadStream.close();
            }
        }
    }

    private void authenticate(HttpServletRequest request)
    throws AuthenticationException {
        // TODO
    }
    
    private void warn(String message, Throwable t) {
        if (LOG.isDebugEnabled()) {
            LOG.warn(message, t);
        } else {
            LOG.warn(message + ": " + t.getMessage());
        }
    }

    /**
     * Get relative resource URI.
     * <p/>
     * For example, "GET /servlet/123", this method will return "123"
     */
    private static String getResourceUri(HttpServletRequest request) {
        int base = request.getContextPath().length();
        String relativeUri = request.getRequestURI().substring(base);
        // remove any leading slashes
        while (relativeUri.startsWith("/")) {
            relativeUri = relativeUri.substring(1);
        }
        return relativeUri;
    }

    protected void exportRMI() {
        int port = _rmi.rmiPort;
        try {
            // Create RMI registry, if necessary
            if (_rmiInit) {
                try {
                    LocateRegistry.createRegistry(port);
                    LOG.debug("Created registry on port " + port);
                } catch (Exception ex) {
                    LOG.error(_("Could not create registry on port {0} (perhaps it's already there)", port));
                }
            }
            Registry registry = LocateRegistry.getRegistry(port);

            Remote deploy = UnicastRemoteObject.exportObject(_deploy, 0);
            registry.rebind(_rmi.deploymentServiceName, deploy);
            LOG.debug(_("Bound DeploymentService in RMI registry as {0} on registry port {1}", _rmi.deploymentServiceName, port));

            Remote callback = UnicastRemoteObject.exportObject(_deploy.getCallback(), 0);
            registry.rebind(_rmi.deploymentCallbackName, callback);
            LOG.debug(_("Bound DeploymentServiceCallback in RMI registry as {0} on registry port {1}", _rmi.deploymentCallbackName, port));
        } catch (Exception except) {
            throw new RuntimeException(except);
        }
    }
    
    protected void unexportRMI() {
        try {
            UnicastRemoteObject.unexportObject(_deploy, true);
        } catch (Exception except) {
            throw new RuntimeException(except);
        }
    }
    
}
    
