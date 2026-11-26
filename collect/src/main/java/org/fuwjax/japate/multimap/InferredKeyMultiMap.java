package org.fuwjax.japate.multimap;

import java.util.function.Function;

import org.fuwjax.japate.iteration.ListView;

public abstract class InferredKeyMultiMap<K, V> extends AbstractMultiMap<K, V, V> {
    public static <K, V> InferredKeyMultiMap<K, V> infer(Function<? super V, ? extends K> keyOf) {
        return new InferredKeyMultiMap<K, V>() {
            @Override
            protected K keyFor(V value) {
                return keyOf.apply(value);
            }            
        };
    }

    @Override
    protected V valueFor(V value) {
        return value;
    }

    @Override
    public void putFirst(V value) {
        super.putFirst(value);
    }

    @Override
    public void putLast(V value) {
        super.putLast(value);
    }

    @Override
    public ListView<V> values() {
        return asList();
    }
}
