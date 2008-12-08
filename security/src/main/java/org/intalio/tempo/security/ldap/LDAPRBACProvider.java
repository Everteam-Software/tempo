/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with the
 * written permission of Intalio Inc. or in accordance with the terms
 * and conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *
 * $Id: LDAPRBACProvider.java,v 1.13 2005/02/24 18:14:12 boisvert Exp $
 */
package org.intalio.tempo.security.ldap;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.rbac.ObjectNotFoundException;
import org.intalio.tempo.security.rbac.RBACAdmin;
import org.intalio.tempo.security.rbac.RBACException;
import org.intalio.tempo.security.rbac.RBACQuery;
import org.intalio.tempo.security.rbac.RBACRuntime;
import org.intalio.tempo.security.rbac.RoleNotFoundException;
import org.intalio.tempo.security.rbac.UserNotFoundException;
import org.intalio.tempo.security.rbac.provider.RBACProvider;
import org.intalio.tempo.security.util.IdentifierUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LDAP RBAC Provider
 * 
 * @author <a href="http://www.intalio.com">&copy; Intalio Inc.</a>
 */
class LDAPRBACProvider implements RBACProvider, LDAPProperties {

    final static Logger LOG = LoggerFactory.getLogger("tempo.security");

    private final static String[] EMPTY_STRINGS = new String[0];

    private String                  _realm;
    
    private LDAPRBACQuery           _query;
    
    private LDAPQueryEngine         _engine;
    
    /**
     * Constructor
     * 
     */
    public LDAPRBACProvider(String realm, LDAPQueryEngine engine, String baseDN) {
        super();
        _realm = realm;
        _engine = engine;
    }

    /**
     * @param config
     * @throws RBACException
     */
    public void initialize(Object config) throws RBACException {
        if ( !(config instanceof Map ) )
            throw new IllegalArgumentException("Configuration is expected to be a Map");
        _query = new LDAPRBACQuery((Map)config);
    }

    /**
     * @throws RBACException
     */
    public String getName() throws RBACException {
        return "LDAP RBAC Provider";
    }

    /**
     * @see org.intalio.tempo.security.rbac.provider.RBACProvider#getAdmin()
     */
    public RBACAdmin getAdmin() throws RBACException {
        throw new RuntimeException("Method not implemented");
    }

    /**
     * @see org.intalio.tempo.security.rbac.provider.RBACProvider#getQuery()
     */
    public RBACQuery getQuery() throws RBACException {
        return _query;
    }

    /**
     * @see org.intalio.tempo.security.rbac.provider.RBACProvider#getRuntime()
     */
    public RBACRuntime getRuntime() throws RBACException {
        throw new RuntimeException("Method not implemented");
    }

    public void dispose() throws RBACException {
    	return;
    }
    

    static Map<String,String> readProperties(String keyRoot, Map source) 
    throws IllegalArgumentException {
        Map<String,String> result = null;
        for (int i=0; true; i++) {
            String key   = keyRoot+'.'+i;
            String value = (String)source.get(key);
            if (value==null)
                break;
            int colon = value.indexOf(':');
            String front, back;
            if (colon==-1 ) {
                front = back = value;
            } else if ( colon==0 ) {
                front = value.substring(1);
                back = front;
            } else if ( colon==value.length()-1 ) {
                front = value.substring(0, colon);
                back = front;
            } else {
                front = value.substring(0, colon).trim();
                back  = value.substring(colon+1).trim();
            }
            if (front.length()==0 || back.length()==0)
                throw new IllegalArgumentException("Format is not reconized! key: "+key+" value: "+value);
            if (result==null)
                result = new TreeMap<String,String>();
            result.put(back, front);
        }
        return result;
    }

    static String getNonNull(String key, Map map) 
    throws IllegalArgumentException {
        Object res = map.get(key);
        if (res!=null)
            return res.toString();

        StringBuffer sb = new StringBuffer();
        sb.append(key);
        sb.append(" cannot be null!");
        throw new IllegalArgumentException(sb.toString());
    }

    private String[] prefix( Collection<String> col ) {
        String[] result = new String[col.size()];
        col.toArray(result);
        return prefix(result);
    }

    private String[] prefix( String[] result ) {
        for (int i = 0; i < result.length; i++) {
            result[i] = _realm+"\\"+result[i];
        }
        return result;
    }
    
    class LDAPRBACQuery implements RBACQuery, LDAPProperties {
        
        private String _permObjects;

        private String _permId;

        private String _permBase;

        private String _rolePerms;

        private String _permRoles;

        private String _roleBase;
        
        private String _roleId;
        
        private String _userBase;
        
        private String _userId;
        
        private String _userRoles;
        
        private Map<String,String> _userProp;
        
        private String _userAllroles;

        private String _roleUsers;
        
        private Map<String,String> _roleProp;
        
        private String _roleAscen;
        
        private String _roleDescen;
        
        /**
         * Constructor
         * @param map contains all configuration
         */
        LDAPRBACQuery( Map map ) {
            _roleBase   = getNonNull(SECURITY_LDAP_ROLE_BASE, map);
            _roleId     = getNonNull(SECURITY_LDAP_ROLE_ID, map);
            _userBase   = getNonNull(SECURITY_LDAP_USER_BASE, map);
            _userId     = getNonNull(SECURITY_LDAP_USER_ID, map);
            
            _userRoles  = (String)map.get(SECURITY_LDAP_USER_ROLES);
            _roleUsers  = (String)map.get(SECURITY_LDAP_ROLE_USERS);
            _userAllroles = (String)map.get(SECURITY_LDAP_USER_ALLROLES);
            if (_userRoles==null && _roleUsers==null) {
                StringBuffer sb = new StringBuffer();
                sb.append(SECURITY_LDAP_ROLE_USERS);
                sb.append(" and ");
                sb.append(SECURITY_LDAP_USER_ROLES);
                sb.append(" cannot be both null!");
                throw new IllegalArgumentException(sb.toString());
            } else if (_userRoles!=null && _roleUsers!=null) {
                StringBuffer sb = new StringBuffer();
                sb.append(SECURITY_LDAP_ROLE_USERS);
                sb.append(" and ");
                sb.append(SECURITY_LDAP_USER_ROLES);
                sb.append(" is mutal exclusive!");
                throw new IllegalArgumentException(sb.toString());
            }
            _roleAscen  = (String)map.get(SECURITY_LDAP_ROLE_ASCEN);
            _roleDescen = (String)map.get(SECURITY_LDAP_ROLE_DESCEN);

            _permId     = getNonNull(SECURITY_LDAP_PERM_ID, map);
            _permBase   = getNonNull(SECURITY_LDAP_PERM_BASE, map);
            _permObjects= getNonNull(SECURITY_LDAP_PERM_OBJECTS, map);
            
            _permRoles  = (String)map.get(SECURITY_LDAP_PERM_ROLES);
            _rolePerms  = (String)map.get(SECURITY_LDAP_ROLE_PERMS);
            if (_permRoles==null && _rolePerms==null) {
                StringBuffer sb = new StringBuffer();
                sb.append(SECURITY_LDAP_PERM_ROLES);
                sb.append(" and ");
                sb.append(SECURITY_LDAP_ROLE_PERMS);
                sb.append(" cannot be both null!");
                throw new IllegalArgumentException(sb.toString());
            }
            _userProp = readProperties(SECURITY_LDAP_USER_PROP, map);

            _roleProp = readProperties(SECURITY_LDAP_ROLE_PROP, map);
        }
                
        /**
         * @see org.intalio.tempo.security.rbac.RBACQuery#assignedUsers(java.lang.String)
         */
        public String[] assignedUsers(String role) 
        throws RoleNotFoundException, RBACException, RemoteException {
            
            try {
                role = IdentifierUtils.stripRealm(role);
                ArrayList<String> list = new ArrayList<String>();
                short found;
                if (_userRoles!=null) {
                    // foreign key on the user
                    boolean checkRole = true;
                    found = _engine.queryRelations(role, _roleBase, _roleId, _userBase, _userRoles, checkRole, list);

                } else {
                    // _roleUsers!=null, foreign key on the role
                    found = _engine.queryFilteredFields(role, _roleBase, _roleId, _roleUsers, _userBase, list);
                }
                if (found==LDAPQueryEngine.SUBJECT_NOT_FOUND)
                    throw new RoleNotFoundException("Role, "+role+", is not found!");
                if (found==LDAPQueryEngine.RELATION_NOT_FOUND)
                    return EMPTY_STRINGS;

                return prefix(list);
            } catch ( NameNotFoundException nnfe ) {
                if (LOG.isInfoEnabled())
                    LOG.info("Role, "+role+", is not found!");
                throw new RoleNotFoundException("Role, "+role+", is not found!", nnfe);
            } catch ( NamingException ne ) {
                if (LOG.isInfoEnabled())
                    LOG.info(ne.getMessage(),ne);
                throw new RBACException(ne);
            }
        }
        
        /**
         * @see org.intalio.tempo.security.rbac.RBACQuery#authorizedUsers(java.lang.String)
         */
        public String[] authorizedUsers(String role) 
        throws RoleNotFoundException, RBACException, RemoteException {

            if (_roleDescen==null && _roleAscen==null)
                // no inherit role support, same as the "flat" method
                return assignedUsers(role);

            role = IdentifierUtils.stripRealm(role);
            try {
                // get all sub roles
                TreeSet<String> list = new TreeSet<String>();
                list.add(role); 
                short found;
                if (_roleAscen!=null)
                    found = _engine.queryInheritRelation(_roleBase, _roleId, _roleAscen, list);
                else
                    found = _engine.queryInheritReference(_roleBase, _roleId, _roleDescen, list);
                if (found==LDAPQueryEngine.SUBJECT_NOT_FOUND)
                    throw new RoleNotFoundException("Role, "+role+", is not found!");
                if (LOG.isDebugEnabled())
                    LOG.debug("roles: "+list);

                TreeSet<String> result = new TreeSet<String>();
                if (_userRoles!=null) {
                    // foreign key on the user
                    _engine.queryRelations(list, _roleBase, _roleId, _userBase, _userRoles, result);
                } else { //if (_roleUsers!=null)
                    // foreign key on the role
                    _engine.queryFilteredFields(list, _roleBase, _roleId, _roleUsers, _userBase, result);
                }
                return prefix(result);
            } catch ( NamingException ne ) {
                if (LOG.isInfoEnabled())
                    LOG.info(ne.getMessage(),ne);
                throw new RBACException(ne);
            }
        }

        /**
         * @see org.intalio.tempo.security.rbac.RBACQuery#assignedRoles(java.lang.String)
         */
        public String[] assignedRoles(String user) 
        throws UserNotFoundException, RBACException, RemoteException {

            user = IdentifierUtils.stripRealm(user);
            try {
                ArrayList<String> list = new ArrayList<String>();
                short result;
                if (_userRoles!=null) {
                    result = _engine.queryFields(user, _userBase, _userId, _userRoles, list);
                } else {
                    boolean checkUser = true;
                    result = _engine.queryRelations(user, _userBase, _userId, _roleBase, _roleUsers, checkUser, list);
                }

                if (result==LDAPQueryEngine.SUBJECT_NOT_FOUND)
                    throw new UserNotFoundException("User, "+user+", is not found!");
                if (result==LDAPQueryEngine.RELATION_NOT_FOUND)
                    return EMPTY_STRINGS;

                return prefix(list);
            } catch (NamingException ne) {
                if (LOG.isInfoEnabled())
                    LOG.info(ne.getMessage(),ne);
                throw new RBACException(ne);
            }
        }

        /**
         * @see org.intalio.tempo.security.rbac.RBACQuery#authorizedRoles(java.lang.String)
         */
        public String[] authorizedRoles(String user) 
        throws UserNotFoundException, RBACException, RemoteException {

            if (_roleDescen==null && _roleAscen==null)
                // no inherit role support, same as the "flat" method
                return assignedRoles(user);
            
            user = IdentifierUtils.stripRealm(user);
            try {
                ArrayList<String> list = new ArrayList<String>();
                short result;
                if (_userAllroles!=null) {  
                    // server support the shortcut
                    result = _engine.queryFields(user, _userBase, _userId, _userAllroles, list);
                } else if (_userRoles!=null) {
                    result = _engine.queryFields(user, _userBase, _userId, _userRoles, list);
                } else {
                    boolean checkUser = true;
                    result = _engine.queryRelations(user, _userBase, _userId, _roleBase, _roleUsers, checkUser, list);
                }

                if (result==LDAPQueryEngine.SUBJECT_NOT_FOUND)
                    throw new UserNotFoundException("User '"+user+"' is not found!");
                if (result==LDAPQueryEngine.RELATION_NOT_FOUND)
                    return EMPTY_STRINGS;
                if (result==LDAPQueryEngine.FIELD_NOT_FOUND)
                    return EMPTY_STRINGS;

                // include all super roles
                if (_roleAscen!=null)
                    _engine.queryInheritReference(_roleBase, _roleId, _roleAscen, list);
                else
                    _engine.queryInheritRelation(_roleBase, _roleId, _roleDescen, list);

                if ( LOG.isDebugEnabled() ) {
                    LOG.debug( "user '" + user + "' authorizedRoles: " + list );                    
                }
                return prefix(list);
            } catch (NamingException ne) {
                if (LOG.isInfoEnabled())
                    LOG.info(ne.getMessage(),ne);
                throw new RBACException(ne);
            }
        }


        /**
         * @see org.intalio.tempo.security.rbac.RBACQuery#topRoles(java.lang.String)
         */
        public String[] topRoles( String realm ) throws RBACException, RemoteException {
            if (!_realm.equals(realm))
                throw new RBACException("Unsupported realm, "+realm);
                
            try {
                ArrayList<String> list = new ArrayList<String>();
                if (_roleAscen==null&&_roleDescen==null) {
                    // return all roles
                    _engine.queryExtent(_roleBase, _roleId, list);
                } else if (_roleAscen!=null ) {
                    // record contains no reference of ascendants
                    _engine.queryNoRelation(_roleBase, _roleAscen, list);
                } else {
                    // warning, operation here is very expensive, we 
                    // basically iterate all records of role twice
                    
                    // record contains the reference of ascendants
                    // we find all tops role by sustract all roles by
                    // roles that contains in the descendant list
                    _engine.queryExtent(_roleBase, _roleId, list);
                    
                    TreeSet<String> descen = new TreeSet<String>();
                    _engine.queryExistRelation(_roleBase, _roleId, _roleDescen, _roleBase, descen);
                    list.removeAll(descen);
                }
                
                return prefix(list);
            } catch ( NamingException ne ) {
                if (LOG.isInfoEnabled())
                    LOG.info(ne.getMessage(),ne);
                throw new RBACException(ne);
            }
        }

        /**
         */
        /*
        public String[] allRoles( String realm ) throws RBACException, RemoteException {
            if (!_realm.equals(realm))
                throw new RBACException("Unsupported realm, "+realm);
                
            try {
                ArrayList list = new ArrayList();
                // return all roles
                _engine.queryExtent(_roleBase, _roleId, list);
                
                return prefix(list);
            } catch ( NamingException ne ) {
                if (LOG.isInfoEnabled())
                    LOG.info(ne.getMessage(),ne);
                throw new RBACException(ne);
            }
        }*/
        
        /**
         * @see org.intalio.tempo.security.rbac.RBACQuery#descendantRoles(java.lang.String)
         */
        public String[] descendantRoles(String role) 
        throws RoleNotFoundException, RBACException, RemoteException {

            role = IdentifierUtils.stripRealm(role);
            try {
                TreeSet<String> result = new TreeSet<String>();
                result.add(role);
                short found;
                if (_roleAscen!=null)
                    found = _engine.queryInheritRelation(_roleBase, _roleId, _roleAscen, result);
                else
                    found = _engine.queryInheritReference(_roleBase, _roleId, _roleDescen, result);
                if (found==LDAPQueryEngine.SUBJECT_NOT_FOUND)
                    throw new RoleNotFoundException("Role, "+role+", is not found!");
                
                if (result.size()==0)
                    return EMPTY_STRINGS;
                else {
                    result.remove(role);
                    return prefix(result);
                }
            } catch (NamingException ne) {
                if (LOG.isInfoEnabled())
                    LOG.info(ne.getMessage(),ne);
                throw new RBACException(ne);
            }
        }

        /**
         * @see org.intalio.tempo.security.rbac.RBACQuery#ascendantRoles(java.lang.String)
         */
        public String[] ascendantRoles(String role) 
        throws RoleNotFoundException, RBACException, RemoteException {

            role = IdentifierUtils.stripRealm(role);
            try {
                TreeSet<String> result = new TreeSet<String>();
                result.add(role);
                short found;
                if (_roleAscen!=null)
                    found = _engine.queryInheritReference(_roleBase, _roleId, _roleAscen, result);
                else
                    found = _engine.queryInheritRelation(_roleBase, _roleId, _roleDescen, result);
                if (found==LDAPQueryEngine.SUBJECT_NOT_FOUND)
                    throw new RoleNotFoundException("Role, "+role+", is not found!");
                
                if (result.size()==0)
                    return EMPTY_STRINGS;
                else {
                    result.remove(role);
                    return prefix(result);
                }
            } catch (NamingException ne) {
                if (LOG.isInfoEnabled())
                    LOG.info(ne.getMessage(),ne);
                throw new RBACException(ne);
            }
        }

        /**
         * @see org.intalio.tempo.security.rbac.RBACQuery#roleOperationsOnObject(java.lang.String, java.lang.String)
         */
        public String[] roleOperationsOnObject(String role, String object) 
        throws RoleNotFoundException, ObjectNotFoundException, RBACException, RemoteException {
            
            role = IdentifierUtils.stripRealm(role);
            object = IdentifierUtils.stripRealm(object);
            try {
                // first, find all authroizedRoles
                ArrayList<String> roles = new ArrayList<String>();
                if (_roleAscen!=null)
                    _engine.queryInheritReference(_roleBase, _roleId, _roleAscen, roles);
                else
                    _engine.queryInheritRelation(_roleBase, _roleId, _roleDescen, roles);

                // then, find the permission objects
                TreeSet<String> perms = new TreeSet<String>();
                if (_permRoles!=null) {
                    // get all the permission from role
                    _engine.queryRelations(roles, _roleBase, _roleId, _permBase, _permRoles, perms);
                } else { // if (_rolePerms!=null)
                    _engine.queryFields(roles, _roleBase, _roleId, _rolePerms, perms);
                }

                // @TODO throws the ObjectNotFoundException, RoleNotFoundException properly
                TreeSet<String> result = new TreeSet<String>();
                _engine.queryFields(perms, _permBase, _permId, _permObjects, result);
                return (String[])result.toArray(new String[result.size()]);
            } catch (NamingException ne) {
                if (LOG.isInfoEnabled())
                    LOG.info(ne.getMessage(),ne);
                throw new RBACException(ne);
            }
        }

        /**
         * @see org.intalio.tempo.security.rbac.RBACQuery#userOperationsOnObject(java.lang.String, java.lang.String)
         */
        public String[] userOperationsOnObject(String user, String object) 
        throws UserNotFoundException, ObjectNotFoundException, RBACException, RemoteException {
            
            user = IdentifierUtils.stripRealm(user);
            object = IdentifierUtils.stripRealm(object);            
            try {
                // first, find all assigned roles of a user
                ArrayList<String> roles = new ArrayList<String>();
                short found;
                if (_userRoles!=null) {
                    found = _engine.queryFields(user, _userBase, _userId, _userRoles, roles);
                } else {
                    boolean checkUser = true;
                    found = _engine.queryRelations(user, _userBase, _userId, _roleBase, _roleUsers, checkUser, roles);
                }
                if (found==LDAPQueryEngine.SUBJECT_NOT_FOUND)
                    throw new RoleNotFoundException("User, "+user+", is not found!");
                if (found==LDAPQueryEngine.RELATION_NOT_FOUND)
                    return EMPTY_STRINGS;

                // then, find all authorizedRoles
                if (_roleAscen!=null)
                    _engine.queryInheritReference(_roleBase, _roleId, _roleAscen, roles);
                else
                    _engine.queryInheritRelation(_roleBase, _roleId, _roleDescen, roles);
                
                // then, find permission object
                TreeSet<String> perms = new TreeSet<String>();
                if (_permRoles!=null) {
                    // get all the permission from role
                    _engine.queryRelations(roles, _roleBase, _roleId, _permBase, _permRoles, perms);
                } else { // if (_rolePerms!=null)
                    _engine.queryFields(roles, _roleBase, _roleId, _rolePerms, perms);
                }

                // @TODO throws the ObjectNotFoundException, RoleNotFoundException properly
                TreeSet<String> result = new TreeSet<String>();
                _engine.queryFields(perms, _permBase, _permId, _permObjects, result);
                return (String[])result.toArray(new String[result.size()]);
            } catch (NamingException ne) {
                if (LOG.isInfoEnabled())
                    LOG.info(ne.getMessage(),ne);
                throw new RBACException(ne);
            }
        }

        /**
         * @see org.intalio.tempo.security.rbac.RBACQuery#userProperties(java.lang.String)
         */
        public Property[] userProperties(String user) 
        throws UserNotFoundException, RBACException, RemoteException {

            user = IdentifierUtils.stripRealm(user);
            try {
                HashMap<String,Property> result = new HashMap<String,Property>();
                _engine.queryProperties(user, _userBase, _userId, _userProp, result);
                
                return (Property[])result.values().toArray(new Property[result.size()]);
            } catch (NamingException ne) {
                if (LOG.isInfoEnabled())
                    LOG.info(ne.getMessage(),ne);
                throw new RBACException(ne);
            }
        }

        /**
         * @see org.intalio.tempo.security.rbac.RBACQuery#roleProperties(java.lang.String)
         */
        public Property[] roleProperties(String role) 
        throws RoleNotFoundException, RBACException, RemoteException {

            role = IdentifierUtils.stripRealm(role);
            try {
                HashMap<String,Property> result = new HashMap<String,Property>();
                _engine.queryProperties(role, _roleBase, _roleId, _roleProp, result);
                
                return (Property[])result.values().toArray(new Property[result.size()]);
            } catch (NamingException ne) {
                if (LOG.isInfoEnabled())
                    LOG.info(ne.getMessage(),ne);
                throw new RBACException(ne);
            }
        }
    }
}