package org.fuwjax.japate.incept.handler;

import java.lang.reflect.Method;

import org.fuwjax.japate.incept.Handler;

public class SetterHandler implements Handler {
    private Method m;
    private Object obj;

    public SetterHandler(Method m, Object obj) {
        assert m.getName().startsWith("set") && m.getParameterCount() == 1;
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
        return Character.toLowerCase(m.getName().charAt(3)) + m.getName().substring(4);
    }

    @Override
    public Object invoke(Object... args) throws Exception {
        return m.invoke(obj, args);
    }
}
