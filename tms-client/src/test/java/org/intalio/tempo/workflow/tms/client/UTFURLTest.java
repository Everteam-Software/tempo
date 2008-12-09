package org.intalio.tempo.workflow.tms.client;

import java.io.InputStream;
import java.net.URI;
import java.util.BitSet;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.intalio.tempo.workflow.tms.client.dependent_tests.Utils;

public class UTFURLTest extends TestCase {

    public void testRussian() throws Exception {
        Properties props = new Properties();
        
        InputStream resourceAsStream = this.getClass().getResourceAsStream("/sample.pipa");
        props.load(resourceAsStream);
        String url = (String)props.get("processEndpoint");
        
        URI uri = URI.create(url);
        System.out.println("Connecting to:\n"+new String(url.getBytes()));
        
        Options options = new Options();
        
        EndpointReference endpointReference = new EndpointReference(uri.toString());
        options.setTo(endpointReference);
        options.setAction((String)props.get("userProcessInitSOAPAction"));
        ServiceClient service = new ServiceClient();
        service.setOptions(options);
        OMElement el = Utils.loadElementFromResource("/requestTo.xml");
        OMElement resp = service.sendReceive(el);
        System.out.println(resp.toString());
    }
    
    protected boolean validate(char component[], int soffset, int eoffset, BitSet generous) {
        if (eoffset == -1)
            eoffset = component.length - 1;
        for (int i = soffset; i <= eoffset; i++)
            if (!generous.get(component[i]))
                return false;

        return true;
    }

}
