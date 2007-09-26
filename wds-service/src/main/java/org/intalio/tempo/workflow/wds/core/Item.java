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
 * A WDS item is a byte stream which is stored on WDS at a specific URI.
 * 
 * @author Iwan Memruk
 * @version $Revision: 1176 $
 */
public class Item {
    private String _uri;
    private String _contentType;
    private byte[] _payload;
    
    /**
     * Create an Item
     */
    public Item(String uri, String contentType, byte[] payload) {
        if (uri == null) throw new NullPointerException("uri");
        if (contentType == null) throw new NullPointerException("contentType");
        if (payload == null) throw new NullPointerException("payload");
        _uri = uri;
        _contentType = contentType;
        _payload = payload;
    }

    /**
     * Returns the (relative) URI of this item.
     * <p />
     * Typically, if the URI of an item is <code>a/b/c</code>, the full URL to access it would be 
     * something like <code>http://localhost:8081/wds/a/b/c</code>
     */
    public String getURI() {
        return _uri;
    }

    /**
     * Returns the MIME content type of this item.
     */
    public String getContentType() {
        return _contentType;
    }

    /**
     * Returns the length of this item, in bytes.
     */
    public int getContentLength() {
        return _payload.length;
    }
    
    /**
     * Returns the payload of this item, as a byte array.
     */
    public byte[] getPayload() {
        return _payload;
    }
    
}
