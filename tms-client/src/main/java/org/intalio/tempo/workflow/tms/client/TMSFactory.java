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
 *
 * $Id: TaskManagementServicesFacade.java 5440 2006-06-09 08:58:15Z imemruk $
 * $Log:$
 */

package org.intalio.tempo.workflow.tms.client;



import org.intalio.tempo.workflow.tms.ITaskManagementServiceFactory;
import org.intalio.tempo.workflow.tms.ITaskManagementService;
import org.intalio.tempo.workflow.tms.server.TMSServer;
import org.intalio.tempo.workflow.tms.server.dao.ITaskDAOConnectionFactory;
import org.intalio.tempo.workflow.util.RequiredArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TMSFactory implements ITaskManagementServiceFactory {

	final static Logger logger = LoggerFactory.getLogger(TMSFactory.class);
	private String _endpoint;
	private String _participantToken;
	private boolean _isLocal = false;
	private ITaskDAOConnectionFactory _taskDAOFactory;
	private TMSServer _server;
	private long _httpTimeOut = 30000;
	//private static boolean isTEMPOLOCAL = true;
    
	public void setHttpTimeOut(long httpTimeOut) {
        this._httpTimeOut = httpTimeOut;
    }
	
	public long getHttpTimeOut() {
        return _httpTimeOut;
    }
	
	public void setServer(TMSServer server) {
        this._server = server;
    }

    public TMSServer getServer() {
        return this._server;
    }

    public void setLocal(boolean isLocal) {
        this._isLocal = isLocal;
    }

    public boolean isLocal() {
        return this._isLocal;
    }
    
    public void setTaskDAOFactory(ITaskDAOConnectionFactory taskDAOFactory) {
        this._taskDAOFactory = taskDAOFactory;
    }

    public ITaskDAOConnectionFactory getTaskDAOFactory() {
        return this._taskDAOFactory;
    }

//    public TMSFactory(String endpoint, String participantToken) {
//		if (endpoint == null) {
//			throw new RequiredArgumentException("endpoint");
//		}
//		if (participantToken == null) {
//			throw new RequiredArgumentException("participantToken");
//		}
//		_endpoint = endpoint;
//		_participantToken = participantToken;
//	}
    
    public TMSFactory configureRemote(){
        setLocal(false);
        return this;
    }

	public ITaskManagementService getService(String endpoint, String participantToken) {
        //logger.debug("RemoteTMSFactory will be initializing Local factory ::: ");
	    
	    if( participantToken == null) {
	        throw new RequiredArgumentException( "participantName" );
	    }
	    
	    if( isLocal() ){
	        if(getServer() == null){
	            throw new RequiredArgumentException( "server" );
	        }
	        if(getTaskDAOFactory() == null ){
	            throw new RequiredArgumentException( "TaskDAOConnectionFactory");
	        }
	        
	        logger.debug( "Factory is configured to use Local TMS Client" );
	        LocalTMSClient client = new LocalTMSClient();
	        client.setTaskDAOFactory(_taskDAOFactory);
	        client.setServer(_server);
	        client.setParticipantToken(participantToken);
	        return client;
	    } else {
	        logger.debug("Factory is configured to use Remote TMS Client");
	        RemoteTMSClient client = new RemoteTMSClient(endpoint, participantToken);
	        client.setHttpTimeOut(_httpTimeOut);
	        return client;
	    }
	    
//            Resource xmlResource = new FileSystemResource(System
//                    .getProperty("org.intalio.deploy.configDirectory")
//                    + File.separatorChar + "tempo-tms.xml");
//            BeanFactory factory = new XmlBeanFactory(xmlResource);
//             
//            TMSClientProvider tmsclientprovider=(TMSClientProvider)factory
//            .getBean("tms.tmsclientprovider");
//            
//            if(tmsclientprovider.isTempoLocal()){
//                logger.debug("RemoteTMSFactory will be initializing LocalTMSClient  ::: ");
//                LocalTMSClient client = (LocalTMSClient) factory
//                .getBean("tms.Localtmsclient");
//                client.setParticipantToken(_participantToken);
//                return client;
//            }
//            else {
//                logger.debug("RemoteTMSFactory will be initializing RemoteTMSClient ::: ");
//            return new RemoteTMSClient(_endpoint, _participantToken);
	    
    }



}
