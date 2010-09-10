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
 * Defines the set of methods an item DAO connection must provide.
 */
public interface ItemDaoConnection {
    
    /**
     * Commits the changes done to the database using this DAO connection.
     */
    void commit();

    /**
     * Closes this DAO connection.
     * <p />
     * If {@link #commit()} was not called before this method, all changes done to the database using this DAO
     * connection instance will be lost.
     */
    void close();

    /**
     * Stores an item on WDS.
     */
    void storeItem(Item item)
            throws UnavailableItemException;

    /**
     * Deletes an item from WDS.
     */
    void deleteItem(String uri)
            throws UnavailableItemException;

    /**
     * Retrieves a stored item from WDS.
     */
    Item retrieveItem(String uri)
            throws UnavailableItemException;

    /**
     * Checks whether an item is stored at a specific URI.
     */
    boolean itemExists(String uri);
}
