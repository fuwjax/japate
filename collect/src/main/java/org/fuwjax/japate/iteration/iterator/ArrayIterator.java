package org.fuwjax.japate.iteration.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayIterator<R> implements Iterator<R> {
    private final R[] source;
    private int index = 0;

    @SafeVarargs
    public ArrayIterator(R... source) {
        this.source = source;
    }

    @Override
    public boolean hasNext() {
        return index < source.length;
    }

    @Override
    public R next() {
        if(!hasNext()) {
            throw new NoSuchElementException();
        }
        return source[index++];
    }
}
