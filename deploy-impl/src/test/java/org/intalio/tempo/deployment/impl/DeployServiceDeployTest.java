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

package org.intalio.tempo.deployment.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.apache.log4j.PropertyConfigurator;
import org.intalio.tempo.deployment.AssemblyId;
import org.intalio.tempo.deployment.ComponentId;
import org.intalio.tempo.deployment.DeploymentResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * Deployment Service implementation test
 */
public class DeployServiceDeployTest extends TestCase {
    
    static final Logger LOG = LoggerFactory.getLogger(DeployServiceDeployTest.class);

    File _deployDir = TestUtils.getTempDeployDir();

    DeploymentServiceImpl service;
    MockComponentManager manager;
    
    public void setUp() throws Exception {
        PropertyConfigurator.configure(new File(TestUtils.getTestBase(), "log4j.properties").getAbsolutePath());
        
        Utils.deleteRecursively(_deployDir);
        
        manager = new MockComponentManager("MockEngine");

        service = loadDeploymentService("test1.xml");
        service.setDeployDirectory(_deployDir.getAbsolutePath());
        service.init();
        
        DataSource ds = service.getDataSource();

        ClassPathResource script = new ClassPathResource("deploy.derby.sql");
        if (script == null) throw new IOException("Unable to find file: deploy.derby.sql");
        SQLScript sql = new SQLScript(script.getInputStream(), ds);
        sql.setIgnoreErrors(true);
        sql.setInteractive(false);
        sql.executeScript();
        
        Connection c = ds.getConnection();
        EasyStatement.execute(c, "DELETE FROM DEPLOY_RESOURCES");
        EasyStatement.execute(c, "DELETE FROM DEPLOY_COMPONENTS");
        EasyStatement.execute(c, "DELETE FROM DEPLOY_ASSEMBLIES");
        c.close();
    }

    public void tearDown() throws Exception {
        service.stop();
    }

    private void start() {
        service.getCallback().available(manager);

        service.start();

        int i=0;
        while (true) {
            if (service.isStarted()) break;
            wait(1);
            i++;
            if (i>5) break;
        }

        if (!service.isStarted()) throw new RuntimeException("DeploymentService cannot start?");

    }

    public DeploymentServiceImpl loadDeploymentService(String xmlConfigFile)
    	throws Exception
    {
        ClassPathResource config = new ClassPathResource(xmlConfigFile);
        XmlBeanFactory factory = new XmlBeanFactory( config );
        DeploymentServiceImpl deployService = (DeploymentServiceImpl) factory.getBean("deploymentService");
		return deployService;
    }

    public void testAccessors() throws Exception {
        // check accessors
        assertEquals(_deployDir.getAbsolutePath(), service.getDeployDirectory());

        service.setScanPeriod(10);
        assertEquals(10, service.getScanPeriod());
    }

    public void testDeployBasic1() throws Exception {
        start();
        
        File assemblyDir = TestUtils.getAssemblyDir("assembly1");
        DeploymentResult result = service.deployExplodedAssembly(assemblyDir, false);

        assertTrue(result.isSuccessful());
        assertEquals("assembly1", result.getAssemblyId().getAssemblyName());
        assertEquals(AssemblyId.NO_VERSION, result.getAssemblyId().getAssemblyVersion());
        assertEquals(0, result.getMessages().size());

        assertEquals(1, service.getDeployedAssemblies().size());
        
        assertTrue(manager.isDeployed(new ComponentId(result.getAssemblyId(), "component1")));
        
        service.undeployAssembly(result.getAssemblyId());
        assertFalse(manager.isDeployed(new ComponentId(result.getAssemblyId(), "component1")));
        
    }

    public void testDeployVersioning() throws Exception {
        start();

        File assemblyDir = TestUtils.getAssemblyDir("assembly1");

        // deploy assembly1
        DeploymentResult result = service.deployExplodedAssembly(assemblyDir, false);
        assertTrue(result.isSuccessful());
        assertEquals("assembly1", result.getAssemblyId().getAssemblyName());
        assertEquals(AssemblyId.NO_VERSION, result.getAssemblyId().getAssemblyVersion());
        assertEquals(0, result.getMessages().size());

        // deploy same assembly:  should result in assembly1.2
        DeploymentResult result2 = service.deployExplodedAssembly(assemblyDir, false);

        assertTrue(result2.isSuccessful());
        assertEquals("assembly1", result2.getAssemblyId().getAssemblyName());
        assertEquals(2, result2.getAssemblyId().getAssemblyVersion());
        assertEquals(0, result2.getMessages().size());
        assertEquals(2, service.getDeployedAssemblies().size());
        assertTrue(manager.isDeployed(new ComponentId(result.getAssemblyId(), "component1")));
        assertTrue(manager.isDeployed(new ComponentId(result2.getAssemblyId(), "component1")));

        // deploy same assembly:  should result in assembly1.3
        DeploymentResult result3 = service.deployExplodedAssembly(assemblyDir, false);

        assertTrue(result3.isSuccessful());
        assertEquals("assembly1", result3.getAssemblyId().getAssemblyName());
        assertEquals(3, result3.getAssemblyId().getAssemblyVersion());
        assertEquals(0, result3.getMessages().size());
        assertEquals(3, service.getDeployedAssemblies().size());
        assertTrue(manager.isDeployed(new ComponentId(result.getAssemblyId(), "component1")));
        assertTrue(manager.isDeployed(new ComponentId(result3.getAssemblyId(), "component1")));

        // undeployed assembly1.3
        service.undeployAssembly(result3.getAssemblyId());
        assertEquals(2, service.getDeployedAssemblies().size());

        // redeploy assembly1.3
        result3 = service.deployExplodedAssembly(assemblyDir, false);

        assertTrue(result3.isSuccessful());
        assertEquals("assembly1", result3.getAssemblyId().getAssemblyName());
        assertEquals(3, result3.getAssemblyId().getAssemblyVersion());
        assertEquals(0, result3.getMessages().size());
        assertEquals(3, service.getDeployedAssemblies().size());
        assertTrue(manager.isDeployed(new ComponentId(result.getAssemblyId(), "component1")));
        assertTrue(manager.isDeployed(new ComponentId(result3.getAssemblyId(), "component1")));

        // undeployed assembly1.2
        service.undeployAssembly(result2.getAssemblyId());
        assertEquals(2, service.getDeployedAssemblies().size());

        // deploy same assembly again:  should result in assembly1.4
        DeploymentResult result4 = service.deployExplodedAssembly(assemblyDir, false);

        assertTrue(result4.isSuccessful());
        assertEquals("assembly1", result4.getAssemblyId().getAssemblyName());
        assertEquals(4, result4.getAssemblyId().getAssemblyVersion());
        assertEquals(0, result4.getMessages().size());
        assertEquals(3, service.getDeployedAssemblies().size());
        assertTrue(manager.isDeployed(new ComponentId(result.getAssemblyId(), "component1")));
        assertTrue(manager.isDeployed(new ComponentId(result4.getAssemblyId(), "component1")));
    }

    public void testDeployFail() throws Exception {
        manager._failDeployment = true;

        start();
        
        File assemblyDir = TestUtils.getAssemblyDir("assembly1");
        DeploymentResult result = service.deployExplodedAssembly(assemblyDir, false);

        assertFalse(result.isSuccessful());
        assertEquals("assembly1", result.getAssemblyId().getAssemblyName());
        assertEquals(AssemblyId.NO_VERSION, result.getAssemblyId().getAssemblyVersion());
        assertEquals(1, result.getMessages().size());
        assertEquals(0, service.getDeployedAssemblies().size());
        assertFalse(manager.isDeployed(new ComponentId(result.getAssemblyId(), "component1")));
    }

    public void testComponentMapping() throws Exception {
        service.addComponentTypeMapping("MappedEngine", "MockEngine");

        start();

        File assemblyDir = TestUtils.getAssemblyDir("assembly_mapping");
        DeploymentResult result = service.deployExplodedAssembly(assemblyDir, false);

        assertTrue(result.isSuccessful());

        // test removal of component mapping
        service.removeComponentTypeMapping("MappedEngine");
        DeploymentResult remove = service.deployExplodedAssembly(assemblyDir, false);

        assertFalse(remove.isSuccessful());
    }

    public void testStart() throws Exception {
        File assemblyDir = TestUtils.getAssemblyDir("assembly1");
        Utils.copyRecursively(assemblyDir, new File(_deployDir, "assembly1"));

        LOG.info("testStart()");
        start();
        LOG.info("testStart() started");
        LOG.info("testStart() "+service.getDeployedAssemblies());
        
        assertEquals(1, service.getDeployedAssemblies().size());
    }

    public void testScan() throws Exception {
        File assemblyDir = TestUtils.getAssemblyDir("assembly1");
        
        // make sure scanner doesn't deploy assembly before it's time
        Utils.createFile(new File(_deployDir, "assembly1.invalid"));
        
        Utils.copyRecursively(assemblyDir, new File(_deployDir, "assembly1"));
        
        service.setScanPeriod(1);
        start();
        
        // scan should not deployed assembly yet
        wait(2);
        assertEquals(0, service.getDeployedAssemblies().size());

        // delete invalid file, scan should now deploy assembly
        Utils.deleteFile(new File(_deployDir, "assembly1.invalid"));
        wait(2);
        assertEquals(1, service.getDeployedAssemblies().size());
        manager.isDeployed(new ComponentId(new AssemblyId("assembly1"), "component1"));
        
        service.stop();
    }

    public void testDeployZip() throws Exception {
        start();

        File assemblyZip = new File(TestUtils.getTestBase(), "assembly1.zip");

        {
        DeploymentResult result = service.deployAssembly("assembly1", new FileInputStream(assemblyZip), false, false);
        System.out.println("testDeployZip: "+result);
        assertTrue(result.isSuccessful());
        assertEquals("assembly1", result.getAssemblyId().getAssemblyName());
        assertEquals(AssemblyId.NO_VERSION, result.getAssemblyId().getAssemblyVersion());
        assertEquals(0, result.getMessages().size());
        }
        
        {
        // deploy new version
        DeploymentResult result2 = service.deployAssembly("assembly1", new FileInputStream(assemblyZip), false, false);
        System.out.println("testDeployZip: result2="+result2);
        assertTrue(result2.isSuccessful());
        assertEquals("assembly1", result2.getAssemblyId().getAssemblyName());
        assertEquals(2, result2.getAssemblyId().getAssemblyVersion());
        assertEquals(0, result2.getMessages().size());
        }
        
        {
        // deploy and replace all existing versions
        DeploymentResult result3 = service.deployAssembly("assembly1", new FileInputStream(assemblyZip), true, false);
        System.out.println("testDeployZip: result3="+result3);
        assertTrue(result3.isSuccessful());
        assertEquals("assembly1", result3.getAssemblyId().getAssemblyName());
        assertEquals(3, result3.getAssemblyId().getAssemblyVersion());
        assertEquals(0, result3.getMessages().size());
        }
    }

    public void testDeployZipFail() throws Exception {
        start();

        File assemblyZip = new File(TestUtils.getTestBase(), "assembly1.zip");

        manager._failDeployment = true;
        
        DeploymentResult result = service.deployAssembly("assembly1", new FileInputStream(assemblyZip), false);
        System.out.println("testDeployZipFail: "+result);
        assertFalse(result.isSuccessful());
        
        // make sure directory was deleted
        assertFalse(new File(TestUtils.getTempDeployDir(), "assembly1").exists());
    }

    public void testDeployZipWithDotFails() throws Exception {
        start();

        File assemblyZip = new File(TestUtils.getTestBase(), "assembly1.zip");

        DeploymentResult result = service.deployAssembly("assembly.1", new FileInputStream(assemblyZip), false);
        System.out.println("testDeployZipWithDot: "+result);
        assertFalse(result.isSuccessful());
        
        // make sure directory was deleted
        assertFalse(new File(TestUtils.getTempDeployDir(), "assembly1").exists());
    }

    public void testDeployWithDash() throws Exception {
        start();

        File assemblyZip = new File(TestUtils.getTestBase(), "assembly1.zip");

        {
        DeploymentResult result = service.deployAssembly("assembly1-1", new FileInputStream(assemblyZip), true);
        System.out.println("testDeployWithDash: "+result);
        assertTrue(result.isSuccessful());
        assertEquals("assembly1-1", result.getAssemblyId().getAssemblyName());
        assertEquals(AssemblyId.NO_VERSION, result.getAssemblyId().getAssemblyVersion());
        assertEquals(0, result.getMessages().size());
        }
        
        {
        // deploy new version
        DeploymentResult result2 = service.deployAssembly("assembly-dev1", new FileInputStream(assemblyZip), true);
        System.out.println("testDeployZip: result2="+result2);
        assertTrue(result2.isSuccessful());
        assertEquals("assembly-dev1", result2.getAssemblyId().getAssemblyName());
        assertEquals(AssemblyId.NO_VERSION, result2.getAssemblyId().getAssemblyVersion());
        assertEquals(0, result2.getMessages().size());
        }
    }    
    
    public void testUndeployViaFS() throws Exception {
        LOG.info("testUndeployViaFS");
        service.setScanPeriod(1);
        start();

        File assemblyZip = new File(TestUtils.getTestBase(), "assembly1.zip");

        {
        DeploymentResult result = service.deployAssembly("assembly1", new FileInputStream(assemblyZip), false);
        assertTrue(result.isSuccessful());
        }

        assertEquals(1, service.getDeployedAssemblies().size());

        File f = new File(_deployDir, "assembly1.deployed");
        assertTrue(f.exists());

        // delete .deployed file, scan should now undeploy assembly and redeploy it
        Utils.deleteFile(f);
        int i=0;
        while (i<10) {
            wait(1);
            if (f.exists()) break;

            if (service.getDeployedAssemblies().size() == 0) break;
            i++;
        }
        assertEquals(1, service.getDeployedAssemblies().size());
        assertTrue(f.exists());
    }
    
    public void testRemoveViaFS() throws Exception {
        LOG.info("testRemoveViaFS");
        
        service.setScanPeriod(1);
        start();

        File assemblyZip = new File(TestUtils.getTestBase(), "assembly1.zip");

        {
        DeploymentResult result = service.deployAssembly("assembly1", new FileInputStream(assemblyZip), false, false);
        assertTrue(result.isSuccessful());
        }

        assertEquals(1, service.getDeployedAssemblies().size());

        File f = new File(_deployDir, "assembly1");
        assertTrue(f.exists() && f.isDirectory());

        // delete assembly directory, scan should now undeploy assembly
        Utils.deleteRecursively(f);
        int i=0;
        while (i<5) {
            wait(1);
            if (service.getDeployedAssemblies().size() == 0) break;
            i++;
        }
        assertEquals(0, service.getDeployedAssemblies().size());
        assertFalse(new File(_deployDir, "assembly1.deployed").exists());
    }

    void wait(int seconds) {
        try {
            Thread.sleep(seconds*1000);
        } catch (InterruptedException except) {
            // ignore
        }
    }
}
