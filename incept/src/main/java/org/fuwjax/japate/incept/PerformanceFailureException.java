package org.fuwjax.japate.incept;

import java.lang.reflect.InvocationTargetException;

public class PerformanceFailureException extends Exception {
    private Throwable worst;
    private int failures;

    public void include(Exception e) {
        failures++;
        if(worst == null || worst instanceof IllegalAccessException || e instanceof InvocationTargetException) {
            worst = e;
        }
    }

    @Override
    public String getMessage() {
        if(failures == 0) {
            return "No Handlers available";
        }
        return failures + " failed attempts";
    }

    @Override
    public synchronized Throwable getCause() {
        return worst;
    }
}
