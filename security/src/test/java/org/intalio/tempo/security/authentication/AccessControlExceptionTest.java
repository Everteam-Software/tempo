package org.intalio.tempo.security.authentication;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccessControlExceptionTest extends TestCase {
    private final static Logger logger = LoggerFactory.getLogger(AccessControlException.class);

    public void testAccessControlException() {
        try {
            throw new AccessControlException("test");

        } catch (AccessControlException ee) {

            try {
                throw new AccessControlException("test", ee);
            } catch (AccessControlException eee) {
                try {
                    throw new AccessControlException("test");
                } catch (AccessControlException eeee) {

                    logger.info(eeee.getMessage());
                }

            }
        }

    }

}
