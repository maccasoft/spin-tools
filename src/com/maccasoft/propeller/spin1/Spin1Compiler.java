/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.maccasoft.propeller.CompilerMessage;
import com.maccasoft.propeller.expressions.Add;
import com.maccasoft.propeller.expressions.CharacterLiteral;
import com.maccasoft.propeller.expressions.ContextLiteral;
import com.maccasoft.propeller.expressions.Divide;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.ExpressionBuilder;
import com.maccasoft.propeller.expressions.HubContextLiteral;
import com.maccasoft.propeller.expressions.Identifier;
import com.maccasoft.propeller.expressions.LocalVariable;
import com.maccasoft.propeller.expressions.Method;
import com.maccasoft.propeller.expressions.Multiply;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.expressions.Register;
import com.maccasoft.propeller.expressions.Subtract;
import com.maccasoft.propeller.expressions.Variable;
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
import com.maccasoft.propeller.spin1.Spin1Bytecode.Descriptor;
import com.maccasoft.propeller.spin1.Spin1Object.LongDataObject;
import com.maccasoft.propeller.spin1.Spin1Object.WordDataObject;
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
import com.maccasoft.propeller.spin1.bytecode.RegisterBitOp;
import com.maccasoft.propeller.spin1.bytecode.RegisterOp;
import com.maccasoft.propeller.spin1.bytecode.RepeatLoop;
import com.maccasoft.propeller.spin1.bytecode.Tjz;
import com.maccasoft.propeller.spin1.bytecode.VariableOp;

public class Spin1Compiler {

    public static boolean OPENSPIN_COMPATIBILITY = false;

    Spin1Context scope = new Spin1GlobalContext();
    List<Spin1PAsmLine> source = new ArrayList<Spin1PAsmLine>();
    List<Spin1Method> methods = new ArrayList<Spin1Method>();
    List<Spin1Object> objects = new ArrayList<Spin1Object>();

    int varOffset = 0;
    int labelCounter;

    boolean errors;
    List<CompilerMessage> messages = new ArrayList<CompilerMessage>();

    Spin1Object object = new Spin1Object();

    public Spin1Compiler() {

    }

    public Spin1Object compile(Node root) {
        Spin1Object obj = compileObject(root);

        object.setClkFreq(obj.getClkFreq());
        object.setClkMode(obj.getClkMode());

        object.writeLong(object.getClkFreq(), "CLKFREQ");
        object.writeByte(object.getClkMode(), "CLKMODE");
        object.writeByte(0, "Checksum");

        WordDataObject pbase = object.writeWord(0, "PBASE");
        WordDataObject vbase = object.writeWord(0, "VBASE");
        WordDataObject dbase = object.writeWord(0, "DBASE");

        WordDataObject pcurr = object.writeWord(0, "PCURR");
        WordDataObject dcurr = object.writeWord(0, "DCURR");

        pbase.setValue(object.getSize());

        object.writeObject(obj);

        vbase.setValue(object.getSize());

        int offset = obj.getVarSize() + 8;
        for (Spin1Object child : objects) {
            offset += child.getVarSize();
        }
        dbase.setValue(object.getSize() + offset);

        pcurr.setValue((int) (pbase.getValue() + (((LongDataObject) obj.getObject(4)).getValue() & 0xFFFF)));

        offset = 4;
        if (methods.size() != 0) {
            offset += methods.get(0).getStackSize();
        }
        dcurr.setValue(dbase.getValue() + offset);

        return object;
    }

    public Spin1Object compileObject(Node root) {
        int address = 0, hubAddress = 0;
        Spin1Object object = new Spin1Object();

        for (Node node : root.getChilds()) {
            if (node instanceof ConstantsNode) {
                compileConBlock(node, object);
            }
        }

        for (Node node : root.getChilds()) {
            if (node instanceof VariablesNode) {
                compileVarBlock(node);
            }
        }

        object.setVarSize(varOffset);

        determineClock();

        object.setClkFreq(scope.getLocalSymbol("CLKFREQ").getNumber().intValue());
        object.setClkMode(scope.getLocalSymbol("CLKMODE").getNumber().intValue());

        for (Node node : root.getChilds()) {
            if (node instanceof ObjectsNode) {
                compileObjBlock(node);
            }
        }

        for (Node node : root.getChilds()) {
            if (node instanceof DataNode) {
                compileDatBlock(node);
            }
        }

        for (Node node : root.getChilds()) {
            if ((node instanceof MethodNode) && "PUB".equalsIgnoreCase(((MethodNode) node).getType().getText())) {
                Spin1Method method = compileMethod((MethodNode) node);
                methods.add(method);
                scope.addSymbol(method.getLabel(), new Method(method.getLabel(), method.getParametersCount(), method.getReturnsCount(), methods.size()));
                scope.addSymbol("@" + method.getLabel(), new Method(method.getLabel(), method.getParametersCount(), method.getReturnsCount(), methods.size()));
                object.addSymbol(method.getLabel(), new Method(method.getLabel(), method.getParametersCount(), method.getReturnsCount(), methods.size()));
                method.register();
            }
        }

        for (Node node : root.getChilds()) {
            if ((node instanceof MethodNode) && "PRI".equalsIgnoreCase(((MethodNode) node).getType().getText())) {
                Spin1Method method = compileMethod((MethodNode) node);
                methods.add(method);
                scope.addSymbol(method.getLabel(), new Method(method.getLabel(), method.getParametersCount(), method.getReturnsCount(), methods.size()));
                scope.addSymbol("@" + method.getLabel(), new Method(method.getLabel(), method.getParametersCount(), method.getReturnsCount(), methods.size()));
                method.register();
            }
        }

        object.writeComment("Object header");

        WordDataObject objectSize = object.writeWord(0, "Object size");
        object.writeByte(methods.size() + 1, "Method count + 1");
        object.writeByte(objects.size(), "Object count");

        LongDataObject[] ld = new LongDataObject[methods.size()];
        for (int i = 0; i < ld.length; i++) {
            ld[i] = object.writeLong(0, "Function " + methods.get(i).getLabel());
        }

        WordDataObject[] headerOffset = new WordDataObject[objects.size()];
        WordDataObject[] variablesOffset = new WordDataObject[objects.size()];
        for (int i = 0; i < headerOffset.length; i++) {
            headerOffset[i] = object.writeWord(0, "Header offset");
            variablesOffset[i] = object.writeWord(0, "Var offset");
        }

        hubAddress = object.getSize();
        for (Spin1PAsmLine line : source) {
            line.getScope().setHubAddress(hubAddress);
            try {
                address = line.resolve(address);
                if (address > 0x1F0) {
                    throw new RuntimeException("error: cog code limit exceeded by " + (address - 0x1F0) + " long(s)");
                }
                hubAddress += line.getInstructionObject().getSize();
            } catch (Exception e) {
                line.getAnnotations().add(e.getMessage());
            }
        }

        for (Spin1Method method : methods) {
            for (Spin1MethodLine line : method.getLines()) {
                try {
                    compileLine(line);
                } catch (CompilerMessage e) {
                    logMessage(e);
                    e.printStackTrace();
                } catch (Exception e) {
                    logMessage(new CompilerMessage(e.getMessage(), (Node) line.getData()));
                    e.printStackTrace();
                }
            }
        }

        for (Spin1PAsmLine line : source) {
            hubAddress = line.getScope().getHubAddress();
            if (object.getSize() < hubAddress) {
                object.writeBytes(new byte[hubAddress - object.getSize()], "(filler)");
            }
            object.writeBytes(line.getScope().getAddress(), line.getInstructionObject().getBytes(), line.toString());
        }
        object.alignToLong();

        if (methods.size() != 0) {
            for (int i = 0; i < 3; i++) {
                address = object.getSize();
                for (Spin1Method method : methods) {
                    address = method.resolve(address);
                }
            }

            int index = 0;
            for (Spin1Method method : methods) {
                ld[index].setValue((method.getStackSize() << 16) | object.getSize());
                ld[index].setText(String.format("Function %s @ $%04X (local size %d)", method.getLabel(), object.getSize(), method.getStackSize()));
                method.writeTo(object);
                index++;
            }

            object.alignToLong();
        }

        objectSize.setValue(object.getSize());

        int index = 0;
        for (Spin1Object o : objects) {
            headerOffset[index].setValue(object.getSize());
            variablesOffset[index].setValue(varOffset);
            object.writeObject(o);
            varOffset += o.getVarSize();
            index++;
        }

        return object;
    }

    public Spin1Object getObject() {
        return object;
    }

    void compileConBlock(Node parent, Spin1Object object) {

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
                        logMessage(new CompilerMessage(e.getMessage(), node.identifier));
                        e.printStackTrace();
                    }
                } catch (CompilerMessage e) {
                    logMessage(e);
                } catch (Exception e) {
                    logMessage(new CompilerMessage(e.getMessage(), node.expression));
                    e.printStackTrace();
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
                    logMessage(new CompilerMessage(e.getMessage(), node.identifier));
                    e.printStackTrace();
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

    void compileVarBlock(Node parent) {
        String type = "LONG";

        for (Node child : parent.getChilds()) {
            VariableNode node = (VariableNode) child;
            if (node.identifier == null) {
                continue;
            }

            if (node.type != null) {
                type = node.type.getText().toUpperCase();
            }

            if ("LONG".equalsIgnoreCase(type)) {
                Expression size = new NumberLiteral(1);
                if (node.size != null) {
                    size = buildExpression(node.size.getTokens(), scope);
                }

                try {
                    scope.addSymbol(node.identifier.getText(), new Variable(type, node.identifier.getText(), size, varOffset));
                    scope.addSymbol("@" + node.identifier.getText(), new Variable(type, node.identifier.getText(), size, varOffset));
                } catch (Exception e) {
                    logMessage(new CompilerMessage(e.getMessage(), node.identifier));
                    e.printStackTrace();
                    continue;
                }

                varOffset += size.getNumber().intValue() * 4;
            }
        }

        type = "LONG";
        for (Node child : parent.getChilds()) {
            VariableNode node = (VariableNode) child;
            if (node.identifier == null) {
                continue;
            }

            if (node.type != null) {
                type = node.type.getText().toUpperCase();
            }

            if ("WORD".equalsIgnoreCase(type)) {
                Expression size = new NumberLiteral(1);
                if (node.size != null) {
                    size = buildExpression(node.size.getTokens(), scope);
                }

                try {
                    scope.addSymbol(node.identifier.getText(), new Variable(type, node.identifier.getText(), size, varOffset));
                    scope.addSymbol("@" + node.identifier.getText(), new Variable(type, node.identifier.getText(), size, varOffset));
                } catch (Exception e) {
                    logMessage(new CompilerMessage(e.getMessage(), node.identifier));
                    e.printStackTrace();
                    continue;
                }

                varOffset += size.getNumber().intValue() * 2;
            }
        }

        type = "LONG";
        for (Node child : parent.getChilds()) {
            VariableNode node = (VariableNode) child;
            if (node.identifier == null) {
                continue;
            }

            if (node.type != null) {
                type = node.type.getText().toUpperCase();
            }

            if ("BYTE".equalsIgnoreCase(type)) {
                Expression size = new NumberLiteral(1);
                if (node.size != null) {
                    size = buildExpression(node.size.getTokens(), scope);
                }

                try {
                    scope.addSymbol(node.identifier.getText(), new Variable(type, node.identifier.getText(), size, varOffset));
                    scope.addSymbol("@" + node.identifier.getText(), new Variable(type, node.identifier.getText(), size, varOffset));
                } catch (Exception e) {
                    logMessage(new CompilerMessage(e.getMessage(), node.identifier));
                    e.printStackTrace();
                    continue;
                }

                varOffset += size.getNumber().intValue() * 1;
            }
        }

        while ((varOffset % 4) != 0) {
            varOffset++;
        }
    }

    void compileObjBlock(Node parent) {

        for (Node child : parent.getChilds()) {
            ObjectNode node = (ObjectNode) child;
            if (node.name != null && node.file != null) {
                String file = node.file.getText().substring(1);
                Spin1Object object = getObject(file.substring(0, file.length() - 1));
                objects.add(object);

                for (Entry<String, Expression> entry : object.getSymbols().entrySet()) {
                    if (entry.getValue() instanceof Method) {
                        String qualifiedName = node.name.getText() + "." + entry.getKey();
                        Method method = ((Method) entry.getValue()).copy();
                        method.setObject(objects.size() + 1);
                        scope.addSymbol(qualifiedName, method);
                    }
                    else {
                        String qualifiedName = node.name.getText() + "#" + entry.getKey();
                        scope.addSymbol(qualifiedName, entry.getValue());
                    }
                }
            }

        }
    }

    protected Spin1Object getObject(String fileName) {
        return null;
    }

    void compileDatBlock(Node parent) {
        Spin1Context savedContext = scope;

        for (Node child : parent.getChilds()) {
            DataLineNode node = (DataLineNode) child;

            String label = node.label != null ? node.label.getText() : null;
            String condition = node.condition != null ? node.condition.getText() : null;
            String mnemonic = node.instruction != null ? node.instruction.getText() : null;
            String modifier = node.modifier != null ? node.modifier.getText() : null;
            List<Spin1PAsmExpression> parameters = new ArrayList<Spin1PAsmExpression>();

            Spin1PAsmLine pasmLine = new Spin1PAsmLine(scope, label, condition, mnemonic, parameters, modifier);

            for (ParameterNode param : node.parameters) {
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
                    } catch (Exception e) {
                        for (Token t : param.getTokens().subList(index, param.getTokens().size())) {
                            System.err.print(" " + t.getText());
                        }
                        System.err.println();
                        throw e;
                    }
                }

                if (param.count != null) {
                    try {
                        count = buildExpression(param.count.getTokens(), pasmLine.getScope());
                    } catch (Exception e) {
                        for (Token t : param.count.getTokens()) {
                            System.err.print(" " + t.getText());
                        }
                        System.err.println();
                        throw e;
                    }
                }
                parameters.add(new Spin1PAsmExpression(prefix, expression, count));
            }

            if (pasmLine.getLabel() != null) {
                try {
                    if (pasmLine.getLabel() != null && !pasmLine.isLocalLabel()) {
                        scope = savedContext;
                    }
                    scope.addSymbol(pasmLine.getLabel(), new ContextLiteral(pasmLine.getScope()));
                    scope.addSymbol("@" + pasmLine.getLabel(), new HubContextLiteral(pasmLine.getScope()));
                    if (pasmLine.getLabel() != null && !pasmLine.isLocalLabel()) {
                        scope = pasmLine.getScope();
                    }
                } catch (RuntimeException e) {
                    System.err.println(pasmLine);
                    e.printStackTrace();
                }
            }
            source.addAll(pasmLine.expand());
        }

        scope = savedContext;
    }

    Spin1Method compileMethod(MethodNode node) {
        Spin1Context localScope = new Spin1Context(scope);
        List<LocalVariable> parameters = new ArrayList<LocalVariable>();
        List<LocalVariable> returns = new ArrayList<LocalVariable>();
        List<LocalVariable> localVariables = new ArrayList<LocalVariable>();

        LocalVariable defaultReturn = new LocalVariable("LONG", "RESULT", new NumberLiteral(1), 0);
        localScope.addSymbol(defaultReturn.getName(), defaultReturn);
        localScope.addSymbol("@" + defaultReturn.getName(), defaultReturn);
        localScope.addSymbol(defaultReturn.getName().toLowerCase(), defaultReturn);
        localScope.addSymbol("@" + defaultReturn.getName().toLowerCase(), defaultReturn);
        returns.add(defaultReturn);

        int offset = 4;
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
            LocalVariable var = new LocalVariable("LONG", name, new NumberLiteral(1), 0);
            localScope.addSymbol(name, var);
            localScope.addSymbol("@" + name, var);
            returns.add(var);
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
            LocalVariable var = new LocalVariable(type, child.getText(), size, offset);
            localScope.addSymbol(child.getIdentifier().getText(), var);
            localScope.addSymbol("@" + child.getIdentifier().getText(), var);
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
            offset += ((count + 3) / 4) * 4;
        }

        Spin1Method method = new Spin1Method(localScope, node.name.getText(), parameters, returns, localVariables);
        method.setComment(node.getText());

        List<Spin1MethodLine> childs = compileStatements(localScope, node.getChilds());
        childs.add(new Spin1MethodLine(localScope, String.format(".label_" + labelCounter++), "RETURN", Collections.emptyList()));
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
                            Spin1TreeBuilder builder = new Spin1TreeBuilder();
                            while (iter.hasNext()) {
                                builder.addToken(iter.next());
                            }
                            Spin1MethodLine line = new Spin1MethodLine(context, String.format(".label_" + labelCounter++), token.getText(), builder.getRoot());
                            line.setText(node.getText());
                            line.setData(node);
                            lines.add(line);
                        }
                        else {
                            Spin1MethodLine line = new Spin1MethodLine(context, String.format(".label_" + labelCounter++), token.getText(), Collections.emptyList());
                            line.setText(node.getText());
                            line.setData(node);
                            lines.add(line);
                        }
                    }
                    else if ("IF".equalsIgnoreCase(token.getText()) || "IFNOT".equalsIgnoreCase(token.getText())) {
                        if (!iter.hasNext()) {
                            throw new RuntimeException("expected expression");
                        }

                        Spin1TreeBuilder builder = new Spin1TreeBuilder();
                        while (iter.hasNext()) {
                            builder.addToken(iter.next());
                        }

                        Spin1MethodLine line = new Spin1MethodLine(context, String.format(".label_" + labelCounter++), token.getText(), builder.getRoot());
                        line.setText(node.getText());
                        line.setData(node);
                        lines.add(line);

                        line.addChilds(compileStatements(context, node.getChilds()));

                        Spin1MethodLine falseLine = new Spin1MethodLine(context, String.format(".label_" + labelCounter++), null, Collections.emptyList());
                        lines.add(falseLine);
                        line.setData("false", falseLine);

                        Spin1MethodLine exitLine = new Spin1MethodLine(context, String.format(".label_" + labelCounter++), null, Collections.emptyList());
                        lines.add(exitLine);
                    }
                    else if ("ELSEIF".equalsIgnoreCase(token.getText()) || "ELSEIFNOT".equalsIgnoreCase(token.getText())) {
                        if (!iter.hasNext()) {
                            throw new RuntimeException("expected expression");
                        }

                        Spin1TreeBuilder builder = new Spin1TreeBuilder();
                        while (iter.hasNext()) {
                            builder.addToken(iter.next());
                        }
                        Spin1StatementNode argument = builder.getRoot();

                        Spin1MethodLine exitLine = lines.remove(lines.size() - 1);
                        lines.get(lines.size() - 2).setData("exit", exitLine);

                        Spin1MethodLine line = new Spin1MethodLine(context, String.format(".label_" + labelCounter++), token.getText(), argument);
                        line.setText(node.getText());
                        line.setData(node);
                        lines.add(line);

                        line.addChilds(compileStatements(context, node.getChilds()));

                        Spin1MethodLine falseLine = new Spin1MethodLine(context, String.format(".label_" + labelCounter++), null, Collections.emptyList());
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

                        Spin1MethodLine line = new Spin1MethodLine(context, String.format(".label_" + labelCounter++), token.getText(), Collections.emptyList());
                        line.setText(node.getText());
                        line.setData(node);
                        lines.add(line);

                        line.addChilds(compileStatements(context, node.getChilds()));

                        lines.add(exitLine);
                    }
                    else if ("REPEAT".equalsIgnoreCase(token.getText())) {
                        List<Spin1StatementNode> arguments = new ArrayList<Spin1StatementNode>();

                        String text = token.getText();

                        if (iter.hasNext()) {
                            token = iter.next();
                            if ("WHILE".equalsIgnoreCase(token.getText()) || "UNTIL".equalsIgnoreCase(token.getText())) {
                                text += " " + token.getText();
                                Spin1TreeBuilder builder = new Spin1TreeBuilder();
                                while (iter.hasNext()) {
                                    builder.addToken(iter.next());
                                }
                                arguments.add(builder.getRoot());
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
                                arguments.add(builder.getRoot());

                                if ("FROM".equalsIgnoreCase(token.getText())) {
                                    builder = new Spin1TreeBuilder();
                                    while (iter.hasNext()) {
                                        token = iter.next();
                                        if ("TO".equalsIgnoreCase(token.getText())) {
                                            break;
                                        }
                                        builder.addToken(token);
                                    }
                                    arguments.add(builder.getRoot());

                                    if ("TO".equalsIgnoreCase(token.getText())) {
                                        builder = new Spin1TreeBuilder();
                                        while (iter.hasNext()) {
                                            token = iter.next();
                                            if ("STEP".equalsIgnoreCase(token.getText())) {
                                                break;
                                            }
                                            builder.addToken(token);
                                        }
                                        arguments.add(builder.getRoot());

                                        if ("STEP".equalsIgnoreCase(token.getText())) {
                                            builder = new Spin1TreeBuilder();
                                            while (iter.hasNext()) {
                                                builder.addToken(iter.next());
                                            }
                                            arguments.add(builder.getRoot());
                                        }
                                    }
                                }
                            }
                        }

                        Spin1MethodLine line = new Spin1MethodLine(context, String.format(".label_" + labelCounter++), text, arguments);
                        line.setText(node.getText());
                        line.setData(node);
                        lines.add(line);

                        Spin1MethodLine loopLine = line;
                        if (arguments.size() == 1) {
                            loopLine = new Spin1MethodLine(context, String.format(".label_" + labelCounter++), null, Collections.emptyList());
                            line.addChild(loopLine);
                        }

                        line.addChilds(compileStatements(context, node.getChilds()));
                        if (line.getChildsCount() == 0) {
                            line.addChild(new Spin1MethodLine(context, String.format(".label_" + labelCounter++), null, Collections.emptyList()));
                        }

                        if (arguments.size() == 3 || arguments.size() == 4) {
                            loopLine = new Spin1MethodLine(context, String.format(".label_" + labelCounter++), "REPEAT-LOOP", Collections.emptyList());
                            line.addChild(loopLine);
                        }
                        else {
                            line.addChild(new Spin1MethodLine(context, String.format(".label_" + labelCounter++), "NEXT", Collections.emptyList()));
                        }

                        line.setData("next", loopLine);

                        Spin1MethodLine quitLine = new Spin1MethodLine(context, String.format(".label_" + labelCounter++), null, Collections.emptyList());
                        lines.add(quitLine);
                        line.setData("quit", quitLine);
                    }
                    else if ("WHILE".equalsIgnoreCase(token.getText()) || "UNTIL".equalsIgnoreCase(token.getText())) {
                        if (!iter.hasNext()) {
                            throw new RuntimeException("expected expression");
                        }
                        Spin1TreeBuilder builder = new Spin1TreeBuilder();
                        while (iter.hasNext()) {
                            builder.addToken(iter.next());
                        }
                        Spin1StatementNode argument = builder.getRoot();

                        Spin1MethodLine repeatLine = lines.get(lines.size() - 2);
                        repeatLine.childs.remove(repeatLine.childs.size() - 1);

                        Spin1MethodLine line = new Spin1MethodLine(context, String.format(".label_" + labelCounter++), token.getText(), argument);
                        line.setText(node.getText());
                        line.setData(node);
                        line.setData("true", repeatLine);
                        lines.add(lines.size() - 1, line);

                        repeatLine.setData("next", line);
                    }
                    else if ("RETURN".equalsIgnoreCase(token.getText())) {
                        List<Spin1StatementNode> arguments = new ArrayList<Spin1StatementNode>();

                        if (iter.hasNext()) {
                            Spin1TreeBuilder builder = new Spin1TreeBuilder();
                            while (iter.hasNext()) {
                                builder.addToken(iter.next());
                            }
                            arguments.add(builder.getRoot());
                        }

                        Spin1MethodLine line = new Spin1MethodLine(context, String.format(".label_" + labelCounter++), token.getText(), arguments);
                        line.setText(node.getText());
                        line.setData(node);
                        lines.add(line);
                    }
                    else if ("QUIT".equalsIgnoreCase(token.getText()) || "NEXT".equalsIgnoreCase(token.getText())) {
                        if (iter.hasNext()) {
                            throw new RuntimeException("syntax error");
                        }
                        Spin1MethodLine line = new Spin1MethodLine(context, String.format(".label_" + labelCounter++), token.getText(), Collections.emptyList());
                        line.setText(node.getText());
                        line.setData(node);
                        lines.add(line);
                    }
                    else if ("CASE".equalsIgnoreCase(token.getText())) {
                        List<Spin1StatementNode> arguments = new ArrayList<Spin1StatementNode>();
                        Spin1MethodLine line = new Spin1MethodLine(context, String.format(".label_" + labelCounter++), token.getText(), arguments);
                        line.setText(node.getText());

                        if (!iter.hasNext()) {
                            throw new RuntimeException("expected expression");
                        }
                        Spin1TreeBuilder builder = new Spin1TreeBuilder();
                        while (iter.hasNext()) {
                            builder.addToken(iter.next());
                        }
                        arguments.add(builder.getRoot());

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

                                Spin1MethodLine targetLine = new Spin1MethodLine(context, String.format(".label_" + labelCounter++), null, Collections.emptyList());
                                targetLine.addChilds(compileStatements(context, child.getChilds()));
                                targetLine.addChild(new Spin1MethodLine(context, String.format(".label_" + labelCounter++), "CASE_DONE", Collections.emptyList()));

                                Spin1StatementNode expression = builder.getRoot();
                                if ("OTHER".equalsIgnoreCase(expression.getText())) {
                                    line.childs.add(0, targetLine);
                                    line.setData("other", targetLine);
                                }
                                else {
                                    expression.setData("true", targetLine);
                                    line.addChild(targetLine);
                                    arguments.add(expression);
                                }
                            }
                        }

                        Spin1MethodLine doneLine = new Spin1MethodLine(context, String.format(".label_" + labelCounter++), null, Collections.emptyList());
                        line.addChild(doneLine);
                        line.setData("end", doneLine);
                        line.setData(node);
                        lines.add(line);
                    }
                    else {
                        Spin1TreeBuilder builder = new Spin1TreeBuilder();
                        iter = node.getTokens().iterator();
                        while (iter.hasNext()) {
                            token = iter.next();
                            builder.addToken(token);
                        }
                        Spin1MethodLine line = new Spin1MethodLine(context, String.format(".label_" + labelCounter++), null, builder.getRoot());
                        line.setText(node.getText());
                        line.setData(node);
                        lines.add(line);
                    }

                } catch (CompilerMessage e) {
                    logMessage(e);
                    throw e;
                } catch (Exception e) {
                    logMessage(new CompilerMessage(e.getMessage(), node));
                    e.printStackTrace();
                }
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
            line.addSource(new Jz(line.getScope(), new Identifier(target.getLabel(), target.getScope())));
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
            line.addSource(new Jnz(line.getScope(), new Identifier(target.getLabel(), target.getScope())));
        }
        else if ("ELSE".equalsIgnoreCase(text)) {

        }
        else if ("REPEAT".equalsIgnoreCase(text)) {
            if (line.getArgumentsCount() == 1) {
                line.addSource(compileConstantExpression(line.getScope(), line.getArgument(0)));
                Spin1MethodLine target = (Spin1MethodLine) line.getData("quit");
                line.addSource(new Tjz(line.getScope(), new Identifier(target.getLabel(), target.getScope())));
            }
            else if (line.getArgumentsCount() == 3 || line.getArgumentsCount() == 4) {
                line.addSource(compileConstantExpression(line.getScope(), line.getArgument(1)));

                String varText = line.getArgument(0).getText();
                Expression expression = line.getScope().getLocalSymbol(varText);
                if (expression == null) {
                    throw new CompilerMessage("undefined symbol " + varText, line.getArgument(0).getToken());
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
            line.addSource(new Jz(line.getScope(), new Identifier(target.getLabel(), target.getScope())));
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
            line.addSource(new Jnz(line.getScope(), new Identifier(target.getLabel(), target.getScope())));
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
            line.addSource(new Jnz(line.getScope(), new Identifier(target.getLabel(), target.getScope())));
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
            line.addSource(new Jz(line.getScope(), new Identifier(target.getLabel(), target.getScope())));
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
                line.addSource(new Jmp(line.getScope(), new Identifier(repeat.getLabel(), repeat.getScope())));
            }
            else if (repeat.getArgumentsCount() == 1) {
                line.addSource(new Djnz(line.getScope(), new Identifier(target.getLabel(), target.getScope())));
            }
            else {
                line.addSource(new Jmp(line.getScope(), new Identifier(target.getLabel(), target.getScope())));
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
                    throw new CompilerMessage("undefined symbol " + varText, line.getArgument(0).getToken());
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
                line.addSource(compileConstantExpression(line.getScope(), repeat.getArgument(1)));
                line.addSource(compileConstantExpression(line.getScope(), repeat.getArgument(2)));

                String varText = repeat.getArgument(0).getText();
                Expression expression = line.getScope().getLocalSymbol(varText);
                if (expression == null) {
                    throw new CompilerMessage("undefined symbol " + varText, repeat.getArgument(0).getToken());
                }
                else if (expression instanceof Variable) {
                    line.addSource(new RepeatLoop(line.getScope(), (Variable) expression, repeat.getArgumentsCount() == 4, new Identifier(target.getLabel(), target.getScope())));
                }
                else {
                    throw new RuntimeException("unsupported " + line.getArgument(0));
                }
            }
        }
        else if ("QUIT".equalsIgnoreCase(text)) {
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
            Spin1MethodLine target = (Spin1MethodLine) repeat.getData("quit");
            line.addSource(new Jmp(line.getScope(), new Identifier(target.getLabel(), target.getScope())));
        }
        else if ("RETURN".equalsIgnoreCase(text)) {
            if (line.getArgumentsCount() == 0) {
                line.addSource(new Bytecode(line.getScope(), 0b00110010, text));
            }
            else if (line.getArgumentsCount() == 1) {
                line.addSource(compileBytecodeExpression(line.getScope(), line.getArgument(0), false));
                line.addSource(new Bytecode(line.getScope(), 0b00110011, text));
            }
            else {
                throw new RuntimeException("unsupported");
            }
        }
        else if ("CASE".equalsIgnoreCase(text)) {
            Spin1MethodLine end = (Spin1MethodLine) line.getData("end");
            line.addSource(new Constant(line.getScope(), new Identifier(end.getLabel(), end.getScope())));

            for (Spin1StatementNode arg : line.getArguments()) {
                Spin1MethodLine target = (Spin1MethodLine) arg.getData("true");
                if (target != null) {
                    if ("..".equals(arg.getText())) {
                        line.addSource(compileBytecodeExpression(line.getScope(), arg.getChild(0), false));
                        line.addSource(compileBytecodeExpression(line.getScope(), arg.getChild(1), false));
                        line.addSource(new CaseRangeJmp(line.getScope(), new Identifier(target.getLabel(), target.getScope())));
                    }
                    else {
                        line.addSource(compileBytecodeExpression(line.getScope(), arg, false));
                        line.addSource(new CaseJmp(line.getScope(), new Identifier(target.getLabel(), target.getScope())));
                    }
                }
                else {
                    target = (Spin1MethodLine) arg.getData("other");
                    if (target != null) {
                        line.addSource(new Jmp(line.getScope(), new Identifier(target.getLabel(), target.getScope())));
                    }
                    else {
                        line.addSource(compileBytecodeExpression(line.getScope(), arg, false));
                    }
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
                    throw new CompilerMessage("expected " + desc.parameters + " argument(s), found " + line.getArgumentsCount(), (Node) line.getData());
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
                line.addSource(compileBytecodeExpression(line.getScope(), arg, false));
            }
        }

        Spin1MethodLine target = (Spin1MethodLine) line.getData("exit");
        if (target != null) {
            Spin1MethodLine newLine = new Spin1MethodLine(line.getScope(), null, null, Collections.emptyList());
            newLine.addSource(new Jmp(line.getScope(), new Identifier(target.getLabel(), target.getScope())));
            line.addChild(newLine);
        }

        for (Spin1MethodLine child : line.getChilds()) {
            compileLine(child);
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
                return new CharacterLiteral(s.charAt(0));
            }
            throw new RuntimeException("string not allowed");
        }
        else if (node.getType() == 0) {
            return new Identifier(node.getText(), context);
        }
        else if ("-".equals(node.getText())) {
            return new Subtract(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
        }
        else if ("+".equals(node.getText())) {
            return new Add(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
        }
        else if ("*".equals(node.getText())) {
            return new Multiply(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
        }
        else if ("/".equals(node.getText())) {
            return new Divide(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
        }

        throw new RuntimeException("unknown " + node.getText());
    }

    List<Spin1Bytecode> compileBytecodeExpression(Spin1Context context, Spin1StatementNode node, boolean push) {
        List<Spin1Bytecode> source = new ArrayList<Spin1Bytecode>();

        Spin1StatementNode argsNode = node;
        if (argsNode.getChildCount() == 1 && ",".equals(argsNode.getChild(0).getText())) {
            argsNode = argsNode.getChild(0);
        }

        Descriptor desc = Spin1Bytecode.getDescriptor(node.getText());
        if (desc != null) {
            if (argsNode.getChildCount() != desc.parameters) {
                throw new RuntimeException("expected " + desc.parameters + " argument(s), found " + argsNode.getChildCount());
            }
            for (int i = 0; i < desc.parameters; i++) {
                source.addAll(compileBytecodeExpression(context, argsNode.getChild(i), true));
            }
            source.add(new Bytecode(context, push ? desc.code_push : desc.code, node.getText()));
        }
        else if ("CONSTANT".equalsIgnoreCase(node.getText())) {
            if (argsNode.getChildCount() != 1) {
                throw new RuntimeException("expected " + 1 + " argument(s), found " + 0);
            }
            try {
                Expression expression = buildConstantExpression(context, argsNode.getChild(0));
                if (!expression.isConstant()) {
                    throw new CompilerMessage("expression is not constant", argsNode.getChild(0).getToken());
                }
                source.add(new Constant(context, expression));
            } catch (Exception e) {
                throw new CompilerMessage("expression is not constant", argsNode.getChild(0).getToken());
            }
        }
        else if ("CLKFREQ".equalsIgnoreCase(node.getText())) {
            source.add(new Constant(context, new NumberLiteral(0)));
            source.add(new MemoryOp(context, MemoryOp.Size.Long, false, MemoryOp.Base.Pop, MemoryOp.Op.Read, null));
        }
        else if ("CLKMODE".equalsIgnoreCase(node.getText())) {
            source.add(new Constant(context, new NumberLiteral(4)));
            source.add(new MemoryOp(context, MemoryOp.Size.Byte, false, MemoryOp.Base.Pop, MemoryOp.Op.Read, null));
        }
        else if ("COGID".equalsIgnoreCase(node.getText())) {
            if (argsNode.getChildCount() != 0) {
                throw new RuntimeException("expected " + 0 + " argument(s), found " + argsNode.getChildCount());
            }
            source.add(new RegisterOp(context, RegisterOp.Op.Read, 0x1E9));
        }
        else if ("COGNEW".equalsIgnoreCase(node.getText())) {
            if (argsNode.getChildCount() != 2) {
                throw new RuntimeException("expected " + 2 + " argument(s), found " + argsNode.getChildCount());
            }
            source.add(new Constant(context, new NumberLiteral(-1)));
            source.addAll(compileBytecodeExpression(context, argsNode.getChild(0), true));
            source.addAll(compileBytecodeExpression(context, argsNode.getChild(1), true));
            desc = Spin1Bytecode.getDescriptor("COGINIT");
            source.add(new Bytecode(context, push ? desc.code_push : desc.code, node.getText()));
        }
        else if ("LOOKDOWN".equalsIgnoreCase(node.getText()) || "LOOKDOWNZ".equalsIgnoreCase(node.getText()) || "LOOKUP".equalsIgnoreCase(node.getText())
            || "LOOKUPZ".equalsIgnoreCase(node.getText())) {
            if (argsNode.getChildCount() == 0) {
                throw new RuntimeException("expected argument(s), found none");
            }
            argsNode = argsNode.getChild(0);
            if (!":".equalsIgnoreCase(argsNode.getText()) || argsNode.getChildCount() < 2) {
                throw new RuntimeException("invalid argument(s)");
            }

            int code = "LOOKDOWN".equalsIgnoreCase(node.getText()) ? 0b00010001 : 0b00010000;
            int code_range = code | 0b00000010;

            source.add(new Constant(context, new NumberLiteral(node.getText().toUpperCase().endsWith("Z") ? 0 : 1)));

            Spin1Bytecode end = new Spin1Bytecode(context);
            source.add(new Constant(context, new ContextLiteral(end.getContext())));

            source.addAll(compileBytecodeExpression(context, argsNode.getChild(0), true));

            for (int i = 1; i < argsNode.getChildCount(); i++) {
                Spin1StatementNode arg = argsNode.getChild(i);
                if ("..".equals(arg.getText())) {
                    source.addAll(compileBytecodeExpression(context, arg.getChild(0), true));
                    source.addAll(compileBytecodeExpression(context, arg.getChild(1), true));
                    source.add(new Bytecode(context, code_range, node.getText().toUpperCase()));
                }
                else {
                    source.addAll(compileBytecodeExpression(context, arg, true));
                    source.add(new Bytecode(context, code, node.getText().toUpperCase()));
                }
            }

            source.add(new Bytecode(context, 0b00001111, "LOOKDONE"));
            source.add(end);
        }
        else if (node.getType() == Token.NUMBER) {
            Expression expression = new NumberLiteral(node.getText());
            source.add(new Constant(context, expression));
        }
        else if (node.getType() == Token.STRING) {
            String s = node.getText().substring(1);
            s = s.substring(0, s.length() - 1);
            if (s.length() == 1) {
                Expression expression = new CharacterLiteral(s.charAt(0));
                source.add(new Constant(context, expression));
            }
            else {
                throw new RuntimeException("string not yet implemented " + node.getText());
            }
        }
        else if (":=".equals(node.getText())) {
            source.addAll(compileBytecodeExpression(context, node.getChild(1), true));

            Spin1StatementNode left = node.getChild(0);
            if (left.getType() == Token.OPERATOR) {
                source.addAll(compileBytecodeExpression(context, left, push));
            }
            else if ("BYTE".equalsIgnoreCase(left.getText()) || "WORD".equalsIgnoreCase(left.getText()) || "LONG".equalsIgnoreCase(left.getText())) {
                source.addAll(compileBytecodeExpression(context, left.getChild(0), true));
                if (left.getChildCount() > 1) {
                    source.addAll(compileBytecodeExpression(context, left.getChild(1), true));
                }

                if ("BYTE".equalsIgnoreCase(left.getText())) {
                    source.add(new MemoryOp(context, MemoryOp.Size.Byte, left.getChildCount() > 1, MemoryOp.Base.Pop, MemoryOp.Op.Write, null));
                }
                else if ("WORD".equalsIgnoreCase(left.getText())) {
                    source.add(new MemoryOp(context, MemoryOp.Size.Word, left.getChildCount() > 1, MemoryOp.Base.Pop, MemoryOp.Op.Write, null));
                }
                else if ("LONG".equalsIgnoreCase(left.getText())) {
                    source.add(new MemoryOp(context, MemoryOp.Size.Long, left.getChildCount() > 1, MemoryOp.Base.Pop, MemoryOp.Op.Write, null));
                }
            }
            else {
                Expression expression = context.getLocalSymbol(left.getText());
                if (expression == null) {
                    throw new CompilerMessage("undefined symbol " + left.getText(), left.getToken());
                }
                if (expression instanceof Register) {
                    if (left.getChildCount() == 1) {
                        source.addAll(compileBytecodeExpression(context, left.getChild(0), true));
                        source.add(new RegisterBitOp(context, RegisterBitOp.Op.Write, expression.getNumber().intValue()));
                    }
                    else {
                        source.add(new RegisterOp(context, RegisterOp.Op.Write, expression.getNumber().intValue()));
                    }
                }
                else if (expression instanceof Variable) {
                    if (push) {
                        source.add(new VariableOp(context, VariableOp.Op.Assign, (Variable) expression));
                        source.add(new Bytecode(context, 0x80, "WRITE"));
                    }
                    else {
                        source.add(new VariableOp(context, VariableOp.Op.Write, (Variable) expression));
                    }
                }
                else {
                    source.add(new MemoryOp(context, MemoryOp.Size.Long, !push, MemoryOp.Base.PBase, MemoryOp.Op.Write, expression));
                }
            }
        }
        else if (MathOp.isAssignMathOp(node.getText())) {
            source.addAll(compileBytecodeExpression(context, node.getChild(1), true));

            Spin1StatementNode left = node.getChild(0);
            if (left.getType() == Token.OPERATOR) {
                source.addAll(compileBytecodeExpression(context, left, true));
            }
            else {
                Expression expression = context.getLocalSymbol(left.getText());
                if (expression == null) {
                    throw new CompilerMessage("undefined symbol " + left.getText(), left.getToken());
                }
                if (expression instanceof Register) {
                    if (left.getChildCount() == 1) {
                        source.addAll(compileBytecodeExpression(context, left.getChild(0), true));
                        source.add(new RegisterBitOp(context, RegisterBitOp.Op.Assign, expression.getNumber().intValue()));
                    }
                    else {
                        source.add(new RegisterOp(context, RegisterOp.Op.Assign, expression.getNumber().intValue()));
                    }
                }
                else if (expression instanceof Variable) {
                    source.add(new VariableOp(context, VariableOp.Op.Assign, (Variable) expression));
                }
                else {
                    source.add(new MemoryOp(context, MemoryOp.Size.Long, !push, MemoryOp.Base.PBase, MemoryOp.Op.Assign, expression));
                }
            }
            source.add(new MathOp(context, node.getText(), push));
        }
        else if ("-".equals(node.getText()) && node.getChildCount() == 1) {
            source.addAll(compileBytecodeExpression(context, node.getChild(0), true));
            source.add(new Bytecode(context, 0b111_00110 | (push ? 0b10000000 : 0b00000000), "NEGATE"));
        }
        else if ("||".equals(node.getText()) && node.getChildCount() == 1) {
            source.addAll(compileBytecodeExpression(context, node.getChild(0), true));
            source.add(new MathOp(context, node.getText(), push));
        }
        else if (MathOp.isMathOp(node.getText())) {
            if (node.getChildCount() != 2) {
                throw new RuntimeException("expression syntax error " + node.getText());
            }
            source.addAll(compileBytecodeExpression(context, node.getChild(0), true));
            source.addAll(compileBytecodeExpression(context, node.getChild(1), true));
            source.add(new MathOp(context, node.getText(), push));
        }
        else if ("[".equalsIgnoreCase(node.getText())) {
            String text0 = node.getChild(0).getText();
            Spin1StatementNode index1 = node.getChild(1);
            Spin1StatementNode index2 = null;

            String postEffect = node.getChild(node.getChildCount() - 1).getText();
            boolean hasPostEffect = "~".equals(postEffect) || "~~".equals(postEffect);

            if (hasPostEffect) {
                if (node.getChildCount() == 4) {
                    index2 = node.getChild(2);
                }
            }
            else {
                if (node.getChildCount() == 3) {
                    index2 = node.getChild(2);
                }
            }

            if (index2 != null) {
                source.addAll(compileBytecodeExpression(context, index1, true));
                source.addAll(compileBytecodeExpression(context, index2, true));
            }
            else {
                source.addAll(compileBytecodeExpression(context, index1, true));
            }

            if ("BYTE".equalsIgnoreCase(text0)) {
                source.add(new MemoryOp(context, MemoryOp.Size.Byte, index2 != null, MemoryOp.Base.Pop, push ? MemoryOp.Op.Read : MemoryOp.Op.Write, null));
            }
            else if ("WORD".equalsIgnoreCase(text0)) {
                source.add(new MemoryOp(context, MemoryOp.Size.Word, index2 != null, MemoryOp.Base.Pop, push ? MemoryOp.Op.Read : MemoryOp.Op.Write, null));
            }
            else if ("LONG".equalsIgnoreCase(text0)) {
                source.add(new MemoryOp(context, MemoryOp.Size.Long, index2 != null, MemoryOp.Base.Pop, push ? MemoryOp.Op.Read : MemoryOp.Op.Write, null));
            }
            else {
                Expression expression = context.getLocalSymbol(text0);
                if (expression == null) {
                    throw new CompilerMessage("undefined symbol " + node.getChild(0).getText(), node.getChild(0).getToken());
                }
                if (expression instanceof Register) {
                    if (hasPostEffect) {
                        source.add(new RegisterBitOp(context, RegisterBitOp.Op.Assign, expression.getNumber().intValue()));
                        if ("~".equalsIgnoreCase(postEffect)) {
                            source.add(new Bytecode(context, 0b0_00110_00, "POST_CLEAR"));
                        }
                        else if ("~~".equalsIgnoreCase(postEffect)) {
                            source.add(new Bytecode(context, 0b0_00111_00, "POST_SET"));
                        }
                        else {
                            throw new RuntimeException("invalid operator " + node.getChild(0).getText());
                        }
                    }
                    else {
                        source.add(new RegisterBitOp(context, push ? RegisterBitOp.Op.Assign : RegisterBitOp.Op.Write, expression.getNumber().intValue()));
                    }
                }
                else if (expression instanceof Variable) {
                    source.add(new VariableOp(context, push ? VariableOp.Op.Assign : VariableOp.Op.Write, (Variable) expression));
                }
                else {
                    source.add(new MemoryOp(context, MemoryOp.Size.Long, !push, MemoryOp.Base.PBase, MemoryOp.Op.Write, expression));
                }
            }
        }
        else if ("?".equalsIgnoreCase(node.getText())) {
            if (node.getChildCount() == 1) {
                Expression expression = context.getLocalSymbol(argsNode.getChild(0).getText());
                if (expression == null) {
                    throw new RuntimeException("undefined symbol " + argsNode.getChild(0).getText());
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
        else if ("\\".equalsIgnoreCase(node.getText())) {
            Expression expression = context.getLocalSymbol(node.getChild(0).getText());
            if (!(expression instanceof Method)) {
                throw new RuntimeException("invalid symbol " + node.getText());
            }

            argsNode = node.getChild(0);
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
            if (argsNode.getChildCount() != 1) {
                throw new RuntimeException("expression syntax error");
            }
            Expression expression = context.getLocalSymbol(argsNode.getChild(0).getText());
            if (expression == null) {
                throw new RuntimeException("undefined symbol " + argsNode.getChild(0).getText());
            }
            if (expression instanceof Variable) {
                int code = 0b0_0100_11_0;
                if (push) {
                    code |= 0b10000000;
                }
                source.add(new VariableOp(context, VariableOp.Op.Assign, (Variable) expression));
                source.add(new Bytecode(context, code, "PRE_INC"));
            }
        }
        else if ("--".equalsIgnoreCase(node.getText())) {
            if (argsNode.getChildCount() != 1) {
                throw new RuntimeException("expression syntax error");
            }
            Expression expression = context.getLocalSymbol(argsNode.getChild(0).getText());
            if (expression == null) {
                throw new RuntimeException("undefined symbol " + argsNode.getChild(0).getText());
            }
            if (expression instanceof Variable) {
                int code = 0b0_0110_11_0;
                if (push) {
                    code |= 0b10000000;
                }
                source.add(new VariableOp(context, VariableOp.Op.Assign, (Variable) expression));
                source.add(new Bytecode(context, code, "PRE_DEC"));
            }
        }
        else if ("~".equalsIgnoreCase(node.getText())) {
            if (argsNode.getChildCount() != 1) {
                throw new RuntimeException("expression syntax error");
            }
            Expression expression = context.getLocalSymbol(argsNode.getChild(0).getText());
            if (expression == null) {
                throw new RuntimeException("undefined symbol " + argsNode.getChild(0).getText());
            }
            if (expression instanceof Variable) {
                int code = 0b0_00100_00;
                if (push) {
                    code |= 0b10000000;
                }
                source.add(new VariableOp(context, VariableOp.Op.Assign, (Variable) expression));
                source.add(new Bytecode(context, code, "SIGN_EXTEND_BYTE"));
            }
        }
        else if ("~~".equalsIgnoreCase(node.getText())) {
            if (argsNode.getChildCount() != 1) {
                throw new RuntimeException("expression syntax error");
            }
            Expression expression = context.getLocalSymbol(argsNode.getChild(0).getText());
            if (expression == null) {
                throw new RuntimeException("undefined symbol " + argsNode.getChild(0).getText());
            }
            if (expression instanceof Variable) {
                int code = 0b0_00101_00;
                if (push) {
                    code |= 0b10000000;
                }
                source.add(new VariableOp(context, VariableOp.Op.Assign, (Variable) expression));
                source.add(new Bytecode(context, code, "SIGN_EXTEND_WORD"));
            }
        }
        else if ("..".equalsIgnoreCase(node.getText())) {
            if (argsNode.getChildCount() != 2) {
                throw new RuntimeException("expression syntax error");
            }
            source.addAll(compileBytecodeExpression(context, argsNode.getChild(0), true));
            source.addAll(compileBytecodeExpression(context, argsNode.getChild(1), true));
        }
        else if ("BYTE".equalsIgnoreCase(node.getText()) || "WORD".equalsIgnoreCase(node.getText()) || "LONG".equalsIgnoreCase(node.getText())) {
            source.addAll(compileBytecodeExpression(context, node.getChild(0), true));
            if (node.getChildCount() > 1) {
                source.addAll(compileBytecodeExpression(context, node.getChild(1), true));
            }

            if ("BYTE".equalsIgnoreCase(node.getText())) {
                source.add(new MemoryOp(context, MemoryOp.Size.Byte, node.getChildCount() > 1, MemoryOp.Base.Pop, push ? MemoryOp.Op.Read : MemoryOp.Op.Write, null));
            }
            else if ("WORD".equalsIgnoreCase(node.getText())) {
                source.add(new MemoryOp(context, MemoryOp.Size.Word, node.getChildCount() > 1, MemoryOp.Base.Pop, push ? MemoryOp.Op.Read : MemoryOp.Op.Write, null));
            }
            else if ("LONG".equalsIgnoreCase(node.getText())) {
                source.add(new MemoryOp(context, MemoryOp.Size.Long, node.getChildCount() > 1, MemoryOp.Base.Pop, push ? MemoryOp.Op.Read : MemoryOp.Op.Write, null));
            }
        }
        else {
            Expression expression = context.getLocalSymbol(node.getText());
            if (expression == null) {
                throw new CompilerMessage("undefined symbol " + node.getText(), node.getToken());
            }
            if (expression instanceof Register) {
                if (node.getChildCount() != 0) {
                    source.addAll(compileBytecodeExpression(context, node.getChild(0), true));
                    if (node.getChildCount() == 2) {
                        source.add(new RegisterBitOp(context, RegisterBitOp.Op.Assign, expression.getNumber().intValue()));
                        if ("~".equalsIgnoreCase(node.getChild(1).getText())) {
                            source.add(new Bytecode(context, 0b0_00110_00, "POST_CLEAR"));
                        }
                        else if ("~~".equalsIgnoreCase(node.getChild(1).getText())) {
                            source.add(new Bytecode(context, 0b0_00111_00, "POST_SET"));
                        }
                        else {
                            throw new RuntimeException("invalid operator " + node.getChild(0).getText());
                        }
                    }
                    else {
                        source.add(new RegisterBitOp(context, push ? RegisterBitOp.Op.Assign : RegisterBitOp.Op.Write, expression.getNumber().intValue()));
                    }
                }
                else {
                    source.add(new RegisterOp(context, RegisterOp.Op.Read, expression.getNumber().intValue()));
                }
            }
            else if (expression instanceof Variable) {
                if (node.getText().startsWith("@")) {
                    source.add(new VariableOp(context, VariableOp.Op.Address, (Variable) expression));
                }
                else if (node.getChildCount() == 1) {
                    if ("++".equalsIgnoreCase(node.getChild(0).getText())) {
                        int code = 0b0_0101_11_0;
                        if (push) {
                            code |= 0b10000000;
                        }
                        source.add(new VariableOp(context, VariableOp.Op.Assign, (Variable) expression));
                        source.add(new Bytecode(context, code, "POST_INC"));
                    }
                    else if ("--".equalsIgnoreCase(node.getChild(0).getText())) {
                        int code = 0b0_0111_11_0;
                        if (push) {
                            code |= 0b10000000;
                        }
                        source.add(new VariableOp(context, VariableOp.Op.Assign, (Variable) expression));
                        source.add(new Bytecode(context, code, "POST_DEC"));
                    }
                    else if ("~".equalsIgnoreCase(node.getChild(0).getText())) {
                        int code = 0b0_00110_00;
                        if (push) {
                            code |= 0b10000000;
                        }
                        source.add(new VariableOp(context, VariableOp.Op.Assign, (Variable) expression));
                        source.add(new Bytecode(context, code, "POST_CLEAR"));
                    }
                    else if ("~~".equalsIgnoreCase(node.getChild(0).getText())) {
                        int code = 0b0_00111_00;
                        if (push) {
                            code |= 0b10000000;
                        }
                        source.add(new VariableOp(context, VariableOp.Op.Assign, (Variable) expression));
                        source.add(new Bytecode(context, code, "POST_SET"));
                    }
                    else if ("?".equalsIgnoreCase(node.getChild(0).getText())) {
                        int code = 0b0_00011_00;
                        if (push) {
                            code |= 0b10000000;
                        }
                        source.add(new VariableOp(context, VariableOp.Op.Assign, (Variable) expression));
                        source.add(new Bytecode(context, code, "RANDOM_REVERSE"));
                    }
                    else {
                        throw new RuntimeException("invalid operator " + node.getChild(0).getText());
                    }
                }
                else {
                    source.add(new VariableOp(context, VariableOp.Op.Read, (Variable) expression));
                }
            }
            else if (expression instanceof Method) {
                int parameters = ((Method) expression).getArgumentsCount();
                if (argsNode.getChildCount() != parameters) {
                    throw new RuntimeException("expected " + parameters + " argument(s), found " + argsNode.getChildCount());
                }
                source.add(new Bytecode(context, new byte[] {
                    (byte) (push ? 0b00000000 : 0b00000001),
                }, "ANCHOR"));
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
            else {
                if (node.getText().startsWith("@")) {
                    source.add(new MemoryOp(context, MemoryOp.Size.Long, !push, MemoryOp.Base.PBase, MemoryOp.Op.Address, expression));
                }
                else {
                    source.add(new Constant(context, expression));
                }
            }
        }

        return source;
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
            throw new RuntimeException("_CLKFREQ / _XINFREQ specified without _CLKMODE");
        }
        if (_clkfreq != null && _xinfreq != null) {
            throw new RuntimeException("Either _CLKFREQ or _XINFREQ must be specified, but not both");
        }

        int mode = _clkmode.getNumber().intValue();
        if (mode == 0 || (mode & 0xFFFFF800) != 0 || (((mode & 0x03) != 0) && ((mode & 0x7FC) != 0))) {
            throw new RuntimeException("Invalid _CLKMODE specified");
        }
        if ((mode & 0x03) != 0) { // RCFAST or RCSLOW
            if (_clkfreq != null || _xinfreq != null) {
                throw new RuntimeException("_CLKFREQ / _XINFREQ not allowed with RCFAST / RCSLOW");
            }

            scope.addSymbol("CLKMODE", new NumberLiteral(mode == 2 ? 1 : 0));
            scope.addSymbol("CLKFREQ", new NumberLiteral(mode == 2 ? 20_000 : 12_000_000));
            return;
        }

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
        scope.addSymbol("CLKMODE", new NumberLiteral(clkmode));
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
            throw new RuntimeException("Invalid _CLKMODE specified");
        }

        return bitPos;
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

    Expression buildExpression(Node node, Spin1Context scope) {
        return buildExpression(node.getTokens(), scope);
    }

    Expression buildExpression(List<Token> tokens, Spin1Context scope) {
        int state = 0;
        ExpressionBuilder expressionBuilder = new ExpressionBuilder();

        Iterator<Token> iter = tokens.iterator();
        while (iter.hasNext()) {
            Token token = iter.next();

            if ("(".equals(token.getText())) {
                expressionBuilder.addOperatorToken(expressionBuilder.GROUP_OPEN);
                state = 0;
                continue;
            }
            else if (")".equals(token.getText())) {
                expressionBuilder.addOperatorToken(expressionBuilder.GROUP_CLOSE);
                state = 1;
                continue;
            }

            switch (state) {
                case 0:
                    if ("+".equals(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.POSITIVE);
                    }
                    else if ("-".equals(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.NEGATIVE);
                    }
                    else if ("^^".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.SQRT);
                    }
                    else if ("||".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.ABS);
                    }
                    else if ("~".equalsIgnoreCase(token.getText())) {
                        // TODO expressionBuilder.addOperatorToken(expressionBuilder.SIGNX7);
                    }
                    else if ("~~".equalsIgnoreCase(token.getText())) {
                        // TODO expressionBuilder.addOperatorToken(expressionBuilder.SIGNX15);
                    }
                    else if ("?".equalsIgnoreCase(token.getText())) {
                        // TODO expressionBuilder.addOperatorToken(expressionBuilder.RND);
                    }
                    else if ("|<".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.DECOD);
                    }
                    else if (">|".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.ENCOD);
                    }
                    else if ("!".equals(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.NOT);
                    }
                    else if ("NOT".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.LOGICAL_NOT);
                    }
                    else if (token.type == Token.NUMBER) {
                        if ("$".equals(token.getText())) {
                            expressionBuilder.addValueToken(new Identifier(token.getText(), scope));
                        }
                        else {
                            expressionBuilder.addValueToken(new NumberLiteral(token.getText()));
                        }
                    }
                    else if (token.type == Token.STRING) {
                        expressionBuilder.addValueToken(new CharacterLiteral(token.getText().charAt(1)));
                    }
                    else {
                        expressionBuilder.addValueToken(new Identifier(token.getText(), scope));
                    }
                    state = 1;
                    break;
                case 1:
                    if ("+".equals(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.ADD);
                    }
                    else if ("-".equals(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.SUBTRACT);
                    }
                    else if ("*".equals(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.MULTIPLY);
                    }
                    else if ("**".equals(token.getText())) {
                        // TODO expressionBuilder.addOperatorToken(expressionBuilder.MULTIPLY_UPPER);
                    }
                    else if ("/".equals(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.DIVIDE);
                    }
                    else if ("//".equals(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.MODULO);
                    }
                    else if ("#>".equals(token.getText())) {
                        // TODO expressionBuilder.addOperatorToken(expressionBuilder.LIMIT_MIN);
                    }
                    else if ("<#".equals(token.getText())) {
                        // TODO expressionBuilder.addOperatorToken(expressionBuilder.LIMIT_MAX);
                    }
                    else if ("~>".equals(token.getText())) {
                        // TODO expressionBuilder.addOperatorToken(expressionBuilder.SAR);
                    }
                    else if ("<<".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.SHIFT_LEFT);
                    }
                    else if (">>".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.SHIFT_RIGHT);
                    }
                    else if ("<-".equalsIgnoreCase(token.getText())) {
                        // TODO expressionBuilder.addOperatorToken(expressionBuilder.ROTATE_LEFT);
                    }
                    else if ("->".equalsIgnoreCase(token.getText())) {
                        // TODO expressionBuilder.addOperatorToken(expressionBuilder.ROTATE_RIGHT);
                    }
                    else if ("><".equalsIgnoreCase(token.getText())) {
                        // TODO expressionBuilder.addOperatorToken(expressionBuilder.REVERSE);
                    }
                    else if ("&".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.AND);
                    }
                    else if ("|".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.OR);
                    }
                    else if ("^".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.XOR);
                    }
                    else if ("AND".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.LOGICAL_AND);
                    }
                    else if ("OR".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.LOGICAL_OR);
                    }
                    else if ("XOR".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.LOGICAL_XOR);
                    }
                    else if ("==".equalsIgnoreCase(token.getText())) {
                        // TODO expressionBuilder.addOperatorToken(expressionBuilder.EQUAL);
                    }
                    else if ("<>".equalsIgnoreCase(token.getText())) {
                        // TODO expressionBuilder.addOperatorToken(expressionBuilder.NOT_EQUAL);
                    }
                    else if ("<".equalsIgnoreCase(token.getText())) {
                        // TODO expressionBuilder.addOperatorToken(expressionBuilder.LESS_THAN);
                    }
                    else if (">".equalsIgnoreCase(token.getText())) {
                        // TODO expressionBuilder.addOperatorToken(expressionBuilder.GREAT_THAN);
                    }
                    else if ("=<".equalsIgnoreCase(token.getText())) {
                        // TODO expressionBuilder.addOperatorToken(expressionBuilder.LESS_OR_EQUAL);
                    }
                    else if ("=>".equalsIgnoreCase(token.getText())) {
                        // TODO expressionBuilder.addOperatorToken(expressionBuilder.GREAT_OR_EQUAL);
                    }
                    else if ("?".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.TERNARYIF);
                    }
                    else if (":".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.TERNARYELSE);
                    }
                    else if (token.type == Token.NUMBER) {
                        if ("$".equals(token.getText())) {
                            expressionBuilder.addValueToken(new Identifier(token.getText(), scope));
                        }
                        else {
                            expressionBuilder.addValueToken(new NumberLiteral(token.getText()));
                        }
                    }
                    else if (token.type == Token.STRING) {
                        expressionBuilder.addValueToken(new CharacterLiteral(token.getText().charAt(1)));
                    }
                    else {
                        expressionBuilder.addValueToken(new Identifier(token.getText(), scope));
                    }
                    state = 0;
                    break;
            }
        }

        return expressionBuilder.getExpression();
    }

}
