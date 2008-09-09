package org.intalio.tempo.deployment.impl;

/**
 * Persistence exception.
 */
public class PersistenceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Construct a new persistence exception wrapping an underlying exception and providing a message.
     * 
     * @param message
     *            The exception message
     * @param except
     *            The underlying exception
     */
    public PersistenceException(String message, Exception except) {
        super(message, except);
    }

    /**
     * Construct a new persistence exception with a message.
     * 
     * @param message
     *            The exception message
     */
    public PersistenceException(String message) {
        super(message);
    }

    /**
     * Construct a new persistence exception wrapping an underlying exception.
     * 
     * @param except
     *            The underlying exception
     */
    public PersistenceException(Exception except) {
        super(except);
    }

}
