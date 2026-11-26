package org.fuwjax.japate.test;

import static org.fuwjax.japate.parser.peg.Expression.Alt;
import static org.fuwjax.japate.parser.peg.Expression.Lit;
import static org.fuwjax.japate.parser.peg.Expression.Seq;

import java.util.List;

import org.fuwjax.japate.parser.ParseException;
import org.fuwjax.japate.parser.ParseResolver;
import org.fuwjax.japate.parser.Parser;
import org.fuwjax.japate.parser.peg.PegGrammar;
import org.junit.jupiter.api.Test;

public class ParserTest {
    private static ParseResolver joiner = (name, result) -> result.join();

    @Test
    void testParserBasic() {
        Parser<String> parser = new PegGrammar()
            .rule("S", Lit("abc"))
            .bind(() -> joiner)
            .parser("S", String.class);
        When.youDo(() -> parser.parse("abc")).expect("abc");
    }

    @Test
    void testParserReuse() throws ParseException {
        Parser<String> parser = new PegGrammar()
            .rule("S", Lit("abc"))
            .bind(() -> joiner)
            .parser("S", String.class);
		parser.parse("abc");
		When.youDo(() -> parser.parse("abc")).expect("abc");
    }

    @Test
    void testParserSequence() {
        Parser<String> parser = new PegGrammar()
            .rule("S", Seq(Lit("a"), Lit("b"), Lit("c")))
            .bind(() -> joiner)
            .parser("S", String.class);
		When.youDo(() -> parser.parse("abc")).expect("abc");
    }

    @Test
    void testParserRef() {
        PegGrammar g = new PegGrammar();
        Parser<String> parser = g
            .rule("S", g.ref("T"))
            .rule("T", Lit("abc"))
            .bind(() -> joiner)
            .parser("S", String.class);
		When.youDo(() -> parser.parse("abc")).expect("abc");
    }

    @Test
    void testParserReducer() {
        PegGrammar g = new PegGrammar();
        @SuppressWarnings("rawtypes")
        Parser<List> parser = g
            .rule("S", g.ref("T"))
            .rule("T", Lit("abc"))
            .bind(() -> (name, result) -> switch(name) {
                case "S" -> result.getAll("T").as(Object.class).asList();
                default -> result.join();
            })
            .parser("S", List.class);
		When.youDo(() -> parser.parse("abc")).expect(List.of("abc"));
    }

    @Test
    void testParserReduceSequence() {
        PegGrammar g = new PegGrammar();
        @SuppressWarnings("rawtypes")        
        Parser<List> parser = g
            .rule("S", Seq(g.ref("T"), g.ref("T")))
            .rule("T", Lit("ab"))
            .bind(() -> (name, result) -> switch(name) {
                case "S" -> result.getAll("T").as(Object.class).asList();
                default -> result.join();
            })
            .parser("S", List.class);
		When.youDo(() -> parser.parse("abab")).expect(List.of("ab", "ab"));
    }

    @Test
    void testParserRightRecursion() {
        PegGrammar g = new PegGrammar();
        Parser<String> parser = g
            .rule("S", Alt(Seq(Lit("a"), g.ref("S")), Lit("a")))
            .bind(() -> joiner)
            .parser("S", String.class);
        When.youDo(() -> parser.parse("a")).expect("a");
        When.youDo(() -> parser.parse("aaa")).expect("aaa");
        When.youDo(() -> parser.parse("aa")).expect("aa");
    }

    @Test
    void testParserLeftRecursion() {
        PegGrammar g = new PegGrammar();
        Parser<String> parser = g
            .rule("S", Alt(Seq(g.ref("S"), Lit("a")), Lit("a")))
            .bind(() -> joiner)
            .parser("S", String.class);
        When.youDo(() -> parser.parse("a")).expect("a");
        When.youDo(() -> parser.parse("aaa")).expect("aaa");
        When.youDo(() -> parser.parse("aa")).expect("aa");
    }

    @Test
    void testParserIndirectLeftRecursion() {
        PegGrammar g = new PegGrammar();
        Parser<String> parser = g
            .rule("S", Alt(Seq(g.ref("T"), Lit("b")), Lit("a")))
            .rule("T", Alt(Seq(g.ref("S"), Lit("a")), Lit("c")))
            .bind(() -> joiner)
            .parser("S", String.class);
        When.youDo(() -> parser.parse("a")).expect("a");
        When.youDo(() -> parser.parse("aaba")).expect("aab");
        When.youDo(() -> parser.parse("aabab")).expect("aabab");
    }
}
