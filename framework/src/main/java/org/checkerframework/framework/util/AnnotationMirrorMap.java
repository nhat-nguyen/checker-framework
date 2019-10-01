package org.checkerframework.framework.util;

import java.util.*;
import javax.lang.model.element.AnnotationMirror;
import org.checkerframework.javacutil.AnnotationUtils;

/**
 * The Map interface defines some of its methods with respect to the equals method. This
 * implementation of Map violates those specifications, but fulfills the same property using {@link
 * AnnotationUtils#areSame}.
 *
 * <p>For example, the specification for the containsKey(Object key) method says: "returns true if
 * and only if this map contains a mapping for a key k such that (key == null ? k == null :
 * key.equals(k))." The specification for {@link AnnotationMirrorMap#containsKey} is "returns true
 * if and only if this map contains a mapping for a key k such that (key == null ? k == null :
 * AnnotationUtils.areSame(key, k))."
 *
 * <p>AnnotationMirror is an interface and not all implementing classes provide a correct equals
 * method; therefore, existing implementations of Map cannot be used.
 */
public class AnnotationMirrorMap<V> implements Map<AnnotationMirror, V> {

    /** The actual map to which all work is delegated. */
    private final Map<AnnotationMirror, V> shadowMap;

    /** Default constructor. */
    public AnnotationMirrorMap() {
        this.shadowMap = new HashMap<>();
    }

    /**
     * Creates an annotation mirror map and adds all the mappings in {@code copy}.
     *
     * @param copy a map whose contents should be copied to the newly created map
     */
    public AnnotationMirrorMap(Map<AnnotationMirror, ? extends V> copy) {
        this();
        this.putAll(copy);
    }

    @Override
    public int size() {
        return shadowMap.size();
    }

    @Override
    public boolean isEmpty() {
        return shadowMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return shadowMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return shadowMap.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return shadowMap.get(key);
    }

    @Override
    public V put(AnnotationMirror key, V value) {
        return shadowMap.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return shadowMap.remove(key);
    }

    @Override
    public void putAll(Map<? extends AnnotationMirror, ? extends V> m) {
        shadowMap.putAll(m);
    }

    @Override
    public void clear() {
        shadowMap.clear();
    }

    @Override
    public Set<AnnotationMirror> keySet() {
        return new AnnotationMirrorSet(shadowMap.keySet());
    }

    @Override
    public Collection<V> values() {
        return shadowMap.values();
    }

    @Override
    public Set<Entry<AnnotationMirror, V>> entrySet() {
        return shadowMap.entrySet();
    }
}
