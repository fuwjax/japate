package org.fuwjax.japate.iteration.iterator;

import java.text.BreakIterator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class BoundaryIterator implements Iterator<String> {
    private int start;
    private final BreakIterator iter;
    private final String input;
    private boolean ready = false;

    public BoundaryIterator(BreakIterator iter, String input) {
        this.input = input;
        this.iter = iter;
        iter.setText(input);
        start = iter.first(); // first text boundary should be the "zero" of the input
    }

    @Override
    public final boolean hasNext() {
        if(!ready && iter.current() != BreakIterator.DONE) {
            start = iter.current();
            ready = iter.next() != BreakIterator.DONE;
        }
        return ready;
    }

    @Override
    public final String next() {
        if(!hasNext()) {
            throw new NoSuchElementException();
        }
        ready = false;
        return input.substring(start, iter.current());
    }
}