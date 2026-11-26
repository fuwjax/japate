package org.fuwjax.japate.parser;

import java.util.Optional;
import java.util.Set;

import org.fuwjax.japate.iteration.Reiterable;

public interface ParseResult extends Reiterable<Result> {
    default Reiterable<Object> values() {
        return map(Result::value);
    }

    default Reiterable<Object> getAll(String name) {
        return filter(r -> name.equals(r.name())).map(Result::value);
    }

    default Optional<Object> getFirst(String name) {
        return getAll(name).first();
    }

    default Reiterable<Result> getAny(String... names) {
        Set<String> set = Set.of(names);
        return filter(r -> set.contains(r.name()));
    }

    default Optional<Object> getAnyFirst(String... names) {
        return getAny(names).map(Result::value).first();
    }

    default String join() {
        return values().join();
    }
}