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
 * Defines the set of methods an item DAO connection factory must provide.
 * 
 * @author Iwan Memruk
 * @version $Revision: 40 $
 */
public interface ItemDaoConnectionFactory {
    /**
     * Returns an item DAO connection.
     * <p />
     * It is required for normal functioning that each item DAO connection must be closed after usage, typically in a
     * <code>finally</code> clause.
     * 
     * @return An item DAO connection.
     */
    ItemDaoConnection getItemDaoConnection();
}
