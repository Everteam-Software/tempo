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
import java.util.List;


/**
 * Interface for the deployment service.
 */
public interface DeploymentService {

    /**
     * Deploy a packaged (zipped) assembly
     */
    DeploymentResult deployZipAssembly(String assemblyName, InputStream zip, 
                                       boolean replaceExistingAssembly, 
                                       boolean activateAfterDeploy,
                                       boolean allowPartialDeployment);

    /**
     * Undeploy an assembly 
     */
    DeploymentResult undeployAssembly(String assemblyName);

    /**
     * Activate an assembly
     */
    DeploymentResult activateAssembly(String assemblyName, boolean allowPartialActivation);

    /**
     * Deactivate an assembly
     */
    DeploymentResult deactivateAssembly(String assemblyName);

    /**
     * Obtain the current list of deployed assemblies
     */
    List<DeployedAssembly> getDeployedAssemblies();
    
}

