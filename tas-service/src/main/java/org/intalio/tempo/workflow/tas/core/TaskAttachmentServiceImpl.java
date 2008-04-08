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
package org.intalio.tempo.workflow.tas.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implementation of {@link org.intalio.tempo.workflow.tas.core.TaskAttachmentService}.
 */
public class TaskAttachmentServiceImpl implements TaskAttachmentService {

    private static final Logger _logger = LoggerFactory.getLogger(TaskAttachmentServiceImpl.class);

    private AuthStrategy _authStrategy;

    private StorageStrategy _storageStrategy;

    public TaskAttachmentServiceImpl() {
        // required for Spring configuration
    }
    
    /**
     * Instance constructor.<br />
     * Caller must provide an implementation instance for each Strategy this class is backed by.
     */
    public TaskAttachmentServiceImpl(AuthStrategy authStrategy,
            StorageStrategy storageStrategy) {
        if (authStrategy == null) {
            throw new NullPointerException("authStrategy");
        }
        if (storageStrategy == null) {
            throw new NullPointerException("storageStrategy");
        }
        _authStrategy = authStrategy;
        _storageStrategy = storageStrategy;
        if (_logger.isDebugEnabled()) {
            _logger.debug("Authentication strategy: " + _authStrategy.getClass().getName() + "\nStorage strategy: "
                    + _storageStrategy.getClass().getName());
        }
    }

    public void setAuthStrategy(AuthStrategy strategy) {
        if (strategy == null) {
            throw new NullPointerException("strategy");
        }        
        _authStrategy = strategy;
    }
    
    public void setStorageStrategy(StorageStrategy strategy) {
        if (strategy == null) {
            throw new NullPointerException("strategy");
        }        
        _storageStrategy = strategy;
    }
        
    public String add(AuthCredentials authCredentials, AttachmentMetadata metadata, String localFileUrl)
            throws AuthException,
                InvalidRequestException,
                IOException {
        _authStrategy.authenticate(authCredentials);
        URL localFileUrlObject = new URL(localFileUrl);
        String storedUrl = _storageStrategy.storeAttachment(metadata, localFileUrlObject.openStream());
        return storedUrl;
    }

    public String add(AuthCredentials authCredentials, AttachmentMetadata metadata, byte[] payload)
            throws AuthException,
                InvalidRequestException {
        _authStrategy.authenticate(authCredentials);
        InputStream inputStream = new ByteArrayInputStream(payload);
        String storedUrl = null;
        try {
            storedUrl = _storageStrategy.storeAttachment(metadata, inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return storedUrl;
    }

    public void delete( AuthCredentials authCredentials, String attachmentUrl)
            throws AuthException,
                InvalidRequestException,
                UnavailableAttachmentException {
        _authStrategy.authenticate(authCredentials);
        _storageStrategy.deleteAttachment(attachmentUrl);
    }

}
