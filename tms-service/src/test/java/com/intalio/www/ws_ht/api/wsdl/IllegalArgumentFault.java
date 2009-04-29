
/**
 * IllegalArgumentFault.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4.1  Built on : Aug 13, 2008 (05:03:35 LKT)
 */

package com.intalio.www.ws_ht.api.wsdl;

public class IllegalArgumentFault extends java.lang.Exception{
    
    private com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.IllegalArgument faultMessage;
    
    public IllegalArgumentFault() {
        super("IllegalArgumentFault");
    }
           
    public IllegalArgumentFault(java.lang.String s) {
       super(s);
    }
    
    public IllegalArgumentFault(java.lang.String s, java.lang.Throwable ex) {
      super(s, ex);
    }
    
    public void setFaultMessage(com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.IllegalArgument msg){
       faultMessage = msg;
    }
    
    public com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.IllegalArgument getFaultMessage(){
       return faultMessage;
    }
}
    