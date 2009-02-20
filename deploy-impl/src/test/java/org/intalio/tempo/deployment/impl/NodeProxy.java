package org.intalio.tempo.deployment.impl;

import java.io.InputStream;
import java.util.Collection;

import org.intalio.tempo.deployment.AssemblyId;
import org.intalio.tempo.deployment.ComponentId;
import org.intalio.tempo.deployment.DeployedAssembly;
import org.intalio.tempo.deployment.DeploymentResult;

public class NodeProxy implements ClusteredNode {
	private ClusteredNode clusteredNode;

	public String getId() throws Exception {
		return clusteredNode.getId();
	}
	
	public ClusteredNode getClusteredNode() {
		return clusteredNode;
	}

	public void setClusteredNode(ClusteredNode clusteredNode) {
		this.clusteredNode = clusteredNode;
	}

	public String getGroupDetails() throws Exception {
		return clusteredNode.getGroupDetails();
	}
	
	public boolean isCoordinator() throws Exception {
		return clusteredNode.isCoordinator();
	}

	public void shutdownNode() throws Exception {
		clusteredNode.shutdownNode();
		
	}

	public void startDeploymentService() throws Exception {
		clusteredNode.startDeploymentService();
	}

	public DeploymentResult deployExplodedAssembly(String assemblyDirPath) {
		return clusteredNode.deployExplodedAssembly(assemblyDirPath);
	}
	
	public DeploymentResult deployAssembly(String assemblyName, String zipPath, boolean replaceExistingAssemblies) {
		return clusteredNode.deployAssembly(assemblyName, zipPath, replaceExistingAssemblies);
	}
	
	public DeploymentResult undeployAssembly(AssemblyId aid) {
		return clusteredNode.undeployAssembly(aid);
	}
	
	public Collection<DeployedAssembly> getDeployedAssemblies() {
		return clusteredNode.getDeployedAssemblies();
	}
	
	public boolean isDeployed(ComponentId name) {
		return clusteredNode.isDeployed(name);
	} 
	
	public boolean isActivated(ComponentId name) {
		return clusteredNode.isActivated(name);
	}
	
	public void sayHello() throws Exception {
		clusteredNode.sayHello();
	}
	
	public void failDeployment() {
		clusteredNode.failDeployment();
	}

	public void setScanPeriod(int scanPeriod) {
		clusteredNode.setScanPeriod(scanPeriod);
	}
}
