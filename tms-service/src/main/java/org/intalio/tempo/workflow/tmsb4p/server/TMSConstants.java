package org.intalio.tempo.workflow.tmsb4p.server;

import javax.xml.namespace.QName;

import org.intalio.tempo.workflow.task.xml.TaskXMLConstants;

public interface TMSConstants {
    // b4p fault
    public static final String TASK_NAMESPACE = "http://www.intalio.com/WS-HT/api/wsdl";
    public static final String TASK_NAMESPACE_PREFIX = "tns";
    public static final QName ILLEGALARGUMENTFAULT = new QName(TMSConstants.TASK_NAMESPACE, "illegalArgumentFault", TMSConstants.TASK_NAMESPACE_PREFIX);
    public static final QName ILLEGALACCESSFAULT = new QName(TMSConstants.TASK_NAMESPACE, "illegalAccessFault", TMSConstants.TASK_NAMESPACE_PREFIX);
    public static final QName ILLEGALOPERATIONFAULT = new QName(TMSConstants.TASK_NAMESPACE, "illegalOperationFault", TMSConstants.TASK_NAMESPACE_PREFIX);
    public static final QName ILLEGALSTATEFAULT = new QName(TMSConstants.TASK_NAMESPACE, "illegalStateFault", TMSConstants.TASK_NAMESPACE_PREFIX);
    public static final QName RECIPIENTNOTALLOWED = new QName(TMSConstants.TASK_NAMESPACE, "recipientNotAllowed", TMSConstants.TASK_NAMESPACE_PREFIX);
   
    
    
}
