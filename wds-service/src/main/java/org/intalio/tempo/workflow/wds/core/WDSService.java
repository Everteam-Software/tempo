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

import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.wds.core.tms.TMSConnectionInterface;
import org.slf4j.LoggerFactory;

/**
 * This class represents a single connection to the WDS service. <p/> There is
 * no public constructor. Use
 * {@link org.intalio.tempo.workflow.wds.core.WDSServiceFactory} to obtain a
 * <code>WDSService</code> instance. <p/> Instances of this class are usable
 * only for one transaction. If you wish to commit any changes performed to WDS
 * using a <code>WDSService</code> instance, call the {@link #commit()}
 * method, and then the {@link #close} method. If you wish to rollback (lose)
 * all the changes, simply call {@link #close()} without calling
 * {@link #commit()}. <p/> If you need to perform another transaction, get
 * another <code>WDSService</code> using
 * {@link org.intalio.tempo.workflow.wds.core.WDSServiceFactory} <p/> It is
 * required for normal functioning that you use the {@link #close()} method to
 * finalize each <code>WDSService</code> instance after you are finished with
 * it (typically in a <code>finally</code> clause). <p/> This object is not
 * thread-safe.
 */
public class WDSService {

    private static final String OXF_PREFIX = "oxf://";

    private ItemDaoConnection _dao;

    private TMSConnectionInterface _tmsConnection;

    private String _wdsEndpoint;

    /**
     * Package-accessible constructor. Used by {@link WDSServiceFactory}.
     */
    WDSService(ItemDaoConnection dao, TMSConnectionInterface tmsConnection) {
        if (dao == null)
            throw new NullPointerException("dao");
        if (tmsConnection == null)
            throw new NullPointerException("tmsConnection");
        _dao = dao;
        _tmsConnection = tmsConnection;
    }

    /**
     * Validates request parameters (e.g. checks for <code>null</code>).
     */
    private void validateRequest(Item item, String participantToken) {
        if (item == null)
            throw new NullPointerException("item");
        if (participantToken == null)
            throw new NullPointerException("participantToken");
    }

    /**
     * Validates request parameters (e.g. checks for <code>null</code>).
     */
    private void validateRequest(Object uri, String participantToken) {
        if (uri == null)
            throw new NullPointerException("uri");
        if (participantToken == null)
            throw new NullPointerException("participantToken");
    }

    public void setWdsEndPoint(String wdsEndpoint) {
        _wdsEndpoint = wdsEndpoint;
    }

    /**
     * Stores an item on WDS.
     */
    public void storeItem(Item item, String participantToken) throws AuthenticationException, UnavailableItemException {
        validateRequest(item, participantToken);
        String uri = item.getURI();

        if (_dao.itemExists(uri))
            _dao.deleteItem(uri);
        _dao.storeItem(item);
        _dao.commit();
    }

    /**
     * Creates a PIPA task in TMS.
     */
    public void storePipaTask(PIPATask pipaTask, String participantToken) throws AuthenticationException {
        if (pipaTask == null)
            throw new NullPointerException("pipaTask");
        if (participantToken == null)
            throw new NullPointerException("participantToken");

        _tmsConnection.deletePipaTask(pipaTask.getFormURLAsString());
        _tmsConnection.storePipaTask(pipaTask);
        _tmsConnection.commit();
    }

    /**
     * Deletes an item from WDS.
     */
    public void deleteItem(String uri, String participantToken) throws AuthenticationException,
            UnavailableItemException {
        validateRequest(uri, participantToken);
        _dao.deleteItem(uri);
        _dao.commit();
    }

    public void deletePIPA(String participantToken, PIPATask pipaTask) {
        validateRequest(pipaTask.getFormURL(), participantToken);
        _tmsConnection.deletePipaTask(pipaTask.getFormURLAsString());
        // TODO: check this is really not needed
        // _tmsConnection.deletePipaTask(OXF_PREFIX + pipaTask.getFormURL());
        // _tmsConnection.deletePipaTask(_wdsEndpoint + pipaTask.getFormURL());
        _tmsConnection.commit();
    }

    /**
     * Retrieves an item from WDS.
     */
    public Item retrieveItem(String uri, String participantToken) throws AuthenticationException,
            UnavailableItemException {
        validateRequest(uri, participantToken);
        return _dao.retrieveItem(uri);
    }

    /**
     * Commits the changes done to WDS using this instance.
     */
    public void commit() {
        LoggerFactory.getLogger(this.getClass()).info("Commit");
        // commit the item
        _dao.commit();
        // commit the pipa
        _tmsConnection.commit();
    }

    /**
     * Closes this WDSService instance.
     * <p />
     * If {@link #commit()} was not called before this method, any changes done
     * to WDS using this instance will be lost.
     */
    public void close() {
        _dao.close();
        _tmsConnection.close();
    }

}
