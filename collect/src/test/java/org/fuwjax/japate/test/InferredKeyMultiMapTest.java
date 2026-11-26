package org.fuwjax.japate.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import org.fuwjax.japate.iteration.Reiterable;
import org.fuwjax.japate.multimap.InferredKeyMultiMap;
import org.fuwjax.japate.multimap.MultiMap;
import org.junit.jupiter.api.Test;

public class InferredKeyMultiMapTest {
    public static void iterableIs(Reiterable<?> iter, Object... values) {
        Iterator<?> i = iter.iterator();
        for(Object value: values) {
            assertTrue(i.hasNext());
            assertEquals(value, i.next());
        }
        assertFalse(i.hasNext());
    }   

    public static String firstWord(String phrase) {
        return phrase.split(" ", 2)[0];
    }

    @Test 
    public void testMultiMap() {
        InferredKeyMultiMap<String, String> map = InferredKeyMultiMap.infer(InferredKeyMultiMapTest::firstWord);
        assertTrue(map.isEmpty());
        
        map.putFirst("bob hope"); // first and last are only meaningful at the time of insertion
        map.putLast("nancy drew"); // first and last-ness are not preserved as more entries are added
        map.putFirst("bob newhart");
        map.putFirst("nancy pelosi");
        map.putFirst("bob dylan");
        map.putLast("nancy reagan");

        assertFalse(map.isEmpty());
        assertEquals(Set.of("bob", "nancy"), map.keySet());
        iterableIs(map.keys(), "bob", "nancy", "bob", "bob", "nancy", "nancy");
        iterableIs(map.values(), "bob dylan", "nancy pelosi", "bob newhart", "bob hope", "nancy drew", "nancy reagan");
        assertTrue(map.contains("bob"));
        assertFalse(map.contains("jimmy"));
        iterableIs(map.getAll("bob"), "bob dylan", "bob newhart", "bob hope");
        assertEquals(Optional.of("nancy pelosi"), map.getFirst("nancy"));
        assertEquals(map.entries(), map.getAny("bob", "nancy"));
        assertEquals(map.values(), map.asList());
    }

    @Test
    public void testMergeLast() {
        // to use mergeLast, create a final concrete subclass of InferredKeyMultiMap
        class FirstNameMultiMap extends InferredKeyMultiMap<String, String>  {
            @Override
            protected String keyFor(String value) {
                return firstWord(value);
            }

            public void mergeLast(MultiMap<String, String> map) {
                if(map instanceof FirstNameMultiMap fm) {
                    super.mergeLast(fm);
                }else if(!map.isEmpty()) {
                    throw new UnsupportedOperationException("cannot merge with other multimaps");
                }
            }
        }
        FirstNameMultiMap map = new FirstNameMultiMap();
        map.putFirst("bob hope"); // first and last are only meaningful at the time of insertion
        map.putLast("nancy drew"); // first and last-ness are not preserved as more entries are added
        map.putFirst("bob newhart");
        map.putFirst("nancy pelosi");
        map.putFirst("bob dylan");
        map.putLast("nancy reagan");

        FirstNameMultiMap second = new FirstNameMultiMap();
        second.putFirst("bob marley");
        second.putFirst("billy joel");
        second.putLast("billy idol");
        second.putFirst("bob ross");

        map.mergeLast(second);

        assertFalse(map.isEmpty());
        assertEquals(Set.of("bob", "nancy", "billy"), map.keySet());
        iterableIs(map.keys(), "bob", "nancy", "bob", "bob", "nancy", "nancy", "bob", "billy", "bob", "billy");
        iterableIs(map.values(), "bob dylan", "nancy pelosi", "bob newhart", "bob hope", "nancy drew", "nancy reagan", "bob ross", "billy joel", "bob marley", "billy idol");

    }
}
