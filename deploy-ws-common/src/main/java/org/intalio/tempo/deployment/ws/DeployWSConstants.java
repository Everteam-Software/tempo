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

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;

public class DeployWSConstants {
    public static final OMFactory OM_FACTORY = OMAbstractFactory.getOMFactory();

    public static final OMNamespace DEPLOY_NS =
        OM_FACTORY.createOMNamespace("http://tempo.intalio.org/deploy/deploymentService", "deploy");

    
    public static final QName USER = new QName(DEPLOY_NS.getNamespaceURI(), "user");

    public static final QName PASSWORD = new QName(DEPLOY_NS.getNamespaceURI(), "password");

    public static final QName TOKEN = new QName(DEPLOY_NS.getNamespaceURI(), "token");

    /* Deploy Request */
    
    public static final QName DEPLOY_REQUEST = new QName(DEPLOY_NS.getNamespaceURI(), "deployAssembly");

    public static final QName ASSEMBLY_NAME = new QName(DEPLOY_NS.getNamespaceURI(), "assemblyName");

    public static final QName REPLACE_EXISTING_ASSEMBLIES =
        new QName(DEPLOY_NS.getNamespaceURI(), "replaceExistingAssemblies");

    public static final QName ZIP = new QName(DEPLOY_NS.getNamespaceURI(), "zip");

    /* Deploy Response */
    
    public static final QName DEPLOY_RESULT = new QName(DEPLOY_NS.getNamespaceURI(), "deployResult");
    public static final QName ASSEMBLY_VERSION = new QName(DEPLOY_NS.getNamespaceURI(), "assemblyVersion");
    public static final QName SUCCESS = new QName(DEPLOY_NS.getNamespaceURI(), "success");
    public static final QName MESSAGES = new QName(DEPLOY_NS.getNamespaceURI(), "messages");
    
    /* DeploymentMessage */
    
    public static final QName MESSAGE = new QName(DEPLOY_NS.getNamespaceURI(), "message");
    public static final QName MSG_COMPONENT_NAME = new QName(DEPLOY_NS.getNamespaceURI(), "component");
    public static final QName MSG_COMPONENT_MANAGER = new QName(DEPLOY_NS.getNamespaceURI(), "componentManager");
    public static final QName MSG_DESCRIPTION = new QName(DEPLOY_NS.getNamespaceURI(), "description");
    public static final QName MSG_LEVEL = new QName(DEPLOY_NS.getNamespaceURI(), "level");
    public static final QName MSG_RESOURCE = new QName(DEPLOY_NS.getNamespaceURI(), "resource");
    public static final QName MSG_LOCATION = new QName(DEPLOY_NS.getNamespaceURI(), "location");
    
    /* Undeploy Request */
    
    public static final QName UNDEPLOY_REQUEST = new QName(DEPLOY_NS.getNamespaceURI(), "undeployAssembly");

    /* getDeployedAssemblies Request */
    
    public static final QName GET_DEPLOYED_ASSEMBLIES_REQUEST = new QName(DEPLOY_NS.getNamespaceURI(), "getDeployedAssemblies");
    public static final QName GET_DEPLOYED_ASSEMBLIES_RESPONSE = new QName(DEPLOY_NS.getNamespaceURI(), "getDeployedAssembliesResponse");
    public static final QName ASSEMBLY_DIR = new QName(DEPLOY_NS.getNamespaceURI(), "assemblyDir");
    public static final QName DEPLOYED_ASSEMBLIES = new QName(DEPLOY_NS.getNamespaceURI(), "deployedAssemblies");
    public static final QName DEPLOYED_ASSEMBLY = new QName(DEPLOY_NS.getNamespaceURI(), "deployedAssembly");
    public static final QName DEPLOYED_COMPONENTS = new QName(DEPLOY_NS.getNamespaceURI(), "deployedComponents");
    public static final QName DEPLOYED_COMPONENT = new QName(DEPLOY_NS.getNamespaceURI(), "deployedComponent");
    public static final QName COMPONENT_DIR = new QName(DEPLOY_NS.getNamespaceURI(), "componentDir");
    public static final QName COMPONENT_MANAGER = new QName(DEPLOY_NS.getNamespaceURI(), "componentManager");
    public static final QName COMPONENT_NAME = new QName(DEPLOY_NS.getNamespaceURI(), "componentName");
}
