/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1;

import com.maccasoft.propeller.model.ConstantNode;
import com.maccasoft.propeller.model.ConstantsNode;
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.DataNode;
import com.maccasoft.propeller.model.DirectiveNode;
import com.maccasoft.propeller.model.ExpressionNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.ObjectNode;
import com.maccasoft.propeller.model.ObjectsNode;
import com.maccasoft.propeller.model.Parser;
import com.maccasoft.propeller.model.RootNode;
import com.maccasoft.propeller.model.SourceLine;
import com.maccasoft.propeller.model.StatementNode;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.VariableNode;
import com.maccasoft.propeller.model.VariablesNode;

public class Spin1Parser extends Parser {

    final String text;

    RootNode root;
    Node parentNode;

    public Spin1Parser(String text) {
        this.text = text;
    }

    @Override
    public RootNode parse() {
        root = new RootNode();
        parentNode = null;

        Spin1TokenStream stream = new Spin1TokenStream(text);
        for (SourceLine sourceLine : stream.parseSourceLines()) {
            processSourceLine(sourceLine);
        }

        return root;
    }

    protected void processSourceLine(SourceLine sourceLine) {
        Token token;

        while ((token = sourceLine.peekNextToken()) != null) {
            if (token.type == Token.NL) {
                break;
            }
            if ("#".equalsIgnoreCase(token.getText())) {
                if (parentNode == null) {
                    parentNode = new ConstantsNode(root);
                }
                int index = sourceLine.getIndex();
                if (parsePreprocessor(parentNode, sourceLine)) {
                    continue;
                }
                sourceLine.setIndex(index);
            }

            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT || token.type == Token.NEXT_LINE) {
                root.addComment(sourceLine.getNextToken());
                continue;
            }
            if ("CON".equalsIgnoreCase(token.getText())) {
                parentNode = parseConBlock(new ConstantsNode(root, sourceLine.getNextToken()), sourceLine);
            }
            else if ("VAR".equalsIgnoreCase(token.getText())) {
                parentNode = parseVarBlock(new VariablesNode(root, sourceLine.getNextToken()), sourceLine);
            }
            else if ("OBJ".equalsIgnoreCase(token.getText())) {
                parentNode = parseObjBlock(new ObjectsNode(root, sourceLine.getNextToken()), sourceLine);
            }
            else if ("PUB".equalsIgnoreCase(token.getText()) || "PRI".equalsIgnoreCase(token.getText())) {
                parentNode = parseMethodBlock(new MethodNode(root, sourceLine.getNextToken()), sourceLine);
            }
            else if ("DAT".equalsIgnoreCase(token.getText())) {
                parentNode = parseDatBlock(new DataNode(root, sourceLine.getNextToken()), sourceLine);
            }
            else {
                if (parentNode == null) {
                    parentNode = new ConstantsNode(root);
                }
                if (parentNode instanceof VariablesNode) {
                    parseVariable(new VariablesNode(parentNode), sourceLine);
                }
                else if (parentNode instanceof ObjectsNode objectsNode) {
                    parseObjBlock(objectsNode, sourceLine);
                }
                else if (parentNode instanceof MethodNode) {
                    if (Spin1Model.isBlockStart(token.getText())) {
                        parentNode = parseStatement(parentNode, sourceLine);
                    }
                    else {
                        parseStatement(parentNode, sourceLine);
                    }
                }
                else if (parentNode instanceof StatementNode) {
                    while (token.column <= parentNode.getStartToken().column) {
                        parentNode = parentNode.getParent();
                        if (parentNode instanceof MethodNode) {
                            break;
                        }
                    }
                    Token parentStartToken = parentNode.getStartToken();
                    if ("CASE".equalsIgnoreCase(parentStartToken.getText())) {
                        parentNode = parseCaseStatement(parentNode, sourceLine);
                    }
                    else if (Spin1Model.isBlockStart(token.getText())) {
                        parentNode = parseStatement(parentNode, sourceLine);
                    }
                    else {
                        parseStatement(parentNode, sourceLine);
                    }
                }
                else if (parentNode instanceof DataNode dataNode) {
                    parseDatLine(dataNode, sourceLine);
                }
                else {
                    parseConstant(parentNode, sourceLine);
                }
            }
        }

        while ((token = sourceLine.getNextToken()) != null) {
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT || token.type == Token.NEXT_LINE) {
                root.addComment(token);
            }
        }
    }

    boolean parsePreprocessor(Node parent, SourceLine sourceLine) {
        Token token;

        Token firstToken = sourceLine.getNextToken();
        if (firstToken == null || !"#".equalsIgnoreCase(firstToken.getText())) {
            return false;
        }

        Token keywordToken = sourceLine.getNextToken();
        if (keywordToken == null) {
            return false;
        }

        switch (keywordToken.getText().toLowerCase()) {
            case "define": {
                DirectiveNode.DefineNode node = new DirectiveNode.DefineNode(parent);
                node.addToken(firstToken);
                node.addToken(keywordToken);
                if ((token = sourceLine.getNextToken()) != null) {
                    node.identifier = token;
                    node.addToken(token);
                }
                while ((token = sourceLine.getNextToken()) != null) {
                    if (token.type == Token.NL) {
                        break;
                    }
                    if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT || token.type == Token.NEXT_LINE) {
                        root.addComment(token);
                    }
                    node.addToken(token);
                }
                while ((token = sourceLine.getNextToken()) != null) {
                    if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT || token.type == Token.NEXT_LINE) {
                        root.addComment(token);
                    }
                }
                return true;
            }

            case "error":
            case "warning": {
                DirectiveNode node = new DirectiveNode(parent);
                node.addToken(firstToken);
                node.addToken(keywordToken);

                Token message = sourceLine.getNextToken();
                if (message != null) {
                    while ((token = sourceLine.getNextToken()) != null) {
                        if (token.type == Token.NL) {
                            break;
                        }
                        message = message.merge(token);
                    }
                    message.type = Token.STRING;
                    node.addToken(message);
                }
                while ((token = sourceLine.getNextToken()) != null) {
                    if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT || token.type == Token.NEXT_LINE) {
                        root.addComment(token);
                    }
                }
                return true;
            }

            case "ifdef":
            case "elifdef":
            case "elseifdef":
            case "ifndef":
            case "elifndef":
            case "elseifndef":
            case "else":
            case "if":
            case "elif":
            case "elseif":
            case "endif":
            case "pragma":
            case "undef": {
                DirectiveNode node = new DirectiveNode(parent);
                node.addToken(firstToken);
                node.addToken(keywordToken);
                while ((token = sourceLine.getNextToken()) != null) {
                    if (token.type == Token.NL) {
                        break;
                    }
                    if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT || token.type == Token.NEXT_LINE) {
                        root.addComment(token);
                    }
                    node.addToken(token);
                }
                while ((token = sourceLine.getNextToken()) != null) {
                    if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT || token.type == Token.NEXT_LINE) {
                        root.addComment(token);
                    }
                }
                return true;
            }
        }

        return false;
    }

    ConstantsNode parseConBlock(ConstantsNode node, SourceLine sourceLine) {
        Token token;

        while ((token = sourceLine.peekNextToken()) != null) {
            if (token.type == Token.NL) {
                break;
            }
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT) {
                if (node.getDescription() == null) {
                    node.setDescription(token);
                }
            }
            else if (token.type != Token.NEXT_LINE) {
                parseConstant(node, sourceLine);
                return node;
            }
            root.addComment(token);
            node.addToken(sourceLine.getNextToken());
        }

        while ((token = sourceLine.getNextToken()) != null) {
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT || token.type == Token.NEXT_LINE) {
                root.addComment(token);
            }
        }

        return node;
    }

    void parseConstant(Node parent, SourceLine sourceLine) {
        int state = 1;
        ConstantNode node = null;

        Token token;
        while ((token = sourceLine.getNextToken()) != null) {
            if (token.type == Token.NL) {
                break;
            }
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT || token.type == Token.NEXT_LINE) {
                root.addComment(token);
                continue;
            }
            switch (state) {
                case 0:
                    if (",".equals(token.getText())) {
                        node = null;
                        state = 1;
                        break;
                    }
                    node.addToken(token);
                    break;
                case 1:
                    if ("#".equals(token.getText())) {
                        node = new ConstantNode(parent);
                        node.start = new ExpressionNode(node);
                        node.addToken(token);
                        state = 2;
                        break;
                    }
                    node = new ConstantNode(parent);
                    node.identifier = token;
                    node.addToken(token);
                    state = 4;
                    break;
                case 2:
                    if (",".equals(token.getText())) {
                        node = null;
                        state = 1;
                        break;
                    }
                    node.addToken(token);
                    if ("[".equals(token.getText())) {
                        node.step = new ExpressionNode(node);
                        state = 3;
                        break;
                    }
                    node.start.addToken(token);
                    break;
                case 3:
                    node.addToken(token);
                    if ("]".equals(token.getText())) {
                        state = 0;
                        break;
                    }
                    node.step.addToken(token);
                    break;
                case 4:
                    if (",".equals(token.getText())) {
                        node = null;
                        state = 1;
                        break;
                    }
                    node.addToken(token);
                    if ("=".equals(token.getText())) {
                        node.expression = new ExpressionNode(node);
                        state = 5;
                    }
                    if ("[".equals(token.getText())) {
                        node.multiplier = new ExpressionNode(node);
                        state = 6;
                        break;
                    }
                    break;
                case 5:
                    if (",".equals(token.getText())) {
                        node = null;
                        state = 1;
                        break;
                    }
                    node.addToken(token);
                    node.expression.addToken(token);
                    break;
                case 6:
                    node.addToken(token);
                    if ("]".equals(token.getText())) {
                        state = 0;
                        break;
                    }
                    node.multiplier.addToken(token);
                    break;
            }
        }

        while ((token = sourceLine.getNextToken()) != null) {
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT || token.type == Token.NEXT_LINE) {
                root.addComment(token);
            }
        }
    }

    VariablesNode parseVarBlock(VariablesNode node, SourceLine sourceLine) {
        Token token;

        while ((token = sourceLine.peekNextToken()) != null) {
            if (token.type == Token.NL) {
                break;
            }
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT) {
                if (node.getDescription() == null) {
                    node.setDescription(token);
                }
            }
            else if (token.type != Token.NEXT_LINE) {
                parseVariable(node, sourceLine);
                return node;
            }
            root.addComment(token);
            node.addToken(sourceLine.getNextToken());
        }

        while ((token = sourceLine.getNextToken()) != null) {
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT || token.type == Token.NEXT_LINE) {
                root.addComment(token);
            }
        }

        return node;
    }

    void parseVariable(VariablesNode parent, SourceLine sourceLine) {
        int state = 1;
        VariableNode node = null;

        Token token;
        while ((token = sourceLine.getNextToken()) != null) {
            if (token.type == Token.NL) {
                break;
            }
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT || token.type == Token.NEXT_LINE) {
                root.addComment(token);
                continue;
            }
            parent.addToken(token);
            switch (state) {
                case 1:
                    if (Spin1Model.isType(token.getText())) {
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

        while ((token = sourceLine.getNextToken()) != null) {
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT || token.type == Token.NEXT_LINE) {
                root.addComment(token);
            }
        }
    }

    ObjectsNode parseObjBlock(ObjectsNode node, SourceLine sourceLine) {
        Token token;

        while ((token = sourceLine.peekNextToken()) != null) {
            if (token.type == Token.NL) {
                break;
            }
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT) {
                if (node.getDescription() == null) {
                    node.setDescription(token);
                }
            }
            else if (token.type != Token.NEXT_LINE) {
                parseObjectLine(node, sourceLine);
                return node;
            }
            root.addComment(token);
            node.addToken(sourceLine.getNextToken());
        }

        while ((token = sourceLine.getNextToken()) != null) {
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT || token.type == Token.NEXT_LINE) {
                root.addComment(token);
            }
        }

        return node;
    }

    void parseObjectLine(ObjectsNode parent, SourceLine sourceLine) {
        int state = 1;
        ObjectNode object = null;

        Token token;
        while ((token = sourceLine.getNextToken()) != null) {
            if (token.type == Token.NL) {
                break;
            }
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT || token.type == Token.NEXT_LINE) {
                root.addComment(token);
                continue;
            }
            switch (state) {
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

        while ((token = sourceLine.getNextToken()) != null) {
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT || token.type == Token.NEXT_LINE) {
                root.addComment(token);
            }
        }
    }

    MethodNode parseMethodBlock(MethodNode node, SourceLine sourceLine) {
        int state = 1;
        MethodNode.ParameterNode param = null;
        MethodNode.ReturnNode ret = null;
        MethodNode.LocalVariableNode local = null;

        Token token;
        while ((token = sourceLine.getNextToken()) != null) {
            if (token.type == Token.NL) {
                break;
            }
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT || token.type == Token.NEXT_LINE) {
                root.addComment(token);
                continue;
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
                    if ("=".equals(token.getText())) {
                        state = 5;
                    }
                    break;
                case 5:
                    if (",".equals(token.getText())) {
                        param = null;
                        state = 4;
                        break;
                    }
                    if (")".equals(token.getText())) {
                        state = 6;
                        break;
                    }
                    if (param.defaultValue == null) {
                        param.defaultValue = new ExpressionNode(param);
                    }
                    param.defaultValue.addToken(token);
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
                    if (Spin1Model.isType(token.getText())) {
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

        while ((token = sourceLine.getNextToken()) != null) {
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT || token.type == Token.NEXT_LINE) {
                root.addComment(token);
            }
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT) {
                if (!token.getText().startsWith("''") && !token.getText().startsWith("{{")) {
                    break;
                }
                node.addDocument(token);
            }
        }

        while ((token = sourceLine.getNextToken()) != null) {
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT || token.type == Token.NEXT_LINE) {
                root.addComment(token);
            }
        }

        return node;
    }

    StatementNode parseStatement(Node parent, SourceLine sourceLine) {
        Token token;
        StatementNode node = new StatementNode(parent);

        while ((token = getNextStatementToken(sourceLine)) != null) {
            if (token.type == Token.NL) {
                break;
            }
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT || token.type == Token.NEXT_LINE) {
                root.addComment(token);
                continue;
            }
            node.addToken(token);
        }

        while ((token = sourceLine.getNextToken()) != null) {
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT || token.type == Token.NEXT_LINE) {
                root.addComment(token);
            }
        }

        return node;
    }

    StatementNode parseCaseStatement(Node parent, SourceLine sourceLine) {
        Token token;
        StatementNode node = new StatementNode(parent);

        while ((token = getNextStatementToken(sourceLine)) != null) {
            if (token.type == Token.NL) {
                break;
            }
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT || token.type == Token.NEXT_LINE) {
                root.addComment(token);
                continue;
            }
            node.addToken(token);
            if (":".equals(token.getText())) {
                break;
            }
        }

        while ((token = sourceLine.peekNextToken()) != null) {
            if (token.type == Token.NL) {
                break;
            }
            if (token.type != Token.COMMENT && token.type != Token.BLOCK_COMMENT && token.type != Token.NEXT_LINE) {
                return node;
            }
            root.addComment(sourceLine.getNextToken());
        }

        while ((token = sourceLine.getNextToken()) != null) {
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT || token.type == Token.NEXT_LINE) {
                root.addComment(token);
            }
        }

        return node;
    }

    Token getNextStatementToken(SourceLine sourceLine) {
        Token token = sourceLine.getNextToken();
        if (token != null) {
            if ("@".equals(token.getText()) || "@@".equals(token.getText()) || "@@@".equals(token.getText())) {
                Token nextToken = sourceLine.peekNextToken();
                if (token.isAdjacent(nextToken) && nextToken.type != Token.OPERATOR) {
                    token = token.merge(sourceLine.getNextToken());
                    if (nextToken.type == Token.STRING) {
                        token.type = Token.STRING;
                    }
                }
            }
            else if (".".equals(token.getText())) {
                Token nextToken = sourceLine.peekNextToken();
                if (token.isAdjacent(nextToken) && nextToken.type != Token.OPERATOR) {
                    token = token.merge(sourceLine.getNextToken());
                    token.type = Token.KEYWORD;
                }
            }
        }
        return token;
    }

    DataNode parseDatBlock(DataNode node, SourceLine sourceLine) {
        Token token;

        while ((token = sourceLine.peekNextToken()) != null) {
            if (token.type == Token.NL) {
                break;
            }
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT) {
                if (node.getDescription() == null) {
                    node.setDescription(token);
                }
            }
            else if (token.type != Token.NEXT_LINE) {
                parseDatLine(node, sourceLine);
                return node;
            }
            root.addComment(token);
            node.addToken(sourceLine.getNextToken());
        }

        while ((token = sourceLine.getNextToken()) != null) {
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT || token.type == Token.NEXT_LINE) {
                root.addComment(token);
            }
        }

        return node;
    }

    void parseDatLine(DataNode parent, SourceLine sourceLine) {
        int state = 1;
        DataLineNode node = new DataLineNode(parent);
        DataLineNode.ParameterNode parameter = null;

        Token token;
        while ((token = sourceLine.getNextToken()) != null) {
            if (token.type == Token.NL) {
                break;
            }
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT || token.type == Token.NEXT_LINE) {
                root.addComment(token);
                continue;
            }
            if (state == 4 || state == 5) {
                if ("@".equals(token.getText()) || "@@".equals(token.getText()) || "@@@".equals(token.getText())) {
                    Token nextToken = sourceLine.peekNextToken();
                    if (":".equals(nextToken.getText()) && token.isAdjacent(nextToken)) {
                        token = token.merge(sourceLine.getNextToken());
                        nextToken = sourceLine.peekNextToken();
                    }
                    if (token.isAdjacent(nextToken) && nextToken.type != Token.OPERATOR) {
                        token = token.merge(sourceLine.getNextToken());
                    }
                }
            }
            if (state == 1 || state == 4 || state == 5) {
                if (":".equals(token.getText())) {
                    Token nextToken = sourceLine.peekNextToken();
                    if (token.isAdjacent(nextToken) && nextToken.type != Token.OPERATOR) {
                        token = token.merge(sourceLine.getNextToken());
                    }
                }
            }
            node.addToken(token);
            switch (state) {
                case 1:
                    if (Spin1Model.isPAsmCondition(token.getText())) {
                        node.condition = token;
                        state = 3;
                        break;
                    }
                    if (Spin1Model.isPAsmInstruction(token.getText())) {
                        node.instruction = token;
                        state = 4;
                        break;
                    }
                    node.label = token;
                    state = 2;
                    break;
                case 2:
                    if (Spin1Model.isPAsmCondition(token.getText())) {
                        node.condition = token;
                        state = 3;
                        break;
                    }
                    // fall-through
                case 3:
                    node.instruction = token;
                    state = 4;
                    break;
                case 4:
                    if (Spin1Model.isPAsmModifier(token.getText())) {
                        node.modifier = new Node(node);
                        node.modifier.addToken(token);
                        state = 6;
                        break;
                    }
                    parameter = new DataLineNode.ParameterNode(node);
                    parameter.addToken(token);
                    node.parameters.add(parameter);
                    state = 5;
                    break;
                case 5:
                    if (",".equals(token.getText())) {
                        parameter = new DataLineNode.ParameterNode(node);
                        node.parameters.add(parameter);
                        break;
                    }
                    if (Spin1Model.isPAsmModifier(token.getText())) {
                        node.modifier = new Node(node);
                        node.modifier.addToken(token);
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
                    node.modifier.addToken(token);
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

        while ((token = sourceLine.getNextToken()) != null) {
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT || token.type == Token.NEXT_LINE) {
                root.addComment(token);
            }
        }
    }

}
