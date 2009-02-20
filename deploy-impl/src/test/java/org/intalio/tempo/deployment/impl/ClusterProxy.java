package org.intalio.tempo.deployment.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public class ClusterProxy {
	private List<String> processConfigs = new ArrayList<String>();
	private List<ClusteredNode> nodes = new ArrayList<ClusteredNode>();
	private DerbyNetworkServerResource derbyNetworkServer;
	private DataSource dataSource;

	private final List<Process> processes = new ArrayList<Process>();
	
	public void setProcessConfigs(List<String> processConfigs) {
		this.processConfigs = processConfigs;
	}

	public List<ClusteredNode> getNodes() {
		return nodes;
	}

	public void setNodes(List<ClusteredNode> nodes) {
		this.nodes = nodes;
	}

	public void setDerbyNetworkServer(DerbyNetworkServerResource derbyNetworkServer) {
		this.derbyNetworkServer = derbyNetworkServer;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void startUpProcesses() throws Exception {
		for( String config : processConfigs ) {
			startUpProcess(config);
		}
	}

	private void startUpProcess(final String config) throws Exception {
		System.out.println("Starting node using config: " + config);

    	String javaHome = System.getProperty("java.home");
    	String javaClassPath = System.getProperty("java.class.path");
    	
    	Process process = new ProcessBuilder(javaHome + File.separator + "bin" + File.separator + "java", "-cp", javaClassPath, MockClusteredNode.class.getName(), config).start();
		System.out.println("Node started: " + process);
		processes.add(process);

		final BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		Thread errorRedirector = new Thread() {
			public void run() {
				try {
					String line = null;
					while( (line = error.readLine()) != null ) {
						System.err.println("[" + config + "] " + line);
					}
				} catch( IOException ie ) {
					ie.printStackTrace();
				}
			}
		};
		errorRedirector.setDaemon(true);
		errorRedirector.start();

		final BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
		Thread stdoutRedirector = new Thread() {
			public void run() {
				try {
					String line = null;
					while( (line = stdout.readLine()) != null ) {
						System.out.println("[" + config + "] " + line);
					}
				} catch( IOException ie ) {
					ie.printStackTrace();
				}
			}
		};
		stdoutRedirector.setDaemon(true);
		stdoutRedirector.start();
	}
	
	public void startDeploymentService() throws Exception {
		for( ClusteredNode node : nodes ) {
			System.out.println("starting node " + node);
			node.startDeploymentService();
			Thread.sleep(5000);
		}
	}
	
	public void shutDownProcesses() throws Exception {
		try {
			for( ClusteredNode node : nodes ) {
				node.shutdownNode();
			}
		} catch( Exception e ) {
			e.printStackTrace();
		}

		for( int i = processes.size() - 1; i >= 0; i--) {
			Process process = processes.get(i);
			process.destroy();
			processes.remove(i);
		}
	}

	public ClusteredNode findCoordinator() throws Exception {
		ClusteredNode coordinator = null;
		try {
			while( coordinator == null ) {
				List<ClusteredNode> candidates = new ArrayList<ClusteredNode>();
				for( ClusteredNode node : nodes ) {
					if( node.isCoordinator() ) {
						candidates.add(node);
					}
				}
				
				if( candidates.size() > 1 ) {
					System.out.println(">>> Cluster not settled down yet.");
				}
				if( candidates.size() == 1 ) {
					coordinator = candidates.get(0);
				} else {
					Thread.sleep(1000);
				}
			}
		} catch( Exception e ) {
			e.printStackTrace();
		}
		
		return coordinator;
	}
	
	public void finalize() {
		for( Process process : processes ) {
			process.destroy();
		}
	}
}