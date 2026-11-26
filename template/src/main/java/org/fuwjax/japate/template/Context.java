package org.fuwjax.japate.template;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fuwjax.japate.incept.Incept;
import org.fuwjax.japate.multimap.MultiMap;

/**
 * Context
 */
public class Context {
    private static class Root extends Context {
        Map<String, Template> partials = new HashMap<>();
        public Root() {
            super(null, null, null);
        }

        public Template checkRegistry(String name) {
            return partials.get(name);
        }

        public void register(String name, Template partial) {
            partials.put(name, partial);
        }
    }

    public static Context seed(Object... data) {
        Context c = new Root();
        for(Object d: List.of(data).reversed()) {
            c = c.with(d);
        }
        return c;
    }

    public static Context of(Object data) {
        return new Context(null, data, null);
    }

    private static final double epsilon = 1e-20;
    public static boolean isTruthy(Object o) {
        switch (o) {
            case null : return false;
            case Integer i : return i != 0;
            case Long l : return l != 0L;
            case Double d : return d > epsilon || d < -epsilon;
            case Boolean b: return b;
            case Byte b: return b != 0;
            case Short s: return s != 0;
            case Float f: return f > epsilon || f < -epsilon;
            case String s : return s.length() > 0;
            case Collection<?> c : return !c.isEmpty();
            case Map<?, ?> m: return !m.isEmpty();
            case MultiMap<?, ?> m: return !m.isEmpty();
            case Object[] a : return a.length > 0;
            //char should always be true?
            default: return true;
        }
    }

    public final Object index;
    public final Object data;
    private final Context prev;

    private Context(Object index, Object data, Context prev) {
        this.index = index;
        this.data = data;
        this.prev = prev;
    }

    public Object resolve(Key key) throws CacheMissException {
        return key.resolve(this);
    }

    public String resolveString(Key key) throws TemplateException {
        Object data = resolve(key);
        if(!isTruthy(data)) {
            return "";
        }
        if (data instanceof Template t) {
            return t.render(this);
        }
        return String.valueOf(data);
    }

    public String resolveName(Key key) throws TemplateException {
        try {
            return resolveString(key);
        } catch(CacheMissException e) {
            return key.resolveName();
        }
    }

    public Context with(Object data) {
        return new Context(null, data, this);
    }

    public Context with(Object index, Object data) {
        return new Context(index, data, this);
    }

    public Template checkRegistry(String name) {
        return prev.checkRegistry(name);
    }

    public void register(String name, Template partial) {
        prev.register(name, partial);
    }

    private static final int toInt(Object o) throws NumberFormatException {
        switch(o) {
            case Integer i:
                return i;
            default:
                return Integer.parseInt(String.valueOf(o));
        }
    }

    public Object get(Object key) throws CacheMissException {
        try {
            switch(data) {
                case null: break;
                case Map<?, ?> m:
                    if(m.containsKey(key)) {
                        return m.get(key);            
                    }
                    break;
                case MultiMap<?, ?> m:
                    if(m.contains(key)) {
                        return m.getAll(key);
                    }
                    break;
                case List<?> l:
                    int index = toInt(key);
                    if(index >= 0 && index <= l.size()) {
                        return l.get(index);
                    }
                    break;
                case Object[] a:
                    index = toInt(key);
                    if(index >= 0 && index <= a.length) {
                        return a[index];
                    }
                    break;
                default:
                    return Incept.of(data).getAction(String.valueOf(key)).perform();
            }
        } catch (Exception e) {
            // continue
        }
        if(prev == null) {
            throw new CacheMissException(key);
        }
        return prev.get(key);
    }
}
