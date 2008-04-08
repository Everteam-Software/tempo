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
package org.intalio.tempo.workflow.tas.axis2;

import java.util.HashMap;
import java.util.Map;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.util.Base64;
import org.apache.axis2.AxisFault;
import org.intalio.tempo.workflow.tas.core.AttachmentMetadata;
import org.intalio.tempo.workflow.tas.core.AuthCredentials;
import org.intalio.tempo.workflow.tas.core.TaskAttachmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class acts as a bridge between a {@link org.intalio.tempo.workflow.tas.core.TaskAttachmentService} implementation
 * and Axis2 service layer.
 * <p />
 * It receives XML messages from the Axis2 engine in AXIOM form, converts them to business objects, invokes the
 * underlying {@link org.intalio.tempo.workflow.tas.core.TaskAttachmentService} instance and converts its response back
 * to AXIOM form.
 */
public class TASAxis2Bridge {
    private static final Logger _logger = LoggerFactory.getLogger(TASAxis2Bridge.class);

    public static final String TAS_XMLNS = "http://www.intalio.com/BPMS/Workflow/TaskAttachmentService/";

    private static final OMNamespace _tasOmNamespace 
        = OMAbstractFactory.getOMFactory().createOMNamespace(TAS_XMLNS, "tas");

    private static final OMFactory _omFactory = OMAbstractFactory.getOMFactory();

    private static AxiomXPathHelper _xpathHelper;

    static {
        Map<String, String> namespaceMap = new HashMap<String, String>();
        namespaceMap.put("tas", TASAxis2Bridge.TAS_XMLNS);
        _xpathHelper = new AxiomXPathHelper(namespaceMap);
    }

    private TaskAttachmentService _serviceImpl;

    /**
     * Construct a TASAxis2Bridge
     */
    public TASAxis2Bridge(TaskAttachmentService serviceImpl) {
        this.setServiceImplementation(serviceImpl);
    }

    /**
     * Parses a <code>tas:attachmentMetadata</code> element into an {@link AttachmentMetadata} object.
     */
    private static AttachmentMetadata parseAttachmentMetadata(OMElement metadataElement)
            throws InvalidMessageFormatException {
        AttachmentMetadata metadata = new AttachmentMetadata();
        String mimeType = _xpathHelper.getRequiredString(metadataElement, "tas:mimeType").trim();
        metadata.setMimeType(mimeType);
        String filename = _xpathHelper.getRequiredString(metadataElement, "tas:filename").trim();
        metadata.setFilename(filename);
        return metadata;
    }

    /**
     * Parses a <code>tas:authCredentials</code> element into an {@link AuthCredentials} object.
     */
    private static AuthCredentials parseAuthCredentials(OMElement credentialElement)
            throws InvalidMessageFormatException {
        String participantToken = _xpathHelper.getRequiredString(credentialElement, "tas:participantToken");

        AuthCredentials credentials = new AuthCredentials(participantToken);
        OMElement usersElement = _xpathHelper.getElement(credentialElement, "tas:authorizedUsers");
        if (usersElement != null) {
            OMElement[] userElements = _xpathHelper.getElements(usersElement, "tas:user");
            for (OMElement userElement : userElements) {
                String user = userElement.getText().trim();
                credentials.getAuthorizedUsers().add(user);
            }
        }

        OMElement rolesElement = _xpathHelper.getElement(credentialElement, "tas:authorizedRoles");
        if (rolesElement != null) {
            OMElement[] roleElements = _xpathHelper.getElements(rolesElement, "tas:role");
            for (OMElement roleElement : roleElements) {
                String role = roleElement.getText().trim();
                credentials.getAuthorizedRoles().add(role);
            }
        }

        return credentials;
    }

    /**
     * Converts an arbitrary exception to an {@link AxisFault}, so that a descriptive <code>SOAPFault</code> can be
     * returned to the client.
     */
    private static AxisFault convertExceptionToSOAPFault(Exception e) {
        return new AxisFault(e.getMessage(), e.getClass().toString(), e);
    }

    /**
     * Builds a <code>tas:okResponse</code>.
     */
    private static OMElement createOkResponse() {
        return _omFactory.createOMElement("okResponse", _tasOmNamespace);
    }

    /**
     * Sets the backing {@link TaskAttachmentService} implementation instance.
     */
    public void setServiceImplementation(TaskAttachmentService serviceImpl) {
        if (serviceImpl == null) {
            throw new NullPointerException("serviceImpl");
        }
        _serviceImpl = serviceImpl;
    }

    /**
     * Implements the <code>add</code> operation, which corresponds to
     * {@link TaskAttachmentService#add(String, AuthCredentials, AttachmentMetadata, byte[])} and
     * {@link TaskAttachmentService#add(String, AuthCredentials, AttachmentMetadata, String)}.
     * <p />
     * See WSDL for the formal operation description.
     */
    public OMElement add(OMElement addRequestElement) throws AxisFault {
        try {
            _logger.debug("Request: add");

            OMElement metadataElement = _xpathHelper.getRequiredElement(addRequestElement, "tas:attachmentMetadata");
            AttachmentMetadata metadata = TASAxis2Bridge.parseAttachmentMetadata(metadataElement);

            OMElement credentialElement = _xpathHelper.getRequiredElement(addRequestElement, "tas:authCredentials");
            AuthCredentials credentials = TASAxis2Bridge.parseAuthCredentials(credentialElement);

            OMElement localFileUrlElement = _xpathHelper.getElement(addRequestElement, "tas:localFileURL");
            OMElement payloadElement = _xpathHelper.getElement(addRequestElement, "tas:payload");
            OMElement plaintextElement = _xpathHelper.getElement(addRequestElement, "tas:plaintext");

            if ((localFileUrlElement == null) && (payloadElement == null) && (plaintextElement == null)) {
                throw new InvalidMessageFormatException("None of tas:payload, tas:localFileURL, tas:plaintext "
                        + "elements is present in the message.");
            }

            String storedUrl = null;

            if (localFileUrlElement != null) {
                String localFileUrl = localFileUrlElement.getText().trim();
                _logger.debug("Using a local file: '" + localFileUrl + "'");
                storedUrl = _serviceImpl.add(credentials, metadata, localFileUrl);
            } else if (payloadElement != null) {
                byte[] payload = Base64.decode(payloadElement.getText().trim());
                _logger.debug("Using embedded base64 payload. Size: " + payload.length);
                storedUrl = _serviceImpl.add(credentials, metadata, payload);
            } else if (plaintextElement != null) {
                byte[] payload = plaintextElement.getText().trim().getBytes();
                _logger.debug("Using embedded plaintext.");
                storedUrl = _serviceImpl.add(credentials, metadata, payload);
            }

            OMElement responseElement = _omFactory.createOMElement("addResponse", _tasOmNamespace);
            OMElement urlElement = _omFactory.createOMElement("url", _tasOmNamespace);
            urlElement.setText(storedUrl);
            responseElement.addChild(urlElement);
            return responseElement;
        } catch (Exception e) {
            _logger.error(e.getMessage(),e);
            throw TASAxis2Bridge.convertExceptionToSOAPFault(e);
        }
    }

    /**
     * Implements the <code>add</code> operation, which corresponds to
     * {@link TaskAttachmentService#delete(String, AuthCredentials, String)}.
     */
    public OMElement delete(OMElement deleteRequestElement) throws AxisFault {
        try {
            _logger.debug("Request: delete");
            OMElement credentialElement = _xpathHelper
                    .getRequiredElement(deleteRequestElement, "tas:authCredentials");
            AuthCredentials credentials = TASAxis2Bridge.parseAuthCredentials(credentialElement);
            String attachmentUrl = _xpathHelper.getRequiredString(deleteRequestElement, "tas:attachmentURL");
            _serviceImpl.delete(credentials, attachmentUrl);
            OMElement responseElement = TASAxis2Bridge.createOkResponse();
            return responseElement;
        } catch (Exception e) {
            throw TASAxis2Bridge.convertExceptionToSOAPFault(e);
        }
    }

}
