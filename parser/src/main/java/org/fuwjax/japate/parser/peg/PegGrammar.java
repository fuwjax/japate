package org.fuwjax.japate.parser.peg;

import static org.fuwjax.japate.iteration.Reiterable.re;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import org.fuwjax.japate.iteration.Reiterable;
import org.fuwjax.japate.parser.Grammar;
import org.fuwjax.japate.parser.ParseException;
import org.fuwjax.japate.parser.ParseResolver;
import org.fuwjax.japate.parser.Parser;
import org.fuwjax.japate.parser.Result;
import org.fuwjax.japate.parser.peg.TokenStream.NameAt;

/*
Represents the collection of rules that specifies a grammar.
*/
public class PegGrammar implements Grammar {
    private final Map<String, Expression> relations = new HashMap<>();
    private Supplier<ParseResolver> resolvers;

    public PegGrammar rule(String name, Expression relation) {
        Expression old = relations.putIfAbsent(name, relation);
        if(old != null) {
            relations.put(name, Expression.Alt(old, relation));
        }
        return this;
    }

    public PegGrammar bind(Supplier<ParseResolver> resolvers) {
        this.resolvers = resolvers;
        return this;
    }

    public <T> Parser<T> parser(String root, Class<T> type) {
        Expression start = ref(root);
        return input -> type.cast(re(start.parse(new TokenStream(input), resolvers.get())).first().get().value());
    }

    public Expression ref(String name) {
        return new Reference(name);
    }

    private class Reference implements Expression {
        String name;
        Expression relation;

        public Reference(String name) {
            this.name = name;
            relation = relations.get(name);
        }

        @Override
        public Iterable<Result> parse(TokenStream input, ParseResolver resolver) throws ParseException {
            if(relation == null) {
                relation = relations.get(name);
                if(relation == null) {
                    throw input.error("no rule named "+name);
                }
            }
            Mark mark = input.mark();
            Memo memo = input.fetch(mark, name);
            if(memo != null) {
                input.reset(memo.end());
                return memo.claim();
            }
            memo = input.enter(mark, name); 
            Iterable<NameAt> retry = Reiterable.empty();
            do {
                input.reset(mark);
                input.removeAllMemos(retry);
                try {
                    Iterable<Result> results = relation.parse(input, resolver);
                    Object resolved = resolver.resolve(name, results::iterator);
                    Iterable<Result> result = Reiterable.of(new Result(name, resolved));
                    retry = memo.save(result, null, input.mark());
                } catch (ParseException cause) {
                    retry = memo.save(null, cause, input.mark());
                }
            } while(retry != null);
            input.exit(); 
            return memo.claim();
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj){
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Reference other = (Reference) obj;
            return Objects.equals(name, other.name);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(relations);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PegGrammar other = (PegGrammar) obj;
        return Objects.equals(relations, other.relations);
    }
}
