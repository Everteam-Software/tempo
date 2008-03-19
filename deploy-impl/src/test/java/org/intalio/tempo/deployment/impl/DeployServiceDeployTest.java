/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with
 * the written permission of Intalio Inc. or in accordance with
 * the terms and conditions stipulated in the agreement/contract
 * under which the program(s) have been supplied.
 *
 * $Id: SimpleSpringTest.java,v 1.5 2005/03/29 22:09:07 ssahuc Exp $
 */

package org.intalio.tempo.deployment.impl;

import java.io.File;
import java.io.FileInputStream;

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
            if (i>60) break;
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
        DeploymentResult result = service.deployExplodedAssembly(assemblyDir);

        assertTrue(result.isSuccessful());
        assertEquals("assembly1", result.getAssemblyId().getAssemblyName());
        assertEquals(AssemblyId.NO_VERSION, result.getAssemblyId().getAssemblyVersion());
        assertEquals(0, result.getMessages().size());

        assertEquals(1, service.getDeployedAssemblies().size());
        
        assertTrue(manager.isDeployed(new ComponentId(result.getAssemblyId(), "component1")));
        
        service.undeployAssembly(result.getAssemblyId());
        assertFalse(manager.isDeployed(new ComponentId(result.getAssemblyId(), "component1")));
        
    }
    
    public void testDeployTwice() throws Exception {
        start();

        File assemblyDir = TestUtils.getAssemblyDir("assembly1");

        // deploy assembly1
        DeploymentResult result = service.deployExplodedAssembly(assemblyDir);
        assertTrue(result.isSuccessful());
        assertEquals("assembly1", result.getAssemblyId().getAssemblyName());
        assertEquals(AssemblyId.NO_VERSION, result.getAssemblyId().getAssemblyVersion());
        assertEquals(0, result.getMessages().size());

        // deploy same assembly:  should result in assembly1-2
        DeploymentResult result2 = service.deployExplodedAssembly(assemblyDir);

        assertTrue(result2.isSuccessful());
        assertEquals("assembly1", result2.getAssemblyId().getAssemblyName());
        assertEquals(2, result2.getAssemblyId().getAssemblyVersion());
        assertEquals(0, result2.getMessages().size());
        assertEquals(2, service.getDeployedAssemblies().size());
        assertTrue(manager.isDeployed(new ComponentId(result.getAssemblyId(), "component1")));
        assertTrue(manager.isDeployed(new ComponentId(result2.getAssemblyId(), "component1")));
    }

    public void testDeployFail() throws Exception {
        manager._failDeployment = true;

        start();
        
        File assemblyDir = TestUtils.getAssemblyDir("assembly1");
        DeploymentResult result = service.deployExplodedAssembly(assemblyDir);

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
        DeploymentResult result = service.deployExplodedAssembly(assemblyDir);

        assertTrue(result.isSuccessful());

        // test removal of component mapping
        service.removeComponentTypeMapping("MappedEngine");
        DeploymentResult remove = service.deployExplodedAssembly(assemblyDir);

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
        DeploymentResult result = service.deployAssembly("assembly1", new FileInputStream(assemblyZip), false);
        System.out.println("testDeployZip: "+result);
        assertTrue(result.isSuccessful());
        assertEquals("assembly1", result.getAssemblyId().getAssemblyName());
        assertEquals(AssemblyId.NO_VERSION, result.getAssemblyId().getAssemblyVersion());
        assertEquals(0, result.getMessages().size());
        }
        
        {
        // deploy new version
        DeploymentResult result2 = service.deployAssembly("assembly1", new FileInputStream(assemblyZip), false);
        System.out.println("testDeployZip: result2="+result2);
        assertTrue(result2.isSuccessful());
        assertEquals("assembly1", result2.getAssemblyId().getAssemblyName());
        assertEquals(2, result2.getAssemblyId().getAssemblyVersion());
        assertEquals(0, result2.getMessages().size());
        }
        
        {
        // deploy and replace all existing versions
        DeploymentResult result3 = service.deployAssembly("assembly1", new FileInputStream(assemblyZip), true);
        System.out.println("testDeployZip: result3="+result3);
        assertTrue(result3.isSuccessful());
        assertEquals("assembly1", result3.getAssemblyId().getAssemblyName());
        assertEquals(AssemblyId.NO_VERSION, result3.getAssemblyId().getAssemblyVersion());
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
        DeploymentResult result = service.deployAssembly("assembly1", new FileInputStream(assemblyZip), false);
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
