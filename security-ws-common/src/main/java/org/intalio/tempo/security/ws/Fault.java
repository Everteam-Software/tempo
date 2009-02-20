package org.intalio.tempo.security.ws;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;

public class Fault extends AxisFault{
    private static final long serialVersionUID = 6320419170578715532L;

    Fault(Throwable exception,OMElement Detail){
		super(exception.getMessage(),exception);
		this.setDetail(Detail);
	}
}
