package ninja.egg82.markov;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public final class ChainState<E> implements List<E> {
    // vars
    private final List<E> wrappedList;

    // constructor
    public ChainState(Collection<E> items) {
        if (items == null) {
            throw new IllegalArgumentException("items cannot be null.");
        }

        wrappedList = Collections.unmodifiableList(new ArrayList<>(items));
    }
    public ChainState(E... items) {
        if (items == null) {
            throw new IllegalArgumentException("items cannot be null.");
        }
        wrappedList = Collections.unmodifiableList(Arrays.asList(items));
    }

    // public
    public int size() { return wrappedList.size(); }
    public boolean isEmpty() { return wrappedList.isEmpty(); }

    public Object[] toArray() { return wrappedList.toArray(); }
    public <T> T[] toArray(T[] a) { return wrappedList.toArray(a); }

    public boolean add(E t) { throw new UnsupportedOperationException(); }
    public boolean remove(Object o) { throw new UnsupportedOperationException(); }

    public boolean contains(Object o) { return wrappedList.contains(o); }
    public boolean containsAll(Collection<?> c) { return wrappedList.containsAll(c); }

    public boolean addAll(Collection<? extends E> c) { throw new UnsupportedOperationException(); }
    public boolean addAll(int index, Collection<? extends E> c) { throw new UnsupportedOperationException(); }
    public boolean removeAll(Collection<?> c) { throw new UnsupportedOperationException(); }
    public boolean retainAll(Collection<?> c) { throw new UnsupportedOperationException(); }

    public void clear() { throw new UnsupportedOperationException(); }

    public E get(int index) { return wrappedList.get(index); }
    public E set(int index, E element) { throw new UnsupportedOperationException(); }
    public void add(int index, E element) { throw new UnsupportedOperationException(); }
    public E remove(int index) { throw new UnsupportedOperationException(); }

    public int indexOf(Object o) { return wrappedList.indexOf(o); }
    public int lastIndexOf(Object o) { return wrappedList.lastIndexOf(o); }

    public Iterator<E> iterator() { return wrappedList.iterator(); }
    public ListIterator<E> listIterator() { return wrappedList.listIterator(); }
    public ListIterator<E> listIterator(int index) { return wrappedList.listIterator(index); }
    public List<E> subList(int fromIndex, int toIndex) { return wrappedList.subList(fromIndex, toIndex); }

    public boolean equals(Object obj) {
        if (obj instanceof ChainState) {
            return equals((ChainState) obj);
        }
        return false;
    }
    public boolean equals(ChainState other) {
        if (other == null) {
            return false;
        }
        if (size() != other.size()) {
            return false;
        }

        for (int i = 0; i < size(); i++) {
            if (!get(i).equals(other.get(i))) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int code = size();
        for (E e : this) {
            code = (code * 37) + e.hashCode();
        }
        return code;
    }

    // private

}
