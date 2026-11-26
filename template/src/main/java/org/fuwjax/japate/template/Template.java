package org.fuwjax.japate.template;

import org.fuwjax.japate.iteration.Reiterable;
import org.fuwjax.japate.parser.ParseException;
import org.fuwjax.japate.template.Templates.Content;
import org.fuwjax.japate.template.Templates.Include;
import org.fuwjax.japate.template.Templates.Invert;
import org.fuwjax.japate.template.Templates.Partial;
import org.fuwjax.japate.template.Templates.Plaintext;
import org.fuwjax.japate.template.Templates.Reference;
import org.fuwjax.japate.template.Templates.Section;

/**
 * Template
 */
public interface Template {
    static Template compile(String template) throws ParseException {
        return Compiler.compile(template);
    }

    String render(Context context) throws TemplateException;

    default String render(Object... context) throws TemplateException {
        return render(Context.seed(context));
    }

    static Template content(Template... snippets) {
        return content(Reiterable.of(snippets));
    }

    static Template content(Iterable<Template> snippets) {
        return new Content(snippets);
    }

    static Template include(Key key) {
        return new Include(key);
    }

    static Template invert(Key key, Template content) {
        return new Invert(key, content);
    }

    static Template partial(Key key, Template content) {
        return new Partial(key, content);
    }

    static Template plaintext(String text){
        return new Plaintext(text);
    }

    static Template reference(Key key) {
        return new Reference(key);
    }

    static Template section(Key key, Template content) {
        return new Section(key, content);
    }
}
