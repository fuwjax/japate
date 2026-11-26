package org.fuwjax.japate.parser;

import static org.fuwjax.japate.iteration.Reiterable.re;

import org.fuwjax.japate.parser.peg.Mark;
import org.fuwjax.japate.parser.peg.TokenStream.NameAt;

public class ParseException extends Exception {
    private final Mark mark;

    public ParseException(Mark mark, String message, Iterable<NameAt> stack) {
        super(message + " at " + mark + re(stack).map(na -> "\n\twhile in "+na).join());
        this.mark = mark;
    }

    public boolean isFurther(ParseException other) {
        return other == null || other.mark.pos() < mark.pos();
    }
}
