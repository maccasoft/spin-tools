/*
 * Copyright (c) 2022 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import com.maccasoft.propeller.Formatter;
import com.maccasoft.propeller.model.Token;

public class Spin2Formatter extends Formatter {

    public Spin2Formatter() {

    }

    @Override
    public String format(String text) {
        return format(new Spin2TokenStream(text));
    }

    @Override
    protected boolean isBlockStart(Token token) {
        return Spin2Model.isBlockStart(token.getText());
    }

    @Override
    protected boolean pasmCondition(Token token) {
        return Spin2Model.isPAsmCondition(token.getText());
    }

    @Override
    protected boolean pasmInstruction(Token token) {
        if (Spin2Model.isPAsmInstruction(token.getText())) {
            return true;
        }
        if ("debug".equalsIgnoreCase(token.getText())) {
            return true;
        }
        return false;
    }

    @Override
    protected boolean pasmModifier(Token token) {
        return Spin2Model.isPAsmModifier(token.getText());
    }

    @Override
    protected Token nextPAsmToken() {
        Token token = stream.nextToken();
        if ("@".equals(token.getText()) || "@@".equals(token.getText())) {
            Token nextToken = stream.peekNext();
            if (".".equals(token.getText()) && token.isAdjacent(nextToken)) {
                token = token.merge(stream.nextToken());
                nextToken = stream.peekNext();
            }
            if (token.isAdjacent(nextToken) && nextToken.type != Token.OPERATOR) {
                token = token.merge(stream.nextToken());
            }
        }
        else if (".".equals(token.getText())) {
            Token nextToken = stream.peekNext();
            if (token.isAdjacent(nextToken) && nextToken.type != Token.OPERATOR) {
                token = token.merge(stream.nextToken());
            }
        }
        return token;
    }

}
