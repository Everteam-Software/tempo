/**
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
import java.util.List;
import java.util.Properties;

import org.intalio.deploy.deployment.ComponentId;
import org.intalio.deploy.deployment.DeploymentMessage;
import org.intalio.deploy.deployment.DeploymentMessage.Level;
import org.intalio.deploy.deployment.spi.ComponentManagerResult;
import org.intalio.tempo.security.token.TokenContext;
import org.intalio.tempo.workflow.auth.AuthException;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.tms.TMSException;
import org.intalio.tempo.workflow.tms.UnavailableTaskException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PIPAComponentManager implements org.intalio.deploy.deployment.spi.ComponentManager {
    private static final Logger LOG = LoggerFactory.getLogger(PIPAComponentManager.class);

    ITMSServer _tms;
    
    public PIPAComponentManager(ITMSServer tms) {
        _tms = tms;
    }

    // ------------------ ComponentManager implementation ------------------------

    public String getComponentManagerName() {
        return "pipa";
    }

    public void initialize(ComponentId name, File path, List<String> deployedResources, boolean active) {
        // nothing
    }

    public void dispose(ComponentId name, File path, List<String> deployedResources, boolean active) {
        // nothing
    }

    public ComponentManagerResult deploy(ComponentId name, File base, boolean activate) {
        List<DeploymentMessage> msgs = new ArrayList<DeploymentMessage>();

        /*
         * ALEX: Disabled until we get token propagation from deploy-impl if (!TokenContext.hasToken()) { msgs.add(new
         * DeploymentMessage(Level.ERROR, "No security context token")); return msgs; }
         */

        String token = TokenContext.getToken();

        /* ALEX: */
        token = "x";

        try {
            // Phase 1: Check for conflicts
            checkDir(base, base, msgs, token);

            // Stop if any error during checks
            for (DeploymentMessage msg : msgs) {
                if (Level.ERROR.equals(msg.getLevel()))
                    return new ComponentManagerResult(msgs);
            }

            // Phase 2: Actual deployment
            ArrayList<String> urls = new ArrayList<String>();
            processDir(base, base, urls, msgs, token);
            return new ComponentManagerResult(msgs, urls);
        } finally {
        }
    }

    public void undeploy(ComponentId name, File path, List<String> deployedResources) {
        String token = "x"; // TODO
        for (String url: deployedResources) {
            try {
                _tms.deletePipa(url, token);
            } catch (UnavailableTaskException e) {
                LOG.warn("Undeploy - PIPA not found: "+url);
            } catch (AuthException e) {
                LOG.warn("Undeploy - AuthException: "+url, e);
                break; // fail-fast
            } catch (TMSException e) {
                LOG.warn("Undeploy - TMSException: "+url, e);
                break; // fail-fast
			}
        }
    }

    public void start(ComponentId name, File path, List<String> deployedResources, boolean active) {
        // nothing
    }

    public void stop(ComponentId name, File path, List<String> deployedResources, boolean active) {
        // nothing
    }

	public void activate(ComponentId name, File path, List<String> deployedResources) {
		// TODO Implement this
	}

	public void retire(ComponentId name, File path, List<String> deployedResources) {
		// TODO Implement this
	}

    public void deployed(ComponentId name, File path, List<String> deployedResources, boolean active) {
        // nothing
    }

    public void undeployed(ComponentId name, File path, List<String> deployedResources) {
        // nothing
    }

	public void activated(ComponentId name, File path, List<String> deployedResources) {
		// TODO Implement this
	}

	public void retired(ComponentId name, File path, List<String> deployedResources) {
		// TODO Implement this
	}

	// ------------------ Common deployment methods ------------------------

    public DeploymentMessage checkPipa(String token, InputStream input, String name) {
        DeploymentMessage msg = null;
        try {
            PIPATask task = loadPIPADescriptor(input);
            if (task.isValid()) {
                /*
                 * ALEX: Disabled until we get token propagation from deploy-impl PIPATask existing =
                 * wds.getPipaTask(task.getFormURLAsString(), token); if (existing != null) { msg = new
                 * DeploymentMessage(Level.ERROR, "PIPA task already exists: "+task.getFormURLAsString());
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

    public DeploymentMessage processPipa(String token, InputStream input, String name, ArrayList<String> urls) {
        DeploymentMessage msg = null;
        try {
            PIPATask task = loadPIPADescriptor(input);
            urls.add(task.getFormURLAsString());
            if (task.isValid()) {
                LOG.debug("Store PIPA {}", name);
                try {
                    _tms.deletePipa(task.getFormURLAsString(), token);    
                } catch (Exception e) {
                    // don't bother with that here
                }
                _tms.storePipa(task, token);
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

    public PIPATask loadPIPADescriptor(InputStream input) throws IOException {
        Properties prop = new Properties();
        prop.load(input);
        PIPATask task = PIPALoader.parsePipa(prop);
        return task;
    }


    // ------------------ Private stuff ------------------------

    private void checkDir(File base, File dir, List<DeploymentMessage> msgs, String token) {
        File[] files = dir.listFiles();
        if( files == null ) return;
        
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
                            msg = processPipa(token, input, itemURL, urls);
                        }
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
}
