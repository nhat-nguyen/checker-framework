package org.checkerframework.javacutil;

import java.util.*;
import javax.lang.model.element.AnnotationMirror;

public class SortedRandomAccessAnnotationMirrorSet
        implements List<AnnotationMirror>,
                Set<AnnotationMirror>,
                RandomAccess,
                RandomAccessSet<AnnotationMirror> {

    private final Comparator<AnnotationMirror> comparator;
    private TreeSet<AnnotationMirror> baseline;
    private ArrayList<AnnotationMirror> shadowList;

    public SortedRandomAccessAnnotationMirrorSet() {
        comparator = AnnotationUtils.annotationOrdering();
        shadowList = new ArrayList<>();
        baseline = new TreeSet<>(comparator);
    }

    public SortedRandomAccessAnnotationMirrorSet(Collection<? extends AnnotationMirror> copy) {
        this();
        this.addAll(copy);
    }

    private void assertSame() {
        assert baseline.size() == shadowList.size();
        Iterator<AnnotationMirror> itBaseline = baseline.iterator();
        Iterator<AnnotationMirror> itNewImpl = shadowList.iterator();
        while (itBaseline.hasNext()) {
            AnnotationMirror a = itBaseline.next();
            AnnotationMirror b = itNewImpl.next();
            assert AnnotationUtils.areSame(a, b);
        }
    }

    @Override
    public int size() {
        return shadowList.size();
    }

    @Override
    public boolean isEmpty() {
        return shadowList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    @Override
    public Iterator<AnnotationMirror> iterator() {
        return shadowList.iterator();
    }

    @Override
    public Object[] toArray() {
        return shadowList.toArray();
    }

    @Override
    public <T> T[] toArray(T[] ts) {
        return shadowList.toArray(ts);
    }

    @Override
    public boolean add(AnnotationMirror annotationMirror) {
        baseline.add(annotationMirror);

        int initialSize = shadowList.size();
        int index = Collections.binarySearch(shadowList, annotationMirror, comparator);
        // Already found, don't insert the same value
        if (index >= 0) {
            assertSame();
            return false;
        }

        // index = -(insertion point) - 1
        int insertionPoint = -index - 1;
        shadowList.add(insertionPoint, annotationMirror);
        assert shadowList.size() == initialSize + 1;

        assertSame();

        return true;
    }

    @Override
    public boolean remove(Object o) {
        baseline.remove(o);

        if (!(o instanceof AnnotationMirror)) {
            assertSame();
            return false;
        }

        int initialSize = shadowList.size();
        int index = Collections.binarySearch(shadowList, (AnnotationMirror) o, comparator);
        if (index < 0) {
            assertSame();
            return false;
        }

        shadowList.remove(index);
        assert shadowList.size() == initialSize - 1;

        assertSame();

        return true;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        for (Object el : collection) {
            if (indexOf(el) == -1) {
                return false;
            }
        }
        return true;
    }

    // O(n^2)
    @Override
    public boolean addAll(Collection<? extends AnnotationMirror> collection) {
        boolean changed = false;
        for (AnnotationMirror anno : collection) {
            changed = add(anno);
        }

        assertSame();

        return changed;
    }

    @Override
    public boolean addAll(int i, Collection<? extends AnnotationMirror> collection) {
        // TODO: This is illegal
        throw new RuntimeException("Illegal operation");
    }

    // O(n^2)
    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean changed = false;
        for (Object val : collection) {
            changed = remove(val);
        }

        assertSame();
        return changed;
    }

    // O(n^2)
    @Override
    public boolean retainAll(Collection<?> collection) {
        baseline.retainAll(collection);
        ArrayList<AnnotationMirror> toRetain = new ArrayList<>(collection.size());
        for (Object el : collection) {
            int index = indexOf(el);
            if (index != -1) {
                toRetain.add(shadowList.get(index));
            }
        }

        if (toRetain.size() == shadowList.size()) {
            return false;
        }

        toRetain.sort(comparator);
        shadowList = toRetain;

        assertSame();

        return true;
    }

    @Override
    public void clear() {
        shadowList.clear();
        baseline.clear();
    }

    @Override
    public AnnotationMirror get(int i) {
        return shadowList.get(i);
    }

    @Override
    public AnnotationMirror set(int i, AnnotationMirror annotationMirror) {
        // TODO: This is illegal
        throw new RuntimeException("Illegal operation");
    }

    @Override
    public void add(int i, AnnotationMirror annotationMirror) {
        // TODO: This is illegal
        throw new RuntimeException("Illegal operation");
    }

    @Override
    public AnnotationMirror remove(int i) {
        // TODO: This is illegal
        throw new RuntimeException("Illegal operation");
    }

    @Override
    public int indexOf(Object o) {
        if (!(o instanceof AnnotationMirror)) {
            return -1;
        }

        int index = Collections.binarySearch(shadowList, (AnnotationMirror) o, comparator);
        return index < 0 ? -1 : index;
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public ListIterator<AnnotationMirror> listIterator() {
        return shadowList.listIterator();
    }

    @Override
    public ListIterator<AnnotationMirror> listIterator(int i) {
        return shadowList.listIterator(i);
    }

    @Override
    public List<AnnotationMirror> subList(int i1, int i2) {
        return shadowList.subList(i1, i2);
    }

    @Override
    public Spliterator<AnnotationMirror> spliterator() {
        return shadowList.spliterator();
    }

    @Override
    public String toString() {
        return shadowList.toString();
    }
}
