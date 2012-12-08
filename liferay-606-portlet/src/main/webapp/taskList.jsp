<%@ page contentType="text/xml; charset=UTF-8" language="java" import="java.sql.*,java.util.*,rokudo.sax.*,org.dom4j.*,java.io.*, java.lang.*, javax.net.*" errorPage="" %>
<%@ include file="properties.jsp" %>
<%
String user = request.getParameter("user");
String pass = request.getParameter("pass");
String tipoList = request.getParameter("tipoList");

String resultado = "";
// System.out.println(user);
//System.out.println(pass);
//System.out.println(tipoList);

int counter = 0;
String countAtt;

//user="admin";
//pass="changeit";
//tipoList="ACTIVITY";
//tipoList="NOTIFICATION";
//tipoList="INIT";
%>

<%
///////////////// RETRIEVE TOKEN
String actiona = "authenticateUser";
String cdataAdd = "";
String nFile = "";
String nURL = "";
String nName = "";
String nDate = "";
String nDescription = "";

SOAPClient wsa = new SOAPClient(wsdlURLa, actiona);
wsa.setDEBUG();

wsa.addNamespace("tok","http://tempo.intalio.org/security/tokenService/");

STElement reqa = wsa.getRequest("authenticateUser", "tok");

reqa.addChild("user", "tok").setText(user);

reqa.addChild("password", "tok").setText(pass);

STElement resa = wsa.call();
resa = resa.getChild("authenticateUserResponse");

String token = resa.getChildContent("token");

///////////////////////////////////////////
%>

<%
////// RETRIEVE TASK LIST

SOAPClient ws = new SOAPClient(wsdlURL, action);
//ws.setDEBUG();

ws.addNamespace("tas","http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/");

STElement req = ws.getRequest("getAvailableTasksRequest", "tas");

req.addChild("participantToken", "tas").setText(token);

req.addChild("taskType", "tas").setText(tipoList);

if (tipoList.equals("INIT")) {

req.addChild("subQuery", "tas").setText("T._processState = PIPATaskState.READY ORDER BY t._creationDate DESC");

} else {

req.addChild("subQuery", "tas")
.setText(
"T._state = TaskState.READY or T._state = TaskState.CLAIMED ORDER BY t._creationDate DESC");

}

STElement res = ws.call();
STElement att;

res = res.getChild("getAvailableTasksResponse");

List<STElement> list = res.getChilds("task");
List<STElement> listas;

/// FORMATING

Element rows = DocumentHelper.createDocument().addElement("Items");

for (STElement ste : list) {

counter++;

Element row = rows.addElement("Item");

row.addElement("id").addText(ste.getChildContent("taskId"));
nDescription = ste.getChildContent("description");
if (nDescription.equals("")) {
nDescription = "Empty Description";
}
row.addElement("description").addText(
"<span class='des'>" + nDescription + "</span>");
nDate = ste.getChildContent("creationDate");
nDate = nDate.substring(0, nDate.length() - 10);
nDate = ste.getChildContent("creationDate");
row.addElement("creationDate").addText(nDate);
row.addElement("userOwner").addText(
ste.getChildContent("userOwner"));
row.addElement("roleOwner").addText(
ste.getChildContent("roleOwner"));

String formURL = ste.getChildContent("formUrl");
row.addElement("formUrl").addText(formURL);

String bpmsURL = ste.getChildContent("formUrl");
if (bpmsURL != null) {
if (!bpmsURL.contains("http://") && !bpmsURL.contains("https://")) {
if (bpmsURL.contains(".xform")){
bpmsURL = bpmsURL.substring(bpmsURL.indexOf(".xform")+6);
row.addElement("bpmsUrl").addText(intalioBpmsURL + "/" + xformsURL + bpmsURL);
}else{
row.addElement("bpmsUrl").addText(intalioBpmsURL + bpmsURL);
row.addElement("formUrl").addText(bpmsURL);
}
} else {
row.addElement("bpmsUrl").addText(bpmsURL);
}
} else {
row.addElement("bpmsUrl").addText(bpmsURL);
}
row.addElement("token").addText(token);
att = ste.getChild("attachments");
listas = att.getChilds("attachment");

Element atts = row.addElement("attachment");
cdataAdd = "";

for (STElement ste2 : listas) {

nFile = ste2.getChild("attachmentMetadata")
.getChildContent("fileName");
nURL = ste2.getChildContent("payloadUrl");

nName = "<img src='/image/image_gallery?uuid=802a6dbe-9c43-48c1-9937-0264a72a9a00&groupId=18&t=1344279777502' title='"
+ nFile + "' border='0' />";

cdataAdd += "<div class='attachment'><a href='" + nURL + "' title='" + nFile + "' target='_BLANK'>"
+ nName + "</a></div>";

//Element filecito = atts.addElement("att").addText(ste2.getChild("attachmentMetadata").getChildContent("fileName"));
//filecito.addAttribute("payload", ste2.getChildContent("payloadUrl"));

}

cdataAdd += "";

atts.addText(cdataAdd);

}

rows.addElement("totalRows").addText(Integer.toString(counter));

resultado = rows.asXML();

out.println(resultado);
%>