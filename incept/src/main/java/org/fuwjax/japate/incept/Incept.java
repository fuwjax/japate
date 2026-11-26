package org.fuwjax.japate.incept;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.fuwjax.japate.incept.handler.ConstructorHandler;
import org.fuwjax.japate.incept.handler.EnumHandler;
import org.fuwjax.japate.incept.handler.GetHandler;
import org.fuwjax.japate.incept.handler.GetterHandler;
import org.fuwjax.japate.incept.handler.MethodHandler;
import org.fuwjax.japate.incept.handler.SetHandler;
import org.fuwjax.japate.incept.handler.SetterHandler;
import org.fuwjax.japate.multimap.InferredKeyMultiMap;

public class Incept {
    private InferredKeyMultiMap<String, Handler> handlers = InferredKeyMultiMap.infer(Handler::name);

    public static Incept of(Object obj) {
        return of(obj, obj.getClass());
    }

    public static Incept of(Object obj, Class<?> cls) {
        return new Incept(cls, obj);
    }

    public static Incept ofType(Class<?> cls) {
        return of(null, cls);
    }
    
    private Incept(Class<?> cls, Object obj) {
        if(obj == null) {
            for(Constructor<?> c: cls.getConstructors()) {
                handlers.putLast(new ConstructorHandler(c));
            }
            for(Method m: cls.getMethods()) {
                if(Modifier.isStatic(m.getModifiers())){
                    handlers.putLast(new MethodHandler(m, null));
                }
            }
            for(Field f: cls.getFields()) {
                if(Modifier.isStatic(f.getModifiers())){
                    handlers.putLast(new GetHandler(f, null));
                    if(!Modifier.isFinal(f.getModifiers())) {
                        handlers.putLast(new SetHandler(f, null));
                    }
                }
            }
            Object[] enums = cls.getEnumConstants();
            if(enums != null) {
                for(Object e: enums) {
                    handlers.putLast(new EnumHandler((Enum<?>)e));
                }
            }
        } else {
            assert cls.isInstance(obj);
            for(Method m: cls.getMethods()) {
                if(!Modifier.isStatic(m.getModifiers())){
                    handlers.putLast(new MethodHandler(m, obj));
                    if(m.getName().startsWith("get") && m.getParameterCount() == 0) {
                        handlers.putLast(new GetterHandler(m, obj));
                    }
                    if(m.getName().startsWith("set") && m.getParameterCount() == 1) {
                        handlers.putLast(new SetterHandler(m, obj));
                    }
                }
            }
            for(Field f: cls.getFields()) { 
                if(!Modifier.isStatic(f.getModifiers())){
                    handlers.putLast(new GetHandler(f, obj));
                    if(!Modifier.isFinal(f.getModifiers())) {
                        handlers.putLast(new SetHandler(f, obj));
                    }
                }
            }
        }
    }

    public Action<?> getAction(String name) {
        return new Action<>(handlers.getAll(name), Object.class);
    }

    public Action<?> anonymous() {
        return new Action<>(handlers.values(), Object.class);
    }
}
