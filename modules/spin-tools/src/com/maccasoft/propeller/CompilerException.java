/*
 * Copyright (c) 2021-23 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.util.ArrayList;
import java.util.List;

import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.Token;

public class CompilerException extends RuntimeException {

    private static final long serialVersionUID = -5865769905704184825L;

    public static final int NONE = 0;
    public static final int WARNING = 1;
    public static final int ERROR = 2;

    public String fileName;

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

    public CompilerException(String message, Object data) {
        this(ERROR, null, message, data);
    }

    public CompilerException(String fileName, String message, Object data) {
        this(ERROR, fileName, message, data);
    }

    public CompilerException(int type, String message, Object data) {
        this(type, null, message, data);
    }

    public CompilerException(int type, String fileName, String message, Object data) {
        super(message);
        this.fileName = fileName;
        this.type = type;
        if (data == null) {
            this.line = 1;
        }
        else if (data instanceof Node) {
            Node node = (Node) data;
            this.line = node.getStartToken().line + 1;
            this.column = node.getStartToken().column;
            this.startToken = node.getStartToken();
            this.stopToken = node.getStopToken();
        }
        else if (data instanceof Token) {
            Token token = (Token) data;
            this.line = token.line + 1;
            this.column = token.column;
            this.startToken = token;
            this.stopToken = token;
        }
        else if (data instanceof List) {
            List<?> c = (List<?>) data;
            if (c.size() != 0) {
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

    public CompilerException(String message, List<Token> list) {
        this(ERROR, null, message, list);
    }

    public CompilerException(int type, String fileName, String message, List<Token> list) {
        super(message);
        this.fileName = fileName;
        this.type = type;

        this.line = list.get(0).line + 1;
        this.column = list.get(0).column;
        this.startToken = list.get(0);
        this.stopToken = list.get(list.size() - 1);
    }

    public CompilerException(Exception cause, Object data) {
        this(ERROR, null, cause, data);
    }

    public CompilerException(String fileName, Exception cause, Object data) {
        this(ERROR, fileName, cause, data);
    }

    public CompilerException(int type, String fileName, Exception cause, Object data) {
        super(cause.getMessage(), cause);
        this.fileName = fileName;
        this.type = type;
        if (data instanceof Node) {
            Node node = (Node) data;
            this.line = node.getStartToken().line + 1;
            this.column = node.getStartToken().column;
            this.startToken = node.getStartToken();
            this.stopToken = node.getStopToken();
        }
        else if (data instanceof Token) {
            Token token = (Token) data;
            this.line = token.line + 1;
            this.column = token.column;
            this.startToken = token;
            this.stopToken = token;
        }
    }

    public String getFileName() {
        return fileName;
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
        return childs.size() != 0;
    }

    public CompilerException[] getChilds() {
        return childs.toArray(new CompilerException[0]);
    }

    public String getText() {
        StringBuilder sb = new StringBuilder();

        String msg = getMessage();
        if (msg != null) {
            if (fileName != null) {
                sb.append(fileName);
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

        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        String msg = getMessage();
        if (msg != null) {
            if (fileName != null) {
                sb.append(fileName);
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

        if (childs.size() != 0) {
            for (CompilerException e : childs) {
                if (sb.length() != 0) {
                    sb.append(System.lineSeparator());
                }
                sb.append(e.toString());
            }
        }

        return sb.toString();
    }

}
