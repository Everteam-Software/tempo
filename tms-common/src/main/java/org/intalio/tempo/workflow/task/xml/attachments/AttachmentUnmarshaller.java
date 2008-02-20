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

package org.intalio.tempo.workflow.task.xml.attachments;

import org.apache.axiom.om.OMElement;
import org.intalio.tempo.workflow.task.attachments.Attachment;
import org.intalio.tempo.workflow.task.attachments.AttachmentMetadata;
import org.intalio.tempo.workflow.task.xml.TaskXMLConstants;
import org.intalio.tempo.workflow.util.RequiredArgumentException;
import org.intalio.tempo.workflow.util.xml.InvalidInputFormatException;
import org.intalio.tempo.workflow.util.xml.OMElementQueue;
import org.intalio.tempo.workflow.util.xml.OMUnmarshaller;
import org.intalio.tempo.workflow.util.xml.XsdDateTime;

public class AttachmentUnmarshaller extends OMUnmarshaller {

    public AttachmentUnmarshaller() {
        super(TaskXMLConstants.TASK_NAMESPACE, TaskXMLConstants.TASK_NAMESPACE_PREFIX);
    }

    public Attachment unmarshalAttachment(OMElement rootElement) throws InvalidInputFormatException {
        if (rootElement == null) {
            throw new RequiredArgumentException("rootElement");
        }

        OMElementQueue rootQueue = new OMElementQueue(rootElement);

        OMElement metadataElement = this.requireElement(rootQueue, "attachmentMetadata");
        OMElementQueue metadataQueue = new OMElementQueue(metadataElement);

        AttachmentMetadata metadata = new AttachmentMetadata();
        String mimeType = this.expectElementValue(metadataQueue, "mimeType");
        if (mimeType != null) {
            metadata.setMimeType(mimeType);
        }
        String fileName = this.expectElementValue(metadataQueue, "fileName");
        if (fileName != null) {
            metadata.setFileName(fileName);
        }
        String title = this.expectElementValue(metadataQueue, "title");
        if (title != null) {
            metadata.setTitle(title);
        }
        String description = this.expectElementValue(metadataQueue, "description");
        if (description != null) {
            metadata.setDescription(description);
        }
        String creationDateStr = this.expectElementValue(metadataQueue, "creationDate");
        if ((creationDateStr != null) && ! ("".equals(creationDateStr))) {
            metadata.setCreationDate(new XsdDateTime(creationDateStr).getTime());
        }

        String payloadURLStr = this.requireElementValue(rootQueue, "payloadUrl");
        return new Attachment(metadata, payloadURLStr);
    }
}
