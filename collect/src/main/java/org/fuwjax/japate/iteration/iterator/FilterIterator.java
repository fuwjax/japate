package org.fuwjax.japate.iteration.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 * 
 * FilterIterator filters an existing iterator for elements matching the predicate filter.
 * @param <R>
 */
public class FilterIterator<R> implements Iterator<R> {
    private final Iterator<R> iterator;
    private boolean ready = false;
    private R candidate;
    private final Predicate<? super R> filter;

    /**
     * Creates a filtering iterator wrapper.
     * @param iterator the source iterator
     * @param filter the predicate that must be true for an element to return from next
     */
    public FilterIterator(Iterator<R> iterator, Predicate<? super R> filter) {
        this.iterator = iterator;
        this.filter = filter;
    }

    @Override
    public final boolean hasNext() {
        while(!ready && iterator.hasNext()) {
            candidate = iterator.next();
            ready = filter.test(candidate);
        }
        return ready;
    }

    @Override
    public final R next() {
        if(!hasNext()) {
            throw new NoSuchElementException();
        }
        ready = false;
        return candidate;
    }
}
