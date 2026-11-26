package org.fuwjax.japate.test;

import java.util.Map;

import org.fuwjax.japate.parser.ParseException;
import org.fuwjax.japate.template.Template;
import org.fuwjax.japate.template.TemplateException;
import org.junit.jupiter.api.Test;

public class TemplateTest {
    @Test 
    public void testInterpolation() throws TemplateException, ParseException {
        When.you(Template.compile("This is plain text!").render()).expect("This is plain text!");
        When.you(Template.compile("This is (^.^) text!").render("kitty")).expect("This is kitty text!");
        When.you(Template.compile("This is (^name^) text!").render(Map.of("name", "Bob"))).expect("This is Bob text!");
        When.you(Template.compile("This is         ( ^name^) text!").render(Map.of("name", "sticky"))).expect("This issticky text!");
        When.you(Template.compile("This is         (^name^ ) text!").render(Map.of("name", "plain"))).expect("This is         plaintext!");
        When.you(Template.compile("""
            ( ^#^ )
            This is (^
            name
            ^) text!
            """).render(Map.of("name", "valid"))).expect("This is valid text!\n");
        When.you(Template.compile("""
            ( ^name^)
		    (^age^)
            """).render(Map.of("name", "Bob", "age", 123))).expect("""
            Bob
            123        
            """);
        When.you(Template.compile("""
            ( ^person.name^)
		    (^person.age^)
            """).render(Map.of("person", Map.of("name", "Bob", "age", 123)))).expect("""
            Bob
            123        
            """);
        When.you(Template.compile("""
            ( ^ person[name] ^ )
		    ( ^ person[age] ^ )
            """).render(Map.of("person", Map.of("name", "Bob", "age", 123)))).expect("Bob123");
    }
}
