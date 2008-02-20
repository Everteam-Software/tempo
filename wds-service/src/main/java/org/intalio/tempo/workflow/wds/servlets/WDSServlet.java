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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.intalio.tempo.web.SysPropApplicationContextLoader;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.wds.core.AuthenticationException;
import org.intalio.tempo.workflow.wds.core.Item;
import org.intalio.tempo.workflow.wds.core.UnavailableItemException;
import org.intalio.tempo.workflow.wds.core.WDSService;
import org.intalio.tempo.workflow.wds.core.WDSServiceFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(WDSServlet.class);
    private static final long serialVersionUID = -5714415376114167497L;
    private static final String DEFAULT_CONFIG_FILE = "file:${org.intalio.tempo.configDirectory}/tempo-tms.xml";

    private WDSServiceFactory _serviceFactory;

    @Override
    /**
     * Load the <code>WDSServiceFactory</code> needed to process the requets
     * 
     * @see WDSServiceFactory
     */
    public void init() throws ServletException {
        SysPropApplicationContextLoader loader;
        String configFile = getServletConfig().getInitParameter("contextConfigLocation");
        if (configFile == null) {
            configFile = DEFAULT_CONFIG_FILE;
        }
        if (logger.isDebugEnabled())
            logger.debug("Loading WDS configuration...");
        try {
            loader = new SysPropApplicationContextLoader(configFile);
        } catch (IOException except) {
            throw new ServletException(except);
        }
        if (logger.isDebugEnabled())
            logger.debug("Creating Service Factory...");
        _serviceFactory = loader.getBean("wds.servicefactory");
        if (logger.isDebugEnabled())
            logger.debug("Servlet initialized.");
    }

    /**
     * Fetches a WDS item URI requested in an HTTP request.
     * <p />
     * For example, given the request "HTTP 1.1 GET /wds/123", this method will
     * return "123"
     */
    private static String getResourceUri(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String relativeUri = requestUri.substring(request.getContextPath().length());
        // handle frequent typo when manually deploy form (using wds-cli)
        while (relativeUri.startsWith("/")) {
            relativeUri = relativeUri.substring(1);
        }
        if (logger.isDebugEnabled())
            logger.debug("Resource URI: '" + relativeUri + "'");
        return relativeUri;
    }

    /**
     * Fetches the participant token specified in an HTTP request.
     */
    private String getParticipantToken(HttpServletRequest request) {
        String participantToken = StringUtils.EMPTY;
        // FIXME: request.getHeader("Participant-Token");
        if (logger.isDebugEnabled())
            logger.debug("Participant token: '" + participantToken + "'");
        return participantToken;
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        String resourceUri = getResourceUri(request);
        String participantToken = getParticipantToken(request);
        WDSService service = null;
        try {
            if (logger.isDebugEnabled())
                logger.debug("Deleting item: '" + resourceUri + "'");
            service = _serviceFactory.getWDSService();
            if (Boolean.valueOf(request.getHeader("Delete-PIPA-Tasks"))) {
                String formUrl = request.getRequestURL().toString();
                String wdsEndPoint = _serviceFactory.getWdsEndpoint();
                if (formUrl.startsWith(wdsEndPoint)) {
                    formUrl = formUrl.substring(wdsEndPoint.length());
                }
                service.deletePIPA(participantToken, formUrl);
            } else {
                service.deleteItem(resourceUri, participantToken);
            }
            if (logger.isDebugEnabled())
                logger.debug("Item '" + resourceUri + "' deleted OK.");
        } catch (AuthenticationException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            logger.warn("Authentication error", e);
        } catch (UnavailableItemException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            logger.warn("Item not found: '" + resourceUri + "'", e);
        } finally {
            if (service != null)
                service.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (logger.isDebugEnabled())
            logger.debug("doGet request=" + request.getRequestURI());

        String resourceUri = getResourceUri(request);
        if ("".equals(resourceUri)) {
            response.getWriter().println(getServletContext().getServletContextName());
        } else {
            String participantToken = getParticipantToken(request);
            WDSService service = null;
            try {
                service = _serviceFactory.getWDSService();
                if (logger.isDebugEnabled())
                    logger.debug("Retrieving the item.");
                Item item = service.retrieveItem(resourceUri, participantToken);
                OutputStream outputStream = response.getOutputStream();
                response.setContentType(item.getContentType());

                if (logger.isDebugEnabled())
                    logger.debug("Sending the data..");
                int length = IOUtils.copy(new ByteArrayInputStream(item.getPayload()), outputStream);
                response.setContentLength(length);

                if (logger.isDebugEnabled())
                    logger.debug("Item retrieved & sent OK.");
            } catch (AuthenticationException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                logger.warn("Authentication error", e);
            } catch (UnavailableItemException e) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                logger.warn("Item not found: '" + resourceUri + "'", e);
            } finally {
                if (service != null)
                    service.close();
            }
        }
    }


   

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("doPut request=" + request + " contentType=" + request.getContentType() + " contentLength="
                    + request.getContentLength());
        }
        String resourceUri = getResourceUri(request);
        String contentType = request.getContentType();
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        String participantToken = getParticipantToken(request);
        InputStream payloadStream = request.getInputStream();
        WDSService service = null;
        try {
            service = _serviceFactory.getWDSService();

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
            logger.warn("Invalid request message format", e);
        } catch (AuthenticationException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(e.getMessage());
            logger.warn("Authentication error", e);
        } catch (UnavailableItemException e) {
            response.sendError(HttpServletResponse.SC_CONFLICT);
            response.getWriter().write(e.getMessage());
            logger.warn("URI already taken", e);
        } finally {
            if (service != null) {
                service.close();
            }
            if (payloadStream != null) {
                payloadStream.close();
            }
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
            throws InvalidRequestFormatException, IOException, AuthenticationException, UnavailableItemException {
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
        if (logger.isDebugEnabled())
            logger.debug("Storing the item: '" + resourceUri);
        try {
            service.deleteItem(item.getURI(), participantToken);
        } catch (UnavailableItemException except) {
            // ignore
        }
        service.storeItem(item, participantToken);
        if (logger.isDebugEnabled())
            logger.debug("Item '" + resourceUri + "' stored OK.");
    }

    /**
     * Generates a new pipa using the parameters found in the http headers.
     */
    private void processCreatePipa(HttpServletRequest request, String participantToken, WDSService service)
            throws InvalidRequestFormatException, AuthenticationException {
        PIPATask pipaTask = new PIPALoader().parsePipa(request);
        if (logger.isDebugEnabled())
            logger.debug("Storing pipa task:\n" + pipaTask);
        service.storePipaTask(pipaTask, participantToken);
        if (logger.isDebugEnabled())
            logger.debug("Pipa task stored OK.");
    }

    private void processZipfile(String resourceUri, String participantToken, InputStream payloadStream,
            WDSService service) throws IOException, InvalidRequestFormatException, AuthenticationException,
            UnavailableItemException {
        if (!resourceUri.endsWith("/")) {
            resourceUri += "/";
        }
        ZipInputStream zstream = new ZipInputStream(payloadStream);
        try {
            ZipEntry entry = zstream.getNextEntry();
            while (entry != null) {
                if (entry.getName().endsWith(".pipa")) {
                    processZipEntryPipa(resourceUri, participantToken, service, zstream, entry);
                } else if (entry.getName().endsWith(".xform")) {
                    processZipEntryXForm(resourceUri, participantToken, service, zstream, entry);
                } else {
                    processZipEntryItem(resourceUri, participantToken, service, zstream, entry);
                }
                entry = zstream.getNextEntry();
            }
        } finally {
            zstream.close();
        }
    }

    private void processZipEntryItem(String resourceUri, String participantToken, WDSService service,
            ZipInputStream zstream, ZipEntry entry) throws IOException, AuthenticationException,
            UnavailableItemException {
        Item item = new Item(entry.getName(), "application/xml", copyToByteArray(zstream));
        service.storeItem(item, participantToken);
        if (logger.isDebugEnabled())
            logger.debug("Storing the item: '" + resourceUri + entry.getName());
    }

    private void processZipEntryXForm(String resourceUri, String participantToken, WDSService service,
            ZipInputStream zstream, ZipEntry entry) {
        if (logger.isDebugEnabled())
            logger.debug("Processing xform '" + resourceUri + "'");
        try {
            Item item = XFormsProcessor.processXForm(entry.getName(), new ByteArrayInputStream(
                    copyToByteArray(zstream)));
            service.storeItem(item, participantToken);
            if (logger.isDebugEnabled())
                logger.debug("Storing the item: '" + resourceUri + entry.getName());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private void processZipEntryPipa(String resourceUri, String participantToken, WDSService service,
            ZipInputStream zstream, ZipEntry entry) throws IOException, InvalidRequestFormatException,
            AuthenticationException {
        if (logger.isDebugEnabled()) {
            logger.debug("Pipa descriptor " + resourceUri + entry.getName());
        }
        Properties prop = new Properties();
        prop.load(zstream);
        PIPATask task = new PIPALoader().parsePipa(prop);
        if (!task.isValid()) {
            throw new InvalidRequestFormatException("Invalid PIPA task:\n" + task);
        }
        service.storePipaTask(task, participantToken);
        if (logger.isDebugEnabled())
            logger.debug("Pipa descriptor '" + resourceUri + "' stored OK.");
    }

    /**
     * Copy the content of the stream to a byte array
     */
    private byte[] copyToByteArray(ZipInputStream input) throws IOException {
        try {
            byte[] bytes = new byte[32768];
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            int bytesRead = 0;
            while ((bytesRead = input.read(bytes)) >= 0)
                output.write(bytes, 0, bytesRead);
            return output.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
