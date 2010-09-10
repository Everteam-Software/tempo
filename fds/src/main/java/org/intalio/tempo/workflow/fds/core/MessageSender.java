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
package org.intalio.tempo.workflow.fds.core;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.intalio.tempo.workflow.fds.FormDispatcherConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sends XML/HTTP requests to web services and returns the replies.
 * <p>
 * This class can be used to send an XML message (e.g. a SOAP message) provided
 * as a <code>Document</code> to a specific endpoint with a specific
 * <code>SOAPAction</code>, using the HTTP protocol. The reply is also
 * returned as a <code>Document</code>.
 * 
 * @author Iwan Memruk
 * @version $Revision: 1172 $
 * @see nu.xom.Document
 * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616.html">The HTTP 1.1
 *      specification.</a>
 * @see <a href="http://www.w3.org/TR/soap/">The SOAP specification.</a>
 */
public class MessageSender {
    static final public Logger log = LoggerFactory.getLogger(MessageSender.class);

    /**
     * Sends an XML request to a specific HTTP endpoint with a specific
     * <code>SOAPAction</code> header and returns the reply as XML.
     * 
     * @param requestMessage
     *            The request XML payload.
     * @param endpoint
     *            The endpoint URL, such as
     *            <code>http://localhost/webservice</code>
     * @param soapAction
     *            The <code>SOAPAction</code> to send the message with.
     * @return The reply from the endpoint as an XML <code>Document</code>.
     * @throws HttpException
     *             If an HTTP-level error happens.
     * @throws IOException
     *             If a low-level input/output error happens (e.g. a
     *             disconnection during the request/response).
     * @throws DocumentException 
     * @see <a href="http://www.w3.org/TR/soap/">The SOAP specification.</a>
     */
    public Document requestAndGetReply(Document requestMessage, String endpoint, String soapAction)
        throws HttpException, IOException, DocumentException
    {
        Document result = null;
        
        PostMethod postMethod = new PostMethod(endpoint);
        postMethod.addRequestHeader("SOAPAction", soapAction);
        postMethod.setRequestEntity(new ByteArrayRequestEntity(requestMessage.asXML().getBytes(), "text/xml; charset=UTF-8"));
        
        HttpClient httpClient = new HttpClient();

        // turn off retrying, since retrying SOAP requests can cause side-effects
        DefaultHttpMethodRetryHandler retryhandler = new DefaultHttpMethodRetryHandler(0, false);
        httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, retryhandler);
        
        // set the timeout
        httpClient.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, FormDispatcherConfiguration.getInstance().getHttpTimeout());

        // Prepare an XML parser 
        SAXReader reader = new SAXReader();
        InputStream responseInputStream = null;
        try {
            httpClient.executeMethod(postMethod);
            responseInputStream = postMethod.getResponseBodyAsStream();
            result = reader.read(responseInputStream);
        } finally {
            postMethod.releaseConnection();
            if (responseInputStream != null) responseInputStream.close();
        }

        return result;
    }
}
