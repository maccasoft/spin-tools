/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1;

import java.util.ArrayList;
import java.util.List;

import com.maccasoft.propeller.model.Token;

public class Spin1BytecodeExpression {

    Token token;
    List<Spin1BytecodeExpression> childs = new ArrayList<Spin1BytecodeExpression>();

    public Spin1BytecodeExpression(Token token) {
        this.token = token;
    }

    public void addChild(Spin1BytecodeExpression node) {
        childs.add(node);
    }

    public List<Spin1BytecodeExpression> getChilds() {
        return childs;
    }

    public String getText() {
        return token.getText();
    }

    @Override
    public String toString() {
        return token.getText();
    }

}