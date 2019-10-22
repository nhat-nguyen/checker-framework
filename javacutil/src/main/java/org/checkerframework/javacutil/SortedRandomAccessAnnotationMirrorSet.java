package org.checkerframework.javacutil;

import java.util.*;
import javax.lang.model.element.AnnotationMirror;

import static org.checkerframework.javacutil.AnnotationUtils.areSame;

public class SortedRandomAccessAnnotationMirrorSet
        implements List<AnnotationMirror>,
        Set<AnnotationMirror>,
        RandomAccess,
        RandomAccessSet<AnnotationMirror> {

    private final Comparator<AnnotationMirror> comparator;
    private ArrayList<AnnotationMirror> shadowList;
    private TreeSet<AnnotationMirror> base = new TreeSet<>(AnnotationUtils.annotationOrdering());

    public SortedRandomAccessAnnotationMirrorSet() {
        comparator = AnnotationUtils.annotationOrdering();
        shadowList = new ArrayList<>();
    }

    private void assertsame() {
        if (shadowList.size() != base.size()) {
            assert false;
        }
        Iterator<AnnotationMirror> iter1 = shadowList.iterator();
        Iterator<AnnotationMirror> iter2 = base.iterator();

        while (iter1.hasNext()) {
            AnnotationMirror anno1 = iter1.next();
            AnnotationMirror anno2 = iter2.next();
            if (anno1 != anno2) {
                assert false;
            }
        }
    }

    //    public SortedRandomAccessAnnotationMirrorSet(Collection<? extends AnnotationMirror> copy)
    // {
    //        this();
    //        this.addAll(copy);
    //    }

    @Override
    public int size() {
        assert shadowList.size() == base.size();
        return shadowList.size();
    }

    @Override
    public boolean isEmpty() {
        assert shadowList.isEmpty() == base.isEmpty();
        return shadowList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        assert (indexOf(o) != -1) == base.contains(o);
        return indexOf(o) != -1;
    }

    @Override
    public Iterator<AnnotationMirror> iterator() {
        return shadowList.iterator();
    }

    @Override
    public Object[] toArray() {
        // assert false;
        return shadowList.toArray();
    }

    @Override
    public <T> T[] toArray(T[] ts) {
        // assert false;
        return shadowList.toArray(ts);
    }

    private boolean add(AnnotationMirror annotationMirror, boolean addbase) {
        boolean ret = false;
        if (addbase) {
            ret = base.add(annotationMirror);
        }
        int index = Collections.binarySearch(shadowList, annotationMirror, comparator);
        // Already found, don't insert the same value
        if (index >= 0) {
            if (addbase) {
                assertsame();
                assert ret == false;
            }
            return false;
        }

        // index = -(insertion point) - 1
        int insertionPoint = -index - 1;
        shadowList.add(insertionPoint, annotationMirror);
        if (addbase) {
            assertsame();
            assert  ret == true;
        }
        return true;
    }

    private boolean remove(Object o, boolean removebase) {
        boolean ret = false;
        if (removebase) {
            ret = base.remove(o);
        }
        // assert false;
        if (!(o instanceof AnnotationMirror)) {
            if (removebase) {
                assertsame();
                assert ret == false;
            }
            return false;
        }

        int index = Collections.binarySearch(shadowList, (AnnotationMirror) o, comparator);
        if (index < 0) {
            if (removebase) {
                assertsame();
                assert ret == false;
            }
            return false;
        }

        shadowList.remove(index);
        if (removebase) {
            assertsame();
            assert ret == true;
        }
        return true;
    }

    @Override
    public boolean add(AnnotationMirror annotationMirror) {
        return add(annotationMirror, true);
    }

    @Override
    public boolean remove(Object o) {
        return remove(o, true);
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
    // TODO: recheck
    @Override
    public boolean addAll(Collection<? extends AnnotationMirror> collection) {
        boolean ret = base.addAll(collection);
        boolean changed = false;
        for (AnnotationMirror anno : collection) {
            changed = add(anno, false) || changed;
        }
        assertsame();
        assert ret == changed;
        return changed;
    }

    // TODO: recheck
    @Override
    public boolean addAll(int i, Collection<? extends AnnotationMirror> collection) {
        // TODO: This is illegal
        throw new RuntimeException("Illegal operation");
    }

    // O(n^2)
    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean ret = base.removeAll(collection);
        boolean changed = false;
        for (Object val : collection) {
            changed = remove(val, false) || changed;
        }
        assertsame();
        assert  ret == changed;
        return changed;
    }

    // O(n^2)
    @Override
    public boolean retainAll(Collection<?> collection) {
         assert false;
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
        return true;
    }

    @Override
    public void clear() {
//        assert false;
        shadowList.clear();
        base.clear();
    }

    @Override
    public AnnotationMirror get(int i) {
        assert false;
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
         assert false;
        return shadowList.listIterator();
    }

    @Override
    public ListIterator<AnnotationMirror> listIterator(int i) {
         assert false;
        return shadowList.listIterator(i);
    }

    @Override
    public List<AnnotationMirror> subList(int i1, int i2) {
         assert false;
        return shadowList.subList(i1, i2);
    }

    @Override
    public Spliterator<AnnotationMirror> spliterator() {
         assert false;
        return shadowList.spliterator();
    }

    @Override
    public String toString() {
        return shadowList.toString();
    }

    @Override
    public int hashCode() {
        assert false;
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Collection)) {
            return false;
        }

        if (o instanceof SortedRandomAccessAnnotationMirrorSet) {
            return containsAll((Collection<?>) o);
        } else {
            return containsAll((Collection<?>) o);
        }
    }

    public static Unmodifiable unmodifiable(Set<AnnotationMirror> set) {
        return new Unmodifiable((SortedRandomAccessAnnotationMirrorSet) set);
    }

    private static class Unmodifiable extends SortedRandomAccessAnnotationMirrorSet {
        private final SortedRandomAccessAnnotationMirrorSet set;

        private Unmodifiable(SortedRandomAccessAnnotationMirrorSet set) {
            this.set = set;
        }

        @Override
        public int size() {
            return set.size();
        }

        @Override
        public boolean isEmpty() {
            return set.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return set.contains(o);
        }

        @Override
        public Iterator<AnnotationMirror> iterator() {
            return set.iterator();
        }

        @Override
        public Object[] toArray() {
            return set.toArray();
        }

        @Override
        public <T> T[] toArray(T[] ts) {
            return set.toArray(ts);
        }

        @Override
        public AnnotationMirror get(int i) {
            return set.get(i);
        }

        @Override
        public int indexOf(Object o) {
            return set.indexOf(o);
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            return set.containsAll(collection);
        }

        @Override
        public ListIterator<AnnotationMirror> listIterator() {
            return set.listIterator();
        }

        @Override
        public ListIterator<AnnotationMirror> listIterator(int i) {
            return set.listIterator(i);
        }

        @Override
        public List<AnnotationMirror> subList(int i1, int i2) {
            return set.subList(i1, i2);
        }

        @Override
        public Spliterator<AnnotationMirror> spliterator() {
            return set.spliterator();
        }

        @Override
        public String toString() {
            return set.toString();
        }

        @Override
        public boolean add(AnnotationMirror annotationMirror) {
            throw new RuntimeException("Illegal operation");
        }

        @Override
        public boolean remove(Object o) {
            throw new RuntimeException("Illegal operation");
        }

        @Override
        public boolean addAll(Collection<? extends AnnotationMirror> collection) {
            throw new RuntimeException("Illegal operation");
        }

        @Override
        public boolean addAll(int i, Collection<? extends AnnotationMirror> collection) {
            throw new RuntimeException("Illegal operation");
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            throw new RuntimeException("Illegal operation");
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            throw new RuntimeException("Illegal operation");
        }

        @Override
        public void clear() {
            throw new RuntimeException("Illegal operation");
        }

        @Override
        public int lastIndexOf(Object o) {
            throw new RuntimeException("Not implemented");
        }
    }
}
