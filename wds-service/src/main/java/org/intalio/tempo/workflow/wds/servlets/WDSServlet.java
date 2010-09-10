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
package org.intalio.tempo.workflow.wds.servlets;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.intalio.deploy.deployment.utils.DeploymentServiceRegister;
import org.intalio.tempo.web.SysPropApplicationContextLoader;
import org.intalio.tempo.workflow.wds.core.Item;
import org.intalio.tempo.workflow.wds.core.UnavailableItemException;
import org.intalio.tempo.workflow.wds.core.WDSService;
import org.intalio.tempo.workflow.wds.core.WDSServiceFactory;
import org.intalio.tempo.workflow.wds.core.XFormComponentManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a REST-style web interface for WDS.
 * <p />
 * The following methods are supported:
 * <ul>
 * <li><code>GET</code> -- retrieves the WDS item with the specified URI and returns it as the response.</li>
 * </ul>
 * <p />
 * The following status codes are returned:
 * <ul>
 * <li><code>200</code> -- if the request was processed successfully.</li>
 * <li><code>401</code> -- if an authentication/authorization error happened.</li>
 * <li><code>404</code> -- if the requested item was not found on WDS.</li>
 * <li><code>409</code> -- if the specified URI is already taken (appliable
 * for <code>PUT</code> requests).</li>
 * <li><code>500</code> -- if an unexpected error happened.</li>
 * </ul>
 */
public class WDSServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(WDSServlet.class);
    private static final long serialVersionUID = -5714415376114167497L;
    private static final String DEFAULT_CONFIG_FILE = "file:${org.intalio.tempo.configDirectory}/tempo-wds.xml";

    private WDSServiceFactory _wdsFactory;
    private XFormComponentManager _xformDeployer;
    private DeploymentServiceRegister _registerXForm;
    
    
    @Override
    public void init() throws ServletException {
        SysPropApplicationContextLoader loader;
        String configFile = getServletConfig().getInitParameter("contextConfigLocation");
        if (configFile == null) {
            configFile = DEFAULT_CONFIG_FILE;
        }
        LOG.debug("Loading WDS configuration...");
        try {
            loader = new SysPropApplicationContextLoader(configFile);
        } catch (IOException except) {
            throw new ServletException(except);
        }
        
        LOG.debug("Creating Service Factory...");
        _wdsFactory = loader.getBean("wds.servicefactory");
        _xformDeployer = new XFormComponentManager(_wdsFactory);
        _registerXForm = new DeploymentServiceRegister(_xformDeployer);
        _registerXForm.init();
        
        LOG.debug("Servlet initialized.");
    }

    @Override
    public void destroy() {
        super.destroy();
        if (_registerXForm != null)
            _registerXForm.destroy();
    }
    
    /**
     * Fetches a WDS item URI requested in an HTTP request.
     * <p />
     * For example, given the request "HTTP 1.1 GET /wds/123", this method will return "123"
     */
    private static String getResourceUri(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String relativeUri = requestUri.substring(request.getContextPath().length());
        // handle frequent typo when manually deploy form (using wds-cli)
        while (relativeUri.startsWith("/")) {
            relativeUri = relativeUri.substring(1);
        }
        LOG.debug("Resource URI: {}", relativeUri);
        return relativeUri;
    }

    /**
     * Fetches the participant token specified in an HTTP request.
     */
    private String getParticipantToken(HttpServletRequest request) {
        String participantToken = StringUtils.EMPTY;
        // FIXME: request.getHeader("Participant-Token");
        LOG.debug("Participant token: {}", participantToken);
        return participantToken;
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException 
    {
        String resourceUri = getResourceUri(request);
        String participantToken = getParticipantToken(request);
        WDSService service = _wdsFactory.getWDSService();;
        try {
            LOG.debug("Deleting item: {}", resourceUri);
            service.deleteItem(resourceUri, participantToken);
            LOG.debug("Item {} deleted OK", resourceUri);
        } catch (UnavailableItemException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            LOG.debug("Item not found: '" + resourceUri + "'");
        } finally {
            service.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOG.debug("doGet request={}", request.getRequestURI());

        String resourceUri = getResourceUri(request);
        if ("".equals(resourceUri)) {
            String text = "<html xmlns=\"http://www.w3.org/1999/xhtml\">"
                + "<head>"
                + "<title>Workflow Deployment Service</title>"
                + "<link rel=\"shortcut icon\" href=\"/favicon.ico\" type=\"image/vnd.microsoft.icon\" />"
                + "</head>"
                + "<body>"
                + "<p>Workflow Deployment Service</p>"
                + "</body>"
                + "</html>";
            response.getWriter().println(text);
        } else {
            String participantToken = getParticipantToken(request);
            WDSService service = null;
            try {
                service = _wdsFactory.getWDSService();
                
                Item item = service.retrieveItem(resourceUri, participantToken);
                OutputStream outputStream = response.getOutputStream();
                response.setContentType(item.getContentType());
                Date lastmodified = item.getLastmodified();
                if (lastmodified != null)
                    response.setDateHeader("Last-Modified", lastmodified.getTime());
                int length = IOUtils.copy(new ByteArrayInputStream(item.getPayload()), outputStream);
                response.setContentLength(length);
            } catch (UnavailableItemException e) {
                LOG.debug("Item not found: '" + resourceUri + "'");
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } finally {
                if (service != null)
                    service.close();
            }
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("doPut request={} contentType={} contentLength={}", 
                       new Object[] { request, request.getContentType(), request.getContentLength() });
        }
        String resourceUri = getResourceUri(request);
        String contentType = request.getContentType();
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        String participantToken = getParticipantToken(request);
        InputStream payloadStream = request.getInputStream();
        try {
            WDSService service =_wdsFactory.getWDSService();
            try {
                byte[] payload = IOUtils.toByteArray(payloadStream);
                Item item = new Item(resourceUri, contentType, payload);
        
                LOG.debug("Storing the item: {}", resourceUri);
                try {
                    service.deleteItem(item.getURI(), participantToken);
                } catch (UnavailableItemException except) {
                    // ignore
                }
                service.storeItem(item, participantToken);
                LOG.debug("Item {} stored OK.", resourceUri);
            } catch (UnavailableItemException e) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                LOG.warn("Item not found: '" + resourceUri + "'");
            } finally {
                service.close();
            }
        } finally {
            payloadStream.close();
        }
    }

}
