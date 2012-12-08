<%@ page contentType="text/xml; charset=UTF-8" language="java" errorPage="" %>

<%

String intalioBpmsURL = "https://localhost:8443";
String intalioAxisURL = "http://localhost:8080";
//url to access axis2 services.
String wsdlURLa = intalioAxisURL+ "/axis2/services/TokenService.Service";
//url to access ode services
String wsdlURL = intalioBpmsURL+ "/ode/processes/TaskManagementServices";
//Action endpoint to fetch tasks from Task Management services
String action = "http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/getAvailableTasks";
String realm = "intalio\\";
String xformsURL = "xFormsManager/init";
%>

