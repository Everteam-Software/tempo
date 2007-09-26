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
package org.intalio.tempo.workflow.processes.xpath;

import java.net.InetAddress;

/**
 * @author Jacques-Alexandre Gerber
 *
 * Returns a unique ID (in the scope of the local machine)
 */
public class UID {
	
	public static String create() {
		String uid = new java.rmi.dgc.VMID().toString();
		try{ 
			InetAddress host = InetAddress.getLocalHost();
			uid += host.getHostAddress();
		} catch(Exception e) {
			uid+= "local";
		}
		return uid;
	}

	public static void main(String[] args) {
		String id = create();
		System.out.println(id + " [" + id.length() + "]");
	}
}
