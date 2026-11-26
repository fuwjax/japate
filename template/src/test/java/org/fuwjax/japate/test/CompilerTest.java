package org.fuwjax.japate.test;

import org.fuwjax.japate.parser.ParseException;
import org.fuwjax.japate.parser.Parser;
import org.fuwjax.japate.template.Compiler;
import org.fuwjax.japate.template.Key;
import org.fuwjax.japate.template.Template;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CompilerTest {
    @Test 
    public void testCompileKeyName() throws ParseException {
        Parser<Key> parser = Compiler.parserFrom("KeyName", Key.class);

        When.you(parser.parse("\"name\"")).expect(Key.lit("name"));
        When.you(parser.parse("name")).expect(Key.bracket("name"));
        When.you(parser.parse("person.name")).expect(Key.dot(Key.bracket("person"), Key.bracket("name")));
        When.you(parser.parse("person[name]")).expect(Key.bracket("person", Key.bracket("name")));
        When.you(parser.parse("cities[\"Europe\"][\"Amsterdam\"].people.0.address[division.name].upper[.]"))
                .expect(Key.dot(
                    Key.bracket("cities", Key.lit("Europe"), Key.lit("Amsterdam")),
                    Key.bracket("people"),
                    Key.bracket("0"),
                    Key.bracket("address", Key.dot(Key.bracket("division"), Key.bracket("name"))),
                    Key.bracket("upper", Key.self())));
    }

    @Test 
    public void testCompileTags() throws ParseException {
        Parser<Template> parser = Compiler.parserFrom("Tag", Template.class);
        
        When.you(parser.parse("(^*name^)(^.^)(^/^)")).expect(Template.section(Key.bracket("name"), Template.content(Template.reference(Key.self()))));
        When.you(parser.parse("(^# a comment^)")).expect(Assertions::assertNull);
        When.you(parser.parse("(^name^)")).expect(Template.reference(Key.bracket("name")));
        When.you(parser.parse("    ( ^name^ )      ")).expect(Template.reference(Key.bracket("name")));
        When.you(parser.parse("(^!name^)miss you!(^/^)")).expect(Template.invert(Key.bracket("name"), Template.content(Template.plaintext("miss you!"))));
        When.you(parser.parse("(^>name^)")).expect(Template.include(Key.bracket("name")));
    }
}
