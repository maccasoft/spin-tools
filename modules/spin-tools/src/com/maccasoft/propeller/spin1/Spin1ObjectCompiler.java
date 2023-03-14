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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.map.ListOrderedMap;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.SpinObject.LongDataObject;
import com.maccasoft.propeller.SpinObject.WordDataObject;
import com.maccasoft.propeller.expressions.ContextLiteral;
import com.maccasoft.propeller.expressions.DataVariable;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.LocalVariable;
import com.maccasoft.propeller.expressions.Method;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.expressions.ObjectContextLiteral;
import com.maccasoft.propeller.expressions.Variable;
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
import com.maccasoft.propeller.spin1.bytecode.RepeatLoop;
import com.maccasoft.propeller.spin1.bytecode.Tjz;
import com.maccasoft.propeller.spin1.bytecode.VariableOp;
import com.maccasoft.propeller.spin1.instructions.FileInc;
import com.maccasoft.propeller.spin1.instructions.Org;
import com.maccasoft.propeller.spin1.instructions.Res;
import com.maccasoft.propeller.spin2.Spin2Model;

public class Spin1ObjectCompiler {

    public static class ObjectInfo {
        String fileName;
        Spin1ObjectCompiler compiler;

        long offset;
        Expression count;

        public ObjectInfo(String fileName, Spin1ObjectCompiler compiler) {
            this.fileName = fileName;
            this.compiler = compiler;
        }

        public ObjectInfo(String fileName, Spin1ObjectCompiler compiler, Expression count) {
            this.fileName = fileName;
            this.compiler = compiler;
            this.count = count;
        }

        public boolean hasErrors() {
            return compiler.hasErrors();
        }

    }

    class BytecodeCompiler extends Spin1BytecodeCompiler {

        public BytecodeCompiler(Map<String, ObjectInfo> objects, boolean openspinCompatible) {
            super(objects, openspinCompatible);
        }

        @Override
        protected void logMessage(CompilerException message) {
            Spin1ObjectCompiler.this.logMessage(message);
        }

    }

    final Spin1Context scope;
    final Map<String, ObjectInfo> childObjects;

    List<Spin1PAsmLine> source = new ArrayList<Spin1PAsmLine>();
    List<Spin1Method> methods = new ArrayList<Spin1Method>();
    Map<String, ObjectInfo> objects = ListOrderedMap.listOrderedMap(new HashMap<String, ObjectInfo>());

    boolean openspinCompatible;

    int dcurr = 0;
    int varOffset = 0;
    int objectVarSize = 0;

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

    public boolean getOpenspinCompatibile() {
        return openspinCompatible;
    }

    public void setOpenspinCompatibile(boolean openspinCompatibile) {
        this.openspinCompatible = openspinCompatibile;
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
            } catch (CompilerException e) {
                logMessage(e);
                iter.remove();
            } catch (Exception e) {
                logMessage(new CompilerException(e, entry.getValue().getData()));
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
            try {
                int count = info.count.getNumber().intValue();
                for (int i = 0; i < count; i++) {
                    objectLinks.add(new LinkDataObject(info, 0, varOffset));
                    varOffset += info.compiler.getVarSize();
                    objectIndex++;
                }
            } catch (CompilerException e) {
                logMessage(e);
            } catch (Exception e) {
                logMessage(new CompilerException(e, info.count.getData()));
            }
        }

        for (Spin1Method method : methods) {
            Spin1BytecodeCompiler compiler = new BytecodeCompiler(objects, openspinCompatible);
            List<Spin1MethodLine> lines = method.getLines();
            for (Spin1MethodLine line : lines) {
                try {
                    compileLine(line, compiler);
                } catch (CompilerException e) {
                    logMessage(e);
                } catch (Exception e) {
                    logMessage(new CompilerException(e, line.getData()));
                }
            }
            Spin1MethodLine dataLine = lines.get(lines.size() - 1);
            dataLine.addSource(compiler.getStringData());
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
                Expression count = new NumberLiteral(1);

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
                                count = builder.getExpression();
                                count.setData(node.count);
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
                String fileName = token.getText().substring(1, token.getText().length() - 1);

                ObjectInfo info = childObjects.get(fileName);
                if (info == null) {
                    info = childObjects.get(fileName + ".spin");
                }
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
                            Spin1ExpressionBuilder builder = new Spin1ExpressionBuilder(pasmLine.getScope(), param.getTokens().subList(index, param.getTokens().size()));
                            expression = builder.getExpression();
                            expression.setData(param);
                        } catch (Exception e) {
                            throw new CompilerException(e, param);
                        }
                    }

                    if (param.count != null) {
                        try {
                            Spin1ExpressionBuilder builder = new Spin1ExpressionBuilder(pasmLine.getScope(), param.count.getTokens());
                            count = builder.getExpression();
                        } catch (Exception e) {
                            throw new CompilerException(e, param.count);
                        }
                    }
                    parameters.add(new Spin1PAsmExpression(prefix, expression, count));
                }

                try {
                    if ("FILE".equalsIgnoreCase(mnemonic)) {
                        if (node.condition != null) {
                            throw new CompilerException("not allowed", node.condition);
                        }
                        if (node.modifier != null) {
                            throw new CompilerException("not allowed", node.modifier);
                        }
                        String fileName = parameters.get(0).getString();
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
                        scope.addSymbol("@" + pasmLine.getLabel(), new ObjectContextLiteral(pasmLine.getScope(), type));
                        scope.addSymbol("@@" + pasmLine.getLabel(), new ObjectContextLiteral(pasmLine.getScope(), type));

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
                                context.addOrUpdateSymbol("@" + line.getLabel(), new ObjectContextLiteral(line.getScope(), type));
                                context.addOrUpdateSymbol("@@" + line.getLabel(), new ObjectContextLiteral(line.getScope(), type));
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
                        context.addOrUpdateSymbol("@" + line.getLabel(), new ObjectContextLiteral(line.getScope(), type));
                        context.addOrUpdateSymbol("@@" + line.getLabel(), new ObjectContextLiteral(line.getScope(), type));
                    }
                    pendingAlias.clear();
                }

                source.addAll(pasmLine.expand());

                if ("INCLUDE".equalsIgnoreCase(pasmLine.getMnemonic())) {
                    if (node.condition != null) {
                        throw new CompilerException("not allowed", node.condition);
                    }
                    if (node.modifier != null) {
                        throw new CompilerException("not allowed", node.modifier);
                    }
                    int index = 0;
                    for (Spin1PAsmExpression argument : pasmLine.getArguments()) {
                        String fileName = argument.getString();
                        Node includedNode = getParsedSource(fileName);
                        try {
                            if (includedNode == null) {
                                throw new RuntimeException("file \"" + fileName + "\" not found");
                            }
                            compileDatInclude(scope, includedNode);
                        } catch (Exception e) {
                            logMessage(new CompilerException(e, node.parameters.get(index)));
                        }
                        index++;
                    }
                }
            } catch (CompilerException e) {
                logMessage(e);
            } catch (Exception e) {
                logMessage(new CompilerException(e, node.instruction));
            }
        }
    }

    void compileDatInclude(Spin1Context scope, Node root) {
        for (Node node : root.getChilds()) {
            if (!(node instanceof ConstantsNode) && !(node instanceof DataNode)) {
                throw new RuntimeException("only CON and DAT sections allowed in included files");
            }
        }

        for (Node node : root.getChilds()) {
            if (node instanceof ConstantsNode) {
                compileConBlock(node);
            }
        }

        for (Node node : root.getChilds()) {
            if (node instanceof DataNode) {
                compileDatBlock(scope, node);
            }
        }
    }

    protected Node getParsedSource(String fileName) {
        return null;
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
        localScope.addBuiltinSymbol(defaultReturn.getName(), defaultReturn);
        localScope.addBuiltinSymbol("@" + defaultReturn.getName(), defaultReturn);
        localScope.addBuiltinSymbol("@@" + defaultReturn.getName(), defaultReturn);
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

    void compileLine(Spin1MethodLine line, Spin1BytecodeCompiler compiler) {
        String text = line.getStatement();

        if ("ABORT".equalsIgnoreCase(text)) {
            if (line.getArgumentsCount() == 0) {
                line.addSource(new Bytecode(line.getScope(), 0b00110000, text));
            }
            else if (line.getArgumentsCount() == 1) {
                line.addSource(compiler.compileBytecodeExpression(line.getScope(), line.getArgument(0), true));
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
            line.addSource(compiler.compileBytecodeExpression(line.getScope(), line.getArgument(0), true));
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
            line.addSource(compiler.compileBytecodeExpression(line.getScope(), line.getArgument(0), true));
            Spin1MethodLine target = (Spin1MethodLine) line.getData("false");
            line.addSource(new Jnz(line.getScope(), new ContextLiteral(target.getScope())));
        }
        else if ("ELSE".equalsIgnoreCase(text)) {

        }
        else if ("REPEAT".equalsIgnoreCase(text)) {
            if (line.getArgumentsCount() == 1) {
                line.addSource(compiler.compileConstantExpression(line.getScope(), line.getArgument(0)));
                Spin1MethodLine target = (Spin1MethodLine) line.getData("quit");
                line.addSource(new Tjz(line.getScope(), new ContextLiteral(target.getScope())));
                line.setData("pop", Integer.valueOf(4));
            }
            else if (line.getArgumentsCount() == 3 || line.getArgumentsCount() == 4) {
                line.addSource(compiler.compileConstantExpression(line.getScope(), line.getArgument(1)));

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
            line.addSource(compiler.compileBytecodeExpression(line.getScope(), line.getArgument(0), true));
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
            line.addSource(compiler.compileBytecodeExpression(line.getScope(), line.getArgument(0), true));
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
            line.addSource(compiler.compileBytecodeExpression(line.getScope(), line.getArgument(0), true));
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
            line.addSource(compiler.compileBytecodeExpression(line.getScope(), line.getArgument(0), true));
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
                    line.addSource(compiler.compileConstantExpression(line.getScope(), repeat.getArgument(3)));
                }
                if (openspinCompatible) {
                    line.addSource(compiler.compileBytecodeExpression(line.getScope(), repeat.getArgument(1), true));
                    line.addSource(compiler.compileBytecodeExpression(line.getScope(), repeat.getArgument(2), true));
                }
                else {
                    line.addSource(compiler.compileConstantExpression(line.getScope(), repeat.getArgument(1)));
                    line.addSource(compiler.compileConstantExpression(line.getScope(), repeat.getArgument(2)));
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
                        os.write(new Constant(line.getScope(), new NumberLiteral(pop), openspinCompatible).getBytes());
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
                line.addSource(compiler.compileBytecodeExpression(line.getScope(), line.getArgument(0), true));
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
                                line.addSource(compiler.compileBytecodeExpression(line.getScope(), child.getChild(0), false));
                                line.addSource(compiler.compileBytecodeExpression(line.getScope(), child.getChild(1), false));
                                line.addSource(new CaseRangeJmp(line.getScope(), new ContextLiteral(target.getScope())));
                            }
                            else {
                                line.addSource(compiler.compileBytecodeExpression(line.getScope(), child, false));
                                line.addSource(new CaseJmp(line.getScope(), new ContextLiteral(target.getScope())));
                            }
                        }
                    }
                    else if ("..".equals(arg.getText())) {
                        line.addSource(compiler.compileBytecodeExpression(line.getScope(), arg.getChild(0), false));
                        line.addSource(compiler.compileBytecodeExpression(line.getScope(), arg.getChild(1), false));
                        line.addSource(new CaseRangeJmp(line.getScope(), new ContextLiteral(target.getScope())));
                    }
                    else {
                        line.addSource(compiler.compileBytecodeExpression(line.getScope(), arg, false));
                        line.addSource(new CaseJmp(line.getScope(), new ContextLiteral(target.getScope())));
                    }
                }
                else {
                    line.addSource(compiler.compileBytecodeExpression(line.getScope(), arg, true));
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
                    List<Spin1Bytecode> list = compiler.compileBytecodeExpression(line.getScope(), arg, true);
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
                    line.addSource(compiler.compileBytecodeExpression(line.getScope(), arg, false));
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
                compileLine(child, compiler);
            } catch (CompilerException e) {
                logMessage(e);
            } catch (Exception e) {
                logMessage(new CompilerException(e, child.getData()));
            }
        }
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

}
