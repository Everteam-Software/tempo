package org.intalio.tempo.web;

import junit.framework.TestCase;

import org.intalio.tempo.web.controller.FakeTokenClient;
import org.junit.Test;

public class SysPropApplicationContextLoaderTest extends TestCase{

    SysPropApplicationContextLoader acl;
    protected void setUp() throws Exception {
        acl = new SysPropApplicationContextLoader("file:src/test/resources/tempo-ui-fw.xml");
    }
    @Test
    public void testContextLoader() throws Exception {
        
        String fileName = acl.getApplicationContextFile();
        assertEquals(fileName, "src/test/resources/tempo-ui-fw.xml");
    }
    
    @Test
    public void testGetBean() throws Exception {
        Object bean = acl.getBean("tokenService");
        assertEquals(bean.getClass().getName(), FakeTokenClient.class.getName());
    }
}
