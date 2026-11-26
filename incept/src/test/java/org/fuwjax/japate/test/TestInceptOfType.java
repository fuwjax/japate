package org.fuwjax.japate.test;

import org.fuwjax.japate.incept.Action;
import org.fuwjax.japate.incept.Incept;
import org.fuwjax.japate.incept.PerformanceFailureException;
import org.junit.jupiter.api.Test;

public class TestInceptOfType {
    public static class StaticTestTarget {
        public static String name;
        public static int age;
        public static final long timestamp = 100;
        protected static String id = "1234";

        public String altName;

        public StaticTestTarget() {
            altName = "default";
        }

        public StaticTestTarget(String altName) {
            this.altName = altName;
        }

        protected StaticTestTarget(boolean ignored) {
            this.altName = "protected";
        }

        public static String fullName() {
            return "full " + name;
        }

        public static String fullName(String name) {
            return "instant " + name;
        }

        protected static int age() {
            return age;
        }
    }

    @Test
    public void testFeasible() throws Exception {
        Incept incept = Incept.ofType(StaticTestTarget.class);
        When.you(incept.getAction("name").isFeasible()).expect(true);
        When.you(incept.getAction("age").isFeasible()).expect(true);
        When.you(incept.getAction("timestamp").isFeasible()).expect(true);
        When.you(incept.getAction("id").isFeasible()).expect(false);
        When.you(incept.getAction("altName").isFeasible()).expect(false);

        When.you(incept.getAction("age").withResult(Integer.TYPE).isFeasible()).expect(true);
        When.you(incept.getAction("age").withResult(Integer.class).isFeasible()).expect(false);

        When.you(incept.getAction("age").withParams(Integer.TYPE).isFeasible()).expect(true);
        When.you(incept.getAction("age").withParams(Integer.class).isFeasible()).expect(false);

        When.you(incept.getAction("new").isFeasible()).expect(true);
        When.you(incept.getAction("fullName").isFeasible()).expect(true);
    }

    @Test
    public void testField() throws Exception {
        Incept incept = Incept.ofType(StaticTestTarget.class);
        incept.getAction("name").perform("bob");
        When.you(StaticTestTarget.name).expect("bob");
        When.you(incept.getAction("name").perform()).expect("bob");
        When.you(incept.getAction("name").perform("sue")).expect("bob");
    }

    @Test
    public void testPrimitiveField() throws Exception {
        Incept incept = Incept.ofType(StaticTestTarget.class);
        Action<?> age = incept.getAction("age");
        age.perform(55);
        When.you(StaticTestTarget.age).expect(55);
        When.you(age.perform()).expect(55);
        When.you(age.withResult(Integer.TYPE).perform()).expect(55);

        When.you(age.perform((short)55)).expect(55);
        When.youDo(() -> age.perform(55L)).expectFailure(PerformanceFailureException.class).withMessage("2 failed attempts");
    }

    @Test
    public void testFinalField() throws Exception {
        Incept incept = Incept.ofType(StaticTestTarget.class);
        Action<?> ts = incept.getAction("timestamp");
        When.youDo(() -> ts.perform(55L)).expectFailure(PerformanceFailureException.class).withMessage("1 failed attempts");;
        When.you(StaticTestTarget.timestamp).expect(100L);
        When.you(ts.perform()).expect(100L);
        When.you(ts.withResult(Long.TYPE).perform()).expect(100L);
    }

    @Test
    public void testMethod() throws Exception {
        Incept incept = Incept.ofType(StaticTestTarget.class);
        Action<?> fn = incept.getAction("fullName");
        StaticTestTarget.name = "Moe";
        When.you(fn.perform()).expect("full Moe");
        When.you(fn.perform("Bob")).expect("instant Bob");
        When.you(fn.withParams(String.class).perform("Bob")).expect("instant Bob");
    }

    @Test
    public void testConstructor() throws Exception {
        Incept incept = Incept.ofType(StaticTestTarget.class);
        Action<StaticTestTarget> con = incept.getAction("new").withResult(StaticTestTarget.class);
        When.you(con.perform().altName).expect("default");
        When.you(con.perform("Jerry").altName).expect("Jerry");
    }
}
