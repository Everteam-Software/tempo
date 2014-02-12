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
 package org.intalio.tempo.workflow.tas.sling;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.jackrabbit.webdav.client.methods.MkColMethod;
import org.apache.jackrabbit.webdav.client.methods.PutMethod;
import org.intalio.tempo.security.Property;
import org.intalio.tempo.workflow.tas.core.AttachmentMetadata;
import org.intalio.tempo.workflow.tas.core.StorageStrategy;
import org.intalio.tempo.workflow.tas.core.TASUtil;
import org.intalio.tempo.workflow.tas.core.UnavailableAttachmentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

public class SlingStorageStrategy implements StorageStrategy {

    static Logger log = LoggerFactory.getLogger(SlingStorageStrategy.class);

    private String slingUrl = "http://localhost:8080/sling";
    private String baseFolder = "/tempo";
    private String userName = "admin";
    private String password = "admin";
    private HttpClient httpclient = new HttpClient();

    public SlingStorageStrategy() throws Exception, IOException {
        init();
    }

    public void deleteAttachment(Property[] arg0, String arg1) throws UnavailableAttachmentException {
        DeleteMethod delete = new DeleteMethod(arg1);
        try {
            httpclient.executeMethod(delete);
        } catch (Exception e) {
            throw new UnavailableAttachmentException(e);
        }
    }

    public void init() throws HttpException, IOException {
        HostConfiguration hostConfig = new HostConfiguration();
        // hostConfig.setHost("www.somehost.com");
        HttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        HttpConnectionManagerParams params = new HttpConnectionManagerParams();
        int maxHostConnections = 20;
        params.setMaxConnectionsPerHost(hostConfig, maxHostConnections);
        connectionManager.setParams(params);
        httpclient = new HttpClient(connectionManager);
        Credentials creds = new UsernamePasswordCredentials(userName, password);
        httpclient.getState().setCredentials(AuthScope.ANY, creds);
        httpclient.setHostConfiguration(hostConfig);

        MkColMethod col = new MkColMethod(getUploadFolder());
        int ret = httpclient.executeMethod(col);
        log.debug(MessageFormatter.format("Created folder {0} in sling: {1}",
                getUploadFolder(), ret).getMessage());
    }

    private String getUploadFolder() {
        return slingUrl + baseFolder;
    }

    public String storeAttachment(Property[] arg0, AttachmentMetadata metadata, InputStream payload) throws IOException {
        String sanitize = TASUtil.sanitize(metadata.getFilename());
        String uploadUrl = getUploadFolder() + "/" + sanitize;

        PutMethod put = new PutMethod(uploadUrl);
        put.setRequestEntity(new InputStreamRequestEntity(payload));
        put.setRequestHeader("Content-type", metadata.getMimeType());

        try {
            int result = httpclient.executeMethod(put);
            if (log.isDebugEnabled()) {
                log.debug("Response status code: " + result);
                log.debug("Response body: ");
                log.debug(put.getResponseBodyAsString());
            }

            return uploadUrl;
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        } finally {
            // Release current connection to the connection pool
            put.releaseConnection();
        }
    }

    public String getSlingUrl() {
        return slingUrl;
    }

    public void setSlingUrl(String slingUrl) {
        this.slingUrl = slingUrl;
    }

    public String getBaseFolder() {
        return baseFolder;
    }

    public void setBaseFolder(String baseFolder) {
        this.baseFolder = baseFolder;
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
}
