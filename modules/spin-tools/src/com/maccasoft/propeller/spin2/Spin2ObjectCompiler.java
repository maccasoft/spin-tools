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
import java.util.Stack;
import java.util.TreeMap;

import org.apache.commons.collections4.map.ListOrderedMap;

import com.maccasoft.propeller.Compiler.ObjectInfo;
import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.ObjectCompiler;
import com.maccasoft.propeller.SpinObject.DataObject;
import com.maccasoft.propeller.SpinObject.LinkDataObject;
import com.maccasoft.propeller.SpinObject.LongDataObject;
import com.maccasoft.propeller.SpinObject.WordDataObject;
import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.ContextLiteral;
import com.maccasoft.propeller.expressions.DataVariable;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.Identifier;
import com.maccasoft.propeller.expressions.LocalVariable;
import com.maccasoft.propeller.expressions.MemoryContextLiteral;
import com.maccasoft.propeller.expressions.Method;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.expressions.ObjectContextLiteral;
import com.maccasoft.propeller.expressions.Register;
import com.maccasoft.propeller.expressions.RegisterAddress;
import com.maccasoft.propeller.expressions.SpinObject;
import com.maccasoft.propeller.expressions.Variable;
import com.maccasoft.propeller.model.ConstantNode;
import com.maccasoft.propeller.model.ConstantsNode;
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.DataNode;
import com.maccasoft.propeller.model.DirectiveNode;
import com.maccasoft.propeller.model.ErrorNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.MethodNode.ReturnNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.ObjectNode;
import com.maccasoft.propeller.model.ObjectsNode;
import com.maccasoft.propeller.model.StatementNode;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.VariableNode;
import com.maccasoft.propeller.model.VariablesNode;
import com.maccasoft.propeller.spin2.Spin2Object.Spin2LinkDataObject;
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
import com.maccasoft.propeller.spin2.instructions.FileInc;
import com.maccasoft.propeller.spin2.instructions.Fit;
import com.maccasoft.propeller.spin2.instructions.Org;
import com.maccasoft.propeller.spin2.instructions.Orgh;
import com.maccasoft.propeller.spin2.instructions.Res;
import com.maccasoft.propeller.spin2.instructions.Word;

public class Spin2ObjectCompiler extends ObjectCompiler {

    Context scope;

    List<Variable> variables = new ArrayList<>();
    List<Spin2PAsmLine> source = new ArrayList<>();
    List<Spin2Method> methods = new ArrayList<>();
    Map<String, ObjectInfo> objects = ListOrderedMap.listOrderedMap(new HashMap<>());

    int objectVarSize;

    int nested;

    boolean debugEnabled;
    Spin2Debug debug = new Spin2Debug();
    List<Object> debugStatements;

    boolean errors;
    List<CompilerException> messages = new ArrayList<>();

    Map<String, Expression> publicSymbols = new HashMap<>();
    List<LinkDataObject> objectLinks = new ArrayList<>();

    Spin2Compiler compiler;
    Spin2BytecodeCompiler bytecodeCompiler;

    public Spin2ObjectCompiler(Spin2Compiler compiler, List<Object> debugStatements) {
        this.scope = new Spin2GlobalContext(compiler.isCaseSensitive());
        this.compiler = compiler;
        this.debugEnabled = compiler.isDebugEnabled();
        this.debugStatements = debugStatements;
    }

    public void setDebugEnabled(boolean enabled) {
        this.debugEnabled = enabled;
    }

    @Override
    public Spin2Object compileObject(Node root) {
        compile(root);
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

        Iterator<Entry<String, Expression>> iter = publicSymbols.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, Expression> entry = iter.next();
            try {
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

        objectVarSize = 4;
        for (Node node : root.getChilds()) {
            if (node instanceof VariablesNode) {
                compileVarBlock(node);
            }
        }
        objectVarSize = (objectVarSize + 3) & ~3;

        for (Node node : root.getChilds()) {
            if (node instanceof DataNode) {
                compileDatBlock(node);
            }
        }

        for (Entry<String, ObjectInfo> infoEntry : objects.entrySet()) {
            ObjectInfo info = infoEntry.getValue();
            String name = infoEntry.getKey();

            try {
                int count = info.count.getNumber().intValue();

                LinkDataObject linkData = new Spin2LinkDataObject(info.compiler, info.compiler.getVarSize());
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

                for (int i = 1; i < count; i++) {
                    objectLinks.add(new Spin2LinkDataObject(info.compiler, info.compiler.getVarSize()));
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
            Node node = (Node) method.getData();
            for (Spin2MethodLine line : compileStatement(method.getScope(), method, null, node)) {
                method.addSource(line);
            }
            Spin2MethodLine line = new Spin2MethodLine(method.getScope(), "RETURN");
            line.addSource(new Bytecode(line.getScope(), 0x04, line.getStatement()));
            method.addSource(line);

            checkConditionStack();
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
                    Iterator<Spin2Method> methodsIterator = methods.iterator();
                    methodsIterator.next();
                    while (methodsIterator.hasNext()) {
                        Spin2Method method = methodsIterator.next();
                        if (!method.isReferenced()) {
                            MethodNode node = (MethodNode) method.getData();
                            logMessage(new CompilerException(CompilerException.WARNING, "method \"" + method.getLabel() + "\" is not used", node.getName()));
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
                    MethodNode node = (MethodNode) method.getData();
                    logMessage(new CompilerException(CompilerException.WARNING, "method \"" + method.getLabel() + "\" is not used", node.getName()));
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

    void compileConBlock(Node parent) {
        int enumValue = 0, enumIncrement = 1;

        for (Node node : parent.getChilds()) {
            try {
                if (node instanceof DirectiveNode) {
                    compileDirective(node);
                }
                else if (skipNode(node)) {
                    continue;
                }
                if (node instanceof ConstantNode) {
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
                }
            } catch (CompilerException e) {
                logMessage(e);
            } catch (Exception e) {
                logMessage(new CompilerException(e, node));
            }
        }

        checkConditionStack();
    }

    void compileVarBlock(Node parent) {
        String type = "LONG";

        for (Node node : parent.getChilds()) {
            try {
                if (node instanceof DirectiveNode) {
                    compileDirective(node);
                }
                else if (skipNode(node)) {
                    continue;
                }
                if (node instanceof VariableNode) {
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

                    Token identifier = token;
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
                        Variable var = new Variable(type, identifier.getText(), size, objectVarSize);
                        scope.addSymbol(identifier.getText(), var);
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
                        Node error = new Node();
                        iter.forEachRemaining(t -> error.addToken(t));
                        logMessage(new CompilerException("unexpected '" + error + "'", error));
                    }
                }
            } catch (CompilerException e) {
                logMessage(e);
            } catch (Exception e) {
                logMessage(new CompilerException(e, node));
            }
        }

        checkConditionStack();
    }

    void compileObjBlock(Node parent) {
        for (Node node : parent.getChilds()) {
            try {
                if (node instanceof DirectiveNode) {
                    compileDirective(node);
                }
                else if (skipNode(node)) {
                    continue;
                }
                if (node instanceof ObjectNode) {
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
                                    count.setData(((ObjectNode) node).count);
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
            } catch (CompilerException e) {
                logMessage(e);
            } catch (Exception e) {
                logMessage(new CompilerException(e, node));
            }
        }

        checkConditionStack();
    }

    Map<Spin2PAsmLine, Context> pendingAlias = new HashMap<Spin2PAsmLine, Context>();

    void compileDatBlock(Node parent) {
        Context savedContext = scope;
        nested = 0;

        for (Node child : parent.getChilds()) {
            try {
                if (child instanceof DirectiveNode) {
                    compileDirective(child);
                }
                else if (skipNode(child)) {
                    continue;
                }
                if (child instanceof DataLineNode) {
                    DataLineNode node = (DataLineNode) child;
                    if (!debugEnabled && node.instruction != null && "DEBUG".equalsIgnoreCase(node.instruction.getText())) {
                        if (node.label == null) {
                            continue;
                        }
                    }

                    Spin2PAsmLine pasmLine = compileDataLine(node);
                    pasmLine.setData(node);
                    source.addAll(pasmLine.expand());

                    if ("INCLUDE".equalsIgnoreCase(pasmLine.getMnemonic())) {
                        if (node.condition != null) {
                            throw new CompilerException("not allowed", node.condition);
                        }
                        if (node.modifier != null) {
                            throw new CompilerException("not allowed", node.modifier);
                        }
                        int index = 0;
                        for (Spin2PAsmExpression argument : pasmLine.getArguments()) {
                            String fileName = argument.getString();
                            Node includedNode = compiler.getParsedObject(fileName, ".spin2");
                            try {
                                if (includedNode == null) {
                                    throw new RuntimeException("file \"" + fileName + "\" not found");
                                }
                                compileDatInclude(includedNode);
                            } catch (CompilerException e) {
                                logMessage(e);
                            } catch (Exception e) {
                                logMessage(new CompilerException(e, node.parameters.get(index)));
                            }
                            index++;
                        }
                    }
                }

            } catch (CompilerException e) {
                logMessage(e);
            } catch (Exception e) {
                logMessage(new CompilerException(e, child));
            }
        }

        pendingAlias.clear();
        scope = savedContext;

        checkConditionStack();
    }

    Spin2PAsmLine compileDataLine(Context scope, DataLineNode node) {
        Context savedContext = this.scope;
        this.scope = scope;
        try {
            return compileDataLine(node);
        } finally {
            this.scope = savedContext;
        }
    }

    Spin2PAsmLine compileDataLine(DataLineNode node) {
        String label = node.label != null ? node.label.getText() : null;
        String condition = node.condition != null ? node.condition.getText() : null;
        String mnemonic = node.instruction != null ? node.instruction.getText() : null;
        String modifier = node.modifier != null ? node.modifier.getText() : null;
        List<Spin2PAsmExpression> parameters = new ArrayList<Spin2PAsmExpression>();

        Context rootScope = scope;
        if (label != null && !label.startsWith(".")) {
            if (nested > 0) {
                if (scope.getParent() != null) {
                    scope = rootScope = scope.getParent();
                }
                nested--;
            }
            scope = new Context(scope);
            nested++;
        }

        if (node.label == null && node.instruction == null) {
            throw new CompilerException("syntax error", node);
        }

        Context localScope = new Context(scope);

        for (DataLineNode.ParameterNode param : node.parameters) {
            int index = 0;
            String prefix = null;
            Expression expression = null, count = null;

            if (parameters.size() == 1 && Spin2InstructionObject.ptrInstructions.contains(mnemonic.toLowerCase()) && isPtrExpression(param.getTokens())) {
                expression = new Identifier(param.getText(), scope);
                expression.setData(param);
            }
            else {
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
                        Spin2ExpressionBuilder expressionBuilder = new Spin2ExpressionBuilder(localScope, param.getTokens().subList(index, param.getTokens().size()));
                        expression = expressionBuilder.getExpression();
                        expression.setData(param);
                    } catch (CompilerException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new CompilerException(e, param);
                    }
                }
            }
            if (param.count != null) {
                try {
                    Spin2ExpressionBuilder expressionBuilder = new Spin2ExpressionBuilder(localScope, param.count.getTokens());
                    count = expressionBuilder.getExpression();
                    count.setData(param.count);
                } catch (CompilerException e) {
                    throw e;
                } catch (Exception e) {
                    throw new CompilerException(e, param.count);
                }
            }
            if (expression != null) {
                parameters.add(new Spin2PAsmExpression(prefix, expression, count));
            }
        }

        if (!debugEnabled && mnemonic != null && "DEBUG".equalsIgnoreCase(mnemonic)) {
            condition = mnemonic = null;
            parameters.clear();
            modifier = null;
        }

        Spin2PAsmLine pasmLine = new Spin2PAsmLine(localScope, label, condition, mnemonic, parameters, modifier);

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
            if ("DEBUG".equalsIgnoreCase(mnemonic)) {
                if (node.condition != null) {
                    throw new CompilerException("not allowed", node.condition);
                }
                if (node.modifier != null) {
                    throw new CompilerException("not allowed", node.modifier);
                }
                int debugIndex = debugStatements.size() + 1;
                if (debugIndex >= 255) {
                    throw new CompilerException("too much debug statements", node);
                }
                parameters.add(new Spin2PAsmExpression("#", new NumberLiteral(debugIndex), null));

                List<Token> tokens = new ArrayList<>(node.getTokens());
                if (node.label != null) {
                    tokens.remove(node.label);
                }
                Spin2PAsmDebugLine debugLine = Spin2PAsmDebugLine.buildFrom(pasmLine.getScope(), tokens);
                debugStatements.add(debugLine);
            }
        } catch (CompilerException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new CompilerException(e, node);
        }

        if (pasmLine.getLabel() != null) {
            try {
                String type = "LONG";
                if (pasmLine.getInstructionFactory() instanceof com.maccasoft.propeller.spin2.instructions.Word) {
                    type = "WORD";
                }
                else if (pasmLine.getInstructionFactory() instanceof com.maccasoft.propeller.spin2.instructions.Byte) {
                    type = "BYTE";
                }
                else if ("FILE".equalsIgnoreCase(pasmLine.getMnemonic())) {
                    type = "BYTE";
                }
                rootScope.addSymbol(pasmLine.getLabel(), new DataVariable(pasmLine.getScope(), type));
                rootScope.addSymbol("#" + pasmLine.getLabel(), new RegisterAddress(pasmLine.getScope(), type));
                rootScope.addSymbol("@" + pasmLine.getLabel(), new ObjectContextLiteral(pasmLine.getScope(), type));
                rootScope.addSymbol("@@" + pasmLine.getLabel(), new MemoryContextLiteral(pasmLine.getScope(), type));

                if (pasmLine.getMnemonic() == null) {
                    if (!pasmLine.isLocalLabel()) {
                        pendingAlias.put(pasmLine, rootScope);
                    }
                }
                else if (pendingAlias.size() != 0) {
                    for (Entry<Spin2PAsmLine, Context> entry : pendingAlias.entrySet()) {
                        Spin2PAsmLine line = entry.getKey();
                        Context context = entry.getValue();
                        context.addOrUpdateSymbol(line.getLabel(), new DataVariable(line.getScope(), type));
                        context.addOrUpdateSymbol("#" + line.getLabel(), new RegisterAddress(line.getScope(), type));
                        context.addOrUpdateSymbol("@" + line.getLabel(), new ObjectContextLiteral(line.getScope(), type));
                        context.addOrUpdateSymbol("@@" + line.getLabel(), new MemoryContextLiteral(line.getScope(), type));
                    }
                    pendingAlias.clear();
                }
            } catch (RuntimeException e) {
                throw new CompilerException(e, node.label);
            }
        }
        else if (pasmLine.getMnemonic() != null && pendingAlias.size() != 0) {
            String type = "LONG";
            if (pasmLine.getInstructionFactory() instanceof com.maccasoft.propeller.spin2.instructions.Word) {
                type = "WORD";
            }
            else if (pasmLine.getInstructionFactory() instanceof com.maccasoft.propeller.spin2.instructions.Byte) {
                type = "BYTE";
            }
            else if ("FILE".equalsIgnoreCase(pasmLine.getMnemonic())) {
                type = "BYTE";
            }

            for (Entry<Spin2PAsmLine, Context> entry : pendingAlias.entrySet()) {
                Spin2PAsmLine line = entry.getKey();
                Context context = entry.getValue();
                context.addOrUpdateSymbol(line.getLabel(), new DataVariable(line.getScope(), type));
                context.addOrUpdateSymbol("#" + line.getLabel(), new RegisterAddress(line.getScope(), type));
                context.addOrUpdateSymbol("@" + line.getLabel(), new ObjectContextLiteral(line.getScope(), type));
                context.addOrUpdateSymbol("@@" + line.getLabel(), new MemoryContextLiteral(line.getScope(), type));
            }
            pendingAlias.clear();
        }

        return pasmLine;
    }

    boolean isPtrExpression(List<Token> tokens) {
        int index = 0;

        if (index >= tokens.size()) {
            return false;
        }
        Token token = tokens.get(index++);
        if ("++".equals(token.getText()) || "--".equals(token.getText())) {
            if (index >= tokens.size()) {
                return false;
            }
            token = tokens.get(index++);
        }
        if (!("PTRA".equalsIgnoreCase(token.getText()) || "PTRB".equalsIgnoreCase(token.getText()))) {
            return false;
        }
        if (index >= tokens.size()) {
            return true;
        }
        token = tokens.get(index++);
        if (!("++".equals(token.getText()) || "--".equals(token.getText()) || "[".equals(token.getText()))) {
            return false;
        }
        return index >= tokens.size();
    }

    void compileDatInclude(Node root) {
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
                compileDatBlock(node);
            }
        }
    }

    protected byte[] getBinaryFile(String fileName) {
        return null;
    }

    Spin2Method compileMethod(MethodNode node) {
        Context localScope = new Context(scope);

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

        Spin2Method method = new Spin2Method(localScope, token.getText());
        method.setComment(node.getText());
        method.setData(node);

        for (MethodNode.ParameterNode child : node.getParameters()) {
            token = child.getToken(0);
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
                    LocalVariable var = method.addParameter("LONG", identifier, new NumberLiteral(1));
                    var.setData(child.getIdentifier());
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

        for (ReturnNode child : node.getReturnVariables()) {
            token = child.getToken(0);
            if (token.type == 0) {
                String identifier = child.getIdentifier().getText();
                Expression expression = localScope.getLocalSymbol(identifier);
                if (expression instanceof LocalVariable) {
                    logMessage(new CompilerException("symbol '" + identifier + "' already defined", child));
                }
                else {
                    if (expression != null) {
                        logMessage(new CompilerException(CompilerException.WARNING, "return variable '" + identifier + "' hides global variable", child));
                    }
                    LocalVariable var = method.addReturnVariable("LONG", identifier);
                    var.setData(child.getIdentifier());
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
                Token identifier = token;
                Expression size = new NumberLiteral(1);

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
                                    size.getNumber().intValue();
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

                Expression expression = localScope.getLocalSymbol(identifier.getText());
                if (expression instanceof LocalVariable) {
                    logMessage(new CompilerException("symbol '" + identifier + "' already defined", child));
                }
                else {
                    if (expression != null) {
                        logMessage(new CompilerException(CompilerException.WARNING, "local variable '" + identifier + "' hides global variable", child));
                    }
                    LocalVariable var = method.addLocalVariable(type, identifier.getText(), size);
                    var.setData(identifier);
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

        return method;
    }

    List<Spin2MethodLine> compileStatement(Context context, Spin2Method method, Spin2MethodLine parent, Node statementNode) {
        List<Spin2MethodLine> lines = new ArrayList<>();

        Spin2MethodLine previousLine = null;

        Iterator<Node> nodeIterator = new ArrayList<>(statementNode.getChilds()).iterator();
        while (nodeIterator.hasNext()) {
            Node node = nodeIterator.next();
            try {
                if (node instanceof DirectiveNode) {
                    compileDirective(node);
                }
                else if (skipNode(node)) {
                    continue;
                }
                if (node instanceof StatementNode) {
                    Spin2MethodLine line = compileStatement(context, method, parent, node, previousLine);
                    if (line != null) {
                        lines.add(line);
                        previousLine = line;
                    }
                }
                else if (node instanceof DataLineNode) {
                    Spin2MethodLine line = new Spin2MethodLine(context, node.getStartToken().getText(), node);
                    lines.add(line);
                    previousLine = line;

                    Context savedContext = scope;
                    try {
                        scope = line.getScope();
                        nested = 0;

                        int address = 0x1E0;
                        for (LocalVariable var : method.getAllLocalVariables()) {
                            scope.addSymbol(var.getName(), new NumberLiteral(address));
                            if (var.getSize() != null) {
                                address += var.getSize().getNumber().intValue();
                            }
                            else {
                                address += 1;
                            }
                            var.setCalledBy(method);
                            if (address >= 0x1F0) {
                                break;
                            }
                        }

                        Spin2PAsmLine pasmLine = compileDataLine(scope, (DataLineNode) node);
                        line.addSource(new InlinePAsm(line.getScope(), pasmLine));

                        compileInlinePAsmStatements(nodeIterator, line);

                    } finally {
                        scope = savedContext;
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

    Spin2MethodLine compileStatement(Context context, Spin2Method method, Spin2MethodLine parent, Node node, Spin2MethodLine previousLine) {
        Spin2MethodLine line = null;

        try {
            Iterator<Token> iter = node.getTokens().iterator();
            if (!iter.hasNext()) {
                throw new RuntimeException("syntax error");
            }

            Token token = iter.next();
            String text = token.getText().toUpperCase();

            if ("ABORT".equals(text)) {
                line = new Spin2MethodLine(context, parent, token.getText(), node);
                if (iter.hasNext()) {
                    Spin2TreeBuilder builder = new Spin2TreeBuilder(context);
                    while (iter.hasNext()) {
                        builder.addToken(iter.next());
                    }
                    line.addSource(bytecodeCompiler.compileConstantExpression(context, method, builder.getRoot()));
                    line.addSource(new Bytecode(line.getScope(), 0x07, text.toUpperCase()));
                }
                else {
                    line.addSource(new Bytecode(line.getScope(), 0x06, text.toUpperCase()));
                }
            }
            else if ("IF".equals(text) || "IFNOT".equals(text)) {
                line = new Spin2MethodLine(context, parent, token.getText(), node);

                Spin2TreeBuilder builder = new Spin2TreeBuilder(context);
                while (iter.hasNext()) {
                    builder.addToken(iter.next());
                }
                line.addSource(bytecodeCompiler.compileConstantExpression(context, method, builder.getRoot()));

                Spin2MethodLine falseLine = new Spin2MethodLine(context);
                if ("IF".equals(text)) {
                    line.addSource(new Jz(line.getScope(), new ContextLiteral(falseLine.getScope())));
                }
                else {
                    line.addSource(new Jnz(line.getScope(), new ContextLiteral(falseLine.getScope())));
                }

                line.addChilds(compileStatement(new Context(context), method, line, node));

                line.addChild(falseLine);
                line.addChild(new Spin2MethodLine(context));
            }
            else if ("ELSE".equals(text) || "ELSEIF".equals(text) || "ELSEIFNOT".equals(text)) {
                if (previousLine == null || (!previousLine.getStatement().toUpperCase().startsWith("IF") && !previousLine.getStatement().toUpperCase().startsWith("ELSEIF"))) {
                    throw new CompilerException("misplaced " + token.getText().toLowerCase(), node);
                }

                line = new Spin2MethodLine(context, parent, text, node);
                Spin2MethodLine falseLine = new Spin2MethodLine(context);
                Spin2MethodLine exitLine = previousLine.getChilds().remove(previousLine.getChilds().size() - 1);

                if ("ELSEIF".equals(text) || "ELSEIFNOT".equals(text)) {
                    Spin2TreeBuilder builder = new Spin2TreeBuilder(context);
                    while (iter.hasNext()) {
                        builder.addToken(iter.next());
                    }
                    line.addSource(bytecodeCompiler.compileConstantExpression(context, method, builder.getRoot()));
                    if ("ELSEIF".equals(text)) {
                        line.addSource(new Jz(line.getScope(), new ContextLiteral(falseLine.getScope())));
                    }
                    else {
                        line.addSource(new Jnz(line.getScope(), new ContextLiteral(falseLine.getScope())));
                    }
                }

                line.addChilds(compileStatement(new Context(context), method, line, node));
                line.addChild(falseLine);
                line.addChild(exitLine);

                Spin2MethodLine jmpLine = new Spin2MethodLine(context);
                jmpLine.addSource(new Jmp(line.getScope(), new ContextLiteral(exitLine.getScope())));
                previousLine.addChild(previousLine.getChilds().size() - 1, jmpLine);
            }
            else if ("RETURN".equals(text)) {
                line = new Spin2MethodLine(context, parent, text, node);

                if (iter.hasNext()) {
                    Spin2TreeBuilder builder = new Spin2TreeBuilder(context);
                    while (iter.hasNext()) {
                        builder.addToken(iter.next());
                    }
                    line.addSource(bytecodeCompiler.compileConstantExpression(line.getScope(), method, builder.getRoot()));
                    line.addSource(new Bytecode(line.getScope(), 0x05, text));
                }
                else {
                    line.addSource(new Bytecode(line.getScope(), 0x04, text));
                }
            }
            else if ("REPEAT".equals(text)) {
                line = new Spin2MethodLine(context, parent, text, node);

                Spin2MethodLine quitLine = new Spin2MethodLine(context);
                line.setData("quit", quitLine);

                if (iter.hasNext()) {
                    token = iter.next();
                    if ("WHILE".equalsIgnoreCase(token.getText()) || "UNTIL".equalsIgnoreCase(token.getText())) {
                        line.setStatement(text + " " + token.getText().toUpperCase());
                        line.setData("next", line);

                        Spin2TreeBuilder builder = new Spin2TreeBuilder(context);
                        while (iter.hasNext()) {
                            builder.addToken(iter.next());
                        }
                        line.addSource(bytecodeCompiler.compileConstantExpression(line.getScope(), method, builder.getRoot()));

                        if ("WHILE".equalsIgnoreCase(token.getText())) {
                            line.addSource(new Jz(line.getScope(), new ContextLiteral(quitLine.getScope())));
                        }
                        else {
                            line.addSource(new Jnz(line.getScope(), new ContextLiteral(quitLine.getScope())));
                        }

                        line.addChilds(compileStatement(new Context(context), method, line, node));

                        Spin2MethodLine loopLine = new Spin2MethodLine(context);
                        loopLine.addSource(new Jmp(loopLine.getScope(), new ContextLiteral(line.getScope())));
                        line.addChild(loopLine);
                    }
                    else {
                        Spin2StatementNode counter = null;
                        Spin2StatementNode from = null;
                        Spin2StatementNode to = null;
                        Spin2StatementNode step = null;

                        Spin2TreeBuilder builder = new Spin2TreeBuilder(context);
                        builder.addToken(token);
                        while (iter.hasNext()) {
                            token = iter.next();
                            if ("FROM".equalsIgnoreCase(token.getText())) {
                                break;
                            }
                            builder.addToken(token);
                        }
                        counter = builder.getRoot();

                        if ("FROM".equalsIgnoreCase(token.getText())) {
                            if (counter.getChildCount() != 0) {
                                throw new CompilerException("syntax error", builder.getTokens());
                            }

                            builder = new Spin2TreeBuilder(context);
                            while (iter.hasNext()) {
                                token = iter.next();
                                if ("TO".equalsIgnoreCase(token.getText())) {
                                    break;
                                }
                                builder.addToken(token);
                            }
                            from = builder.getRoot();

                            if ("TO".equalsIgnoreCase(token.getText())) {
                                builder = new Spin2TreeBuilder(context);
                                while (iter.hasNext()) {
                                    token = iter.next();
                                    if ("STEP".equalsIgnoreCase(token.getText())) {
                                        break;
                                    }
                                    builder.addToken(token);
                                }
                                to = builder.getRoot();

                                if ("STEP".equalsIgnoreCase(token.getText())) {
                                    builder = new Spin2TreeBuilder(context);
                                    while (iter.hasNext()) {
                                        builder.addToken(iter.next());
                                    }
                                    step = builder.getRoot();
                                }
                            }
                        }

                        if (from != null && to != null) {
                            line.setData("pop", Integer.valueOf(16));

                            Spin2MethodLine loopLine = new Spin2MethodLine(context);

                            line.addSource(new Address(line.getScope(), new ContextLiteral(loopLine.getScope())));

                            line.addSource(bytecodeCompiler.compileConstantExpression(line.getScope(), method, to));
                            if (step != null) {
                                line.addSource(bytecodeCompiler.compileConstantExpression(line.getScope(), method, step));
                            }
                            line.addSource(bytecodeCompiler.compileConstantExpression(line.getScope(), method, from));

                            Expression expression = line.getScope().getLocalSymbol(counter.getText());
                            if (expression == null) {
                                throw new CompilerException("undefined symbol " + counter.getText(), counter.getToken());
                            }
                            line.addSource(new VariableOp(line.getScope(), VariableOp.Op.Setup, false, (Variable) expression));
                            line.addSource(new Bytecode(line.getScope(), step != null ? 0x7C : 0x7B, "REPEAT"));

                            Spin2MethodLine nextLine = new Spin2MethodLine(context);
                            line.setData("next", nextLine);

                            line.addChild(loopLine);
                            line.addChilds(compileStatement(new Context(context), method, line, node));

                            nextLine.addSource(new VariableOp(line.getScope(), VariableOp.Op.Setup, false, (Variable) expression));
                            nextLine.addSource(new Bytecode(line.getScope(), 0x7D, "REPEAT_LOOP"));
                            line.addChild(nextLine);
                        }
                        else {
                            try {
                                Expression expression = bytecodeCompiler.buildConstantExpression(line.getScope(), counter);
                                line.addSource(new Constant(line.getScope(), expression));
                            } catch (Exception e) {
                                line.addSource(bytecodeCompiler.compileBytecodeExpression(line.getScope(), method, counter, true));
                                line.addSource(new Tjz(line.getScope(), new ContextLiteral(quitLine.getScope())));
                            }
                            line.setData("pop", Integer.valueOf(4));

                            Spin2MethodLine nextLine = new Spin2MethodLine(context);
                            line.setData("next", nextLine);

                            Spin2MethodLine loopLine = new Spin2MethodLine(context);
                            line.addChild(loopLine);
                            line.addChilds(compileStatement(new Context(context), method, line, node));

                            nextLine.addSource(new Djnz(nextLine.getScope(), new ContextLiteral(loopLine.getScope())));
                            line.addChild(nextLine);
                        }
                    }
                }
                else {
                    Spin2MethodLine nextLine = new Spin2MethodLine(context);
                    line.setData("next", nextLine);

                    line.addChild(nextLine);
                    line.addChilds(compileStatement(new Context(context), method, line, node));

                    Spin2MethodLine loopLine = new Spin2MethodLine(context);
                    loopLine.addSource(new Jmp(loopLine.getScope(), new ContextLiteral(line.getScope())));
                    line.addChild(loopLine);
                    line.setData("loop", loopLine);
                }

                line.addChild(quitLine);
            }
            else if ("WHILE".equals(text) || "UNTIL".equals(text)) {
                if (previousLine == null || (!"REPEAT".equalsIgnoreCase(previousLine.getStatement()))) {
                    throw new CompilerException("misplaced " + token.getText().toLowerCase(), node);
                }

                previousLine.getChilds().remove(previousLine.getData("loop"));

                line = new Spin2MethodLine(context, parent, text, node);

                Spin2MethodLine nextLine = (Spin2MethodLine) previousLine.getData("next");
                previousLine.getChilds().remove(nextLine);
                line.addChild(nextLine);

                Spin2MethodLine conditionLine = new Spin2MethodLine(context);

                Spin2TreeBuilder builder = new Spin2TreeBuilder(context);
                while (iter.hasNext()) {
                    builder.addToken(iter.next());
                }
                conditionLine.addSource(bytecodeCompiler.compileConstantExpression(line.getScope(), method, builder.getRoot()));

                if ("WHILE".equals(text)) {
                    conditionLine.addSource(new Jnz(previousLine.getScope(), new ContextLiteral(previousLine.getScope())));
                }
                else {
                    conditionLine.addSource(new Jz(previousLine.getScope(), new ContextLiteral(previousLine.getScope())));
                }

                line.addChild(conditionLine);

                Spin2MethodLine quitLine = (Spin2MethodLine) previousLine.getData("quit");
                previousLine.getChilds().remove(quitLine);
                line.addChild(quitLine);
            }
            else if ("NEXT".equals(text)) {
                line = new Spin2MethodLine(context, parent, text, node);

                while (parent != null && parent.getData("next") == null) {
                    parent = parent.getParent();
                }
                if (parent == null || parent.getData("next") == null) {
                    throw new CompilerException("misplaced next", node);
                }

                Spin2MethodLine nextLine = (Spin2MethodLine) parent.getData("next");
                line.addSource(new Jmp(line.getScope(), new ContextLiteral(nextLine.getScope())));
            }
            else if ("QUIT".equalsIgnoreCase(text)) {
                int pop = 0;
                boolean hasCase = false;

                line = new Spin2MethodLine(context, parent, text, node);

                while (parent != null) {
                    if ("CASE".equalsIgnoreCase(parent.getStatement()) || "CASE_FAST".equalsIgnoreCase(parent.getStatement())) {
                        hasCase = true;
                    }

                    if (parent.getData("pop") != null) {
                        pop += ((Integer) parent.getData("pop")).intValue();
                    }
                    if (parent.getData("quit") != null) {
                        break;
                    }
                    parent = parent.getParent();
                }
                if (parent == null || parent.getData("quit") == null) {
                    throw new CompilerException("misplaced quit", node);
                }

                Spin2MethodLine nextLine = (Spin2MethodLine) parent.getData("quit");
                if (!hasCase && pop == 4) {
                    line.addSource(new Jnz(line.getScope(), new ContextLiteral(nextLine.getScope())));
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
                            line.addSource(new Bytecode(line.getScope(), os.toByteArray(), String.format("POP %d", pop)));
                        } catch (Exception e) {
                            // Do nothing
                        }
                    }
                    line.addSource(new Jmp(line.getScope(), new ContextLiteral(nextLine.getScope())));
                }
            }
            else if ("CASE".equalsIgnoreCase(text)) {
                line = new Spin2MethodLine(context, parent, text, node);
                line.setData("pop", Integer.valueOf(8));

                Spin2MethodLine endLine = new Spin2MethodLine(context);
                line.addSource(new Address(line.getScope(), new ContextLiteral(endLine.getScope())));

                Spin2TreeBuilder builder = new Spin2TreeBuilder(context);
                while (iter.hasNext()) {
                    builder.addToken(iter.next());
                }
                line.addSource(bytecodeCompiler.compileBytecodeExpression(context, method, builder.getRoot(), true));

                boolean hasOther = false;
                for (Node child : node.getChilds()) {
                    if (child instanceof DirectiveNode) {
                        compileDirective(child);
                    }
                    else if (skipNode(child)) {
                        continue;
                    }
                    if (child instanceof StatementNode) {
                        Spin2MethodLine caseLine = new Spin2MethodLine(context);
                        caseLine.addChilds(compileStatement(new Context(context), method, line, child));

                        Iterator<Token> childIter = child.getTokens().iterator();
                        token = childIter.next();

                        if ("OTHER".equalsIgnoreCase(token.getText())) {
                            line.addChild(0, caseLine);
                            hasOther = true;
                        }
                        else {
                            builder = new Spin2TreeBuilder(context);
                            builder.addToken(token);
                            while (childIter.hasNext()) {
                                token = childIter.next();
                                if (":".equals(token.getText())) {
                                    break;
                                }
                                builder.addToken(token);
                            }
                            compileCase(method, line, builder.getRoot(), caseLine, bytecodeCompiler);

                            line.addChild(caseLine);
                        }

                        Spin2MethodLine doneLine = new Spin2MethodLine(context);
                        doneLine.addSource(new Bytecode(line.getScope(), 0x1E, "CASE_DONE"));
                        caseLine.addChild(doneLine);
                    }
                }

                if (!hasOther) {
                    line.addSource(new Bytecode(line.getScope(), 0x1E, "CASE_DONE"));
                }

                line.addChild(endLine);
            }
            else if ("CASE_FAST".equalsIgnoreCase(text)) {
                line = new Spin2MethodLine(context, parent, text, node);
                line.setData("pop", Integer.valueOf(4));

                Spin2MethodLine endLine = new Spin2MethodLine(context);
                line.addSource(new Address(line.getScope(), new ContextLiteral(endLine.getScope())));

                Spin2TreeBuilder builder = new Spin2TreeBuilder(context);
                while (iter.hasNext()) {
                    builder.addToken(iter.next());
                }
                line.addSource(bytecodeCompiler.compileBytecodeExpression(context, method, builder.getRoot(), true));
                line.addSource(new Bytecode(line.getScope(), 0x1A, "CASE_FAST"));

                int min = Integer.MAX_VALUE;
                int max = Integer.MIN_VALUE;
                Map<Integer, Spin2MethodLine> map = new TreeMap<Integer, Spin2MethodLine>();

                Spin2MethodLine doneLine = null;
                Spin2MethodLine otherLine = null;

                for (Node child : node.getChilds()) {
                    if (child instanceof DirectiveNode) {
                        compileDirective(child);
                    }
                    else if (skipNode(child)) {
                        continue;
                    }
                    if (child instanceof StatementNode) {
                        Spin2MethodLine caseLine = new Spin2MethodLine(context);
                        caseLine.addChilds(compileStatement(new Context(context), method, line, child));

                        Iterator<Token> childIter = child.getTokens().iterator();
                        token = childIter.next();

                        if ("OTHER".equalsIgnoreCase(token.getText())) {
                            otherLine = caseLine;
                        }
                        else {
                            builder = new Spin2TreeBuilder(context);
                            builder.addToken(token);
                            while (childIter.hasNext()) {
                                token = childIter.next();
                                if (":".equals(token.getText())) {
                                    break;
                                }
                                builder.addToken(token);
                            }
                            for (Entry<Integer, Spin2MethodLine> entry : compileCaseFast(line, builder.getRoot(), caseLine).entrySet()) {
                                if (map.containsKey(entry.getKey())) {
                                    throw new CompilerException("index is not unique", builder.getTokens());
                                }
                                map.put(entry.getKey(), entry.getValue());
                                min = Math.min(min, entry.getKey().intValue());
                                max = Math.max(max, entry.getKey().intValue());
                                if ((max - min) > 255) {
                                    throw new CompilerException("values must be within 255 of each other", builder.getTokens());
                                }
                            }

                        }
                        line.addChild(caseLine);

                        doneLine = new Spin2MethodLine(context);
                        doneLine.addSource(new Bytecode(line.getScope(), 0x1B, "CASE_FAST_DONE"));
                        caseLine.addChild(doneLine);
                    }
                }

                line.addSource(new Bytecode(line.getScope(), Constant.wrLong(min), String.format("FROM %d", min)));
                line.addSource(new Bytecode(line.getScope(), Constant.wrWord(max - min + 1), String.format("TO %d", max)));
                Context ref = line.getSource().get(line.getSource().size() - 1).getContext();

                int index = min;
                for (Entry<Integer, Spin2MethodLine> entry : map.entrySet()) {
                    int target = entry.getKey().intValue();
                    while (index < target) {
                        line.addSource(new CaseFastJmp(ref, new ContextLiteral(otherLine != null ? otherLine.getScope() : doneLine.getScope())));
                        index++;
                    }
                    line.addSource(new CaseFastJmp(ref, new ContextLiteral(entry.getValue().getScope())));
                    index++;
                }
                if (otherLine != null) {
                    line.addSource(new CaseFastJmp(ref, new ContextLiteral(otherLine.getScope())));
                }
                else {
                    line.addSource(new CaseFastJmp(ref, new ContextLiteral(doneLine.getScope())));
                }

                line.addChild(endLine);
            }
            else {
                if (!debugEnabled && "DEBUG".equalsIgnoreCase(token.getText())) {
                    return null;
                }

                Spin2TreeBuilder builder = new Spin2TreeBuilder(context);
                builder.addToken(token);
                while (iter.hasNext()) {
                    builder.addToken(iter.next());
                }
                line = new Spin2MethodLine(context, parent, null, node);
                line.addSource(bytecodeCompiler.compileBytecodeExpression(context, method, builder.getRoot(), false));
            }

        } catch (CompilerException e) {
            logMessage(e);
        } catch (Exception e) {
            logMessage(new CompilerException(e, node));
        }

        return line;
    }

    void compileInlinePAsmStatements(Iterator<Node> linesIterator, Spin2MethodLine line) {
        int org = 0;
        int count = 0;
        Context localScope = new Context(line.getScope());

        while (linesIterator.hasNext()) {
            Node node = linesIterator.next();
            if (node instanceof DirectiveNode) {
                compileDirective(node);
            }
            else if (skipNode(node)) {
                continue;
            }
            if (node instanceof DataLineNode) {
                Spin2PAsmLine pasmLine = compileDataLine(localScope, (DataLineNode) node);
                line.addSource(new InlinePAsm(localScope, pasmLine));
            }
            else if (node instanceof StatementNode) {
                if ("END".equalsIgnoreCase(node.getStartToken().getText())) {
                    Spin2PAsmLine pasmLine = new Spin2PAsmLine(line.getScope(), null, null, "ret", Collections.emptyList(), null);
                    line.addSource(new InlinePAsm(localScope, pasmLine));
                    break;
                }
            }
        }

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

    Map<Integer, Spin2MethodLine> compileCaseFast(Spin2MethodLine line, Spin2StatementNode arg, Spin2MethodLine target) {
        Map<Integer, Spin2MethodLine> map = new TreeMap<Integer, Spin2MethodLine>();
        if (",".equals(arg.getText())) {
            for (Spin2StatementNode child : arg.getChilds()) {
                map.putAll(compileCaseFast(line, child, target));
            }
        }
        else if ("..".equals(arg.getText())) {
            try {
                Expression expression = bytecodeCompiler.buildConstantExpression(line.getScope(), arg.getChild(0));
                if (!expression.isConstant()) {
                    throw new CompilerException("expression is not constant", arg.getChild(0));
                }
                int value = expression.getNumber().intValue();
                map.put(value, target);
            } catch (Exception e) {
                throw new CompilerException(e, arg);
            }
            try {
                Expression expression = bytecodeCompiler.buildConstantExpression(line.getScope(), arg.getChild(1));
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
                Expression expression = bytecodeCompiler.buildConstantExpression(line.getScope(), arg);
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

    void compileDirective(Node node) {
        Token token;
        boolean skip = !conditionStack.isEmpty() && conditionStack.peek().skip;

        Iterator<Token> iter = node.getTokens().iterator();
        token = iter.next();
        if (!iter.hasNext()) {
            throw new CompilerException("expecting directive", new Token(token.getStream(), token.stop));
        }
        token = iter.next();
        if ("define".equals(token.getText())) {
            if (!skip) {
                if (!iter.hasNext()) {
                    throw new CompilerException("expecting identifier", new Token(token.getStream(), token.stop));
                }
                token = iter.next();
                if (token.type != 0 && token.type != Token.KEYWORD) {
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
        }
        else if ("ifdef".equals(token.getText())) {
            if (!skip) {
                if (!iter.hasNext()) {
                    throw new CompilerException("expecting identifier", new Token(token.getStream(), token.stop));
                }
                Token identifier = iter.next();
                if (token.type != 0 && token.type != Token.KEYWORD) {
                    throw new CompilerException("invalid identifier", identifier);
                }
                skip = !(scope.isDefined(identifier.getText()) || scope.hasSymbol(identifier.getText()));
                conditionStack.push(new Condition(node, true, skip));
            }
            else {
                conditionStack.push(new Condition(node, false, skip));
            }
        }
        else if ("elifdef".equals(token.getText()) || "elseifdef".equals(token.getText())) {
            if (conditionStack.isEmpty()) {
                throw new CompilerException("misplaced #" + token.getText(), token);
            }
            if (conditionStack.peek().evaluated) {
                conditionStack.pop();

                Token identifier = iter.next();
                if (token.type != 0 && token.type != Token.KEYWORD) {
                    throw new CompilerException("invalid identifier", identifier);
                }
                skip = !(scope.isDefined(identifier.getText()) || scope.hasSymbol(identifier.getText()));

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
                if (token.type != 0 && token.type != Token.KEYWORD) {
                    throw new CompilerException("invalid identifier", identifier);
                }
                skip = scope.isDefined(identifier.getText()) || scope.hasSymbol(identifier.getText());
                conditionStack.push(new Condition(node, true, skip));
            }
            else {
                conditionStack.push(new Condition(node, false, skip));
            }
        }
        else if ("elifndef".equals(token.getText()) || "elseifndef".equals(token.getText())) {
            if (conditionStack.isEmpty()) {
                throw new CompilerException("misplaced #" + token.getText(), token);
            }
            if (conditionStack.peek().evaluated) {
                conditionStack.pop();

                Token identifier = iter.next();
                if (token.type != 0 && token.type != Token.KEYWORD) {
                    throw new CompilerException("invalid identifier", identifier);
                }
                skip = scope.isDefined(identifier.getText()) || scope.hasSymbol(identifier.getText());

                conditionStack.push(new Condition(node, true, skip));
            }
            else {
                conditionStack.push(new Condition(node, false, skip));
            }
        }
        else if ("else".equals(token.getText())) {
            if (conditionStack.isEmpty()) {
                throw new CompilerException("misplaced #" + token.getText(), token);
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
            else {
                conditionStack.push(new Condition(node, false, skip));
            }
        }
        else if ("elif".equals(token.getText()) || "elseif".equals(token.getText())) {
            if (conditionStack.isEmpty()) {
                throw new CompilerException("misplaced #" + token.getText(), token);
            }
            if (conditionStack.peek().evaluated) {
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
            else {
                conditionStack.push(new Condition(node, false, skip));
            }
        }
        else if ("endif".equals(token.getText())) {
            if (conditionStack.isEmpty()) {
                throw new CompilerException("misplaced #" + token.getText(), token);
            }
            conditionStack.pop();
        }
        else {
            throw new CompilerException("unsupported directive", token);
        }
    }

    boolean skipNode(Node node) {
        return !conditionStack.isEmpty() && conditionStack.peek().skip;
    }

    void checkConditionStack() {
        while (!conditionStack.isEmpty()) {
            Condition c = conditionStack.pop();
            logMessage(new CompilerException("unbalanced conditional directive", c.node));
        }
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

    public Context getScope() {
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
