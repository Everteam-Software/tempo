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


import java.util.Date;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.intalio.tempo.workflow.util.RequiredArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttachmentMetadataTest extends TestCase {
    
    private static final Logger _logger = LoggerFactory.getLogger(AttachmentMetadataTest.class);

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AttachmentMetadataTest.class);
    }

    public void testAttachmentMetadata() {
        AttachmentMetadata metadata = new AttachmentMetadata();
        Assert.assertEquals(AttachmentMetadata.DEFAULT_MIME_TYPE, metadata.getMimeType());
    }

    public void testToString() {
        AttachmentMetadata metadata = new AttachmentMetadata();
        _logger.debug(metadata.toString());
    }

    public void testGetAndSetMimeType() {
        AttachmentMetadata metadata = new AttachmentMetadata();
        String mimeType = "image/jpeg";
        metadata.setMimeType(mimeType);
        Assert.assertEquals(mimeType, metadata.getMimeType());
        try {
            metadata.setMimeType(null);
            Assert.fail("RequiredArgumentException expected");
        } catch (RequiredArgumentException e) {
            
        }
    }

    public void testGetAndSetFileName() {
        AttachmentMetadata metadata = new AttachmentMetadata();
        Assert.assertNotNull(metadata.getFileName());
        
        String fileName = "myfile";
        metadata.setFileName(fileName);
        Assert.assertEquals(fileName, metadata.getFileName());
        
        try {
            metadata.setFileName(null);
            Assert.fail("RequiredArgumentException expected");
        } catch (RequiredArgumentException e) {
            
        }
    }
    
    public void testGetAndSetTitle() {
        AttachmentMetadata metadata = new AttachmentMetadata();
        Assert.assertNotNull(metadata.getTitle());
        
        String title = "my title";
        metadata.setTitle(title);
        Assert.assertEquals(title, metadata.getTitle());
        
        try {
            metadata.setTitle(null);
            Assert.fail("RequiredArgumentException expected");
        } catch (RequiredArgumentException e) {
            
        }        
    }

    public void testGetAndSetDescription() {
        AttachmentMetadata metadata = new AttachmentMetadata();
        Assert.assertNotNull(metadata.getDescription());
        
        String description = "my description";
        metadata.setDescription(description);
        Assert.assertEquals(description, metadata.getDescription());
        
        try {
            metadata.setDescription(null);
            Assert.fail("RequiredArgumentException expected");
        } catch (RequiredArgumentException e) {
            
        }        
    }

    public void testGetAndSetCreationDate() {
        AttachmentMetadata metadata = new AttachmentMetadata();
        Assert.assertNotNull(metadata.getCreationDate());

        Date creationDate = new Date();
        metadata.setCreationDate(creationDate);
        Assert.assertEquals(creationDate, metadata.getCreationDate());        
        
        try {
            metadata.setCreationDate(null);
            Assert.fail("RequiredArgumentException expected");
        } catch (RequiredArgumentException e) {
            
        }                
    }    
}
