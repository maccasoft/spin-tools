/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.io.File;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.Token;

public class CompilerException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -5865769905704184825L;

    public static final int NONE = 0;
    public static final int WARNING = 1;
    public static final int ERROR = 2;

    public File file;

    public int type;
    public int line;
    public int column;

    Token startToken;
    Token stopToken;

    List<CompilerException> childs = new ArrayList<CompilerException>();

    public CompilerException() {

    }

    public CompilerException(List<CompilerException> childs) {
        this.childs.addAll(childs);
    }

    public CompilerException(int type, String message, Object data) {
        this(type, null, message, null, data);
    }

    public CompilerException(int type, File file, String message, Object data) {
        this(type, file, message, null, data);
    }

    public CompilerException(String message, Object data) {
        this(ERROR, null, message, null, data);
    }

    public CompilerException(File file, String message, Object data) {
        this(ERROR, file, message, null, data);
    }

    public CompilerException(Exception cause, Object data) {
        this(ERROR, null, cause.getMessage(), cause, data);
    }

    public CompilerException(File file, Exception cause, Object data) {
        this(ERROR, file, cause.getMessage(), cause, data);
    }

    public CompilerException(int type, File file, String message, Exception cause, Object data) {
        super(message, cause);

        this.file = file;
        this.type = type;
        this.line = 1;

        if (data instanceof Node node) {
            this.line = node.getStartToken().line + 1;
            this.column = node.getStartToken().column;
            this.startToken = node.getStartToken();
            this.stopToken = node.getStopToken();
        }
        else if (data instanceof Token token) {
            this.line = token.line + 1;
            this.column = token.column;
            this.startToken = token;
            this.stopToken = token;
        }
        else if (data instanceof List<?> c) {
            if (!c.isEmpty()) {
                Object first = c.get(0);
                Object last = c.get(c.size() - 1);
                if ((first instanceof Token) && (last instanceof Token)) {
                    this.startToken = (Token) first;
                    this.stopToken = (Token) last;
                    this.line = this.startToken.line + 1;
                    this.column = this.startToken.column;
                }
            }
        }
    }

    public File getFile() {
        return file;
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

    public void addMessage(CompilerException msg) {
        childs.add(msg);
        if (msg.type == ERROR) {
            type = ERROR;
        }
    }

    public boolean hasChilds() {
        return !childs.isEmpty();
    }

    public CompilerException[] getChilds() {
        return childs.toArray(new CompilerException[0]);
    }

    @Override
    public int hashCode() {
        return Objects.hash(childs, column, file, line, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CompilerException other = (CompilerException) obj;
        return Objects.equals(childs, other.childs) && column == other.column && Objects.equals(file, other.file) && line == other.line && type == other.type;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        String msg = getMessage();
        if (msg != null) {
            if (file != null) {
                sb.append(file.getName());
                sb.append(": ");
                sb.append(line);
                sb.append(":");
                sb.append(column);
                sb.append(" : ");
            }
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
            sb.append(msg);
        }

        if (!childs.isEmpty()) {
            for (CompilerException e : childs) {
                if (!sb.isEmpty()) {
                    sb.append(System.lineSeparator());
                }
                sb.append(e.toString());
            }
        }

        return sb.toString();
    }

}
