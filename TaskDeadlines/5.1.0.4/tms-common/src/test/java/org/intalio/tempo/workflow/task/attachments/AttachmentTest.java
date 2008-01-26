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

import junit.framework.Assert;
import junit.framework.TestCase;
import org.intalio.tempo.workflow.task.attachments.Attachment;
import org.intalio.tempo.workflow.task.attachments.AttachmentMetadata;

public class AttachmentTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AttachmentTest.class);
    }

    public void testAttachment() throws Exception {
        AttachmentMetadata metadata = new AttachmentMetadata();
        URL url = new URL("http://localhost/attachment");
        Attachment attachment = new Attachment(metadata, url);
        Assert.assertEquals(metadata, attachment.getMetadata());
        Assert.assertEquals(url, attachment.getPayloadURL());
    }
}
