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

import com.google.common.collect.ImmutableSet;
import org.antlr.v4.runtime.tree.ParseTree;
import org.tendiwa.inflectible.antlr.LexemeBundleParser;
import org.tenidwa.collections.utils.Collectors;

/**
 * {@link WordForm} parsed from an ANTLR parse tree.
 * @author Georgy Vlasov (suseika@tendiwa.org)
 * @version $Id$
 * @since 0.1
 */
public final class ParsedWordForm implements WordForm {
    /**
     * Grammar of the language of this word form.
     */
    private final transient Grammar grammar;

    /**
     * ANTLR parse tree of this word from.
     */
    private final transient LexemeBundleParser.WordFormContext ctx;

    /**
     * Ctor.
     * @param grammemes Grammar of the language of this word form
     * @param context ANTLR parse tree of this word form
     */
    ParsedWordForm(
        final Grammar grammemes,
        final LexemeBundleParser.WordFormContext context
    ) {
        this.grammar = grammemes;
        this.ctx = context;
    }

    @Override
    public String spelling() {
        return this.delegate().spelling();
    }

    @Override
    public int similarity(final ImmutableSet<Grammeme> grammemes) {
        return this.delegate().similarity(grammemes);
    }

    /**
     * Obtains grammemes from the ANTLR parse tree.
     * @return Grammemes of this word form
     */
    private ImmutableSet<Grammeme> grammemes() {
        final ImmutableSet<Grammeme> grammemes;
        if (this.ctx.grammemes() == null) {
            grammemes = ImmutableSet.of();
        } else {
            grammemes = this.ctx.grammemes().GRAMMEME()
                .stream()
                .map(ParseTree::getText)
                .map(ParsedWordForm.this.grammar::grammemeByName)
                .collect(Collectors.toImmutableSet());
        }
        return grammemes;
    }

    /**
     * Obtains spelling of this word form from the ANTLR parse tree.
     * @return Spelling of this word form
     */
    private String wordSpelling() {
        return this.ctx.WORD_FORM().getText();
    }

    // To be refactored in #47
    /**
     * Creates a word form from the {@link ParsedWordForm#ctx}.
     * @return Word form
     */
    private WordForm delegate() {
        return new BasicWordForm(this.wordSpelling(), this.grammemes());
    }
}
