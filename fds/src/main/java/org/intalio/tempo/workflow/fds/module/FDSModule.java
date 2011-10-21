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
package org.intalio.tempo.workflow.fds.module;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.AxisDescription;
import org.apache.axis2.description.AxisModule;
import org.apache.axis2.modules.Module;
import org.apache.axis2.transport.local.LocalTransportReceiver;
import org.apache.neethi.Assertion;
import org.apache.neethi.Policy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Axis2 module is used to transform task-specific message sent from a UBP
 * to the generic TMP interface and vice versa. It provides an alternative
 * approch to the FDS servlet.
 * 
 * The module will engage itself globally.
 * 
 * @author Tammo van Lessen
 */
public class FDSModule implements Module {
	private static Logger _log = LoggerFactory.getLogger(FDSModule.class);
	public static final String FDS_HANDLER_CONTEXT = "org.intalio.tempo.workflow.fds::context";
	
	public void init(ConfigurationContext configContext, AxisModule module)
			throws AxisFault {
		try {
			configContext.getAxisConfiguration().engageModule(module.getName());
			
			// evil hack to overcome AXIS2-3219
			LocalTransportReceiver.CONFIG_CONTEXT = configContext;
			
			_log.info("FDS module initialized and engaged.");
		} catch (Exception e) {
			_log.error("Error during FDS module initialization", e);
		}
	}

	public void engageNotify(AxisDescription axisDescription) throws AxisFault {
		// nothing to do here.
	}

	public boolean canSupportAssertion(Assertion assertion) {
		return false;
	}

	public void applyPolicy(Policy policy, AxisDescription axisDescription)
			throws AxisFault {
		// nothing to do here.
	}

	public void shutdown(ConfigurationContext configurationContext)
			throws AxisFault {
		// nothing to do here.
	}

}
