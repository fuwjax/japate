package org.fuwjax.japate.template;

import static org.fuwjax.japate.iteration.Reiterable.re;

import java.util.Objects;

/**
 * Key
 */
public class Keys {
    private Keys() {}

    public static record LitKey(String name) implements Key {
        @Override
        public Object resolve(Context context) throws CacheMissException {
            return name;
        }

        @Override
        public String resolveName() {
            return name;
        }
    }

    public static record SelfKey() implements Key {
        @Override
        public Object resolve(Context context) throws CacheMissException {
            return context.data;
        }            
    }

    public static record IndexKey() implements Key {
        @Override
        public Object resolve(Context context) throws CacheMissException {
            return context.index;
        }            
    }

    public static record DotKey(Iterable<Key> brackets) implements Key{
        @Override
        public Object resolve(Context context) throws CacheMissException {
            Context current = context;
            Object data = null;
            for(Key bracket: brackets) {
                data = current.resolve(bracket);
                current = Context.of(data);
            }
            return data;
        }

        @Override
        public int hashCode() {
            return Objects.hash(re(brackets).asList());
        }

        @Override
        public final boolean equals(Object obj) {
            if (this == obj){
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            DotKey other = (DotKey)obj;
            return re(brackets).asList().equals(re(other.brackets).asList());
        }
    }

    public static record RefKey(String name) implements Key {
        @Override
        public Object resolve(Context context) throws CacheMissException {
            return context.get(name);
        }

        @Override 
        public String resolveName() {
            return name;
        }
    }

    public static record BracketKey(String name, Iterable<Key> args) implements Key {
        @Override
        public Object resolve(Context context) throws CacheMissException {
            Object data = context.get(name);
            for(Key arg: args) {
                Object key;
                try {
                    // this context is intentionally fixed
                    key = context.resolve(arg);
                }catch(CacheMissException e) {
                    key = arg.resolveName();
                }
                // and this one is intentionally new
                data = Context.of(data).get(key);
            }
            return data;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, re(args).asList());
        }

        @Override
        public final boolean equals(Object obj) {
            if (this == obj){
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            BracketKey other = (BracketKey)obj;
            return Objects.equals(name, other.name) && re(args).asList().equals(re(other.args).asList());
        }
    }
}
