<%@page import="java.io.Reader"%><%@page import="java.io.OutputStream"%><%@page import="org.apache.commons.io.IOUtils"%><%@page import="org.apache.commons.httpclient.HttpClient"%><%@page import="org.apache.commons.httpclient.methods.*"%><%
    // WARN: do not put any spaces before this javacode, otherwise compilation fails.
    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/xml");

    String soapAction = request.getHeader("SOAPAction");
    String soapServer = request.getHeader("SOAPServer");
    OutputStream o = response.getOutputStream();

    //System.out.println(soapServer);
    //System.out.println(soapAction);

    int length = request.getContentLength();
    try {
        PostMethod post = new PostMethod(soapServer);
        post.setRequestHeader("SOAPAction", soapAction);
        InputStreamRequestEntity is = new InputStreamRequestEntity(request.getInputStream(), length, "text/xml");
        post.setRequestEntity(is);
        HttpClient httpclient = new HttpClient();
        int result = httpclient.executeMethod(post);
        IOUtils.copy(post.getResponseBodyAsStream(), response.getOutputStream());
        post.releaseConnection();
        o.flush();
        // *important* to ensure no more jsp output
        o.close();
    } catch (Exception e) {
        e.printStackTrace();
        //throw e;
    }
%>