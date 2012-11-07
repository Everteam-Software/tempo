<%@ page contentType="text/html; charset=UTF-8" language="java" import="java.net.*,java.io.*,java.sql.*,rokudo.sax.*,org.dom4j.*,java.util.*" errorPage="" %>
<% 
String bpmsurl = request.getParameter("bpmsurl");
String url = request.getParameter("url");
String id = request.getParameter("id");
String tkn = request.getParameter("tkn");
String usr = request.getParameter("usr");
String typ = request.getParameter("typ");

String redirected = bpmsurl+"?id="+id+"&type="+typ+"&url="+url+"&token="+tkn+"&user="+usr;
if (typ.equals("PATask")) { redirected += "&claimTaskOnOpen=false"; }
System.out.println(redirected);
response.sendRedirect(redirected); %>
