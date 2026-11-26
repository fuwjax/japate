package org.fuwjax.japate.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fuwjax.japate.iteration.ListView;
import org.fuwjax.japate.iteration.MapView;
import org.fuwjax.japate.iteration.Reiterable;
import org.fuwjax.japate.iteration.SetView;
import org.junit.jupiter.api.Test;

public class ViewTest {
    @Test
    public void testListViewOf() {
        ListView<Integer> view = ListView.of(List.of(0, 1, 2, 3));
        assertEquals(List.of(0, 1, 2, 3), view);
        assertEquals(4, view.size());
    }

    @Test
    public void testListViewOver() {
        ListView<Integer> view = ListView.over(Reiterable.range(0, 4));
        assertEquals(List.of(0, 1, 2, 3), view);
        assertEquals(4, view.size());
    }

    @Test
    public void testSetViewOf() {
        SetView<Integer> view = SetView.of(Set.of(0, 1, 2, 3));
        assertEquals(Set.of(0, 1, 2, 3), view);
        assertEquals(4, view.size());
    }

    @Test
    public void testSetViewOver() {
        SetView<Integer> view = SetView.over(Reiterable.range(0, 4));
        assertEquals(Set.of(0, 1, 2, 3), view);
        assertEquals(4, view.size());
    }

    @Test
    public void testMapViewOf() {
        MapView<Integer, Integer> view = MapView.of(Map.of(0, 1, 1, 3, 2, 5));
        assertEquals(Map.of(0, 1, 1, 3, 2, 5), view);
        assertEquals(3, view.size());
    }

    @Test
    public void testMapViewOver() {
        MapView<Integer, Integer> view = MapView.over(Reiterable.range(0, 3).map(i -> new SimpleImmutableEntry<>(i, 2*i+1)));
        assertEquals(Map.of(0, 1, 1, 3, 2, 5), view);
        assertEquals(3, view.size());

        MapView<Integer, Integer> inferKey = MapView.over(Reiterable.range(1, 7, 2), i -> (i-1)/2);
        assertEquals(Map.of(0, 1, 1, 3, 2, 5), inferKey);
        assertEquals(3, inferKey.size());

        MapView<Integer, Integer> both = MapView.over(Reiterable.range(0, 6,2), i -> i/2, i -> i+1);
        assertEquals(Map.of(0, 1, 1, 3, 2, 5), both);
        assertEquals(3, both.size());
    }
}
