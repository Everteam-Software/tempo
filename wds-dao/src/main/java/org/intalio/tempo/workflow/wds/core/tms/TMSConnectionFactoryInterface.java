package org.intalio.tempo.workflow.wds.core.tms;

public interface TMSConnectionFactoryInterface {

    /**
     * Returns a new TMS connection instance. <br />
     * It is required that you use the <code>close()</code> method after using the connection instance.
     */
    public TMSConnectionInterface getTMSConnection();

}