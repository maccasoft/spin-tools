/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spinc;

import java.util.ArrayList;
import java.util.List;

import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.DataLineNode.ModifierNode;
import com.maccasoft.propeller.model.DirectiveNode;
import com.maccasoft.propeller.model.ExpressionNode;
import com.maccasoft.propeller.model.FunctionNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.Parser;
import com.maccasoft.propeller.model.RootNode;
import com.maccasoft.propeller.model.SourceLine;
import com.maccasoft.propeller.model.StatementNode;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.TypeDefinitionNode;
import com.maccasoft.propeller.model.VariableNode;
import com.maccasoft.propeller.spin2.Spin2Model;

public class CParser extends Parser {

    enum State {
        S1, S2, S3, S4, S5, S6, Struct1, Struct2
    }

    String text;

    RootNode root;
    Node parentNode;

    State state = State.S1;
    List<Token> tokens = new ArrayList<>();
    List<String> nestingBraces = new ArrayList<>();

    public CParser(String text) {
        this.text = text;
    }

    @Override
    public RootNode parse() {
        Token token;
        List<Token> lineTokens = new ArrayList<>();

        root = new RootNode();
        parentNode = root;

        state = State.S1;
        CTokenStream stream = new CTokenStream(text);

        while ((token = stream.nextToken()).type != Token.EOF) {
            if (token.type == Token.NL) {
                if (!lineTokens.isEmpty()) {
                    if ("#".equals(lineTokens.getFirst().getText())) {
                        parsePreprocessor(parentNode, new SourceLine(lineTokens));
                        lineTokens.clear();
                    }
                    else if (parentNode instanceof StatementNode) {
                        Token firstToken = parentNode.getStartToken();
                        if (firstToken != null && "asm".equals(firstToken.getText())) {
                            parseDatLine(parentNode, new SourceLine(lineTokens));
                            lineTokens.clear();
                        }
                    }
                }
            }
            else if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT || token.type == Token.NEXT_LINE) {
                root.addComment(token);
            }
            else {
                lineTokens.add(token);

                if (processParenthesis(token)) {
                    continue;
                }
                if (!nestingBraces.isEmpty()) {
                    continue;
                }

                if ("}".equals(token.getText())) {
                    Token nextToken = stream.peekNext();
                    if (";".equals(nextToken.getText())) {
                        if (parentNode instanceof TypeDefinitionNode) {
                            lineTokens.add(stream.nextToken());
                        }
                        else if ("struct".equals(lineTokens.getFirst().getText())) {
                            lineTokens.add(stream.nextToken());
                        }
                    }
                    else if ("while".equals(nextToken.getText())) {
                        Token parentFirstToken = parentNode.getStartToken();
                        if (parentFirstToken != null && "do".equals(parentFirstToken.getText())) {
                            continue;
                        }
                    }
                }
                if (";".equals(token.getText()) || "{".equals(token.getText()) || "}".equals(token.getText())) {
                    SourceLine sourceLine = new SourceLine(lineTokens);
                    if (isExcluded()) {
                        root.addComment(sourceLine.getAsToken(Token.COMMENT));
                    }
                    else if (parentNode instanceof RootNode) {
                        if ("struct".equals(sourceLine.peekNextToken().getText())) {
                            parseStructure(sourceLine);
                        }
                        else {
                            Node node = parseDeclaration(sourceLine);
                            if (node instanceof FunctionNode) {
                                parentNode = node;
                                state = State.S1;
                            }
                        }
                    }
                    else if (parentNode instanceof TypeDefinitionNode) {
                        parseStructure(sourceLine);
                    }
                    else {
                        parseStatement(sourceLine);
                    }
                    lineTokens.clear();
                }
            }
        }

        if (!lineTokens.isEmpty()) {
            StatementNode node = new StatementNode((parentNode));
            node.addAllTokens(lineTokens);
        }

        return root;
    }

    void parseStatement(SourceLine sourceLine) {
        Token token;

        while ((token = sourceLine.getNextToken()) != null) {
            if ("&".equals(token.getText())) {
                Token nextToken = sourceLine.peekNextToken();
                if (nextToken != null && token.isAdjacent(nextToken) && nextToken.type != Token.OPERATOR) {
                    token = token.merge(sourceLine.getNextToken());
                }
            }
            else if (".".equals(token.getText())) {
                Token nextToken = sourceLine.peekNextToken();
                if (nextToken != null && token.isAdjacent(nextToken) && nextToken.type != Token.OPERATOR) {
                    token = token.merge(sourceLine.getNextToken());
                    token.type = 0;
                }
            }

            switch (state) {
                case S1: // Start of statement
                    if ("{".equals(token.getText())) {
                        parentNode = new StatementNode((parentNode));
                        parentNode.addToken(token);
                        break;
                    }
                    if ("}".equals(token.getText())) {
                        if (parentNode instanceof FunctionNode) {
                            StatementNode node = new StatementNode((parentNode));
                            node.addToken(token);
                            parentNode = parentNode.getParent();
                            break;
                        }
                        while (!(parentNode.getParent() instanceof FunctionNode)) {
                            Token parentLastToken = parentNode.getStopToken();
                            if (parentLastToken == null || "{".equals(parentLastToken.getText())) {
                                break;
                            }
                            parentNode = parentNode.getParent();
                        }
                        Token parentFirstToken = parentNode.getStartToken();
                        parentNode = parentNode.getParent();
                        if (parentFirstToken != null && "do".equals(parentFirstToken.getText())) {
                            tokens.add(token);
                            state = State.S2;
                            break;
                        }
                        StatementNode node = new StatementNode((parentNode));
                        node.addToken(token);
                        break;
                    }
                    tokens.add(token);
                    if ("if".equals(token.getText()) || "for".equals(token.getText()) || "while".equals(token.getText()) || "switch".equals(token.getText())) {
                        state = State.S3;
                        break;
                    }
                    if ("case".equals(token.getText()) || "default".equals(token.getText())) {
                        while (!(parentNode instanceof FunctionNode)) {
                            Token nodeToken = parentNode.getStartToken();
                            if (nodeToken != null && "switch".equals(nodeToken.getText())) {
                                break;
                            }
                            parentNode = parentNode.getParent();
                        }
                        state = State.S6;
                        break;
                    }
                    if ("else".equals(token.getText())) {
                        state = State.S4;
                        while (!(parentNode.getParent() instanceof RootNode) && !parentNode.getChilds().isEmpty()) {
                            Node node = parentNode.getChilds().getLast();
                            Token nodeToken = node.getStartToken();
                            if (nodeToken != null) {
                                if ("}".equals(nodeToken.getText())) {
                                    break;
                                }
                                if ("else".equals(nodeToken.getText())) {
                                    nodeToken = node.getStopToken();
                                }
                                if (nodeToken != null && "if".equals(nodeToken.getText())) {
                                    break;
                                }
                            }
                            parentNode = parentNode.getParent();
                        }
                        break;
                    }
                    while (!(parentNode.getParent() instanceof RootNode)) {
                        Token parentFirstToken = parentNode.getStartToken();
                        if (parentFirstToken != null && ("case".equals(parentFirstToken.getText()) || "default".equals(parentFirstToken.getText()))) {
                            break;
                        }
                        Token parentLastToken = parentNode.getStopToken();
                        if (parentLastToken == null || "{".equals(parentLastToken.getText())) {
                            break;
                        }
                        parentNode = parentNode.getParent();
                    }
                    state = State.S2;
                    break;

                case S2: // Terminate with ;
                    tokens.add(token);
                    if (processParenthesis(token)) {
                        break;
                    }
                    if ("{".equals(token.getText())) {
                        parentNode = new StatementNode((parentNode));
                        parentNode.addAllTokens(tokens);
                        tokens.clear();
                        state = State.S1;
                        break;
                    }
                    if (";".equals(token.getText())) {
                        StatementNode node = new StatementNode((parentNode));
                        node.addAllTokens(tokens);
                        tokens.clear();

                        Token parentFirstToken = parentNode.getStartToken();
                        if (parentFirstToken != null && ("case".equals(parentFirstToken.getText()) || "default".equals(parentFirstToken.getText()))) {
                            state = State.S1;
                            break;
                        }

                        Token parentLastToken = parentNode.getStopToken();
                        if (parentLastToken != null && !"{".equals(parentLastToken.getText())) {
                            parentNode = parentNode.getParent();
                        }

                        state = State.S1;
                        break;
                    }
                    break;

                case S3:
                    tokens.add(token);
                    if ("(".equals(token.getText())) {
                        nestingBraces.add(token.getText());
                        state = State.S5;
                        break;
                    }
                    break;

                case S4:
                    if ("if".equals(token.getText())) {
                        tokens.add(token);
                        state = State.S3;
                        break;
                    }
                    parentNode = new StatementNode((parentNode));
                    parentNode.addAllTokens(tokens);

                    tokens.clear();

                    if ("{".equals(token.getText())) {
                        parentNode.addToken(token);
                        state = State.S1;
                        break;
                    }

                    tokens.add(token);
                    if ("for".equals(token.getText()) || "while".equals(token.getText())) {
                        state = State.S3;
                        break;
                    }
                    state = State.S2;
                    break;

                case S5: // Balance parenthesis and start new block
                    if (processParenthesis(token)) {
                        tokens.add(token);
                        break;
                    }
                    if (nestingBraces.isEmpty()) {
                        parentNode = new StatementNode((parentNode));
                        parentNode.addAllTokens(tokens);

                        tokens.clear();

                        if ("{".equals(token.getText())) {
                            parentNode.addToken(token);
                            state = State.S1;
                            break;
                        }

                        tokens.add(token);
                        if ("if".equals(token.getText()) || "for".equals(token.getText()) || "while".equals(token.getText()) || "switch".equals(token.getText())) {
                            state = State.S3;
                            break;
                        }
                        if ("else".equals(token.getText())) {
                            state = State.S4;
                            break;
                        }
                        state = State.S2;
                        break;
                    }
                    tokens.add(token);
                    break;

                case S6: // Case statement
                    tokens.add(token);
                    if (processParenthesis(token)) {
                        break;
                    }
                    if (nestingBraces.isEmpty() && ":".equals(token.getText())) {
                        parentNode = new StatementNode((parentNode));
                        parentNode.addAllTokens(tokens);
                        tokens.clear();
                        state = State.S1;
                        break;
                    }
                    break;
            }
        }

        if (!sourceLine.hasMoreTokens() && !tokens.isEmpty()) {
            Node node = new StatementNode(parentNode);
            node.addAllTokens(tokens);
            tokens.clear();
        }
    }

    boolean processParenthesis(Token token) {
        if ("(".equals(token.getText()) || "[".equals(token.getText())) {
            nestingBraces.add(token.getText());
            return true;
        }
        if (")".equals(token.getText())) {
            if (!nestingBraces.isEmpty() && "(".equals(nestingBraces.getLast())) {
                nestingBraces.removeLast();
            }
            return true;
        }
        if ("]".equals(token.getText())) {
            if (!nestingBraces.isEmpty() && "[".equals(nestingBraces.getLast())) {
                nestingBraces.removeLast();
            }
            return true;
        }
        return false;
    }

    void parseStructure(SourceLine sourceLine) {
        Token token;

        if (parentNode instanceof RootNode) {
            Token type = sourceLine.getNextToken();
            Token identifier = sourceLine.getNextToken();

            token = sourceLine.getNextToken();
            if (token != null) {
                if ("{".equals(token.getText())) {
                    TypeDefinitionNode node = new TypeDefinitionNode(root, type, identifier);
                    node.addToken(token);
                    parentNode = node;
                    state = State.Struct1;
                }
                else {
                    sourceLine.setIndex(1);
                    parseDeclaration(sourceLine);
                }
            }
        }
        else if (parentNode instanceof TypeDefinitionNode node) {
            if (state == State.Struct2) {
                parseStructureVariable(node, sourceLine);
                parentNode = root;
                return;
            }
            while ((token = sourceLine.getNextToken()) != null) {
                if ("}".equals(token.getText())) {
                    node.addToken(token);
                    state = State.Struct2;
                    break;
                }

                TypeDefinitionNode.Definition member = new TypeDefinitionNode.Definition(node);

                member.addToken(member.type = token);
                if ((token = sourceLine.getNextToken()) != null) {
                    if ("*".equals(token.getText())) {
                        member.addToken(token);
                        member.type = member.type.merge(token);
                        member.type.type = Token.KEYWORD;
                        if ((token = sourceLine.getNextToken()) == null) {
                            break;
                        }
                    }
                }
                member.addToken(member.identifier = token);

                if ((token = sourceLine.getNextToken()) != null) {
                    if ("[".equals(token.getText())) {
                        member.addToken(token);
                        member.size = new ExpressionNode(member);
                        while ((token = sourceLine.getNextToken()) != null) {
                            if ("]".equals(token.getText())) {
                                member.addToken(token);
                                break;
                            }
                            member.size.addToken(token);
                        }
                        if (token != null && "]".equals(token.getText())) {
                            token = sourceLine.getNextToken();
                        }
                    }
                    if (token != null && ",".equals(token.getText())) {
                        member.addToken(token);
                        while ((token = sourceLine.getNextToken()) != null) {
                            TypeDefinitionNode.Definition child = new TypeDefinitionNode.Definition(member);

                            if ("*".equals(token.getText())) {
                                child.addToken(child.type = token);
                                if ((token = sourceLine.getNextToken()) == null) {
                                    break;
                                }
                            }
                            child.addToken(child.identifier = token);

                            if ((token = sourceLine.getNextToken()) != null) {
                                if ("[".equals(token.getText())) {
                                    child.addToken(token);
                                    child.size = new ExpressionNode(node);
                                    while ((token = sourceLine.getNextToken()) != null) {
                                        if ("]".equals(token.getText())) {
                                            child.addToken(token);
                                            break;
                                        }
                                        child.size.addToken(token);
                                    }
                                    if (token != null && "]".equals(token.getText())) {
                                        token = sourceLine.getNextToken();
                                    }
                                }
                            }
                            if (token == null || !",".equals(token.getText())) {
                                break;
                            }
                        }
                    }
                    if (token != null) {
                        member.addToken(token);
                    }
                }
            }
            if (token != null && "}".equals(token.getText())) {
                if ((token = sourceLine.peekNextToken()) != null) {
                    if (";".equals(token.getText())) {
                        node.addToken(token);
                    }
                    else {
                        parseStructureVariable(node, sourceLine);
                    }
                    parentNode = root;
                }
            }
        }
    }

    void parseStructureVariable(TypeDefinitionNode struct, SourceLine sourceLine) {
        Token token;

        while ((token = sourceLine.getNextToken()) != null) {
            VariableNode node = new VariableNode(root);

            node.addToken(node.type = struct.identifier);
            if ("*".equals(token.getText())) {
                node.addToken(token);
                node.type = new Token(node.type.start, node.type.line, node.type.column, Token.KEYWORD, node.type.getText() + " " + token.getText());
                if ((token = sourceLine.getNextToken()) == null) {
                    return;
                }
            }
            node.addToken(node.identifier = token);

            token = sourceLine.getNextToken();
            if (token != null) {
                if ("[".equals(token.getText())) {
                    node.addToken(token);
                    node.size = new ExpressionNode(node);
                    while ((token = sourceLine.getNextToken()) != null) {
                        if ("]".equals(token.getText())) {
                            node.addToken(token);
                            break;
                        }
                        node.size.addToken(token);
                    }
                    if (token != null && "]".equals(token.getText())) {
                        token = sourceLine.getNextToken();
                    }
                }
                else if ("=".equals(token.getText())) {
                    node.addToken(token);
                    while ((token = sourceLine.getNextToken()) != null) {
                        if (";".equals(token.getText()) || ",".equals(token.getText())) {
                            break;
                        }
                        node.addToken(token);
                    }
                }
            }
            if (token != null) {
                node.addToken(token);
            }

            if (token != null && ",".equals(token.getText())) {
                while ((token = sourceLine.getNextToken()) != null) {
                    VariableNode child = new VariableNode(node);

                    if ("*".equals(token.getText())) {
                        child.addToken(child.type = token);
                        if ((token = sourceLine.getNextToken()) == null) {
                            break;
                        }
                    }
                    child.addToken(child.identifier = token);

                    if ((token = sourceLine.getNextToken()) != null) {
                        if ("[".equals(token.getText())) {
                            child.addToken(token);
                            child.size = new ExpressionNode(node);
                            while ((token = sourceLine.getNextToken()) != null) {
                                if ("]".equals(token.getText())) {
                                    child.addToken(token);
                                    break;
                                }
                                child.size.addToken(token);
                            }
                            if (token != null && "]".equals(token.getText())) {
                                token = sourceLine.getNextToken();
                            }
                        }
                        else if ("=".equals(token.getText())) {
                            child.addToken(token);
                            while ((token = sourceLine.getNextToken()) != null) {
                                if (";".equals(token.getText()) || ",".equals(token.getText())) {
                                    break;
                                }
                                child.addToken(token);
                            }
                        }
                    }

                    while (token != null) {
                        node.addToken(token);
                        if (";".equals(token.getText()) || ",".equals(token.getText())) {
                            break;
                        }
                        token = sourceLine.getNextToken();
                    }
                    if (token == null || !",".equals(token.getText())) {
                        break;
                    }
                }
            }
        }
    }

    Node parseDeclaration(SourceLine sourceLine) {
        Token token;

        if ((token = sourceLine.getNextToken()) == null) {
            return null;
        }

        Token modifier = null;
        if ("static".equals(token.getText())) {
            modifier = token;
            if ((token = sourceLine.getNextToken()) == null) {
                VariableNode node = new VariableNode(root);
                node.addToken(node.modifier = modifier);
                return node;
            }
        }

        Token type = token;
        Token pointer = null;
        if ((token = sourceLine.getNextToken()) != null) {
            if ("*".equals(token.getText())) {
                pointer = token;
                if ((token = sourceLine.getNextToken()) == null) {
                    VariableNode node = new VariableNode(root);
                    node.modifier = modifier;
                    node.type = new Token(type.getStream(), type.start, type.line, type.column, Token.KEYWORD, type.getText() + " " + pointer.getText());
                    if (modifier != null) {
                        node.addToken(modifier);
                    }
                    node.addToken(type);
                    node.addToken(pointer);
                    return node;
                }
            }
        }
        Token identifier = token;

        token = sourceLine.getNextToken();
        if (token != null && "(".equals(token.getText())) {
            FunctionNode node = new FunctionNode(root, modifier, type, identifier);
            node.addToken(token);

            while ((token = sourceLine.getNextToken()) != null) {
                if (")".equals(token.getText())) {
                    node.addToken(token);
                    break;
                }

                FunctionNode.ParameterNode param = new FunctionNode.ParameterNode(node);
                param.type = token;
                param.addToken(token);
                if ((token = sourceLine.getNextToken()) != null) {
                    param.addToken(token);
                    if ("*".equals(token.getText())) {
                        param.type = type.merge(token);
                        param.type.type = Token.KEYWORD;
                        if ((token = sourceLine.getNextToken()) == null) {
                            break;
                        }
                        param.addToken(token);
                    }
                }
                param.identifier = token;

                while ((token = sourceLine.getNextToken()) != null) {
                    if (",".equals(token.getText()) || ")".equals(token.getText())) {
                        node.addToken(token);
                        break;
                    }
                    param.addToken(token);
                }
                if (token == null || !",".equals(token.getText())) {
                    break;
                }
            }

            token = sourceLine.getNextToken();
            if (token != null) {
                node.addToken(token);
                if ("{".equals(token.getText())) {
                    return node;
                }
            }
        }
        else {
            VariableNode node = new VariableNode(root);
            if (modifier != null) {
                node.addToken(node.modifier = modifier);
            }
            if (pointer != null) {
                node.addToken(type);
                node.addToken(pointer);
                node.type = new Token(type.getStream(), type.start, type.line, type.column, Token.KEYWORD, type.getText() + " " + pointer.getText());
            }
            else {
                node.addToken(node.type = type);
            }
            node.addToken(node.identifier = identifier);

            if (token != null) {
                if ("[".equals(token.getText())) {
                    node.addToken(token);
                    node.size = new ExpressionNode(node);
                    while ((token = sourceLine.getNextToken()) != null) {
                        if ("]".equals(token.getText())) {
                            node.addToken(token);
                            break;
                        }
                        node.size.addToken(token);
                    }
                    if (token != null && "]".equals(token.getText())) {
                        token = sourceLine.getNextToken();
                    }
                }
                else if ("=".equals(token.getText())) {
                    node.addToken(token);
                    while ((token = sourceLine.getNextToken()) != null) {
                        if (";".equals(token.getText()) || ",".equals(token.getText())) {
                            break;
                        }
                        node.addToken(token);
                    }
                }
            }
            if (token != null) {
                node.addToken(token);
            }

            if (token != null && ",".equals(token.getText())) {
                while ((token = sourceLine.getNextToken()) != null) {
                    VariableNode child = new VariableNode(node);

                    if ("*".equals(token.getText())) {
                        child.addToken(child.type = token);
                        if ((token = sourceLine.getNextToken()) == null) {
                            break;
                        }
                    }
                    child.addToken(child.identifier = token);

                    if ((token = sourceLine.getNextToken()) != null) {
                        if ("[".equals(token.getText())) {
                            child.addToken(token);
                            child.size = new ExpressionNode(node);
                            while ((token = sourceLine.getNextToken()) != null) {
                                if ("]".equals(token.getText())) {
                                    child.addToken(token);
                                    break;
                                }
                                child.size.addToken(token);
                            }
                            if (token != null && "]".equals(token.getText())) {
                                token = sourceLine.getNextToken();
                            }
                        }
                        else if ("=".equals(token.getText())) {
                            child.addToken(token);
                            while ((token = sourceLine.getNextToken()) != null) {
                                if (";".equals(token.getText()) || ",".equals(token.getText())) {
                                    break;
                                }
                                child.addToken(token);
                            }
                        }
                    }

                    while (token != null) {
                        node.addToken(token);
                        if (";".equals(token.getText()) || ",".equals(token.getText())) {
                            break;
                        }
                        token = sourceLine.getNextToken();
                    }
                    if (token == null || !",".equals(token.getText())) {
                        break;
                    }
                }
            }

            return node;
        }

        return null;
    }

    void parsePreprocessor(Node parent, SourceLine sourceLine) {
        Token token;

        Token firstToken = sourceLine.getNextToken();
        Token keywordToken = sourceLine.getNextToken();
        if (keywordToken == null) {
            return;
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
                processDirective(node);
                break;
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
                processDirective(node);
                break;
            }

            case "include": {
                DirectiveNode.IncludeNode node = new DirectiveNode.IncludeNode(parent);
                node.addToken(firstToken);
                node.addToken(keywordToken);

                node.file = sourceLine.getNextToken();
                if (node.file != null) {
                    if ("<".equals(node.file.getText())) {
                        node.addToken(node.file);
                        while ((token = sourceLine.getNextToken()) != null) {
                            node.addToken(token);
                            node.file = node.file.merge(token);
                            if (">".equals(token.getText())) {
                                break;
                            }
                        }
                    }
                    else {
                        node.addToken(node.file);
                    }
                    while ((token = sourceLine.getNextToken()) != null) {
                        node.addToken(token);
                    }
                }

                processDirective(node);
                break;
            }

            default: {
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
                processDirective(node);
                break;
            }
        }
    }

    protected boolean isExcluded() {
        return false;
    }

    protected void processDirective(DirectiveNode node) {
        // Do nothing
    }

    void parseDatLine(Node parent, SourceLine sourceLine) {
        int state = 1;
        DataLineNode node = new DataLineNode(parent);
        DataLineNode.ParameterNode parameter = null;

        Token token, nextToken;
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
                    if ((nextToken = sourceLine.peekNextToken()) != null) {
                        if (".".equals(nextToken.getText()) && token.isAdjacent(nextToken)) {
                            token = token.merge(sourceLine.getNextToken());
                            nextToken = sourceLine.peekNextToken();
                        }
                    }
                    if (nextToken != null) {
                        if (nextToken.type != Token.OPERATOR && token.isAdjacent(nextToken)) {
                            token = token.merge(sourceLine.getNextToken());
                        }
                    }
                }
            }
            if (state == 1 || state == 4 || state == 5) {
                if (".".equals(token.getText())) {
                    if ((nextToken = sourceLine.peekNextToken()) != null) {
                        if (nextToken.type != Token.OPERATOR && token.isAdjacent(nextToken)) {
                            token = token.merge(sourceLine.getNextToken());
                        }
                    }
                }
            }
            node.addToken(token);
            switch (state) {
                case 1:
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
                    if ("debug".equalsIgnoreCase(token.getText())) {
                        node.instruction = token;
                        state = 9;
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
                    // fall-through
                case 3:
                    node.instruction = token;
                    state = "debug".equalsIgnoreCase(token.getText()) ? 9 : 4;
                    break;
                case 4:
                    if (Spin2Model.isPAsmModifier(token.getText())) {
                        node.modifier = new ModifierNode(node);
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
                        Token next = sourceLine.peekNextToken();
                        if ("++".equals(next.getText()) || "--".equals(next.getText()) || "ptra".equals(next.getText()) || "ptrb".equals(next.getText())) {
                            state = 8;
                            break;
                        }
                        break;
                    }
                    if (Spin2Model.isPAsmModifier(token.getText())) {
                        node.modifier = new ModifierNode(node);
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
                case 8:
                    if (",".equals(token.getText())) {
                        parameter = new DataLineNode.ParameterNode(node);
                        node.parameters.add(parameter);
                        state = 5;
                        break;
                    }
                    if (Spin2Model.isPAsmModifier(token.getText())) {
                        node.modifier = new ModifierNode(node);
                        node.modifier.addToken(token);
                        state = 6;
                        break;
                    }
                    parameter.addToken(token);
                    break;
            }
        }
    }

}
