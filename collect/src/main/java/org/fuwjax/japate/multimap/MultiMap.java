package org.fuwjax.japate.multimap;

import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.fuwjax.japate.iteration.ListView;
import org.fuwjax.japate.iteration.Reiterable;
import org.fuwjax.japate.iteration.SetView;

public interface MultiMap<K, V> {
    @SuppressWarnings("rawtypes")
    static MultiMap EMPTY = new MultiMap() {
        @Override
        public SetView keySet() {
            return SetView.over(Reiterable.empty());
        }

        @Override
        public ListView entries() {
            return ListView.over(Reiterable.empty());
        }
    };

    @SuppressWarnings("unchecked")
    static <K, V> MultiMap<K, V> empty() {
        return EMPTY;
    }

    default boolean isEmpty() {
        return entries().isEmpty();
    }

    default ListView<K> keys() {
        return entries().map(Entry::getKey).asList();
    }

    SetView<K> keySet();

    default ListView<V> values() {
        return entries().map(Entry::getValue).asList();
    }

    ListView<? extends Entry<K, V>> entries();

    //Object instead of K because Java is, was, and always will be silly about generics
    default boolean contains(Object key) {
        return keySet().contains(key);
    }

    default ListView<V> getAll(Object key) {
        return entries().filter(e -> Objects.equals(e.getKey(), key)).map(Entry::getValue).asList();
    }

    default Optional<V> getFirst(Object key) {
        return getAll(key).first();
    }

    default ListView<? extends Entry<K, V>> getAny(Object... keys) {
        Set<?> keySet = Set.of(keys);
        return entries().filter(e -> keySet.contains(e.getKey())).asList();
    }
}
