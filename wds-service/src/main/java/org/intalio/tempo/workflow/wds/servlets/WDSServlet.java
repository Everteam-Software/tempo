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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.intalio.tempo.web.SysPropApplicationContextLoader;
import org.intalio.tempo.workflow.wds.config.WDSConfigBean;
import org.intalio.tempo.workflow.wds.core.AuthenticationException;
import org.intalio.tempo.workflow.wds.core.Item;
import org.intalio.tempo.workflow.wds.core.ItemDaoConnectionFactory;
import org.intalio.tempo.workflow.wds.core.JdbcItemDaoConnectionFactory;
import org.intalio.tempo.workflow.wds.core.UnavailableItemException;
import org.intalio.tempo.workflow.wds.core.WDSService;
import org.intalio.tempo.workflow.wds.core.WDSServiceFactory;
import org.intalio.tempo.workflow.wds.core.tms.PipaTask;
import org.intalio.tempo.workflow.wds.core.tms.TMSConnectionFactory;
import org.intalio.tempo.workflow.wds.core.tms.TMSConnectionFactoryInterface;
import org.intalio.tempo.workflow.wds.core.xforms.XFormsProcessor;

/**
 * Provides a REST-style web interface for WDS.
 * <p />
 * The following methods are supported:
 * <ul>
 * <li><code>GET</code> -- retrieves the WDS item with the specified URI and returns it as the response.</li>
 * <li><code>PUT</code> -- stores the body of the response as a WDS item with the specified URI.</li>
 * <li><code>DELETE</code> -- deletes the WDS item with the specified URI.</li>
 * </ul>
 * <p />
 * The following status codes are returned:
 * <ul>
 * <li><code>200</code> -- if the request was processed successfully.</li>
 * <li><code>401</code> -- if an authentication/authorization error happened.</li>
 * <li><code>404</code> -- if the requested item was not found on WDS.</li>
 * <li><code>409</code> -- if the specified URI is already taken (appliable for <code>PUT</code> requests).</li>
 * <li><code>500</code> -- if an unexpected error happened.</li>
 * </ul>
 * 
 * @version $Revision: 1176 $
 */
public class WDSServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(WDSServlet.class);
    private static final long serialVersionUID = -5714415376114167497L;
    private static final String DEFAULT_CONFIG_FILE = "file:${org.intalio.tempo.configDirectory}/tempo-tms.xml";
    
    private WDSServiceFactory _serviceFactory;

    @Override
    public void init() throws ServletException {
        SysPropApplicationContextLoader loader;
        String configFile = getServletConfig().getInitParameter("contextConfigLocation");
        if (configFile == null) {
            configFile = DEFAULT_CONFIG_FILE;
        }
        if (logger.isDebugEnabled()) logger.debug("Loading WDS configuration...");
        try { loader = new SysPropApplicationContextLoader(configFile); } catch (IOException except) {
            throw new ServletException(except);
        }
        WDSConfigBean config = loader.getBean("config");

        if (logger.isDebugEnabled()) logger.debug("Creating DAO factory...");
        ItemDaoConnectionFactory daoFactory = new JdbcItemDaoConnectionFactory(config.getWdsDataSource());

        if (logger.isDebugEnabled()) logger.debug("Creating TMS factory...");
        TMSConnectionFactoryInterface tmsFactory = new TMSConnectionFactory(config.getTmsDataSource());

        if (logger.isDebugEnabled()) logger.debug("Creating Service Factory...");
        _serviceFactory = new WDSServiceFactory(daoFactory, tmsFactory);
        _serviceFactory.setWdsEndpoint(config.getWdsEndpoint());
        
        if (logger.isDebugEnabled())  logger.debug("Servlet initialized.");
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
        while (relativeUri.startsWith("/")) {relativeUri = relativeUri.substring(1); }
        if (logger.isDebugEnabled())  logger.debug("Resource URI: '" + relativeUri + "'");
        return relativeUri;
    }

    /**
     * Fetches the participant token specified in an HTTP request.
     */
    private String getParticipantToken(HttpServletRequest request) {
        String participantToken = StringUtils.EMPTY; 
        // FIXME: request.getHeader("Participant-Token");
        if (logger.isDebugEnabled())  logger.debug("Participant token: '" + participantToken + "'");
        return participantToken;
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException
    {
        if (logger.isDebugEnabled()) logger.debug("doDelete request="+request);

        String resourceUri = getResourceUri(request);
        String participantToken = getParticipantToken(request);
        WDSService service = null;
        try {
            if (logger.isDebugEnabled()) logger.debug("Deleting item: '" + resourceUri + "'");
            service = _serviceFactory.getWDSService();
            if (Boolean.valueOf(request.getHeader("Delete-PIPA-Tasks"))) {
                String formUrl = request.getRequestURL().toString();
                String wdsEndPoint = _serviceFactory.getWdsEndpoint();
                if (formUrl.startsWith(wdsEndPoint)) {
                    formUrl = formUrl.substring(wdsEndPoint.length());
                }
                PipaTask pipa = newPipa(request, formUrl);
                service.deletePIPA(participantToken, pipa);
            } else {
                service.deleteItem(resourceUri, participantToken);
                service.commit();
            }
            if (logger.isDebugEnabled()) logger.debug("Item '" + resourceUri + "' deleted OK.");
        } catch (AuthenticationException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            warn("Authentication error", e);
        } catch (UnavailableItemException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            warn("Item not found: '" + resourceUri + "'", e);
        } finally {
            if (service != null) service.close();
        }
    }

    private PipaTask newPipa(HttpServletRequest request, String formUrl) {
        PipaTask pipaTask = new PipaTask();
        pipaTask.setFormURL(formUrl);
        pipaTask.setDescription(request.getHeader("Delete-PIPA-Description"));
        pipaTask.setProcessEndpoint(request.getHeader("Delete-PIPA-Process-endpoint"));
        pipaTask.setFormNamespace(request.getHeader("Delete-PIPA-Form-namespace"));
        pipaTask.setInitSoapAction(request.getHeader("Delete-PIPA-InitSOAP-Action"));
        return pipaTask;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (logger.isDebugEnabled()) logger.debug("doGet request="+request);

        String resourceUri = getResourceUri(request);
        if ("".equals(resourceUri)) {
            response.getWriter().println(getServletContext().getServletContextName());
        } else {
            String participantToken = getParticipantToken(request);
            InputStream dataStream = null;
            WDSService service = null;
            try {
                service = _serviceFactory.getWDSService();

                if (logger.isDebugEnabled()) logger.debug("Retrieving the item.");
                Item item = service.retrieveItem(resourceUri, participantToken);
                dataStream = new ByteArrayInputStream(item.getPayload());
                OutputStream outputStream = response.getOutputStream();
                response.setContentLength(item.getContentLength());
                response.setContentType(item.getContentType());

                if (logger.isDebugEnabled()) logger.debug("Sending the data..");
                IOUtils.copy(dataStream, outputStream);
                outputStream.flush();
                service.commit();
                if (logger.isDebugEnabled()) logger.debug("Item retrieved & sent OK.");
            } catch (AuthenticationException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                logger.warn("Authentication error", e);
            } catch (UnavailableItemException e) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                logger.warn("Item not found: '" + resourceUri + "'", e);
            } finally {
                if (service != null) service.close();
                if (dataStream != null) dataStream.close();
            }
        }
    }

    /**
     * Splits comma-delimited values into array
     */
    private String[] split(String source) {
        if (source == null) return new String[0];
        List<String> list = new ArrayList<String>();
        StringTokenizer tok = new StringTokenizer(source, ",");
        while (tok.hasMoreTokens()) { list.add(tok.nextToken());}
        return list.toArray(new String[list.size()]);
    }

    /**
     * Fetches PIPA task properties from HTTP request headers and builds a PipaTask
     */
    private PipaTask parsePipaTaskFromHeaders(HttpServletRequest request) 
        throws InvalidRequestFormatException
    {
        PipaTask task = new PipaTask();

        task.setId(request.getHeader("Task-ID"));
        task.setDescription(request.getHeader("Task-Description"));
        task.setFormURL(request.getHeader("Form-URL"));
        task.setFormNamespace(request.getHeader("Form-Namespace"));
        task.setProcessEndpoint(request.getHeader("Process-Endpoint"));
        task.setInitSoapAction(request.getHeader("Process-InitSOAPAction"));

        String userOwnerHeader = request.getHeader("Task-UserOwners");
        String[] userOwners = split(userOwnerHeader);
        task.setUserOwners(userOwners);

        String roleOwnerHeader = request.getHeader("Task-RoleOwners");
        String[] roleOwners = split(roleOwnerHeader);
        task.setRoleOwners(roleOwners);

        if (!task.isValid()) {
            throw new InvalidRequestFormatException("Invalid PIPA task:\n" + task);
        }

        return task;
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException
    {
        if (logger.isDebugEnabled()) {
            logger.debug("doPut request="+request+" contentType="+request.getContentType()+
                      " contentLength="+request.getContentLength());
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
            	// added by atoulme 03-28
            	// we send a zip file, containing all the elements
            	// our objectives are :
            	// 1. Delete the previous version if present (version with same name)
            	// 2. Install all files in the zip as items
            	// as for now 1# is TODO.
            	// for more info on what is sent here, please refer to the 
            	// WorkflowDeploymentJob in XForms builder
            	if ("application/zip".equals(contentType)) {
            		if (!resourceUri.endsWith("/")) {
            			resourceUri += "/";
            		}
            		ZipInputStream zstream = new ZipInputStream(payloadStream);
            		try {
            			ZipEntry entry = zstream.getNextEntry(); 
                        while (entry != null) {
                            if (entry.getName().endsWith(".pipa")) {
                                if (logger.isDebugEnabled()) {
                                    logger.debug("Pipa descriptor "+ resourceUri + entry.getName());
                                }
                                Properties prop = new Properties();
                                prop.load(zstream);
                                PipaTask task = newPipa(prop);
                                if (!task.isValid()) {
                                    throw new InvalidRequestFormatException("Invalid PIPA task:\n" + task);
                                }
                                service.storePipaTask(task, participantToken);
                                if (logger.isDebugEnabled()) logger.debug("Pipa descriptor '" + resourceUri + "' stored OK.");
                            } else if (entry.getName().endsWith(".xform")){
                                if (logger.isDebugEnabled()) logger.debug("Processing xform '" + resourceUri + "'");
                                try {
                                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                                    copy(zstream, output);
                                    ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
                                    Item item = XFormsProcessor.processXForm(entry.getName(), input);
                                    service.storeItem(item, participantToken);
                                    if (logger.isDebugEnabled()) logger.debug("Storing the item: '" + resourceUri + entry.getName());
                                } catch (Exception e) {
                                   logger.error(e.getMessage(), e);
                                   throw new RuntimeException(e);
                                }
                            } else {
                                ByteArrayOutputStream output = new ByteArrayOutputStream();
                                copy(zstream, output);
                                Item item = new Item(entry.getName(), "application/xml", output.toByteArray());
                                service.storeItem(item, participantToken);
                                if (logger.isDebugEnabled())  logger.debug("Storing the item: '" + resourceUri + entry.getName());
                            }
                            entry = zstream.getNextEntry(); 
            			}
            		} finally {
            			zstream.close();
            		}
            	} else {
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
            		if (logger.isDebugEnabled())  logger.debug("Storing the item: '" + resourceUri + "'.");
                    try {
                        service.deleteItem(item.getURI(), participantToken);
                    } catch (UnavailableItemException except) {
                        // ignore
                    }
            		service.storeItem(item, participantToken);
            		if (logger.isDebugEnabled()) logger.debug("Item '" + resourceUri + "' stored OK.");
            	}
            	
            } else if ("True".equals(request.getHeader("Create-PIPA-Task"))) {
                PipaTask pipaTask = parsePipaTaskFromHeaders(request);
                if (logger.isDebugEnabled()) logger.debug("Storing pipa task:\n" + pipaTask);
                service.storePipaTask(pipaTask, participantToken);
                if (logger.isDebugEnabled()) logger.debug("Pipa task stored OK.");
            }
            service.commit();
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

    private PipaTask newPipa(Properties prop) {
        PipaTask task = new PipaTask();
        task.setId(UUID.randomUUID().toString());
        task.setDescription(prop.getProperty("task-description"));
        task.setFormURL(prop.getProperty("formURI"));
        task.setFormNamespace(prop.getProperty("formNamespace"));
        task.setProcessEndpoint(prop.getProperty("processEndpoint"));
        task.setInitSoapAction(prop.getProperty("userProcessInitSOAPAction"));

        String userOwnerHeader = prop.getProperty("task-user-owners");
        String[] userOwners = split(userOwnerHeader);
        task.setUserOwners(userOwners);

        String roleOwnerHeader = prop.getProperty("task-role-owners");
        String[] roleOwners = split(roleOwnerHeader);
        task.setRoleOwners(roleOwners);
        return task;
    }

    /**
     * Copies the contents of the <code>InputStream</code> into the 
     * <code>OutputStream</code>.
     * <p/>
     * Note: when the copy is successful the <code>OutputStream</code> is 
     * closed.
     */
    private void copy(InputStream input, OutputStream output) { 
        try {
            byte[] bytes = new byte[4096];
            int bytesRead = 0;
            while ((bytesRead = input.read(bytes)) >= 0) {
                output.write(bytes, 0, bytesRead);
            }
            output.flush();
            output.close();
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void warn(String message, Throwable t) {
        if (logger.isDebugEnabled())  logger.warn(message, t);
        else  logger.warn(message + ": " + t.getMessage());
    }
}
