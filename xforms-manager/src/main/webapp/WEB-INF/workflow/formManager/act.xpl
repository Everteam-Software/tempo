<?xml version="1.0" encoding="UTF-8"?>
<!--
* Copyright (c) 2005-2008 Intalio inc.
*
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Intalio inc. - initial API and implementation
-->
        <!--
            Description: This page implements the Form Manager.
            This page is typically called by the task list that provides task meta data in the HTTP request.
            It retrieves the task input from the Task Management Services WSDL API,
            dynamically loads the XForms form associated with the task
            and provides the controls that support all task actions.
            Author: Jacques-Alexandre Gerber [gerber@intalio.com]
            Creation Date: October, 10th, 2005
            Copyright Intalio,Inc. All rights reserved.
        -->
<p:config xmlns:p="http://www.orbeon.com/oxf/pipeline"
          xmlns:oxf="http://www.orbeon.com/oxf/processors"
          xmlns:xforms="http://www.w3.org/2002/xforms"
          xmlns:xxforms="http://orbeon.org/oxf/xml/xforms"
          xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
          xmlns:xhtml="http://www.w3.org/1999/xhtml"
          xmlns:delegation="http://orbeon.org/oxf/xml/delegation"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xmlns:xs="http://www.w3.org/2001/XMLSchema"
          xmlns:tms="http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/"
          xmlns:ev="http://www.w3.org/2001/xml-events">

    <!-- name must be data because it's passed from model4task.xpl  -->
    <p:param name="data" type="input"/>
    <!-- name must be data because it's called by the page-flow  -->
    <p:param name="data" type="output"/>

    <!-- Maps input from the HTTP request as defined in taskIdFromTaskList.xml to URL-Generator input -->
    <p:processor name="oxf:xslt">
        <p:input name="data" href="#data"/>
        <p:input name="config">
            <config xsl:version="2.0">
                <url>
                    <xsl:value-of select="/task/url"/>
                </url>
                <content-type>application/xml</content-type>
                <handle-xinclude>false</handle-xinclude>
            </config>
        </p:input>
        <p:output name="data" id="urlGeneratorConfig"/>
    </p:processor>

    <!-- Dynamically loads the form defined at the given URL that is previously retrieved from the HTTP request -->
    <p:processor name="oxf:url-generator">
        <p:input name="config" href="#urlGeneratorConfig"/>
        <p:output name="data" id="fetch-form"/>
    </p:processor>

    <p:processor name="oxf:exception-catcher">
        <p:input name="data" href="#fetch-form"/>
        <p:output name="data" id="form"/>
    </p:processor>

    <p:choose href="#form">
        <p:when test="/exceptions">
            <p:processor name="oxf:xslt">
                <p:input name="data" href="#data"/>
                <p:input name="config" href="oxf:/config/formNotFound.xml"/>
                <p:output name="data" ref="data"/>
            </p:processor>
        </p:when>
        <p:otherwise>
            <p:processor name="oxf:xslt">
                <p:input name="data" href="#data"/>
                <p:input name="config">
                    <delegation:execute service="tms" operation="getTaskRequest" xsl:version="2.0">
                        <tms:taskId>
                            <xsl:value-of select="/task/id"/>
                        </tms:taskId>
                        <tms:participantToken>
                            <xsl:value-of select="/task/token"/>
                        </tms:participantToken>
                    </delegation:execute>
                </p:input>
                <p:output name="data" id="getTaskRequest"/>
            </p:processor>

            <!-- Replace this Web Service call with actual GetTask Web Service -->
            <!-- Invokes the web service -->
            <p:processor name="oxf:delegation">
				<p:input name="interface" href="oxf:/config/services.xml"/>
                <p:input name="call" href="#getTaskRequest"/>
                <p:output name="data" id="tms-output"/>
            </p:processor>

            <p:processor name="oxf:exception-catcher">
                <p:input name="data" href="#tms-output"/>
                <p:output name="data" id="getTaskResponse"/>
            </p:processor>

            <p:choose href="#getTaskResponse">
                <p:when test="/exceptions">
                    <p:processor name="oxf:pipeline">
                        <p:input name="config" href="exception-handler.xpl"/>
                        <p:input name="data" href="#getTaskResponse"/>
                        <p:input name="ws-request" href="#getTaskRequest"/>
                        <p:input name="header">
                            <b>GetTaskRequest</b>
                        </p:input>
                        <p:output name="data" ref="data"/>
                    </p:processor>
                </p:when>
            <p:otherwise>
            <!-- Wraps the form with everything that is required to handle task actions -->
            <p:processor name="oxf:xslt">
                <p:input name="data" href="#form"/>
                <p:input name="getTaskResponse" href="#getTaskResponse"/>
                <p:input name="xpl-input" href="#data"/>
                <p:input name="config">
                    <xsl:stylesheet version="2.0">
                        <xsl:import href="oxf:/oxf/xslt/utils/copy.xsl"/>

                        <xsl:template xmlns:xhtml="http://www.w3.org/1999/xhtml" match="xhtml:head">
                            <xsl:copy>
                                <xsl:apply-templates select="@*|*"/>
                                <xhtml:link type="text/css" rel="stylesheet" href="/config/intalioForms.css"/>
                            </xsl:copy>
                        </xsl:template>

                        <xsl:template match="xforms:model[xforms:instance/@id='taskoutput']">
                            <xsl:copy>
                                <xsl:apply-templates select="@*|*"/>

                                <!-- Adding instance for Attachments data handling -->
                                <xforms:instance id="taskAttachments">
                                    <attachments>
                                        <xsl:attribute name="taskId">
                                            <xsl:value-of select="doc('input:xpl-input')/task/id"/>
                                        </xsl:attribute>
                                        <xsl:attribute name="formURL">
                                            <xsl:value-of select="doc('input:xpl-input')/task/url"/>
                                        </xsl:attribute>
                                        <xsl:attribute name="participantToken">
                                            <xsl:value-of select="doc('input:xpl-input')/task/token"/>
                                        </xsl:attribute>
                                        <xsl:attribute name="user">
                                            <xsl:value-of select="doc('input:xpl-input')/task/user"/>
                                        </xsl:attribute>

						                <xsl:for-each select="doc('input:getTaskResponse')//tms:attachments/tms:attachment">
						                    <attachment>
						                        <mime-type>
						                            <xsl:value-of select="tms:attachmentMetadata/tms:mimeType"/>
						                        </mime-type>
						                        <title>
						                            <xsl:value-of select="tms:attachmentMetadata/tms:title"/>
						                        </title>
						                        <hint>
						                            <xsl:value-of select="tms:attachmentMetadata/tms:description"/>
						                        </hint>
						                        <create-date>
						                            <xsl:value-of select="tms:attachmentMetadata/tms:creationDate"/>
						                        </create-date>
						                        <url>
						                            <xsl:value-of select="tms:payloadUrl"/>
						                        </url>
						                    </attachment>
						                </xsl:for-each>                                        

                                        <new title="" content="file">
                                            <file filename="" mediatype="" size="" attachFile=""/>
                                            <plaintext attachText="">type your text here</plaintext>
                                        </new>
                                        <delete/>
                                        <owners>
                                            <xsl:for-each select="doc('input:getTaskResponse')//tms:userOwner">
                                                <userOwner>
                                                    <xsl:value-of select="."/>
                                                </userOwner>
                                            </xsl:for-each>
                                            <xsl:for-each select="doc('input:getTaskResponse')//tms:roleOwner">
                                                <roleOwner>
                                                    <xsl:value-of select="."/>
                                                </roleOwner>
                                            </xsl:for-each>
                                        </owners>
                                        <action>
											<xsl:choose>
												<xsl:when test="doc('input:getTaskResponse')//tms:attachments/tms:attachment/*">show</xsl:when>
												<xsl:otherwise>hide</xsl:otherwise>
											</xsl:choose>
										</action>
                                    </attachments>
                                </xforms:instance>

                                <!-- To minimize data exchange following defines relevancy rules -->
                                <xforms:bind nodeset="instance('taskAttachments')/attachment"
                                             relevant="instance('taskAttachments')/action = 'show'"/>
                                <xforms:bind nodeset="instance('taskAttachments')/delete"
                                             relevant="instance('taskAttachments')/action = 'delete'"/>
                                <xforms:bind nodeset="instance('taskAttachments')/new"
                                             relevant="instance('taskAttachments')/action = 'new' or instance('taskAttachments')/action = 'show'"/>
                                <xforms:bind nodeset="instance('taskAttachments')/new/file" xsi:type="xs:anyURI"
                                             relevant="instance('taskAttachments')/new/@content = 'file'"/>
                                <xforms:bind nodeset="instance('taskAttachments')/new/plaintext" xsi:type="xs:string"
                                             relevant="instance('taskAttachments')/new/@content = 'text'"/>
                                <xforms:bind nodeset="instance('taskAttachments')/new/file/@attachFile"
                                             relevant="instance('taskAttachments')/new/@content = 'file'"/>
                                <xforms:bind nodeset="instance('taskAttachments')/new/plaintext/@attachText"
                                             relevant="instance('taskAttachments')/new/@content = 'text'"/>

                                <!-- Defines visibility of Attachments section -->
                                <xforms:bind nodeset="instance('taskAttachments')" required="false()"
                                             relevant="instance('taskAttachments')/action != 'hide'"/>

                                <!-- Completed task shown as read-only fulfilled form -->
                                <xsl:if test="doc('input:getTaskResponse')/tms:task/tms:taskState = 'COMPLETED'">
                                    <xforms:bind nodeset="instance('taskinput')" readonly="true()"/>
                                    <xforms:bind nodeset="instance('taskoutput')" readonly="true()"/>
                                </xsl:if>

                                <xforms:submission id="showAttachments" ref="instance('taskAttachments')"
                                                   validate="false" action="/act" method="post" replace="instance"
                                                   instance="taskAttachments">
                                    <xforms:message ev:event="xforms-submit-error" level="modal">Error while attachments retrieval</xforms:message>
                                </xforms:submission>

                                <xforms:submission id="attachFile" ref="instance('taskAttachments')" validate="false"
                                                   action="/new-attachment" method="post" replace="all">

                                    <xforms:toggle ev:event="xforms-submit" case="submit-wait"/>
                                    <xforms:toggle ev:event="xforms-submit-done" case="viewTask"/>
                                    <xforms:action ev:event="xforms-submit-error">
                                        <xforms:toggle case="viewTask"/>
                                        <xforms:message level="modal">Error, cannot attach file</xforms:message>
                                    </xforms:action>
                                </xforms:submission>

                                <xforms:submission id="attachText" ref="instance('taskAttachments')" validate="false"
                                                   action="/attachments" method="post" replace="instance"
                                                   instance="taskAttachments">

                                    <xforms:message ev:event="xforms-submit-error" level="modal">Error, cannot attach note</xforms:message>
                                </xforms:submission>

                                <xforms:submission id="deleteAttachment" ref="instance('taskAttachments')"
                                                   validate="false"
                                                   action="/attachments" method="post" replace="instance"
                                                   instance="taskAttachments">

												<xforms:action ev:event="xforms-submit-error">
													<xforms:setvalue ref="instance('taskAttachments')/action" value="'show'"/>
													<xforms:message ev:event="xforms-submit-error" level="modal">Error, cannot delete attachment</xforms:message>
												</xforms:action>
												<xforms:action ev:event="xforms-submit-done">
												<xforms:setvalue ref="instance('taskAttachments')/action" value="'show'"/>
												<xforms:setvalue ref="instance('taskAttachments')/new/@content" value="'file'"/>
												<xforms:recalculate/>
												</xforms:action>
                                </xforms:submission>

                                <!-- Declares the Save Task action -->
                                <xforms:submission id="saveSubmission" ref="instance('taskoutput')" validate="false"
                                                   action="/formManager/save" method="post" replace="all">

                                    <xforms:toggle ev:event="xforms-submit" case="submit-wait"/>
                                    <xforms:toggle ev:event="xforms-submit-done" case="refresh-all"/>
                                    <xforms:action ev:event="xforms-submit-error">
                                        <xforms:toggle case="viewTask"/>
                                        <xforms:message level="modal">Error, cannot save output</xforms:message>
                                    </xforms:action>
                                </xforms:submission>

                                <!-- Declares the Complete Task action -->
                                <xforms:submission id="completeSubmission" ref="instance('taskoutput')" validate="true"
                                                   action="/formManager/complete" method="post" replace="all">
                                    <xforms:delete ev:event="xforms-submit" nodeset="instance('taskoutput')/@saved" at="0"/>
                                    <xforms:toggle ev:event="xforms-submit" case="submit-wait"/>
                                    <xforms:toggle ev:event="xforms-submit-done" case="refresh-all"/>
                                    <xforms:action ev:event="xforms-submit-error">
                                        <xforms:toggle case="viewTask"/>
                                        <xforms:message level="modal">Can not complete task because the form is not correctly filled out</xforms:message>
                                    </xforms:action>
                                </xforms:submission>

                                <!-- Declares the Claim Task action -->
                                <xforms:submission id="claimSubmission" ref="instance('taskoutput')" validate="false"
                                                   action="/formManager/claim" method="post" replace="all">

                                    <xforms:toggle ev:event="xforms-submit" case="submit-wait"/>
                                    <xforms:toggle ev:event="xforms-submit-done" case="refresh-all"/>
                                    <xforms:action ev:event="xforms-submit-error">
                                        <xforms:toggle case="viewTask"/>
                                        <xforms:message level="modal">Error, cannot claim task</xforms:message>
                                    </xforms:action>
                                </xforms:submission>

                                <!-- Declares the Revoke Task action -->
                                <xforms:submission id="revokeSubmission" ref="instance('taskoutput')" validate="false"
                                                   action="/formManager/revoke" method="post" replace="all">

                                    <xforms:toggle ev:event="xforms-submit" case="submit-wait"/>
                                    <xforms:toggle ev:event="xforms-submit-done" case="refresh-all"/>
                                    <xforms:action ev:event="xforms-submit-error">
                                        <xforms:toggle case="viewTask"/>
                                        <xforms:message level="modal">Error, cannot revoke task</xforms:message>
                                    </xforms:action>
                                </xforms:submission>
                            </xsl:copy>
                        </xsl:template>

						
						<!-- Copy the input of the task while preserving some input that was coded in the form -->
						<xsl:template match="xforms:instance[@id = 'taskinput']/*:input">
							<xsl:variable name="xinput" select="."/>
							<xsl:variable name="input" select="doc('input:getTaskResponse')/tms:task/*:input/*"/>
							<!-- Create an input element that will basically receive all the nodes coming from the task response -->
							<xsl:element name="input" namespace="{namespace-uri($input)}">
								<xsl:for-each select="$input/*">
									<xsl:variable name="current" select="."/>
									<xsl:choose>
										<!-- 
											if the value of the node from TMS is empty, we copy the one from the form.
											In the best case, the form has a value so we preserve it.
											In the worst case, the form had no value so we keep the same element.
										-->
										<xsl:when test="not(normalize-space(.))">
											<xsl:variable name="same" select="$xinput//*[local-name( ) = local-name($current)]"/>
											<xsl:copy-of select="$same"/>
										</xsl:when>
										<xsl:otherwise>
											<!-- Copy all the nodes containing data as is -->
											<xsl:copy-of select="."/>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:for-each>
							</xsl:element>
						</xsl:template>

                        <!-- copy the task meta data to the 'taskMetaData' instance -->
                        <xsl:template match="xforms:instance[@id = 'taskmetadata']">
                            <xsl:copy>
                                <xsl:copy-of select="@* | doc('input:xpl-input')/*"/>
                            </xsl:copy>
                        </xsl:template>

                        <!-- adds meta data information to the 'taskoutput' instance
                            The form is responsible for putting the actual data output but
                            it is the Form Manager responsibility to add the meta data element
                            that are required to invoked the Complete Task action
                        -->

                        <!-- Checks if task-output data present in task data,
                               if so, replace the form instance('taskoutput') content and set "saved" attribute to "true"
                               otherwise - remains content untouched, as it was in design.
                        -->
                        <xsl:template match="xforms:instance[@id = 'taskoutput']/*">
                            <xsl:variable name="output" select="doc('input:getTaskResponse')/tms:task/*:output"/>
                            <xsl:choose>
                                <xsl:when test="$output">
                                    <xsl:variable name="child" select="$output/*"/>
                                    <xsl:element name="{name($child)}" namespace="{namespace-uri($child)}">
                                        <xsl:copy-of select="$child/@*"/>
                                        <xsl:attribute name="saved">true</xsl:attribute>
                                        <xsl:copy-of select="$child/*"/>
                                    </xsl:element>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:copy>
                                        <xsl:apply-templates select="*"/>
                                    </xsl:copy>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:template>

                        <!-- Adds the controls for Task Actions and handle the logic -->
                        <xsl:template match="xhtml:body">
                            <xsl:copy>
	
                                <!-- display the proper page -->
                                <xforms:switch>
                                    <!-- Display the form and adds task action controls-->
                                    <xforms:case id="viewTask" selected="true">
	
                                        <xforms:trigger appearance="xxforms:image" style="float: top">
                                            <xforms:label>Show/Refresh Attachments</xforms:label>
                                            <xxforms:img title="Add Attachments to Task" src="/images/attachments.gif"/>
                                            <xforms:action ev:event="DOMActivate">
                                                <xforms:setvalue ref="instance('taskAttachments')/action" value="'show'"/>
                                                <xforms:recalculate/>
                                                <xforms:send submission="showAttachments"/>
                                            </xforms:action>
                                        </xforms:trigger>

                                        <xforms:group ref="instance('taskAttachments')" style="width : 97%; border-color: #CCCCCC; float: top">
                                                <xforms:trigger appearance="xxforms:image" style="float: right">
                                                    <xforms:label>Close Attachments Section</xforms:label>
                                                    <xxforms:img title="Close Attachments" src="/images/close.png"/>
                                                    <xforms:setvalue ev:event="DOMActivate" ref="instance('taskAttachments')/action" value="'hide'"/>
                                                </xforms:trigger>
                                                <xhtml:table
                                                        style="width: 100%; cell-padding: 4px; cell-spacing: 0px; border-collapse: collapse;"
                                                        border="1">
                                                    <col width="5%"/>
                                                    <col width="55%"/>
                                                    <col width="15%"/>
                                                    <col width="25%"/>
                                                    <xhtml:tr>
                                                        <xhtml:th/>
                                                        <xhtml:th>Title</xhtml:th>
                                                        <xhtml:th>Mime Type</xhtml:th>
                                                        <xhtml:th>Creation Date</xhtml:th>
                                                    </xhtml:tr>
                                                    <xforms:repeat nodeset="attachment" id="attachmentsTable">
                                                        <xhtml:tr>
                                                            <xhtml:td style="border-width: 0px; border-collapse: collapse;text-align: center">
                                                                <xforms:trigger appearance="xxforms:image">
                                                                    <xforms:label>Delete Attachment</xforms:label>
                                                                    <xxforms:img title="Remove" src="/images/remove.gif"/>
                                                                    <xforms:action ev:event="DOMActivate">
                                                                        <xforms:setvalue
                                                                                ref="instance('taskAttachments')/delete"
                                                                                value="instance('taskAttachments')/attachment[index('attachmentsTable')]/url"/>
                                                                        <xforms:setvalue
                                                                                ref="instance('taskAttachments')/action"
                                                                                value="'delete'"/>
                                                                        <xforms:recalculate/>
                                                                        <xforms:send submission="deleteAttachment"/>
                                                                    </xforms:action>
                                                                </xforms:trigger>
                                                            </xhtml:td>
                                                            <xhtml:td>
                                                                <xforms:trigger appearance="xxforms:link">
                                                                    <xforms:label ref="title"/>
                                                                    <xforms:load
                                                                            ref="instance('taskAttachments')/attachment[index('attachmentsTable')]/url"
                                                                            ev:event="DOMActivate"
                                                                            show="new"/>
                                                                </xforms:trigger>
                                                            </xhtml:td>
                                                            <xhtml:td>
                                                                <xforms:trigger appearance="xxforms:link">
                                                                    <xforms:label ref="mime-type"/>
                                                                    <xforms:load
                                                                            ref="instance('taskAttachments')/attachment[index('attachmentsTable')]/url"
                                                                            ev:event="DOMActivate"
                                                                            show="new"/>
                                                                </xforms:trigger>
                                                            </xhtml:td>
                                                            <xhtml:td>
                                                                <xforms:trigger appearance="xxforms:link">
                                                                    <xforms:label ref="create-date"/>
                                                                    <xforms:load
                                                                            ref="instance('taskAttachments')/attachment[index('attachmentsTable')]/url"
                                                                            ev:event="DOMActivate"
                                                                            show="new"/>
                                                                </xforms:trigger>
                                                            </xhtml:td>
                                                        </xhtml:tr>
                                                    </xforms:repeat>

                                                    <xhtml:tr>
                                                        <xhtml:th colspan="5" title="Select Attachment Type" style="text-align: center">
                                                            <xforms:select1 appearance="minimal" ref="new/@content">
                                                                <xforms:label>Add New Attachment  </xforms:label>
                                                                <xforms:item>
                                                                    <xforms:label>File &lt; 1Mb</xforms:label><!-- See oxf:/config/properties.xml#max-upload-size -->
                                                                    <xforms:value>file</xforms:value>
                                                                </xforms:item>
                                                                <xforms:item>
                                                                    <xforms:label>Text Note</xforms:label>
                                                                    <xforms:value>text</xforms:value>
                                                                </xforms:item>
                                                            </xforms:select1>
                                                        </xhtml:th>
                                                    </xhtml:tr>
                                                    <xhtml:tr>
                                                        <xhtml:td
                                                                style="border-width: 0px; border-collapse: collapse;text-align: center">
                                                            <xforms:trigger ref="new/file/@attachFile" appearance="xxforms:image">
                                                                <xforms:label>Click to Attach File</xforms:label>
                                                                <xxforms:img title="Attach File" src="/images/add.gif"/>
                                                                <xforms:action ev:event="DOMActivate"
                                                                               xforms:if="not(string-length(normalize-space(instance('taskAttachments')/new/@title)) = 0)">
                                                                    <xforms:setvalue ref="instance('taskAttachments')/action" value="'new'"/>
                                                                    <xforms:recalculate/>
                                                                    <xforms:insert
                                                                            nodeset="instance('taskAttachments')/action"
                                                                            at="1" position="after"
                                                                            origin="instance('taskoutput')"/>
                                                                    <xforms:insert
                                                                            nodeset="instance('taskAttachments')/action"
                                                                            at="1" position="after"
                                                                            origin="instance('taskinput')"/>
                                                                    <xforms:send submission="attachFile"/>
                                                                </xforms:action>
                                                            </xforms:trigger>
                                                            <xforms:trigger ref="new/plaintext/@attachText"
                                                                            appearance="xxforms:image">
                                                                <xforms:label>Click to Attach Text</xforms:label>
                                                                <xxforms:img title="Attache text" src="/images/add.gif"/>
                                                                <xforms:action ev:event="DOMActivate"
                                                                        if="not(string-length(normalize-space(instance('taskAttachments')/new/@title)) = 0 or string-length(normalize-space(instance('taskAttachments')/new/plaintext)) = 0)">
                                                                    <xforms:setvalue
                                                                            ref="instance('taskAttachments')/action"
                                                                            value="'new'"/>
                                                                    <xforms:recalculate/>
                                                                    <xforms:send submission="attachText"/>
                                                                </xforms:action>
                                                            </xforms:trigger>
                                                        </xhtml:td>
                                                        <xhtml:td>
                                                            <xforms:input ref="instance('taskAttachments')/new/@title"
                                                                          id="new-attachment-title" xxforms:maxlength="512">
                                                              </xforms:input>
                                                        </xhtml:td>
                                                        <xhtml:td colspan="2" title="Browse for Files" >
                                                            <xforms:upload ref="instance('taskAttachments')/new/file" id="new-attachment-file">
                                                                <xforms:filename ref="@filename"/>
                                                                <xforms:mediatype ref="@mediatype"/>
                                                                <xxforms:size ref="@size"/>
                                                            </xforms:upload>
                                                            <xforms:textarea
                                                                    ref="instance('taskAttachments')/new/plaintext"
                                                                    xxforms:rows="7"
                                                                    id="new-attachment-text">
                                                            </xforms:textarea>
                                                        </xhtml:td>
                                                    </xhtml:tr>

                                                </xhtml:table>
                                        </xforms:group>

                                        <xsl:apply-templates select="@*|*"/>

                                        <xhtml:div>
                                            <xsl:variable name="user" select="doc('input:xpl-input')/task/user/text()"/>
                                            <xsl:variable name="metadata" select="doc('input:getTaskResponse')/tms:task/tms:metadata"/>

                                            <xsl:if test="$metadata/tms:taskState = 'READY' and
 (count($metadata/tms:claimAction/tms:authorized) = 0 or $metadata/tms:claimAction/tms:authorized/text() != 'false')">
												<span class="button" title="Claim Task for Processing">
                                                <xforms:submit class="button4" submission="claimSubmission">
                                                    <xforms:label><span>Claim</span></xforms:label>
                                                </xforms:submit>
											    </span>
                                            </xsl:if>
                                            <xsl:if test="$metadata/tms:taskState = 'CLAIMED' and
 (count($metadata/tms:revokeAction/tms:authorized) = 0 or $metadata/tms:revokeAction/tms:authorized/text() != 'false')">
												<span class="button" title="Revoke Claim on Task ">
                                                <xforms:submit class="button4" submission="revokeSubmission">
                                                    <xforms:label ><span>Revoke</span></xforms:label>
                                                </xforms:submit>
                                                </span>
                                            </xsl:if>
                                            <xsl:if test="$metadata/tms:taskState != 'COMPLETED' and
 (count($metadata/tms:saveAction/tms:authorized) = 0 or $metadata/tms:saveAction/tms:authorized/text() != 'false')">
												<span class="button" title="Save Task">
                                                <xforms:submit submission="saveSubmission">
                                                    <xforms:label>Save</xforms:label>
                                                </xforms:submit>
     											</span>
                                            </xsl:if>
                                            <xsl:if test="$metadata/tms:taskState != 'COMPLETED' and
 (count($metadata/tms:completeAction/tms:authorized) = 0 or $metadata/tms:completeAction/tms:authorized/text() != 'false')">
												<span class="button" title="Complete Task and Continue Workflow ">
                                                <xforms:submit submission="completeSubmission">
                                                    <xforms:label>Complete</xforms:label>
                                                </xforms:submit>
											    </span>
                                            </xsl:if>
                                        </xhtml:div>

                                    </xforms:case>
                                    <!-- Display while submission - wait page -->
                                    <xforms:case id="submit-wait">
                                        <xhtml:center>Sending request, please wait</xhtml:center>
                                        <xhtml:center>
                                            <xhtml:img src="/images/loading.gif" alt="..."/>
                                        </xhtml:center>
                                    </xforms:case>
                                    <!-- Display after submit done, waiting for reply -->
                                    <xforms:case id="refresh-all">
                                    <xsl:if test="true">
                                       	  <xhtml:html>
						                        <xhtml:body onLoad="parent.window.hideWindow();"/>
						                    </xhtml:html>
                                     </xsl:if>
                                    </xforms:case>
                                </xforms:switch>
                            </xsl:copy>
                        </xsl:template>
                    </xsl:stylesheet>
                </p:input>
                <p:output name="data" id="pre-form"/>
            </p:processor>

            <!-- Need to add params for requests to TMS or TMP.
            They are added to taskoutput toot element as attributes
            and later purged when the web-service is called -->
            <p:processor name="oxf:xslt">
                <p:input name="data" href="#pre-form"/>
                <p:input name="xpl-input" href="#data"/>
                <p:input name="config">
                    <xsl:stylesheet version="2.0">
                        <xsl:import href="oxf:/oxf/xslt/utils/copy.xsl"/>

                        <xsl:template match="xforms:instance[@id = 'taskoutput']/*">
                            <xsl:copy>
                                <xsl:apply-templates select="@*"/>
                                <xsl:attribute name="taskId">
                                    <xsl:value-of select="doc('input:xpl-input')/task/id"/>
                                </xsl:attribute>
                                <xsl:attribute name="participantToken">
                                    <xsl:value-of select="doc('input:xpl-input')/task/token"/>
                                </xsl:attribute>
                                <xsl:attribute name="user">
                                    <xsl:value-of select="doc('input:xpl-input')/task/user"/>
                                </xsl:attribute>
                                <xsl:attribute name="formUrl">
                                    <xsl:value-of select="doc('input:xpl-input')/task/url"/>
                                </xsl:attribute>
                                <xsl:apply-templates select="*"/>
                            </xsl:copy>
                        </xsl:template>
                    </xsl:stylesheet>
                </p:input>
                <p:output name="data" ref="data"/>
            </p:processor>
                </p:otherwise>
            </p:choose>
        </p:otherwise>
    </p:choose>

</p:config>