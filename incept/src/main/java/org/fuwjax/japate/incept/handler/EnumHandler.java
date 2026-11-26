package org.fuwjax.japate.incept.handler;

import org.fuwjax.japate.incept.Handler;
import org.fuwjax.japate.incept.InvalidSignatureException;

public class EnumHandler implements Handler {
    private Enum<?> e;

    public EnumHandler(Enum<?> e) {
        this.e = e;
    }

    @Override
    public boolean resultsIn(Class<?> type) {
        return type.isAssignableFrom(e.getDeclaringClass());
    }

    @Override
    public boolean accepts(Class<?>... types) {
        return types.length == 0;
    }

    @Override
    public String name() {
        return e.name();
    }

    @Override
    public Object invoke(Object... args) throws Exception {
        if(args.length != 0) {
            throw new InvalidSignatureException("Enum value cannot accept args");
        }
        return e;
    }
}
