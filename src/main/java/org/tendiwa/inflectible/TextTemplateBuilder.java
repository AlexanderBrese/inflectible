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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A builder used to create {@link TextTemplate}s.
 * @author Georgy Vlasov (suseika@tendiwa.org)
 * @version $Id$
 * @since 0.1
 */
public final class TextTemplateBuilder {
    /**
     * Names of the arguments of a template.
     */
    private final transient ImmutableList<String> arguments;

    /**
     * Parts of a template. Those can be either placeholders or plain text
     * parts (which may be seen as constant-value placeholders, hence the type).
     */
    private final transient List<Placeholder> parts;

    /**
     * If this builder has already been used to produce a template.
     */
    private transient boolean used;

    /**
     * Ctor.
     * @param names Names of the arguments of a template
     */
    TextTemplateBuilder(final ImmutableList<String> names) {
        this.arguments = names;
        this.parts = new ArrayList<>(
            TextTemplateBuilder.expectedPartsNumber(names)
        );
        this.used = false;
    }

    /**
     * Adds a placeholder to the body of the constructed template.
     * @param placeholder A placeholder
     * @return This builder
     */
    public TextTemplateBuilder addPlaceholder(final Placeholder placeholder) {
        this.parts.add(placeholder);
        return this;
    }

    /**
     * Adds plain text to the body of the constucted template.
     * @param text Plain text
     * @return This builder
     */
    public TextTemplateBuilder addText(final String text) {
        this.addPlaceholder((actuals, vocabulary) -> text);
        return this;
    }

    /**
     * Builds a template.
     * @return A new {@link TextTemplate}
     */
    public TextTemplate build() {
        if (this.used) {
            throw new IllegalStateException(
                "This builder has already been used"
            );
        }
        this.used = true;
        return new BasicTextTemplate(this.arguments, this.parts);
    }

    /**
     * A good approximated size for an array list that will hold the parts of
     * this text.
     * @param arguments Arguments to this text.
     * @return Good size for an array list.
     */
    private static int expectedPartsNumber(
        final ImmutableList<String> arguments
    ) {
        return arguments.size() * 2 + 1;
    }

    /**
     * {@link TextTemplate} defined by its arguments' names and a
     * heterogeneous list of its parts (placeholders and plain text chunks).
     */
    private static class BasicTextTemplate implements TextTemplate {
        /**
         * Argument names.
         */
        private final transient ImmutableList<String> arguments;
        /**
         * Heterogeneous list of template's parts (placeholders and plain text
         * chunks).
         */
        private final transient List<Placeholder> parts;

        /**
         * Ctor.
         * @param names Argument names
         * @param pieces Heterogeneous list of template's parts (placeholders
         *  and plain text chunks)
         */
        BasicTextTemplate(
            final ImmutableList<String> names,
            final List<Placeholder> pieces
        ) {
            this.arguments = names;
            this.parts = pieces;
        }

        @Override
        public String fillUp(
            final ImmutableList<Lexeme> lexemes,
            final ImmutableMap<String, Lexeme> vocabulary
        ) {
            final ImmutableMap<String, Lexeme> actualArguments =
                this.actualArguments(lexemes);
            return this.parts.stream()
                .map(part -> part.fillUp(actualArguments, vocabulary))
                .collect(Collectors.joining());
        }

        /**
         * Creates a map of actual arguments.
         * @param lexemes Lexemes passed to this template to fill it out.
         * @return Map from argument names to lexemes passed for those
         *  arguments.
         */
        private ImmutableMap<String, Lexeme> actualArguments(
            final List<Lexeme> lexemes
        ) {
            if (lexemes.size() != this.arguments.size()) {
                throw new IllegalArgumentException(
                    String.format(
                        "Wrong number of arguments. Expected: %s, actual: %s",
                        this.arguments.size(),
                        lexemes.size()
                    )
                );
            }
            final ImmutableMap.Builder<String, Lexeme> builder =
                ImmutableMap.builder();
            for (int idx = 0; idx < lexemes.size(); idx += 1) {
                builder.put(this.arguments.get(idx), lexemes.get(idx));
            }
            return builder.build();
        }
    }
}
