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
import org.apache.axiom.om.OMFactory;

import org.intalio.tempo.workflow.task.attachments.Attachment;
import org.intalio.tempo.workflow.task.attachments.AttachmentMetadata;
import org.intalio.tempo.workflow.task.xml.TaskXMLConstants;
import org.intalio.tempo.workflow.util.xml.OMMarshaller;
import org.intalio.tempo.workflow.util.xml.XsdDateTime;

public class AttachmentMarshaller extends OMMarshaller {

    public AttachmentMarshaller(OMFactory omFactory) {
        super(omFactory, omFactory.createOMNamespace(TaskXMLConstants.TASK_NAMESPACE,
                TaskXMLConstants.TASK_NAMESPACE_PREFIX));
    }

    public void marshalAttachment(Attachment attachment, OMElement parentElement) {
        OMElement attachmentElement = this.createElement(parentElement, "attachment");
        OMElement metadataElement = this.createElement(attachmentElement, "attachmentMetadata");

        AttachmentMetadata metadata = attachment.getMetadata();
        this.createElement(metadataElement, "mimeType", metadata.getMimeType());
        this.createElement(metadataElement, "fileName", metadata.getFileName());
        this.createElement(metadataElement, "title", metadata.getTitle());
        this.createElement(metadataElement, "description", metadata.getDescription());
        this.createElement(metadataElement, "creationDate", new XsdDateTime(metadata.getCreationDate()).toString());

        this.createElement(attachmentElement, "payloadUrl", attachment.getPayloadURL().toString());
    }
}
