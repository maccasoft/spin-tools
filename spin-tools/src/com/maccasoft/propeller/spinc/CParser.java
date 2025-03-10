/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spinc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.DirectiveNode;
import com.maccasoft.propeller.model.ExpressionNode;
import com.maccasoft.propeller.model.FunctionNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.Parser;
import com.maccasoft.propeller.model.StatementNode;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.TokenStream.Position;
import com.maccasoft.propeller.model.TypeDefinitionNode;
import com.maccasoft.propeller.model.VariableNode;
import com.maccasoft.propeller.spin2.Spin2Model;

public class CParser extends Parser {

    CTokenStream stream;

    Node root;

    public CParser(String text) {
        this.stream = new CTokenStream(text);
    }

    @Override
    public Node parse() {
        root = new Node();

        Token token, commentToken = null;
        while ((token = stream.nextToken()).type != Token.EOF) {
            if (token.type == Token.NL) {
                continue;
            }
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT) {
                root.addComment(token);
                if (token.type == Token.BLOCK_COMMENT) {
                    commentToken = token;
                }
            }
            else if ("#".equals(token.getText())) {
                parsePreprocessor(root, token);
                commentToken = null;
            }
            else if ("struct".equals(token.getText())) {
                Token type = token;

                if ((token = nextTokenSkipNL()).type == Token.EOF) {
                    break;
                }
                Token identifier = token;

                if ((token = nextTokenSkipNL()).type != Token.EOF) {
                    if ("{".equals(token.getText())) {
                        TypeDefinitionNode node = new TypeDefinitionNode(root, type, identifier);
                        node.addToken(token);
                        parseStructure(node);
                    }
                    else {
                        VariableNode node = new VariableNode(root, type, identifier, token);
                        VariableNode child = null;

                        token = nextTokenSkipNL();
                        while (token.type != Token.EOF) {
                            if (";".equals(token.getText())) {
                                break;
                            }
                            node.addToken(token);
                            if (",".equals(token.getText())) {
                                if ((token = nextTokenSkipNL()).type == Token.EOF) {
                                    break;
                                }
                                node.addToken(token);
                                if ("*".equals(token.getText())) {
                                    type = token;
                                    if ((token = nextTokenSkipNL()).type == Token.EOF) {
                                        break;
                                    }
                                    node.addToken(token);
                                    child = new VariableNode(node, null, type, token);
                                }
                                else {
                                    child = new VariableNode(node, null, null, token);
                                }
                            }
                            else if (child != null) {
                                child.addToken(token);
                            }
                            token = nextTokenSkipNL();
                        }
                    }
                }

            }
            else {
                Token modifier = null;

                if ("static".equals(token.getText())) {
                    modifier = token;
                    if ((token = nextTokenSkipNL()).type != Token.EOF) {
                        break;
                    }
                }

                Token type = token;

                token = nextTokenSkipNL();
                if ("*".equals(token.getText())) {
                    type = type.merge(token);
                    type.type = Token.KEYWORD;
                    if ((token = nextTokenSkipNL()).type == Token.EOF) {
                        break;
                    }
                }

                Token identifier = token;

                if ((token = nextTokenSkipNL()).type == Token.EOF) {
                    break;
                }

                Node node;
                if ("(".equals(token.getText())) {
                    node = new FunctionNode(root, modifier, type, identifier);
                    node.addToken(token);

                    if (commentToken != null) {
                        node.addDocument(commentToken);
                    }

                    while ((token = nextTokenSkipNL()).type != Token.EOF) {
                        if (")".equals(token.getText())) {
                            node.addToken(token);
                            break;
                        }

                        FunctionNode.ParameterNode param = new FunctionNode.ParameterNode((FunctionNode) node);
                        param.type = token;

                        token = nextTokenSkipNL();
                        if ("*".equals(token.getText())) {
                            param.type = param.type.merge(token);
                            param.type.type = Token.KEYWORD;
                            if ((token = nextTokenSkipNL()).type == Token.EOF) {
                                break;
                            }
                        }
                        param.addToken(param.type);

                        if (")".equals(token.getText())) {
                            node.addToken(token);
                            break;
                        }
                        if (!",".equals(token.getText())) {
                            param.identifier = token;
                            param.addToken(token);
                            while ((token = nextTokenSkipNL()).type != Token.EOF) {
                                if (")".equals(token.getText()) || ",".equals(token.getText())) {
                                    node.addToken(token);
                                    break;
                                }
                                param.addToken(token);
                            }
                        }
                        if (")".equals(token.getText())) {
                            break;
                        }
                    }

                    while ((token = nextTokenSkipNL()).type != Token.EOF) {
                        if ("#".equals(token.getText())) {
                            parsePreprocessor(node, token);
                        }
                        else {
                            node.addToken(token);
                            if ("{".equals(token.getText())) {
                                while ((token = peekNextTokenSkipNL()).type != Token.EOF) {
                                    if ("}".equals(token.getText())) {
                                        node = new StatementNode(node);
                                        node.addToken(nextTokenSkipNL());
                                        break;
                                    }
                                    parseStatement(node);
                                }
                                break;
                            }
                            else if (!";".equals(token.getText())) {
                                while ((token = nextTokenSkipNL()).type != Token.EOF) {
                                    if (";".equals(token.getText())) {
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
                else {
                    node = new VariableNode(root, modifier, type, identifier);
                    VariableNode child = null;
                    while (token.type != Token.EOF) {
                        if (";".equals(token.getText())) {
                            break;
                        }
                        node.addToken(token);
                        if (",".equals(token.getText())) {
                            if ((token = nextTokenSkipNL()).type == Token.EOF) {
                                break;
                            }
                            node.addToken(token);
                            if ("*".equals(token.getText())) {
                                type = token;
                                if ((token = nextTokenSkipNL()).type == Token.EOF) {
                                    break;
                                }
                                node.addToken(token);
                                child = new VariableNode(node, null, type, token);
                            }
                            else {
                                child = new VariableNode(node, null, null, token);
                            }
                        }
                        else if (child != null) {
                            child.addToken(token);
                        }
                        token = nextTokenSkipNL();
                    }
                }
                commentToken = null;
            }
        }

        return root;
    }

    void parseStructure(TypeDefinitionNode parent) {
        Token token;

        while ((token = nextTokenSkipNL()).type != Token.EOF) {
            parent.addToken(token);

            if ("}".equals(token.getText())) {
                break;
            }

            Token modifier = null;

            if ("struct".equals(token.getText())) {
                modifier = token;
                if ((token = nextTokenSkipNL()).type != Token.EOF) {
                    break;
                }
                parent.addToken(token);
            }

            Token type = token;

            token = nextTokenSkipNL();
            if ("*".equals(token.getText())) {
                parent.addToken(token);
                type = type.merge(token);
                type.type = Token.KEYWORD;
                if ((token = nextTokenSkipNL()).type == Token.EOF) {
                    break;
                }
            }

            Token identifier = token;
            parent.addToken(token);

            if ((token = nextTokenSkipNL()).type == Token.EOF) {
                break;
            }

            TypeDefinitionNode.Definition node = new TypeDefinitionNode.Definition(parent, modifier, type, identifier);
            TypeDefinitionNode.Definition child = node;

            while (token.type != Token.EOF) {
                parent.addToken(token);
                if (";".equals(token.getText())) {
                    break;
                }
                if (",".equals(token.getText())) {
                    if ((token = nextTokenSkipNL()).type == Token.EOF) {
                        break;
                    }
                    parent.addToken(token);
                    if ("*".equals(token.getText())) {
                        type = token;
                        if ((token = nextTokenSkipNL()).type == Token.EOF) {
                            break;
                        }
                        parent.addToken(token);
                        child = new TypeDefinitionNode.Definition(node, null, type, token);
                    }
                    else {
                        child = new TypeDefinitionNode.Definition(node, null, null, token);
                    }
                }
                else if (child != null) {
                    child.addToken(token);
                }
                token = nextTokenSkipNL();
            }
        }

        if ((token = nextTokenSkipNL()).type != Token.EOF) {
            parent.addToken(token);
            if (!";".equals(token.getText())) {
                new VariableNode(parent, null, null, token);

                token = nextTokenSkipNL();
                while (token.type != Token.EOF) {
                    parent.addToken(token);
                    if (";".equals(token.getText())) {
                        break;
                    }
                    if (",".equals(token.getText())) {
                        if ((token = nextTokenSkipNL()).type == Token.EOF) {
                            break;
                        }
                        parent.addToken(token);
                        if ("*".equals(token.getText())) {
                            Token type = token;
                            if ((token = nextTokenSkipNL()).type == Token.EOF) {
                                break;
                            }
                            parent.addToken(token);
                            new VariableNode(parent, null, type, token);
                        }
                        else {
                            new VariableNode(parent, null, null, token);
                        }
                    }
                    token = nextTokenSkipNL();
                }
            }
        }
    }

    private static Set<String> argBlocks = new HashSet<>(Arrays.asList(new String[] {
        "for", "if", "while", "until", "switch"
    }));

    void parseStatement(Node parent) {
        Token token;
        Node node;

        if ((token = nextTokenSkipNL()).type != Token.EOF) {
            if ("#".equals(token.getText())) {
                node = parsePreprocessor(parent, token);
            }
            else {
                node = new StatementNode(parent);
                node.addToken(token);

                if ("else".equals(token.getText())) {
                    if ("if".equals(peekNextToken().getText())) {
                        token = nextToken();
                        node.addToken(token);
                    }
                }

                if ("asm".equals(token.getText())) {
                    if ((token = peekNextTokenSkipNL()).type != Token.EOF) {
                        if ("{".equals(token.getText())) {
                            node.addToken(nextTokenSkipNL());
                            while ((token = peekNextTokenSkipNL()).type != Token.EOF) {
                                if ("}".equals(token.getText())) {
                                    node = new StatementNode(parent);
                                    node.addToken(nextTokenSkipNL());
                                    break;
                                }
                                parseDatLine(node);
                            }
                        }
                    }
                }
                else if ("case".equals(token.getText()) || "default".equals(token.getText())) {
                    while ((token = nextTokenSkipNL()).type != Token.EOF) {
                        node.addToken(token);
                        if (":".equals(token.getText())) {
                            break;
                        }
                    }
                    if ((token = peekNextTokenSkipNL()).type != Token.EOF) {
                        if ("{".equals(token.getText())) {
                            node.addToken(token);
                            while ((token = peekNextTokenSkipNL()).type != Token.EOF) {
                                if ("}".equals(token.getText())) {
                                    node = new StatementNode(parent);
                                    node.addToken(nextTokenSkipNL());
                                    break;
                                }
                                parseStatement(node);
                            }
                        }
                        else {
                            StatementNode child;
                            do {
                                if ("case".equals(peekNextTokenSkipNL().getText()) || "default".equals(peekNextTokenSkipNL().getText())) {
                                    break;
                                }
                                child = new StatementNode(node);
                                while ((token = nextTokenSkipNL()).type != Token.EOF) {
                                    child.addToken(token);
                                    if (";".equals(token.getText())) {
                                        break;
                                    }
                                }
                            } while (token.type != Token.EOF && !"break".equals(child.getStartToken().getText()));
                        }
                    }
                }
                else if (argBlocks.contains(token.getText())) {
                    int nested = 0;

                    while ((token = nextTokenSkipNL()).type != Token.EOF) {
                        node.addToken(token);
                        if ("(".equals(token.getText())) {
                            nested++;
                        }
                        if (")".equals(token.getText())) {
                            nested--;
                            if (nested <= 0) {
                                break;
                            }
                        }
                    }

                    if ((token = peekNextTokenSkipNL()).type != Token.EOF) {
                        if (";".equals(token.getText())) {
                            node.addToken(nextTokenSkipNL());
                        }
                        else if ("{".equals(token.getText())) {
                            node.addToken(nextTokenSkipNL());
                            while ((token = peekNextTokenSkipNL()).type != Token.EOF) {
                                if ("}".equals(token.getText())) {
                                    node = new StatementNode(parent);
                                    node.addToken(nextTokenSkipNL());
                                    break;
                                }
                                parseStatement(node);
                            }
                        }
                        else {
                            parseStatement(node);
                        }
                    }
                }
                else if ("{".equals(token.getText())) {
                    node.addToken(token);
                    while ((token = peekNextTokenSkipNL()).type != Token.EOF) {
                        if ("}".equals(token.getText())) {
                            node = new StatementNode(parent);
                            node.addToken(nextTokenSkipNL());

                            token = peekNextTokenSkipNL();
                            if ("while".equals(token.getText()) || "until".equals(token.getText())) {
                                while ((token = nextTokenSkipNL()).type != Token.EOF) {
                                    node.addToken(token);
                                    if (";".equals(token.getText())) {
                                        break;
                                    }
                                }
                            }

                            break;
                        }
                        parseStatement(node);
                    }
                }
                else if ("else".equals(token.getText())) {
                    if ((token = peekNextTokenSkipNL()).type != Token.EOF) {
                        if ("{".equals(token.getText())) {
                            node.addToken(nextTokenSkipNL());
                            while ((token = peekNextTokenSkipNL()).type != Token.EOF) {
                                if ("}".equals(token.getText())) {
                                    boolean isDo = "do".equals(node.getStartToken().getText());

                                    node = new StatementNode(parent);
                                    node.addToken(nextTokenSkipNL());

                                    if (isDo) {
                                        token = peekNextTokenSkipNL();
                                        if ("while".equals(token.getText()) || "until".equals(token.getText())) {
                                            while ((token = nextTokenSkipNL()).type != Token.EOF) {
                                                node.addToken(token);
                                                if (";".equals(token.getText())) {
                                                    break;
                                                }
                                            }
                                        }
                                    }

                                    break;
                                }
                                parseStatement(node);
                            }
                        }
                        else {
                            parseStatement(node);
                        }
                    }
                }
                else if (!";".equals(token.getText())) {
                    while ((token = nextToken()).type != Token.EOF && token.type != Token.NL) {
                        node.addToken(token);
                        if (";".equals(token.getText())) {
                            break;
                        }
                        if (",".equals(token.getText())) {
                            token = nextTokenSkipNL();
                            if (token.type == Token.EOF) {
                                break;
                            }
                            node.addToken(token);
                        }
                        if ("{".equals(token.getText())) {
                            while ((token = peekNextTokenSkipNL()).type != Token.EOF) {
                                if ("}".equals(token.getText())) {
                                    boolean isDo = "do".equals(node.getStartToken().getText());

                                    node = new StatementNode(parent);
                                    node.addToken(nextTokenSkipNL());

                                    if (isDo) {
                                        token = peekNextTokenSkipNL();
                                        if ("while".equals(token.getText()) || "until".equals(token.getText())) {
                                            while ((token = nextTokenSkipNL()).type != Token.EOF) {
                                                node.addToken(token);
                                                if (";".equals(token.getText())) {
                                                    break;
                                                }
                                            }
                                        }
                                    }

                                    break;
                                }
                                parseStatement(node);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    DirectiveNode parsePreprocessor(Node parent, Token token) {
        Token directive = nextToken();

        if ("include".equals(directive.getText())) {
            Token file = nextToken();
            if ("<".equals(file.getText())) {
                Token t;
                while ((t = nextToken()).type != Token.EOF) {
                    if (t.type == Token.NL) {
                        break;
                    }
                    file = file.merge(t);
                    if (">".equals(t.getText())) {
                        break;
                    }
                }
                file.type = Token.STRING;
            }
            DirectiveNode node = new DirectiveNode.IncludeNode(parent, file.type == Token.STRING && file.getText().length() > 2 ? file : null);
            node.addToken(token);
            node.addToken(directive);
            if (file.type != Token.EOF && file.type != Token.NL) {
                node.addToken(file);
            }
            while ((token = nextToken()).type != Token.EOF) {
                if (token.type == Token.NL) {
                    break;
                }
                node.addToken(token);
            }
            return node;
        }
        else if ("define".equals(directive.getText())) {
            Token identifier = nextToken();
            DirectiveNode.DefineNode node = new DirectiveNode.DefineNode(parent, identifier.type == Token.KEYWORD ? identifier : null);
            node.addToken(token);
            node.addToken(directive);
            if (identifier.type == Token.KEYWORD) {
                node.addToken(identifier);
                while ((token = nextToken()).type != Token.EOF) {
                    if (token.type == Token.NL) {
                        break;
                    }
                    node.addDefinition(token);
                }
            }
            else if (identifier.type != Token.EOF && identifier.type != Token.NL) {
                node.addToken(identifier);
                while ((token = nextToken()).type != Token.EOF) {
                    if (token.type == Token.NL) {
                        break;
                    }
                    node.addToken(token);
                }
            }
            return node;
        }
        else if ("error".equals(directive.getText()) || "warning".equals(directive.getText())) {
            DirectiveNode node = new DirectiveNode(parent);
            node.addToken(token);
            node.addToken(directive);

            Token message = stream.nextToken();
            if (message.type != Token.EOF && message.type != Token.NL) {
                while ((token = nextToken()).type != Token.EOF) {
                    if (token.type == Token.NL) {
                        break;
                    }
                    message = message.merge(token);
                }
                message.type = Token.STRING;
                node.addToken(message);
            }

            return node;
        }

        DirectiveNode node = new DirectiveNode(parent);
        node.addToken(token);
        if (directive.type != Token.EOF && directive.type != Token.NL) {
            node.addToken(directive);
            while ((token = nextToken()).type != Token.EOF) {
                if (token.type == Token.NL) {
                    break;
                }
                node.addToken(token);
            }
        }
        return node;
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
                    if ("debug".equalsIgnoreCase(token.getText())) {
                        node.instruction = token;
                        state = 9;
                        break;
                    }
                    if (Spin2Model.isPAsmCondition(token.getText())) {
                        node.condition = token;
                        state = 3;
                        break;
                    }
                    if (Spin2Model.isPAsmInstruction(token.getText())) {
                        node.instruction = token;
                        state = 4;
                        break;
                    }
                    if (Spin2Model.isPAsmModifier(token.getText())) {
                        node.modifier = new Node(node);
                        node.modifier.addToken(token);
                        state = 6;
                        break;
                    }
                    node.label = token;
                    state = 2;
                    break;
                case 2:
                    if (Spin2Model.isPAsmCondition(token.getText())) {
                        node.condition = token;
                        state = 3;
                        break;
                    }
                    if (Spin2Model.isPAsmModifier(token.getText())) {
                        node.modifier = new Node(node);
                        node.modifier.addToken(token);
                        state = 6;
                        break;
                    }
                    // fall-through
                case 3:
                    if (Spin2Model.isPAsmModifier(token.getText())) {
                        node.modifier = new Node(node);
                        node.modifier.addToken(token);
                        state = 6;
                        break;
                    }
                    node.instruction = token;
                    state = "debug".equalsIgnoreCase(token.getText()) ? 9 : 4;
                    break;
                case 4:
                    if (Spin2Model.isPAsmModifier(token.getText())) {
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
                    if (Spin2Model.isPAsmModifier(token.getText())) {
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
            if (".".equals(nextToken.getText()) && token.isAdjacent(nextToken)) {
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
        else if ("#".equals(token.getText())) {
            Token nextToken = stream.peekNext();
            if (token.isAdjacent(nextToken) && "#".equals(nextToken.getText())) {
                token = token.merge(stream.nextToken());
            }
        }
        return token;
    }

    Token nextToken() {
        Token token = stream.nextToken();
        while (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT) {
            root.addComment(token);
            token = stream.nextToken();
        }
        if ("&".equals(token.getText())) {
            Token nextToken = stream.peekNext();
            if (token.isAdjacent(nextToken) && nextToken.type != Token.OPERATOR) {
                token = token.merge(stream.nextToken());
            }
        }
        else if (".".equals(token.getText())) {
            Token nextToken = stream.peekNext();
            if (token.isAdjacent(nextToken) && nextToken.type != Token.OPERATOR) {
                token = token.merge(stream.nextToken());
                token.type = 0;
            }
        }
        return token;
    }

    Token peekNextToken() {
        Token token = stream.peekNext();
        while (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT) {
            root.addComment(stream.nextToken());
            token = stream.peekNext();
        }
        return token;
    }

    Token nextTokenSkipNL() {
        Token token = stream.nextToken();
        while (true) {
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT) {
                root.addComment(token);
            }
            else if (token.type != Token.NL) {
                break;
            }
            token = stream.nextToken();
        }
        if ("&".equals(token.getText())) {
            Token nextToken = stream.peekNext();
            if (token.isAdjacent(nextToken) && nextToken.type != Token.OPERATOR) {
                token = token.merge(stream.nextToken());
            }
        }
        else if (".".equals(token.getText())) {
            Token nextToken = stream.peekNext();
            if (token.isAdjacent(nextToken) && nextToken.type != Token.OPERATOR) {
                token = token.merge(stream.nextToken());
                token.type = 0;
            }
        }
        return token;
    }

    Token peekNextTokenSkipNL() {
        Position pos = new Position(stream);
        try {
            Token token = stream.nextToken();
            while (true) {
                if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT) {
                    ;
                }
                else if (token.type != Token.NL) {
                    break;
                }
                token = stream.nextToken();
            }
            if ("&".equals(token.getText())) {
                Token nextToken = stream.peekNext();
                if (token.isAdjacent(nextToken) && nextToken.type != Token.OPERATOR) {
                    token = token.merge(stream.nextToken());
                }
            }
            else if (".".equals(token.getText())) {
                Token nextToken = stream.peekNext();
                if (token.isAdjacent(nextToken) && nextToken.type != Token.OPERATOR) {
                    token = token.merge(stream.nextToken());
                    token.type = 0;
                }
            }
            return token;
        } finally {
            pos.restore();
        }
    }

}
