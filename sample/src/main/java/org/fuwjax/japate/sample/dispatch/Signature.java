package org.fuwjax.japate.sample.dispatch;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;

import org.fuwjax.japate.iteration.Reiterable;

/**
 * Signature
 */
public interface Signature {
    AnnotatedType namespace();
    
    String name();

    Reiterable<Annotation> annotations();

    AnnotatedType returns();

    Reiterable<AnnotatedType> params();

    Reiterable<AnnotatedType> exceptions();
}
