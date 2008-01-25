/**
 * Copyright (c) 2005-2006 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 */
package org.intalio.tempo.workflow.wds.cli;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.intalio.tempo.workflow.wds.client.WDSClient;

import java.util.List;
import java.util.LinkedList;

/**
 * Instances of this class work as <a href="http://ant.apache.org">Ant</a> task implementation and used to provide
 * functionality of WDS-CLI (Workflow Deployment Service Client), i.e. used to deploy/undeploy forms and PIPA tasks.
 *
 * The task served with this class can have any number of following elments in any order:
 * &lt;store&gt;
 * &lt;storePipa&gt;
 * &lt;createPipa&gt;
 * &lt;storeActivity&gt;
 * &lt;retrieve&gt;
 * &lt;delete&gt;
 * &lt;deleteXForm&gt;.
 * All of these elements can have following attributes:
 * <ul>
 * <li>destinationURI</li>
 * <li>sourceURI</li>
 * <li>deployment_descriptor</li>
 * </ul>
 *
 * Following is the example of using this class in Ant scripts:
 * <pre>
 *
 &lt;path id="lib.path"&gt;
   &lt;fileset dir="target" includes="intalio-bpms-workflow-wds-cli-*.jar"/&gt;
 &lt;/path&gt;

 &lt;taskdef name="wds-cli" classname="org.intalio.tempo.workflow.wds.cli.WDSCliTaskDef" classpathref="lib.path"/&gt;

 &lt;typedef name="store" classname="org.intalio.tempo.workflow.wds.cli.WDSCliTaskDef$StoreCommandHandlerElement"
          classpathref="lib.path"/&gt;

 &lt;typedef name="storePipa" classname="org.intalio.tempo.workflow.wds.cli.WDSCliTaskDef$StorePipaCommandHandlerElement"
              classpathref="lib.path"/&gt;

 &lt;typedef name="storeActivity" classname="org.intalio.tempo.workflow.wds.cli.WDSCliTaskDef$StoreActivityCommandHandlerElement"
              classpathref="lib.path"/&gt;


   &lt;target name="deploy-AR"&gt;
     &lt;wds-cli force_overwrite="true"&gt;
         &lt;storePipa deployment_descriptor="ar-deploy.xml" sourceURI="AbsenceRequest.xform" destinationURI="AbsenceRequest/AbsenceRequest.xform"/&gt;
         &lt;storeActivity sourceURI="AbsenceApproval.xform" destinationURI="AbsenceRequest/AbsenceApproval.xform"/&gt;
     &lt;/wds-cli&gt;
   &lt;/target&gt;

   &lt;!-- Following examples of task &lt;wds-cli&gt; use shows all attributes supported and there implied values --&gt;
   &lt;target name="deploy-PIPA"&gt;
       &lt;wds-cli force_overwrite="true" participant_token=""
                wds_base_url="http://localhost:8080/wds/" contentType="application/octet-stream"&gt;

           &lt;storePipa deployment_descriptor="pipa-deploy.xml" sourceURI="startform.xform" destinationURI="PIPA/PIPA.xform"/&gt;
       &lt;/wds-cli&gt;
   &lt;/target&gt;

   &lt;target name="deploy-Store"&gt;
       &lt;wds-cli force_overwrite="true" participant_token=""
                wds_base_url="http://localhost:8080/wds/" contentType="application/octet-stream"&gt;

           &lt;storePipa deployment_descriptor="store-pipa-deploy.xml" sourceURI="SelectItem.xform" destinationURI="ChainedExecution/SelectItem.xform"/&gt;
           &lt;storeActivity sourceURI="Address.xform" destinationURI="ChainedExecution/Address.xform"/&gt;
           &lt;storeActivity sourceURI="Payment.xform" destinationURI="ChainedExecution/Payment.xform"/&gt;
       &lt;/wds-cli&gt;
   &lt;/target&gt;

 *
 * </pre>
 *
 * The first target named "deploy-AR" deploys two forms of {@code AbsenceRequest} scenario and creates one PIPA task.
 * The second - just creates PIPA task and deploys form used in it task.
 * The third one used to deploy one PIPA task and three forms.
 *
 *
 * @author Oleg Zenzin
 * @version $Revision: 138 $, $LastChangedDate: 2006-09-21 19:52:16 -0700 (Thu, 21 Sep 2006) $
 */
public class WDSCliTaskDef extends Task {

    private List<WDSCliCommandParams> handlers = new LinkedList<WDSCliCommandParams>();

    private String participant_token;

    private String wds_base_url;

    private String contentType;

    private boolean force_overwrite;

    public void init() throws BuildException {
        wds_base_url = "http://localhost:8080/wds/";
        contentType = "application/octet-stream";
        participant_token = ""; //otherwise we gettin NPE at org.intalio.tempo.workflow.wds.client.WDSClient.setParticipantToken(WDSClient.java:85)
    }


    /* Attributes of defined task */

    public void setParticipant_token(String participant_token) {
        this.participant_token = participant_token;
    }

    public void setWds_base_url(String wds_base_url) {
        this.wds_base_url = wds_base_url;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setForce_overwrite(boolean force_overwrite) {
        this.force_overwrite = force_overwrite;
    }

    /* Enclosed elements - wds-cli commands */

    public void addConfiguredStore(StoreCommandHandlerElement elementHandler) {
        handlers.add(elementHandler);
    }

    public void addConfiguredStorePipa(StorePipaCommandHandlerElement elementHandler) {
        handlers.add(elementHandler);
    }

    public void addConfiguredCreatePipa(CreatePipaCommandHandlerElement elementHandler) {
        handlers.add(elementHandler);
    }

    public void addConfiguredStoreActivity(StoreActivityCommandHandlerElement elementHandler) {
        handlers.add(elementHandler);
    }

    public void addConfiguredRetrieve(RetrieveCommandHandlerElement elementHandler) {
        handlers.add(elementHandler);
    }

    public void addConfiguredDelete(DeleteCommandHandlerElement elementHandler) {
        handlers.add(elementHandler);
    }

    public void addConfiguredDeleteXForm(DeleteXFormCommandHandler elementHandler) {
        handlers.add(elementHandler);
    }

    @Override
    public void execute() throws BuildException {
        for (WDSCliCommandParams handler : handlers) try {
            WDSClient client = new WDSClient(wds_base_url, participant_token);
            handler.handleCommand(client, handler.getDestinationURI(),
                    handler.getSourceURI(), contentType, handler.getDeployment_descriptor(), force_overwrite);
        } catch (Exception e) {
            throw new BuildException("Error while calling WDS with params: wds_base_url='" + wds_base_url +
                    "' participant_token='" + "' destination_uri='" + handler.getDestinationURI() + "' source_uri='" + handler.getSourceURI() +
                    "' contentType='" + contentType + "' deployment_descriptor='" + handler.getDeployment_descriptor() +
                    "' force_overwrite=" + force_overwrite, e);
        }

    }

    private interface WDSCliCommandParams extends WDSCommandLineClient.CommandHandler {

        String getDestinationURI();
        String getSourceURI();
        String getDeployment_descriptor();

    }

    public static class StoreCommandHandlerElement
            extends WDSCommandLineClient.StoreCommandHandler implements WDSCliCommandParams {

        private String destinationURI;

        private String sourceURI;

        public String getDestinationURI() {
            return destinationURI;
        }

        public void setDestinationURI(String destinationURI) {
            this.destinationURI = destinationURI;
        }

        public String getSourceURI() {
            return sourceURI;
        }

        public void setSourceURI(String sourceURI) {
            this.sourceURI = sourceURI;
        }

        public String getDeployment_descriptor() {return null;}

    }

    public static class StorePipaCommandHandlerElement
            extends WDSCommandLineClient.StorePipaCommandHandler implements WDSCliCommandParams  {

        private String destinationURI;

        private String sourceURI;

        private String deployment_descriptor;

        public String getDeployment_descriptor() {
            return deployment_descriptor;
        }

        public void setDeployment_descriptor(String deployment_descriptor) {
            this.deployment_descriptor = deployment_descriptor;
        }

        public String getDestinationURI() {
            return destinationURI;
        }

        public void setDestinationURI(String destinationURI) {
            this.destinationURI = destinationURI;
        }

        public String getSourceURI() {
            return sourceURI;
        }

        public void setSourceURI(String sourceURI) {
            this.sourceURI = sourceURI;
        }

    }

    public static class CreatePipaCommandHandlerElement
            extends WDSCommandLineClient.CreatePipaCommandHandler  implements WDSCliCommandParams {

        private String destinationURI;

        private String sourceURI;

        private String deployment_descriptor;

        public String getDeployment_descriptor() {
            return deployment_descriptor;
        }

        public void setDeployment_descriptor(String deployment_descriptor) {
            this.deployment_descriptor = deployment_descriptor;
        }

        public String getDestinationURI() {
            return destinationURI;
        }

        public void setDestinationURI(String destinationURI) {
            this.destinationURI = destinationURI;
        }

        public String getSourceURI() {
            return sourceURI;
        }

        public void setSourceURI(String sourceURI) {
            this.sourceURI = sourceURI;
        }

    }

    public static class StoreActivityCommandHandlerElement
            extends WDSCommandLineClient.StoreActivityCommandHandler  implements WDSCliCommandParams {

        private String destinationURI;

        private String sourceURI;

        public String getDestinationURI() {
            return destinationURI;
        }

        public void setDestinationURI(String destinationURI) {
            this.destinationURI = destinationURI;
        }

        public String getSourceURI() {
            return sourceURI;
        }

        public void setSourceURI(String sourceURI) {
            this.sourceURI = sourceURI;
        }

        public String getDeployment_descriptor() {return null;}
    }

    public static class RetrieveCommandHandlerElement
            extends WDSCommandLineClient.RetrieveCommandHandler  implements WDSCliCommandParams {

        private String destinationURI;

        private String sourceURI;

        public String getDestinationURI() {
            return destinationURI;
        }

        public void setDestinationURI(String destinationURI) {
            this.destinationURI = destinationURI;
        }

        public String getSourceURI() {
            return sourceURI;
        }

        public void setSourceURI(String sourceURI) {
            this.sourceURI = sourceURI;
        }

        public String getDeployment_descriptor() {return null;}
    }

    public static class DeleteCommandHandlerElement
            extends WDSCommandLineClient.DeleteCommandHandler  implements WDSCliCommandParams {

        private String destinationURI;

        private String sourceURI;

        public String getDestinationURI() {
            return destinationURI;
        }

        public void setDestinationURI(String destinationURI) {
            this.destinationURI = destinationURI;
        }

        public String getSourceURI() {
            return sourceURI;
        }

        public void setSourceURI(String sourceURI) {
            this.sourceURI = sourceURI;
        }

        public String getDeployment_descriptor() {return null;}
    }

    public static class DeleteXFormCommandHandler
            extends WDSCommandLineClient.DeleteXFormCommandHandler  implements WDSCliCommandParams {

        private String destinationURI;

        private String sourceURI;

        public String getDestinationURI() {
            return destinationURI;
        }

        public void setDestinationURI(String destinationURI) {
            this.destinationURI = destinationURI;
        }

        public String getSourceURI() {
            return sourceURI;
        }

        public void setSourceURI(String sourceURI) {
            this.sourceURI = sourceURI;
        }

        public String getDeployment_descriptor() {return null;}
    }

}
