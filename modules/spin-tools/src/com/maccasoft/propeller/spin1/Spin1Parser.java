/*
 * Copyright (c) 2021-23 Marco Maccaferri and others.
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
import com.maccasoft.propeller.model.StatementNode;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.VariableNode;
import com.maccasoft.propeller.model.VariablesNode;

public class Spin1Parser extends Parser {

    private static final Set<String> sections = new HashSet<String>(Arrays.asList(new String[] {
        "CON", "VAR", "OBJ", "PUB", "PRI", "DAT"
    }));

    private static final Set<String> preprocessor = new HashSet<>(Arrays.asList(new String[] {
        "define", "ifdef", "elifdef", "elseifdef", "ifndef", "elifndef", "elseifndef", "else", "if", "elif", "elseif", "endif"
    }));

    final Spin1TokenStream stream;

    Node root;

    public Spin1Parser(Spin1TokenStream stream) {
        this.stream = stream;
    }

    @Override
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
            else if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT) {
                root.addComment(stream.nextToken());
            }
            else {
                if ("VAR".equalsIgnoreCase(token.getText())) {
                    parseVariables();
                }
                else if ("OBJ".equalsIgnoreCase(token.getText())) {
                    parseObjects();
                }
                else if ("PUB".equalsIgnoreCase(token.getText()) || "PRI".equalsIgnoreCase(token.getText())) {
                    parseMethod();
                }
                else if ("DAT".equalsIgnoreCase(token.getText())) {
                    parseDat();
                }
                else if ("CON".equalsIgnoreCase(token.getText())) {
                    parseConstants();
                }
                else {
                    if (defaultNode == null) {
                        defaultNode = new ConstantsNode(root);
                    }
                    parseConstant(defaultNode);
                }
            }
        }
        stream.nextToken();

        return root;
    }

    void parsePreprocessor(Node parent) {
        parsePreprocessor(parent, null);
    }

    void parsePreprocessor(Node parent, Token token) {
        if (token == null) {
            token = stream.nextToken();
        }

        if ("define".equals(stream.peekNext().getText())) {
            DirectiveNode.DefineNode node = new DirectiveNode.DefineNode(parent);
            node.addToken(token);
            node.addToken(stream.nextToken());
            if ((token = nextToken()).type != Token.EOF && token.type != Token.NL) {
                node.identifier = token;
                node.addToken(token);
            }
            if (token.type != Token.EOF && token.type != Token.NL) {
                while ((token = nextToken()).type != Token.EOF) {
                    if (token.type == Token.NL) {
                        break;
                    }
                    node.addToken(token);
                }
            }
        }
        else {
            DirectiveNode node = new DirectiveNode(parent);
            node.addToken(token);
            while ((token = nextToken()).type != Token.EOF) {
                if (token.type == Token.NL) {
                    break;
                }
                node.addToken(token);
            }
        }
    }

    void parseConstants() {
        Token token;

        Node node = new ConstantsNode(root, stream.nextToken());

        while ((token = stream.peekNext()).type != Token.EOF) {
            if (token.type != Token.COMMENT && token.type != Token.BLOCK_COMMENT) {
                break;
            }
            node.addToken(token);
            root.addComment(stream.nextToken());
        }

        if ((token = stream.peekNext()).type == Token.NL) {
            stream.nextToken();
        }
        else {
            parseConstant(node);
        }

        while ((token = stream.peekNext()).type != Token.EOF) {
            if (sections.contains(token.getText().toUpperCase())) {
                return;
            }
            parseConstant(node);
        }
    }

    void parseConstant(Node parent) {
        int state = 0;
        ConstantNode node = null;

        Token token;
        while ((token = nextToken()).type != Token.EOF) {
            if (token.type == Token.NL) {
                break;
            }
            switch (state) {
                case 0:
                    if ("#".equals(token.getText())) {
                        Token next = stream.peekNext();
                        if (preprocessor.contains(next.getText().toLowerCase())) {
                            parsePreprocessor(parent, token);
                            return;
                        }
                    }
                    state = 1;
                    // Fall-through
                case 1:
                    if (",".equals(token.getText())) {
                        node = null;
                        break;
                    }
                    if ("#".equals(token.getText())) {
                        node = new ConstantNode(parent);
                        node.start = new ExpressionNode(node);
                        state = 2;
                    }
                    if (node == null) {
                        node = new ConstantNode(parent, token);
                        state = 4;
                    }
                    node.addToken(token);
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
                    if (",".equals(token.getText())) {
                        node = null;
                        state = 1;
                        break;
                    }
                    node.addToken(token);
                    if ("]".equals(token.getText())) {
                        node = null;
                        state = 1;
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
                    if (",".equals(token.getText())) {
                        node = null;
                        state = 1;
                        break;
                    }
                    node.addToken(token);
                    if ("]".equals(token.getText())) {
                        node = null;
                        state = 1;
                        break;
                    }
                    node.multiplier.addToken(token);
                    break;
            }
        }
    }

    void parseVariables() {
        Token token;

        Node node = new VariablesNode(root);
        node.addToken(stream.nextToken());

        while ((token = stream.peekNext()).type != Token.EOF) {
            if (token.type != Token.COMMENT && token.type != Token.BLOCK_COMMENT) {
                break;
            }
            node.addToken(token);
            root.addComment(stream.nextToken());
        }

        if ((token = stream.peekNext()).type == Token.NL) {
            stream.nextToken();
        }
        else {
            parseVariable(node);
        }

        while ((token = stream.peekNext()).type != Token.EOF) {
            if (sections.contains(token.getText().toUpperCase())) {
                break;
            }
            if ("#".equalsIgnoreCase(token.getText())) {
                parsePreprocessor(node);
            }
            else {
                parseVariable(node);
            }
        }
    }

    void parseVariable(Node parent) {
        int state = 1;
        VariableNode node = null;

        Token token;
        while ((token = nextToken()).type != Token.EOF) {
            if (token.type == Token.NL) {
                break;
            }
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
    }

    void parseObjects() {
        Token token;

        Node node = new ObjectsNode(root);
        node.addToken(stream.nextToken());

        while ((token = stream.peekNext()).type != Token.EOF) {
            if (token.type != Token.COMMENT && token.type != Token.BLOCK_COMMENT) {
                break;
            }
            node.addToken(token);
            root.addComment(stream.nextToken());
        }

        if ((token = stream.peekNext()).type == Token.NL) {
            stream.nextToken();
        }
        else {
            parseObject(node);
        }

        while ((token = stream.peekNext()).type != Token.EOF) {
            if (sections.contains(token.getText().toUpperCase())) {
                break;
            }
            if ("#".equalsIgnoreCase(token.getText())) {
                parsePreprocessor(node);
            }
            else {
                parseObject(node);
            }
        }
    }

    void parseObject(Node parent) {
        int state = 1;
        ObjectNode object = null;

        Token token;
        while ((token = nextToken()).type != Token.EOF) {
            if (token.type == Token.NL) {
                break;
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
    }

    void parseMethod() {
        int state = 1;
        MethodNode.ParameterNode param = null;
        MethodNode.ReturnNode ret = null;
        MethodNode.LocalVariableNode local = null;
        MethodNode node = new MethodNode(root, stream.nextToken());

        Token token;
        while ((token = nextToken()).type != Token.EOF) {
            if (token.type == Token.NL) {
                token = stream.peekNext();
                if (sections.contains(token.getText().toUpperCase())) {
                    return;
                }
                if ("#".equalsIgnoreCase(token.getText())) {
                    parsePreprocessor(node);
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

        while ((token = stream.peekNext()).type != Token.EOF) {
            if (token.type == Token.NL) {
                stream.nextToken();

                token = stream.peekNext();
                if (sections.contains(token.getText().toUpperCase())) {
                    return;
                }
            }
            else {
                if (token.type != Token.COMMENT && token.type != Token.BLOCK_COMMENT) {
                    break;
                }
                token = stream.nextToken();
                if (token.getText().startsWith("''") || token.getText().startsWith("{{")) {
                    node.document.add(token);
                }
                root.addComment(token);
            }
        }

        while ((token = stream.peekNext()).type != Token.EOF) {
            if (token.type == Token.NL) {
                stream.nextToken();
                if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                    break;
                }
            }
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT) {
                root.addComment(stream.nextToken());
            }
            else {
                parseStatement(node, 0);
                if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                    break;
                }
            }
        }
    }

    void parseStatement(Node parent, int column) {
        Token token;

        while ((token = stream.peekNext()).type != Token.EOF) {
            if (token.type == Token.NL) {
                stream.nextToken();
                token = stream.peekNext();
                if (sections.contains(token.getText().toUpperCase())) {
                    return;
                }
                if ("#".equalsIgnoreCase(token.getText())) {
                    parsePreprocessor(parent);
                }
            }
            else if (sections.contains(token.getText().toUpperCase())) {
                return;
            }
            else if ("#".equalsIgnoreCase(token.getText())) {
                parsePreprocessor(parent);
            }
            else if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT) {
                root.addComment(stream.nextToken());
            }
            else if (token.column < column) {
                break;
            }
            else {
                Token startToken = nextToken();

                Node statement = new StatementNode(parent);
                statement.addToken(startToken);

                while ((token = nextToken()).type != Token.EOF) {
                    if (token.type == Token.NL) {
                        break;
                    }
                    statement.addToken(token);
                }

                if ("CASE".equalsIgnoreCase(startToken.getText())) {
                    parseCaseStatement(statement, startToken.column + 1);
                }
                else if (Spin1Model.isBlockStart(startToken.getText())) {
                    parseStatement(statement, startToken.column + 1);
                }
            }
        }
    }

    void parseCaseStatement(Node parent, int column) {
        Token token;

        while ((token = stream.peekNext()).type != Token.EOF) {
            if (token.type == Token.NL) {
                stream.nextToken();
                token = stream.peekNext();
                if (sections.contains(token.getText().toUpperCase())) {
                    return;
                }
                if ("#".equalsIgnoreCase(token.getText())) {
                    parsePreprocessor(parent);
                }
            }
            else if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT) {
                root.addComment(stream.nextToken());
            }
            else if (token.column < column) {
                break;
            }
            else {
                Token startToken = nextToken();

                Node statement = new StatementNode(parent);
                statement.addToken(startToken);

                while ((token = nextToken()).type != Token.EOF) {
                    if (token.type == Token.NL) {
                        token = stream.peekNext();
                        if (sections.contains(token.getText().toUpperCase())) {
                            return;
                        }
                        if ("#".equalsIgnoreCase(token.getText())) {
                            parsePreprocessor(parent);
                        }
                        break;
                    }
                    statement.addToken(token);
                    if (":".equals(token.getText())) {
                        while ((token = stream.peekNext()).type != Token.EOF) {
                            if (token.type != Token.COMMENT && token.type != Token.BLOCK_COMMENT) {
                                break;
                            }
                            root.addComment(stream.nextToken());
                        }
                        break;
                    }
                }

                parseStatement(statement, startToken.column + 1);
                token = stream.peekNext();
                if (sections.contains(token.getText().toUpperCase())) {
                    return;
                }
                if ("#".equalsIgnoreCase(token.getText())) {
                    parsePreprocessor(parent);
                }
            }
        }
    }

    void parseDat() {
        Token token;

        Node node = new DataNode(root);
        node.addToken(stream.nextToken());

        while ((token = stream.peekNext()).type != Token.EOF) {
            if (token.type != Token.COMMENT && token.type != Token.BLOCK_COMMENT) {
                break;
            }
            node.addToken(token);
            root.addComment(stream.nextToken());
        }

        if ((token = stream.peekNext()).type == Token.NL) {
            stream.nextToken();
        }
        else {
            parseDatLine(node);
        }

        while ((token = stream.peekNext()).type != Token.EOF) {
            if (sections.contains(token.getText().toUpperCase())) {
                return;
            }
            if ("#".equalsIgnoreCase(token.getText())) {
                parsePreprocessor(node);
            }
            else if (token.type == Token.NL) {
                stream.nextToken();
            }
            else if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT) {
                root.addComment(stream.nextToken());
            }
            else {
                parseDatLine(node);
            }
        }
    }

    void parseDatLine(Node parent) {
        int state = 1;
        DataLineNode node = null;
        DataLineNode.ParameterNode parameter = null;

        Token token;
        while ((token = nextPAsmToken()).type != Token.EOF) {
            if (token.type == Token.NL) {
                break;
            }
            if (node != null) {
                node.addToken(token);
            }
            switch (state) {
                case 1:
                    node = new DataLineNode(parent);
                    node.addToken(token);
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
                    if (Spin1Model.isPAsmModifier(token.getText())) {
                        node.modifier = new Node(node);
                        node.modifier.addToken(token);
                        state = 6;
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
                    if (Spin1Model.isPAsmModifier(token.getText())) {
                        node.modifier = new Node(node);
                        node.modifier.addToken(token);
                        state = 6;
                        break;
                    }
                    // fall-through
                case 3:
                    if (Spin1Model.isPAsmModifier(token.getText())) {
                        node.modifier = new Node(node);
                        node.modifier.addToken(token);
                        state = 6;
                        break;
                    }
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
    }

    Token nextToken() {
        Token token = stream.nextToken();
        while (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT) {
            root.addComment(token);
            token = stream.nextToken();
        }
        if (token.type == Token.NL) {
            while (stream.peekNext().type == Token.NL) {
                stream.nextToken();
            }
        }
        else if ("@".equals(token.getText()) || "@@".equals(token.getText())) {
            Token nextToken = stream.peekNext();
            if (token.isAdjacent(nextToken) && nextToken.type != Token.OPERATOR) {
                token = token.merge(stream.nextToken());
                if (nextToken.type == Token.STRING) {
                    token.type = Token.STRING;
                }
            }
        }
        else if (".".equals(token.getText())) {
            Token nextToken = stream.peekNext();
            if (token.isAdjacent(nextToken) && nextToken.type != Token.OPERATOR) {
                token = token.merge(stream.nextToken());
                token.type = Token.FUNCTION;
            }
        }
        return token;
    }

    Token nextPAsmToken() {
        Token token = stream.nextToken();
        while (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT) {
            root.addComment(token);
            token = stream.nextToken();
        }
        if (token.type == Token.NL) {
            while (stream.peekNext().type == Token.NL) {
                stream.nextToken();
            }
        }
        else if ("@".equals(token.getText()) || "@@".equals(token.getText())) {
            Token nextToken = stream.peekNext();
            if (":".equals(nextToken.getText()) && token.isAdjacent(nextToken)) {
                token = token.merge(stream.nextToken());
                nextToken = stream.peekNext();
            }
            if (token.isAdjacent(nextToken) && nextToken.type != Token.OPERATOR) {
                token = token.merge(stream.nextToken());
            }
        }
        else if (":".equals(token.getText())) {
            Token nextToken = stream.peekNext();
            if (token.isAdjacent(nextToken) && nextToken.type != Token.OPERATOR) {
                token = token.merge(stream.nextToken());
            }
        }
        return token;
    }

}
