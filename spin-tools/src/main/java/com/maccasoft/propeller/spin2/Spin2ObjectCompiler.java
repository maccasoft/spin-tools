/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.collections4.map.CaseInsensitiveMap;

import com.maccasoft.propeller.Compiler.ObjectInfo;
import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.ObjectCompiler;
import com.maccasoft.propeller.SpinObject.LinkDataObject;
import com.maccasoft.propeller.SpinObject.LongDataObject;
import com.maccasoft.propeller.expressions.Add;
import com.maccasoft.propeller.expressions.BinaryOperator;
import com.maccasoft.propeller.expressions.CharacterLiteral;
import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.ContextLiteral;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.Identifier;
import com.maccasoft.propeller.expressions.LocalVariable;
import com.maccasoft.propeller.expressions.Method;
import com.maccasoft.propeller.expressions.Multiply;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.expressions.ObjectIdentifier;
import com.maccasoft.propeller.expressions.Register;
import com.maccasoft.propeller.expressions.Variable;
import com.maccasoft.propeller.model.ConstantsNode;
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.DirectiveNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.ObjectsNode;
import com.maccasoft.propeller.model.RootNode;
import com.maccasoft.propeller.model.SourceLine;
import com.maccasoft.propeller.model.StatementNode;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.TokenIterator;
import com.maccasoft.propeller.model.VariablesNode;
import com.maccasoft.propeller.spin2.Spin2Debug.DebugDataObject;
import com.maccasoft.propeller.spin2.Spin2Object.Spin2LinkDataObject;
import com.maccasoft.propeller.spin2.Spin2Struct.Spin2StructMember;
import com.maccasoft.propeller.spin2.bytecode.Address;
import com.maccasoft.propeller.spin2.bytecode.Bytecode;
import com.maccasoft.propeller.spin2.bytecode.CaseFastJmp;
import com.maccasoft.propeller.spin2.bytecode.CaseJmp;
import com.maccasoft.propeller.spin2.bytecode.CaseRangeJmp;
import com.maccasoft.propeller.spin2.bytecode.Constant;
import com.maccasoft.propeller.spin2.bytecode.Djnz;
import com.maccasoft.propeller.spin2.bytecode.InlinePAsm;
import com.maccasoft.propeller.spin2.bytecode.InlinePAsmExec;
import com.maccasoft.propeller.spin2.bytecode.Jmp;
import com.maccasoft.propeller.spin2.bytecode.Jnz;
import com.maccasoft.propeller.spin2.bytecode.Jz;
import com.maccasoft.propeller.spin2.bytecode.Tjz;
import com.maccasoft.propeller.spin2.instructions.Org;
import com.maccasoft.propeller.spin2.instructions.Orgh;

public class Spin2ObjectCompiler extends Spin2BytecodeCompiler {

    List<Variable> variables = new ArrayList<>();
    List<Spin2Method> methods = new ArrayList<>();

    int objectVarSize;

    Map<String, Expression> publicSymbols = new HashMap<>();
    List<LinkDataObject> objectLinks = new ArrayList<>();
    List<Spin2Struct> objectStructures = new ArrayList<>();

    Set<Spin2Struct> structureStack = new HashSet<>();

    Expression enumValue = new NumberLiteral(0);
    Expression enumIncrement = new NumberLiteral(1);

    public Spin2ObjectCompiler(Spin2Compiler compiler, File file) {
        this(compiler, null, file, Collections.emptyMap());
    }

    public Spin2ObjectCompiler(Spin2Compiler compiler, ObjectCompiler parent, File file, Map<String, Expression> parameters) {
        super(new Spin2GlobalContext(compiler.isCaseSensitive()), compiler, parent, file);

        if (parent != null) {
            scope.addDefinitions(parent.getScope().getDefinitions());
        }
        scope.addDefinitions(compiler.getDefines());
        scope.addParameters(parameters);

        Date now = new Date();
        scope.addDefinition("__DATE__", new CharacterLiteral(SimpleDateFormat.getDateInstance().format(now)));
        scope.addDefinition("__TIME__", new CharacterLiteral(SimpleDateFormat.getTimeInstance().format(now)));
        scope.addDefinition("__FILE__", new CharacterLiteral(file.getAbsolutePath()));

        scope.addDefinition("__P2__", new NumberLiteral(1));
        scope.addDefinition("__SPINTOOLS__", new NumberLiteral(1));
        if (compiler.isDebugEnabled()) {
            scope.addDefinition("__DEBUG__", new NumberLiteral(1));
        }
        scope.addDefinition("__propeller__", new NumberLiteral(2));
        scope.addDefinition("__propeller2__", new NumberLiteral(1));
    }

    @Override
    public Spin2Object compileObject(String text) {
        compileStep1(text);
        compileStep2(true);
        return generateObject(0);
    }

    @Override
    public RootNode compileStep1(String text) {
        List<VariablesNode> variableNodes = new ArrayList<>();

        Spin2Parser parser = new Spin2Parser(text) {

            @Override
            protected boolean isExcluded() {
                return !conditionStack.isEmpty() && conditionStack.peek().skip;
            }

            @Override
            protected void processDirective(DirectiveNode node) {
                try {
                    compileDirective(node);
                } catch (CompilerException e) {
                    logMessage(e);
                } catch (Exception e) {
                    logMessage(new CompilerException(e, node.getTokens()));
                }
            }

            @Override
            protected ConstantsNode parseConBlock(ConstantsNode node, SourceLine sourceLine) {
                enumValue = new NumberLiteral(0);
                enumIncrement = new NumberLiteral(1);
                return super.parseConBlock(node, sourceLine);
            }

            @Override
            protected void parseConstant(Node parent, SourceLine sourceLine) {
                int index = sourceLine.getIndex();
                try {
                    compileConstant(sourceLine);
                } catch (CompilerException e) {
                    logMessage(e);
                } finally {
                    sourceLine.setIndex(index);
                }
                super.parseConstant(parent, sourceLine);
            }

            @Override
            void parseObjectLine(ObjectsNode parent, SourceLine sourceLine) {
                int index = sourceLine.getIndex();
                try {
                    compileObject(sourceLine);
                } catch (CompilerException e) {
                    logMessage(e);
                } finally {
                    sourceLine.setIndex(index);
                }
                super.parseObjectLine(parent, sourceLine);
            }

            @Override
            void parseVariable(VariablesNode parent, SourceLine sourceLine) {
                super.parseVariable(parent, sourceLine);
                variableNodes.add(parent);
            }

        };
        RootNode root = parser.parse();
        for (String name : objects.keySet()) {
            root.addObjectRoot(name, objects.get(name).root);
        }

        while (!conditionStack.isEmpty()) {
            Condition c = conditionStack.pop();
            logMessage(new CompilerException("unbalanced conditional directive", c.node));
        }

        computeClockMode();

        Iterator<Entry<String, Expression>> iter = publicSymbols.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, Expression> entry = iter.next();
            try {
                Expression expression = entry.getValue();
                if (expression instanceof Identifier) {
                    String identifier = ((Identifier) expression).getName();
                    Spin2Struct struct = scope.getStructureDefinition(identifier);
                    if (struct == null) {
                        for (Entry<String, ObjectInfo> infoEntry : objects.entrySet()) {
                            struct = infoEntry.getValue().compiler.getScope().getStructureDefinition(identifier);
                            if (struct != null) {
                                break;
                            }
                        }
                    }
                    if (struct != null) {
                        iter.remove();
                        scope.addStructureDefinition(entry.getKey(), struct);
                        continue;
                    }
                }
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

        for (Spin2Struct struct : objectStructures) {
            updateStructureMemberOffsets(struct);
        }

        objectVarSize = 4;
        variables.clear();

        for (VariablesNode node : variableNodes) {
            compileVarBlock(node);
        }

        objectVarSize = (objectVarSize + 3) & ~3;

        compileDataBlocks(root);

        for (Entry<String, ObjectInfo> infoEntry : objects.entrySet()) {
            ObjectInfo info = infoEntry.getValue();
            String name = infoEntry.getKey();

            try {
                int count = info.count.getNumber().intValue();
                int objectIndex = objectLinks.size();

                for (Entry<String, Expression> objEntry : info.compiler.getPublicSymbols().entrySet()) {
                    if (objEntry.getValue() instanceof Method objectMethod) {
                        String qualifiedName = name + "." + objEntry.getKey();
                        Method method = new Method(objectMethod.getName(), objectMethod.getMinArgumentsCount(), objectMethod.getArgumentsCount(), objectMethod.getReturnLongs()) {

                            @Override
                            public int getObjectIndex() {
                                return objectIndex;
                            }

                            @Override
                            public int getIndex() {
                                return objectMethod.getIndex();
                            }

                        };
                        method.setData(Spin2Method.class.getName(), objectMethod.getData(Spin2Method.class.getName()));
                        scope.addSymbol(qualifiedName, method);
                    }
                }
                scope.addSymbol(name, new ObjectIdentifier(name, count) {

                    @Override
                    public int getIndex() {
                        return objectIndex;
                    }

                });

                for (int i = 0; i < count; i++) {
                    objectLinks.add(new Spin2LinkDataObject(info.compiler, info.compiler.getVarSize()));
                }

            } catch (CompilerException e) {
                logMessage(e);
            } catch (Exception e) {
                logMessage(new CompilerException(e, info.count.getData()));
            }
        }

        for (Node node : root.getChilds()) {
            if (node.isExclude()) {
                continue;
            }
            if ((node instanceof MethodNode) && "PUB".equalsIgnoreCase(((MethodNode) node).getType().getText())) {
                try {
                    Spin2Method method = compileMethod((MethodNode) node);
                    if (method != null) {
                        Method exp = new Method(method.getLabel(), method.getMinParameterLongs(), method.getParameterLongs(), method.getReturnLongs()) {

                            @Override
                            public int getIndex() {
                                return objectLinks.size() * 2 + methods.indexOf(method);
                            }

                        };
                        exp.setData(method.getClass().getName(), method);

                        try {
                            scope.addSymbol(method.getLabel(), exp);
                            publicSymbols.put(method.getLabel(), exp);
                        } catch (Exception e) {
                            logMessage(new CompilerException(e.getMessage(), node));
                        }

                        methods.add(method);
                    }
                } catch (CompilerException e) {
                    logMessage(e);
                } catch (Exception e) {
                    e.printStackTrace();
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
                    Spin2Method method = compileMethod((MethodNode) node);
                    if (method != null) {
                        Method exp = new Method(method.getLabel(), method.getMinParameterLongs(), method.getParameterLongs(), method.getReturnLongs()) {

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

        if (!methods.isEmpty()) {
            scope.addBuiltinSymbol("@CLKMODE", new NumberLiteral(0x40));
            scope.addBuiltinSymbol("@CLKFREQ", new NumberLiteral(0x44));
        }

        generateDatObject();

        for (Spin2Method method : methods) {
            Node node = (Node) method.getData();
            for (Spin2MethodLine line : compileStatement(method.getScope(), method, null, node)) {
                method.addSource(line);
            }
            Spin2MethodLine line = new Spin2MethodLine(method.getScope(), "RETURN");
            line.addSource(new Bytecode(line.getScope(), Spin2Bytecode.bc_return_results, line.getStatement()));
            method.addSource(line);
        }

        for (Spin2Method method : methods) {
            for (Spin2StatementNode node : method.debugNodes) {
                DebugDataObject debugData = node.getDebugData();
                if (debugData != null) {
                    compiler.addDebugStatement(debugData);
                }
            }
        }

        return root;
    }

    void updateStructureMemberOffsets(Spin2Struct struct) {
        int offset = 0;

        structureStack.add(struct);

        for (Spin2StructMember member : struct.getMembers()) {
            Token type = member.getType();
            String typeText = type != null ? type.getText() : "LONG";

            member.setOffset(offset);

            int typeSize = 0;
            switch (typeText.toUpperCase()) {
                case "LONG":
                    typeSize = 4;
                    break;
                case "WORD":
                    typeSize = 2;
                    break;
                case "BYTE":
                    typeSize = 1;
                    break;
                case "^LONG":
                case "^WORD":
                case "^BYTE":
                    typeSize = 4;
                    break;
                default: {
                    Spin2Struct memberStruct = scope.getStructureDefinition(typeText.startsWith("^") ? typeText.substring(1) : typeText);
                    if (memberStruct == null) {
                        logMessage(new CompilerException("undefined type " + typeText, type));
                        break;
                    }
                    if (structureStack.contains(memberStruct)) {
                        logMessage(new CompilerException("illegal circular structure reference " + typeText, type));
                        break;
                    }
                    if (typeText.startsWith("^")) {
                        typeSize = 4;
                    }
                    else {
                        updateStructureMemberOffsets(memberStruct);
                        typeSize = memberStruct.getTypeSize();
                    }
                    break;
                }
            }
            try {
                Expression expression = member.getSize();
                if (!expression.isConstant()) {
                    logMessage(new CompilerException("expression is not constant", expression.getData()));
                }
                offset += typeSize * member.getSize().getNumber().intValue();
            } catch (CompilerException e) {
                logMessage(e);
            } catch (Exception e) {
                logMessage(new CompilerException(e, member.getSize().getData()));
            }
        }

        struct.setTypeSize(offset);

        structureStack.remove(struct);
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

        Iterator<Spin2Method> methodsIterator = methods.iterator();
        if (methodsIterator.hasNext()) {
            if (keepFirst) {
                Spin2Method method = methodsIterator.next();
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
                Spin2Method method = methodsIterator.next();
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
            Iterator<Spin2Method> methodsIterator = methods.iterator();
            if (methodsIterator.hasNext()) {
                if (keepFirst) {
                    methodsIterator.next();
                }
                while (methodsIterator.hasNext()) {
                    Spin2Method method = methodsIterator.next();
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
    public Spin2Object generateObject(int memoryOffset) {
        Spin2Object object = new Spin2Object(getFile(), memoryOffset);

        Expression exp = scope.getSystemSymbol("CLKFREQ_");
        if (exp != null) {
            object.setClkFreq(exp.getNumber().intValue());
        }
        exp = scope.getSystemSymbol("CLKMODE_");
        if (exp != null) {
            object.setClkMode(exp.getNumber().intValue());
        }

        exp = scope.getSystemSymbol("DEBUG_PIN");
        if (exp != null) {
            object.setDebugRxPin(exp.getNumber().intValue() & 0x3F);
        }
        exp = scope.getSystemSymbol("DEBUG_PIN_RX");
        if (exp != null) {
            object.setDebugRxPin(exp.getNumber().intValue() & 0x3F);
        }
        exp = scope.getSystemSymbol("DEBUG_PIN_TX");
        if (exp != null) {
            object.setDebugTxPin(exp.getNumber().intValue() & 0x3F);
        }
        exp = scope.getSystemSymbol("DEBUG_BAUD");
        if (exp != null) {
            object.setDebugBaud(exp.getNumber().intValue());
        }
        exp = scope.getSystemSymbol("DEBUG_DELAY");
        if (exp != null) {
            object.setDebugDelay(exp.getNumber().intValue());
        }
        exp = scope.getSystemSymbol("DEBUG_COGINIT");
        if (exp != null) {
            object.setDebugBrkCond(0x110);
        }
        exp = scope.getSystemSymbol("DEBUG_MAIN");
        if (exp != null) {
            object.setDebugBrkCond(0x001);
        }
        exp = scope.getSystemSymbol("DEBUG_COGS");
        if (exp != null) {
            object.setDebugCogs(exp.getNumber().intValue());
        }

        object.setVarSize(getVarSize());
        object.writeComment(String.format("Object \"%s\" header (var size %d)", getFile().getName(), object.getVarSize()));

        int linkedVarOffset = objectVarSize;
        for (LinkDataObject linkData : objectLinks) {
            linkData.setVarOffset(linkedVarOffset);
            object.write(linkData);
            linkedVarOffset += linkData.getVarSize();
        }

        List<LongDataObject> methodData = new ArrayList<>();
        if (!methods.isEmpty()) {
            for (Spin2Method method : methods) {
                LongDataObject dataObject = new LongDataObject(0, "Method " + method.getLabel());
                object.write(dataObject);
                methodData.add(dataObject);
            }
            LongDataObject dataObject = new LongDataObject(0, "End");
            object.write(dataObject);
            methodData.add(dataObject);
        }

        writeDatBinary(memoryOffset, object, !methods.isEmpty());

        if (!methods.isEmpty()) {
            boolean hasErrors = false;
            boolean loop;
            do {
                loop = false;
                int address = object.getSize();
                for (Spin2Method method : methods) {
                    hasErrors = true;
                    try {
                        address = method.resolve(address, isDebugEnabled());
                        loop |= method.isAddressChanged();
                        hasErrors = false;
                    } catch (CompilerException e) {
                        logMessage(e);
                    } catch (Exception e) {
                        logMessage(new CompilerException(e, method.getData()));
                    }
                }
            } while (loop && !hasErrors);

            if (!hasErrors) {
                int index = 0;
                for (Spin2Method method : methods) {
                    int value = 0x80000000;

                    value = Spin2Method.address_bit.setValue(value, object.getSize());
                    value = Spin2Method.returns_bit.setValue(value, method.getReturnLongs());
                    value = Spin2Method.parameters_bit.setValue(value, method.getParameterLongs());

                    methodData.get(index).setValue(value);
                    methodData.get(index).setText(
                        String.format("Method %s @ $%05X (%d parameters, %d returns)", method.getLabel(), object.getSize(), method.getParameterLongs(), method.getReturnLongs()));
                    try {
                        method.writeTo(object, isDebugEnabled());
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
            }
        }

        object.alignToLong();

        return object;
    }

    void compileConstant(SourceLine sourceLine) {
        Token token;

        do {
            token = sourceLine.skipCommentsAndGetNextToken();
            if ("#".equals(token.getText())) {
                if (!sourceLine.hasMoreTokens()) {
                    logMessage(new CompilerException("expecting expression", token.stop + 1));
                    break;
                }
                Spin2ExpressionBuilder builder = new Spin2ExpressionBuilder(scope, true);
                try {
                    while (sourceLine.hasMoreTokens()) {
                        token = sourceLine.skipCommentsAndGetNextToken();
                        if ("[".equals(token.getText()) || ",".equals(token.getText())) {
                            break;
                        }
                        builder.addToken(token);
                    }
                    if (builder.getTokenCount() == 0) {
                        logMessage(new CompilerException("expecting expression", token));
                        break;
                    }
                    enumValue = builder.getExpression();
                } catch (CompilerException e) {
                    logMessage(e);
                } catch (Exception e) {
                    logMessage(new CompilerException("expression syntax error", builder.getTokens()));
                }
                enumIncrement = new NumberLiteral(1);

                if ("[".equals(token.getText())) {
                    if (!sourceLine.hasMoreTokens()) {
                        logMessage(new CompilerException("expecting expression", token.stop + 1));
                        break;
                    }
                    builder = new Spin2ExpressionBuilder(scope, true);
                    try {
                        while (sourceLine.hasMoreTokens()) {
                            token = sourceLine.skipCommentsAndGetNextToken();
                            if ("]".equals(token.getText())) {
                                break;
                            }
                            builder.addToken(token);
                        }
                        if (builder.getTokenCount() == 0) {
                            logMessage(new CompilerException("expecting expression", token));
                            break;
                        }
                        enumIncrement = builder.getExpression();
                    } catch (CompilerException e) {
                        logMessage(e);
                    } catch (Exception e) {
                        logMessage(new CompilerException(e, builder.getTokens()));
                    }
                    if (!"]".equals(token.getText())) {
                        logMessage(new CompilerException("expecting ']'", token));
                        break;
                    }
                }

                if (!",".equals(token.getText()) && sourceLine.hasMoreTokens()) {
                    token = sourceLine.skipCommentsAndGetNextToken();
                    if (!",".equals(token.getText())) {
                        logMessage(new CompilerException("expecting ',' or end of line", token));
                        break;
                    }
                }
            }
            else if ("struct".equalsIgnoreCase(token.getText())) {
                if (!sourceLine.hasMoreTokens()) {
                    logMessage(new CompilerException("expecting identifier", token.stop + 1));
                    break;
                }
                Token identifier = sourceLine.skipCommentsAndGetNextToken();
                if (identifier.type != Token.KEYWORD) {
                    logMessage(new CompilerException("expecting identifier", identifier));
                    break;
                }
                compileStruct(identifier, sourceLine);
                if (sourceLine.hasMoreTokens()) {
                    token = sourceLine.skipCommentsAndGetNextToken();
                    if (!",".equals(token.getText())) {
                        logMessage(new CompilerException("expecting ',' or end of line", token));
                    }
                }
            }
            else {
                if (token.type != Token.KEYWORD) {
                    logMessage(new CompilerException("expecting identifier", token));
                    break;
                }
                Token identifier = token;

                if (!sourceLine.hasMoreTokens()) {
                    try {
                        scope.addSymbol(identifier.getText(), enumValue);
                        publicSymbols.put(identifier.getText(), enumValue);
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
                else if ("(".equals(sourceLine.skipCommentsAndPeekNextToken().getText())) {
                    compileStruct(identifier, sourceLine);
                    if (sourceLine.hasMoreTokens()) {
                        token = sourceLine.skipCommentsAndGetNextToken();
                        if (!",".equals(token.getText())) {
                            logMessage(new CompilerException("expecting ',' or end of line", token));
                        }
                    }
                }
                else {
                    token = sourceLine.skipCommentsAndGetNextToken();
                    if ("[".equals(token.getText())) {
                        try {
                            scope.addSymbol(identifier.getText(), enumValue);
                            publicSymbols.put(identifier.getText(), enumValue);
                        } catch (CompilerException e) {
                            logMessage(e);
                        } catch (Exception e) {
                            logMessage(new CompilerException(e, token));
                        }

                        Spin2ExpressionBuilder builder = new Spin2ExpressionBuilder(scope, true);
                        try {
                            while (sourceLine.hasMoreTokens()) {
                                token = sourceLine.skipCommentsAndGetNextToken();
                                if ("]".equals(token.getText())) {
                                    break;
                                }
                                builder.addToken(token);
                            }
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

                        if (sourceLine.hasMoreTokens()) {
                            token = sourceLine.skipCommentsAndGetNextToken();
                            if (!",".equals(token.getText())) {
                                logMessage(new CompilerException("expecting ',' or end of line", token));
                                break;
                            }
                        }
                    }
                    else if ("=".equals(token.getText())) {
                        Spin2ExpressionBuilder builder = new Spin2ExpressionBuilder(scope, true);
                        try {
                            while (sourceLine.hasMoreTokens()) {
                                token = sourceLine.skipCommentsAndGetNextToken();
                                if (",".equals(token.getText())) {
                                    break;
                                }
                                builder.addToken(token);
                            }
                            Expression expression = builder.getExpression();
                            try {
                                scope.addSymbol(identifier.getText(), expression);
                                publicSymbols.put(identifier.getText(), expression);
                            } catch (CompilerException e) {
                                logMessage(e);
                            } catch (Exception e) {
                                logMessage(new CompilerException(e, builder.getTokens()));
                            }
                        } catch (CompilerException e) {
                            logMessage(e);
                        } catch (Exception e) {
                            if (builder.getTokens().isEmpty()) {
                                logMessage(new CompilerException("expecting expression", token));
                            }
                            else {
                                logMessage(new CompilerException("expression syntax error", builder.getTokens()));
                            }
                        }
                        if (!",".equals(token.getText()) && sourceLine.hasMoreTokens()) {
                            token = sourceLine.skipCommentsAndGetNextToken();
                            if (!",".equals(token.getText())) {
                                logMessage(new CompilerException("expecting ',' or end of line", token));
                                break;
                            }
                        }
                    }
                    else if (",".equals(token.getText())) {
                        try {
                            scope.addSymbol(identifier.getText(), enumValue);
                            publicSymbols.put(identifier.getText(), enumValue);
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
                        logMessage(new CompilerException("expecting ',' or end of line", token));
                        break;
                    }
                }
            }
        } while (sourceLine.hasMoreTokens());
    }

    void compileStruct(Token identifier, SourceLine sourceLine) {
        if (!sourceLine.hasMoreTokens()) {
            logMessage(new CompilerException("expecting '(' or '='", identifier.stop + 1));
            return;
        }

        Token token = sourceLine.skipCommentsAndGetNextToken();
        if ("(".equals(token.getText())) {
            if (!sourceLine.hasMoreTokens()) {
                logMessage(new CompilerException("expecting identifier", token.stop + 1));
                return;
            }

            Spin2Struct struct = new Spin2Struct(scope);
            while (sourceLine.hasMoreTokens()) {
                token = sourceLine.skipCommentsAndGetNextToken();
                if (token.type != Token.KEYWORD) {
                    logMessage(new CompilerException("expecting identifier", token));
                    break;
                }

                Token type = null;
                if (sourceLine.hasMoreTokens() && sourceLine.skipCommentsAndPeekNextToken().type == Token.KEYWORD) {
                    type = token;
                    token = sourceLine.skipCommentsAndGetNextToken();
                }
                Token memberIdentifier = token;

                Expression memberSize = new NumberLiteral(1);
                if (sourceLine.hasMoreTokens()) {
                    token = sourceLine.skipCommentsAndGetNextToken();
                    if ("[".equals(token.getText())) {
                        if (!sourceLine.hasMoreTokens()) {
                            throw new CompilerException("expecting expression", token);
                        }
                        Spin2ExpressionBuilder builder = new Spin2ExpressionBuilder(scope);
                        while (sourceLine.hasMoreTokens()) {
                            token = sourceLine.skipCommentsAndGetNextToken();
                            if ("]".equals(token.getText())) {
                                try {
                                    if (builder.getTokenCount() != 0) {
                                        memberSize = builder.getExpression();
                                    }
                                    else {
                                        logMessage(new CompilerException("expecting expression", token));
                                    }
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
                        if (sourceLine.hasMoreTokens()) {
                            token = sourceLine.skipCommentsAndGetNextToken();
                        }
                    }
                }

                struct.addMember(type, memberIdentifier, memberSize);

                if (")".equals(token.getText())) {
                    break;
                }
                if (!",".equals(token.getText())) {
                    logMessage(new CompilerException("expecting ',' or ')'", token.stop + 1));
                    break;
                }
            }
            if (scope.hasStructureDefinition(identifier.getText())) {
                logMessage(new CompilerException("structure " + identifier.getText() + " already defined", identifier));
            }
            else {
                objectStructures.add(struct);
                scope.addStructureDefinition(identifier.getText(), struct);
            }
        }
        else if ("=".equals(token.getText())) {
            if (!sourceLine.hasMoreTokens()) {
                logMessage(new CompilerException("expecting structure name", token));
            }
            while (sourceLine.hasMoreTokens()) {
                token = sourceLine.skipCommentsAndGetNextToken();
                if (scope.hasSymbol(identifier.getText()) || scope.hasStructureDefinition(identifier.getText())) {
                    logMessage(new CompilerException("structure " + identifier.getText() + " already defined", identifier));
                }
                else {
                    Identifier struct = new Identifier(token.getText(), scope);
                    struct.setData(token);
                    publicSymbols.put(identifier.getText(), struct);
                }
                if (!sourceLine.hasMoreTokens()) {
                    break;
                }
                if (",".equals(sourceLine.skipCommentsAndPeekNextToken().getText())) {
                    break;
                }
            }
        }
        else {
            logMessage(new CompilerException("expecting '(' or '='", token));
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

                if ("alignl".equalsIgnoreCase(token.getText())) {
                    objectVarSize = (objectVarSize + 3) & ~3;
                    if (iter.hasNext()) {
                        logMessage(new CompilerException("expecting end of line", iter.next()));
                    }
                    return;
                }
                if ("alignw".equalsIgnoreCase(token.getText())) {
                    objectVarSize = (objectVarSize + 1) & ~1;
                    if (iter.hasNext()) {
                        logMessage(new CompilerException("expecting end of line", iter.next()));
                    }
                    return;
                }

                do {
                    int varSize = 1;

                    boolean valid = Spin2Model.isType(token.getText());
                    if (!valid) {
                        valid = scope.hasStructureDefinition(token.getText());
                    }
                    if (!valid) {
                        if (token.getText().startsWith("^")) {
                            valid = Spin2Model.isType(token.getText().substring(1));
                            if (!valid) {
                                valid = scope.hasStructureDefinition(token.getText().substring(1));
                            }
                        }
                    }
                    if (valid) {
                        type = token;
                        if (!iter.hasNext()) {
                            logMessage(new CompilerException("expecting identifier", token.stop + 1));
                            return;
                        }
                        token = iter.next();
                    }

                    String typeText = type != null ? type.getText() : "LONG";

                    if (token.type != Token.KEYWORD) {
                        logMessage(new CompilerException("expecting identifier", token));
                        return;
                    }
                    Token identifier = token;

                    if (iter.hasNext()) {
                        token = iter.peekNext();

                        if ("[".equals(token.getText())) {
                            token = iter.next();
                            if (type != null && type.getText().startsWith("^")) {
                                logMessage(new CompilerException("syntax error", token));
                            }
                            if (!iter.hasNext()) {
                                throw new CompilerException("expecting expression", token.stop + 1);
                            }
                            Spin2ExpressionBuilder builder = new Spin2ExpressionBuilder(scope);
                            while (iter.hasNext()) {
                                token = iter.next();
                                if ("]".equals(token.getText())) {
                                    try {
                                        if (builder.getTokenCount() != 0) {
                                            Expression expression = builder.getExpression();
                                            if (!expression.isConstant()) {
                                                throw new CompilerException("not a constant expression", expression.getData());
                                            }
                                            varSize = expression.getNumber().intValue();
                                        }
                                        else {
                                            logMessage(new CompilerException("expecting expression", token));
                                        }
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
                        Variable var = new Variable(typeText, identifier.getText(), varSize, objectVarSize);
                        if (type != null) {
                            Spin2Struct memberStruct = scope.getStructureDefinition(type.getText());
                            if (memberStruct != null) {
                                compileStructureVariable(var, memberStruct);
                            }
                        }
                        var.setData(identifier);

                        scope.addSymbol(identifier.getText(), var);
                        variables.add(var);

                        objectVarSize += var.getTypeSize() * var.getSize();
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
                        throw new CompilerException("expecting identifier", token.stop + 1);
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

    void compileStructureVariable(Variable target, Spin2Struct struct) {
        for (Spin2StructMember member : struct.getMembers()) {
            String memberType = "LONG";
            Spin2Struct memberStruct = null;

            if (member.getType() != null) {
                memberType = member.getType().getText();

                boolean valid = Spin2Model.isType(memberType);
                if (!valid && memberType.startsWith("^")) {
                    valid = Spin2Model.isType(memberType.substring(1));
                }
                if (!valid) {
                    valid = (memberStruct = member.getStructureDefinition()) != null;
                }
                if (!valid) {
                    logMessage(new CompilerException("expecting type", member.getType()));
                    break;
                }
            }

            int memberSize = 1;
            if (member.getSize() != null) {
                try {
                    if (!member.getSize().isConstant()) {
                        throw new RuntimeException("not a constant expression");
                    }
                    memberSize = member.getSize().getNumber().intValue();
                } catch (CompilerException e) {
                    logMessage(e);
                }
            }

            Variable var = target.addMember(memberType, member.getIdentifier().getText(), memberSize);
            if (memberStruct != null) {
                compileStructureVariable(var, memberStruct);
            }
        }
    }

    void compileObject(SourceLine sourceLine) {
        Token token;

        Token nameToken = sourceLine.skipCommentsAndGetNextToken();
        if (nameToken.type != Token.KEYWORD) {
            logMessage(new CompilerException("expecting identifier", nameToken));
            return;
        }
        if (objects.containsKey(nameToken.getText())) {
            logMessage(new CompilerException("expecting unique object name", nameToken));
            return;
        }

        String name = nameToken.getText();
        Expression count = new NumberLiteral(1);

        if (!sourceLine.hasMoreTokens()) {
            logMessage(new CompilerException("expecting ':' or '['", nameToken.stop + 1));
            return;
        }
        token = sourceLine.skipCommentsAndGetNextToken();

        if ("[".equals(token.getText())) {
            if (!sourceLine.hasMoreTokens()) {
                logMessage(new CompilerException("expecting expression", token.stop + 1));
                return;
            }
            Spin2ExpressionBuilder builder = new Spin2ExpressionBuilder(scope);
            try {
                while (sourceLine.hasMoreTokens()) {
                    token = sourceLine.skipCommentsAndGetNextToken();
                    if ("]".equals(token.getText())) {
                        break;
                    }
                    builder.addToken(token);
                }
                if (!"]".equals(token.getText())) {
                    logMessage(new CompilerException("expecting ']'", token));
                    return;
                }
                if (builder.getTokenCount() == 0) {
                    logMessage(new CompilerException("expecting expression", sourceLine.getLastToken()));
                    return;
                }
                count = builder.getExpression();
                count.setData(builder.getTokens());
            } catch (CompilerException e) {
                logMessage(e);
            } catch (Exception e) {
                logMessage(new CompilerException(e, builder.getTokens()));
            }
            if (!sourceLine.hasMoreTokens()) {
                logMessage(new CompilerException("expecting ':'", token.stop + 1));
                return;
            }
            token = sourceLine.skipCommentsAndGetNextToken();
        }

        if (!":".equals(token.getText())) {
            logMessage(new CompilerException("expecting ':'", token));
            return;
        }
        if (!sourceLine.hasMoreTokens()) {
            logMessage(new CompilerException("expecting object file name", token.stop + 1));
            return;
        }
        Token fileToken = sourceLine.skipCommentsAndGetNextToken();
        if (fileToken.type != Token.STRING) {
            logMessage(new CompilerException("expecting object file name", fileToken));
            return;
        }

        Map<String, Expression> parameters = compiler.isCaseSensitive() ? new HashMap<>() : new CaseInsensitiveMap<>();

        if (sourceLine.hasMoreTokens()) {
            token = sourceLine.skipCommentsAndGetNextToken();
            if (!"|".equals(token.getText())) {
                logMessage(new CompilerException("expecting '|' or end of line", token));
                return;
            }
            if (!sourceLine.hasMoreTokens()) {
                logMessage(new CompilerException("expecting parameter name", token.stop + 1));
            }
            while (sourceLine.hasMoreTokens()) {
                token = sourceLine.skipCommentsAndGetNextToken();
                if (token.type != Token.KEYWORD) {
                    logMessage(new CompilerException("expecting parameter name", token));
                    break;
                }

                String identifier = token.getText();
                if (!sourceLine.hasMoreTokens()) {
                    logMessage(new CompilerException("expecting '='", token.stop + 1));
                    break;
                }
                token = sourceLine.skipCommentsAndGetNextToken();
                if (!"=".equals(token.getText())) {
                    logMessage(new CompilerException("expecting '='", token));
                    break;
                }

                Spin2ExpressionBuilder builder = new Spin2ExpressionBuilder(scope);
                try {
                    while (sourceLine.hasMoreTokens()) {
                        if (",".equals(sourceLine.skipCommentsAndPeekNextToken().getText())) {
                            break;
                        }
                        builder.addToken(sourceLine.skipCommentsAndGetNextToken());
                    }
                    if (builder.getTokenCount() == 0) {
                        logMessage(new CompilerException("expecting expression", token.stop + 1));
                        return;
                    }
                    Expression expression = builder.getExpression();
                    parameters.put(identifier, expression);
                } catch (CompilerException e) {
                    logMessage(e);
                } catch (Exception e) {
                    logMessage(new CompilerException(e, builder.getTokens()));
                }

                if (sourceLine.hasMoreTokens()) {
                    token = sourceLine.skipCommentsAndGetNextToken();
                    if (",".equals(token.getText())) {
                        if (!sourceLine.hasMoreTokens()) {
                            logMessage(new CompilerException("expecting parameter name", token.stop + 1));
                        }
                    }
                    else {
                        logMessage(new CompilerException("expecting ',' or end of line", token));
                    }
                }
            }
        }

        if (sourceLine.hasMoreTokens()) {
            logMessage(new CompilerException("expecting end of line", sourceLine.skipCommentsAndGetNextToken()));
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
            logMessage(new CompilerException("object '" + fileName + "' not found", fileToken));
            return;
        }

        try {
            ObjectInfo info = compiler.getObjectInfo(this, file, parameters);
            if (info == null) {
                logMessage(new CompilerException("object '" + fileName + "' not found", fileToken));
                return;
            }
            objects.put(name, new ObjectInfo(info, count));

            for (Entry<String, Expression> entry : info.compiler.getPublicSymbols().entrySet()) {
                if (!(entry.getValue() instanceof Method)) {
                    String qualifiedName = name + "." + entry.getKey();
                    scope.addSymbol(qualifiedName, entry.getValue());
                }
            }
            for (Entry<String, Spin2Struct> entry : info.compiler.getScope().getStructures().entrySet()) {
                try {
                    scope.addStructureDefinition(nameToken.getText() + "." + entry.getKey(), entry.getValue());
                } catch (Exception e) {
                    logMessage(new CompilerException(e.getMessage(), nameToken));
                }
            }

            if (info.hasErrors()) {
                for (CompilerException msg : info.compiler.getMessages()) {
                    if (file.equals(msg.getFile()) && "illegal circular reference".equals(msg.getMessage())) {
                        logMessage(new CompilerException(msg.getMessage(), fileToken));
                        return;
                    }
                }
                logMessage(new CompilerException("object '" + fileName + "' has errors", fileToken));
            }
        } catch (CompilerException e) {
            logMessage(e);
        } catch (Exception e) {
            logMessage(new CompilerException(e, fileToken));
        }
    }

    Spin2Method compileMethod(MethodNode node) {
        Context localScope = new Context(scope);

        localScope.addBuiltinSymbol("RECV", new Register(0x1D1));
        localScope.addBuiltinSymbol("SEND", new Register(0x1D2));

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

        Spin2Method method = new Spin2Method(localScope, token.getText());
        method.setComment(node.getText());
        method.setData(node);

        if (!iter.hasNext()) {
            logMessage(new CompilerException("expecting '(' after '" + token.getText() + "'", token));
            return method;
        }

        token = iter.next();
        if (!"(".equals(token.getText())) {
            logMessage(new CompilerException("expecting '('", token));
            return method;
        }
        if (!iter.hasNext()) {
            logMessage(new CompilerException("expecting identifier or ')' after '" + token.getText() + "'", token));
            return method;
        }
        while (iter.hasNext()) {
            token = iter.next();
            if (")".equals(token.getText())) {
                break;
            }

            Token type = null;
            Token identifier = token;

            if (iter.hasNext()) {
                token = iter.peekNext();
                if (token.type == Token.KEYWORD) {
                    type = identifier;
                    identifier = iter.next();
                }
            }

            if (type != null) {
                boolean valid = "LONG".equalsIgnoreCase(type.getText());
                if (!valid) {
                    valid = scope.hasStructureDefinition(type.getText());
                }
                if (!valid) {
                    if (type.getText().startsWith("^")) {
                        valid = Spin2Model.isType(type.getText().substring(1));
                        if (!valid) {
                            valid = scope.hasStructureDefinition(type.getText().substring(1));
                        }
                    }
                }
                if (!valid) {
                    logMessage(new CompilerException("undefined type '" + type.getText() + "'", type));
                }
            }

            if (identifier.type != Token.KEYWORD) {
                logMessage(new CompilerException("invalid identifier '" + identifier.getText() + "'", identifier));
            }

            Expression value = null;
            if (iter.hasNext() && "=".equals(iter.peekNext().getText())) {
                token = iter.next();
                if (!iter.hasNext()) {
                    logMessage(new CompilerException("expecting expression after '='", token));
                }
                else {
                    Spin2ExpressionBuilder builder = new Spin2ExpressionBuilder(scope);
                    while (iter.hasNext()) {
                        Token next = iter.peekNext();
                        if (",".equals(next.getText()) || ")".equals(next.getText())) {
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
            }
            if (value == null && method.getParametersCount() != 0) {
                if (method.getParameter(method.getParametersCount() - 1).getValue() != null) {
                    logMessage(new CompilerException("expecting default value for parameter '" + identifier.getText() + "'", identifier));
                }
            }

            Expression expression = localScope.getLocalSymbol(identifier.getText());
            if (expression instanceof LocalVariable) {
                logMessage(new CompilerException("symbol '" + identifier + "' already defined", identifier));
            }
            else {
                if (expression != null) {
                    logMessage(new CompilerException(CompilerException.WARNING, "parameter '" + identifier + "' hides global variable", identifier));
                }

                if (method.getParameterLongs() <= 127) {
                    String typeText = type != null ? type.getText() : "LONG";
                    LocalVariable var = new LocalVariable(typeText, identifier.getText(), value, 1, method.getVarOffset());

                    Spin2Struct struct = scope.getStructureDefinition(typeText);
                    if (struct != null) {
                        compileStructureVariable(var, struct);
                    }
                    var.setData(identifier);

                    method.addParameter(var);

                    if (method.getParameterLongs() > 127) {
                        logMessage(new CompilerException("limit of 127 parameters exceeded", identifier));
                        return method;
                    }
                }
            }

            if (iter.hasNext()) {
                token = iter.next();
                if (")".equals(token.getText())) {
                    break;
                }
                if (",".equals(token.getText())) {
                    if (!iter.hasNext()) {
                        logMessage(new CompilerException("expecting identifier after ','", token));
                        return method;
                    }
                }
                else {
                    logMessage(new CompilerException("expecting ',' or ')'", token));
                    return method;
                }
            }
            else {
                logMessage(new CompilerException("expecting ',' or ')' after '" + token + "'", token));
                return method;
            }
        }

        if (iter.hasNext()) {
            token = iter.peekNext();
            if (!":".equals(token.getText()) && !"|".equals(token.getText())) {
                logMessage(new CompilerException("expecting ':', '|' or end of line", token));
                return method;
            }
        }

        if (iter.hasNext() && ":".equals(iter.peekNext().getText())) {
            token = iter.next();
            if (!iter.hasNext()) {
                logMessage(new CompilerException("expecting return variable name after '" + iter.current() + "'", iter.current()));
            }
            while (iter.hasNext()) {
                Token type = null;
                Token identifier = iter.next();
                if (iter.hasNext()) {
                    Token next = iter.peekNext();
                    if (next.type == Token.KEYWORD) {
                        type = identifier;
                        identifier = iter.next();
                    }
                }

                if (type != null) {
                    boolean valid = scope.hasStructureDefinition(type.getText());
                    if (!valid) {
                        if (type.getText().startsWith("^")) {
                            valid = Spin2Model.isType(type.getText().substring(1));
                            if (!valid) {
                                valid = scope.hasStructureDefinition(type.getText().substring(1));
                            }
                        }
                    }
                    if (!valid) {
                        logMessage(new CompilerException("invalid type", type));
                    }
                }

                if (identifier.type == Token.KEYWORD) {
                    Expression expression = localScope.getLocalSymbol(identifier.getText());
                    if (expression instanceof LocalVariable) {
                        logMessage(new CompilerException("symbol '" + identifier.getText() + "' already defined", identifier));
                    }
                    else {
                        if (expression != null) {
                            logMessage(new CompilerException(CompilerException.WARNING, "return variable '" + identifier.getText() + "' hides global variable", identifier));
                        }

                        if (method.getReturnLongs() <= 15) {
                            String typeText = type != null ? type.getText() : "LONG";
                            LocalVariable var = new LocalVariable(typeText, identifier.getText(), 1, method.getVarOffset());

                            Spin2Struct struct = scope.getStructureDefinition(typeText);
                            if (struct != null) {
                                compileStructureVariable(var, struct);
                            }
                            var.setData(identifier);

                            method.addReturnVariable(var);

                            if (method.getReturnLongs() > 15) {
                                logMessage(new CompilerException("limit of 15 results exceeded", identifier));
                            }
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
                Token type = null;
                Token identifier = iter.next();

                if ("alignl".equalsIgnoreCase(identifier.getText())) {
                    method.alignLong();
                    if (!iter.hasNext()) {
                        logMessage(new CompilerException("expecting local variable name after '" + identifier.getText() + "'", identifier));
                    }
                    identifier = iter.next();
                }
                else if ("alignw".equalsIgnoreCase(identifier.getText())) {
                    method.alignWord();
                    if (!iter.hasNext()) {
                        logMessage(new CompilerException("expecting local variable name after '" + identifier.getText() + "'", identifier));
                    }
                    identifier = iter.next();
                }

                if (iter.hasNext()) {
                    Token next = iter.peekNext();
                    if (next.type == Token.KEYWORD) {
                        type = identifier;
                        identifier = iter.next();
                    }
                }

                if (type != null) {
                    boolean valid = Spin2Model.isType(type.getText());
                    if (!valid) {
                        valid = scope.hasStructureDefinition(type.getText());
                    }
                    if (!valid) {
                        if (type.getText().startsWith("^")) {
                            valid = Spin2Model.isType(type.getText().substring(1));
                            if (!valid) {
                                valid = scope.hasStructureDefinition(type.getText().substring(1));
                            }
                        }
                    }
                    if (!valid) {
                        logMessage(new CompilerException("invalid type", type));
                    }
                }

                if (identifier.type == Token.KEYWORD) {
                    int size = 1;

                    if (iter.hasNext() && "[".equals(iter.peekNext().getText())) {
                        Token start = iter.next();
                        if (!iter.hasNext()) {
                            logMessage(new CompilerException("expecting expression after '['", start));
                        }
                        else {
                            Spin2ExpressionBuilder builder = new Spin2ExpressionBuilder(scope);
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

                        if (method.getLocalVariableLongs() <= 16383) {
                            String typeText = type != null ? type.getText() : "LONG";
                            LocalVariable var = new LocalVariable(typeText, identifier.getText(), size, method.getVarOffset());

                            Spin2Struct struct = scope.getStructureDefinition(typeText);
                            if (struct != null) {
                                compileStructureVariable(var, struct);
                            }
                            var.setData(identifier);

                            method.addLocalVariable(var);

                            if (method.getLocalVariableLongs() > 16383) {
                                logMessage(new CompilerException("limit of 64Kb of local variables exceeded", identifier));
                                return method;
                            }
                        }
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
                        return method;
                    }
                }
            }
        }

        if (iter.hasNext()) {
            logMessage(new CompilerException("expecting end of line", iter.next()));
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

                    Context lineScope = line.getScope();

                    int address = 0x1E0;
                    for (LocalVariable var : method.getAllLocalVariables()) {
                        lineScope.addSymbol(var.getName(), new NumberLiteral(address, 16) {

                            @Override
                            public Number getNumber() {
                                if ((var.getOffset() >> 2) >= 0x10) {
                                    throw new CompilerException("local variable must be within the first 16 longs", var.getData());
                                }
                                if ("BYTE".equalsIgnoreCase(var.getType()) || "WORD".equalsIgnoreCase(var.getType())) {
                                    throw new CompilerException("local variable must be long or structure", var.getData());
                                }
                                if ((var.getOffset() & 3) != 0) {
                                    throw new CompilerException("local variable must be long-aligned", var.getData());
                                }
                                return super.getNumber();
                            }

                        });
                        address += (var.getTypeSize() * var.getSize() + 3) >> 2;
                        var.setCalledBy(method);
                    }

                    Spin2PAsmLine pasmLine = compileDataLine(scope, lineScope, (DataLineNode) node);
                    line.addSource(new InlinePAsm(line.getScope(), pasmLine));

                    compileInlinePAsmStatements(nodeIterator, line);

                    line.resolve(0, compiler.isDebugEnabled());
                    line.writeTo(new Spin2Object(), compiler.isDebugEnabled());
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

    Spin2MethodLine compileStatement(Context context, Spin2Method method, Spin2MethodLine parent, Node node, Spin2MethodLine previousLine) {
        Spin2MethodLine line = null;

        try {
            TokenIterator iter = node.tokenIterator();
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
                    line.addSource(new Bytecode(line.getScope(), Spin2Bytecode.bc_abort_arg, text.toUpperCase()));
                }
                else {
                    line.addSource(new Bytecode(line.getScope(), Spin2Bytecode.bc_abort_0, text.toUpperCase()));
                }
            }
            else if ("IF".equals(text) || "IFNOT".equals(text)) {
                line = new Spin2MethodLine(context, parent, token.getText(), node);

                Spin2TreeBuilder builder = new Spin2TreeBuilder(context);
                while (iter.hasNext()) {
                    builder.addToken(iter.next());
                }
                Spin2StatementNode statementNode = builder.getRoot();
                line.addSource(compileConstantExpression(context, method, statementNode));

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
                    Spin2StatementNode statementNode = builder.getRoot();
                    line.addSource(compileConstantExpression(context, method, statementNode));
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
                    Spin2StatementNode statementNode = builder.getRoot();
                    line.addSource(compileConstantExpression(line.getScope(), method, statementNode));
                    line.addSource(new Bytecode(line.getScope(), Spin2Bytecode.bc_return_args, text));
                    if (statementNode.getReturnLongs() < method.getReturnLongs()) {
                        logMessage(new CompilerException("not enough return values", statementNode.getTokens()));
                    }
                    else if (statementNode.getReturnLongs() > method.getReturnLongs()) {
                        logMessage(new CompilerException("too many return values", statementNode.getTokens()));
                    }
                    for (LocalVariable var : method.getReturns()) {
                        var.setCalledBy(method);
                    }
                }
                else {
                    line.addSource(new Bytecode(line.getScope(), Spin2Bytecode.bc_return_results, text));
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
                        Spin2StatementNode statementNode = builder.getRoot();
                        line.addSource(compileConstantExpression(line.getScope(), method, statementNode));

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
                        Spin2StatementNode with = null;

                        Spin2TreeBuilder builder = new Spin2TreeBuilder(context);
                        builder.addToken(token);
                        while (iter.hasNext()) {
                            token = iter.next();
                            if ("FROM".equalsIgnoreCase(token.getText()) || "WITH".equalsIgnoreCase(token.getText())) {
                                break;
                            }
                            builder.addToken(token);
                        }
                        counter = builder.getRoot();

                        if ("FROM".equalsIgnoreCase(token.getText())) {
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
                        else if ("WITH".equalsIgnoreCase(token.getText())) {
                            builder = new Spin2TreeBuilder(context);
                            while (iter.hasNext()) {
                                builder.addToken(iter.next());
                            }
                            with = builder.getRoot();
                            if (with.getChildCount() != 0) {
                                throw new CompilerException("syntax error", builder.getTokens());
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

                            try {
                                line.addSource(leftAssign(context, method, counter, true, false));
                            } catch (CompilerException e) {
                                logMessage(e);
                            } catch (Exception e) {
                                logMessage(new CompilerException(e, counter.getTokens()));
                            }
                            line.addSource(new Bytecode(line.getScope(), step != null ? Spin2Bytecode.bc_repeat_var_init : Spin2Bytecode.bc_repeat_var_init_1, "REPEAT"));

                            Spin2MethodLine nextLine = new Spin2MethodLine(context);
                            line.setData("next", nextLine);

                            line.addChild(loopLine);
                            line.addChilds(compileStatement(new Context(context), method, line, node));

                            try {
                                nextLine.addSource(leftAssign(context, method, counter, true, false));
                            } catch (CompilerException e) {
                                logMessage(e);
                            } catch (Exception e) {
                                logMessage(new CompilerException(e, counter.getTokens()));
                            }
                            nextLine.addSource(new Bytecode(line.getScope(), Spin2Bytecode.bc_repeat_var_loop, "REPEAT_LOOP"));
                            line.addChild(nextLine);
                        }
                        else if (with != null) {
                            line.setData("pop", Integer.valueOf(16));

                            Spin2MethodLine loopLine = new Spin2MethodLine(context);

                            line.addSource(new Address(line.getScope(), new ContextLiteral(loopLine.getScope())));

                            line.addSource(compileConstantExpression(line.getScope(), method, counter));
                            try {
                                line.addSource(leftAssign(context, method, with, true, false));
                            } catch (CompilerException e) {
                                logMessage(e);
                            } catch (Exception e) {
                                logMessage(new CompilerException(e, counter.getTokens()));
                            }
                            line.addSource(new Bytecode(line.getScope(), Spin2Bytecode.bc_repeat_var_init_n, "REPEAT"));

                            Spin2MethodLine nextLine = new Spin2MethodLine(context);
                            line.setData("next", nextLine);

                            line.addChild(loopLine);
                            line.addChilds(compileStatement(new Context(context), method, line, node));

                            try {
                                nextLine.addSource(leftAssign(context, method, with, true, false));
                            } catch (CompilerException e) {
                                logMessage(e);
                            } catch (Exception e) {
                                logMessage(new CompilerException(e, counter.getTokens()));
                            }
                            nextLine.addSource(new Bytecode(line.getScope(), Spin2Bytecode.bc_repeat_var_loop, "REPEAT_LOOP"));
                            line.addChild(nextLine);
                        }
                        else {
                            boolean skip = false;
                            try {
                                Expression expression = buildConstantExpression(line.getScope(), counter);
                                if (expression.getNumber().longValue() == 0) {
                                    skip = true;
                                }
                                else {
                                    line.addSource(new Constant(line.getScope(), expression));
                                }
                            } catch (Exception e) {
                                line.addSource(compileBytecodeExpression(line.getScope(), method, counter, true));
                                line.addSource(new Tjz(line.getScope(), new ContextLiteral(quitLine.getScope())));
                            }
                            if (skip) {
                                compileStatement(new Context(context), method, line, node);
                            }
                            else {
                                line.setData("pop", Integer.valueOf(0));

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
                Spin2StatementNode statementNode = builder.getRoot();
                conditionLine.addSource(compileConstantExpression(line.getScope(), method, statementNode));

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
            else if ("QUIT".equals(text) || "NEXT".equals(text)) {
                int level = 0;
                int pop = 0;
                String key = text.toLowerCase();

                line = new Spin2MethodLine(context, parent, text, node);

                if (iter.hasNext()) {
                    Spin2TreeBuilder builder = new Spin2TreeBuilder(context);
                    while (iter.hasNext()) {
                        builder.addToken(iter.next());
                    }
                    try {
                        Expression expression = buildConstantExpression(line.getScope(), builder.getRoot());
                        level = Math.abs(expression.getNumber().intValue());
                        if (level < 1 || level > 15) {
                            logMessage(new CompilerException("next/quit level count must be from 1 to 15", builder.getTokens()));
                        }
                    } catch (Exception e) {
                        logMessage(new CompilerException("expecting constant expression", builder.getTokens()));
                    }
                }

                while (parent != null) {
                    if (parent.getStatement().startsWith("REPEAT")) {
                        if (level == 0) {
                            break;
                        }
                        level--;
                    }
                    if (parent.getData("pop") != null) {
                        pop += (Integer) parent.getData("pop");
                    }
                    parent = parent.getParent();
                }
                if (parent == null) {
                    throw new CompilerException("misplaced " + text, node);
                }

                if ("QUIT".equals(text) && parent.getData("pop") != null) {
                    pop += (Integer) parent.getData("pop");
                }

                if (pop != 0) {
                    try {
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        if (pop == 4) {
                            os.write(Spin2Bytecode.bc_pop);
                        }
                        else {
                            os.write(Spin2Bytecode.bc_pop_rfvar);
                            os.write(Constant.wrVars(pop - 4));
                        }
                        line.addSource(new Bytecode(line.getScope(), os.toByteArray(), String.format("POP %d", pop)));
                    } catch (Exception e) {
                        // Do nothing
                    }
                }

                Spin2MethodLine nextLine = (Spin2MethodLine) parent.getData(key);
                if ("NEXT".equals(text)) {
                    line.addSource(new Jmp(line.getScope(), new ContextLiteral(nextLine.getScope())));
                }
                else {
                    if (parent.getData("pop") != null && (Integer) parent.getData("pop") == 0) {
                        line.addSource(new Jnz(line.getScope(), new ContextLiteral(nextLine.getScope())));
                    }
                    else {
                        line.addSource(new Jmp(line.getScope(), new ContextLiteral(nextLine.getScope())));
                    }
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
                Spin2StatementNode statementNode = builder.getRoot();
                line.addSource(compileBytecodeExpression(context, method, statementNode, true));

                boolean hasOther = false;
                for (Node child : node.getChilds()) {
                    if (child.isExclude()) {
                        continue;
                    }
                    if (child instanceof StatementNode) {
                        Spin2MethodLine caseLine = new Spin2MethodLine(context);
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
                            builder = new Spin2TreeBuilder(context);
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

                        Spin2MethodLine doneLine = new Spin2MethodLine(context);
                        doneLine.addSource(new Bytecode(line.getScope(), Spin2Bytecode.bc_case_done, "CASE_DONE"));
                        caseLine.addChild(doneLine);
                    }
                }

                if (!hasOther) {
                    line.addSource(new Bytecode(line.getScope(), Spin2Bytecode.bc_case_done, "CASE_DONE"));
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
                Spin2StatementNode statementNode = builder.getRoot();
                line.addSource(compileBytecodeExpression(context, method, statementNode, true));
                line.addSource(new Bytecode(line.getScope(), Spin2Bytecode.bc_case_fast_init, "CASE_FAST"));

                int min = Integer.MAX_VALUE;
                int max = Integer.MIN_VALUE;
                Map<Integer, Spin2MethodLine> map = new TreeMap<Integer, Spin2MethodLine>();

                Spin2MethodLine doneLine = null;
                Spin2MethodLine otherLine = null;

                for (Node child : node.getChilds()) {
                    if (child.isExclude()) {
                        continue;
                    }
                    if (child instanceof StatementNode) {
                        Spin2MethodLine caseLine = new Spin2MethodLine(context);
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
                            if (!":".equals(token.getText())) {
                                logMessage(new CompilerException("expecting ':'", token));
                            }
                            statementNode = builder.getRoot();
                            for (Entry<Integer, Spin2MethodLine> entry : compileCaseFast(line, statementNode, caseLine).entrySet()) {
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
                        if (childIter.hasNext()) {
                            logMessage(new CompilerException("unexpected", childIter.next()));
                        }

                        line.addChild(caseLine);

                        doneLine = new Spin2MethodLine(context);
                        doneLine.addSource(new Bytecode(line.getScope(), Spin2Bytecode.bc_case_fast_done, "CASE_FAST_DONE"));
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
                Spin2TreeBuilder builder = new Spin2TreeBuilder(context);
                builder.addToken(token);
                while (iter.hasNext()) {
                    builder.addToken(iter.next());
                }
                Spin2StatementNode statementNode = builder.getRoot();
                line = new Spin2MethodLine(context, parent, null, node);
                line.addSource(compileBytecodeExpression(context, method, statementNode, false));
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
        int code = Spin2Bytecode.bc_org;
        Context rootScope = new Context(line.getScope());
        Context lineScope = rootScope;

        while (linesIterator.hasNext()) {
            Node node = linesIterator.next();
            if (node.isExclude()) {
                continue;
            }
            if (node instanceof DataLineNode lineNode) {
                if (lineNode.label != null && !lineNode.label.getText().startsWith(".")) {
                    lineScope = rootScope;
                }
                if (lineNode.instruction != null && "DITTO".equalsIgnoreCase(lineNode.instruction.getText())) {
                    DataLineNode beginLineNode = lineNode;
                    DataLineNode endLineNode = null;

                    List<DataLineNode> list = new ArrayList<>();
                    while (linesIterator.hasNext()) {
                        Node n2 = linesIterator.next();
                        if (!n2.isExclude() && (n2 instanceof DataLineNode)) {
                            lineNode = (DataLineNode) n2;
                            if ("DITTO".equalsIgnoreCase(lineNode.instruction.getText())) {
                                if (!lineNode.parameters.isEmpty() && "END".equalsIgnoreCase(lineNode.parameters.getFirst().getText())) {
                                    endLineNode = lineNode;
                                    break;
                                }
                            }
                            list.add(lineNode);
                        }
                    }
                    if (endLineNode == null) {
                        logMessage(new CompilerException("missing DITTO END line", beginLineNode));
                    }
                    List<Spin2PAsmLine> source = processDittoBlock(null, rootScope, lineScope, beginLineNode, list, endLineNode);
                    for (Spin2PAsmLine pasmLine : source) {
                        line.addSource(new InlinePAsm(rootScope, pasmLine));
                    }
                }
                else {
                    Spin2PAsmLine pasmLine = compileDataLine(rootScope, lineScope, (DataLineNode) node);
                    line.addSource(new InlinePAsm(rootScope, pasmLine));
                    if (lineNode.label != null && !lineNode.label.getText().startsWith(".")) {
                        lineScope = pasmLine.getScope();
                    }
                }
            }
            else if (node instanceof StatementNode) {
                if ("END".equalsIgnoreCase(node.getStartToken().getText())) {
                    processAliases();
                    Spin2PAsmLine pasmLine = new Spin2PAsmLine(line.getScope(), null, null, "ret", Spin2PAsmInstructionFactory.get("ret"), Collections.emptyList(), null);
                    pasmLine.setData(node);
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
                    org = 0x00;
                    if (!arguments.isEmpty()) {
                        org = arguments.get(0).getInteger();
                    }
                }
                else if (pasmLine.getInstructionFactory() instanceof Orgh) {
                    org = 0x400;
                    if (!arguments.isEmpty()) {
                        logMessage(new CompilerException("argument(s) not allowed", arguments.get(0).getExpression().getData()));
                    }
                    code = Spin2Bytecode.bc_orgh;
                }
            }
        }

        line.source.add(0, new InlinePAsmExec(line.getScope(), org, code == Spin2Bytecode.bc_orgh));
    }

    void compileCase(Spin2Method method, Spin2MethodLine line, Spin2StatementNode arg, Spin2MethodLine target) {
        if (",".equals(arg.getText())) {
            for (Spin2StatementNode child : arg.getChilds()) {
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
                int first = expression.getNumber().intValue();

                expression = buildConstantExpression(line.getScope(), arg.getChild(1));
                if (!expression.isConstant()) {
                    throw new CompilerException("expression is not constant", arg.getChild(1));
                }
                int last = expression.getNumber().intValue();

                if (last < first) {
                    for (int value = last; value <= first; value++) {
                        map.put(value, target);
                    }
                }
                else {
                    for (int value = first; value <= last; value++) {
                        map.put(value, target);
                    }
                }
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

    void computeClockMode() {
        Expression clkMode = scope.getSystemSymbol("_CLKMODE");
        Expression clkFreq = scope.getSystemSymbol("_CLKFREQ");
        Expression xtlFreq = scope.getSystemSymbol("_XTLFREQ");
        Expression xinFreq = scope.getSystemSymbol("_XINFREQ");
        Expression errFreq = scope.getSystemSymbol("_ERRFREQ");

        double clkfreq;
        double xinfreq = 20000000.0; // default crystal frequency
        double errfreq = 1000000.0;
        int clkmode = isDebugEnabled() ? 0x0000000A : 0x00000000;
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

}
