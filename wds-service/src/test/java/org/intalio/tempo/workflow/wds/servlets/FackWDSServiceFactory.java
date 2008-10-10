package org.intalio.tempo.workflow.wds.servlets;

import static com.googlecode.instinct.expect.behaviour.Mocker.mock;

import org.intalio.tempo.workflow.wds.core.WDSService;
import org.intalio.tempo.workflow.wds.core.WDSServiceFactory;

public class FackWDSServiceFactory extends WDSServiceFactory{
    
    private static WDSService wdsService;
    
    @Override
    public WDSService getWDSService() {
        if (wdsService == null)
            wdsService = mock(org.intalio.tempo.workflow.wds.core.WDSService.class);
        return wdsService;
    }

}
