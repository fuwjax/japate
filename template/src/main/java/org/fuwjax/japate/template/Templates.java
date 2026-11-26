package org.fuwjax.japate.template;

import static org.fuwjax.japate.iteration.Reiterable.re;
import static org.fuwjax.japate.template.Context.isTruthy;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.fuwjax.japate.iteration.Reiterable;
import org.fuwjax.japate.multimap.MultiMap;

public class Templates {
    private Templates() {}

    public static record Content(Iterable<Template> snippets) implements Template {
        @Override
        public String render(Context context) throws TemplateException {
            StringBuilder s = new StringBuilder();
            for (Template t: snippets) {
                String text = t.render(context);
                s.append(text);
            }
            return s.toString();
        }

        @Override
        public int hashCode() {
            return Objects.hash(re(snippets).asList());
        }

        @Override
        public final boolean equals(Object obj) {
            if (this == obj){
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Content other = (Content)obj;
            return re(snippets).asList().equals(re(other.snippets).asList());
        }
    }

    public static record Include(Key key) implements Template {
        @Override
        public String render(Context context) throws TemplateException {
            String name = context.resolveName(key);
            Template t = context.checkRegistry(name);
            if (t == null) {
                throw new TemplateException("No template named "+name+" for "+ key+ " found in context");
            }
            return t.render(context);
        }
    }

    public static record Invert(Key key, Template content) implements Template {
        @Override
        public String render(Context context) throws TemplateException {
            try{
                Object data = context.resolve(key);
                if(isTruthy(data)) {
                    return "";
                }
            } catch(CacheMissException e) {
                // continue
            }
            return content.render(context);
        }        
    }

    public static record Partial(Key key, Template content) implements Template {
        @Override
        public String render(Context context) throws TemplateException {
            String name = context.resolveName(key);
            context.register(name, content);
            return "";
        }
    }

    public static record Plaintext(String text) implements Template {
        @Override
        public String render(Context context) throws TemplateException {
            return text;
        }
    }

    public static record Reference(Key key) implements Template {
        @Override
        public String render(Context context) throws TemplateException {
            return context.resolveString(key);
        }
    }

    protected static Reiterable<? extends Entry<?, ?>> container(Object data) {
        final int[] index = new int[1];
        switch(data) {
            case Map<?, ?> map : return Reiterable.re(map.entrySet());
            case MultiMap<?, ?> map: return map.entries();
            case Iterable<?> iter: return Reiterable.re(iter).map(v -> new SimpleImmutableEntry<>(index[0]++, v)); // no way to handle Iterable<Entry> yet
            case Object[] array: return Reiterable.of(array).map(v -> new SimpleImmutableEntry<>(index[0]++, v));
            default:
                // this exception shouldn't leave this class
                throw new UnsupportedOperationException("cannot convert data of type " + data.getClass() + " to Iterable<Entry>");
        }
    }

    public static record Section(Key key, Template content) implements Template {
        @Override
        public String render(Context context) throws TemplateException {
            Object data = context.resolve(key);
            try {
                Reiterable<? extends Entry<?, ?>> iter = container(data);
                StringBuilder builder = new StringBuilder(); // can't use join because of TemplateException
                for(Entry<?, ?> entry: iter) {
                    String result = content.render(context.with(entry.getKey(), entry.getValue()));
                    builder.append(result);
                }
                return builder.toString();
            } catch(UnsupportedOperationException e) {
                if(!isTruthy(data)) {
                    return "";
                }
                return content.render(context.with(data));
            }
        }
    }
}
