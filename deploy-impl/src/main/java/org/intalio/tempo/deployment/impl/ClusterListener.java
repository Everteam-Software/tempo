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
	 */
	void onDeployed(DeployedAssembly assembly);
	
	/**
	 * Called when the coordinator un-deploys an assembly.
	 * 
	 * @param assembly
	 */
	void onUndeployed(DeployedAssembly assembly);
}
