package org.intalio.tempo.workflow.tms.server;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.client.ServiceClient;

public class MockServiceClient extends ServiceClient {

    public MockServiceClient() throws AxisFault {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public OMElement sendReceive(OMElement elem){
        //this.sendReceive(elem));
        return elem;
    }

}
