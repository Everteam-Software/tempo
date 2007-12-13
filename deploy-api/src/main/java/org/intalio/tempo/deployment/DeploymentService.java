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

package org.intalio.tempo.deployment;

import java.io.InputStream;
import java.util.Collection;


/**
 * Interface for the deployment service.
 */
public interface DeploymentService {

    /**
     * Deploy a packaged (zipped) assembly
     */
    DeploymentResult deployAssembly(String assemblyName, InputStream zip, 
                                    boolean replaceExistingAssemblies);

    /**
     * Undeploy an assembly 
     */
    DeploymentResult undeployAssembly(AssemblyId assemblyId);

    /**
     * Obtain the current list of deployed assemblies
     */
    Collection<DeployedAssembly> getDeployedAssemblies();
    
}
