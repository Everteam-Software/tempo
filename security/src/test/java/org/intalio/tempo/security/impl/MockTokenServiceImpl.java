package org.intalio.tempo.security.impl;

import static com.googlecode.instinct.expect.behaviour.Mocker.mock;
import edu.yale.its.tp.cas.client.ProxyTicketValidator;

public class MockTokenServiceImpl extends TokenServiceImpl {
    ProxyTicketValidator _p;
    public ProxyTicketValidator getProxyTicketValidator(){
        if (_p == null)
            _p=mock(ProxyTicketValidator.class);
        return _p;                
    }
}
