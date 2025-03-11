/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 */

package com.maccasoft.propeller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.maccasoft.propeller.SpinObject.LinkDataObject;
import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.model.DirectiveNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.TokenIterator;
import com.maccasoft.propeller.spin2.Spin2ExpressionBuilder;

public abstract class ObjectCompiler {

    ObjectCompiler parent;
    File file;
    protected Context scope;

    List<ObjectCompiler> childs = new ArrayList<>();

    boolean errors;
    List<CompilerException> messages = new ArrayList<>();

    public ObjectCompiler(File file, Context scope) {
        this.parent = null;
        this.file = file;
        this.scope = new Context(scope);
    }

    public ObjectCompiler(ObjectCompiler parent, File file, Context scope) {
        this.parent = parent;
        this.file = file;
        this.scope = new Context(scope);
        if (parent != null) {
            parent.childs.add(this);
        }
    }

    public ObjectCompiler getParent() {
        return parent;
    }

    public File getFile() {
        return file;
    }

    public Context getScope() {
        return scope;
    }

    public abstract SpinObject compileObject(Node root);

    public abstract void compileStep1(Node root);

    public abstract void compileStep2(boolean keepFirst);

    public abstract Map<String, Expression> getPublicSymbols();

    public abstract int getVarSize();

    public abstract SpinObject generateObject(int memoryOffset);

    public abstract List<LinkDataObject> getObjectLinks();

    public static class Condition {

        public Node node;
        public boolean evaluated;
        public boolean skip;
        public boolean flipped;

        public Condition(Node node, boolean evaluated, boolean skip) {
            this.node = node;
            this.evaluated = evaluated;
            this.skip = skip;
        }

    }

    protected Stack<Condition> conditionStack = new Stack<>();

    protected void compileDirective(DirectiveNode node) {
        Token token;
        Condition condition;
        boolean skip = !conditionStack.isEmpty() && conditionStack.peek().skip;

        TokenIterator iter = node.tokenIterator();

        token = iter.next();
        if (!iter.hasNext()) {
            throw new CompilerException("expecting directive", new Token(token.getStream(), token.stop));
        }

        token = iter.next();
        switch (token.getText().toLowerCase()) {
            case "define":
                node.setExclude(skip);
                if (!skip) {
                    if (!iter.hasNext()) {
                        throw new CompilerException("expecting identifier", new Token(token.getStream(), token.stop));
                    }
                    token = iter.next();
                    if (token.type != Token.KEYWORD) {
                        throw new CompilerException("invalid identifier", token);
                    }
                    String identifier = token.getText();
                    if (scope.hasSymbol(identifier) || scope.isDefined(identifier)) {
                        logMessage(new CompilerException(CompilerException.WARNING, "duplicated definition: " + identifier, token));
                    }

                    List<Token> list = new ArrayList<>();
                    iter.forEachRemaining(t -> {
                        list.add(t);
                    });
                    scope.addDefinition(identifier, list);
                }
                break;

            case "error":
                node.setExclude(skip);
                if (!skip) {
                    throw new CompilerException(CompilerException.ERROR, node.getText(), node);
                }
                break;

            case "warning":
                node.setExclude(skip);
                if (!skip) {
                    throw new CompilerException(CompilerException.WARNING, node.getText(), node);
                }
                break;

            case "ifdef":
                node.setExclude(skip);
                if (!skip) {
                    if (!iter.hasNext()) {
                        throw new CompilerException("expecting identifier", new Token(token.getStream(), token.stop));
                    }
                    Token identifier = iter.next();
                    if (token.type != Token.KEYWORD) {
                        throw new CompilerException("invalid identifier", identifier);
                    }
                    skip = !(scope.isDefined(identifier.getText()) || scope.hasSymbol(identifier.getText()));
                    conditionStack.push(new Condition(node, true, skip));
                }
                else {
                    conditionStack.push(new Condition(node, false, skip));
                }
                break;

            case "elifdef":
            case "elseifdef":
                if (conditionStack.isEmpty()) {
                    throw new CompilerException("misplaced #" + token.getText(), token);
                }
                condition = conditionStack.peek();
                node.setExclude(skip && !condition.evaluated);
                if (condition.evaluated) {
                    if (!condition.flipped) {
                        condition.skip = !condition.skip;
                        condition.flipped = true;
                    }
                    if (!condition.skip) {
                        conditionStack.pop();

                        Token identifier = iter.next();
                        if (token.type != Token.KEYWORD) {
                            throw new CompilerException("invalid identifier", identifier);
                        }
                        skip = !(scope.isDefined(identifier.getText()) || scope.hasSymbol(identifier.getText()));

                        conditionStack.push(new Condition(node, true, skip));
                    }
                }
                break;

            case "ifndef":
                node.setExclude(skip);
                if (!skip) {
                    if (!iter.hasNext()) {
                        throw new CompilerException("expecting identifier", new Token(token.getStream(), token.stop));
                    }
                    Token identifier = iter.next();
                    if (token.type != Token.KEYWORD) {
                        throw new CompilerException("invalid identifier", identifier);
                    }
                    skip = scope.isDefined(identifier.getText()) || scope.hasSymbol(identifier.getText());
                    conditionStack.push(new Condition(node, true, skip));
                }
                else {
                    conditionStack.push(new Condition(node, false, skip));
                }
                break;

            case "elifndef":
            case "elseifndef":
                if (conditionStack.isEmpty()) {
                    throw new CompilerException("misplaced #" + token.getText(), token);
                }
                condition = conditionStack.peek();
                node.setExclude(skip && !condition.evaluated);
                if (condition.evaluated) {
                    if (!condition.flipped) {
                        condition.skip = !condition.skip;
                        condition.flipped = true;
                    }
                    if (!condition.skip) {
                        conditionStack.pop();

                        Token identifier = iter.next();
                        if (token.type != Token.KEYWORD) {
                            throw new CompilerException("invalid identifier", identifier);
                        }
                        skip = scope.isDefined(identifier.getText()) || scope.hasSymbol(identifier.getText());

                        conditionStack.push(new Condition(node, true, skip));
                    }
                }
                break;

            case "else":
                if (conditionStack.isEmpty()) {
                    throw new CompilerException("misplaced #" + token.getText(), token);
                }
                condition = conditionStack.peek();
                node.setExclude(skip && !condition.evaluated);
                if (condition.evaluated) {
                    if (!condition.flipped) {
                        condition.skip = !condition.skip;
                        condition.flipped = true;
                    }
                }
                break;

            case "if":
                node.setExclude(skip);
                if (!skip) {
                    Spin2ExpressionBuilder builder = new Spin2ExpressionBuilder(scope);
                    while (iter.hasNext()) {
                        token = iter.next();
                        if ("defined".equalsIgnoreCase(token.getText())) {
                            builder.addToken(token);
                            if (iter.hasNext()) {
                                builder.addTokenLiteral(iter.next());
                            }
                            if (iter.hasNext()) {
                                builder.addTokenLiteral(iter.next());
                            }
                            if (iter.hasNext()) {
                                builder.addTokenLiteral(iter.next());
                            }
                        }
                        else {
                            builder.addToken(token);
                        }
                    }
                    Expression expression = builder.getExpression();
                    if (!expression.isConstant()) {
                        throw new RuntimeException("expression is not constant");
                    }
                    skip = expression.getNumber().intValue() == 0;
                    conditionStack.push(new Condition(node, true, skip));
                }
                else {
                    conditionStack.push(new Condition(node, false, skip));
                }
                break;

            case "elif":
            case "elseif":
                if (conditionStack.isEmpty()) {
                    throw new CompilerException("misplaced #" + token.getText(), token);
                }
                condition = conditionStack.peek();
                node.setExclude(skip && !condition.evaluated);
                if (condition.evaluated) {
                    if (!condition.flipped) {
                        condition.skip = !condition.skip;
                        condition.flipped = true;
                    }
                    if (!condition.skip) {
                        conditionStack.pop();

                        Spin2ExpressionBuilder builder = new Spin2ExpressionBuilder(scope);
                        while (iter.hasNext()) {
                            token = iter.next();
                            if ("defined".equals(token.getText())) {
                                builder.addToken(token);
                                if (iter.hasNext()) {
                                    builder.addTokenLiteral(iter.next());
                                }
                                if (iter.hasNext()) {
                                    builder.addTokenLiteral(iter.next());
                                }
                                if (iter.hasNext()) {
                                    builder.addTokenLiteral(iter.next());
                                }
                            }
                            else {
                                builder.addToken(token);
                            }
                        }
                        Expression expression = builder.getExpression();
                        if (!expression.isConstant()) {
                            throw new RuntimeException("expression is not constant");
                        }
                        skip = expression.getNumber().intValue() == 0;

                        conditionStack.push(new Condition(node, true, skip));
                    }
                }
                else {
                    conditionStack.push(new Condition(node, false, skip));
                }
                break;

            case "endif":
                if (conditionStack.isEmpty()) {
                    throw new CompilerException("misplaced #" + token.getText(), token);
                }
                condition = conditionStack.pop();
                node.setExclude(skip && !condition.evaluated);
                break;

            default:
                logMessage(new CompilerException("unsupported directive", token));
                break;
        }
    }

    protected void logMessage(CompilerException message) {
        message.fileName = file.getName();
        if (message.hasChilds()) {
            for (CompilerException msg : message.getChilds()) {
                logMessage(msg);
            }
        }
        else {
            if (message.type == CompilerException.ERROR) {
                errors = true;
            }
            if (!messages.contains(message)) {
                messages.add(message);
            }
        }
    }

    public boolean hasErrors() {
        return errors;
    }

    public List<CompilerException> getMessages() {
        return messages;
    }

}
