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

package org.intalio.tempo.workflow.tas.axis2;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;

/**
 * This class is used as the Axis2 service implementation class (in <code>META-INF/services.xml</code>).
 * <p />
 * Its responsibility is to construct a shared instance of {@link org.intalio.tempo.workflow.tas.axis2.TASAxis2Bridge}.
 * Axis2 will instantiate <b>this</b> class on each request, and each new instance will reuse the same shared
 * {@link org.intalio.tempo.workflow.tas.axis2.TASAxis2Bridge} instance for actual request processing (which gets relaid
 * to the shared bridge instance).
 */
public class TASAxis2SingleInstanceFacade {

    private TASAxis2Bridge _bridgeInstance = null;

    public void setAxis2TASBridge(TASAxis2Bridge bridgeInstance) {
        _bridgeInstance = bridgeInstance;
    }

    /**
     * Relays <code>add</code> request processing to the shared bridge instance.
     */
    public OMElement add(OMElement soapPayloadElement)
            throws AxisFault {
        return _bridgeInstance.add(soapPayloadElement);
    }

    /**
     * Relays <code>delete</code> request processing to the shared bridge instance.
     */
    public OMElement delete(OMElement soapPayloadElement)
            throws AxisFault {
        return _bridgeInstance.delete(soapPayloadElement);
    }
}
