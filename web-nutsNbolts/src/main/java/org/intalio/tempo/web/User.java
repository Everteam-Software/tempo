/**
 * Copyright (C) 2006, Intalio Inc.
 * 
 * The program(s) herein may be used and/or copied only with the written permission of Intalio Inc. 
 * or in accordance with the terms and conditions stipulated in the agreement/contract under which
 * the program(s) have been supplied.
 */
package org.intalio.tempo.web;

import java.io.Serializable;

/**
 * Used as an authentication container.
 * 
 */
public class User implements Serializable {

    private static final long serialVersionUID = 6896286857101107227L;

    private String _name;

    private String[] _roles;

    private String _token;

    public User(String name, String[] roles, String token) {
        _name = name;
        _roles = copyArray(roles);
        _token = token;
    }

    public String getName() {
        return _name;
    }

    public String[] getRoles() {
        return copyArray(_roles);
    }

    public String getToken() {
        return _token;
    }

    public boolean hasRole(String role) {
        for (String r : _roles) {
            if (r.equalsIgnoreCase(role)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasOneRoleOf(String[] roles) {
        for (String r : roles) {
            if (hasRole(r)) {
                return true;
            }
        }
        return false;
    }

    private static String[] copyArray(String[] a) {
        if (a == null) {
            return new String[0];
        }
        String[] b = new String[a.length];
        System.arraycopy(a, 0, b, 0, a.length);
        return b;
    }
}
