package org.fuwjax.japate.sample;

public interface ParseSuccess<T> {
    T success(String value);

    T success(Iterable<T> results);
    
    T success();

    T success(String name, T result);
}
