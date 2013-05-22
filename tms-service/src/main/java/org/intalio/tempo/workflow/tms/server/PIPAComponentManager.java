/*
 * Copyright (c) 2008 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 */

package org.intalio.tempo.workflow.tms.server;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilderFactory;

import org.intalio.deploy.deployment.AssemblyId;
import org.intalio.deploy.deployment.ComponentId;
import org.intalio.deploy.deployment.DeploymentMessage;
import org.intalio.deploy.deployment.DeploymentMessage.Level;
import org.intalio.deploy.deployment.spi.ComponentManagerResult;
import org.intalio.tempo.workflow.auth.AuthException;
import org.intalio.tempo.workflow.task.CustomColumn;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.PIPATaskState;
import org.intalio.tempo.workflow.tms.TMSException;
import org.intalio.tempo.workflow.tms.UnavailableTaskException;
import org.intalio.tempo.workflow.tms.server.dao.ITaskDAOConnection;
import org.intalio.tempo.workflow.tms.server.dao.ITaskDAOConnectionFactory;
import org.jasypt.util.text.BasicTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class PIPAComponentManager implements org.intalio.deploy.deployment.spi.ComponentManager {
    private static final Logger LOG = LoggerFactory.getLogger(PIPAComponentManager.class);

    ITMSServer _tms;
    private ITaskDAOConnectionFactory _taskDAOFactory;
    private HashMap<String, HashSet<AssemblyId>> _versions = new HashMap<String, HashSet<AssemblyId>>();
    private String internalPassword = "verylongpassword";
    private static final String COLUMN_ELEMENT ="column";
    private static final String COLUMN_NAME_ELEMENT ="column_name";


    public String getInternalPassword() {
        return internalPassword;
    }

    public void setInternalPassword(String internalPassword) {
    this.internalPassword = internalPassword;
    }

	public PIPAComponentManager(ITMSServer tms,ITaskDAOConnectionFactory taskDAOFactory) {
        _tms = tms;
        _taskDAOFactory=taskDAOFactory;
    }

    // ------------------ ComponentManager implementation
    // ------------------------

    public String getComponentManagerName() {
        return "pipa";
    }

    public void initialize(ComponentId name, File path, List<String> deployedResources, boolean active) {
        // nothing
        updatePipa(deployedResources,PIPATaskState.READY);
    }

    public void dispose(ComponentId name, File path, List<String> deployedResources, boolean active) {
        // nothing
    }

    public ComponentManagerResult deploy(ComponentId name, File base, boolean activate) {
        List<DeploymentMessage> msgs = new ArrayList<DeploymentMessage>();

        /*
         * ALEX: Disabled until we get token propagation from deploy-impl if
         * (!TokenContext.hasToken()) { msgs.add(new
         * DeploymentMessage(Level.ERROR, "No security context token")); return
         * msgs; }
         */
//        String intaliohash = System.getProperty("intaliohash");

        BasicTextEncryptor encryptor = new BasicTextEncryptor();
        // setPassword uses hash to decrypt password which should be same as hash of encryptor
        encryptor.setPassword("IntalioEncryptedpasswordfortempo#123");
        try {
            // Phase 1: Check for conflicts
            checkDir(base, base, msgs, internalPassword);

            // Stop if any error during checks
            for (DeploymentMessage msg : msgs) {
                if (Level.ERROR.equals(msg.getLevel()))
                    return new ComponentManagerResult(msgs);
            }

            // Phase 2: Actual deployment
            ArrayList<String> urls = new ArrayList<String>();
            processDir(base, base, urls, msgs,encryptor.encrypt(internalPassword));
            return new ComponentManagerResult(msgs, urls);
        } finally {
        }
    }



    public void undeploy(ComponentId name, File path, List<String> deployedResources) {
       undeploy(name, path, deployedResources,false);
    }

    /* Added assembly's state as a new argument to stop showing PIPA task in ui-fw
     * when undeploying the active assembly and have more than one versions remaining.
     * but they all are in Retired state.   
     */
    public void undeploy(ComponentId name, File path, List<String> deployedResources , boolean active) {
       
        // only undeploy if this is the last version of this assembly
        String assembly = name.getAssemblyId().getAssemblyName();
        HashSet<AssemblyId> set = _versions.get(assembly);
        if (set == null || set.size() < 1) {
            // if set is equal to 1, we have one more version remaining.
            // fix for WF-1324
            ITaskDAOConnection dao=null;
            //ITaskDAOConnection is accessed here for fix of JIRA WF-1466
            try {
                dao=_taskDAOFactory.openConnection();
                for (String url : deployedResources) {
                    try {
                       if(LOG.isDebugEnabled()) LOG.debug("versions>> "+_versions.toString());
                        _tms.deletePipa(dao,url,encrypt(internalPassword));
                    } catch (UnavailableTaskException e) {
                        LOG.warn("Undeploy - PIPA not found: " + url);
                    } catch (AuthException e) {
                        LOG.warn("Undeploy - AuthException: " + url, e);
                        break; // fail-fast
                    } catch (TMSException e) {
                        LOG.warn("Undeploy - TMSException: " + url, e);
                        break; // fail-fast
                    }
                }
            }finally{
                if(dao!=null)
                    dao.close();
            }

          //  retire the PIPA Task only if it's assembly is active and there is more than one version of it's assembly exist.
        }else if (active){
            retire(name, path, deployedResources);
          }
    }

    /*
     * Added this method to fix WF-1488 issue
     * 
     */
    public void retireProcess(ComponentId name, File path, List<String> deployedResources,String formURL) {
        ITaskDAOConnection dao=null;

        try {
            dao=_taskDAOFactory.openConnection();
                try {
                	if(LOG.isDebugEnabled()) LOG.debug("versions>> "+_versions.toString());
                    _tms.updatePipa(dao,formURL,encrypt(internalPassword),PIPATaskState.RETIRED);
                } catch (UnavailableTaskException e) {
                    LOG.warn("Undeploy - PIPA not found: " + formURL);
                } catch (AuthException e) {
                    LOG.warn("Undeploy - AuthException: " + formURL, e);

                }

        }finally{
            if(dao!=null)
                dao.close();
        }

  }

    /*
     * Added this method to fix WF-1488 issue
     * 
     */
    public void activateProcess(ComponentId name, File path, List<String> deployedResources, String formURL) {
        ITaskDAOConnection dao=null;
        try {
            dao=_taskDAOFactory.openConnection();
            try {
                if(LOG.isDebugEnabled()) LOG.debug("versions>> "+_versions.toString());
                _tms.updatePipa(dao,formURL,encrypt(internalPassword),PIPATaskState.READY);
            } catch (UnavailableTaskException e) {
                LOG.warn("Undeploy - PIPA not found: " + formURL);
            } catch (AuthException e) {
                LOG.warn("Undeploy - AuthException: " + formURL, e);
            }

        }finally {
            if(dao!=null)
                dao.close();
        }

    }

    public void activate(ComponentId name, File path, List<String> deployedResources) {
        updatePipa(deployedResources,PIPATaskState.READY);
    }

    /*
     * Added this method to fix WF-1488
     * 
     */
    public void retire(ComponentId name, File path, List<String> deployedResources) {
        updatePipa(deployedResources, PIPATaskState.RETIRED);
    }


    public void start(ComponentId name, File path, List<String> deployedResources, boolean active) {
        // nothing
    }

    public void stop(ComponentId name, File path, List<String> deployedResources, boolean active) {
        // nothing
    }
    
    public void deployed(ComponentId name, File path, List<String> deployedResources, boolean active) {
        // increment number of versions for the given assembly
        String assembly = name.getAssemblyId().getAssemblyName();
        HashSet<AssemblyId> set = _versions.get(assembly);
        if (set == null)
            set = new HashSet<AssemblyId>();
        set.add(name.getAssemblyId());
        _versions.put(assembly, set);
    }

    public void undeployed(ComponentId name, File path, List<String> deployedResources) {
        // decrement number of versions for the given assembly
        String assembly = name.getAssemblyId().getAssemblyName();
        HashSet<AssemblyId> set = _versions.get(assembly);
        if (set != null)
            set.remove(name.getAssemblyId());
    }

    public void activated(ComponentId name, File path, List<String> deployedResources) {
        // TODO Implement this
    }

    public void retired(ComponentId name, File path, List<String> deployedResources) {
        // TODO Implement this
    }

    private String getFormUrl(PIPATask task) {
        // return task.getFormURLAsString();
        return task.getProcessEndpoint().toString();
    }

    // ------------------ Common deployment methods ------------------------

    public DeploymentMessage checkPipa(String token, InputStream input, String name) {
        DeploymentMessage msg = null;
        try {
            PIPATask task = loadPIPADescriptor(input);
            if (task.isValid()) {
                /*
                 * ALEX: Disabled until we get token propagation from
                 * deploy-impl PIPATask existing =
                 * wds.getPipaTask(task.getFormURLAsString(), token); if
                 * (existing != null) { msg = new DeploymentMessage(Level.ERROR,
                 * "PIPA task already exists: "+task.getFormURLAsString());
                 * msg.setResource(name); }
                 */
            } else {
                msg = new DeploymentMessage(Level.ERROR, "Invalid PIPA task descriptor: " + name);
                msg.setResource(name);
            }
        } catch (Exception e) {
            LOG.error("Error while storing PIPA: " + name, e);
            msg = new DeploymentMessage(Level.ERROR, e.toString());
            msg.setResource(name);
        }
        return msg;
    }

    public DeploymentMessage processPipa(String token, InputStream input, String name, ArrayList<String> urls, File dir) {
        DeploymentMessage msg = null;

        try {
            PIPATask task = loadPIPADescriptor(input);
            String processName=getProcessName(dir);
            CustomColumn[] customColumn=loadCustomMetadata(dir,processName);

            urls.add(getFormUrl(task));
            if (task.isValid()) {
                LOG.debug("Store PIPA {}", name);
                ITaskDAOConnection dao=_taskDAOFactory.openConnection();
                try {
                    _tms.deletePipa(dao,getFormUrl(task), token);
                    _tms.deleteCustomColumn(dao,processName, token);
                } catch (Exception e) {
                    // don't bother with that here
                }
                _tms.storePipa(dao,task, token);
                 if (customColumn!=null)
                	 _tms.storeCustomColumn(dao,customColumn, token);
                dao.close();
            } else {
                msg = new DeploymentMessage(Level.ERROR, "Invalid PIPA task descriptor: " + name);
                msg.setResource(name);
            }
        } catch (Exception e) {
            LOG.error("Error while storing PIPA: " + name, e);
            msg = new DeploymentMessage(Level.ERROR, e.toString());
            msg.setResource(name);
        }
        return msg;
    }

    private String getProcessName(File dir) {
        File parentDir = dir.getParentFile();
        String processName=parentDir.getName();
        processName = processName.replaceAll(".\\d*$", "");
        return processName;
    }

    private String encrypt(String internalPassword){
        BasicTextEncryptor encryptor = new BasicTextEncryptor();
        // setPassword uses hash to decrypt password which should be same as hash of encryptor
        encryptor.setPassword("IntalioEncryptedpasswordfortempo#123");
        return encryptor.encrypt(internalPassword);

    }

    private void updatePipa(List<String> deployedResources, PIPATaskState state) {	

        ITaskDAOConnection dao=null;
        //ITaskDAOConnection is accessed here for fix of JIRA WF-1466
        try {
            dao=_taskDAOFactory.openConnection();
            for (String url : deployedResources) {
                try {
                    if(LOG.isDebugEnabled()) LOG.debug("versions>> "+_versions.toString());
                    _tms.updatePipa(dao,url,encrypt(internalPassword),state);
                } catch (UnavailableTaskException e) {
                    LOG.warn("Undeploy - PIPA not found: " + url);
                } catch (AuthException e) {
                    LOG.warn("Undeploy - AuthException: " + url, e);
                    break; // fail-fast
                }
            }
        }finally{
            if(dao!=null)
                dao.close();
        }
    }

    private CustomColumn[] loadCustomMetadata(File dir,String processName) {
        CustomColumn customColumn[]=null;
        File metadataFile=new File(dir.getAbsolutePath()+"//..//processes.ode/metadata.xml");
        LOG.debug("Metadata file: " + metadataFile.getAbsolutePath() + " ,exists: " + metadataFile.exists());
        if (metadataFile.exists()){
            customColumn= parse(metadataFile,processName);	
        }
        return  customColumn;
    }

    public PIPATask loadPIPADescriptor(InputStream input) throws IOException {
        Properties prop = new Properties();
        prop.load(input);
        PIPATask task = PIPALoader.parsePipa(prop);
        return task;
    }

    // ------------------ Private stuff ------------------------

    private void checkDir(File base, File dir, List<DeploymentMessage> msgs, String token) {
        File[] files = dir.listFiles();
        if (files == null)
            return;

        for (File f : files) {
            LOG.debug("Check: {}", f);
            String itemURL = relativePath(base, f);
            if (f.isDirectory()) {
                checkDir(base, f, msgs, token);
            } else if (f.isFile()) {
                try {
                    DeploymentMessage msg = null;
                    InputStream input = new FileInputStream(f);
                    try {
                        if (f.getName().endsWith(".pipa")) {
                            msg = checkPipa(token, input, itemURL);
                        }
                        if (msg != null)
                            msgs.add(msg);
                    } finally {
                        close(input);
                    }
                } catch (Exception e) {
                    LOG.error("Error while checking PIPA: " + f, e);
                    DeploymentMessage msg = new DeploymentMessage(Level.ERROR, e.toString());
                    msg.setResource(itemURL);
                    msgs.add(msg);
                }
            } else {
                DeploymentMessage msg = new DeploymentMessage(Level.WARNING, "Unknown file type: " + f);
                msg.setResource(itemURL);
                msgs.add(msg);
            }
        }
    }

    private void processDir(File base, File dir, ArrayList<String> urls, List<DeploymentMessage> msgs, String token) {
        File[] files = dir.listFiles();
        for (File f : files) {
            LOG.debug("Process: {}", f);
            String itemURL = relativePath(base, f);
            if (f.isDirectory()) {
                processDir(base, f, urls, msgs, token);
            } else if (f.isFile()) {
                try {
                    DeploymentMessage msg = null;
                    InputStream input = new FileInputStream(f);
                    try {
                        if (f.getName().endsWith(".pipa")) {
                            msg = processPipa(token, input, itemURL, urls, base);
                        }
                        // TODO Call MetaData and do the needful

                        if (msg != null)
                            msgs.add(msg);
                    } finally {
                        close(input);
                    }
                } catch (Exception e) {
                    // this shouldn't happen but if it does, fail fast
                    LOG.error("Error while processing PIPA: " + f, e);
                    DeploymentMessage msg = new DeploymentMessage(Level.ERROR, e.toString());
                    msg.setResource(itemURL);
                    msgs.add(msg);
                    break;
                }
            } else {
                DeploymentMessage msg = new DeploymentMessage(Level.WARNING, "Unknown file type: " + f);
                msg.setResource(itemURL);
                msgs.add(msg);
            }
        }
    }

    private static String relativePath(File parent, File child) {
        return parent.toURI().relativize(child.toURI()).toString();
    }

    private static void close(Closeable c) {
        try {
            c.close();
        } catch (Exception e) {
            // ignore
        }
    }
    
    private  CustomColumn[] parse(File metadataFile, String processName){
        List<CustomColumn> columns = new ArrayList<CustomColumn>();
        try {
        InputStream metadataInputStream = new FileInputStream(metadataFile);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

            Document document = documentBuilderFactory.newDocumentBuilder().parse( metadataInputStream);
            Element docElement = document.getDocumentElement();
            NodeList columnList = docElement.getElementsByTagName(COLUMN_ELEMENT);
            for(int i=0; i < columnList.getLength(); i++){
                CustomColumn custCol=new CustomColumn();
                String columnName = columnList.item(i).getAttributes().getNamedItem(COLUMN_NAME_ELEMENT).getNodeValue();
                custCol.setCustomColumnName(columnName);
                custCol.setProjectName(processName);
                columns.add(custCol);
            }
        } catch (Exception e) {
            LOG.error("Error while parsing metadata.xml of the Process Name: " + processName, e);
        }
        return (CustomColumn[]) columns.toArray(new CustomColumn[columns.size()]);
    }

}
