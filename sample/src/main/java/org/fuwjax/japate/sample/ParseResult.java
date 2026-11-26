package org.fuwjax.japate.sample;

import java.util.Optional;
import java.util.Set;

import org.fuwjax.japate.iteration.Reiterable;

public interface ParseResult extends Reiterable<RuleResult> {
    default Reiterable<Object> getAll(String name) {
        return filter(r -> name.equals(r.name())).map(RuleResult::value);
    }

    default Optional<Object> getFirst(String name) {
        return getAll(name).first();
    }

    default Reiterable<RuleResult> getAny(String... names) {
        Set<String> set = Set.of(names);
        return filter(r -> set.contains(r.name()));
    }

    default Optional<Object> getAnyFirst(String... names) {
        return getAny(names).map(RuleResult::value).first();
    }
}