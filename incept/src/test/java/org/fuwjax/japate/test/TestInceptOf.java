package org.fuwjax.japate.test;

import org.fuwjax.japate.incept.Action;
import org.fuwjax.japate.incept.Incept;
import org.fuwjax.japate.incept.PerformanceFailureException;
import org.junit.jupiter.api.Test;

public class TestInceptOf {
    public static class TestTarget {
        public String name;
        public int age;
        public final long timestamp = 100;
        protected String id = "1234";

        public String age(String prefix) {
            return prefix + " age " + age;
        }

        public String func() {
            return "zero";
        }

        public String func(String arg) {
            return "one "+ arg;
        }

        public String func(String arg1, String arg2) {
            return arg1 + " and " + arg2;
        }
    }

    @Test
    public void testFeasible() throws Exception {
        TestTarget target = new TestTarget();
        Incept incept = Incept.of(target);
        When.you(incept.getAction("name").isFeasible()).expect(true);
        When.you(incept.getAction("age").isFeasible()).expect(true);
        When.you(incept.getAction("timestamp").isFeasible()).expect(true);
        When.you(incept.getAction("id").isFeasible()).expect(false);
    }

    @Test
    public void testField() throws Exception {
        TestTarget target = new TestTarget();
        Incept incept = Incept.of(target);
        incept.getAction("name").perform("bob");
        When.you(target.name).expect("bob");
        When.you(incept.getAction("name").perform()).expect("bob");
        When.you(incept.getAction("name").perform("sue")).expect("bob");
    }

    @Test
    public void testPrimitiveField() throws Exception {
        TestTarget target = new TestTarget();
        Incept incept = Incept.of(target);
        Action<?> age = incept.getAction("age");
        age.perform(55);
        When.you(target.age).expect(55);
        When.you(age.perform()).expect(55);
        When.you(age.withResult(Integer.TYPE).perform()).expect(55);
        When.you(age.perform("it")).expect("it age 55");

        When.you(age.perform((short)55)).expect(55);
        When.youDo(() -> age.perform(55L)).expectFailure(PerformanceFailureException.class).withMessage("3 failed attempts");
    }

    @Test
    public void testFinalField() throws Exception {
        TestTarget target = new TestTarget();
        Incept incept = Incept.of(target);
        Action<?> ts = incept.getAction("timestamp");
        When.youDo(() -> ts.perform(55L)).expectFailure(PerformanceFailureException.class).withMessage("1 failed attempts");;
        When.you(target.timestamp).expect(100L);
        When.you(ts.perform()).expect(100L);
        When.you(ts.withResult(Long.TYPE).perform()).expect(100L);
    }

    @Test
    public void testMethod() throws Exception {
        TestTarget target = new TestTarget();
        Incept incept = Incept.of(target);
        Action<?> fn = incept.getAction("func");
        When.you(fn.perform()).expect("zero");
        When.you(fn.perform("day")).expect("one day");
        When.you(fn.perform("a", "b")).expect("a and b");
    }

    @Test
    public void testBean() throws Exception {
        TestTarget target = new TestTarget();
        Incept incept = Incept.of(target);
        Action<?> fn = incept.getAction("class");
        When.you(fn.perform()).expect(TestTarget.class);

        Action<?> name = Incept.of(TestTarget.class).getAction("simpleName");
        When.you(name.perform()).expect(TestTarget.class.getSimpleName());
    }
}
