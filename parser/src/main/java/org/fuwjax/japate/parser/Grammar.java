package org.fuwjax.japate.parser;

import java.util.function.Supplier;

/**
 * Grammar
 */
public interface Grammar {
    public Grammar bind(Supplier<ParseResolver> resolvers);

    public <T> Parser<T> parser(String root, Class<T> type);
}
