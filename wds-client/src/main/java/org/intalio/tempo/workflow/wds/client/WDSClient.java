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
package org.intalio.tempo.workflow.wds.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.io.IOUtils;

/**
 * A client for WDS REST interface.
 * <p />
 * Based on Commons HttpClient.
 * 
 * @author Iwan Memruk
 * @version $Revision: 535 $
 */
public class WDSClient {

    private static final String OXF_PREFIX = "oxf://";
    /**
     * Base URL of WDS.
     */
    private String _wdsUrl;

    /**
     * Security participant token.
     */
    private String _participantToken;

    /**
     * HTTP client instance.
     */
    private HttpClient _httpClient;

    /**
     * Instance constructor.
     * 
     * @param wdsUrl
     *            Base URL of WDS.
     * @param participantToken
     *            Security participant token.
     */
    public WDSClient(String wdsUrl, String participantToken) {
        this.setWdsUrl(wdsUrl);
        this.setParticipantToken(participantToken);
        this._httpClient = new HttpClient();
    }

    /**
     * Returns the security participant token.
     * 
     * @return Security participant token.
     */
    public String getParticipantToken() {
        return _participantToken;
    }

    /**
     * Sets the security participant token.
     * 
     * @param participantToken
     *            The security participant token to set.
     */
    public void setParticipantToken(String participantToken) {
        if (participantToken == null) {
            throw new NullPointerException("participantToken");
        }
        _participantToken = participantToken;
    }

    /**
     * Returns the base URL of WDS.
     * 
     * @return Base URL of WDS.
     */
    public String getWdsUrl() {
        return _wdsUrl;
    }

    /**
     * Sets the base URL of WDS
     * 
     * @param wdsUrl
     *            Base URL of WDS to set.
     */
    public void setWdsUrl(String wdsUrl) {
        if (wdsUrl == null) {
            throw new NullPointerException("wdsUrl");
        }
        _wdsUrl = wdsUrl;
    }

    /**
     * Processes the HTTP status code from a WDS response.
     * <p />
     * On 200 OK does nothing. On other (non-OK) responses throws a corresponding <code>WDSException</code>.
     * 
     * @param statusCode
     *            The status code to process.
     * @throws WDSException
     *             On a non-OK status code.
     */
    private void processStatusCode(int statusCode)
            throws WDSException {
        switch (statusCode) {
        case 200: // OK
            break;
        case 401: // Unauthorized
            throw new AuthException("Authentication/authorization error.");
        case 404: // Not Found
            throw new UnavailableItemException("Item not available.");
        case 409: // Conflict
            throw new ConflictException("URI already taken.");
        default:
            throw new RuntimeException("Unknown HTTP error code: " + statusCode);
        }
    }

    /**
     * Retrieves a WDS item.
     * 
     * @param uri
     *            URI of the item to retrieve.
     * @return Data stream containing the item payload. Must be closed by the user.
     * @throws IOException
     *             On a low-level I/O problem.
     * @throws WDSException
     *             On a logical WDS problem.
     */
    public InputStream retrieveItem(String uri) throws IOException, WDSException {
        GetMethod getMethod = new GetMethod(_wdsUrl + uri);
        getMethod.addRequestHeader(new Header("Particpant-Token", _participantToken));

        _httpClient.executeMethod(getMethod);
        int status = getMethod.getStatusCode();
        this.processStatusCode(status);

        return getMethod.getResponseBodyAsStream();
    }

    /**
     * Retrieves a WDS item and saves it to a file.
     * 
     * @param uri
     *            URI of the item to retrieve.
     * @param file
     *            File to save the item payload to. Must be writable.
     * @throws IOException
     *             On a low-level I/O problem.
     * @throws WDSException
     *             On a logical WDS problem.
     */
    public void retrieveItemToFile(String uri, File file)
            throws IOException,
                WDSException {
        OutputStream fileOutputStream = new FileOutputStream(file);

        InputStream dataStream = this.retrieveItem(uri);
        IOUtils.copy(dataStream, fileOutputStream);
    }

    /**
     * Stores a new WDS item.
     * 
     * @param uri
     *            URI of the item to store.
     * @param dataStream
     *            Data stream containing the item payload.
     * @param contentLength
     *            Length of the data in <code>dataStream</code>
     * @throws IOException
     *             On a low-level I/O problem.
     * @throws WDSException
     *             On a logical WDS problem.
     */
    public void storeItem(String uri, InputStream dataStream, long contentLength)
            throws IOException,
                WDSException {
        this.storeItem(uri, dataStream, contentLength, "application/octet-stream");
    }

    /**
     * Stores a new WDS item.
     * 
     * @param uri
     *            URI of the item to store.
     * @param dataStream
     *            Data stream containing the item payload.
     * @param contentLength
     *            Length of the data in <code>dataStream</code>
     * @param contentType
     *            MIME content type of item data.
     * @throws IOException
     *             On a low-level I/O problem.
     * @throws WDSException
     *             On a logical WDS problem.
     */
    public void storeItem(String uri, InputStream dataStream, long contentLength, String contentType)
            throws IOException,
                WDSException {
        PutMethod putMethod = new PutMethod(_wdsUrl + uri);
        putMethod.addRequestHeader(new Header("Content-Type", contentType));
        putMethod.setRequestEntity(new InputStreamRequestEntity(dataStream, contentLength));
        _httpClient.executeMethod(putMethod);

        int status = putMethod.getStatusCode();
        this.processStatusCode(status);
    }

    /**
     * Stores a new WDS item using a file as the item payload.
     * 
     * @param uri
     *            URI of the item to store.
     * @param file
     *            The file to use as the item payload. Must be readable.
     * @throws IOException
     *             On a low-level I/O problem.
     * @throws WDSException
     *             On a logical WDS problem.
     */
    public void storeFile(String uri, File file) throws IOException, WDSException {
        this.storeFile(uri, file, "application/octet-stream");
    }

    /**
     * Stores a new WDS item using a file as the item payload.
     * 
     * @param uri
     *            URI of the item to store.
     * @param file
     *            The file to use as the item payload. Must be readable.
     * @param contentType
     *            MIME content type of item data.
     * @throws IOException
     *             On a low-level I/O problem.
     * @throws WDSException
     *             On a logical WDS problem.
     */
    public void storeFile(String uri, File file, String contentType) throws IOException, WDSException {
        InputStream stream = new FileInputStream(file);
        long length = file.length();

        this.storeItem(uri, stream, length, contentType);
    }

    private static String joinAuthIdentifiers(String[] identifiers) {
        StringBuilder builder = new StringBuilder();
        for (String identifier : identifiers) {
            builder.append(identifier);
            builder.append(' ');
        }
        return builder.toString();
    }

    public void storeXForm(String uri, File file) throws IOException, WDSException {
        InputStream dataStream = new FileInputStream(file);
        long contentLength = file.length();

        PutMethod storeFormPutMethod = new PutMethod(_wdsUrl + uri);
        storeFormPutMethod.setRequestEntity(new InputStreamRequestEntity(dataStream, contentLength));
        storeFormPutMethod.addRequestHeader(new Header("Is-XForm", "True"));

        _httpClient.executeMethod(storeFormPutMethod);

        int storeFormStatus = storeFormPutMethod.getStatusCode();
        this.processStatusCode(storeFormStatus);
    }

    public void storePipaTask(PipaTask task) throws IOException, WDSException {
        PutMethod createPipaTaskPutMethod = new PutMethod(_wdsUrl + task.getFormURL());
        
        createPipaTaskPutMethod.addRequestHeader(new Header("Create-PIPA-Task", "True"));
        createPipaTaskPutMethod.addRequestHeader(new Header("Task-ID", task.getId()));
        createPipaTaskPutMethod.addRequestHeader(new Header("Task-Description", task.getDescription()));
        createPipaTaskPutMethod.addRequestHeader(new Header("Form-URL", task.getFormURL()));
        createPipaTaskPutMethod.addRequestHeader(new Header("Process-Endpoint", task.getProcessEndpoint()));
        createPipaTaskPutMethod.addRequestHeader(new Header("Form-Namespace", task.getFormNamespace()));
        createPipaTaskPutMethod.addRequestHeader(new Header("Process-InitSOAPAction", task.getInitSoapAction()));

        String taskUserOwners = WDSClient.joinAuthIdentifiers(task.getUserOwners());
        if (taskUserOwners.length() > 0) {
            createPipaTaskPutMethod.addRequestHeader(new Header("Task-UserOwners", taskUserOwners));
        }

        String taskRoleOwners = WDSClient.joinAuthIdentifiers(task.getRoleOwners());
        if (taskRoleOwners.length() > 0) {
            createPipaTaskPutMethod.addRequestHeader(new Header("Task-RoleOwners", taskRoleOwners));
        }

        _httpClient.executeMethod(createPipaTaskPutMethod);

        int createPipaTaskStatus = createPipaTaskPutMethod.getStatusCode();
        processStatusCode(createPipaTaskStatus);
    }

    /**
     * Deletes an item from WDS.
     * 
     * @param uri
     *            URI of the item to delete.
     * @throws IOException
     *             On a low-level I/O problem.
     * @throws WDSException
     *             On a logical WDS problem.
     */
    public void deleteItem(String uri) throws IOException, WDSException {
        DeleteMethod deleteMethod = new DeleteMethod(_wdsUrl + uri);
        _httpClient.executeMethod(deleteMethod);

        int status = deleteMethod.getStatusCode();
        this.processStatusCode(status);
    }

    public void deletePIPA(PipaTask pipaTask) throws IOException, WDSException {
        DeleteMethod deleteMethod = new DeleteMethod(normalizeFormUrl(pipaTask.getFormURL()));
        deleteMethod.addRequestHeader(new Header("Delete-PIPA-Tasks", "True"));
        _httpClient.executeMethod(deleteMethod);
        processStatusCode(deleteMethod.getStatusCode());
    }

    /**
     * Makes {@code formURL} absolute HTTP URL (if it not already).
     *
     * @param formURL
     * @return absolute HTTP URL to form
     */
    private String normalizeFormUrl(String formURL) {
        String result;
        if (formURL.startsWith(OXF_PREFIX)) {
            result = _wdsUrl + formURL.substring(OXF_PREFIX.length());
        } else if (!formURL.startsWith("http://")) {
            result = _wdsUrl + formURL;
        } else {
            result = formURL;
        }
        return result;
    }
}
