/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.spin.Spin2TokenMarker.TokenId;

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
        Iterator<Entry<Token, TokenId>> iter = subject.tokens.entrySet().iterator();

        Entry<Token, TokenId> entry = iter.next();
        Assertions.assertEquals(TokenId.COMMENT, entry.getValue());

        entry = iter.next();
        Assertions.assertEquals(TokenId.SECTION, entry.getValue());
    }

    @Test
    void testConstants() {
        String text = ""
            + "CON  EnableFlow = 8                'single assignments\n"
            + "     DisableFlow = 4\n"
            + "";

        Spin2TokenMarker subject = new Spin2TokenMarker();
        subject.refreshTokens(text);
        Iterator<Entry<Token, TokenId>> iter = subject.tokens.entrySet().iterator();

        Entry<Token, TokenId> entry = iter.next();
        Assertions.assertEquals("CON", entry.getKey().getText());
        Assertions.assertEquals(TokenId.SECTION, entry.getValue());

        entry = iter.next();
        Assertions.assertEquals("EnableFlow", entry.getKey().getText());
        Assertions.assertEquals(TokenId.CONSTANT, entry.getValue());

        entry = iter.next();
        Assertions.assertEquals("8", entry.getKey().getText());
        Assertions.assertEquals(TokenId.NUMBER, entry.getValue());

        entry = iter.next();
        Assertions.assertEquals("'single assignments", entry.getKey().getText());
        Assertions.assertEquals(TokenId.COMMENT, entry.getValue());

        entry = iter.next();
        Assertions.assertEquals("DisableFlow", entry.getKey().getText());
        Assertions.assertEquals(TokenId.CONSTANT, entry.getValue());

        entry = iter.next();
        Assertions.assertEquals("4", entry.getKey().getText());
        Assertions.assertEquals(TokenId.NUMBER, entry.getValue());

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
        Iterator<Entry<Token, TokenId>> iter = subject.tokens.entrySet().iterator();

        Entry<Token, TokenId> entry = iter.next();
        Assertions.assertEquals("CON", entry.getKey().getText());
        Assertions.assertEquals(TokenId.SECTION, entry.getValue());

        entry = iter.next();
        Assertions.assertEquals("EnableFlow", entry.getKey().getText());
        Assertions.assertEquals(TokenId.CONSTANT, entry.getValue());

        entry = iter.next();
        Assertions.assertEquals("8", entry.getKey().getText());
        Assertions.assertEquals(TokenId.NUMBER, entry.getValue());

        entry = iter.next();
        Assertions.assertEquals("'single assignments", entry.getKey().getText());
        Assertions.assertEquals(TokenId.COMMENT, entry.getValue());

        entry = iter.next();
        Assertions.assertEquals("EnableFlow", entry.getKey().getText());
        Assertions.assertEquals(TokenId.ERROR, entry.getValue());

        entry = iter.next();
        Assertions.assertEquals("4", entry.getKey().getText());
        Assertions.assertEquals(TokenId.NUMBER, entry.getValue());
    }

    @Test
    void testGetLineTokens() {
        String text = ""
            + "CON  EnableFlow = 8                'single assignments\n"
            + "     DisableFlow = 4\n"
            + "";

        Spin2TokenMarker subject = new Spin2TokenMarker();
        subject.refreshTokens(text);

        Map<Token, TokenId> result = subject.getLineTokens(0, 54);
        Iterator<Entry<Token, TokenId>> iter = result.entrySet().iterator();

        Entry<Token, TokenId> entry = iter.next();
        Assertions.assertEquals("CON", entry.getKey().getText());
        Assertions.assertEquals(TokenId.SECTION, entry.getValue());

        entry = iter.next();
        Assertions.assertEquals("EnableFlow", entry.getKey().getText());
        Assertions.assertEquals(TokenId.CONSTANT, entry.getValue());

        entry = iter.next();
        Assertions.assertEquals("8", entry.getKey().getText());
        Assertions.assertEquals(TokenId.NUMBER, entry.getValue());

        entry = iter.next();
        Assertions.assertEquals("'single assignments", entry.getKey().getText());
        Assertions.assertEquals(TokenId.COMMENT, entry.getValue());
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

        Map<Token, TokenId> result = subject.getLineTokens(0, 1);
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

        Map<Token, TokenId> result = subject.getLineTokens(text.length(), text.length());
        Assertions.assertEquals(0, result.size());
    }

}
