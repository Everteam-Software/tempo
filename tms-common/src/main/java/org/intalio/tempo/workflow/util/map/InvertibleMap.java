package org.intalio.tempo.workflow.util.map;

import java.util.HashMap;

public class InvertibleMap<K, V> extends HashMap<K, V> {
    
    private static final long serialVersionUID = -1042826909290047059L;
    
    private HashMap<V, K> inverse = new HashMap<V, K>();

    public V put(K key, V val) {
        // Put into the forward map, which is super.
        V oldVal = super.put(key, val);
        inverse.put(val, key);
        return oldVal;
    }

    @Override
    public V remove(Object key) {
        throw new RuntimeException("Not implemented remove");
    }

    public K getInverse(V val) {
        return inverse.get(val);
    }
}