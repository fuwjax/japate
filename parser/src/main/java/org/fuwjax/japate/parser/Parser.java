package org.fuwjax.japate.parser;

import org.fuwjax.japate.parser.peg.PegParsing;

public interface Parser<T> {
    static Parser<Grammar> PEG = PegParsing.peg();

    T parse(String input) throws ParseException;
}
