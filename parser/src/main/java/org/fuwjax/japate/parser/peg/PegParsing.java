package org.fuwjax.japate.parser.peg;

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

import java.util.List;
import java.util.Map;

import org.fuwjax.japate.parser.Grammar;
import org.fuwjax.japate.parser.ParseResolver;
import org.fuwjax.japate.parser.ParseResult;
import org.fuwjax.japate.parser.Parser;

public class PegParsing implements ParseResolver {
    private static final PegGrammar PEG = new PegGrammar();

    private static Expression Ref(String name) {
        return PEG.ref(name);
    }

    static {
        PEG.rule("Grammar", Seq(Ref("Line"), Rep(Seq(Ref("EOL"), Ref("Line"))), Opt(Ref("EOL")), Ref("EOF")));
        PEG.rule("Line", Alt(Ref("Rule"), Ref("Comment"), Ref("WS")));
        PEG.rule("Rule", Seq(Ref("WS"), Ref("Name"), Ref("WS"), Lit("="), Ref("WS"), Ref("Expr"), Ref("WS")));
        PEG.rule("Expr", Seq(Ref("Sequence"), Rep(Seq(Ref("WS"), Lit("/"), Ref("WS"), Ref("Sequence")))));
        PEG.rule("Sequence", Seq(Ref("Prefix"), Rep(Seq(Ref("WS"), Ref("Prefix")))));
        PEG.rule("Prefix", Alt(Ref("AndExpr"), Ref("NotExpr"), Ref("Suffix")));
        PEG.rule("AndExpr", Seq(Lit("&"), Ref("WS"), Ref("Suffix")));
        PEG.rule("NotExpr", Seq(Lit("!"), Ref("WS"), Ref("Suffix")));
        PEG.rule("Suffix", Alt(Ref("OptExpr"), Ref("RepExpr"), Ref("ReqExpr"), Ref("Primary")));
        PEG.rule("OptExpr", Seq(Ref("Primary"), Ref("WS"), Lit("?")));
        PEG.rule("RepExpr", Seq(Ref("Primary"), Ref("WS"), Lit("*")));
        PEG.rule("ReqExpr", Seq(Ref("Primary"), Ref("WS"), Lit("+")));
        PEG.rule("Primary", Alt(Ref("AnyExpr"), Ref("ParExpr"), Ref("Literal"), Ref("CharClass"), Ref("Reference")));
        PEG.rule("AnyExpr", Lit("."));
        PEG.rule("ParExpr", Seq(Lit("("), Ref("WS"), Ref("Expr"), Ref("WS"), Lit(")")));
        PEG.rule("Literal", Alt(Seq(Lit("'"), Ref("SingleLit"), Lit("'")), Seq(Lit("\""), Ref("DoubleLit"), Lit("\""))));
        PEG.rule("CharClass", Ref("Pattern"));
        PEG.rule("Reference", Ref("Name"));
        PEG.rule("Comment", Seq(Lit("#"), Rep(Seq(Not(Ref("EOL")), Dot()))));
        PEG.rule("Name", Seq(Cls("[_a-zA-Z]"), Rep(Cls("[_a-zA-Z0-9]"))));
        PEG.rule("Pattern", Seq(Lit("["), Req(Alt(Lit("\\]"), Cls("[^]]"))), Lit("]")));
        PEG.rule("SingleLit", Rep(Alt(Ref("Escape"), Ref("SinglePlain"))));
        PEG.rule("DoubleLit", Rep(Alt(Ref("Escape"), Ref("DoublePlain"))));
        PEG.rule("Escape", Seq(Lit("\\"), Dot()));
        PEG.rule("SinglePlain", Req(Cls("[^\\']")));
        PEG.rule("DoublePlain", Req(Cls("[^\\\"]")));
        PEG.rule("WS", Rep(Cls("[ \t]")));
        PEG.rule("EOL", Cls("[\n\r]"));
        PEG.rule("EOF", Not(Dot()));
        PEG.bind(PegParsing::new);
    }

    public static Parser<Grammar> peg() {
        return PEG.parser("Grammar", Grammar.class);
    }

    public static <T> Parser<T> pegFrom(String start, Class<T> type) {
        return PEG.parser(start, type);
    }

    private PegGrammar grammar;
    public PegParsing() {
        grammar = new PegGrammar();
    }

    public Object resolve(String name, ParseResult result) {
        return switch(name) {
            case "Grammar" -> grammar;
            case "Line" -> null;
            case "Rule" -> Rule(result);
            case "Expr" -> Expr(result);
            case "Sequence" -> Sequence(result);
            case "Prefix" -> Prefix(result);
            case "AndExpr" -> AndExpr(result);
            case "NotExpr" -> NotExpr(result);
            case "Suffix" -> Suffix(result);
            case "OptExpr" -> OptExpr(result);
            case "RepExpr" -> RepExpr(result);
            case "ReqExpr" -> ReqExpr(result);
            case "Primary" -> Primary(result);
            case "AnyExpr" -> AnyExpr(result);
            case "ParExpr" -> ParExpr(result);
            case "Literal" -> Literal(result);
            case "CharClass" -> CharClass(result);
            case "Reference" -> Reference(result);
            case "Comment" -> null;
            case "Name" -> result.join();
            case "Pattern" -> result.join();
            case "SingleLit" -> result.join();
            case "DoubleLit" -> result.join();
            case "Escape" -> Escape(result);
            case "SinglePlain" -> result.join();
            case "DoublePlain" -> result.join();
            case "WS" -> null;
            case "EOL" -> null;
            case "EOF" -> null;
            default -> throw new IllegalArgumentException("No handler for "+ name);
        };
    }

    public Object Rule(ParseResult result) {
        // this is cheating a bit. If a rule is successfully matched it will _always_ bubble up to grammar to be added, 
        // so we can safely short circuit the normal return chain and add it directly to grammar here. Note that because
        // of this, Line and Grammar callbacks are moot.
        String name = result.getFirst("Name").map(String.class::cast).get();
        Expression expr = result.getFirst("Expr").map(Expression.class::cast).get();
        grammar.rule(name, expr);
        return null;
    }

    public Expression Expr(ParseResult result) {
        List<Expression> seqs = result.getAll("Sequence").map(Expression.class::cast).asList();
        return Alt(seqs);
    }

    public Expression Sequence(ParseResult result) {
        List<Expression> prefixs = result.getAll("Prefix").map(Expression.class::cast).asList();
        return Seq(prefixs);
    }

    public Object Prefix(ParseResult result) {
        return result.getAnyFirst("AndExpr", "NotExpr", "Suffix").get();
    }

    public Expression AndExpr(ParseResult result) {
        Expression expr = result.getFirst("Suffix").map(Expression.class::cast).get();
        return See(expr);
    }

    public Expression NotExpr(ParseResult result) {
        Expression expr = result.getFirst("Suffix").map(Expression.class::cast).get();
        return Not(expr);
    }

    public Object Suffix(ParseResult result) {
        return result.getAnyFirst("OptExpr", "RepExpr", "ReqExpr", "Primary").get();
    }

    public Expression OptExpr(ParseResult result) {
        Expression expr = result.getFirst("Primary").map(Expression.class::cast).get();
        return Opt(expr);
    }

    public Expression RepExpr(ParseResult result) {
        Expression expr = result.getFirst("Primary").map(Expression.class::cast).get();
        return Rep(expr);
    }

    public Expression ReqExpr(ParseResult result) {
        Expression expr = result.getFirst("Primary").map(Expression.class::cast).get();
        return Req(expr);
    }

    public Object Primary(ParseResult result) {
        return result.getAnyFirst("AnyExpr", "ParExpr", "Literal", "CharClass", "Reference").get();
    }

    public Expression AnyExpr(ParseResult result) {
        return Dot();
    }

    public Expression ParExpr(ParseResult result) {
        return result.getFirst("Expr").map(Expression.class::cast).get();
    }

    public Expression Literal(ParseResult result) {
        String lit = result.getAnyFirst("SingleLit", "DoubleLit").map(String.class::cast).get();
        return Lit(lit);
    }

    private static final Map<String, String> escape = Map.of("n", "\n", "r", "\r", "t", "\t");
    public String Escape(ParseResult result) {
        String code = result.values().select(1).map(String.class::cast).get();
        return escape.getOrDefault(code, code);
    }

    public Expression CharClass(ParseResult result) {
        String pattern = result.getFirst("Pattern").map(String.class::cast).get();
        return Cls(pattern);
    }

    public Expression Reference(ParseResult result) {
        String name = result.getFirst("Name").map(String.class::cast).get();
        // This is dangerous in general. For this to be safe, the call target (here, "grammar") should
        // be created by the resolver, so as not to mix between separate Parser.parse() calls. Additionally,
        // no reference to the result of the call (here, "ref") should be held by anyone anywhere. It is perhaps
        // allowable for it to be held internal to the resolver instance. But be forewarned that any objects
        // created in any resolver operations may not be present in the final result. Spurious objects can and will occur.
        return grammar.ref(name);
    }
}
