package org.intalio.tempo.deployment.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.intalio.tempo.deployment.AssemblyId;
import org.intalio.tempo.deployment.ComponentId;
import org.intalio.tempo.deployment.DeployedAssembly;
import org.intalio.tempo.deployment.DeploymentResult;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MockClusteredNode implements ClusteredNode, ApplicationContextAware {
	private static String config;
	private ApplicationContext ctx;        
	
	private static Boolean shutdownRequested = false;

	private File _deployDir = TestUtils.getTempDeployDir();
	private DeploymentServiceImpl service;
	
	private MockComponentManager manager;

	public String getId() throws Exception {
		return service.getCluster().getServerId();
	}

	public DeploymentServiceImpl getService() {
		return service;
	}
	public void setService(DeploymentServiceImpl service) {
		this.service = service;
	}

	public void shutdownNode() throws Exception {
		try {
			if( service != null) service.stop();
		} finally {
			shutdownRequested = true;
		}
	}

	public void initDeploymentService() throws Exception {
		manager = new MockComponentManager("MockEngine");

		service.setDeployDirectory(_deployDir.getAbsolutePath());
		service.init();

        System.out.println(">>>" + config + "] DeploymentService initialized.");
	}

    public void startDeploymentService() {
        System.out.println(">>>" + config + "] Starting DeploymentService...");

        service.getCallback().available(manager);

        service.start();

//        int i=0;
//        while (true) {
//            if (service.isStarted()) break;
//            wait(1);
//            i++;
//            if (i>60) break;
//        }
//
//        if (!service.isStarted()) throw new RuntimeException("DeploymentService cannot start?");
//        
//        System.out.println(">>>" + config + "] DeploymentService Started.");
    }
    
    public DeploymentResult deployExplodedAssembly(String assemblyDirPath) {
        File assemblyDir = TestUtils.getAssemblyDir(assemblyDirPath);
        return service.deployExplodedAssembly(assemblyDir, true);
    }
    
    public DeploymentResult deployAssembly(String assemblyName, String zipPath, boolean replaceExistingAssemblies) {
    	try {
			return service.deployAssembly(assemblyName, new FileInputStream(zipPath), replaceExistingAssemblies);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
    }
    
    public DeploymentResult undeployAssembly(AssemblyId aid) {
    	return service.undeployAssembly(aid);
    }
    
    public Collection<DeployedAssembly> getDeployedAssemblies() {
    	List<DeployedAssembly> assemblies = new ArrayList<DeployedAssembly>();
    	
    	for( DeployedAssembly assembly : service.getDeployedAssemblies() ) {
    		assemblies.add(assembly);
    	}
    	
    	return assemblies;
    }
    
    public String getGroupDetails() throws Exception {
    	return "" + service.getCluster().getAllCurrentMembers();
    }
    
    public boolean isCoordinator() throws Exception {
    	return service.getCluster().isCoordinator();
    }
    
    public boolean isDeployed(ComponentId name) {
    	return manager.isDeployed(name);
    }

    public boolean isActivated(ComponentId name) {
    	return manager.isActivated(name);
    }
    
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		this.ctx = ctx;
	}
	
	public void sayHello() throws Exception {
		service.sayHello();
	}

	public void failDeployment() {
		manager._failDeployment = true;
	}
	
	public void setScanPeriod(int scanPeriod) {
		service.setScanPeriod(scanPeriod);
	}
	
	private void wait(int seconds) {
        try {
            Thread.sleep(seconds*1000);
        } catch (InterruptedException except) {
            // ignore
        }
    }

	private static void initializeProcess() throws Exception {
		// configure spring
		System.out.println("Initializing node with spring config: " + config);
		new ClassPathXmlApplicationContext(config);
		
		synchronized(shutdownRequested) {
			while( !shutdownRequested ) {
				shutdownRequested.wait(1000);
			}
		}
	}

    public static void main(String args[]) throws Throwable {
		MockClusteredNode mockClusteredNode = new MockClusteredNode();
		
		try {
			System.out.println("Node starting in its own process..");
			
			config = args[0];
			initializeProcess();
		} finally {
			mockClusteredNode.shutdownNode();
		}
	}
}