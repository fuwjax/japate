package org.fuwjax.japate.sample.dispatch;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.util.function.Predicate;

/**
 * MessageSignature
 */
public interface SignatureFilter extends Predicate<Signature> {
    SignatureFilter namespace(FilterOp<AnnotatedType> type);

    SignatureFilter name(FilterOp<String> name);

    SignatureFilter annotation(FilterOp<Annotation> annotation);

    SignatureFilter result(FilterOp<AnnotatedType> type);

    SignatureFilter paramCount(FilterOp<Integer> count);

    SignatureFilter parameter(int index, FilterOp<AnnotatedType> param);

    SignatureFilter error(FilterOp<AnnotatedType> exception);
}
