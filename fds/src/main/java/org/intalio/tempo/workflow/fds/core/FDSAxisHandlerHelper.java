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

public class FDSAxisHandlerHelper {

    private static Logger _log = LoggerFactory.getLogger(FDSAxisHandlerHelper.class);
    
    public enum Direction {WF2UP, UP2WF}

    private WorkflowProcessesMessageConvertor _wf2up = null;
    private UserProcessMessageConvertor _up2wf = null;    
    private IDispatcher _dispatcher = null;
    private String _soapAction = null;
    private String _targetEPR = null;
    private Direction _requestDirection;

    public FDSAxisHandlerHelper(boolean changeCallbackAddress) {
        _wf2up = new WorkflowProcessesMessageConvertor();
        _up2wf = new UserProcessMessageConvertor();
        _up2wf.setChangeCallbackAddress(changeCallbackAddress);
    }

    
    public Document processOutMessage(Document soapEnv, String soapAction, String toAddress) throws InvalidInputFormatException, AxisFault, MessageFormatException {
        _soapAction = soapAction;
        Document result = null;
        
        if (_log.isDebugEnabled()) {
			_log.debug("Outgoing document before conversion: {}", soapEnv.toString());
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
			_log.debug("Outgoing document before conversion: {}", result.toString());
		}

        return result;
    }

    public Document processInMessage(Document soapEnv, String soapAction,String toAddress) throws MessageFormatException, AxisFault, InvalidInputFormatException {
        _soapAction = soapAction;
        Document result = null;

        if (_log.isDebugEnabled()) {
			_log.debug("Incoming document before conversion: {}", soapEnv.toString());
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
			_log.debug("Incoming document after conversion: {}", result.toString());
		}
		
		return result;
    }


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


	private Document convertWF2UP(Document soapEnv) throws MessageFormatException {
		_wf2up.convertMessage(soapEnv, _up2wf.getUserProcessNamespaceUri());
		
		if (_wf2up.getSoapAction() != null) {
		    _soapAction = _wf2up.getSoapAction();
		}

		// SOAP Action should always be quoted (WS-Interop)
		if (_soapAction == null || _soapAction.length() == 0) {
		    _soapAction = "\"\"";
		} else if (_soapAction.charAt(0) != '\"') {
		    _soapAction = "\"" + _soapAction + "\"";
		}

		_targetEPR = _wf2up.getUserProcessEndpoint();
		
		return soapEnv;
	}

    
    public String getSoapAction(){
        return _soapAction;
    }
    
    public String getTargetEPR(){
        return _targetEPR;        
    }
    
    protected Document getPayloadFromEnvelope(Document soapEnvelope) throws InvalidInputFormatException {
        Document pureRequest = SoapTools.unwrapMessage(soapEnvelope);
        return pureRequest;
    }

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

    protected Document createDispatcherRequest(IDispatcher dispatcher,Document soapEnv) throws InvalidInputFormatException {
        Document payload = SoapTools.unwrapMessage(soapEnv);
        Document processedRequest = dispatcher.dispatchRequest(payload);
        Document wrappedRequest = SoapTools.wrapMessage(processedRequest);
        return wrappedRequest;
    }

    protected Document createDispatcherResponse(IDispatcher dispatcher,Document soapEnv) throws InvalidInputFormatException {
        Document unwrappedResponse = SoapTools.unwrapMessage(soapEnv);
        Document processedResponse = dispatcher
                .dispatchResponse(unwrappedResponse);
        Document responseDocument = SoapTools.wrapMessage(processedResponse);

        return responseDocument;
    }

}
