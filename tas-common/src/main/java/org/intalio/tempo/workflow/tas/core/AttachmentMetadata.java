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

/**
 * Attachment metadata
 */
public class AttachmentMetadata {

    private String _mimeType = "application/octet-stream";
    
    private String _filename = "";

    public AttachmentMetadata() {
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Attachment metadata:");
        builder.append(" mime-type: "+_mimeType);
        builder.append(" filename: "+_filename);
        return builder.toString();
    }
    
    /**
     * Returns the attachment mime-type
     */
    public String getMimeType() {
        return _mimeType;
    }

    /**
     * Set attachment mime-type
     */
    public void setMimeType(String mimeType) {
        _mimeType = mimeType;
    }
    
    /**
     * Get the attachment filename.
     */
    public String getFilename() {
        return _filename;
    }
    
    /**
     * Set the attachment filename.
     */
    public void setFilename(String filename) {
        _filename = filename;
    }
}
