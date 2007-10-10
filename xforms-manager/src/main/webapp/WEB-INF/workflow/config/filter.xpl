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
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:oxf="http://www.orbeon.com/oxf/processors"
    xmlns:xforms="http://www.w3.org/2002/xforms"
    xmlns:xxforms="http://orbeon.org/oxf/xml/xforms"
    xmlns="http://www.orbeon.com/oxf/controller">

    <!-- Extract request body -->
    <p:processor name="oxf:servlet-filter-generator">
        <p:output name="data" id="request-body"/>
    </p:processor>

    <p:processor name="oxf:pipeline">
        <p:input name="config" href="epilogue.xpl"/>
        <p:input name="data" href="#request-body"/>
        <p:input name="xforms-model"><dummy/></p:input>
        <p:input name="instance"><dummy/></p:input>
    </p:processor>

</p:config>
