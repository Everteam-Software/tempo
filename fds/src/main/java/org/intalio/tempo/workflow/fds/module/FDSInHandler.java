package org.intalio.tempo.workflow.fds.module;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.context.OperationContext;
import org.apache.axis2.description.HandlerDescription;
import org.apache.axis2.handlers.AbstractHandler;
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
public class FDSInHandler extends AbstractHandler {
	private static Logger _log = LoggerFactory.getLogger(FDSInHandler.class);

	public FDSInHandler() {
		init(new HandlerDescription("FDSInHandler"));
	}

	public InvocationResponse invoke(MessageContext msgContext)
			throws AxisFault {

		OperationContext oCtx = msgContext.getOperationContext();
		if (oCtx != null) {
			FDSAxisHandlerHelper helper = (FDSAxisHandlerHelper)oCtx.getProperty(FDSModule.FDS_HANDLER_CONTEXT);
			
			if (helper != null) {
				if (_log.isDebugEnabled()) {
					_log.debug("Processing incoming response from FDS...");
				}
				
				try {
					Document mediatedRequest = helper.processInMessage(SoapTools.fromAxiom(msgContext.getEnvelope()), msgContext.getSoapAction(),msgContext.getTo().getAddress());
					msgContext.setEnvelope(SoapTools.fromDocument(mediatedRequest));
					msgContext.setSoapAction(helper.getSoapAction());
					msgContext.setWSAAction(helper.getSoapAction());

				} catch (MessageFormatException e) {
					_log.warn("Invalid message format: " + e.getMessage(), e);
				} catch (DocumentException e) {
					_log.warn("Invalid XML in message: " + e.getMessage(), e);
				} catch (XMLStreamException e) {
					_log.warn("Invalid XML in message: " + e.getMessage(), e);
				} catch (FactoryConfigurationError e) {
					_log.warn("Invalid XML in message: " + e.getMessage(), e);
				} catch (InvalidInputFormatException e) {
					_log.warn("Invalid XML in message: " + e.getMessage(), e);
				}
			}
		}
		
		return InvocationResponse.CONTINUE;
	}

}
