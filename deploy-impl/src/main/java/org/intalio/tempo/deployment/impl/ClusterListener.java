package org.intalio.tempo.deployment.impl;

import org.intalio.tempo.deployment.DeployedAssembly;

/**
 * Through this interface, a deployment node is notified on deployment related events.
 * 
 * @author sean
 *
 */
public interface ClusterListener {
	/**
	 * Called when the coordinator deploys a new assembly.
	 * 
	 * @param assembly
	 * @param activate when set to true, the version should also be activated
	 */
	void onDeployed(DeployedAssembly assembly, boolean activate);
	
	/**
	 * Called when the coordinator un-deploys an assembly.
	 * 
	 * @param assembly
	 */
	void onUndeployed(DeployedAssembly assembly);
	
	/** 
	 * Called when the coordinator activates the assembly version.
	 * 
	 * @param assembly
	 */
	void onActivated(DeployedAssembly assembly);

	/**
	 * Called when the coordinator retires the assembly version.
	 * 
	 * @param assembly
	 */
	void onRetired(DeployedAssembly assembly);
}
