/*
 * Copyright (c) 2021-22 Marco Maccaferri and others.
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
import com.maccasoft.propeller.SpinObject.DataObject;
import com.maccasoft.propeller.SpinObject.LongDataObject;
import com.maccasoft.propeller.SpinObject.WordDataObject;
import com.maccasoft.propeller.expressions.Abs;
import com.maccasoft.propeller.expressions.Add;
import com.maccasoft.propeller.expressions.Addbits;
import com.maccasoft.propeller.expressions.Addpins;
import com.maccasoft.propeller.expressions.And;
import com.maccasoft.propeller.expressions.CharacterLiteral;
import com.maccasoft.propeller.expressions.ContextLiteral;
import com.maccasoft.propeller.expressions.DataVariable;
import com.maccasoft.propeller.expressions.Divide;
import com.maccasoft.propeller.expressions.Equals;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.GreaterOrEquals;
import com.maccasoft.propeller.expressions.GreaterThan;
import com.maccasoft.propeller.expressions.Identifier;
import com.maccasoft.propeller.expressions.IfElse;
import com.maccasoft.propeller.expressions.LessOrEquals;
import com.maccasoft.propeller.expressions.LessThan;
import com.maccasoft.propeller.expressions.LocalVariable;
import com.maccasoft.propeller.expressions.LogicalAnd;
import com.maccasoft.propeller.expressions.LogicalOr;
import com.maccasoft.propeller.expressions.LogicalXor;
import com.maccasoft.propeller.expressions.MemoryContextLiteral;
import com.maccasoft.propeller.expressions.Method;
import com.maccasoft.propeller.expressions.Modulo;
import com.maccasoft.propeller.expressions.Multiply;
import com.maccasoft.propeller.expressions.Nan;
import com.maccasoft.propeller.expressions.Negative;
import com.maccasoft.propeller.expressions.Not;
import com.maccasoft.propeller.expressions.NotEquals;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.expressions.ObjectContextLiteral;
import com.maccasoft.propeller.expressions.Or;
import com.maccasoft.propeller.expressions.Register;
import com.maccasoft.propeller.expressions.RegisterAddress;
import com.maccasoft.propeller.expressions.Round;
import com.maccasoft.propeller.expressions.Sca;
import com.maccasoft.propeller.expressions.Scas;
import com.maccasoft.propeller.expressions.ShiftLeft;
import com.maccasoft.propeller.expressions.ShiftRight;
import com.maccasoft.propeller.expressions.Sqrt;
import com.maccasoft.propeller.expressions.Subtract;
import com.maccasoft.propeller.expressions.Trunc;
import com.maccasoft.propeller.expressions.UnsignedDivide;
import com.maccasoft.propeller.expressions.UnsignedModulo;
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
import com.maccasoft.propeller.spin2.Spin2Bytecode.Descriptor;
import com.maccasoft.propeller.spin2.Spin2Object.LinkDataObject;
import com.maccasoft.propeller.spin2.bytecode.Address;
import com.maccasoft.propeller.spin2.bytecode.BitField;
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
import com.maccasoft.propeller.spin2.bytecode.MathOp;
import com.maccasoft.propeller.spin2.bytecode.MemoryOp;
import com.maccasoft.propeller.spin2.bytecode.RegisterOp;
import com.maccasoft.propeller.spin2.bytecode.Tjz;
import com.maccasoft.propeller.spin2.bytecode.VariableOp;
import com.maccasoft.propeller.spin2.instructions.Empty;
import com.maccasoft.propeller.spin2.instructions.FileInc;
import com.maccasoft.propeller.spin2.instructions.Fit;
import com.maccasoft.propeller.spin2.instructions.Org;
import com.maccasoft.propeller.spin2.instructions.Orgh;
import com.maccasoft.propeller.spin2.instructions.Res;
import com.maccasoft.propeller.spin2.instructions.Word;

public class Spin2ObjectCompiler {

    public static class ObjectInfo {
        String fileName;
        Spin2ObjectCompiler compiler;

        long offset;
        int count;

        public ObjectInfo(String fileName, Spin2ObjectCompiler compiler) {
            this.fileName = fileName;
            this.compiler = compiler;
        }

        public ObjectInfo(String fileName, Spin2ObjectCompiler compiler, int count) {
            this.fileName = fileName;
            this.compiler = compiler;
            this.count = count;
        }

        public boolean hasErrors() {
            return compiler.hasErrors();
        }

    }

    Spin2Context scope;
    Map<String, ObjectInfo> childObjects;

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

    public Spin2ObjectCompiler(Spin2Context scope, Map<String, ObjectInfo> childObjects) {
        this(scope, childObjects, false);
    }

    public Spin2ObjectCompiler(Spin2Context scope, Map<String, ObjectInfo> childObjects, boolean debugEnabled) {
        this.scope = new Spin2Context(scope);
        this.childObjects = childObjects;
        this.debugEnabled = debugEnabled;
        this.debugStatements = new ArrayList<Object>();
    }

    protected Spin2ObjectCompiler(Spin2Context scope, Map<String, ObjectInfo> childObjects, boolean debugEnabled, List<Object> debugStatements) {
        this.scope = new Spin2Context(scope);
        this.childObjects = childObjects;
        this.debugEnabled = debugEnabled;
        this.debugStatements = debugStatements;
    }

    public void setDebugEnabled(boolean enabled) {
        this.debugEnabled = enabled;
    }

    public Spin2Object compileObject(Node root) {
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
            } catch (Exception e) {
                if (e instanceof CompilerException) {
                    logMessage((CompilerException) e);
                }
                else {
                    logMessage(new CompilerException(e, entry.getValue().getData()));
                }
                iter.remove();
            }
        }

        compileVarBlocks(root);

        while ((varOffset % 4) != 0) {
            varOffset++;
        }

        int _clkfreq = 20000000;
        if (scope.hasSymbol("_clkfreq")) {
            _clkfreq = scope.getSymbol("_clkfreq").getNumber().intValue();
        }
        else if (scope.hasSymbol("_CLKFREQ")) {
            _clkfreq = scope.getSymbol("_CLKFREQ").getNumber().intValue();
        }
        int _xinfreq = 20000000;
        if (scope.hasSymbol("_xinfreq")) {
            _xinfreq = scope.getSymbol("_xinfreq").getNumber().intValue();
        }
        else if (scope.hasSymbol("_XINFREQ")) {
            _xinfreq = scope.getSymbol("_XINFREQ").getNumber().intValue();
        }

        if (!scope.hasSymbol("_xinfreq")) {
            scope.addSymbol("_xinfreq", new NumberLiteral(_xinfreq));
        }
        if (!scope.hasSymbol("_XINFREQ")) {
            scope.addSymbol("_XINFREQ", new NumberLiteral(_xinfreq));
        }

        if (!scope.hasSymbol("_clkfreq")) {
            scope.addSymbol("_clkfreq", new NumberLiteral(_clkfreq));
        }
        if (!scope.hasSymbol("_CLKFREQ")) {
            scope.addSymbol("_CLKFREQ", new NumberLiteral(_clkfreq));
        }

        int _clkmode = getClockMode(_xinfreq, _clkfreq);
        if (!scope.hasSymbol("clkmode_")) {
            scope.addSymbol("clkmode_", new NumberLiteral(_clkmode));
        }
        if (!scope.hasSymbol("CLKMODE_")) {
            scope.addSymbol("CLKMODE_", new NumberLiteral(_clkmode));
        }

        for (Node node : root.getChilds()) {
            if (node instanceof DataNode) {
                compileDatBlock(node);
            }
        }

        int objectIndex = 1;
        for (Entry<String, ObjectInfo> infoEntry : objects.entrySet()) {
            ObjectInfo info = infoEntry.getValue();
            String name = infoEntry.getKey();
            for (Entry<String, Expression> objEntry : info.compiler.getPublicSymbols().entrySet()) {
                if (objEntry.getValue() instanceof Method) {
                    String qualifiedName = name + "." + objEntry.getKey();
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

        for (Node node : root.getChilds()) {
            if ((node instanceof MethodNode) && "PUB".equalsIgnoreCase(((MethodNode) node).getType().getText())) {
                Spin2Method method = compileMethod((MethodNode) node);
                try {
                    int offset = -1;
                    if (isReferenced((MethodNode) node)) {
                        offset = objects.size() * 2 + methodData.size();
                        methodData.add(new LongDataObject(0, "Method " + method.getLabel()));
                    }
                    else {
                        logMessage(new CompilerException(CompilerException.WARNING, "method \"" + method.label + "\" is not used", node));
                    }
                    publicSymbols.put(method.getLabel(), new Method(method.getLabel(), method.getParametersCount(), method.getReturnsCount(), offset));
                    scope.addSymbol(method.getLabel(), new Method(method.getLabel(), method.getParametersCount(), method.getReturnsCount(), offset));
                    scope.addSymbol("@" + method.getLabel(), new Method(method.getLabel(), method.getParametersCount(), method.getReturnsCount(), offset));
                    method.register();
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
                    int offset = -1;
                    if (isReferenced((MethodNode) node)) {
                        offset = objects.size() * 2 + methodData.size();
                        methodData.add(new LongDataObject(0, "Method " + method.getLabel()));
                    }
                    else {
                        logMessage(new CompilerException(CompilerException.WARNING, "method \"" + method.label + "\" is not used", node));
                    }
                    scope.addSymbol(method.getLabel(), new Method(method.getLabel(), method.getParametersCount(), method.getReturnsCount(), offset));
                    scope.addSymbol("@" + method.getLabel(), new Method(method.getLabel(), method.getParametersCount(), method.getReturnsCount(), offset));
                    method.register();
                    methods.add(method);
                } catch (Exception e) {
                    logMessage(new CompilerException(e.getMessage(), node));
                }
            }
        }
        if (methodData.size() != 0) {
            methodData.add(new LongDataObject(0, "End"));
            scope.addBuiltinSymbol("@CLKMODE", new NumberLiteral(0x40));
            scope.addBuiltinSymbol("@CLKFREQ", new NumberLiteral(0x44));
        }

        for (Spin2Method method : methods) {
            for (Spin2MethodLine line : method.getLines()) {
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

    public Spin2Object generateObject() {
        return generateObject(0);
    }

    public Spin2Object generateObject(int memoryOffset) {
        Spin2Object object = new Spin2Object();

        object.setClkFreq(scope.getSymbol("_CLKFREQ").getNumber().intValue());
        object.setClkMode(scope.getSymbol("CLKMODE_").getNumber().intValue());

        object.writeComment("Object header");

        object.links.addAll(objectLinks);
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
                    if (!isReferenced((MethodNode) method.getData())) {
                        continue;
                    }
                    address = method.resolve(address);
                    loop |= method.isAddressChanged();
                }
            } while (loop);

            int index = 0;
            for (Spin2Method method : methods) {
                if (!isReferenced((MethodNode) method.getData())) {
                    continue;
                }

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
                    Spin2ExpressionBuilder builder = new Spin2ExpressionBuilder(scope);
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
                String fileName = token.getText().substring(1, token.getText().length() - 1) + ".spin2";

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

    Map<Spin2PAsmLine, Spin2Context> pendingAlias = new HashMap<Spin2PAsmLine, Spin2Context>();

    void compileDatBlock(Node parent) {
        Spin2Context savedContext = scope;
        nested = 0;

        for (Node child : parent.getChilds()) {
            DataLineNode node = (DataLineNode) child;
            if (!debugEnabled && node.instruction != null && "DEBUG".equalsIgnoreCase(node.instruction.getText())) {
                continue;
            }
            try {
                Spin2PAsmLine pasmLine = compileDataLine(node);
                pasmLine.setData(node);
                source.addAll(pasmLine.expand());
            } catch (CompilerException e) {
                logMessage(e);
            } catch (Exception e) {
                logMessage(new CompilerException(e, node.instruction));
            }
        }

        pendingAlias.clear();
        scope = savedContext;
    }

    Spin2PAsmLine compileDataLine(DataLineNode node) {
        String label = node.label != null ? node.label.getText() : null;
        String condition = node.condition != null ? node.condition.getText() : null;
        String mnemonic = node.instruction != null ? node.instruction.getText() : null;
        String modifier = node.modifier != null ? node.modifier.getText() : null;
        List<Spin2PAsmExpression> parameters = new ArrayList<Spin2PAsmExpression>();

        Spin2Context rootScope = scope;
        if (label != null && !label.startsWith(".")) {
            if (nested > 0) {
                scope = rootScope = scope.getParent();
                nested--;
            }
            scope = new Spin2Context(scope);
            nested++;
        }

        if (node.label == null && node.instruction == null) {
            throw new CompilerException("syntax error", node);
        }

        Spin2Context localScope = new Spin2Context(scope);

        for (DataLineNode.ParameterNode param : node.parameters) {
            int index = 0;
            String prefix = null;
            Expression expression = null, count = null;

            for (Token token : param.getTokens()) {
                if ("ptra".equalsIgnoreCase(token.getText()) || "ptrb".equalsIgnoreCase(token.getText())) {
                    expression = new Identifier(param.getText(), scope);
                    expression.setData(param);
                    index = param.getTokens().size();
                    break;
                }
            }

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
                    expression = buildExpression(param.getTokens().subList(index, param.getTokens().size()), localScope);
                    expression.setData(param);
                } catch (Exception e) {
                    throw new CompilerException(e, param);
                }
            }
            if (param.count != null) {
                try {
                    count = buildExpression(param.count, localScope);
                } catch (Exception e) {
                    throw new CompilerException(e, param.count);
                }
            }
            if (expression != null) {
                parameters.add(new Spin2PAsmExpression(prefix, expression, count));
            }
        }

        Spin2PAsmLine pasmLine = new Spin2PAsmLine(localScope, label, condition, mnemonic, parameters, modifier);

        try {
            if ("FILE".equalsIgnoreCase(mnemonic)) {
                String fileName = parameters.get(0).getString();
                byte[] data = getBinaryFile(fileName);
                if (data == null) {
                    throw new CompilerException("file \"" + fileName + "\" not found", node.parameters.get(0));
                }
                pasmLine.setInstructionObject(new FileInc(pasmLine.getScope(), data));
            }
            if ("DEBUG".equalsIgnoreCase(mnemonic)) {
                int debugIndex = debugStatements.size() + 1;
                if (debugIndex >= 255) {
                    throw new CompilerException("too much debug statements", node);
                }
                parameters.add(new Spin2PAsmExpression("#", new NumberLiteral(debugIndex), null));

                Spin2PAsmDebugLine debugLine = Spin2PAsmDebugLine.buildFrom(pasmLine.getScope(), node.getTokens());
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
                    for (Entry<Spin2PAsmLine, Spin2Context> entry : pendingAlias.entrySet()) {
                        Spin2PAsmLine line = entry.getKey();
                        Spin2Context context = entry.getValue();
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

            for (Entry<Spin2PAsmLine, Spin2Context> entry : pendingAlias.entrySet()) {
                Spin2PAsmLine line = entry.getKey();
                Spin2Context context = entry.getValue();
                context.addOrUpdateSymbol(line.getLabel(), new DataVariable(line.getScope(), type));
                context.addOrUpdateSymbol("#" + line.getLabel(), new RegisterAddress(line.getScope(), type));
                context.addOrUpdateSymbol("@" + line.getLabel(), new ObjectContextLiteral(line.getScope(), type));
                context.addOrUpdateSymbol("@@" + line.getLabel(), new MemoryContextLiteral(line.getScope(), type));
            }
            pendingAlias.clear();
        }

        return pasmLine;
    }

    protected byte[] getBinaryFile(String fileName) {
        return null;
    }

    protected boolean isReferenced(MethodNode node) {
        return true;
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
                            loopLine = new Spin2MethodLine(context, null);
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

                    Spin2PAsmLine pasmLine = compileDataLine((DataLineNode) node);
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
                Spin2PAsmLine pasmLine = compileDataLine((DataLineNode) node);
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

    void compileLine(Spin2MethodLine line) {
        String text = line.getStatement();

        if ("ABORT".equalsIgnoreCase(text)) {
            if (line.getArgumentsCount() == 0) {
                line.addSource(new Bytecode(line.getScope(), 0x06, text.toUpperCase()));
            }
            else if (line.getArgumentsCount() == 1) {
                line.addSource(compileBytecodeExpression(line.getScope(), line.getArgument(0), true));
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
            line.addSource(compileConstantExpression(line.getScope(), line.getArgument(0)));
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
            line.addSource(compileBytecodeExpression(line.getScope(), line.getArgument(0), true));
            Spin2MethodLine target = (Spin2MethodLine) line.getData("false");
            line.addSource(new Jnz(line.getScope(), new ContextLiteral(target.getScope())));
        }
        else if ("ELSE".equalsIgnoreCase(text)) {

        }
        else if ("REPEAT".equalsIgnoreCase(text)) {
            if (line.getArgumentsCount() == 1) {
                try {
                    Expression expression = buildConstantExpression(line.getScope(), line.getArgument(0));
                    line.addSource(new Constant(line.getScope(), expression));
                } catch (Exception e) {
                    line.addSource(compileBytecodeExpression(line.getScope(), line.getArgument(0), true));
                    Spin2MethodLine target = (Spin2MethodLine) line.getData("quit");
                    line.addSource(new Tjz(line.getScope(), new ContextLiteral(target.getScope())));
                }
                line.setData("pop", Integer.valueOf(4));
            }
            else if (line.getArgumentsCount() == 3 || line.getArgumentsCount() == 4) {
                Spin2MethodLine end = line.getChilds().get(0);
                line.addSource(new Address(line.getScope(), new ContextLiteral(end.getScope())));

                line.addSource(compileConstantExpression(line.getScope(), line.getArgument(2)));
                if (line.getArgumentsCount() == 4) {
                    line.addSource(compileConstantExpression(line.getScope(), line.getArgument(3)));
                }
                line.addSource(compileConstantExpression(line.getScope(), line.getArgument(1)));
                line.setData("pop", Integer.valueOf(16));

                String varText = line.getArgument(0).getText();
                Expression expression = line.getScope().getLocalSymbol(varText);
                if (expression == null) {
                    throw new CompilerException("undefined symbol " + varText, line.getArgument(0).getToken());
                }
                else if ((expression instanceof Variable) || (expression instanceof LocalVariable)) {
                    line.addSource(new VariableOp(line.getScope(), VariableOp.Op.Setup, false, (Variable) expression));
                    line.addSource(new Bytecode(line.getScope(), line.getArgumentsCount() == 4 ? 0x7D : 0x7C, "REPEAT"));
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
            line.addSource(compileBytecodeExpression(line.getScope(), line.getArgument(0), true));
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
            line.addSource(compileBytecodeExpression(line.getScope(), line.getArgument(0), true));
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
            line.addSource(compileBytecodeExpression(line.getScope(), line.getArgument(0), true));
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
                line.addSource(new Bytecode(line.getScope(), 0x7E, "REPEAT_LOOP"));
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
                    line.addSource(compileBytecodeExpression(line.getScope(), arg, true));
                }
                line.addSource(new Bytecode(line.getScope(), 0x05, text));
            }
        }
        else if ("CASE".equalsIgnoreCase(text)) {
            Spin2MethodLine end = (Spin2MethodLine) line.getData("end");
            line.addSource(new Address(line.getScope(), new ContextLiteral(end.getScope())));

            Iterator<Spin2StatementNode> iter = line.getArguments().iterator();
            line.addSource(compileBytecodeExpression(line.getScope(), iter.next(), true));

            while (iter.hasNext()) {
                Spin2StatementNode arg = iter.next();
                Spin2MethodLine target = (Spin2MethodLine) arg.getData("true");
                compileCase(line, arg, target);
            }
        }
        else if ("CASE_FAST".equalsIgnoreCase(text)) {
            Spin2MethodLine end = (Spin2MethodLine) line.getData("end");
            line.addSource(new Address(line.getScope(), new ContextLiteral(end.getScope())));

            Iterator<Spin2StatementNode> iter = line.getArguments().iterator();
            line.addSource(compileBytecodeExpression(line.getScope(), iter.next(), true));
            line.addSource(new Bytecode(line.getScope(), 0x1A, "CASE_FAST"));

            Map<Integer, Spin2MethodLine> map = new TreeMap<Integer, Spin2MethodLine>();

            while (iter.hasNext()) {
                Spin2StatementNode arg = iter.next();
                Spin2MethodLine target = (Spin2MethodLine) arg.getData("true");
                for (Entry<Integer, Spin2MethodLine> entry : compileCaseFast(line, arg, target).entrySet()) {
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
                    if (pasmLine.getInstructionFactory() instanceof Org) {
                        continue;
                    }
                    if (pasmLine.getInstructionFactory() instanceof Empty) {
                        continue;
                    }
                    count++;
                    List<Spin2PAsmExpression> arguments = pasmLine.getArguments();
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
                0x19, 0x5C
            }, "INLINE-EXEC"));
        }
        else if (text != null) {
            Descriptor desc = Spin2Bytecode.getDescriptor(text);
            if (desc != null) {
                if (line.getArgumentsCount() != desc.parameters) {
                    throw new CompilerException("expected " + desc.parameters + " argument(s), found " + line.getArgumentsCount(), line.getData());
                }
                for (Spin2StatementNode arg : line.getArguments()) {
                    List<Spin2Bytecode> list = compileBytecodeExpression(line.getScope(), arg, true);
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
                    line.addSource(compileBytecodeExpression(line.getScope(), arg, false));
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
                compileLine(child);
            } catch (CompilerException e) {
                logMessage(e);
            } catch (Exception e) {
                logMessage(new CompilerException(e, child.getData()));
            }
        }
    }

    void compileCase(Spin2MethodLine line, Spin2StatementNode arg, Spin2MethodLine target) {
        if (",".equals(arg.getText())) {
            for (Spin2StatementNode child : arg.getChilds()) {
                compileCase(line, child, target);
            }
        }
        else if ("..".equals(arg.getText())) {
            line.addSource(compileBytecodeExpression(line.getScope(), arg.getChild(0), false));
            line.addSource(compileBytecodeExpression(line.getScope(), arg.getChild(1), false));
            if (target != null) {
                line.addSource(new CaseRangeJmp(line.getScope(), new ContextLiteral(target.getScope())));
            }
        }
        else {
            line.addSource(compileBytecodeExpression(line.getScope(), arg, false));
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
                Expression expression = buildConstantExpression(line.getScope(), arg.getChild(0));
                if (!expression.isConstant()) {
                    throw new CompilerException("expression is not constant", arg.getChild(0));
                }
                int value = expression.getNumber().intValue();
                map.put(value, target);
            } catch (Exception e) {
                throw new CompilerException(e, arg);
            }
            try {
                Expression expression = buildConstantExpression(line.getScope(), arg.getChild(1));
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
                Expression expression = buildConstantExpression(line.getScope(), arg);
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

    List<Spin2Bytecode> compileBytecodeExpression(Spin2Context context, Spin2StatementNode node, boolean push) {
        List<Spin2Bytecode> source = new ArrayList<Spin2Bytecode>();

        try {
            Descriptor desc = Spin2Bytecode.getDescriptor(node.getText());
            if (desc != null) {
                if (node.getChildCount() != desc.parameters) {
                    throw new RuntimeException("expected " + desc.parameters + " argument(s), found " + node.getChildCount());
                }
                for (int i = 0; i < desc.parameters; i++) {
                    source.addAll(compileConstantExpression(context, node.getChild(i)));
                }
                source.add(new Bytecode(context, desc.code, node.getText().toUpperCase()));
            }
            else if ("ABORT".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() == 0) {
                    source.add(new Bytecode(context, 0x06, node.getText().toUpperCase()));
                }
                else if (node.getChildCount() == 1) {
                    source.addAll(compileBytecodeExpression(context, node.getChild(0), true));
                    source.add(new Bytecode(context, 0x07, node.getText().toUpperCase()));
                }
                else {
                    throw new RuntimeException("expected 0 or 1 argument(s), found " + node.getChildCount());
                }
            }
            else if ("COGINIT".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 3) {
                    throw new RuntimeException("expected " + 3 + " argument(s), found " + node.getChildCount());
                }
                for (int i = 0; i < node.getChildCount(); i++) {
                    source.addAll(compileBytecodeExpression(context, node.getChild(i), true));
                }
                source.add(new Bytecode(context, push ? 0x26 : 0x25, node.getText().toUpperCase()));
            }
            else if ("COGNEW".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 2) {
                    throw new RuntimeException("expected " + 2 + " argument(s), found " + node.getChildCount());
                }
                source.add(new Constant(context, new NumberLiteral(16)));
                for (int i = 0; i < node.getChildCount(); i++) {
                    source.addAll(compileBytecodeExpression(context, node.getChild(i), true));
                }
                source.add(new Bytecode(context, push ? 0x26 : 0x25, node.getText().toUpperCase()));
            }
            else if ("COGSPIN".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 3) {
                    throw new RuntimeException("expected " + 3 + " argument(s), found " + node.getChildCount());
                }

                source.addAll(compileConstantExpression(context, node.getChild(0)));

                Spin2StatementNode methodNode = node.getChild(1);
                Expression expression = context.getLocalSymbol(methodNode.getText());
                if (!(expression instanceof Method)) {
                    throw new CompilerException("invalid method " + methodNode.getText(), methodNode.getToken());
                }
                if (methodNode.getChildCount() != ((Method) expression).getArgumentsCount()) {
                    throw new CompilerException("expected " + ((Method) expression).getArgumentsCount() + " argument(s), found " + methodNode.getChildCount(), methodNode.getToken());
                }
                for (int i = 0; i < methodNode.getChildCount(); i++) {
                    source.addAll(compileConstantExpression(context, methodNode.getChild(i)));
                }
                source.add(new Bytecode(context, new byte[] {
                    (byte) 0x11,
                    (byte) ((Method) expression).getOffset()
                }, "SUB_ADDRESS (" + ((Method) expression).getOffset() + ")"));

                source.addAll(compileConstantExpression(context, node.getChild(2)));

                source.add(new Bytecode(context, new byte[] {
                    0x19, 0x5A
                }, node.getText().toUpperCase()));

                source.add(new Bytecode(context, new byte[] {
                    (byte) methodNode.getChildCount(), (byte) (push ? 0x26 : 0x25)
                }, "POP_RETURN (???)"));
            }
            else if ("RECV".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 0) {
                    throw new RuntimeException("expected " + 0 + " argument(s), found " + node.getChildCount());
                }
                source.add(new Bytecode(context, 0x0C, node.getText().toUpperCase()));
            }
            else if ("SEND".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() == 0) {
                    throw new RuntimeException("syntax error");
                }
                boolean bytes = true;
                for (Spin2StatementNode child : node.getChilds()) {
                    if (child.getType() != Token.NUMBER) {
                        bytes = false;
                        break;
                    }
                }
                if (bytes) {
                    byte[] code = new byte[node.getChildCount() + 2];
                    code[0] = 0x0E;
                    code[1] = (byte) node.getChildCount();
                    for (int i = 0; i < node.getChildCount(); i++) {
                        code[i + 2] = (byte) new NumberLiteral(node.getChild(i).getText()).getNumber().intValue();
                    }
                    source.add(new Bytecode(context, code, node.getText().toUpperCase()));
                }
                else {
                    for (Spin2StatementNode child : node.getChilds()) {
                        source.addAll(compileBytecodeExpression(context, child, true));
                        source.add(new Bytecode(context, 0x0D, node.getText().toUpperCase()));
                    }
                }
            }
            else if ("DEBUG".equalsIgnoreCase(node.getText())) {
                int debugIndex = debugStatements.size() + 1;
                if (debugIndex >= 255) {
                    throw new RuntimeException("too much debug statements");
                }

                int pop = 0;
                for (Spin2StatementNode child : node.getChilds()) {
                    for (Spin2StatementNode param : child.getChilds()) {
                        source.addAll(compileBytecodeExpression(context, param, true));
                        pop += 4;
                    }
                }
                node.setData("context", context);
                debugStatements.add(node);
                source.add(new Bytecode(context, new byte[] {
                    0x44, (byte) pop, (byte) debugIndex
                }, node.getText().toUpperCase() + " #" + debugIndex));
            }
            else if ("END".equalsIgnoreCase(node.getText())) {
                // Ignored
            }
            else if ("LOOKDOWN".equalsIgnoreCase(node.getText()) || "LOOKDOWNZ".equalsIgnoreCase(node.getText()) || "LOOKUP".equalsIgnoreCase(node.getText())
                || "LOOKUPZ".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() == 0) {
                    throw new RuntimeException("expected argument(s), found none");
                }
                Spin2StatementNode argsNode = node.getChild(0);
                if (!":".equalsIgnoreCase(argsNode.getText()) || argsNode.getChildCount() < 2) {
                    throw new RuntimeException("invalid argument(s)");
                }

                int code = 0x1F;
                int code_range = 0x21;
                if ("LOOKDOWN".equalsIgnoreCase(node.getText()) || "LOOKDOWNZ".equalsIgnoreCase(node.getText())) {
                    code = 0x20;
                    code_range = 0x22;
                }

                Spin2Bytecode end = new Spin2Bytecode(context);
                source.add(new Address(context, new ContextLiteral(end.getContext())));

                source.addAll(compileBytecodeExpression(context, argsNode.getChild(0), true));

                source.add(new Constant(context, new NumberLiteral(node.getText().toUpperCase().endsWith("Z") ? 0 : 1)));

                for (int i = 1; i < argsNode.getChildCount(); i++) {
                    Spin2StatementNode arg = argsNode.getChild(i);
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

                source.add(new Bytecode(context, 0x23, "LOOKDONE"));
                source.add(end);
            }
            else if ("STRING".equalsIgnoreCase(node.getText())) {
                StringBuilder sb = new StringBuilder();
                for (Spin2StatementNode child : node.getChilds()) {
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
                byte[] code = new byte[sb.length() + 3];
                int index = 0;
                code[index++] = (byte) 0x9E;
                code[index++] = (byte) (sb.length() + 1);
                for (int i = 0; i < sb.length(); i++) {
                    code[index++] = (byte) sb.charAt(i);
                }
                code[index++] = (byte) 0x00;
                source.add(new Bytecode(context, code, node.getText().toUpperCase()));
            }
            else if (node.getType() == Token.NUMBER) {
                Expression expression = new NumberLiteral(node.getText());
                source.add(new Constant(context, expression));
            }
            else if (node.getType() == Token.STRING) {
                String s = node.getText();
                if (s.startsWith("@")) {
                    s = s.substring(2, s.length() - 1);
                    byte[] code = new byte[s.length() + 3];
                    int index = 0;
                    code[index++] = (byte) 0x9E;
                    code[index++] = (byte) (s.length() + 1);
                    for (int i = 0; i < s.length(); i++) {
                        code[index++] = (byte) s.charAt(i);
                    }
                    code[index++] = (byte) 0x00;
                    source.add(new Bytecode(context, code, "STRING"));
                }
                else {
                    s = s.substring(1, s.length() - 1);
                    if (s.length() == 1) {
                        Expression expression = new CharacterLiteral(s);
                        source.add(new Constant(context, expression));
                    }
                    else {
                        byte[] code = new byte[s.length() + 3];
                        int index = 0;
                        code[index++] = (byte) 0x9E;
                        code[index++] = (byte) (s.length() + 1);
                        for (int i = 0; i < s.length(); i++) {
                            code[index++] = (byte) s.charAt(i);
                        }
                        code[index++] = (byte) 0x00;
                        source.add(new Bytecode(context, code, "STRING"));
                    }
                }
            }
            else if ("-".equalsIgnoreCase(node.getText()) && node.getChildCount() == 1) {
                try {
                    Expression expression = buildConstantExpression(context, node);
                    source.add(new Constant(context, expression));
                } catch (Exception e) {
                    source.addAll(compileBytecodeExpression(context, node.getChild(0), true));
                    source.add(new Bytecode(context, 0x79, "NEGATE"));
                }
            }
            else if (":=".equals(node.getText())) {
                if (node.getChildCount() != 2) {
                    throw new RuntimeException("expression syntax error " + node.getText());
                }
                source.addAll(compileConstantExpression(context, node.getChild(1)));
                source.addAll(leftAssign(context, node.getChild(0), push, push));
            }
            else if ("-=".equalsIgnoreCase(node.getText()) && node.getChildCount() == 1) {
                source.addAll(leftAssign(context, node.getChild(0), true, false));
                source.add(new Bytecode(context, 0x92, "NEGATE_ASSIGN"));
            }
            else if (MathOp.isAssignMathOp(node.getText()) && node.getChildCount() == 1) {
                source.addAll(leftAssign(context, node.getChild(0), true, false));
                source.add(new MathOp(context, node.getText(), push));
            }
            else if (MathOp.isAssignMathOp(node.getText())) {
                source.addAll(compileConstantExpression(context, node.getChild(1)));
                source.addAll(leftAssign(context, node.getChild(0), true, false));
                source.add(new MathOp(context, node.getText(), push));
            }
            else if (MathOp.isUnaryMathOp(node.getText())) {
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("expression syntax error " + node.getText());
                }
                source.addAll(compileBytecodeExpression(context, node.getChild(0), true));
                source.add(new MathOp(context, node.getText(), push));
            }
            else if (MathOp.isMathOp(node.getText())) {
                if (node.getChildCount() != 2) {
                    throw new RuntimeException("expression syntax error " + node.getText());
                }
                source.addAll(compileBytecodeExpression(context, node.getChild(0), true));
                source.addAll(compileBytecodeExpression(context, node.getChild(1), true));
                source.add(new MathOp(context, node.getText(), false));
            }
            else if ("?".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 2) {
                    throw new RuntimeException("expression syntax error " + node.getText());
                }
                if (!":".equals(node.getChild(1).getText())) {
                    throw new RuntimeException("expression syntax error " + node.getText());
                }
                source.addAll(compileBytecodeExpression(context, node.getChild(0), true));
                source.addAll(compileBytecodeExpression(context, node.getChild(1).getChild(0), true));
                source.addAll(compileBytecodeExpression(context, node.getChild(1).getChild(1), true));
                source.add(new Bytecode(context, 0x6B, "TERNARY_IF_ELSE"));
            }
            else if ("_".equalsIgnoreCase(node.getText())) {
                source.add(new Bytecode(context, 0x17, "POP"));
            }
            else if ("(".equalsIgnoreCase(node.getText())) {
                source.addAll(compileBytecodeExpression(context, node.getChild(0), push));
            }
            else if (",".equalsIgnoreCase(node.getText())) {
                for (Spin2StatementNode child : node.getChilds()) {
                    source.addAll(compileBytecodeExpression(context, child, push));
                }
            }
            else if ("\\".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() == 2) {
                    source.addAll(compileConstantExpression(context, node.getChild(1)));
                    source.addAll(leftAssign(context, node.getChild(0), push, false));
                    source.add(new Bytecode(context, 0x8D, "SWAP"));
                }
                else {
                    if (node.getChildCount() != 1) {
                        throw new RuntimeException("expression syntax error " + node.getText());
                    }

                    Expression expression = context.getLocalSymbol(node.getChild(0).getText());
                    if (!(expression instanceof Method)) {
                        throw new CompilerException("symbol " + node.getChild(0).getText() + " is not a method", node.getChild(0).getToken());
                    }
                    int parameters = ((Method) expression).getArgumentsCount();
                    if (node.getChild(0).getChildCount() != parameters) {
                        throw new RuntimeException("expected " + parameters + " argument(s), found " + node.getChild(0).getChildCount());
                    }
                    source.add(new Bytecode(context, push ? 0x03 : 0x02, "ANCHOR_TRAP"));
                    for (int i = 0; i < parameters; i++) {
                        source.addAll(compileBytecodeExpression(context, node.getChild(0).getChild(i), true));
                    }
                    Method method = (Method) expression;
                    if (method.getObject() != 0) {
                        source.add(new Bytecode(context, new byte[] {
                            (byte) 0x08,
                            (byte) (method.getObject() - 1),
                            (byte) method.getOffset()
                        }, "CALL_OBJ_SUB (" + (method.getObject() - 1) + "." + method.getOffset() + ")"));
                    }
                    else {
                        source.add(new Bytecode(context, new byte[] {
                            (byte) 0x0A,
                            (byte) method.getOffset()
                        }, "CALL_SUB (" + method.getOffset() + ")"));
                    }
                }
            }
            else if ("++".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("expression syntax error " + node.getText());
                }
                Expression expression = context.getLocalSymbol(node.getChild(0).getText());
                if (expression == null) {
                    throw new RuntimeException("undefined symbol " + node.getChild(0).getText());
                }
                if (expression instanceof Variable) {
                    source.add(new VariableOp(context, VariableOp.Op.Setup, false, (Variable) expression));
                    source.add(new Bytecode(context, push ? 0x85 : 0x83, "PRE_INC" + (push ? " (push)" : "")));
                }
                else if (expression instanceof DataVariable) {
                    String type = ((DataVariable) expression).getType();
                    MemoryOp.Size ss = MemoryOp.Size.Long;
                    if ("BYTE".equalsIgnoreCase(type)) {
                        ss = MemoryOp.Size.Byte;
                    }
                    else if ("WORD".equalsIgnoreCase(type)) {
                        ss = MemoryOp.Size.Word;
                    }
                    source.add(new MemoryOp(context, ss, MemoryOp.Base.PBase, MemoryOp.Op.Setup, expression));
                    source.add(new Bytecode(context, push ? 0x85 : 0x83, "PRE_INC" + (push ? " (push)" : "")));
                }
                else {
                    throw new CompilerException("unsupported operation on " + node.getChild(0).getText(), node.getChild(0).getToken());
                }
            }
            else if ("--".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("expression syntax error " + node.getText());
                }
                Expression expression = context.getLocalSymbol(node.getChild(0).getText());
                if (expression == null) {
                    throw new RuntimeException("undefined symbol " + node.getChild(0).getText());
                }
                if (expression instanceof Variable) {
                    source.add(new VariableOp(context, VariableOp.Op.Setup, false, (Variable) expression));
                    source.add(new Bytecode(context, push ? 0x86 : 0x84, "PRE_DEC" + (push ? " (push)" : "")));
                }
                else if (expression instanceof DataVariable) {
                    String type = ((DataVariable) expression).getType();
                    MemoryOp.Size ss = MemoryOp.Size.Long;
                    if ("BYTE".equalsIgnoreCase(type)) {
                        ss = MemoryOp.Size.Byte;
                    }
                    else if ("WORD".equalsIgnoreCase(type)) {
                        ss = MemoryOp.Size.Word;
                    }
                    source.add(new MemoryOp(context, ss, MemoryOp.Base.PBase, MemoryOp.Op.Setup, expression));
                    source.add(new Bytecode(context, push ? 0x86 : 0x84, "PRE_DEC" + (push ? " (push)" : "")));
                }
                else {
                    throw new CompilerException("unsupported operation on " + node.getChild(0).getText(), node.getChild(0).getToken());
                }
            }
            else if ("??".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("expression syntax error " + node.getText());
                }
                Expression expression = context.getLocalSymbol(node.getChild(0).getText());
                if (expression == null) {
                    throw new RuntimeException("undefined symbol " + node.getChild(0).getText());
                }
                if (expression instanceof Variable) {
                    source.add(new VariableOp(context, VariableOp.Op.Setup, false, (Variable) expression));
                    source.add(new Bytecode(context, push ? 0x8F : 0x8E, "PRE_RND" + (push ? " (push)" : "")));
                }
                else if (expression instanceof DataVariable) {
                    String type = ((DataVariable) expression).getType();
                    MemoryOp.Size ss = MemoryOp.Size.Long;
                    if ("BYTE".equalsIgnoreCase(type)) {
                        ss = MemoryOp.Size.Byte;
                    }
                    else if ("WORD".equalsIgnoreCase(type)) {
                        ss = MemoryOp.Size.Word;
                    }
                    source.add(new MemoryOp(context, ss, MemoryOp.Base.PBase, MemoryOp.Op.Setup, expression));
                    source.add(new Bytecode(context, push ? 0x8F : 0x8E, "PRE_RND" + (push ? " (push)" : "")));
                }
                else {
                    throw new CompilerException("unsupported operation on " + node.getChild(0).getText(), node.getChild(0).getToken());
                }
            }
            else if ("..".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 2) {
                    throw new RuntimeException("expression syntax error");
                }
                source.addAll(compileBytecodeExpression(context, node.getChild(0), true));
                source.addAll(compileBytecodeExpression(context, node.getChild(1), true));
            }
            else if ("BYTE".equalsIgnoreCase(node.getText()) || "WORD".equalsIgnoreCase(node.getText()) || "LONG".equalsIgnoreCase(node.getText())) {
                Spin2StatementNode indexNode = null;
                Spin2StatementNode bitfieldNode = null;
                Spin2StatementNode postEffectNode = null;

                int n = 1;
                if (n < node.getChildCount()) {
                    if (".".equals(node.getChild(n).getText())) {
                        n++;
                        if (n >= node.getChildCount()) {
                            throw new CompilerException("expected bitfield expression", node.getToken());
                        }
                        bitfieldNode = node.getChild(n++);
                    }
                    else if (!isPostEffect(node.getChild(n))) {
                        indexNode = node.getChild(n++);
                    }
                }
                if (n < node.getChildCount()) {
                    if (".".equals(node.getChild(n).getText())) {
                        if (bitfieldNode != null) {
                            throw new CompilerException("syntax error", node.getToken());
                        }
                        n++;
                        if (n >= node.getChildCount()) {
                            throw new CompilerException("expected bitfield expression", node.getToken());
                        }
                        bitfieldNode = node.getChild(n++);
                    }
                }
                if (n < node.getChildCount()) {
                    if (!isPostEffect(node.getChild(n))) {
                        if (indexNode != null) {
                            throw new CompilerException("syntax error", node.getToken());
                        }
                        indexNode = node.getChild(n++);
                    }
                }
                if (n < node.getChildCount()) {
                    if (isPostEffect(node.getChild(n))) {
                        postEffectNode = node.getChild(n++);
                    }
                }
                if (n < node.getChildCount()) {
                    throw new RuntimeException("syntax error " + node.getText());
                }

                if (postEffectNode != null) {
                    if ("~".equalsIgnoreCase(postEffectNode.getText())) {
                        source.add(new Constant(context, new NumberLiteral(0)));
                    }
                    else if ("~~".equalsIgnoreCase(postEffectNode.getText())) {
                        source.add(new Constant(context, new NumberLiteral(-1)));
                    }
                }

                source.addAll(compileBytecodeExpression(context, node.getChild(0), true));

                if (indexNode != null) {
                    source.addAll(compileBytecodeExpression(context, indexNode, true));
                }

                int bitfield = -1;
                if (bitfieldNode != null) {
                    if ("..".equals(bitfieldNode.getText())) {
                        try {
                            Expression exp1 = buildConstantExpression(context, bitfieldNode.getChild(0));
                            Expression exp2 = buildConstantExpression(context, bitfieldNode.getChild(1));
                            if (exp1.isConstant() && exp2.isConstant()) {
                                int lowest = Math.min(exp1.getNumber().intValue(), exp2.getNumber().intValue());
                                int highest = Math.max(exp1.getNumber().intValue(), exp2.getNumber().intValue());
                                Expression exp = new Addbits(new NumberLiteral(lowest), new NumberLiteral(highest - lowest));
                                bitfield = exp.getNumber().intValue();
                            }
                        } catch (Exception e) {
                            // Do nothing
                        }

                        if (bitfield == -1) {
                            source.addAll(compileBytecodeExpression(context, bitfieldNode, true));
                            source.add(new Bytecode(context, new byte[] {
                                (byte) 0x9F, (byte) 0x94
                            }, "ADDBITS"));
                        }
                    }
                    else {
                        try {
                            Expression exp = buildConstantExpression(context, bitfieldNode);
                            if (exp.isConstant()) {
                                bitfield = exp.getNumber().intValue();
                            }
                        } catch (Exception e) {
                            // Do nothing
                        }

                        if (bitfield == -1) {
                            source.addAll(compileBytecodeExpression(context, bitfieldNode, true));
                        }
                    }
                }

                MemoryOp.Size ss = MemoryOp.Size.Long;
                if ("BYTE".equalsIgnoreCase(node.getText())) {
                    ss = MemoryOp.Size.Byte;
                }
                else if ("WORD".equalsIgnoreCase(node.getText())) {
                    ss = MemoryOp.Size.Word;
                }
                MemoryOp.Op op = bitfieldNode == null && postEffectNode == null ? (push ? MemoryOp.Op.Read : MemoryOp.Op.Write) : MemoryOp.Op.Setup;
                source.add(new MemoryOp(context, ss, MemoryOp.Base.Pop, op, indexNode != null));

                if (bitfieldNode != null) {
                    source.add(new BitField(context, postEffectNode == null ? BitField.Op.Read : BitField.Op.Setup, bitfield));
                }

                if (postEffectNode != null) {
                    if ("~".equalsIgnoreCase(postEffectNode.getText())) {
                        source.add(new Bytecode(context, push ? 0x8D : 0x81, push ? "SWAP" : "WRITE"));
                    }
                    else if ("~~".equalsIgnoreCase(postEffectNode.getText())) {
                        source.add(new Bytecode(context, push ? 0x8D : 0x81, push ? "SWAP" : "WRITE"));
                    }
                    else if ("++".equalsIgnoreCase(postEffectNode.getText())) {
                        source.add(new Bytecode(context, push ? 0x87 : 0x83, "POST_INC" + (push ? " (push)" : "")));
                    }
                    else if ("--".equalsIgnoreCase(postEffectNode.getText())) {
                        source.add(new Bytecode(context, push ? 0x88 : 0x84, "POST_DEC" + (push ? " (push)" : "")));
                    }
                    else if ("!!".equalsIgnoreCase(postEffectNode.getText())) {
                        source.add(new Bytecode(context, push ? 0x8A : 0x89, "POST_LOGICAL_NOT" + (push ? " (push)" : "")));
                    }
                    else if ("!".equalsIgnoreCase(postEffectNode.getText())) {
                        source.add(new Bytecode(context, push ? 0x8C : 0x8B, "POST_NOT" + (push ? " (push)" : "")));
                    }
                    else {
                        throw new CompilerException("unhandled post effect " + postEffectNode.getText(), postEffectNode.getToken());
                    }
                }
            }
            else {
                String[] s = node.getText().split("[\\.]");
                if (s.length == 2 && ("BYTE".equalsIgnoreCase(s[1]) || "WORD".equalsIgnoreCase(s[1]) || "LONG".equalsIgnoreCase(s[1]))) {
                    int index = 0;
                    boolean popIndex = false;
                    Spin2StatementNode indexNode = null;
                    Spin2StatementNode postEffectNode = null;

                    Expression expression = context.getLocalSymbol(s[0]);
                    if (expression instanceof ObjectContextLiteral) {
                        expression = context.getLocalSymbol(s[0].substring(1));
                    }
                    if (expression == null) {
                        throw new CompilerException("undefined symbol " + node.getText(), node.getToken());
                    }

                    int n = 0;
                    if (n < node.getChildCount()) {
                        if (!isPostEffect(node.getChild(n))) {
                            indexNode = node.getChild(n++);
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
                    if ("BYTE".equalsIgnoreCase(s[1])) {
                        ss = MemoryOp.Size.Byte;
                    }
                    else if ("WORD".equalsIgnoreCase(s[1])) {
                        ss = MemoryOp.Size.Word;
                    }
                    MemoryOp.Base bb = MemoryOp.Base.PBase;
                    if (expression instanceof LocalVariable) {
                        bb = MemoryOp.Base.DBase;
                    }
                    else if (expression instanceof Variable) {
                        bb = MemoryOp.Base.VBase;
                    }

                    if (indexNode != null) {
                        popIndex = true;
                        try {
                            Expression exp = buildConstantExpression(context, indexNode);
                            if (exp.isConstant()) {
                                index = exp.getNumber().intValue();
                                popIndex = false;
                            }
                        } catch (Exception e) {
                            // Do nothing
                        }
                        if (popIndex) {
                            source.addAll(compileBytecodeExpression(context, indexNode, true));
                        }
                    }

                    if (s[0].startsWith("@")) {
                        source.add(new MemoryOp(context, ss, bb, MemoryOp.Op.Address, popIndex, expression, index));
                    }
                    else {
                        source.add(new MemoryOp(context, ss, bb, push ? MemoryOp.Op.Read : MemoryOp.Op.Write, popIndex, expression, index));
                    }

                    if (postEffectNode != null) {
                        if ("++".equalsIgnoreCase(postEffectNode.getText())) {
                            source.add(new Bytecode(context, push ? 0x87 : 0x83, "POST_INC" + (push ? " (push)" : "")));
                        }
                        else if ("--".equalsIgnoreCase(postEffectNode.getText())) {
                            source.add(new Bytecode(context, push ? 0x88 : 0x84, "POST_DEC" + (push ? " (push)" : "")));
                        }
                        else if ("!!".equalsIgnoreCase(postEffectNode.getText())) {
                            source.add(new Bytecode(context, push ? 0x8A : 0x89, "POST_LOGICAL_NOT" + (push ? " (push)" : "")));
                        }
                        else if ("!".equalsIgnoreCase(postEffectNode.getText())) {
                            source.add(new Bytecode(context, push ? 0x8C : 0x8B, "POST_NOT" + (push ? " (push)" : "")));
                        }
                        else {
                            throw new CompilerException("unhandled post effect " + postEffectNode.getText(), postEffectNode.getToken());
                        }
                    }
                }
                else {
                    Expression expression = context.getLocalSymbol(node.getText());
                    if (expression == null && node.getText().startsWith("@")) {
                        expression = context.getLocalSymbol(node.getText().substring(1));
                    }
                    if (expression instanceof ObjectContextLiteral) {
                        expression = context.getLocalSymbol(node.getText().substring(1));
                    }
                    if (expression == null && node.getChildCount() != 0) {
                        ObjectInfo info = objects.get(node.getText());
                        String qualifiedName = node.getText() + node.getChild(1).getText();
                        if (info == null && node.getText().startsWith("@")) {
                            info = objects.get(node.getText().substring(1));
                            qualifiedName = node.getText().substring(1) + node.getChild(1).getText();
                        }
                        if (info != null) {
                            if (node.getChildCount() != 2) {
                                throw new RuntimeException("syntax error" + node);
                            }
                            expression = context.getLocalSymbol(qualifiedName);
                            if (expression != null) {
                                Method method = (Method) expression;
                                if (node.getText().startsWith("@")) {
                                    source.addAll(compileConstantExpression(context, node.getChild(0)));
                                    source.add(new Bytecode(context, new byte[] {
                                        (byte) 0x10,
                                        (byte) (method.getObject() - 1),
                                        (byte) ((Method) expression).getOffset()
                                    }, "OBJ_SUB_ADDRESS (" + (method.getObject() - 1) + "." + method.getOffset() + ")"));
                                }
                                else {
                                    int parameters = method.getArgumentsCount();
                                    if (node.getChild(1).getChildCount() != parameters) {
                                        throw new RuntimeException("expected " + parameters + " argument(s), found " + node.getChild(1).getChildCount());
                                    }
                                    if (push && method.getReturnsCount() == 0) {
                                        throw new RuntimeException("method doesn't return any value");
                                    }
                                    source.add(new Bytecode(context, push ? 0x01 : 0x00, "ANCHOR"));
                                    for (int i = 0; i < parameters; i++) {
                                        source.addAll(compileConstantExpression(context, node.getChild(1).getChild(i)));
                                    }
                                    source.addAll(compileConstantExpression(context, node.getChild(0)));
                                    source.add(new Bytecode(context, new byte[] {
                                        (byte) 0x09,
                                        (byte) (method.getObject() - 1),
                                        (byte) method.getOffset()
                                    }, "CALL_OBJ_SUB (" + (method.getObject() - 1) + "." + method.getOffset() + ")"));
                                }
                                return source;
                            }
                        }
                    }
                    if (expression == null) {
                        throw new CompilerException("undefined symbol " + node.getText(), node.getToken());
                    }
                    if (node.getText().startsWith("@")) {
                        if (expression instanceof Method) {
                            Method method = (Method) expression;
                            if (method.getObject() != 0) {
                                source.add(new Bytecode(context, new byte[] {
                                    (byte) 0x0F,
                                    (byte) (method.getObject() - 1),
                                    (byte) ((Method) expression).getOffset()
                                }, "OBJ_SUB_ADDRESS (" + (method.getObject() - 1) + "." + method.getOffset() + ")"));
                            }
                            else {
                                source.add(new Bytecode(context, new byte[] {
                                    (byte) 0x11,
                                    (byte) method.getOffset()
                                }, "SUB_ADDRESS (" + method.getOffset() + ")"));
                            }
                        }
                        else if (expression instanceof Variable) {
                            Spin2StatementNode indexNode = null;

                            int n = 0;
                            if (n < node.getChildCount()) {
                                if (!isPostEffect(node.getChild(n))) {
                                    indexNode = node.getChild(n++);
                                }
                            }

                            int index = 0;
                            boolean hasIndex = false;
                            boolean popIndex = false;

                            if (indexNode != null) {
                                popIndex = true;
                                try {
                                    Expression exp = buildConstantExpression(context, indexNode);
                                    if (exp.isConstant()) {
                                        index = exp.getNumber().intValue();
                                        hasIndex = true;
                                        popIndex = false;
                                    }
                                } catch (Exception e) {
                                    // Do nothing
                                }
                                if (popIndex) {
                                    source.addAll(compileBytecodeExpression(context, indexNode, true));
                                }
                            }

                            VariableOp.Op op = VariableOp.Op.Address;
                            if (node.getText().startsWith("@@")) {
                                op = VariableOp.Op.PBaseAddress;
                            }

                            source.add(new VariableOp(context, op, popIndex, (Variable) expression, hasIndex, index));
                        }
                        else {
                            int index = 0;
                            boolean popIndex = false;
                            Spin2StatementNode indexNode = null;
                            Spin2StatementNode postEffectNode = null;

                            int n = 0;
                            if (n < node.getChildCount()) {
                                if (!isPostEffect(node.getChild(n))) {
                                    indexNode = node.getChild(n++);
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

                            if (indexNode != null) {
                                popIndex = true;
                                try {
                                    Expression exp = buildConstantExpression(context, indexNode);
                                    if (exp.isConstant()) {
                                        index = exp.getNumber().intValue();
                                        popIndex = false;
                                    }
                                } catch (Exception e) {
                                    // Do nothing
                                }
                                if (popIndex) {
                                    source.addAll(compileBytecodeExpression(context, indexNode, true));
                                }
                            }

                            MemoryOp.Size ss = MemoryOp.Size.Long;
                            MemoryOp.Base bb = MemoryOp.Base.PBase;

                            if ((expression instanceof MemoryContextLiteral) && node.getText().startsWith("@@")) {
                                expression = context.getLocalSymbol(node.getText().substring(1));
                            }
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
                            else if (expression instanceof ObjectContextLiteral) {
                                switch (((ObjectContextLiteral) expression).getType()) {
                                    case "BYTE":
                                        ss = MemoryOp.Size.Byte;
                                        break;
                                    case "WORD":
                                        ss = MemoryOp.Size.Word;
                                        break;
                                }
                            }
                            if (node.getText().startsWith("@@")) {
                                source.add(new MemoryOp(context, ss, bb, MemoryOp.Op.Read, popIndex, expression, index));
                                source.add(new Bytecode(context, 0x24, "ADD_PBASE"));
                            }
                            else {
                                source.add(new MemoryOp(context, ss, bb, MemoryOp.Op.Address, popIndex, expression, index));
                            }

                            if (postEffectNode != null) {
                                if ("++".equalsIgnoreCase(postEffectNode.getText())) {
                                    source.add(new Bytecode(context, push ? 0x87 : 0x83, "POST_INC" + (push ? " (push)" : "")));
                                }
                                else if ("--".equalsIgnoreCase(postEffectNode.getText())) {
                                    source.add(new Bytecode(context, push ? 0x88 : 0x84, "POST_DEC" + (push ? " (push)" : "")));
                                }
                                else if ("!!".equalsIgnoreCase(postEffectNode.getText())) {
                                    source.add(new Bytecode(context, push ? 0x8A : 0x89, "POST_LOGICAL_NOT" + (push ? " (push)" : "")));
                                }
                                else if ("!".equalsIgnoreCase(postEffectNode.getText())) {
                                    source.add(new Bytecode(context, push ? 0x8C : 0x8B, "POST_NOT" + (push ? " (push)" : "")));
                                }
                                else {
                                    throw new CompilerException("unhandled post effect " + postEffectNode.getText(), postEffectNode.getToken());
                                }
                            }
                        }
                    }
                    else if (expression instanceof Method) {
                        Method method = (Method) expression;
                        int expected = method.getArgumentsCount();
                        int actual = 0;
                        for (int i = 0; i < node.getChildCount(); i++) {
                            Expression child = context.getLocalSymbol(node.getChild(i).getText());
                            if (child != null && (child instanceof Method)) {
                                actual += ((Method) child).getReturnsCount();
                            }
                            else {
                                actual++;
                            }
                        }
                        if (expected != actual) {
                            throw new RuntimeException("expected " + expected + " argument(s), found " + actual);
                        }
                        if (push && method.getReturnsCount() == 0) {
                            throw new RuntimeException("method doesn't return any value");
                        }
                        source.add(new Bytecode(context, push ? 0x01 : 0x00, "ANCHOR"));
                        for (int i = 0; i < node.getChildCount(); i++) {
                            source.addAll(compileConstantExpression(context, node.getChild(i)));
                        }
                        if (method.getObject() != 0) {
                            source.add(new Bytecode(context, new byte[] {
                                (byte) 0x08,
                                (byte) (method.getObject() - 1),
                                (byte) method.getOffset()
                            }, "CALL_OBJ_SUB (" + (method.getObject() - 1) + "." + method.getOffset() + ")"));
                        }
                        else {
                            source.add(new Bytecode(context, new byte[] {
                                (byte) 0x0A,
                                (byte) method.getOffset()
                            }, "CALL_SUB (" + method.getOffset() + ")"));
                        }
                    }
                    else if (expression instanceof Register) {
                        source.addAll(compileVariableRead(context, expression, node, push));
                    }
                    else if (expression instanceof Variable) {
                        if (node instanceof Spin2StatementNode.Method) {
                            source.add(new Bytecode(context, push ? 0x01 : 0x00, "ANCHOR"));
                            for (int i = 0; i < node.getChildCount(); i++) {
                                source.addAll(compileConstantExpression(context, node.getChild(i)));
                            }
                            source.add(new VariableOp(context, VariableOp.Op.Read, false, (Variable) expression));
                            source.add(new Bytecode(context, new byte[] {
                                (byte) 0x0B,
                            }, "CALL_PTR"));
                        }
                        else {
                            source.addAll(compileVariableRead(context, expression, node, push));
                        }
                    }
                    else if (expression instanceof DataVariable) {
                        if (node instanceof Spin2StatementNode.Method) {
                            source.add(new Bytecode(context, push ? 0x01 : 0x00, "ANCHOR"));
                            for (int i = 0; i < node.getChildCount(); i++) {
                                source.addAll(compileConstantExpression(context, node.getChild(i)));
                            }
                            MemoryOp.Size ss = MemoryOp.Size.Long;
                            switch (((DataVariable) expression).getType()) {
                                case "BYTE":
                                    ss = MemoryOp.Size.Byte;
                                    break;
                                case "WORD":
                                    ss = MemoryOp.Size.Word;
                                    break;
                            }
                            source.add(new MemoryOp(context, ss, MemoryOp.Base.PBase, MemoryOp.Op.Read, false, expression, 0));
                            source.add(new Bytecode(context, new byte[] {
                                (byte) 0x0B,
                            }, "CALL_PTR"));
                        }
                        else {
                            source.addAll(compileVariableRead(context, expression, node, push));
                        }
                    }
                    else if (expression instanceof ContextLiteral) {
                        source.addAll(compileVariableRead(context, expression, node, push));
                    }
                    else if (expression.isConstant()) {
                        source.add(new Constant(context, expression));
                    }
                    else {
                        source.add(new MemoryOp(context, MemoryOp.Size.Long, MemoryOp.Base.PBase, MemoryOp.Op.Read, expression));
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

    List<Spin2Bytecode> compileVariableRead(Spin2Context context, Expression expression, Spin2StatementNode node, boolean push) {
        Spin2StatementNode indexNode = null;
        Spin2StatementNode bitfieldNode = null;
        Spin2StatementNode postEffectNode = null;
        List<Spin2Bytecode> source = new ArrayList<Spin2Bytecode>();

        MemoryOp.Size ss = MemoryOp.Size.Long;
        MemoryOp.Base bb = MemoryOp.Base.PBase;
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

        int n = 0;
        if (n < node.getChildCount()) {
            if (!".".equals(node.getChild(n).getText()) && !isPostEffect(node.getChild(n))) {
                indexNode = node.getChild(n++);
                if ("..".equals(indexNode.getText())) {
                    bitfieldNode = indexNode;
                    indexNode = null;
                }
            }
        }
        if (n < node.getChildCount()) {
            if (".".equals(node.getChild(n).getText())) {
                if (bitfieldNode != null) {
                    throw new CompilerException("invalid bitfield expression", node.getToken());
                }
                n++;
                if (n >= node.getChildCount()) {
                    throw new CompilerException("expected bitfield expression", node.getToken());
                }
                bitfieldNode = node.getChild(n++);
            }
        }
        if (n < node.getChildCount()) {
            if (isPostEffect(node.getChild(n))) {
                postEffectNode = node.getChild(n++);
            }
        }
        if (n < node.getChildCount()) {
            throw new CompilerException("unexpected " + node.getChild(n).getText(), node.getChild(n).getToken());
        }

        int index = 0;
        boolean hasIndex = false;
        boolean popIndex = false;

        if (indexNode != null) {
            popIndex = true;
            try {
                Expression exp = buildConstantExpression(context, indexNode);
                if (exp.isConstant()) {
                    index = exp.getNumber().intValue();
                    hasIndex = true;
                    popIndex = false;
                }
            } catch (Exception e) {
                // Do nothing
            }
        }

        if (bitfieldNode != null) {
            if (postEffectNode != null) {
                if ("~".equalsIgnoreCase(postEffectNode.getText()) || "~~".equalsIgnoreCase(postEffectNode.getText())) {
                    if ("~".equalsIgnoreCase(postEffectNode.getText())) {
                        source.add(new Constant(context, new NumberLiteral(0)));
                    }
                    else {
                        source.add(new Constant(context, new NumberLiteral(-1)));
                    }
                }
            }

            int bitfield = -1;
            if ("..".equals(bitfieldNode.getText())) {
                try {
                    Expression exp1 = buildConstantExpression(context, bitfieldNode.getChild(0));
                    Expression exp2 = buildConstantExpression(context, bitfieldNode.getChild(1));
                    if (exp1.isConstant() && exp2.isConstant()) {
                        int lowest = Math.min(exp1.getNumber().intValue(), exp2.getNumber().intValue());
                        int highest = Math.max(exp1.getNumber().intValue(), exp2.getNumber().intValue());
                        Expression exp = new Addbits(new NumberLiteral(lowest), new NumberLiteral(highest - lowest));
                        bitfield = exp.getNumber().intValue();
                    }
                } catch (Exception e) {
                    // Do nothing
                }

                if (bitfield == -1) {
                    source.addAll(compileBytecodeExpression(context, bitfieldNode, true));
                    source.add(new Bytecode(context, new byte[] {
                        (byte) 0x9F, (byte) 0x94
                    }, "ADDBITS"));
                }
            }
            else {
                try {
                    Expression exp = buildConstantExpression(context, bitfieldNode);
                    if (exp.isConstant()) {
                        bitfield = exp.getNumber().intValue();
                    }
                } catch (Exception e) {
                    // Do nothing
                }

                if (bitfield == -1) {
                    source.addAll(compileBytecodeExpression(context, bitfieldNode, true));
                }
            }

            if (popIndex) {
                source.addAll(compileBytecodeExpression(context, indexNode, true));
            }

            if (expression instanceof Register) {
                source.add(new RegisterOp(context, RegisterOp.Op.Setup, popIndex, expression, index));
            }
            else if (expression instanceof ContextLiteral) {
                source.add(new MemoryOp(context, ss, bb, MemoryOp.Op.Setup, popIndex, expression, index));
            }
            else {
                source.add(new VariableOp(context, VariableOp.Op.Setup, popIndex, (Variable) expression, hasIndex, index));
            }

            source.add(new BitField(context, postEffectNode == null ? BitField.Op.Read : BitField.Op.Setup, bitfield));

            if (postEffectNode != null) {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                StringBuilder sb = new StringBuilder();
                if ("~".equalsIgnoreCase(postEffectNode.getText()) || "~~".equalsIgnoreCase(postEffectNode.getText())) {
                    os.write(push ? 0x8D : 0x81);
                    sb.append(push ? "SWAP" : "WRITE");
                }
                else if ("++".equalsIgnoreCase(postEffectNode.getText())) {
                    os.write(push ? 0x87 : 0x83);
                    sb.append("POST_INC");
                }
                else if ("--".equalsIgnoreCase(postEffectNode.getText())) {
                    os.write(push ? 0x88 : 0x84);
                    sb.append("POST_DEC");
                }
                else if ("!!".equalsIgnoreCase(postEffectNode.getText())) {
                    os.write(push ? 0x8A : 0x89);
                    sb.append("POST_LOGICAL_NOT");
                }
                else if ("!".equalsIgnoreCase(postEffectNode.getText())) {
                    os.write(push ? 0x8C : 0x8B);
                    sb.append("POST_NOT");
                }
                else {
                    throw new CompilerException("unsupported post effect " + postEffectNode.getText(), postEffectNode.getToken());
                }
                source.add(new Bytecode(context, os.toByteArray(), sb.toString()));
            }
        }
        else {
            if (popIndex) {
                source.addAll(compileBytecodeExpression(context, indexNode, true));
            }
            if (postEffectNode != null) {
                if ("~".equalsIgnoreCase(postEffectNode.getText()) || "~~".equalsIgnoreCase(postEffectNode.getText())) {
                    if ("~".equalsIgnoreCase(postEffectNode.getText())) {
                        source.add(new Constant(context, new NumberLiteral(0)));
                    }
                    else {
                        source.add(new Constant(context, new NumberLiteral(-1)));
                    }
                    if (expression instanceof Register) {
                        source.add(new RegisterOp(context, push ? RegisterOp.Op.Setup : RegisterOp.Op.Write, popIndex, expression, index));
                    }
                    else if (expression instanceof ContextLiteral) {
                        source.add(new MemoryOp(context, ss, bb, push ? MemoryOp.Op.Setup : MemoryOp.Op.Write, popIndex, expression, index));
                    }
                    else {
                        source.add(new VariableOp(context, push ? VariableOp.Op.Setup : VariableOp.Op.Write, popIndex, (Variable) expression, hasIndex, index));
                    }
                    if (push) {
                        source.add(new Bytecode(context, 0x8D, "SWAP"));
                    }
                }
                else {
                    if (expression instanceof Register) {
                        source.add(new RegisterOp(context, RegisterOp.Op.Setup, popIndex, expression, index));
                    }
                    else if (expression instanceof ContextLiteral) {
                        source.add(new MemoryOp(context, ss, bb, MemoryOp.Op.Setup, popIndex, expression, index));
                    }
                    else {
                        source.add(new VariableOp(context, VariableOp.Op.Setup, popIndex, (Variable) expression, hasIndex, index));
                    }
                    if ("++".equalsIgnoreCase(postEffectNode.getText())) {
                        source.add(new Bytecode(context, push ? 0x87 : 0x83, "POST_INC" + (push ? " (push)" : "")));
                    }
                    else if ("--".equalsIgnoreCase(postEffectNode.getText())) {
                        source.add(new Bytecode(context, push ? 0x88 : 0x84, "POST_DEC" + (push ? " (push)" : "")));
                    }
                    else if ("!!".equalsIgnoreCase(postEffectNode.getText())) {
                        source.add(new Bytecode(context, push ? 0x8A : 0x89, "POST_LOGICAL_NOT" + (push ? " (push)" : "")));
                    }
                    else if ("!".equalsIgnoreCase(postEffectNode.getText())) {
                        source.add(new Bytecode(context, push ? 0x8C : 0x8B, "POST_NOT" + (push ? " (push)" : "")));
                    }
                    else {
                        throw new CompilerException("unsupported post effect " + postEffectNode.getText(), postEffectNode.getToken());
                    }
                }
            }
            else {
                if (expression instanceof Register) {
                    source.add(new RegisterOp(context, RegisterOp.Op.Read, popIndex, expression, index));
                }
                else if (expression instanceof ContextLiteral) {
                    source.add(new MemoryOp(context, ss, bb, MemoryOp.Op.Read, popIndex, expression, index));
                }
                else {
                    source.add(new VariableOp(context, VariableOp.Op.Read, popIndex, (Variable) expression, hasIndex, index));
                }
            }
        }

        return source;
    }

    List<Spin2Bytecode> leftAssign(Spin2Context context, Spin2StatementNode node, boolean push, boolean write) {
        Spin2StatementNode indexNode = null;
        Spin2StatementNode bitfieldNode = null;
        int index = 0;
        boolean popIndex = false;
        List<Spin2Bytecode> source = new ArrayList<Spin2Bytecode>();

        String[] s = node.getText().split("[\\.]");
        if (s.length == 2 && ("BYTE".equalsIgnoreCase(s[1]) || "WORD".equalsIgnoreCase(s[1]) || "LONG".equalsIgnoreCase(s[1]))) {
            Expression expression = context.getLocalSymbol(s[0]);
            if (expression == null) {
                throw new CompilerException("undefined symbol " + node.getText(), node.getToken());
            }

            int n = 0;
            if (n < node.getChildCount()) {
                indexNode = node.getChild(n++);
                popIndex = true;
                try {
                    Expression exp = buildConstantExpression(context, indexNode);
                    if (exp.isConstant()) {
                        index = exp.getNumber().intValue();
                        popIndex = false;
                    }
                } catch (Exception e) {
                    // Do nothing
                }
            }
            if (n < node.getChildCount()) {
                throw new CompilerException("unexpected " + node.getChild(n).getText(), node.getChild(n).getToken());
            }

            MemoryOp.Size ss = MemoryOp.Size.Long;
            if ("BYTE".equalsIgnoreCase(s[1])) {
                ss = MemoryOp.Size.Byte;
            }
            else if ("WORD".equalsIgnoreCase(s[1])) {
                ss = MemoryOp.Size.Word;
            }
            MemoryOp.Base bb = MemoryOp.Base.PBase;
            if (expression instanceof LocalVariable) {
                bb = MemoryOp.Base.DBase;
            }
            else if (expression instanceof Variable) {
                bb = MemoryOp.Base.VBase;
            }

            if (popIndex) {
                source.addAll(compileBytecodeExpression(context, indexNode, true));
            }
            source.add(new MemoryOp(context, ss, bb, push ? MemoryOp.Op.Setup : MemoryOp.Op.Write, popIndex, expression, index));
        }
        else if ("_".equalsIgnoreCase(node.getText())) {
            source.add(new Bytecode(context, 0x17, "POP"));
        }
        else if (",".equals(node.getText())) {
            for (int i = node.getChildCount() - 1; i >= 0; i--) {
                source.addAll(leftAssign(context, node.getChild(i), push, false));
            }
        }
        else if (node.getType() == Token.OPERATOR) {
            source.addAll(leftAssign(context, node.getChild(1), true, true));
            source.addAll(leftAssign(context, node.getChild(0), node.getChild(0).getType() == Token.OPERATOR, false));
        }
        else if ("BYTE".equalsIgnoreCase(node.getText()) || "WORD".equalsIgnoreCase(node.getText()) || "LONG".equalsIgnoreCase(node.getText())) {
            indexNode = null;
            bitfieldNode = null;

            source.addAll(compileBytecodeExpression(context, node.getChild(0), true));

            int n = 1;
            if (n < node.getChildCount()) {
                if (".".equals(node.getChild(n).getText())) {
                    n++;
                    if (n >= node.getChildCount()) {
                        throw new CompilerException("expected bitfield expression", node.getToken());
                    }
                    bitfieldNode = node.getChild(n++);
                }
                else if (!isPostEffect(node.getChild(n))) {
                    indexNode = node.getChild(n++);
                }
            }
            if (n < node.getChildCount()) {
                if (".".equals(node.getChild(n).getText())) {
                    if (bitfieldNode != null) {
                        throw new CompilerException("syntax error", node.getToken());
                    }
                    n++;
                    if (n >= node.getChildCount()) {
                        throw new CompilerException("expected bitfield expression", node.getToken());
                    }
                    bitfieldNode = node.getChild(n++);
                }
            }
            if (n < node.getChildCount()) {
                if (!isPostEffect(node.getChild(n))) {
                    if (indexNode != null) {
                        throw new CompilerException("syntax error", node.getToken());
                    }
                    indexNode = node.getChild(n++);
                }
            }
            if (n < node.getChildCount()) {
                throw new RuntimeException("syntax error " + node.getText());
            }

            if (indexNode != null) {
                source.addAll(compileBytecodeExpression(context, indexNode, true));
            }

            int bitfield = -1;
            if (bitfieldNode != null) {
                if ("..".equals(bitfieldNode.getText())) {
                    try {
                        Expression exp1 = buildConstantExpression(context, bitfieldNode.getChild(0));
                        Expression exp2 = buildConstantExpression(context, bitfieldNode.getChild(1));
                        if (exp1.isConstant() && exp2.isConstant()) {
                            int lowest = Math.min(exp1.getNumber().intValue(), exp2.getNumber().intValue());
                            int highest = Math.max(exp1.getNumber().intValue(), exp2.getNumber().intValue());
                            Expression exp = new Addbits(new NumberLiteral(lowest), new NumberLiteral(highest - lowest));
                            bitfield = exp.getNumber().intValue();
                        }
                    } catch (Exception e) {
                        // Do nothing
                    }

                    if (bitfield == -1) {
                        source.addAll(compileBytecodeExpression(context, bitfieldNode, true));
                        source.add(new Bytecode(context, new byte[] {
                            (byte) 0x9F, (byte) 0x94
                        }, "ADDBITS"));
                    }
                }
                else {
                    try {
                        Expression exp = buildConstantExpression(context, bitfieldNode);
                        if (exp.isConstant()) {
                            bitfield = exp.getNumber().intValue();
                        }
                    } catch (Exception e) {
                        // Do nothing
                    }

                    if (bitfield == -1) {
                        source.addAll(compileBytecodeExpression(context, bitfieldNode, true));
                    }
                }
            }

            MemoryOp.Size ss = MemoryOp.Size.Long;
            if ("BYTE".equalsIgnoreCase(node.getText())) {
                ss = MemoryOp.Size.Byte;
            }
            else if ("WORD".equalsIgnoreCase(node.getText())) {
                ss = MemoryOp.Size.Word;
            }
            MemoryOp.Op op = push || bitfieldNode != null ? MemoryOp.Op.Setup : MemoryOp.Op.Write;
            source.add(new MemoryOp(context, ss, MemoryOp.Base.Pop, op, indexNode != null));

            if (bitfieldNode != null) {
                source.add(new BitField(context, BitField.Op.Write, push, bitfield));
            }
        }
        else {
            Expression expression = context.getLocalSymbol(node.getText());
            if (expression == null) {
                throw new CompilerException("undefined symbol " + node.getText(), node.getToken());
            }

            boolean hasIndex = false;

            int n = 0;
            if (n < node.getChildCount()) {
                if (".".equals(node.getChild(n).getText())) {
                    n++;
                    if (n >= node.getChildCount()) {
                        throw new CompilerException("expected bitfield expression", node.getToken());
                    }
                    bitfieldNode = node.getChild(n++);
                }
                else {
                    indexNode = node.getChild(n++);
                    popIndex = true;
                    try {
                        Expression exp = buildConstantExpression(context, indexNode);
                        if (exp.isConstant()) {
                            index = exp.getNumber().intValue();
                            hasIndex = true;
                            popIndex = false;
                        }
                    } catch (Exception e) {
                        // Do nothing
                    }
                    if (n < node.getChildCount()) {
                        if (".".equals(node.getChild(n).getText())) {
                            n++;
                            if (n >= node.getChildCount()) {
                                throw new CompilerException("expected bitfield expression", node.getToken());
                            }
                            bitfieldNode = node.getChild(n++);
                        }
                    }
                }
            }
            if (n < node.getChildCount()) {
                throw new CompilerException("unexpected " + node.getChild(n).getText(), node.getChild(n).getToken());
            }

            int bitfield = -1;
            if (bitfieldNode != null) {
                if ("..".equals(bitfieldNode.getText())) {
                    try {
                        Expression exp1 = buildConstantExpression(context, bitfieldNode.getChild(0));
                        Expression exp2 = buildConstantExpression(context, bitfieldNode.getChild(1));
                        if (exp1.isConstant() && exp2.isConstant()) {
                            int lowest = Math.min(exp1.getNumber().intValue(), exp2.getNumber().intValue());
                            int highest = Math.max(exp1.getNumber().intValue(), exp2.getNumber().intValue());
                            Expression exp = new Addbits(new NumberLiteral(lowest), new NumberLiteral(highest - lowest));
                            bitfield = exp.getNumber().intValue();
                        }
                    } catch (Exception e) {
                        // Do nothing
                    }

                    if (bitfield == -1) {
                        source.addAll(compileBytecodeExpression(context, bitfieldNode, true));
                        source.add(new Bytecode(context, new byte[] {
                            (byte) 0x9F, (byte) 0x94
                        }, "ADDBITS"));
                    }
                }
                else {
                    try {
                        Expression exp = buildConstantExpression(context, bitfieldNode);
                        if (exp.isConstant()) {
                            bitfield = exp.getNumber().intValue();
                        }
                    } catch (Exception e) {
                        // Do nothing
                    }

                    if (bitfield == -1) {
                        source.addAll(compileBytecodeExpression(context, bitfieldNode, true));
                    }
                }
            }

            if (popIndex) {
                source.addAll(compileBytecodeExpression(context, indexNode, true));
            }

            if (expression instanceof Register) {
                source.add(new RegisterOp(context, bitfieldNode != null || push ? RegisterOp.Op.Setup : RegisterOp.Op.Write, popIndex, expression, index));
            }
            else if (expression instanceof ContextLiteral) {
                MemoryOp.Size ss = MemoryOp.Size.Long;
                MemoryOp.Base bb = MemoryOp.Base.PBase;
                if (expression instanceof LocalVariable) {
                    bb = MemoryOp.Base.DBase;
                }
                else if (expression instanceof Variable) {
                    bb = MemoryOp.Base.VBase;
                }
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

                source.add(new MemoryOp(context, ss, bb, bitfieldNode != null || push ? MemoryOp.Op.Setup : MemoryOp.Op.Write, popIndex, expression, index));
            }
            else {
                source.add(new VariableOp(context, bitfieldNode != null || push ? VariableOp.Op.Setup : VariableOp.Op.Write, popIndex, (Variable) expression, hasIndex, index));
            }

            if (bitfieldNode != null) {
                source.add(new BitField(context, BitField.Op.Write, push, bitfield));
            }
            else if (write) {
                source.add(new Bytecode(context, 0x82, "WRITE"));
            }
        }

        return source;
    }

    boolean isPostEffect(Spin2StatementNode node) {
        if (node.getChildCount() != 0) {
            return false;
        }
        String s = node.getText();
        return "++".equals(s) || "--".equals(s) || "!!".equals(s) || "!".equals(s) || "~".equals(s) || "~~".equals(s);
    }

    List<Spin2Bytecode> compileConstantExpression(Spin2Context context, Spin2StatementNode node) {
        try {
            Expression expression = buildConstantExpression(context, node);
            if (expression.isConstant()) {
                return Collections.singletonList(new Constant(context, expression));
            }
        } catch (Exception e) {

        }
        return compileBytecodeExpression(context, node, true);
    }

    Expression buildConstantExpression(Spin2Context context, Spin2StatementNode node) {
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
            if (expression.isConstant() && node.getChildCount() == 0) {
                return expression;
            }
            throw new RuntimeException("not a constant (" + expression + ")");
        }

        switch (node.getText().toUpperCase()) {
            case ">>":
                return new ShiftRight(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
            case "<<":
                return new ShiftLeft(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));

            case "&":
                return new And(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
            case "^":
                return new Xor(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
            case "|":
                return new Or(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));

            case "*":
            case "*.":
                return new Multiply(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
            case "/":
            case "/.":
                return new Divide(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
            case "//":
                return new Modulo(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
            case "+/":
                return new UnsignedDivide(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
            case "+//":
                return new UnsignedModulo(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
            case "SCA":
                return new Sca(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
            case "SCAS":
                return new Scas(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));

            case "+":
            case "+.":
                if (node.getChildCount() == 1) {
                    return buildConstantExpression(context, node.getChild(0));
                }
                return new Add(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
            case "-":
            case "-.":
                if (node.getChildCount() == 1) {
                    return new Negative(buildConstantExpression(context, node.getChild(0)));
                }
                return new Subtract(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));

            case "ADDBITS":
                return new Addbits(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
            case "ADDPINS":
                return new Addpins(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));

            case "&&":
            case "AND":
                return new LogicalAnd(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
            case "||":
            case "OR":
                return new LogicalOr(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
            case "^^":
            case "XOR":
                return new LogicalXor(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));

            case "<":
                return new LessThan(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
            case "<=":
                return new LessOrEquals(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
            case "==":
                return new Equals(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
            case "<>":
                return new NotEquals(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
            case ">=":
                return new GreaterOrEquals(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
            case ">":
                return new GreaterThan(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));

            case "!":
                if (node.getChildCount() == 1) {
                    return new Not(buildConstantExpression(context, node.getChild(0)));
                }
                throw new RuntimeException("unary operator with " + node.getChildCount() + " arguments");

            case "?": {
                Expression right = buildConstantExpression(context, node.getChild(1));
                if (!(right instanceof IfElse)) {
                    throw new RuntimeException("unsupported operator " + node.getText());
                }
                return new IfElse(buildConstantExpression(context, node.getChild(0)), ((IfElse) right).getTrueTerm(), ((IfElse) right).getFalseTerm());
            }
            case ":":
                return new IfElse(null, buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));

            case "TRUNC":
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("misplaced unary operator (" + node.getText() + ")");
                }
                return new Trunc(buildConstantExpression(context, node.getChild(0)));
            case "FSQRT":
            case "SQRT":
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("misplaced unary operator (" + node.getText() + ")");
                }
                return new Sqrt(buildConstantExpression(context, node.getChild(0)));
            case "ROUND":
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("misplaced unary operator (" + node.getText() + ")");
                }
                return new Round(buildConstantExpression(context, node.getChild(0)));
            case "FLOAT":
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("misplaced unary operator (" + node.getText() + ")");
                }
                return new NumberLiteral(buildConstantExpression(context, node.getChild(0)).getNumber().doubleValue());
            case "ABS":
            case "FABS":
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("misplaced unary operator (" + node.getText() + ")");
                }
                return new Abs(buildConstantExpression(context, node.getChild(0)));
            case "NAN":
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("misplaced unary operator (" + node.getText() + ")");
                }
                return new Nan(buildConstantExpression(context, node.getChild(0)));
        }

        throw new RuntimeException("unknown " + node.getText());
    }

    int getClockMode(int xinfreq, int clkfreq) {
        int divd;
        int zzzz = 11; // 0b10_11
        double e, post, mult, Fpfd, Fvco, Fout;
        double result_mult = 0;
        //double result_Fout = 0;
        int result_pppp = 0, result_divd = 0;
        double error = 1e9;

        if (clkfreq == 20000000) {
            return 0;
        }

        for (int pppp = 0; pppp <= 15; pppp++) {
            if (pppp == 0) {
                post = 1.0;
            }
            else {
                post = pppp * 2.0;
            }
            for (divd = 64; divd >= 1; --divd) {
                Fpfd = Math.round(xinfreq / (double) divd);
                mult = Math.round(clkfreq * post / Fpfd);
                Fvco = Math.round(Fpfd * mult);
                Fout = Math.round(Fvco / post);
                e = Math.abs(Fout - clkfreq);
                if ((e <= error) && (Fpfd >= 250000) && (mult <= 1024) && (Fvco > 99e6) && ((Fvco <= 201e6) || (Fvco <= clkfreq + 1e6))) {
                    result_divd = divd;
                    result_mult = mult;
                    result_pppp = (pppp - 1) & 15;
                    //result_Fout = Fout;
                    error = e;
                }
            }
        }
        if (error > 100000.0) {
            throw new RuntimeException(String.format("Unable to find clock settings for freq %d Hz with input freq %d Hz", clkfreq, xinfreq));
        }

        int D = result_divd - 1;
        int M = ((int) result_mult) - 1;
        int clkmode = zzzz | (result_pppp << 4) | (M << 8) | (D << 18) | (1 << 24);

        //int finalfreq = (int) Math.round(result_Fout);

        return clkmode;
    }

    public List<Spin2PAsmLine> getSource() {
        return source;
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

    Expression buildExpression(Node node, Spin2Context scope) {
        return buildExpression(node.getTokens(), scope);
    }

    Expression buildExpression(List<Token> tokens, Spin2Context scope) {
        Spin2ExpressionBuilder expressionBuilder = new Spin2ExpressionBuilder(scope, tokens);
        return expressionBuilder.getExpression();
    }

}
