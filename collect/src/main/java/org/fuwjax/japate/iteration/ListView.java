package org.fuwjax.japate.iteration;

import java.util.AbstractSequentialList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * ListView is an unmodifiable presentation of an Iterable as a List. It does not provide any storage, nor any checks
 * on concurrent modification. It should only be used when the underlying Iterable can be treated as unmodifiable. If
 * you're looking for map/reduce goodness on a collection, simply extend your favorite implementation and toss in an
 * "implements Reiterable<>".
 * @param <E> the element type
 */
public class ListView<E> extends AbstractSequentialList<E> implements Reiterable<E> {
    /**
     * Wraps the given collection as a ListView.
     * @param <E> the element type
     * @param collection the source and continued storage for this view
     * @return the ListView
     */
    public static <E> ListView<E> of(Collection<E> collection) {
        return new ListView<>(collection, collection.size());
    }

    /**
     * Presents the given iterable as a ListView.
     * @param <E> the element type
     * @param iterable the element source
     * @return the ListView
     */
    public static <E> ListView<E> over(Iterable<E> iterable) {
        return new ListView<>(iterable);
    }

    private final Iterable<E> iterable;
    private int size = -1;

    protected ListView(Iterable<E> iterable) {
        this.iterable = iterable;
    }

    protected ListView(Iterable<E> iterable, int size) {
        this.iterable = iterable;
        this.size = size;
    }

    @Override
    public int size() {
        if(size < 0) {
            size = count();
        }
        return size;
    }

    @Override
    public Iterator<E> iterator() {
        return iterable.iterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        ListIterator<E> i = listIterator();
        while(i.hasNext() && i.nextIndex() < index) {
            i.next();
        }
        return i;
    }

    // An iterable is effectively a singly-linked list, and so there is no "previous" iteration possible
    @Override
    public ListIterator<E> listIterator() {
        return new ListIterator<E>() {
            private Iterator<E> i = iterator();
            private int index = 0;

            @Override
            public boolean hasNext() {
                return i.hasNext();
            }

            @Override
            public E next() {
                E result = i.next();
                index++;
                return result;
            }

            @Override
            public int nextIndex() {
                return index;
            }

            @Override
            public boolean hasPrevious() {
                throw new UnsupportedOperationException("ListView only supports forward iteration");
            }

            @Override
            public E previous() {
                throw new UnsupportedOperationException("ListView only supports forward iteration");
            }

            @Override
            public int previousIndex() {
                throw new UnsupportedOperationException("ListView only supports forward iteration");
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("ListView is unmodifiable");
            }

            @Override
            public void set(E e) {
                throw new UnsupportedOperationException("ListView is unmodifiable");
            }

            @Override
            public void add(E e) {
                throw new UnsupportedOperationException("ListView is unmodifiable");
            }
        };
    }
}
