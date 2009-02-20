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

import static org.intalio.tempo.deployment.ws.DeployWSConstants.ASSEMBLY_DIR;
import static org.intalio.tempo.deployment.ws.DeployWSConstants.ASSEMBLY_NAME;
import static org.intalio.tempo.deployment.ws.DeployWSConstants.ASSEMBLY_VERSION;
import static org.intalio.tempo.deployment.ws.DeployWSConstants.COMPONENT_DIR;
import static org.intalio.tempo.deployment.ws.DeployWSConstants.COMPONENT_MANAGER;
import static org.intalio.tempo.deployment.ws.DeployWSConstants.COMPONENT_NAME;
import static org.intalio.tempo.deployment.ws.DeployWSConstants.DEPLOYED_ASSEMBLIES;
import static org.intalio.tempo.deployment.ws.DeployWSConstants.DEPLOYED_ASSEMBLY;
import static org.intalio.tempo.deployment.ws.DeployWSConstants.DEPLOYED_COMPONENT;
import static org.intalio.tempo.deployment.ws.DeployWSConstants.DEPLOYED_COMPONENTS;
import static org.intalio.tempo.deployment.ws.DeployWSConstants.DEPLOY_RESULT;
import static org.intalio.tempo.deployment.ws.DeployWSConstants.MESSAGE;
import static org.intalio.tempo.deployment.ws.DeployWSConstants.MESSAGES;
import static org.intalio.tempo.deployment.ws.DeployWSConstants.MSG_COMPONENT_MANAGER;
import static org.intalio.tempo.deployment.ws.DeployWSConstants.MSG_COMPONENT_NAME;
import static org.intalio.tempo.deployment.ws.DeployWSConstants.MSG_DESCRIPTION;
import static org.intalio.tempo.deployment.ws.DeployWSConstants.MSG_LEVEL;
import static org.intalio.tempo.deployment.ws.DeployWSConstants.MSG_LOCATION;
import static org.intalio.tempo.deployment.ws.DeployWSConstants.MSG_RESOURCE;
import static org.intalio.tempo.deployment.ws.DeployWSConstants.OM_FACTORY;
import static org.intalio.tempo.deployment.ws.DeployWSConstants.SUCCESS;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMText;
import org.intalio.tempo.deployment.AssemblyId;
import org.intalio.tempo.deployment.ComponentId;
import org.intalio.tempo.deployment.DeployedAssembly;
import org.intalio.tempo.deployment.DeployedComponent;
import org.intalio.tempo.deployment.DeploymentMessage;
import org.intalio.tempo.deployment.DeploymentResult;
import org.intalio.tempo.deployment.DeploymentMessage.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OMParser {
    private static final Logger LOG = LoggerFactory.getLogger(OMParser.class);

    private OMElement _element;

    public OMParser(OMElement element) {
        _element = element;
        _element.build();
        if (_element.getParent() != null) _element.detach();
    }

    public OMElement getRequiredElement(QName parameter) {
        OMElement e = _element.getFirstChildWithName(parameter);
        if (e == null)
            throw new IllegalArgumentException("Missing parameter: " + parameter);
        return e;
    }
    
    
    @SuppressWarnings("unchecked")
    public Iterator<OMElement> getElements(QName parameter) {
        return _element.getChildrenWithName(parameter);
    }
    
    
    public String getRequiredString(QName parameter) {
        OMElement e = _element.getFirstChildWithName(parameter);
        if (e == null)
            throw new IllegalArgumentException("Missing parameter: " + parameter);
        String text = e.getText();
        if (text == null || text.trim().length() == 0)
            throw new IllegalArgumentException("Empty parameter: " + parameter);
        if (LOG.isDebugEnabled())
            LOG.debug("Parameter " + parameter + ": " + text);
        return text;
    }

    
    public String getOptionalString(QName parameter) {
        OMElement e = _element.getFirstChildWithName(parameter);
        if (e == null) return "";
        String text = e.getText();
        if (text == null) text = "";
        if (LOG.isDebugEnabled())
            LOG.debug("Parameter " + parameter + ": " + text);
        return text.trim();
    }
    

    public boolean getRequiredBoolean(QName parameter) {
        OMElement e = _element.getFirstChildWithName(parameter);
        if (e == null)
            throw new IllegalArgumentException("Missing parameter: " + parameter);
        String text = e.getText();
        if (!"true".equalsIgnoreCase(text) && !"false".equalsIgnoreCase(text))
            throw new IllegalArgumentException("Invalid boolean parameter: " + parameter + " value: "+text);
        if (LOG.isDebugEnabled())
            LOG.debug("Parameter " + parameter + ": " + text + "value: "+text);
        return "true".equalsIgnoreCase(text);
    }
    

    public int getRequiredInt(QName parameter) {
        String value = getRequiredString(parameter);
        return Integer.parseInt(value);
    }

    
    public InputStream getInputStream(QName parameter) {
        OMElement e = _element.getFirstChildWithName(parameter);
        if (e == null)
            throw new IllegalArgumentException("Missing parameter: " + parameter);
        
        OMText binaryNode = (OMText) e.getFirstOMChild();
        if (binaryNode == null)
            throw new IllegalArgumentException("Empty binary node for parameter: " + parameter);
        if (LOG.isDebugEnabled())
            LOG.debug("Parameter " + parameter + ": contains (binary) text");

        binaryNode.setOptimize(true);
        DataHandler data = (DataHandler) binaryNode.getDataHandler();
        if (data == null)
            throw new IllegalStateException("Null data handler for binary parameter: " + parameter);

        try {
            InputStream input = data.getInputStream();
            if (input == null)
                throw new IllegalStateException("Null input stream for binary parameter: " + parameter);
            return input;
        } catch (IOException except) {
            throw new RuntimeException(except);
        }
    }

    static OMElement createElement(QName element) {
        return OM_FACTORY.createOMElement(element);
    }
    
    static OMElement addElement(OMElement parent, QName element) {
        OMElement e = OM_FACTORY.createOMElement(element);
        parent.addChild(e);
        return e;
    }

    static void addTextElement(OMElement parent, QName element, String text) {
        OMElement e = OM_FACTORY.createOMElement(element);
        e.setText(text);
        parent.addChild(e);
    }

    static void addBooleanElement(OMElement parent, QName element, boolean value) {
        addTextElement(parent, element, value ? "true" : "false");
    }

    static void addIntElement(OMElement parent, QName element, int value) {
        addTextElement(parent, element, Integer.toString(value));
    }

    public static OMElement marshallDeploymentResult(DeploymentResult result) {
        OMElement response = createElement(DEPLOY_RESULT);
        addTextElement(response,    ASSEMBLY_NAME,    result.getAssemblyId().getAssemblyName());
        addIntElement(response,     ASSEMBLY_VERSION, result.getAssemblyId().getAssemblyVersion());
        addBooleanElement(response, SUCCESS,          result.isSuccessful());
        
        OMElement messages = addElement(response, MESSAGES);
        for (DeploymentMessage msg: result.getMessages()) {
            OMElement message = addElement(messages, MESSAGE);
            if (msg.getComponentId() != null) 
                addTextElement(message, MSG_COMPONENT_NAME,    msg.getComponentId().getComponentName());
            addTextElement(message, MSG_COMPONENT_MANAGER, msg.getComponentManagerName());
            addTextElement(message, MSG_DESCRIPTION,       msg.getDescription());
            addTextElement(message, MSG_LEVEL,             msg.getLevel().name());
            addTextElement(message, MSG_LOCATION,          msg.getLocation());
            addTextElement(message, MSG_RESOURCE,          msg.getResource());
        }
        return response;
    }
    
    public static OMElement marshallGetDeployedAssemblies(Collection<DeployedAssembly> assemblies) {
        OMElement response = createElement(DEPLOYED_ASSEMBLIES);
        
        for (DeployedAssembly assembly: assemblies) {
            OMElement oAssembly = addElement(response, DEPLOYED_ASSEMBLY);
            addTextElement(oAssembly, ASSEMBLY_NAME,    assembly.getAssemblyId().getAssemblyName());
            addIntElement( oAssembly, ASSEMBLY_VERSION, assembly.getAssemblyId().getAssemblyVersion());
            addTextElement(oAssembly, ASSEMBLY_DIR,     assembly.getAssemblyDir());

            OMElement components = addElement(oAssembly, DEPLOYED_COMPONENTS);
            for (DeployedComponent component: assembly.getDeployedComponents()) {
                OMElement oComponent = addElement(components, DEPLOYED_COMPONENT);
                addTextElement(oComponent, COMPONENT_NAME,    component.getComponentId().getComponentName());
                addTextElement(oComponent, COMPONENT_DIR,     component.getComponentDir());
                addTextElement(oComponent, COMPONENT_MANAGER, component.getComponentManagerName());
            }
        }
        return response;
    }

    public static DeploymentResult parseDeploymentResult(OMParser response) {
        List<DeploymentMessage> messages = new ArrayList<DeploymentMessage>();
        
        String assemblyName = response.getRequiredString(ASSEMBLY_NAME);
        int assemblyVersion = Integer.parseInt( response.getRequiredString(ASSEMBLY_VERSION) );
        boolean success = response.getRequiredBoolean(SUCCESS);
        
        OMParser responseMessages = new OMParser(response.getRequiredElement(MESSAGES));
        Iterator<OMElement> iter = responseMessages.getElements(MESSAGE);
        while (iter.hasNext()) {
            OMParser message = new OMParser(iter.next());

            Level level = Level.valueOf(message.getRequiredString(MSG_LEVEL));
            String description = message.getOptionalString(MSG_DESCRIPTION);
            String componentName = message.getOptionalString(MSG_COMPONENT_NAME);
            String componentManager = message.getOptionalString(MSG_COMPONENT_MANAGER);
            String resource = message.getOptionalString(MSG_RESOURCE);
            String location = message.getOptionalString(MSG_LOCATION);
            
            AssemblyId aid = new AssemblyId(assemblyName, assemblyVersion);
            ComponentId cid = new ComponentId(aid, componentName);
            
            DeploymentMessage msg = new DeploymentMessage(level, description);
            msg.setComponentId(cid);
            msg.setComponentManagerName(componentManager);
            msg.setLocation(location);
            msg.setResource(resource);
            
            messages.add(msg);
        }        
        AssemblyId aid = new AssemblyId(assemblyName, assemblyVersion);
        return new DeploymentResult(aid, success, messages);
    }

    
    public static Collection<DeployedAssembly> parseDeployedAssemblies(OMParser response) {
        List<DeployedAssembly> assemblies = new ArrayList<DeployedAssembly>();
        Iterator<OMElement> iter = response.getElements(DEPLOYED_ASSEMBLY);
        while (iter.hasNext()) {
            OMParser assembly = new OMParser(iter.next());
            
            String assemblyName = assembly.getRequiredString(ASSEMBLY_NAME);
            int assemblyVersion = Integer.parseInt( assembly.getRequiredString(ASSEMBLY_VERSION) );
            String assemblyDir = assembly.getRequiredString(ASSEMBLY_DIR);
            AssemblyId aid = new AssemblyId(assemblyName, assemblyVersion);
            
            List<DeployedComponent> components = new ArrayList<DeployedComponent>();
			OMParser responseComponents = new OMParser(assembly.getRequiredElement(DEPLOYED_COMPONENTS));
            Iterator<OMElement> iter2 = responseComponents.getElements(DEPLOYED_COMPONENT);
            while (iter2.hasNext()) {
                OMParser component = new OMParser(iter2.next());
                
                String componentName = component.getRequiredString(COMPONENT_NAME);
                String componentDir = component.getRequiredString(COMPONENT_DIR);
                String componentManager = component.getRequiredString(COMPONENT_MANAGER);
                
                ComponentId cid = new ComponentId(aid, componentName);
                DeployedComponent dc = new DeployedComponent(cid, componentDir, componentManager);
                components.add(dc);
            }
            
            DeployedAssembly da = new DeployedAssembly(aid, assemblyDir, components, false);
            assemblies.add(da);
        }
        
        return assemblies;
    }
    
}
