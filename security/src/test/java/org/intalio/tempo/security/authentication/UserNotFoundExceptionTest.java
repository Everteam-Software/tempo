package org.intalio.tempo.security.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;

public class UserNotFoundExceptionTest extends TestCase {
    private final static Logger logger = LoggerFactory.getLogger(UserNotFoundException.class);

    public void testUserNotFoundException() {
        try {
            throw new UserNotFoundException("test");

        } catch (UserNotFoundException ee) {

            try {
                throw new UserNotFoundException("test", ee);
            } catch (UserNotFoundException eee) {
                try {
                    throw new UserNotFoundException("test");
                } catch (UserNotFoundException eeee) {

                    logger.info(eeee.getMessage());
                }

            }
        }

    }
}
