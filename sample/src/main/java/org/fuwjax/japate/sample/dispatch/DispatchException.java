package org.fuwjax.japate.sample.dispatch;

import java.util.LinkedList;
import java.util.List;

/**
 * DispatchException
 */
public class DispatchException extends Exception {
    private List<Exception> failures = new LinkedList<>();
    
    public DispatchException() {
        failures = new LinkedList<>();
    }

    public DispatchException(String message) {
        super(message);
    }

    public DispatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public void failed(Exception e) {
        if(failures.isEmpty() && getCause() == null) {
            initCause(e);
        }
        failures.add(e);
    }
}
