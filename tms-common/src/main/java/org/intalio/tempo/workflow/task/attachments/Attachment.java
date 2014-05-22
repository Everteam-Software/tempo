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

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.intalio.tempo.workflow.util.RequiredArgumentException;

@Entity
@Table(name="tempo_attachment")
@NamedQueries({
    @NamedQuery(name= Attachment.FIND_BY_URL, query= "select m from Attachment m where m.payloadURLAsString = ?1")
})
public class Attachment {

    public static final String FIND_BY_URL = "find_by_attachment_url";
    
    @OneToOne(cascade = CascadeType.ALL)
    private AttachmentMetadata metadata;

    @Column(name = "payload_url") 
    private String payloadURLAsString;

    public Attachment() {
        
    }
    
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
        if (url == null){
            throw new RequiredArgumentException("url");
        }
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

    /*get PayloadURL as string*/
    public String getPayloadURLAsString() {
        return payloadURLAsString;
    }

    /*set PayloadURL as string*/
    public void setPayloadURLAsString(String payloadURLAsString) {
        this.payloadURLAsString = payloadURLAsString;
    }
    
    
    @Column(name = "ID")
    @Basic
    @Id
    @TableGenerator(name="attach" , table="OPENJPA_SEQUENCE_TABLE", pkColumnName="ID" , valueColumnName="SEQUENCE_VALUE" , pkColumnValue = "0", allocationSize=10)
    @GeneratedValue(strategy=GenerationType.TABLE , generator="attach")
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
}
