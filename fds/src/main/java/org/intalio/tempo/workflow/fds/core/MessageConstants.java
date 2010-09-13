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
package org.intalio.tempo.workflow.fds.core;

import java.util.HashMap;

/**
 * This non-instantiatable class acts as a qualifier for FDS constants, such
 * as standard namespaces. <br>
 * It also holds the shared <code>XPathContext</code> instance which contains
 * the standard namespace bindings.
 * 
 * @author Iwan Memruk
 * @version $Revision: 1172 $
 * @see nu.xom.XPathContext
 */
public final class MessageConstants {
	/**
	 * The standard namespace for Intalio Business Processes. Used by the
	 * Workflow Processes messages.
	 */
	public final static String IB4P_NS = "http://www.intalio.com/bpms/workflow/ib4p_20051115";

	/**
	 * The namespace of SOAP Envelope elements.
	 * 
	 * @see <a href="http://www.w3.org/TR/soap/">The SOAP specification</a>
	 */
	public final static String SOAPENV_NS = "http://schemas.xmlsoap.org/soap/envelope/";

	/**
	 * The namespace of Web Services Addressing elements, which can be present
	 * in any SOAP message received by the FDS.
	 * 
	 * @see <a href="http://www.w3.org/Submission/ws-addressing/">The Web
	 *      Services Addressing homepage</a>
	 */
	public final static String WS_ADDRESSING_NS = "http://schemas.xmlsoap.org/ws/2004/08/addressing";

	/**
	 * The standard namespace for W3C WS Addressing.
	 */
	public final static String ADDR_NS = "http://www.w3.org/2005/08/addressing";

	/**
	 * The standard namespace for Intalio Magic Session.
	 */
	public final static String INTALIO_NS = "http://www.intalio.com/type/session";

	public final static String AR_NS = "http://www.intalio.com/bpms/workflow/forms/examples/absence-request/absence-approval";
	/**
	 * Inaccessible instance constructor. <br>
	 * Prevents instantiation.
	 */

	public static HashMap<String, String> _nsMap = new HashMap<String, String>();
	static {
		_nsMap.put("addr", MessageConstants.ADDR_NS);
		_nsMap.put("intalio", MessageConstants.INTALIO_NS);
		_nsMap.put("soapenv", MessageConstants.SOAPENV_NS);
		_nsMap.put("ib4p", MessageConstants.IB4P_NS);
		_nsMap.put("wsa", MessageConstants.WS_ADDRESSING_NS);
		_nsMap.put("userProcess", MessageConstants.AR_NS);
	}

	private MessageConstants() {

	}

	public static HashMap<String, String> get_nsMap() {
		return _nsMap;
	}
	
	
}
