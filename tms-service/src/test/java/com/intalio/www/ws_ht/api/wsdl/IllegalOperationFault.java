
/**
 * IllegalOperationFault.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4.1  Built on : Aug 13, 2008 (05:03:35 LKT)
 */

package com.intalio.www.ws_ht.api.wsdl;

public class IllegalOperationFault extends java.lang.Exception{
    
    private com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.IllegalOperation faultMessage;
    
    public IllegalOperationFault() {
        super("IllegalOperationFault");
    }
           
    public IllegalOperationFault(java.lang.String s) {
       super(s);
    }
    
    public IllegalOperationFault(java.lang.String s, java.lang.Throwable ex) {
      super(s, ex);
    }
    
    public void setFaultMessage(com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.IllegalOperation msg){
       faultMessage = msg;
    }
    
    public com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.IllegalOperation getFaultMessage(){
       return faultMessage;
    }
}
    