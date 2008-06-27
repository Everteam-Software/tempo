/**
 * Copyright (c) 2007-2008 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 */

package org.intalio.tempo.deployment.ws;

import static org.intalio.tempo.deployment.ws.DeployWSConstants.ASSEMBLY_NAME;
import static org.intalio.tempo.deployment.ws.DeployWSConstants.ASSEMBLY_VERSION;
import static org.intalio.tempo.deployment.ws.DeployWSConstants.REPLACE_EXISTING_ASSEMBLIES;
import static org.intalio.tempo.deployment.ws.DeployWSConstants.ZIP;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.intalio.tempo.deployment.AssemblyId;
import org.intalio.tempo.deployment.DeployedAssembly;
import org.intalio.tempo.deployment.DeploymentResult;
import org.intalio.tempo.deployment.impl.DeploymentServiceImpl;
import org.intalio.tempo.deployment.utils.DeploymentServiceLookup;
import org.intalio.tempo.registry.Registry;
import org.intalio.tempo.registry.RegistryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;


/**
 * Deployment Web Service: handles marshalling/unmarshalling of requests to DeploymentServiceImpl.
 */
public class DeployWS {
    private static final Logger LOG = LoggerFactory.getLogger(DeployWS.class);

    public static final String CONFIG_DIR_PROPERTY = "org.intalio.tempo.configDirectory";
    
    private static boolean _initialized;

    protected static File _configDir;

    protected DeploymentServiceImpl _deployService;
    
    public DeployWS() {
        this(true);
    }

    public DeployWS(boolean bindRegistry) {
        initialize();
        if (bindRegistry) bindRegistry();
    }
    
    protected void initialize() {
        try {
            synchronized (DeployWS.class) {
                if (_initialized)
                    return;
                LOG.debug("Initializing configuration.");
                String configDir = System.getProperty(CONFIG_DIR_PROPERTY);
                if (configDir == null) {
                    throw new RuntimeException("System property " + CONFIG_DIR_PROPERTY + " not defined.");
                }
                _configDir = new File(configDir);
                if (!_configDir.exists()) {
                    throw new RuntimeException("Configuration directory " + _configDir.getAbsolutePath()
                            + " doesn't exist.");
                }
                ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
                Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
                try {
                    FileSystemResource config = new FileSystemResource(new File(_configDir, "tempo-deploy.xml"));
                    XmlBeanFactory factory = new XmlBeanFactory(config);
        
                    PropertyPlaceholderConfigurer propsCfg = new PropertyPlaceholderConfigurer();
                    propsCfg.setSearchSystemEnvironment(true);
                    propsCfg.postProcessBeanFactory(factory);
                    _deployService = (DeploymentServiceImpl) factory.getBean("deploymentService");
                    _deployService.init();
                    _deployService.start();
                    _initialized = true;
                } finally {
                    Thread.currentThread().setContextClassLoader(oldClassLoader);
                }
            }
        } catch (RuntimeException except) {
            LOG.error("Error during initialization of deployment service", except);
            throw except;
        }
    }

    protected void bindRegistry() {
        try {
            DeploymentServiceLookup lookup = new DeploymentServiceLookup();
            lookup.loadProperties();

            RegistryFactory registryFactory = new RegistryFactory();
            registryFactory.init();
            Registry registry = registryFactory.getRegistry();
            
            LOG.info("Registering DeploymentService at "+lookup.deploymentServiceName);
            registry.bind(lookup.deploymentServiceName, _deployService);

            LOG.info("Registering DeploymentServiceCallback at "+lookup.deploymentCallbackName);
            registry.bind(lookup.deploymentCallbackName, _deployService.getCallback());
        } catch (RuntimeException except) {
            LOG.error("Error during initialization of deployment service", except);
            throw except;
        } catch (Exception except) {
            LOG.error("Error during initialization of deployment service", except);
            throw new RuntimeException(except);
        }
    }

    public OMElement deployAssembly(OMElement requestEl) throws AxisFault {
        if (!_initialized) throw new IllegalStateException("Deployment service not initialized");
        
        OMParser request = new OMParser(requestEl);
        String assemblyName = request.getRequiredString(ASSEMBLY_NAME);
        boolean replaceExistingAssemblies = request.getRequiredBoolean(REPLACE_EXISTING_ASSEMBLIES);
        InputStream zip = request.getInputStream(ZIP);
        DeploymentResult result = _deployService.deployAssembly(assemblyName, zip, replaceExistingAssemblies);
        return OMParser.marshallDeploymentResult(result);
    }
    
    public OMElement undeployAssembly(OMElement requestEl) throws AxisFault {
        if (!_initialized) throw new IllegalStateException("Deployment service not initialized");
        OMParser request = new OMParser(requestEl);
        String assemblyName = request.getRequiredString(ASSEMBLY_NAME);
        int assemblyVersion = request.getRequiredInt(ASSEMBLY_VERSION);
        AssemblyId aid = new AssemblyId(assemblyName, assemblyVersion);
        DeploymentResult result = _deployService.undeployAssembly(aid);
        return OMParser.marshallDeploymentResult(result);
    }

    public OMElement getDeployedAssemblies(OMElement requestEl) throws AxisFault {
        if (!_initialized) throw new IllegalStateException("Deployment service not initialized");
        Collection<DeployedAssembly> assemblies = _deployService.getDeployedAssemblies();
        return OMParser.marshallGetDeployedAssemblies(assemblies);
    }

}
