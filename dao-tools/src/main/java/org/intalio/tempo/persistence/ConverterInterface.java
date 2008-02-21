package org.intalio.tempo.persistence;

import java.util.List;

public interface ConverterInterface {

    /**
     * Copy an item from jdbc to jpa
     */
    public void copyItem(String uri) throws Exception;

    /**
     * Copy a task from jdbc to jpa
     */
    public void copyTask(String id) throws Exception;

    /**
     * Find all items urls
     */
    public List<String> findAllItems() throws Exception;

    /**
     * find all task ids
     */
    public List<String> findAllTasks() throws Exception;

    public void copyAllTasks() throws Exception;

    public void copyAllItems() throws Exception;

}