package org.fuwjax.japate.parser.peg;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.fuwjax.japate.iteration.Reiterable;
import org.fuwjax.japate.parser.ParseException;

public class TokenStream {
    private final BreakIterator iter = BreakIterator.getCharacterInstance();
    private final String input;
    private Mark current;
    private Map<NameAt, Memo> cache = new HashMap<>();
    private List<NameAt> stack = new ArrayList<>();
    private int depth;

    public record NameAt(String name, Mark mark) {
        @Override
        public final String toString() {
            return name+" at "+mark;
        }
    }

    public TokenStream(String input) {
        this.input = input;
        iter.setText(input);
        current = new Mark(iter.first());
    }

    public Mark mark() {
        return current;
    }

    public void reset(Mark mark) {
        this.current = mark;
    }

    public ParseException error(String message ) {
        return new ParseException(current, message, stack);
    }

    public void next() throws ParseException {
        if(isDone()) {
            throw error( "attempted read past end of input");
        }
        int next = iter.following(current.pos());
        boolean newLine = current.pos() < input.length() && input.charAt(current.pos()) == '\n';
        current = current.next(next, newLine);
    }

    public String substring(Mark start) throws ParseException {
        if(isDone() || start.isDone()) {
            throw error( "attempted read past end of input");
        }
        return input.substring(start.pos(), current.pos());
    }

    public boolean at(Mark mark) {
        return mark.pos() == current.pos();
    }

    public boolean isDone() {
        return current.isDone();
    }

    public Memo fetch(Mark mark, String name){
        NameAt key = new NameAt(name, mark);
        Memo memo = cache.get(key);
        if(memo != null) {
            memo.checkRecursion(Reiterable.re(stack).until(n -> n.equals(key)).asList());
        }
        return memo;
    }

    public void removeAllMemos(Iterable<NameAt> names) {
        for(NameAt name: names) {
            cache.remove(name);
        }
    }

    public Memo enter(Mark mark, String name) {
        NameAt key = new NameAt(name, mark);
        stack.addFirst(key);
        Memo memo = new Memo(error("Left Recursion Detected"));
        cache.put(key, memo);
        return memo;
    }

    public NameAt exit() {
        if(depth == 0) {
            Iterator<Entry<NameAt, Memo>> entries = cache.entrySet().iterator();
            while(entries.hasNext()) {
                Entry<NameAt, Memo> entry = entries.next();
                if(!entry.getValue().isPending()) {
                    entries.remove();
                }
            }
        }
        return stack.removeFirst();
    }

    public void enterAlternative() {
        depth += 1;
    }

    public void exitAlternative() {
        depth -= 1;
    }
}
