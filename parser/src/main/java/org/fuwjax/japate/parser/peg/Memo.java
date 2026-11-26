package org.fuwjax.japate.parser.peg;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fuwjax.japate.parser.ParseException;
import org.fuwjax.japate.parser.Result;
import org.fuwjax.japate.parser.peg.TokenStream.NameAt;

public class Memo {
    private boolean pending = true;
    private Set<NameAt> paths = null;
    private Iterable<Result> success;
    private ParseException failure;
    private Mark end;

    public Memo(ParseException failure) {
        this.failure = failure;
    }

    public Mark end() {
        return end;
    }

    public Iterable<NameAt> save(Iterable<Result> success, ParseException failure, Mark end) {
        boolean advanced = this.end == null || (failure == null && end.pos() > this.end.pos());
        pending = advanced && paths != null; // left recursion detected
        if(advanced) {
            this.success = success;
            this.failure = failure;
            this.end = end;
            if(pending) {
                List<NameAt> drops = List.copyOf(paths);
                paths.clear();
                return drops;
            }
        }
        return null;
    }

    public Iterable<Result> claim() throws ParseException {
        if(failure != null) {
            throw failure;
        }
        return success;
    }

    public void checkRecursion(Collection<NameAt> stackBetween) {
        if(pending) {
            if(paths == null) {
                paths = new HashSet<>();
            }
            paths.addAll(stackBetween);
        }
    }

    public boolean isPending() {
        return pending;
    }
}
