package org.intalio.tempo.security.util;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.intalio.tempo.security.Property;

public class PropertyUtilsTest extends TestCase {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        junit.textui.TestRunner.run(PropertyUtilsTest.class);
    }

    public void testGetProperty() {
        Property[] properties = new Property[] { new Property("name", "object") };
        Property[] nullProperties = null;
        String name = "name";
        String nullName = null;

        Exception iae = null;
        try {
            PropertyUtils.getProperty(nullProperties, name);
        } catch (Exception e) {
            iae = e;
        }
        assertEquals(iae.getClass(), IllegalArgumentException.class);
        assertEquals(iae.getMessage(), "Argument 'properties' is null");

        iae = null;
        try {
            PropertyUtils.getProperty(properties, nullName);
        } catch (Exception e) {
            iae = e;
        }
        assertEquals(iae.getClass(), IllegalArgumentException.class);
        assertEquals(iae.getMessage(), "Argument 'name' is null");

        assertEquals(PropertyUtils.getProperty(properties, name), new Property("name", "object"));
        assertNull(PropertyUtils.getProperty(properties, "anything"));
    }

    public void testToMap() {
        Property[] properties = new Property[] { new Property("name", "object") };
        Property[] nullProperties = null;

        Exception iae = null;
        try {
            PropertyUtils.toMap(nullProperties);
        } catch (Exception e) {
            iae = e;
        }
        assertEquals(iae.getClass(), IllegalArgumentException.class);
        assertEquals(iae.getMessage(), "Argument 'properties' is null");

        Map<String, Object> map = PropertyUtils.toMap(properties);
        assertEquals(map.get("name"), new Property("name", "object"));
        assertEquals(map.size(), 1);
    }

    public void testGetPropertyMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", new Property("name", "object"));
        Map<String, Object> nullMap = null;
        String nullName = null;

        Exception iae = null;
        try {
            PropertyUtils.getProperty(nullMap, "name");
        } catch (Exception e) {
            iae = e;
        }
        assertEquals(iae.getClass(), IllegalArgumentException.class);
        assertEquals(iae.getMessage(), "Argument 'properties' is null");
        
        iae = null;
        try {
            PropertyUtils.getProperty(map, nullName);
        } catch (Exception e) {
            iae = e;
        }
        assertEquals(iae.getClass(), IllegalArgumentException.class);
        assertEquals(iae.getMessage(), "Argument 'name' is null");
        
        assertEquals(PropertyUtils.getProperty(map, "name"),new Property("name","object"));

    }

}
