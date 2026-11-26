package org.fuwjax.japate.multimap;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.fuwjax.japate.iteration.ListView;
import org.fuwjax.japate.iteration.Reiterable;
import org.fuwjax.japate.iteration.SetView;
import org.fuwjax.japate.node.NodeList;
import org.fuwjax.japate.node.SimpleNodeList;

public abstract class AbstractMultiMap<K, V, T> implements Reiterable<T>, MultiMap<K, V> {
    private Map<K, NodeList<V>> keys = new HashMap<>();
    private NodeList<T> values = NodeList.empty();

    protected abstract K keyFor(T value);
    protected abstract V valueFor(T value);

    @Override
    public Iterator<T> iterator() {
        return values.iterator();
    }

    @Override
    public ListView<K> keys() {
        return values.map(this::keyFor).asList();
    }
    
    @Override
    public SetView<K> keySet() {
        return SetView.of(keys.keySet());
    }

    @Override
    public ListView<V> values() {
        return values.map(this::valueFor).asList();
    }

    @Override
    public ListView<? extends Map.Entry<K, V>> entries() {
        return values.map(v -> new SimpleImmutableEntry<>(keyFor(v), valueFor(v))).asList();
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public boolean contains(Object key) {
        return keys.containsKey(key);
    }

    protected void putFirst(T value) {
        NodeList<T> newNode = new SimpleNodeList<>(value);
        values = newNode.append(values);

        K key = keyFor(value);
        NodeList<V> newKeyNode = new SimpleNodeList<V>(valueFor(value));
        NodeList<V> old = keys.put(key, newKeyNode);
        newKeyNode.append(old); // no chance for old to still be the head, so we can ignore the return value
    }

    protected void putLast(T value) {
        NodeList<T> newNode = new SimpleNodeList<>(value);
        values = values.append(newNode);

        putKeyLast(keyFor(value), new SimpleNodeList<V>(valueFor(value)));
    }

    protected void putKeyLast(K key, NodeList<V> newNode) {
        NodeList<V> old = keys.get(key);
        NodeList<V> result = newNode.prepend(old); // use prepend to avoid a null check here
        if (result != old) { // intentionally not .equals
            keys.put(key, result);
        }
    }

    // this can't be public, it only works if you can force the keyFor to always always always return the same result between the two maps
    protected void mergeLast(AbstractMultiMap<K, V, T> map) {
        values = values.append(map.values);
        for(Map.Entry<K, NodeList<V>> entry: map.keys.entrySet()) {
            putKeyLast(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public ListView<V> getAll(Object key) {
        return keys.getOrDefault(key, NodeList.empty()).asList();
    }
}
