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
package org.intalio.tempo.workflow.task.attachments;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.openjpa.persistence.Persistent;
import org.intalio.tempo.workflow.util.RequiredArgumentException;

/**
 * 
 * @author Iwan Memruk
 * @version $Revision: 1022 $
 */
@Entity
@Table(name="attmeta")
public class AttachmentMetadata {

    public static final String DEFAULT_MIME_TYPE = "application/octet-stream";

    /**
     * MIME type of the atachment.
     */
    @Persistent
    @Column(name="mime_type")
    private String _mimeType = DEFAULT_MIME_TYPE;

    @Persistent
    @Column(name="widget")
    private String _widget = "";

    @Persistent
    @Column(name="file_name")
    private String _fileName = "attachment";

    @Persistent
    @Column(name="title")
    private String _title = "(untitled)";

    @Persistent
    @Column(name="description")
    private String _description = "";

    @Persistent
    @Column(name="creation_date")
    private Date _creationDate = new Date();

    /**
     * Instance constructor. <br />
     * 
     * Initializes the <code>mimeType</code> property to <code>application/octet-stream</code>.
     */
    public AttachmentMetadata() {

    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("Attachment metadata:\n");

        builder.append("MIME type: ");
        builder.append(_mimeType);

        builder.append("widget: ");
        builder.append(_widget);

        builder.append("\nFile name: '");
        builder.append(_fileName);
        builder.append("'");

        builder.append("\nTitle: '");
        builder.append(_title);
        builder.append("'");

        builder.append("\nDescription: '");
        builder.append(_description);
        builder.append("'");

        builder.append("\nCreation date: ");
        builder.append(_creationDate == null ? "not specified" : _creationDate);

        return builder.toString();
    }

    /**
     * Returns the MIME type of the atachment.
     * 
     * @return The MIME type of the atachment.
     */
    public String getMimeType() {
        return _mimeType;
    }

    /**
     * Sets the MIME type of the atachment.
     * 
     * @param mimeType The MIME type of the atachment.
     */
    public void setMimeType(String mimeType) {
        if (mimeType == null) {
            throw new RequiredArgumentException("mimeType");
        }
        _mimeType = mimeType;
    }

    public String getWidget() {
        return _widget;
    }

    public void setWidget(String widget) {
        _widget = widget;
    }

    public String getFileName() {
        return _fileName;
    }

    public void setFileName(String fileName) {
        if (fileName == null) {
            throw new RequiredArgumentException("fileName");
        }
        _fileName = fileName;
    }

    public String getTitle() {
        return _title;
    }

    public void setTitle(String title) {
        if (title == null) {
            throw new RequiredArgumentException("title");
        }
        _title = title;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        if (description == null) {
            throw new RequiredArgumentException("description");
        }
        _description = description;
    }

    public Date getCreationDate() {
        return _creationDate;
    }

    public void setCreationDate(Date creationDate) {
        if (creationDate == null) {
            throw new RequiredArgumentException("creationDate");
        }
        _creationDate = creationDate;
    }
}
