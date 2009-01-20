package org.intalio.tempo.workflow.tmsb4p.server;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axis2.AxisFault;
import org.intalio.tempo.deployment.utils.DeploymentServiceRegister;
import org.intalio.tempo.workflow.auth.AuthException;
import org.intalio.tempo.workflow.task.xml.TaskXMLConstants;
import org.intalio.tempo.workflow.tms.AccessDeniedException;
import org.intalio.tempo.workflow.tms.TMSException;
import org.intalio.tempo.workflow.tms.UnavailableAttachmentException;
import org.intalio.tempo.workflow.tms.UnavailableTaskException;
import org.intalio.tempo.workflow.tms.server.PIPAComponentManager;
import org.intalio.tempo.workflow.tms.server.TMSConstants;
import org.intalio.tempo.workflow.util.xml.InvalidInputFormatException;
import org.intalio.tempo.workflow.util.xml.OMMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TMSRequestProcessor {   
    final static Logger _logger = LoggerFactory.getLogger(TMSRequestProcessor.class);
    private ITMSServer _server;
    private static final OMFactory OM_FACTORY = OMAbstractFactory.getOMFactory();
    
    /**
     * dumy function
     * @return
     */
    public OMElement marshallResponse(){
        return new TMSResponseMarshaller(OM_FACTORY) {
            public OMElement createOkResponse() {
                OMElement okResponse = createElement("okResponse");
                return okResponse;
            }
        }.createOkResponse();
    }
    
    public OMElement createOkResponse(){
        return new TMSResponseMarshaller(OM_FACTORY) {
            public OMElement createOkResponse() {
                OMElement okResponse = createElement("okResponse");
                return okResponse;
            }
        }.createOkResponse();
    }
    private AxisFault makeFault(Exception e) {
        if (e instanceof TMSException) {
            if (_logger.isDebugEnabled())
                _logger.debug(e.getMessage(), e);
            OMElement response = null;
            if (e instanceof InvalidInputFormatException)
                response = OM_FACTORY.createOMElement(TMSConstants.INVALID_INPUT_FORMAT);
            else if (e instanceof AccessDeniedException)
                response = OM_FACTORY.createOMElement(TMSConstants.ACCESS_DENIED);
            else if (e instanceof UnavailableTaskException)
                response = OM_FACTORY.createOMElement(TMSConstants.UNAVAILABLE_TASK);
            else if (e instanceof UnavailableAttachmentException)
                response = OM_FACTORY.createOMElement(TMSConstants.UNAVAILABLE_ATTACHMENT);
            else if (e instanceof AuthException)
                response = OM_FACTORY.createOMElement(TMSConstants.INVALID_TOKEN);

            else
                return AxisFault.makeFault(e);

            response.setText(e.getMessage());
            AxisFault axisFault = new AxisFault(e.getMessage(), e);
            axisFault.setDetail(response);
            return axisFault;
        } else if (e instanceof AxisFault) {
            _logger.error(e.getMessage(), e);
            return (AxisFault) e;
        } else {
            _logger.error(e.getMessage(), e);
            return AxisFault.makeFault(e);
        }
    }
    public void setServer(ITMSServer server) {
        _logger.debug("TMSRequestProcessor.setServer:" + server.getClass().getSimpleName());
        _server = server;
    }
   
    public OMElement create(OMElement requestElement) throws AxisFault{
        try{
        // unmarshall request
        
        
        // process request
      // _server.create(task, participantToken);
        
        // marshall response        
        return createOkResponse();
    } catch (Exception e) {
        throw makeFault(e);
    }
    
    }
    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }
    private abstract class TMSResponseMarshaller extends OMMarshaller {
        public TMSResponseMarshaller(OMFactory omFactory) {
            super(omFactory, omFactory.createOMNamespace("http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/", "tmsb4p"));
        }
    }
}
