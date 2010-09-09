package org.intalio.tempo.workflow.util.map;

import org.intalio.tempo.workflow.util.*;
import junit.framework.TestCase;

public class InvertibleMapTest extends TestCase {
    public static void main(String[] args) {
        junit.textui.TestRunner.run(InvertibleMapTest.class);
    }

    public void testRemove() throws Exception {
        InvertibleMap<Object, Object> map = new InvertibleMap<Object, Object>();
        Object key = "key";
        Exception re = null;
        try {
            map.remove(key);
        } catch (Exception e) {
            re = e;
        }
        assertEquals(re.getClass(), RuntimeException.class);
    }

}
