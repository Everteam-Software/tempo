package org.intalio.tempo.workflow.wds.core.tms;

import org.intalio.tempo.workflow.task.PIPATask;

public interface TMSConnectionInterface {

    /**
     * Stores a PIPA task in TMS database.
     */
    public void storePipaTask(PIPATask task);

    /**
     * Deletes all PIPA tasks which use the specified form URL, from TMS database.
     *
     * @param formUrl The form URL. Tasks which use this form URL will be deleted by this method.
     */
    public void deletePipaTask(String formUrl);

    /**
     * Commits the changes done to TMS database using this connection.
     */
    public void commit();

    /**
     * Closes this connection.
     * <p/>
     * If {@link #commit()} was not called before this method, all changes to TMS database 
     * using this connection instance will be lost.
     */
    public void close();

}