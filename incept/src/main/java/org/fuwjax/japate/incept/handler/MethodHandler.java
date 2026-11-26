package org.fuwjax.japate.incept.handler;

import java.lang.reflect.Method;

import org.fuwjax.japate.incept.Handler;

public class MethodHandler implements Handler {
    private Method m;
    private Object obj;

    public MethodHandler(Method m, Object obj) {
        this.m = m;
        this.obj = obj;
    }

    @Override
    public boolean resultsIn(Class<?> type) {
        return type.isAssignableFrom(m.getReturnType());
    }

    @Override
    public boolean accepts(Class<?>... types) {
        return Handler.checkParams(m.getParameterTypes(), types);
    }

    @Override
    public String name() {
        return m.getName();
    }

    @Override
    public Object invoke(Object... args) throws Exception {
        return m.invoke(obj, args);
    }
}
