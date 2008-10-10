package org.intalio.tempo.workflow.wds.core;

import static com.googlecode.instinct.expect.behaviour.Mocker.mock;

import org.intalio.tempo.workflow.tms.ITaskManagementService;

public class FackWDSServiceFactory extends WDSServiceFactory{
    private static JPAItemDaoConnection _jpac;
    private static ITaskManagementService _tms;
    
    @Override
    public WDSService getWDSService() {
        if(_jpac == null){
            _jpac = mock(JPAItemDaoConnection.class);
        }
        if (_tms == null){
            _tms = mock(ITaskManagementService.class);
        }
        return new WDSService(_jpac, "http://localhost/tms/endpoint"){
            @Override
            protected ITaskManagementService getTMSService(String participantToken){
                return _tms;
            }
        };
    }
    
    public static JPAItemDaoConnection getJPAMock() {
        return _jpac;
    }
    
    public static ITaskManagementService getTMS() {
        return _tms;
    }
}
