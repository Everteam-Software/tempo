/**
 * Copyright (c) 2005-2007 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 */

package org.intalio.tempo.uiframework.versions;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.intalio.tempo.versions.BpmsDescriptorParser;

public class BpmsVersionsServlet extends HttpServlet {
    private static final long serialVersionUID = -76889544882620584L;

    public static final String BPMS_VERSION_PROP = "bpms-version";
    public static final String BPMS_BUILD_NUMBER_PROP = "bpms-build-number";
    static final BpmsDescriptorParser bdp = new BpmsDescriptorParser();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        new BpmsDescriptorParser().getBpmsVersionsAsHtml(response.getWriter());
    }

}
