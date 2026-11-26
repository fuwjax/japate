package org.fuwjax.japate.incept;

import org.fuwjax.japate.iteration.Reiterable;

public class Action<T> {
    private Reiterable<Handler> handlers;
    private Class<T> result;
    public Action(Reiterable<Handler> handlers, Class<T> type) {
        this.handlers = handlers;
        this.result = type;
    }

    public Action<T> withParams(Class<?>... types) {
        return new Action<>(handlers.filter(h -> h.accepts(types)), result);
    }

    public <R> Action<R> withResult(Class<R> type) {
        return new Action<>(handlers.filter(h -> h.resultsIn(type)), type);
    }

    public boolean isFeasible() {
        return !handlers.isEmpty();
    }
    
    @SuppressWarnings("unchecked")
    public T perform(Object... args) throws PerformanceFailureException {
        PerformanceFailureException cause = new PerformanceFailureException();
        for(Handler handler: handlers) {
            try {                
                return (T)(handler.invoke(args)); // result.cast cannot cast primitives to wrappers and vice versa
            } catch(Exception e) {
                cause.include(e);
            }
        }
        throw cause;
    }
}