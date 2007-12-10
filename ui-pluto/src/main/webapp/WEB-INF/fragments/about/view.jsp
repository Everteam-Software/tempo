<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<%--
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed  under the  License is distributed on an "AS IS" BASIS,
WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
implied.

See the License for the specific language governing permissions and
limitations under the License.
--%>

<!-- Pluto about portlet fragment (displayed in VIEW mode). -->

<table>
  
  <tr>
    <td colspan="2"><h2>About Pluto Portal Driver</h2></td>
  </tr>
  
  <tr>
    <td>Portal Name:</td>
    <td><c:out value="${driverConfig.portalName}"/></td>
  </tr>
  <tr>
    <td>Portal Version:</td>
    <td><c:out value="${driverConfig.portalVersion}"/></td>
  </tr>
  <tr>
    <td>Servlet Container:</td>
    <td><%= config.getServletContext().getServerInfo() %></td>
  </tr>
  <tr>
    <td>Pluto Website:</td>
    <td>
      <a href="http://portals.apache.org/pluto/" target="_blank">
        http://portals.apache.org/pluto/
      </a>
    </td>
  </tr>
  
  <tr>
    <td colspan="2">
      <i>Please use the <a href="http://issues.apache.org/jira/secure/BrowseProject.jspa?id=10560" target="_blank">
      Jira issue tracking site</a> to record any problems you are having with
      the Pluto portal server.</i>
    </td>
  </tr>
  
</table>

