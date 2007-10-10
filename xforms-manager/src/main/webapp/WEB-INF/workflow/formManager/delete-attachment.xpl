<!--
  ~ Copyright (c) 2005-2007 Intalio inc.
  ~
  ~ All rights reserved. This program and the accompanying materials
  ~ are made available under the terms of the Eclipse Public License v1.0
  ~ which accompanies this distribution, and is available at
  ~ http://www.eclipse.org/legal/epl-v10.html
  ~
  ~ Contributors:
  ~ Intalio inc. - initial API and implementation
  -->

<p:config xmlns:p="http://www.orbeon.com/oxf/pipeline"
	xmlns:oxf="http://www.orbeon.com/oxf/processors"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:delegation="http://orbeon.org/oxf/xml/delegation"
        xmlns:tas="http://www.intalio.com/BPMS/Workflow/TaskAttachmentService/"
        xmlns:tms="http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <p:param name="instance" type="input"/>
    <p:param name="data" type="output"/>


    <!-- Call TAS WS for TAS.delete operation to delete attachment -->
    <p:processor name="oxf:xslt">
        <p:input name="data" href="#instance"/>
        <p:input name="config">
            <delegation:execute service="tasWS" operation="deleteRequest" xsl:version="2.0">
                <tas:authCredentials>
                    <tas:participantToken>
                        <xsl:value-of select="/*/@participantToken"/>
                    </tas:participantToken>
                    <tas:authorizedUsers>
                        <xsl:for-each select="/attachments/owners/userOwner">
                            <tas:user><xsl:value-of select="."/></tas:user>
                        </xsl:for-each>
                    </tas:authorizedUsers>
                    <tas:authorizedRoles>
                        <xsl:for-each select="/attachments/owners/roleOwner">
                            <tas:role><xsl:value-of select="."/></tas:role>
                        </xsl:for-each>
                    </tas:authorizedRoles>
                </tas:authCredentials>
                <tas:attachmentURL>
                    <xsl:value-of select="/attachments/delete"/>
                </tas:attachmentURL>
            </delegation:execute>
        </p:input>
        <p:output name="data" id="deleteRequest"/>
    </p:processor>

    <p:processor name="oxf:delegation">
        <p:input name="interface">
            <config>
                <service id="tasWS" type="webservice"
                    endpoint="http://localhost:8080/axis2/services/tas">
                    <operation nsuri="http://www.intalio.com/BPMS/Workflow/TaskAttachmentService/" name="deleteRequest" soap-action="delete"/>
                </service>
            </config>
        </p:input>
        <p:input name="call" href="#deleteRequest"/>
        <p:output name="data" id="deleteResponse"/>
    </p:processor>

    <!-- The following is required just to make OPS to execute above processor: its output must be referenced somewhere -->
    <p:processor name="oxf:null-serializer">
       <p:input name="data" href="#deleteResponse"/>
    </p:processor>

    <!-- Call TMS WS for TMS.add operation to add new attachment -->
    <p:processor name="oxf:xslt">
        <p:input name="data" href="#instance"/>
        <p:input name="config">
            <delegation:execute service="TaskManagementServiceWS" operation="removeAttachmentTMS" xsl:version="2.0">
                <tms:taskId>
                    <xsl:value-of select="/*/@taskId"/>
                </tms:taskId>
                <tms:attachmentUrl>
                    <xsl:value-of select="/attachments/delete"/>
                </tms:attachmentUrl>
                <tms:participantToken>
                    <xsl:value-of select="/*/@participantToken"/>
                </tms:participantToken>
            </delegation:execute>
        </p:input>
        <p:output name="data" id="removeAttachmentTMS"/>
    </p:processor>

    <p:processor name="oxf:delegation">
        <p:input name="interface">
            <config>
                <service id="TaskManagementServiceWS" type="webservice"
                    endpoint="http://localhost:8080/axis2/services/TaskManagementServices">
                    <operation nsuri="http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/"
                               name="removeAttachmentTMS" soap-action="removeAttachment"/>
                </service>
            </config>
        </p:input>
        <p:input name="call" href="#removeAttachmentTMS"/>
        <!-- This output is never used, but should be here as otherwise Orbeon XPL engine consider that calling
        processor which output is not referenced further does not makes sense -->
        <p:output name="data" ref="data"/>
    </p:processor>

</p:config>

