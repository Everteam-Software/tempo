/**
 * Copyright (c) 2005-2008 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 *
 */

package org.intalio.tempo.workflow.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.intalio.tempo.workflow.auth.BaseRestrictedEntity;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.task.attachments.Attachment;
import org.intalio.tempo.workflow.task.traits.ITaskWithAttachments;
import org.intalio.tempo.workflow.task.traits.ITaskWithInput;
import org.intalio.tempo.workflow.task.traits.ITaskWithOutput;
import org.intalio.tempo.workflow.task.xml.XmlTooling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * Methods to check for equality on Tempo tasks
 * 
 * @author Niko
 * 
 */
public final class TaskEquality {

    static final Logger _log = LoggerFactory.getLogger(TaskEquality.class);

    /**
     * Those fields whose name is in the list are completely ignored
     */
    static List<String> IGNORED_FIELDS = Arrays.asList(new String[] { "pcsubclass", "pcStateManager",
            "pcDetachedState", "backingSet", "_input", "_output" });
    /**
     * Those class whose name starts with an item of this list, are ignored when
     * checking on fields
     */
    static List<String> IGNORED_CLASS = Arrays.asList(new String[] { "org.apache.openjpa", "java.util.Date" });

    /**
     * JPA Transient fields won't be loaded after a load from database, so field
     * can be skipped PersistentMap is too complex to load on the fly even with
     * eager caching. Filtering
     */
    static List<String> IGNORED_ANNOTATIONS = Arrays.asList(new String[] { "javax.persistence.Transient",
            "org.apache.openjpa.persistence.PersistentMap" });

    /**
     * Generic method to check that two objects are equals.
     * <ul>
     * <li>it checks for nullity of objects first</li>
     * <li>check the classname</li>
     * <li>check the value if it is a simple objects</li>
     * <li>recursive check of the field if complicated object</li>
     * </ul>
     */
    static public boolean isEqual(Object a1, Object a2) {
        if (filterNull(a1, a2))
            return true;
        Class<?> klass = filterClass(a1, a2);
        if(null == klass){
            throw new NotEqualException("Classes are not the same");
        }
        String className = klass.getName();
        if (!className.startsWith("org.intalio.tempo")) {
            if (!(a1.equals(a2)))
                throw new NotEqualException("Values are not equal for:" + className + " and object\n" + a1.toString()
                        + "\n" + a2.toString());
            else
                return true;
        } else {
            while (klass != null) {
                // class are not equals
                if (klass == null)
                    new NotEqualException("Classes are not the same");
                areFieldsEqual(a1, a2, klass);
                klass = klass.getSuperclass();
            }
            return true;
        }
    }

    /**
     * This method uses isEqual as a base for comparing tasks We also want to do
     * some finer tuning for testing thoroughly some fields:
     * <ul>
     * <li>inputs</li>
     * <li>outputs
     * <li>
     * <li>authorized actions</li>
     * <li>owners
     * <li>
     * </ul>
     * 
     */
    static public boolean areTasksEquals(Task t1, Task t2) {
//        boolean b = isEqual(t1, t2);
        boolean b = true;
        b &= t1.getCreationDate().equals(t2.getCreationDate());
        b &= t1.getID().equals(t2.getID());
        b &= t1.getDescription().equals(t2.getDescription());
        b &= t1.getClass().equals(t2.getClass());
        // we skip the ones below because they are too complex to check when out
        // of jpa
        // doing them one by one
        b &= areAuthorizedActionEqual(t1, t2);
        b &= areOwnersEqual(t1, t2);
        // we know they share the same class if we are here since isEqual checks
        // for class identity
        if (t1 instanceof ITaskWithInput)
            b &= areInputsEquals((ITaskWithInput) t1, (ITaskWithInput) t2);
        if (t1 instanceof ITaskWithOutput)
            b &= areOutputEquals((ITaskWithOutput) t1, (ITaskWithOutput) t2);
        return b;
    }

    /**
     * Is the opposite of <code>isEqual</code>. Convenience method.
     */
    static public boolean isNotEqual(Object a1, Object a2) {
        try {
            isEqual(a1, a2);
            throw new NotEqualException("Was checking for not equal but was equal");
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * check the two objects are null, or the two are not null. Not equal if one
     * null and not the other.
     * 
     * @return true if both are null, false if both are not null
     */
    private static boolean filterNull(Object a1, Object a2) {
        if (a1 == null && a2 == null)
            // both are null
            return true;
        if (a1 == null && a2 != null)
            throw new NotEqualException("First object is null but not:" + a2.toString());
        if (a1 != null && a2 == null)
            throw new NotEqualException("Second object is null but not:" + a1.toString());
        // both are not null
        return false;
    }

    /**
     * Get the common class of two objects
     */
    private static Class<?> filterClass(Object c1, Object c2) {
        Class<? extends Object> class1 = c1.getClass();
        Class<? extends Object> class2 = c2.getClass();
        if (filterNull(class1, class2))
            throw new RuntimeException("class was null for:" + c2);
        _log.debug("Checking class:" + class2.toString());
        return class1.equals(class2) ? class1 : null;
    }

    /**
     * Check two tasks have the same authorized actions This is a complicated
     * field, filtered by jpa and need to be checked separately
     */
    static public boolean areAuthorizedActionEqual(Task t1, Task t2) {
        Set<String> actions1 = t1.getAuthorizedActions();
        Set<String> actions2 = t2.getAuthorizedActions();
        if (!actions1.equals(actions2)) {
            throw new NotEqualException("Action sets are not equal");
        }
        for (String act : actions1) {
            areAuthIdSetEquals(t1.getAuthorizedRoles(act), t2.getAuthorizedRoles(act));
            areAuthIdSetEquals(t1.getAuthorizedUsers(act), t2.getAuthorizedUsers(act));
        }
        return true;
    }

    /**
     * Check two tasks have the same user owners and the same role owners
     */
    static public boolean areOwnersEqual(BaseRestrictedEntity b1, BaseRestrictedEntity b2) {
        boolean bool1 = areAuthIdSetEquals(b1.getRoleOwners(), b2.getRoleOwners());
        if (!bool1)
            throw new NotEqualException("Role owners are not equals");
        boolean bool2 = areAuthIdSetEquals(b1.getUserOwners(), b2.getUserOwners());
        if (!bool2)
            throw new NotEqualException("User owners are not equals");
        return true;
    }

    /**
     * Convenience method to check that two sets are equals will not throw an
     * exception if they are not.
     */
    static public boolean areAuthIdSetEquals(Collection set1, Collection set2) {
        return set1.equals(set2);
    }

    /**
     * Check two tasks have the same number of attachments, and that each
     * attachment has its sibling
     */
    static public boolean areAttachmentsEqual(ITaskWithAttachments task1, ITaskWithAttachments task2) {
        if (filterNull(task1, task2))
            return true;

        Attachment[] attachments1 = getSortedAttachments(task1);
        Attachment[] attachments2 = getSortedAttachments(task2);

        if (attachments1.length != attachments2.length)
            throw new NotEqualException("Number of attachments is different");
        for (int i = 0; i < attachments1.length; i++) {
            isEqual(attachments1[i], attachments2[i]);
        }
        return true;
    }

    /**
     * Check whether the inputs are equals or not. We usually need this methods
     * since we want to check at the xml level
     */
    static public boolean areInputsEquals(ITaskWithInput task1, ITaskWithInput task2) {
        if (filterNull(task1, task2))
            return true;
        return areDocumentsEqual(task1.getInput(), task2.getInput());
    }

    /**
     * Same as above, we want to check at the xml level if the content is the
     * same
     */
    static public boolean areOutputEquals(ITaskWithOutput task1, ITaskWithOutput task2) {
        if (filterNull(task1, task2))
            return true;
        return areDocumentsEqual(task1.getOutput(), task2.getOutput());
    }

    /**
     * Supprt method for testing equality between xml documents Can also be used
     * standalone
     */
    static public boolean areDocumentsEqual(Document doc1, Document doc2) {
        if (filterNull(doc1, doc2))
            return true;
        return XmlTooling.equals(doc1, doc2);
    }

    /**
     * convenience method to sort the attachments of a task
     */
    private static Attachment[] getSortedAttachments(ITaskWithAttachments task1) {
        Collection<Attachment> attachments1 = task1.getAttachments();
        Attachment[] atts = attachments1.toArray(new Attachment[attachments1.size()]);
        Arrays.sort(atts);
        return atts;
    }

    /**
     * Check all fields are equals for the two objects, where the class is the
     * one given as a parameter We already know the class is common for the two
     * objects, and they are both not null
     */
    static private boolean areFieldsEqual(Object o1, Object o2, Class<?> c) {
        Field[] fields = c.getDeclaredFields();
        try {
            for (Field f : fields) {
                f.setAccessible(true);
                filterDateField(o1, o2, f);
                if (filterIgnoreField(f))
                    continue;
                _log.debug("Checking Field:" + f.getName() + " for class:" + c.getName());
                final Object a1 = f.get(o1);
                final Object a2 = f.get(o2);
                isEqual(a1, a2);
            }
            return true;
        } catch (IllegalAccessException e) {
            throw new NotEqualException(e.getMessage());
        }
    }

    private static void filterDateField(Object o1, Object o2, Field f) {
        String n = f.getType().getName();
        if (n.startsWith("java.util.Date") || n.startsWith("org.apache.openjpa.util.java$util$Date$proxy")) {
            try {
                final Object object = f.get(o1);
                final Object object2 = f.get(o2);
                boolean eq = filterNull(object, object2) || object.toString().equals(object2.toString());
                if (!eq)  throw new NotEqualException("Dates are not equal");
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static boolean isProxyDate(Object o1, Field f) {
        try {
            _log.debug(o1.toString());
            return o1.getClass().getField(f.getName()).getClass().getName().startsWith(
                    "org.apache.openjpa.util.java$util$Date$proxy");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Could not check for proxy date");
        }

    }

    /**
     * Convenience method to filter the fields that needs no testing We ignore
     * the following:
     * <ul>
     * <li>static fields</li>
     * <li>final fields</li>
     * <li>ignored classes from the static list</li>
     * <li>ignored fields from the static list</li>
     * <li>fields marked with transient annotation</li>
     * </ul>
     */
    static private boolean filterIgnoreField(Field f) {
        int m = f.getModifiers();
        if (Modifier.isStatic(m))
            return true;
        if (Modifier.isFinal(m))
            return true;
        if (filterIgnore(f.getType().getName(), IGNORED_CLASS))
            return true;
        String fieldName = f.getName();
        if (filterIgnore(fieldName, IGNORED_FIELDS))
            return true;
        for (String annotation : IGNORED_ANNOTATIONS)
            if (filterIgnoredAnnotations(f, annotation))
                return true;
        return false;
    }

    /**
     * Filter on the presence of some annotations
     */
    private static boolean filterIgnoredAnnotations(Field f, String annotationClass) {
        try {
            Class jpaTransient = Class.forName(annotationClass);
            if (f.getAnnotation(jpaTransient) != null)
                return true; // ignore JPA transient fields
            else
                return false;
        } catch (Exception e) {
            // cannot load JPA classes
            // filter that field
            return true;
        }
    }

    /**
     * Convenience method that check whether the given name starts with an
     * element of the given list
     */
    static private boolean filterIgnore(String name, List<String> ignored) {
        for (String ig : ignored)
            if (name.startsWith(ig))
                return true;
        return false;
    }
}