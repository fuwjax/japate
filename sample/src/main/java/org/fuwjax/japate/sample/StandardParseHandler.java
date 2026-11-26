package org.fuwjax.japate.sample;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;

public class StandardParseHandler implements ParseSuccess<Iterable<RuleResult>>{
    private static record RuleResultImpl(String name, Object value) implements RuleResult{}

    private final BiFunction<String, Iterable<RuleResult>, Object> resolver;

    public StandardParseHandler(BiFunction<String, Iterable<RuleResult>, Object> resolver) {
        this.resolver = resolver;
    }

    @Override
    public Iterable<RuleResult> success(String match) {
        return result("", match);
    }

    @Override
    public Iterable<RuleResult> success(String name, Iterable<RuleResult> result) {
        Object value = resolver.apply(name, result);
        return result(name, value);
    }

    private Iterable<RuleResult> result(String name, Object value) {
        RuleResult pair = new RuleResultImpl(name, value);
        return () -> new Iterator<>() {
            private boolean ready = true;

            @Override
            public boolean hasNext() {
                return ready;
            }

            @Override
            public RuleResult next() {
                if(!hasNext()) {
                    throw new NoSuchElementException();
                }
                ready = false;
                return pair;
            }
        };
    }

    @Override
    public Iterable<RuleResult> success(Iterable<Iterable<RuleResult>> results) {
        return () -> new Iterator<>() {
            private Iterator<Iterable<RuleResult>> r = results.iterator();
            private Iterator<RuleResult> iter;
            @Override
            public boolean hasNext() {
                while(iter == null || !iter.hasNext()) {
                    if (!r.hasNext()) {
                        return false;
                    }
                    iter = r.next().iterator();
                }
                return true;
            }

            @Override
            public RuleResult next() {
                if(!hasNext()) {
                    throw new NoSuchElementException();
                }
                return iter.next();
            }

        };
    }

    @Override
    public Iterable<RuleResult> success() {
        return () -> new Iterator<>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public RuleResult next() {
                throw new NoSuchElementException();
            }
        };
    }
}
