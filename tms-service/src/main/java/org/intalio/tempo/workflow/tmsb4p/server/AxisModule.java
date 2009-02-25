package org.intalio.tempo.workflow.tmsb4p.server;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.AxisDescription;
import org.apache.axis2.modules.Module;
import org.apache.neethi.Assertion;
import org.apache.neethi.Policy;

public class AxisModule implements Module {

    public void applyPolicy(Policy arg0, AxisDescription arg1) throws AxisFault {
        // TODO Auto-generated method stub

    }

    public boolean canSupportAssertion(Assertion arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    public void engageNotify(AxisDescription arg0) throws AxisFault {
        // TODO Auto-generated method stub

    }

    public void init(ConfigurationContext arg0, org.apache.axis2.description.AxisModule arg1) throws AxisFault {
        // TODO Auto-generated method stub

    }

    public void shutdown(ConfigurationContext arg0) throws AxisFault {
        // TODO Auto-generated method stub

    }

}
