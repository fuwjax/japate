package org.fuwjax.japate.iteration.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class MapIterator<T, R> implements Iterator<R> {
    private final Iterator<T> source;
    private final Function<? super T, ? extends R> mapper;

    public MapIterator(Iterator<T> source, Function<? super T, ? extends R> mapper) {
        this.source = source;
        this.mapper = mapper;
    }

    @Override
    public boolean hasNext() {
        return source.hasNext();
    }

    @Override
    public R next() {
        if(!hasNext()) {
            throw new NoSuchElementException();
        }
       return mapper.apply(source.next());
    }
}
