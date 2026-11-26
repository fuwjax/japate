package org.fuwjax.japate.sample.dispatch;

import org.fuwjax.japate.iteration.Reiterable;

public interface Namespace extends Reiterable<Action> {
    <T> Reiterable<Action> filter(SignatureFilter signature);
}
