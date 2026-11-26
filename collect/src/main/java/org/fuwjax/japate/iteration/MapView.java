package org.fuwjax.japate.iteration;

import static org.fuwjax.japate.iteration.Reiterable.re;

import java.util.AbstractMap;
import java.util.Map;
import java.util.function.Function;

/**
 * MapView is an unmodifiable presentation of an Iterable as a Map. It does not provide any storage, nor any checks
 * on concurrent modification. It should only be used when the underlying Iterable can be treated as unmodifiable.
 * 
 * Additionally, no check for uniqueness is made. It is the responsibility of the caller of the static builder methods
 * of this class to ensure that the Map requirements are guaranteed.
 * @param <K> the key type
 * @param <V> the value type
 */
public class MapView<K, V> extends AbstractMap<K, V> {
    /**
     * Wraps the given collection as a MapView.
     * @param <K> the key type
     * @param <V> the value type
     * @param map the source and continued storage for this view
     * @return the MapView
     */
    public static <K, V> MapView<K, V> of(Map<K, V> map) {
        return new MapView<>(SetView.of(map.entrySet()));
    }

    /**
     * Presents the given iterable as a MapView.
     * @param <K> the key type
     * @param <V> the value type
     * @param entries the entry source
     * @return the MapView
     */
    public static <K, V> MapView<K, V> over(Iterable<Entry<K, V>> entries) {
        return new MapView<>(re(entries).asSet());
    }

    /**
     * Presents the given iterable as a MapView, inferring the keys from the values via the key extraction function.
     * @param <K> the key type
     * @param <V> the value type
     * @param values the values for this map
     * @param key the key extraction function
     * @return the MapView
     */
    public static <K, V> MapView<K, V> over(Iterable<? extends V> values, Function<? super V, ? extends K> key) {
        return new MapView<>(re(values).<Entry<K,V>>map(v -> new SimpleImmutableEntry<K, V>(key.apply(v), v)).asSet());
    }

    /**
     * Presents the given iterable as a MapView, inferring the keys and values from the sources via the key and value extraction functions.
     * @param <K> the key type
     * @param <V> the value type
     * @param sources the sourcces for this map
     * @param key the key extraction function
     * @param value the value extraction function
     * @return the MapView
     */
    public static <K, V, T> MapView<K, V> over(Iterable<? extends T> sources, Function<? super T, ? extends K> key, Function<? super T, ? extends V> value) {
        return new MapView<>(re(sources).<Entry<K,V>>map(t -> new SimpleImmutableEntry<K, V>(key.apply(t), value.apply(t))).asSet());
    }
 
    private final SetView<Entry<K, V>> entries;

    protected MapView(SetView<Entry<K, V>> entries) {
        this.entries = entries;
    }

    @Override
    public SetView<Entry<K, V>> entrySet() {
        return entries;
    }
}
