package org.fuwjax.japate.test;

import java.util.List;
import java.util.Map;

import org.fuwjax.japate.parser.ParseException;
import org.fuwjax.japate.parser.Parser;
import org.fuwjax.japate.template.CacheMissException;
import org.fuwjax.japate.template.Compiler;
import org.fuwjax.japate.template.Context;
import org.fuwjax.japate.template.Key;
import org.fuwjax.japate.template.TemplateException;
import org.junit.jupiter.api.Test;

public class ResolveTest {
    private static Parser<Key> keyParser = Compiler.parserFrom("KeyName", Key.class);
    static Key key(String keyText) throws ParseException {
        return keyParser.parse(keyText);
    }

    @Test 
    public void testResolve() throws CacheMissException, ParseException {
        When.you(Context.seed(Map.of("name", "Bob")).resolve(key("name"))).expect("Bob");
        When.youDo(() -> Context.seed(Map.of()).resolve(key("name"))).expectFailure(CacheMissException.class).withMessage( "name");
        When.you(Context.seed(Map.of("name", "Bob")).resolve(key("\"name\""))).expect("name");
        When.you(Context.seed(Map.of("name", "Bob"), Map.of("name", "Jim")).resolve(key("name"))).expect("Bob");
        When.you(Context.seed(Map.of("first_name", "Bob"), Map.of("name", "Jim")).resolve(key("name"))).expect("Jim");
        When.you(Context.seed(Map.of("name", "Bob")).resolve(key("."))).expect(Map.of("name", "Bob"));
        When.you(Context.seed(Map.of("person", Map.of("name", "Bob"))).resolve(key("person.name"))).expect("Bob");
        When.you(Context.seed(Map.of("person", Map.of("name", "Bob"))).resolve(key("person[name]"))).expect("Bob");
        When.you(Context.seed(Map.of("person", Map.of("name", "Bob"))).resolve(key("person[\"name\"]"))).expect("Bob");
        When.you(Context.seed(Map.of("name", "last_name", "person", Map.of("name", "Bob", "last_name", "Hope"))).resolve(key("person[name]"))).expect("Hope");
        When.you(Context.seed(Map.of("name", "last_name", "person", Map.of("name", "Bob", "last_name", "Hope"))).resolve(key("person[\"name\"]"))).expect("Bob");
    }

    @Test 
    public void testResolveName() throws TemplateException, ParseException {
        When.you(Context.seed(Map.of("name", "Bob")).resolveName(key("name"))).expect("Bob");
        When.youDo(() -> Context.seed(Map.of()).resolveName(key("name"))).expect("name");
        When.you(Context.seed(Map.of("name", "Bob")).resolveName(key("\"name\""))).expect("name");
        When.you(Context.seed(Map.of("name", "Bob"), Map.of("name", "Jim")).resolveName(key("name"))).expect("Bob");
        When.you(Context.seed(Map.of("first_name", "Bob"), Map.of("name", "Jim")).resolveName(key("name"))).expect("Jim");
        When.you(Context.seed(Map.of("name", "Bob")).resolveName(key("."))).expect("{name=Bob}");
        When.you(Context.seed(Map.of("person", Map.of("name", "Bob"))).resolveName(key("person.name"))).expect("Bob");
        When.you(Context.seed(Map.of("person", Map.of("name", "Bob"))).resolveName(key("person[name]"))).expect("Bob");
        When.you(Context.seed(Map.of("person", Map.of("name", "Bob"))).resolveName(key("person[\"name\"]"))).expect("Bob");
        When.you(Context.seed(Map.of("name", "last_name", "person", Map.of("name", "Bob", "last_name", "Hope"))).resolveName(key("person.name"))).expect("Bob");
        When.you(Context.seed(Map.of("name", "last_name", "person", Map.of("name", "Bob", "last_name", "Hope"))).resolveName(key("person[name]"))).expect("Hope");
        When.you(Context.seed(Map.of("name", "last_name", "person", Map.of("name", "Bob", "last_name", "Hope"))).resolveName(key("person[\"name\"]"))).expect("Bob");
    }

    public static record Person(String name, int age){}

    @Test 
    public void testResolveInterestingContext() throws CacheMissException, ParseException {
        Context context = Context.seed(Map.of("people", List.of(new Person("Bob", 123), new Person("Jim", 456)),
                                              "cities", Map.of("Europe", "Antwerp", "NorthAmerica", "Chicago")),
                                    new Person("Bill", 789));
        When.you(context.resolve(key("name"))).expect("Bill");
        When.you(context.resolve(key("people.0.name"))).expect("Bob");
        When.you(context.resolve(key("people[1].age"))).expect(456);
    }
}
