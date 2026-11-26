package org.fuwjax.japate.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import org.fuwjax.japate.iteration.Reiterable;
import org.fuwjax.japate.multimap.StandardMultiMap;
import org.junit.jupiter.api.Test;

public class StandardMultiMapTest {
    public static void iterableIs(Reiterable<?> iter, Object... values) {
        Iterator<?> i = iter.iterator();
        for(Object value: values) {
            assertTrue(i.hasNext());
            assertEquals(value, i.next());
        }
        assertFalse(i.hasNext());
    }   

    @Test 
    public void testMultiMap() {
        StandardMultiMap<String, String> map = new StandardMultiMap<>();
        assertTrue(map.isEmpty());
        
        map.putFirst("bob", "hope"); // first and last are only meaningful at the time of insertion
        map.putLast("nancy", "drew"); // first and last-ness are not preserved as more entries are added
        map.putFirst("bob", "newhart");
        map.putFirst("nancy", "pelosi");
        map.putFirst("bob", "dylan");
        map.putLast("nancy", "reagan");

        assertFalse(map.isEmpty());
        assertEquals(Set.of("bob", "nancy"), map.keySet());
        iterableIs(map.keys(), "bob", "nancy", "bob", "bob", "nancy", "nancy");
        iterableIs(map.values(), "dylan", "pelosi", "newhart", "hope", "drew", "reagan");
        assertTrue(map.contains("bob"));
        assertFalse(map.contains("jimmy"));
        iterableIs(map.getAll("bob"), "dylan", "newhart", "hope");
        assertEquals(Optional.of("pelosi"), map.getFirst("nancy"));
        assertEquals(map.entries(), map.getAny("bob", "nancy"));
        assertEquals(map.entries(), map.asList());
    }

    @Test 
    public void testMergeLast() {
        StandardMultiMap<String, String> map = new StandardMultiMap<>();
        map.putFirst("bob", "hope"); // first and last are only meaningful at the time of insertion
        map.putLast("nancy", "drew"); // first and last-ness are not preserved as more entries are added
        map.putFirst("bob", "newhart");
        map.putFirst("nancy", "pelosi");
        map.putFirst("bob", "dylan");
        map.putLast("nancy", "reagan");

        StandardMultiMap<String, String> second = new StandardMultiMap<>();
        second.putFirst("bob", "marley");
        second.putFirst("billy", "joel");
        second.putLast("billy", "idol");
        second.putFirst("bob", "ross");

        map.mergeLast(second);

        assertFalse(map.isEmpty());
        assertEquals(Set.of("bob", "nancy", "billy"), map.keySet());
        iterableIs(map.keys(), "bob", "nancy", "bob", "bob", "nancy", "nancy", "bob", "billy", "bob", "billy");
        iterableIs(map.values(), "dylan", "pelosi", "newhart", "hope", "drew", "reagan", "ross", "joel", "marley", "idol");

        StandardMultiMap<String, String> third = new StandardMultiMap<>();
        third.putFirst("billy", "graham");
        map.mergeLast(third);

        iterableIs(map.values(), "dylan", "pelosi", "newhart", "hope", "drew", "reagan", "ross", "joel", "marley", "idol", "graham");
        //expect weird behavior if you keep using a multimap after passing it as a mergeLast argument
        iterableIs(second.values(), "ross", "joel", "marley", "idol", "graham");
    }
}
