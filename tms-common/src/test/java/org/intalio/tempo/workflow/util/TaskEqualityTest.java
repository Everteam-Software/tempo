package org.intalio.tempo.workflow.util;

import junit.framework.TestCase;

public class TaskEqualityTest extends TestCase {
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TaskEqualityTest.class);
    }

    public void testIsNotEqual_NotSameClass() throws Exception {
        Object a1 = new Integer(1);
        Object a2 = new String("a string");
        boolean result = TaskEquality.isNotEqual(a1, a2);
        assertEquals(result, true);
    }
    
    public void testIsNotEqual_SameObject() throws Exception {
        Object a1 = new String("a string");
        Object a2 = new String("a string");
        boolean result = TaskEquality.isNotEqual(a1, a2);
        assertEquals(result, true);
    }

    public void testIsEqual_NotEqualObject() throws Exception {
        Object a1 = new String("a string");
        Object a2 = new String("another string");
        try {
            boolean result = TaskEquality.isEqual(a1, a2);
        } catch (Exception nee) {
            assertEquals(nee.getClass(), NotEqualException.class);
        }
    }
    
    public void testIsEqual_NotSameClass() throws Exception {
        Object a1 = new String("a string");
        Object a2 = new Integer(998);
        try {
            boolean result = TaskEquality.isEqual(a1, a2);
        } catch (Exception nee) {
            assertEquals(nee.getClass(), NotEqualException.class);
            assertEquals(nee.getMessage(), "Classes are not the same");
        }
    }
}
