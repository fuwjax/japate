package org.fuwjax.japate.test;

import static org.fuwjax.japate.test.Matchers.iterableOf;

import java.util.Map;
import java.util.function.Function;

import org.fuwjax.japate.iteration.BreakIterable;
import org.junit.jupiter.api.Test;

public class BreakIterableTest {
    @Test 
    public void testCharacter() {
        When.you(BreakIterable.characters("abcdef")).expect(iterableOf("a", "b", "c", "d", "e", "f"));
    }

    @Test 
    public void testEscape() {
        Function<String, String> escape = BreakIterable.escape("\r\n\t\"\\", Map.of("\r", "", "\n", "\\n", "\t", "\\t"));
        When.you(escape.apply("abc")).expect("abc");
        When.you(escape.apply("Hello\nWorld!")).expect("Hello\\nWorld!");
    }
}
