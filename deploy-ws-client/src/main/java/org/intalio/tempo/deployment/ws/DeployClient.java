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

package org.intalio.tempo.deployment.ws;

import static org.intalio.tempo.deployment.ws.DeployWSConstants.*;

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

/**
 * Client web services API for the Token Service.
 */
public class DeployClient implements DeploymentService {

    String _endpoint;
    String _username;
    String _password;
    String _token;

    /**
     * Create a deployment service client
     */
    public DeployClient() {
    }

    public String getEndpointURL() {
        return _endpoint;
    }
    
    public void setEndpointURL(String url) {
        _endpoint = url;
    }
    
    public String getUser() {
        return _username;
    }
    
    public void setUser(String user) {
        _username = user;
    }
    
    public String getPassword() {
        return _password;
    }
    
    public void setPassword(String password) {
        _password = password;
    }
    
    public String getToken() {
        return _token;
    }
    
    public void setToken(String token) {
        _token = token;
    }
    
    public DeploymentResult deployAssembly(String assemblyName, InputStream zip, boolean replaceExistingAssemblies) throws RemoteException {
        OMElement request = element(DEPLOY_REQUEST);
        setAuthentication(request);
        request.addChild( elementText(ASSEMBLY_NAME, assemblyName) );
        request.addChild( elementBinary(ZIP, zip) );
        request.addChild( elementBoolean(REPLACE_EXISTING_ASSEMBLIES, replaceExistingAssemblies) );
        OMParser response = invoke(DEPLOY_REQUEST.getLocalPart(), request);
        return OMParser.parseDeploymentResult(response);
    }

    private void setAuthentication(OMElement request) {
        if (_username != null) 
            request.addChild( elementText(USER, _username) );
        if (_password != null) 
            request.addChild( elementText(PASSWORD, _password) );
        if (_token != null) 
            request.addChild( elementText(TOKEN, _token) );
    }

    public Collection<DeployedAssembly> getDeployedAssemblies() throws RemoteException {
        OMElement request = element(GET_DEPLOYED_ASSEMBLIES_REQUEST);
        setAuthentication(request);
        OMParser response = invoke(GET_DEPLOYED_ASSEMBLIES_REQUEST.getLocalPart(), request);
        return OMParser.parseDeployedAssemblies(response);
    }

    public DeploymentResult undeployAssembly(AssemblyId assemblyId) throws RemoteException {
        OMElement request = element(UNDEPLOY_REQUEST);
        setAuthentication(request);
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

	public void activate(AssemblyId assemblyId) {
		// TODO implement this
		throw new RuntimeException("Not implemented yet!!!");
	}

	public void retire(AssemblyId assemblyId) {
		// TODO implement this
		throw new RuntimeException("Not implemented yet!!!");
	}

	public DeploymentResult deployAssembly(String assemblyName,
			InputStream zip, DeployControlParam param) throws RemoteException {
        OMElement request = element(DEPLOY_REQUEST);
        setAuthentication(request);
        request.addChild( elementText(ASSEMBLY_NAME, assemblyName) );
        request.addChild( elementBinary(ZIP, zip) );
        request.addChild( elementText(DEPLOY_CONTROL_PARAM, 
        		DeployControlParam.REPLACE_EXISTING_ASSEMBLIES_AND_ACTIVATE.equals(param) ? DEPLOY_CONTROL_PARAM_REPLACE_EXISTING_ASSEMBLIES_AND_ACTIVATE :
        			(DeployControlParam.DO_NOT_ACTIVATE.equals(param) ? DEPLOY_CONTROL_PARAM_DO_NOT_ACTIVATE : DEPLOY_CONTROL_PARAM_ACTIVATE)) );
        OMParser response = invoke(DEPLOY_REQUEST.getLocalPart(), request);
        return OMParser.parseDeploymentResult(response);
	}
}
