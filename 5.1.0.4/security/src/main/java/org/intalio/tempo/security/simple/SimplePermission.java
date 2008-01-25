/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with the
 * written permission of Intalio Inc. or in accordance with the terms
 * and conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package org.intalio.tempo.security.simple;

/**
 * Permission definition.
 *
 * @author <a href="boisvert@intalio.com">Alex Boisvert</a>
 */
public class SimplePermission
{

    /**
     * Operation
     */
    private String _operation;
    
    
    /**
     * Target object.
     */
    private String _object;

    
    /** 
     * Construct a SimplePermission (used for unmarshalling)
     */
    public SimplePermission()
    {
        // empty
    }
    
    
    /**
     * Get the operation.
     */
    public String getOperation()
    {
        return _operation;
    }
    
    
    /**
     * Set the operation.
     */
    public void setOperation( String operation )
    {
        _operation = operation;
    }
    
    /**
     * Get the target object.
     */
    public String getObject()
    {
        return _object;
    }
    
    
    /**
     * Set the target object.
     */
    public void setObject( String object )
    {
        _object = object;
    }
    
}
