/**
 * Copyright (c) 2005-2008 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 *
 * $Id: TaskManagementServicesFacade.java 5440 2006-06-09 08:58:15Z imemruk $
 * $Log:$
 */

package org.intalio.tempo.workflow.task.xml;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.values.XmlValueOutOfRangeException;
import org.intalio.tempo.workflow.auth.ACL;
import org.intalio.tempo.workflow.auth.AuthIdentifierSet;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.task.TaskState;
import org.intalio.tempo.workflow.task.attachments.Attachment;
import org.intalio.tempo.workflow.task.attachments.AttachmentMetadata;
import org.intalio.tempo.workflow.task.traits.IChainableTask;
import org.intalio.tempo.workflow.task.traits.ICompleteReportingTask;
import org.intalio.tempo.workflow.task.traits.IInstanceBoundTask;
import org.intalio.tempo.workflow.task.traits.IProcessBoundTask;
import org.intalio.tempo.workflow.task.traits.ITaskWithAttachments;
import org.intalio.tempo.workflow.task.traits.ITaskWithCustomMetadata;
import org.intalio.tempo.workflow.task.traits.ITaskWithDeadline;
import org.intalio.tempo.workflow.task.traits.ITaskWithInput;
import org.intalio.tempo.workflow.task.traits.ITaskWithOutput;
import org.intalio.tempo.workflow.task.traits.ITaskWithPriority;
import org.intalio.tempo.workflow.task.traits.ITaskWithState;
import org.intalio.tempo.workflow.task.traits.InitTask;
import org.intalio.tempo.workflow.util.RequiredArgumentException;
import org.intalio.tempo.workflow.util.xml.InvalidInputFormatException;
import org.intalio.tempo.workflow.util.xml.XmlBeanUnmarshaller;
import org.intalio.tempo.workflow.util.xml.XsdDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.intalio.bpms.workflow.taskManagementServices20051109.Attachments;
import com.intalio.bpms.workflow.taskManagementServices20051109.CustomMetadataKeyValueType;
import com.intalio.bpms.workflow.taskManagementServices20051109.CustomMetadataType;
import com.intalio.bpms.workflow.taskManagementServices20051109.TaskMetadata;

/**
 * As noted by Alexis, we are now careful of using dispose properly throughout this class.
 * You can refer to this for more info:
 * <a href="http://xmlbeans.apache.org/docs/2.0.0/reference/org/apache/xmlbeans/XmlCursor.html#dispose%28%29">Dispose cursors</a>
 *
 */
public class TaskUnmarshaller extends XmlBeanUnmarshaller {

    private static final Logger _logger = LoggerFactory.getLogger(TaskUnmarshaller.class);
    private static final String[] actions = new String[] { "claim", "revoke", "save", "complete", "dismiss" };

    public TaskUnmarshaller() {
        super(TaskXMLConstants.TASK_NAMESPACE, TaskXMLConstants.TASK_NAMESPACE_PREFIX);
    }

    // for compatibility usage
    public Task unmarshalTaskFromMetadata(OMElement rootElement) throws InvalidInputFormatException {
            return unmarshalTaskFromMetadata(unmarshalTaskMetadata(rootElement));  
    }
    
    private TaskMetadata unmarshalTaskMetadata(OMElement rootElement) throws InvalidInputFormatException {
        try {
            XmlObject xmlObject = XmlObject.Factory.parse(rootElement.getXMLStreamReader());
            if (_logger.isDebugEnabled()) {
                _logger.debug(xmlObject.xmlText());
            }
            XmlCursor xmlCursor = xmlObject.newCursor();
            xmlCursor.toStartDoc();
            xmlCursor.toNextToken();
            TaskMetadata taskMetadata = com.intalio.bpms.workflow.taskManagementServices20051109.Task.Factory.newInstance().addNewMetadata();
            taskMetadata.set(xmlCursor.getObject());
            xmlCursor.dispose();
            return taskMetadata;
        } catch (InvalidInputFormatException e) {
            throw e;
        } catch (XmlException e) {
            throw new InvalidInputFormatException(e);
        }
    }

    private Task unmarshalTaskFromMetadata(TaskMetadata taskMetadata) throws XmlValueOutOfRangeException {
        if (taskMetadata == null) {
            throw new RequiredArgumentException("rootElement");
        }
        checkNS(taskMetadata);

        String taskID = taskMetadata.getTaskId();
        if (taskID == null) {
            throw new InvalidInputFormatException("No task id specified");
        }
        String taskStateStr = taskMetadata.getTaskState();
        String taskTypeStr = taskMetadata.getTaskType();
        String description = taskMetadata.getDescription();
        String processID = taskMetadata.getProcessId();
        String instanceID = taskMetadata.getInstanceId();
        AuthIdentifierSet userOwners = new AuthIdentifierSet(Arrays.asList(taskMetadata.getUserOwnerArray()));
        AuthIdentifierSet roleOwners = new AuthIdentifierSet(Arrays.asList(taskMetadata.getRoleOwnerArray()));

        Integer priority = null;
        if (taskMetadata.xgetPriority() != null && taskMetadata.xgetPriority().validate()) {
            priority = taskMetadata.getPriority();
        }

        String formURLStr = taskMetadata.getFormUrl();
        URI formURL = null;
        if (formURLStr != null) {
            try {
                formURL = new URI(formURLStr);
            } catch (URISyntaxException e) {
                throw new InvalidInputFormatException(e);
            }
        } else {
            throw new InvalidInputFormatException("No URL found for form");
        }

        String failureCode = taskMetadata.getFailureCode();
        String failureReason = taskMetadata.getFailureReason();
        expectElementValue(taskMetadata, "userProcessEndpoint");
        // TODO: these violate the WSDL! do something
        expectElementValue(taskMetadata, "userProcessNamespaceURI");
        String completeSOAPAction = taskMetadata.getUserProcessCompleteSOAPAction();
        Attachments attachmentsElement = taskMetadata.getAttachments();
        String isChainedBeforeStr = expectElementValue(taskMetadata, "isChainedBefore");
        String previousTaskID = expectElementValue(taskMetadata, "previousTaskId");
        CustomMetadataType customMetadataTypeElement = taskMetadata.getCustomMetadata();

        Class<? extends Task> taskClass = TaskTypeMapper.getTypeClassByName(taskTypeStr);
        Task resultTask = null;
        TaskState taskState = null;

        if (!ITaskWithState.class.isAssignableFrom(taskClass)) {
            forbidParameter(taskStateStr, "task state");
            forbidParameter(failureCode, "failure code");
            forbidParameter(failureReason, "failure reason");
        } else {
            try {
                taskState = (taskStateStr == null) ? TaskState.READY : TaskState.valueOf(taskStateStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                _logger.error("Error in unmarshalling task from metadata", e);
                throw new InvalidInputFormatException("Unknown task state: '" + taskStateStr + "'");
            }
        }
        if (IProcessBoundTask.class.isAssignableFrom(taskClass)) {
            if (taskMetadata.xgetProcessId() == null) {
                /*
                 * The following line will be commented to support versions of
                 * designer that do not generate the processId
                 */

                // throw new
                // InvalidInputFormatException("ProcessID not specified");
            }
        } else {
            forbidParameter(processID, "processID");
        }
        if (IInstanceBoundTask.class.isAssignableFrom(taskClass)) {
            if (taskMetadata.xgetInstanceId() == null) {
                /*
                 * The following line will be commented to support versions of
                 * designer that do not generate the InstanceId
                 */
                // throw new
                // InvalidInputFormatException("instanceID not specified");
            }
        } else {
            forbidParameter(instanceID, "instanceID");
        }
        if (ICompleteReportingTask.class.isAssignableFrom(taskClass)) {
            requireParameter(completeSOAPAction, "completion SOAPAction");
        } else {
            forbidParameter(completeSOAPAction, "completion SOAPAction");
        }
        if (!ITaskWithAttachments.class.isAssignableFrom(taskClass)) {
            forbidParameter(attachmentsElement, "task attachment(s)");
        }
        if (!IChainableTask.class.isAssignableFrom(taskClass)) {
            forbidParameter(isChainedBeforeStr, "is-chained-before flag");
            forbidParameter(previousTaskID, "previous chained task ID");
        }

        resultTask = TaskTypeMapper.getNewInstance(taskClass, taskID, formURL);

        resultTask.getUserOwners().addAll(userOwners);
        resultTask.getRoleOwners().addAll(roleOwners);

        resultTask.setDescription(description == null ? "" : description);

        try {
            Calendar creationDate = taskMetadata.getCreationDate();
            if (creationDate != null)
                resultTask.setCreationDate(creationDate.getTime());
            else
                resultTask.setCreationDate(new Date());
        } catch (XmlValueOutOfRangeException e) {
            resultTask.setCreationDate(new Date());
        }

        for (String action : actions) {
            ACL acl = readACL(taskMetadata, action);
            authorize(resultTask, action, acl);
        }

        if (ITaskWithState.class.isAssignableFrom(taskClass)) {
            ITaskWithState taskWithState = (ITaskWithState) resultTask;
            taskWithState.setState(taskState);
            if (taskWithState.getState().equals(TaskState.FAILED)) {
                requireParameter(failureCode, "failure code");

                taskWithState.setFailureCode(failureCode);
                taskWithState.setFailureReason(failureReason == null ? "" : failureReason);
            } else {
                forbidParameter(failureCode, "failure code");
                forbidParameter(failureReason, "failure reason");
            }
        }
        if (InitTask.class.isAssignableFrom(taskClass)) {
            InitTask task = (InitTask) resultTask;
            String uri1 = taskMetadata.getInitMessageNamespaceURI();
            if (uri1 != null)
                task.setInitMessageNamespaceURI(URI.create(uri1));
            String soap = taskMetadata.getInitOperationSOAPAction();
            if (soap != null)
                task.setInitOperationSOAPAction(soap);
            String uri2 = taskMetadata.getProcessEndpoint();
            if (uri2 != null)
                task.setProcessEndpoint(URI.create(uri2));
        }
        if (IProcessBoundTask.class.isAssignableFrom(taskClass)) {
            if (taskMetadata.xgetProcessId() != null && processID != null)
                ((IProcessBoundTask) resultTask).setProcessID(processID);
        }
        if (IInstanceBoundTask.class.isAssignableFrom(taskClass)) {
            if (taskMetadata.xgetInstanceId() != null && taskMetadata.getInstanceId() != null) {
                ((IInstanceBoundTask) resultTask).setInstanceId(instanceID);
            }
        }

        if (ICompleteReportingTask.class.isAssignableFrom(taskClass)) {
            ((ICompleteReportingTask) resultTask).setCompleteSOAPAction(completeSOAPAction);
        }
        if(ITaskWithCustomMetadata.class.isAssignableFrom(taskClass)){
            ITaskWithCustomMetadata taskWithCustomMetadata = (ITaskWithCustomMetadata)resultTask;
            Map<String, String> customMetadata = new HashMap<String, String>();
            if(customMetadataTypeElement !=null){
                for(int i=0; i < customMetadataTypeElement.sizeOfCustomMetadataKeyValueArray(); i ++){
                    CustomMetadataKeyValueType customMetadataKeyValueTypeElement = customMetadataTypeElement.getCustomMetadataKeyValueArray(i);
                    if(customMetadataKeyValueTypeElement !=null){
                        String key = customMetadataKeyValueTypeElement.getKey();
                        String value = customMetadataKeyValueTypeElement.getValue();
                        customMetadata.put(key, value);
                    }
                }
            }
            taskWithCustomMetadata.setCustomMetadata(customMetadata);
        }
        
        
        if (ITaskWithAttachments.class.isAssignableFrom(taskClass)) {
            ITaskWithAttachments taskWithAttachments = (ITaskWithAttachments) resultTask;

            if (attachmentsElement != null) {
                for (int i = 0; i < attachmentsElement.sizeOfAttachmentArray(); i++) {
                    com.intalio.bpms.workflow.taskManagementServices20051109.Attachment attachmentElement = attachmentsElement.getAttachmentArray(i);

                    if (attachmentElement != null) {
                        // The following line has been added to handle the case
                        // where an attachment element is present
                        // but do not contain any data: no title,
                        // nodescription , ect...
                        // The reason why is this is added is to
                        // handle the initial initialization on
                        // Designer
                        // In which designer generates by default an
                        // attachment element as a part of the
                        // initialization of the message
                        // even if no attachment is used

                        // TODO: When Designer and Server will
                        // support "lazy initialization", this line
                        // can be omitted

                        XmlCursor attCursor = attachmentElement.newCursor();
                        try {
                            if (attCursor.getTextValue().trim().length() != 0) {
                                com.intalio.bpms.workflow.taskManagementServices20051109.AttachmentMetadata attachmentMetadata = attachmentElement
                                                .getAttachmentMetadata();
                                AttachmentMetadata metadata = new AttachmentMetadata();
                                String mimeType = attachmentMetadata.getMimeType();
                                if (mimeType != null) {
                                    metadata.setMimeType(mimeType);
                                }
                                String fileName = attachmentMetadata.getFileName();
                                if (fileName != null)
                                    metadata.setFileName(fileName);
                                String title = attachmentMetadata.getTitle();
                                if (title != null)
                                    metadata.setTitle(title);
                                String description2 = attachmentMetadata.getDescription();
                                if (description2 != null)
                                    metadata.setDescription(description2);

                                try {
                                    Calendar cal = attachmentMetadata.getCreationDate();
                                    if ((cal != null)) {
                                        metadata.setCreationDate(new XsdDateTime(cal.toString()).getTime());
                                    }
                                } catch (Exception e) {
                                    _logger.warn("Error in unmarshalling creation date in attachment from metadata");
                                    metadata.setCreationDate(new Date());
                                }

                                String payloadURLStr = attachmentElement.getPayloadUrl();
                                URL payloadURL;
                                try {
                                    payloadURL = new URL(payloadURLStr);
                                } catch (MalformedURLException e) {
                                    throw new InvalidInputFormatException(e);
                                }

                                Attachment attachment = new Attachment(metadata, payloadURL);
                                taskWithAttachments.addAttachment(attachment);
                            }
                        } finally {
                            attCursor.dispose();
                        }
                    }
                }
            }
        }
        if (IChainableTask.class.isAssignableFrom(taskClass)) {
            IChainableTask chainableTask = (IChainableTask) resultTask;
            if (isChainedBeforeStr != null) {
                if ("1".equals(isChainedBeforeStr) || "true".equals(isChainedBeforeStr)) {
                    if (previousTaskID == null) {
                        throw new InvalidInputFormatException("tms:previousTaskId is required " + "if tms:isChainedBefore is true");
                    }
                    chainableTask.setPreviousTaskID(previousTaskID);
                    chainableTask.setChainedBefore(true);
                } else {
                    if ((previousTaskID != null) && (!"".equals(previousTaskID))) {
                        throw new InvalidInputFormatException("tms:previousTaskId must be empty or not present " + "if tms:isChainedBefore is false");
                    }
                }
            } else {
                if (previousTaskID != null) {
                    throw new InvalidInputFormatException("tms:isChainedBefore is required " + "if tms:previousTaskId is present");
                }
            }
        }

        // / the following is added to support task deadlines
        if (ITaskWithDeadline.class.isAssignableFrom(taskClass)) {
            ITaskWithDeadline taskWithDeadline = (ITaskWithDeadline) resultTask;
            try {
                Calendar deadline = taskMetadata.getDeadline();
                if (deadline != null) {
                    taskWithDeadline.setDeadline(deadline.getTime());
                } else {
                    // do nothing, deadline is null by default
                }
            } catch (XmlValueOutOfRangeException e) {
                // do nothing, not a valid xml date
            }

        }

        // the following is added to support task priorities
        if (ITaskWithPriority.class.isAssignableFrom(taskClass)) {
            ITaskWithPriority taskWithDeadline = (ITaskWithPriority) resultTask;
            taskWithDeadline.setPriority(priority);
        }

        return resultTask;
    }

    private void authorize(Task resultTask, String action, ACL acl) {
        for (String user : acl.getUserOwners()) {
            resultTask.authorizeActionForUser(action, user);
        }
        for (String role : acl.getRoleOwners()) {
            resultTask.authorizeActionForRole(action, role);
        }
    }

    private ACL readACL(XmlObject root, String action) {
        ACL acl = new ACL();
        XmlObject el = expectElement(root, action + "Action");
        if (el != null) {
            acl.getUserOwners().addAll(expectAuthIdentifiers(el, "user"));
            acl.getRoleOwners().addAll(expectAuthIdentifiers(el, "role"));
        }
        return acl;
    }

    private void checkTaskPayload(XmlObject containerElement) throws InvalidInputFormatException {
        if (containerElement == null) {
            throw new RequiredArgumentException("containerElement");
        }
        XmlCursor payloadCursor = containerElement.newCursor();
        try {
            if (!payloadCursor.toFirstChild()) {
                throw new InvalidInputFormatException("Payload container element must contain exactly one child element");
            }
            if (payloadCursor.toNextSibling()) {
                throw new InvalidInputFormatException("Task payload must consist of exactly one element.");
            }
        } finally {
            payloadCursor.dispose();
        }
    }

    public XmlObject unmarshalTaskInput(XmlObject inputContainerElement) throws InvalidInputFormatException {
        checkTaskPayload(inputContainerElement);
        return inputContainerElement;
    }

    // for compatibility usage
    private Document unmarshalTaskPayload(OMElement containerElement) throws InvalidInputFormatException {
        if (containerElement == null) {
            throw new RequiredArgumentException("containerElement");
        }
        Iterator<OMElement> it = containerElement.getChildElements();
        if (!it.hasNext()) {
            throw new InvalidInputFormatException("Payload container element must contain exactly one child element");
        }
        Document result = null;
        OMElement firstPayloadElement = it.next();
        if (it.hasNext()) {
            throw new InvalidInputFormatException("Task payload must consist of exactly one element.");
        } else {
            result = new XmlTooling().convertOMToDOM(firstPayloadElement);
        }
        return result;
    }

    // for compatibility usage
    public Document unmarshalTaskOutput(OMElement outputContainerElement) throws InvalidInputFormatException {
        return unmarshalTaskPayload(outputContainerElement);
    }

    public XmlObject unmarshalTaskOutput(XmlObject outputContainerElement) throws InvalidInputFormatException {
        checkTaskPayload(outputContainerElement);
        return outputContainerElement;
    }

    public Task unmarshalFullTask(OMElement rootElement) throws InvalidInputFormatException {
        try {
//            XmlObject xmlObject = XmlObject.Factory.parse(rootElement.getXMLStreamReader());
            XmlObject xmlObject = XmlObject.Factory.parse(rootElement.getXMLStreamReader(), new XmlOptions().setLoadStripWhitespace());
            return unmarshalFullTask(xmlObject);
        } catch (XmlException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    public TaskMetadata unmarshalPartialTask(OMElement rootElement) throws InvalidInputFormatException {
        try {
            XmlObject xmlObject = XmlObject.Factory.parse(rootElement.getXMLStreamReader());
            requireElement(xmlObject, "metadata");

            com.intalio.bpms.workflow.taskManagementServices20051109.Task taskElement = com.intalio.bpms.workflow.taskManagementServices20051109.Task.Factory
                            .newInstance();

            TaskMetadata metadataElement = taskElement.addNewMetadata();
            metadataElement.set(expectElement(xmlObject, "metadata"));
            return metadataElement;
        } catch (XmlException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    public TaskMetadata unmarshalPartialTask2(OMElement rootElement) throws InvalidInputFormatException {
    	try {
        	XmlObject xmlObject = XmlObject.Factory.parse(rootElement.getXMLStreamReader());
        	com.intalio.bpms.workflow.taskManagementServices20051109.Task taskElement = com.intalio.bpms.workflow.taskManagementServices20051109.Task.Factory.newInstance();
            TaskMetadata metadata = taskElement.addNewMetadata();
            metadata.set(new TaskUnmarshaller().expectElement(xmlObject, "taskMetadata"));
            return metadata;
        } catch (XmlException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private Task unmarshalFullTask(XmlObject rootElement) throws InvalidInputFormatException {
        if (rootElement == null) {
            throw new RequiredArgumentException("rootElement");
        }
        Task resultTask = null;

        requireElement(rootElement, "metadata");

        com.intalio.bpms.workflow.taskManagementServices20051109.Task taskElement = com.intalio.bpms.workflow.taskManagementServices20051109.Task.Factory
                        .newInstance();

        TaskMetadata metadataElement = taskElement.addNewMetadata();
        metadataElement.set(expectElement(rootElement, "metadata"));

        com.intalio.bpms.workflow.taskManagementServices20051109.TaskData inputElement = null;
        XmlObject xmlInput = expectElement(rootElement, "input");
        if (xmlInput != null) {
            inputElement = taskElement.addNewInput();
            inputElement.set(xmlInput);
        }

        com.intalio.bpms.workflow.taskManagementServices20051109.TaskData outputElement = null;
        XmlObject xmlOutput = expectElement(rootElement, "output");
        if (xmlOutput != null) {
            outputElement = taskElement.addNewOutput();
            outputElement.set(xmlOutput);
        }

        resultTask = unmarshalTaskFromMetadata(metadataElement);
        if (resultTask instanceof ITaskWithInput) {
            requireParameter(inputElement, "task input");
            XmlObject input = unmarshalTaskInput(inputElement);
            ((ITaskWithInput) resultTask).setInput(serializeXMLObject(input));
        } else {
            forbidParameter(inputElement, "task input");
        }
        if ((resultTask instanceof ITaskWithOutput) && outputElement != null) {
            requireParameter(outputElement, "task output");
            XmlObject output = unmarshalTaskOutput(outputElement);
            ((ITaskWithOutput) resultTask).setOutput(serializeXMLObject(output));
        } else {
            forbidParameter(outputElement, "task output");
        }
        
        if(resultTask instanceof ITaskWithCustomMetadata){     
            NodeList list= xmlInput.getDomNode().getFirstChild().getChildNodes();
            Map<String, String> customMetadata = new HashMap<String, String>();
            int elements = list.getLength();
            for (int j = 0 ; j < elements ; j++) {
//                System.out.println("NodeName : " + list.item(j).getNodeName() + " NodeValue : " + list.item(j).getFirstChild().getNodeValue());
                if(list.item(j).getFirstChild() != null)
                    customMetadata.put(getTextNodeName(list.item(j)), getTextNodeValue(list.item(j)));
            }
            ((ITaskWithCustomMetadata) resultTask).setCustomMetadata(customMetadata);
        }

        return resultTask;
    }

    private String getTextNodeName(Node node) {
        {
            if(node.getChildNodes().getLength() > 0)
            {
                if(node.getFirstChild().getNodeType() != Node.TEXT_NODE) {
                    return getTextNodeName(node.getFirstChild());
                }
        }
        }
        return node.getNodeName().toLowerCase();
    }

    private String getTextNodeValue(Node node) {
         if (node.getChildNodes().getLength() > 0) {
             return getTextNodeValue(node.getFirstChild());
         } 
         return node.getNodeValue();
    }
    
    /**
     * Xmlbeans is wrapping the content with some <code>xml-fragment</code> tag
     * Position the cursor on the data we really want.
     */
    private String serializeXMLObject(XmlObject xmlObject) {
        XmlCursor cursor = xmlObject.newCursor();
        try {
            cursor.toFirstChild();
            XmlOptions opts = new XmlOptions();
            opts.setSaveNoXmlDecl();
            opts.setLoadReplaceDocumentElement(cursor.getName());
            return cursor.xmlText(opts);
        } finally {
            cursor.dispose();
        }

    }

    private void checkNS(XmlObject containerElement) throws InvalidInputFormatException {
        if (containerElement == null) {
            throw new RequiredArgumentException("containerElement");
        }
        XmlCursor payloadCursor = containerElement.newCursor();
        try {
            if (!payloadCursor.toFirstChild()) {
                throw new InvalidInputFormatException("No taskmetadata element");
            }
            QName qName = payloadCursor.getName();
            if (qName == null || qName.getNamespaceURI() == null || qName.getNamespaceURI().trim().length() == 0) {
                throw new InvalidInputFormatException("No namespace defined");
            }
        } finally {
            payloadCursor.dispose();
        }
    }

}