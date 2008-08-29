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

import java.util.Calendar;

import org.apache.axiom.om.OMElement;




import org.intalio.tempo.workflow.task.attachments.AttachmentMetadata;

import org.intalio.tempo.workflow.task.xml.XmlTooling;



import com.intalio.bpms.workflow.taskManagementServices20051109.Attachment;
import com.intalio.bpms.workflow.taskManagementServices20051109.GetAttachmentsResponseDocument;

import com.intalio.bpms.workflow.taskManagementServices20051109.Attachments;

import com.intalio.bpms.workflow.taskManagementServices20051109.impl.AttachmentMetadataImpl;

import com.intalio.bpms.workflow.taskManagementServices20051109.impl.GetAttachmentsResponseDocumentImpl;

public class AttachmentMarshaller  {


public static OMElement  marshalAttachment(org.intalio.tempo.workflow.task.attachments.Attachment attachment) {
        	    	return XmlTooling.convertDocument(transformAttachment(attachment));
    }
    public static OMElement  marshalAttachments(org.intalio.tempo.workflow.task.attachments.Attachment[] attachments) {
    	
    	Attachment[] attsTable=new Attachment[attachments.length];
    	int i=0;
    	for( org.intalio.tempo.workflow.task.attachments.Attachment attachment:attachments){
    		
    		
        	attsTable[i]=transformAttachment(attachment);
        	i++;
    	}
    	Attachments atts=(Attachments )Attachments.Factory.newInstance();
    	atts.setAttachmentArray(attsTable);
    	GetAttachmentsResponseDocumentImpl response=(GetAttachmentsResponseDocumentImpl )GetAttachmentsResponseDocument.Factory.newInstance();
    	response.setGetAttachmentsResponse(atts);
    	    	return XmlTooling.convertDocument(response);
    }
    
    private static Attachment transformAttachment(org.intalio.tempo.workflow.task.attachments.Attachment attachment){
    	AttachmentMetadataImpl metadata=(AttachmentMetadataImpl)  com.intalio.bpms.workflow.taskManagementServices20051109.AttachmentMetadata.Factory.newInstance();
    	AttachmentMetadata data= attachment.getMetadata();
    	Calendar cal=Calendar.getInstance();
    	cal.setTime(data.getCreationDate());
    	metadata.setCreationDate(cal);
    	metadata.setDescription(data.getDescription());
    	metadata.setFileName(data.getFileName());
    	metadata.setMimeType(data.getMimeType());
    	metadata.setTitle(data.getTitle());
    	
    	Attachment att=(Attachment )Attachment.Factory.newInstance();
    	att.setAttachmentMetadata(metadata);
    	att.setPayloadUrl(attachment.getPayloadURL().toString());
    	return att;
    
    }
    }

