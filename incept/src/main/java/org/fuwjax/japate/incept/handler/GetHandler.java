package org.fuwjax.japate.incept.handler;

import java.lang.reflect.Field;

import org.fuwjax.japate.incept.Handler;
import org.fuwjax.japate.incept.InvalidSignatureException;

public class GetHandler implements Handler {
    private final Field f;
    private final Object obj;

    public GetHandler(Field f, Object obj) {
        this.f = f;
        this.obj = obj;
    }

    @Override
    public boolean resultsIn(Class<?> type) {
       return type.isAssignableFrom(f.getType());
    }

    @Override
    public boolean accepts(Class<?>... types) {
        if(types.length == 0) {
            return true;
        }
        return false;
    }

    @Override
    public String name() {
        return f.getName();
    }

    @Override
    public Object invoke(Object... args) throws Exception {
        if(args.length != 0) {
            throw new InvalidSignatureException("Getters must take no arguments");
        }
        return f.get(obj);
    }
}
