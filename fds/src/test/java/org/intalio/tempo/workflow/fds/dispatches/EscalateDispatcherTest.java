package org.intalio.tempo.workflow.fds.dispatches;

import java.io.InputStream;

import junit.framework.TestCase;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.intalio.tempo.workflow.fds.FormDispatcherServletTest;

public class EscalateDispatcherTest extends TestCase {
    final InputStream requestInputStream = FormDispatcherServletTest.class.getResourceAsStream("/createMessageToOde.xml");
    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        junit.textui.TestRunner.run(EscalateDispatcherTest.class);
    }
    
    public void testGetter()throws Exception{
        EscalateDispatcher dispatcher = new EscalateDispatcher();
        String endpoint =  dispatcher.getTargetEndpoint();
        String soapAction = dispatcher.getTargetSoapAction();
        
        assertTrue(endpoint.endsWith("workflow/ib4p"));
        assertEquals(soapAction, "escalateTask");
    }
    
    public void testDispatchRequest()throws Exception{
        EscalateDispatcher dispatcher = new EscalateDispatcher();
        SAXReader reader = new SAXReader();
        Document request = reader.read(requestInputStream);
        dispatcher.dispatchRequest(request);
        assertEquals(request.getRootElement().getQName().getName(), "escalateTaskRequest");
    }
    
    public void testDispatchResponse()throws Exception{
        EscalateDispatcher dispatcher = new EscalateDispatcher();
        SAXReader reader = new SAXReader();
        Document response = reader.read(requestInputStream);
        dispatcher.dispatchResponse(response);
        assertEquals(response.getRootElement().getQName().getName(), "escalateResponse");
    }

}
