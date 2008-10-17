package org.intalio.tempo.workflow.fds.remote;

import java.io.IOException;

import junit.framework.TestCase;

import org.apache.commons.httpclient.HttpException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.intalio.tempo.workflow.fds.core.MessageSender;

public class RemoteFDSTest extends TestCase {

    private Document createDocument(String filename) throws Exception {
        return new SAXReader().read(this.getClass().getResourceAsStream(filename));
    }

    public void testDispathToOde() throws Exception {
        String[] files = new String[] {
                        "/createMessageToOde.xml",
                        "/createMessageNoSessionToOde.xml"
        };
        for(String filename : files) makeSoapRequestToFDS(filename);
    }

    private void makeSoapRequestToFDS(String filename) throws Exception, HttpException, IOException, DocumentException {
        Document doc = createDocument(filename);
        String endpoint = "http://localhost:8080/ode/processes/workflow/ib4p";
        String soapAction = "createTask";

        MessageSender sender = new MessageSender();
        Document reply = sender.requestAndGetReply(doc, endpoint, soapAction);

        System.out.println(reply.asXML());
    }
}
