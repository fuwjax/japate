package org.fuwjax.japate.sample.dispatch;

/**
 * Action
 */
public interface Action {
    Signature signature();

    Object attempt(Object... args) throws Exception;
}
