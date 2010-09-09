/**
 * Copyright (c) 2005-2008 Intalio inc.
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
 * Factory for {@link WDSService} instances.
 */
public class WDSServiceFactory {
    /**
     * The DAO connection factory to get DAO connections from.
     */
    private ItemDaoConnectionFactory _daoFactory;
    private String _tmsEndpoint;
    private String _wdsEndpoint;

    public WDSServiceFactory() {
        // nothing
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
        _wdsEndpoint = wdsEndpoint;
    }

    public String getWdsEndpoint() {
        return _wdsEndpoint;
    }

    /**
     * Creates a new {@link WDSService} instance.
     * <p />
     * The created instance will use a DAO connection provided by the DAO
     * connection factory that was specified when instantiating this factory.
     * <p />
     * Clients must call {@link WDSService#close()} to release resources
     * associated with this object.
     * 
     * @return A new <code>WDSService</code> instance.
     */
    public WDSService getWDSService() {
        return new WDSService(_daoFactory.getItemDaoConnection(), _tmsEndpoint);
    }
}
