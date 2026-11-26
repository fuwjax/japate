package org.fuwjax.japate.iteration;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

/**
 * SetView is an unmodifiable presentation of an Iterable as a Set. It does not provide any storage, nor any checks
 * on concurrent modification. It should only be used when the underlying Iterable can be treated as unmodifiable.
 * 
 * Additionally, no check for uniqueness is made. It is the responsibility of the caller of the static builder methods
 * of this class to ensure that the Set requirements are guaranteed.
 * @param <E>
 */
public class SetView<E> extends AbstractSet<E> implements Reiterable<E> {
    /**
     * Wraps the given collection as a ListView.
     * @param <E> the element type
     * @param collection the source and continued storage for this view
     * @return the SetView
     */
    public static <E> SetView<E> of(Set<E> set) {
        return new SetView<>(set, set.size());
    }

    /**
     * Presents the given iterable as a SetView.
     * @param <E> the element type
     * @param iterable the element source
     * @return the SetView
     */
   public static <E> SetView<E> over(Iterable<E> iterable) {
        return new SetView<>(iterable);
    }

    private int size = -1;
    private final Iterable<E> iterable;

    protected SetView(Iterable<E> iterable) {
        this.iterable = iterable;
    }

    protected SetView(Iterable<E> iterable, int size) {
        this.iterable = iterable;
        this.size = size;
    }

    @Override
    public int size() {
        if(size < 0) {
            size = count();
        }
        return size;
    }

    @Override
    public Iterator<E> iterator() {
        return iterable.iterator();
    }
}
