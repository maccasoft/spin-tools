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

import java.util.List;

import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.LocalVariable;
import com.maccasoft.propeller.model.LocalVariableNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.StatementNode;
import com.maccasoft.propeller.model.Token;

public class Spin1Method {

    Spin1Context scope;

    String name;
    int localSize;
    Spin1BytecodeExpression root;

    public Spin1Method(Spin1Context scope, MethodNode node) {
        this.scope = scope;

        this.name = node.name.getText();

        if (node.getReturnVariables().size() != 0) {
            Node child = node.getReturnVariable(0);
            scope.addSymbol(child.getText(), new LocalVariable(child.getText(), 0));
            scope.addSymbol("@" + child.getText(), new LocalVariable(child.getText(), 0));
        }

        int offset = 4;
        for (Node child : node.getParameters()) {
            scope.addSymbol(child.getText(), new LocalVariable(child.getText(), offset));
            scope.addSymbol("@" + child.getText(), new LocalVariable(child.getText(), offset));
            offset += 4;
        }
        for (LocalVariableNode child : node.getLocalVariables()) {
            scope.addSymbol(child.getIdentifier().getText(), new LocalVariable(child.getIdentifier().getText(), offset));
            scope.addSymbol("@" + child.getIdentifier().getText(), new LocalVariable(child.getIdentifier().getText(), offset));

            int size = 4;
            if (child.getType() != null) {
                if ("BYTE".equalsIgnoreCase(child.getType().getText())) {
                    size = 1;
                }
                if ("WORD".equalsIgnoreCase(child.getType().getText())) {
                    size = 2;
                }
            }
            if (child.getSize() != null) {
                Expression count = Spin1Compiler.buildExpression(child.getSize().getTokens(), scope);
                size *= count.getNumber().intValue();
            }
            offset += ((size + 3) / 4) * 4;
        }

        this.localSize = offset;

        root = new Spin1BytecodeExpression("RETURN");

        for (Node child : node.getChilds()) {
            if (child instanceof StatementNode) {
                compileStatementBlock(root, child);
            }
        }
    }

    void compileStatementBlock(Spin1BytecodeExpression parent, Node node) {
        Spin1BytecodeExpressionCompiler compiler = new Spin1BytecodeExpressionCompiler();

        List<Token> tokens = node.getTokens();
        int state = 0;

        if (tokens.size() != 0) {
            if ("REPEAT".equalsIgnoreCase(tokens.get(0).getText())) {
                Spin1BytecodeExpression expression = new Spin1BytecodeExpression(tokens.get(0));
                parent.childs.add(expression);
                for (Node child : node.getChilds()) {
                    if (child instanceof StatementNode) {
                        compileStatementBlock(expression, child);
                    }
                }
                return;
            }
        }

        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            switch (state) {
                case 0:
                    if (Spin1BytecodeExpressionCompiler.operatorPrecedence.containsKey(token.getText().toUpperCase())) {
                        compiler.addUnaryOperator(token);
                        break;
                    }
                    // fall through
                case 1:
                    if (Spin1BytecodeExpressionCompiler.operatorPrecedence.containsKey(token.getText().toUpperCase())) {
                        compiler.addOperatorToken(token);
                        break;
                    }
                    if (i + 1 < tokens.size()) {
                        Token next = tokens.get(i + 1);
                        if (next.type != Token.EOF && next.type != Token.NL) {
                            if ("(".equals(next.getText())) {
                                compiler.addFunctionOperatorToken(token, next);
                                i++;
                                state = 0;
                                break;
                            }
                        }
                    }
                    compiler.addValueToken(token);
                    state = 2;
                    break;
                case 2:
                    compiler.addOperatorToken(token);
                    state = 1;
                    break;
            }
        }

        Spin1BytecodeExpression expression = compiler.getExpression();
        parent.childs.add(expression);

        for (Node child : node.getChilds()) {
            if (child instanceof StatementNode) {
                compileStatementBlock(expression, child);
            }
        }
    }

    public Spin1Context getScope() {
        return scope;
    }

    public String getName() {
        return name;
    }

    public int resolve(int address) {
        scope.setAddress(address);
        return address;
    }

    public void setLocalSize(int localSize) {
        this.localSize = localSize;
    }

    public int getLocalSize() {
        return localSize;
    }

}
