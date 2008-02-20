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
 *
 * $Id: TaskManagementServicesFacade.java 5440 2006-06-09 08:58:15Z imemruk $
 * $Log:$
 */

package org.intalio.tempo.workflow.task.attachments;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.openjpa.persistence.Persistent;
import org.intalio.tempo.workflow.util.RequiredArgumentException;

@Entity
@Table(name = "TEMPO_ATTACHMENT")
public class Attachment {

    @Persistent(cascade = { CascadeType.ALL })
    private AttachmentMetadata metadata;

    @Persistent
    @Column(name = "payload_url")
    private String payloadURLAsString;

    public Attachment(AttachmentMetadata metadata, URL payloadURL) {
        this.setMetadata(metadata);
        this.setPayloadURL(payloadURL);
    }
    
    public Attachment(AttachmentMetadata metadata, String payloadURL) {
        this.setMetadata(metadata);
        this.setPayloadURLFromString(payloadURL);
    }

    public AttachmentMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(AttachmentMetadata metadata) {
        if (metadata == null) {
            throw new RequiredArgumentException("metadata");
        }
        this.metadata = metadata;
    }

    public URL getPayloadURL() {
        try {
            return URI.create(payloadURLAsString).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public void setPayloadURLFromString(String url) {
        payloadURLAsString = url;
        getPayloadURL(); // check URL is valid
    }
    
    public void setPayloadURL(URL payloadURL) {
        if (payloadURL == null) {
            throw new RequiredArgumentException("payloadURL");
        }
        payloadURLAsString = payloadURL.toExternalForm();
    }

    @Override
    public String toString() {
        return payloadURLAsString;
    }
}
