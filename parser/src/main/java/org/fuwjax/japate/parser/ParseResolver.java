package org.fuwjax.japate.parser;

public interface ParseResolver {
    Object resolve(String name, ParseResult results);
}
