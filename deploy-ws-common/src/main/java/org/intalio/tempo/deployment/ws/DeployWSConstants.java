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

    public static final String DEPLOY_NS_PREFIX = "deploy";
    public static final OMNamespace DEPLOY_NS =
        OM_FACTORY.createOMNamespace("http://tempo.intalio.org/deploy/deploymentService", DEPLOY_NS_PREFIX);

    
    public static final QName USER = new QName(DEPLOY_NS.getNamespaceURI(), "user", DEPLOY_NS_PREFIX );

    public static final QName PASSWORD = new QName(DEPLOY_NS.getNamespaceURI(), "password", DEPLOY_NS_PREFIX);

    public static final QName TOKEN = new QName(DEPLOY_NS.getNamespaceURI(), "token", DEPLOY_NS_PREFIX);

    /* Deploy Request */
    
    public static final QName DEPLOY_REQUEST = new QName(DEPLOY_NS.getNamespaceURI(), "deployAssembly", DEPLOY_NS_PREFIX);

    public static final QName ASSEMBLY_NAME = new QName(DEPLOY_NS.getNamespaceURI(), "assemblyName", DEPLOY_NS_PREFIX);

    public static final QName REPLACE_EXISTING_ASSEMBLIES =
        new QName(DEPLOY_NS.getNamespaceURI(), "replaceExistingAssemblies", DEPLOY_NS_PREFIX);

    public static final QName DEPLOY_CONTROL_PARAM =
        new QName(DEPLOY_NS.getNamespaceURI(), "deployControlParam", DEPLOY_NS_PREFIX);

    public static final String DEPLOY_CONTROL_PARAM_DO_NOT_ACTIVATE =
        "deployControlParamDoNotActivate";

    public static final String DEPLOY_CONTROL_PARAM_ACTIVATE =
        "deployControlParamActivate";

    public static final String DEPLOY_CONTROL_PARAM_REPLACE_EXISTING_ASSEMBLIES_AND_ACTIVATE =
        "deployControlParamReplaceExistingAssembliesAndActivate";

    public static final QName ZIP = new QName(DEPLOY_NS.getNamespaceURI(), "zip", DEPLOY_NS_PREFIX);

    /* Deploy Response */
    
    public static final QName DEPLOY_RESULT = new QName(DEPLOY_NS.getNamespaceURI(), "deployResult", DEPLOY_NS_PREFIX);
    public static final QName ASSEMBLY_VERSION = new QName(DEPLOY_NS.getNamespaceURI(), "assemblyVersion", DEPLOY_NS_PREFIX);
    public static final QName SUCCESS = new QName(DEPLOY_NS.getNamespaceURI(), "success", DEPLOY_NS_PREFIX);
    public static final QName MESSAGES = new QName(DEPLOY_NS.getNamespaceURI(), "messages", DEPLOY_NS_PREFIX);
    
    /* DeploymentMessage */
    
    public static final QName MESSAGE = new QName(DEPLOY_NS.getNamespaceURI(), "message", DEPLOY_NS_PREFIX);
    public static final QName MSG_COMPONENT_NAME = new QName(DEPLOY_NS.getNamespaceURI(), "component", DEPLOY_NS_PREFIX);
    public static final QName MSG_COMPONENT_MANAGER = new QName(DEPLOY_NS.getNamespaceURI(), "componentManager", DEPLOY_NS_PREFIX);
    public static final QName MSG_DESCRIPTION = new QName(DEPLOY_NS.getNamespaceURI(), "description", DEPLOY_NS_PREFIX);
    public static final QName MSG_LEVEL = new QName(DEPLOY_NS.getNamespaceURI(), "level", DEPLOY_NS_PREFIX);
    public static final QName MSG_RESOURCE = new QName(DEPLOY_NS.getNamespaceURI(), "resource", DEPLOY_NS_PREFIX);
    public static final QName MSG_LOCATION = new QName(DEPLOY_NS.getNamespaceURI(), "location", DEPLOY_NS_PREFIX);
    
    /* Undeploy Request */
    
    public static final QName UNDEPLOY_REQUEST = new QName(DEPLOY_NS.getNamespaceURI(), "undeployAssembly", DEPLOY_NS_PREFIX);

    /* getDeployedAssemblies Request */
    
    public static final QName GET_DEPLOYED_ASSEMBLIES_REQUEST = new QName(DEPLOY_NS.getNamespaceURI(), "getDeployedAssemblies", DEPLOY_NS_PREFIX);
    public static final QName GET_DEPLOYED_ASSEMBLIES_RESPONSE = new QName(DEPLOY_NS.getNamespaceURI(), "getDeployedAssembliesResponse", DEPLOY_NS_PREFIX);
    public static final QName ASSEMBLY_DIR = new QName(DEPLOY_NS.getNamespaceURI(), "assemblyDir", DEPLOY_NS_PREFIX);
    public static final QName DEPLOYED_ASSEMBLIES = new QName(DEPLOY_NS.getNamespaceURI(), "deployedAssemblies", DEPLOY_NS_PREFIX);
    public static final QName DEPLOYED_ASSEMBLY = new QName(DEPLOY_NS.getNamespaceURI(), "deployedAssembly", DEPLOY_NS_PREFIX);
    public static final QName DEPLOYED_COMPONENTS = new QName(DEPLOY_NS.getNamespaceURI(), "deployedComponents", DEPLOY_NS_PREFIX);
    public static final QName DEPLOYED_COMPONENT = new QName(DEPLOY_NS.getNamespaceURI(), "deployedComponent", DEPLOY_NS_PREFIX);
    public static final QName COMPONENT_DIR = new QName(DEPLOY_NS.getNamespaceURI(), "componentDir", DEPLOY_NS_PREFIX);
    public static final QName COMPONENT_MANAGER = new QName(DEPLOY_NS.getNamespaceURI(), "componentManager", DEPLOY_NS_PREFIX);
    public static final QName COMPONENT_NAME = new QName(DEPLOY_NS.getNamespaceURI(), "componentName", DEPLOY_NS_PREFIX);
}
