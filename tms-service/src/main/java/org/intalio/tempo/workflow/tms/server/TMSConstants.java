package org.intalio.tempo.workflow.tms.server;

import javax.xml.namespace.QName;

import org.intalio.tempo.workflow.task.xml.TaskXMLConstants;

public interface TMSConstants {
    public static final QName INVALID_INPUT_FORMAT = new QName(TaskXMLConstants.TASK_NAMESPACE, "invalidInputMessageFault", TaskXMLConstants.TASK_NAMESPACE_PREFIX);
    public static final QName UNAVAILABLE_TASK = new QName(TaskXMLConstants.TASK_NAMESPACE, "unavailableTaskFault", TaskXMLConstants.TASK_NAMESPACE_PREFIX);
    public static final QName UNAVAILABLE_ATTACHMENT = new QName(TaskXMLConstants.TASK_NAMESPACE, "unavailableAttachmentFault", TaskXMLConstants.TASK_NAMESPACE_PREFIX);
    public static final QName INVALID_TOKEN = new QName(TaskXMLConstants.TASK_NAMESPACE, "invalidParticipantTokenFault", TaskXMLConstants.TASK_NAMESPACE_PREFIX);
    public static final QName ACCESS_DENIED = new QName(TaskXMLConstants.TASK_NAMESPACE, "accessDeniedFault", TaskXMLConstants.TASK_NAMESPACE_PREFIX);
}
