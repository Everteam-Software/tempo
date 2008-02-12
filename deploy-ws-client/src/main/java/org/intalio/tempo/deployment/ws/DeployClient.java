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

package org.intalio.tempo.deployment.ws;

import static org.intalio.tempo.deployment.ws.DeployWSConstants.ASSEMBLY_NAME;
import static org.intalio.tempo.deployment.ws.DeployWSConstants.ASSEMBLY_VERSION;
import static org.intalio.tempo.deployment.ws.DeployWSConstants.DEPLOY_REQUEST;
import static org.intalio.tempo.deployment.ws.DeployWSConstants.GET_DEPLOYED_ASSEMBLIES_REQUEST;
import static org.intalio.tempo.deployment.ws.DeployWSConstants.OM_FACTORY;
import static org.intalio.tempo.deployment.ws.DeployWSConstants.REPLACE_EXISTING_ASSEMBLIES;
import static org.intalio.tempo.deployment.ws.DeployWSConstants.UNDEPLOY_REQUEST;
import static org.intalio.tempo.deployment.ws.DeployWSConstants.ZIP;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Collection;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.util.Base64;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.intalio.tempo.deployment.AssemblyId;
import org.intalio.tempo.deployment.DeployedAssembly;
import org.intalio.tempo.deployment.DeploymentResult;
import org.intalio.tempo.deployment.DeploymentService;
import org.intalio.tempo.deployment.ws.OMParser;

/**
 * Client web services API for the Token Service.
 */
public class DeployClient implements DeploymentService {

    String _endpoint;

    /**
     * Create a deployment service client
     * 
     * @param endpointUrl endpoint of the deployment service
     */
    public DeployClient(String endpointUrl) {
        _endpoint = endpointUrl;
    }

    public DeploymentResult deployAssembly(String assemblyName, InputStream zip, boolean replaceExistingAssemblies) throws RemoteException {
        OMElement request = element(DEPLOY_REQUEST);
        request.addChild( elementText(ASSEMBLY_NAME, assemblyName) );
        request.addChild( elementBinary(ZIP, zip) );
        request.addChild( elementBoolean(REPLACE_EXISTING_ASSEMBLIES, replaceExistingAssemblies) );
        OMParser response = invoke(DEPLOY_REQUEST.getLocalPart(), request);
        return OMParser.parseDeploymentResult(response);
    }

    public Collection<DeployedAssembly> getDeployedAssemblies() throws RemoteException {
        OMElement request = element(GET_DEPLOYED_ASSEMBLIES_REQUEST);
        OMParser response = invoke(GET_DEPLOYED_ASSEMBLIES_REQUEST.getLocalPart(), request);
        return OMParser.parseDeployedAssemblies(response);
    }

    public DeploymentResult undeployAssembly(AssemblyId assemblyId) throws RemoteException {
        OMElement request = element(UNDEPLOY_REQUEST);
        request.addChild( elementText(ASSEMBLY_NAME, assemblyId.getAssemblyName()) );
        request.addChild( elementText(ASSEMBLY_VERSION, Integer.toString(assemblyId.getAssemblyVersion())) );
        OMParser response = invoke(UNDEPLOY_REQUEST.getLocalPart(), request);
        return OMParser.parseDeploymentResult(response);
    }

    protected OMParser invoke(String action, OMElement request) throws AxisFault {
        ServiceClient serviceClient = new ServiceClient();
        Options options = serviceClient.getOptions();
        EndpointReference targetEPR = new EndpointReference(_endpoint);
        options.setTo(targetEPR);
        options.setAction(action);
        OMElement response = serviceClient.sendReceive(request);
        return new OMParser(response);
    }
    
    private static OMElement element(QName name) {
        return OM_FACTORY.createOMElement(name);
    }

    private static OMElement elementText(QName name, String text) {
        OMElement element = OM_FACTORY.createOMElement(name);
        element.setText(text);
        return element;
    }

    private static OMElement elementBinary(QName name, InputStream input) {
        OMElement element = OM_FACTORY.createOMElement(name);
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(64*1024);
            byte[] buffer = new byte[4096];
            int len;
            while ((len = input.read(buffer)) >= 0) {
                outputStream.write(buffer, 0, len);
            }
            String base64Enc = Base64.encode(outputStream.toByteArray());
            OMText binary = OM_FACTORY.createOMText(base64Enc, "application/zip", true);
            element.addChild(binary);
            return element;
        } catch (IOException except) {
            throw new RuntimeException(except);
        }
    }

    private static OMElement elementBoolean(QName name, boolean value) {
        OMElement element = OM_FACTORY.createOMElement(name);
        element.setText(value ? "true" : "false");
        return element;
    }

}
