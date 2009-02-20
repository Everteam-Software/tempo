package org.intalio.tempo.deployment.impl;

import java.lang.reflect.*;
import java.net.InetAddress;
import java.io.*;

public class DerbyNetworkServerResource {
	private int _port = 0;

	private String _host = "localhost";

	public String getHost() {
		return _host;
	}

	public void setHost(String host) {
		_host = host;
	}

	public String getPort() {
		return Integer.toString(_port);
	}

	public void setPort(String port) {
		_port = Integer.parseInt(port);
	}

	public void start() {
		if (_host != null && _port != 0) {
			try {
				// using reflection to avoid compile dependencies...
				Class<?> clazz = Class.forName("org.apache.derby.drda.NetworkServerControl");
				System.out.println("Starting Derby Network server on " + _host
						+ ":" + _port);
				Constructor<?> ctr = clazz.getConstructor(java.net.InetAddress.class, Integer.TYPE);
				Object obj = ctr.newInstance(InetAddress.getByName(_host), _port);
				Method method = clazz.getMethod("start", PrintWriter.class);
				method.invoke(obj, new PrintWriter(System.out, true));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}
