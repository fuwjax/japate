package org.fuwjax.japate.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.fuwjax.japate.iteration.Reiterable;
import org.junit.jupiter.api.Test;

public class ReiterableTest {
    public static void iterableIs(Reiterable<?> iter, Object... values) {
        Iterator<?> i = iter.iterator();
        for(Object value: values) {
            assertTrue(i.hasNext());
            assertEquals(value, i.next());
        }
        assertFalse(i.hasNext());
    }   

    public static void iterableIs(Reiterable<?> iter, Iterable<?> values) {
        Iterator<?> i = iter.iterator();
        for(Object value: values) {
            assertTrue(i.hasNext());
            assertEquals(value, i.next());
        }
        assertFalse(i.hasNext());
    }   

    @Test 
    public void testEmpty() {
        iterableIs(Reiterable.empty());
    }

    @Test
    public void testRe() {
        iterableIs(Reiterable.re(List.of()));
        iterableIs(Reiterable.re(List.of(1, 2, 3)), 1, 2, 3);
    }

    @Test 
    public void testRearraynge() {
        iterableIs(Reiterable.of());
        iterableIs(Reiterable.of(1, 2, 3), 1, 2, 3);
    }

    @Test
    public void testRange() {
        iterableIs(Reiterable.range(0,0));
        iterableIs(Reiterable.range(0,4), 0, 1, 2, 3);
        iterableIs(Reiterable.range(0,4, 2), 0, 2);
        iterableIs(Reiterable.range(3,3, 0)); // step 0 requires start == stop
    }

    public void testUntil() {
        // see Reiterable.range for exercise
    }

    @Test 
    public void testFlatten() {
        iterableIs(Reiterable.flatten(List.of(List.of(0, 1, 2), List.of(6, 7))), 0, 1, 2, 6, 7);
        iterableIs(Reiterable.flatten(List.of(List.of(0, 1, 2), List.of(), List.of(6, 7))), 0, 1, 2, 6, 7);
        iterableIs(Reiterable.flatten(List.of(List.of(), List.of(), List.of())));
    }

    @Test 
    public void testAppend() {
        iterableIs(Reiterable.re(List.of(0, 1, 2)).append(List.of(6, 7)), 0, 1, 2, 6, 7);
        iterableIs(Reiterable.re(List.of(0, 1, 2)).append(List.of()), 0, 1, 2);
        iterableIs(Reiterable.re(List.of()).append(List.of(6, 7)), 6, 7);
        iterableIs(Reiterable.re(List.of()).append(List.of()));
    }

    @Test 
    public void testFilter() {
        iterableIs(Reiterable.re(List.of(0, 1, 2)).filter(i -> i%2 == 0), 0, 2);
        iterableIs(Reiterable.re(List.of(0, 1, 2)).filter(i -> i%2 == 1), 1);
        iterableIs(Reiterable.re(List.of(1, 3, 5)).filter(i -> i%2 == 0));
        iterableIs(Reiterable.re(List.<Integer>of()).filter(i -> i%2 == 0));
    }

    @Test 
    public void testMap() {
        iterableIs(Reiterable.re(List.of(0, 1, 2)).map(String::valueOf), "0", "1", "2");
        iterableIs(Reiterable.re(List.<Integer>of()).map(String::valueOf));
    }

    @Test 
    public void testReduce() {
        assertEquals(Reiterable.re(List.of(0, 1, 2)).reduce(0, (r, v) -> r + v), 3);
        assertEquals(Reiterable.re(List.<Integer>of()).reduce(0, (r, v) -> r + v), 0);
    }

    @Test 
    public void testAs() {
        List<Object> list = List.<Object>of(0, 1, 2);
        // Note that the following are in all ways equivalent. 
        iterableIs(Reiterable.re(list).as(Integer.class), 0, 1, 2);
        iterableIs(Reiterable.re(list).map(Integer.class::cast), 0, 1, 2);
    }

    @Test 
    public void testIsEmpty() {
        assertFalse(Reiterable.re(List.of(0, 1, 2)).isEmpty());
        assertTrue(Reiterable.re(List.of()).isEmpty());
        assertTrue(Reiterable.re(List.of(1, 3, 5)).filter(i -> i%2 == 0).isEmpty());
    }

    @Test 
    public void testFirst() {
        assertEquals(Reiterable.re(List.of(0, 1, 2)).first(), Optional.of(0));
        assertEquals(Reiterable.re(List.of()).first(), Optional.empty());
        assertEquals(Reiterable.re(List.of(1, 3, 5)).filter(i -> i%2 == 0).first(), Optional.empty());
    }

    @Test 
    public void testSelect() {
        assertEquals(Reiterable.re(List.of(0, 1, 2)).select(0), Optional.of(0));
        assertEquals(Reiterable.re(List.of(0, 1, 2)).select(1), Optional.of(1));
        assertEquals(Reiterable.re(List.of(0, 1, 2)).select(5), Optional.empty());
        assertEquals(Reiterable.of(0, 1, null).select(2), Optional.empty());
        assertEquals(Reiterable.re(List.of()).select(0), Optional.empty());
        assertEquals(Reiterable.re(List.of(1, 3, 5)).filter(i -> i%2 == 0).select(0), Optional.empty());
    }

    @Test 
    public void testJoin() {
        assertEquals(Reiterable.re(List.of("hello", ", ", "world", "!")).join(), "hello, world!");
        assertEquals(Reiterable.re(List.of("hello", ", ", "", "world!")).join(), "hello, world!");
        assertEquals(Reiterable.re(List.of("hello")).join(), "hello");
        assertEquals(Reiterable.re(List.of()).join(), "");

        assertEquals(Reiterable.re(List.of("hello", "world!")).join(", "), "hello, world!");
        assertEquals(Reiterable.re(List.of("hello", "", "world!")).join(", "), "hello, , world!");
        assertEquals(Reiterable.re(List.of("hello")).join(", "), "hello");
        assertEquals(Reiterable.re(List.of()).join(", "), "");
    }

    @Test 
    public void testCount() {
        assertEquals(Reiterable.re(List.of(0, 1, 2)).count(), 3);
        assertEquals(Reiterable.re(List.of()).count(), 0);
        assertEquals(Reiterable.re(List.of(1, 3, 5)).filter(i -> i%2 == 0).count(), 0);
    }

    @Test 
    public void testAny() {
        assertTrue(Reiterable.re(List.of(0, 1, 2)).any(i -> i == 2));
        assertFalse(Reiterable.re(List.of(0, 1, 2)).any(i -> i == 3));
        assertFalse(Reiterable.re(List.of()).any(i -> i != null));
        assertFalse(Reiterable.re(List.of(1, 3, 5)).filter(i -> i%2 == 0).any(i -> i == 1));
    }

    @Test 
    public void testContains() {
        assertTrue(Reiterable.re(List.of(0, 1, 2)).contains(2));
        assertFalse(Reiterable.re(List.of(0, 1, 2)).contains(3));
        assertFalse(Reiterable.re(List.of()).contains(1));
        assertFalse(Reiterable.re(List.of(1, 3, 5)).filter(i -> i%2 == 0).contains(1));
    }

    @Test 
    public void testAsList() {
        assertEquals(Reiterable.re(List.of(0, 1, 2)).asList(), List.of(0, 1, 2));
        assertEquals(Reiterable.empty().asList(), List.of());
    }

    @Test 
    public void testAsSet() {
        assertEquals(Reiterable.re(List.of(0, 1, 2)).asSet(), Set.of(0, 1, 2));
        assertEquals(Reiterable.empty().asSet(), Set.of());
    }

    @Test 
    public void testAsMap() {
        assertEquals(Reiterable.re(List.of(0, 1, 2)).asMap(v -> v), Map.of(0, 0, 1, 1, 2, 2));
        assertEquals(Reiterable.empty().asMap(v -> v), Map.of());

        assertEquals(Reiterable.re(List.of(0, 1, 2)).asMap(v -> v, v -> v), Map.of(0, 0, 1, 1, 2, 2));
        assertEquals(Reiterable.empty().asMap(v -> v, v -> v), Map.of());
    }
}