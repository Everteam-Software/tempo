/**
 * Copyright (C) 2003-2004, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with the
 * written permission of Intalio Inc. or in accordance with the terms
 * and conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *
 * $Id: LDAPQueryEngine.java,v 1.8 2004/11/20 02:15:26 boisvert Exp $
 */
package org.intalio.tempo.security.ldap;

import static org.intalio.tempo.security.ldap.LDAPSecurityProvider.close;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.util.StringArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LDAPQueryEngine
 *
 * @author <a href="mailto:yip@intalio.com>Thomas Yip</a>
 * @author <a href="http://www.intalio.com">&copy; Intalio Inc.</a>
 */
public class LDAPQueryEngine {

    protected final static Logger LOG = LoggerFactory.getLogger("tempo.security");

    private final static Inverter BASE_STRIPER  = new BaseStriper();

    private final static Inverter ID_STRIPER    = new IDStriper();

    private final static Object[] EMPTY_ARGS    = new Object[0];

    public final static short RESULT_FOUND          = 1;

    public final static short SUBJECT_NOT_FOUND     = 2;

    public final static short FIELD_NOT_FOUND       = 3;

    public final static short RELATION_NOT_FOUND    = 4;

    private final LDAPSecurityProvider  _provider;

    private final String                _baseDN;

    /**
     * Constructor
     */
    public LDAPQueryEngine( LDAPSecurityProvider provider, String baseDN ) {
        _provider = provider;
        _baseDN  = baseDN;
    }

    public short queryRelations(String subject, String sbjBase, String id, String relBase, String rel,
    boolean checkSubject, Collection<String> result)
    throws NamingException {

        if (LOG.isDebugEnabled())
            LOG.debug("query name: "+subject);
        SearchControls sc = new SearchControls();
        if (checkSubject){
            String search = id+"={0}";
            if (!isExist(sbjBase, search, new Object[] {subject}, sc))
                return SUBJECT_NOT_FOUND;
        }
        String search = rel+"={0}";
        Object[] args = new Object[] { (id+"="+subject+','+sbjBase+','+_baseDN) };
        return findNames(relBase, search, args, sc, result);
    }

    /**
     * Obtain the extent of object of the same base. (An extent is the
     * complete set)
     *
     * @param sjbBase
     * @param id
     * @param result
     * @throws NamingException
     */
    public void queryExtent(String sjbBase, String id, Collection<String> result)
    throws NamingException {

        findAllNames(sjbBase, result);
    }

    public boolean queryExist(String subject, String cond, String base, String id, String field, Map map)
    throws NamingException {
        SearchControls sc = new SearchControls();
        for (Iterator iter = map.entrySet().iterator(); iter.hasNext();) {
            Map.Entry element = (Map.Entry)iter.next();
            if (element.getValue().equals(field)) {
                field = (String)element.getKey();
                break;
            }
        }
        String search = "(& "+"("+id+"={0}) ("+field+"={1}) )";
        Object[] args = new Object[] { subject, cond };
        return isExist(base, search, args, sc);
    }
    public short queryFields(String subject, String base, String id, String field, Collection<String> result )
    throws NamingException {
        return queryFilteredFields(subject, base, id, field, null, result);
    }

    public short queryFilteredFields(String subject, String base, String id, String field,
    String fieldType, Collection<String> result )
    throws NamingException {

        if (LOG.isDebugEnabled())
            LOG.debug("query field: "+subject);
        SearchControls sc = new SearchControls();
        String search = id+"="+subject;
        String filter = fieldType;
        return findFilteredFields(base, search, field, filter, sc, result);
    }

    public short queryProperties(String subject, String base, String id,
    Map<String,String> map, Map<String,Property> result )
    throws NamingException {

        SearchControls sc = new SearchControls();
        String search = id+"="+subject;
        return findProperties(base, search, map, sc, result);
    }

    public short queryNoRelation(String base, String field, Collection<String> result )
    throws NamingException {

        SearchControls sc = new SearchControls();
        String search = "(! ("+field+"=*))";
        return findNames(base, search, EMPTY_ARGS, sc, result);
    }

    public short queryExistRelation(String base, String id, String field, String relBase, Collection<String> result )
    throws NamingException {

        SearchControls sc = new SearchControls();
        String search = id+"=*";
        String filter = relBase;
        return findFilteredFields(base, search, field, filter, sc, result);
    }

    public void queryRelations(Collection subjects, String sbjBase, String id,
    String relBase, String rel, Collection<String> result)
    throws NamingException {

        SearchControls sc = new SearchControls();
        for (Iterator itor = subjects.iterator(); itor.hasNext(); ) {
            String subject = itor.next().toString();
            String search  = rel+"={0}";
            Object[] args  = new Object[] { id+"="+subject+','+sbjBase+','+_baseDN };
            short found = findNames(relBase, search, args, sc, result);
            if (found==SUBJECT_NOT_FOUND)
                LOG.warn("Role hierachy tree is incomplete. Reference role, "+subject+", is not found!");
            if (found==RELATION_NOT_FOUND)
                continue;
        }
    }

    public void queryFields(Collection subjects, String base, String id, String field, Collection<String> result)
    throws NamingException {
        queryFilteredFields(subjects, base, id, field, null, result);
    }

    public void queryFilteredFields(Collection subjects, String base, String id,
    String field, String fieldType, Collection<String> result)
    throws NamingException {

        SearchControls sc = new SearchControls();
        for (Iterator itor = subjects.iterator(); itor.hasNext(); ) {
            String subject = itor.next().toString();
            String search  = id+"="+subject;
            String filter  = fieldType;
            short found = findFilteredFields(base, search, field, filter, sc, result);
            if (found==SUBJECT_NOT_FOUND)
                LOG.warn("Role hierachy tree is incomplete. Reference role, "+subject+", is not found!");
            if (found==RELATION_NOT_FOUND)
                continue;
        }
    }

    public short queryInheritReference(String base, String id, String field, Collection<String> result)
    throws NamingException {

        SearchControls sc = new SearchControls();
        ArrayList<String> todo = new ArrayList<String>(result);
        TreeSet<String> newItems = new TreeSet<String>();
        boolean firstLoop = true;

        //ArrayList
        do {
            String subject = todo.get(0).toString();
            // loop item from the search list (ie. roles),
            // one by one, and find all ascendants
            todo.remove(subject);
            newItems.clear();

            String search = id+"="+subject;
            short found = findFilteredFields(base, search, field, base, sc, newItems);
            if (firstLoop) {
                firstLoop = false;
                if (found!=RESULT_FOUND)
                    return found;
            } else {
                firstLoop = false;
                if (found==SUBJECT_NOT_FOUND)
                    LOG.warn("Role hierachy tree is incomplete. Reference role, "+subject+", is not found!");
                if (found==RELATION_NOT_FOUND)
                    continue;
            }

            while ( newItems.size()!=0) {
                String item = newItems.first().toString();
                newItems.remove(item);
                if (!result.contains(item)) {
                    todo.add(item);
                    result.add(item);
                }
            }
        } while (todo.size()!=0);
        return RESULT_FOUND;
    }


    public short queryInheritRelation(String base, String id, String field, Collection<String> result)
    throws NamingException {

        SearchControls sc = new SearchControls();
        ArrayList<Object> todo = new ArrayList<Object>(result);
        TreeSet<String> newItems = new TreeSet<String>();
        boolean added = false;

        String cond = id+"="+todo.get(0).toString();
        if (!isExist(base, cond, EMPTY_ARGS, sc))
            return SUBJECT_NOT_FOUND;

        //ArrayList
        do {
            String subject = todo.get(0).toString();
            // loop item from the search list (ie. roles),
            // one by one, and find all ascendants
            todo.remove(subject);
            newItems.clear();

            String search = field+"={0}";
            Object[] args = new Object[] {id+"="+subject+','+base+','+_baseDN};
            short found = findNames(base, search, args, sc, newItems);
            if (found==RELATION_NOT_FOUND)
                continue;
            added = true;

            while ( newItems.size()!=0) {
                String item = newItems.first().toString();
                newItems.remove(item);
                if (!result.contains(item)) {
                    todo.add(item);
                    result.add(item);
                }
            }
        } while (todo.size()!=0);
        if (added)
            return RESULT_FOUND;
        else
            return RELATION_NOT_FOUND;
    }

    //======================================================
    //                 protected methods
    //======================================================

    protected boolean isExist(String base, String condition, Object[] args, SearchControls sc)
    throws NamingException {

        DirContext context = _provider.getContext(_baseDN);
        try {
            NamingEnumeration sbjResult = context.search(base(base), condition, args, sc);
            if (sbjResult.hasMore()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("is exist, condition met: "+condition);
                    LOG.debug("args: "+(args!=null&&args.length>=1?args[0]:null)+" "+(args!=null&&args.length>=2?args[1]:""));
                }
                return true;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("not exist, condition not met: "+condition+" for "+base);
                LOG.debug("args: "+(args!=null&&args.length>=1?args[0]:null)+" "+(args!=null&&args.length>=2?args[1]:""));
            }
            return false;
        } catch (NamingException ne) {
            throw ne;
        } finally {
            close(context);
        }
    }

    protected short findNames(String base, String condition, Object[] args, SearchControls sc, Collection<String> result)
    throws NamingException {

        if (LOG.isDebugEnabled())
            LOG.debug("find names: "+condition+" args "+(args.length>0?args[0]:null));

        DirContext context = _provider.getContext(_baseDN);
        try {
            NamingEnumeration relResult = context.search(base(base), condition, args, sc);
            if (!relResult.hasMore()) {
                if (LOG.isDebugEnabled())
                    LOG.debug("Nothing found!");
                return RELATION_NOT_FOUND;
            }
            do {
                SearchResult sr = (SearchResult)relResult.next();
                result.add(ID_STRIPER.invert(sr.getName()));
                if (LOG.isDebugEnabled())
                    LOG.debug("Found name:"+ID_STRIPER.invert(sr.getName()));
            } while (relResult.hasMore());
            return RESULT_FOUND;
        } catch (NamingException ne) {
            throw ne;
        } finally {
            close(context);
        }
    }

    protected short findAllNames(String base, Collection<String> result)
    throws NamingException {

        if (LOG.isDebugEnabled())
            LOG.debug("find all names: "+base);

        DirContext context = _provider.getContext(_baseDN);
        try {
            NamingEnumeration relResult = context.list(base(base));
            if (!relResult.hasMore()) {
                if (LOG.isDebugEnabled())
                    LOG.debug("Nothing found!");
                return RELATION_NOT_FOUND;
            }
            do {
                NameClassPair ncp = (NameClassPair)relResult.next();
                result.add(ID_STRIPER.invert(ncp.getName()));
                if (LOG.isDebugEnabled())
                    LOG.debug("Found name:"+ID_STRIPER.invert(ncp.getName()));
            } while (relResult.hasMore());
            return RESULT_FOUND;
        } catch (NamingException ne) {
            throw ne;
        } finally {
            close(context);
        }
    }

    protected short findFields(String base, String condition, String field, SearchControls sc, Collection<String> result)
    throws NamingException {
        return findFilteredFields(base, condition, field, null, sc, result);
    }

    protected short findFilteredFields(String base, String condition, String field, String filter, SearchControls sc, Collection<String> result)
    throws NamingException {

        if (LOG.isDebugEnabled())
            LOG.debug("find fields: base="+base+" condition="+condition+" field "+field+" filter="+filter);

        DirContext context = _provider.getContext(_baseDN);
        try {
            String[] oldReturning = sc.getReturningAttributes();
            sc.setReturningAttributes( new String[] { field } );
            NamingEnumeration subResult = context.search(base(base), condition, sc);
            if (!subResult.hasMore()) {
                if (LOG.isDebugEnabled())
                    LOG.debug("subject not found!");
                sc.setReturningAttributes(oldReturning);
                return SUBJECT_NOT_FOUND;
            }
            if (LOG.isDebugEnabled())
                LOG.debug("subject found!");
            boolean found = false;
            do {
                SearchResult sr = (SearchResult)subResult.next();
                if (sr == null) continue;
                if (sr.getAttributes() == null) continue;
                if (field == null) continue;
                Attribute fields = sr.getAttributes().get(field);
                if (fields==null) {
                    if (LOG.isDebugEnabled())
                        LOG.debug("no such field. "+field+" in object "+sr.getName()+" result "+sr.toString());
                    continue;
                }
                int size = fields.size();
                for ( int i=0; i<size; i++) {
                    String res = fields.get(i).toString();
                    if (filter==null || isPass(filter, res)) {
                        found = true;
                        result.add(BASE_STRIPER.invert(res));
                    }
                }
            } while (subResult.hasMore());
            sc.setReturningAttributes(oldReturning);
            if ( found )
                return RESULT_FOUND;
            else
                return FIELD_NOT_FOUND;
        } catch (NamingException ne) {
            throw ne;
        } finally {
            close(context);
        }
    }

    private boolean isPass( String filter, String field ) {
        boolean result = (field.indexOf(filter)!=-1);
        if (LOG.isDebugEnabled()) {
            LOG.debug("is pass? "+ result +" filter: "+filter+" field: "+field);
        }
        return result;
    }

    protected short findProperties(String base, String condition, Map<String,String> map, SearchControls sc, Map<String,Property> result)
    throws NamingException {

        if (LOG.isDebugEnabled())
            LOG.debug("find propeties, "+condition+" type: "+base);
        if (map!=null) {
            String[] atts = (String[])map.values().toArray(new String[map.size()]);
            sc.setReturningAttributes(atts);
        }

        DirContext context = _provider.getContext(_baseDN);
        try {
            NamingEnumeration subResult = context.search(base(base), condition, sc);
            if (!subResult.hasMore()) {
                if (LOG.isDebugEnabled())
                    LOG.debug("object not found. "+condition);
                return SUBJECT_NOT_FOUND;
            }
            if (LOG.isDebugEnabled())
                LOG.debug("object found! "+condition);

            boolean found = false;
            do {
                SearchResult sr = (SearchResult)subResult.next();
                Iterator entries = map.entrySet().iterator();
                while (entries.hasNext()) {
                    Map.Entry entry = (Map.Entry)entries.next();
                    Attribute att = sr.getAttributes().get((String)entry.getValue());
                    if (att==null)
                        continue;
                    for (int i=0; i<att.size(); i++) {
                        found = true;
                        String name = (String) entry.getKey();
                        String value = att.get(i).toString();
                        if (LOG.isDebugEnabled())
                            LOG.debug("found attribute name: "+name+" value: "+value);

                        Property existing = result.get(name);
                        if (existing != null) {
                            existing.setValue( StringArrayUtils.addCommaDelimited((String)existing.getValue(), value) );
                        } else {
                            result.put(name, new Property(name, value));
                        }
                    }
                }
            } while (subResult.hasMore());
            if (found) {
                return RESULT_FOUND;
            } else {
                if (LOG.isDebugEnabled())
                    LOG.debug("no proporty found in the object(s). "+condition);
                return FIELD_NOT_FOUND;
            }
        } catch (NamingException ne) {
            throw ne;
        } finally {
            close(context);
        }
    }

    private String base( String base ) {
        return base;
    }

    interface Inverter {
        public String invert( String st );
    }

    static class BaseStriper implements Inverter {
        public String invert( String s ) {
            int comma = s.indexOf(',');
            int eq = s.indexOf('=');
            if ( eq >= comma )
                throw new IllegalArgumentException("Invalid format "+s);
            if ( eq==-1 || comma==-1 )
                throw new IllegalArgumentException("Invalid format "+s);
            return s.substring(eq+1,comma);
        }
    }

    static class IDStriper implements Inverter {
        public String invert( String s ) {
            int eq = s.indexOf('=');
            if ( eq==-1 )
                throw new IllegalArgumentException("Invalid format "+s);
            return s.substring(eq+1);
        }
    }
}

