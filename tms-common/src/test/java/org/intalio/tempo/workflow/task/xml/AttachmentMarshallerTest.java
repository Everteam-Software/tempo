package org.intalio.tempo.workflow.task.xml;

import java.net.URL;

import junit.framework.TestCase;

import org.apache.axiom.om.OMElement;
import org.intalio.tempo.workflow.task.attachments.Attachment;
import org.intalio.tempo.workflow.task.attachments.AttachmentMetadata;
import org.intalio.tempo.workflow.task.xml.attachments.AttachmentMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttachmentMarshallerTest extends TestCase {
    private static final Logger _logger = LoggerFactory.getLogger(AttachmentMarshallerTest.class);

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AttachmentMarshallerTest.class);
    }

    public void testAttachmentMarshaller() throws Exception {
        Attachment attachment1 = new Attachment(new AttachmentMetadata(), new URL("http://localhost/a1"));
        OMElement att = AttachmentMarshaller.marshalAttachment(attachment1);

        _logger.debug(TestUtils.toPrettyXML(att));
    }

    public void testAttachmentsMarshaller() throws Exception {
        Attachment[] attachments = new Attachment[2];
        Attachment attachment1 = new Attachment(new AttachmentMetadata(), new URL("http://localhost/a1"));
        Attachment attachment2 = new Attachment(new AttachmentMetadata(), new URL("http://localhost/a2"));
        attachments[0] = attachment1;
        attachments[1] = attachment2;
        OMElement att = AttachmentMarshaller.marshalAttachments(attachments);

        _logger.debug(TestUtils.toPrettyXML(att));
    }
}
