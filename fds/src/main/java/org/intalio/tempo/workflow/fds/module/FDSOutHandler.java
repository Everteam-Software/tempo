package org.intalio.tempo.workflow.fds.module;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.HandlerDescription;
import org.apache.axis2.description.TransportInDescription;
import org.apache.axis2.description.TransportOutDescription;
import org.apache.axis2.handlers.AbstractHandler;
import org.apache.axis2.transport.local.LocalTransportReceiver;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.intalio.tempo.workflow.fds.core.FDSAxisHandlerHelper;
import org.intalio.tempo.workflow.fds.core.MessageFormatException;
import org.intalio.tempo.workflow.fds.dispatches.InvalidInputFormatException;
import org.intalio.tempo.workflow.fds.tools.SoapTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Tammo van Lessen
 */
public class FDSOutHandler extends AbstractHandler {
	private static Logger _log = LoggerFactory.getLogger(FDSOutHandler.class);

	private static final String FDS_URI= "/fds/workflow";

	public FDSOutHandler() {
		init(new HandlerDescription("FDSOutHandler"));
	}


	public InvocationResponse invoke(MessageContext msgContext)
			throws AxisFault {

         if (isRequestToFDS(msgContext)) {
			if (_log.isDebugEnabled()) {
				_log.debug("Processing outgoing request to FDS...");
			}
			try {
				FDSAxisHandlerHelper helper = new FDSAxisHandlerHelper(false);
				Document mediatedRequest = helper.processOutMessage(SoapTools.fromAxiom(msgContext.getEnvelope()), msgContext.getSoapAction(),msgContext.getTo().getAddress());
				msgContext.setSoapAction(helper.getSoapAction());
				msgContext.setWSAAction(helper.getSoapAction());
				msgContext.getTo().setAddress(helper.getTargetEPR());
				msgContext.setEnvelope(SoapTools.fromDocument(mediatedRequest));
				msgContext.getOperationContext().setProperty(FDSModule.FDS_HANDLER_CONTEXT, helper);

				// use the local transport
		        TransportOutDescription tOut = msgContext.getConfigurationContext().getAxisConfiguration().getTransportOut(
		                Constants.TRANSPORT_LOCAL);
		        msgContext.setTransportOut(tOut);

				if (_log.isDebugEnabled()) {
					_log.debug("Request redirected to [To: {}, SOAPAction: {}, WSAAction: {}].", new String[] { msgContext.getTo().getAddress(), msgContext.getSoapAction(), msgContext.getWSAAction() });
				}

			} catch (MessageFormatException e) {
				_log.warn("Invalid message format: " + e.getMessage(), e);
			} catch (InvalidInputFormatException e) {
				_log.warn("Invalid message format: " + e.getMessage(), e);
			} catch (DocumentException e) {
				_log.warn("Invalid XML in message: " + e.getMessage(), e);
			} catch (XMLStreamException e) {
				_log.warn("Invalid XML in message: " + e.getMessage(), e);
			} catch (FactoryConfigurationError e) {
				_log.warn("Invalid XML in message: " + e.getMessage(), e);
			}
		}
		
		return InvocationResponse.CONTINUE;
	}

	private boolean isRequestToFDS(MessageContext msgCtx) {
		return msgCtx.getTo().getAddress().contains(FDS_URI);
	}
}
