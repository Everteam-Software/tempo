package org.intalio.tempo.security.util;

import junit.framework.TestCase;

public class IdentifierUtilsTest extends TestCase {
    private static final String separator = "\\:|/";

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        junit.textui.TestRunner.run(IdentifierUtilsTest.class);
    }

    public void testIndexOfSeparator() {
        assertEquals(IdentifierUtils.indexOfSeparator("a\\:|/b", separator), 1);

        Exception iae = null;
        try {
            IdentifierUtils.indexOfSeparator(null, separator);
        } catch (Exception e) {
            iae = e;
        }
        assertEquals(iae.getClass(), IllegalArgumentException.class);
        assertEquals(iae.getMessage(), "Argument 'id' is null");

        iae = null;
        try {
            IdentifierUtils.indexOfSeparator("a\\:|/b", null);
        } catch (Exception e) {
            iae = e;
        }
        assertEquals(iae.getClass(), IllegalArgumentException.class);
        assertEquals(iae.getMessage(), "Argument 'separators' is null");
    }

    public void testGetRealm() {
        assertEquals(IdentifierUtils.getRealm("a\\:\b"), "a");
        assertEquals(IdentifierUtils.getRealm("a\\:\b", ':'), "a\\");

        Exception iae = null;
        try {
            IdentifierUtils.getRealm(null);
        } catch (Exception e) {
            iae = e;
        }
        assertEquals(iae.getClass(), IllegalArgumentException.class);
        assertEquals(iae.getMessage(), "Argument 'id' is null");

        iae = null;
        try {
            IdentifierUtils.getRealm(null, separator);
        } catch (Exception e) {
            iae = e;
        }
        assertEquals(iae.getClass(), IllegalArgumentException.class);
        assertEquals(iae.getMessage(), "Argument 'id' is null");

        iae = null;
        try {
            IdentifierUtils.getRealm(null, ':');
        } catch (Exception e) {
            iae = e;
        }
        assertEquals(iae.getClass(), IllegalArgumentException.class);
        assertEquals(iae.getMessage(), "Argument 'id' is null");

        iae = null;
        try {
            IdentifierUtils.getRealm("id", null);
        } catch (Exception e) {
            iae = e;
        }
        assertEquals(iae.getClass(), IllegalArgumentException.class);
        assertEquals(iae.getMessage(), "Argument 'separators' is null");
    }
    
    public void testStripRealm() {
        assertEquals(IdentifierUtils.stripRealm("a\\:\b"), ":\b");
        assertEquals(IdentifierUtils.stripRealm("a\\:\b", ':'), "\b");

        Exception iae = null;
        try {
            IdentifierUtils.stripRealm(null);
        } catch (Exception e) {
            iae = e;
        }
        assertEquals(iae.getClass(), IllegalArgumentException.class);
        assertEquals(iae.getMessage(), "Argument 'id' is null");

        iae = null;
        try {
            IdentifierUtils.stripRealm(null, separator);
        } catch (Exception e) {
            iae = e;
        }
        assertEquals(iae.getClass(), IllegalArgumentException.class);
        assertEquals(iae.getMessage(), "Argument 'id' is null");

        iae = null;
        try {
            IdentifierUtils.stripRealm(null, ':');
        } catch (Exception e) {
            iae = e;
        }
        assertEquals(iae.getClass(), IllegalArgumentException.class);
        assertEquals(iae.getMessage(), "Argument 'id' is null");

        iae = null;
        try {
            IdentifierUtils.stripRealm("id", null);
        } catch (Exception e) {
            iae = e;
        }
        assertEquals(iae.getClass(), IllegalArgumentException.class);
        assertEquals(iae.getMessage(), "Argument 'separators' is null");
    }
    
    public void testNormalize(){
        Exception iae = null;
        try{
            IdentifierUtils.normalize(null, null, false, ':');
        }catch(Exception e){
            iae = e;
        }
        assertEquals(iae.getClass(), IllegalArgumentException.class);
        assertEquals(iae.getMessage(), "Argument 'id' is null");

        
        String lower = IdentifierUtils.normalize("A\\:|/B", "\\:|/", false, ':');
        String normal = IdentifierUtils.normalize("a\\:|/b", "\\:|/", true, ':');
        assertEquals(lower, normal);
    }

}
