package org.intalio.tempo.deployment.impl;

import java.util.Collection;

import org.intalio.tempo.deployment.AssemblyId;
import org.intalio.tempo.deployment.ComponentId;
import org.intalio.tempo.deployment.DeployedAssembly;
import org.intalio.tempo.deployment.DeploymentResult;

public interface ClusteredNode {
	public String getId() throws Exception;
	
	public void shutdownNode() throws Exception;
	
	public void startDeploymentService() throws Exception;

	public String getGroupDetails() throws Exception;
	
	public boolean isCoordinator() throws Exception;
	
	public DeploymentResult deployExplodedAssembly(String assemblyDirPath);
	
	public DeploymentResult deployAssembly(String assemblyName, String zipPath, boolean replaceExistingAssemblies);
	
	public DeploymentResult undeployAssembly(AssemblyId aid);
	
	public Collection<DeployedAssembly> getDeployedAssemblies();
	
	public boolean isDeployed(ComponentId name);
	
	public boolean isActivated(ComponentId name);
	
	public void sayHello() throws Exception;
	
	public void failDeployment();
	
	public void setScanPeriod(int scanPeriod);
}
