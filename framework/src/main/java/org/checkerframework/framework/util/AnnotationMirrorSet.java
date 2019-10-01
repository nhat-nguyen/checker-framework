package org.checkerframework.framework.util;

import java.util.*;
import javax.lang.model.element.AnnotationMirror;
import org.checkerframework.javacutil.AnnotationUtils;

/**
 * The Set interface defines many methods with respect to the equals method. This implementation of
 * Set violates those specifications, but fulfills the same property using {@link
 * AnnotationUtils#areSame} rather than equals.
 *
 * <p>For example, the specification for the contains(Object o) method says: "returns true if and
 * only if this collection contains at least one element e such that (o == null ? e == null : o
 * .equals(e))." The specification for {@link AnnotationMirrorSet#contains} is "returns true if and
 * only if this collection contains at least one element e such that (o == null ? e == null :
 * AnnotationUtils.areSame(o, e))".
 *
 * <p>AnnotationMirror is an interface and not all implementing classes provide a correct equals
 * method; therefore, the existing implementations of Set cannot be used.
 */
public class AnnotationMirrorSet implements Set<AnnotationMirror> {
    private Set<AnnotationMirror> shadowSet = new HashSet<>();

    /** Default constructor. */
    public AnnotationMirrorSet() {}

    public AnnotationMirrorSet(Collection<? extends AnnotationMirror> values) {
        this();
        this.addAll(values);
    }

    @Override
    public int size() {
        return shadowSet.size();
    }

    @Override
    public boolean isEmpty() {
        return shadowSet.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return shadowSet.contains(o);
    }

    @Override
    public Iterator<AnnotationMirror> iterator() {
        return shadowSet.iterator();
    }

    @Override
    public Object[] toArray() {
        return shadowSet.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return shadowSet.toArray(a);
    }

    @Override
    public boolean add(AnnotationMirror annotationMirror) {
        return shadowSet.add(annotationMirror);
    }

    @Override
    public boolean remove(Object o) {
        return shadowSet.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return shadowSet.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends AnnotationMirror> c) {
        return shadowSet.addAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return shadowSet.retainAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return shadowSet.removeAll(c);
    }

    @Override
    public void clear() {
        shadowSet.clear();
    }

    /**
     * Returns a new {@link AnnotationMirrorSet} that contains {@code value}.
     *
     * @param value AnnotationMirror to put in the set
     * @return a new {@link AnnotationMirrorSet} that contains {@code value}.
     */
    public static AnnotationMirrorSet singleElementSet(AnnotationMirror value) {
        AnnotationMirrorSet newSet = new AnnotationMirrorSet();
        newSet.add(value);
        return newSet;
    }
}
