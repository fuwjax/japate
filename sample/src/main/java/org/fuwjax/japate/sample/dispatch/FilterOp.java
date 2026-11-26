package org.fuwjax.japate.sample.dispatch;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * FilterOp
 */
public interface FilterOp<T> extends Predicate<T> {
    static <T> FilterOp<T> not(Predicate<? super T> op) {
        return t -> !op.test(t);
    }

    default FilterOp<T> is(T expected) {
        return t -> Objects.equals(t, expected);
    }

    default FilterOp<T> and(Predicate<? super T> expected) {
        return t -> test(t) && expected.test(t);
    }

    default FilterOp<T> or(Predicate<? super T> expected) {
        return t -> test(t) || expected.test(t);
    }
}
