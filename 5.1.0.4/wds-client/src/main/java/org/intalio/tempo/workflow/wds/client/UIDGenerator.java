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
package org.intalio.tempo.workflow.wds.client;

import java.util.Random;

public class UIDGenerator {
    public static final int UID_LENGTH = 32;
    public static final String _USED_CHARACTERS="abcdefghijklmnopqrstuvwxyz0123456789";

    public UIDGenerator() {

    }

    public String generateUID() {
        StringBuilder builder = new StringBuilder(UIDGenerator.UID_LENGTH);
        Random rng = new Random();
        for (int i = 0; i < UIDGenerator.UID_LENGTH; ++i) {
            int index = rng.nextInt(_USED_CHARACTERS.length());
            char c = _USED_CHARACTERS.charAt(index);
            builder.append(c);
        }
        return builder.toString();
    }
}
