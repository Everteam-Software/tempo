package org.intalio.tempo.workflow.fds.remote;

import junit.framework.TestCase;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.intalio.tempo.workflow.fds.core.MessageSender;

public class RemoteFDSTest extends TestCase {

    private Document createDocument(String filename) throws Exception {
        return new SAXReader().read(this.getClass().getResourceAsStream(filename));
    }

    public void testDispathToOde() throws Exception {
        Document doc = createDocument("/createMessageToOde.xml");
        String endpoint = "http://localhost:8080/ode/processes/workflow/ib4p";
        String soapAction = "createTask";

        MessageSender sender = new MessageSender();
        Document reply = sender.requestAndGetReply(doc, endpoint, soapAction);

        System.out.println(reply.asXML());
    }
}
