package org.fuwjax.japate.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;

import org.fuwjax.japate.node.NodeList;
import org.fuwjax.japate.node.SimpleNodeList;
import org.junit.jupiter.api.Test;

public class NodeListTest {
    public static void iterableIs(Iterable<?> iter, Object... values) {
        Iterator<?> i = iter.iterator();
        for(Object value: values) {
            assertTrue(i.hasNext());
            assertEquals(value, i.next());
        }
        assertFalse(i.hasNext());
    }   

    @Test 
    public void testEmpty() {
        NodeList<Object> empty = NodeList.empty();
        assertTrue(empty.isEmpty());
        iterableIs(empty);
        
        NodeList<Object> append = empty.append(NodeList.empty());
        assertTrue(append.isEmpty());
        append = append.append(null);
        assertTrue(append.isEmpty());

        NodeList<Object> prepend = empty.prepend(NodeList.empty());
        assertTrue(prepend.isEmpty());
        prepend = prepend.prepend(null);
        assertTrue(prepend.isEmpty());
    }

    @Test 
    public void testAppend() {
        NodeList<Integer> one = new SimpleNodeList<Integer>(1);
        NodeList<Integer> two = new SimpleNodeList<Integer>(2);
        NodeList<Integer> three = new SimpleNodeList<Integer>(3);

        assertFalse(one.isEmpty());
        iterableIs(one, 1);

        NodeList<Integer> onetwo = one.append(two);
        assertFalse(onetwo.isEmpty());
        iterableIs(onetwo, 1, 2);
        iterableIs(one, 1, 2);

        NodeList<Integer> onetwothree = three.prepend(onetwo);
        assertFalse(onetwothree.isEmpty());
        iterableIs(onetwothree, 1, 2, 3);
        iterableIs(onetwo, 1, 2, 3);
        iterableIs(one, 1, 2, 3);
        iterableIs(two, 2, 3);

        NodeList<Integer> num = onetwothree;
        while(!num.isEmpty()) {
            assertEquals(3, num.tail().value());
            num = num.next();
        }
    
        try {
            one.append(one);
        } catch(IllegalStateException e) {
            assertEquals("Cannot append target, already in list", e.getMessage());
        }
    }
}
