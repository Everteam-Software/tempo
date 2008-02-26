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
package org.intalio.tempo.workflow.wds.core;


/**
 * This factory class is used to obtain {@link org.intalio.tempo.workflow.wds.core.WDSService} instances.
 * <p />
 * To instantiate this class, you first need an {@link org.intalio.tempo.workflow.wds.core.ItemDaoConnectionFactory}
 * instance for this factory to get DAO connections from.
 *
 * @author Iwan Memruk
 * @version $Revision: 536 $
 */
public class WDSServiceFactory {
    /**
     * The DAO connection factory to get DAO connections from.
     */
    private ItemDaoConnectionFactory _daoFactory;
    private String _tmsEndpoint;
    private String _wdsEndpoint;
    
    public WDSServiceFactory() {
    	
    }
    
    public String getTmsEndpoint() {
        return _tmsEndpoint;
    }

    public void setTmsEndpoint(String endpoint) {
        _tmsEndpoint = endpoint;
    }

    public void setDaoFactory(ItemDaoConnectionFactory daoFactory) {
    	_daoFactory = daoFactory;
    }

    public ItemDaoConnectionFactory getDaoFactory() {
    	return _daoFactory;
    }

    public void setWdsEndpoint(String wdsEndpoint) {
        this._wdsEndpoint = wdsEndpoint;
    }

    public String getWdsEndpoint() {
        return _wdsEndpoint;
    }

    /**
     * Creates a new {@link WDSService} instance.
     * <p />
     * The created instance will use a DAO connection provided by the DAO connection factory that was specified when
     * instantiating this factory.
     * <p />
     * It is required for normal functioning that you use the {@link WDSService#close()} method to finalize each
     * <code>WDSService</code> instance after you are finished with it (typically in a <code>finally</code> clause).
     * 
     * @return A new <code>WDSService</code> instance.
     */
    public WDSService getWDSService() {
        return new WDSService(_daoFactory.getItemDaoConnection(), _tmsEndpoint);
    }
}
