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
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

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
public class ClusteredDeployServiceDeployTest extends TestCase {
    
    static final Logger LOG = LoggerFactory.getLogger(ClusteredDeployServiceDeployTest.class);

    File _deployDir = TestUtils.getTempDeployDir();
    
    ClusteredNode coordinator;

//    DeploymentServiceImpl service;
//    MockComponentManager manager;
//    
//    DeploymentServiceImpl service2;
//    DeploymentServiceImpl service3;
//    MockComponentManager manager2;
//    MockComponentManager manager3;
    
    ClusterProxy cluster;
    
    public void setUp() throws Exception {
        PropertyConfigurator.configure(new File(TestUtils.getTestBase(), "log4j.properties").getAbsolutePath());
        Utils.deleteRecursively(_deployDir);

        XmlBeanFactory factory = new XmlBeanFactory(new ClassPathResource("clustered-test.xml"));
		cluster = (ClusterProxy)factory.getBean("cluster");
		cluster.startUpProcesses();
		Thread.sleep(6000);
		
		cluster.setNodes((List<ClusteredNode>) factory.getBean("nodes"));

		// setup database
        DataSource ds = cluster.getDataSource();

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
    	cluster.shutDownProcesses();
    }

    public DeploymentServiceImpl loadDeploymentService(String xmlConfigFile)
    	throws Exception
    {
        ClassPathResource config = new ClassPathResource(xmlConfigFile);
        XmlBeanFactory factory = new XmlBeanFactory( config );
        DeploymentServiceImpl deployService = (DeploymentServiceImpl) factory.getBean("deploymentService");
        
		return deployService;
    }

    public void testDeployBasic1() throws Exception {
        cluster.startDeploymentService();
        coordinator = cluster.findCoordinator();
        
        final DeploymentResult result = coordinator.deployExplodedAssembly("assembly1");
        
        assertTrue(result.isSuccessful());
        assertEquals("assembly1", result.getAssemblyId().getAssemblyName());
        assertEquals(AssemblyId.NO_VERSION, result.getAssemblyId().getAssemblyVersion());
        assertEquals(0, result.getMessages().size());

        assertEquals(3, waitForCompletion(new Condition() {
			public boolean doesSatisfy(ClusteredNode node) {
				return node.isDeployed(new ComponentId(result.getAssemblyId(), "component1"));
			}
        }));
        
        assertEquals(3, waitForCompletion(new Condition() {
			public boolean doesSatisfy(ClusteredNode node) {
				return node.isActivated(new ComponentId(result.getAssemblyId(), "component1"));
			}
        }));

        coordinator.undeployAssembly(result.getAssemblyId());
        assertEquals(3, waitForCompletion(new Condition() {
			public boolean doesSatisfy(ClusteredNode node) {
				return !node.isDeployed(new ComponentId(result.getAssemblyId(), "component1"));
			}
        }));
    }

    public void testDeployFail() throws Exception {
        cluster.startDeploymentService();
        coordinator = cluster.findCoordinator();
        
        coordinator.failDeployment();

        final DeploymentResult result = coordinator.deployExplodedAssembly("assembly1");
        assertFalse(result.isSuccessful());
        assertEquals("assembly1", result.getAssemblyId().getAssemblyName());
        assertEquals(AssemblyId.NO_VERSION, result.getAssemblyId().getAssemblyVersion());
        assertEquals(1, result.getMessages().size());
        assertEquals(0, coordinator.getDeployedAssemblies().size());
        assertFalse(coordinator.isDeployed(new ComponentId(result.getAssemblyId(), "component1")));

        assertEquals(3, waitForCompletion(new Condition() {
			public boolean doesSatisfy(ClusteredNode node) {
				return !node.isDeployed(new ComponentId(result.getAssemblyId(), "component1"));
			}
        }));
    }

    public void testStart() throws Exception {
        File assemblyDir = TestUtils.getAssemblyDir("assembly1");
        Utils.copyRecursively(assemblyDir, new File(_deployDir, "assembly1"));

        LOG.info("testStart()");
        cluster.startDeploymentService();
        LOG.info("testStart() started");
        
        assertEquals(3, waitForCompletion(new Condition() {
			public boolean doesSatisfy(ClusteredNode node) {
				return node.getDeployedAssemblies().size() == 1;
			}
        }));
        coordinator = cluster.findCoordinator();
    }

    public void testScan() throws Exception {
        File assemblyDir = TestUtils.getAssemblyDir("assembly1");
        
        // make sure scanner doesn't deploy assembly before it's time
        Utils.createFile(new File(_deployDir, "assembly1.invalid"));
        
        Utils.copyRecursively(assemblyDir, new File(_deployDir, "assembly1"));
        
        cluster.startDeploymentService();
        coordinator = cluster.findCoordinator();
        coordinator.setScanPeriod(1);
        
        // scan should not deployed assembly yet
        wait(2);
        assertEquals(3, waitForCompletion(new Condition() {
			public boolean doesSatisfy(ClusteredNode node) {
				return node.getDeployedAssemblies().size() == 0;
			}
        }));

        // delete invalid file, scan should now deploy assembly
        Utils.deleteFile(new File(_deployDir, "assembly1.invalid"));
        wait(2);
        assertEquals(3, waitForCompletion(new Condition() {
			public boolean doesSatisfy(ClusteredNode node) {
				return node.getDeployedAssemblies().size() == 1;
			}
        }));
        assertEquals(3, waitForCompletion(new Condition() {
			public boolean doesSatisfy(ClusteredNode node) {
				return node.isDeployed(new ComponentId(new AssemblyId("assembly1"), "component1"));
			}
        }));
    }
    
    public void testDeployZip() throws Exception {
        cluster.startDeploymentService();
        coordinator = cluster.findCoordinator();
        
        File assemblyZip = new File(TestUtils.getTestBase(), "assembly1.zip");

        {
        DeploymentResult result = coordinator.deployAssembly("assembly1", assemblyZip.getAbsolutePath(), false);
        System.out.println("testDeployZip: "+result);
        assertTrue(result.isSuccessful());
        assertEquals("assembly1", result.getAssemblyId().getAssemblyName());
        assertEquals(AssemblyId.NO_VERSION, result.getAssemblyId().getAssemblyVersion());
        assertEquals(0, result.getMessages().size());
        }
        
        {
        // deploy new version
        DeploymentResult result2 = coordinator.deployAssembly("assembly1", assemblyZip.getAbsolutePath(), false);
        System.out.println("testDeployZip: result2="+result2);
        assertTrue(result2.isSuccessful());
        assertEquals("assembly1", result2.getAssemblyId().getAssemblyName());
        assertEquals(2, result2.getAssemblyId().getAssemblyVersion());
        assertEquals(0, result2.getMessages().size());
        }
        
        {
        // deploy and replace all existing versions
        DeploymentResult result3 = coordinator.deployAssembly("assembly1", assemblyZip.getAbsolutePath(), true);
        System.out.println("testDeployZip: result3="+result3);
        assertTrue(result3.isSuccessful());
        assertEquals("assembly1", result3.getAssemblyId().getAssemblyName());
        assertEquals(3, result3.getAssemblyId().getAssemblyVersion());
        assertEquals(0, result3.getMessages().size());
        }
    }

    public void testDeployZipFail() throws Exception {
    	cluster.startDeploymentService();
    	ClusteredNode coordinator = cluster.findCoordinator();
    	
        File assemblyZip = new File(TestUtils.getTestBase(), "assembly1.zip");

        coordinator.failDeployment();
        
        DeploymentResult result = coordinator.deployAssembly("assembly1", assemblyZip.getAbsolutePath(), false);
        System.out.println("testDeployZipFail: "+result);
        assertFalse(result.isSuccessful());
        
        // make sure directory was deleted
        assertFalse(new File(TestUtils.getTempDeployDir(), "assembly1").exists());
    }

    public void testDeployZipWithDotFails() throws Exception {
    	cluster.startDeploymentService();
    	ClusteredNode coordinator = cluster.findCoordinator();

        File assemblyZip = new File(TestUtils.getTestBase(), "assembly1.zip");

        DeploymentResult result = coordinator.deployAssembly("assembly.1", assemblyZip.getAbsolutePath(), false);
        System.out.println("testDeployZipWithDot: "+result);
        assertFalse(result.isSuccessful());
        
        // make sure directory was deleted
        assertFalse(new File(TestUtils.getTempDeployDir(), "assembly1").exists());
    }

    public void testDeployWithDash() throws Exception {
    	cluster.startDeploymentService();
    	ClusteredNode coordinator = cluster.findCoordinator();

        File assemblyZip = new File(TestUtils.getTestBase(), "assembly1.zip");

        {
        DeploymentResult result = coordinator.deployAssembly("assembly1-1", assemblyZip.getAbsolutePath(), true);
        System.out.println("testDeployWithDash: "+result);
        assertTrue(result.isSuccessful());
        assertEquals("assembly1-1", result.getAssemblyId().getAssemblyName());
        assertEquals(AssemblyId.NO_VERSION, result.getAssemblyId().getAssemblyVersion());
        assertEquals(0, result.getMessages().size());
        }
        
        {
        // deploy new version
        DeploymentResult result2 = coordinator.deployAssembly("assembly-dev1", assemblyZip.getAbsolutePath(), true);
        System.out.println("testDeployZip: result2="+result2);
        assertTrue(result2.isSuccessful());
        assertEquals("assembly-dev1", result2.getAssemblyId().getAssemblyName());
        assertEquals(AssemblyId.NO_VERSION, result2.getAssemblyId().getAssemblyVersion());
        assertEquals(0, result2.getMessages().size());
        }
    }    

    public void testUndeployViaFS() throws Exception {
        LOG.info("testUndeployViaFS");
        cluster.startDeploymentService();
        ClusteredNode coordinator = cluster.findCoordinator();
        coordinator.setScanPeriod(1);

        File assemblyZip = new File(TestUtils.getTestBase(), "assembly1.zip");

        {
        DeploymentResult result = coordinator.deployAssembly("assembly1", assemblyZip.getAbsolutePath(), false);
        assertTrue(result.isSuccessful());
        }

        assertEquals(1, coordinator.getDeployedAssemblies().size());

        File f = new File(_deployDir, "assembly1.deployed");
        assertTrue(f.exists());

        // delete .deployed file, scan should now undeploy assembly and redeploy it
        Utils.deleteFile(f);
        int i=0;
        while (i<10) {
            wait(1);
            if (f.exists()) break;

            if (coordinator.getDeployedAssemblies().size() == 0) break;
            i++;
        }
        assertEquals(3, waitForCompletion(new Condition() {
			public boolean doesSatisfy(ClusteredNode node) {
				return node.getDeployedAssemblies().size() == 1;
			}
        }));
        assertTrue(f.exists());
    }

    public void testRemoveViaFS() throws Exception {
        LOG.info("testRemoveViaFS");
        cluster.startDeploymentService();
        ClusteredNode coordinator = cluster.findCoordinator();
        
        coordinator.setScanPeriod(1);

        File assemblyZip = new File(TestUtils.getTestBase(), "assembly1.zip");

        {
        DeploymentResult result = coordinator.deployAssembly("assembly1", assemblyZip.getAbsolutePath(), false);
        assertTrue(result.isSuccessful());
        }

        assertEquals(1, coordinator.getDeployedAssemblies().size());

        File f = new File(_deployDir, "assembly1");
        assertTrue(f.exists() && f.isDirectory());

        // delete assembly directory, scan should now undeploy assembly
        Utils.deleteRecursively(f);
        int i=0;
        while (i<5) {
            wait(1);
            if (coordinator.getDeployedAssemblies().size() == 0) break;
            i++;
        }
        assertEquals(3, waitForCompletion(new Condition() {
			public boolean doesSatisfy(ClusteredNode node) {
				return node.getDeployedAssemblies().size() == 0;
			}
        }));
        assertFalse(new File(_deployDir, "assembly1.deployed").exists());
    }

    void wait(int seconds) {
        try {
            Thread.sleep(seconds*1000);
        } catch (InterruptedException except) {
            // ignore
        }
    }
    
    interface Condition {
    	public boolean doesSatisfy(ClusteredNode node);
    }
    
    int waitForCompletion(Condition condition) throws Exception {
        int completedNodeCnt = 0;
        
        for( int i = 0; i < 5 && completedNodeCnt < 2; i++ ) {
        	if( completedNodeCnt < 2 ) {
        		completedNodeCnt = 0;
        	}
        	for(ClusteredNode node : cluster.getNodes()) {
		        if( condition.doesSatisfy(node) ) {
		        	completedNodeCnt++;
		        }
        	}
        	Thread.sleep(1000);
        }
        
        return completedNodeCnt;
    }
}