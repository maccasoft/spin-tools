/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.spin1.Spin1TokenStream;
import com.maccasoft.propeller.spin2.Spin2TokenStream;

public class TokenStreamTest {

    @Test
    void testParseLines() {
        TokenStream subject = new Spin1TokenStream("""
            PUB main()
            {{ Comment }}
            
            PRI loop()
            
            """);

        SourceLine[] lines = subject.parseSourceLines();

        Assertions.assertEquals(2, lines.length);
    }

    @Test
    void testParseLinesWithContinuation() {
        TokenStream subject = new Spin2TokenStream("""
            PUB main()
            {{ Comment }}
            
            PRI loop() ...
                       | var
            
            """);

        SourceLine[] lines = subject.parseSourceLines();

        Assertions.assertEquals(2, lines.length);
    }

    @Test
    void testSourceLineGetText() {
        String text = """
            PRI loop() ...
                       | var
            """;
        TokenStream subject = new Spin2TokenStream(text);

        SourceLine[] lines = subject.parseSourceLines();

        Assertions.assertEquals(text, lines[0].getText());
    }

}
