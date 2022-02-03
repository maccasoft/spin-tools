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

import org.apache.commons.collections4.map.ListOrderedMap;

import com.maccasoft.propeller.CompilerMessage;
import com.maccasoft.propeller.SpinObject.LongDataObject;
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
import com.maccasoft.propeller.expressions.HubContextLiteral;
import com.maccasoft.propeller.expressions.Identifier;
import com.maccasoft.propeller.expressions.IfElse;
import com.maccasoft.propeller.expressions.LessOrEquals;
import com.maccasoft.propeller.expressions.LessThan;
import com.maccasoft.propeller.expressions.LocalVariable;
import com.maccasoft.propeller.expressions.LogicalAnd;
import com.maccasoft.propeller.expressions.LogicalOr;
import com.maccasoft.propeller.expressions.LogicalXor;
import com.maccasoft.propeller.expressions.Method;
import com.maccasoft.propeller.expressions.Modulo;
import com.maccasoft.propeller.expressions.Multiply;
import com.maccasoft.propeller.expressions.Negative;
import com.maccasoft.propeller.expressions.Not;
import com.maccasoft.propeller.expressions.NotEquals;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.expressions.Or;
import com.maccasoft.propeller.expressions.Register;
import com.maccasoft.propeller.expressions.Sca;
import com.maccasoft.propeller.expressions.Scas;
import com.maccasoft.propeller.expressions.ShiftLeft;
import com.maccasoft.propeller.expressions.ShiftRight;
import com.maccasoft.propeller.expressions.Subtract;
import com.maccasoft.propeller.expressions.Trunc;
import com.maccasoft.propeller.expressions.UnsignedDivide;
import com.maccasoft.propeller.expressions.UnsignedModulo;
import com.maccasoft.propeller.expressions.Variable;
import com.maccasoft.propeller.expressions.Xor;
import com.maccasoft.propeller.model.ConstantAssignEnumNode;
import com.maccasoft.propeller.model.ConstantAssignNode;
import com.maccasoft.propeller.model.ConstantSetEnumNode;
import com.maccasoft.propeller.model.ConstantsNode;
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.DataNode;
import com.maccasoft.propeller.model.LocalVariableNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.NodeVisitor;
import com.maccasoft.propeller.model.ObjectNode;
import com.maccasoft.propeller.model.ObjectsNode;
import com.maccasoft.propeller.model.ParameterNode;
import com.maccasoft.propeller.model.StatementNode;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.VariableNode;
import com.maccasoft.propeller.model.VariablesNode;
import com.maccasoft.propeller.spin2.Spin2Bytecode.Descriptor;
import com.maccasoft.propeller.spin2.Spin2Object.LinkDataObject;
import com.maccasoft.propeller.spin2.bytecode.Address;
import com.maccasoft.propeller.spin2.bytecode.Bytecode;
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

public class Spin2ObjectCompiler {

    public static class ObjectInfo {
        String fileName;
        Spin2Object object;

        long offset;

        public ObjectInfo(String fileName, Spin2Object object) {
            this.fileName = fileName;
            this.object = object;
        }
    }

    Spin2Context scope;
    Map<String, ObjectInfo> childObjects;

    List<Spin2PAsmLine> source = new ArrayList<Spin2PAsmLine>();
    List<Spin2Method> methods = new ArrayList<Spin2Method>();
    Map<String, ObjectInfo> objects = ListOrderedMap.listOrderedMap(new HashMap<String, ObjectInfo>());

    int varOffset = 4;
    int nested;

    boolean errors;
    List<CompilerMessage> messages = new ArrayList<CompilerMessage>();

    public Spin2ObjectCompiler(Spin2Context scope, Map<String, ObjectInfo> childObjects) {
        this.scope = new Spin2Context(scope);
        this.childObjects = childObjects;
    }

    public Spin2Object compileObject(Node root) {
        boolean hubMode = false;
        int address = 0, hubAddress = 0;
        Spin2Object object = new Spin2Object();

        for (Node node : root.getChilds()) {
            if (node instanceof ObjectsNode) {
                compileObjBlock(node);
            }
        }

        for (Node node : root.getChilds()) {
            if (node instanceof ConstantsNode) {
                compileConBlock(node, object);
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

        object.setClkFreq(_clkfreq);
        object.setClkMode(_clkmode);

        for (Node node : root.getChilds()) {
            if (node instanceof DataNode) {
                compileDatBlock(node);
            }
        }

        int offset = objects.size() * 2;
        for (Node node : root.getChilds()) {
            if ((node instanceof MethodNode) && "PUB".equalsIgnoreCase(((MethodNode) node).getType().getText())) {
                Spin2Method method = compileMethod((MethodNode) node);
                if (method != null) {
                    scope.addSymbol(method.getLabel(), new Method(method.getLabel(), method.getParametersCount(), method.getReturnsCount(), offset));
                    scope.addSymbol("@" + method.getLabel(), new Method(method.getLabel(), method.getParametersCount(), method.getReturnsCount(), offset));
                    object.addSymbol(method.getLabel(), new Method(method.getLabel(), method.getParametersCount(), method.getReturnsCount(), offset));
                    method.register();
                    offset++;
                    methods.add(method);
                }
            }
        }
        for (Node node : root.getChilds()) {
            if ((node instanceof MethodNode) && "PRI".equalsIgnoreCase(((MethodNode) node).getType().getText())) {
                Spin2Method method = compileMethod((MethodNode) node);
                if (method != null) {
                    scope.addSymbol(method.getLabel(), new Method(method.getLabel(), method.getParametersCount(), method.getReturnsCount(), offset));
                    scope.addSymbol("@" + method.getLabel(), new Method(method.getLabel(), method.getParametersCount(), method.getReturnsCount(), offset));
                    method.register();
                    offset++;
                    methods.add(method);
                }
            }
        }

        int objectIndex = 1;
        for (Entry<String, ObjectInfo> infoEntry : objects.entrySet()) {
            String name = infoEntry.getKey();
            Spin2Object obj = infoEntry.getValue().object;
            for (Entry<String, Expression> objEntry : obj.getSymbols().entrySet()) {
                if (objEntry.getValue() instanceof Method) {
                    String qualifiedName = name + "." + objEntry.getKey();
                    Method method = ((Method) objEntry.getValue()).copy();
                    method.setObject(objectIndex);
                    scope.addSymbol(qualifiedName, method);
                }
            }
            objectIndex++;
        }

        for (Spin2Method method : methods) {
            for (Spin2MethodLine line : method.getLines()) {
                try {
                    compileLine(line);
                } catch (CompilerMessage e) {
                    logMessage(e);
                } catch (Exception e) {
                    logMessage(new CompilerMessage(e, line.getData()));
                }
            }
        }

        object.writeComment("Object header");

        for (Entry<String, ObjectInfo> infoEntry : objects.entrySet()) {
            ObjectInfo info = infoEntry.getValue();
            LinkDataObject linkData = new LinkDataObject(info.object, 0);
            object.links.add(linkData);
            object.write(linkData);
            object.writeLong(varOffset, String.format("Variables @ $%05X", varOffset));
            varOffset += info.object.getVarSize();
        }

        LongDataObject[] ld = new LongDataObject[methods.size() + 1];

        if (methods.size() != 0) {
            scope.addSymbol("@CLKMODE", new NumberLiteral(0x40));
            scope.addSymbol("@clkmode", new NumberLiteral(0x40));
            scope.addSymbol("@CLKFREQ", new NumberLiteral(0x44));
            scope.addSymbol("@clkfreq", new NumberLiteral(0x44));

            int index = 0;
            for (Spin2Method method : methods) {
                ld[index] = object.writeLong(0, "Method " + method.getLabel());
                index++;
            }
            ld[index] = object.writeLong(0, "End");
        }

        hubMode = true;
        hubAddress = object.getSize();

        for (Spin2PAsmLine line : source) {
            line.getScope().setHubAddress(hubAddress);
            if (line.getInstructionFactory() instanceof Orgh) {
                hubMode = true;
            }
            if ((line.getInstructionFactory() instanceof Org) || (line.getInstructionFactory() instanceof Res)) {
                hubMode = false;
                hubAddress = (hubAddress + 3) & ~3;
            }
            boolean isCogCode = address < 0x200;
            if (line.getInstructionFactory() instanceof Fit) {
                ((Fit) line.getInstructionFactory()).setDefaultLimit(isCogCode ? 0x1F0 : 0x400);
            }

            try {
                address = line.resolve(hubMode ? hubAddress : address);
                hubAddress += line.getInstructionObject().getSize();
            } catch (CompilerMessage e) {
                logMessage(e);
            } catch (Exception e) {
                logMessage(new CompilerMessage(e, line.getData()));
            }

            if (line.getInstructionFactory() instanceof Org) {
                isCogCode = address < 0x200;
            }
            if (hubMode) {
                if (address > hubAddress) {
                    hubAddress = address;
                }
                else {
                    address = hubAddress;
                }
            }
            else {
                if (isCogCode && address > 0x1F0) {
                    logMessage(new CompilerMessage("cog code limit exceeded by " + (address - 0x1F0) + " long(s)", line.getData()));
                }
                else if (!isCogCode && address > 0x400) {
                    logMessage(new CompilerMessage("lut code limit exceeded by " + (address - 0x400) + " long(s)", line.getData()));
                }
            }
        }

        for (Spin2PAsmLine line : source) {
            hubAddress = line.getScope().getHubAddress();
            if (object.getSize() < hubAddress) {
                object.writeBytes(new byte[hubAddress - object.getSize()], "(filler)");
            }
            try {
                object.writeBytes(line.getScope().getAddress(), line.getInstructionObject().getBytes(), line.toString());
            } catch (Exception e) {
                logMessage(new CompilerMessage(e, line.getData()));
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

                ld[index].setValue(value | 0x80000000L);
                ld[index].setText(String.format("Method %s @ $%05X (%d parameters, %d returns)", method.getLabel(), object.getSize(), method.getParametersCount(), method.getReturnsCount()));
                method.writeTo(object);
                index++;
            }
            ld[index].setValue(object.getSize());

            object.alignToLong();
        }

        object.setVarSize(varOffset);

        return object;
    }

    void compileConBlock(Node parent, Spin2Object object) {

        parent.accept(new NodeVisitor() {
            int enumValue = 0, enumIncrement = 1;

            @Override
            public void visitConstantAssign(ConstantAssignNode node) {
                String name = node.identifier.getText();
                try {
                    Expression expression = buildExpression(node.expression, scope);
                    expression.setData(node);
                    try {
                        scope.addSymbol(name, expression);
                        object.addSymbol(name, expression);
                    } catch (CompilerMessage e) {
                        logMessage(e);
                    } catch (Exception e) {
                        logMessage(new CompilerMessage(e, node.identifier));
                    }
                } catch (CompilerMessage e) {
                    logMessage(e);
                } catch (Exception e) {
                    logMessage(new CompilerMessage(e, node.expression));
                }
            }

            @Override
            public void visitConstantSetEnum(ConstantSetEnumNode node) {
                Expression expression = buildExpression(node.start, scope);
                enumValue = expression.getNumber().intValue();
                if (node.step != null) {
                    expression = buildExpression(node.step, scope);
                    enumIncrement = expression.getNumber().intValue();
                }
                else {
                    enumIncrement = 1;
                }
            }

            @Override
            public void visitConstantAssignEnum(ConstantAssignEnumNode node) {
                try {
                    scope.addSymbol(node.identifier.getText(), new NumberLiteral(enumValue));
                    object.addSymbol(node.identifier.getText(), new NumberLiteral(enumValue));
                } catch (Exception e) {
                    logMessage(new CompilerMessage(e, node.identifier));
                    return;
                }
                if (node.multiplier != null) {
                    Expression expression = buildExpression(node.multiplier, scope);
                    enumValue += enumIncrement * expression.getNumber().intValue();
                }
                else {
                    enumValue += enumIncrement;
                }
            }

        });
    }

    void compileVarBlocks(Node root) {
        root.accept(new NodeVisitor() {

            String type = "LONG";

            @Override
            public void visitVariables(VariablesNode node) {
                type = "LONG";
            }

            @Override
            public void visitVariable(VariableNode node) {
                if (node.identifier == null) {
                    return;
                }

                if (node.type != null) {
                    type = node.type.getText().toUpperCase();
                }

                Expression size = new NumberLiteral(1);
                if (node.size != null) {
                    size = buildExpression(node.size.getTokens(), scope);
                }

                try {
                    scope.addSymbol(node.identifier.getText(), new Variable(type, node.identifier.getText(), size, varOffset));
                    scope.addSymbol("@" + node.identifier.getText(), new Variable(type, node.identifier.getText(), size, varOffset));

                    int varSize = size.getNumber().intValue();
                    if ("WORD".equalsIgnoreCase(type)) {
                        varSize = varSize * 2;
                    }
                    else if (!"BYTE".equalsIgnoreCase(type)) {
                        varSize = varSize * 4;
                    }
                    varOffset += varSize;
                } catch (Exception e) {
                    logMessage(new CompilerMessage(e, node.identifier));
                }
            }

        });
    }

    void compileObjBlock(Node parent) {

        for (Node child : parent.getChilds()) {
            ObjectNode node = (ObjectNode) child;
            if (node.name != null && node.file != null) {
                String fileName = node.file.getText().substring(1, node.file.getText().length() - 1) + ".spin2";

                ObjectInfo info = childObjects.get(fileName);
                if (info == null) {
                    logMessage(new CompilerMessage("object \"" + fileName + "\" not found", node.file));
                    continue;
                }

                objects.put(node.name.getText(), info);

                for (Entry<String, Expression> entry : info.object.getSymbols().entrySet()) {
                    if (!(entry.getValue() instanceof Method)) {
                        String qualifiedName = node.name.getText() + "." + entry.getKey();
                        scope.addSymbol(qualifiedName, entry.getValue());
                    }
                }
            }

        }
    }

    Map<Spin2PAsmLine, Spin2Context> pendingAlias = new HashMap<Spin2PAsmLine, Spin2Context>();

    void compileDatBlock(Node parent) {
        Spin2Context savedContext = scope;
        nested = 0;

        for (Node child : parent.getChilds()) {
            DataLineNode node = (DataLineNode) child;
            try {
                Spin2PAsmLine pasmLine = compileDataLine(node);
                pasmLine.setData(node);
                source.addAll(pasmLine.expand());
            } catch (CompilerMessage e) {
                logMessage(e);
            } catch (Exception e) {
                logMessage(new CompilerMessage(e, node.instruction));
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

        Spin2Context localScope = new Spin2Context(scope);

        for (ParameterNode param : node.parameters) {
            int index = 0;
            String prefix = null;
            Expression expression = null, count = null;

            if (param.getText().toUpperCase().contains("PTRA") || param.getText().toUpperCase().contains("PTRB")) {
                expression = new Identifier(param.getText(), scope);
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
                        expression = buildExpression(param.getTokens().subList(index, param.getTokens().size()), localScope);
                    } catch (Exception e) {
                        throw new CompilerMessage(e, param);
                    }
                }
            }
            if (param.count != null) {
                try {
                    count = buildExpression(param.count, localScope);
                } catch (Exception e) {
                    throw new CompilerMessage(e, param.count);
                }
            }
            parameters.add(new Spin2PAsmExpression(prefix, expression, count));
        }

        Spin2PAsmLine pasmLine = new Spin2PAsmLine(localScope, label, condition, mnemonic, parameters, modifier);

        try {
            if ("FILE".equalsIgnoreCase(mnemonic)) {
                String fileName = parameters.get(0).toString().substring(1);
                fileName = fileName.substring(0, fileName.length() - 1);
                byte[] data = getBinaryFile(fileName);
                if (data == null) {
                    throw new CompilerMessage("file \"" + fileName + "\" not found", node.parameters.get(0));
                }
                pasmLine.setInstructionObject(new FileInc(pasmLine.getScope(), data));
            }
        } catch (RuntimeException e) {
            throw new CompilerMessage(e, node);
        }

        if (pasmLine.getLabel() != null) {
            try {
                if (!pasmLine.isLocalLabel() && nested > 0) {
                    scope = scope.getParent();
                    nested--;
                }
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
                scope.addSymbol(pasmLine.getLabel(), new DataVariable(pasmLine.getScope(), type));
                scope.addSymbol("@" + pasmLine.getLabel(), new HubContextLiteral(pasmLine.getScope()));

                if (pasmLine.getMnemonic() == null) {
                    if (!pasmLine.isLocalLabel()) {
                        pendingAlias.put(pasmLine, scope);
                    }
                }
                else if (pendingAlias.size() != 0) {
                    for (Entry<Spin2PAsmLine, Spin2Context> entry : pendingAlias.entrySet()) {
                        Spin2PAsmLine line = entry.getKey();
                        Spin2Context context = entry.getValue();
                        context.addOrUpdateSymbol(line.getLabel(), new DataVariable(line.getScope(), type));
                        context.addOrUpdateSymbol("@" + line.getLabel(), new HubContextLiteral(line.getScope()));
                    }
                    pendingAlias.clear();
                }

                if (!pasmLine.isLocalLabel()) {
                    scope = new Spin2Context(scope);
                    nested++;
                }
            } catch (RuntimeException e) {
                throw new CompilerMessage(e, node.label);
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
                context.addOrUpdateSymbol("@" + line.getLabel(), new HubContextLiteral(line.getScope()));
            }
            pendingAlias.clear();
        }

        return pasmLine;
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

        localScope.addSymbol("recv", new Register(0x1D2));
        localScope.addSymbol("send", new Register(0x1D3));

        localScope.addSymbol("pr0", new Register(0x1D8));
        localScope.addSymbol("pr1", new Register(0x1D9));
        localScope.addSymbol("pr2", new Register(0x1DA));
        localScope.addSymbol("pr3", new Register(0x1DB));
        localScope.addSymbol("pr4", new Register(0x1DC));
        localScope.addSymbol("pr5", new Register(0x1DD));
        localScope.addSymbol("pr6", new Register(0x1DE));
        localScope.addSymbol("pr7", new Register(0x1DF));

        localScope.addSymbol("RECV", new Register(0x1D2));
        localScope.addSymbol("SEND", new Register(0x1D3));

        localScope.addSymbol("PR0", new Register(0x1D8));
        localScope.addSymbol("PR1", new Register(0x1D9));
        localScope.addSymbol("PR2", new Register(0x1DA));
        localScope.addSymbol("PR3", new Register(0x1DB));
        localScope.addSymbol("PR4", new Register(0x1DC));
        localScope.addSymbol("PR5", new Register(0x1DD));
        localScope.addSymbol("PR6", new Register(0x1DE));
        localScope.addSymbol("PR7", new Register(0x1DF));

        int offset = 0;
        for (Node child : node.getParameters()) {
            String name = child.getText();
            Expression expression = localScope.getLocalSymbol(name);
            if (expression instanceof LocalVariable) {
                logMessage(new CompilerMessage("symbol " + name + " already defined", child));
                continue;
            }
            else if (expression != null) {
                logMessage(new CompilerMessage(CompilerMessage.WARNING, "parameter " + name + " hides global variable", child));
            }
            LocalVariable var = new LocalVariable("LONG", name, new NumberLiteral(1), offset);
            localScope.addSymbol(name, var);
            localScope.addSymbol("@" + name, var);
            parameters.add(var);
            offset += 4;
        }

        for (Node child : node.getReturnVariables()) {
            String name = child.getText();
            Expression expression = localScope.getLocalSymbol(name);
            if (expression instanceof LocalVariable) {
                logMessage(new CompilerMessage("symbol " + name + " already defined", child));
                continue;
            }
            else if (expression != null) {
                logMessage(new CompilerMessage(CompilerMessage.WARNING, "return variable " + name + " hides global variable", child));
            }
            LocalVariable var = new LocalVariable("LONG", name, new NumberLiteral(1), offset);
            localScope.addSymbol(name, var);
            localScope.addSymbol("@" + name, var);
            returns.add(var);
            offset += 4;
        }

        for (LocalVariableNode child : node.getLocalVariables()) {
            String name = child.getIdentifier().getText();
            Expression expression = localScope.getLocalSymbol(name);
            if (expression instanceof LocalVariable) {
                logMessage(new CompilerMessage("symbol " + name + " already defined", child.getIdentifier()));
                continue;
            }
            else if (expression != null) {
                logMessage(new CompilerMessage(CompilerMessage.WARNING, "local variable " + name + " hides global variable", child.getIdentifier()));
            }

            String type = "LONG";
            if (child.type != null) {
                type = child.type.getText().toUpperCase();
            }
            Expression size = null;
            if (child.size != null) {
                try {
                    size = buildExpression(child.size.getTokens(), scope);
                    if (!size.isConstant()) {
                        logMessage(new CompilerMessage("expression is not constant", child.size));
                        continue;
                    }
                } catch (Exception e) {
                    logMessage(new CompilerMessage("expression syntax error", child.size));
                    continue;
                }
            }
            LocalVariable var = new LocalVariable(type, name, size, offset);
            localScope.addSymbol(name, var);
            localScope.addSymbol("@" + name, var);
            localVariables.add(var);

            int count = 4;
            if ("BYTE".equalsIgnoreCase(type)) {
                count = 1;
            }
            if ("WORD".equalsIgnoreCase(type)) {
                count = 2;
            }
            if (size != null) {
                count = count * size.getNumber().intValue();
            }
            offset += count;
        }

        Spin2Method method = new Spin2Method(localScope, node.name.getText(), parameters, returns, localVariables);
        method.setComment(node.getText());

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
                            throw new CompilerMessage("unexpected argument", iter.next());
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
                                        throw new CompilerMessage("expected TO", token);
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
                    else {
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

                } catch (CompilerMessage e) {
                    logMessage(e);
                } catch (Exception e) {
                    logMessage(new CompilerMessage(e, node));
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

                line.setData("pop", Integer.valueOf(12));

                String varText = line.getArgument(0).getText();
                Expression expression = line.getScope().getLocalSymbol(varText);
                if (expression == null) {
                    throw new CompilerMessage("undefined symbol " + varText, line.getArgument(0).getToken());
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
                throw new CompilerMessage("undefined symbol " + varText, repeat.getArgument(0).getToken());
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
                    if (pop != 0) {
                        pop += 4;
                    }
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
                if (pop != 0) {
                    pop += 4;
                }
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
                        os.write(0x18);
                        os.write(Constant.wrVars(pop));
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
        else if ("CASE_DONE".equalsIgnoreCase(text)) {
            line.addSource(new Bytecode(line.getScope(), 0x1E, text));
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
                    throw new CompilerMessage("expected " + desc.parameters + " argument(s), found " + line.getArgumentsCount(), line.getData());
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
                } catch (CompilerMessage e) {
                    logMessage(e);
                } catch (Exception e) {
                    logMessage(new CompilerMessage(e, arg.getData()));
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
            } catch (CompilerMessage e) {
                logMessage(e);
            } catch (Exception e) {
                logMessage(new CompilerMessage(e, child.getData()));
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
                    throw new CompilerMessage("invalid method " + methodNode.getText(), methodNode.getToken());
                }
                if (methodNode.getChildCount() != ((Method) expression).getArgumentsCount()) {
                    throw new CompilerMessage("expected " + ((Method) expression).getArgumentsCount() + " argument(s), found " + methodNode.getChildCount(), methodNode.getToken());
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
                // Ignored
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
                                throw new CompilerMessage("expression is not constant", child.getToken());
                            }
                            sb.append((char) expression.getNumber().intValue());
                        } catch (Exception e) {
                            throw new CompilerMessage("expression is not constant", child.getToken());
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
                String s = node.getText().substring(1);
                s = s.substring(0, s.length() - 1);
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
                Expression expression = context.getLocalSymbol(node.getChild(0).getText());
                if (!(expression instanceof Method)) {
                    throw new CompilerMessage("symbol " + node.getChild(0).getText() + " is not a method", node.getChild(0).getToken());
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
                    throw new CompilerMessage("unsupported operation on " + node.getChild(0).getText(), node.getChild(0).getToken());
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
                    throw new CompilerMessage("unsupported operation on " + node.getChild(0).getText(), node.getChild(0).getToken());
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
                    throw new CompilerMessage("unsupported operation on " + node.getChild(0).getText(), node.getChild(0).getToken());
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
                boolean indexed = false;

                source.addAll(compileBytecodeExpression(context, node.getChild(0), true));
                if (node.getChildCount() > 1) {
                    source.addAll(compileBytecodeExpression(context, node.getChild(1), true));
                    indexed = true;
                    if (node.getChildCount() > 2) {
                        throw new RuntimeException("expression syntax error " + node.getText());
                    }
                }

                StringBuilder sb = new StringBuilder(node.getText().toUpperCase());
                sb.append(push ? "_READ" : "_WRITE");
                sb.append(indexed ? "_INDEXED" : "");

                if ("BYTE".equalsIgnoreCase(node.getText())) {
                    source.add(new Bytecode(context, new byte[] {
                        indexed ? (byte) 0x62 : (byte) 0x65, push ? (byte) 0x80 : (byte) 0x81
                    }, node.getText().toUpperCase() + (push ? "_READ" : "_WRITE")));
                }
                else if ("WORD".equalsIgnoreCase(node.getText())) {
                    source.add(new Bytecode(context, new byte[] {
                        indexed ? (byte) 0x63 : (byte) 0x66, push ? (byte) 0x80 : (byte) 0x81
                    }, node.getText().toUpperCase() + (push ? "_READ" : "_WRITE")));
                }
                else if ("LONG".equalsIgnoreCase(node.getText())) {
                    source.add(new Bytecode(context, new byte[] {
                        indexed ? (byte) 0x64 : (byte) 0x67, push ? (byte) 0x80 : (byte) 0x81
                    }, node.getText().toUpperCase() + (push ? "_READ" : "_WRITE")));
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
                    if (expression instanceof HubContextLiteral) {
                        expression = context.getLocalSymbol(s[0].substring(1));
                    }
                    if (expression == null) {
                        throw new CompilerMessage("undefined symbol " + node.getText(), node.getToken());
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
                            throw new CompilerMessage("unhandled post effect " + postEffectNode.getText(), postEffectNode.getToken());
                        }
                    }
                }
                else {
                    Expression expression = context.getLocalSymbol(node.getText());
                    if (expression instanceof HubContextLiteral) {
                        expression = context.getLocalSymbol(node.getText().substring(1));
                    }
                    if (expression == null) {
                        throw new CompilerMessage("undefined symbol " + node.getText(), node.getToken());
                    }
                    if (node.getText().startsWith("@")) {
                        if (expression instanceof Method) {
                            source.add(new Bytecode(context, new byte[] {
                                (byte) 0x11,
                                (byte) ((Method) expression).getOffset()
                            }, "SUB_ADDRESS (" + ((Method) expression).getOffset() + ")"));
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
                            }

                            source.add(new VariableOp(context, VariableOp.Op.Address, popIndex, (Variable) expression, hasIndex, index));
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
                            source.add(new MemoryOp(context, ss, bb, MemoryOp.Op.Address, popIndex, expression, index));

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
                                    throw new CompilerMessage("unhandled post effect " + postEffectNode.getText(), postEffectNode.getToken());
                                }
                            }
                        }
                    }
                    else if (expression instanceof Method) {
                        int parameters = ((Method) expression).getArgumentsCount();
                        if (node.getChildCount() != parameters) {
                            throw new RuntimeException("expected " + parameters + " argument(s), found " + node.getChildCount());
                        }
                        source.add(new Bytecode(context, push ? 0x01 : 0x00, "ANCHOR"));
                        for (int i = 0; i < parameters; i++) {
                            source.addAll(compileConstantExpression(context, node.getChild(i)));
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
                            }, "CALL_SUB (" + ((Method) expression).getOffset() + ")"));
                        }
                    }
                    else if (expression instanceof Register) {
                        source.addAll(compileVariableRead(context, expression, node, push));
                    }
                    else if (expression instanceof Variable) {
                        source.addAll(compileVariableRead(context, expression, node, push));
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
        } catch (CompilerMessage e) {
            logMessage(e);
        } catch (Exception e) {
            logMessage(new CompilerMessage(e, node.getToken()));
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
                    throw new CompilerMessage("invalid bitfield expression", node.getToken());
                }
                n++;
                if (n >= node.getChildCount()) {
                    throw new CompilerMessage("expected bitfield expression", node.getToken());
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
            throw new CompilerMessage("unexpected " + node.getChild(n).getText(), node.getChild(n).getToken());
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

            ByteArrayOutputStream os = new ByteArrayOutputStream();

            StringBuilder sb = new StringBuilder();
            sb.append("BITFIELD_");

            try {
                if (bitfield == -1) {
                    os.write(0xDE); // Read (pop)
                }
                else {
                    if (bitfield >= 0 && bitfield <= 15) {
                        os.write(0xE0 + bitfield);
                    }
                    else if (bitfield >= 16 && bitfield <= 31) {
                        os.write(0xF0 + (bitfield - 16));
                    }
                    else {
                        os.write(0xDF);
                        os.write(Constant.wrVar(bitfield));
                    }
                }
            } catch (Exception e) {
                // Do nothing
            }

            if (postEffectNode != null) {
                if ("~".equalsIgnoreCase(postEffectNode.getText()) || "~~".equalsIgnoreCase(postEffectNode.getText())) {
                    os.write(0x8D);
                    sb.append("SWAP");
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
                    throw new CompilerMessage("unsupported post effect " + postEffectNode.getText(), postEffectNode.getToken());
                }
            }
            else {
                os.write(0x80);
                sb.append("READ");
            }

            if (bitfield == -1) {
                sb.append(" (pop)");
            }
            else {
                if (bitfield >= 0 && bitfield <= 15) {
                    sb.append(" (short)");
                }
                else if (bitfield >= 16 && bitfield <= 31) {
                    sb.append(" (short)");
                }
            }
            if (postEffectNode != null && push) {
                sb.append(" (push)");
            }

            source.add(new Bytecode(context, os.toByteArray(), sb.toString()));
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
                        throw new CompilerMessage("unsupported post effect " + postEffectNode.getText(), postEffectNode.getToken());
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
                throw new CompilerMessage("undefined symbol " + node.getText(), node.getToken());
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
                throw new CompilerMessage("unexpected " + node.getChild(n).getText(), node.getChild(n).getToken());
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
            boolean indexed = false;

            source.addAll(compileBytecodeExpression(context, node.getChild(0), true));
            if (node.getChildCount() > 1) {
                source.addAll(compileBytecodeExpression(context, node.getChild(1), true));
                indexed = true;
                if (node.getChildCount() > 2) {
                    throw new RuntimeException("expression syntax error " + node.getText());
                }
            }

            StringBuilder sb = new StringBuilder(node.getText().toUpperCase());
            sb.append(push ? "_MODIFY" : "_WRITE");
            sb.append(indexed ? "_INDEXED" : "");

            if (push) {
                if ("BYTE".equalsIgnoreCase(node.getText())) {
                    source.add(new Bytecode(context, new byte[] {
                        indexed ? (byte) 0x62 : (byte) 0x65
                    }, sb.toString()));
                }
                else if ("WORD".equalsIgnoreCase(node.getText())) {
                    source.add(new Bytecode(context, new byte[] {
                        indexed ? (byte) 0x63 : (byte) 0x66
                    }, sb.toString()));
                }
                else if ("LONG".equalsIgnoreCase(node.getText())) {
                    source.add(new Bytecode(context, new byte[] {
                        indexed ? (byte) 0x64 : (byte) 0x67
                    }, sb.toString()));
                }
            }
            else {
                if ("BYTE".equalsIgnoreCase(node.getText())) {
                    source.add(new Bytecode(context, new byte[] {
                        indexed ? (byte) 0x62 : (byte) 0x65, (byte) 0x81
                    }, sb.toString()));
                }
                else if ("WORD".equalsIgnoreCase(node.getText())) {
                    source.add(new Bytecode(context, new byte[] {
                        indexed ? (byte) 0x63 : (byte) 0x66, (byte) 0x81
                    }, sb.toString()));
                }
                else if ("LONG".equalsIgnoreCase(node.getText())) {
                    source.add(new Bytecode(context, new byte[] {
                        indexed ? (byte) 0x64 : (byte) 0x67, (byte) 0x81
                    }, sb.toString()));
                }
            }
        }
        else {
            Expression expression = context.getLocalSymbol(node.getText());
            if (expression == null) {
                throw new CompilerMessage("undefined symbol " + node.getText(), node.getToken());
            }

            boolean hasIndex = false;

            int n = 0;
            if (n < node.getChildCount()) {
                if (".".equals(node.getChild(n).getText())) {
                    n++;
                    if (n >= node.getChildCount()) {
                        throw new CompilerMessage("expected bitfield expression", node.getToken());
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
                                throw new CompilerMessage("expected bitfield expression", node.getToken());
                            }
                            bitfieldNode = node.getChild(n++);
                        }
                    }
                }
            }
            if (n < node.getChildCount()) {
                throw new CompilerMessage("unexpected " + node.getChild(n).getText(), node.getChild(n).getToken());
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
                StringBuilder sb = new StringBuilder();
                sb.append("BITFIELD_");

                ByteArrayOutputStream os = new ByteArrayOutputStream();

                try {
                    if (bitfield == -1) {
                        os.write(0xDE); // Read (pop)
                    }
                    else {
                        if (bitfield >= 0 && bitfield <= 15) {
                            os.write(0xE0 + bitfield);
                        }
                        else if (bitfield >= 16 && bitfield <= 31) {
                            os.write(0xF0 + (bitfield - 16));
                        }
                        else {
                            os.write(0xDF);
                            os.write(Constant.wrVar(bitfield));
                        }
                    }
                } catch (Exception e) {
                    // Do nothing
                }

                os.write(push ? 0x82 : 0x81);
                sb.append("WRITE");

                if (bitfield == -1) {
                    sb.append(" (pop)");
                }
                else {
                    if (bitfield >= 0 && bitfield <= 15) {
                        sb.append(" (short)");
                    }
                    else if (bitfield >= 16 && bitfield <= 31) {
                        sb.append(" (short)");
                    }
                }
                if (push) {
                    sb.append(" (push)");
                }

                source.add(new Bytecode(context, os.toByteArray(), sb.toString()));
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
            if (expression.isConstant()) {
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
                return new Multiply(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
            case "/":
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
                if (node.getChildCount() == 1) {
                    return buildConstantExpression(context, node.getChild(0));
                }
                return new Add(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
            case "-":
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
            throw new RuntimeException(String.format("Unable to find clock settings for freq %f Hz with input freq %f Hz", clkfreq, xinfreq));
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

    protected void logMessage(CompilerMessage message) {
        if (message.type == CompilerMessage.ERROR) {
            errors = true;
        }
        messages.add(message);
    }

    public boolean hasErrors() {
        return errors;
    }

    public List<CompilerMessage> getMessages() {
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