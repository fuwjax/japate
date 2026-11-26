package org.fuwjax.japate.node;

/**
 * SimpleNodeList is the basic reference implementation of NodeList. It does not copy or clone on append,
 * so its use is restricted to contexts where lists are no longer needed after they're merged into another.
 * @param <V> the element type
 */
public class SimpleNodeList<V> implements NodeList<V> {
    private NodeList<V> next = NodeList.empty();
    private NodeList<V> tail = this;
    protected V value;

    public SimpleNodeList(V value) {
        this.value = value;
    }

    @Override
    public V value() {
        return value;
    }

    @Override
    public NodeList<V> next() {
        return next;
    }

    @Override
    public NodeList<V> tail() {
        if(!tail.next().isEmpty()) {
            tail = tail.next().tail();
        }
        return tail;
    }

    @Override
    public NodeList<V> append(NodeList<V> target) {
        if(target == null || target.isEmpty()) {
            return this;
        }
        if(isEmpty()) {
            return target;
        }
        NodeList<V> newTail = target.tail();
        if(newTail == this) { //identically
            throw new IllegalStateException("Cannot append target, already in list");
        }
        if(next.isEmpty()) {
            next = target;
        } else {
            tail.append(target);
        }
        tail = newTail;
        return this;
    }
}