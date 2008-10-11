package org.intalio.tempo.security.ws;

import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.AxisFault;
import org.apache.axis2.client.ServiceClient;
import org.jmock.Expectations;
import org.junit.runner.RunWith;

import com.googlecode.instinct.expect.ExpectThat;
import com.googlecode.instinct.expect.ExpectThatImpl;
import com.googlecode.instinct.integrate.junit4.InstinctRunner;
import com.googlecode.instinct.marker.annotate.Mock;
import com.googlecode.instinct.marker.annotate.Specification;
import com.googlecode.instinct.marker.annotate.Subject;

@RunWith(InstinctRunner.class)
public class TokenWSGetTokenFromTicketTest {
    final static ExpectThat expect = new ExpectThatImpl();
    
    @Mock
    private ServiceClient _serviceClient;
    
    @Subject
    private TokenClient _client = new TokenClient("http://localhost/security/tokenService"){
        
        protected ServiceClient getServiceClient() throws AxisFault{ 
            return _serviceClient;
        }
    };
    
    @Specification
    void getTokenFromTicket() throws Exception{
        final OMElement request = loadElementFromResource("/getTokenFromTicketRequest.xml");
        final OMElement response = loadElementFromResource("/getTokenFromTicketResponse.xml");
        expect.that(new Expectations() {
            {
                one(_serviceClient).getOptions();
                //one(_serviceClient).sendReceive(request); will(returnValue(response));
                allowing(_serviceClient); will(returnValue(response));
                //TODO need to add the real check for the input
            }
        });
        
        _client.getTokenFromTicket("TICKET", "https://localhost:8443/cas/proxyValidate");
    }
    
    private OMElement loadElementFromResource(String resource) throws Exception {
        InputStream requestInputStream = TokenWSGetTokenFromTicketTest.class.getResourceAsStream(resource);
        if (requestInputStream == null)
            throw new IllegalStateException("Missing resource: " + resource);

        XMLStreamReader parser = XMLInputFactory.newInstance().createXMLStreamReader(requestInputStream);
        StAXOMBuilder builder = new StAXOMBuilder(parser);

        return builder.getDocumentElement();
    }
}
