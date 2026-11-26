package org.fuwjax.japate.multimap;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map;
import java.util.Map.Entry;

import org.fuwjax.japate.iteration.ListView;

public class StandardMultiMap<K, V> extends AbstractMultiMap<K, V, Entry<K, V>> {
    public static <K, V> StandardMultiMap<K, V> of(K key, V value) {
        StandardMultiMap<K, V> map = new StandardMultiMap<>();
        map.putFirst(key, value);
        return map;
    }

    @Override
    protected K keyFor(Entry<K, V> entry) {
        return entry.getKey();
    }

    @Override
    protected V valueFor(Entry<K, V> entry) {
        return entry.getValue();
    }

    @Override
    public ListView<Map.Entry<K, V>> entries() {
        return asList();
    }

    public void putFirst(K key, V value) {
        super.putFirst(new SimpleImmutableEntry<>(key, value));
    }

    public void putLast(K key, V value) {
        super.putLast(new SimpleImmutableEntry<>(key, value));
    }

    public void mergeLast(MultiMap<K, V> map) {
        if (map instanceof StandardMultiMap<K, V> s) {
            super.mergeLast(s);
        } else {
            map.entries().forEach(e -> putLast(e.getKey(), e.getValue())); // map.entries().forEach(super::putLast) ???
        }
    }
}
