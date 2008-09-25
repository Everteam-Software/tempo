package org.intalio.tempo.workflow.task.xml;

import java.io.InputStream;

import junit.framework.TestCase;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class XmlToolingTest extends TestCase {
    private static final Logger _logger = LoggerFactory.getLogger(TaskUnmarshallerTest.class);

    public static void main(String[] args) {
        junit.textui.TestRunner.run(XmlToolingTest.class);
    }

    public void testBadXmlToolingFromSource() throws Exception {
        String resource = "/badXML.xml";
        InputStream requestInputStream = TestUtils.class.getResourceAsStream(resource);
        XmlTooling xmltool = new XmlTooling();
        try {
            xmltool.parseXml(new InputSource(requestInputStream));
            Assert.fail("RuntimeException expected");
        } catch (RuntimeException e) {

        }
    }

    public void testBadXmlToolingFromString() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<root>" + "<first>" + "</root>";
        XmlTooling xmltool = new XmlTooling();
        try {
            xmltool.parseXML(xml);
            Assert.fail("RuntimeException expected");
        } catch (RuntimeException e) {

        }
    }

    public void testSerializeNullXML() throws Exception {
        Document xml = null;
        XmlTooling xmltool = new XmlTooling();

        assertNull(xmltool.serializeXML(xml));
    }
}
