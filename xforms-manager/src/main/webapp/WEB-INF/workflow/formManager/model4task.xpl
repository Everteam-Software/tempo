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
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <p:param name="instance" type="input"/>

    <!-- name must be data because it's called by the page-flow  -->
	<p:param name="data" type="output"/>

    <p:processor name="oxf:request">
        <p:input name="config">
            <config>
                <include>/request/parameters</include>
            </config>
        </p:input>
        <p:output name="data" id="request"/>
    </p:processor>

    <p:processor name="oxf:xslt">
        <p:input name="data" href="#request"/>
        <p:input name="config">
            <task xsl:version="2.0">
                <id><xsl:value-of select="/request/parameters/parameter/value[../name = 'id']"/></id>
                <url><xsl:value-of select="/request/parameters/parameter/value[../name = 'url']"/></url>
                <type><xsl:value-of select="/request/parameters/parameter/value[../name = 'type']"/></type>
                <token><xsl:value-of select="/request/parameters/parameter/value[../name = 'token']"/></token>
                <user><xsl:value-of select="/request/parameters/parameter/value[../name = 'user']"/></user>
            </task>
        </p:input>
        <p:output name="data" ref="data"/>
    </p:processor>

</p:config>
