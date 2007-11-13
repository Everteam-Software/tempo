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

import java.net.URL;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.openjpa.persistence.Persistent;
import org.intalio.tempo.workflow.util.RequiredArgumentException;

@Entity
@Table(name="attachments")
public class Attachment {
    
    @OneToOne(fetch=FetchType.LAZY, cascade={CascadeType.PERSIST,CascadeType.REMOVE})
    private AttachmentMetadata metadata;
    
    @Transient
    private URL _payloadURL;
    
    @Persistent
    @Column(name="payload_url")
    private String payloadURLAsString;

    protected Attachment() {

    }

    public static Attachment createAttachmentSkeleton() {
        return new Attachment();
    }

    public Attachment(AttachmentMetadata metadata, URL payloadURL) {
        this.setMetadata(metadata);
        this.setPayloadURL(payloadURL); 
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
        return _payloadURL;
    }

    public void setPayloadURL(URL payloadURL) {
        if (payloadURL == null) {
            throw new RequiredArgumentException("payloadURL");
        }
        _payloadURL = payloadURL;
        payloadURLAsString = _payloadURL.toExternalForm();
    }

    @Override
    public String toString() {
        return payloadURLAsString;
    }
}
