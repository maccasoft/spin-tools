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
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.TreeMap;

import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.commons.collections4.map.ListOrderedMap;

import com.maccasoft.propeller.Compiler.ObjectInfo;
import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.ObjectCompiler;
import com.maccasoft.propeller.SpinObject.LinkDataObject;
import com.maccasoft.propeller.SpinObject.LongDataObject;
import com.maccasoft.propeller.expressions.Add;
import com.maccasoft.propeller.expressions.BinaryOperator;
import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.ContextLiteral;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.LocalVariable;
import com.maccasoft.propeller.expressions.Method;
import com.maccasoft.propeller.expressions.Multiply;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.expressions.Register;
import com.maccasoft.propeller.expressions.SpinObject;
import com.maccasoft.propeller.expressions.Variable;
import com.maccasoft.propeller.model.ConstantNode;
import com.maccasoft.propeller.model.ConstantsNode;
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.DataNode;
import com.maccasoft.propeller.model.DirectiveNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.NodeVisitor;
import com.maccasoft.propeller.model.ObjectNode;
import com.maccasoft.propeller.model.StatementNode;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.TokenIterator;
import com.maccasoft.propeller.model.VariableNode;
import com.maccasoft.propeller.model.VariablesNode;
import com.maccasoft.propeller.spin1.Spin1ExpressionBuilder;
import com.maccasoft.propeller.spin1.Spin1Model;
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
import com.maccasoft.propeller.spin2.instructions.Alignl;
import com.maccasoft.propeller.spin2.instructions.Alignw;
import com.maccasoft.propeller.spin2.instructions.Empty;
import com.maccasoft.propeller.spin2.instructions.Fit;
import com.maccasoft.propeller.spin2.instructions.Org;
import com.maccasoft.propeller.spin2.instructions.Orgh;
import com.maccasoft.propeller.spin2.instructions.Res;
import com.maccasoft.propeller.spin2.instructions.Word;

public class Spin2ObjectCompiler extends Spin2BytecodeCompiler {

    List<Variable> variables = new ArrayList<>();
    List<Spin2Method> methods = new ArrayList<>();
    Map<String, ObjectInfo> objects = ListOrderedMap.listOrderedMap(new HashMap<>());

    int objectVarSize;

    Map<String, Expression> publicSymbols;
    List<LinkDataObject> objectLinks = new ArrayList<>();

    public Spin2ObjectCompiler(Spin2Compiler compiler, File file) {
        this(compiler, null, file);
    }

    public Spin2ObjectCompiler(Spin2Compiler compiler, ObjectCompiler parent, File file) {
        super(new Spin2GlobalContext(compiler.isCaseSensitive()), compiler, parent, file);

        scope.addDefinition("__P1__", new NumberLiteral(0));
        scope.addDefinition("__P2__", new NumberLiteral(1));
        scope.addDefinition("__SPINTOOLS__", new NumberLiteral(1));
        scope.addDefinition("__debug__", new NumberLiteral(compiler.isDebugEnabled() ? 1 : 0));
    }

    @Override
    public Spin2Object compileObject(Node root) {
        compileStep1(root);
        return generateObject(0);
    }

    @Override
    public void compileStep1(Node root) {
        publicSymbols = compiler.isCaseSensitive() ? new HashMap<>() : new CaseInsensitiveMap<>();

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
            public boolean visitConstants(ConstantsNode node) {
                enumValue = new NumberLiteral(0);
                enumIncrement = new NumberLiteral(1);
                return true;
            }

            @Override
            public void visitConstant(ConstantNode node) {
                if (!conditionStack.isEmpty() && conditionStack.peek().skip) {
                    excludedNodes.add(node);
                }
                if (!skipNode(node)) {
                    compileConstant(node);
                }
            }

            @Override
            public void visitVariable(VariableNode node) {
                if (!conditionStack.isEmpty() && conditionStack.peek().skip) {
                    excludedNodes.add(node);
                }
            }

            @Override
            public void visitObject(ObjectNode node) {
                if (!conditionStack.isEmpty() && conditionStack.peek().skip) {
                    excludedNodes.add(node);
                }
                if (!skipNode(node)) {
                    compileObject(node);
                }
            }

            @Override
            public boolean visitMethod(MethodNode node) {
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

            @Override
            public void visitDataLine(DataLineNode node) {
                if (!conditionStack.isEmpty() && conditionStack.peek().skip) {
                    excludedNodes.add(node);
                }
            }

        });

        while (!conditionStack.isEmpty()) {
            Condition c = conditionStack.pop();
            logMessage(new CompilerException("unbalanced conditional directive", c.node));
        }

        computeClockMode();

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
            if (skipNode(node)) {
                continue;
            }
            if ((node instanceof MethodNode) && "PUB".equalsIgnoreCase(((MethodNode) node).getType().getText())) {
                try {
                    Spin2Method method = compileMethod((MethodNode) node);
                    if (method != null) {
                        Method exp = new Method(method.getLabel(), method.getParametersCount(), method.getReturnsCount()) {

                            @Override
                            public int getIndex() {
                                return objectLinks.size() * 2 + methods.indexOf(method);
                            }

                        };
                        exp.setData(method.getClass().getName(), method);

                        try {
                            if (publicSymbols.containsKey(method.getLabel())) {
                                logMessage(new CompilerException("symbol " + method.getLabel() + " already defined", method.getLabel()));
                            }
                            else {
                                scope.addSymbol(method.getLabel(), exp);
                                publicSymbols.put(method.getLabel(), exp);
                            }
                        } catch (Exception e) {
                            logMessage(new CompilerException(e.getMessage(), node));
                        }

                        methods.add(method);
                    }
                } catch (CompilerException e) {
                    logMessage(e);
                } catch (Exception e) {
                    logMessage(new CompilerException(e.getMessage(), node));
                }
            }
        }
        for (Node node : root.getChilds()) {
            if (skipNode(node)) {
                continue;
            }
            if ((node instanceof MethodNode) && "PRI".equalsIgnoreCase(((MethodNode) node).getType().getText())) {
                try {
                    Spin2Method method = compileMethod((MethodNode) node);
                    if (method != null) {
                        Method exp = new Method(method.getLabel(), method.getParametersCount(), method.getReturnsCount()) {

                            @Override
                            public int getIndex() {
                                return objectLinks.size() * 2 + methods.indexOf(method);
                            }

                        };
                        exp.setData(method.getClass().getName(), method);

                        try {
                            scope.addSymbol(method.getLabel(), exp);
                        } catch (Exception e) {
                            logMessage(new CompilerException(e.getMessage(), node));
                        }

                        methods.add(method);
                    }
                } catch (CompilerException e) {
                    logMessage(e);
                } catch (Exception e) {
                    logMessage(new CompilerException(e.getMessage(), node));
                }
            }
        }
        if (methods.size() != 0) {
            scope.addBuiltinSymbol("@CLKMODE", new NumberLiteral(0x40));
            scope.addBuiltinSymbol("@CLKFREQ", new NumberLiteral(0x44));
        }

        for (Spin2Method method : methods) {
            Node node = (Node) method.getData();
            for (Spin2MethodLine line : compileStatement(method.getScope(), method, null, node)) {
                method.addSource(line);
            }
            Spin2MethodLine line = new Spin2MethodLine(method.getScope(), "RETURN");
            line.addSource(new Bytecode(line.getScope(), 0x04, line.getStatement()));
            method.addSource(line);
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
                            compiler.debugStatements.removeAll(method.debugNodes);
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
    public Spin2Object generateObject(int memoryOffset) {
        Spin2Object object = new Spin2Object();

        if (scope.hasSymbol("CLKFREQ_")) {
            object.setClkFreq(scope.getSymbol("CLKFREQ_").getNumber().intValue());
        }
        if (scope.hasSymbol("CLKMODE_")) {
            object.setClkMode(scope.getSymbol("CLKMODE_").getNumber().intValue());
        }

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
        int hubAddress = -1;
        int objectAddress = object.getSize();
        int fitAddress = 0x1F8 << 2;
        boolean hubMode = true;
        boolean cogCode = false;
        boolean spinMode = methods.size() != 0;

        for (Spin2PAsmLine line : source) {
            if (!hubMode && !(line.getInstructionFactory() instanceof com.maccasoft.propeller.spin2.instructions.Byte) && !(line.getInstructionFactory() instanceof Word)) {
                if (hubAddress != -1) {
                    hubAddress = (hubAddress + 3) & ~3;
                }
                objectAddress = (objectAddress + 3) & ~3;
                address = (address + 3) & ~3;
            }
            line.getScope().setObjectAddress(objectAddress);
            line.getScope().setMemoryAddress(memoryOffset + objectAddress);
            if (line.getInstructionFactory() instanceof Orgh) {
                hubMode = true;
                cogCode = false;
                if (hubAddress == -1) {
                    hubAddress = 0x400;
                }
                address = spinMode ? hubAddress : objectAddress;
            }
            if ((line.getInstructionFactory() instanceof Org) || (line.getInstructionFactory() instanceof Res)) {
                hubMode = false;
                if (hubAddress != -1) {
                    hubAddress = (hubAddress + 3) & ~3;
                }
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
                if (line.getInstructionFactory() instanceof Alignl) {
                    if (hubAddress != -1) {
                        hubAddress = (hubAddress + 3) & ~3;
                    }
                    objectAddress = (objectAddress + 3) & ~3;
                }
                else if (line.getInstructionFactory() instanceof Alignw) {
                    if (hubAddress != -1) {
                        hubAddress = (hubAddress + 1) & ~1;
                    }
                    objectAddress = (objectAddress + 1) & ~1;
                }
                else {
                    objectAddress += line.getInstructionObject().getSize();
                    if (hubAddress != -1) {
                        hubAddress += line.getInstructionObject().getSize();
                    }
                }
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

    Expression enumValue = new NumberLiteral(0);
    Expression enumIncrement = new NumberLiteral(1);

    void compileConstant(ConstantNode node) {
        try {
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
                        enumValue = builder.getExpression();
                    } catch (CompilerException e) {
                        logMessage(e);
                    } catch (Exception e) {
                        logMessage(new CompilerException("expression syntax error", builder.tokens));
                    }
                    enumIncrement = new NumberLiteral(1);
                    if ("[".equals(token.getText())) {
                        builder = new Spin2ExpressionBuilder(scope);
                        while (iter.hasNext()) {
                            token = iter.next();
                            if ("]".equals(token.getText())) {
                                try {
                                    enumIncrement = builder.getExpression();
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
                    Token identifier = token;
                    if (!iter.hasNext()) {
                        try {
                            if (publicSymbols.containsKey(identifier.getText())) {
                                logMessage(new CompilerException("symbol " + identifier.getText() + " already defined", identifier));
                            }
                            else {
                                if (!scope.hasSymbol(identifier.getText())) {
                                    scope.addSymbol(identifier.getText(), enumValue);
                                }
                                publicSymbols.put(identifier.getText(), scope.getLocalSymbol(identifier.getText()));
                            }
                        } catch (CompilerException e) {
                            logMessage(e);
                        } catch (Exception e) {
                            logMessage(new CompilerException(e, token));
                        }
                        if (enumValue instanceof BinaryOperator) {
                            enumValue = Expression.fold(new Add(((BinaryOperator) enumValue).getTerm1(), new Add(((BinaryOperator) enumValue).getTerm2(), enumIncrement)));
                        }
                        else {
                            enumValue = Expression.fold(new Add(enumValue, enumIncrement));
                        }
                    }
                    else {
                        token = iter.next();
                        if ("[".equals(token.getText())) {
                            try {
                                if (publicSymbols.containsKey(identifier.getText())) {
                                    logMessage(new CompilerException("symbol " + identifier.getText() + " already defined", identifier));
                                }
                                else {
                                    if (!scope.hasSymbol(identifier.getText())) {
                                        scope.addSymbol(identifier.getText(), enumValue);
                                    }
                                    publicSymbols.put(identifier.getText(), scope.getLocalSymbol(identifier.getText()));
                                }
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
                                if (enumValue instanceof BinaryOperator) {
                                    Expression increment = new Multiply(enumIncrement, expression);
                                    enumValue = Expression.fold(new Add(((BinaryOperator) enumValue).getTerm1(), new Add(((BinaryOperator) enumValue).getTerm2(), increment)));
                                }
                                else {
                                    enumValue = Expression.fold(new Add(enumValue, new Multiply(enumIncrement, expression)));
                                }
                            } catch (CompilerException e) {
                                logMessage(e);
                            } catch (Exception e) {
                                logMessage(new CompilerException(e, builder.getTokens()));
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
                                    if (publicSymbols.containsKey(identifier.getText())) {
                                        logMessage(new CompilerException("symbol " + identifier.getText() + " already defined", identifier));
                                    }
                                    else {
                                        if (!scope.hasSymbol(identifier.getText())) {
                                            scope.addSymbol(identifier.getText(), expression);
                                        }
                                        publicSymbols.put(identifier.getText(), scope.getLocalSymbol(identifier.getText()));
                                    }
                                } catch (CompilerException e) {
                                    logMessage(e);
                                } catch (Exception e) {
                                    logMessage(new CompilerException(e, node));
                                }
                            } catch (CompilerException e) {
                                logMessage(e);
                            } catch (Exception e) {
                                if (builder.getTokens().size() == 0) {
                                    logMessage(new CompilerException("expecting expression", token));
                                }
                                else {
                                    logMessage(new CompilerException("expression syntax error", builder.getTokens()));
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

        } catch (CompilerException e) {
            logMessage(e);
        } catch (Exception e) {
            logMessage(new CompilerException(e, node));
        }
    }

    void compileVarBlock(Node parent) {
        String type = "LONG";

        for (Node node : parent.getChilds()) {
            try {
                if (skipNode(node)) {
                    continue;
                }
                if (node instanceof VariableNode) {
                    Iterator<Token> iter = node.getTokens().iterator();

                    Token token = iter.next();
                    if (Spin2Model.isType(token.getText())) {
                        type = token.getText().toUpperCase();
                        if (!iter.hasNext()) {
                            throw new CompilerException("expecting identifier", token);
                        }
                        token = iter.next();
                    }

                    Token identifier = token;
                    int varSize = 1;

                    if (iter.hasNext()) {
                        token = iter.next();
                        if (!"[".equals(token.getText())) {
                            throw new CompilerException("unexpected", token);
                        }
                        if (!iter.hasNext()) {
                            throw new CompilerException("expecting expression", token);
                        }
                        Spin1ExpressionBuilder builder = new Spin1ExpressionBuilder(scope);
                        while (iter.hasNext()) {
                            token = iter.next();
                            if ("]".equals(token.getText())) {
                                try {
                                    Expression expression = builder.getExpression();
                                    if (!expression.isConstant()) {
                                        throw new Exception("not a constant expression");
                                    }
                                    varSize = expression.getNumber().intValue();
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
                            throw new CompilerException("expecting ']'", token);
                        }
                        if (iter.hasNext()) {
                            throw new CompilerException("unexpected", iter.next());
                        }
                    }

                    try {
                        Variable var = new Variable(type, identifier.getText(), varSize, objectVarSize);
                        scope.addSymbol(identifier.getText(), var);
                        variables.add(var);
                        var.setData(identifier);

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
                }
            } catch (CompilerException e) {
                logMessage(e);
            } catch (Exception e) {
                logMessage(new CompilerException(e, node));
            }
        }
    }

    void compileObject(ObjectNode node) {
        try {
            TokenIterator iter = node.iterator();

            Token token = iter.next();
            String name = token.getText();
            Expression count = new NumberLiteral(1);

            if (!iter.hasNext()) {
                logMessage(new CompilerException("expecting file name", token.substring(token.stop - token.start)));
                return;
            }
            token = iter.next();

            if ("[".equals(token.getText())) {
                if (!iter.hasNext()) {
                    logMessage(new CompilerException("expecting expression", token.substring(token.stop - token.start)));
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
                            logMessage(new CompilerException(e, builder.getTokens()));
                        }
                        break;
                    }
                    builder.addToken(token);
                }
                if (!"]".equals(token.getText())) {
                    logMessage(new CompilerException("expecting ']'", token));
                    return;
                }
                if (!iter.hasNext()) {
                    logMessage(new CompilerException("expecting file name", token.substring(token.stop - token.start)));
                    return;
                }
                token = iter.next();
            }

            if (!":".equals(token.getText())) {
                logMessage(new CompilerException("expecting ':'", token));
                return;
            }
            if (!iter.hasNext()) {
                logMessage(new CompilerException("expecting file name", token.substring(token.stop - token.start)));
                return;
            }
            Token fileToken = iter.next();
            if (fileToken.type != Token.STRING) {
                logMessage(new CompilerException("expecting file name", fileToken));
                return;
            }

            Map<String, Expression> parameters = compiler.isCaseSensitive() ? new HashMap<>() : new CaseInsensitiveMap<>();
            if (iter.hasNext()) {
                token = iter.next();
                if (!"|".equals(token.getText())) {
                    logMessage(new CompilerException("expecting '|'", token));
                    return;
                }
                if (!iter.hasNext()) {
                    logMessage(new CompilerException("expecting parameter", token.substring(token.stop - token.start)));
                }
                while (iter.hasNext()) {
                    token = iter.next();
                    if (token.type != 0) {
                        logMessage(new CompilerException("expecting identifier", token));
                        break;
                    }
                    String identifier = token.getText();
                    if (!iter.hasNext()) {
                        logMessage(new CompilerException("expecting expression", token.substring(token.stop - token.start)));
                        break;
                    }
                    token = iter.next();
                    if (!"=".equals(token.getText())) {
                        logMessage(new CompilerException("expecting '='", token));
                        break;
                    }
                    Spin2ExpressionBuilder builder = new Spin2ExpressionBuilder(scope);
                    try {
                        while (iter.hasNext()) {
                            if (",".equals(iter.peekNext().getText())) {
                                break;
                            }
                            builder.addToken(iter.next());
                        }
                        Expression expression = builder.getExpression();
                        try {
                            parameters.put(identifier, expression);
                        } catch (CompilerException e) {
                            logMessage(e);
                        } catch (Exception e) {
                            logMessage(new CompilerException(e, builder.getTokens()));
                        }
                    } catch (CompilerException e) {
                        logMessage(e);
                    } catch (Exception e) {
                        logMessage(new CompilerException(e, builder.tokens));
                    }

                    if (iter.hasNext()) {
                        token = iter.next();
                        if (",".equals(token.getText())) {
                            if (!iter.hasNext()) {
                                logMessage(new CompilerException("expecting parameter", token.substring(token.stop - token.start)));
                            }
                        }
                        else {
                            logMessage(new CompilerException("expecting ','", token));
                        }
                    }
                }
            }

            if (iter.hasNext()) {
                token = iter.next();
                logMessage(new CompilerException("unexpected '" + token.getText() + "'", token));
            }

            String fileName = fileToken.getText();
            if (fileName.startsWith("\"")) {
                fileName = fileName.substring(1);
            }
            if (!fileName.endsWith("\"")) {
                logMessage(new CompilerException("unterminated string", fileToken));
                return;
            }
            fileName = fileName.substring(0, fileName.length() - 1);

            File file = compiler.getFile(fileName, ".spin2");
            if (file == null) {
                logMessage(new CompilerException("object " + fileName + " not found", fileToken));
                return;
            }

            ObjectInfo info = compiler.getObjectInfo(this, file, parameters);
            if (info == null) {
                logMessage(new CompilerException("object " + fileName + " not found", fileToken));
                return;
            }

            objects.put(name, new ObjectInfo(info, count));

            for (Entry<String, Expression> entry : info.compiler.getPublicSymbols().entrySet()) {
                if (!(entry.getValue() instanceof Method)) {
                    String qualifiedName = name + "." + entry.getKey();
                    scope.addSymbol(qualifiedName, entry.getValue());
                }
            }

            if (info.hasErrors()) {
                for (CompilerException msg : compiler.getMessages()) {
                    if (file.getName().equals(msg.getFileName()) && "illegal circular reference".equals(msg.getMessage())) {
                        logMessage(new CompilerException(msg.getMessage(), fileToken));
                        return;
                    }
                }
                logMessage(new CompilerException("object " + fileName + " has errors", fileToken));
            }

        } catch (CompilerException e) {
            logMessage(e);
        } catch (Exception e) {
            logMessage(new CompilerException(e, node));
        }
    }

    @Override
    protected void compileDatInclude(Node root) {
        for (Node node : root.getChilds()) {
            if (!(node instanceof ConstantsNode) && !(node instanceof DataNode)) {
                throw new RuntimeException("only CON and DAT sections allowed in included files");
            }
        }
        for (Node node : root.getChilds()) {
            if (node instanceof ConstantsNode) {
                for (Node child : node.getChilds()) {
                    if (child instanceof ConstantNode) {
                        compileConstant((ConstantNode) child);
                    }
                }
            }
        }
        for (Node node : root.getChilds()) {
            if (node instanceof DataNode) {
                compileDatBlock(node);
            }
        }
    }

    Spin2Method compileMethod(MethodNode node) {
        Context localScope = new Context(scope);

        localScope.addBuiltinSymbol("RECV", new Register(0x1D2));
        localScope.addBuiltinSymbol("SEND", new Register(0x1D3));

        TokenIterator iter = node.iterator();
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

        while (iter.hasNext()) {
            token = iter.next();
            if ("(".equals(token.getText())) {
                if (!iter.hasNext()) {
                    logMessage(new CompilerException("expecting parameter(s)", token.substring(token.stop - token.start)));
                }
                while (iter.hasNext()) {
                    Token identifier = iter.next();
                    if (")".equals(identifier.getText())) {
                        break;
                    }
                    if (Spin1Model.isType(identifier.getText())) {
                        logMessage(new CompilerException("type not allowed", identifier));
                    }
                    else if (identifier.type == 0) {
                        Expression expression = localScope.getLocalSymbol(identifier.getText());
                        if (expression instanceof LocalVariable) {
                            logMessage(new CompilerException("symbol '" + identifier + "' already defined", identifier));
                        }
                        else {
                            if (expression != null) {
                                logMessage(new CompilerException(CompilerException.WARNING, "parameter '" + identifier + "' hides global variable", identifier));
                            }
                            LocalVariable var = method.addParameter("LONG", identifier.getText(), 1);
                            var.setData(identifier);
                        }
                    }
                    else {
                        logMessage(new CompilerException("invalid identifier", identifier));
                    }
                    if (iter.hasNext()) {
                        token = iter.next();
                        if (")".equals(token.getText())) {
                            break;
                        }
                        else if (",".equals(token.getText())) {
                            if (!iter.hasNext()) {
                                logMessage(new CompilerException("expecting identifier", token.substring(token.stop - token.start)));
                            }
                        }
                        else {
                            logMessage(new CompilerException("expecting ',' or ')'", token));
                        }
                    }
                    else {
                        logMessage(new CompilerException("expecting ',' or ')'", identifier.substring(identifier.stop - identifier.start)));
                    }
                }
            }
            else if ("|".equals(token.getText())) {
                if (!iter.hasNext()) {
                    logMessage(new CompilerException("expecting local variable(s)", token.substring(token.stop - token.start)));
                }
                while (iter.hasNext()) {
                    Token identifier = iter.next();

                    String type = "LONG";
                    if (Spin1Model.isType(identifier.getText())) {
                        type = identifier.getText().toUpperCase();
                        if (!iter.hasNext()) {
                            logMessage(new CompilerException("expecting identifier", token.substring(token.stop - token.start)));
                        }
                        identifier = iter.next();
                    }
                    if (identifier.type == 0) {
                        Expression size = new NumberLiteral(1);

                        if (iter.hasNext() && "[".equals(iter.peekNext().getText())) {
                            token = iter.next();
                            if (!iter.hasNext()) {
                                logMessage(new CompilerException("expecting expression", token.substring(token.stop - token.start)));
                            }
                            Spin1ExpressionBuilder builder = new Spin1ExpressionBuilder(scope);
                            while (iter.hasNext()) {
                                token = iter.next();
                                if ("]".equals(token.getText())) {
                                    try {
                                        size = builder.getExpression();
                                        size.getNumber().intValue();
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
                                logMessage(new CompilerException("expecting ']'", token));
                            }
                        }

                        Expression expression = localScope.getLocalSymbol(identifier.getText());
                        if (expression instanceof LocalVariable) {
                            logMessage(new CompilerException("symbol '" + identifier + "' already defined", identifier));
                        }
                        else {
                            if (expression != null) {
                                logMessage(new CompilerException(CompilerException.WARNING, "local variable '" + identifier + "' hides global variable", identifier));
                            }
                            LocalVariable var = method.addLocalVariable(type, identifier.getText(), size.getNumber().intValue()); // new LocalVariable(type, identifier.getText(), size, offset);
                            var.setData(identifier);
                        }
                    }
                    else {
                        logMessage(new CompilerException("invalid identifier", identifier));
                    }

                    if (iter.hasNext()) {
                        if (":".equals(iter.peekNext().getText())) {
                            break;
                        }
                        token = iter.next();
                        if (",".equals(token.getText())) {
                            if (!iter.hasNext()) {
                                logMessage(new CompilerException("expecting identifier", token.substring(token.stop - token.start)));
                            }
                        }
                        else {
                            logMessage(new CompilerException("expecting ',' or ':'", token));
                        }
                    }
                }
            }
            else if (":".equals(token.getText())) {
                if (!iter.hasNext()) {
                    logMessage(new CompilerException("expecting return variable(s)", token.substring(token.stop - token.start)));
                }
                while (iter.hasNext()) {
                    Token identifier = iter.next();
                    if (Spin1Model.isType(identifier.getText())) {
                        logMessage(new CompilerException("type not allowed", identifier));
                    }
                    else if (identifier.type == 0) {
                        Expression expression = localScope.getLocalSymbol(identifier.getText());
                        if (expression instanceof LocalVariable) {
                            logMessage(new CompilerException("symbol '" + identifier.getText() + "' already defined", identifier));
                        }
                        else {
                            if (expression != null) {
                                logMessage(new CompilerException(CompilerException.WARNING, "return variable '" + identifier.getText() + "' hides global variable", identifier));
                            }
                            LocalVariable var = method.addReturnVariable("LONG", identifier.getText());
                            var.setData(identifier);
                        }
                    }
                    else {
                        logMessage(new CompilerException("invalid identifier", identifier));
                    }

                    if (iter.hasNext()) {
                        if ("|".equals(iter.peekNext().getText())) {
                            break;
                        }
                        token = iter.next();
                        if (",".equals(token.getText())) {
                            if (!iter.hasNext()) {
                                logMessage(new CompilerException("expecting identifier", token.substring(token.stop - token.start)));
                            }
                        }
                        else {
                            logMessage(new CompilerException("expecting ',' or ':'", token));
                        }
                    }
                }
            }
            else {
                logMessage(new CompilerException("unexpected '" + token.getText() + "'", token));
                break;
            }
        }

        return method;
    }

    List<Spin2MethodLine> compileStatement(Context context, Spin2Method method, Spin2MethodLine parent, Node statementNode) {
        List<Spin2MethodLine> lines = new ArrayList<>();
        Spin2MethodLine previousLine = null;

        List<Node> statements = buildFilteredStatements(statementNode);

        Iterator<Node> nodeIterator = statements.iterator();
        while (nodeIterator.hasNext()) {
            Node node = nodeIterator.next();
            try {
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

                        int address = 0x1E0;
                        for (LocalVariable var : method.getAllLocalVariables()) {
                            scope.addSymbol(var.getName(), new NumberLiteral(address));
                            address += var.getSize();
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

    List<Node> buildFilteredStatements(Node statementNode) {
        List<Node> list = new ArrayList<>();
        for (Node node : statementNode.getChilds()) {
            if (skipNode(node)) {
                if (node instanceof StatementNode) {
                    list.addAll(buildFilteredStatements(node));
                }
            }
            else {
                list.add(node);
            }
        }
        return list;
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
                    line.addSource(compileConstantExpression(context, method, builder.getRoot()));
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
                line.addSource(compileConstantExpression(context, method, builder.getRoot()));

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
                    line.addSource(compileConstantExpression(context, method, builder.getRoot()));
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
                    line.addSource(compileConstantExpression(line.getScope(), method, builder.getRoot()));
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
                        line.addSource(compileConstantExpression(line.getScope(), method, builder.getRoot()));

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

                            line.addSource(compileConstantExpression(line.getScope(), method, to));
                            if (step != null) {
                                line.addSource(compileConstantExpression(line.getScope(), method, step));
                            }
                            line.addSource(compileConstantExpression(line.getScope(), method, from));

                            Expression expression = line.getScope().getLocalSymbol(counter.getText());
                            line.addSource(compileVariableSetup(context, method, expression, counter));
                            line.addSource(new Bytecode(line.getScope(), step != null ? 0x7C : 0x7B, "REPEAT"));

                            Spin2MethodLine nextLine = new Spin2MethodLine(context);
                            line.setData("next", nextLine);

                            line.addChild(loopLine);
                            line.addChilds(compileStatement(new Context(context), method, line, node));

                            nextLine.addSource(compileVariableSetup(context, method, expression, counter));
                            nextLine.addSource(new Bytecode(line.getScope(), 0x7D, "REPEAT_LOOP"));
                            line.addChild(nextLine);
                        }
                        else {
                            try {
                                Expression expression = buildConstantExpression(line.getScope(), counter);
                                line.addSource(new Constant(line.getScope(), expression));
                            } catch (Exception e) {
                                line.addSource(compileBytecodeExpression(line.getScope(), method, counter, true));
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
                conditionLine.addSource(compileConstantExpression(line.getScope(), method, builder.getRoot()));

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
                line.addSource(compileBytecodeExpression(context, method, builder.getRoot(), true));

                boolean hasOther = false;
                for (Node child : node.getChilds()) {
                    if (skipNode(child)) {
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
                            compileCase(method, line, builder.getRoot(), caseLine);

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
                line.addSource(compileBytecodeExpression(context, method, builder.getRoot(), true));
                line.addSource(new Bytecode(line.getScope(), 0x1A, "CASE_FAST"));

                int min = Integer.MAX_VALUE;
                int max = Integer.MIN_VALUE;
                Map<Integer, Spin2MethodLine> map = new TreeMap<Integer, Spin2MethodLine>();

                Spin2MethodLine doneLine = null;
                Spin2MethodLine otherLine = null;

                for (Node child : node.getChilds()) {
                    if (skipNode(child)) {
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
                if (!compiler.isDebugEnabled() && "DEBUG".equalsIgnoreCase(token.getText())) {
                    return null;
                }

                Spin2TreeBuilder builder = new Spin2TreeBuilder(context);
                builder.addToken(token);
                while (iter.hasNext()) {
                    builder.addToken(iter.next());
                }
                line = new Spin2MethodLine(context, parent, null, node);
                line.addSource(compileBytecodeExpression(context, method, builder.getRoot(), false));
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
        Context rootScope = new Context(line.getScope());
        Context lineScope = rootScope;

        while (linesIterator.hasNext()) {
            Node node = linesIterator.next();
            if (skipNode(node)) {
                continue;
            }
            if (node instanceof DataLineNode) {
                DataLineNode lineNode = (DataLineNode) node;
                if (lineNode.label != null && !lineNode.label.getText().startsWith(".")) {
                    lineScope = rootScope;
                }
                Spin2PAsmLine pasmLine = compileInlineDataLine(rootScope, lineScope, (DataLineNode) node);
                line.addSource(new InlinePAsm(rootScope, pasmLine));
                if (lineNode.label != null && !lineNode.label.getText().startsWith(".")) {
                    lineScope = pasmLine.getScope();
                }
            }
            else if (node instanceof StatementNode) {
                if ("END".equalsIgnoreCase(node.getStartToken().getText())) {
                    Spin2PAsmLine pasmLine = new Spin2PAsmLine(line.getScope(), null, null, "ret", Collections.emptyList(), null);
                    line.addSource(new InlinePAsm(rootScope, pasmLine));
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
            logMessage(new CompilerException("unsupported directive", token));
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

        try {
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
        } catch (Exception e) {
            // Do nothing, errors logged at constants check stage
        }

        scope.addBuiltinSymbol("CLKMODE_", new NumberLiteral(clkmode));
        scope.addBuiltinSymbol("CLKFREQ_", new NumberLiteral(finalfreq));
    }

    @Override
    public Context getScope() {
        return scope;
    }

}
