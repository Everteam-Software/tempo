package org.intalio.tempo.workflow.tas.axis2;

import org.intalio.tempo.security.token.TokenService;
import org.intalio.tempo.workflow.tas.core.N3AuthStrategy;

import com.googlecode.instinct.expect.behaviour.Mocker;

public class FakeN3AuthStrategy extends N3AuthStrategy {
    public TokenService connect2tokenService() throws Exception {
        if (_tokenService == null) {
//            _tokenService = new TokenClient("internal://"){
//                protected ServiceClient getServiceClient() throws AxisFault{
//                    return new DummyServiceClient();
//                }
//            };
            _tokenService = Mocker.mock(TokenService.class);
        }
        return _tokenService;
    }
}
