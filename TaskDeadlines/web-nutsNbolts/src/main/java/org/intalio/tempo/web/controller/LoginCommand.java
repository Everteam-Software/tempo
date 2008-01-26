/**
 * Copyright (C) 2006, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with
 * the written permission of Intalio Inc. or in accordance with
 * the terms and conditions stipulated in the agreement/contract
 * under which the program(s) have been supplied.
 *
 * $Id$
 * $Log$
 */
package org.intalio.tempo.web.controller;

public class LoginCommand {
    private String _username;
    private String _password;
    private boolean _autoLogin;
    /**
     * @return the password
     */
    public String getPassword() {
        return _password;
    }
    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        _password = password;
    }
    /**
     * @return the username
     */
    public String getUsername() {
        return _username;
    }
    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        _username = username;
    }
    /**
     * @return the autoLogin
     */
    public boolean isAutoLogin() {
        return _autoLogin;
    }
    /**
     * @param autoLogin the autoLogin to set
     */
    public void setAutoLogin(boolean autoLogin) {
        _autoLogin = autoLogin;
    }
 
    
}