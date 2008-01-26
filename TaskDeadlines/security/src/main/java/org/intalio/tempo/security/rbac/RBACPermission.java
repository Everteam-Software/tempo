/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with the
 * written permission of Intalio Inc. or in accordance with the terms
 * and conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package org.intalio.tempo.security.rbac;

/**
 * RBAC permission to perform an operation on an object.
 *
 * @author <a href="http://www.intalio.com">&copy; Intalio Inc.</a>
 */
public final class RBACPermission
    implements java.io.Serializable
{

    /** Allowed operation */
    private String _operation;
    
    /** Target object */
    private String _object;
    

    /**
     * Construct a permission to perform an operation on an object.
     *
     * @param operation identifier of the allowed operation
     * @param object target object
     */
    public RBACPermission( String operation, String object )
    {
        if ( operation == null ) {
            throw new IllegalArgumentException( "Argument 'operation' is null" );
        }
        if ( object == null ) {
            throw new IllegalArgumentException( "Argument 'object' is null" );
        }
        _operation = operation;
        _object = object;
    }
    
    
    /**
     * Return the allowed operation of this permission.
     */
    public String getOperation()
    {
        return _operation;
    }

    
	/**
	 * Set the operation allowed for this permission.
	 */
	public void setOperation( String operation )
	{
		_operation = operation;
	}


    /**
     * Return the target object of this permission.
     */
    public String getObject()
    {
        return _object;
    }

    
	/**
	 * Set the target object of this permission.
	 */
	public void setObject( String object )
	{
		_object = object;
	}


    /**
     * Return true if this object is equal to another.
     */
    public boolean equals( Object obj )
    {
        RBACPermission other;
        
        if ( obj instanceof RBACPermission == false ) {
            return false;
        }
        
        other = (RBACPermission) obj;
        return ( other._operation.equals( this._operation ) &&
                 other._object.equals( this._object ) );
    }

    
    /**
     * Return a hash code for this object.
     */
    public int hashCode()
    {
        return _operation.hashCode() + _object.hashCode();
    }
    
}