/*
 * Copyright (c) 2022 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1;

import com.maccasoft.propeller.Formatter;
import com.maccasoft.propeller.model.Token;

public class Spin1Formatter extends Formatter {

    public Spin1Formatter() {
    }

    @Override
    public String format(String text) {
        return format(new Spin1TokenStream(text));
    }

    @Override
    protected boolean pasmLabel(Token token) {
        if (Spin1Model.isCondition(token.getText())) {
            return false;
        }
        if (Spin1Model.isInstruction(token.getText())) {
            return false;
        }
        if (Spin1Model.isPAsmType(token.getText())) {
            return false;
        }
        return true;
    }

    @Override
    protected boolean pasmCondition(Token token) {
        return Spin1Model.isCondition(token.getText());
    }

    @Override
    protected boolean pasmInstruction(Token token) {
        if (Spin1Model.isInstruction(token.getText())) {
            return true;
        }
        if (Spin1Model.isPAsmType(token.getText())) {
            return true;
        }
        return false;
    }

    @Override
    protected boolean pasmModifier(Token token) {
        return Spin1Model.isModifier(token.getText());
    }

}
