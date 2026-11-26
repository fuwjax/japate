package org.fuwjax.japate.test;

import static org.fuwjax.japate.parser.peg.Expression.Alt;
import static org.fuwjax.japate.parser.peg.Expression.Cls;
import static org.fuwjax.japate.parser.peg.Expression.Dot;
import static org.fuwjax.japate.parser.peg.Expression.Lit;
import static org.fuwjax.japate.parser.peg.Expression.Not;
import static org.fuwjax.japate.parser.peg.Expression.Opt;
import static org.fuwjax.japate.parser.peg.Expression.Rep;
import static org.fuwjax.japate.parser.peg.Expression.Req;
import static org.fuwjax.japate.parser.peg.Expression.See;
import static org.fuwjax.japate.parser.peg.Expression.Seq;

import org.fuwjax.japate.parser.ParseException;
import org.fuwjax.japate.parser.peg.Expression;
import org.fuwjax.japate.parser.peg.PegGrammar;
import org.fuwjax.japate.parser.peg.PegParsing;
import org.junit.jupiter.api.Test;

public class BootstrapTest {
    private static PegGrammar fake = new PegGrammar();

    // This may not always work. In theory a valid equals for PegGrammar.Reference should include the grammar itself, but that
    // would cause a circular dependency on equals, as grammar's equals depends on the expressions, some of which will be refs.
    private static Expression Ref(String name) {
        return fake.ref(name);
    }

    public <T> T testParse(String root, String input, Class<T> type) throws ParseException {
        return PegParsing.pegFrom(root, type).parse(input);
    }

    @Test
    public void testBootstrap() {
        When.youDo(() -> testParse("EOF", "", String.class)).expectNull();
        When.youDo(() -> testParse("EOF", "a", String.class))
            .expectFailure(ParseException.class).withMessage("found an unexpected . at 1:1 [0]\n\twhile in EOF at 1:1 [0]");
        When.youDo(() -> testParse("EOL", "\n", String.class)).expectNull();
        When.youDo(() -> testParse("EOL", "a", String.class))
            .expectFailure(ParseException.class).withMessage("Regex [\\n\\r] does not match \"a\" at 1:1 [0]\n\twhile in EOL at 1:1 [0]");
        When.youDo(() -> testParse("WS", "   ", String.class)).expectNull();
        When.youDo(() -> testParse("WS", " a ", String.class)).expectNull();
        When.youDo(() -> testParse("WS", "a ", String.class)).expectNull();
        When.youDo(() -> testParse("Name", "bob", String.class)).expect("bob");
        When.youDo(() -> testParse("Name", "B0B ross", String.class)).expect("B0B");
        When.youDo(() -> testParse("Name", " bob", String.class))
            .expectFailure(ParseException.class).withMessage("Regex [_a-zA-Z] does not match \" \" at 1:1 [0]\n\twhile in Name at 1:1 [0]");
        When.youDo(() -> testParse("Name", "1234", String.class))
            .expectFailure(ParseException.class).withMessage("Regex [_a-zA-Z] does not match \"1\" at 1:1 [0]\n\twhile in Name at 1:1 [0]");
        When.youDo(() -> testParse("Pattern", "[a-z]", String.class)).expect("[a-z]");
        When.youDo(() -> testParse("Pattern", "[\\\"]", String.class)).expect("[\\\"]");
        When.youDo(() -> testParse("Pattern", "[\"]", String.class)).expect("[\"]");
        When.youDo(() -> testParse("Pattern", "[^\\n]", String.class)).expect("[^\\n]");
        When.youDo(() -> testParse("Pattern", "[]]", String.class))
            .expectFailure(ParseException.class).withMessage("Expected \"\\\\\\]\", but found \"]\" at 1:2 [1]\n\twhile in Pattern at 1:1 [0]");
        When.youDo(() -> testParse("Pattern", "1234", String.class))
            .expectFailure(ParseException.class).withMessage("Expected \"[\", but found \"1\" at 1:1 [0]\n\twhile in Pattern at 1:1 [0]");
        When.youDo(() -> testParse("Comment", "#comment", String.class)).expectNull();
        When.youDo(() -> testParse("Comment", "#", String.class)).expectNull();
        When.youDo(() -> testParse("Comment", "not a comment", String.class))
            .expectFailure(ParseException.class).withMessage("Expected \"#\", but found \"n\" at 1:1 [0]\n\twhile in Comment at 1:1 [0]");
        When.youDo(() -> testParse("Reference", "bob", Expression.class)).expect(Ref("bob"));
        When.youDo(() -> testParse("Reference", "1234", Expression.class))
            .expectFailure(ParseException.class).withMessage("Regex [_a-zA-Z] does not match \"1\" at 1:1 [0]\n\twhile in Name at 1:1 [0]\n\twhile in Reference at 1:1 [0]");
        When.youDo(() -> testParse("CharClass", "[a-z]", Expression.class)).expect(Cls("[a-z]"));
        When.youDo(() -> testParse("CharClass", "1234", Expression.class))
            .expectFailure(ParseException.class).withMessage("Expected \"[\", but found \"1\" at 1:1 [0]\n\twhile in Pattern at 1:1 [0]\n\twhile in CharClass at 1:1 [0]");
        When.youDo(() -> testParse("Literal", "\"hello, world\"", Expression.class)).expect(Lit("hello, world"));
        When.youDo(() -> testParse("Literal", "'hello, world'", Expression.class)).expect(Lit("hello, world"));
        When.youDo(() -> testParse("Literal", "1234", Expression.class))
            .expectFailure(ParseException.class).withMessage("Expected \"'\", but found \"1\" at 1:1 [0]\n\twhile in Literal at 1:1 [0]");
        When.youDo(() -> testParse("Literal", "'\\\\'", Expression.class)).expect(Lit("\\"));
        When.youDo(() -> testParse("AnyExpr", ".", Expression.class)).expect(Dot());
        When.youDo(() -> testParse("AnyExpr", "1234", Expression.class))
            .expectFailure(ParseException.class).withMessage("Expected \".\", but found \"1\" at 1:1 [0]\n\twhile in AnyExpr at 1:1 [0]");
        When.youDo(() -> testParse("Primary", ".", Expression.class)).expect(Dot());
        When.youDo(() -> testParse("Primary", "\"double\"", Expression.class)).expect(Lit("double"));
        When.youDo(() -> testParse("Primary", "'single'", Expression.class)).expect(Lit("single"));
        When.youDo(() -> testParse("Primary", "[^\"]", Expression.class)).expect(Cls("[^\"]"));
        When.youDo(() -> testParse("Primary", "RefName", Expression.class)).expect(Ref("RefName"));
        When.youDo(() -> testParse("ReqExpr", "[0-9]+", Expression.class)).expect(Req(Cls("[0-9]")));
        When.youDo(() -> testParse("ReqExpr", "'hi'  +", Expression.class)).expect(Req(Lit("hi")));
        When.youDo(() -> testParse("ReqExpr", "Bob", Expression.class))
            .expectFailure(ParseException.class).withMessage("Expected \"+\" at 1:4 [3]\n\twhile in ReqExpr at 1:1 [0]");
        When.youDo(() -> testParse("RepExpr", "\"yup\"*", Expression.class)).expect(Rep(Lit("yup")));
        When.youDo(() -> testParse("RepExpr", ".  *", Expression.class)).expect(Rep(Dot()));
        When.youDo(() -> testParse("RepExpr", "Bob", Expression.class))
            .expectFailure(ParseException.class).withMessage("Expected \"*\" at 1:4 [3]\n\twhile in RepExpr at 1:1 [0]");
        When.youDo(() -> testParse("OptExpr", "RefName?", Expression.class)).expect(Opt(Ref("RefName")));
        When.youDo(() -> testParse("OptExpr", ".  ?", Expression.class)).expect(Opt(Dot()));
        When.youDo(() -> testParse("OptExpr", "Bob", Expression.class))
            .expectFailure(ParseException.class).withMessage("Expected \"?\" at 1:4 [3]\n\twhile in OptExpr at 1:1 [0]");
        When.youDo(() -> testParse("Suffix", ".+", Expression.class)).expect(Req(Dot()));
        When.youDo(() -> testParse("Suffix", "\"double\" *", Expression.class)).expect(Rep(Lit("double")));
        When.youDo(() -> testParse("Suffix", "[^\"]?", Expression.class)).expect(Opt(Cls("[^\"]")));
        When.youDo(() -> testParse("Suffix", "Bob", Expression.class)).expect(Ref("Bob"));
        When.youDo(() -> testParse("NotExpr", "!RefName", Expression.class)).expect(Not(Ref("RefName")));
        When.youDo(() -> testParse("NotExpr", "!  .  ?", Expression.class)).expect(Not(Opt(Dot())));
        When.youDo(() -> testParse("NotExpr", "Bob", Expression.class))
            .expectFailure(ParseException.class).withMessage("Expected \"!\", but found \"B\" at 1:1 [0]\n\twhile in NotExpr at 1:1 [0]");
        When.youDo(() -> testParse("AndExpr", "&RefName", Expression.class)).expect(See(Ref("RefName")));
        When.youDo(() -> testParse("AndExpr", "&  .  ?", Expression.class)).expect(See(Opt(Dot())));
        When.youDo(() -> testParse("AndExpr", "Bob", Expression.class))
            .expectFailure(ParseException.class).withMessage("Expected \"&\", but found \"B\" at 1:1 [0]\n\twhile in AndExpr at 1:1 [0]");
        When.youDo(() -> testParse("Prefix", "!.", Expression.class)).expect(Not(Dot()));
        When.youDo(() -> testParse("Prefix", "& \"double\" *", Expression.class)).expect(See(Rep(Lit("double"))));
        When.youDo(() -> testParse("Prefix", "[^\"]", Expression.class)).expect(Cls("[^\"]"));
        When.youDo(() -> testParse("Sequence", "A B C", Expression.class)).expect(Seq(Ref("A"), Ref("B"), Ref("C")));
        When.youDo(() -> testParse("Sequence", ". 'hi' [a-z]", Expression.class)).expect(Seq(Dot(), Lit("hi"), Cls("[a-z]")));
        When.youDo(() -> testParse("Sequence", "Jim", Expression.class)).expect(Ref("Jim"));
        When.youDo(() -> testParse("Expr", "A / B / C", Expression.class)).expect(Alt(Ref("A"), Ref("B"), Ref("C")));
        When.youDo(() -> testParse("Expr", ". 'hi' / [a-z]", Expression.class)).expect(Alt(Seq(Dot(), Lit("hi")), Cls("[a-z]")));
        When.youDo(() -> testParse("Expr", "Jim", Expression.class)).expect(Ref("Jim"));
        When.youDo(() -> testParse("ParExpr", "(A B C)", Expression.class)).expect(Seq(Ref("A"), Ref("B"), Ref("C")));
        When.youDo(() -> testParse("ParExpr", "('hi' / [a-z])", Expression.class)).expect(Alt(Lit("hi"), Cls("[a-z]")));
        When.youDo(() -> testParse("ParExpr", "(Jim)", Expression.class)).expect(Ref("Jim"));
        When.youDo(() -> testParse("Expr", "'\"' (Plain / \"\\\\u\" Hex / \"\\\\\" Escape)* '\"'", Expression.class))
            .expect(Seq(Lit("\""), Rep(Alt(Ref("Plain"), Seq(Lit("\\u"), Ref("Hex")), Seq(Lit("\\"), Ref("Escape")))), Lit("\"")));
        // Java text blocks process escapes, for reasons that genuinely baffle me
        When.youDo(() -> testParse("Grammar","""
                WS = [ \\t\\r\\n]
                """, PegGrammar.class)).expect(new PegGrammar().rule("WS", Cls("[ \\t\\r\\n]")));
    }

}
