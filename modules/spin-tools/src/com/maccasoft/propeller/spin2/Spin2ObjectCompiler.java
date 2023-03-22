/*
 * Copyright (c) 2021-23 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.collections4.map.ListOrderedMap;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.ObjectCompiler;
import com.maccasoft.propeller.SpinObject.DataObject;
import com.maccasoft.propeller.SpinObject.LongDataObject;
import com.maccasoft.propeller.SpinObject.WordDataObject;
import com.maccasoft.propeller.expressions.ContextLiteral;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.LocalVariable;
import com.maccasoft.propeller.expressions.Method;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.expressions.Register;
import com.maccasoft.propeller.expressions.SpinObject;
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
import com.maccasoft.propeller.spin2.Spin2Bytecode.Descriptor;
import com.maccasoft.propeller.spin2.Spin2Compiler.ObjectInfo;
import com.maccasoft.propeller.spin2.Spin2Object.LinkDataObject;
import com.maccasoft.propeller.spin2.bytecode.Address;
import com.maccasoft.propeller.spin2.bytecode.Bytecode;
import com.maccasoft.propeller.spin2.bytecode.CaseFastJmp;
import com.maccasoft.propeller.spin2.bytecode.CaseJmp;
import com.maccasoft.propeller.spin2.bytecode.CaseRangeJmp;
import com.maccasoft.propeller.spin2.bytecode.Constant;
import com.maccasoft.propeller.spin2.bytecode.Djnz;
import com.maccasoft.propeller.spin2.bytecode.InlinePAsm;
import com.maccasoft.propeller.spin2.bytecode.Jmp;
import com.maccasoft.propeller.spin2.bytecode.Jnz;
import com.maccasoft.propeller.spin2.bytecode.Jz;
import com.maccasoft.propeller.spin2.bytecode.Tjz;
import com.maccasoft.propeller.spin2.bytecode.VariableOp;
import com.maccasoft.propeller.spin2.instructions.Empty;
import com.maccasoft.propeller.spin2.instructions.Fit;
import com.maccasoft.propeller.spin2.instructions.Org;
import com.maccasoft.propeller.spin2.instructions.Orgh;
import com.maccasoft.propeller.spin2.instructions.Res;
import com.maccasoft.propeller.spin2.instructions.Word;

public class Spin2ObjectCompiler extends ObjectCompiler {

    Spin2Context scope;

    List<Spin2PAsmLine> source = new ArrayList<Spin2PAsmLine>();
    List<Spin2Method> methods = new ArrayList<Spin2Method>();
    Map<String, ObjectInfo> objects = ListOrderedMap.listOrderedMap(new HashMap<String, ObjectInfo>());

    int varOffset = 4;
    int nested;

    boolean debugEnabled;
    Spin2Debug debug = new Spin2Debug();
    List<Object> debugStatements;

    boolean errors;
    List<CompilerException> messages = new ArrayList<CompilerException>();

    Map<String, Expression> publicSymbols = new HashMap<String, Expression>();
    List<LinkDataObject> objectLinks = new ArrayList<LinkDataObject>();
    List<LongDataObject> methodData = new ArrayList<LongDataObject>();

    Spin2Compiler compiler;
    Spin2BytecodeCompiler bytecodeCompiler;
    Spin2PasmCompiler pasmCompiler;

    public Spin2ObjectCompiler(Spin2Compiler compiler, List<Object> debugStatements) {
        this.scope = new Spin2GlobalContext(compiler.isCaseSensitive());
        this.compiler = compiler;
        this.debugEnabled = compiler.isDebugEnabled();
        this.debugStatements = debugStatements;
    }

    public void setDebugEnabled(boolean enabled) {
        this.debugEnabled = enabled;
    }

    public Spin2Object compileObject(Node root) {
        compile(root);
        compilePass2();
        return generateObject();
    }

    @Override
    public void compile(Node root) {
        bytecodeCompiler = new Spin2BytecodeCompiler(debugStatements) {

            @Override
            protected void logMessage(CompilerException message) {
                Spin2ObjectCompiler.this.logMessage(message);
            }

        };

        pasmCompiler = new Spin2PasmCompiler(scope, debugEnabled, debugStatements) {

            @Override
            protected List<Spin2PAsmLine> compileDatInclude(Spin2Context scope, Node root) {
                for (Node node : root.getChilds()) {
                    if (!(node instanceof ConstantsNode) && !(node instanceof DataNode)) {
                        throw new RuntimeException("only CON and DAT sections allowed in included files");
                    }
                }

                for (Node node : root.getChilds()) {
                    if (node instanceof ConstantsNode) {
                        compileConBlock((ConstantsNode) node);
                    }
                }

                return super.compileDatInclude(scope, root);
            }

            @Override
            protected Node getParsedSource(String fileName) {
                return Spin2ObjectCompiler.this.getParsedSource(fileName);
            }

            @Override
            protected byte[] getBinaryFile(String fileName) {
                return Spin2ObjectCompiler.this.getBinaryFile(fileName);
            }

            @Override
            protected void logMessage(CompilerException message) {
                Spin2ObjectCompiler.this.logMessage(message);
            }

        };

        for (Node node : root.getChilds()) {
            if (node instanceof ObjectsNode) {
                compileObjBlock(node);
            }
        }

        for (Node node : root.getChilds()) {
            if (node instanceof ConstantsNode) {
                compileConBlock((ConstantsNode) node);
            }
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

        while ((varOffset % 4) != 0) {
            varOffset++;
        }

        for (Node node : root.getChilds()) {
            if (node instanceof DataNode) {
                source.addAll(pasmCompiler.compileDat((DataNode) node));
            }
        }

        for (Entry<String, ObjectInfo> infoEntry : objects.entrySet()) {
            ObjectInfo info = infoEntry.getValue();
            String name = infoEntry.getKey();

            try {
                int count = info.count.getNumber().intValue();

                LinkDataObject linkData = new LinkDataObject(info.compiler, 0, varOffset);
                for (Entry<String, Expression> objEntry : info.compiler.getPublicSymbols().entrySet()) {
                    if (objEntry.getValue() instanceof Method) {
                        String qualifiedName = name + "." + objEntry.getKey();
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
                scope.addSymbol(name, new SpinObject(name, count) {

                    @Override
                    public int getIndex() {
                        return objectLinks.indexOf(linkData);
                    }

                });
                objectLinks.add(linkData);
                varOffset += info.compiler.getVarSize();

                for (int i = 1; i < count; i++) {
                    objectLinks.add(new LinkDataObject(info.compiler, 0, varOffset));
                    varOffset += info.compiler.getVarSize();
                }

            } catch (CompilerException e) {
                logMessage(e);
            } catch (Exception e) {
                logMessage(new CompilerException(e, info.count.getData()));
            }
        }

        for (Node node : root.getChilds()) {
            if ((node instanceof MethodNode) && "PUB".equalsIgnoreCase(((MethodNode) node).getType().getText())) {
                Spin2Method method = compileMethod((MethodNode) node);
                try {
                    Method exp = new Method(method.getLabel(), method.getParametersCount(), method.getReturnsCount()) {

                        @Override
                        public int getIndex() {
                            return objectLinks.size() * 2 + methods.indexOf(method);
                        }

                    };
                    exp.setData(method.getClass().getName(), method);

                    publicSymbols.put(method.getLabel(), exp);
                    scope.addSymbol(method.getLabel(), exp);
                    scope.addSymbol("@" + method.getLabel(), exp);

                    methods.add(method);

                } catch (Exception e) {
                    logMessage(new CompilerException(e.getMessage(), node));
                }
            }
        }
        for (Node node : root.getChilds()) {
            if ((node instanceof MethodNode) && "PRI".equalsIgnoreCase(((MethodNode) node).getType().getText())) {
                Spin2Method method = compileMethod((MethodNode) node);
                try {
                    Method exp = new Method(method.getLabel(), method.getParametersCount(), method.getReturnsCount()) {

                        @Override
                        public int getIndex() {
                            return objectLinks.size() * 2 + methods.indexOf(method);
                        }

                    };
                    exp.setData(method.getClass().getName(), method);

                    scope.addSymbol(method.getLabel(), exp);
                    scope.addSymbol("@" + method.getLabel(), exp);

                    methods.add(method);
                } catch (Exception e) {
                    logMessage(new CompilerException(e.getMessage(), node));
                }
            }
        }
        if (methods.size() != 0) {
            scope.addBuiltinSymbol("@CLKMODE", new NumberLiteral(0x40));
            scope.addBuiltinSymbol("@CLKFREQ", new NumberLiteral(0x44));
        }

        computeClockMode();

        for (Spin2Method method : methods) {
            for (Spin2MethodLine line : method.getLines()) {
                try {
                    compileLine(method, line, bytecodeCompiler);
                } catch (CompilerException e) {
                    logMessage(e);
                } catch (Exception e) {
                    logMessage(new CompilerException(e, line.getData()));
                }
            }
        }
    }

    @Override
    public void compilePass2() {

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
                            method.remove();
                            methodsIterator.remove();
                            loop = true;
                        }
                    }
                } while (loop);
            }

            Iterator<Spin2Method> methodsIterator = methods.iterator();
            while (methodsIterator.hasNext()) {
                Spin2Method method = methodsIterator.next();
                methodData.add(new LongDataObject(0, "Method " + method.getLabel()));
            }
            methodData.add(new LongDataObject(0, "End"));
        }
    }

    @Override
    public Map<String, Expression> getPublicSymbols() {
        return publicSymbols;
    }

    @Override
    public int getVarSize() {
        return varOffset;
    }

    @Override
    public List<LinkDataObject> getObjectLinks() {
        return objectLinks;
    }

    @Override
    public Spin2Object generateObject() {
        return generateObject(0);
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

        object.writeComment("Object header");

        for (LinkDataObject linkData : objectLinks) {
            object.write(linkData);
            object.writeLong(linkData.getVarOffset(), String.format("Variables @ $%05X", linkData.getVarOffset()));
        }
        for (LongDataObject data : methodData) {
            object.write(data);
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

        object.setVarSize(varOffset);

        return object;
    }

    public Spin2Object generateDebugData() {
        Spin2Object object = new Spin2Object();
        object.writeComment("Debug data");
        WordDataObject sizeWord = object.writeWord(2);

        int pos = (debugStatements.size() + 1) * 2;
        List<DataObject> l = new ArrayList<DataObject>();
        for (Object node : debugStatements) {
            try {
                if (node instanceof Spin2StatementNode) {
                    byte[] data = debug.compileDebugStatement((Spin2StatementNode) node);
                    l.add(new DataObject(data));
                    object.writeWord(pos);
                    pos += data.length;
                }
                else if (node instanceof Spin2PAsmDebugLine) {
                    byte[] data = debug.compilePAsmDebugStatement((Spin2PAsmDebugLine) node);
                    l.add(new DataObject(data));
                    object.writeWord(pos);
                    pos += data.length;
                }
            } catch (CompilerException e) {
                logMessage(e);
            } catch (Exception e) {
                logMessage(new CompilerException(e, node));
            }
        }
        for (DataObject data : l) {
            object.write(data);
        }
        sizeWord.setValue(object.getSize());

        if (object.getSize() > 16384) {
            throw new CompilerException("debug data is too long", null);
        }

        return object;
    }

    void compileConBlock(ConstantsNode parent) {
        parent.accept(new NodeVisitor() {
            int enumValue = 0, enumIncrement = 1;

            @Override
            public boolean visitConstant(ConstantNode node) {
                Iterator<Token> iter = node.getTokens().iterator();

                Token token = iter.next();
                do {
                    if ("#".equals(token.getText())) {
                        Spin2ExpressionBuilder builder = new Spin2ExpressionBuilder(scope);
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
                            builder = new Spin2ExpressionBuilder(scope);
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

                                Spin2ExpressionBuilder builder = new Spin2ExpressionBuilder(scope);
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
                                Spin2ExpressionBuilder builder = new Spin2ExpressionBuilder(scope);
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
                Iterator<Token> iter = node.getTokens().iterator();

                Token token = iter.next();
                if (Spin2Model.isType(token.getText())) {
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
                    if ("[".equals(token.getText())) {
                        if (!iter.hasNext()) {
                            logMessage(new CompilerException("expecting expression", token));
                            return;
                        }
                        Spin2ExpressionBuilder builder = new Spin2ExpressionBuilder(scope);
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
                    else {
                        Node error = new Node();
                        iter.forEachRemaining(t -> error.addToken(t));
                        logMessage(new CompilerException("unexpected '" + error + "'", error));
                    }
                }

                try {
                    scope.addSymbol(identifier, new Variable(type, identifier, size, varOffset));
                    scope.addSymbol("@" + identifier, new Variable(type, identifier, size, varOffset));
                    scope.addSymbol("@@" + identifier, new Variable(type, identifier, size, varOffset));

                    int varSize = size.getNumber().intValue();
                    if ("WORD".equalsIgnoreCase(type)) {
                        varSize = varSize * 2;
                    }
                    else if (!"BYTE".equalsIgnoreCase(type)) {
                        varSize = varSize * 4;
                    }
                    varOffset += varSize;
                } catch (Exception e) {
                    logMessage(new CompilerException(e, node.identifier));
                }

                if (iter.hasNext()) {
                    Node error = new Node();
                    iter.forEachRemaining(t -> error.addToken(t));
                    logMessage(new CompilerException("unexpected '" + error + "'", error));
                }
            }

        });
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
                    Spin2ExpressionBuilder builder = new Spin2ExpressionBuilder(scope);
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

                ObjectInfo info = compiler.getObjectInfo(fileName);
                if (info == null) {
                    logMessage(new CompilerException("object " + token + " not found", token));
                    return;
                }
                if (info.hasErrors()) {
                    logMessage(new CompilerException("object " + token + " has errors", token));
                    return;
                }

                objects.put(name, new ObjectInfo(info.compiler, count));

                for (Entry<String, Expression> entry : info.compiler.getPublicSymbols().entrySet()) {
                    if (!(entry.getValue() instanceof Method)) {
                        String qualifiedName = name + "." + entry.getKey();
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

    protected Node getParsedSource(String fileName) {
        return null;
    }

    protected byte[] getBinaryFile(String fileName) {
        return null;
    }

    Spin2Method compileMethod(MethodNode node) {
        Spin2Context localScope = new Spin2Context(scope);
        List<LocalVariable> parameters = new ArrayList<LocalVariable>();
        List<LocalVariable> returns = new ArrayList<LocalVariable>();
        List<LocalVariable> localVariables = new ArrayList<LocalVariable>();

        //print(node, 0);

        localScope.addBuiltinSymbol("RECV", new Register(0x1D2));
        localScope.addBuiltinSymbol("SEND", new Register(0x1D3));

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

        Spin2Method method = new Spin2Method(localScope, token.getText(), parameters, returns, localVariables);
        method.setComment(node.getText());
        method.setData(node);

        int offset = 0;
        for (MethodNode.ParameterNode child : node.getParameters()) {
            if (token.type == 0) {
                String identifier = child.getIdentifier().getText();
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
            else {
                logMessage(new CompilerException("expecting '('", token));
            }
        }
        else {
            logMessage(new CompilerException("expecting '('", token));
        }

        for (Node child : node.getReturnVariables()) {
            token = child.getToken(0);
            if (token.type == 0) {
                String identifier = child.getText();
                Expression expression = localScope.getLocalSymbol(identifier);
                if (expression instanceof LocalVariable) {
                    logMessage(new CompilerException("symbol '" + identifier + "' already defined", child));
                }
                else {
                    if (expression != null) {
                        logMessage(new CompilerException(CompilerException.WARNING, "return variable '" + identifier + "' hides global variable", child));
                    }
                    LocalVariable var = new LocalVariable("LONG", identifier, new NumberLiteral(1), offset);
                    localScope.addSymbol(identifier, var);
                    localScope.addSymbol("@" + identifier, var);
                    localScope.addSymbol("@@" + identifier, var);
                    returns.add(var);
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

        for (Node child : node.getLocalVariables()) {
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
                        Spin2ExpressionBuilder builder = new Spin2ExpressionBuilder(scope);
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
                    offset += count * varSize;
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

        List<Spin2MethodLine> childs = compileStatements(method, node.getChilds());
        childs.add(new Spin2MethodLine(localScope, "RETURN"));
        for (Spin2MethodLine line : childs) {
            method.addSource(line);
        }

        return method;
    }

    List<Spin2MethodLine> compileStatements(Spin2Method method, List<Node> childs) {
        Spin2Context context = method.getScope();
        List<Spin2MethodLine> lines = new ArrayList<Spin2MethodLine>();

        Iterator<Node> linesIterator = childs.iterator();
        while (linesIterator.hasNext()) {
            Node node = linesIterator.next();
            if (node instanceof StatementNode) {
                try {
                    Iterator<Token> iter = node.getTokens().iterator();
                    if (!iter.hasNext()) {
                        throw new RuntimeException("syntax error");
                    }

                    Token token = iter.next();
                    if ("ABORT".equalsIgnoreCase(token.getText())) {
                        Spin2MethodLine line = new Spin2MethodLine(context, token.getText(), node);

                        if (iter.hasNext()) {
                            Spin2TreeBuilder builder = new Spin2TreeBuilder();
                            while (iter.hasNext()) {
                                builder.addToken(iter.next());
                            }
                            line.addArgument(builder.getRoot());
                        }
                        if (iter.hasNext()) {
                            throw new CompilerException("unexpected argument", iter.next());
                        }

                        lines.add(line);
                    }
                    else if ("IF".equalsIgnoreCase(token.getText()) || "IFNOT".equalsIgnoreCase(token.getText())) {
                        if (!iter.hasNext()) {
                            throw new RuntimeException("expected expression");
                        }

                        Spin2MethodLine line = new Spin2MethodLine(context, token.getText(), node);

                        Spin2TreeBuilder builder = new Spin2TreeBuilder();
                        while (iter.hasNext()) {
                            builder.addToken(iter.next());
                        }
                        line.addArgument(builder.getRoot());

                        lines.add(line);

                        line.addChilds(compileStatements(method, node.getChilds()));

                        Spin2MethodLine falseLine = new Spin2MethodLine(context);
                        lines.add(falseLine);
                        line.setData("false", falseLine);

                        Spin2MethodLine exitLine = new Spin2MethodLine(context);
                        lines.add(exitLine);
                    }
                    else if ("ELSEIF".equalsIgnoreCase(token.getText()) || "ELSEIFNOT".equalsIgnoreCase(token.getText())) {
                        if (!iter.hasNext()) {
                            throw new RuntimeException("expected expression");
                        }

                        Spin2MethodLine line = new Spin2MethodLine(context, token.getText(), node);

                        Spin2TreeBuilder builder = new Spin2TreeBuilder();
                        while (iter.hasNext()) {
                            builder.addToken(iter.next());
                        }
                        line.addArgument(builder.getRoot());

                        Spin2MethodLine exitLine = lines.remove(lines.size() - 1);
                        lines.get(lines.size() - 2).setData("exit", exitLine);

                        lines.add(line);

                        line.addChilds(compileStatements(method, node.getChilds()));

                        Spin2MethodLine falseLine = new Spin2MethodLine(context);
                        lines.add(falseLine);
                        line.setData("false", falseLine);

                        lines.add(exitLine);
                    }
                    else if ("ELSE".equalsIgnoreCase(token.getText())) {
                        if (iter.hasNext()) {
                            throw new RuntimeException("syntax error");
                        }

                        Spin2MethodLine exitLine = lines.remove(lines.size() - 1);
                        lines.get(lines.size() - 2).setData("exit", exitLine);

                        Spin2MethodLine line = new Spin2MethodLine(context, token.getText(), node);
                        lines.add(line);

                        line.addChilds(compileStatements(method, node.getChilds()));

                        lines.add(exitLine);
                    }
                    else if ("REPEAT".equalsIgnoreCase(token.getText())) {
                        String text = token.getText();
                        Spin2MethodLine line = new Spin2MethodLine(context, text, node);

                        if (iter.hasNext()) {
                            token = iter.next();
                            if ("WHILE".equalsIgnoreCase(token.getText()) || "UNTIL".equalsIgnoreCase(token.getText())) {
                                text += " " + token.getText();
                                line.setStatement(text);
                                Spin2TreeBuilder builder = new Spin2TreeBuilder();
                                while (iter.hasNext()) {
                                    builder.addToken(iter.next());
                                }
                                line.addArgument(builder.getRoot());
                            }
                            else {
                                Spin2TreeBuilder builder = new Spin2TreeBuilder();
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
                                    builder = new Spin2TreeBuilder();
                                    while (iter.hasNext()) {
                                        token = iter.next();
                                        if ("TO".equalsIgnoreCase(token.getText())) {
                                            break;
                                        }
                                        builder.addToken(token);
                                    }
                                    line.addArgument(builder.getRoot());

                                    if (!"TO".equalsIgnoreCase(token.getText())) {
                                        throw new CompilerException("expected TO", token);
                                    }

                                    builder = new Spin2TreeBuilder();
                                    while (iter.hasNext()) {
                                        token = iter.next();
                                        if ("STEP".equalsIgnoreCase(token.getText())) {
                                            break;
                                        }
                                        builder.addToken(token);
                                    }
                                    line.addArgument(builder.getRoot());

                                    if ("STEP".equalsIgnoreCase(token.getText())) {
                                        builder = new Spin2TreeBuilder();
                                        while (iter.hasNext()) {
                                            builder.addToken(iter.next());
                                        }
                                        line.addArgument(builder.getRoot());
                                    }
                                }
                            }
                        }

                        lines.add(line);

                        Spin2MethodLine loopLine = line;
                        if (line.getArgumentsCount() == 1) {
                            loopLine = new Spin2MethodLine(context);
                            line.addChild(loopLine);
                        }

                        line.addChilds(compileStatements(method, node.getChilds()));

                        if (line.getArgumentsCount() == 3 || line.getArgumentsCount() == 4) {
                            loopLine = new Spin2MethodLine(context, "REPEAT-LOOP");
                            line.addChild(loopLine);
                        }
                        else {
                            line.addChild(new Spin2MethodLine(context, "NEXT"));
                        }

                        line.setData("next", loopLine);

                        Spin2MethodLine quitLine = new Spin2MethodLine(context);
                        lines.add(quitLine);
                        line.setData("quit", quitLine);
                    }
                    else if ("WHILE".equalsIgnoreCase(token.getText()) || "UNTIL".equalsIgnoreCase(token.getText())) {
                        if (!iter.hasNext()) {
                            throw new RuntimeException("expected expression");
                        }

                        Spin2MethodLine line = new Spin2MethodLine(context, token.getText(), node);

                        Spin2TreeBuilder builder = new Spin2TreeBuilder();
                        while (iter.hasNext()) {
                            builder.addToken(iter.next());
                        }
                        line.addArgument(builder.getRoot());

                        Spin2MethodLine repeatLine = lines.get(lines.size() - 2);
                        repeatLine.childs.remove(repeatLine.childs.size() - 1);

                        line.setData("true", repeatLine);
                        lines.add(lines.size() - 1, line);

                        repeatLine.setData("next", line);
                    }
                    else if ("RETURN".equalsIgnoreCase(token.getText())) {
                        Spin2MethodLine line = new Spin2MethodLine(context, token.getText(), node);

                        if (iter.hasNext()) {
                            Spin2TreeBuilder builder = new Spin2TreeBuilder();
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
                        Spin2MethodLine line = new Spin2MethodLine(context, token.getText(), node);
                        lines.add(line);
                    }
                    else if ("CASE".equalsIgnoreCase(token.getText())) {
                        Spin2MethodLine line = new Spin2MethodLine(context, token.getText(), node);

                        if (!iter.hasNext()) {
                            throw new RuntimeException("expected expression");
                        }
                        Spin2TreeBuilder builder = new Spin2TreeBuilder();
                        while (iter.hasNext()) {
                            builder.addToken(iter.next());
                        }
                        line.addArgument(builder.getRoot());

                        boolean hasOther = false;
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

                                Spin2MethodLine targetLine = new Spin2MethodLine(context);
                                targetLine.addChilds(compileStatements(method, child.getChilds()));
                                targetLine.addChild(new Spin2MethodLine(context, "CASE_DONE"));

                                Spin2StatementNode expression = builder.getRoot();
                                if ("OTHER".equalsIgnoreCase(expression.getText())) {
                                    line.addChild(0, targetLine);
                                    hasOther = true;
                                }
                                else {
                                    expression.setData("true", targetLine);
                                    line.addChild(targetLine);
                                    line.addArgument(expression);
                                }
                            }
                        }
                        if (!hasOther) {
                            line.childs.add(0, new Spin2MethodLine(context, "CASE_DONE"));
                        }

                        Spin2MethodLine doneLine = new Spin2MethodLine(context);
                        line.addChild(doneLine);
                        line.setData("end", doneLine);
                        lines.add(line);
                    }
                    else if ("CASE_FAST".equalsIgnoreCase(token.getText())) {
                        Spin2MethodLine line = new Spin2MethodLine(context, token.getText(), node);

                        if (!iter.hasNext()) {
                            throw new RuntimeException("expected expression");
                        }
                        Spin2TreeBuilder builder = new Spin2TreeBuilder();
                        while (iter.hasNext()) {
                            builder.addToken(iter.next());
                        }
                        line.addArgument(builder.getRoot());

                        Spin2MethodLine doneLine = null;
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

                                Spin2MethodLine targetLine = new Spin2MethodLine(context);
                                targetLine.addChilds(compileStatements(method, child.getChilds()));
                                targetLine.addChild(doneLine = new Spin2MethodLine(context, "CASE_FAST_DONE"));
                                targetLine.setData(child);
                                line.addChild(targetLine);

                                Spin2StatementNode expression = builder.getRoot();
                                if ("OTHER".equalsIgnoreCase(expression.getText())) {
                                    line.setData("other", doneLine = targetLine);
                                }
                                else {
                                    expression.setData("true", targetLine);
                                    line.addArgument(expression);
                                }
                            }
                        }
                        Spin2MethodLine endLine = new Spin2MethodLine(context);
                        line.addChild(endLine);
                        line.setData("end", endLine);
                        line.setData("done", doneLine);
                        lines.add(line);
                    }
                    else {
                        if (!debugEnabled && "DEBUG".equalsIgnoreCase(token.getText())) {
                            continue;
                        }

                        Spin2MethodLine line = new Spin2MethodLine(context, null, node);

                        Spin2TreeBuilder builder = new Spin2TreeBuilder();
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
            else if (node instanceof DataLineNode) {
                Spin2MethodLine line = new Spin2MethodLine(context, node.getStartToken().getText(), node);
                lines.add(line);

                Spin2Context savedContext = scope;
                try {
                    scope = line.getScope();
                    nested = 0;

                    int address = 0x1E0;
                    for (LocalVariable var : method.getLocalVariables()) {
                        scope.addSymbol(var.getName(), new NumberLiteral(address));
                        if (var.getSize() != null) {
                            address += var.getSize().getNumber().intValue();
                        }
                        else {
                            address += 1;
                        }
                        if (address >= 0x1F0) {
                            break;
                        }
                    }

                    Spin2PAsmLine pasmLine = pasmCompiler.compileDataLine(scope, (DataLineNode) node);
                    line.addSource(new InlinePAsm(line.getScope(), pasmLine));

                    compileInlinePAsmStatements(linesIterator, line);

                } finally {
                    scope = savedContext;
                }
            }
            else {
                lines.addAll(compileStatements(method, node.getChilds()));
            }
        }

        return lines;
    }

    void compileInlinePAsmStatements(Iterator<Node> linesIterator, Spin2MethodLine line) {
        Spin2Context localScope = new Spin2Context(line.getScope());

        while (linesIterator.hasNext()) {
            Node node = linesIterator.next();
            if (node instanceof DataLineNode) {
                Spin2PAsmLine pasmLine = pasmCompiler.compileDataLine(scope, (DataLineNode) node);
                line.addSource(new InlinePAsm(localScope, pasmLine));
            }
            else if (node instanceof StatementNode) {
                if ("END".equalsIgnoreCase(node.getStartToken().getText())) {
                    Spin2PAsmLine pasmLine = new Spin2PAsmLine(line.getScope(), null, null, "ret", Collections.emptyList(), null);
                    line.addSource(new InlinePAsm(localScope, pasmLine));
                    return;
                }
            }
        }
    }

    void compileLine(Spin2Method method, Spin2MethodLine line, Spin2BytecodeCompiler compiler) {
        String text = line.getStatement();

        if ("ABORT".equalsIgnoreCase(text)) {
            if (line.getArgumentsCount() == 0) {
                line.addSource(new Bytecode(line.getScope(), 0x06, text.toUpperCase()));
            }
            else if (line.getArgumentsCount() == 1) {
                line.addSource(compiler.compileBytecodeExpression(line.getScope(), method, line.getArgument(0), true));
                line.addSource(new Bytecode(line.getScope(), 0x07, text.toUpperCase()));
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
            line.addSource(compiler.compileConstantExpression(line.getScope(), method, line.getArgument(0)));
            Spin2MethodLine target = (Spin2MethodLine) line.getData("false");
            line.addSource(new Jz(line.getScope(), new ContextLiteral(target.getScope())));
        }
        else if ("IFNOT".equalsIgnoreCase(text) || "ELSEIFNOT".equalsIgnoreCase(text)) {
            if (line.getArgumentsCount() == 0) {
                throw new RuntimeException("expected expression");
            }
            if (line.getArgumentsCount() != 1) {
                throw new RuntimeException("syntax error");
            }
            line.addSource(compiler.compileBytecodeExpression(line.getScope(), method, line.getArgument(0), true));
            Spin2MethodLine target = (Spin2MethodLine) line.getData("false");
            line.addSource(new Jnz(line.getScope(), new ContextLiteral(target.getScope())));
        }
        else if ("ELSE".equalsIgnoreCase(text)) {

        }
        else if ("REPEAT".equalsIgnoreCase(text)) {
            if (line.getArgumentsCount() == 1) {
                try {
                    Expression expression = compiler.buildConstantExpression(line.getScope(), line.getArgument(0));
                    line.addSource(new Constant(line.getScope(), expression));
                } catch (Exception e) {
                    line.addSource(compiler.compileBytecodeExpression(line.getScope(), method, line.getArgument(0), true));
                    Spin2MethodLine target = (Spin2MethodLine) line.getData("quit");
                    line.addSource(new Tjz(line.getScope(), new ContextLiteral(target.getScope())));
                }
                line.setData("pop", Integer.valueOf(4));
            }
            else if (line.getArgumentsCount() == 3 || line.getArgumentsCount() == 4) {
                Spin2MethodLine end = line.getChilds().get(0);
                line.addSource(new Address(line.getScope(), new ContextLiteral(end.getScope())));

                line.addSource(compiler.compileConstantExpression(line.getScope(), method, line.getArgument(2)));
                if (line.getArgumentsCount() == 4) {
                    line.addSource(compiler.compileConstantExpression(line.getScope(), method, line.getArgument(3)));
                }
                line.addSource(compiler.compileConstantExpression(line.getScope(), method, line.getArgument(1)));
                line.setData("pop", Integer.valueOf(16));

                String varText = line.getArgument(0).getText();
                Expression expression = line.getScope().getLocalSymbol(varText);
                if (expression == null) {
                    throw new CompilerException("undefined symbol " + varText, line.getArgument(0).getToken());
                }
                else if ((expression instanceof Variable) || (expression instanceof LocalVariable)) {
                    line.addSource(new VariableOp(line.getScope(), VariableOp.Op.Setup, false, (Variable) expression));
                    line.addSource(new Bytecode(line.getScope(), line.getArgumentsCount() == 4 ? 0x7C : 0x7B, "REPEAT"));
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
            line.addSource(compiler.compileBytecodeExpression(line.getScope(), method, line.getArgument(0), true));
            Spin2MethodLine target = (Spin2MethodLine) line.getData("quit");
            line.addSource(new Jz(line.getScope(), new ContextLiteral(target.getScope())));
        }
        else if ("REPEAT UNTIL".equalsIgnoreCase(text)) {
            if (line.getArgumentsCount() == 0) {
                throw new RuntimeException("expected expression");
            }
            if (line.getArgumentsCount() != 1) {
                throw new RuntimeException("syntax error");
            }
            line.addSource(compiler.compileBytecodeExpression(line.getScope(), method, line.getArgument(0), true));
            Spin2MethodLine target = (Spin2MethodLine) line.getData("quit");
            line.addSource(new Jnz(line.getScope(), new ContextLiteral(target.getScope())));
        }
        else if ("WHILE".equalsIgnoreCase(text)) {
            if (line.getArgumentsCount() == 0) {
                throw new RuntimeException("expected expression");
            }
            if (line.getArgumentsCount() != 1) {
                throw new RuntimeException("syntax error");
            }
            line.addSource(compiler.compileBytecodeExpression(line.getScope(), method, line.getArgument(0), true));
            Spin2MethodLine target = (Spin2MethodLine) line.getData("true");
            line.addSource(new Jnz(line.getScope(), new ContextLiteral(target.getScope())));
        }
        else if ("UNTIL".equalsIgnoreCase(text)) {
            if (line.getArgumentsCount() == 0) {
                throw new RuntimeException("expected expression");
            }
            if (line.getArgumentsCount() != 1) {
                throw new RuntimeException("syntax error");
            }
            line.addSource(compiler.compileBytecodeExpression(line.getScope(), method, line.getArgument(0), true));
            Spin2MethodLine target = (Spin2MethodLine) line.getData("true");
            line.addSource(new Jz(line.getScope(), new ContextLiteral(target.getScope())));
        }
        else if ("NEXT".equalsIgnoreCase(text)) {
            Spin2MethodLine repeat = line.getParent();
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

            Spin2MethodLine target = (Spin2MethodLine) repeat.getData("next");

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
            Spin2MethodLine repeat = line.getParent();
            while (repeat != null) {
                if ("REPEAT".equalsIgnoreCase(repeat.getStatement())) {
                    break;
                }
                repeat = repeat.getParent();
            }

            String varText = repeat.getArgument(0).getText();
            Expression expression = line.getScope().getLocalSymbol(varText);
            if (expression == null) {
                throw new CompilerException("undefined symbol " + varText, repeat.getArgument(0).getToken());
            }
            else if ((expression instanceof Variable) || (expression instanceof LocalVariable)) {
                line.addSource(new VariableOp(line.getScope(), VariableOp.Op.Setup, false, (Variable) expression));
                line.addSource(new Bytecode(line.getScope(), 0x7D, "REPEAT_LOOP"));
            }
            else {
                throw new RuntimeException("unsupported " + line.getArgument(0));
            }
        }
        else if ("QUIT".equalsIgnoreCase(text)) {
            int pop = 0;
            boolean hasCase = false;

            Spin2MethodLine repeat = line.getParent();
            while (repeat != null) {
                if ("CASE".equalsIgnoreCase(repeat.getStatement())) {
                    pop += 8;
                    hasCase = true;
                }
                if ("CASE_FAST".equalsIgnoreCase(repeat.getStatement())) {
                    pop += 4;
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
                Spin2MethodLine target = (Spin2MethodLine) repeat.getData("quit");
                line.addSource(new Jnz(line.getScope(), new ContextLiteral(target.getScope())));
            }
            else {
                if (pop != 0) {
                    try {
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        if (pop == 4) {
                            os.write(0x17);
                        }
                        else {
                            os.write(0x18);
                            os.write(Constant.wrVars(pop - 4));
                        }
                        line.addSource(new Bytecode(line.getScope(), os.toByteArray(), "POP"));
                    } catch (Exception e) {
                        // Do nothing
                    }
                }
                Spin2MethodLine target = (Spin2MethodLine) repeat.getData("quit");
                line.addSource(new Jmp(line.getScope(), new ContextLiteral(target.getScope())));
            }
        }
        else if ("RETURN".equalsIgnoreCase(text)) {
            if (line.getArgumentsCount() == 0) {
                line.addSource(new Bytecode(line.getScope(), 0x04, text));
            }
            else {
                for (Spin2StatementNode arg : line.getArguments()) {
                    line.addSource(compiler.compileBytecodeExpression(line.getScope(), method, arg, true));
                }
                line.addSource(new Bytecode(line.getScope(), 0x05, text));
            }
        }
        else if ("CASE".equalsIgnoreCase(text)) {
            Spin2MethodLine end = (Spin2MethodLine) line.getData("end");
            line.addSource(new Address(line.getScope(), new ContextLiteral(end.getScope())));

            Iterator<Spin2StatementNode> iter = line.getArguments().iterator();
            line.addSource(compiler.compileBytecodeExpression(line.getScope(), method, iter.next(), true));

            while (iter.hasNext()) {
                Spin2StatementNode arg = iter.next();
                Spin2MethodLine target = (Spin2MethodLine) arg.getData("true");
                compileCase(method, line, arg, target, compiler);
            }
        }
        else if ("CASE_FAST".equalsIgnoreCase(text)) {
            Spin2MethodLine end = (Spin2MethodLine) line.getData("end");
            line.addSource(new Address(line.getScope(), new ContextLiteral(end.getScope())));

            Iterator<Spin2StatementNode> iter = line.getArguments().iterator();
            line.addSource(compiler.compileBytecodeExpression(line.getScope(), method, iter.next(), true));
            line.addSource(new Bytecode(line.getScope(), 0x1A, "CASE_FAST"));

            Map<Integer, Spin2MethodLine> map = new TreeMap<Integer, Spin2MethodLine>();

            while (iter.hasNext()) {
                Spin2StatementNode arg = iter.next();
                Spin2MethodLine target = (Spin2MethodLine) arg.getData("true");
                for (Entry<Integer, Spin2MethodLine> entry : compileCaseFast(line, arg, target, compiler).entrySet()) {
                    if (map.containsKey(entry.getKey())) {
                        throw new CompilerException("index is not unique", target.getData());
                    }
                    map.put(entry.getKey(), entry.getValue());
                }
            }
            if (map.size() == 0) {
                throw new CompilerException("no cases", line.getData());
            }

            Spin2MethodLine done = (Spin2MethodLine) line.getData("done");

            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;
            for (Entry<Integer, Spin2MethodLine> entry : map.entrySet()) {
                min = Math.min(min, entry.getKey().intValue());
                max = Math.max(max, entry.getKey().intValue());
                if ((max - min) > 255) {
                    throw new CompilerException("values must be within 255 of each other", entry.getValue().getData());
                }
            }

            line.addSource(new Bytecode(line.getScope(), Constant.wrLong(min), String.format("FROM %d", min)));
            line.addSource(new Bytecode(line.getScope(), Constant.wrWord(max - min + 1), String.format("TO %d", max)));
            Spin2Context ref = line.getSource().get(line.getSource().size() - 1).getContext();

            int index = min;
            for (Entry<Integer, Spin2MethodLine> entry : map.entrySet()) {
                int target = entry.getKey().intValue();
                while (index < target) {
                    line.addSource(new CaseFastJmp(ref, new ContextLiteral(done.getScope())));
                    index++;
                }
                line.addSource(new CaseFastJmp(ref, new ContextLiteral(entry.getValue().getScope())));
                index++;
            }
            Spin2MethodLine other = (Spin2MethodLine) line.getData("other");
            if (other != null) {
                line.addSource(new CaseFastJmp(ref, new ContextLiteral(other.getScope())));
            }
            else {
                line.addSource(new CaseFastJmp(ref, new ContextLiteral(done.getScope())));
            }
        }
        else if ("CASE_DONE".equalsIgnoreCase(text)) {
            line.addSource(new Bytecode(line.getScope(), 0x1E, text));
        }
        else if ("CASE_FAST_DONE".equalsIgnoreCase(text)) {
            line.addSource(new Bytecode(line.getScope(), 0x1B, text));
        }
        else if ("ORG".equalsIgnoreCase(text)) {
            int org = 0;

            int count = 0;
            for (Spin2Bytecode bc : line.getSource()) {
                if (bc instanceof InlinePAsm) {
                    Spin2PAsmLine pasmLine = ((InlinePAsm) bc).getLine();
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
                }
            }

            count--;
            line.source.add(0, new Bytecode(line.getScope(), new byte[] {
                (byte) org,
                (byte) (org >> 8),
                (byte) count,
                (byte) (count >> 8),
            }, String.format("ORG=$%03x, %d", org, count + 1)));
            line.source.add(0, new Bytecode(line.getScope(), new byte[] {
                0x19, 0x5E
            }, "INLINE-EXEC"));
        }
        else if (text != null) {
            Descriptor desc = Spin2Bytecode.getDescriptor(text);
            if (desc != null) {
                if (line.getArgumentsCount() != desc.parameters) {
                    throw new CompilerException("expected " + desc.parameters + " argument(s), found " + line.getArgumentsCount(), line.getData());
                }
                for (Spin2StatementNode arg : line.getArguments()) {
                    List<Spin2Bytecode> list = compiler.compileBytecodeExpression(line.getScope(), method, arg, true);
                    line.addSource(list);
                }
                line.addSource(new Bytecode(line.getScope(), desc.code, text));
            }
            else {
                //throw new RuntimeException("unknown " + text);
            }
        }
        else {
            for (Spin2StatementNode arg : line.getArguments()) {
                try {
                    line.addSource(compiler.compileBytecodeExpression(line.getScope(), method, arg, false));
                } catch (CompilerException e) {
                    logMessage(e);
                } catch (Exception e) {
                    logMessage(new CompilerException(e, arg.getData()));
                }
            }
        }

        Spin2MethodLine target = (Spin2MethodLine) line.getData("exit");
        if (target != null) {
            Spin2MethodLine newLine = new Spin2MethodLine(line.getScope());
            newLine.addSource(new Jmp(line.getScope(), new ContextLiteral(target.getScope())));
            line.addChild(newLine);
        }

        for (Spin2MethodLine child : line.getChilds()) {
            try {
                compileLine(method, child, compiler);
            } catch (CompilerException e) {
                logMessage(e);
            } catch (Exception e) {
                logMessage(new CompilerException(e, child.getData()));
            }
        }
    }

    void compileCase(Spin2Method method, Spin2MethodLine line, Spin2StatementNode arg, Spin2MethodLine target, Spin2BytecodeCompiler compiler) {
        if (",".equals(arg.getText())) {
            for (Spin2StatementNode child : arg.getChilds()) {
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

    Map<Integer, Spin2MethodLine> compileCaseFast(Spin2MethodLine line, Spin2StatementNode arg, Spin2MethodLine target, Spin2BytecodeCompiler compiler) {
        Map<Integer, Spin2MethodLine> map = new TreeMap<Integer, Spin2MethodLine>();
        if (",".equals(arg.getText())) {
            for (Spin2StatementNode child : arg.getChilds()) {
                map.putAll(compileCaseFast(line, child, target, compiler));
            }
        }
        else if ("..".equals(arg.getText())) {
            try {
                Expression expression = compiler.buildConstantExpression(line.getScope(), arg.getChild(0));
                if (!expression.isConstant()) {
                    throw new CompilerException("expression is not constant", arg.getChild(0));
                }
                int value = expression.getNumber().intValue();
                map.put(value, target);
            } catch (Exception e) {
                throw new CompilerException(e, arg);
            }
            try {
                Expression expression = compiler.buildConstantExpression(line.getScope(), arg.getChild(1));
                if (!expression.isConstant()) {
                    throw new CompilerException("expression is not constant", arg.getChild(1));
                }
                int value = expression.getNumber().intValue();
                map.put(value, target);
            } catch (Exception e) {
                throw new CompilerException(e, arg);
            }
        }
        else {
            try {
                Expression expression = compiler.buildConstantExpression(line.getScope(), arg);
                if (!expression.isConstant()) {
                    throw new CompilerException("expression is not constant", arg);
                }
                int value = expression.getNumber().intValue();
                map.put(value, target);
            } catch (Exception e) {
                throw new CompilerException(e, arg);
            }
        }
        return map;
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
            if (methodData.size() != 0) {
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

    public List<Spin2PAsmLine> getSource() {
        return source;
    }

    public Spin2Context getScope() {
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

    @Override
    public boolean hasErrors() {
        return errors;
    }

    public List<CompilerException> getMessages() {
        return messages;
    }

}
