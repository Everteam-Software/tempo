package org.intalio.tempo.workflow.tmsb4p.server;


import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.engine.Handler;
import org.apache.axis2.handlers.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AxisHandler extends AbstractHandler implements Handler  {
    final static Logger log = LoggerFactory.getLogger(AxisHandler.class);
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
 //       Log.event("--------InvocationResponse");
        return InvocationResponse.CONTINUE;        
    }

    public void revoke(MessageContext msgContext) {
        log.info(msgContext.getEnvelope().toString());
 //       Log.event("--------revoke");
      
    }

    public void setName(String name) {
        this.name = name;
    }
}
