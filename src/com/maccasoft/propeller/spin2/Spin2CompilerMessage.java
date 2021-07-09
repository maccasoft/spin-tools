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

import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.Token;

public class Spin2CompilerMessage extends RuntimeException {

    private static final long serialVersionUID = 3321056003143890537L;

    public static final int NONE = 0;
    public static final int WARNING = 1;
    public static final int ERROR = 2;

    int type;
    int line;
    int column;

    Token startToken;
    Token stopToken;

    public Spin2CompilerMessage(String message, Node node) {
        super(message);
        this.type = ERROR;
        this.line = node.getStartToken().line;
        this.column = node.getStartToken().column;
        this.startToken = node.getStartToken();
        this.stopToken = node.getStopToken();
    }

    public Spin2CompilerMessage(String message, Token token) {
        super(message);
        this.type = ERROR;
        this.line = token.line;
        this.column = token.column;
        this.startToken = token;
        this.stopToken = token;
    }

    public int getType() {
        return type;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public Token getStartToken() {
        return startToken;
    }

    public Token getStopToken() {
        return stopToken;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(line);
        sb.append(":");
        sb.append(column);
        sb.append(": ");
        if (type == WARNING) {
            sb.append("warning");
        }
        else if (type == ERROR) {
            sb.append("error");
        }
        else {
            sb.append("note");
        }
        sb.append(" : ");
        sb.append(getMessage());
        return sb.toString();
    }

}
