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

public class FDSAxisHanderHelper {

    private static Logger _log = LoggerFactory.getLogger(FDSAxisHanderHelper.class);

    private WorkflowProcessesMessageConvertor _wf2up = null;
    private UserProcessMessageConvertor _up2wf = null;    
    private IDispatcher _dispatcher = null;
    private String _soapAction = null;
    private String _targetEPR = null;

    public FDSAxisHanderHelper(boolean changeCallbackAddress) {
        _log.debug("Constructor called");
        _wf2up = new WorkflowProcessesMessageConvertor();
        _up2wf = new UserProcessMessageConvertor();
        _up2wf.setChangeCallbackAddress(changeCallbackAddress);
    }

    
    public Document processOutMessage(Document soapEnv, String soapAction) throws InvalidInputFormatException, AxisFault, MessageFormatException {
        Document responseDocument = null;

        _soapAction = soapAction;

        String rootElementName = getRootElementName(soapEnv);
        
        IDispatcher dispatcher = getDispatcher(rootElementName);
        
        if (dispatcher != null) {
            try {
                Document dispatcherReq = createDispatcherRequest(dispatcher,
                        soapEnv);
                _targetEPR = dispatcher.getTargetEndpoint();
                _soapAction = dispatcher.getTargetSoapAction();
        
                responseDocument = dispatcherReq;
            } catch (InvalidInputFormatException e) {
                _log.error("Error converting user process request", e);
                throw new RuntimeException(e);
            }
        } else {
            _log.debug("Before Conversion: "+soapEnv.asXML());
            _up2wf.convertMessage(soapEnv);
            _log.debug("After Conversion: "+soapEnv.asXML());
        
            if (_up2wf.getSoapAction() != null) {
                _soapAction = _up2wf.getSoapAction();
            }
            
            FormDispatcherConfiguration _config = FormDispatcherConfiguration.getInstance();            
        
            _targetEPR = _config.getPxeBaseUrl()
                    + _config.getWorkflowProcessesRelativeUrl();
        
            responseDocument = soapEnv;
        }
        
        return responseDocument;
    }

    public Document processInMessage(Document soapEnv, String soapAction) throws MessageFormatException {
        Document responseDocument = null;
    
        _soapAction = soapAction;
        _targetEPR = null;
    
        if (_dispatcher != null) {
            try {
                responseDocument = createDispatcherResponse(_dispatcher,
                        soapEnv);
            } catch (InvalidInputFormatException e) {
                _log.error("Error converting user process request", e);
                throw new RuntimeException(e);
            }
        } else {
            _log.debug("Before Conversion: "+soapEnv.asXML());
            _wf2up.convertMessage(soapEnv, _up2wf.getUserProcessNamespaceUri());
            _log.debug("After Conversion: "+soapEnv.asXML());
            responseDocument = soapEnv;
        }
    
        return responseDocument;    
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

    protected String getRootElementName(Document soapEnvelope) throws InvalidInputFormatException {
        Document payload = getPayloadFromEnvelope(soapEnvelope);
        Element rootElement = payload.getRootElement();
        String rootElementName = rootElement.getName();

        return rootElementName;
    }

    protected IDispatcher getDispatcher(String elementName) {
        if (_dispatcher != null) {
            return _dispatcher;
        }

        _dispatcher = createDispatcher(elementName);

        return _dispatcher;
    }

    protected IDispatcher createDispatcher(String elementName) {
        IDispatcher dispatcher = null;

        try {
            dispatcher = Dispatchers.createDispatcher(elementName);
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
