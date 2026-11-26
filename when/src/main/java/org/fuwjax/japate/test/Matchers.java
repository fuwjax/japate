package org.fuwjax.japate.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class Matchers {
    public static <T> Consumer<T> is(Object expected) {
        return actual -> assertEquals(expected, actual);
    }

    public static <T> Consumer<Iterable<?>> iterableOf(Object... expected) {
        return iterableIs(List.of(expected));
    }

    public static <T> Consumer<Iterable<?>> iterableIs(Iterable<?> expected) {
        return actual -> {
            Iterator<?> i = actual.iterator();
            for(Object value: expected) {
                assertTrue(i.hasNext());
                assertEquals(value, i.next());
            }
            assertFalse(i.hasNext());
        };
    }
}
