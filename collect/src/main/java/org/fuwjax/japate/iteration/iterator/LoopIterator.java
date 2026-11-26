package org.fuwjax.japate.iteration.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 
 * LoopIterator creates an iterator that returns values starting with start, produced by the walker, until a value tests
 * true with stop.
 * @param <R>
 */
public class LoopIterator<R> implements Iterator<R> {
    private R candidate;
    private final Function<? super R, ? extends R> walker;
    private final Predicate<? super R> stop;

    public LoopIterator(R start, Function<? super R, ? extends R> walker, Predicate<? super R> stop) {
        candidate = start;
        this.walker = walker;
        this.stop = stop;
    }

    @Override
    public final boolean hasNext() {
        return !stop.test(candidate);
    }

    @Override
    public final R next() {
        if(!hasNext()) {
            throw new NoSuchElementException();
        }
        R next = candidate;
        candidate = walker.apply(candidate);
        return next;
    }
}
