/**
 * Copyright (c) 2005-2008 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 */
package org.intalio.tempo.workflow.tms.feeds;

import org.intalio.tempo.security.ws.TokenClient;

public class Configuration {
    String tmsService;
    TokenClient tokenClient;

    public String getTmsService() {
        return tmsService;
    }

    public void setTmsService(String tmsService) {
        this.tmsService = tmsService;
    }

    public TokenClient getTokenClient() {
        return tokenClient;
    }

    public void setTokenClient(TokenClient tokenClient) {
        this.tokenClient = tokenClient;
    }

    private static Configuration INSTANCE = new Configuration();
    
    public static Configuration getInstance() {
        return INSTANCE;
    }
}
