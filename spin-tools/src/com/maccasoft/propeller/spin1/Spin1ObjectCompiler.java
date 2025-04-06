/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.map.ListOrderedMap;

import com.maccasoft.propeller.Compiler.ObjectInfo;
import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.ObjectCompiler;
import com.maccasoft.propeller.SpinObject.LinkDataObject;
import com.maccasoft.propeller.SpinObject.LongDataObject;
import com.maccasoft.propeller.SpinObject.WordDataObject;
import com.maccasoft.propeller.expressions.Add;
import com.maccasoft.propeller.expressions.BinaryOperator;
import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.ContextLiteral;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.LocalVariable;
import com.maccasoft.propeller.expressions.Method;
import com.maccasoft.propeller.expressions.Multiply;
import com.maccasoft.propeller.expressions.NumberLiteral;
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
import com.maccasoft.propeller.model.ObjectsNode;
import com.maccasoft.propeller.model.RootNode;
import com.maccasoft.propeller.model.StatementNode;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.TokenIterator;
import com.maccasoft.propeller.model.VariableNode;
import com.maccasoft.propeller.model.VariablesNode;
import com.maccasoft.propeller.spin1.Spin1Object.Spin1LinkDataObject;
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
import com.maccasoft.propeller.spin1.instructions.Org;
import com.maccasoft.propeller.spin1.instructions.Res;

public class Spin1ObjectCompiler extends Spin1BytecodeCompiler {

    List<Variable> variables = new ArrayList<>();
    List<Spin1Method> methods = new ArrayList<>();
    Map<String, ObjectInfo> objects = ListOrderedMap.listOrderedMap(new HashMap<>());

    int clkMode;
    int xinFreq;
    int clkFreq;
    int objectVarSize;

    Map<String, Expression> publicSymbols = new HashMap<>();
    List<LinkDataObject> objectLinks = new ArrayList<>();

    public Spin1ObjectCompiler(Spin1Compiler compiler, File file) {
        this(compiler, null, file);
    }

    public Spin1ObjectCompiler(Spin1Compiler compiler, ObjectCompiler parent, File file) {
        super(new Spin1GlobalContext(compiler.isCaseSensitive()), compiler, parent, file);

        if (parent != null) {
            scope.addDefinitions(parent.getScope().getDefinitions());
        }
        scope.addDefinitions(compiler.getDefines());

        scope.addDefinition("__P1__", new NumberLiteral(1));
        scope.addDefinition("__SPINTOOLS__", new NumberLiteral(1));
    }

    @Override
    public Spin1Object compileObject(RootNode root) {
        compileStep1(root);
        return generateObject(0);
    }

    @Override
    public void compileStep1(RootNode root) {
        List<VariablesNode> variableNodes = new ArrayList<>();

        root.accept(new NodeVisitor() {

            Node lastParent;

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
                node.setExclude(!conditionStack.isEmpty() && conditionStack.peek().skip);
                if (!node.isExclude()) {
                    enumValue = new NumberLiteral(0);
                    enumIncrement = new NumberLiteral(1);
                    lastParent = node;
                }
                return true;
            }

            @Override
            public void visitConstant(ConstantNode node) {
                node.setExclude(!conditionStack.isEmpty() && conditionStack.peek().skip);
                if (!node.isExclude()) {
                    if (!(lastParent instanceof ConstantsNode)) {
                        logMessage(new CompilerException("misplaced constant", node));
                    }
                    else {
                        compileConstant(node);
                    }
                }
            }

            @Override
            public boolean visitVariables(VariablesNode node) {
                node.setExclude(!conditionStack.isEmpty() && conditionStack.peek().skip);
                if (!node.isExclude()) {
                    if (node.getParent() instanceof RootNode) {
                        lastParent = node;
                        variableNodes.add(node);
                    }
                    else if (lastParent instanceof VariablesNode) {
                        variableNodes.add(node);
                    }
                    else {
                        logMessage(new CompilerException("misplaced variables", node));
                    }
                }
                return true;
            }

            @Override
            public void visitVariable(VariableNode node) {
                node.setExclude(!conditionStack.isEmpty() && conditionStack.peek().skip);
            }

            @Override
            public boolean visitObjects(ObjectsNode node) {
                node.setExclude(!conditionStack.isEmpty() && conditionStack.peek().skip);
                if (!node.isExclude()) {
                    lastParent = node;
                }
                return true;
            }

            @Override
            public void visitObject(ObjectNode node) {
                node.setExclude(!conditionStack.isEmpty() && conditionStack.peek().skip);
                if (!node.isExclude()) {
                    if (!(lastParent instanceof ObjectsNode)) {
                        logMessage(new CompilerException("misplaced object", node));
                    }
                    else {
                        compileObject(node);
                    }
                }
            }

            @Override
            public boolean visitMethod(MethodNode node) {
                node.setExclude(!conditionStack.isEmpty() && conditionStack.peek().skip);
                if (!node.isExclude()) {
                    lastParent = node;
                }
                return true;
            }

            @Override
            public boolean visitStatement(StatementNode node) {
                node.setExclude(!conditionStack.isEmpty() && conditionStack.peek().skip);
                return true;
            }

            @Override
            public boolean visitData(DataNode node) {
                node.setExclude(!conditionStack.isEmpty() && conditionStack.peek().skip);
                if (!node.isExclude()) {
                    lastParent = node;
                }
                return true;
            }

            @Override
            public void visitDataLine(DataLineNode node) {
                node.setExclude(!conditionStack.isEmpty() && conditionStack.peek().skip);
            }

        });

        while (!conditionStack.isEmpty()) {
            Condition c = conditionStack.pop();
            logMessage(new CompilerException("unbalanced conditional directive", c.node));
        }

        try {
            determineClock();
            if (!scope.hasSymbol("_CLKFREQ")) {
                scope.addSymbol("_CLKFREQ", new NumberLiteral(clkFreq));
            }
            if (!scope.hasSymbol("_XINFREQ")) {
                scope.addSymbol("_XINFREQ", new NumberLiteral(xinFreq));
            }
        } catch (CompilerException e) {
            logMessage(e);
        }

        scope.addBuiltinSymbol("@CLKMODE", new NumberLiteral(0x00));
        scope.addBuiltinSymbol("@CLKFREQ", new NumberLiteral(0x04));

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

        objectVarSize = 0;
        variables.clear();

        for (VariablesNode node : variableNodes) {
            compileVarBlock(node);
        }

        Collections.sort(variables, new Comparator<Variable>() {

            @Override
            public int compare(Variable o1, Variable o2) {
                return o2.getTypeSize() - o1.getTypeSize();
            }

        });
        for (Variable var : variables) {
            var.setOffset(objectVarSize);
            objectVarSize += var.getTypeSize() * var.getSize();
        }

        objectVarSize = (objectVarSize + 3) & ~3;

        compileDataBlocks(root);

        for (Node node : root.getChilds()) {
            if (node.isExclude()) {
                continue;
            }
            if ((node instanceof MethodNode) && "PUB".equalsIgnoreCase(((MethodNode) node).getType().getText())) {
                try {
                    Spin1Method method = compileMethod((MethodNode) node);
                    if (method != null) {
                        Method exp = new Method(method.getLabel(), method.getMinParameters(), method.getParametersCount(), method.getReturnsCount()) {

                            @Override
                            public int getIndex() {
                                return methods.indexOf(method);
                            }

                        };
                        exp.setData(method.getClass().getName(), method);

                        try {
                            scope.addSymbol(method.getLabel(), exp);
                            scope.addSymbol("@" + method.getLabel(), exp);
                            publicSymbols.put(method.getLabel(), exp);
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
            if (node.isExclude()) {
                continue;
            }
            if ((node instanceof MethodNode) && "PRI".equalsIgnoreCase(((MethodNode) node).getType().getText())) {
                try {
                    Spin1Method method = compileMethod((MethodNode) node);
                    if (method != null) {
                        Method exp = new Method(method.getLabel(), method.getMinParameters(), method.getParametersCount(), method.getReturnsCount()) {

                            @Override
                            public int getIndex() {
                                return methods.indexOf(method);
                            }

                        };
                        exp.setData(method.getClass().getName(), method);

                        try {
                            scope.addSymbol(method.getLabel(), exp);
                            scope.addSymbol("@" + method.getLabel(), exp);
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

        for (Entry<String, ObjectInfo> infoEntry : objects.entrySet()) {
            ObjectInfo info = infoEntry.getValue();
            String name = infoEntry.getKey();

            try {
                int count = info.count.getNumber().intValue();

                LinkDataObject linkData = new Spin1LinkDataObject(info.compiler, info.compiler.getVarSize());
                for (Entry<String, Expression> objEntry : info.compiler.getPublicSymbols().entrySet()) {
                    if (objEntry.getValue() instanceof Method) {
                        String qualifiedName = name + "." + objEntry.getKey();
                        Method objectMethod = (Method) objEntry.getValue();
                        Method method = new Method(objectMethod.getName(), objectMethod.getMinArgumentsCount(), objectMethod.getArgumentsCount(), objectMethod.getReturnLongs()) {

                            @Override
                            public int getIndex() {
                                return objectMethod.getIndex();
                            }

                            @Override
                            public int getObjectIndex() {
                                return objectLinks.indexOf(linkData) + methods.size() + 1;
                            }

                        };
                        method.setData(Spin1Method.class.getName(), objectMethod.getData(Spin1Method.class.getName()));
                        scope.addSymbol(qualifiedName, method);
                    }
                }
                scope.addSymbol(name, new SpinObject(name, count) {

                    @Override
                    public int getIndex() {
                        return objectLinks.indexOf(linkData) + methods.size() + 1;
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
        }

        for (Spin1Method method : methods) {
            Node node = (Node) method.getData();
            for (Spin1MethodLine line : compileStatement(method.getScope(), method, null, node)) {
                method.addSource(line);
            }

            Spin1MethodLine line = new Spin1MethodLine(method.getScope(), "RETURN");
            line.addSource(new Bytecode(line.getScope(), 0b00110010, line.getStatement()));
            method.addSource(line);

            if (!compiler.isOptimizeStrings()) {
                List<Spin1Bytecode> data = getStringData();
                if (data.size() != 0) {
                    Spin1MethodLine stringDataLine = new Spin1MethodLine(method.getScope());
                    stringDataLine.setText("(string data)");
                    stringDataLine.addSource(data);
                    method.addSource(stringDataLine);
                    data.clear();
                }
            }
        }
    }

    @Override
    public void compileStep2(boolean keepFirst) {
        String unusedVariable = "variable \"%s\" is not used";

        if (compiler.warnRemovedUnusedMethods()) {
            warnUnusedMethods(keepFirst);
            if (compiler.removeUnusedMethods()) {
                removeUnusedMethods(keepFirst);
            }
        }
        else {
            if (compiler.removeUnusedMethods()) {
                removeUnusedMethods(keepFirst);
            }
            warnUnusedMethods(keepFirst);
        }

        if (compiler.warnUnusedVariables()) {
            for (Variable var : variables) {
                if (!var.isReferenced() && var.getData() != null) {
                    logMessage(new CompilerException(CompilerException.WARNING, String.format(unusedVariable, var.getName()), var.getData()));
                }
            }
        }
    }

    void warnUnusedMethods(boolean keepFirst) {
        String unusedMethod = "method \"%s\" is not used";
        String unusedMethodParameter = "method \"%s\" parameter \"%s\" is not used";
        String unusedMethodVariable = "method \"%s\" local variable \"%s\" is not used";
        String unusedMethodReturn = "method \"%s\" return variable \"%s\" is not used";

        Iterator<Spin1Method> methodsIterator = methods.iterator();
        if (methodsIterator.hasNext()) {
            if (keepFirst) {
                Spin1Method method = methodsIterator.next();
                if (compiler.warnUnusedMethodVariables()) {
                    for (Variable var : method.getParameters()) {
                        if (!var.isReferenced() && var.getData() != null) {
                            logMessage(new CompilerException(CompilerException.WARNING, String.format(unusedMethodParameter, method.getLabel(), var.getName()), var.getData()));
                        }
                    }
                    for (Variable var : method.getLocalVariables()) {
                        if (!var.isReferenced() && var.getData() != null) {
                            logMessage(new CompilerException(CompilerException.WARNING, String.format(unusedMethodVariable, method.getLabel(), var.getName()), var.getData()));
                        }
                    }
                    for (Variable var : method.getReturns()) {
                        if (!var.isReferenced() && var.getData() != null) {
                            logMessage(new CompilerException(CompilerException.WARNING, String.format(unusedMethodReturn, method.getLabel(), var.getName()), var.getData()));
                        }
                    }
                }
            }
            while (methodsIterator.hasNext()) {
                Spin1Method method = methodsIterator.next();
                if (!method.isReferenced() && compiler.warnUnusedMethods()) {
                    MethodNode node = (MethodNode) method.getData();
                    logMessage(new CompilerException(CompilerException.WARNING, String.format(unusedMethod, method.getLabel()), node.getName()));
                }
                if (compiler.warnUnusedMethodVariables()) {
                    for (Variable var : method.getParameters()) {
                        if (!var.isReferenced() && var.getData() != null) {
                            logMessage(new CompilerException(CompilerException.WARNING, String.format(unusedMethodParameter, method.getLabel(), var.getName()), var.getData()));
                        }
                    }
                    for (Variable var : method.getLocalVariables()) {
                        if (!var.isReferenced() && var.getData() != null) {
                            logMessage(new CompilerException(CompilerException.WARNING, String.format(unusedMethodVariable, method.getLabel(), var.getName()), var.getData()));
                        }
                    }
                    for (Variable var : method.getReturns()) {
                        if (!var.isReferenced() && var.getData() != null) {
                            logMessage(new CompilerException(CompilerException.WARNING, String.format(unusedMethodReturn, method.getLabel(), var.getName()), var.getData()));
                        }
                    }
                }
            }
        }
    }

    void removeUnusedMethods(boolean keepFirst) {
        boolean loop;
        do {
            loop = false;
            Iterator<Spin1Method> methodsIterator = methods.iterator();
            if (methodsIterator.hasNext()) {
                if (keepFirst) {
                    methodsIterator.next();
                }
                while (methodsIterator.hasNext()) {
                    Spin1Method method = methodsIterator.next();
                    if (!method.isReferenced()) {
                        method.remove();
                        methodsIterator.remove();
                        loop = true;
                    }
                }
            }
        } while (loop);
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
    public Spin1Object generateObject(int memoryOffset) {
        int address = 0, hubAddress = 0;
        Spin1Object object = new Spin1Object();

        object.setClkFreq(clkFreq);
        object.setClkMode(clkMode);

        object.writeComment("Object header (var size " + objectVarSize + ")");

        WordDataObject objectSize = object.writeWord(0, "Object size");
        object.writeByte(methods.size() + 1, "Method count + 1");

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

        List<LongDataObject> methodData = new ArrayList<>();
        if (methods.size() != 0) {
            Iterator<Spin1Method> methodsIterator = methods.iterator();
            while (methodsIterator.hasNext()) {
                Spin1Method method = methodsIterator.next();
                LongDataObject dataObject = new LongDataObject(0, "Function " + method.getLabel());
                object.write(dataObject);
                methodData.add(dataObject);
            }
            object.setDcurr(methods.get(0).getStackSize());
        }

        int linkedVarOffset = objectVarSize;
        for (LinkDataObject linkData : objectLinks) {
            linkData.setVarOffset(linkedVarOffset);
            object.write(linkData);
            linkedVarOffset += linkData.getVarSize();
        }
        object.setVarSize(linkedVarOffset);

        hubAddress = object.getSize();

        for (Spin1PAsmLine line : source) {
            if ((line.getInstructionFactory() instanceof com.maccasoft.propeller.spin1.instructions.Word)
                || (line.getInstructionFactory() instanceof com.maccasoft.propeller.spin1.instructions.Wordfit)) {
                hubAddress = (hubAddress + 1) & ~1;
                address = (address + 1) & ~1;
            }
            else if (line.getMnemonic() != null && !(line.getInstructionFactory() instanceof com.maccasoft.propeller.spin1.instructions.Byte)
                && !(line.getInstructionFactory() instanceof com.maccasoft.propeller.spin1.instructions.Bytefit)
                && !"FILE".equalsIgnoreCase(line.getMnemonic())) {
                hubAddress = (hubAddress + 3) & ~3;
                address = (address + 3) & ~3;
            }
            line.getScope().setObjectAddress(hubAddress);
            if ((line.getInstructionFactory() instanceof Org) || (line.getInstructionFactory() instanceof Res)) {
                hubAddress = (hubAddress + 3) & ~3;
                address = (address + 3) & ~3;
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
        if (compiler.isOptimizeStrings() && getStringData().size() != 0) {
            stringDataLine = new Spin1MethodLine(scope);
            stringDataLine.setText("(string data)");
            stringDataLine.addSource(getStringData());
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

        }

        object.alignToLong();

        objectSize.setValue(object.getSize());

        return object;
    }

    Expression enumValue = new NumberLiteral(0);
    Expression enumIncrement = new NumberLiteral(1);

    void compileConstant(ConstantNode node) {
        try {
            TokenIterator iter = node.tokenIterator();

            Token token = iter.next();
            do {
                if ("#".equals(token.getText())) {
                    Spin1ExpressionBuilder builder = new Spin1ExpressionBuilder(scope, true);
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
                        logMessage(new CompilerException("expression syntax error", builder.getTokens()));
                    }
                    enumIncrement = new NumberLiteral(1);
                    if ("[".equals(token.getText())) {
                        builder = new Spin1ExpressionBuilder(scope, true);
                        while (iter.hasNext()) {
                            token = iter.next();
                            if ("]".equals(token.getText())) {
                                try {
                                    enumIncrement = builder.getExpression();
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
                            logMessage(new CompilerException("expecting '['", token));
                        }
                    }
                }
                else {
                    if (token.type != Token.KEYWORD) {
                        logMessage(new CompilerException("expecting identifier", token));
                        break;
                    }
                    String identifier = token.getText();
                    if (!iter.hasNext()) {
                        try {
                            scope.addSymbol(identifier, enumValue);
                            publicSymbols.put(identifier, enumValue);
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
                                scope.addSymbol(identifier, enumValue);
                                publicSymbols.put(identifier, enumValue);
                            } catch (CompilerException e) {
                                logMessage(e);
                            } catch (Exception e) {
                                logMessage(new CompilerException(e, token));
                            }

                            Spin1ExpressionBuilder builder = new Spin1ExpressionBuilder(scope, true);
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
                            Spin1ExpressionBuilder builder = new Spin1ExpressionBuilder(scope, true);
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

    void compileVarBlock(Node node) {
        Token token;
        TokenIterator iter = node.tokenIterator();

        if (iter.hasNext()) {
            Token section = iter.peekNext();
            if ("VAR".equalsIgnoreCase(section.getText())) {
                iter.next();
            }
        }

        if (iter.hasNext()) {
            try {
                Token type = null;

                token = iter.next();
                if (Spin1Model.isType(token.getText())) {
                    type = token;
                    if (!iter.hasNext()) {
                        logMessage(new CompilerException("expecting identifier after type", token));
                        return;
                    }
                    token = iter.next();
                }

                String typeText = type != null ? type.getText().toUpperCase() : "LONG";

                do {
                    int varSize = 1;

                    if (token.type != Token.KEYWORD) {
                        throw new CompilerException("expecting identifier", token);
                    }
                    if (Spin1Model.isType(token.getText())) {
                        throw new CompilerException("expecting identifier", token);
                    }
                    Token identifier = token;

                    if (iter.hasNext()) {
                        token = iter.peekNext();

                        if ("[".equals(token.getText())) {
                            token = iter.next();
                            if (!iter.hasNext()) {
                                throw new CompilerException("expecting expression after '['", token);
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
                        }
                    }

                    try {
                        Variable var = new Variable(typeText.toUpperCase(), identifier.getText(), varSize);
                        var.setData(identifier);
                        scope.addSymbol(identifier.getText(), var);
                        variables.add(var);
                    } catch (Exception e) {
                        logMessage(new CompilerException(e, identifier));
                    }

                    if (!iter.hasNext()) {
                        break;
                    }

                    token = iter.next();
                    if (!",".equals(token.getText())) {
                        throw new CompilerException("expecting ',' or end of line", token);
                    }
                    if (!iter.hasNext()) {
                        throw new CompilerException("expecting identifier after ','", token);
                    }
                    token = iter.next();

                } while (true);

            } catch (CompilerException e) {
                logMessage(e);
            } catch (Exception e) {
                logMessage(new CompilerException(e, node));
            }
        }
    }

    void compileObject(ObjectNode node) {
        try {
            TokenIterator iter = node.tokenIterator();

            Token token = iter.next();
            String name = token.getText();
            Expression count = new NumberLiteral(1);

            if (!iter.hasNext()) {
                logMessage(new CompilerException("expecting ':' or '[' after '" + token.getText() + "'", token));
                return;
            }
            token = iter.next();

            if ("[".equals(token.getText())) {
                if (!iter.hasNext()) {
                    logMessage(new CompilerException("expecting expression after '" + token.getText() + "'", token));
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
                    logMessage(new CompilerException("expecting ']'", token));
                    return;
                }
                if (!iter.hasNext()) {
                    logMessage(new CompilerException("expecting ':' after '" + token.getText() + "'", token));
                    return;
                }
                token = iter.next();
            }

            if (!":".equals(token.getText())) {
                logMessage(new CompilerException("expecting ':'", token));
                return;
            }
            if (!iter.hasNext()) {
                logMessage(new CompilerException("expecting object file name '" + token.getText() + "'", token));
                return;
            }
            Token fileToken = iter.next();
            if (fileToken.type != Token.STRING) {
                logMessage(new CompilerException("expecting object file name", fileToken));
                return;
            }

            if (iter.hasNext()) {
                logMessage(new CompilerException("expecting end of line", iter.next()));
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

            File file = compiler.getFile(fileName, ".spin");
            if (file == null) {
                logMessage(new CompilerException("object " + fileName + " not found", fileToken));
                return;
            }

            ObjectInfo info = compiler.getObjectInfo(this, file, Collections.emptyMap());
            if (info == null) {
                logMessage(new CompilerException("object " + fileName + " not found", fileToken));
                return;
            }
            objects.put(name, new ObjectInfo(info, count));

            for (Entry<String, Expression> entry : info.compiler.getPublicSymbols().entrySet()) {
                if (!(entry.getValue() instanceof Method)) {
                    String qualifiedName = name + "#" + entry.getKey();
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
    protected void compileDatInclude(RootNode root) {
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
        compileDataBlocks(root);
    }

    Spin1Method compileMethod(MethodNode node) {
        Context localScope = new Context(scope);

        TokenIterator iter = node.tokenIterator();
        Token token = iter.next(); // First token is PUB/PRI already checked

        if (!iter.hasNext()) {
            logMessage(new CompilerException("expecting method name after '" + token.getText() + "'", token));
            return null;
        }
        token = iter.next();
        if (token.type != Token.KEYWORD) {
            logMessage(new CompilerException("expecting identifier", token));
            return null;
        }

        Spin1Method method = new Spin1Method(localScope, token.getText());
        method.setComment(node.getText());
        method.setData(node);

        if (iter.hasNext() && "(".equals(iter.peekNext().getText())) {
            token = iter.next();
            if (!iter.hasNext()) {
                logMessage(new CompilerException("expecting identifier or ')' after '" + token.getText() + "'", token));
                return method;
            }
            while (iter.hasNext()) {
                Token identifier = iter.next();
                if (")".equals(identifier.getText())) {
                    break;
                }
                if (Spin1Model.isType(identifier.getText())) {
                    logMessage(new CompilerException("type not allowed", identifier));
                }
                else if (identifier.type == Token.KEYWORD) {
                    Expression expression = localScope.getLocalSymbol(identifier.getText());
                    if (expression instanceof LocalVariable) {
                        logMessage(new CompilerException("symbol '" + identifier + "' already defined", identifier));
                    }
                    else {
                        if (expression != null) {
                            logMessage(new CompilerException(CompilerException.WARNING, "parameter '" + identifier + "' hides global variable", identifier));
                        }

                        Expression value = null;
                        if (iter.hasNext() && "=".equals(iter.peekNext().getText())) {
                            token = iter.next();
                            Spin1ExpressionBuilder builder = new Spin1ExpressionBuilder(scope);
                            while (iter.hasNext()) {
                                token = iter.peekNext();
                                if (",".equals(token.getText()) || ")".equals(token.getText())) {
                                    if (builder.getTokenCount() != 0) {
                                        try {
                                            value = builder.getExpression();
                                            if (!value.isConstant()) {
                                                logMessage(new CompilerException("expecting constant expression", builder.getTokens()));
                                            }
                                        } catch (CompilerException e) {
                                            logMessage(e);
                                        } catch (Exception e) {
                                            logMessage(new CompilerException(e, builder.getTokens()));
                                        }
                                    }
                                    else {
                                        logMessage(new CompilerException("expecting expression after '='", token));
                                    }
                                    break;
                                }
                                builder.addToken(iter.next());
                            }
                        }
                        if (value == null && method.getParametersCount() != 0) {
                            if (method.getParameter(method.getParametersCount() - 1).getValue() != null) {
                                logMessage(new CompilerException("expecting default value", identifier));
                            }
                        }

                        LocalVariable var = method.addParameter("LONG", identifier.getText(), value);
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
                            logMessage(new CompilerException("expecting identifier after '" + iter.current() + "'", iter.current()));
                        }
                    }
                    else {
                        logMessage(new CompilerException("expecting ',' or ')'", iter.current()));
                    }
                }
                else {
                    logMessage(new CompilerException("expecting ',' or ')' after '" + iter.current() + "'", iter.current()));
                }
            }
        }

        if (iter.hasNext() && ":".equals(iter.peekNext().getText())) {
            token = iter.next();
            if (!iter.hasNext()) {
                logMessage(new CompilerException("expecting return variable name after '" + token.getText() + "'", token));
            }
            while (iter.hasNext()) {
                Token identifier = iter.next();
                if (Spin1Model.isType(identifier.getText())) {
                    logMessage(new CompilerException("type not allowed", identifier));
                }
                else if (identifier.type == Token.KEYWORD) {
                    if (!"RESULT".equalsIgnoreCase(identifier.getText())) {
                        Expression expression = localScope.getLocalSymbol(identifier.getText());
                        if (expression instanceof LocalVariable) {
                            logMessage(new CompilerException("symbol '" + identifier.getText() + "' already defined", identifier));
                        }
                        else {
                            if (expression != null) {
                                logMessage(new CompilerException(CompilerException.WARNING, "return variable '" + identifier.getText() + "' hides global variable", identifier));
                            }
                            LocalVariable var = method.addReturnVariable(identifier.getText());
                            var.setData(identifier);
                        }
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
                            logMessage(new CompilerException("expecting identifier after ','", token));
                            return method;
                        }
                    }
                    else {
                        logMessage(new CompilerException("expecting ',' or ':'", token));
                        return method;
                    }
                }
            }
        }

        if (iter.hasNext() && "|".equals(iter.peekNext().getText())) {
            token = iter.next();
            if (!iter.hasNext()) {
                logMessage(new CompilerException("expecting local variable name after '" + token.getText() + "'", token));
            }
            while (iter.hasNext()) {
                Token identifier = iter.next();

                String type = "LONG";
                if (Spin1Model.isType(identifier.getText())) {
                    type = identifier.getText().toUpperCase();
                    if (!iter.hasNext()) {
                        logMessage(new CompilerException("expecting local variable name after '" + identifier.getText() + "'", identifier));
                    }
                    identifier = iter.next();
                }

                if (identifier.type == Token.KEYWORD) {
                    int size = 1;

                    if (iter.hasNext() && "[".equals(iter.peekNext().getText())) {
                        Token start = iter.next();
                        if (!iter.hasNext()) {
                            logMessage(new CompilerException("expecting expression after '['", start));
                        }
                        else {
                            Spin1ExpressionBuilder builder = new Spin1ExpressionBuilder(scope);
                            while (iter.hasNext()) {
                                token = iter.next();
                                if ("]".equals(token.getText())) {
                                    if (builder.getTokenCount() != 0) {
                                        try {
                                            Expression expression = builder.getExpression();
                                            if (expression.isConstant()) {
                                                size = expression.getNumber().intValue();
                                            }
                                            else {
                                                logMessage(new CompilerException("expecting constant expression", builder.getTokens()));
                                            }
                                        } catch (CompilerException e) {
                                            logMessage(e);
                                        } catch (Exception e) {
                                            logMessage(new CompilerException(e, builder.getTokens()));
                                        }
                                    }
                                    else {
                                        logMessage(new CompilerException("expecting expression after '['", start));
                                    }
                                    break;
                                }
                                builder.addToken(token);
                            }
                            if (!"]".equals(token.getText())) {
                                logMessage(new CompilerException("expecting ']' after '" + token.getText() + "'", token));
                            }
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
                        LocalVariable var = method.addLocalVariable(type, identifier.getText(), size); // new LocalVariable(type, identifier.getText(), size, offset);
                        var.setData(identifier);
                    }
                }
                else {
                    logMessage(new CompilerException("invalid identifier", identifier));
                }

                if (iter.hasNext()) {
                    token = iter.next();
                    if (",".equals(token.getText())) {
                        if (!iter.hasNext()) {
                            logMessage(new CompilerException("expecting identifier after '" + token.getText() + "'", token));
                        }
                    }
                    else {
                        logMessage(new CompilerException("expecting ',' or end of line", token));
                    }
                }
            }
        }

        if (iter.hasNext()) {
            logMessage(new CompilerException("expecting end of line", iter.next()));
        }

        return method;
    }

    List<Spin1MethodLine> compileStatement(Context context, Spin1Method method, Spin1MethodLine parent, Node statementNode) {
        List<Spin1MethodLine> lines = new ArrayList<>();
        Spin1MethodLine previousLine = null;

        List<Node> statements = buildFilteredStatements(statementNode);

        Iterator<Node> nodeIterator = statements.iterator();
        while (nodeIterator.hasNext()) {
            Node node = nodeIterator.next();
            try {
                if (node instanceof StatementNode) {
                    Spin1MethodLine line = compileStatement(context, method, parent, node, previousLine);
                    if (line != null) {
                        lines.add(line);
                        previousLine = line;
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
            if (node.isExclude()) {
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

    Spin1MethodLine compileStatement(Context context, Spin1Method method, Spin1MethodLine parent, Node node, Spin1MethodLine previousLine) {
        Spin1MethodLine line = null;

        try {
            TokenIterator iter = node.tokenIterator();
            if (!iter.hasNext()) {
                throw new RuntimeException("syntax error");
            }

            Token token = iter.next();
            String text = token.getText().toUpperCase();

            if ("ABORT".equals(text)) {
                line = new Spin1MethodLine(context, parent, token.getText(), node);
                if (iter.hasNext()) {
                    Spin1TreeBuilder builder = new Spin1TreeBuilder(context);
                    while (iter.hasNext()) {
                        builder.addToken(iter.next());
                    }
                    line.addSource(compileBytecodeExpression(context, method, builder.getRoot(), true));
                    line.addSource(new Bytecode(line.getScope(), 0b00110001, text));
                }
                else {
                    line.addSource(new Bytecode(line.getScope(), 0b00110000, text));
                }
            }
            else if ("IF".equals(text) || "IFNOT".equals(text)) {
                line = new Spin1MethodLine(context, parent, token.getText(), node);

                Spin1TreeBuilder builder = new Spin1TreeBuilder(context);
                while (iter.hasNext()) {
                    builder.addToken(iter.next());
                }
                Spin1StatementNode statementNode = builder.getRoot();
                line.addSource(compileBytecodeExpression(context, method, statementNode, true));

                Spin1MethodLine falseLine = new Spin1MethodLine(context);
                if ("IF".equals(text)) {
                    line.addSource(new Jz(line.getScope(), new ContextLiteral(falseLine.getScope())));
                }
                else {
                    line.addSource(new Jnz(line.getScope(), new ContextLiteral(falseLine.getScope())));
                }

                line.addChilds(compileStatement(new Context(context), method, line, node));

                line.addChild(falseLine);
                line.addChild(new Spin1MethodLine(context));
            }
            else if ("ELSE".equals(text) || "ELSEIF".equals(text) || "ELSEIFNOT".equals(text)) {
                if (previousLine == null || (!previousLine.getStatement().toUpperCase().startsWith("IF") && !previousLine.getStatement().toUpperCase().startsWith("ELSEIF"))) {
                    throw new CompilerException("misplaced " + token.getText().toLowerCase(), node);
                }

                line = new Spin1MethodLine(context, parent, text, node);
                Spin1MethodLine falseLine = new Spin1MethodLine(context);
                Spin1MethodLine exitLine = previousLine.getChilds().remove(previousLine.getChilds().size() - 1);

                if ("ELSEIF".equals(text) || "ELSEIFNOT".equals(text)) {
                    Spin1TreeBuilder builder = new Spin1TreeBuilder(context);
                    while (iter.hasNext()) {
                        builder.addToken(iter.next());
                    }
                    Spin1StatementNode statementNode = builder.getRoot();
                    line.addSource(compileBytecodeExpression(context, method, statementNode, true));
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

                Spin1MethodLine jmpLine = new Spin1MethodLine(context);
                jmpLine.addSource(new Jmp(line.getScope(), new ContextLiteral(exitLine.getScope())));
                previousLine.addChild(previousLine.getChilds().size() - 1, jmpLine);
            }
            else if ("RETURN".equals(text)) {
                line = new Spin1MethodLine(context, parent, text, node);

                if (iter.hasNext()) {
                    Spin1TreeBuilder builder = new Spin1TreeBuilder(context);
                    while (iter.hasNext()) {
                        builder.addToken(iter.next());
                    }
                    line.addSource(compileBytecodeExpression(line.getScope(), method, builder.getRoot(), true));
                    line.addSource(new Bytecode(line.getScope(), 0b00110011, text));
                }
                else {
                    line.addSource(new Bytecode(line.getScope(), 0b00110010, text));
                }
            }
            else if ("REPEAT".equals(text)) {
                line = new Spin1MethodLine(context, parent, text, node);

                Spin1MethodLine quitLine = new Spin1MethodLine(context);
                line.setData("quit", quitLine);

                if (iter.hasNext()) {
                    token = iter.next();
                    if ("WHILE".equalsIgnoreCase(token.getText()) || "UNTIL".equalsIgnoreCase(token.getText())) {
                        line.setStatement(text + " " + token.getText().toUpperCase());
                        line.setData("next", line);

                        Spin1TreeBuilder builder = new Spin1TreeBuilder(context);
                        while (iter.hasNext()) {
                            builder.addToken(iter.next());
                        }
                        Spin1StatementNode statementNode = builder.getRoot();
                        line.addSource(compileBytecodeExpression(line.getScope(), method, statementNode, true));

                        if ("WHILE".equalsIgnoreCase(token.getText())) {
                            line.addSource(new Jz(line.getScope(), new ContextLiteral(quitLine.getScope())));
                        }
                        else {
                            line.addSource(new Jnz(line.getScope(), new ContextLiteral(quitLine.getScope())));
                        }

                        line.addChilds(compileStatement(new Context(context), method, line, node));

                        Spin1MethodLine loopLine = new Spin1MethodLine(context);
                        loopLine.addSource(new Jmp(loopLine.getScope(), new ContextLiteral(line.getScope())));
                        line.addChild(loopLine);
                    }
                    else {
                        Spin1StatementNode counter = null;
                        Spin1StatementNode from = null;
                        Spin1StatementNode to = null;
                        Spin1StatementNode step = null;

                        Spin1TreeBuilder builder = new Spin1TreeBuilder(context);
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
                            builder = new Spin1TreeBuilder(context);
                            while (iter.hasNext()) {
                                token = iter.next();
                                if ("TO".equalsIgnoreCase(token.getText())) {
                                    break;
                                }
                                builder.addToken(token);
                            }
                            from = builder.getRoot();

                            if ("TO".equalsIgnoreCase(token.getText())) {
                                builder = new Spin1TreeBuilder(context);
                                while (iter.hasNext()) {
                                    token = iter.next();
                                    if ("STEP".equalsIgnoreCase(token.getText())) {
                                        break;
                                    }
                                    builder.addToken(token);
                                }
                                to = builder.getRoot();

                                if ("STEP".equalsIgnoreCase(token.getText())) {
                                    builder = new Spin1TreeBuilder(context);
                                    while (iter.hasNext()) {
                                        builder.addToken(iter.next());
                                    }
                                    step = builder.getRoot();
                                }
                            }
                        }

                        if (from != null && to != null) {
                            line.addSource(compileBytecodeExpression(line.getScope(), method, from, true));

                            line.addSource(leftAssign(context, method, counter, false));

                            Spin1MethodLine nextLine = new Spin1MethodLine(context);
                            line.setData("next", nextLine);

                            Spin1MethodLine loopLine = new Spin1MethodLine(context);
                            line.addChild(loopLine);
                            line.addChilds(compileStatement(new Context(context), method, line, node));

                            if (step != null) {
                                nextLine.addSource(compileBytecodeExpression(line.getScope(), method, step, true));
                            }
                            nextLine.addSource(compileBytecodeExpression(line.getScope(), method, from, true));
                            nextLine.addSource(compileBytecodeExpression(line.getScope(), method, to, true));

                            nextLine.addSource(leftAssign(context, method, counter, true));
                            nextLine.addSource(new RepeatLoop(line.getScope(), step != null, new ContextLiteral(loopLine.getScope())));
                            line.addChild(nextLine);
                        }
                        else {
                            line.addSource(compileBytecodeExpression(line.getScope(), method, counter, true));
                            line.addSource(new Tjz(line.getScope(), new ContextLiteral(quitLine.getScope())));
                            line.setData("pop", Integer.valueOf(0));

                            Spin1MethodLine nextLine = new Spin1MethodLine(context);
                            line.setData("next", nextLine);

                            Spin1MethodLine loopLine = new Spin1MethodLine(context);
                            line.addChild(loopLine);
                            line.addChilds(compileStatement(new Context(context), method, line, node));

                            nextLine.addSource(new Djnz(nextLine.getScope(), new ContextLiteral(loopLine.getScope())));
                            line.addChild(nextLine);
                        }
                    }
                }
                else {
                    Spin1MethodLine nextLine = new Spin1MethodLine(context);
                    line.setData("next", nextLine);

                    line.addChild(nextLine);
                    line.addChilds(compileStatement(new Context(context), method, line, node));

                    Spin1MethodLine loopLine = new Spin1MethodLine(context);
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

                line = new Spin1MethodLine(context, parent, text, node);

                Spin1MethodLine nextLine = (Spin1MethodLine) previousLine.getData("next");
                previousLine.getChilds().remove(nextLine);
                line.addChild(nextLine);

                Spin1MethodLine conditionLine = new Spin1MethodLine(context);

                Spin1TreeBuilder builder = new Spin1TreeBuilder(context);
                while (iter.hasNext()) {
                    builder.addToken(iter.next());
                }
                conditionLine.addSource(compileBytecodeExpression(line.getScope(), method, builder.getRoot(), true));

                if ("WHILE".equals(text)) {
                    conditionLine.addSource(new Jnz(previousLine.getScope(), new ContextLiteral(previousLine.getScope())));
                }
                else {
                    conditionLine.addSource(new Jz(previousLine.getScope(), new ContextLiteral(previousLine.getScope())));
                }

                line.addChild(conditionLine);

                Spin1MethodLine quitLine = (Spin1MethodLine) previousLine.getData("quit");
                previousLine.getChilds().remove(quitLine);
                line.addChild(quitLine);
            }
            else if ("QUIT".equals(text) || "NEXT".equals(text)) {
                int pop = 0;
                String key = text.toLowerCase();

                line = new Spin1MethodLine(context, parent, text, node);

                while (parent != null && parent.getData(key) == null) {
                    if (parent.getData("pop") != null) {
                        pop += ((Integer) parent.getData("pop")).intValue();
                    }
                    parent = parent.getParent();
                }
                if (parent == null || parent.getData(key) == null) {
                    throw new CompilerException("misplaced " + text, node);
                }

                if ("QUIT".equals(text) && parent.getData("pop") != null) {
                    pop += ((Integer) parent.getData("pop")).intValue();
                }

                if (pop != 0) {
                    try {
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        os.write(new Constant(line.getScope(), new NumberLiteral(pop), compiler.isFastByteConstants()).getBytes());
                        os.write(0x14);
                        line.addSource(new Bytecode(line.getScope(), os.toByteArray(), String.format("POP %d", pop)));
                    } catch (Exception e) {
                        // Do nothing
                    }
                }

                Spin1MethodLine nextLine = (Spin1MethodLine) parent.getData(key);
                if ("NEXT".equals(text)) {
                    line.addSource(new Jmp(line.getScope(), new ContextLiteral(nextLine.getScope())));
                }
                else {
                    if (parent.getData("pop") != null && ((Integer) parent.getData("pop")).intValue() == 0) {
                        line.addSource(new Jnz(line.getScope(), new ContextLiteral(nextLine.getScope())));
                    }
                    else {
                        line.addSource(new Jmp(line.getScope(), new ContextLiteral(nextLine.getScope())));
                    }
                }
            }
            else if ("CASE".equalsIgnoreCase(text)) {
                line = new Spin1MethodLine(context, parent, text, node);
                line.setData("pop", Integer.valueOf(8));

                Spin1MethodLine endLine = new Spin1MethodLine(context);
                line.addSource(new Address(line.getScope(), new ContextLiteral(endLine.getScope())));

                Spin1TreeBuilder builder = new Spin1TreeBuilder(context);
                while (iter.hasNext()) {
                    builder.addToken(iter.next());
                }
                Spin1StatementNode statementNode = builder.getRoot();
                line.addSource(compileBytecodeExpression(context, method, statementNode, true));

                boolean hasOther = false;
                for (Node child : node.getChilds()) {
                    if (child.isExclude()) {
                        continue;
                    }
                    if (child instanceof StatementNode) {
                        Spin1MethodLine caseLine = new Spin1MethodLine(context);
                        caseLine.addChilds(compileStatement(new Context(context), method, line, child));

                        TokenIterator childIter = child.tokenIterator();
                        token = childIter.next();

                        if ("OTHER".equalsIgnoreCase(token.getText())) {
                            if (!childIter.hasNext()) {
                                logMessage(new CompilerException("expecting ':'", token));
                            }
                            else {
                                token = childIter.next();
                                if (!":".equals(token.getText())) {
                                    logMessage(new CompilerException("expecting ':'", token));
                                }
                            }
                            line.addChild(0, caseLine);
                            hasOther = true;
                        }
                        else {
                            builder = new Spin1TreeBuilder(context);
                            builder.addToken(token);
                            while (childIter.hasNext()) {
                                token = childIter.next();
                                if (":".equals(token.getText())) {
                                    break;
                                }
                                builder.addToken(token);
                            }
                            if (!":".equals(token.getText())) {
                                logMessage(new CompilerException("expecting ':'", token));
                            }
                            statementNode = builder.getRoot();
                            compileCase(method, line, statementNode, caseLine);

                            line.addChild(caseLine);
                        }
                        if (childIter.hasNext()) {
                            logMessage(new CompilerException("unexpected", childIter.next()));
                        }

                        Spin1MethodLine doneLine = new Spin1MethodLine(context);
                        doneLine.addSource(new Bytecode(line.getScope(), 0x0C, "CASE_DONE"));
                        caseLine.addChild(doneLine);
                    }
                }

                if (!hasOther) {
                    line.addSource(new Bytecode(line.getScope(), 0x0C, "CASE_DONE"));
                }

                line.addChild(endLine);
            }
            else {
                Spin1TreeBuilder builder = new Spin1TreeBuilder(context);
                builder.addToken(token);
                while (iter.hasNext()) {
                    builder.addToken(iter.next());
                }
                Spin1StatementNode statementNode = builder.getRoot();
                line = new Spin1MethodLine(context, parent, null, node);
                line.addSource(compileBytecodeExpression(context, method, statementNode, false));
            }

        } catch (CompilerException e) {
            logMessage(e);
        } catch (Exception e) {
            logMessage(new CompilerException(e, node));
        }

        return line;
    }

    void compileCase(Spin1Method method, Spin1MethodLine line, Spin1StatementNode arg, Spin1MethodLine target) {
        if (",".equals(arg.getText())) {
            for (Spin1StatementNode child : arg.getChilds()) {
                compileCase(method, line, child, target);
            }
        }
        else if ("..".equals(arg.getText())) {
            line.addSource(compileBytecodeExpression(line.getScope(), method, arg.getChild(0), true));
            line.addSource(compileBytecodeExpression(line.getScope(), method, arg.getChild(1), true));
            if (target != null) {
                line.addSource(new CaseRangeJmp(line.getScope(), new ContextLiteral(target.getScope())));
            }
        }
        else {
            line.addSource(compileBytecodeExpression(line.getScope(), method, arg, true));
            if (target != null) {
                line.addSource(new CaseJmp(line.getScope(), new ContextLiteral(target.getScope())));
            }
        }
    }

    void determineClock() {
        Expression _clkmode = scope.getSystemSymbol("_CLKMODE");
        Expression _clkfreq = scope.getSystemSymbol("_CLKFREQ");
        Expression _xinfreq = scope.getSystemSymbol("_XINFREQ");

        if (_clkmode == null && _clkfreq == null && _xinfreq == null) {
            clkMode = 0;
            clkFreq = 12_000_000;
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
            clkMode = mode == 2 ? 1 : 0;
            clkFreq = mode == 2 ? 20_000 : 12_000_000;
            return;
        }

        if (_clkfreq == null && _xinfreq == null) {
            throw new CompilerException("_CLKFREQ or _XINFREQ must be specified", _clkmode.getData());
        }

        try {
            int bitPos = getBitPos((mode >> 2) & 0x0F);
            clkMode = (bitPos << 3) | 0x22;

            int freqshift = 0;
            if ((mode & 0x7C0) != 0) {
                freqshift = getBitPos(mode >> 6);
                clkMode += freqshift + 0x41;
            }
            if (_xinfreq != null) {
                clkFreq = _xinfreq.getNumber().intValue() << freqshift;
            }
            else {
                clkFreq = _clkfreq.getNumber().intValue();
                xinFreq = _clkfreq.getNumber().intValue() >> freqshift;
            }
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

}
