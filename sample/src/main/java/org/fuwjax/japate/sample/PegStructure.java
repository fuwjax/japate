package org.fuwjax.japate.sample;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class PegStructure {
    public static record Grammar(Map<String, Rule> rules) {
        public Rule rule(String name) {
            return rules.get(name);
        }
    }

    public static record Rule(String name, Expression Expression) {}

    public static interface Expression {
        int id();
    }

    public static record ReferenceExpression(int id, String name) implements Expression {}

    public static record AlternativeExpression(int id, List<Expression> exprs) implements Expression {}

    public static record SequenceExpression(int id, List<Expression> exprs) implements Expression {}

    public static record PositiveLookaheadExpression(int id, Expression expr) implements Expression {}

    public static record NegativeLookaheadExpression(int id, Expression expr) implements Expression {}

    public static record RepeatingExpression(int id, Expression expr) implements Expression {}

    public static record RequiredExpression(int id, Expression expr) implements Expression {}

    public static record OptionalExpression(int id, Expression expr) implements Expression {}

    public static record LiteralExpression(int id, String literal) implements Expression {}

    public static record PatternExpression(int id, String regex) implements Expression {}

    public static record DotExpression(int id) implements Expression {}

    private static Supplier<Integer> ids = new Supplier<>() {
        private int index = 0;
        
        public Integer get() {
            return ++index;
        }
    };

    public static StandardParseHandler HANDLER = new StandardParseHandler(PegStructure::resolver);

    public static Object resolver(String name, Iterable<RuleResult> results) {
        ParseResult result = results::iterator;
        return switch(name) {
            case "Grammar" -> Grammar(result);
            case "Line" -> Line(result);
            case "Rule" -> Rule(result);
            case "Expr" -> Expr(result);
            case "Seq" -> Seq(result);
            case "Prefix" -> Prefix(result);
            case "AndExpr" -> AndExpr(result);
            case "NotExpr" -> NotExpr(result);
            case "Suffix" -> Suffix(result);
            case "OptExpr" -> OptExpr(result);
            case "RepExpr" -> RepExpr(result);
            case "ReqExpr" -> ReqExpr(result);
            case "Primary" -> Primary(result);
            case "Dot" -> AnyExpr(result);
            case "ParExpr" -> ParExpr(result);
            case "Literal" -> Literal(result);
            case "CharClass" -> CharClass(result);
            case "Ref" -> Reference(result);
            case "Escape" -> Escape(result);
            default -> result.join();
        };
    }

    public static Grammar Grammar(ParseResult result) {
        Map<String, Rule> rules = result.getAll("Line").as(Rule.class).filter(o -> o != null).asMap(Rule::name);
        return new Grammar(rules);
    }

    public static Rule Line(ParseResult result) {
        return result.getFirst("Rule").map(Rule.class::cast).orElse(null); //these nulls are filtered in Grammar
    }

    public static Rule Rule(ParseResult result) {
        String name = result.getFirst("Name").map(String.class::cast).get();
        Expression expr = result.getFirst("Expr").map(Expression.class::cast).get();
        return new Rule(name, expr);
    }

    public static Expression Expr(ParseResult result) {
        List<Expression> seqs = result.getAll("Seq").map(Expression.class::cast).asList();
        if(seqs.size() == 1) {
            return seqs.get(0);
        }
        return new AlternativeExpression(ids.get(), seqs);
    }

    public static Expression Seq(ParseResult result) {
        List<Expression> prefixs = result.getAll("Prefix").map(Expression.class::cast).asList();
        if(prefixs.size() == 1) {
            return prefixs.get(0);
        }
        return new SequenceExpression(ids.get(), prefixs);
    }

    public static Expression Prefix(ParseResult result) {
        return result.getAnyFirst("AndExpr", "NotExpr", "Suffix").map(Expression.class::cast).get();
    }

    public static Expression AndExpr(ParseResult result) {
        Expression expr = result.getFirst("Suffix").map(Expression.class::cast).get();
        return new PositiveLookaheadExpression(ids.get(), expr);
    }

    public static Expression NotExpr(ParseResult result) {
        Expression expr = result.getFirst("Suffix").map(Expression.class::cast).get();
        return new NegativeLookaheadExpression(ids.get(), expr);
    }

    public static Expression Suffix(ParseResult result) {
        return result.getAnyFirst("OptExpr", "RepExpr", "ReqExpr", "Primary").map(Expression.class::cast).get();
    }

    public static Expression OptExpr(ParseResult result) {
        Expression expr = result.getFirst("Primary").map(Expression.class::cast).get();
        return new OptionalExpression(ids.get(), expr);
    }

    public static Expression RepExpr(ParseResult result) {
        Expression expr = result.getFirst("Primary").map(Expression.class::cast).get();
        return new RepeatingExpression(ids.get(), expr);
    }

    public static Expression ReqExpr(ParseResult result) {
        Expression expr = result.getFirst("Primary").map(Expression.class::cast).get();
        return new RequiredExpression(ids.get(), expr);
    }

    public static Expression Primary(ParseResult result) {
        return result.getAnyFirst("AnyExpr", "ParExpr", "Literal", "CharClass", "Ref").map(Expression.class::cast).get();
    }

    public static Expression AnyExpr(ParseResult result) {
        return new DotExpression(ids.get());
    }

    public static Expression ParExpr(ParseResult result) {
        return result.getFirst("Expr").map(Expression.class::cast).get();
    }

    public static Expression Literal(ParseResult result) {
        String lit = result.getAnyFirst("SingleLit", "DoubleLit").map(String.class::cast).get();
        return new LiteralExpression(ids.get(), lit);
    }

    private static final Map<String, String> escape = Map.of("n", "\n", "r", "\r", "t", "\t");
    public static String Escape(ParseResult result) {
        String code = result.map(String.class::cast).first().get();
        return escape.getOrDefault(code, code);
    }

    public static Expression CharClass(ParseResult result) {
        String pattern = result.getFirst("Pattern").map(String.class::cast).get();
        return new PatternExpression(ids.get(), pattern);
    }

    public static Expression Reference(ParseResult result) {
        String name = result.getFirst("Name").map(String.class::cast).get();
        return new ReferenceExpression(ids.get(), name);
    }
}
