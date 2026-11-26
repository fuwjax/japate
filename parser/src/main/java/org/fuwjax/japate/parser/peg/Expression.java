package org.fuwjax.japate.parser.peg;

import java.util.List;

import org.fuwjax.japate.parser.ParseException;
import org.fuwjax.japate.parser.ParseResolver;
import org.fuwjax.japate.parser.Result;
import org.fuwjax.japate.parser.peg.Expressions.AlternativeExpression;
import org.fuwjax.japate.parser.peg.Expressions.AnyExpression;
import org.fuwjax.japate.parser.peg.Expressions.LiteralExpression;
import org.fuwjax.japate.parser.peg.Expressions.NegativeLookaheadExpression;
import org.fuwjax.japate.parser.peg.Expressions.OptionalExpression;
import org.fuwjax.japate.parser.peg.Expressions.PositiveLookaheadExpression;
import org.fuwjax.japate.parser.peg.Expressions.RegexExpression;
import org.fuwjax.japate.parser.peg.Expressions.RepeatedExpression;
import org.fuwjax.japate.parser.peg.Expressions.RequiredExpression;
import org.fuwjax.japate.parser.peg.Expressions.SequenceExpression;

public interface Expression {
    Iterable<Result> parse(TokenStream input, ParseResolver resolver) throws ParseException;

    public static Expression Seq(Expression... exprs) {
        if(exprs.length == 1) {
            return exprs[0];
        }
        return Seq(List.of(exprs));
    }

    public static Expression Seq(List<Expression> exprs) {
        assert exprs != null && !exprs.isEmpty();
        if(exprs.size() == 1) {
            return exprs.getFirst();
        }
        return new SequenceExpression(exprs);
    }

    public static Expression Alt(Expression... exprs) {
        if(exprs.length == 1) {
            return exprs[0];
        }
        return Alt(List.of(exprs));
    }

    public static Expression Alt(List<Expression> exprs) {
        assert exprs != null && !exprs.isEmpty();
        if(exprs.size() == 1) {
            return exprs.getFirst();
        }
        return new AlternativeExpression(exprs);
    }

    public static Expression Rep(Expression expr) {
        assert expr != null;
        return new RepeatedExpression(expr);
    }

    public static Expression Req(Expression expr) {
        return new RequiredExpression(expr);
    }
    
    public static Expression Opt(Expression expr) {
        return new OptionalExpression(expr);
    }

    public static Expression Not(Expression expr) {
        return new NegativeLookaheadExpression(expr);
    }

    public static Expression See(Expression expr) {
        return new PositiveLookaheadExpression(expr);
    }

    public static Expression Lit(String literal) {
        return new LiteralExpression(literal);
    }

    public static Expression Cls(String regex) {
        return new RegexExpression(regex);
    }

    public static Expression Dot() {
        return new AnyExpression();
    }
}
