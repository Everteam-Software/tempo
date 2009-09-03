/**
 * Copyright (c) 2005-2009 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 */

package org.intalio.tempo.workflow.tas.nuxeo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.intalio.tempo.security.Property;
import org.intalio.tempo.workflow.tas.core.AttachmentMetadata;
import org.intalio.tempo.workflow.tas.core.StorageStrategy;
import org.intalio.tempo.workflow.tas.core.UnavailableAttachmentException;
import org.intalio.tempo.workflow.tas.sling.SlingStorageStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NuxeoStorageStrategy implements StorageStrategy {

    private static final String NUXEO_FILE = "File";
    private static final String NUXEO_FOLDER = "Folder";
    private static final String NUXEO_DUBLINCORE_TITLE = "dublincore:title";
    private static final String NUXEO_DOC_TYPE = "docType";
    private static final String NUXEO_DOC_REF = "docRef";
    private static final String STRING_EMPTY = "";
    private static final String NUXEO_TYPE = "type";
    private static final String NUXEO_ID = "id";
    private static final String NUXEO_NAME = "name";
    private static final String NUXEO_DOCUMENT = "document";
    private static final String REST_BROWSE = "/browse";
    private static final String REST_UPLOAD = "/uploadFile";
    private static final String REST_CREATE = "/createDocument";
    private static final String REST_DELETE = "/delete";
    private static final String REST_DOWNLOAD = "/downloadFile";

    static Logger log = LoggerFactory.getLogger(SlingStorageStrategy.class);

    private String nuxeoBaseUrl = "http://localhost:9080/";
    // private String nuxeoUrl = nuxeoBaseUrl + "restAPI/default";
    private String repoName = "tempo";
    private String userName = "Administrator";
    private String password = "Administrator";

    // created on init()
    private HttpClient httpclient;
    // computed on init()
    private String repoId;

    private boolean init = false;

    // NOT USED FOR NOW, but we could when we access versions
    // private String nuxeoPublicUrl = nuxeoBaseUrl +
    // "/admin/repository/default-domain/workspaces/" + repoName;

    public NuxeoStorageStrategy() throws Exception, IOException {

    }

    public void init() throws HttpException, IOException {
        initHttpClient();
        try {
            String localRepoId = getWorkspacesURL(getNuxeoRestUrl(), "workspaces", "WorkspaceRoot");
            log.debug("LOCAL REPO" + localRepoId);
            repoId = getDocumentId(browse(localRepoId), this.repoName);
            if (repoId == null)
                repoId = createFolder(getNuxeoRestUrl() + "/" + localRepoId, this.repoName);
            log.debug("REPOURL:" + repoId);
            init = true;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    /**
     * Everything we need to do to have an authenticated http client
     * 
     * @throws URIException
     */
    private void initHttpClient() throws URIException {
        httpclient = new HttpClient();
        HostConfiguration hostConfig = new HostConfiguration();
        org.apache.commons.httpclient.URI uri = new org.apache.commons.httpclient.URI(getNuxeoRestUrl(), false);
        hostConfig.setHost(uri);
        HttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        HttpConnectionManagerParams params = new HttpConnectionManagerParams();
        int maxHostConnections = 20;
        params.setMaxConnectionsPerHost(hostConfig, maxHostConnections);
        connectionManager.setParams(params);
        httpclient = new HttpClient(connectionManager);
        httpclient.setHostConfiguration(hostConfig);
        Credentials creds = new UsernamePasswordCredentials(userName, password);
        AuthScope authScope = new AuthScope(hostConfig.getHost(), hostConfig.getPort());
        httpclient.getState().setCredentials(authScope, creds);
        httpclient.getParams().setAuthenticationPreemptive(true);
    }

    /**
     * Implement the interface for deleting a file
     */
    public void deleteAttachment(Property[] props, String url) throws UnavailableAttachmentException {
        try {
            if (!init)
                init();
            String fileUrl = url.substring(0, url.indexOf(this.REST_DOWNLOAD));
            String newUri = fileUrl + REST_DELETE;
            log.debug("NUXEO DELETE:" + newUri);
            DeleteMethod method = new DeleteMethod(newUri);
            httpclient.executeMethod(method);
            // needed for proper httpclient handling
            // but we do not return the value
            // method.getResponseBodyAsString();
            method.releaseConnection();
        } catch (Exception e) {
            throw new UnavailableAttachmentException(e);
        }
    }

    /**
     * Store the attachment, implement the java interface
     */
    public String storeAttachment(Property[] properties, AttachmentMetadata metadata, InputStream payload) throws IOException {
        if (!init)
            init();
        OMElement omEle = null;
        String encodedName = URLEncoder.encode(metadata.getFilename(), "utf-8");
        try {
            omEle = getCreateFileEle(getNuxeoRestUrl(), repoId, encodedName);
            log.debug("NUXEO ATTACH " + omEle.toString());
        } catch (Exception e) {
            throw new IOException(e);
        }
        if (omEle == null) {
            throw new RuntimeException("Failed to create file :" + metadata.getFilename());
        }

        String fileId = getDocumentRef(omEle);
        String fileUrl = getNuxeoRestUrl() + "/" + fileId;
        String newUri = fileUrl + "/" + encodedName;
        uploadFile(newUri + REST_UPLOAD, payload);

        return fileUrl + REST_DOWNLOAD;
    }

    /**
     * Create a file reference in nuxeo, which does not contain the file, but
     * creates a file container so we can upload
     */
    private OMElement getCreateFileEle(String uri, String repoId, String fileName) throws Exception {
        String newUri = uri + "/" + repoId + REST_CREATE;
        GetMethod method = new GetMethod(newUri);

        NameValuePair[] pairs = new NameValuePair[2];
        pairs[0] = new NameValuePair(NUXEO_DOC_TYPE, NUXEO_FILE);
        String encodedName = URLEncoder.encode(fileName, "utf-8");
        pairs[1] = new NameValuePair(NUXEO_DUBLINCORE_TITLE, encodedName);

        method.setQueryString(pairs);
        httpclient.executeMethod(method);
        String value = method.getResponseBodyAsString();

        method.releaseConnection();
        return buildOMElement(value);
    }

    /**
     * Upload the binary content of the file to nuxeo
     */
    private void uploadFile(String uri, InputStream payload) throws IOException {
        PostMethod method = new PostMethod(uri);
        method.setRequestEntity(new InputStreamRequestEntity(payload));
        httpclient.executeMethod(method);
        // needed for proper httpclient handling
        // but we do not return the value
        // method.getResponseBodyAsString();
        method.releaseConnection();
    }

    /**
     * Creates a folder in nuxeo. We could expand that to create other things
     * like comments, ... This takes the place where to create the folder, and
     * the name of the folder to create, stored as dublin:core metadata
     */
    private String createFolder(String repoUri, String repoName) throws Exception {
        GetMethod method = new GetMethod(repoUri + REST_CREATE);
        log.debug("NUXEO CREATE FOLDER " + repoUri + " " + repoName);
        NameValuePair[] pairs = new NameValuePair[2];
        pairs[0] = new NameValuePair(NUXEO_DOC_TYPE, NUXEO_FOLDER);
        pairs[1] = new NameValuePair(NUXEO_DUBLINCORE_TITLE, repoName);
        method.setQueryString(pairs);
        httpclient.executeMethod(method);
        String value = method.getResponseBodyAsString();
        method.releaseConnection();
        OMElement omEle = buildOMElement(value);
        String docRef = omEle.getFirstChildWithName(new QName(STRING_EMPTY, NUXEO_DOC_REF)).getText();
        return docRef;
    }

    /**
     * Browse a container location in nuxeo. This is used in the code so we can
     * find duplicate folder. As such, we return the complete rest call xml
     */
    private OMElement browse(String folderId) throws Exception {
        GetMethod method = new GetMethod(getNuxeoRestUrl() + "/" + folderId + REST_BROWSE);
        httpclient.executeMethod(method);
        String value = method.getResponseBodyAsString();
        method.releaseConnection();
        OMElement ele = buildOMElement(value);
        return ele;
    }

    public String getNuxeoBaseUrl() {
        return nuxeoBaseUrl;
    }

    public void setNuxeoBaseUrl(String nuxeoBaseUrl) {
        this.nuxeoBaseUrl = nuxeoBaseUrl;
    }

    private String getNuxeoRestUrl() {
        return this.nuxeoBaseUrl + "restAPI/default";
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    private String getWorkspacesURL(String repRootUri, String repName, String repType) throws Exception {
        String newUri = repRootUri + REST_BROWSE;
        String domainId = getDomainId(newUri);
        if (domainId == null) {
            throw new RuntimeException("Failed to find repository domain with url: " + repRootUri);
        }
        newUri = repRootUri + "/" + domainId + REST_BROWSE;
        log.debug("NUXEO BROWSE " + newUri);
        String repoId = getConfRepoId(newUri, repName, repType);
        if (repoId == null) {
            throw new RuntimeException("Failed to find repository with name=" + repName + " and type=" + repType);
        }
        return repoId;
    }

    private String getDomainId(String uri) throws Exception {
        GetMethod method = new GetMethod(uri);
        httpclient.executeMethod(method);
        String value = method.getResponseBodyAsString();
        method.releaseConnection();
        OMElement ele = buildOMElement(value);
        OMElement domainEle = getFirstChildWithName(ele, NUXEO_DOCUMENT);
        if (domainEle == null) {
            throw new RuntimeException("Failed to find repository domain with url: " + uri);
        }
        return getId(domainEle);
    }

    private String getConfRepoId(String uri, String name, String type) throws Exception {
        GetMethod method = new GetMethod(uri);

        httpclient.executeMethod(method);
        String value = method.getResponseBodyAsString();

        method.releaseConnection();
        return getId(buildOMElement(value), name, type);
    }

    private String getDocumentId(OMElement elem, String name) {
        Iterator<OMElement> children = elem.getChildrenWithLocalName(NUXEO_DOCUMENT);
        while (children.hasNext()) {
            OMElement child = children.next();
            OMAttribute attribute = child.getAttribute(new QName(STRING_EMPTY, NUXEO_NAME));
            String attributeValue = attribute.getAttributeValue();
            if (name.equalsIgnoreCase(attributeValue)) {
                return child.getAttributeValue(new QName(STRING_EMPTY, NUXEO_ID));
            }
        }
        return null;
    }

    private static String getId(OMElement omEle, String name, String type) {
        Iterator<OMElement> childEles = omEle.getChildElements();

        while (childEles.hasNext()) {
            OMElement childEle = childEles.next();

            QName nameQname = new QName(STRING_EMPTY, NUXEO_NAME);
            QName typeQname = new QName(STRING_EMPTY, NUXEO_TYPE);

            String curName = childEle.getAttributeValue(nameQname);
            String curType = childEle.getAttributeValue(typeQname);

            if ((name.equalsIgnoreCase(curName) && type.equalsIgnoreCase(curType))) {
                return getId(childEle);
            }
        }

        return null;
    }

    private static String getId(OMElement idEle) {
        QName idQname = new QName(STRING_EMPTY, NUXEO_ID);
        return idEle.getAttributeValue(idQname);
    }

    private String getDocumentRef(OMElement omEle) {
        OMElement docRefEle = getFirstChildWithName(omEle, NUXEO_DOC_REF);
        if (docRefEle != null) {
            return docRefEle.getText();
        }
        return null;
    }

    private static OMElement getFirstChildWithName(OMElement el, String name) {
        for (Iterator<OMElement> it = el.getChildElements(); it.hasNext();) {
            OMElement child = it.next();
            if (child.getLocalName().equals(name))
                return child;
        }
        return null;
    }

    private static OMElement buildOMElement(String xmlData) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(xmlData.getBytes());
        XMLStreamReader parser = XMLInputFactory.newInstance().createXMLStreamReader(byteArrayInputStream);
        StAXOMBuilder builder = new StAXOMBuilder(parser);
        return builder.getDocumentElement();
    }

}
