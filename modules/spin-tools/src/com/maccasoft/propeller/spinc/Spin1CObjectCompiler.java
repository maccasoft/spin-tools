/*
 * Copyright (c) 2023 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spinc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.collections4.map.ListOrderedMap;

import com.maccasoft.propeller.Compiler.ObjectInfo;
import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.ObjectCompiler;
import com.maccasoft.propeller.SpinObject.LinkDataObject;
import com.maccasoft.propeller.SpinObject.LongDataObject;
import com.maccasoft.propeller.SpinObject.WordDataObject;
import com.maccasoft.propeller.expressions.ContextLiteral;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.LocalVariable;
import com.maccasoft.propeller.expressions.Method;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.expressions.SpinObject;
import com.maccasoft.propeller.expressions.Variable;
import com.maccasoft.propeller.model.DirectiveNode;
import com.maccasoft.propeller.model.FunctionNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.StatementNode;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.TokenIterator;
import com.maccasoft.propeller.model.VariableNode;
import com.maccasoft.propeller.spin1.Spin1Compiler;
import com.maccasoft.propeller.spin1.Spin1Context;
import com.maccasoft.propeller.spin1.Spin1ExpressionBuilder;
import com.maccasoft.propeller.spin1.Spin1GlobalContext;
import com.maccasoft.propeller.spin1.Spin1Method;
import com.maccasoft.propeller.spin1.Spin1MethodLine;
import com.maccasoft.propeller.spin1.Spin1Object;
import com.maccasoft.propeller.spin1.Spin1Object.Spin1LinkDataObject;
import com.maccasoft.propeller.spin1.Spin1PAsmCompiler;
import com.maccasoft.propeller.spin1.Spin1PAsmLine;
import com.maccasoft.propeller.spin1.Spin1StatementNode;
import com.maccasoft.propeller.spin1.bytecode.Address;
import com.maccasoft.propeller.spin1.bytecode.Bytecode;
import com.maccasoft.propeller.spin1.bytecode.CaseJmp;
import com.maccasoft.propeller.spin1.bytecode.CaseRangeJmp;
import com.maccasoft.propeller.spin1.bytecode.Jmp;
import com.maccasoft.propeller.spin1.bytecode.Jnz;
import com.maccasoft.propeller.spin1.bytecode.Jz;
import com.maccasoft.propeller.spin1.instructions.Org;
import com.maccasoft.propeller.spin1.instructions.Res;

public class Spin1CObjectCompiler extends ObjectCompiler {

    Spin1CContext scope;

    int objectVarSize;

    List<Variable> variables = new ArrayList<>();
    List<Spin1PAsmLine> source = new ArrayList<>();
    List<Spin1MethodLine> setupLines = new ArrayList<>();
    List<Spin1Method> methods = new ArrayList<>();
    Map<String, ObjectInfo> objects = ListOrderedMap.listOrderedMap(new HashMap<>());

    boolean errors;
    List<CompilerException> messages = new ArrayList<CompilerException>();

    Map<String, Expression> publicSymbols = new HashMap<String, Expression>();
    List<LinkDataObject> objectLinks = new ArrayList<>();
    List<LongDataObject> methodData = new ArrayList<>();

    Spin1Compiler compiler;
    Spin1CBytecodeCompiler bytecodeCompiler;
    Spin1PAsmCompiler pasmCompiler;

    public Spin1CObjectCompiler(Spin1Compiler compiler) {
        this.scope = new Spin1CContext(new Spin1GlobalContext(true));
        this.compiler = compiler;

        this.scope.addDefinition("__P1__", new NumberLiteral(1));
        this.scope.addDefinition("__P2__", new NumberLiteral(0));
        this.scope.addDefinition("__SPINTOOLS__", new NumberLiteral(1));
    }

    public Spin1Object compileObject(Node root) {
        compile(root);
        compilePass2();
        return generateObject();
    }

    @Override
    public void compile(Node root) {
        objectVarSize = 0;

        bytecodeCompiler = new Spin1CBytecodeCompiler() {

            @Override
            protected boolean isAddress(String text) {
                return text.startsWith("&");
            }

            @Override
            protected void logMessage(CompilerException message) {
                Spin1CObjectCompiler.this.logMessage(message);
            }

        };
        pasmCompiler = new Spin1PAsmCompiler() {

            @Override
            protected Node getParsedSource(String fileName) {
                return null;
            }

            @Override
            protected byte[] getBinaryFile(String fileName) {
                return Spin1CObjectCompiler.this.getBinaryFile(fileName);
            }

            @Override
            protected void logMessage(CompilerException message) {
                Spin1CObjectCompiler.this.logMessage(message);
            }

        };

        for (Node node : new ArrayList<>(root.getChilds())) {
            try {
                if (node instanceof DirectiveNode) {
                    compileDirective((DirectiveNode) node);
                }
                else if (node instanceof VariableNode) {
                    if (conditionStack.isEmpty() || !conditionStack.peek().skip) {
                        compileVariable(bytecodeCompiler, (VariableNode) node);
                    }
                    else {
                        Token token = new Token(node.getStartToken().getStream(), node.getStartIndex());
                        token.stop = node.getStopIndex();
                        root.addComment(token);
                        node.getParent().getChilds().remove(node);
                    }
                }
                else if (node instanceof FunctionNode) {
                    compileFunction((FunctionNode) node);
                }
            } catch (CompilerException e) {
                logMessage(e);
            } catch (Exception e) {
                logMessage(new CompilerException(e, node));
            }
        }

        objectVarSize = (objectVarSize + 3) & ~3;

        if (scope.isDefined("_CLKMODE")) {
            scope.addSymbol("_CLKMODE", compileDefinedExpression("_CLKMODE"));
        }
        if (scope.isDefined("_CLKFREQ")) {
            scope.addSymbol("_CLKFREQ", compileDefinedExpression("_CLKFREQ"));
        }
        if (scope.isDefined("_XINFREQ")) {
            scope.addSymbol("_XINFREQ", compileDefinedExpression("_XINFREQ"));
        }

        try {
            determineClock();
        } catch (CompilerException e) {
            logMessage(e);
        }

        for (Spin1Method method : methods) {
            Node node = (Node) method.getData();
            List<Spin1MethodLine> methodLines = compileStatement(method, (Spin1CContext) method.getScope(), null, node);
            for (Spin1MethodLine line : methodLines) {
                method.addSource(line);
            }

            if (setupLines.size() != 0) {
                method.getLines().addAll(0, setupLines);
                setupLines.clear();
            }

            List<Spin1MethodLine> lines = method.getLines();
            if (lines.size() == 0 || !"return".equals(lines.get(lines.size() - 1).getStatement())) {
                Spin1MethodLine line = new Spin1MethodLine(method.getScope(), "RETURN");
                line.addSource(new Bytecode(line.getScope(), 0b00110010, line.getStatement()));
                method.addSource(line);
            }
        }
    }

    Expression compileDefinedExpression(String identifier) {
        try {
            List<Token> tokens = scope.getDefinition(identifier);
            if (tokens != null) {
                Spin1ExpressionBuilder builder = new Spin1ExpressionBuilder(scope);
                tokens.iterator().forEachRemaining((t) -> {
                    builder.addToken(t);
                });
                return builder.getExpression();
            }
        } catch (CompilerException e) {
            logMessage(e);
        } catch (Exception e) {

        }
        return null;
    }

    void compileDirective(DirectiveNode node) {
        Token token;
        boolean skip = !conditionStack.isEmpty() && conditionStack.peek().skip;

        Iterator<Token> iter = node.getTokens().iterator();
        token = iter.next();
        if (!iter.hasNext()) {
            throw new CompilerException("expecting directive", new Token(token.getStream(), token.stop));
        }
        token = iter.next();
        if ("include".equals(token.getText())) {
            if (!skip) {
                if (!iter.hasNext()) {
                    throw new CompilerException("expecting object file", new Token(token.getStream(), token.stop));
                }
                token = iter.next();
                if (token.type != Token.STRING) {
                    throw new CompilerException("invalid keyword", token);
                }
                if (iter.hasNext()) {
                    throw new CompilerException("unexpected", iter.next());
                }

                String fileName = token.getText().substring(1, token.getText().length() - 1);

                ObjectInfo info = compiler.getObjectInfo(fileName);
                if (info == null) {
                    logMessage(new CompilerException("object " + token + " not found", token));
                    return;
                }
                if (info.hasErrors()) {
                    logMessage(new CompilerException("object " + token + " has errors", token));
                    return;
                }

                String name = fileName;
                if (name.toLowerCase().endsWith(".spin2") || name.toLowerCase().endsWith(".c")) {
                    name = name.substring(0, name.lastIndexOf('.'));
                }

                for (Entry<String, Expression> objEntry : info.compiler.getPublicSymbols().entrySet()) {
                    if (!(objEntry.getValue() instanceof Method)) {
                        String identifier = objEntry.getKey();
                        if (scope.hasSymbol(identifier) || scope.isDefined(identifier)) {
                            logMessage(new CompilerException(CompilerException.WARNING, "duplicated definition: " + identifier, token));
                        }
                        else {
                            scope.addSymbol(identifier, objEntry.getValue());
                        }
                    }
                }
            }
            return;
        }
        else if ("define".equals(token.getText())) {
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
                if (iter.hasNext()) {
                    while (iter.hasNext()) {
                        list.add(iter.next());
                    }
                }
                scope.addDefinition(identifier, list);
            }
            else {
                Node root = node.getParent();
                while (root.getParent() != null) {
                    root = root.getParent();
                }
                for (Token t : node.getTokens()) {
                    root.addComment(t);
                }
                node.getParent().getChilds().remove(node);
            }
            return;
        }
        else if ("pragma".equals(token.getText())) {
            // Ignore
        }
        else {
            compileConditionalDirective(token, iter);
        }
    }

    class Condition {

        final boolean evaluated;
        final boolean skip;

        public Condition(boolean evaluated, boolean skip) {
            this.evaluated = evaluated;
            this.skip = skip;
        }

    }
    Stack<Condition> conditionStack = new Stack<>();

    void compileConditionalDirective(Token token, Iterator<Token> iter) {
        boolean skip = !conditionStack.isEmpty() && conditionStack.peek().skip;

        if ("ifdef".equals(token.getText())) {
            if (!skip) {
                if (!iter.hasNext()) {
                    throw new CompilerException("expecting identifier", new Token(token.getStream(), token.stop));
                }
                Token identifier = iter.next();
                if (token.type != Token.KEYWORD) {
                    throw new CompilerException("invalid identifier", new Token(token.getStream(), token.stop));
                }
                skip = !scope.isDefined(identifier.getText());
                conditionStack.push(new Condition(true, skip));
            }
            else {
                conditionStack.push(new Condition(false, skip));
            }
        }
        else if ("ifndef".equals(token.getText())) {
            if (!skip) {
                if (!iter.hasNext()) {
                    throw new CompilerException("expecting identifier", new Token(token.getStream(), token.stop));
                }
                Token identifier = iter.next();
                if (token.type != Token.KEYWORD) {
                    throw new CompilerException("invalid identifier", new Token(token.getStream(), token.stop));
                }
                skip = scope.isDefined(identifier.getText());
                conditionStack.push(new Condition(true, skip));
            }
            else {
                conditionStack.push(new Condition(false, skip));
            }
        }
        else if ("else".equals(token.getText())) {
            if (conditionStack.isEmpty()) {
                throw new CompilerException("misplaced #else", token);
            }
            if (conditionStack.peek().evaluated) {
                skip = !conditionStack.pop().skip;
                conditionStack.push(new Condition(true, skip));
            }
            else {
                conditionStack.push(new Condition(false, skip));
            }
        }
        else if ("if".equals(token.getText())) {
            if (!skip) {
                CExpressionBuilder builder = new CExpressionBuilder(scope);
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
                conditionStack.push(new Condition(true, skip));
            }
            else {
                conditionStack.push(new Condition(false, skip));
            }
        }
        else if ("elif".equals(token.getText())) {
            if (conditionStack.isEmpty()) {
                throw new CompilerException("misplaced #elif", token);
            }
            if (conditionStack.peek().evaluated) {
                conditionStack.pop();

                CExpressionBuilder builder = new CExpressionBuilder(scope);
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

                conditionStack.push(new Condition(true, skip));
            }
            else {
                conditionStack.push(new Condition(false, skip));
            }
        }
        else if ("endif".equals(token.getText())) {
            if (conditionStack.isEmpty()) {
                throw new CompilerException("misplaced #endif", token);
            }
            conditionStack.pop();
        }
        else {
            throw new CompilerException("unsupported directive", token);
        }
    }

    void compileVariable(Spin1CBytecodeCompiler compiler, VariableNode node) {
        TokenIterator iter = node.iterator();

        Token token = iter.next();
        String type = token.getText();

        if (!type.matches("(int|long|short|word|byte)[\\s]*[*]?")) {
            type = token.getText();

            if (!iter.hasNext()) {
                throw new CompilerException("expecting identifier", new Token(token.getStream(), token.stop));
            }
            Token identifier = iter.next();
            if (identifier.type != Token.KEYWORD) {
                throw new CompilerException("expecting identifier", identifier);
            }

            ObjectInfo info = objects.get(type);
            if (info == null) {
                info = this.compiler.getObjectInfo(type);
                if (info == null) {
                    logMessage(new CompilerException("object '" + type + "' not found", token));
                    return;
                }
                if (info.hasErrors()) {
                    logMessage(new CompilerException("object '" + type + "' has errors", token));
                    return;
                }
            }

            Expression size = new NumberLiteral(1);

            if (iter.hasNext() && "[".equals(iter.peekNext().getText())) {
                token = iter.next();
                if (!iter.hasNext()) {
                    throw new CompilerException("expecting expression", token);
                }
                size = buildIndexExpression(iter);
            }

            if (iter.hasNext()) {
                throw new CompilerException("expecting end of statement", iter.next());
            }

            objects.put(identifier.getText(), new ObjectInfo(info.compiler, size));

            try {
                int count = size.getNumber().intValue();

                LinkDataObject linkData = new Spin1LinkDataObject(info.compiler, info.compiler.getVarSize());
                for (Entry<String, Expression> objEntry : info.compiler.getPublicSymbols().entrySet()) {
                    if (objEntry.getValue() instanceof Method) {
                        String qualifiedName = identifier.getText() + "." + objEntry.getKey();
                        Method objectMethod = (Method) objEntry.getValue();
                        Method method = new Method(objectMethod.getName(), objectMethod.getArgumentsCount(), objectMethod.getReturnsCount()) {

                            @Override
                            public int getIndex() {
                                return objectMethod.getIndex();
                            }

                            @Override
                            public int getObjectIndex() {
                                return objectLinks.indexOf(linkData) + methodData.size() + 1;
                            }

                        };
                        method.setData(Spin1Method.class.getName(), objectMethod.getData(Spin1Method.class.getName()));
                        scope.addSymbol(qualifiedName, method);
                    }
                }
                scope.addSymbol(identifier.getText(), new SpinObject(identifier.getText(), count) {

                    @Override
                    public int getIndex() {
                        return objectLinks.indexOf(linkData) + methodData.size() + 1;
                    }

                });
                objectLinks.add(linkData);

                for (int i = 1; i < count; i++) {
                    objectLinks.add(new Spin1LinkDataObject(info.compiler, info.compiler.getVarSize()));
                }

            } catch (CompilerException e) {
                logMessage(e);
            } catch (Exception e) {
                logMessage(new CompilerException(e, info.count.getData()));
            }

            return;
        }

        while (iter.hasNext()) {
            Token identifier = iter.next();
            if ("*".equals(identifier.getText())) {
                type += " *";
                if (!iter.hasNext()) {
                    throw new CompilerException("expecting identifier", identifier);
                }
                identifier = iter.next();
            }
            if (identifier.type != Token.KEYWORD) {
                throw new CompilerException("expecting identifier", identifier);
            }
            Expression size = new NumberLiteral(1);

            if (iter.hasNext() && "[".equals(iter.peekNext().getText())) {
                token = iter.next();
                if (!iter.hasNext()) {
                    throw new CompilerException("expecting expression", token);
                }
                size = buildIndexExpression(iter);
            }

            try {
                String identifierText = identifier.getText();
                Variable var = new Variable(type, identifier.getText(), size, objectVarSize);
                scope.addSymbol(identifierText, var);
                variables.add(var);
                var.setData(identifier);

                int varSize = size.getNumber().intValue();
                if ("WORD".equalsIgnoreCase(type)) {
                    varSize = varSize * 2;
                }
                else if (!"BYTE".equalsIgnoreCase(type)) {
                    varSize = varSize * 4;
                }
                objectVarSize += varSize;
            } catch (Exception e) {
                logMessage(new CompilerException(e, identifier));
            }

            if (iter.hasNext()) {
                token = iter.next();
                if (";".equals(token.getText())) {
                    break;
                }
                if ("=".equals(token.getText())) {
                    Spin1CTreeBuilder builder = new Spin1CTreeBuilder(scope);
                    builder.addToken(identifier);
                    builder.addToken(token);
                    while (iter.hasNext()) {
                        token = iter.next();
                        if (";".equals(token.getText()) || ",".equals(token.getText())) {
                            break;
                        }
                        builder.addToken(token);
                    }
                    Spin1MethodLine line = new Spin1MethodLine(scope);
                    line.addSource(compiler.compileBytecodeExpression(scope, null, builder.getRoot(), false));
                    setupLines.add(line);

                    if (!",".equals(token.getText())) {
                        if (!iter.hasNext()) {
                            break;
                        }
                        token = iter.next();
                    }
                }
                if (!",".equals(token.getText())) {
                    throw new CompilerException("expecting comma or statement end", token);
                }
            }

            if (type.endsWith("*")) {
                type = type.substring(0, type.indexOf('*')).trim();
            }
        }
    }

    Expression buildIndexExpression(TokenIterator iter) {
        Spin1ExpressionBuilder builder = new Spin1ExpressionBuilder(scope);

        Token token = null;
        while (iter.hasNext()) {
            token = iter.next();
            if ("]".equals(token.getText())) {
                try {
                    return builder.getExpression();
                } catch (CompilerException e) {
                    logMessage(e);
                } catch (Exception e) {
                    logMessage(new CompilerException(e, builder.getTokens()));
                }
                break;
            }
            builder.addToken(token);
        }
        if (!"]".equals(token.getText())) {
            throw new CompilerException("expecting '['", token);
        }

        return null;
    }

    Set<String> types = new HashSet<>(Arrays.asList(new String[] {
        "void", "int", "byte", "word", "float"
    }));

    void compileFunction(FunctionNode node) {
        Iterator<Token> iter = node.getTokens().iterator();

        Token type = iter.next();
        if (!types.contains(type.getText())) {
            throw new CompilerException("unsupported type", type);
        }

        Spin1CContext localScope = new Spin1CContext(scope);

        Token token = iter.next();
        if (token.type != Token.KEYWORD) {
            throw new CompilerException("expecting identifier", token);
        }
        String functionIdentifier = token.getText();

        Spin1Method method = new Spin1Method(localScope, functionIdentifier);
        method.setComment(node.getText().replaceAll("[\n\r]+", " "));
        method.setData(node);

        if (!"void".equals(type.getText())) {
            if (!"int".equals(type.getText())) {
                throw new CompilerException("unsupported return type", type);
            }
            method.addReturnVariable("__default_return__");
        }

        token = iter.next();
        if (!"(".equals(token.getText())) {
            throw new CompilerException("expecting open bracket", token);
        }

        while (iter.hasNext()) {
            token = iter.next();
            if (")".equals(token.getText())) {
                break;
            }

            String paramType = token.getText();
            if (!paramType.matches("(int|long|short|word|byte)[\\s]*[*]?")) {
                throw new CompilerException("unsupported parameter type", token);
            }

            token = iter.next();
            if (token.type != Token.KEYWORD) {
                throw new CompilerException("expecting identifier", token);
            }
            Token identifier = token;

            LocalVariable var = method.addParameter(paramType.toUpperCase(), identifier.getText(), new NumberLiteral(1));
            var.setData(identifier);

            if (!iter.hasNext()) {
                throw new CompilerException("expecting comma or closing bracket", new Token(token.getStream(), token.stop));
            }
            token = iter.next();
            if (")".equals(token.getText())) {
                break;
            }
            if (!",".equals(token.getText())) {
                throw new CompilerException("expecting comma or closing bracket", token);
            }
        }

        Method exp = new Method(method.getLabel(), method.getParametersCount(), method.getReturnsCount()) {

            @Override
            public int getIndex() {
                return methods.indexOf(method);
            }

        };
        exp.setData(method.getClass().getName(), method);

        publicSymbols.put(method.getLabel(), exp);
        scope.addSymbol(method.getLabel(), exp);

        methods.add(method);

        if (iter.hasNext()) {
            token = iter.next();
            if (!"{".equals(token.getText())) {
                throw new CompilerException("unexpected", token);
            }
        }
    }

    List<Spin1MethodLine> compileStatement(Spin1Method method, Spin1CContext context, Spin1MethodLine parent, Node statementNode) {
        List<Spin1MethodLine> lines = new ArrayList<Spin1MethodLine>();

        Spin1MethodLine previousLine = null;

        Iterator<Node> nodeIterator = new ArrayList<>(statementNode.getChilds()).iterator();
        while (nodeIterator.hasNext()) {
            Node node = nodeIterator.next();
            try {
                if (node instanceof DirectiveNode) {
                    Token token;

                    Iterator<Token> iter = node.getTokens().iterator();
                    token = iter.next();
                    if (!iter.hasNext()) {
                        throw new CompilerException("expecting directive", new Token(token.getStream(), token.stop));
                    }
                    token = iter.next();
                    compileConditionalDirective(token, iter);
                }
                else if (node instanceof StatementNode) {
                    if (conditionStack.isEmpty() || !conditionStack.peek().skip) {
                        Spin1MethodLine line = compileStatement(method, context, parent, node, previousLine);
                        if (line != null) {
                            lines.add(line);
                            if (!"}".equals(line.getStatement())) {
                                previousLine = line;
                            }
                        }
                    }
                }
            } catch (CompilerException e) {
                logMessage(e);
            } catch (Exception e) {
                logMessage(new CompilerException(e, node));
            }
        }

        return lines;
    }

    Spin1MethodLine compileStatement(Spin1Method method, Spin1CContext context, Spin1MethodLine parent, Node node, Spin1MethodLine previousLine) {
        Spin1MethodLine line = null;

        TokenIterator iter = node.iterator();
        if (!iter.hasNext()) {
            return null;
        }

        Token token = iter.next();
        if (previousLine == null || !"do".equals(previousLine.getStatement())) {
            if (";".equals(token.getText()) || "{".equals(token.getText()) || "}".equals(token.getText())) {
                if (iter.hasNext()) {
                    throw new CompilerException("unexpected", iter.next());
                }
                return new Spin1MethodLine(context, parent, token.getText(), node);
            }
        }
        if ("}".equals(token.getText())) {
            if (!iter.hasNext()) {
                return null;
            }
            token = iter.next();
        }

        if (token.getText().matches("(int|long|short|word|byte)[\\s]*[*]?")) {
            Token type = token;
            String typeText = token.getText();

            if ("[".equals(iter.peekNext().getText())) {
                Spin1CTreeBuilder builder = new Spin1CTreeBuilder(scope);
                builder.addToken(type);
                while (iter.hasNext()) {
                    token = iter.next();
                    if (";".equals(token.getText())) {
                        break;
                    }
                    builder.addToken(token);
                }
                line = new Spin1MethodLine(context, parent, null, node);
                line.addSource(bytecodeCompiler.compileBytecodeExpression(context, method, builder.getRoot(), false));
            }
            else {
                while (iter.hasNext()) {
                    Token identifier = iter.next();
                    if ("*".equals(identifier.getText())) {
                        typeText += " *";
                        if (!iter.hasNext()) {
                            throw new CompilerException("expecting identifier", identifier);
                        }
                        identifier = iter.next();
                    }
                    if (identifier.type != Token.KEYWORD) {
                        throw new CompilerException("expecting identifier", identifier);
                    }
                    Expression size = new NumberLiteral(1);

                    if (iter.hasNext()) {
                        token = iter.next();
                        if ("[".equals(token.getText())) {
                            if (!iter.hasNext()) {
                                throw new CompilerException("expecting expression", new Token(token.getStream(), token.stop));
                            }
                            Spin1ExpressionBuilder builder = new Spin1ExpressionBuilder(context);
                            while (iter.hasNext()) {
                                token = iter.next();
                                if ("]".equals(token.getText())) {
                                    try {
                                        size = builder.getExpression();
                                    } catch (CompilerException e) {
                                        logMessage(e);
                                    } catch (Exception e) {
                                        logMessage(new CompilerException(e, builder.getTokens()));
                                    }
                                    break;
                                }
                                builder.addToken(token);
                            }
                            if (!"]".equals(token.getText())) {
                                throw new CompilerException("expecting '['", token);
                            }
                            size = builder.getExpression();
                            if (!iter.hasNext()) {
                                throw new CompilerException("expecting comma or statement end", token);
                            }
                            token = iter.next();
                        }
                    }

                    try {
                        String identifierText = identifier.getText();
                        LocalVariable variable = method.addLocalVariable(typeText, identifierText, size);
                        variable.setData(identifier);

                        boolean add = true;
                        FunctionNode functionNode = (FunctionNode) method.getData();
                        for (FunctionNode.LocalVariableNode param : functionNode.getLocalVariables()) {
                            if (param.getIdentifier().equals(identifier)) {
                                add = false;
                                break;
                            }
                        }
                        if (add) {
                            FunctionNode.LocalVariableNode local = new FunctionNode.LocalVariableNode(functionNode);
                            local.type = type;
                            local.identifier = identifier;
                        }

                    } catch (Exception e) {
                        logMessage(new CompilerException(e, identifier));
                    }

                    if (!iter.hasNext()) {
                        break;
                    }
                    if ("=".equals(token.getText())) {
                        Spin1CTreeBuilder builder = new Spin1CTreeBuilder(context);
                        builder.addToken(identifier);
                        builder.addToken(token);
                        while (iter.hasNext()) {
                            token = iter.next();
                            if (";".equals(token.getText()) || ",".equals(token.getText())) {
                                break;
                            }
                            builder.addToken(token);
                        }

                        if (line == null) {
                            line = new Spin1MethodLine(context, parent, null, node);
                        }
                        line.addSource(bytecodeCompiler.compileBytecodeExpression(context, method, builder.getRoot(), false));

                        if (!",".equals(token.getText())) {
                            if (!iter.hasNext()) {
                                break;
                            }
                            token = iter.next();
                        }
                    }
                    if (!",".equals(token.getText())) {
                        throw new CompilerException("expecting comma or statement end", token);
                    }
                }
            }
        }
        else if ("for".equals(token.getText())) {
            if (!iter.hasNext()) {
                throw new RuntimeException("syntax error");
            }
            token = iter.next();
            if (!"(".equals(token.getText())) {
                throw new RuntimeException("syntax error");
            }

            Spin1StatementNode initializer = null;
            Spin1StatementNode condition = null;
            Spin1StatementNode increment = null;

            Spin1CTreeBuilder builder = new Spin1CTreeBuilder(context);
            if (iter.hasNext()) {
                token = iter.peekNext();
                if (types.contains(token.getText())) {
                    String type = "LONG";
                    if ("short".equals(token.getText()) || "word".equals(token.getText())) {
                        type = "WORD";
                    }
                    else if ("byte".equals(token.getText())) {
                        type = "BYTE";
                    }
                    else if (!"int".equals(token.getText()) && !"long".equals(token.getText())) {
                        throw new CompilerException("unsupported type", token);
                    }
                    iter.next();

                    if (!iter.hasNext()) {
                        throw new CompilerException("expecting identifier", new Token(token.getStream(), token.stop));
                    }
                    Token identifier = iter.next();
                    if (identifier.type != Token.KEYWORD) {
                        throw new CompilerException("invalid identifier", identifier);
                    }
                    if (!iter.hasNext()) {
                        throw new CompilerException("expecting initializer", new Token(token.getStream(), token.stop));
                    }
                    token = iter.peekNext();
                    if (!"=".equals(token.getText())) {
                        throw new CompilerException("expecting initializer", new Token(token.getStream(), token.stop));
                    }
                    if (!iter.hasNext()) {
                        throw new CompilerException("expecting expression", new Token(token.getStream(), token.stop));
                    }
                    try {
                        String identifierText = identifier.getText();
                        LocalVariable variable = method.addLocalVariable(type, identifierText, new NumberLiteral(1));
                        variable.setData(identifier);
                    } catch (Exception e) {
                        logMessage(new CompilerException(e, identifier));
                    }

                    builder.addToken(identifier);
                }

                while (iter.hasNext()) {
                    token = iter.next();
                    if (";".equals(token.getText()) || ")".equals(token.getText())) {
                        if (!builder.isEmpty()) {
                            if (initializer == null) {
                                initializer = new Spin1StatementNode(null);
                            }
                            initializer.addChild(builder.getRoot());
                        }
                        break;
                    }
                    else if (",".equals(token.getText())) {
                        if (!builder.isEmpty()) {
                            if (initializer == null) {
                                initializer = new Spin1StatementNode(null);
                            }
                            initializer.addChild(builder.getRoot());
                        }
                        builder = new Spin1CTreeBuilder(context);
                    }
                    else {
                        builder.addToken(token);
                    }
                }

                if (!")".equals(token.getText())) {
                    builder = new Spin1CTreeBuilder(context);
                    while (iter.hasNext()) {
                        token = iter.next();
                        if (";".equals(token.getText()) || ")".equals(token.getText())) {
                            if (!builder.isEmpty()) {
                                condition = builder.getRoot();
                            }
                            break;
                        }
                        else {
                            builder.addToken(token);
                        }
                    }

                    if (!")".equals(token.getText())) {
                        builder = new Spin1CTreeBuilder(context);
                        while (iter.hasNext()) {
                            token = iter.next();
                            if (";".equals(token.getText()) || ")".equals(token.getText())) {
                                if (!builder.isEmpty()) {
                                    if (increment == null) {
                                        increment = new Spin1StatementNode(null);
                                    }
                                    increment.addChild(builder.getRoot());
                                }
                                break;
                            }
                            else if (",".equals(token.getText())) {
                                if (!builder.isEmpty()) {
                                    if (increment == null) {
                                        increment = new Spin1StatementNode(null);
                                    }
                                    increment.addChild(builder.getRoot());
                                }
                                builder = new Spin1CTreeBuilder(context);
                            }
                            else {
                                builder.addToken(token);
                            }
                        }
                    }
                }
            }
            if (!")".equals(token.getText())) {
                throw new RuntimeException("syntax error");
            }

            line = new Spin1MethodLine(context, parent, null, node);
            if (initializer != null) {
                for (int i = 0; i < initializer.getChildCount(); i++) {
                    line.addSource(bytecodeCompiler.compileBytecodeExpression(context, method, initializer.getChild(i), false));
                }
            }

            Spin1MethodLine loopLine = new Spin1MethodLine(context);
            line.setData("continue", loopLine);
            line.addChild(loopLine);

            Spin1MethodLine quitLine = new Spin1MethodLine(context);
            line.setData("break", quitLine);

            if (condition != null) {
                loopLine.addSource(bytecodeCompiler.compileBytecodeExpression(context, method, condition, true));
                loopLine.addSource(new Jz(line.getScope(), new ContextLiteral(quitLine.getScope())));
            }

            line.addChilds(compileStatement(method, new Spin1CContext(context), line, node));

            Spin1MethodLine repeatLine = new Spin1MethodLine(context);
            if (increment != null) {
                for (int i = 0; i < increment.getChildCount(); i++) {
                    repeatLine.addSource(bytecodeCompiler.compileBytecodeExpression(context, method, increment.getChild(i), false));
                }
            }
            repeatLine.addSource(new Jmp(line.getScope(), new ContextLiteral(loopLine.getScope())));
            line.addChild(repeatLine);

            line.addChild(quitLine);
        }
        else if ("do".equals(token.getText())) {
            line = new Spin1MethodLine(context, parent, token.getText(), node);
            line.setData("continue", line);
            line.addChilds(compileStatement(method, new Spin1CContext(context), line, node));
        }
        else if ("while".equals(token.getText())) {
            Spin1CTreeBuilder builder = new Spin1CTreeBuilder(context);
            builder.addToken(token);
            while (iter.hasNext()) {
                token = iter.next();
                if ("{".equals(token.getText()) || ";".equals(token.getText())) {
                    break;
                }
                builder.addToken(token);
            }
            line = new Spin1MethodLine(context, parent, token.getText(), node);
            line.addSource(bytecodeCompiler.compileBytecodeExpression(context, method, builder.getRoot().getChild(0), true));

            if (previousLine != null && "do".equals(previousLine.getStatement())) {
                line.addSource(new Jnz(previousLine.getScope(), new ContextLiteral(previousLine.getScope())));

                Spin1MethodLine quitLine = new Spin1MethodLine(context);
                line.addChild(quitLine);
                line.setData("break", quitLine);
            }
            else {
                line.setData("continue", line);

                Spin1MethodLine quitLine = new Spin1MethodLine(context);
                line.setData("break", quitLine);

                line.addChilds(compileStatement(method, new Spin1CContext(context), line, node));

                line.addSource(new Jz(line.getScope(), new ContextLiteral(quitLine.getScope())));

                Spin1MethodLine loopLine = new Spin1MethodLine(context);
                loopLine.addSource(new Jmp(line.getScope(), new ContextLiteral(line.getScope())));
                line.addChild(loopLine);

                line.addChild(quitLine);
            }
        }
        else if ("until".equalsIgnoreCase(token.getText())) {
            if (previousLine == null || !"do".equals(previousLine.getStatement())) {
                throw new CompilerException("misplaced until", node);
            }

            Spin1CTreeBuilder builder = new Spin1CTreeBuilder(context);
            builder.addToken(token);
            while (iter.hasNext()) {
                token = iter.next();
                if ("{".equals(token.getText()) || ";".equals(token.getText())) {
                    break;
                }
                builder.addToken(token);
            }
            line = new Spin1MethodLine(context, parent, null, node);
            line.addSource(bytecodeCompiler.compileBytecodeExpression(context, method, builder.getRoot().getChild(0), true));
            line.addSource(new Jz(line.getScope(), new ContextLiteral(previousLine.getScope())));

            Spin1MethodLine quitLine = new Spin1MethodLine(context);
            line.addChild(quitLine);
            line.setData("break", quitLine);
        }
        else if ("switch".equalsIgnoreCase(token.getText())) {
            line = new Spin1MethodLine(context, token.getText(), node);
            Spin1MethodLine doneLine = new Spin1MethodLine(context);

            line.addSource(new Address(line.getScope(), new ContextLiteral(doneLine.getScope())));

            Spin1CTreeBuilder builder = new Spin1CTreeBuilder(context);
            builder.addToken(token);
            while (iter.hasNext()) {
                token = iter.next();
                if ("{".equals(token.getText()) || ";".equals(token.getText())) {
                    break;
                }
                builder.addToken(token);
            }
            line.addSource(bytecodeCompiler.compileBytecodeExpression(context, method, builder.getRoot().getChild(0), true));

            boolean hasDefault = false;
            for (Node child : node.getChilds()) {
                if (child instanceof StatementNode) {
                    Spin1MethodLine targetLine = new Spin1MethodLine(context);

                    Iterator<Token> childIter = child.getTokens().iterator();
                    if ("default".equals(childIter.next().getText())) {
                        if (!childIter.hasNext()) {
                            throw new RuntimeException("expected end of statement");
                        }
                        token = childIter.next();
                        if (!":".equals(token.getText())) {
                            throw new RuntimeException("expected end of statement");
                        }
                        if (childIter.hasNext()) {
                            throw new RuntimeException("unexpected");
                        }
                        line.addChild(0, targetLine);
                        hasDefault = true;
                    }
                    else {
                        line.addChild(targetLine);
                        if (!childIter.hasNext()) {
                            throw new RuntimeException("expected expression");
                        }
                        builder = new Spin1CTreeBuilder(context);
                        while (childIter.hasNext()) {
                            token = childIter.next();
                            if (":".equals(token.getText())) {
                                break;
                            }
                            builder.addToken(token);
                        }
                        compileCase(method, line, builder.getRoot(), targetLine, bytecodeCompiler);
                    }

                    targetLine.addChilds(compileStatement(method, new Spin1CContext(context), line, child));
                }
            }

            if (!hasDefault) {
                line.addSource(new Bytecode(line.getScope(), 0x0C, "CASE_DONE"));
            }

            line.addChild(doneLine);
        }
        else if ("break".equalsIgnoreCase(token.getText())) {
            while (parent != null) {
                if ("switch".equals(parent.getStatement())) {
                    line = new Spin1MethodLine(context);
                    line.addSource(new Bytecode(line.getScope(), 0x0C, "CASE_DONE"));
                    break;
                }
                Spin1MethodLine targetLine = (Spin1MethodLine) parent.getData(token.getText());
                if (targetLine != null) {
                    line = new Spin1MethodLine(context, token.getText(), node);
                    line.addSource(new Jmp(line.getScope(), new ContextLiteral(targetLine.getScope())));
                    break;
                }
                parent = parent.getParent();
            }
            if (parent == null) {
                throw new CompilerException("misplaced break", token);
            }
        }
        else if ("continue".equalsIgnoreCase(token.getText())) {
            while (parent != null) {
                Spin1MethodLine targetLine = (Spin1MethodLine) parent.getData(token.getText());
                if (targetLine != null) {
                    line = new Spin1MethodLine(context, token.getText(), node);
                    line.addSource(new Jmp(line.getScope(), new ContextLiteral(targetLine.getScope())));
                    break;
                }
                parent = parent.getParent();
            }
            if (parent == null) {
                throw new CompilerException("misplaced continue", token);
            }
        }
        else if ("if".equalsIgnoreCase(token.getText())) {
            line = new Spin1MethodLine(context, parent, token.getText(), node);
            Spin1MethodLine falseLine = new Spin1MethodLine(context);

            Spin1CTreeBuilder builder = new Spin1CTreeBuilder(context);
            builder.addToken(token);
            while (iter.hasNext()) {
                token = iter.next();
                if ("{".equals(token.getText()) || ";".equals(token.getText())) {
                    break;
                }
                builder.addToken(token);
            }
            line.addSource(bytecodeCompiler.compileBytecodeExpression(context, method, builder.getRoot().getChild(0), true));
            line.addSource(new Jz(line.getScope(), new ContextLiteral(falseLine.getScope())));

            line.addChilds(compileStatement(method, new Spin1CContext(context), line, node));
            line.addChild(falseLine);
            line.addChild(new Spin1MethodLine(context));
        }
        else if ("else".equalsIgnoreCase(token.getText())) {
            if (previousLine == null || !"if".equals(previousLine.getStatement())) {
                throw new CompilerException("misplaced else", node);
            }

            line = new Spin1MethodLine(context, parent, token.getText(), node);
            Spin1MethodLine falseLine = new Spin1MethodLine(context);
            Spin1MethodLine exitLine = previousLine.getChilds().remove(previousLine.getChilds().size() - 1);

            if (iter.hasNext()) {
                token = iter.next();
                if ("if".equals(token.getText())) {
                    line = new Spin1MethodLine(context, parent, token.getText(), node);

                    Spin1CTreeBuilder builder = new Spin1CTreeBuilder(context);
                    builder.addToken(token);
                    while (iter.hasNext()) {
                        token = iter.next();
                        if ("{".equals(token.getText()) || ";".equals(token.getText())) {
                            break;
                        }
                        builder.addToken(token);
                    }
                    line.addSource(bytecodeCompiler.compileBytecodeExpression(context, method, builder.getRoot().getChild(0), true));
                    line.addSource(new Jz(line.getScope(), new ContextLiteral(falseLine.getScope())));
                }
            }

            line.addChilds(compileStatement(method, new Spin1CContext(context), line, node));
            line.addChild(falseLine);
            line.addChild(exitLine);

            Spin1MethodLine jmpLine = new Spin1MethodLine(context);
            jmpLine.addSource(new Jmp(line.getScope(), new ContextLiteral(exitLine.getScope())));
            previousLine.addChild(previousLine.getChilds().size() - 1, jmpLine);
        }
        else if ("return".equalsIgnoreCase(token.getText())) {
            line = new Spin1MethodLine(context, parent, token.getText(), node);

            //builder.addToken(token);
            if (iter.hasNext()) {
                Spin1CTreeBuilder builder = new Spin1CTreeBuilder(context);
                while (iter.hasNext()) {
                    token = iter.next();
                    if (";".equals(token.getText())) {
                        break;
                    }
                    builder.addToken(token);
                }
                line.addSource(bytecodeCompiler.compileBytecodeExpression(context, method, builder.getRoot(), true));
                line.addSource(new Bytecode(line.getScope(), 0b00110011, line.getStatement()));
            }
            else {
                line.addSource(new Bytecode(line.getScope(), 0b00110010, line.getStatement()));
            }
        }
        else {
            Spin1CTreeBuilder builder = new Spin1CTreeBuilder(scope);
            builder.addToken(token);
            while (iter.hasNext()) {
                token = iter.next();
                if (";".equals(token.getText())) {
                    break;
                }
                builder.addToken(token);
            }
            line = new Spin1MethodLine(context, parent, null, node);
            line.addSource(bytecodeCompiler.compileBytecodeExpression(context, method, builder.getRoot(), false));
        }

        return line;
    }

    void compileCase(Spin1Method method, Spin1MethodLine line, Spin1StatementNode arg, Spin1MethodLine target, Spin1CBytecodeCompiler compiler) {
        if (",".equals(arg.getText())) {
            for (Spin1StatementNode child : arg.getChilds()) {
                compileCase(method, line, child, target, compiler);
            }
        }
        else if ("..".equals(arg.getText())) {
            line.addSource(compiler.compileBytecodeExpression(line.getScope(), method, arg.getChild(0), false));
            line.addSource(compiler.compileBytecodeExpression(line.getScope(), method, arg.getChild(1), false));
            if (target != null) {
                line.addSource(new CaseRangeJmp(line.getScope(), new ContextLiteral(target.getScope())));
            }
        }
        else {
            line.addSource(compiler.compileBytecodeExpression(line.getScope(), method, arg, false));
            if (target != null) {
                line.addSource(new CaseJmp(line.getScope(), new ContextLiteral(target.getScope())));
            }
        }
    }

    @Override
    public void compilePass2() {

        for (Variable var : variables) {
            if (!var.isReferenced() && var.getData() != null) {
                logMessage(new CompilerException(CompilerException.WARNING, "variable \"" + var.getName() + "\" is not used", var.getData()));
            }
        }

        if (methods.size() != 0) {
            if (compiler.isRemoveUnusedMethods()) {
                boolean loop;
                do {
                    loop = false;
                    Iterator<Spin1Method> methodsIterator = methods.iterator();
                    methodsIterator.next();
                    while (methodsIterator.hasNext()) {
                        Spin1Method method = methodsIterator.next();
                        if (!method.isReferenced()) {
                            FunctionNode node = (FunctionNode) method.getData();
                            logMessage(new CompilerException(CompilerException.WARNING, "method \"" + method.getLabel() + "\" is not used", node.getIdentifier()));
                            for (Variable var : method.getParameters()) {
                                if (!var.isReferenced() && var.getData() != null) {
                                    logMessage(new CompilerException(CompilerException.WARNING, "parameter \"" + var.getName() + "\" is not used", var.getData()));
                                }
                            }
                            for (Variable var : method.getLocalVariables()) {
                                if (!var.isReferenced() && var.getData() != null) {
                                    logMessage(new CompilerException(CompilerException.WARNING, "local variable \"" + var.getName() + "\" is not used", var.getData()));
                                }
                            }
                            method.remove();
                            methodsIterator.remove();
                            loop = true;
                        }
                    }
                } while (loop);
            }

            Iterator<Spin1Method> methodsIterator = methods.iterator();

            Spin1Method method = methodsIterator.next();
            for (Variable var : method.getParameters()) {
                if (!var.isReferenced() && var.getData() != null) {
                    logMessage(new CompilerException(CompilerException.WARNING, "parameter \"" + var.getName() + "\" is not used", var.getData()));
                }
            }
            for (Variable var : method.getLocalVariables()) {
                if (!var.isReferenced() && var.getData() != null) {
                    logMessage(new CompilerException(CompilerException.WARNING, "local variable \"" + var.getName() + "\" is not used", var.getData()));
                }
            }

            methodData.add(new LongDataObject(0, "Function " + method.getLabel()));
            while (methodsIterator.hasNext()) {
                method = methodsIterator.next();
                if (!method.isReferenced()) {
                    FunctionNode node = (FunctionNode) method.getData();
                    logMessage(new CompilerException(CompilerException.WARNING, "method \"" + method.getLabel() + "\" is not used", node.getIdentifier()));
                }
                for (Variable var : method.getParameters()) {
                    if (!var.isReferenced() && var.getData() != null) {
                        logMessage(new CompilerException(CompilerException.WARNING, "parameter \"" + var.getName() + "\" is not used", var.getData()));
                    }
                }
                for (Variable var : method.getLocalVariables()) {
                    if (!var.isReferenced() && var.getData() != null) {
                        logMessage(new CompilerException(CompilerException.WARNING, "local variable \"" + var.getName() + "\" is not used", var.getData()));
                    }
                }
                methodData.add(new LongDataObject(0, "Function " + method.getLabel()));
            }
        }
    }

    @Override
    public Map<String, Expression> getPublicSymbols() {
        return publicSymbols;
    }

    @Override
    public int getVarSize() {
        int linkedVarOffset = objectVarSize;
        for (LinkDataObject linkData : objectLinks) {
            linkedVarOffset += linkData.getVarSize();
        }
        return linkedVarOffset;
    }

    @Override
    public List<LinkDataObject> getObjectLinks() {
        return objectLinks;
    }

    @Override
    public Spin1Object generateObject() {
        return generateObject(0);
    }

    @Override
    public Spin1Object generateObject(int memoryOffset) {
        int address = 0, hubAddress = 0;
        Spin1Object object = new Spin1Object();

        object.setClkFreq(scope.getLocalSymbol("CLKFREQ").getNumber().intValue());
        object.setClkMode(scope.getLocalSymbol("CLKMODE").getNumber().intValue());

        object.writeComment("Object header (var size " + objectVarSize + ")");

        WordDataObject objectSize = object.writeWord(0, "Object size");
        object.writeByte(methodData.size() + 1, "Method count + 1");

        int count = 0;
        for (Entry<String, ObjectInfo> infoEntry : objects.entrySet()) {
            ObjectInfo info = infoEntry.getValue();
            try {
                count += info.count.getNumber().intValue();
            } catch (Exception e) {
                // Do nothing, error throw in compile
            }
        }
        object.writeByte(count, "Object count");

        for (LongDataObject linkData : methodData) {
            object.write(linkData);
        }
        if (methods.size() != 0) {
            object.setDcurr(methods.get(0).getStackSize());
        }

        int linkedVarOffset = objectVarSize;
        for (LinkDataObject linkData : objectLinks) {
            linkData.setVarOffset(linkedVarOffset);
            object.write(linkData);
            linkedVarOffset += linkData.getVarSize();
        }
        object.setVarSize(linkedVarOffset);

        object.addAllSymbols(publicSymbols);

        hubAddress = object.getSize();

        for (Spin1PAsmLine line : source) {
            if (line.getInstructionFactory() instanceof com.maccasoft.propeller.spin1.instructions.Long) {
                hubAddress = (hubAddress + 3) & ~3;
            }
            line.getScope().setObjectAddress(hubAddress);
            if ((line.getInstructionFactory() instanceof Org) || (line.getInstructionFactory() instanceof Res)) {
                hubAddress = (hubAddress + 3) & ~3;
            }
            try {
                address = line.resolve(address, memoryOffset + hubAddress);
                hubAddress += line.getInstructionObject().getSize();
                for (CompilerException msg : line.getAnnotations()) {
                    logMessage(msg);
                }
            } catch (CompilerException e) {
                logMessage(e);
            } catch (Exception e) {
                logMessage(new CompilerException(e, line.getData()));
            }
        }

        for (Spin1PAsmLine line : source) {
            hubAddress = line.getScope().getObjectAddress();
            if (object.getSize() < hubAddress) {
                object.writeBytes(new byte[hubAddress - object.getSize()], "(filler)");
            }
            try {
                object.writeBytes(line.getScope().getAddress(), line.getInstructionObject().getBytes(), line.toString());
            } catch (CompilerException e) {
                logMessage(e);
            } catch (Exception e) {
                logMessage(new CompilerException(e, line.getData()));
            }
        }

        Spin1MethodLine stringDataLine = null;
        if (bytecodeCompiler.getStringData().size() != 0) {
            stringDataLine = new Spin1MethodLine(scope);
            stringDataLine.setText("(string data)");
            stringDataLine.addSource(bytecodeCompiler.getStringData());
        }

        if (methods.size() != 0) {
            boolean loop;
            do {
                loop = false;
                address = object.getSize();
                for (Spin1Method method : methods) {
                    address = method.resolve(address);
                    loop |= method.isAddressChanged();
                }
                if (stringDataLine != null) {
                    address = stringDataLine.resolve(address);
                    loop |= stringDataLine.isAddressChanged();
                }
            } while (loop);

            int index = 0;
            for (Spin1Method method : methods) {
                methodData.get(index).setValue((method.getLocalsSize() << 16) | object.getSize());
                methodData.get(index).setText(String.format("Function %s @ $%04X (local size %d)", method.getLabel(), object.getSize(), method.getLocalsSize()));
                method.writeTo(object);
                index++;
            }

            if (stringDataLine != null) {
                stringDataLine.writeTo(object);
            }

            object.alignToLong();
        }

        objectSize.setValue(object.getSize());

        return object;
    }

    void determineClock() {
        Expression _clkmode = scope.getLocalSymbol("_CLKMODE");
        Expression _clkfreq = scope.getLocalSymbol("_CLKFREQ");
        Expression _xinfreq = scope.getLocalSymbol("_XINFREQ");

        if (_clkmode == null && _clkfreq == null && _xinfreq == null) {
            scope.addSymbol("CLKMODE", new NumberLiteral(0));
            scope.addSymbol("CLKFREQ", new NumberLiteral(12_000_000));
            return;
        }

        if (_clkmode == null && (_clkfreq != null || _xinfreq != null)) {
            throw new CompilerException("_CLKFREQ / _XINFREQ specified without _CLKMODE", _clkfreq != null ? _clkfreq.getData() : _xinfreq.getData());
        }
        if (_clkfreq != null && _xinfreq != null) {
            throw new CompilerException("either _CLKFREQ or _XINFREQ must be specified, but not both", _clkfreq != null ? _clkfreq.getData() : _xinfreq.getData());
        }

        int mode = _clkmode.getNumber().intValue();
        if (mode == 0 || (mode & 0xFFFFF800) != 0 || (((mode & 0x03) != 0) && ((mode & 0x7FC) != 0))) {
            throw new CompilerException("invalid _CLKMODE specified", _clkmode.getData());
        }
        if ((mode & 0x03) != 0) { // RCFAST or RCSLOW
            if (_clkfreq != null || _xinfreq != null) {
                throw new CompilerException("_CLKFREQ / _XINFREQ not allowed with RCFAST / RCSLOW", _clkfreq != null ? _clkfreq.getData() : _xinfreq.getData());
            }

            scope.addSymbol("CLKMODE", new NumberLiteral(mode == 2 ? 1 : 0));
            scope.addSymbol("CLKFREQ", new NumberLiteral(mode == 2 ? 20_000 : 12_000_000));
            return;
        }

        if (_clkfreq == null && _xinfreq == null) {
            throw new CompilerException("_CLKFREQ or _XINFREQ must be specified", _clkmode.getData());
        }

        try {
            int bitPos = getBitPos((mode >> 2) & 0x0F);
            int clkmode = (bitPos << 3) | 0x22;

            int freqshift = 0;
            if ((mode & 0x7C0) != 0) {
                freqshift = getBitPos(mode >> 6);
                clkmode += freqshift + 0x41;
            }
            if (_xinfreq != null) {
                scope.addSymbol("CLKFREQ", new NumberLiteral(_xinfreq.getNumber().intValue() << freqshift));
            }
            else {
                scope.addSymbol("CLKFREQ", _clkfreq);
            }
            scope.addSymbol("CLKMODE", new NumberLiteral(clkmode));
        } catch (Exception e) {
            throw new CompilerException(e, _clkmode.getData());
        }
    }

    int getBitPos(int value) {
        int bitCount = 0;
        int bitPos = 0;

        for (int i = 32; i > 0; i--) {
            if ((value & 0x01) != 0) {
                bitPos = 32 - i;
                bitCount++;
            }
            value >>= 1;
        }

        if (bitCount != 1) {
            throw new RuntimeException("invalid _CLKMODE specified");
        }

        return bitPos;
    }

    public Spin1Context getScope() {
        return scope;
    }

    protected void logMessage(CompilerException message) {
        if (message.hasChilds()) {
            for (CompilerException msg : message.getChilds()) {
                if (msg.type == CompilerException.ERROR) {
                    errors = true;
                }
            }
        }
        else {
            if (message.type == CompilerException.ERROR) {
                errors = true;
            }
        }
        messages.add(message);
    }

    protected Node getParsedSource(String fileName) {
        return null;
    }

    protected byte[] getBinaryFile(String fileName) {
        return null;
    }

    @Override
    public boolean hasErrors() {
        return errors;
    }

    public List<CompilerException> getMessages() {
        return messages;
    }

}
