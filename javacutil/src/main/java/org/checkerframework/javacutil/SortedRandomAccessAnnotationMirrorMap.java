package org.checkerframework.javacutil;

import java.util.*;
import javax.lang.model.element.AnnotationMirror;

public class SortedRandomAccessAnnotationMirrorMap<V> implements Map<AnnotationMirror, V>, RandomAccess {

    @SuppressWarnings("serial")
     static final class SortedArraySet<T> extends ArrayList<T> implements Set<T> {

    }

    private final Comparator<AnnotationMirror> comparator;
    private SortedArraySet<AnnotationMirror> keys;
    private ArrayList<V> values;

    public SortedRandomAccessAnnotationMirrorMap() {
        comparator = AnnotationUtils.annotationOrdering();
        keys = new SortedArraySet<>();
        values = new ArrayList<>();
    }

    @Override
    public int size() {
        return keys.size();
    }

    @Override
    public boolean isEmpty() {
        return keys.isEmpty();
    }

    @Override
    public boolean containsKey(Object o) {
        if (!(o instanceof AnnotationMirror)) {
            return false;
        }

        return Collections.binarySearch(keys, (AnnotationMirror) o, comparator) >= 0;
    }

    @Override
    public boolean containsValue(Object o) {
        for (V value : values) {
            if (value.equals(o)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public V get(Object o) {
        if (!(o instanceof AnnotationMirror)) {
            return null;
        }

        int index = Collections.binarySearch(keys, (AnnotationMirror) o, comparator);
        if (index >= 0) {
            return values.get(index);
        }
        return null;
    }

    @Override
    public V put(AnnotationMirror annotationMirror, V v) {
        int index = Collections.binarySearch(keys, annotationMirror, comparator);
        if (index >= 0) {
            V value = values.get(index);
            values.set(index, v);
            return value;
        }

        // index = -(insertion point) - 1
        int insertionPoint = -index - 1;
        keys.add(insertionPoint, annotationMirror);
        values.add(insertionPoint, v);

        return null;
    }

    @Override
    public V remove(Object o) {
        if (!(o instanceof AnnotationMirror)) {
            return null;
        }

        int index = Collections.binarySearch(keys, (AnnotationMirror) o, comparator);
        if (index >= 0) {
            V value = values.get(index);
            keys.remove(index);
            values.remove(index);
            return value;
        }

        return null;
    }

    @Override
    public void putAll(Map<? extends AnnotationMirror, ? extends V> map) {
        for (AnnotationMirror annotationMirror : map.keySet()) {
            put(annotationMirror, map.get(annotationMirror));
        }
    }

    @Override
    public void clear() {
        keys.clear();
        values.clear();
    }

    @Override
    public Set<AnnotationMirror> keySet() {
        return keys;
    }

    @Override
    public Collection<V> values() {
        return values;
    }

    @Override
    public Set<Entry<AnnotationMirror, V>> entrySet() {
        assert false;
        return null;
    }
}
