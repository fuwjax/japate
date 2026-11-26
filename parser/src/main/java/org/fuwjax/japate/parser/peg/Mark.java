package org.fuwjax.japate.parser.peg;

import java.text.BreakIterator;

public record Mark(int pos, int line, int column) {
    public Mark(int start) {
        this(start, 1, 1);
    }

    public Mark next(int pos, boolean newLine) {
        if(pos == BreakIterator.DONE) {
            return new Mark(pos, line, column);
        } else if(newLine) {
            return new Mark(pos, line + 1, 1);
        }
        return new Mark(pos, line, column + 1);
    }

    public boolean isDone() {
        return pos == BreakIterator.DONE;
    }

    public String toString(){
        if(isDone()) {
            return line+":"+column+" [EOF]";
        }
        return line+":"+column+" ["+pos+"]";
    }
}
