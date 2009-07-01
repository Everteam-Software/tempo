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

import java.util.Arrays;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;

import org.apache.openjpa.persistence.Persistent;

/**
 * A WDS item is a byte stream which is stored on WDS at a specific URI.
 * 
 * @author Iwan Memruk
 * @version $Revision: 1176 $
 */
@Entity
@Table(name = "TEMPO_ITEM")
@NamedQueries({
    @NamedQuery(
            name=Item.FIND_BY_URI, 
            query="select m from Item m where m._uri=?1", 
            hints={ @QueryHint  (name="openjpa.hint.OptimizeResultCount", value="1")}),
    @NamedQuery(
             name=Item.COUNT_FOR_URI, 
             query="select COUNT(m) from Item m where m._uri=?1", 
             hints={ @QueryHint  (name="openjpa.hint.OptimizeResultCount", value="1")})       
    })
public class Item {

	public static final String FIND_BY_URI = "find_by_uri";
    public static final String COUNT_FOR_URI = "count_for_uri";

    public Item() {
        
    }
    
    @Persistent
    @Column(name="uri")
    private String _uri;
    
    @Persistent
    @Column(name="content_type")
    private String _contentType;
    
    @Persistent
    @Column(name="payload", length=4096)
    @Lob
    private byte[] _payload;
    
    @Persistent
    @Column(name="lastmodified")
    private Date _lastmodified;
    
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

    
    
    public Date getLastmodified() {
        return _lastmodified;
    }


    public void setLastmodified(Date _lastmodified) {
        this._lastmodified = _lastmodified;
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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Item)) return false;
        Item item = (Item)obj;
        return 
        item._contentType.equals(_contentType)
        && Arrays.equals(item._payload,_payload)
        && _uri.equals(_uri);
    }
    
}
