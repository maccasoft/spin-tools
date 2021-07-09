/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.util.Iterator;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.spin2.EditorTokenMarker.TokenId;
import com.maccasoft.propeller.spin2.EditorTokenMarker.TokenMarker;

class Spin2TokenMarkerTest {

    @Test
    void testComments() {
        String text = ""
            + "{\n"
            + "     Constant declarations\n"
            + "}\n"
            + "CON\n"
            + "";

        Spin2TokenMarker subject = new Spin2TokenMarker();
        subject.refreshTokens(text);
        Iterator<TokenMarker> iter = subject.tokens.iterator();

        TokenMarker entry = iter.next();
        Assertions.assertEquals(TokenId.COMMENT, entry.getId());

        entry = iter.next();
        Assertions.assertEquals(TokenId.SECTION, entry.getId());
    }

    @Test
    void testConstants() {
        String text = ""
            + "CON  EnableFlow = 8                'single assignments\n"
            + "     DisableFlow = 4\n"
            + "";

        Spin2TokenMarker subject = new Spin2TokenMarker();
        subject.refreshTokens(text);
        Iterator<TokenMarker> iter = subject.tokens.iterator();

        TokenMarker entry = iter.next();
        Assertions.assertEquals(TokenId.SECTION, entry.getId());

        entry = iter.next();
        Assertions.assertEquals(TokenId.CONSTANT, entry.getId());

        entry = iter.next();
        Assertions.assertEquals(TokenId.NUMBER, entry.getId());

        entry = iter.next();
        Assertions.assertEquals(TokenId.COMMENT, entry.getId());

        entry = iter.next();
        Assertions.assertEquals(TokenId.CONSTANT, entry.getId());

        entry = iter.next();
        Assertions.assertEquals(TokenId.NUMBER, entry.getId());

        Assertions.assertEquals(2, subject.symbols.size());
        Assertions.assertEquals(TokenId.CONSTANT, subject.symbols.get("EnableFlow"));
        Assertions.assertEquals(TokenId.CONSTANT, subject.symbols.get("DisableFlow"));
    }

    @Test
    void testDuplicatedConstants() {
        String text = ""
            + "CON  EnableFlow = 8                'single assignments\n"
            + "     EnableFlow = 4\n"
            + "";

        Spin2TokenMarker subject = new Spin2TokenMarker();
        subject.refreshTokens(text);
        Iterator<TokenMarker> iter = subject.tokens.iterator();

        TokenMarker entry = iter.next();
        Assertions.assertEquals(TokenId.SECTION, entry.getId());

        entry = iter.next();
        Assertions.assertEquals(TokenId.CONSTANT, entry.getId());

        entry = iter.next();
        Assertions.assertEquals(TokenId.NUMBER, entry.getId());

        entry = iter.next();
        Assertions.assertEquals(TokenId.COMMENT, entry.getId());

        entry = iter.next();
        Assertions.assertEquals(TokenId.CONSTANT, entry.getId());

        entry = iter.next();
        Assertions.assertEquals(TokenId.NUMBER, entry.getId());
    }

    @Test
    void testGetLineTokens() {
        String text = ""
            + "CON  EnableFlow = 8                'single assignments\n"
            + "     DisableFlow = 4\n"
            + "";

        Spin2TokenMarker subject = new Spin2TokenMarker();
        subject.refreshTokens(text);

        Set<TokenMarker> result = subject.getLineTokens(0, 54);
        Iterator<TokenMarker> iter = result.iterator();

        TokenMarker entry = iter.next();
        Assertions.assertEquals(TokenId.SECTION, entry.getId());

        entry = iter.next();
        Assertions.assertEquals(TokenId.CONSTANT, entry.getId());

        entry = iter.next();
        Assertions.assertEquals(TokenId.NUMBER, entry.getId());

        entry = iter.next();
        Assertions.assertEquals(TokenId.COMMENT, entry.getId());
    }

    @Test
    void testGetLineComments() {
        String text = ""
            + "{\n"
            + "     Constant declarations\n"
            + "}\n"
            + "CON\n"
            + "";

        Spin2TokenMarker subject = new Spin2TokenMarker();
        subject.refreshTokens(text);

        Set<TokenMarker> result = subject.getLineTokens(0, 1);
        Assertions.assertEquals(1, result.size());

        result = subject.getLineTokens(2, 27);
        Assertions.assertEquals(1, result.size());

        result = subject.getLineTokens(29, 29);
        Assertions.assertEquals(1, result.size());
    }

    @Test
    void testGetLastLineTokens() {
        String text = ""
            + "{\n"
            + "     Constant declarations\n"
            + "}\n"
            + "CON\n"
            + "";

        Spin2TokenMarker subject = new Spin2TokenMarker();
        subject.refreshTokens(text);

        Set<TokenMarker> result = subject.getLineTokens(text.length(), text.length());
        Assertions.assertEquals(0, result.size());
    }

}
