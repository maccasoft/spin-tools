/*
 * Copyright (c) 2021-22 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.maccasoft.propeller.model.ConstantNode;
import com.maccasoft.propeller.model.ConstantsNode;
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.DataNode;
import com.maccasoft.propeller.model.ErrorNode;
import com.maccasoft.propeller.model.ExpressionNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.ObjectNode;
import com.maccasoft.propeller.model.ObjectsNode;
import com.maccasoft.propeller.model.StatementNode;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.VariableNode;
import com.maccasoft.propeller.model.VariablesNode;

public class Spin2Parser {

    private static final Set<String> sections = new HashSet<String>(Arrays.asList(new String[] {
        "CON", "VAR", "OBJ", "PUB", "PRI", "DAT"
    }));

    final Spin2TokenStream stream;

    Node root;

    public Spin2Parser(Spin2TokenStream stream) {
        this.stream = stream;
    }

    public Node parse() {
        Token token;
        ConstantsNode defaultNode = null;

        root = new Node();

        while ((token = stream.peekNext()).type != Token.EOF) {
            if (token.type == Token.EOF) {
                break;
            }
            if (token.type == Token.NL) {
                stream.nextToken();
            }
            else {
                if ("VAR".equalsIgnoreCase(token.getText())) {
                    parseVar();
                }
                else if ("OBJ".equalsIgnoreCase(token.getText())) {
                    parseObj();
                }
                else if ("PUB".equalsIgnoreCase(token.getText()) || "PRI".equalsIgnoreCase(token.getText())) {
                    parseMethod();
                }
                else if ("DAT".equalsIgnoreCase(token.getText())) {
                    parseDat();
                }
                else if ("CON".equalsIgnoreCase(token.getText())) {
                    ConstantsNode node = new ConstantsNode(root, stream.nextToken());
                    parseConstants(node);
                }
                else {
                    if (defaultNode == null) {
                        defaultNode = new ConstantsNode(root);
                    }
                    parseConstants(defaultNode);
                }
            }
        }

        return root;
    }

    void parseConstants(ConstantsNode parent) {
        int state = 1;
        ConstantNode child = null;

        Token token;
        while ((token = nextToken()).type != Token.EOF) {
            if (token.type == Token.NL) {
                if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                    return;
                }
                child = null;
                state = 0;
                continue;
            }
            switch (state) {
                case 0:
                    state = 1;
                    // fall-through
                case 1:
                    if (",".equals(token.getText())) {
                        child = null;
                        break;
                    }
                    if ("#".equals(token.getText())) {
                        child = new ConstantNode(parent);
                        child.start = new ExpressionNode(child);
                        state = 2;
                    }
                    if (child == null) {
                        child = new ConstantNode(parent, token);
                        state = 4;
                    }
                    child.addToken(token);
                    break;
                case 2:
                    if (",".equals(token.getText())) {
                        child = null;
                        state = 1;
                        break;
                    }
                    child.addToken(token);
                    if ("[".equals(token.getText())) {
                        child.step = new ExpressionNode(child);
                        state = 3;
                        break;
                    }
                    child.start.addToken(token);
                    break;
                case 3:
                    if (",".equals(token.getText())) {
                        child = null;
                        state = 1;
                        break;
                    }
                    child.addToken(token);
                    if ("]".equals(token.getText())) {
                        child = null;
                        state = 1;
                        break;
                    }
                    child.step.addToken(token);
                    break;
                case 4:
                    if (",".equals(token.getText())) {
                        child = null;
                        state = 1;
                        break;
                    }
                    child.addToken(token);
                    if ("=".equals(token.getText())) {
                        child.expression = new ExpressionNode(child);
                        state = 5;
                    }
                    if ("[".equals(token.getText())) {
                        child.multiplier = new ExpressionNode(child);
                        state = 6;
                        break;
                    }
                    break;
                case 5:
                    if (",".equals(token.getText())) {
                        child = null;
                        state = 1;
                        break;
                    }
                    child.addToken(token);
                    child.expression.addToken(token);
                    break;
                case 6:
                    if (",".equals(token.getText())) {
                        child = null;
                        state = 1;
                        break;
                    }
                    child.addToken(token);
                    if ("]".equals(token.getText())) {
                        child = null;
                        state = 1;
                        break;
                    }
                    child.multiplier.addToken(token);
                    break;
            }
        }
    }

    void parseVar() {
        Node parent = new VariablesNode(root);
        parent.addToken(stream.nextToken());

        int state = 1;
        VariableNode node = null;

        Token token;
        while ((token = nextToken()).type != Token.EOF) {
            if (token.type == Token.NL) {
                if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                    return;
                }
                node = null;
                state = 0;
                continue;
            }
            switch (state) {
                case 0:
                    state = 1;
                    // fall-through
                case 1:
                    if (Spin2Model.isType(token.getText())) {
                        node = new VariableNode(parent);
                        node.addToken(token);
                        node.type = token;
                        state = 2;
                        break;
                    }
                    // fall-through
                case 2:
                    if (",".equals(token.getText())) {
                        node = null;
                        state = 1;
                        break;
                    }
                    if (node == null) {
                        node = new VariableNode(parent);
                    }
                    if (node.identifier == null) {
                        node.identifier = token;
                    }
                    node.addToken(token);
                    if ("[".equals(token.getText())) {
                        node.size = new ExpressionNode(node);
                        state = 3;
                        break;
                    }
                    break;

                case 3:
                    node.addToken(token);
                    if ("]".equals(token.getText())) {
                        state = 2;
                        break;
                    }
                    node.size.addToken(token);
                    break;
            }
        }
    }

    void parseObj() {
        Node parent = new ObjectsNode(root);
        parent.addToken(stream.nextToken());

        ObjectNode object = null;
        int state = 1;

        Token token;
        while ((token = nextToken()).type != Token.EOF) {
            if (token.type == Token.NL) {
                if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                    return;
                }
                state = 0;
                continue;
            }
            switch (state) {
                case 0:
                    state = 1;
                    // fall-through
                case 1:
                    object = new ObjectNode(parent);
                    object.addToken(token);
                    object.name = token;
                    state = 2;
                    break;
                case 2:
                    if ("[".equals(token.getText())) {
                        object.addToken(token);
                        object.count = new ExpressionNode(object);
                        state = 5;
                        break;
                    }
                    // fall-through
                case 3:
                    object.addToken(token);
                    if (":".equals(token.getText())) {
                        state = 4;
                        break;
                    }
                    break;
                case 4:
                    object.addToken(token);
                    object.file = token;
                    state = 8;
                    break;

                case 5:
                    object.addToken(token);
                    if ("]".equals(token.getText())) {
                        state = 3;
                        break;
                    }
                    object.count.addToken(token);
                    break;

                case 8:
                    object.addToken(token);
                    break;
            }
        }
    }

    void parseMethod() {
        MethodNode node = new MethodNode(root, stream.nextToken());

        int state = 1;
        Node parent = node;
        Node child = node;
        MethodNode.ParameterNode param = null;
        MethodNode.ReturnNode ret = null;
        MethodNode.LocalVariableNode local = null;
        ErrorNode error = null;

        Token token;
        while ((token = nextToken()).type != Token.EOF) {
            if (token.type == Token.NL) {
                if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                    return;
                }
                break;
            }
            node.addToken(token);
            switch (state) {
                case 1:
                    node.name = token;
                    state = 2;
                    break;
                case 2:
                    if ("(".equals(token.getText())) {
                        state = 4;
                        break;
                    }
                    if (":".equals(token.getText())) {
                        state = 7;
                        break;
                    }
                    if ("|".equals(token.getText())) {
                        state = 9;
                        break;
                    }
                    if (error == null) {
                        error = new ErrorNode(node);
                    }
                    error.addToken(token);
                    break;

                case 4:
                    if (",".equals(token.getText())) {
                        param = null;
                        break;
                    }
                    if (")".equals(token.getText())) {
                        state = 6;
                        break;
                    }
                    if (param == null) {
                        param = new MethodNode.ParameterNode(node);
                        param.identifier = token;
                    }
                    param.addToken(token);
                    break;

                case 6:
                    if (":".equals(token.getText())) {
                        state = 7;
                        break;
                    }
                    if ("|".equals(token.getText())) {
                        state = 9;
                        break;
                    }
                    if (error == null) {
                        error = new ErrorNode(node);
                    }
                    error.addToken(token);
                    break;

                case 7:
                    ret = new MethodNode.ReturnNode(node);
                    ret.identifier = token;
                    ret.addToken(token);
                    state = 8;
                    break;
                case 8:
                    if (",".equals(token.getText())) {
                        state = 7;
                        break;
                    }
                    else if ("|".equals(token.getText())) {
                        state = 9;
                        break;
                    }
                    ret.addToken(token);
                    break;

                case 9:
                    local = new MethodNode.LocalVariableNode(node);
                    if (Spin2Model.isType(token.getText())) {
                        local.type = token;
                        local.addToken(token);
                        state = 10;
                        break;
                    }
                    // fall-through
                case 10:
                    local.identifier = token;
                    local.addToken(token);
                    state = 11;
                    break;
                case 11:
                    if (",".equals(token.getText())) {
                        state = 9;
                        break;
                    }
                    if (":".equals(token.getText())) {
                        state = 7;
                        break;
                    }
                    local.addToken(token);
                    if ("[".equals(token.getText())) {
                        local.size = new ExpressionNode(local);
                        state = 12;
                        break;
                    }
                    break;
                case 12:
                    local.addToken(token);
                    if ("]".equals(token.getText())) {
                        state = 11;
                        break;
                    }
                    local.size.addToken(token);
                    break;
            }
        }

        boolean skipComments = false;

        while ((token = nextToken(skipComments)).type != Token.EOF) {
            if (token.type == Token.NL) {
                if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                    return;
                }
                continue;
            }

            if (token.type == Token.COMMENT) {
                if (token.getText().startsWith("''")) {
                    node.addDocument(token);
                    continue;
                }
                skipComments = true;
                continue;
            }
            else if (token.type == Token.BLOCK_COMMENT) {
                if (token.getText().startsWith("{{")) {
                    node.addDocument(token);
                    continue;
                }
                skipComments = true;
                continue;
            }
            else {
                skipComments = true;
            }

            if (child.getTokens().size() != 0) {
                while (token.column < child.getToken(0).column && child.getParent() != node) {
                    child = child.getParent();
                    parent = child.getParent();
                }

                if (token.column > child.getToken(0).column) {
                    if (Spin2Model.isBlockStart(child.getToken(0).getText())) {
                        parent = child;
                    }
                    else if (child.getText().endsWith(":")) {
                        parent = child;
                    }
                }
            }

            if ("ORG".equalsIgnoreCase(token.getText())) {
                parseInlineCode(parent, token);
            }
            else {
                child = parseStatement(parent, token);
                if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                    return;
                }
            }
        }
    }

    Node parseStatement(Node parent, Token token) {
        boolean isCase = parent.getTokens().size() != 0 && ("CASE".equalsIgnoreCase(parent.getStartToken().getText()) || "CASE_FAST".equalsIgnoreCase(parent.getStartToken().getText()));
        Node statement = new StatementNode(parent);

        while (true) {
            statement.addToken(token);
            if (isCase && ":".equals(token.getText())) {
                break;
            }
            token = nextToken();
            if (token.type == Token.NL || token.type == Token.EOF) {
                break;
            }
        }

        return statement;
    }

    void parseInlineCode(Node parent, Token token) {

        while (true) {
            parseDatLine(parent, token);

            token = nextPAsmToken();
            if (token.type == Token.EOF) {
                return;
            }
            if ("END".equalsIgnoreCase(token.getText())) {
                Node statement = new StatementNode(parent);
                statement.addToken(token);
                return;
            }
        }
    }

    void parseDat() {
        Node node = new DataNode(root);
        node.addToken(stream.nextToken());

        boolean skipComments = false;

        Token token;
        while ((token = nextPAsmToken(skipComments)).type != Token.EOF) {
            if (token.type == Token.NL) {
                if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                    return;
                }
                continue;
            }
            if (token.type == Token.COMMENT) {
                node.addDocument(token);
                continue;
            }
            if (token.type == Token.BLOCK_COMMENT) {
                continue;
            }
            parseDatLine(node, token);
            if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                return;
            }
            skipComments = true;
        }
    }

    void parseDatLine(Node node, Token token) {
        int state = 0;
        DataLineNode parent = new DataLineNode(node);
        DataLineNode.ParameterNode parameter = null;

        while (true) {
            if (state != 0) {
                token = nextPAsmToken();
            }
            if (token.type == Token.EOF || token.type == Token.NL) {
                return;
            }
            parent.addToken(token);
            switch (state) {
                case 0:
                    state = 1;
                    // fall-through
                case 1:
                    if ("debug".equalsIgnoreCase(token.getText())) {
                        parent.instruction = token;
                        state = 9;
                        break;
                    }
                    if (Spin2Model.isPAsmCondition(token.getText())) {
                        parent.condition = token;
                        state = 3;
                        break;
                    }
                    if (Spin2Model.isPAsmInstruction(token.getText())) {
                        parent.instruction = token;
                        state = 4;
                        break;
                    }
                    parent.label = token;
                    state = 2;
                    break;
                case 2:
                    if (Spin2Model.isPAsmCondition(token.getText())) {
                        parent.condition = token;
                        state = 3;
                        break;
                    }
                    // fall-through
                case 3:
                    parent.instruction = token;
                    state = "debug".equalsIgnoreCase(token.getText()) ? 9 : 4;
                    break;
                case 4:
                    if (Spin2Model.isPAsmModifier(token.getText())) {
                        parent.modifier = new Node(parent);
                        parent.modifier.addToken(token);
                        state = 6;
                        break;
                    }
                    parameter = new DataLineNode.ParameterNode(parent);
                    parameter.addToken(token);
                    parent.parameters.add(parameter);
                    state = 5;
                    break;
                case 5:
                    if (",".equals(token.getText())) {
                        parameter = new DataLineNode.ParameterNode(parent);
                        parent.parameters.add(parameter);
                        break;
                    }
                    if (Spin2Model.isPAsmModifier(token.getText())) {
                        parent.modifier = new Node(parent);
                        parent.modifier.addToken(token);
                        state = 6;
                        break;
                    }
                    if ("[".equals(token.getText())) {
                        parameter.count = new ExpressionNode();
                        parameter.addChild(parameter.count);
                        state = 7;
                        break;
                    }
                    parameter.addToken(token);
                    break;
                case 6:
                    parent.modifier.addToken(token);
                    break;
                case 7:
                    if ("]".equals(token.getText())) {
                        state = 5;
                        break;
                    }
                    parameter.count.addToken(token);
                    break;
            }
        }
    }

    Token nextToken() {
        return nextToken(true);
    }

    Token nextToken(boolean skipComments) {
        Token token = stream.nextToken(skipComments);
        if (token.type == Token.NL) {
            while (stream.peekNext(false).type == Token.NL) {
                stream.nextToken();
            }
        }
        else if ("@".equals(token.getText())) {
            Token nextToken = stream.peekNext(true);
            if ("@".equals(nextToken.getText()) && token.isAdjacent(nextToken)) {
                token = token.merge(stream.nextToken(true));
                nextToken = stream.peekNext(true);
            }
            if (token.isAdjacent(nextToken) && nextToken.type != Token.OPERATOR) {
                token = token.merge(stream.nextToken(true));
                if (nextToken.type == Token.STRING) {
                    token.type = Token.STRING;
                }
            }
        }
        else if (".".equals(token.getText())) {
            Token nextToken = stream.peekNext(true);
            if (token.isAdjacent(nextToken) && nextToken.type != Token.OPERATOR) {
                token = token.merge(stream.nextToken(true));
            }
        }
        return token;
    }

    Token nextPAsmToken() {
        return nextPAsmToken(true);
    }

    Token nextPAsmToken(boolean skipComments) {
        Token token = stream.nextToken(skipComments);
        if (token.type == Token.NL) {
            while (stream.peekNext().type == Token.NL) {
                stream.nextToken();
            }
        }
        else if ("@".equals(token.getText())) {
            Token nextToken = stream.peekNext(true);
            if ("@".equals(nextToken.getText()) && token.isAdjacent(nextToken)) {
                token = token.merge(stream.nextToken(true));
                nextToken = stream.peekNext(true);
            }
            if (".".equals(nextToken.getText()) && token.isAdjacent(nextToken)) {
                token = token.merge(stream.nextToken(true));
                nextToken = stream.peekNext(true);
            }
            if (token.isAdjacent(nextToken) && nextToken.type != Token.OPERATOR) {
                token = token.merge(stream.nextToken(true));
            }
        }
        else if (".".equals(token.getText())) {
            Token nextToken = stream.peekNext(true);
            if (token.isAdjacent(nextToken) && nextToken.type != Token.OPERATOR) {
                token = token.merge(stream.nextToken(true));
            }
        }
        return token;
    }

}
