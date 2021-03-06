/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Georgy Vlasov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.tendiwa.inflectible;

import com.google.common.collect.ImmutableMap;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link BasicVocabulary}.
 * @author Georgy Vlasov (suseika@tendiwa.org)
 * @version $Id$
 * @since 0.1.0
 */
public final class BasicVocabularyTest {
    /**
     * {@link BasicVocabulary} can return a lexeme by its identifier if
     * it is present in the vocabulary.
     * @throws Exception If fails
     */
    @Test
    public void returnsLexemeByIdentifier() throws Exception {
        final Concept concept = new ValidatedConcept("DUDE");
        MatcherAssert.assertThat(
            new BasicVocabulary(
                ImmutableMap.of(
                    concept,
                    new SingleFormLexeme("dude")
                )
            )
                .hasLexeme(concept),
            CoreMatchers.is(true)
        );
    }

    /**
     * {@link BasicVocabulary} can tell if there isn't a lexeme with particular
     * name.
     * @throws Exception If fails
     */
    @Test
    public void seesAbsenceOfLexeme() throws Exception {
        MatcherAssert.assertThat(
            new BasicVocabulary(
                ImmutableMap.of(
                    new ValidatedConcept("GUY"),
                    new SingleFormLexeme("guy")
                )
            )
                .hasLexeme(new ValidatedConcept("MAN")),
            CoreMatchers.is(false)
        );
    }

    /**
     * {@link BasicVocabulary} can fail it it tries to return a lexeme that
     * isn't there.
     * @throws Exception If doesn't have a lexeme with particular name
     */
    @Test(expected = Exception.class)
    public void failsIfTriesToReturnMissingLexeme() throws Exception {
        new BasicVocabulary(
            ImmutableMap.of(
                new ValidatedConcept("TABLE"),
                new SingleFormLexeme("table")
            )
        )
            .lexeme(new ValidatedConcept("CHAIR"));
    }

    /**
     * {@link BasicVocabulary} can tell if it contains a lexeme for a
     * particular concept.
     * @throws Exception If fails
     */
    @Test
    public void mayContainLexeme() throws Exception {
        final Concept name = () -> "dude.hello";
        MatcherAssert.assertThat(
            new BasicVocabulary(
                ImmutableMap.of(
                    name,
                    Mockito.mock(Lexeme.class)
                )
            )
                .hasLexeme(name),
            CoreMatchers.is(true)
        );
    }

    /**
     * {@link BasicVocabulary} can tell if it doesn't contain a lexeme for a
     * particular concept.
     * @throws Exception If fails
     */
    @Test
    public void mayBeMissingLexeme() throws Exception {
        MatcherAssert.assertThat(
            new BasicVocabulary(
                ImmutableMap.of(
                    () -> "name.of.lexeme",
                    Mockito.mock(Lexeme.class)
                )
            )
                .hasLexeme(() -> "different.name"),
            CoreMatchers.is(false)
        );
    }
}
