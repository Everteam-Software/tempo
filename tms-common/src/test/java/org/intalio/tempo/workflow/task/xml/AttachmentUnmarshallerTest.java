package org.intalio.tempo.workflow.task.xml;

import junit.framework.TestCase;

import org.apache.axiom.om.OMElement;
import org.intalio.tempo.workflow.task.attachments.Attachment;
import org.intalio.tempo.workflow.task.xml.attachments.AttachmentUnmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttachmentUnmarshallerTest extends TestCase{
    private static final Logger _logger = LoggerFactory.getLogger(AttachmentUnmarshallerTest.class);

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AttachmentUnmarshallerTest.class);
    }
    
    public void testAttachmentUnmarshaller() throws Exception {
        OMElement att = TestUtils.loadElementFromResource("/attachment1.xml");
        AttachmentUnmarshaller au = new AttachmentUnmarshaller();
        Attachment attachment = au.unmarshalAttachment(att);
        assertEquals("http://localhost/a1", attachment.toString());
    }
}
