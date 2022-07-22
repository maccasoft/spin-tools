/*
 * Copyright (c) 2021-22 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.map.ListOrderedMap;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.SpinObject.LongDataObject;
import com.maccasoft.propeller.SpinObject.WordDataObject;
import com.maccasoft.propeller.expressions.Add;
import com.maccasoft.propeller.expressions.And;
import com.maccasoft.propeller.expressions.CharacterLiteral;
import com.maccasoft.propeller.expressions.ContextLiteral;
import com.maccasoft.propeller.expressions.DataVariable;
import com.maccasoft.propeller.expressions.Decod;
import com.maccasoft.propeller.expressions.Divide;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.LocalVariable;
import com.maccasoft.propeller.expressions.MemoryContextLiteral;
import com.maccasoft.propeller.expressions.Method;
import com.maccasoft.propeller.expressions.Modulo;
import com.maccasoft.propeller.expressions.Multiply;
import com.maccasoft.propeller.expressions.Negative;
import com.maccasoft.propeller.expressions.Not;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.expressions.ObjectContextLiteral;
import com.maccasoft.propeller.expressions.Or;
import com.maccasoft.propeller.expressions.Register;
import com.maccasoft.propeller.expressions.ShiftLeft;
import com.maccasoft.propeller.expressions.ShiftRight;
import com.maccasoft.propeller.expressions.Subtract;
import com.maccasoft.propeller.expressions.Trunc;
import com.maccasoft.propeller.expressions.Variable;
import com.maccasoft.propeller.expressions.Xor;
import com.maccasoft.propeller.model.ConstantNode;
import com.maccasoft.propeller.model.ConstantsNode;
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.DataNode;
import com.maccasoft.propeller.model.ErrorNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.NodeVisitor;
import com.maccasoft.propeller.model.ObjectNode;
import com.maccasoft.propeller.model.ObjectsNode;
import com.maccasoft.propeller.model.StatementNode;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.VariableNode;
import com.maccasoft.propeller.model.VariablesNode;
import com.maccasoft.propeller.spin1.Spin1Bytecode.Descriptor;
import com.maccasoft.propeller.spin1.Spin1Object.LinkDataObject;
import com.maccasoft.propeller.spin1.bytecode.Address;
import com.maccasoft.propeller.spin1.bytecode.Bytecode;
import com.maccasoft.propeller.spin1.bytecode.CaseJmp;
import com.maccasoft.propeller.spin1.bytecode.CaseRangeJmp;
import com.maccasoft.propeller.spin1.bytecode.Constant;
import com.maccasoft.propeller.spin1.bytecode.Djnz;
import com.maccasoft.propeller.spin1.bytecode.Jmp;
import com.maccasoft.propeller.spin1.bytecode.Jnz;
import com.maccasoft.propeller.spin1.bytecode.Jz;
import com.maccasoft.propeller.spin1.bytecode.MathOp;
import com.maccasoft.propeller.spin1.bytecode.MemoryOp;
import com.maccasoft.propeller.spin1.bytecode.MemoryRef;
import com.maccasoft.propeller.spin1.bytecode.RegisterBitOp;
import com.maccasoft.propeller.spin1.bytecode.RegisterOp;
import com.maccasoft.propeller.spin1.bytecode.RepeatLoop;
import com.maccasoft.propeller.spin1.bytecode.Tjz;
import com.maccasoft.propeller.spin1.bytecode.VariableOp;
import com.maccasoft.propeller.spin1.instructions.FileInc;
import com.maccasoft.propeller.spin1.instructions.Org;
import com.maccasoft.propeller.spin1.instructions.Res;
import com.maccasoft.propeller.spin2.Spin2Model;

public class Spin1ObjectCompiler {

    public static boolean OPENSPIN_COMPATIBILITY = false;

    public static class ObjectInfo {
        String fileName;
        Spin1ObjectCompiler compiler;

        long offset;
        int count;

        public ObjectInfo(String fileName, Spin1ObjectCompiler compiler) {
            this.fileName = fileName;
            this.compiler = compiler;
        }

        public ObjectInfo(String fileName, Spin1ObjectCompiler compiler, int count) {
            this.fileName = fileName;
            this.compiler = compiler;
            this.count = count;
        }

        public boolean hasErrors() {
            return compiler.hasErrors();
        }

    }

    final Spin1Context scope;
    final Map<String, ObjectInfo> childObjects;

    List<Spin1PAsmLine> source = new ArrayList<Spin1PAsmLine>();
    List<Spin1Method> methods = new ArrayList<Spin1Method>();
    Map<String, ObjectInfo> objects = ListOrderedMap.listOrderedMap(new HashMap<String, ObjectInfo>());

    int dcurr = 0;
    int varOffset = 0;
    int objectVarSize = 0;
    Spin1MethodLine dataLine;

    boolean errors;
    List<CompilerException> messages = new ArrayList<CompilerException>();

    Map<String, Expression> publicSymbols = new HashMap<String, Expression>();
    List<LinkDataObject> objectLinks = new ArrayList<LinkDataObject>();
    List<LongDataObject> methodData = new ArrayList<LongDataObject>();

    public Spin1ObjectCompiler(Spin1Context scope, Map<String, ObjectInfo> childObjects) {
        this.scope = new Spin1Context(scope);
        this.childObjects = childObjects;
    }

    public Spin1Object compileObject(Node root) {
        compile(root);
        return generateObject();
    }

    public void compile(Node root) {

        for (Node node : root.getChilds()) {
            if (node instanceof ObjectsNode) {
                compileObjBlock(node);
            }
        }

        for (Node node : root.getChilds()) {
            if (node instanceof ConstantsNode) {
                compileConBlock(node);
            }
        }

        try {
            determineClock();
        } catch (CompilerException e) {
            logMessage(e);
        }

        Iterator<Entry<String, Expression>> iter = scope.symbols.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, Expression> entry = iter.next();
            try {
                if ("$".equals(entry.getKey()) || "@$".equals(entry.getKey())) {
                    continue;
                }
                Expression expression = entry.getValue().resolve();
                if (!expression.isConstant()) {
                    logMessage(new CompilerException("expression is not constant", expression.getData()));
                }
            } catch (Exception e) {
                if (e instanceof CompilerException) {
                    logMessage((CompilerException) e);
                }
                else {
                    logMessage(new CompilerException(e, entry.getValue().toString()));
                }
                iter.remove();
            }
        }

        compileVarBlocks(root);

        for (Node node : root.getChilds()) {
            if (node instanceof DataNode) {
                compileDatBlock(scope, node);
            }
        }

        for (Node node : root.getChilds()) {
            if ((node instanceof MethodNode) && "PUB".equalsIgnoreCase(((MethodNode) node).getType().getText())) {
                Spin1Method method = compileMethod((MethodNode) node);
                if (method == null) {
                    continue;
                }
                try {
                    int index = -1;
                    if (isReferenced((MethodNode) node)) {
                        if (methodData.size() == 0) {
                            dcurr = method.getStackSize();
                        }
                        methodData.add(new LongDataObject(0, "Function " + method.getLabel()));
                        index = methodData.size();
                    }
                    else {
                        logMessage(new CompilerException(CompilerException.WARNING, "method \"" + method.label + "\" is not used", node));
                    }
                    publicSymbols.put(method.getLabel(), new Method(method.getLabel(), method.getParametersCount(), method.getReturnsCount(), index));
                    scope.addSymbol(method.getLabel(), new Method(method.getLabel(), method.getParametersCount(), method.getReturnsCount(), index));
                    scope.addSymbol("@" + method.getLabel(), new Method(method.getLabel(), method.getParametersCount(), method.getReturnsCount(), index));
                    method.register();
                    methods.add(method);
                } catch (Exception e) {
                    logMessage(new CompilerException(e.getMessage(), node));
                }
            }
        }
        for (Node node : root.getChilds()) {
            if ((node instanceof MethodNode) && "PRI".equalsIgnoreCase(((MethodNode) node).getType().getText())) {
                Spin1Method method = compileMethod((MethodNode) node);
                if (method == null) {
                    continue;
                }
                try {
                    int index = -1;
                    if (isReferenced((MethodNode) node)) {
                        if (methodData.size() == 0) {
                            dcurr = method.getStackSize();
                        }
                        methodData.add(new LongDataObject(0, "Function " + method.getLabel()));
                        index = methodData.size();
                    }
                    else {
                        logMessage(new CompilerException(CompilerException.WARNING, "method \"" + method.label + "\" is not used", node));
                    }
                    scope.addSymbol(method.getLabel(), new Method(method.getLabel(), method.getParametersCount(), method.getReturnsCount(), index));
                    scope.addSymbol("@" + method.getLabel(), new Method(method.getLabel(), method.getParametersCount(), method.getReturnsCount(), index));
                    method.register();
                    methods.add(method);
                } catch (Exception e) {
                    logMessage(new CompilerException(e.getMessage(), node));
                }
            }
        }

        int objectIndex = methodData.size() + 1;
        for (Entry<String, ObjectInfo> infoEntry : objects.entrySet()) {
            ObjectInfo info = infoEntry.getValue();
            for (Entry<String, Expression> objEntry : info.compiler.getPublicSymbols().entrySet()) {
                if (objEntry.getValue() instanceof Method) {
                    String qualifiedName = infoEntry.getKey() + "." + objEntry.getKey();
                    Method method = ((Method) objEntry.getValue()).copy();
                    method.setObject(objectIndex);
                    scope.addSymbol(qualifiedName, method);
                }
            }
            for (int i = 0; i < info.count; i++) {
                objectLinks.add(new LinkDataObject(info, 0, varOffset));
                varOffset += info.compiler.getVarSize();
                objectIndex++;
            }
        }

        for (Spin1Method method : methods) {
            List<Spin1MethodLine> lines = method.getLines();
            dataLine = lines.get(lines.size() - 1);
            for (Spin1MethodLine line : lines) {
                try {
                    compileLine(line);
                } catch (CompilerException e) {
                    logMessage(e);
                } catch (Exception e) {
                    logMessage(new CompilerException(e, line.getData()));
                }
            }
        }
    }

    public Map<String, Expression> getPublicSymbols() {
        return publicSymbols;
    }

    public int getVarSize() {
        return varOffset;
    }

    public List<LinkDataObject> getObjectLinks() {
        return objectLinks;
    }

    public Spin1Object generateObject() {
        return generateObject(0);
    }

    public Spin1Object generateObject(int memoryOffset) {
        int address = 0, hubAddress = 0;
        Spin1Object object = new Spin1Object();

        object.setClkFreq(scope.getLocalSymbol("CLKFREQ").getNumber().intValue());
        object.setClkMode(scope.getLocalSymbol("CLKMODE").getNumber().intValue());

        if (objects.size() != 0) {
            object.writeComment("Object header (var size " + objectVarSize + ")");
        }
        else {
            object.writeComment("Object header");
        }

        WordDataObject objectSize = object.writeWord(0, "Object size");
        object.writeByte(methodData.size() + 1, "Method count + 1");

        int count = 0;
        for (Entry<String, ObjectInfo> infoEntry : objects.entrySet()) {
            ObjectInfo info = infoEntry.getValue();
            count += info.count;
        }
        object.writeByte(count, "Object count");

        for (LongDataObject linkData : methodData) {
            object.write(linkData);
        }

        for (LinkDataObject linkData : objectLinks) {
            object.write(linkData);
        }
        object.links.addAll(objectLinks);

        object.setDcurr(dcurr);
        object.addAllSymbols(publicSymbols);
        object.setVarSize(varOffset);

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

        if (methods.size() != 0) {
            boolean loop;
            do {
                loop = false;
                address = object.getSize();
                for (Spin1Method method : methods) {
                    if (!isReferenced((MethodNode) method.getData())) {
                        continue;
                    }
                    address = method.resolve(address);
                    loop |= method.isAddressChanged();
                }
            } while (loop);

            int index = 0;
            for (Spin1Method method : methods) {
                if (!isReferenced((MethodNode) method.getData())) {
                    continue;
                }
                methodData.get(index).setValue((method.getLocalsSize() << 16) | object.getSize());
                methodData.get(index).setText(String.format("Function %s @ $%04X (local size %d)", method.getLabel(), object.getSize(), method.getLocalsSize()));
                method.writeTo(object);
                index++;
            }

            object.alignToLong();
        }

        objectSize.setValue(object.getSize());

        return object;
    }

    void compileConBlock(Node parent) {
        parent.accept(new NodeVisitor() {
            int enumValue = 0, enumIncrement = 1;

            @Override
            public boolean visitConstant(ConstantNode node) {
                Iterator<Token> iter = node.getTokens().iterator();

                Token token = iter.next();
                do {
                    if ("#".equals(token.getText())) {
                        Spin1ExpressionBuilder builder = new Spin1ExpressionBuilder(scope);
                        while (iter.hasNext()) {
                            token = iter.next();
                            if ("[".equals(token.getText())) {
                                break;
                            }
                            builder.addToken(token);
                        }
                        try {
                            Expression expression = builder.getExpression();
                            enumValue = expression.getNumber().intValue();
                        } catch (CompilerException e) {
                            logMessage(e);
                        } catch (Exception e) {
                            logMessage(new CompilerException("expression syntax error", builder.tokens));
                        }
                        if ("[".equals(token.getText())) {
                            builder = new Spin1ExpressionBuilder(scope);
                            while (iter.hasNext()) {
                                token = iter.next();
                                if ("]".equals(token.getText())) {
                                    try {
                                        Expression expression = builder.getExpression();
                                        enumIncrement = expression.getNumber().intValue();
                                    } catch (CompilerException e) {
                                        logMessage(e);
                                    } catch (Exception e) {
                                        logMessage(new CompilerException(e, builder.tokens));
                                    }
                                    break;
                                }
                                builder.addToken(token);
                            }
                            if (!"]".equals(token.getText())) {
                                logMessage(new CompilerException("expecting '['", token));
                            }
                        }
                    }
                    else {
                        if (token.type != 0) {
                            logMessage(new CompilerException("expecting identifier", token));
                            break;
                        }
                        String identifier = token.getText();
                        if (!iter.hasNext()) {
                            try {
                                scope.addSymbol(identifier, new NumberLiteral(enumValue));
                                publicSymbols.put(identifier, new NumberLiteral(enumValue));
                            } catch (CompilerException e) {
                                logMessage(e);
                            } catch (Exception e) {
                                logMessage(new CompilerException(e, token));
                            }
                            enumValue += enumIncrement;
                        }
                        else {
                            token = iter.next();
                            if ("[".equals(token.getText())) {
                                try {
                                    scope.addSymbol(identifier, new NumberLiteral(enumValue));
                                    publicSymbols.put(identifier, new NumberLiteral(enumValue));
                                } catch (CompilerException e) {
                                    logMessage(e);
                                } catch (Exception e) {
                                    logMessage(new CompilerException(e, token));
                                }

                                Spin1ExpressionBuilder builder = new Spin1ExpressionBuilder(scope);
                                while (iter.hasNext()) {
                                    token = iter.next();
                                    if ("]".equals(token.getText())) {
                                        break;
                                    }
                                    builder.addToken(token);
                                }
                                try {
                                    Expression expression = builder.getExpression();
                                    enumValue += enumIncrement * expression.getNumber().intValue();
                                } catch (CompilerException e) {
                                    logMessage(e);
                                } catch (Exception e) {
                                    logMessage(new CompilerException(e, builder.tokens));
                                }
                            }
                            else if ("=".equals(token.getText())) {
                                Spin1ExpressionBuilder builder = new Spin1ExpressionBuilder(scope);
                                while (iter.hasNext()) {
                                    token = iter.next();
                                    if ("]".equals(token.getText())) {
                                        break;
                                    }
                                    builder.addToken(token);
                                }
                                try {
                                    Expression expression = builder.getExpression();
                                    try {
                                        scope.addSymbol(identifier, expression);
                                        publicSymbols.put(identifier, expression);
                                    } catch (CompilerException e) {
                                        logMessage(e);
                                    } catch (Exception e) {
                                        logMessage(new CompilerException(e, node));
                                    }
                                } catch (CompilerException e) {
                                    logMessage(e);
                                } catch (Exception e) {
                                    if (builder.tokens.size() == 0) {
                                        logMessage(new CompilerException("expecting expression", token));
                                    }
                                    else {
                                        logMessage(new CompilerException("expression syntax error", builder.tokens));
                                    }
                                }
                            }
                            else {
                                logMessage(new CompilerException("unexpected '" + token.getText() + "'", token));
                                break;
                            }
                        }
                    }
                } while (iter.hasNext());

                return false;
            }

        });
    }

    void compileVarBlocks(Node root) {
        root.accept(new NodeVisitor() {

            String type = "LONG";

            @Override
            public boolean visitVariables(VariablesNode node) {
                type = "LONG";
                return true;
            }

            @Override
            public void visitVariable(VariableNode node) {
                if (node.type != null) {
                    type = node.type.getText().toUpperCase();
                }
                if ("LONG".equalsIgnoreCase(type)) {
                    compileVariable(type, node);
                }
            }

        });

        root.accept(new NodeVisitor() {

            String type = "LONG";

            @Override
            public boolean visitVariables(VariablesNode node) {
                type = "LONG";
                return true;
            }

            @Override
            public void visitVariable(VariableNode node) {
                if (node.type != null) {
                    type = node.type.getText().toUpperCase();
                }
                if ("WORD".equalsIgnoreCase(type)) {
                    compileVariable(type, node);
                }
            }

        });

        root.accept(new NodeVisitor() {

            String type = "LONG";

            @Override
            public boolean visitVariables(VariablesNode node) {
                type = "LONG";
                return true;
            }

            @Override
            public void visitVariable(VariableNode node) {
                if (node.type != null) {
                    type = node.type.getText().toUpperCase();
                }
                if ("BYTE".equalsIgnoreCase(type)) {
                    compileVariable(type, node);
                }
            }

        });

        objectVarSize = (objectVarSize + 3) & ~3;
        varOffset = objectVarSize;
    }

    void compileVariable(String type, VariableNode node) {
        Iterator<Token> iter = node.getTokens().iterator();

        Token token = iter.next();
        if (Spin1Model.isType(token.getText())) {
            type = token.getText().toUpperCase();
            if (!iter.hasNext()) {
                logMessage(new CompilerException("expecting identifier", token));
                return;
            }
            token = iter.next();
        }

        String identifier = token.getText();
        Expression size = new NumberLiteral(1);

        if (iter.hasNext()) {
            token = iter.next();
            if (!"[".equals(token.getText())) {
                logMessage(new CompilerException("unexpected '" + token + "'", token));
                return;
            }
            if (!iter.hasNext()) {
                logMessage(new CompilerException("expecting expression", token));
                return;
            }
            Spin1ExpressionBuilder builder = new Spin1ExpressionBuilder(scope);
            while (iter.hasNext()) {
                token = iter.next();
                if ("]".equals(token.getText())) {
                    try {
                        size = builder.getExpression();
                    } catch (CompilerException e) {
                        logMessage(e);
                    } catch (Exception e) {
                        logMessage(new CompilerException(e, builder.tokens));
                    }
                    break;
                }
                builder.addToken(token);
            }
            if (!"]".equals(token.getText())) {
                logMessage(new CompilerException("expecting '['", token));
                return;
            }
        }

        try {
            scope.addSymbol(identifier, new Variable(type, identifier, size, objectVarSize));
            scope.addSymbol("@" + identifier, new Variable(type, identifier, size, objectVarSize));
            scope.addSymbol("@@" + identifier, new Variable(type, identifier, size, objectVarSize));

            int varSize = size.getNumber().intValue();
            if ("WORD".equalsIgnoreCase(type)) {
                varSize = varSize * 2;
            }
            else if (!"BYTE".equalsIgnoreCase(type)) {
                varSize = varSize * 4;
            }
            objectVarSize += varSize;
        } catch (Exception e) {
            logMessage(new CompilerException(e, node.identifier));
        }

        if (iter.hasNext()) {
            Node error = new Node();
            iter.forEachRemaining(t -> error.addToken(t));
            logMessage(new CompilerException("unexpected '" + error + "'", error));
        }
    }

    void compileObjBlock(Node parent) {
        parent.accept(new NodeVisitor() {

            @Override
            public void visitObject(ObjectNode node) {
                Iterator<Token> iter = node.getTokens().iterator();

                Token token = iter.next();
                String name = token.getText();
                int count = 1;

                if (!iter.hasNext()) {
                    logMessage(new CompilerException("syntax error", node));
                    return;
                }
                token = iter.next();

                if ("[".equals(token.getText())) {
                    if (!iter.hasNext()) {
                        logMessage(new CompilerException("syntax error", node));
                        return;
                    }
                    Spin1ExpressionBuilder builder = new Spin1ExpressionBuilder(scope);
                    while (iter.hasNext()) {
                        token = iter.next();
                        if ("]".equals(token.getText())) {
                            try {
                                Expression expression = builder.getExpression();
                                count = expression.getNumber().intValue();
                            } catch (CompilerException e) {
                                logMessage(e);
                            } catch (Exception e) {
                                logMessage(new CompilerException(e, builder.tokens));
                            }
                            break;
                        }
                        builder.addToken(token);
                    }
                    if (!"]".equals(token.getText())) {
                        logMessage(new CompilerException("expecting '['", token));
                        return;
                    }
                    if (!iter.hasNext()) {
                        logMessage(new CompilerException("expecting object file", token));
                        return;
                    }
                    token = iter.next();
                }

                if (!":".equals(token.getText())) {
                    logMessage(new CompilerException("expecting ':'", token));
                    return;
                }
                if (!iter.hasNext()) {
                    logMessage(new CompilerException("syntax error", node));
                    return;
                }
                token = iter.next();
                if (token.type != Token.STRING) {
                    logMessage(new CompilerException("expecting file name", token));
                    return;
                }
                String fileName = token.getText().substring(1, token.getText().length() - 1) + ".spin";

                ObjectInfo info = childObjects.get(fileName);
                if (info == null) {
                    logMessage(new CompilerException("object " + token + " not found", token));
                    return;
                }
                if (info.hasErrors()) {
                    logMessage(new CompilerException("object " + token + " has errors", token));
                    return;
                }

                objects.put(name, new ObjectInfo(info.fileName, info.compiler, count));

                for (Entry<String, Expression> entry : info.compiler.getPublicSymbols().entrySet()) {
                    if (!(entry.getValue() instanceof Method)) {
                        String qualifiedName = name + "#" + entry.getKey();
                        scope.addSymbol(qualifiedName, entry.getValue());
                    }
                }

                if (iter.hasNext()) {
                    Node error = new Node();
                    iter.forEachRemaining(t -> error.addToken(t));
                    logMessage(new CompilerException("unexpected '" + error + "'", error));
                }
            }

        });
    }

    void compileDatBlock(Spin1Context scope, Node parent) {
        Spin1Context savedContext = scope;
        Map<Spin1PAsmLine, Spin1Context> pendingAlias = new HashMap<Spin1PAsmLine, Spin1Context>();

        for (Node child : parent.getChilds()) {
            DataLineNode node = (DataLineNode) child;
            if (node.getTokenCount() == 0) {
                continue;
            }
            if (node.label == null && node.instruction == null) {
                throw new CompilerException("syntax error", node);
            }

            String label = node.label != null ? node.label.getText() : null;
            String condition = node.condition != null ? node.condition.getText() : null;
            String mnemonic = node.instruction != null ? node.instruction.getText() : null;
            String modifier = node.modifier != null ? node.modifier.getText() : null;
            List<Spin1PAsmExpression> parameters = new ArrayList<Spin1PAsmExpression>();

            try {
                Spin1PAsmLine pasmLine = new Spin1PAsmLine(scope, label, condition, mnemonic, parameters, modifier);
                pasmLine.setData(node);

                for (DataLineNode.ParameterNode param : node.parameters) {
                    int index = 0;
                    String prefix = null;
                    Expression expression = null, count = null;

                    Token token;
                    if (index < param.getTokens().size()) {
                        token = param.getToken(index);
                        if (token.getText().startsWith("#")) {
                            prefix = (prefix == null ? "" : prefix) + token.getText();
                            index++;
                        }
                    }
                    if (index < param.getTokens().size()) {
                        token = param.getToken(index);
                        if ("\\".equals(token.getText())) {
                            prefix = (prefix == null ? "" : prefix) + token.getText();
                            index++;
                        }
                    }
                    if (index < param.getTokens().size()) {
                        try {
                            expression = buildExpression(param.getTokens().subList(index, param.getTokens().size()), pasmLine.getScope());
                            expression.setData(param);
                        } catch (Exception e) {
                            throw new CompilerException(e, param);
                        }
                    }

                    if (param.count != null) {
                        try {
                            count = buildExpression(param.count.getTokens(), pasmLine.getScope());
                        } catch (Exception e) {
                            throw new CompilerException(e, param.count);
                        }
                    }
                    parameters.add(new Spin1PAsmExpression(prefix, expression, count));
                }

                try {
                    if ("FILE".equalsIgnoreCase(mnemonic)) {
                        String fileName = parameters.get(0).toString();
                        fileName = fileName.substring(1, fileName.length() - 1);
                        byte[] data = getBinaryFile(fileName);
                        if (data == null) {
                            throw new CompilerException("file \"" + fileName + "\" not found", node.parameters.get(0));
                        }
                        pasmLine.setInstructionObject(new FileInc(pasmLine.getScope(), data));
                    }
                } catch (RuntimeException e) {
                    throw new CompilerException(e, node);
                }

                if (pasmLine.getLabel() != null) {
                    try {
                        if (pasmLine.getLabel() != null && !pasmLine.isLocalLabel()) {
                            scope = savedContext;
                        }
                        String type = "LONG";
                        if (pasmLine.getInstructionFactory() instanceof com.maccasoft.propeller.spin1.instructions.Word) {
                            type = "WORD";
                        }
                        else if (pasmLine.getInstructionFactory() instanceof com.maccasoft.propeller.spin1.instructions.Byte) {
                            type = "BYTE";
                        }
                        else if ("FILE".equalsIgnoreCase(pasmLine.getMnemonic())) {
                            type = "BYTE";
                        }
                        scope.addSymbol(pasmLine.getLabel(), new DataVariable(pasmLine.getScope(), type));
                        scope.addSymbol("@" + pasmLine.getLabel(), new ObjectContextLiteral(pasmLine.getScope()));
                        scope.addSymbol("@@" + pasmLine.getLabel(), new MemoryContextLiteral(pasmLine.getScope()));

                        if (pasmLine.getMnemonic() == null) {
                            if (!pasmLine.isLocalLabel()) {
                                pendingAlias.put(pasmLine, scope);
                            }
                        }
                        else if (pendingAlias.size() != 0) {
                            for (Entry<Spin1PAsmLine, Spin1Context> entry : pendingAlias.entrySet()) {
                                Spin1PAsmLine line = entry.getKey();
                                Spin1Context context = entry.getValue();
                                context.addOrUpdateSymbol(line.getLabel(), new DataVariable(line.getScope(), type));
                                context.addOrUpdateSymbol("@" + line.getLabel(), new ObjectContextLiteral(line.getScope()));
                                context.addOrUpdateSymbol("@@" + line.getLabel(), new MemoryContextLiteral(line.getScope()));
                            }
                            pendingAlias.clear();
                        }

                        if (pasmLine.getLabel() != null && !pasmLine.isLocalLabel()) {
                            scope = pasmLine.getScope();
                        }
                    } catch (RuntimeException e) {
                        throw new CompilerException(e, node.label);
                    }
                }
                else if (pasmLine.getMnemonic() != null && pendingAlias.size() != 0) {
                    String type = "LONG";
                    if (pasmLine.getInstructionFactory() instanceof com.maccasoft.propeller.spin1.instructions.Word) {
                        type = "WORD";
                    }
                    else if (pasmLine.getInstructionFactory() instanceof com.maccasoft.propeller.spin1.instructions.Byte) {
                        type = "BYTE";
                    }
                    else if ("FILE".equalsIgnoreCase(pasmLine.getMnemonic())) {
                        type = "BYTE";
                    }

                    for (Entry<Spin1PAsmLine, Spin1Context> entry : pendingAlias.entrySet()) {
                        Spin1PAsmLine line = entry.getKey();
                        Spin1Context context = entry.getValue();
                        context.addOrUpdateSymbol(line.getLabel(), new DataVariable(line.getScope(), type));
                        context.addOrUpdateSymbol("@" + line.getLabel(), new ObjectContextLiteral(line.getScope()));
                        context.addOrUpdateSymbol("@@" + line.getLabel(), new MemoryContextLiteral(line.getScope()));
                    }
                    pendingAlias.clear();
                }

                source.addAll(pasmLine.expand());
            } catch (CompilerException e) {
                logMessage(e);
            } catch (Exception e) {
                logMessage(new CompilerException(e, node.instruction));
            }
        }
    }

    protected byte[] getBinaryFile(String fileName) {
        return null;
    }

    protected boolean isReferenced(MethodNode node) {
        return true;
    }

    Spin1Method compileMethod(MethodNode node) {
        Spin1Context localScope = new Spin1Context(scope);
        List<LocalVariable> parameters = new ArrayList<LocalVariable>();
        List<LocalVariable> returns = new ArrayList<LocalVariable>();
        List<LocalVariable> localVariables = new ArrayList<LocalVariable>();

        LocalVariable defaultReturn = new LocalVariable("LONG", "RESULT", new NumberLiteral(1), 0);
        localScope.addSymbol(defaultReturn.getName(), defaultReturn);
        localScope.addSymbol("@" + defaultReturn.getName(), defaultReturn);
        localScope.addSymbol("@@" + defaultReturn.getName(), defaultReturn);
        localScope.addSymbol(defaultReturn.getName().toLowerCase(), defaultReturn);
        localScope.addSymbol("@" + defaultReturn.getName().toLowerCase(), defaultReturn);
        localScope.addSymbol("@@" + defaultReturn.getName().toLowerCase(), defaultReturn);
        returns.add(defaultReturn);

        Iterator<Token> iter = node.getTokens().iterator();
        Token token = iter.next(); // First token is PUB/PRI already checked

        if (!iter.hasNext()) {
            logMessage(new CompilerException("expecting method name", token));
            return null;
        }
        token = iter.next();
        if (token.type != 0) {
            logMessage(new CompilerException("expecting identifier", token));
            return null;
        }

        Spin1Method method = new Spin1Method(localScope, token.getText(), parameters, returns, localVariables);
        method.setComment(node.getText());
        method.setData(node);

        int offset = 4;
        for (Node child : node.getParameters()) {
            token = child.getToken(0);
            if (token.type == 0) {
                String identifier = token.getText();
                Expression expression = localScope.getLocalSymbol(identifier);
                if (expression instanceof LocalVariable) {
                    logMessage(new CompilerException("symbol '" + identifier + "' already defined", child));
                }
                else {
                    if (expression != null) {
                        logMessage(new CompilerException(CompilerException.WARNING, "parameter '" + identifier + "' hides global variable", child));
                    }
                    LocalVariable var = new LocalVariable("LONG", identifier, new NumberLiteral(1), offset);
                    localScope.addSymbol(identifier, var);
                    localScope.addSymbol("@" + identifier, var);
                    localScope.addSymbol("@@" + identifier, var);
                    parameters.add(var);
                    offset += 4;
                }
            }
            else {
                logMessage(new CompilerException("expecting identifier", token));
            }
            if (child.getTokenCount() > 1) {
                Node error = new Node(child.getTokens().subList(1, child.getTokenCount()));
                logMessage(new CompilerException("unexpected '" + error + "'", error));
            }
        }

        if (iter.hasNext()) {
            token = iter.next();
            if ("(".equals(token.getText())) {
                if (parameters.size() == 0) {
                    logMessage(new CompilerException("expecting parameter name", token));
                }
                else {
                    while (iter.hasNext()) {
                        token = iter.next();
                        if (")".equals(token.getText())) {
                            break;
                        }
                    }
                    if (!")".equals(token.getText())) {
                        logMessage(new CompilerException("expecting ',' or ')'", token));
                    }
                }
            }
        }

        for (Node child : node.getReturnVariables()) {
            token = child.getToken(0);
            if (token.type == 0) {
                String identifier = child.getText();
                if ("result".equals(identifier) || "RESULT".equals(identifier)) {
                    continue;
                }
                Expression expression = localScope.getLocalSymbol(identifier);
                if (expression instanceof LocalVariable) {
                    logMessage(new CompilerException("symbol '" + identifier + "' already defined", child));
                }
                else {
                    if (expression != null) {
                        logMessage(new CompilerException(CompilerException.WARNING, "return variable '" + identifier + "' hides global variable", child));
                    }
                    LocalVariable var = new LocalVariable("LONG", identifier, new NumberLiteral(1), 0);
                    localScope.addSymbol(identifier, var);
                    localScope.addSymbol("@" + identifier, var);
                    localScope.addSymbol("@@" + identifier, var);
                    returns.add(var);
                }
            }
            else {
                logMessage(new CompilerException("expecting identifier", token));
            }
            if (child.getTokenCount() > 1) {
                Node error = new Node(child.getTokens().subList(1, child.getTokenCount()));
                logMessage(new CompilerException("unexpected '" + error + "'", error));
            }
        }

        for (MethodNode.LocalVariableNode child : node.getLocalVariables()) {
            String type = "LONG";
            iter = child.getTokens().iterator();

            token = iter.next();
            if (Spin2Model.isType(token.getText())) {
                type = token.getText().toUpperCase();
                if (!iter.hasNext()) {
                    logMessage(new CompilerException("expecting identifier", token));
                    continue;
                }
                token = iter.next();
            }
            if (token.type == 0) {
                String identifier = token.getText();
                Expression size = new NumberLiteral(1);
                int varSize = 1;

                if (iter.hasNext()) {
                    token = iter.next();
                    if ("[".equals(token.getText())) {
                        if (!iter.hasNext()) {
                            logMessage(new CompilerException("expecting expression", token));
                            continue;
                        }
                        Spin1ExpressionBuilder builder = new Spin1ExpressionBuilder(scope);
                        while (iter.hasNext()) {
                            token = iter.next();
                            if ("]".equals(token.getText())) {
                                try {
                                    size = builder.getExpression();
                                    varSize = size.getNumber().intValue();
                                } catch (CompilerException e) {
                                    logMessage(e);
                                } catch (Exception e) {
                                    logMessage(new CompilerException(e, builder.tokens));
                                }
                                break;
                            }
                            builder.addToken(token);
                        }
                        if (!"]".equals(token.getText())) {
                            logMessage(new CompilerException("expecting '['", token));
                            continue;
                        }
                    }
                    else {
                        Node error = new Node();
                        error.addToken(token);
                        iter.forEachRemaining(t -> error.addToken(t));
                        logMessage(new CompilerException("unexpected '" + error + "'", error));
                    }
                }

                Expression expression = localScope.getLocalSymbol(identifier);
                if (expression instanceof LocalVariable) {
                    logMessage(new CompilerException("symbol '" + identifier + "' already defined", child));
                }
                else {
                    if (expression != null) {
                        logMessage(new CompilerException(CompilerException.WARNING, "local variable '" + identifier + "' hides global variable", child));
                    }
                    LocalVariable var = new LocalVariable(type, identifier, size, offset);
                    localScope.addSymbol(identifier, var);
                    localScope.addSymbol("@" + identifier, var);
                    localScope.addSymbol("@@" + identifier, var);
                    localVariables.add(var);

                    int count = 4;
                    if ("BYTE".equalsIgnoreCase(type)) {
                        count = 1;
                    }
                    if ("WORD".equalsIgnoreCase(type)) {
                        count = 2;
                    }
                    offset += ((count * varSize + 3) / 4) * 4;
                }

                if (iter.hasNext()) {
                    Node error = new Node();
                    iter.forEachRemaining(t -> error.addToken(t));
                    logMessage(new CompilerException("unexpected '" + error + "'", error));
                }
            }
            else {
                logMessage(new CompilerException("expecting identifier", token));
            }
        }

        for (Node child : node.getChilds()) {
            if (child instanceof ErrorNode) {
                logMessage(new CompilerException("unexpected '" + child + "'", child));
            }
        }

        List<Spin1MethodLine> childs = compileStatements(localScope, node.getChilds());
        childs.add(new Spin1MethodLine(localScope, "RETURN"));
        childs.add(new Spin1MethodLine(localScope));
        for (Spin1MethodLine line : childs) {
            method.addSource(line);
        }

        return method;
    }

    List<Spin1MethodLine> compileStatements(Spin1Context context, List<Node> childs) {
        List<Spin1MethodLine> lines = new ArrayList<Spin1MethodLine>();

        for (Node node : childs) {
            if (node instanceof StatementNode) {
                try {
                    Iterator<Token> iter = node.getTokens().iterator();
                    if (!iter.hasNext()) {
                        throw new RuntimeException("syntax error");
                    }

                    Token token = iter.next();
                    if ("ABORT".equalsIgnoreCase(token.getText())) {
                        if (iter.hasNext()) {
                            Spin1MethodLine line = new Spin1MethodLine(context, token.getText(), node);

                            Spin1TreeBuilder builder = new Spin1TreeBuilder();
                            while (iter.hasNext()) {
                                builder.addToken(iter.next());
                            }
                            line.addArgument(builder.getRoot());

                            lines.add(line);
                        }
                        else {
                            Spin1MethodLine line = new Spin1MethodLine(context, token.getText(), node);
                            lines.add(line);
                        }
                    }
                    else if ("IF".equalsIgnoreCase(token.getText()) || "IFNOT".equalsIgnoreCase(token.getText())) {
                        if (!iter.hasNext()) {
                            throw new RuntimeException("expected expression");
                        }

                        Spin1MethodLine line = new Spin1MethodLine(context, token.getText(), node);

                        Spin1TreeBuilder builder = new Spin1TreeBuilder();
                        while (iter.hasNext()) {
                            builder.addToken(iter.next());
                        }
                        line.addArgument(builder.getRoot());

                        lines.add(line);

                        line.addChilds(compileStatements(context, node.getChilds()));

                        Spin1MethodLine falseLine = new Spin1MethodLine(context);
                        lines.add(falseLine);
                        line.setData("false", falseLine);

                        Spin1MethodLine exitLine = new Spin1MethodLine(context);
                        lines.add(exitLine);
                    }
                    else if ("ELSEIF".equalsIgnoreCase(token.getText()) || "ELSEIFNOT".equalsIgnoreCase(token.getText())) {
                        if (!iter.hasNext()) {
                            throw new RuntimeException("expected expression");
                        }

                        Spin1MethodLine line = new Spin1MethodLine(context, token.getText(), node);

                        Spin1TreeBuilder builder = new Spin1TreeBuilder();
                        while (iter.hasNext()) {
                            builder.addToken(iter.next());
                        }
                        line.addArgument(builder.getRoot());

                        Spin1MethodLine exitLine = lines.remove(lines.size() - 1);
                        lines.get(lines.size() - 2).setData("exit", exitLine);

                        lines.add(line);

                        line.addChilds(compileStatements(context, node.getChilds()));

                        Spin1MethodLine falseLine = new Spin1MethodLine(context);
                        lines.add(falseLine);
                        line.setData("false", falseLine);

                        lines.add(exitLine);
                    }
                    else if ("ELSE".equalsIgnoreCase(token.getText())) {
                        if (iter.hasNext()) {
                            throw new RuntimeException("syntax error");
                        }

                        Spin1MethodLine exitLine = lines.remove(lines.size() - 1);
                        lines.get(lines.size() - 2).setData("exit", exitLine);

                        Spin1MethodLine line = new Spin1MethodLine(context, token.getText(), node);
                        lines.add(line);

                        line.addChilds(compileStatements(context, node.getChilds()));

                        lines.add(exitLine);
                    }
                    else if ("REPEAT".equalsIgnoreCase(token.getText())) {
                        String text = token.getText();

                        Spin1MethodLine line = new Spin1MethodLine(context, text, node);

                        if (iter.hasNext()) {
                            token = iter.next();
                            if ("WHILE".equalsIgnoreCase(token.getText()) || "UNTIL".equalsIgnoreCase(token.getText())) {
                                line.setStatement(text + " " + token.getText());
                                Spin1TreeBuilder builder = new Spin1TreeBuilder();
                                while (iter.hasNext()) {
                                    builder.addToken(iter.next());
                                }
                                line.addArgument(builder.getRoot());
                            }
                            else {
                                Spin1TreeBuilder builder = new Spin1TreeBuilder();
                                builder.addToken(token);

                                while (iter.hasNext()) {
                                    token = iter.next();
                                    if ("FROM".equalsIgnoreCase(token.getText())) {
                                        break;
                                    }
                                    builder.addToken(token);
                                }
                                line.addArgument(builder.getRoot());

                                if ("FROM".equalsIgnoreCase(token.getText())) {
                                    builder = new Spin1TreeBuilder();
                                    while (iter.hasNext()) {
                                        token = iter.next();
                                        if ("TO".equalsIgnoreCase(token.getText())) {
                                            break;
                                        }
                                        builder.addToken(token);
                                    }
                                    line.addArgument(builder.getRoot());

                                    if ("TO".equalsIgnoreCase(token.getText())) {
                                        builder = new Spin1TreeBuilder();
                                        while (iter.hasNext()) {
                                            token = iter.next();
                                            if ("STEP".equalsIgnoreCase(token.getText())) {
                                                break;
                                            }
                                            builder.addToken(token);
                                        }
                                        line.addArgument(builder.getRoot());

                                        if ("STEP".equalsIgnoreCase(token.getText())) {
                                            builder = new Spin1TreeBuilder();
                                            while (iter.hasNext()) {
                                                builder.addToken(iter.next());
                                            }
                                            line.addArgument(builder.getRoot());
                                        }
                                    }
                                }
                            }
                        }

                        lines.add(line);

                        Spin1MethodLine loopLine = line;
                        if (line.getArgumentsCount() == 1) {
                            loopLine = new Spin1MethodLine(context);
                            line.addChild(loopLine);
                        }

                        line.addChilds(compileStatements(context, node.getChilds()));
                        if (line.getChildsCount() == 0) {
                            line.addChild(new Spin1MethodLine(context));
                        }

                        if (line.getArgumentsCount() == 3 || line.getArgumentsCount() == 4) {
                            loopLine = new Spin1MethodLine(context, "REPEAT-LOOP");
                            line.addChild(loopLine);
                        }
                        else {
                            line.addChild(new Spin1MethodLine(context, "NEXT"));
                        }

                        line.setData("next", loopLine);

                        Spin1MethodLine quitLine = new Spin1MethodLine(context);
                        lines.add(quitLine);
                        line.setData("quit", quitLine);
                    }
                    else if ("WHILE".equalsIgnoreCase(token.getText()) || "UNTIL".equalsIgnoreCase(token.getText())) {
                        if (!iter.hasNext()) {
                            throw new RuntimeException("expected expression");
                        }

                        Spin1MethodLine line = new Spin1MethodLine(context, token.getText(), node);

                        Spin1TreeBuilder builder = new Spin1TreeBuilder();
                        while (iter.hasNext()) {
                            builder.addToken(iter.next());
                        }
                        line.addArgument(builder.getRoot());

                        Spin1MethodLine repeatLine = lines.get(lines.size() - 2);
                        repeatLine.childs.remove(repeatLine.childs.size() - 1);

                        line.setData("true", repeatLine);
                        lines.add(lines.size() - 1, line);

                        repeatLine.setData("next", line);
                    }
                    else if ("RETURN".equalsIgnoreCase(token.getText())) {
                        Spin1MethodLine line = new Spin1MethodLine(context, token.getText(), node);

                        if (iter.hasNext()) {
                            Spin1TreeBuilder builder = new Spin1TreeBuilder();
                            while (iter.hasNext()) {
                                builder.addToken(iter.next());
                            }
                            line.addArgument(builder.getRoot());
                        }

                        lines.add(line);
                    }
                    else if ("QUIT".equalsIgnoreCase(token.getText()) || "NEXT".equalsIgnoreCase(token.getText())) {
                        if (iter.hasNext()) {
                            throw new RuntimeException("syntax error");
                        }
                        Spin1MethodLine line = new Spin1MethodLine(context, token.getText(), node);
                        lines.add(line);
                    }
                    else if ("CASE".equalsIgnoreCase(token.getText())) {
                        Spin1MethodLine line = new Spin1MethodLine(context, token.getText(), node);

                        if (!iter.hasNext()) {
                            throw new RuntimeException("expected expression");
                        }
                        Spin1TreeBuilder builder = new Spin1TreeBuilder();
                        while (iter.hasNext()) {
                            builder.addToken(iter.next());
                        }
                        line.addArgument(builder.getRoot());

                        for (Node child : node.getChilds()) {
                            if (child instanceof StatementNode) {
                                Iterator<Token> childIter = child.getTokens().iterator();
                                if (!childIter.hasNext()) {
                                    throw new RuntimeException("expected expression");
                                }

                                while (childIter.hasNext()) {
                                    token = childIter.next();
                                    if (":".equals(token.getText())) {
                                        break;
                                    }
                                    builder.addToken(token);
                                }
                                if (childIter.hasNext()) {
                                    throw new RuntimeException("syntax error");
                                }

                                Spin1MethodLine targetLine = new Spin1MethodLine(context);
                                targetLine.addChilds(compileStatements(context, child.getChilds()));
                                targetLine.addChild(new Spin1MethodLine(context, "CASE_DONE"));

                                try {
                                    Spin1StatementNode expression = builder.getRoot();
                                    if ("OTHER".equalsIgnoreCase(expression.getText())) {
                                        line.childs.add(0, targetLine);
                                        targetLine.parent = line;
                                        line.setData("other", targetLine);
                                    }
                                    else {
                                        expression.setData("true", targetLine);
                                        line.addChild(targetLine);
                                        line.addArgument(expression);
                                    }
                                } catch (CompilerException e) {
                                    throw e;
                                } catch (Exception e) {
                                    throw new CompilerException(e, child);
                                }
                            }
                        }

                        Spin1MethodLine doneLine = new Spin1MethodLine(context);
                        line.addChild(doneLine);
                        line.setData("end", doneLine);
                        lines.add(line);
                    }
                    else {
                        Spin1MethodLine line = new Spin1MethodLine(context, null, node);

                        Spin1TreeBuilder builder = new Spin1TreeBuilder();
                        iter = node.getTokens().iterator();
                        while (iter.hasNext()) {
                            token = iter.next();
                            builder.addToken(token);
                        }
                        line.addArgument(builder.getRoot());

                        lines.add(line);
                    }

                } catch (CompilerException e) {
                    logMessage(e);
                } catch (Exception e) {
                    logMessage(new CompilerException(e, node));
                }
            }
            else {
                lines.addAll(compileStatements(context, node.getChilds()));
            }
        }

        return lines;
    }

    void compileLine(Spin1MethodLine line) {
        String text = line.getStatement();

        if ("ABORT".equalsIgnoreCase(text)) {
            if (line.getArgumentsCount() == 0) {
                line.addSource(new Bytecode(line.getScope(), 0b00110000, text));
            }
            else if (line.getArgumentsCount() == 1) {
                line.addSource(compileBytecodeExpression(line.getScope(), line.getArgument(0), true));
                line.addSource(new Bytecode(line.getScope(), 0b00110001, text));
            }
            else {
                throw new RuntimeException("expected 0 or 1 argument(s), found " + line.getArgumentsCount());
            }
        }
        else if ("IF".equalsIgnoreCase(text) || "ELSEIF".equalsIgnoreCase(text)) {
            if (line.getArgumentsCount() == 0) {
                throw new RuntimeException("expected expression");
            }
            if (line.getArgumentsCount() != 1) {
                throw new RuntimeException("syntax error");
            }
            line.addSource(compileBytecodeExpression(line.getScope(), line.getArgument(0), true));
            Spin1MethodLine target = (Spin1MethodLine) line.getData("false");
            line.addSource(new Jz(line.getScope(), new ContextLiteral(target.getScope())));
        }
        else if ("IFNOT".equalsIgnoreCase(text) || "ELSEIFNOT".equalsIgnoreCase(text)) {
            if (line.getArgumentsCount() == 0) {
                throw new RuntimeException("expected expression");
            }
            if (line.getArgumentsCount() != 1) {
                throw new RuntimeException("syntax error");
            }
            line.addSource(compileBytecodeExpression(line.getScope(), line.getArgument(0), true));
            Spin1MethodLine target = (Spin1MethodLine) line.getData("false");
            line.addSource(new Jnz(line.getScope(), new ContextLiteral(target.getScope())));
        }
        else if ("ELSE".equalsIgnoreCase(text)) {

        }
        else if ("REPEAT".equalsIgnoreCase(text)) {
            if (line.getArgumentsCount() == 1) {
                line.addSource(compileConstantExpression(line.getScope(), line.getArgument(0)));
                Spin1MethodLine target = (Spin1MethodLine) line.getData("quit");
                line.addSource(new Tjz(line.getScope(), new ContextLiteral(target.getScope())));
                line.setData("pop", Integer.valueOf(4));
            }
            else if (line.getArgumentsCount() == 3 || line.getArgumentsCount() == 4) {
                line.addSource(compileConstantExpression(line.getScope(), line.getArgument(1)));

                String varText = line.getArgument(0).getText();
                Expression expression = line.getScope().getLocalSymbol(varText);
                if (expression == null) {
                    throw new CompilerException("undefined symbol " + varText, line.getArgument(0).getToken());
                }
                else if ((expression instanceof Variable) || (expression instanceof LocalVariable)) {
                    line.addSource(new VariableOp(line.getScope(), VariableOp.Op.Write, (Variable) expression));
                }
                else {
                    throw new RuntimeException("unsupported " + line.getArgument(0));
                }
            }
            else if (line.getArgumentsCount() != 0) {
                throw new RuntimeException("unsupported");
            }
        }
        else if ("REPEAT WHILE".equalsIgnoreCase(text)) {
            if (line.getArgumentsCount() == 0) {
                throw new RuntimeException("expected expression");
            }
            if (line.getArgumentsCount() != 1) {
                throw new RuntimeException("syntax error");
            }
            line.addSource(compileBytecodeExpression(line.getScope(), line.getArgument(0), true));
            Spin1MethodLine target = (Spin1MethodLine) line.getData("quit");
            line.addSource(new Jz(line.getScope(), new ContextLiteral(target.getScope())));
        }
        else if ("REPEAT UNTIL".equalsIgnoreCase(text)) {
            if (line.getArgumentsCount() == 0) {
                throw new RuntimeException("expected expression");
            }
            if (line.getArgumentsCount() != 1) {
                throw new RuntimeException("syntax error");
            }
            line.addSource(compileBytecodeExpression(line.getScope(), line.getArgument(0), true));
            Spin1MethodLine target = (Spin1MethodLine) line.getData("quit");
            line.addSource(new Jnz(line.getScope(), new ContextLiteral(target.getScope())));
        }
        else if ("WHILE".equalsIgnoreCase(text)) {
            if (line.getArgumentsCount() == 0) {
                throw new RuntimeException("expected expression");
            }
            if (line.getArgumentsCount() != 1) {
                throw new RuntimeException("syntax error");
            }
            line.addSource(compileBytecodeExpression(line.getScope(), line.getArgument(0), true));
            Spin1MethodLine target = (Spin1MethodLine) line.getData("true");
            line.addSource(new Jnz(line.getScope(), new ContextLiteral(target.getScope())));
        }
        else if ("UNTIL".equalsIgnoreCase(text)) {
            if (line.getArgumentsCount() == 0) {
                throw new RuntimeException("expected expression");
            }
            if (line.getArgumentsCount() != 1) {
                throw new RuntimeException("syntax error");
            }
            line.addSource(compileBytecodeExpression(line.getScope(), line.getArgument(0), true));
            Spin1MethodLine target = (Spin1MethodLine) line.getData("true");
            line.addSource(new Jz(line.getScope(), new ContextLiteral(target.getScope())));
        }
        else if ("NEXT".equalsIgnoreCase(text)) {
            Spin1MethodLine repeat = line.getParent();
            while (repeat != null) {
                if ("REPEAT".equalsIgnoreCase(repeat.getStatement())) {
                    break;
                }
                if ("REPEAT WHILE".equalsIgnoreCase(repeat.getStatement())) {
                    break;
                }
                if ("REPEAT UNTIL".equalsIgnoreCase(repeat.getStatement())) {
                    break;
                }
                repeat = repeat.getParent();
            }

            Spin1MethodLine target = (Spin1MethodLine) repeat.getData("next");

            if ("REPEAT WHILE".equalsIgnoreCase(repeat.getStatement()) || "REPEAT UNTIL".equalsIgnoreCase(repeat.getStatement())) {
                line.addSource(new Jmp(line.getScope(), new ContextLiteral(repeat.getScope())));
            }
            else if (repeat.getArgumentsCount() == 1) {
                line.addSource(new Djnz(line.getScope(), new ContextLiteral(target.getScope())));
            }
            else {
                line.addSource(new Jmp(line.getScope(), new ContextLiteral(target.getScope())));
            }
        }
        else if ("REPEAT-LOOP".equalsIgnoreCase(text)) {
            Spin1MethodLine repeat = line.getParent();
            while (repeat != null) {
                if ("REPEAT".equalsIgnoreCase(repeat.getStatement())) {
                    break;
                }
                repeat = repeat.getParent();
            }

            Spin1MethodLine target = repeat.getChild(0);

            if (repeat.getArgumentsCount() == 1) {
                String varText = repeat.getArgument(0).getText();
                Expression expression = line.getScope().getLocalSymbol(varText);
                if (expression == null) {
                    throw new CompilerException("undefined symbol " + varText, line.getArgument(0).getToken());
                }
                else if (expression instanceof Variable) {
                    line.addSource(new VariableOp(line.getScope(), VariableOp.Op.Read, (Variable) expression));
                }
                else {
                    throw new RuntimeException("unsupported " + line.getArgument(0));
                }
            }
            else if (repeat.getArgumentsCount() == 3 || repeat.getArgumentsCount() == 4) {
                if (repeat.getArgumentsCount() == 4) {
                    line.addSource(compileConstantExpression(line.getScope(), repeat.getArgument(3)));
                }
                if (OPENSPIN_COMPATIBILITY) {
                    line.addSource(compileBytecodeExpression(line.getScope(), repeat.getArgument(1), true));
                    line.addSource(compileBytecodeExpression(line.getScope(), repeat.getArgument(2), true));
                }
                else {
                    line.addSource(compileConstantExpression(line.getScope(), repeat.getArgument(1)));
                    line.addSource(compileConstantExpression(line.getScope(), repeat.getArgument(2)));
                }

                String varText = repeat.getArgument(0).getText();
                Expression expression = line.getScope().getLocalSymbol(varText);
                if (expression == null) {
                    throw new CompilerException("undefined symbol " + varText, repeat.getArgument(0).getToken());
                }
                else if (expression instanceof Variable) {
                    line.addSource(new VariableOp(line.getScope(), VariableOp.Op.Assign, (Variable) expression));
                    line.addSource(new RepeatLoop(line.getScope(), repeat.getArgumentsCount() == 4, new ContextLiteral(target.getScope())));
                }
                else {
                    throw new RuntimeException("unsupported " + line.getArgument(0));
                }
            }
        }
        else if ("QUIT".equalsIgnoreCase(text)) {
            int pop = 0;
            boolean hasCase = false;

            Spin1MethodLine repeat = line.getParent();
            while (repeat != null) {
                if ("CASE".equalsIgnoreCase(repeat.getStatement())) {
                    pop += 8;
                    hasCase = true;
                }
                if ("REPEAT".equalsIgnoreCase(repeat.getStatement())) {
                    break;
                }
                if ("REPEAT WHILE".equalsIgnoreCase(repeat.getStatement())) {
                    break;
                }
                if ("REPEAT UNTIL".equalsIgnoreCase(repeat.getStatement())) {
                    break;
                }
                repeat = repeat.getParent();
            }

            if (repeat.getData("pop") != null) {
                pop += (Integer) repeat.getData("pop");
            }

            if (hasCase == false && pop == 4) {
                Spin1MethodLine target = (Spin1MethodLine) repeat.getData("quit");
                line.addSource(new Jnz(line.getScope(), new ContextLiteral(target.getScope())));
            }
            else {
                if (pop != 0) {
                    try {
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        os.write(new Constant(line.getScope(), new NumberLiteral(pop)).getBytes());
                        os.write(0x14);
                        line.addSource(new Bytecode(line.getScope(), os.toByteArray(), String.format("POP %d", pop)));
                    } catch (Exception e) {
                        // Do nothing
                    }
                }
                Spin1MethodLine target = (Spin1MethodLine) repeat.getData("quit");
                line.addSource(new Jmp(line.getScope(), new ContextLiteral(target.getScope())));
            }
        }
        else if ("RETURN".equalsIgnoreCase(text)) {
            if (line.getArgumentsCount() == 0) {
                line.addSource(new Bytecode(line.getScope(), 0b00110010, text));
            }
            else if (line.getArgumentsCount() == 1) {
                line.addSource(compileBytecodeExpression(line.getScope(), line.getArgument(0), true));
                line.addSource(new Bytecode(line.getScope(), 0b00110011, text));
            }
            else {
                throw new RuntimeException("unsupported");
            }
        }
        else if ("CASE".equalsIgnoreCase(text)) {
            Spin1MethodLine end = (Spin1MethodLine) line.getData("end");
            line.addSource(new Address(line.getScope(), new ContextLiteral(end.getScope())));

            for (Spin1StatementNode arg : line.getArguments()) {
                Spin1MethodLine target = (Spin1MethodLine) arg.getData("true");
                if (target != null) {
                    if (",".equals(arg.getText())) {
                        for (Spin1StatementNode child : arg.getChilds()) {
                            if ("..".equals(child.getText())) {
                                line.addSource(compileBytecodeExpression(line.getScope(), child.getChild(0), false));
                                line.addSource(compileBytecodeExpression(line.getScope(), child.getChild(1), false));
                                line.addSource(new CaseRangeJmp(line.getScope(), new ContextLiteral(target.getScope())));
                            }
                            else {
                                line.addSource(compileBytecodeExpression(line.getScope(), child, false));
                                line.addSource(new CaseJmp(line.getScope(), new ContextLiteral(target.getScope())));
                            }
                        }
                    }
                    else if ("..".equals(arg.getText())) {
                        line.addSource(compileBytecodeExpression(line.getScope(), arg.getChild(0), false));
                        line.addSource(compileBytecodeExpression(line.getScope(), arg.getChild(1), false));
                        line.addSource(new CaseRangeJmp(line.getScope(), new ContextLiteral(target.getScope())));
                    }
                    else {
                        line.addSource(compileBytecodeExpression(line.getScope(), arg, false));
                        line.addSource(new CaseJmp(line.getScope(), new ContextLiteral(target.getScope())));
                    }
                }
                else {
                    line.addSource(compileBytecodeExpression(line.getScope(), arg, true));
                }
            }

            if (line.getData("other") == null) {
                line.addSource(new Bytecode(line.getScope(), 0x0C, "JMP_POP"));
            }
        }
        else if ("CASE_DONE".equalsIgnoreCase(text)) {
            line.addSource(new Bytecode(line.getScope(), 0x0C, text));
        }
        else if (text != null) {
            Descriptor desc = Spin1Bytecode.getDescriptor(text);
            if (desc != null) {
                if (line.getArgumentsCount() != desc.parameters) {
                    throw new CompilerException("expected " + desc.parameters + " argument(s), found " + line.getArgumentsCount(), line.getData());
                }
                for (Spin1StatementNode arg : line.getArguments()) {
                    List<Spin1Bytecode> list = compileBytecodeExpression(line.getScope(), arg, true);
                    line.addSource(list);
                }
                line.addSource(new Bytecode(line.getScope(), desc.code, text));
            }
            else {
                //throw new RuntimeException("unknown " + text);
            }
        }
        else {
            for (Spin1StatementNode arg : line.getArguments()) {
                try {
                    line.addSource(compileBytecodeExpression(line.getScope(), arg, false));
                } catch (CompilerException e) {
                    logMessage(e);
                } catch (Exception e) {
                    logMessage(new CompilerException(e, arg.getData()));
                }
            }
        }

        Spin1MethodLine target = (Spin1MethodLine) line.getData("exit");
        if (target != null) {
            Spin1MethodLine newLine = new Spin1MethodLine(line.getScope());
            newLine.addSource(new Jmp(line.getScope(), new ContextLiteral(target.getScope())));
            line.addChild(newLine);
        }

        for (Spin1MethodLine child : line.getChilds()) {
            try {
                compileLine(child);
            } catch (CompilerException e) {
                logMessage(e);
            } catch (Exception e) {
                logMessage(new CompilerException(e, child.getData()));
            }
        }
    }

    List<Spin1Bytecode> compileConstantExpression(Spin1Context context, Spin1StatementNode node) {
        try {
            Expression expression = buildConstantExpression(context, node);
            if (expression.isConstant()) {
                return Collections.singletonList(new Constant(context, expression));
            }
        } catch (Exception e) {

        }
        return compileBytecodeExpression(context, node, true);
    }

    Expression buildConstantExpression(Spin1Context context, Spin1StatementNode node) {
        if (node.getType() == Token.NUMBER) {
            return new NumberLiteral(node.getText());
        }
        else if (node.getType() == Token.STRING) {
            String s = node.getText().substring(1);
            s = s.substring(0, s.length() - 1);
            if (s.length() == 1) {
                return new CharacterLiteral(s);
            }
            throw new RuntimeException("string not allowed");
        }

        Expression expression = context.getLocalSymbol(node.getText());
        if (expression != null) {
            if (expression.isConstant()) {
                return expression;
            }
            throw new RuntimeException("not a constant (" + expression + ")");
        }

        if ("+".equals(node.getText())) {
            if (node.getChildCount() == 1) {
                return buildConstantExpression(context, node.getChild(0));
            }
            return new Add(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
        }
        if ("-".equals(node.getText())) {
            if (node.getChildCount() == 1) {
                return new Negative(buildConstantExpression(context, node.getChild(0)));
            }
            return new Subtract(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
        }
        if ("!".equals(node.getText())) {
            if (node.getChildCount() == 1) {
                return new Not(buildConstantExpression(context, node.getChild(0)));
            }
            throw new RuntimeException("unary operator with " + node.getChildCount() + " arguments");
        }
        if ("*".equals(node.getText())) {
            return new Multiply(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
        }
        if ("/".equals(node.getText())) {
            return new Divide(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
        }
        if ("//".equals(node.getText())) {
            return new Modulo(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
        }
        if ("|".equals(node.getText())) {
            return new Or(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
        }
        if ("&".equals(node.getText())) {
            return new And(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
        }
        if ("^".equals(node.getText())) {
            return new Xor(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
        }
        if ("<<".equals(node.getText())) {
            return new ShiftLeft(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
        }
        if (">>".equals(node.getText())) {
            return new ShiftRight(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
        }
        if ("TRUNC".equalsIgnoreCase(node.getText())) {
            if (node.getChildCount() != 1) {
                throw new RuntimeException("misplaced unary operator (" + node.getText() + ")");
            }
            return new Trunc(buildConstantExpression(context, node.getChild(0)));
        }
        if ("|<".equals(node.getText())) {
            return new Decod(buildConstantExpression(context, node.getChild(0)));
        }

        throw new RuntimeException("unknown " + node.getText());
    }

    List<Spin1Bytecode> compileBytecodeExpression(Spin1Context context, Spin1StatementNode node, boolean push) {
        List<Spin1Bytecode> source = new ArrayList<Spin1Bytecode>();

        try {
            Descriptor desc = Spin1Bytecode.getDescriptor(node.getText());
            if (desc != null) {
                if (node.getChildCount() != desc.parameters) {
                    throw new RuntimeException("expected " + desc.parameters + " argument(s), found " + node.getChildCount());
                }
                if (push && desc.code_push == null) {
                    throw new RuntimeException("function " + node.getText() + " doesn't return a value");
                }
                for (int i = 0; i < desc.parameters; i++) {
                    source.addAll(compileBytecodeExpression(context, node.getChild(i), true));
                }
                source.add(new Bytecode(context, push ? desc.code_push : desc.code, node.getText()));
            }
            else if ("CONSTANT".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("expected " + 1 + " argument(s), found " + node.getChildCount());
                }
                try {
                    Expression expression = buildConstantExpression(context, node.getChild(0));
                    if (!expression.isConstant()) {
                        throw new CompilerException("expression is not constant", node.getChild(0).getToken());
                    }
                    source.add(new Constant(context, expression));
                } catch (Exception e) {
                    throw new CompilerException("expression is not constant", node.getChild(0).getToken());
                }
            }
            else if ("TRUNC".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("expected " + 1 + " argument(s), found " + node.getChildCount());
                }
                try {
                    Expression expression = buildConstantExpression(context, node.getChild(0));
                    if (!expression.isConstant()) {
                        throw new CompilerException("expression is not constant", node.getChild(0).getToken());
                    }
                    source.add(new Constant(context, new Trunc(expression)));
                } catch (Exception e) {
                    throw new CompilerException("expression is not constant", node.getChild(0).getToken());
                }
            }
            else if ("CHIPVER".equalsIgnoreCase(node.getText())) {
                source.add(new Bytecode(context, new byte[] {
                    (byte) 0x34, (byte) 0x80
                }, "CHIPVER"));
            }
            else if ("CLKFREQ".equalsIgnoreCase(node.getText())) {
                source.add(new Constant(context, new NumberLiteral(0)));
                source.add(new MemoryOp(context, MemoryOp.Size.Long, false, MemoryOp.Base.Pop, MemoryOp.Op.Read, null));
            }
            else if ("CLKMODE".equalsIgnoreCase(node.getText())) {
                source.add(new Address(context, new NumberLiteral(4)));
                source.add(new MemoryOp(context, MemoryOp.Size.Byte, false, MemoryOp.Base.Pop, MemoryOp.Op.Read, null));
            }
            else if ("COGID".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 0) {
                    throw new RuntimeException("expected " + 0 + " argument(s), found " + node.getChildCount());
                }
                source.add(new RegisterOp(context, RegisterOp.Op.Read, 0x1E9));
            }
            else if ("COGNEW".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 2) {
                    throw new RuntimeException("expected " + 2 + " argument(s), found " + node.getChildCount());
                }
                Expression expression = context.getLocalSymbol(node.getChild(0).getText());
                if (expression instanceof Method) {
                    Spin1StatementNode methodNode = node.getChild(0);
                    if (methodNode.getChildCount() != ((Method) expression).getArgumentsCount()) {
                        throw new RuntimeException("expected " + ((Method) expression).getArgumentsCount() + " argument(s), found " + methodNode.getChildCount());
                    }
                    for (int i = 0; i < methodNode.getChildCount(); i++) {
                        source.addAll(compileBytecodeExpression(context, methodNode.getChild(i), true));
                    }
                    source.add(new Constant(context, new NumberLiteral((methodNode.getChildCount() << 8) | ((Method) expression).getOffset())));
                    source.addAll(compileBytecodeExpression(context, node.getChild(1), true));
                    source.add(new Bytecode(context, 0x15, "MARK_INTERPRETED"));
                }
                else {
                    source.add(new Constant(context, new NumberLiteral(-1)));
                    source.addAll(compileBytecodeExpression(context, node.getChild(0), true));
                    source.addAll(compileBytecodeExpression(context, node.getChild(1), true));
                }
                desc = Spin1Bytecode.getDescriptor("COGINIT");
                source.add(new Bytecode(context, push ? desc.code_push : desc.code, node.getText()));
            }
            else if ("LOOKDOWN".equalsIgnoreCase(node.getText()) || "LOOKDOWNZ".equalsIgnoreCase(node.getText()) || "LOOKUP".equalsIgnoreCase(node.getText())
                || "LOOKUPZ".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() == 0) {
                    throw new RuntimeException("expected argument(s), found none");
                }
                Spin1StatementNode argsNode = node.getChild(0);
                if (!":".equalsIgnoreCase(argsNode.getText()) || argsNode.getChildCount() < 2) {
                    throw new RuntimeException("invalid argument(s)");
                }

                int code = 0b00010000;
                if ("LOOKDOWN".equalsIgnoreCase(node.getText()) || "LOOKDOWNZ".equalsIgnoreCase(node.getText())) {
                    code |= 0b00000001;
                }
                int code_range = code | 0b00000010;

                source.add(new Constant(context, new NumberLiteral(node.getText().toUpperCase().endsWith("Z") ? 0 : 1)));

                Spin1Bytecode end = new Spin1Bytecode(context);
                source.add(new Address(context, new ContextLiteral(end.getContext())));

                source.addAll(compileBytecodeExpression(context, argsNode.getChild(0), true));

                for (int i = 1; i < argsNode.getChildCount(); i++) {
                    Spin1StatementNode arg = argsNode.getChild(i);
                    if ("..".equals(arg.getText())) {
                        source.addAll(compileBytecodeExpression(context, arg.getChild(0), true));
                        source.addAll(compileBytecodeExpression(context, arg.getChild(1), true));
                        source.add(new Bytecode(context, code_range, node.getText().toUpperCase()));
                    }
                    else if (arg.getType() == Token.STRING) {
                        String s = arg.getText().substring(1, arg.getText().length() - 1);
                        for (int x = 0; x < s.length(); x++) {
                            source.add(new Constant(context, new CharacterLiteral(s.substring(x, x + 1))));
                            source.add(new Bytecode(context, code, node.getText().toUpperCase()));
                        }
                    }
                    else {
                        source.addAll(compileBytecodeExpression(context, arg, true));
                        source.add(new Bytecode(context, code, node.getText().toUpperCase()));
                    }
                }

                source.add(new Bytecode(context, 0b00001111, "LOOKDONE"));
                source.add(end);
            }
            else if ("STRING".equalsIgnoreCase(node.getText())) {
                StringBuilder sb = new StringBuilder();
                for (Spin1StatementNode child : node.getChilds()) {
                    if (child.getType() == Token.STRING) {
                        String s = child.getText().substring(1);
                        sb.append(s.substring(0, s.length() - 1));
                    }
                    else if (child.getType() == Token.NUMBER) {
                        NumberLiteral expression = new NumberLiteral(child.getText());
                        sb.append((char) expression.getNumber().intValue());
                    }
                    else {
                        try {
                            Expression expression = buildConstantExpression(context, child);
                            if (!expression.isConstant()) {
                                throw new CompilerException("expression is not constant", child.getToken());
                            }
                            sb.append((char) expression.getNumber().intValue());
                        } catch (Exception e) {
                            throw new CompilerException("expression is not constant", child.getToken());
                        }
                    }
                }
                sb.append((char) 0x00);
                Spin1Bytecode target = new Bytecode(context, sb.toString().getBytes(), "STRING".toUpperCase());
                source.add(new MemoryRef(context, MemoryRef.Size.Byte, false, MemoryRef.Base.PBase, MemoryRef.Op.Address, new ContextLiteral(target.getContext())));
                dataLine.addSource(target);
            }
            else if (node.getType() == Token.NUMBER) {
                Expression expression = new NumberLiteral(node.getText());
                source.add(new Constant(context, expression));
            }
            else if (node.getType() == Token.STRING) {
                String s = node.getText();
                if (s.startsWith("@")) {
                    s = s.substring(2, s.length() - 1);
                    s += (char) 0x00;
                    Spin1Bytecode target = new Bytecode(context, s.getBytes(), "STRING".toUpperCase());
                    source.add(new MemoryOp(context, MemoryOp.Size.Byte, false, MemoryOp.Base.PBase, MemoryOp.Op.Address, new ContextLiteral(target.getContext())));
                    dataLine.addSource(target);
                }
                else {
                    s = s.substring(1, s.length() - 1);
                    if (s.length() == 1) {
                        Expression expression = new CharacterLiteral(s);
                        source.add(new Constant(context, expression));
                    }
                    else {
                        s += (char) 0x00;
                        Spin1Bytecode target = new Bytecode(context, s.getBytes(), "STRING".toUpperCase());
                        source.add(new MemoryOp(context, MemoryOp.Size.Byte, false, MemoryOp.Base.PBase, MemoryOp.Op.Address, new ContextLiteral(target.getContext())));
                        dataLine.addSource(target);
                    }
                }
            }
            else if ("-".equals(node.getText()) && node.getChildCount() == 1) {
                if (node.getChild(0).getToken().type == Token.NUMBER) {
                    Spin1Bytecode bc1 = new Constant(context, new Negative(new NumberLiteral(node.getChild(0).getText())));
                    Spin1Bytecode bc2 = new Constant(context, new Subtract(new NumberLiteral(node.getChild(0).getText()), new NumberLiteral(1)));
                    if (bc1.getSize() <= (bc2.getSize() + 1)) {
                        source.add(bc1);
                    }
                    else {
                        source.add(bc2);
                        source.add(new Bytecode(context, 0b111_00111 | (push ? 0b10000000 : 0b00000000), "COMPLEMENT"));
                    }
                }
                else {
                    Expression expression = context.getLocalSymbol(node.getChild(0).getText());
                    if (expression != null && expression.isConstant() && push) {
                        source.add(new Constant(context, new Negative(expression)));
                    }
                    else {
                        if (!push && (expression instanceof Variable)) {
                            source.addAll(leftAssign(context, node.getChild(0), true));
                        }
                        else {
                            source.addAll(compileBytecodeExpression(context, node.getChild(0), true));
                        }
                        source.add(new Bytecode(context, push ? 0b111_00110 : 0b010_00110, "NEGATE"));
                    }
                }
            }
            else if (":=".equals(node.getText())) {
                source.addAll(compileBytecodeExpression(context, node.getChild(1), true));
                source.addAll(leftAssign(context, node.getChild(0), push));
                if (push) {
                    source.add(new Bytecode(context, 0x80, "WRITE"));
                }
            }
            else if (MathOp.isAssignMathOp(node.getText())) {
                source.addAll(compileBytecodeExpression(context, node.getChild(1), true));
                source.addAll(leftAssign(context, node.getChild(0), true));
                source.add(new MathOp(context, node.getText(), push));
            }
            else if ("||".equals(node.getText()) || "|<".equals(node.getText()) || ">|".equals(node.getText()) || "!".equals(node.getText()) || "^^".equals(node.getText())
                || "NOT".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("expression syntax error " + node.getText());
                }
                if (!push) {
                    source.addAll(leftAssign(context, node.getChild(0), true));
                    source.add(new MathOp(context, node.getText() + "=", false));
                }
                else {
                    source.addAll(compileBytecodeExpression(context, node.getChild(0), true));
                    source.add(new MathOp(context, node.getText(), push));
                }
            }
            else if (MathOp.isMathOp(node.getText())) {
                if (node.getChildCount() != 2) {
                    throw new RuntimeException("expression syntax error " + node.getText());
                }
                if (OPENSPIN_COMPATIBILITY) {
                    source.addAll(compileBytecodeExpression(context, node.getChild(0), true));
                    source.addAll(compileBytecodeExpression(context, node.getChild(1), true));
                    source.add(new MathOp(context, node.getText(), push));
                }
                else {
                    try {
                        Expression expression = buildConstantExpression(context, node);
                        source.add(new Constant(context, expression));
                    } catch (Exception e) {
                        source.addAll(compileBytecodeExpression(context, node.getChild(0), true));
                        source.addAll(compileBytecodeExpression(context, node.getChild(1), true));
                        source.add(new MathOp(context, node.getText(), push));
                    }
                }
            }
            else if ("?".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() == 1) {
                    Expression expression = context.getLocalSymbol(node.getChild(0).getText());
                    if (expression == null) {
                        throw new RuntimeException("undefined symbol " + node.getChild(0).getText());
                    }
                    if (expression instanceof Variable) {
                        int code = 0b0_00010_00;
                        if (push) {
                            code |= 0b10000000;
                        }
                        source.add(new VariableOp(context, VariableOp.Op.Assign, (Variable) expression));
                        source.add(new Bytecode(context, code, "RANDOM_FORWARD"));
                    }
                }
                else {
                    if (node.getChildCount() != 2) {
                        throw new RuntimeException("expression syntax error " + node.getText());
                    }
                    if (!":".equals(node.getChild(1).getText())) {
                        throw new RuntimeException("expression syntax error " + node.getText());
                    }

                    source.addAll(compileBytecodeExpression(context, node.getChild(0), true));

                    List<Spin1Bytecode> falseSource = compileBytecodeExpression(context, node.getChild(1).getChild(1), true);
                    source.add(new Jz(context, new ContextLiteral(falseSource.get(0).getContext())));
                    source.addAll(compileBytecodeExpression(context, node.getChild(1).getChild(0), true));

                    Spin1Bytecode endSource = new Spin1Bytecode(context);
                    source.add(new Jmp(context, new ContextLiteral(endSource.getContext())));

                    source.addAll(falseSource);
                    source.add(endSource);
                }
            }
            else if ("(".equalsIgnoreCase(node.getText())) {
                source.addAll(compileBytecodeExpression(context, node.getChild(0), push));
            }
            else if (",".equalsIgnoreCase(node.getText())) {
                for (Spin1StatementNode child : node.getChilds()) {
                    source.addAll(compileBytecodeExpression(context, child, push));
                }
            }
            else if ("\\".equalsIgnoreCase(node.getText())) {
                Expression expression = context.getLocalSymbol(node.getChild(0).getText());
                if (!(expression instanceof Method)) {
                    throw new RuntimeException("invalid symbol " + node.getText());
                }

                Spin1StatementNode argsNode = node.getChild(0);
                if (argsNode.getChildCount() == 1 && ",".equals(argsNode.getChild(0).getText())) {
                    argsNode = argsNode.getChild(0);
                }

                int parameters = ((Method) expression).getArgumentsCount();
                if (argsNode.getChildCount() != parameters) {
                    throw new RuntimeException("expected " + parameters + " argument(s), found " + argsNode.getChildCount());
                }
                source.add(new Bytecode(context, new byte[] {
                    (byte) (push ? 0b00000010 : 0b00000011),
                }, "ANCHOR (TRY)"));
                for (int i = 0; i < parameters; i++) {
                    source.addAll(compileBytecodeExpression(context, argsNode.getChild(i), true));
                }
                int objectIndex = ((Method) expression).getObject();
                int objectOffset = ((Method) expression).getOffset();
                if (objectIndex == 0) {
                    source.add(new Bytecode(context, new byte[] {
                        (byte) 0b00000101,
                        (byte) objectOffset
                    }, "CALL_SUB"));
                }
                else {
                    source.add(new Bytecode(context, new byte[] {
                        (byte) 0b00000110,
                        (byte) objectIndex,
                        (byte) objectOffset
                    }, "CALL_OBJ_SUB"));
                }
            }
            else if ("++".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("expression syntax error");
                }
                Expression expression = context.getLocalSymbol(node.getChild(0).getText());
                if (expression == null) {
                    throw new RuntimeException("undefined symbol " + node.getChild(0).getText());
                }
                if (expression instanceof Variable) {
                    Spin1Bytecode.Type type = Spin1Bytecode.Type.fromString(((Variable) expression).getType());
                    int code = Spin1Bytecode.op_ss.setValue(0b0_0100_000, type.ordinal() + 1);
                    source.add(new VariableOp(context, VariableOp.Op.Assign, (Variable) expression));
                    source.add(new Bytecode(context, Spin1Bytecode.op_p.setBoolean(code, push), "PRE_INC"));
                }
                else if (expression instanceof ContextLiteral) {
                    MemoryOp.Size ss = MemoryOp.Size.Long;
                    if (expression instanceof DataVariable) {
                        switch (((DataVariable) expression).getType()) {
                            case "BYTE":
                                ss = MemoryOp.Size.Byte;
                                break;
                            case "WORD":
                                ss = MemoryOp.Size.Word;
                                break;
                        }
                    }
                    source.add(new MemoryOp(context, ss, false, MemoryOp.Base.PBase, MemoryOp.Op.Assign, expression));
                    int code = Spin1Bytecode.op_ss.setValue(0b0_0100_000, ss.ordinal() + 1);
                    source.add(new Bytecode(context, Spin1Bytecode.op_p.setBoolean(code, push), "PRE_INC"));
                }
                else {
                    throw new CompilerException("unsupported operation on " + node.getChild(0).getText(), node.getChild(0).getToken());
                }
            }
            else if ("--".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("expression syntax error");
                }
                Expression expression = context.getLocalSymbol(node.getChild(0).getText());
                if (expression == null) {
                    throw new RuntimeException("undefined symbol " + node.getChild(0).getText());
                }
                if (expression instanceof Variable) {
                    Spin1Bytecode.Type type = Spin1Bytecode.Type.fromString(((Variable) expression).getType());
                    int code = Spin1Bytecode.op_ss.setValue(0b0_0110_000, type.ordinal() + 1);
                    source.add(new VariableOp(context, VariableOp.Op.Assign, (Variable) expression));
                    source.add(new Bytecode(context, Spin1Bytecode.op_p.setBoolean(code, push), "PRE_DEC"));
                }
                else if (expression instanceof ContextLiteral) {
                    MemoryOp.Size ss = MemoryOp.Size.Long;
                    if (expression instanceof DataVariable) {
                        switch (((DataVariable) expression).getType()) {
                            case "BYTE":
                                ss = MemoryOp.Size.Byte;
                                break;
                            case "WORD":
                                ss = MemoryOp.Size.Word;
                                break;
                        }
                    }
                    source.add(new MemoryOp(context, ss, false, MemoryOp.Base.PBase, MemoryOp.Op.Assign, expression));
                    int code = Spin1Bytecode.op_ss.setValue(0b0_0110_000, ss.ordinal() + 1);
                    source.add(new Bytecode(context, Spin1Bytecode.op_p.setBoolean(code, push), "PRE_DEC"));
                }
                else {
                    throw new CompilerException("unsupported operation on " + node.getChild(0).getText(), node.getChild(0).getToken());
                }
            }
            else if ("~".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("expression syntax error");
                }
                source.addAll(leftAssign(context, node.getChild(0), true));
                int code = 0b0_00100_00;
                if (push) {
                    code |= 0b10000000;
                }
                source.add(new Bytecode(context, code, "SIGN_EXTEND_BYTE"));
            }
            else if ("~~".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("expression syntax error");
                }
                source.addAll(leftAssign(context, node.getChild(0), true));
                int code = 0b0_00101_00;
                if (push) {
                    code |= 0b10000000;
                }
                source.add(new Bytecode(context, code, "SIGN_EXTEND_WORD"));
            }
            else if ("..".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 2) {
                    throw new RuntimeException("expression syntax error");
                }
                source.addAll(compileBytecodeExpression(context, node.getChild(0), true));
                source.addAll(compileBytecodeExpression(context, node.getChild(1), true));
            }
            else if ("BYTE".equalsIgnoreCase(node.getText()) || "WORD".equalsIgnoreCase(node.getText()) || "LONG".equalsIgnoreCase(node.getText())) {
                boolean popIndex = false;
                Spin1StatementNode postEffectNode = null;

                if (node.getChildCount() == 0) {
                    throw new RuntimeException("expected index expression");
                }
                source.addAll(compileBytecodeExpression(context, node.getChild(0), true));

                int n = 1;
                if (n < node.getChildCount()) {
                    if (!isPostEffect(node.getChild(n))) {
                        source.addAll(compileBytecodeExpression(context, node.getChild(n++), true));
                        popIndex = true;
                    }
                }
                if (n < node.getChildCount()) {
                    if (isPostEffect(node.getChild(n))) {
                        postEffectNode = node.getChild(n++);
                    }
                }
                if (n < node.getChildCount()) {
                    throw new RuntimeException("unexpected expression");
                }

                MemoryOp.Op op = push ? MemoryOp.Op.Read : MemoryOp.Op.Write;
                if (postEffectNode != null) {
                    op = MemoryOp.Op.Assign;
                }
                if ("BYTE".equalsIgnoreCase(node.getText())) {
                    source.add(new MemoryOp(context, MemoryOp.Size.Byte, popIndex, MemoryOp.Base.Pop, op, null));
                }
                else if ("WORD".equalsIgnoreCase(node.getText())) {
                    source.add(new MemoryOp(context, MemoryOp.Size.Word, popIndex, MemoryOp.Base.Pop, op, null));
                }
                else if ("LONG".equalsIgnoreCase(node.getText())) {
                    source.add(new MemoryOp(context, MemoryOp.Size.Long, popIndex, MemoryOp.Base.Pop, op, null));
                }

                if (postEffectNode != null) {
                    if ("++".equals(postEffectNode.getText())) {
                        int code = 0b0_0101_11_0;
                        if (push) {
                            code |= 0b10000000;
                        }
                        source.add(new Bytecode(context, code, "POST_INC"));
                    }
                    else if ("--".equals(postEffectNode.getText())) {
                        int code = 0b0_0111_11_0;
                        if (push) {
                            code |= 0b10000000;
                        }
                        source.add(new Bytecode(context, code, "POST_DEC"));
                    }
                    else if ("~".equals(postEffectNode.getText())) {
                        int code = 0b0_00110_00;
                        if (push) {
                            code |= 0b10000000;
                        }
                        source.add(new Bytecode(context, code, "POST_CLEAR"));
                    }
                    else if ("~~".equals(postEffectNode.getText())) {
                        int code = 0b0_00111_00;
                        if (push) {
                            code |= 0b10000000;
                        }
                        source.add(new Bytecode(context, code, "POST_SET"));
                    }
                    else if ("?".equals(postEffectNode.getText())) {
                        int code = 0b0_00011_00;
                        if (push) {
                            code |= 0b10000000;
                        }
                        source.add(new Bytecode(context, code, "RANDOM_REVERSE"));
                    }
                }
            }
            else if ("@BYTE".equalsIgnoreCase(node.getText()) || "@WORD".equalsIgnoreCase(node.getText()) || "@LONG".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() == 0) {
                    throw new RuntimeException("expected index expression");
                }
                source.addAll(compileBytecodeExpression(context, node.getChild(0), true));

                int n = 1;
                boolean popIndex = false;
                if (n < node.getChildCount()) {
                    if (!isPostEffect(node.getChild(n))) {
                        source.addAll(compileBytecodeExpression(context, node.getChild(n++), true));
                        popIndex = true;
                    }
                }
                if (n < node.getChildCount()) {
                    throw new RuntimeException("unexpected expression");
                }

                MemoryOp.Size ss = MemoryOp.Size.Byte;
                if ("@WORD".equalsIgnoreCase(node.getText())) {
                    ss = MemoryOp.Size.Word;
                }
                else if ("@LONG".equalsIgnoreCase(node.getText())) {
                    ss = MemoryOp.Size.Long;
                }

                source.add(new MemoryOp(context, ss, popIndex, MemoryOp.Base.Pop, MemoryOp.Op.Address, new NumberLiteral(0)));
            }
            else if ("@@BYTE".equalsIgnoreCase(node.getText()) || "@@WORD".equalsIgnoreCase(node.getText()) || "@@LONG".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() == 0) {
                    throw new RuntimeException("expected index expression");
                }
                source.addAll(compileBytecodeExpression(context, node.getChild(0), true));

                int n = 1;
                boolean popIndex = false;
                if (n < node.getChildCount()) {
                    source.addAll(compileBytecodeExpression(context, node.getChild(n++), true));
                    popIndex = true;
                }
                if (n < node.getChildCount()) {
                    throw new RuntimeException("unexpected expression");
                }

                MemoryOp.Size ss = MemoryOp.Size.Byte;
                if ("@@WORD".equalsIgnoreCase(node.getText())) {
                    ss = MemoryOp.Size.Word;
                }
                else if ("@@LONG".equalsIgnoreCase(node.getText())) {
                    ss = MemoryOp.Size.Long;
                }

                source.add(new MemoryOp(context, ss, popIndex, MemoryOp.Base.Pop, MemoryOp.Op.Read, new NumberLiteral(0)));
                source.add(new MemoryOp(context, MemoryOp.Size.Byte, true, MemoryOp.Base.PBase, MemoryOp.Op.Address, new NumberLiteral(0)));
            }
            else {
                String[] s = node.getText().split("[\\.]");
                if (s.length == 2 && ("BYTE".equalsIgnoreCase(s[1]) || "WORD".equalsIgnoreCase(s[1]) || "LONG".equalsIgnoreCase(s[1]))) {
                    Spin1StatementNode postEffect = null;
                    boolean indexed = false;

                    Expression expression = context.getLocalSymbol(s[0]);
                    if (expression == null) {
                        throw new CompilerException("undefined symbol " + node.getText(), node.getToken());
                    }

                    if (node.getChildCount() != 0) {
                        indexed = true;
                        source.addAll(compileBytecodeExpression(context, node.getChild(0), true));
                        if (node.getChildCount() > 1) {
                            if (isPostEffect(node.getChild(1))) {
                                postEffect = node.getChild(1);
                            }
                        }
                    }

                    MemoryOp.Base bb = MemoryOp.Base.PBase;
                    if (expression instanceof LocalVariable) {
                        bb = MemoryOp.Base.DBase;
                    }
                    else if (expression instanceof Variable) {
                        bb = MemoryOp.Base.VBase;
                    }
                    MemoryOp.Op op = push ? MemoryOp.Op.Read : MemoryOp.Op.Write;
                    if (postEffect != null) {
                        op = MemoryOp.Op.Assign;
                    }
                    if (s[0].startsWith("@")) {
                        op = MemoryOp.Op.Address;
                    }
                    if ("BYTE".equalsIgnoreCase(s[1])) {
                        source.add(new MemoryOp(context, MemoryOp.Size.Byte, indexed, bb, op, expression));
                    }
                    else if ("WORD".equalsIgnoreCase(s[1])) {
                        source.add(new MemoryOp(context, MemoryOp.Size.Word, indexed, bb, op, expression));
                    }
                    else if ("LONG".equalsIgnoreCase(s[1])) {
                        source.add(new MemoryOp(context, MemoryOp.Size.Long, indexed, bb, op, expression));
                    }

                    if (postEffect != null) {
                        if ("++".equals(postEffect.getText())) {
                            int code = 0b0_0101_11_0;
                            if (push) {
                                code |= 0b10000000;
                            }
                            source.add(new Bytecode(context, code, "POST_INC"));
                        }
                        else if ("--".equals(postEffect.getText())) {
                            int code = 0b0_0111_11_0;
                            if (push) {
                                code |= 0b10000000;
                            }
                            source.add(new Bytecode(context, code, "POST_DEC"));
                        }
                        else if ("~".equals(postEffect.getText())) {
                            int code = 0b0_00110_00;
                            if (push) {
                                code |= 0b10000000;
                            }
                            source.add(new Bytecode(context, code, "POST_CLEAR"));
                        }
                        else if ("~~".equals(postEffect.getText())) {
                            int code = 0b0_00111_00;
                            if (push) {
                                code |= 0b10000000;
                            }
                            source.add(new Bytecode(context, code, "POST_SET"));
                        }
                        else if ("?".equals(postEffect.getText())) {
                            int code = 0b0_00011_00;
                            if (push) {
                                code |= 0b10000000;
                            }
                            source.add(new Bytecode(context, code, "RANDOM_REVERSE"));
                        }
                    }
                }
                else {
                    Expression expression = context.getLocalSymbol(node.getText());
                    if (expression instanceof ObjectContextLiteral) {
                        expression = context.getLocalSymbol(node.getText().substring(1));
                    }
                    if (expression == null) {
                        ObjectInfo info = objects.get(node.getText());
                        if (info != null) {
                            if (node.getChildCount() != 2) {
                                throw new RuntimeException("syntax error" + node);
                            }
                            String qualifiedName = node.getText() + node.getChild(1).getText();
                            expression = context.getLocalSymbol(qualifiedName);
                            if (expression != null) {
                                Method method = (Method) expression;
                                int parameters = method.getArgumentsCount();
                                if (node.getChild(1).getChildCount() != parameters) {
                                    throw new RuntimeException("expected " + parameters + " argument(s), found " + node.getChild(1).getChildCount());
                                }
                                source.add(new Bytecode(context, new byte[] {
                                    (byte) (push ? 0b00000000 : 0b00000001),
                                }, "ANCHOR"));
                                for (int i = 0; i < parameters; i++) {
                                    source.addAll(compileBytecodeExpression(context, node.getChild(1).getChild(i), true));
                                }
                                source.addAll(compileConstantExpression(context, node.getChild(0)));
                                source.add(new Bytecode(context, new byte[] {
                                    (byte) 0b00000111,
                                    (byte) method.getObject(),
                                    (byte) method.getOffset()
                                }, "CALL_OBJ_SUB"));
                                return source;
                            }
                        }
                    }
                    if (expression == null) {
                        throw new CompilerException("undefined symbol " + node.getText(), node.getToken());
                    }

                    if (node.getText().startsWith("@@")) {
                        if (node.getChildCount() != 0) {
                            throw new CompilerException("syntax error", node.getToken());
                        }
                        if (expression instanceof Variable) {
                            source.add(new VariableOp(context, VariableOp.Op.Read, false, (Variable) expression));
                            source.add(new MemoryOp(context, MemoryOp.Size.Byte, true, MemoryOp.Base.PBase, MemoryOp.Op.Address, new NumberLiteral(0)));
                        }
                        else if (expression instanceof MemoryContextLiteral) {
                            source.add(new Constant(context, expression));
                        }
                        else {
                            throw new CompilerException("syntax error", node.getToken());
                        }
                    }
                    else if (node.getText().startsWith("@")) {
                        boolean popIndex = false;
                        Spin1StatementNode postEffectNode = null;

                        int n = 0;
                        if (n < node.getChildCount()) {
                            if (!isPostEffect(node.getChild(n))) {
                                source.addAll(compileBytecodeExpression(context, node.getChild(n++), true));
                                popIndex = true;
                            }
                        }
                        if (n < node.getChildCount()) {
                            if (isPostEffect(node.getChild(n))) {
                                postEffectNode = node.getChild(n++);
                            }
                        }
                        if (n < node.getChildCount()) {
                            throw new RuntimeException("syntax error");
                        }
                        if (expression instanceof Variable) {
                            if (postEffectNode != null) {
                                source.add(new VariableOp(context, VariableOp.Op.Assign, popIndex, (Variable) expression));
                                if ("++".equals(postEffectNode.getText())) {
                                    Spin1Bytecode.Type type = Spin1Bytecode.Type.fromString(((Variable) expression).getType());
                                    int code = Spin1Bytecode.op_ss.setValue(0b0_0101_000, type.ordinal() + 1);
                                    source.add(new Bytecode(context, Spin1Bytecode.op_p.setBoolean(code, push), "POST_INC"));
                                }
                                else if ("--".equals(postEffectNode.getText())) {
                                    Spin1Bytecode.Type type = Spin1Bytecode.Type.fromString(((Variable) expression).getType());
                                    int code = Spin1Bytecode.op_ss.setValue(0b0_0111_000, type.ordinal() + 1);
                                    source.add(new Bytecode(context, Spin1Bytecode.op_p.setBoolean(code, push), "POST_DEC"));
                                }
                                else if ("~".equals(postEffectNode.getText())) {
                                    int code = 0b0_00110_00;
                                    if (push) {
                                        code |= 0b10000000;
                                    }
                                    source.add(new Bytecode(context, code, "POST_CLEAR"));
                                }
                                else if ("~~".equals(postEffectNode.getText())) {
                                    int code = 0b0_00111_00;
                                    if (push) {
                                        code |= 0b10000000;
                                    }
                                    source.add(new Bytecode(context, code, "POST_SET"));
                                }
                                else if ("?".equals(postEffectNode.getText())) {
                                    int code = 0b0_00011_00;
                                    if (push) {
                                        code |= 0b10000000;
                                    }
                                    source.add(new Bytecode(context, code, "RANDOM_REVERSE"));
                                }
                            }
                            else {
                                source.add(new VariableOp(context, VariableOp.Op.Address, popIndex, (Variable) expression));
                            }
                        }
                        else if (expression instanceof ContextLiteral) {
                            MemoryOp.Size ss = MemoryOp.Size.Long;
                            if (expression instanceof DataVariable) {
                                switch (((DataVariable) expression).getType()) {
                                    case "BYTE":
                                        ss = MemoryOp.Size.Byte;
                                        break;
                                    case "WORD":
                                        ss = MemoryOp.Size.Word;
                                        break;
                                }
                            }
                            if (postEffectNode != null) {
                                source.add(new MemoryOp(context, ss, popIndex, MemoryOp.Base.PBase, MemoryOp.Op.Assign, expression));
                                if ("++".equals(postEffectNode.getText())) {
                                    int code = Spin1Bytecode.op_ss.setValue(0b0_0101_000, ss.ordinal() + 1);
                                    source.add(new Bytecode(context, Spin1Bytecode.op_p.setBoolean(code, push), "POST_INC"));
                                }
                                else if ("--".equals(postEffectNode.getText())) {
                                    int code = Spin1Bytecode.op_ss.setValue(0b0_0111_000, ss.ordinal() + 1);
                                    source.add(new Bytecode(context, Spin1Bytecode.op_p.setBoolean(code, push), "POST_DEC"));
                                }
                                else if ("~".equals(postEffectNode.getText())) {
                                    int code = 0b0_00110_00;
                                    if (push) {
                                        code |= 0b10000000;
                                    }
                                    source.add(new Bytecode(context, code, "POST_CLEAR"));
                                }
                                else if ("~~".equals(postEffectNode.getText())) {
                                    int code = 0b0_00111_00;
                                    if (push) {
                                        code |= 0b10000000;
                                    }
                                    source.add(new Bytecode(context, code, "POST_SET"));
                                }
                                else if ("?".equals(postEffectNode.getText())) {
                                    int code = 0b0_00011_00;
                                    if (push) {
                                        code |= 0b10000000;
                                    }
                                    source.add(new Bytecode(context, code, "RANDOM_REVERSE"));
                                }
                            }
                            else {
                                source.add(new MemoryOp(context, ss, popIndex, MemoryOp.Base.PBase, MemoryOp.Op.Address, expression));
                            }
                        }
                        else {
                            throw new CompilerException("syntax error", node.getToken());
                        }
                    }
                    else if (expression instanceof Register) {
                        if (node.getChildCount() != 0) {
                            boolean range = "..".equals(node.getChild(0).getText());
                            source.addAll(compileBytecodeExpression(context, node.getChild(0), true));
                            if (node.getChildCount() == 2) {
                                source.add(new RegisterBitOp(context, RegisterBitOp.Op.Assign, range, expression.getNumber().intValue()));
                                if ("~".equalsIgnoreCase(node.getChild(1).getText())) {
                                    source.add(new Bytecode(context, 0b0_00110_00, "POST_CLEAR"));
                                }
                                else if ("~~".equalsIgnoreCase(node.getChild(1).getText())) {
                                    source.add(new Bytecode(context, 0b0_00111_00, "POST_SET"));
                                }
                                else {
                                    throw new CompilerException("invalid operator " + node.getText(), node.getToken());
                                }
                            }
                            else {
                                source.add(new RegisterBitOp(context, push ? RegisterBitOp.Op.Read : RegisterBitOp.Op.Write, range, expression.getNumber().intValue()));
                            }
                        }
                        else {
                            source.add(new RegisterOp(context, RegisterOp.Op.Read, expression.getNumber().intValue()));
                        }
                    }
                    else if (expression instanceof Variable) {
                        boolean popIndex = false;
                        Spin1StatementNode postEffectNode = null;

                        int n = 0;
                        if (n < node.getChildCount()) {
                            if (!isPostEffect(node.getChild(n))) {
                                source.addAll(compileBytecodeExpression(context, node.getChild(n++), true));
                                popIndex = true;
                            }
                        }
                        if (n < node.getChildCount()) {
                            if (isPostEffect(node.getChild(n))) {
                                postEffectNode = node.getChild(n++);
                            }
                        }
                        if (n < node.getChildCount()) {
                            throw new RuntimeException("syntax error");
                        }

                        if (postEffectNode != null) {
                            source.add(new VariableOp(context, VariableOp.Op.Assign, popIndex, (Variable) expression));
                            if ("++".equals(postEffectNode.getText())) {
                                Spin1Bytecode.Type type = Spin1Bytecode.Type.fromString(((Variable) expression).getType());
                                int code = Spin1Bytecode.op_ss.setValue(0b0_0101_000, type.ordinal() + 1);
                                source.add(new Bytecode(context, Spin1Bytecode.op_p.setBoolean(code, push), "POST_INC"));
                            }
                            else if ("--".equals(postEffectNode.getText())) {
                                Spin1Bytecode.Type type = Spin1Bytecode.Type.fromString(((Variable) expression).getType());
                                int code = Spin1Bytecode.op_ss.setValue(0b0_0111_000, type.ordinal() + 1);
                                source.add(new Bytecode(context, Spin1Bytecode.op_p.setBoolean(code, push), "POST_DEC"));
                            }
                            else if ("~".equals(postEffectNode.getText())) {
                                int code = 0b0_00110_00;
                                if (push) {
                                    code |= 0b10000000;
                                }
                                source.add(new Bytecode(context, code, "POST_CLEAR"));
                            }
                            else if ("~~".equals(postEffectNode.getText())) {
                                int code = 0b0_00111_00;
                                if (push) {
                                    code |= 0b10000000;
                                }
                                source.add(new Bytecode(context, code, "POST_SET"));
                            }
                            else if ("?".equals(postEffectNode.getText())) {
                                int code = 0b0_00011_00;
                                if (push) {
                                    code |= 0b10000000;
                                }
                                source.add(new Bytecode(context, code, "RANDOM_REVERSE"));
                            }
                        }
                        else {
                            source.add(new VariableOp(context, VariableOp.Op.Read, popIndex, (Variable) expression));
                        }
                    }
                    else if (expression instanceof ContextLiteral) {
                        boolean popIndex = false;
                        Spin1StatementNode postEffectNode = null;

                        int n = 0;
                        if (n < node.getChildCount()) {
                            if (!isPostEffect(node.getChild(n))) {
                                source.addAll(compileBytecodeExpression(context, node.getChild(n++), true));
                                popIndex = true;
                            }
                        }
                        if (n < node.getChildCount()) {
                            if (isPostEffect(node.getChild(n))) {
                                postEffectNode = node.getChild(n++);
                            }
                        }
                        if (n < node.getChildCount()) {
                            throw new RuntimeException("syntax error");
                        }

                        MemoryOp.Size ss = MemoryOp.Size.Long;
                        if (expression instanceof DataVariable) {
                            switch (((DataVariable) expression).getType()) {
                                case "BYTE":
                                    ss = MemoryOp.Size.Byte;
                                    break;
                                case "WORD":
                                    ss = MemoryOp.Size.Word;
                                    break;
                            }
                        }

                        if (postEffectNode != null) {
                            source.add(new MemoryOp(context, ss, popIndex, MemoryOp.Base.PBase, MemoryOp.Op.Assign, expression));
                            if ("++".equals(postEffectNode.getText())) {
                                int code = Spin1Bytecode.op_ss.setValue(0b0_0101_000, ss.ordinal() + 1);
                                source.add(new Bytecode(context, Spin1Bytecode.op_p.setBoolean(code, push), "POST_INC"));
                            }
                            else if ("--".equals(postEffectNode.getText())) {
                                int code = Spin1Bytecode.op_ss.setValue(0b0_0111_000, ss.ordinal() + 1);
                                source.add(new Bytecode(context, Spin1Bytecode.op_p.setBoolean(code, push), "POST_DEC"));
                            }
                            else if ("~".equals(postEffectNode.getText())) {
                                int code = 0b0_00110_00;
                                if (push) {
                                    code |= 0b10000000;
                                }
                                source.add(new Bytecode(context, code, "POST_CLEAR"));
                            }
                            else if ("~~".equals(postEffectNode.getText())) {
                                int code = 0b0_00111_00;
                                if (push) {
                                    code |= 0b10000000;
                                }
                                source.add(new Bytecode(context, code, "POST_SET"));
                            }
                            else if ("?".equals(postEffectNode.getText())) {
                                int code = 0b0_00011_00;
                                if (push) {
                                    code |= 0b10000000;
                                }
                                source.add(new Bytecode(context, code, "RANDOM_REVERSE"));
                            }
                        }
                        else {
                            source.add(new MemoryOp(context, ss, popIndex, MemoryOp.Base.PBase, MemoryOp.Op.Read, expression));
                        }
                    }
                    else if (expression instanceof Method) {
                        Method method = (Method) expression;
                        int parameters = method.getArgumentsCount();
                        if (node.getChildCount() != parameters) {
                            throw new RuntimeException("expected " + parameters + " argument(s), found " + node.getChildCount());
                        }
                        source.add(new Bytecode(context, new byte[] {
                            (byte) (push ? 0b00000000 : 0b00000001),
                        }, "ANCHOR"));
                        for (int i = 0; i < parameters; i++) {
                            source.addAll(compileBytecodeExpression(context, node.getChild(i), true));
                        }
                        int objectIndex = method.getObject();
                        if (objectIndex == 0) {
                            source.add(new Bytecode(context, new byte[] {
                                (byte) 0b00000101,
                                (byte) method.getOffset()
                            }, "CALL_SUB"));
                        }
                        else {
                            source.add(new Bytecode(context, new byte[] {
                                (byte) 0b00000110,
                                (byte) objectIndex,
                                (byte) method.getOffset()
                            }, "CALL_OBJ_SUB"));
                        }
                    }
                    else if (expression.isConstant()) {
                        source.add(new Constant(context, expression));
                    }
                    else {
                        throw new CompilerException("invalid operand " + node.getText(), node.getToken());
                    }
                }
            }
        } catch (CompilerException e) {
            logMessage(e);
        } catch (Exception e) {
            logMessage(new CompilerException(e, node.getToken()));
        }

        return source;
    }

    List<Spin1Bytecode> leftAssign(Spin1Context context, Spin1StatementNode node, boolean push) {
        List<Spin1Bytecode> source = new ArrayList<Spin1Bytecode>();

        String[] s = node.getText().split("[\\.]");
        if (s.length == 2 && ("BYTE".equalsIgnoreCase(s[1]) || "WORD".equalsIgnoreCase(s[1]) || "LONG".equalsIgnoreCase(s[1]))) {
            Spin1StatementNode postEffect = null;
            boolean indexed = false;

            Expression expression = context.getLocalSymbol(s[0]);
            if (expression == null) {
                throw new CompilerException("undefined symbol " + node.getText(), node.getToken());
            }

            if (node.getChildCount() != 0) {
                indexed = true;
                source.addAll(compileBytecodeExpression(context, node.getChild(0), true));
                if (node.getChildCount() > 1) {
                    if (isPostEffect(node.getChild(1))) {
                        postEffect = node.getChild(1);
                    }
                }
            }

            MemoryOp.Base bb = MemoryOp.Base.PBase;
            if (expression instanceof LocalVariable) {
                bb = MemoryOp.Base.DBase;
            }
            else if (expression instanceof Variable) {
                bb = MemoryOp.Base.VBase;
            }
            MemoryOp.Op op = push ? MemoryOp.Op.Assign : MemoryOp.Op.Write;
            if ("BYTE".equalsIgnoreCase(s[1])) {
                source.add(new MemoryOp(context, MemoryOp.Size.Byte, indexed, bb, op, expression));
            }
            else if ("WORD".equalsIgnoreCase(s[1])) {
                source.add(new MemoryOp(context, MemoryOp.Size.Word, indexed, bb, op, expression));
            }
            else if ("LONG".equalsIgnoreCase(s[1])) {
                source.add(new MemoryOp(context, MemoryOp.Size.Long, indexed, bb, op, expression));
            }

            if (postEffect != null) {
                if ("++".equals(postEffect.getText())) {
                    int code = 0b0_0101_11_0;
                    if (push) {
                        code |= 0b10000000;
                    }
                    source.add(new Bytecode(context, code, "POST_INC"));
                }
                else if ("--".equals(postEffect.getText())) {
                    int code = 0b0_0111_11_0;
                    if (push) {
                        code |= 0b10000000;
                    }
                    source.add(new Bytecode(context, code, "POST_DEC"));
                }
                else if ("~".equals(postEffect.getText())) {
                    int code = 0b0_00110_00;
                    if (push) {
                        code |= 0b10000000;
                    }
                    source.add(new Bytecode(context, code, "POST_CLEAR"));
                }
                else if ("~~".equals(postEffect.getText())) {
                    int code = 0b0_00111_00;
                    if (push) {
                        code |= 0b10000000;
                    }
                    source.add(new Bytecode(context, code, "POST_SET"));
                }
                else if ("?".equals(postEffect.getText())) {
                    int code = 0b0_00011_00;
                    if (push) {
                        code |= 0b10000000;
                    }
                    source.add(new Bytecode(context, code, "RANDOM_REVERSE"));
                }
            }
        }
        else if (node.getType() == Token.OPERATOR) {
            source.addAll(leftAssign(context, node.getChild(1), true));
            source.add(new Bytecode(context, 0x80, "WRITE"));
            source.addAll(leftAssign(context, node.getChild(0), node.getChild(0).getType() == Token.OPERATOR));
        }
        else if ("BYTE".equalsIgnoreCase(node.getText()) || "WORD".equalsIgnoreCase(node.getText()) || "LONG".equalsIgnoreCase(node.getText())) {
            source.addAll(compileBytecodeExpression(context, node.getChild(0), true));
            if (node.getChildCount() > 1) {
                source.addAll(compileBytecodeExpression(context, node.getChild(1), true));
            }

            MemoryOp.Op op = push ? MemoryOp.Op.Assign : MemoryOp.Op.Write;
            if ("BYTE".equalsIgnoreCase(node.getText())) {
                source.add(new MemoryOp(context, MemoryOp.Size.Byte, node.getChildCount() > 1, MemoryOp.Base.Pop, op, null));
            }
            else if ("WORD".equalsIgnoreCase(node.getText())) {
                source.add(new MemoryOp(context, MemoryOp.Size.Word, node.getChildCount() > 1, MemoryOp.Base.Pop, op, null));
            }
            else if ("LONG".equalsIgnoreCase(node.getText())) {
                source.add(new MemoryOp(context, MemoryOp.Size.Long, node.getChildCount() > 1, MemoryOp.Base.Pop, op, null));
            }
        }
        else {
            Expression expression = context.getLocalSymbol(node.getText());
            if (expression == null) {
                throw new CompilerException("undefined symbol " + node.getText(), node.getToken());
            }
            if (expression instanceof Register) {
                if (node.getChildCount() == 1) {
                    boolean range = "..".equals(node.getChild(0).getText());
                    source.addAll(compileBytecodeExpression(context, node.getChild(0), true));
                    source.add(new RegisterBitOp(context, push ? RegisterBitOp.Op.Assign : RegisterBitOp.Op.Write, range, expression.getNumber().intValue()));
                }
                else {
                    source.add(new RegisterOp(context, push ? RegisterOp.Op.Assign : RegisterOp.Op.Write, expression.getNumber().intValue()));
                }
            }
            else if (expression instanceof Variable) {
                boolean indexed = false;
                if (node.getChildCount() != 0) {
                    source.addAll(compileBytecodeExpression(context, node.getChild(0), true));
                    indexed = true;
                }
                source.add(new VariableOp(context, push ? VariableOp.Op.Assign : VariableOp.Op.Write, indexed, (Variable) expression));
            }
            else {
                MemoryOp.Size ss = MemoryOp.Size.Long;
                if (expression instanceof DataVariable) {
                    switch (((DataVariable) expression).getType()) {
                        case "BYTE":
                            ss = MemoryOp.Size.Byte;
                            break;
                        case "WORD":
                            ss = MemoryOp.Size.Word;
                            break;
                    }
                }

                boolean indexed = false;
                if (node.getChildCount() != 0) {
                    source.addAll(compileBytecodeExpression(context, node.getChild(0), true));
                    indexed = true;
                }
                source.add(new MemoryOp(context, ss, indexed, MemoryOp.Base.PBase, push ? MemoryOp.Op.Assign : MemoryOp.Op.Write, expression));
            }
        }

        return source;
    }

    int getArgumentsCount(Spin1StatementNode node) {
        int count = 0;

        for (Spin1StatementNode child : node.getChilds()) {
            if (",".equals(child.getText())) {
                count += getArgumentsCount(child);
            }
            else {
                count++;
            }
        }

        return count;
    }

    boolean isPostEffect(Spin1StatementNode node) {
        if (node.getChildCount() != 0) {
            return false;
        }
        String s = node.getText();
        return "++".equals(s) || "--".equals(s) || "~".equals(s) || "~~".equals(s) || "?".equals(s);
    }

    void determineClock() {
        Expression _clkmode = scope.getLocalSymbol("_CLKMODE");
        if (_clkmode == null) {
            _clkmode = scope.getLocalSymbol("_clkmode");
        }

        Expression _clkfreq = scope.getLocalSymbol("_CLKFREQ");
        if (_clkfreq == null) {
            _clkfreq = scope.getLocalSymbol("_clkfreq");
        }

        Expression _xinfreq = scope.getLocalSymbol("_XINFREQ");
        if (_xinfreq == null) {
            _xinfreq = scope.getLocalSymbol("_xinfreq");
        }

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

    public boolean hasErrors() {
        return errors;
    }

    public List<CompilerException> getMessages() {
        return messages;
    }

    Expression buildExpression(Node node, Spin1Context scope) {
        return buildExpression(node.getTokens(), scope);
    }

    Expression buildExpression(List<Token> tokens, Spin1Context scope) {
        return new Spin1ExpressionBuilder(scope, tokens).getExpression();
    }

}
