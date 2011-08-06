package org.intalio.tempo.workflow.tms.client;

import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;


public class MultiThreadedHttpConnectionManagerFactory {
	private static MultiThreadedHttpConnectionManager connectionManager = null;

	private MultiThreadedHttpConnectionManagerFactory() {
	} 

	public static MultiThreadedHttpConnectionManager getInstance(){
		if (connectionManager == null) {
			connectionManager = new MultiThreadedHttpConnectionManager(); 
            HttpConnectionManagerParams params = new HttpConnectionManagerParams(); 
            params.setDefaultMaxConnectionsPerHost(20); 
            params.setSoTimeout(120*1000);
            connectionManager.setParams(params);
		}
		return connectionManager;
	}
}
