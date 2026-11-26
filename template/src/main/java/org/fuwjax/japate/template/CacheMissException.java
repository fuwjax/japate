package org.fuwjax.japate.template;

/**
 * CacheMissException
 */
public class CacheMissException extends TemplateException {
    public CacheMissException(Object key) {
        super(String.valueOf(key));
    }
}
