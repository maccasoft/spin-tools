/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.maccasoft.propeller.Formatter;
import com.maccasoft.propeller.model.Token;

public class Spin1Formatter extends Formatter {

    static Set<String> builtInConstants = new HashSet<>(Arrays.asList(
        "_CLKMODE",
        "_CLKFREQ",
        "_XINFREQ",

        "CLKMODE",
        "CLKFREQ",

        "TRUE",
        "FALSE",
        "POSX",
        "NEGX",
        "PI",

        "RCFAST",
        "RCSLOW",
        "XINPUT",

        "XTAL1",
        "XTAL2",
        "XTAL3",

        "PLL1X",
        "PLL2X",
        "PLL4X",
        "PLL8X",
        "PLL16X"
    ));

    public Spin1Formatter() {

    }

    @Override
    public String format(String text) {
        return format(new Spin1TokenStream(text));
    }

    @Override
    protected boolean isBlockStart(Token token) {
        return Spin1Model.isBlockStart(token.getText());
    }

    @Override
    protected boolean pasmCondition(Token token) {
        return Spin1Model.isPAsmCondition(token.getText());
    }

    @Override
    protected boolean pasmInstruction(Token token) {
        if (Spin1Model.isPAsmInstruction(token.getText())) {
            return true;
        }
        return false;
    }

    @Override
    protected boolean pasmModifier(Token token) {
        return Spin1Model.isPAsmModifier(token.getText());
    }

    @Override
    protected boolean isBuiltInConstant(Token token) {
        return builtInConstants.contains(token.getText().toUpperCase());
    }

    @Override
    protected Token nextPAsmToken() {
        Token token = stream.nextToken();
        if ("@".equals(token.getText()) || "@@".equals(token.getText())) {
            Token nextToken = stream.peekNext();
            if (":".equals(token.getText()) && token.isAdjacent(nextToken)) {
                token = token.merge(stream.nextToken());
                nextToken = stream.peekNext();
            }
            if (token.isAdjacent(nextToken) && nextToken.type == Token.KEYWORD) {
                token = token.merge(stream.nextToken());
            }
        }
        else if (":".equals(token.getText())) {
            Token nextToken = stream.peekNext();
            if (token.isAdjacent(nextToken) && nextToken.type == Token.KEYWORD) {
                token = token.merge(stream.nextToken());
            }
        }
        return token;
    }

}
