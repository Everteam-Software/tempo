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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.intalio.tempo.deployment.DeploymentMessage;
import org.intalio.tempo.deployment.utils.DeploymentServiceRegister;
import org.intalio.tempo.web.SysPropApplicationContextLoader;
import org.intalio.tempo.workflow.auth.AuthException;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.tms.UnavailableTaskException;
import org.intalio.tempo.workflow.wds.core.ComponentManager;
import org.intalio.tempo.workflow.wds.core.Item;
import org.intalio.tempo.workflow.wds.core.PIPALoader;
import org.intalio.tempo.workflow.wds.core.UnavailableItemException;
import org.intalio.tempo.workflow.wds.core.WDSService;
import org.intalio.tempo.workflow.wds.core.WDSServiceFactory;
import org.intalio.tempo.workflow.wds.core.XFormComponentManager;
import org.intalio.tempo.workflow.wds.core.xforms.XFormsProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a REST-style web interface for WDS.
 * <p />
 * The following methods are supported:
 * <ul>
 * <li><code>GET</code> -- retrieves the WDS item with the specified URI and
 * returns it as the response.</li>
 * <li><code>PUT</code> -- stores the body of the response as a WDS item with
 * the specified URI.</li>
 * <li><code>DELETE</code> -- deletes the WDS item with the specified URI.</li>
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
 * 
 * @version $Revision: 1176 $
 */
public class WDSServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(WDSServlet.class);
    private static final long serialVersionUID = -5714415376114167497L;
    private static final String DEFAULT_CONFIG_FILE = "file:${org.intalio.tempo.configDirectory}/tempo-tms.xml";

    private WDSServiceFactory _wdsFactory;
    private ComponentManager _deployer;
    private XFormComponentManager _xformDeployer;
    private DeploymentServiceRegister _register;
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
        _deployer = new ComponentManager(_wdsFactory);
        _register = new DeploymentServiceRegister(_deployer);
        _register.init();

        _xformDeployer = new XFormComponentManager(_wdsFactory);
        _registerXForm = new DeploymentServiceRegister(_xformDeployer);
        _registerXForm.init();
        
        LOG.debug("Servlet initialized.");
    }

    @Override
    public void destroy() {
        super.destroy();
        if (_register != null)
            _register.destroy();
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
            
            if (Boolean.valueOf(request.getHeader("Delete-PIPA-Tasks"))) {
                String formUrl = request.getRequestURL().toString();
                String wdsEndPoint = _wdsFactory.getWdsEndpoint();
                if (formUrl.startsWith(wdsEndPoint)) {
                    formUrl = formUrl.substring(wdsEndPoint.length());
                }
                service.deletePIPA(participantToken, formUrl);
            } else {
                service.deleteItem(resourceUri, participantToken);
            }
            LOG.debug("Item {} deleted OK", resourceUri);
        } catch (AuthException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            LOG.warn("Authentication error", e);
        } catch (UnavailableItemException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            LOG.warn("Item not found: '" + resourceUri + "'");
        } catch (UnavailableTaskException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            LOG.warn("PIPA not found: '" + resourceUri + "'");
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
                
                LOG.debug("Retrieving the item.");
                Item item = service.retrieveItem(resourceUri, participantToken);
                OutputStream outputStream = response.getOutputStream();
                response.setContentType(item.getContentType());

                LOG.debug("Sending the data..");
                int length = IOUtils.copy(new ByteArrayInputStream(item.getPayload()), outputStream);
                response.setContentLength(length);

                LOG.debug("Item retrieved & sent OK.");
            } catch (UnavailableItemException e) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                LOG.warn("Item not found: '" + resourceUri + "'");
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
                if (request.getContentLength() != 0) {
                    if ("application/zip".equals(contentType)) {
                        processZipfile(resourceUri, participantToken, payloadStream, service);
                    } else {
                        processItem(request, resourceUri, contentType, participantToken, payloadStream, service);
                    }
                } else if ("True".equals(request.getHeader("Create-PIPA-Task"))) {
                    processCreatePipa(request, participantToken, service);
                }
                response.setStatus(HttpServletResponse.SC_OK);
            } catch (InvalidRequestFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(e.getMessage());
                LOG.warn("Invalid request message format", e);
            } catch (AuthException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(e.getMessage());
                LOG.warn("Authentication error", e);
            } catch (UnavailableItemException e) {
                response.sendError(HttpServletResponse.SC_CONFLICT);
                response.getWriter().write(e.getMessage());
                LOG.warn("URI already taken", e);
            } catch (UnavailableTaskException e) {
                response.sendError(HttpServletResponse.SC_CONFLICT);
                response.getWriter().write(e.getMessage());
                LOG.warn("Task not available", e);
            } finally {
                service.close();
            }
        } finally {
            payloadStream.close();
        }
    }

    /**
     * Store an item. The item can be either:
     * <ul>
     * <li>xform; ; in this case we process it slightly using the
     * <code>XFormsProcessor</code></li>
     * <li>a generic item ; in this case, we just stream the content to the
     * <code>Item</code> object as a byte[] array</li>
     * </ul>
     */
    private void processItem(HttpServletRequest request, String resourceUri, String contentType,
            String participantToken, InputStream payloadStream, WDSService service)
            throws InvalidRequestFormatException, IOException, UnavailableItemException {
        Item item;
        if ("True".equals(request.getHeader("Is-XForm"))) {
            try {
                item = XFormsProcessor.processXForm(resourceUri, payloadStream);
            } catch (Exception e) {
                throw new InvalidRequestFormatException("Invalid XForm XML.", e);
            }
        } else {
            byte[] payload = IOUtils.toByteArray(payloadStream);
            item = new Item(resourceUri, contentType, payload);
        }

        LOG.debug("Storing the item: {}", resourceUri);
        try {
            service.deleteItem(item.getURI(), participantToken);
        } catch (UnavailableItemException except) {
            // ignore
        }
        service.storeItem(item, participantToken);
        LOG.debug("Item {} stored OK.", resourceUri);
    }

    /**
     * Generates a new PIPA using parameters found in the HTTP headers.
     * 
     * @throws UnavailableTaskException 
     */
    private void processCreatePipa(HttpServletRequest request, String participantToken, WDSService service)
            throws InvalidRequestFormatException, AuthException, UnavailableTaskException {
        PIPATask pipaTask = PIPALoader.parsePipa(request);
        if (LOG.isDebugEnabled()) LOG.debug("Storing pipa task:\n{}", pipaTask);
        service.storePipaTask(pipaTask, participantToken);
        LOG.debug("Pipa task stored OK.");
    }

    private void processZipfile(String resourceUri, String participantToken, InputStream payloadStream,
            WDSService service) throws IOException, InvalidRequestFormatException, AuthException,
            UnavailableItemException, UnavailableTaskException {
        if (!resourceUri.endsWith("/")) {
            resourceUri += "/";
        }
        ZipInputStream zstream = new ZipInputStream(payloadStream);
        try {
            ZipEntry entry = zstream.getNextEntry();
            while (entry != null) {
                DeploymentMessage msg = null;
                if (entry.getName().endsWith(".pipa")) {
                    msg = _deployer.processPipa(participantToken, service, zstream, entry.getName());
                } else if (entry.getName().endsWith(".xform")) {
                    msg = _deployer.processXForm(participantToken, service, zstream, entry.getName());
                } else {
                    msg = _deployer.processItem(participantToken, service, zstream, entry.getName());
                }
                if (msg != null) throw new RuntimeException(msg.getDescription());
                entry = zstream.getNextEntry();
            }
        } finally {
            zstream.close();
        }
    }

}
