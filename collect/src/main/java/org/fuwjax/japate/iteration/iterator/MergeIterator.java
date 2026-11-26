package org.fuwjax.japate.iteration.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * MergeIterator flattens an iterator of iterators of elements into an iterator of elements. 
 * @param <T> the element type
 */
public class MergeIterator<T> implements Iterator<T> {
    private Iterator<? extends T> current;
    private Iterator<? extends Iterator<? extends T>> inception;

    public MergeIterator(Iterator<? extends Iterator<? extends T>> inception) {
        this.inception = inception;
    }

    @Override
    public boolean hasNext() {
        while(current == null || !current.hasNext()) {
            if(!inception.hasNext()) {
                return false;
            }
            current = inception.next();
        }
        return true;
    }

    @Override
    public T next() {
        if(!hasNext()) {
            throw new NoSuchElementException();
        }
        return current.next();
    }
}
