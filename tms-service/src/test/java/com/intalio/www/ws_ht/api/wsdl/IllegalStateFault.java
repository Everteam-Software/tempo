
/**
 * IllegalStateFault.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4.1  Built on : Aug 13, 2008 (05:03:35 LKT)
 */

package com.intalio.www.ws_ht.api.wsdl;

public class IllegalStateFault extends java.lang.Exception{
    
    private com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.IllegalState faultMessage;
    
    public IllegalStateFault() {
        super("IllegalStateFault");
    }
           
    public IllegalStateFault(java.lang.String s) {
       super(s);
    }
    
    public IllegalStateFault(java.lang.String s, java.lang.Throwable ex) {
      super(s, ex);
    }
    
    public void setFaultMessage(com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.IllegalState msg){
       faultMessage = msg;
    }
    
    public com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.IllegalState getFaultMessage(){
       return faultMessage;
    }
}
    