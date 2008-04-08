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
package org.intalio.tempo.workflow.tas.core;

import java.io.IOException;
import java.io.InputStream;

import org.intalio.tempo.workflow.tas.core.AttachmentMetadata;

/**
 * Storage strategy is responsible for storing attachment payload and removing it from storage.
 */
public interface StorageStrategy {
    
    /**
     * Stores an attachment payload provided using an {@link InputStream}.
     * 
     * @param metadata
     *            Attachment metadata.
     * @param payload
     *            An <code>InputStream</code> containing the attachment payload.
     * @return An absolute HTTP URL at which the attachment payload is now available (and can be obtained using a GET
     *         request).
     * @throws IOException
     *             If the payload storage operation failed. <b>Note:</b> logicallly, there can be no high-level error
     *             at this point, thus only a low-level <code>IOException</code> may be thrown.
     */
    String storeAttachment(AttachmentMetadata metadata, InputStream payload)
            throws IOException;

    /**
     * Deletes a stored attachment payload from the storage.
     * 
     * @param url
     *            The absolute URL of the attachment payload to delete from storage.
     * @throws UnavailableAttachmentException
     *             If the specified attachment payloadis not available for deletion.
     */
    void deleteAttachment(String url)
            throws UnavailableAttachmentException;
}
