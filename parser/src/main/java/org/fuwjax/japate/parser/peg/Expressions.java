package org.fuwjax.japate.parser.peg;

import static org.fuwjax.japate.iteration.Reiterable.re;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fuwjax.japate.iteration.Reiterable;
import org.fuwjax.japate.iteration.BreakIterable;
import org.fuwjax.japate.parser.ParseException;
import org.fuwjax.japate.parser.ParseResolver;
import org.fuwjax.japate.parser.Result;

public class Expressions {
    public static record SequenceExpression(List<Expression> expressions)  implements Expression {
        @Override
        public Iterable<Result> parse(TokenStream input, ParseResolver resolver) throws ParseException {
            List<Iterable<Result>> results = new ArrayList<>(expressions.size());
            for(Expression e : expressions) {
                results.add(e.parse(input, resolver));
            }
            return Reiterable.flatten(results);
        }

        @Override
        public final String toString() {
            return re(expressions).join(" ");
        }
    }

    public static record AlternativeExpression(List<Expression> expressions) implements Expression {
        @Override
        public Iterable<Result> parse(TokenStream input, ParseResolver resolver) throws ParseException {
            Mark mark = input.mark();
            ParseException furthest = null;
            for(Expression e : expressions) {
                try {
                    input.enterAlternative();
                    return e.parse(input, resolver);
                } catch (ParseException cause) {
                    if(cause.isFurther(furthest)) {
                        furthest = cause;
                    }
                    input.reset(mark);
                } finally {
                    input.exitAlternative();
                }
            }
            if(furthest == null) { // this should never happen in practice. It means there were no alternative expressions.
                return Reiterable.empty();
            }
            throw furthest;
        }

        @Override
        public final String toString() {
            return re(expressions).join(" / ");
        }
    }

    public static record RepeatedExpression(Expression expression) implements Expression {
        @Override
        public Iterable<Result> parse(TokenStream input, ParseResolver resolver) throws ParseException {
            List<Iterable<Result>> results = new ArrayList<>();
            Mark mark;
            do {
                mark = input.mark();
                try {
                    results.add(expression.parse(input, resolver));
                } catch (ParseException cause) {
                    input.reset(mark);
                }
            } while(!input.at(mark)); //terminates at both a parse fail and zero match
            return Reiterable.flatten(results);
        }

        @Override
        public final String toString() {
            return expression + "*";
        }
    }

    public static record RequiredExpression(Expression expression) implements Expression {
        @Override
        public Iterable<Result> parse(TokenStream input, ParseResolver resolver) throws ParseException {
            List<Iterable<Result>> results = new ArrayList<>();
            Mark mark = input.mark();
            results.add(expression.parse(input, resolver));
            while(!input.at(mark)) {
                mark = input.mark();
                try {
                    results.add(expression.parse(input, resolver));
                } catch (ParseException cause) {
                    input.reset(mark);
                }
            }
            return Reiterable.flatten(results);
        }

        @Override
        public final String toString() {
            return expression + "+";
        }
    }

    public static record OptionalExpression(Expression expression) implements Expression {
        @Override
        public Iterable<Result> parse(TokenStream input, ParseResolver resolver) throws ParseException {
            Mark mark = input.mark();
            try {
                return expression.parse(input, resolver);
            } catch (ParseException cause) {
                input.reset(mark);
                return Reiterable.empty();
            }
        }

        @Override
        public final String toString() {
            return expression + "?";
        }
    }

    public static record NegativeLookaheadExpression(Expression expression) implements Expression {
        @Override
        public Iterable<Result> parse(TokenStream input, ParseResolver resolver) throws ParseException {
            Mark mark = input.mark();
            try {
                expression.parse(input, resolver);
            } catch(ParseException e) {
                return Reiterable.empty();
            } finally {
                input.reset(mark);
            }
            throw input.error("found an unexpected "+ expression);
        }

        @Override
        public final String toString() {
            return "!" + expression;
        }
    }

    public static record PositiveLookaheadExpression(Expression expression) implements Expression {
        @Override
        public Iterable<Result> parse(TokenStream input, ParseResolver resolver) throws ParseException {
            Mark mark = input.mark();
            expression.parse(input, resolver);
            input.reset(mark);
            return Reiterable.empty();
            // no need to reset on a thrown parse exception
        }

        @Override
        public final String toString() {
            return "&" + expression;
        }
    }

    private static Function<String, String> escape = BreakIterable.escape("\r\n\t\"\\", Map.of("\r", "\\r", "\n", "\\n", "\t", "\\t"));

    public static record LiteralExpression(String literal) implements Expression {
        @Override
        public Iterable<Result> parse(TokenStream input, ParseResolver resolver) throws ParseException {
            Mark start = input.mark();
            String match = "";
            while(literal.startsWith(match)) {
                if(literal.equals(match)) {
                    return Reiterable.of(new Result("", match));
                }
                input.next();
                if(input.isDone()) {
                    input.reset(start);
                    throw input.error("Expected \"" + escape.apply(literal) + "\"");
                }
                match = input.substring(start);
            }
            input.reset(start);
            throw input.error("Expected \"" + escape.apply(literal) + "\", but found \"" + escape.apply(match) + "\"");
        }

        @Override
        public final String toString() {
            return "\"" + escape.apply(literal) + "\"";
        }
    }

    public static final class RegexExpression implements Expression {
        private final String regex;
        private final Pattern pattern;

        public RegexExpression(String regex) {
            this.regex = regex;
            this.pattern = Pattern.compile(regex);
        }

        @Override
        public Iterable<Result> parse(TokenStream input, ParseResolver resolver) throws ParseException {
            Mark mark = input.mark();
            input.next();
            if(input.isDone()) {
                input.reset(mark);
                throw input.error("Expected regex "+ escape.apply(regex));
            }
            String ch = input.substring(mark);
            Matcher match = pattern.matcher(ch);
            if(!match.matches()) {
                input.reset(mark);
                throw input.error("Regex " + escape.apply(regex) + " does not match \"" + escape.apply(ch) + "\"");
            }
            return Reiterable.of(new Result("", match.group()));
        }

        @Override
        public int hashCode() {
            return regex.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj){
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            RegexExpression other = (RegexExpression) obj;
            return Objects.equals(regex, other.regex);
        }

        @Override
        public final String toString() {
            return regex;
        }
    }

    public static record AnyExpression() implements Expression {
        @Override
        public Iterable<Result> parse(TokenStream input, ParseResolver resolver) throws ParseException {
            Mark start = input.mark();
            input.next();
            if(input.isDone()) {
                input.reset(start);
                throw input.error("Expected anything, but found nothing");
            }
            return Reiterable.of(new Result("", input.substring(start)));
        }

        @Override
        public String toString() {
            return ".";
        }
    }
}
