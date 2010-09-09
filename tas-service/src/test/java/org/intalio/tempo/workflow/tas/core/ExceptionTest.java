package org.intalio.tempo.workflow.tas.core;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionTest extends TestCase {
    private final static Logger logger = LoggerFactory.getLogger(ExceptionTest.class);

    public void testAuthException() {
        try {
            throw new AuthException();
        } catch (AuthException e) {

            try {
                throw new AuthException(e);

            } catch (AuthException ee) {

                try {
                    throw new AuthException("test", ee);
                } catch (AuthException eee) {
                    try {
                        throw new AuthException("test");
                    } catch (AuthException eeee) {

                        logger.info(eeee.getMessage());
                    }

                }
            }

        }
    }
    
    public void testUnavailableAttachmentException() {
        try {
            throw new UnavailableAttachmentException();
        } catch (UnavailableAttachmentException e) {

            try {
                throw new UnavailableAttachmentException(e);

            } catch (UnavailableAttachmentException ee) {

                try {
                    throw new UnavailableAttachmentException("test", ee);
                } catch (UnavailableAttachmentException eee) {
                    try {
                        throw new UnavailableAttachmentException("test");
                    } catch (UnavailableAttachmentException eeee) {

                        logger.info(eeee.getMessage());
                    }

                }
            }

        }
    }

    public void testInvalidRequestException() {
        try {
            throw new InvalidRequestException();
        } catch (InvalidRequestException e) {

            try {
                throw new InvalidRequestException(e);

            } catch (InvalidRequestException ee) {

                try {
                    throw new InvalidRequestException("test", ee);
                } catch (InvalidRequestException eee) {
                    try {
                        throw new InvalidRequestException("test");
                    } catch (InvalidRequestException eeee) {

                        logger.info(eeee.getMessage());
                    }

                }
            }

        }
    }
    
}
