package org.intalio.tempo.workflow.fds.module;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.AxisDescription;
import org.apache.axis2.description.AxisModule;
import org.apache.axis2.modules.Module;
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

	public void init(ConfigurationContext configContext, AxisModule module)
			throws AxisFault {
		try {
			configContext.getAxisConfiguration().engageModule(module.getName());
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
