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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.ContextLiteral;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.LocalVariable;
import com.maccasoft.propeller.expressions.Method;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.expressions.SpinObject;
import com.maccasoft.propeller.expressions.StructureVariable;
import com.maccasoft.propeller.expressions.Variable;
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.DirectiveNode;
import com.maccasoft.propeller.model.FunctionNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.NodeVisitor;
import com.maccasoft.propeller.model.StatementNode;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.TokenIterator;
import com.maccasoft.propeller.model.TypeDefinitionNode;
import com.maccasoft.propeller.model.VariableNode;
import com.maccasoft.propeller.spin2.Spin2Compiler;
import com.maccasoft.propeller.spin2.Spin2ExpressionBuilder;
import com.maccasoft.propeller.spin2.Spin2GlobalContext;
import com.maccasoft.propeller.spin2.Spin2Method;
import com.maccasoft.propeller.spin2.Spin2MethodLine;
import com.maccasoft.propeller.spin2.Spin2Object;
import com.maccasoft.propeller.spin2.Spin2Object.Spin2LinkDataObject;
import com.maccasoft.propeller.spin2.Spin2PAsmExpression;
import com.maccasoft.propeller.spin2.Spin2PAsmLine;
import com.maccasoft.propeller.spin2.Spin2StatementNode;
import com.maccasoft.propeller.spin2.bytecode.Address;
import com.maccasoft.propeller.spin2.bytecode.Bytecode;
import com.maccasoft.propeller.spin2.bytecode.CaseJmp;
import com.maccasoft.propeller.spin2.bytecode.CaseRangeJmp;
import com.maccasoft.propeller.spin2.bytecode.InlinePAsm;
import com.maccasoft.propeller.spin2.bytecode.Jmp;
import com.maccasoft.propeller.spin2.bytecode.Jnz;
import com.maccasoft.propeller.spin2.bytecode.Jz;
import com.maccasoft.propeller.spin2.instructions.Empty;
import com.maccasoft.propeller.spin2.instructions.Fit;
import com.maccasoft.propeller.spin2.instructions.Org;
import com.maccasoft.propeller.spin2.instructions.Orgh;
import com.maccasoft.propeller.spin2.instructions.Res;
import com.maccasoft.propeller.spin2.instructions.Word;

public class Spin2CObjectCompiler extends Spin2CBytecodeCompiler {

    int objectVarSize;

    List<Variable> variables = new ArrayList<>();
    List<Spin2MethodLine> setupLines = new ArrayList<>();
    List<Spin2Method> methods = new ArrayList<>();
    Map<String, ObjectInfo> objects = ListOrderedMap.listOrderedMap(new HashMap<>());

    Map<String, Expression> publicSymbols = new HashMap<String, Expression>();
    List<LinkDataObject> objectLinks = new ArrayList<>();

    Map<String, Node> structures = new HashMap<>();

    public Spin2CObjectCompiler(Spin2Compiler compiler, File file) {
        this(compiler, null, file);
    }

    public Spin2CObjectCompiler(Spin2Compiler compiler, ObjectCompiler parent, File file) {
        super(new Spin2GlobalContext(compiler.isCaseSensitive()), compiler, parent, file);

        this.scope.addDefinition("__P1__", new NumberLiteral(0));
        this.scope.addDefinition("__P2__", new NumberLiteral(1));
        this.scope.addDefinition("__SPINTOOLS__", new NumberLiteral(1));
        this.scope.addDefinition("__debug__", new NumberLiteral(compiler.isDebugEnabled() ? 1 : 0));
    }

    @Override
    public Spin2Object compileObject(Node root) {
        compileStep1(root);
        return generateObject(0);
    }

    @Override
    public void compileStep1(Node root) {
        objectVarSize = 4;

        root.accept(new NodeVisitor() {

            @Override
            public void visitDirective(DirectiveNode node) {
                try {
                    compileDirective(node);
                } catch (CompilerException e) {
                    logMessage(e);
                } catch (Exception e) {
                    logMessage(new CompilerException(e, node));
                }
            }

            @Override
            public void visitVariable(VariableNode node) {
                if (!conditionStack.isEmpty() && conditionStack.peek().skip) {
                    excludedNodes.add(node);
                }
            }

            @Override
            public boolean visitFunction(FunctionNode node) {
                if (!conditionStack.isEmpty() && conditionStack.peek().skip) {
                    excludedNodes.add(node);
                }
                return true;
            }

            @Override
            public boolean visitStatement(StatementNode node) {
                if (!conditionStack.isEmpty() && conditionStack.peek().skip) {
                    excludedNodes.add(node);
                }
                return true;
            }

        });

        while (!conditionStack.isEmpty()) {
            Condition c = conditionStack.pop();
            logMessage(new CompilerException("unbalanced conditional directive", c.node));
        }

        for (Node node : new ArrayList<>(root.getChilds())) {
            try {
                if (skipNode(node)) {
                    continue;
                }
                else if (node instanceof TypeDefinitionNode) {
                    if (conditionStack.isEmpty() || !conditionStack.peek().skip) {
                        TypeDefinitionNode typeNode = (TypeDefinitionNode) node;
                        if (typeNode.getIdentifier() != null) {
                            String symbol = typeNode.getIdentifier().getText();
                            if (structures.containsKey(symbol)) {
                                logMessage(new CompilerException("structure " + symbol + " redefinition", typeNode.getIdentifier()));
                            }

                            StructureVariable var = new StructureVariable("BYTE", "struct " + symbol, 1, 0, false);
                            for (Node child : node.getChilds()) {
                                if (child instanceof TypeDefinitionNode.Definition) {
                                    compileStructureDefinition(var, child);
                                }
                            }
                            try {
                                scope.addSymbol(var.getName(), var);
                            } catch (Exception e) {
                                // Ignore
                            }

                            structures.put(symbol, typeNode);
                        }
                    }
                    else {
                        Token token = new Token(node.getStartToken().getStream(), node.getStartIndex());
                        token.stop = node.getStopIndex();
                        root.addComment(token);
                        node.getParent().getChilds().remove(node);
                    }
                }
                else if (node instanceof VariableNode) {
                    if (conditionStack.isEmpty() || !conditionStack.peek().skip) {
                        compileVariable((VariableNode) node);
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

        if (scope.isDefined("_CLKFREQ")) {
            scope.addSymbol("_CLKFREQ", compileDefinedExpression("_CLKFREQ"));
        }
        if (scope.isDefined("_XINFREQ")) {
            scope.addSymbol("_XINFREQ", compileDefinedExpression("_XINFREQ"));
        }
        if (scope.isDefined("_XTLFREQ")) {
            scope.addSymbol("_XTLFREQ", compileDefinedExpression("_XTLFREQ"));
        }
        if (scope.isDefined("_RCSLOW")) {
            scope.addSymbol("_RCSLOW", new NumberLiteral(1));
        }
        if (scope.isDefined("_RCFAST")) {
            scope.addSymbol("_RCFAST", new NumberLiteral(1));
        }

        if (methods.size() != 0) {
            scope.addBuiltinSymbol("@CLKMODE", new NumberLiteral(0x40));
            scope.addBuiltinSymbol("@CLKFREQ", new NumberLiteral(0x44));
        }

        computeClockMode();

        for (Spin2Method method : methods) {
            Node node = (Node) method.getData();
            List<Spin2MethodLine> methodLines = compileStatement(method, method.getScope(), null, node);
            for (Spin2MethodLine line : methodLines) {
                method.addSource(line);
            }

            if (setupLines.size() != 0) {
                method.getLines().addAll(0, setupLines);
                setupLines.clear();
            }

            List<Spin2MethodLine> lines = method.getLines();
            if (lines.size() == 0 || !"return".equals(lines.get(lines.size() - 1).getStatement())) {
                Spin2MethodLine line = new Spin2MethodLine(method.getScope(), "RETURN");
                line.addSource(new Bytecode(line.getScope(), 0x04, line.getStatement()));
                method.addSource(line);
            }
        }
    }

    Expression compileDefinedExpression(String identifier) {
        try {
            List<Token> tokens = scope.getDefinition(identifier);
            if (tokens != null) {
                Spin2ExpressionBuilder builder = new Spin2ExpressionBuilder(scope);
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

    class Condition {

        final Node node;
        final boolean evaluated;
        final boolean skip;

        public Condition(Node node, boolean evaluated, boolean skip) {
            this.node = node;
            this.evaluated = evaluated;
            this.skip = skip;
        }

    }

    Stack<Condition> conditionStack = new Stack<>();

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

                ObjectInfo info = compiler.getObjectInclude(fileName, Collections.emptyMap());
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
        else if ("error".equals(token.getText())) {
            if (!skip) {
                throw new CompilerException(CompilerException.ERROR, node.getText(), node);
            }
        }
        else if ("warning".equals(token.getText())) {
            if (!skip) {
                throw new CompilerException(CompilerException.WARNING, node.getText(), node);
            }
        }
        else if ("pragma".equals(token.getText())) {
            // Ignore
        }
        else if ("ifdef".equals(token.getText())) {
            if (!skip) {
                if (!iter.hasNext()) {
                    throw new CompilerException("expecting identifier", new Token(token.getStream(), token.stop));
                }
                Token identifier = iter.next();
                if (token.type != Token.KEYWORD) {
                    throw new CompilerException("invalid identifier", new Token(token.getStream(), token.stop));
                }
                skip = !scope.isDefined(identifier.getText());
                conditionStack.push(new Condition(node, true, skip));
            }
            else {
                conditionStack.push(new Condition(node, false, skip));
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
                conditionStack.push(new Condition(node, true, skip));
            }
            else {
                conditionStack.push(new Condition(node, false, skip));
            }
        }
        else if ("else".equals(token.getText())) {
            if (conditionStack.isEmpty()) {
                throw new CompilerException("misplaced #else", token);
            }
            if (conditionStack.peek().evaluated) {
                skip = !conditionStack.pop().skip;
                conditionStack.push(new Condition(node, true, skip));
            }
            else {
                conditionStack.push(new Condition(node, false, skip));
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
                conditionStack.push(new Condition(node, true, skip));
            }
            else {
                conditionStack.push(new Condition(node, false, skip));
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

                conditionStack.push(new Condition(node, true, skip));
            }
            else {
                conditionStack.push(new Condition(node, false, skip));
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

    void compileStructureDefinition(StructureVariable target, Node node) {
        TokenIterator iter = node.iterator();

        Token token = iter.next();

        String type = token.getText();
        if (!type.matches("(int|long|short|word|byte)[\\s]*[*]?")) {
            throw new CompilerException("unsupported type " + type, token);
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

            if (target.getVariable(identifier.getText()) != null) {
                logMessage(new CompilerException("symbol '" + identifier.getText() + "' already defined", identifier));
            }
            target.addVariable(type, identifier.getText(), size.getNumber().intValue());

            if (iter.hasNext()) {
                token = iter.next();
                if (";".equals(token.getText())) {
                    break;
                }
                if (!",".equals(token.getText())) {
                    logMessage(new CompilerException("expecting comma or statement end", token));
                    break;
                }
            }

            if (type.endsWith("*")) {
                type = type.substring(0, type.indexOf('*')).trim();
            }
        }
    }

    void compileVariable(VariableNode node) {
        TokenIterator iter = node.iterator();

        Token token = iter.next();

        String type = token.getText();
        if ("struct".equals(type) && iter.hasNext()) {
            token = iter.next();
            type = token.getText();
        }

        if (structures.containsKey(type)) {
            Expression size = new NumberLiteral(1);

            if (!iter.hasNext()) {
                throw new CompilerException("expecting identifier", new Token(token.getStream(), token.stop));
            }

            Token identifier = iter.next();

            if (iter.hasNext() && "[".equals(iter.peekNext().getText())) {
                token = iter.next();
                if (!iter.hasNext()) {
                    throw new CompilerException("expecting expression", token);
                }
                size = buildIndexExpression(iter);
            }

            StructureVariable var = new StructureVariable("BYTE", identifier.getText(), size.getNumber().intValue(), objectVarSize, false);
            scope.addSymbol(identifier.getText(), var);
            variables.add(var);
            var.setData(node.getIdentifier());

            Node definitionRoot = structures.get(type);
            for (Node child : definitionRoot.getChilds()) {
                if (child instanceof TypeDefinitionNode.Definition) {
                    compileStructureDefinition(var, child);
                }
            }

            objectVarSize += size.getNumber().intValue() * var.getTypeSize();

            if (iter.hasNext()) {
                throw new CompilerException("expecting end of statement", iter.next());
            }
        }
        else if (!type.matches("(int|long|float|short|word|byte)[\\s]*[*]?")) {
            if (!iter.hasNext()) {
                throw new CompilerException("expecting identifier", new Token(token.getStream(), token.stop));
            }
            Token identifier = iter.next();
            if (identifier.type != Token.KEYWORD) {
                throw new CompilerException("expecting identifier", identifier);
            }

            try {
                ObjectInfo info = objects.get(type);
                if (info == null) {
                    File file = compiler.getFile(type, ".c", ".spin2");
                    if (file != null) {
                        info = compiler.getObjectInfo(this, file, Collections.emptyMap());
                    }
                    if (info == null) {
                        logMessage(new CompilerException("object '" + type + "' not found", token));
                        return;
                    }
                    if (info.hasErrors()) {
                        for (CompilerException msg : compiler.getMessages()) {
                            if (file.getName().equals(msg.getFileName()) && "illegal circular reference".equals(msg.getMessage())) {
                                logMessage(new CompilerException(msg.getMessage(), token));
                                return;
                            }
                        }
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

                objects.put(identifier.getText(), new ObjectInfo(info, size));

                try {
                    int count = size.getNumber().intValue();

                    LinkDataObject linkData = new Spin2LinkDataObject(info.compiler, info.compiler.getVarSize());
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
                                    return objectLinks.indexOf(linkData);
                                }

                            };
                            method.setData(Spin2Method.class.getName(), objectMethod.getData(Spin2Method.class.getName()));
                            scope.addSymbol(qualifiedName, method);
                        }
                    }
                    scope.addSymbol(identifier.getText(), new SpinObject(identifier.getText(), count) {

                        @Override
                        public int getIndex() {
                            return objectLinks.indexOf(linkData);
                        }

                    });
                    objectLinks.add(linkData);

                    for (int i = 1; i < count; i++) {
                        objectLinks.add(new Spin2LinkDataObject(info.compiler, info.compiler.getVarSize()));
                    }

                } catch (CompilerException e) {
                    logMessage(e);
                } catch (Exception e) {
                    logMessage(new CompilerException(e, info.count.getData()));
                }

            } catch (CompilerException e) {
                logMessage(e);
            } catch (Exception e) {
                logMessage(new CompilerException(e, node));
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
                Variable var = new Variable(type, identifierText, size.getNumber().intValue(), objectVarSize);
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
                    Spin2CTreeBuilder builder = new Spin2CTreeBuilder(scope);
                    builder.addToken(identifier);
                    builder.addToken(token);
                    while (iter.hasNext()) {
                        token = iter.next();
                        if (";".equals(token.getText()) || ",".equals(token.getText())) {
                            break;
                        }
                        builder.addToken(token);
                    }
                    Spin2MethodLine line = new Spin2MethodLine(scope);
                    line.addSource(compileBytecodeExpression(scope, null, builder.getRoot(), false));
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
        Spin2ExpressionBuilder builder = new Spin2ExpressionBuilder(scope);

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

        Context localScope = new Context(scope);

        Token token = iter.next();
        if (token.type != Token.KEYWORD) {
            throw new CompilerException("expecting identifier", token);
        }
        String functionIdentifier = token.getText();

        Spin2Method method = new Spin2Method(localScope, functionIdentifier);
        method.setComment(node.getText().replaceAll("[\n\r]+", " "));
        method.setData(node);

        if (!"void".equals(type.getText())) {
            if (!"int".equals(type.getText()) && !"float".equals(type.getText())) {
                throw new CompilerException("unsupported return type", type);
            }
            method.addReturnVariable("float".equals(type.getText()) ? "FLOAT" : "LONG", "__default_return__");
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
            if (!paramType.matches("(int|long|float|short|word|byte)[\\s]*[*]?")) {
                throw new CompilerException("unsupported parameter type", token);
            }

            token = iter.next();
            if (token.type != Token.KEYWORD) {
                throw new CompilerException("expecting identifier", token);
            }
            Token identifier = token;

            LocalVariable var = method.addParameter(paramType.toUpperCase(), identifier.getText(), 1);
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
                return objectLinks.size() * 2 + methods.indexOf(method);
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

    List<Spin2MethodLine> compileStatement(Spin2Method method, Context context, Spin2MethodLine parent, Node statementNode) {
        List<Spin2MethodLine> lines = new ArrayList<Spin2MethodLine>();

        Spin2MethodLine previousLine = null;

        Iterator<Node> nodeIterator = new ArrayList<>(statementNode.getChilds()).iterator();
        while (nodeIterator.hasNext()) {
            Node node = nodeIterator.next();
            try {
                if (skipNode(node)) {
                    continue;
                }
                if (node instanceof StatementNode) {
                    if (conditionStack.isEmpty() || !conditionStack.peek().skip) {
                        Spin2MethodLine line = compileStatement(method, context, parent, node, previousLine);
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

    Spin2MethodLine compileStatement(Spin2Method method, Context context, Spin2MethodLine parent, Node node, Spin2MethodLine previousLine) {
        Spin2MethodLine line = null;

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
                return new Spin2MethodLine(context, parent, token.getText(), node);
            }
        }
        if ("}".equals(token.getText())) {
            if (!iter.hasNext()) {
                return null;
            }
            token = iter.next();
        }

        if (token.getText().matches("(int|long|float|short|word|byte)[\\s]*[*]?")) {
            Token type = token;
            String typeText = token.getText();

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

                if (iter.hasNext() && "[".equals(iter.peekNext().getText())) {
                    token = iter.next();
                    if (!iter.hasNext()) {
                        throw new CompilerException("expecting expression", token);
                    }
                    size = buildIndexExpression(iter);
                }

                try {
                    String identifierText = identifier.getText();

                    Expression expression = method.getScope().getLocalSymbol(identifierText);
                    if (expression instanceof LocalVariable) {
                        logMessage(new CompilerException("symbol '" + identifier + "' already defined", identifier));
                    }
                    else {
                        if (expression != null) {
                            logMessage(new CompilerException(CompilerException.WARNING, "local variable '" + identifier.getText() + "' hides global", identifier));
                        }
                    }

                    LocalVariable variable = method.addLocalVariable(typeText, identifierText, size.getNumber().intValue());
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

                if (iter.hasNext()) {
                    token = iter.next();
                    if (";".equals(token.getText())) {
                        break;
                    }
                    if ("=".equals(token.getText())) {
                        Spin2CTreeBuilder builder = new Spin2CTreeBuilder(scope);
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
                            line = new Spin2MethodLine(context, parent, null, node);
                        }
                        line.addSource(compileBytecodeExpression(context, method, builder.getRoot(), false));

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

                if (typeText.endsWith("*")) {
                    typeText = typeText.substring(0, typeText.indexOf('*')).trim();
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

            Spin2StatementNode initializer = null;
            Spin2StatementNode condition = null;
            Spin2StatementNode increment = null;

            line = new Spin2MethodLine(context, parent, null, node);

            Spin2CTreeBuilder builder = new Spin2CTreeBuilder(context);
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
                        LocalVariable variable = method.addLocalVariable(type, identifierText, 1);
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
                                initializer = new Spin2StatementNode(null);
                            }
                            initializer.addChild(builder.getRoot());
                        }
                        break;
                    }
                    else if (",".equals(token.getText())) {
                        if (!builder.isEmpty()) {
                            if (initializer == null) {
                                initializer = new Spin2StatementNode(null);
                            }
                            initializer.addChild(builder.getRoot());
                        }
                        builder = new Spin2CTreeBuilder(context);
                    }
                    else {
                        builder.addToken(token);
                    }
                }

                if (!")".equals(token.getText())) {
                    builder = new Spin2CTreeBuilder(context);
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
                        builder = new Spin2CTreeBuilder(context);
                        while (iter.hasNext()) {
                            token = iter.next();
                            if (";".equals(token.getText()) || ")".equals(token.getText())) {
                                if (!builder.isEmpty()) {
                                    if (increment == null) {
                                        increment = new Spin2StatementNode(null);
                                    }
                                    increment.addChild(builder.getRoot());
                                }
                                break;
                            }
                            else if (",".equals(token.getText())) {
                                if (!builder.isEmpty()) {
                                    if (increment == null) {
                                        increment = new Spin2StatementNode(null);
                                    }
                                    increment.addChild(builder.getRoot());
                                }
                                builder = new Spin2CTreeBuilder(context);
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

            if (initializer != null) {
                for (int i = 0; i < initializer.getChildCount(); i++) {
                    line.addSource(compileBytecodeExpression(context, method, initializer.getChild(i), false));
                }
            }

            Spin2MethodLine loopLine = new Spin2MethodLine(context);
            line.setData("continue", loopLine);
            line.addChild(loopLine);

            Spin2MethodLine quitLine = new Spin2MethodLine(context);
            line.setData("break", quitLine);

            if (condition != null) {
                loopLine.addSource(compileBytecodeExpression(context, method, condition, true));
                loopLine.addSource(new Jz(line.getScope(), new ContextLiteral(quitLine.getScope())));
            }

            line.addChilds(compileStatement(method, new Context(context), line, node));

            Spin2MethodLine repeatLine = new Spin2MethodLine(context);
            if (increment != null) {
                for (int i = 0; i < increment.getChildCount(); i++) {
                    repeatLine.addSource(compileBytecodeExpression(context, method, increment.getChild(i), false));
                }
            }
            repeatLine.addSource(new Jmp(line.getScope(), new ContextLiteral(loopLine.getScope())));
            line.addChild(repeatLine);

            line.addChild(quitLine);
        }
        else if ("do".equals(token.getText())) {
            line = new Spin2MethodLine(context, parent, token.getText(), node);
            line.setData("continue", line);
            line.addChilds(compileStatement(method, new Context(context), line, node));
        }
        else if ("while".equals(token.getText())) {
            Spin2CTreeBuilder builder = new Spin2CTreeBuilder(context);
            builder.addToken(token);
            while (iter.hasNext()) {
                token = iter.next();
                if ("{".equals(token.getText()) || ";".equals(token.getText())) {
                    break;
                }
                builder.addToken(token);
            }
            line = new Spin2MethodLine(context, parent, token.getText(), node);
            line.addSource(compileBytecodeExpression(context, method, builder.getRoot().getChild(0), true));

            if (previousLine != null && "do".equals(previousLine.getStatement())) {
                line.addSource(new Jnz(previousLine.getScope(), new ContextLiteral(previousLine.getScope())));

                Spin2MethodLine quitLine = new Spin2MethodLine(context);
                line.addChild(quitLine);
                line.setData("break", quitLine);
            }
            else {
                line.setData("continue", line);

                Spin2MethodLine quitLine = new Spin2MethodLine(context);
                line.setData("break", quitLine);

                line.addChilds(compileStatement(method, new Context(context), line, node));

                line.addSource(new Jz(line.getScope(), new ContextLiteral(quitLine.getScope())));

                Spin2MethodLine loopLine = new Spin2MethodLine(context);
                loopLine.addSource(new Jmp(line.getScope(), new ContextLiteral(line.getScope())));
                line.addChild(loopLine);

                line.addChild(quitLine);
            }
        }
        else if ("until".equalsIgnoreCase(token.getText())) {
            if (previousLine == null || !"do".equals(previousLine.getStatement())) {
                throw new CompilerException("misplaced until", node);
            }

            Spin2CTreeBuilder builder = new Spin2CTreeBuilder(context);
            builder.addToken(token);
            while (iter.hasNext()) {
                token = iter.next();
                if ("{".equals(token.getText()) || ";".equals(token.getText())) {
                    break;
                }
                builder.addToken(token);
            }
            line = new Spin2MethodLine(context, parent, null, node);
            line.addSource(compileBytecodeExpression(context, method, builder.getRoot().getChild(0), true));
            line.addSource(new Jz(line.getScope(), new ContextLiteral(previousLine.getScope())));

            Spin2MethodLine quitLine = new Spin2MethodLine(context);
            line.addChild(quitLine);
            line.setData("break", quitLine);
        }
        else if ("switch".equalsIgnoreCase(token.getText())) {
            line = new Spin2MethodLine(context, token.getText(), node);
            Spin2MethodLine doneLine = new Spin2MethodLine(context);

            line.addSource(new Address(line.getScope(), new ContextLiteral(doneLine.getScope())));

            Spin2CTreeBuilder builder = new Spin2CTreeBuilder(context);
            builder.addToken(token);
            while (iter.hasNext()) {
                token = iter.next();
                if ("{".equals(token.getText()) || ";".equals(token.getText())) {
                    break;
                }
                builder.addToken(token);
            }
            line.addSource(compileBytecodeExpression(context, method, builder.getRoot().getChild(0), true));

            boolean hasDefault = false;
            for (Node child : node.getChilds()) {
                if (skipNode(child)) {
                    continue;
                }
                if (child instanceof StatementNode) {
                    Spin2MethodLine targetLine = new Spin2MethodLine(context);

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
                        builder = new Spin2CTreeBuilder(context);
                        while (childIter.hasNext()) {
                            token = childIter.next();
                            if (":".equals(token.getText())) {
                                break;
                            }
                            builder.addToken(token);
                        }
                        compileCase(method, line, builder.getRoot(), targetLine);
                    }

                    targetLine.addChilds(compileStatement(method, new Context(context), line, child));
                }
            }

            if (!hasDefault) {
                line.addSource(new Bytecode(line.getScope(), 0x1E, "CASE_DONE"));
            }

            line.addChild(doneLine);
        }
        else if ("break".equalsIgnoreCase(token.getText())) {
            while (parent != null) {
                if ("switch".equals(parent.getStatement())) {
                    line = new Spin2MethodLine(context);
                    line.addSource(new Bytecode(line.getScope(), 0x1E, "CASE_DONE"));
                    break;
                }
                Spin2MethodLine targetLine = (Spin2MethodLine) parent.getData(token.getText());
                if (targetLine != null) {
                    line = new Spin2MethodLine(context, token.getText(), node);
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
                Spin2MethodLine targetLine = (Spin2MethodLine) parent.getData(token.getText());
                if (targetLine != null) {
                    line = new Spin2MethodLine(context, token.getText(), node);
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
            line = new Spin2MethodLine(context, parent, token.getText(), node);
            Spin2MethodLine falseLine = new Spin2MethodLine(context);

            Spin2CTreeBuilder builder = new Spin2CTreeBuilder(context);
            builder.addToken(token);
            while (iter.hasNext()) {
                token = iter.next();
                if ("{".equals(token.getText()) || ";".equals(token.getText())) {
                    break;
                }
                builder.addToken(token);
            }
            line.addSource(compileBytecodeExpression(context, method, builder.getRoot().getChild(0), true));
            line.addSource(new Jz(line.getScope(), new ContextLiteral(falseLine.getScope())));

            line.addChilds(compileStatement(method, new Context(context), line, node));
            line.addChild(falseLine);
            line.addChild(new Spin2MethodLine(context));
        }
        else if ("else".equalsIgnoreCase(token.getText())) {
            if (previousLine == null || !"if".equals(previousLine.getStatement())) {
                throw new CompilerException("misplaced else", node);
            }

            line = new Spin2MethodLine(context, parent, token.getText(), node);
            Spin2MethodLine falseLine = new Spin2MethodLine(context);
            Spin2MethodLine exitLine = previousLine.getChilds().remove(previousLine.getChilds().size() - 1);

            if (iter.hasNext()) {
                token = iter.next();
                if ("if".equals(token.getText())) {
                    line = new Spin2MethodLine(context, parent, token.getText(), node);

                    Spin2CTreeBuilder builder = new Spin2CTreeBuilder(context);
                    builder.addToken(token);
                    while (iter.hasNext()) {
                        token = iter.next();
                        if ("{".equals(token.getText()) || ";".equals(token.getText())) {
                            break;
                        }
                        builder.addToken(token);
                    }
                    line.addSource(compileBytecodeExpression(context, method, builder.getRoot().getChild(0), true));
                    line.addSource(new Jz(line.getScope(), new ContextLiteral(falseLine.getScope())));
                }
            }

            line.addChilds(compileStatement(method, new Context(context), line, node));
            line.addChild(falseLine);
            line.addChild(exitLine);

            Spin2MethodLine jmpLine = new Spin2MethodLine(context);
            jmpLine.addSource(new Jmp(line.getScope(), new ContextLiteral(exitLine.getScope())));
            previousLine.addChild(previousLine.getChilds().size() - 1, jmpLine);
        }
        else if ("return".equalsIgnoreCase(token.getText())) {
            line = new Spin2MethodLine(context, parent, token.getText(), node);

            //builder.addToken(token);
            if (iter.hasNext()) {
                Spin2CTreeBuilder builder = new Spin2CTreeBuilder(context);
                while (iter.hasNext()) {
                    token = iter.next();
                    if (";".equals(token.getText())) {
                        break;
                    }
                    builder.addToken(token);
                }
                line.addSource(compileBytecodeExpression(context, method, builder.getRoot(), true));
                line.addSource(new Bytecode(line.getScope(), 0x05, line.getStatement()));
            }
            else {
                line.addSource(new Bytecode(line.getScope(), 0x04, line.getStatement()));
            }
        }
        else if ("asm".equalsIgnoreCase(token.getText())) {
            int org = 0;

            line = new Spin2MethodLine(context, parent, token.getText(), node);

            Context rootScope = new Context(context);
            Context lineScope = rootScope;

            int address = 0x1E0;
            for (LocalVariable var : method.getAllLocalVariables()) {
                rootScope.addSymbol(var.getName(), new NumberLiteral(address));
                address += var.getSize();
                if (address >= 0x1F0) {
                    break;
                }
            }

            int count = 0;
            for (Node child : node.getChilds()) {
                DataLineNode lineNode = (DataLineNode) child;
                if (lineNode.label != null && !lineNode.label.getText().startsWith(".")) {
                    lineScope = rootScope;
                }
                Spin2PAsmLine pasmLine = compileInlineDataLine(rootScope, lineScope, lineNode);
                if (lineNode.label != null && !lineNode.label.getText().startsWith(".")) {
                    lineScope = pasmLine.getScope();
                }

                List<Spin2PAsmExpression> arguments = pasmLine.getArguments();
                if (pasmLine.getInstructionFactory() instanceof Org) {
                    if (arguments.size() > 0) {
                        org = arguments.get(0).getInteger();
                    }
                    continue;
                }
                if (pasmLine.getInstructionFactory() instanceof Empty) {
                    continue;
                }
                count++;
                if (arguments.size() > 0) {
                    if (arguments.get(0).isLongLiteral()) {
                        count++;
                    }
                    if (arguments.size() > 1) {
                        if (arguments.get(1).isLongLiteral()) {
                            count++;
                        }
                    }
                }

                line.addSource(new InlinePAsm(rootScope, pasmLine));
            }

            count--;
            line.addSource(0, new Bytecode(context, new byte[] {
                (byte) org,
                (byte) (org >> 8),
                (byte) count,
                (byte) (count >> 8),
            }, String.format("ORG=$%03x, %d", org, count + 1)));
            line.addSource(0, new Bytecode(context, new byte[] {
                0x19, 0x5E
            }, "INLINE-EXEC"));
        }
        else {
            if (!compiler.isDebugEnabled() && "debug".equals(token.getText())) {
                return null;
            }
            Spin2CTreeBuilder builder = new Spin2CTreeBuilder(scope);
            builder.addToken(token);
            while (iter.hasNext()) {
                token = iter.next();
                if (";".equals(token.getText())) {
                    break;
                }
                builder.addToken(token);
            }
            line = new Spin2MethodLine(context, parent, null, node);
            line.addSource(compileBytecodeExpression(context, method, builder.getRoot(), false));
        }

        return line;
    }

    void compileCase(Spin2Method method, Spin2MethodLine line, Spin2StatementNode arg, Spin2MethodLine target) {
        if (",".equals(arg.getText())) {
            for (Spin2StatementNode child : arg.getChilds()) {
                compileCase(method, line, child, target);
            }
        }
        else if ("..".equals(arg.getText())) {
            line.addSource(compileBytecodeExpression(line.getScope(), method, arg.getChild(0), false));
            line.addSource(compileBytecodeExpression(line.getScope(), method, arg.getChild(1), false));
            if (target != null) {
                line.addSource(new CaseRangeJmp(line.getScope(), new ContextLiteral(target.getScope())));
            }
        }
        else {
            line.addSource(compileBytecodeExpression(line.getScope(), method, arg, false));
            if (target != null) {
                line.addSource(new CaseJmp(line.getScope(), new ContextLiteral(target.getScope())));
            }
        }
    }

    @Override
    public void compileStep2() {

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
                    Iterator<Spin2Method> methodsIterator = methods.iterator();
                    methodsIterator.next();
                    while (methodsIterator.hasNext()) {
                        Spin2Method method = methodsIterator.next();
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

            Iterator<Spin2Method> methodsIterator = methods.iterator();

            Spin2Method method = methodsIterator.next();
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
    public Spin2Object generateObject(int memoryOffset) {
        Spin2Object object = new Spin2Object();

        object.setClkFreq(scope.getSymbol("CLKFREQ_").getNumber().intValue());
        object.setClkMode(scope.getSymbol("CLKMODE_").getNumber().intValue());

        if (scope.hasSymbol("DEBUG_PIN")) {
            object.setDebugRxPin(scope.getSymbol("DEBUG_PIN").getNumber().intValue() & 0x3F);
        }
        if (scope.hasSymbol("DEBUG_PIN_RX")) {
            object.setDebugRxPin(scope.getSymbol("DEBUG_PIN_RX").getNumber().intValue() & 0x3F);
        }
        if (scope.hasSymbol("DEBUG_PIN_TX")) {
            object.setDebugTxPin(scope.getSymbol("DEBUG_PIN_TX").getNumber().intValue() & 0x3F);
        }
        if (scope.hasSymbol("DEBUG_BAUD")) {
            object.setDebugBaud(scope.getSymbol("DEBUG_BAUD").getNumber().intValue());
        }

        object.writeComment("Object header (var size " + objectVarSize + ")");

        int linkedVarOffset = objectVarSize;
        for (LinkDataObject linkData : objectLinks) {
            object.write(linkData);
            object.writeLong(linkedVarOffset, String.format("Variables @ $%05X", linkedVarOffset));
            linkedVarOffset += linkData.getVarSize();
        }
        object.setVarSize(linkedVarOffset);

        List<LongDataObject> methodData = new ArrayList<>();
        if (methods.size() != 0) {
            Iterator<Spin2Method> methodsIterator = methods.iterator();
            while (methodsIterator.hasNext()) {
                Spin2Method method = methodsIterator.next();
                LongDataObject dataObject = new LongDataObject(0, "Method " + method.getLabel());
                object.write(dataObject);
                methodData.add(dataObject);
            }
            LongDataObject dataObject = new LongDataObject(0, "End");
            object.write(dataObject);
            methodData.add(dataObject);
        }

        object.addAllSymbols(publicSymbols);

        int address = 0;
        int objectAddress = object.getSize();
        int fitAddress = 0x1F8 << 2;
        boolean hubMode = true;
        boolean cogCode = false;
        boolean spinMode = methods.size() != 0;

        for (Spin2PAsmLine line : source) {
            if (!hubMode && !(line.getInstructionFactory() instanceof com.maccasoft.propeller.spin2.instructions.Byte) && !(line.getInstructionFactory() instanceof Word)) {
                objectAddress = (objectAddress + 3) & ~3;
                address = (address + 3) & ~3;
            }
            line.getScope().setObjectAddress(objectAddress);
            line.getScope().setMemoryAddress(memoryOffset + objectAddress);
            if (line.getInstructionFactory() instanceof Orgh) {
                hubMode = true;
                cogCode = false;
                address = spinMode ? 0x400 : objectAddress;
            }
            if ((line.getInstructionFactory() instanceof Org) || (line.getInstructionFactory() instanceof Res)) {
                hubMode = false;
                objectAddress = (objectAddress + 3) & ~3;
            }
            if (line.getInstructionFactory() instanceof Fit) {
                ((Fit) line.getInstructionFactory()).setDefaultLimit(hubMode ? 0x80000 : (cogCode ? 0x1F8 : 0x400));
            }

            try {
                if (!hubMode && line.getLabel() != null) {
                    if ((address & 0x03) != 0) {
                        throw new CompilerException("cog symbols must be long-aligned", line.getData());
                    }
                }
                address = line.resolve(address, hubMode);
                objectAddress += line.getInstructionObject().getSize();
                if ((line.getInstructionFactory() instanceof Org)) {
                    cogCode = address < 0x200 * 4;
                    fitAddress = cogCode ? 0x1F8 * 4 : 0x400 * 4;
                    if (line.getArguments().size() > 1) {
                        fitAddress = line.getArguments().get(1).getInteger() * 4;
                    }
                }
            } catch (CompilerException e) {
                logMessage(e);
            } catch (Exception e) {
                logMessage(new CompilerException(e, line.getData()));
            }

            if (hubMode) {
                if (!spinMode && address > objectAddress) {
                    objectAddress = address;
                }
            }
            else if (address > fitAddress) {
                if (cogCode) {
                    logMessage(new CompilerException("cog code limit exceeded by " + ((address - fitAddress + 3) >> 2) + " long(s)", line.getData()));
                }
                else {
                    logMessage(new CompilerException("lut code limit exceeded by " + ((address - fitAddress + 3) >> 2) + " long(s)", line.getData()));
                }
            }
        }

        hubMode = true;
        for (Spin2PAsmLine line : source) {
            objectAddress = line.getScope().getObjectAddress();
            if (object.getSize() < objectAddress) {
                object.writeBytes(new byte[objectAddress - object.getSize()], "(filler)");
            }
            try {
                if (line.getInstructionFactory() instanceof Orgh) {
                    hubMode = true;
                }
                if ((line.getInstructionFactory() instanceof Org) || (line.getInstructionFactory() instanceof Res)) {
                    hubMode = false;
                }
                object.writeBytes(line.getScope().getAddress(), hubMode, line.getInstructionObject().getBytes(), line.toString());
            } catch (CompilerException e) {
                logMessage(e);
            } catch (Exception e) {
                logMessage(new CompilerException(e, line.getData()));
            }
        }

        if (methods.size() != 0) {
            boolean loop;
            do {
                loop = false;
                address = object.getSize();
                for (Spin2Method method : methods) {
                    address = method.resolve(address);
                    loop |= method.isAddressChanged();
                }
            } while (loop);

            int index = 0;
            for (Spin2Method method : methods) {
                int value = 0;

                value = Spin2Method.address_bit.setValue(value, object.getSize());
                value = Spin2Method.returns_bit.setValue(value, method.getReturnsCount());
                value = Spin2Method.parameters_bit.setValue(value, method.getParametersCount());

                methodData.get(index).setValue(value | 0x80000000L);
                methodData.get(index).setText(
                    String.format("Method %s @ $%05X (%d parameters, %d returns)", method.getLabel(), object.getSize(), method.getParametersCount(), method.getReturnsCount()));
                try {
                    method.writeTo(object);
                } catch (CompilerException e) {
                    logMessage(e);
                } catch (Exception e) {
                    logMessage(new CompilerException(e, method.getData()));
                }
                index++;
            }
            if (index > 0) {
                methodData.get(index).setValue(object.getSize());
            }

            object.alignToLong();
        }

        return object;
    }

    void computeClockMode() {
        Expression clkMode = scope.getLocalSymbol("_CLKMODE");
        Expression clkFreq = scope.getLocalSymbol("_CLKFREQ");
        Expression xtlFreq = scope.getLocalSymbol("_XTLFREQ");
        Expression xinFreq = scope.getLocalSymbol("_XINFREQ");
        Expression errFreq = scope.getLocalSymbol("_ERRFREQ");

        double clkfreq;
        double xinfreq = 20000000.0; // default crystal frequency
        double errfreq = 1000000.0;
        int clkmode = 0;
        int finalfreq = 20000000;
        int zzzz = 0b10_11;
        int pppp;
        double error;

        if (scope.hasSymbol("_RCSLOW")) {
            clkmode = 0b0001;
            finalfreq = 20000;
        }
        else if (clkMode != null || clkFreq != null || xtlFreq != null || xinFreq != null) {
            if (methods.size() != 0) {
                clkfreq = 20000000.0;
            }
            else {
                clkfreq = 160000000.0;
            }

            if (xinFreq != null) {
                if (xtlFreq != null) {
                    throw new RuntimeException("only one of _XTLFREQ or _XINFREQ may be specified");
                }
                clkfreq = xinfreq = xinFreq.getNumber().doubleValue();
                zzzz = 0b01_10;
            }
            else if (xtlFreq != null) {
                clkfreq = xinfreq = xtlFreq.getNumber().doubleValue();
                if (xinfreq >= 16000000.0) {
                    zzzz = 0b10_10;
                }
                else {
                    zzzz = 0b11_10;
                }
            }

            if (clkMode != null) {
                if (clkFreq == null) {
                    throw new RuntimeException("_CLKMODE definition requires _CLKFREQ as well");
                }
                clkmode = clkMode.getNumber().intValue();
                finalfreq = clkFreq.getNumber().intValue();
                //goto set_symbols;
            }
            else if (clkFreq != null) {
                clkfreq = clkFreq.getNumber().doubleValue();
                if (errFreq != null) {
                    errfreq = errFreq.getNumber().doubleValue();
                }
                zzzz |= 0b00_01;

                // figure out clock mode based on frequency
                int divd;
                double abse, post, mult, fpfd, fvco, fout;
                double result_mult = 0;
                double result_fout = 0;
                int result_pppp = 0, result_divd = 0;
                error = 1e9;
                for (pppp = 0; pppp <= 15; pppp++) {
                    if (pppp == 0) {
                        post = 1.0;
                    }
                    else {
                        post = pppp * 2.0;
                    }
                    for (divd = 64; divd >= 1; --divd) {
                        fpfd = Math.round(xinfreq / divd);
                        mult = Math.round((post * divd) * clkfreq / xinfreq);
                        fvco = Math.round(xinfreq * mult / divd);
                        fout = Math.round(fvco / post);
                        abse = Math.abs(fout - clkfreq);
                        if ((abse <= error) && (fpfd >= 250000) && (mult <= 1024) && (fvco > 99e6) && ((fvco <= 201e6) || (fvco <= clkfreq + errfreq))) {
                            error = abse;
                            result_divd = divd;
                            result_mult = mult;
                            result_pppp = (pppp - 1) & 15;
                            result_fout = fout;
                        }
                    }
                }

                if (error > errfreq) {
                    throw new RuntimeException(String.format("unable to find clock settings for freq %d Hz with input freq %d Hz", (int) clkfreq, (int) xinfreq));
                }
                int D, M;
                D = result_divd - 1;
                M = ((int) result_mult) - 1;
                clkmode = zzzz | (result_pppp << 4) | (M << 8) | (D << 18) | (1 << 24);

                finalfreq = (int) Math.round(result_fout);
            }
            else {
                clkmode = zzzz;
                finalfreq = (int) xinfreq;
            }
        }

        scope.addBuiltinSymbol("CLKMODE_", new NumberLiteral(clkmode));
        scope.addBuiltinSymbol("CLKFREQ_", new NumberLiteral(finalfreq));
    }

    @Override
    protected void compileDatInclude(Node root) {

    }

}
