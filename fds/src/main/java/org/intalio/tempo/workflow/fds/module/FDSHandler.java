package org.intalio.tempo.workflow.fds.module;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.HandlerDescription;
import org.apache.axis2.handlers.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Tammo van Lessen
 */
public class FDSHandler extends AbstractHandler {
	private static Logger _log = LoggerFactory.getLogger(FDSHandler.class);
    /**
     * The common prefix for all handled request URI's. <br>
     * This must comply to the servlet mapping specified in <code>web.xml</code>
     * .
     */
    private static final String _URI_PREFIX = "/fds/workflow";

    /**
     * The fixed URI for Workflow Processes request.
     */
    private static final String _IB4P_URI = "/ib4p";
	

	public FDSHandler() {
		init(new HandlerDescription("FDSHandler"));
	}


	public InvocationResponse invoke(MessageContext msgContext)
			throws AxisFault {

		if (isFDSRequest(msgContext)) {
			_log.debug("FDS request received.");

			_log.error("From: {}", msgContext.getFrom());
			_log.error("To: {}", msgContext.getTo());
			_log.error("SOAPAction: {}", msgContext.getSoapAction());
			_log.error("WSAAction: {}", msgContext.getWSAAction());
			
			if (isFromTMP(msgContext)) {
				_log.info("Workflow Processes -> User Process");
				
				// do data mediation, set To, SOAPAction and WSAAction to new values.
			} else {
				_log.info("User Process -> Workflow Processes");
				
				// do data mediation, set To, SOAPAction and WSAAction to new values.
			}
		}
		
		return InvocationResponse.CONTINUE;
	}

	private boolean isFDSRequest(MessageContext msgCtx) {
		return msgCtx.getTo().getAddress().contains(_URI_PREFIX);
	}
	
	private boolean isFromTMP(MessageContext msgCtx) {
		return msgCtx.getTo().getAddress().endsWith(_IB4P_URI);
	}
}
