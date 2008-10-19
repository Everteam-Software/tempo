package org.intalio.tempo.web;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class FakeHttpServletResponse implements HttpServletResponse {
    private FakeServletOutputStream _fsos;
    private String _contentType;
    private int _contentLength;
    private int _errorCode;
    private PrintWriter pw;
    private CharArrayWriter caw;
    private int _status;
    private String _redirectURL;
    
    public void addCookie(Cookie arg0) {
        // TODO Auto-generated method stub
        
    }

    public void addDateHeader(String arg0, long arg1) {
        // TODO Auto-generated method stub
        
    }

    public void addHeader(String arg0, String arg1) {
        // TODO Auto-generated method stub
        
    }

    public void addIntHeader(String arg0, int arg1) {
        // TODO Auto-generated method stub
        
    }

    public boolean containsHeader(String arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    public String encodeRedirectURL(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public String encodeRedirectUrl(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public String encodeURL(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public String encodeUrl(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public void sendError(int errorCode) throws IOException {
        _errorCode = errorCode;
        
    }

    public int getErrorCode(){
        return _errorCode;
    }
    public void sendError(int arg0, String arg1) throws IOException {
        // TODO Auto-generated method stub
        
    }

    public void sendRedirect(String url) throws IOException {
        _redirectURL = url;
        
    }
    
    public String getRedirectURL() {
        return _redirectURL;
    }

    public void setDateHeader(String arg0, long arg1) {
        // TODO Auto-generated method stub
        
    }

    public void setHeader(String arg0, String arg1) {
        // TODO Auto-generated method stub
        
    }

    public void setIntHeader(String arg0, int arg1) {
        // TODO Auto-generated method stub
        
    }

    public void setStatus(int status) {
        _status = status;
        
    }
    
    public int getStatus(){
        return _status;
    }

    public void setStatus(int arg0, String arg1) {
        // TODO Auto-generated method stub
        
    }

    public void flushBuffer() throws IOException {
        // TODO Auto-generated method stub
        
    }

    public int getBufferSize() {
        // TODO Auto-generated method stub
        return 0;
    }

    public String getCharacterEncoding() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getContentType() {
        return _contentType;
    }

    public Locale getLocale() {
        // TODO Auto-generated method stub
        return null;
    }

    public ServletOutputStream getOutputStream() throws IOException {
        if (_fsos == null)
            _fsos = new FakeServletOutputStream();
        return _fsos;
    }

    public PrintWriter getWriter() throws IOException {
        if (pw == null){
            caw = new CharArrayWriter();
            pw = new PrintWriter(caw);
        }
        return pw;
    }
    
    public CharArrayWriter getCharWriter(){
        return caw;
    }

    public boolean isCommitted() {
        // TODO Auto-generated method stub
        return false;
    }

    public void reset() {
        // TODO Auto-generated method stub
        
    }

    public void resetBuffer() {
        // TODO Auto-generated method stub
        
    }

    public void setBufferSize(int arg0) {
        // TODO Auto-generated method stub
        
    }

    public void setCharacterEncoding(String arg0) {
        // TODO Auto-generated method stub
        
    }

    public void setContentLength(int contentLength) {
        _contentLength = contentLength;
        
    }
    
    public int getContentLength(){
        return _contentLength;
    }

    public void setContentType(String contentType) {
        _contentType = contentType;
    }

    public void setLocale(Locale arg0) {
        // TODO Auto-generated method stub
        
    }

}
