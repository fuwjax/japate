package org.fuwjax.japate.incept;

import java.lang.reflect.InvocationTargetException;

/**
 * A function-like abstraction for Java reflection. Reflection objects are abstracted to a sort of signature: a name, result type, and parameter types. 
 * This abstraction can then be invoked as though it were a static method with the implied signature
 *      Result Name(Param0 arg0, Param1 arg1, ...)
 * 
 * Currently supports constructors, fields, methods, and enum constants.
 */
public interface Handler {
    public static boolean checkParams(Class<?>[] params, Class<?>[] args) {
        if(params.length != args.length) {
            return false;
        }
        for(int i=0; i < params.length; i++) {
            if(!params[i].isAssignableFrom(args[i])) {
                return false;
            }
        }
        return true;
    }

    boolean accepts(Class<?>... types);
    boolean resultsIn(Class<?> type);
    String name();

    /**
     * Attempts to invoke this abstracted function with the supplied args. 
     * @param args the required args
     * @return the invocation result
     * @throws InvalidSignatureException if the inferred signature checks fail.
     * @throws IllegalAccessException if the underlying element is inaccessible or unmodifiable.
     * @throws IllegalArgumentException if the target is not an instance of the class or interface declaring the underlying element, or if an unwrapping conversion fails.
     * @throws NullPointerException if the target is null and the underlying element is non-static.
     * @throws InstantiationException if the underlying element invocation attempts to construct an abstract class.
     * @throws ExceptionInInitializerError if a provoked initialization fails.
     * @throws InvocationTargetException if the underlying element invocation throws an exception.
     */
    Object invoke(Object... args) throws Exception;
}