package org.fuwjax.japate.incept.handler;

import java.lang.reflect.Constructor;

import org.fuwjax.japate.incept.Handler;

public class ConstructorHandler implements Handler {
    private Constructor<?> c;

    public ConstructorHandler(Constructor<?> c) {
        this.c = c;
    }

    @Override
    public boolean resultsIn(Class<?> type) {
        return type.isAssignableFrom(c.getDeclaringClass());
    }

    @Override
    public boolean accepts(Class<?>... types) {
        return Handler.checkParams(c.getParameterTypes(), types);
    }

    @Override
    public String name() {
        return "new";
    }

    @Override
    public Object invoke(Object... args) throws Exception {
        return c.newInstance(args);
    }
}