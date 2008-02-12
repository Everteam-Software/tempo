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

package org.intalio.tempo.deployment.ws;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.intalio.tempo.deployment.AssemblyId;
import org.intalio.tempo.deployment.DeploymentResult;
import org.junit.Test;

/**
 * Integration tests for DeployWS: exercise the marshalling/unmarshalling between the client and server.
 * 
 * Uses direct in-VM invocation by default.  If the property "org.intalio.tempo.deploy.ws.endpoint"
 * is defined, it is used to make actual WS calls against a live instance.
 */
public class DeployWSTest extends TestCase {

    protected static DeployClient _client;

    //@BeforeClass
    static {
        String config = DeployWSTest.class.getClassLoader().getResource("tempo-deploy.xml").getFile();
        String dir = new File(config).getParent();
        System.setProperty(DeployWS.CONFIG_DIR_PROPERTY, dir);

        String endpoint = System.getProperty("org.intalio.tempo.deploy.ws.endpoint");
        if (endpoint != null) {
            System.out.println("Using LIVE ENDPOINT "+endpoint);
            _client = new DeployClient(endpoint);
        } else {
            _client = new DeployClientMock();
        }
    }

    @Test
    public void testDeploy() throws Exception {
        String file = DeployWSTest.class.getClassLoader().getResource("assembly1.zip").getFile();
        FileInputStream zip = new FileInputStream(file);
        DeploymentResult result = _client.deployAssembly("assembly1", zip, true);
        assertEquals("assembly1", result.getAssemblyId().getAssemblyName());
    }

    @Test
    public void testUndeploy() throws Exception {
        _client.undeployAssembly(new AssemblyId("assembly1"));
    }

    
    @Test
    public void testGetDeployedAssemblies() throws Exception {
        _client.getDeployedAssemblies();
    }

    static class DeployClientMock extends DeployClient {

        DeployWS _deployWS = new DeployWS();

        public DeployClientMock() {
            super("internal://");
        }

        protected OMParser invoke(String action, OMElement request) throws AxisFault {
            try {
                long start = System.currentTimeMillis();
                while (!_deployWS._deployService.isStarted()) {
                    try { 
                        Thread.sleep(100);
                        if (System.currentTimeMillis()-start > 30000) 
                            throw new RuntimeException("DeploymentService did not start after 30 seconds!");
                    } catch (InterruptedException except) {}
                }
                Method method = _deployWS.getClass().getMethod(action, OMElement.class);
                OMElement response = (OMElement) method.invoke(_deployWS, request);
                return new OMParser(response);
            } catch (InvocationTargetException except) {
                throw AxisFault.makeFault(except.getTargetException());
            } catch (Exception except) {
                throw new RuntimeException(except);
            }
        }
    }
}
