package org.fuwjax.japate.iteration.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 * 
 * UntilIterator returns elements until the predicate tests true for an element or it reaches the natural end if never found.
 * @param <R>
 */
public class UntilIterator<R> implements Iterator<R> {
    private final Iterator<R> iterator;
    private boolean ready = false;
    private boolean abort = false;
    private R candidate;
    private final Predicate<? super R> stop;

    /**
     * Creates a filtering iterator wrapper.
     * @param iterator the source iterator
     * @param stop the predicate that must be true for an element to return from next
     */
    public UntilIterator(Iterator<R> iterator, Predicate<? super R> stop) {
        this.iterator = iterator;
        this.stop = stop;
    }

    @Override
    public final boolean hasNext() {
        while(!abort && !ready && iterator.hasNext()) {
            candidate = iterator.next();
            abort = stop.test(candidate);
            ready = !abort;
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
