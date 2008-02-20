package org.intalio.tempo.workflow.task.traits;

import java.net.URI;

public interface InitTask {
    URI getProcessEndpoint();
    void setProcessEndpoint(URI endpoint);
    
    URI getInitMessageNamespaceURI();
    void setInitMessageNamespaceURI(URI initMessageNamespaceURI);
    
    String getInitOperationSOAPAction();
    void setInitOperationSOAPAction(String initOperationSOAPAction);

}
