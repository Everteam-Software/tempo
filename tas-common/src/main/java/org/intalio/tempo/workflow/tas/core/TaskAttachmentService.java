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

import org.intalio.tempo.workflow.tas.core.AttachmentMetadata;
import org.intalio.tempo.workflow.tas.core.AuthCredentials;

/**
 * Defines the set of operations a TAS implementation must support.
 * 
 * @author Iwan Memruk
 * @version $Revision: 1021 $
 */
public interface TaskAttachmentService {

    /**
     * Stores a new attachment.<br />
     * This method overload loads the attachment payload from a local file.
     * 
     * @param authCredentials
     *            Authentication credentials for this invocation.
     * @param metadata
     *            Metadata of the new attachment.
     * @param localFileUrl
     *            URL of the local file to use as the attachment payload, such as
     *            <code>'file:///tmp/attachment.doc'</code>.
     * @return An HTTP URL the new attachment can be retrieved at.
     * @throws AuthException
     *             If the supplied <code>authCredentials</code> are incorrect or do not allow this operation.
     * @throws InvalidRequestException
     *             If the supplied parameters are invalid or insufficient to perform the operation.
     * @throws IOException
     *             If reading the payload from <code>localFileUrl</code> fails.
     */
    String add(AuthCredentials authCredentials, AttachmentMetadata metadata, String localFileUrl)
            throws AuthException,
                InvalidRequestException,
                IOException;

    /**
     * Stores a new attachment.<br />
     * This method overload uses an immediate byte array as the attachment payload.
     * 
     * @param authCredentials
     *            Authentication credentials for this invocation.
     * @param metadata
     *            Metadata of the new attachment.
     * @param payload
     *            Free-form data which will be used as the attachment payload.
     * @return An HTTP URL the new attachment can be retrieved at.
     * @throws AuthException
     *             If the supplied <code>authCredentials</code> are incorrect or do not allow this operation.
     * @throws InvalidRequestException
     *             If the supplied parameters are invalid or insufficient to perform the operation.
     */
    String add(AuthCredentials authCredentials, AttachmentMetadata metadata, byte[] payload)
            throws AuthException,
                InvalidRequestException;

    /**
     * Deletes an attachment.
     * 
     * @param authCredentials
     *            Authentication credentials for this invocation.
     * @param attachmentUrl
     *            URL of the attachment to delete.
     * @throws AuthException
     *             If the supplied <code>authCredentials</code> are incorrect or do not allow this operation.
     * @throws InvalidRequestException
     *             If the supplied parameters are invalid or insufficient to perform the operation.
     * @throws UnavailableAttachmentException
     *             If the requested attachment does not exist or is unavailable for the invoker to manage.
     */
    void delete(AuthCredentials authCredentials, String attachmentUrl)
            throws AuthException,
                InvalidRequestException,
                UnavailableAttachmentException;
}
