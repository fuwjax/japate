package org.fuwjax.japate.node;

import java.util.Iterator;

import org.fuwjax.japate.iteration.Reiterable;

/**
 * NodeList is a singly linked list implementation for use-cases that rely on list appends. Note that there
 * is no mechanism given here for inserts, removals, or updates. This is by design, as NodeList implementations
 * may directly link other NodeLists. This can result in unintentional side effects, for example:
 * 
 * <pre>
 * NodeList<Object> a = ...
 * NodeList<Object> b = ...
 * NodeList<Object> c = ...
 * a = a.append(b);
 * a = a.append(c);
 * </pre>
 * Iterating over NodeList a will see the elements of the original a, followed by b and then c. But iterating over NodeList b may see
 * the elements of b followed by those from c. This may seem like a flaw in the design, but in nested/recursive functions it allows for
 * a low impact way of returning lists that will ultimately be merged together. Note that this behavior is not required and should not
 * be relied upon. After append() or prepend() both the invoker and argument should be discarded in favor of the return value.
 * @param <V> the element type
 */
public interface NodeList<V> extends Reiterable<V> {
    @SuppressWarnings("rawtypes")
    public static NodeList EMPTY = new NodeList() {
        @Override
        public NodeList next() {
            throw new UnsupportedOperationException("An empty NodeList has no next");
        }

        @Override
        public Object value() {
            throw new UnsupportedOperationException("An empty NodeList has no value");
        }

        @Override
        public NodeList tail() {
            throw new UnsupportedOperationException("An empty NodeList has no tail");
        }

        @Override
        public NodeList append(NodeList target) {
            if(target == null) {
                return this;
            }
            return target;
        }

        @Override 
        public boolean isEmpty() {
            return true;
        }
    };

    /**
     * Returns an empty NodeList.
     * @param <V> the element type
     * @return an empty NodeList
     */
    @SuppressWarnings("unchecked")
    public static <V> NodeList<V> empty() {
        return EMPTY;
    }

    default boolean isEmpty() {
        return false;
    }

    @Override
    default Iterator<V> iterator() {
        return Reiterable.loop(this, NodeList::next, NodeList::isEmpty).map(NodeList::value).iterator();
    }

    /**
     * Returns the next node in the list.
     * @return the next node, or empty() if none exists
     */
    NodeList<V> next();

    /**
     * Returns the value of this node.
     * @return the node value
     */
    V value();

    /**
     * Returns the tail of this list
     * @return the tail, potentially this node
     */
    NodeList<V> tail();

    /**
     * Prepends target to this list. Identical to target.append(this). Supplied only for convenience.
     * @param target a nodelist to append this nodelist to, possibly null
     * @return the head of the resulting list
     */
    default NodeList<V> prepend(NodeList<V> target) {
        if(target == null) {
            return this;
        }
        return target.append(this);
    }

    /**
     * Appends target to this list. Identical to target.prepend(this).
     * @param target a nodelist to append to this nodelist, possibly null
     * @return the head of the resulting list
     */
    NodeList<V> append(NodeList<V> target);

}