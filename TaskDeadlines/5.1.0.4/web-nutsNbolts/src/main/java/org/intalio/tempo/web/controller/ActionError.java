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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import org.intalio.tempo.web.BundlesCache;

public class ActionError {
    private final int _code;

    private final String _target;

    private final String _message;

    private final String _details;

    private final String _detailsKey;

    private final Object[] _messageArguments;

    private final Object[] _detailArguments;

    public ActionError(String message, String details) {
        super();
        _message = message;
        _details = details;
        _messageArguments = new Object[0];
        _detailArguments = new Object[0];
        _target = null;
        _detailsKey = null;
        _code = -1;
    }

    public ActionError(int code, String target, String message, Object[] messageArguments, String details, String detailsKey,
            Object[] detailsArguments) {
        super();
        _code = code;
        _target = target;
        _message = message;
        _details = details;
        _detailsKey = detailsKey;
        _messageArguments = messageArguments;
        _detailArguments = detailsArguments;
    }

    /**
     * @return Returns the code.
     */
    public int getCode() {
        return _code;
    }

    /**
     * @return Returns the details.
     */
    public String getDetails() {
        return _details;
    }

    /**
     * @return Returns the detailsParams.
     */
    public Object[] getDetailArguments() {
        return _detailArguments;
    }

    /**
     * @return Returns the message.
     */
    public String getMessage() {
        return _message;
    }

    /**
     * @return Returns the messageParams.
     */
    public Object[] getMessageArguments() {
        return _messageArguments;
    }

    /**
     * @return Returns the target.
     */
    public String getTarget() {
        return _target;
    }

    public static String getStackTrace(Throwable th) {
        StringWriter swriter = new StringWriter(1000);
        PrintWriter writer = new PrintWriter(swriter);
        try {
            th.printStackTrace(writer);
        } finally {
            writer.close();
        }
        return swriter.toString();
    }

    static String getMessage(String key, Locale locale) {
        ResourceBundle bundle = BundlesCache.getBundle("messages", locale);
        return bundle == null ? null : bundle.getString(key);
    }

    static String formatMessage(String message, Object[] arguments) {
        return MessageFormat.format(message, arguments);
    }

    public String getFormattedMessage(Locale locale) {
        String unfrmtMsg = getMessage(_message, locale);
        if (unfrmtMsg == null) {
            return null;
        }
        return formatMessage(unfrmtMsg, _messageArguments);
    }

    public String getFormattedDetail(Locale locale) {
        String unfrmtMsg = getMessage(_detailsKey, locale);
        if (unfrmtMsg == null) {
            return null;
        }
        return formatMessage(unfrmtMsg, _detailArguments);
    }

    public String getFormattedMessage() {
        return getFormattedMessage(Locale.getDefault());
    }

    public String getFormattedDetail() {
        return getFormattedDetail(Locale.getDefault());
    }
}
