package org.intalio.tempo.workflow.fds.core;

import org.apache.axis2.AxisFault;
import org.dom4j.Document;
import org.dom4j.Element;
import org.intalio.tempo.workflow.fds.FormDispatcherConfiguration;
import org.intalio.tempo.workflow.fds.dispatches.Dispatchers;
import org.intalio.tempo.workflow.fds.dispatches.IDispatcher;
import org.intalio.tempo.workflow.fds.dispatches.InvalidInputFormatException;
import org.intalio.tempo.workflow.fds.dispatches.NoDispatcherException;
import org.intalio.tempo.workflow.fds.tools.SoapTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the mediator class that facilitates the transformation of soap messages from one format to another <br>
 * format in the FDS IN/OUT axis2 handlers. It is used to transform data from UBP to TMP and vice-versa.   
 *  
 * This class should in initiated in the Out handler and placed in the appropriate context (OperationContext) <br>
 * and retrieved in the In Handler from the same context to carry out the transformations. It maintains the 
 * state of transformations carried out and the transformers used. 
 * 
 * Note: This class is probably the replacement for org.intalio.tempo.workflow.fds.FormDispatcherServlet, due to the <br> 
 * fact that a lot of transport timeout errors were being encountered in the FormDispatcherServlet.
 * 
 * @see org.intalio.tempo.workflow.fds.FormDispatcherServlet
 * @see org.intalio.tempo.workflow.fds.module.FDSOutHandler
 * @see org.intalio.tempo.workflow.fds.module.FDSInHandler
 *   
 * @author Sathwik
 */
public class FDSAxisHandlerHelper {

    private static Logger _log = LoggerFactory.getLogger(FDSAxisHandlerHelper.class);
    
    /** Used to identify the direction of transformations to be performed. */
    public enum Direction {WF2UP, UP2WF}

    /** TMP to UBP transformer */
    private WorkflowProcessesMessageConvertor _wf2up = null;
    
    /** UBP to TMP transformer */
    private UserProcessMessageConvertor _up2wf = null;
    
    /** Dispatcher transformer which handles the Escalate and Notify requests coming in from UBP. */
    private IDispatcher _dispatcher = null;
    
    /** Maintains the value of SoapAction header upon transformation and which 
     * needs to be used while routing the request to the target service. */
    private String _soapAction = null;
    
    /** Maintains the address of target service to which the transformed data is to be routed. */
    private String _targetEPR = null;
    
    /** Maintains the state of direction of transformations, whether it is from UBP->TMP or TMP->UBP. */
    private Direction _requestDirection;

    public FDSAxisHandlerHelper(boolean changeCallbackAddress) {
        _wf2up = new WorkflowProcessesMessageConvertor();
        _up2wf = new UserProcessMessageConvertor();
        _up2wf.setChangeCallbackAddress(changeCallbackAddress);
    }

    /**
     * This methods handles the transformations for the out going messages. Will be invoked from Out Handlers.  
     * 
     * @param soapEnv
     * @param soapAction
     * @param toAddress
     * @return
     * @throws InvalidInputFormatException
     * @throws AxisFault
     * @throws MessageFormatException
     */
    public Document processOutMessage(Document soapEnv, String soapAction, String toAddress) throws InvalidInputFormatException, AxisFault, MessageFormatException {
        _soapAction = soapAction;
        Document result = null;
        
        if (_log.isDebugEnabled()) {
			_log.debug("Outgoing document before conversion: {}", soapEnv.asXML());
		}

        if (toAddress.contains("fds/workflow/ib4p")) {
        	// request from TMP to UBP
        	_requestDirection = Direction.WF2UP;
        	
            result = convertWF2UP(soapEnv);            
        } else {
        	// request from UBP to TMP
        	_requestDirection = Direction.UP2WF;
        	
    		_dispatcher = createDispatcher(soapEnv);
    		if (_dispatcher != null) {
		        Document dispatcherReq = createDispatcherRequest(_dispatcher,
		                soapEnv);
		        _targetEPR = _dispatcher.getTargetEndpoint();
		        _soapAction = _dispatcher.getTargetSoapAction();
		
		        result = dispatcherReq;
    		} else {
    			result = convertUP2WF(soapEnv);
    		}
        }
        
        if (_log.isDebugEnabled()) {
			_log.debug("Outgoing document before conversion: {}", result.asXML());
		}

        return result;
    }

    /**
     * This methods handles the transformations for the in coming messages. Will be invoked from In Handlers.
     * 
     * @param soapEnv
     * @param soapAction
     * @param toAddress
     * @return
     * @throws MessageFormatException
     * @throws AxisFault
     * @throws InvalidInputFormatException
     */
    public Document processInMessage(Document soapEnv, String soapAction,String toAddress) throws MessageFormatException, AxisFault, InvalidInputFormatException {
        _soapAction = soapAction;
        Document result = null;

        if (_log.isDebugEnabled()) {
			_log.debug("Incoming document before conversion: {}", soapEnv.asXML());
		}
        
        if (Direction.WF2UP.equals(_requestDirection)) {
        	// request came from TMP to UBP, now translating the response from UBP to TMP.
        	_log.debug("request came from TMP to UBP, now translating the response from UBP to TMP.");
        	result = convertUP2WF(soapEnv);
        } else {
        	// request came from UBP to TMP, now translating the response from TMP to UBP.
        	_log.debug("request came from UBP to TMP, now translating the response from TMP to UBP.");
	        if (_dispatcher != null) {
	        	result = createDispatcherResponse(_dispatcher,
	                        soapEnv);
	        } else {
	        	result = convertWF2UP(soapEnv);
	        }
        }
        
		if (_log.isDebugEnabled()) {
			_log.debug("Incoming document after conversion: {}", result.asXML());
		}
		
		return result;
    }

    /**
     * Converts UBP data to TMP data
     * 
     * @param soapEnv
     * @return
     * @throws MessageFormatException
     * @throws AxisFault
     */
	private Document convertUP2WF(Document soapEnv)
			throws MessageFormatException,
			AxisFault {

	    _up2wf.convertMessage(soapEnv);
	
	    if (_up2wf.getSoapAction() != null) {
	        _soapAction = _up2wf.getSoapAction();
	    }
	    
	    FormDispatcherConfiguration _config = FormDispatcherConfiguration.getInstance();            
	    _targetEPR = _config.getPxeBaseUrl() + _config.getWorkflowProcessesRelativeUrl();
	
	    return soapEnv;
	}

    /**
     * Converts TMP data to UBP data.
     * 
     * @param soapEnv
     * @return
     * @throws MessageFormatException
     */
	private Document convertWF2UP(Document soapEnv) throws MessageFormatException {
		_wf2up.convertMessage(soapEnv, _up2wf.getUserProcessNamespaceUri());
		
		if (_wf2up.getSoapAction() != null) {
		    _soapAction = _wf2up.getSoapAction();
		}

		_targetEPR = _wf2up.getUserProcessEndpoint();
		
		return soapEnv;
	}

    /**
     * Returns the SoapAction to be used while sending the request to the target service. 
     * 
     * @return
     */
    public String getSoapAction(){
        return _soapAction;
    }
    
    /**
     * Returns the endpoint address of the target service.
     * @return
     */
    public String getTargetEPR(){
        return _targetEPR;        
    }
    
    /**
     * Retrieves the payload from the soap message.
     * 
     * @param soapEnvelope - Soap Envelope a Dom4j Document object.
     * @return payload - Dom4j Document object.
     * @throws InvalidInputFormatException
     */
    protected Document getPayloadFromEnvelope(Document soapEnvelope) throws InvalidInputFormatException {
        Document pureRequest = SoapTools.unwrapMessage(soapEnvelope);
        return pureRequest;
    }

    /**
     * Creates and return the appropriate dispatcher that needs to handle the transformations.
     * 
     * @param soapEnvelope - Dom4j Document object
     * @return - IDispatcher instance if found for the root element in the payload otherwise null.
     * @throws InvalidInputFormatException
     */
    protected IDispatcher createDispatcher(Document soapEnvelope) throws InvalidInputFormatException {
        IDispatcher dispatcher = null;

        Document payload = getPayloadFromEnvelope(soapEnvelope);
        Element rootElement = payload.getRootElement();
        String rootElementName = rootElement.getName();

        try {
            dispatcher = Dispatchers.createDispatcher(rootElementName);
        } catch (NoDispatcherException e) {
            _log.debug("No custom dispatcher, using the default processing");
        }
        return dispatcher;
    }

    /**
     * Transforms the incoming request soap message using the dispatcher and return the transformed document. 
     * 
     * @param dispatcher - The dispatcher that should be used to transform the incoming data
     * @param soapEnv - Request message 
     * @return transformed data.
     * @throws InvalidInputFormatException
     */
    protected Document createDispatcherRequest(IDispatcher dispatcher,Document soapEnv) throws InvalidInputFormatException {
        Document payload = SoapTools.unwrapMessage(soapEnv);
        Document processedRequest = dispatcher.dispatchRequest(payload);
        Document wrappedRequest = SoapTools.wrapMessage(processedRequest);
        return wrappedRequest;
    }

    /**
     * Transforms the incoming response soap message using the dispatcher and return the transformed document.
     * 
     * @param dispatcher - The dispatcher that should be used to transform the incoming data
     * @param soapEnv - Response message
     * @return transformed data.
     * @throws InvalidInputFormatException
     */
    protected Document createDispatcherResponse(IDispatcher dispatcher,Document soapEnv) throws InvalidInputFormatException {
        Document unwrappedResponse = SoapTools.unwrapMessage(soapEnv);
        Document processedResponse = dispatcher
                .dispatchResponse(unwrappedResponse);
        Document responseDocument = SoapTools.wrapMessage(processedResponse);

        return responseDocument;
    }

}
