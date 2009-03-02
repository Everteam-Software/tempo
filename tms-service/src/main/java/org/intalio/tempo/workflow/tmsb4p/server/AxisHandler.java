package org.intalio.tempo.workflow.tmsb4p.server;


import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.AxisMessage;
import org.apache.axis2.engine.Handler;
import org.apache.axis2.handlers.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AxisHandler extends AbstractHandler implements Handler  {
    final static Logger log = LoggerFactory.getLogger(AxisHandler.class);
   // private static ThreadLocal _username = new ThreadLocal();
    
    private String name;

    {
        //Log.setFile("d:\\temp.log");
       // Log.setCache(false, 0, 0);
    }
    public String getName() {
        return name;
    }

    public InvocationResponse invoke(MessageContext msgContext) throws AxisFault {
        log.info(msgContext.getEnvelope().toString());
        //boolean processedHeader = false;
        
        try {
         AxisMessage msg = msgContext.getAxisMessage();
         Log.event("========header:"+msgContext.getEnvelope().toString());
         Log.event("========header:"+msgContext.getEnvelope().getHeader().toString());
         int l = msg.getSoapHeaders().size();
         Log.event("========= header size:" + l);
//         for (int i = 0; i< l; i++){
//             SOAPHeaderMessage shm = (SOAPHeaderMessage)soapHeaders.get(i);
//             QName ele = shm.getElement();
//             QName m = shm.getMessage();
//             System.out.println("========= ele:" + ele +", msg:" + m);
//             //Log.log("========= ele:" + ele +", msg:" + m);
//             
//             TMSRequestProcessor.participantToken.set("");
//         }
         
        
        } catch (Exception e) {
         //capture and wrap any exception.
         throw new AxisFault("Failed to retrieve the SOAP Header or it's details properly.", e);
        }

//        if(!processedHeader)
//         throw new AxisFault("Failed to retrieve the SOAP Header");
       
       
        //Log.event("--------InvocationResponse");
        return InvocationResponse.CONTINUE;        
    }

    public void revoke(MessageContext msgContext) {
        log.info(msgContext.getEnvelope().toString());
       // Log.event("--------revoke");
      
    }

    public void setName(String name) {
        this.name = name;
    }
}
