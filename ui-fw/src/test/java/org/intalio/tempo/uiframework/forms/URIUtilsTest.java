package org.intalio.tempo.uiframework.forms;

import org.intalio.tempo.uiframework.URIUtils;
import org.junit.Test;


public class URIUtilsTest {

    @Test
    public void testURIResolveForBadHostname() throws Exception {
        URIUtils.resolveURI("http", "w64ia_q.informatica.com", 8080, "/hello");
    }
}
