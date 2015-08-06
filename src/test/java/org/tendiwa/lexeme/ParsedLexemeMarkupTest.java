package org.tendiwa.lexeme;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.commons.io.IOUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.tendiwa.lexeme.antlr.WordBundleLexer;
import org.tendiwa.lexeme.antlr.WordBundleParser;
import org.tenidwa.collections.utils.Collectors;

/**
 * @since 0.1
 */
public final class ParsedLexemeMarkupTest {
    @Test
    public void seesAllComponents() throws Exception {
        final LexemeMarkup markup = new ParsedLexemeMarkup(
            new WordBundleParser(
                new CommonTokenStream(
                    new WordBundleLexer(
                        new ANTLRInputStream(
                            IOUtils.toInputStream(
                                Joiner.on('\n').join(
                                    "\"dude\" [Grammeme] {",
                                    "  dude",
                                    "  dudes [Plur]",
                                    "}"
                                )
                            )
                        )
                    )
                )
            )
                .word()
        );
        MatcherAssert.assertThat(
            markup.conceptionId(),
            CoreMatchers.equalTo("dude")
        );
        MatcherAssert.assertThat(
            markup
                .persistentGrammemes()
                .collect(Collectors.toImmutableList()),
            CoreMatchers.equalTo(ImmutableList.of("Grammeme"))
        );
        MatcherAssert.assertThat(
            markup
                .wordForms()
                .collect(Collectors.toImmutableList())
                .isEmpty(),
            CoreMatchers.is(false)
        );
    }
}
