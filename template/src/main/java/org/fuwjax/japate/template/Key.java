package org.fuwjax.japate.template;

import org.fuwjax.japate.iteration.Reiterable;
import org.fuwjax.japate.template.Keys.BracketKey;
import org.fuwjax.japate.template.Keys.DotKey;
import org.fuwjax.japate.template.Keys.IndexKey;
import org.fuwjax.japate.template.Keys.LitKey;
import org.fuwjax.japate.template.Keys.RefKey;
import org.fuwjax.japate.template.Keys.SelfKey;

/**
 * Key
 */
public interface Key {
    Object resolve(Context context) throws CacheMissException;

    default String resolveName() throws CacheMissException {
        throw new CacheMissException(this);
    }

    static Key lit(String name) {
        return new LitKey(name);
    }

    static Key self() {
        return new SelfKey();
    }

    static Key index() {
        return new IndexKey();
    }

    static Key dot(Key... brackets) {
        return dot(Reiterable.of(brackets));
    }

    static Key dot(Reiterable<Key> brackets) {
        if(brackets.count() == 1) {
            return brackets.first().get();
        }
        return new DotKey(brackets);
    }

    static Key bracket(String name, Key... args) {
        return bracket(name, Reiterable.of(args));
    }

    static Key ref(String name) {
        return new RefKey(name);
    }

    static Key bracket(String name, Reiterable<Key> args) {
        if(args.isEmpty()) {
            return new RefKey(name);
        }
        return new BracketKey(name, args);
    }
}
