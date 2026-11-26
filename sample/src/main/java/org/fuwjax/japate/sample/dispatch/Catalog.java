package org.fuwjax.japate.sample.dispatch;

import static org.fuwjax.japate.iteration.Reiterable.flatten;
import static org.fuwjax.japate.iteration.Reiterable.re;

import java.util.ArrayList;
import java.util.List;

public class Catalog {
    private final List<Namespace> sources = new ArrayList<>();

    public void add(Namespace source) {
        this.sources.add(source);
    }

    public Message matching(SignatureFilter sig) {
        return new Message(flatten(re(sources).map(s -> s.filter(sig))));
    }

    public Message tryHard(){
        return new Message(flatten(sources));
    }
}
