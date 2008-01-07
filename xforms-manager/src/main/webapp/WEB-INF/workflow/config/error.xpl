<!--
    Copyright (C) 2004-2007 Orbeon, Inc.

    This program is free software; you can redistribute it and/or modify it under the terms of the
    GNU Lesser General Public License as published by the Free Software Foundation; either version
    2.1 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
    without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Lesser General Public License for more details.

    The full text of the license is available at http://www.gnu.org/copyleft/lesser.html
-->
<p:pipeline xmlns:p="http://www.orbeon.com/oxf/pipeline"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:oxf="http://www.orbeon.com/oxf/processors">

    <!-- Generate exception document -->
    <p:processor name="oxf:exception">
        <p:output name="data" id="exception"/>
    </p:processor>

    <!-- Format exception page -->
    <p:processor name="oxf:unsafe-xslt">
        <p:input name="data" href="#exception"/>
        <p:input name="config" href="oxf:/config/error.xsl"/>
        <p:output name="data" id="document"/>
    </p:processor>

    <!-- Get some request information -->
    <p:processor name="oxf:request">
        <p:input name="config">
            <config>
                <include>/request/container-type</include>
                <include>/request/request-path</include>
            </config>
        </p:input>
        <p:output name="data" id="request"/>
    </p:processor>

    <!-- Apply theme -->
    <p:processor name="oxf:unsafe-xslt">
        <p:input name="data" href="#document"/>
        <p:input name="request" href="#request"/>
        <p:input name="config">
            <xsl:stylesheet version="2.0">
                <xsl:import href="oxf:/config/theme-plain.xsl"/>
                <xsl:import href="oxf:/ops/utils/formatting/formatting.xsl"/>
            </xsl:stylesheet>
        </p:input>
        <p:output name="data" id="themed"/>
    </p:processor>

    <!-- Rewrite all URLs in XHTML documents -->
    <p:processor name="oxf:xhtml-rewrite">
        <p:input name="rewrite-in" href="#themed"/>
        <p:output name="rewrite-out" id="rewritten-data"/>
    </p:processor>

    <!-- Convert to HTML -->
    <p:processor name="oxf:qname-converter">
        <p:input name="config">
            <config>
                <match>
                    <uri>http://www.w3.org/1999/xhtml</uri>
                </match>
                <replace>
                    <uri></uri>
                    <prefix></prefix>
                </replace>
            </config>
        </p:input>
        <p:input name="data" href="#rewritten-data"/>
        <p:output name="data" id="html-data"/>
    </p:processor>

    <p:processor name="oxf:html-converter">
        <p:input name="config">
            <config>
                <public-doctype>-//W3C//DTD HTML 4.01 Transitional//EN</public-doctype>
                <version>4.01</version>
                <encoding>utf-8</encoding>
            </config>
        </p:input>
        <p:input name="data" href="#html-data"/>
        <p:output name="data" id="converted"/>
    </p:processor>

    <!-- Serialize -->
    <p:processor name="oxf:http-serializer">
        <p:input name="config">
            <config>
                <status-code>500</status-code>
                <header>
                    <name>Cache-Control</name>
                    <value>post-check=0, pre-check=0</value>
                </header>
            </config>
        </p:input>
        <p:input name="data" href="#converted"/>
    </p:processor>

</p:pipeline>
