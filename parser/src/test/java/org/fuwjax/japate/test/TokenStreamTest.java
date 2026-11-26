package org.fuwjax.japate.test;

import org.fuwjax.japate.parser.ParseException;
import org.fuwjax.japate.parser.peg.Mark;
import org.fuwjax.japate.parser.peg.TokenStream;
import org.junit.jupiter.api.Test;

public class TokenStreamTest {
    private String accept(TokenStream stream) throws ParseException {
        Mark mark = stream.mark();
        stream.next();
        return stream.substring(mark);
    }

    @Test
    public void testFlow() throws ParseException {
        TokenStream stream = new TokenStream("abc");
        When.you(accept(stream)).expect("a");
        When.you(accept(stream)).expect("b");
        When.you(accept(stream)).expect("c");
        When.youDo(() -> accept(stream)).expectFailure(ParseException.class).withMessage("attempted read past end of input at 1:4 [EOF]");
        When.youDo(() -> accept(stream)).expectFailure(ParseException.class).withMessage("attempted read past end of input at 1:4 [EOF]");
    }

    @Test
    public void testMark() throws ParseException {
        TokenStream stream = new TokenStream("abc");
        Mark start = stream.mark();
        When.you(accept(stream)).expect("a");
        stream.reset(start);
        When.you(stream.mark()).expect(start);
        When.you(accept(stream)).expect("a");
        Mark first = stream.mark();
        When.you(accept(stream)).expect("b");
        stream.reset(first);
        When.you(accept(stream)).expect("b");
        stream.reset(start);
        When.you(accept(stream)).expect("a");
        //When.you(stream.mark()).expect(first); // no longer true
        When.you(accept(stream)).expect("b");
        When.you(accept(stream)).expect("c");
        // the mark is now the boundary at the end of the input, but not the actual end of the input. This allows !. to work as expected.
        When.you(stream.mark().isDone()).expect(false); 
        When.youDo(() -> accept(stream)).expectFailure(ParseException.class).withMessage("attempted read past end of input at 1:4 [EOF]");
        When.you(stream.mark().isDone()).expect(true);
        When.youDo(() -> accept(stream)).expectFailure(ParseException.class).withMessage("attempted read past end of input at 1:4 [EOF]");
        When.you(stream.mark().isDone()).expect(true);
        stream.reset(first);
        When.you(accept(stream)).expect("b");
        When.you(accept(stream)).expect("c");
        When.you(stream.mark().isDone()).expect(false); 
        When.youDo(() -> accept(stream)).expectFailure(ParseException.class).withMessage("attempted read past end of input at 1:4 [EOF]");
        When.you(stream.mark().isDone()).expect(true);
    }
}
