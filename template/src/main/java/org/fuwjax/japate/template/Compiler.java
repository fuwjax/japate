package org.fuwjax.japate.template;

import static org.fuwjax.japate.template.Template.content;
import static org.fuwjax.japate.template.Template.include;
import static org.fuwjax.japate.template.Template.invert;
import static org.fuwjax.japate.template.Template.partial;
import static org.fuwjax.japate.template.Template.plaintext;
import static org.fuwjax.japate.template.Template.reference;
import static org.fuwjax.japate.template.Template.section;

import org.fuwjax.japate.iteration.Reiterable;
import org.fuwjax.japate.parser.ParseException;
import org.fuwjax.japate.parser.ParseResolver;
import org.fuwjax.japate.parser.ParseResult;
import org.fuwjax.japate.parser.Parser;
import org.fuwjax.japate.parser.Result;

public class Compiler implements ParseResolver {
    private static String spec = """
Template = Content EOF
Content = (Plain? Tag)* Plain?
Tag = Open (Comment / Value / Include / Section / Else / Partial) Close

Comment = '#' Text?
Value = Key
Include = '>' KeyName
Section = '*' Key Close Content Open '/' Text?
Else = '!' Key Close Content Open '/' Text?
Partial = '=' KeyName Close Content Open '/' Text? 

KeyName = Key / Name
Name = WS '"' String '"' WS
Key = WS (Dotted / Dot / At) WS
Dotted = Inter ('.' Inter)*
Inter = Bracket / Ref
Bracket = Ident ('[' KeyName ']')+
Ref = Ident
Dot = '.'
At = '@'

Ident = [_a-zA-Z0-9]+
Plain = (!Open .)+
Text = (!Close .)+
String = [^"]*

Open = '(^' / WS '( ^'
Close = '^)' / '^ )' WS
WS = [ \\t\\r\\n]*
EOF = !.
            """;
    private static org.fuwjax.japate.parser.Grammar grammar;
    static {
        try{
            grammar = Parser.PEG.parse(spec).bind(Compiler::new);
        } catch (ParseException e) {
            throw new IllegalStateException("Compiler spec failed to parse", e);
        }
    }
    private static Parser<Template> parser = parserFrom("Template", Template.class);

    public static <T> Parser<T> parserFrom(String start, Class<T> type) {
        return grammar.parser(start, type);
    }

    public static Template compile(String template) throws ParseException {
        return parser.parse(template);
    }

    public Object resolve(String name, ParseResult result) {
        return switch(name) {
            case "Template" -> Template(result);
            case "Content" -> Content(result);
            case "Tag" -> Tag(result);
            case "Comment" -> null;
            case "Value" -> Value(result);
            case "Include" -> Include(result);
            case "Section" -> Section(result);
            case "Else" -> Else(result);
            case "Partial" -> Partial(result);
            case "KeyName" -> KeyName(result);
            case "Name" -> Name(result);
            case "Key" -> Key(result);
            case "Dotted" -> Dotted(result);
            case "Inter" -> Inter(result);
            case "Bracket" -> Bracket(result);
            case "Ref" -> Ref(result);
            case "Dot" -> Dot(result);
            case "At" -> At(result);
            case "Ident" -> result.join();
            case "Plain" -> Plain(result);
            case "Text" -> null; // comment text
            case "String" -> result.join();
            case "Open" -> null;
            case "Close" -> null;
            case "WS" -> null;
            case "EOF" -> null;
            default -> throw new IllegalArgumentException("No handler for "+ name);
        };
    }

    public Template Template(ParseResult result) {
        return result.getFirst("Content").map(Template.class::cast).get();
    }

    public Template Content(ParseResult result) {
        Iterable<Template> snippets = result.getAny("Plain", "Tag").map(Result::value).filter(o -> o != null).as(Template.class);
        return content(snippets);
    }

    public Template Plain(ParseResult result) {
        String text = result.values().join();
        return plaintext(text);
    }

    public Template Tag(ParseResult result) {
        return result.getAny("Value", "Else", "Include", "Section", "Partial").map(Result::value).map(Template.class::cast).first().orElse(null);
    }

    public Template Value(ParseResult result) {
        Key key = result.getFirst("Key").map(Key.class::cast).get();
        return reference(key);
    }

    public Template Include(ParseResult result) {
        Key key = result.getFirst("KeyName").map(Key.class::cast).get();
        return include(key);
    }

    public Template Section(ParseResult result) {
        Key key = result.getFirst("Key").map(Key.class::cast).get();
        Template content = result.getFirst("Content").map(Template.class::cast).get();
        return section(key, content);
    }

    public Template Else(ParseResult result) {
        Key key = result.getFirst("Key").map(Key.class::cast).get();
        Template content = result.getFirst("Content").map(Template.class::cast).get();
        return invert(key, content);
    }

    public Template Partial(ParseResult result) {
        Key key = result.getFirst("KeyName").map(Key.class::cast).get();
        Template content = result.getFirst("Content").map(Template.class::cast).get();
        return partial(key, content);
    }

    public Key KeyName(ParseResult result) {
        return result.getAny("Key", "Name").map(Result::value).map(Key.class::cast).first().get();
    }

    public Key Name(ParseResult result) {
        String value = result.getFirst("String").map(String.class::cast).get();
        return Key.lit(value);
    }

    public Key Key(ParseResult result) {
        return result.getAnyFirst("Dotted", "Dot", "At").map(Key.class::cast).get();
    }

    public Key Dotted(ParseResult result) {
        Reiterable<Key> brackets = result.getAll("Inter").map(Key.class::cast);
        return Key.dot(brackets);
    }

    public Key Inter(ParseResult result) {
        return result.getAnyFirst("Bracket", "Ref").map(Key.class::cast).get();
    }

    public Key Dot(ParseResult result) {
        return Key.self();
    }

    public Key At(ParseResult result) {
        return Key.index();
    }

    public Key Bracket(ParseResult result) {
        String name = result.getFirst("Ident").map(String.class::cast).get();
        Reiterable<Key> args = result.getAll("KeyName").map(Key.class::cast);
        return Key.bracket(name, args);
    }

    public Key Ref(ParseResult result) {
        String name = result.getFirst("Ident").map(String.class::cast).get();
        return Key.ref(name);
    }
}
