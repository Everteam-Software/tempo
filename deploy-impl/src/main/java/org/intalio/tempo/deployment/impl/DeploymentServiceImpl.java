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

import static org.intalio.tempo.deployment.impl.LocalizedMessages._;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.sql.DataSource;

import org.intalio.tempo.deployment.AssemblyId;
import org.intalio.tempo.deployment.ComponentId;
import org.intalio.tempo.deployment.DeployedAssembly;
import org.intalio.tempo.deployment.DeployedComponent;
import org.intalio.tempo.deployment.DeploymentMessage;
import org.intalio.tempo.deployment.DeploymentResult;
import org.intalio.tempo.deployment.DeploymentService;
import org.intalio.tempo.deployment.DeploymentMessage.Level;
import org.intalio.tempo.deployment.spi.ComponentManager;
import org.intalio.tempo.deployment.spi.ComponentManagerResult;
import org.intalio.tempo.deployment.spi.DeploymentServiceCallback;    

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SystemPropertyUtils;

/**
 * Deployment service
 */
public class DeploymentServiceImpl implements DeploymentService, Remote, ClusterListener {
    private static final Logger LOG = LoggerFactory.getLogger(DeploymentServiceImpl.class);

    // Constants

    public static final String DEFAULT_DEPLOY_DIR = "${org.intalio.tempo.configDirectory}/../deploy";

    public static final String DEPLOY_COMPONENT = "TempoDeploymentService";
    
    //
    // Configuration
    //

    private int _scanPeriod = 5; // in seconds

    private String _deployDir = SystemPropertyUtils.resolvePlaceholders(DEFAULT_DEPLOY_DIR);

    private List<String> _requiredComponentManagers = new ArrayList<String>();

    //
    // Internal state
    //

    enum ServiceState {
        INITIALIZED, CLUSTERIZING, STARTING, STARTED, STOPPING
    }

    private ServiceState _serviceState;

    /**
     * Mapping of [componentType] to [ComponentManager name] e.g. "BPEL" => "ApacheOde"
     */
    private final Map<String, String> _componentTypes = 
        Collections.synchronizedMap(new HashMap<String, String>());

    /**
     * Mapping of [name] to [DeploymentManager] e.g. "MyApplication" => OdeComponentManager
     */
    private final Map<String, ComponentManager> _componentManagers = 
        Collections.synchronizedMap(new HashMap<String, ComponentManager>());

    private final Object LIFECYCLE_LOCK = new Object();

    private final Object DEPLOY_LOCK = new Object();

    private Callback _callback = new Callback();


    //
    // Services
    //

    private Timer _timer;

    private final StartTask _startTask = new StartTask();

    private final TimerTask clusterizeTask = new TimerTask() {
    	public void run() {
    		try {
    			cluster.start();
    			onClustered();
    		} catch( Exception e ) {
    			e.printStackTrace();
    		}
    	}
    };

    private final ScanTask _scanTask = new ScanTask();

    private DataSource _dataSource;

    private Persistence _persist;

    private Cluster cluster = new SingleNodeCluster();
    
    //
    // Constructor
    //

    public DeploymentServiceImpl() {
    }

    //
    // Accessors / Setters
    //

    public Cluster getCluster() {
		return cluster;
	}

	public void setCluster(Cluster cluster) {
		this.cluster = cluster;
	}

	public String getDeployDirectory() {
        return _deployDir;
    }

    public void setDeployDirectory(String path) {
        _deployDir = SystemPropertyUtils.resolvePlaceholders(path);
    }

    public int getScanPeriod() {
        return _scanPeriod;
    }

    public void setScanPeriod(int scanPeriod) {
        _scanPeriod = scanPeriod;
    }

    public void addComponentTypeMapping(String componentType, String componentManager) {
        _componentTypes.put(componentType, componentManager);
    }

    public void removeComponentTypeMapping(String componentType) {
        _componentTypes.remove(componentType);
    }

    public List<String> getRequiredComponentManagers() {
        return _requiredComponentManagers;
    }

    public void setRequiredComponentManagers(List<String> componentManagers) {
        _requiredComponentManagers = componentManagers;
    }

    public void addRequiredComponentManager(String componentManager) {
        _requiredComponentManagers.add(componentManager);
    }

    public void removeRequiredComponentManager(String componentManager) {
        _requiredComponentManagers.remove(componentManager);
    }

    public DeploymentServiceCallback getCallback() {
        return _callback;
    }

    public DataSource getDataSource() {
        return _dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        _dataSource = dataSource;
    }
    
    //
    // Lifecycle Methods
    //

    /**
     * Initialize the service
     */
    public void init() {
        synchronized (LIFECYCLE_LOCK) {
            if (_serviceState != null) {
                throw new IllegalStateException("Service already initialized");
            }
            ensureDeploymentDirExists();
            if (_dataSource == null) throw new IllegalStateException("Datasource not set");
            
            _persist = new Persistence(new File(_deployDir), _dataSource);
            _timer = new Timer("Deployment Service Timer", true);
            _serviceState = ServiceState.INITIALIZED;
            LOG.info(_("DeploymentService state is now INITIALIZED"));
        }
    }

    /**
     * Start the service
     */
    public void start() {
        synchronized (LIFECYCLE_LOCK) {
            if (_serviceState != ServiceState.INITIALIZED) {
                throw new IllegalStateException("Service not initialized");
            }
            _serviceState = ServiceState.CLUSTERIZING;
            LOG.info(_("DeploymentService state is now CLUSTERIZING"));
            _timer.schedule(clusterizeTask, 0);
        }
    }

    public void onClustered() {
        synchronized (LIFECYCLE_LOCK) {
            if (_serviceState != ServiceState.CLUSTERIZING) {
                throw new IllegalStateException("Service not in clusterizing mode.");
            }
            _serviceState = ServiceState.STARTING;
            LOG.info(_("DeploymentService state is now STARTING"));
            checkRequiredComponentManagersAvailable();
        }
    }

    /**
     * Return true if service is started.
     */
    public boolean isStarted() {
        synchronized (LIFECYCLE_LOCK) {
            return _serviceState == ServiceState.STARTED;
        }
    }

    /**
     * Stop the service
     */
    public void stop() {
        synchronized (LIFECYCLE_LOCK) {
            if (_serviceState == ServiceState.STARTED) {
                _timer.cancel();
                cluster.shutdown();
            }
            _serviceState = ServiceState.STOPPING;
            LOG.info(_("DeploymentService state is now STOPPING"));

            Collection<DeployedAssembly> assemblies = getDeployedAssemblies();
            stopAndDispose(assemblies);

            _serviceState = null;
            LOG.info(_("DeploymentService state is now STOPPED"));
        }
    }

    //
    // Operations
    //
    public DeploymentResult deployAssembly(String assemblyName, InputStream zip, 
            DeployControlParam param ) throws RemoteException {
    	if( DeployControlParam.REPLACE_EXISTING_ASSEMBLIES_AND_ACTIVATE.equals(param) ) {
    		return deployAssembly(assemblyName, zip, true, true);
    	} else if( DeployControlParam.DO_NOT_ACTIVATE.equals(param) ) {
    		return deployAssembly(assemblyName, zip, false, false);
    	} else {
    		return deployAssembly(assemblyName, zip, false, true);
    	}
    }

    public DeploymentResult deployAssembly(String assemblyName, InputStream zip, boolean replaceExistingAssemblies) {
    	return deployAssembly(assemblyName, zip, replaceExistingAssemblies, true);
    }

    /**
     * Deploy a packaged (zipped) assembly
     */
    DeploymentResult deployAssembly(String assemblyName, InputStream zip, boolean replaceExistingAssemblies, boolean activate) {
        assertStarted();

        if (assemblyName.indexOf(".") >= 0) {
            Exception except = new Exception(_("Assembly name cannot contain dot character ('.'): {0}", assemblyName));
            LOG.error(except.getMessage());
            return convertToResult(except, newAssemblyId(assemblyName));
        }

        AssemblyId aid = versionAssemblyId(assemblyName);
        if (replaceExistingAssemblies) {
            Collection<DeployedAssembly> deployed = getDeployedAssemblies();
            for (DeployedAssembly da : deployed) {
                if (da.getAssemblyId().getAssemblyName().equals(aid.getAssemblyName())) {
                    try {
                        stopAndDispose(da.getAssemblyId());
                        DeploymentResult result = undeployAssembly(da);
                        Utils.deleteRecursively(new File(da.getAssemblyDir()));
                        if (!result.isSuccessful()) {
                            return result;
                        }
                    } catch (Exception except) {
                        LOG.error(_("Error while undeploying assembly {0}", da.getAssemblyId()), except);
                        return convertToResult(except, aid);
                    }
                }
            }
        }

        synchronized (DEPLOY_LOCK) {
            try {
                setMarkedAsInvalid(aid, true);

                File assemblyDir = createAssemblyDir(aid);
                DeploymentResult result = null;
                try {
                    Utils.unzip(zip, assemblyDir);
                    result = deployExplodedAssembly(assemblyDir, activate);
                } finally {
                    if (result == null || !result.isSuccessful()) {
                        Utils.deleteRecursively(assemblyDir);
                    }
                }
                return result;
            } catch (Exception except) {
                throw new RuntimeException(except);
            } finally {
                setMarkedAsInvalid(aid, false);
            }
        }
    }

    /**
     * Deploy an exploded assembly
     */
    public DeploymentResult deployExplodedAssembly(File assemblyDir, boolean activate) {
        File parent = assemblyDir.getParentFile();
        while (true) {
            if (parent == null)
                break;
            parent = parent.getParentFile();
            if (_deployDir.equals(parent)) {
                throw new IllegalArgumentException(
                        "Assembly directory must either be direct child of deployment directory, or outside the deployment area: assemblDir="
                                + assemblyDir + " deploymentdir: " + _deployDir);
            }
        }

        AssemblyId aid;
        boolean local = assemblyDir.getParentFile().equals(new File(_deployDir));
        if (local) {
            aid = parseAssemblyId(assemblyDir.getName());
            if (isMarkedAsDeployed(aid)) {
                throw new RuntimeException("Assembly already deployed: " + aid);
            }
        } else {
            if (assemblyDir.getName().indexOf(".") >= 0) 
                throw new IllegalArgumentException("Assembly dir cannot contain dot '.' character: "+assemblyDir);
            aid = versionAssemblyId(assemblyDir.getName());
        }

        // mark as invalid while we deploy to avoid concurrency issues with scanner
        setMarkedAsInvalid(aid, true);

        // if assemblyDir is outside deployDir, copy files into deployDir
        if (!local) {
            File d = getAssemblyDir(aid);
            try {
                Utils.copyRecursively(assemblyDir, d);
            } catch (IOException except) {
                Utils.deleteRecursively(d);
                throw new RuntimeException(except);
            }
        }

        TemporaryResult result = new TemporaryResult(aid);
        List<DeployedComponent> deployed = new ArrayList<DeployedComponent>();

        synchronized (DEPLOY_LOCK) {
            try {
                _persist.startTransaction();

                try {
                    File[] files = assemblyDir.listFiles();

                    // deploy each component
                    for (File f : files) {
                        if (!f.isDirectory()) {
                            // ignore files at top-level
                            continue;
                        }
                        int dot = f.getName().lastIndexOf('.');
                        if (dot < 0) {
                            // ignore directories without extension (no mapping)
                            continue;
                        }
                        String componentManager = f.getName().substring(dot+1);
                        String componentName = f.getName().substring(0, dot);
                        ComponentId component = new ComponentId(aid, componentName);

                        ComponentManager manager = getComponentManager(componentManager);
                        try {
                            result.addAll(component, componentManager, manager.deploy(component, f, activate).getMessages());
                            deployed.add(new DeployedComponent(component, f.getAbsolutePath(), componentManager));
                        } catch (Exception except) {
                            String msg = _("Exception while deploying component {0}: {1}", componentName, except.getLocalizedMessage());
                            result.add(component, componentManager, error(msg));
                            LOG.error(msg, except);
                        }
                    }
                } catch (Exception except) {
                    String msg = _("Exception while deploying assembly {0}: {1}", aid, except.getLocalizedMessage());
                    result.add(null, null, error(msg));
                    LOG.error(msg, except);
                }

                if (result.isSuccessful()) {
                    // update persistent state
                    DeployedAssembly assembly = loadAssemblyState(aid);
                    
                    _persist.retire(aid.getAssemblyName());
                    _persist.add(assembly);

                    _persist.commitTransaction();
                    
                    deployed(assembly, activate);
                    
                    cluster.sendMessage(new DeployedMessage(assembly, activate));
                    
                    initializeAndStart(aid);
                } else {
                    // in case of failure, we undeploy already deployed components
                    for (DeployedComponent dc : deployed) {
                        try {
                            ComponentManager manager = getComponentManager(dc.getComponentManagerName());
                            manager.undeploy(dc.getComponentId(), new ArrayList<String>());
                        } catch (Exception except) {
                            String msg = _("Exception while undeploying component {0} after failed deployment: {1}", dc.getComponentId(),
                                    except.getLocalizedMessage());
                            result.add(dc, error(msg));
                            LOG.error(msg, except);
                        }
                    }
                    _persist.rollbackTransaction("Deployment errors");
                }

                setMarkedAsDeployed(aid, result.isSuccessful());
                setMarkedAsInvalid(aid, false);
            } finally {
                _persist.rollbackTransaction("Unknown reason"); 
            }
        }
        
        return result.finalResult();
    }

    /**
     * Undeploy an assembly by name
     */
    public DeploymentResult undeployAssembly(AssemblyId aid) {
        assertStarted();
        
        if (!exist(aid))
            return errorResult(aid, "Assembly directory does not exist: {0}", aid);
        
        DeployedAssembly assembly = loadAssemblyState(aid);
        stopAndDispose(aid);
        if (cluster.isCoordinator()) {
            cluster.sendMessage(new UndeployedMessage(assembly));
        }
        onUndeployed(assembly);
        
        try {
            return undeployAssembly(assembly);
        } finally {
            try {
                Utils.deleteRecursively(new File(assembly.getAssemblyDir()));
            } catch (Exception e) {
                LOG.warn(_("Exception while undeploying assembly {0}: {1}", assembly.getAssemblyId(), e.toString()));
            }
        }
    }

    /**
     * Scan all exploded assemblies in the deployment directory, and optionally
     * start newly deployed assemblies.
     */
    public void scan() {
    	if( !cluster.isCoordinator() ) {
    		return;
    	}
    	
        LOG.debug(_("Scanning deployment directory {0}", _deployDir));
        LOG.debug(_("Component managers: {0}", _componentManagers));
        synchronized (DEPLOY_LOCK) {
            Map<AssemblyId, DeployedAssembly> deployedMap = _persist.load();
            LOG.debug(_("Deployed assemblies: {0}", deployedMap.keySet()));

            Set<AssemblyId> available = new HashSet<AssemblyId>();
            // read available assemblies
            {
                File[] files = new File(_deployDir).listFiles();
                if (files == null) {
                    LOG.warn(_("Deployment directory not available: {0}", _deployDir));
                    return;
                }
                
                for (int i = 0; i < files.length; ++i) {
                    if (files[i].isDirectory()) {
                        AssemblyId aid = parseAssemblyId(files[i].getName());
                        available.add(aid);
                    }
                }
            }

            // Phase 1: undeploy missing assemblies
            Set<DeployedAssembly> undeploy = new HashSet<DeployedAssembly>();

            // check for previously deployed but now missing
            for (DeployedAssembly assembly : deployedMap.values()) {
                if (!available.contains(assembly.getAssemblyId()))
                    undeploy.add(assembly);
            }

            // check for available but deployed flag missing
            for (AssemblyId aid : available) {
                DeployedAssembly assembly = deployedMap.get(aid);
                if (assembly != null && !isMarkedAsDeployed(aid))
                    undeploy.add(assembly);
            }

            // stop and dispose all at once
            stopAndDispose(undeploy);

            for (DeployedAssembly assembly : undeploy) {
                DeploymentResult result = undeployAssembly(assembly);
                if (result.isSuccessful())
                    LOG.info(_("Undeployed assembly: {0}", assembly.getAssemblyId()));
                else
                    LOG.error(_("Error while undeploying assembly {0}: {1}", assembly.getAssemblyId()), result);
                deployedMap.remove(assembly.getAssemblyId());
            }

            // phase 2: deploy new assemblies
            File[] files = new File(_deployDir).listFiles();
            for (int i = 0; i < files.length; ++i) {
                if (files[i].isDirectory()) {
                    AssemblyId aid = parseAssemblyId(files[i].getName());
                    if (!isMarkedAsDeployed(aid) && !isMarkedAsInvalid(aid)) {
                        try {
                        	// auto-detected assemblies are always activated after deployment
                            DeploymentResult result = deployExplodedAssembly(files[i], true);
                            if (_serviceState == ServiceState.STARTED) {
                                initializeAndStart(aid);
                            }
                            if (result.isSuccessful())
                                LOG.info(_("Deployed Assembly: {0}", result));
                            else 
                                LOG.warn(_("Assembly deployment failed: {0}", result));
                            
                            setMarkedAsInvalid(aid, !result.isSuccessful());
                        } catch (Exception except) {
                            LOG.error(_("Error deploying assembly {0}. Assembly will be marked as invalid.", files[i]), except);
                            setMarkedAsInvalid(aid, true);
                        }
                    }
                }
            }
        }
    }

    /**
     * Obtain the current list of deployed assemblies
     */
    public Collection<DeployedAssembly> getDeployedAssemblies() {
        synchronized (DEPLOY_LOCK) {
            Map<AssemblyId, DeployedAssembly> assemblies = _persist.load();
            return assemblies.values();
        }
    }

    public Collection<DeployedAssembly> readDeployedAssemblies() {
        List<DeployedAssembly> assemblies = new ArrayList<DeployedAssembly>();
        synchronized (DEPLOY_LOCK) {
            File[] files = new File(_deployDir).listFiles();
            for (int i = 0; i < files.length; ++i) {
                if (files[i].isDirectory()) {
                    AssemblyId aid = parseAssemblyId(files[i].getName());
                    if (isMarkedAsDeployed(aid) && !isMarkedAsInvalid(aid)) {
                        try {
                            assemblies.add(loadAssemblyState(aid));
                        } catch (Exception except) {
                            LOG.error(_("Error reading assembly state {0}", aid), except);
                        }
                    }
                }
            }
        }
        return assemblies;
    }

    //
    // Private / Protected Internal Methods
    //

    /**
     * Undeploy an assembly
     */
    private DeploymentResult undeployAssembly(DeployedAssembly assembly) {
        AssemblyId aid = assembly.getAssemblyId();

        TemporaryResult result = new TemporaryResult(aid);
        synchronized (DEPLOY_LOCK) {
            try {
                // undeploy all components
                for (DeployedComponent dc : assembly.getDeployedComponents()) {
                    try {
                        ComponentManager manager = getComponentManager(dc.getComponentManagerName());
                        manager.undeploy(dc.getComponentId(), new ArrayList<String>());
                    } catch (Exception except) {
                        String msg = _("Exception while undeploying component {0}: {1}", dc.getComponentId(), except.getLocalizedMessage());
                        result.add(dc, error(msg));
                        LOG.error(msg, except);
                    }
                }
            } catch (Exception except) {
                String msg = _("Exception while undeploying assembly {0}: {1} ", aid, except.getLocalizedMessage());
                result.add(null, null, error(msg));
                LOG.error(msg, except);
            } finally {
                // update persistent state
                _persist.remove(aid);

                if (!result.isSuccessful() && exist(aid)) {
                    setMarkedAsInvalid(aid, true);
                }
                setMarkedAsDeployed(aid, false);
            }
        }
        return result.finalResult();
    }

    public void onDeployed(DeployedAssembly assembly, boolean activate) {
        deployed(assembly, activate);

        initializeAndStart(assembly.getAssemblyId());
    }
    
    private void deployed(DeployedAssembly assembly, boolean activate) {
        for (DeployedComponent dc : assembly.getDeployedComponents()) {
            try {
                LOG.debug(_("Deployed component {0}", dc));
                ComponentManager manager = getComponentManager(dc.getComponentManagerName());
                manager.deployed(dc.getComponentId(), new File(dc.getComponentDir()), activate);
            } catch (Exception except) {
                String msg = _("Error during deployment notification of component {0}: {1}", dc.getComponentId(), except);
                LOG.error(msg, except);
                break;
            }
        }
    }

    public void onUndeployed(DeployedAssembly assembly) {
        for (DeployedComponent dc : assembly.getDeployedComponents()) {
            try {
                LOG.debug(_("Undeployed component {0}", dc));
                ComponentManager manager = getComponentManager(dc.getComponentManagerName());
                manager.undeployed(dc.getComponentId());
            } catch (Exception except) {
                String msg = _("Error during undeployment notification of component {0}: {1}", dc.getComponentId(), except);
                LOG.error(msg, except);
                break;
            }
        }
    }
    
    public void onActivated(DeployedAssembly assembly) {
        for (DeployedComponent dc : assembly.getDeployedComponents()) {
            try {
                LOG.debug(_("Activated component {0}", dc));
                ComponentManager manager = getComponentManager(dc.getComponentManagerName());
                manager.activated(dc.getComponentId());
            } catch (Exception except) {
                String msg = _("Error during activation notification of component {0}: {1}", dc.getComponentId(), except);
                LOG.error(msg, except);
                break;
            }
        }
    }
    
    public void onRetired(DeployedAssembly assembly) {
        for (DeployedComponent dc : assembly.getDeployedComponents()) {
            try {
                LOG.debug(_("Retired component {0}", dc));
                ComponentManager manager = getComponentManager(dc.getComponentManagerName());
                manager.retired(dc.getComponentId());
            } catch (Exception except) {
                String msg = _("Error during retirement notification of component {0}: {1}", dc.getComponentId(), except);
                LOG.error(msg, except);
                break;
            }
        }
    }

    private DeployedAssembly initializeAndStart(AssemblyId aid) {
        DeployedAssembly assembly = loadAssemblyState(aid);
        List<DeployedAssembly> assemblies = new ArrayList<DeployedAssembly>();
        assemblies.add(assembly);
        initializeAndStart(assemblies);
        
        return assembly;
    }

    private boolean initializeAndStart(Collection<DeployedAssembly> assemblies) {
        boolean success = true;

        // Phase 1: Initialize all components of all assemblies
        List<DeployedComponent> initialized = new ArrayList<DeployedComponent>();
        for (DeployedAssembly assembly : assemblies) {
            for (DeployedComponent dc : assembly.getDeployedComponents()) {
                try {
                    LOG.debug(_("Initialize component {0}", dc));
                    ComponentManager manager = getComponentManager(dc.getComponentManagerName());
                    manager.initialize(dc.getComponentId(), new File(dc.getComponentDir()));
                    initialized.add(dc);
                } catch (Exception except) {
                    success = false;
                    String msg = _("Error during activation of component {0}: {1}", dc.getComponentId(), except);
                    LOG.error(msg, except);
                    break;
                }
            }
        }

        if (success) {
            // Phase 2: Startup all components
            for (DeployedAssembly assembly : assemblies) {
                for (DeployedComponent dc : assembly.getDeployedComponents()) {
                    try {
                        LOG.debug(_("Start component {0}", dc));
                        ComponentManager manager = getComponentManager(dc.getComponentManagerName());
                        manager.start(dc.getComponentId());
                    } catch (Exception except) {
                        String msg = _("Error during startup of component {0}: {1}", dc.getComponentId(), except);
                        LOG.error(msg, except);
                    }
                }
            }
        } else {
            for (DeployedComponent dc : initialized) {
                try {
                    ComponentManager manager = getComponentManager(dc.getComponentManagerName());
                    manager.dispose(dc.getComponentId());
                } catch (Exception except) {
                    String msg = _("Error during deactivation of component {0} after startup failure: {1}", dc.getComponentId(), except);
                    LOG.error(msg, except);
                }
            }
        }
        
        return success;
    }

    private void stopAndDispose(AssemblyId aid) {
        DeployedAssembly assembly = loadAssemblyState(aid);
        List<DeployedAssembly> assemblies = new ArrayList<DeployedAssembly>();
        assemblies.add(assembly);
        stopAndDispose(assemblies);
    }

    private void stopAndDispose(Collection<DeployedAssembly> assemblies) {
        // Phase 1: Stop all components
        for (DeployedAssembly assembly : assemblies) {
            for (DeployedComponent dc : assembly.getDeployedComponents()) {
                ComponentManager manager = getComponentManager(dc.getComponentManagerName());
                try {
                    LOG.debug(_("Stop component {0}", dc));
                    manager.stop(dc.getComponentId());
                } catch (Exception except) {
                    String msg = _("Error while stopping component {0}: {1}", dc.getComponentId(), except);
                    LOG.error(msg, except);
                }
            }
        }

        // Phase 2: Dispose all components
        for (DeployedAssembly assembly : assemblies) {
            for (DeployedComponent dc : assembly.getDeployedComponents()) {
                ComponentManager manager = getComponentManager(dc.getComponentManagerName());
                try {
                    LOG.debug(_("Dispose component {0}", dc));
                    manager.dispose(dc.getComponentId());
                } catch (Exception except) {
                    String msg = _("Error while disposing component {0}: {1}", dc.getComponentId(), except);
                    LOG.error(msg, except);
                }
            }
        }
    }

	public void activate(AssemblyId assemblyId) {
        DeployedAssembly assembly = loadAssemblyState(assemblyId);
        for (DeployedComponent dc : assembly.getDeployedComponents()) {
            ComponentManager manager = getComponentManager(dc.getComponentManagerName());
            try {
                LOG.debug(_("Activate component {0}", dc));
                manager.activate(dc.getComponentId());
            } catch (Exception except) {
                String msg = _("Error while activating component {0}: {1}", dc.getComponentId(), except);
                LOG.error(msg, except);
            }
        }
        
        cluster.sendMessage(new ActivatedMessage(assembly));
	}

	public void retire(AssemblyId assemblyId) {
        DeployedAssembly assembly = loadAssemblyState(assemblyId);
        for (DeployedComponent dc : assembly.getDeployedComponents()) {
            ComponentManager manager = getComponentManager(dc.getComponentManagerName());
            try {
                LOG.debug(_("Retire component {0}", dc));
                manager.retire(dc.getComponentId());
            } catch (Exception except) {
                String msg = _("Error while retiring component {0}: {1}", dc.getComponentId(), except);
                LOG.error(msg, except);
            }
        }

        cluster.sendMessage(new RetiredMessage(assembly));
	}

	private void checkRequiredComponentManagersAvailable() {
        boolean available = true;
        StringBuffer missing = new StringBuffer();
        for (String cm : _requiredComponentManagers) {
            if (!_componentManagers.containsKey(cm)) {
                if (missing.length() > 0)
                    missing.append(", ");
                missing.append(cm);
                available = false;
            }
        }
        synchronized (LIFECYCLE_LOCK) {
            if (ServiceState.STARTING.equals(_serviceState) && available) {
                synchronized (_startTask) {
                    if (!_startTask.scheduled) {
                        _startTask.scheduled = true;
                        _timer.schedule(_startTask, 0);
                    }
                }
            }
        }

        if (!available)
            LOG.info(_("Waiting for component managers: {0}", missing));
    }

    public void sayHello() throws Exception {
    	cluster.sayHello();
    }
    
    private void internalStart() {
        synchronized (DEPLOY_LOCK) {
            try {
                scan();
            } catch (Exception e) {
                LOG.error(_("Error while scanning deployment repository"), e);
            }

            Collection<DeployedAssembly> assemblies = getDeployedAssemblies();
            // let the runtime ComponentManagers be aware of the deployed components
            for( DeployedAssembly assembly : assemblies ) {
            	for( DeployedComponent component : assembly.getDeployedComponents() ) {
            		ComponentManager manager = _componentManagers.get(component.getComponentManagerName());
            		manager.deployed(component.getComponentId(), new File(component.getComponentDir()), false);
            	}
            }
            
            if (initializeAndStart(assemblies)) {
                _serviceState = ServiceState.STARTED;
                LOG.info(_("DeploymentService state is now STARTED"));

                _timer.schedule(_scanTask, _scanPeriod * 1000, _scanPeriod * 1000);
            }
        }
    }


    /**
     * Ensure deployment directory exists
     */
    private void ensureDeploymentDirExists() {
        if (_deployDir.contains("${"))
            throw new IllegalStateException("Invalid deployment directory: " + _deployDir);
        File dir = new File(_deployDir);
        if (dir.exists() && !dir.isDirectory()) {
            throw new RuntimeException("Deployment path exists but is not a directory: " + _deployDir);
        }
        if (!dir.exists()) {
            LOG.debug("Creating deployment directory: " + _deployDir);
            boolean created = dir.mkdirs();
            if (!created) {
                throw new RuntimeException("Unable to create deployment directory: " + _deployDir);
            }
        }
    }

    /**
     * Create an assembly directory
     */
    private File createAssemblyDir(AssemblyId aid) {
        File dir = getAssemblyDir(aid);
        if (dir.exists()) {
            throw new IllegalStateException("Deployment path already exists: " + dir);
        }
        LOG.debug("Creating deployment directory: " + _deployDir);
        boolean created = dir.mkdirs();
        if (!created) {
            throw new IllegalStateException("Unable to create deployment directory: " + _deployDir);
        }
        return dir;
    }

    /**
     * Create a unique assembly id, e.g. myAssembly.1, myAssembly.2, ...
     */
    private AssemblyId versionAssemblyId(String assemblyName) {
        int version = AssemblyId.NO_VERSION;
        if (new File(_deployDir, assemblyName).exists())
            version = 2;
        File[] files = Utils.listFiles(new File(_deployDir), assemblyName+".*");
        for (File f: files) {
            String name = f.getName();
            int pos = name.lastIndexOf(".");
            try {
                int v = Integer.parseInt(name.substring(pos+1));
                if (version == AssemblyId.NO_VERSION || v >= version) 
                    version = v+1;
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        return new AssemblyId(assemblyName, version);
    }

    DeployedAssembly loadAssemblyState(AssemblyId aid) {
        File assemblyDir = getAssemblyDir(aid);
        if (!assemblyDir.exists()) {
            throw new IllegalStateException("Assembly does not exist: " + aid);
        }
        if (!assemblyDir.isDirectory()) {
            throw new IllegalArgumentException("Assembly name does not map to a directory: " + assemblyDir);
        }

        List<DeployedComponent> components = new ArrayList<DeployedComponent>();

        File[] files = assemblyDir.listFiles();

        for (File componentDir : files) {
            if (!componentDir.isDirectory()) {
                // ignore files at top-level
                continue;
            }
            int dot = componentDir.getName().lastIndexOf('.');
            if (dot < 0) {
                // ignore directories without extension (no mapping)
                continue;
            }
            String componentManager = componentDir.getName().substring(dot+1);
            String componentName = componentDir.getName().substring(0, dot);
            ComponentId component = new ComponentId(aid, componentName);
            components.add(new DeployedComponent(component, componentDir.getAbsolutePath(), componentManager));
        }
        return new DeployedAssembly(aid, assemblyDir.getAbsolutePath(), components, false);
    }

    private ComponentManager getComponentManager(String componentType) {
        ComponentManager manager = _componentManagers.get(componentType);
        if (manager == null) {
            String componentManagerName = _componentTypes.get(componentType);
            if (componentManagerName != null) {
                manager = _componentManagers.get(componentManagerName);
            }
        }
        if (manager == null)
            manager = new MissingComponentManager(componentType);
        return manager;
    }

    private File getAssemblyDir(AssemblyId aid) {
        return new File(_deployDir, toDirName(aid));
    }

    private boolean exist(AssemblyId aid) {
        return getAssemblyDir(aid).exists();
    }

    private File getDeployedFile(AssemblyId aid) {
        return new File(_deployDir, toDirName(aid) + ".deployed");
    }

    private File getInvalidFile(AssemblyId aid) {
        return new File(_deployDir, toDirName(aid) + ".invalid");
    }

    private String toDirName(AssemblyId aid) {
        if (aid.getAssemblyVersion() == AssemblyId.NO_VERSION)
            return aid.getAssemblyName();
        else
            return aid.getAssemblyName() + "." + aid.getAssemblyVersion();
    }

    private boolean isMarkedAsDeployed(AssemblyId aid) {
        return getDeployedFile(aid).exists();
    }

    private void setMarkedAsDeployed(AssemblyId aid, boolean isDeployed) {
        File deployed = getDeployedFile(aid);
        if (isDeployed)
            Utils.createFile(deployed);
        else
            Utils.deleteFile(deployed);
    }

    private boolean isMarkedAsInvalid(AssemblyId aid) {
        return getInvalidFile(aid).exists();
    }

    private void setMarkedAsInvalid(AssemblyId aid, boolean isValid) {
        File invalid = getInvalidFile(aid);
        if (isValid)
            Utils.createFile(invalid);
        else
            Utils.deleteFile(invalid);
    }

    private DeploymentResult convertToResult(Exception except, AssemblyId aid) {
        DeploymentMessage msg = new DeploymentMessage(Level.ERROR, except.getLocalizedMessage());
        return new DeploymentResult(aid, false, msg);
    }

    private DeploymentMessage error(String message) {
        return new DeploymentMessage(Level.ERROR, message);
    }

    private DeploymentMessage error(String pattern, Object... arguments) {
        return new DeploymentMessage(Level.ERROR, _(pattern, arguments));
    }

    private DeploymentResult errorResult(AssemblyId aid, String pattern, Object... arguments) {
        DeploymentMessage msg = error(pattern, arguments);
        DeploymentResult result = new DeploymentResult(aid, false, msg);
        return result;
    }

    private AssemblyId parseAssemblyId(String dirName) {
        String assemblyName = dirName;
        int version = AssemblyId.NO_VERSION;
        int pos = dirName.length();
        while (pos > 1) {
            pos--;
            char c = dirName.charAt(pos);
            if (Character.isDigit(c))
                continue;
            if (c != '.')
                break;
            version = Integer.parseInt(dirName.substring(pos + 1));
            assemblyName = dirName.substring(0, pos);
        }
        return new AssemblyId(assemblyName, version);
    }

    private AssemblyId newAssemblyId(String assemblyName) {
        return new AssemblyId(assemblyName, AssemblyId.NO_VERSION);
    }

    private void assertStarted() {
        synchronized (LIFECYCLE_LOCK) {
        	int secs = 10;
            while (secs-- > 0 && _serviceState != ServiceState.STARTED) {
                try {
                	LOG.info("Deployment has been requested. However, the service is still starting up(retrying in 1 sec).");
					LIFECYCLE_LOCK.wait(1000);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
            }
            if (_serviceState != ServiceState.STARTED) {
                throw new IllegalStateException(_("Service not started.  Current state is {0}", _serviceState));
            }
        }
    }

    //
    // Nested Classes
    //

    /**
     * Recurring scan of the deployment directory every "scanPeriod"
     * milliseconds
     */
    class StartTask extends TimerTask {
        boolean scheduled = false;
        
        public void run() {
            internalStart();
            synchronized (this) {
                scheduled = false;
            }
        }
    }

    /**
     * Recurring scan of the deployment directory every "scanPeriod"
     * milliseconds
     */
    class ScanTask extends TimerTask {
        public void run() {
            synchronized (LIFECYCLE_LOCK) {
                if (!ServiceState.STARTED.equals(_serviceState)) {
                    _timer.cancel();
                    return;
                }
            }
            try {
                scan();
            } catch (Exception e) {
                LOG.error("Error while scanning deployment repository", e);
            }
        }
    }

    /**
     * Callback when ComponentManager's become available/unavailable.
     * <p>
     * Note:  This implementation class needs to be public due to Java reflection limitations
     *        http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4071957
     */
    public class Callback implements DeploymentServiceCallback {

        public void available(ComponentManager manager) {
            String name = manager.getComponentManagerName();
            _componentManagers.put(name, manager);
            LOG.info(_("ComponentManager now available: {0}", name));
            
            synchronized (LIFECYCLE_LOCK) {
                if (ServiceState.STARTED.equals(_serviceState)) {
                    synchronized (DEPLOY_LOCK) {
                        Collection<DeployedAssembly> assemblies = getDeployedAssemblies();
                        for (DeployedAssembly assembly : assemblies) {
                            Collection<DeployedComponent> components = assembly.getDeployedComponents();
                            for (DeployedComponent component: components) {
                                String type = _componentTypes.get(component.getComponentManagerName());
                                if (name.equals(component.getComponentManagerName()) || name.equals(type)) {
                                    try {
                                        LOG.debug(_("Initialize component {0}", component));
                                        manager.initialize(component.getComponentId(), new File(component.getComponentDir()));
                                    } catch (Exception except) {
                                        LOG.error(_("Error while activating component {0}", component), except);
                                    }
                                    try {
                                        LOG.debug(_("Start component {0}", component));
                                        manager.start(component.getComponentId());
                                    } catch (Exception except) {
                                        LOG.error(_("Error while activating component {0}", component), except);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            checkRequiredComponentManagersAvailable();
        }

        public void unavailable(ComponentManager manager) {
            _componentManagers.remove(manager.getComponentManagerName());
        }

    }

    /**
     * Accumulate results during deployment operations
     */
    class TemporaryResult {
        private AssemblyId _aid;
        boolean _success = true;
        List<DeploymentMessage> _messages = new ArrayList<DeploymentMessage>();

        TemporaryResult(AssemblyId aid) {
            _aid = aid;
        }

        boolean add(DeployedComponent dc, DeploymentMessage msg) {
            return add(dc.getComponentId(), dc.getComponentManagerName(), msg);
        }

        boolean add(ComponentId componentId, String componentManagerName, DeploymentMessage msg) {
            _messages.add(msg);
            msg.setComponentId(componentId);
            msg.setComponentManagerName(componentManagerName);
            if (msg.isError())
                _success = false;
            return msg.isError();
        }

        boolean addAll(DeployedComponent dc, List<DeploymentMessage> messages) {
            return addAll(dc.getComponentId(), dc.getComponentManagerName(), messages);
        }

        boolean addAll(ComponentId ComponentId, String componentManagerName, List<DeploymentMessage> messages) {
            boolean localSuccess = true;
            _messages.addAll(messages);
            for (DeploymentMessage m : messages) {
                m.setComponentId(ComponentId);
                m.setComponentManagerName(componentManagerName);
                if (m.isError()) {
                    _success = false;
                    localSuccess = false;
                }
            }
            return localSuccess;
        }

        boolean isSuccessful() {
            return _success;
        }

        DeploymentResult finalResult() {
            return new DeploymentResult(_aid, _success, _messages);
        }
    }

    class MissingComponentManager implements ComponentManager {

        String _componentType;

        public MissingComponentManager(String componentType) {
            _componentType = componentType;
        }

        List<DeploymentMessage> message(DeploymentMessage msg) {
            List<DeploymentMessage> msgs = new ArrayList<DeploymentMessage>();
            msgs.add(msg);
            return msgs;
        }

        public void initialize(ComponentId name, File path) {
            LOG.warn(_("Missing component manager: activate {0} {1}", name, path));
        }

        public void dispose(ComponentId cid) {
            LOG.warn(_("Missing component manager: deactivate {0}", cid));
        }

        public void deployed(ComponentId cid, File path, boolean activate) {
            LOG.warn(_("Missing component manager: deployed {0}", cid));
        }

        public String getComponentManagerName() {
            return _componentType;
        }

        public void start(ComponentId cid) {
            LOG.warn(_("Missing component manager: start {0}", cid));
        }

        public void stop(ComponentId cid) {
            LOG.warn(_("Missing component manager: stop {0}", cid));
        }

        public void undeploy(ComponentId cid, List<String> deployedObjects) {
            LOG.warn(_("Missing component manager: undeploy {0}", cid));
        }

        public void undeployed(ComponentId cid) {
            LOG.warn(_("Missing component manager: undeploy {0}", cid));
        }

		public void activate(ComponentId cid) {
            LOG.warn(_("Missing component manager: undeploy {0}", cid));
		}

		public ComponentManagerResult deploy(ComponentId name, File path,
				boolean activate) {
            String msg = _("No component manager for component type {0}", _componentType);
            return new ComponentManagerResult(message(error(msg)));
		}

		public void retire(ComponentId cid) {
            LOG.warn(_("Missing component manager: undeploy {0}", cid));
		}

		public void activated(ComponentId cid) {
            LOG.warn(_("Missing component manager: undeploy {0}", cid));
		}

		public void retired(ComponentId cid) {
            LOG.warn(_("Missing component manager: undeploy {0}", cid));
		}
    }
    
    static class DeployedMessage implements Serializable {
        private static final long serialVersionUID = 1L;
        
        public DeployedAssembly assembly;
        public boolean activate;

        DeployedMessage(DeployedAssembly assembly, boolean activate) {
            this.assembly = assembly;
            this.activate = activate;
        }
    }
    
    static class UndeployedMessage implements Serializable {
        private static final long serialVersionUID = 1L;

        public DeployedAssembly assembly;

        UndeployedMessage(DeployedAssembly assembly) {
            this.assembly = assembly;
        }
    }

    static class ActivatedMessage implements Serializable {
        private static final long serialVersionUID = 1L;
        
        public DeployedAssembly assembly;

        ActivatedMessage(DeployedAssembly assembly) {
            this.assembly = assembly;
        }
    }

    static class RetiredMessage implements Serializable {
        private static final long serialVersionUID = 1L;
        
        public DeployedAssembly assembly;

        RetiredMessage(DeployedAssembly assembly) {
            this.assembly = assembly;
        }
    }
}

/**
 * 
 * Possible Assembly States (0=missing, 1=exist/deployed)
 * 
 * Assembly .deployed .invalid Assembly 
 * Directory flag/file flag/file State Action(s)
 * ========== ========= ========= ========= ======================================================== 
 * 0 0 0 0 Nothing (no assembly) 
 * 0 0 0 1 Undeploy, remove from deploy.state 
 * 0 0 1 0 Nothing (no assembly) 
 * 0 0 1 1 Undeploy, remove from deploy.state 
 * 0 1 0 0 Nothing (no assembly) 
 * 0 1 0 1 Undeploy, remove from deploy.state 
 * 0 1 1 0 Nothing (no assembly) 
 * 0 1 1 1 Undeploy, remove from deploy.state 1 0 0 0 Deploy* 
 * 1 0 0 1 Undeploy, remove from deploy.state, deploy* 
 * 1 0 1 0 Nothing (invalid) 
 * 1 0 1 1 Undeploy, remove from deploy.state 
 * 1 1 0 0 Nothing (ignore => conservative to avoid undeploy) 
 * 1 1 0 1 Nothing (normal) 
 * 1 1 1 0 Nothing (invalid) 
 * 1 1 1 1 Nothing (already deployed?)
 * 
 * where deploy* = create .invalid, deploy, if successful, create .deployed and remove .invalid
 * 
 * In Java logic,
 * 
 * if (!assemblyDir && deploy.state) undeploy, remove from deploy.state 
 * if (assemblyDir && !.deployed && deploy.state) undeploy, remove from deploy.state 
 * if (assemblyDir && !.deployed && !.invalid && !deploy.state) deploy
 * 
 */
