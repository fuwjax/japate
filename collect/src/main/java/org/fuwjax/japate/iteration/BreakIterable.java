package org.fuwjax.japate.iteration;

import java.text.BreakIterator;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fuwjax.japate.iteration.iterator.BoundaryIterator;



/**
 * BreakIterable provides iterables backed by a BreakIterator, which for reasons that escape me,
 * does not actually implement Iterator.
 */
public class BreakIterable implements Reiterable<String> {
    private final Supplier<BreakIterator> iters;
    private final String input;


    private BreakIterable(Supplier<BreakIterator> iters, String input) {
        this.iters = iters;
        this.input = input;}

    @Override
    public Iterator<String> iterator() {
        return new BoundaryIterator(iters.get(), input);
    }

    /**
     * Iterates over character breaks for the current locale.
     * @param input the input
     * @return an interable over characters
     */
    public static Reiterable<String> characters(String input) {
        return new BreakIterable(BreakIterator::getCharacterInstance, input);
    }

    /**
     * Iterates over word breaks for the current locale.
     * @param input the input
     * @return an interable over words
     */
    public static Reiterable<String> words(String input) {
        return new BreakIterable(BreakIterator::getWordInstance, input);
    }

    /**
     * Iterates over sentence breaks for the current locale.
     * @param input the input
     * @return an interable over sentences
     */
    public static Reiterable<String> sentences(String input) {
        return new BreakIterable(BreakIterator::getSentenceInstance, input);
    }

    /**
     * Iterates over line breaks for the current locale.
     * @param input the input
     * @return an interable over lines
     */
    public static Reiterable<String> lines(String input) {
        return new BreakIterable(BreakIterator::getLineInstance, input);
    }

    /**
     * Iterates over character breaks for the given locale.
     * @param locale the character set
     * @param input the input
     * @return an interable over characters
     */
    public static Reiterable<String> characters(Locale locale, String input) {
        return new BreakIterable(() -> BreakIterator.getCharacterInstance(locale), input);
    }

    /**
     * Iterates over word breaks for the given locale.
     * @param locale the character set
     * @param input the input
     * @return an interable over words
     */
    public static Reiterable<String> words(Locale locale, String input) {
        return new BreakIterable(() -> BreakIterator.getWordInstance(locale), input);
    }

    /**
     * Iterates over sentence breaks for the given locale.
     * @param locale the character set
     * @param input the input
     * @return an interable over sentences
     */
    public static Reiterable<String> sentences(Locale locale, String input) {
        return new BreakIterable(() -> BreakIterator.getSentenceInstance(locale), input);
    }

    /**
     * Iterates over line breaks for the given locale.
     * @param locale the character set
     * @param input the input
     * @return an interable over lines
     */
    public static Reiterable<String> lines(Locale locale, String input) {
        return new BreakIterable(() -> BreakIterator.getLineInstance(locale), input);
    }

    public static Function<String, String> escape(String special, Map<String, String> replacements) {
        return escape(special, replacements, s -> "\\\\" + s);
    }

    public static Function<String, String> escape(String special, Map<String, String> replacements, Function<String, String> defaultMapping) {
        Pattern p = Pattern.compile("[" + Pattern.quote(special) + "]", Pattern.MULTILINE);
        return input -> {
            Matcher m = p.matcher(input);
            StringBuilder sb = new StringBuilder();
            while (m.find()) {
                m.appendReplacement(sb, Matcher.quoteReplacement(replacements.getOrDefault(m.group(), defaultMapping.apply(m.group()))));
            }
            m.appendTail(sb);
            return sb.toString();
        };
    }
}
