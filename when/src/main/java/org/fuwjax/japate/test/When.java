package org.fuwjax.japate.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.function.Consumer;

import org.junit.jupiter.api.Assertions;

public class When {
    public static interface Expectation<T> {
        default T expect(Object expected) {
            return expect(a -> assertEquals(expected, a));
        }

        default T expectNull() {
            return expect(Assertions::assertNull);
        }

        T expect(Consumer<? super T> matcher);
    }

    public static interface FailableExpectation<T> extends Expectation<T> {
        default <Z extends Throwable> FailureExpectation<Z> expectFailure(Class<Z> type) {
            ChainExpectation<Z> chain = new ChainExpectation<>();
            expectFailure(err -> chain.also(assertInstanceOf(type, err)));
            return chain;
        }

        void expectFailure(Consumer<? super Throwable> matcher);
    }

    public static interface FailureExpectation<X extends Throwable> {
        default FailureExpectation<X> withMessage(String message) {
            ChainExpectation<X> chain = new ChainExpectation<>();
            is(err -> {
                assertEquals(message, err.getMessage());
                chain.also(err);
            });
            return chain;
        }

        default <Z extends Throwable> FailureExpectation<Z> causedBy(Class<Z> type) {
            ChainExpectation<Z> chain = new ChainExpectation<>();
            is(err -> chain.also(assertInstanceOf(type, err.getCause())));
            return chain;
        }

        void is(Consumer<X> matcher);
    }

    static class ChainExpectation<Z extends Throwable> implements FailureExpectation<Z> {
        private Z err;

        public ChainExpectation<Z> also(Z err) {
            this.err = err;
            return this;
        }

        @Override
        public void is(Consumer<Z> matcher) {
            matcher.accept(err);
        }
    };

    public static <T> Expectation<T> you(T actual) {
        return new Expectation<T>(){
            @Override
            public T expect(Consumer<? super T> matcher) {
                matcher.accept(actual);
                return actual;
            }
        };
    }

    public static interface Op<T> {
        T perform() throws Throwable;
    }

    public static <T> FailableExpectation<T> youDo(Op<T> op) {
        return new FailableExpectation<>(){
            @Override
            public T expect(Consumer<? super T> matcher) {
                T actual = null;
                try {
                    actual = op.perform();
                } catch (AssertionError e) {
                    throw e;
                } catch (Throwable e) {
                    fail("did not expect error", e);
                }
                matcher.accept(actual);
                return actual;
            }

            @Override
            public void expectFailure(Consumer<? super Throwable> matcher) {
                try {
                    T actual = op.perform();
                    fail("expected error, actual success "+ actual +" instead");
                } catch (AssertionError e) {
                    throw e;
                } catch (Throwable e) {
                    matcher.accept(e);
                }
            }
        };
    }
}
