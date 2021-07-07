/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.maccasoft.propeller.expressions.CharacterLiteral;
import com.maccasoft.propeller.expressions.ContextLiteral;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.ExpressionBuilder;
import com.maccasoft.propeller.expressions.HubContextLiteral;
import com.maccasoft.propeller.expressions.Identifier;
import com.maccasoft.propeller.expressions.LocalVariable;
import com.maccasoft.propeller.expressions.Method;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.expressions.Register;
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
import com.maccasoft.propeller.model.ParameterNode;
import com.maccasoft.propeller.model.StatementNode;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.VariableNode;
import com.maccasoft.propeller.model.VariablesNode;
import com.maccasoft.propeller.spin2.Spin2Bytecode.Descriptor;
import com.maccasoft.propeller.spin2.Spin2Object.LongDataObject;
import com.maccasoft.propeller.spin2.bytecode.Bytecode;
import com.maccasoft.propeller.spin2.bytecode.CaseJmp;
import com.maccasoft.propeller.spin2.bytecode.Constant;
import com.maccasoft.propeller.spin2.bytecode.Djnz;
import com.maccasoft.propeller.spin2.bytecode.Jmp;
import com.maccasoft.propeller.spin2.bytecode.Jnz;
import com.maccasoft.propeller.spin2.bytecode.Jz;
import com.maccasoft.propeller.spin2.bytecode.MathOp;
import com.maccasoft.propeller.spin2.bytecode.MemoryOp;
import com.maccasoft.propeller.spin2.bytecode.VariableOp;
import com.maccasoft.propeller.spin2.instructions.Org;
import com.maccasoft.propeller.spin2.instructions.Orgh;

public class Spin2Compiler {

    Spin2Context scope = new Spin2GlobalContext();
    List<Spin2PAsmLine> source = new ArrayList<Spin2PAsmLine>();

    List<Spin2Method> methods = new ArrayList<Spin2Method>();
    List<Spin2Bytecode> bytecodeSource = new ArrayList<Spin2Bytecode>();

    ExpressionBuilder expressionBuilder = new ExpressionBuilder();

    int varOffset = 4;
    int labelCounter;

    public Spin2Compiler() {

    }

    public Spin2Object compile(Node root) {
        boolean hubMode = false;
        int address = 0, hubAddress = 0;
        Spin2Object object = new Spin2Object();

        for (Node node : root.getChilds()) {
            if (node instanceof ConstantsNode) {
                compileConBlock(node);
            }
        }

        for (Node node : root.getChilds()) {
            if (node instanceof VariablesNode) {
                compileVarBlock(node);
            }
        }

        int _clkfreq = 20000000;
        if (scope.hasSymbol("_clkfreq")) {
            _clkfreq = scope.getSymbol("_clkfreq").getNumber().intValue();
        }
        else if (scope.hasSymbol("_CLKFREQ")) {
            _clkfreq = scope.getSymbol("_CLKFREQ").getNumber().intValue();
        }

        if (!scope.hasSymbol("_clkfreq")) {
            scope.addSymbol("_clkfreq", new NumberLiteral(_clkfreq));
        }
        if (!scope.hasSymbol("_CLKFREQ")) {
            scope.addSymbol("_CLKFREQ", new NumberLiteral(_clkfreq));
        }

        int _clkmode = getClockMode(20000000, _clkfreq);
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

        for (Node node : root.getChilds()) {
            if ((node instanceof MethodNode) && "PUB".equalsIgnoreCase(((MethodNode) node).getType().getText())) {
                Spin2Method method = compileMethod((MethodNode) node);

                scope.addSymbol(method.getLabel(), new Method(method.getLabel(), method.getParametersCount(), method.getReturnsCount(), methods.size()));
                method.register();

                methods.add(method);
            }
        }

        for (Node node : root.getChilds()) {
            if ((node instanceof MethodNode) && "PRI".equalsIgnoreCase(((MethodNode) node).getType().getText())) {
                Spin2Method method = compileMethod((MethodNode) node);

                scope.addSymbol(method.getLabel(), new Method(method.getLabel(), method.getParametersCount(), method.getReturnsCount(), methods.size()));
                method.register();

                methods.add(method);
            }
        }

        for (Spin2Method method : methods) {
            for (Spin2MethodLine line : method.getLines()) {
                compileLine(line);
            }
        }

        if (methods.size() != 0) {
            hubAddress = (methods.size() + 1) * 4;
        }

        for (Spin2PAsmLine line : source) {
            try {
                line.getScope().setHubAddress(hubAddress);
                if (line.getInstructionFactory() instanceof Orgh) {
                    hubMode = true;
                }
                if (line.getInstructionFactory() instanceof Org) {
                    hubMode = false;
                    hubAddress = (hubAddress + 3) & ~3;
                }
                address = line.resolve(hubMode ? hubAddress : address);
                if (hubMode) {
                    hubAddress += line.getInstructionObject().getSize();
                    if (address > hubAddress) {
                        hubAddress = address;
                    }
                    else {
                        address = hubAddress;
                    }
                }
                else {
                    hubAddress += line.getInstructionObject().getSize();
                }
            } catch (Exception e) {
                System.err.println(line);
                e.printStackTrace();
            }
        }

        LongDataObject[] ld = new LongDataObject[methods.size() + 1];

        if (methods.size() != 0) {
            int index = 0;
            for (Spin2Method method : methods) {
                ld[index] = object.writeLong(0, "Method " + method.getLabel());
                index++;
            }
            ld[index] = object.writeLong(0, "End");
        }

        for (Spin2PAsmLine line : source) {
            hubAddress = line.getScope().getHubAddress();
            if (object.getSize() < hubAddress) {
                object.writeBytes(new byte[hubAddress - object.getSize()], "(filler)");
            }
            object.writeBytes(line.getScope().getAddress(), line.getInstructionObject().getBytes(), line.toString());
        }
        object.alignToLong();

        if (methods.size() != 0) {
            address = object.getSize();
            for (Spin2Method method : methods) {
                address++;
                address = method.resolve(address);
            }

            address = object.getSize();
            for (Spin2Method method : methods) {
                address++;
                address = method.resolve(address);
            }

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

            Spin2Interpreter interpreter = new Spin2Interpreter();
            interpreter.setVBase(interpreter.getPBase() + object.getSize());
            interpreter.setDBase(interpreter.getPBase() + object.getSize() + varOffset);
            object.setInterpreter(interpreter);
        }

        return object;
    }

    void compileConBlock(Node parent) {

        parent.accept(new NodeVisitor() {
            int enumValue = 0, enumIncrement = 1;

            @Override
            public void visitConstantAssign(ConstantAssignNode node) {
                String name = node.identifier.getText();
                try {
                    Expression expression = buildExpression(node.expression, scope);
                    scope.addSymbol(name, expression);
                } catch (Exception e) {
                    for (Token t : node.expression.getTokens()) {
                        System.err.print(" " + t.getText());
                    }
                    System.err.println();
                    throw e;
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
                scope.addSymbol(node.identifier.getText(), new NumberLiteral(enumValue));
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

        for (Node child : parent.getChilds()) {
            VariableNode node = (VariableNode) child;

            String type = "LONG";
            if (node.type != null) {
                type = node.type.getText().toUpperCase();
            }
            Expression size = new NumberLiteral(1);
            if (node.size != null) {
                size = buildExpression(node.size.getTokens(), scope);
            }
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
        }
    }

    void compileDatBlock(Node parent) {
        Spin2Context savedContext = scope;

        for (Node child : parent.getChilds()) {
            DataLineNode node = (DataLineNode) child;
            Spin2PAsmLine pasmLine = compileDataLine(node);
            source.addAll(pasmLine.expand());
        }

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
                        for (Token t : param.getTokens().subList(index, param.getTokens().size())) {
                            System.err.print(" " + t.getText());
                        }
                        System.err.println();
                        throw e;
                    }
                }
            }
            if (param.count != null) {
                try {
                    count = buildExpression(param.count, localScope);
                } catch (Exception e) {
                    for (Token t : param.count.getTokens()) {
                        System.err.print(" " + t.getText());
                    }
                    System.err.println();
                    throw e;
                }
            }
            parameters.add(new Spin2PAsmExpression(prefix, expression, count));
        }

        Spin2PAsmLine pasmLine = new Spin2PAsmLine(localScope, label, condition, mnemonic, parameters, modifier);
        if (pasmLine.getLabel() != null) {
            try {
                if (!pasmLine.isLocalLabel() && scope.getParent() != null) {
                    scope = scope.getParent();
                }
                scope.addSymbol(pasmLine.getLabel(), new ContextLiteral(pasmLine.getScope()));
                scope.addSymbol("@" + pasmLine.getLabel(), new HubContextLiteral(pasmLine.getScope()));
                if (!pasmLine.isLocalLabel()) {
                    scope = new Spin2Context(scope);
                }
            } catch (RuntimeException e) {
                System.err.println(pasmLine);
                e.printStackTrace();
            }
        }

        return pasmLine;
    }

    Spin2Method compileMethod(MethodNode node) {
        Spin2Context localScope = new Spin2Context(scope);
        List<LocalVariable> parameters = new ArrayList<LocalVariable>();
        List<LocalVariable> returns = new ArrayList<LocalVariable>();
        List<LocalVariable> localVariables = new ArrayList<LocalVariable>();

        //print(node, 0);

        int offset = 0;
        for (Node child : node.getParameters()) {
            LocalVariable var = new LocalVariable("LONG", child.getText(), new NumberLiteral(1), offset);
            localScope.addSymbol(child.getText(), var);
            localScope.addSymbol("@" + child.getText(), var);
            parameters.add(var);
            offset += 4;
        }

        for (Node child : node.getReturnVariables()) {
            LocalVariable var = new LocalVariable("LONG", child.getText(), new NumberLiteral(1), offset);
            localScope.addSymbol(child.getText(), var);
            localScope.addSymbol("@" + child.getText(), var);
            returns.add(var);
            offset += 4;
        }

        for (LocalVariableNode child : node.getLocalVariables()) {
            String type = "LONG";
            if (child.type != null) {
                type = child.type.getText().toUpperCase();
            }
            Expression size = null;
            if (child.size != null) {
                size = buildExpression(child.size.getTokens(), scope);
                if (!size.isConstant()) {
                    throw new RuntimeException("expression is not constant");
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

        Spin2Method method = new Spin2Method(localScope, node.name.getText(), parameters, returns, localVariables);
        method.setComment(node.getText());

        List<Spin2MethodLine> childs = compileStatements(localScope, node.getChilds());
        childs.add(new Spin2MethodLine(localScope, String.format(".label_" + labelCounter++), "RETURN", Collections.emptyList()));
        for (Spin2MethodLine line : childs) {
            method.addSource(line);
        }

        return method;
    }

    void print(Spin2MethodLine line) {
        System.out.println(line);
        for (Spin2StatementNode arg : line.getArguments()) {
            print(arg, 1);
        }
        for (Spin2MethodLine child : line.getChilds()) {
            print(child);
        }
    }

    List<Spin2MethodLine> compileStatements(Spin2Context context, List<Node> childs) {
        List<Spin2MethodLine> lines = new ArrayList<Spin2MethodLine>();

        for (Node child : childs) {
            if (child instanceof StatementNode) {
                Spin2MethodLine line = compileStatement(context, child);
                lines.add(line);
            }
        }

        Spin2MethodLine line = new Spin2MethodLine(context, String.format(".label_" + labelCounter++), null, Collections.emptyList());
        lines.add(line);

        int i = 0;
        while (i < lines.size() - 1) {
            String text = lines.get(i).getStatement();
            if ("IF".equalsIgnoreCase(text) || "IFNOT".equalsIgnoreCase(text) || "ELSEIF".equalsIgnoreCase(text) || "ELSEIFNOT".equalsIgnoreCase(text)) {
                lines.get(i).setData("false", lines.get(i + 1));
            }
            else if ("REPEAT".equalsIgnoreCase(text)) {
                String text2 = lines.get(i + 1).getStatement();
                if ("WHILE".equalsIgnoreCase(text2) || "UNTIL".equalsIgnoreCase(text2)) {
                    lines.get(i).setData("next", lines.get(i + 1));
                    lines.get(i).setData("quit", lines.get(i + 2));
                }
                else {
                    lines.get(i).setData("quit", lines.get(i + 1));

                    Spin2MethodLine nextLine;
                    if (lines.get(i).getArgumentsCount() == 1) {
                        if (lines.get(i).getChilds().size() != 0) {
                            nextLine = new Spin2MethodLine(context, String.format(".label_" + labelCounter++), "NEXT_DEC", Collections.emptyList());
                            nextLine.setData("next", lines.get(i).getChilds().get(0));
                        }
                        else {
                            nextLine = new Spin2MethodLine(context, String.format(".label_" + labelCounter++), "NEXT_DEC", Collections.emptyList());
                            nextLine.setData("next", nextLine);
                        }
                    }
                    else {
                        nextLine = new Spin2MethodLine(context, String.format(".label_" + labelCounter++), "NEXT", Collections.emptyList());
                        nextLine.setData("next", lines.get(i));
                    }
                    lines.add(i + 1, nextLine);
                }
            }
            else if ("REPEAT WHILE".equalsIgnoreCase(text) || "REPEAT UNTIL".equalsIgnoreCase(text)) {
                lines.get(i).setData("quit", lines.get(i + 1));
                Spin2MethodLine nextLine = new Spin2MethodLine(context, String.format(".label_" + labelCounter++), "NEXT", Collections.emptyList());
                nextLine.setData("next", lines.get(i));
                lines.add(i + 1, nextLine);
            }
            else if ("WHILE".equalsIgnoreCase(text) || "UNTIL".equalsIgnoreCase(text)) {
                lines.get(i).setData("true", lines.get(i - 1));
            }
            i++;
        }

        i = 0;
        while (i < lines.size() - 1) {
            String text = lines.get(i).getStatement();
            if ("IF".equalsIgnoreCase(text) || "IFNOT".equalsIgnoreCase(text) || "ELSEIF".equalsIgnoreCase(text) || "ELSEIFNOT".equalsIgnoreCase(text)) {
                int exitIndex = i;
                do {
                    exitIndex++;
                    text = lines.get(exitIndex).getStatement();
                } while ("ELSEIF".equalsIgnoreCase(text) || "ELSEIFNOT".equalsIgnoreCase(text) || "ELSE".equalsIgnoreCase(text));
                if (exitIndex != i + 1) {
                    lines.get(i).setData("exit", lines.get(exitIndex));
                }
            }
            i++;
        }

        return lines;
    }

    Spin2MethodLine compileStatement(Spin2Context context, Node node) {
        Spin2TreeBuilder builder = new Spin2TreeBuilder();
        Spin2MethodLine line;

        Iterator<Token> iter = node.getTokens().iterator();
        if (!iter.hasNext()) {
            throw new RuntimeException("syntax error");
        }

        Token token = iter.next();
        if ("ABORT".equalsIgnoreCase(token.getText())) {
            if (iter.hasNext()) {
                while (iter.hasNext()) {
                    builder.addToken(iter.next());
                }
                line = new Spin2MethodLine(context, String.format(".label_" + labelCounter++), token.getText(), builder.getRoot());
                line.setText(node.getText());
            }
            else {
                line = new Spin2MethodLine(context, String.format(".label_" + labelCounter++), token.getText(), Collections.emptyList());
                line.setText(node.getText());
            }
        }
        else if ("IF".equalsIgnoreCase(token.getText()) || "IFNOT".equalsIgnoreCase(token.getText()) || "ELSEIF".equalsIgnoreCase(token.getText()) || "ELSEIFNOT".equalsIgnoreCase(token.getText())) {
            if (!iter.hasNext()) {
                throw new RuntimeException("expected expression");
            }

            builder.setState(2);
            while (iter.hasNext()) {
                builder.addToken(iter.next());
            }

            line = new Spin2MethodLine(context, String.format(".label_" + labelCounter++), token.getText(), builder.getRoot());
            line.setText(node.getText());

            line.addChilds(compileStatements(context, node.getChilds()));
        }
        else if ("ELSE".equalsIgnoreCase(token.getText())) {
            if (iter.hasNext()) {
                throw new RuntimeException("syntax error");
            }
            line = new Spin2MethodLine(context, String.format(".label_" + labelCounter++), token.getText(), Collections.emptyList());
            line.setText(node.getText());

            line.addChilds(compileStatements(context, node.getChilds()));
        }
        else if ("REPEAT".equalsIgnoreCase(token.getText())) {
            List<Spin2StatementNode> arguments = new ArrayList<Spin2StatementNode>();

            String text = token.getText();

            if (iter.hasNext()) {
                token = iter.next();
                if ("WHILE".equalsIgnoreCase(token.getText()) || "UNTIL".equalsIgnoreCase(token.getText())) {
                    text += " " + token.getText();
                    builder.setState(2);
                    while (iter.hasNext()) {
                        builder.addToken(iter.next());
                    }
                    arguments.add(builder.getRoot());
                }
                else {
                    arguments.add(new Spin2StatementNode(token.type, token.getText()));
                }
            }

            line = new Spin2MethodLine(context, String.format(".label_" + labelCounter++), text, arguments);
            line.setText(node.getText());
            line.setData("next", line);

            line.addChilds(compileStatements(context, node.getChilds()));
        }
        else if ("WHILE".equalsIgnoreCase(token.getText()) || "UNTIL".equalsIgnoreCase(token.getText())) {
            if (!iter.hasNext()) {
                throw new RuntimeException("expected expression");
            }
            builder.setState(2);
            while (iter.hasNext()) {
                builder.addToken(iter.next());
            }
            line = new Spin2MethodLine(context, String.format(".label_" + labelCounter++), token.getText(), builder.getRoot());
            line.setText(node.getText());
        }
        else if ("QUIT".equalsIgnoreCase(token.getText()) || "NEXT".equalsIgnoreCase(token.getText())) {
            if (iter.hasNext()) {
                throw new RuntimeException("syntax error");
            }
            line = new Spin2MethodLine(context, String.format(".label_" + labelCounter++), token.getText(), Collections.emptyList());
            line.setText(node.getText());
        }
        else if ("CASE".equalsIgnoreCase(token.getText())) {
            List<Spin2StatementNode> arguments = new ArrayList<Spin2StatementNode>();
            line = new Spin2MethodLine(context, String.format(".label_" + labelCounter++), token.getText(), arguments);
            line.setText(node.getText());

            if (!iter.hasNext()) {
                throw new RuntimeException("expected expression");
            }
            builder.setState(2);
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

                    builder.setState(2);
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

                    Spin2MethodLine targetLine = new Spin2MethodLine(context, String.format(".label_" + labelCounter++), null, Collections.emptyList());
                    targetLine.addChilds(compileStatements(context, child.getChilds()));
                    targetLine.addChild(new Spin2MethodLine(context, String.format(".label_" + labelCounter++), "CASE_DONE", Collections.emptyList()));

                    Spin2StatementNode expression = builder.getRoot();
                    if ("OTHER".equalsIgnoreCase(expression.getText())) {
                        line.childs.add(0, targetLine);
                    }
                    else {
                        expression.setData("true", targetLine);
                        line.addChild(targetLine);
                        arguments.add(expression);
                    }
                }
            }

            Spin2MethodLine doneLine = new Spin2MethodLine(context, String.format(".label_" + labelCounter++), null, Collections.emptyList());
            line.addChild(doneLine);
            line.setData("end", doneLine);
        }
        else {
            iter = node.getTokens().iterator();
            while (iter.hasNext()) {
                builder.addToken(iter.next());
            }
            line = new Spin2MethodLine(context, String.format(".label_" + labelCounter++), null, builder.getRoot());
            line.setText(node.getText());
        }

        return line;
    }

    void compileLine(Spin2MethodLine line) {
        String text = line.getStatement();
        if ("ABORT".equalsIgnoreCase(text)) {
            if (line.getArgumentsCount() == 0) {
                line.addSource(new Bytecode(line.getScope(), 0x06, text));
            }
            else if (line.getArgumentsCount() == 1) {
                line.addSource(compileBytecodeExpression(line.getScope(), line.getArgument(0), true));
                line.addSource(new Bytecode(line.getScope(), 0x07, text));
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
            Spin2MethodLine target = (Spin2MethodLine) line.getData("false");
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
            Spin2MethodLine target = (Spin2MethodLine) line.getData("false");
            line.addSource(new Jz(line.getScope(), new Identifier(target.getLabel(), target.getScope())));
        }
        else if ("ELSE".equalsIgnoreCase(text)) {

        }
        else if ("REPEAT".equalsIgnoreCase(text)) {
            if (line.getArgumentsCount() == 1) {
                line.addSource(compileBytecodeExpression(line.getScope(), line.getArgument(0), true));
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
            Spin2MethodLine target = (Spin2MethodLine) line.getData("quit");
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
            Spin2MethodLine target = (Spin2MethodLine) line.getData("true");
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
            Spin2MethodLine target = (Spin2MethodLine) line.getData("true");
            line.addSource(new Jz(line.getScope(), new Identifier(target.getLabel(), target.getScope())));
        }
        else if ("NEXT".equalsIgnoreCase(text) || "NEXT_DEC".equalsIgnoreCase(text)) {
            Spin2MethodLine target = (Spin2MethodLine) line.getData("next");
            if (target == null) {
                Spin2MethodLine parent = line.getParent();
                while (parent != null) {
                    if (parent.getData("next") != null) {
                        target = (Spin2MethodLine) parent.getData("next");
                        break;
                    }
                    parent = parent.getParent();
                }
            }
            if (target != null) {
                if ("NEXT_DEC".equalsIgnoreCase(text)) {
                    line.addSource(new Djnz(line.getScope(), new Identifier(target.getLabel(), target.getScope())));
                }
                else {
                    line.addSource(new Jmp(line.getScope(), new Identifier(target.getLabel(), target.getScope())));
                }
            }
        }
        else if ("QUIT".equalsIgnoreCase(text)) {
            Spin2MethodLine parent = line.getParent();
            while (parent != null) {
                if (parent.getData("quit") != null) {
                    Spin2MethodLine target = (Spin2MethodLine) parent.getData("quit");
                    line.addSource(new Jmp(line.getScope(), new Identifier(target.getLabel(), target.getScope())));
                    break;
                }
                parent = parent.getParent();
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
            line.addSource(new Constant(line.getScope(), new Identifier(end.getLabel(), end.getScope())));

            for (Spin2StatementNode arg : line.getArguments()) {
                line.addSource(compileBytecodeExpression(line.getScope(), arg, false));
                Spin2MethodLine target = (Spin2MethodLine) arg.getData("true");
                if (target != null) {
                    line.addSource(new CaseJmp(line.getScope(), new Identifier(target.getLabel(), target.getScope())));
                }
            }
        }
        else if ("CASE_DONE".equalsIgnoreCase(text)) {
            line.addSource(new Bytecode(line.getScope(), 0x1E, text));
        }
        else if (text != null) {
            Descriptor desc = Spin2Bytecode.getDescriptor(text);
            if (desc != null) {
                if (line.getArgumentsCount() != desc.parameters) {
                    throw new RuntimeException("expected " + desc.parameters + " argument(s), found " + line.getArgumentsCount());
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
                line.addSource(compileBytecodeExpression(line.getScope(), arg, false));
            }
        }

        Spin2MethodLine target = (Spin2MethodLine) line.getData("exit");
        if (target != null) {
            Spin2MethodLine newLine = new Spin2MethodLine(line.getScope(), null, null, Collections.emptyList());
            newLine.addSource(new Jmp(line.getScope(), new Identifier(target.getLabel(), target.getScope())));
            line.addChild(newLine);
        }

        for (Spin2MethodLine child : line.getChilds()) {
            compileLine(child);
        }
    }

    List<Spin2Bytecode> compileBytecodeExpression(Spin2Context context, Spin2StatementNode node, boolean push) {
        List<Spin2Bytecode> source = new ArrayList<Spin2Bytecode>();

        Spin2StatementNode argsNode = node;
        if (node.getChildCount() == 1 && ",".equals(node.getChild(0).getText())) {
            argsNode = node.getChild(0);
        }

        Descriptor desc = Spin2Bytecode.getDescriptor(node.getText());
        if (desc != null) {
            if (argsNode.getChildCount() != desc.parameters) {
                throw new RuntimeException("expected " + desc.parameters + " argument(s), found " + argsNode.getChildCount());
            }
            for (int i = 0; i < desc.parameters; i++) {
                source.addAll(compileBytecodeExpression(context, argsNode.getChild(i), true));
            }
            source.add(new Bytecode(context, desc.code, node.getText()));
        }
        else if (node.type == Token.NUMBER) {
            Expression expression = new NumberLiteral(node.getText());
            source.add(new Constant(context, expression));
        }
        else if (":=".equals(node.getText())) {
            source.addAll(compileBytecodeExpression(context, node.getChild(1), push));
            if (node.getChild(0).type == Token.OPERATOR) {
                source.addAll(compileBytecodeExpression(context, node.getChild(0), push));
            }
            else {
                Expression expression = context.getLocalSymbol(node.getChild(0).getText());
                if (expression instanceof Register) {
                    throw new RuntimeException("unhandled register expression");
                }
                else if ((expression instanceof Variable) || (expression instanceof LocalVariable)) {
                    source.add(new VariableOp(context, VariableOp.Op.Write, (Variable) expression));
                }
                else {
                    throw new RuntimeException("unknown: " + node.getText());
                }
            }
        }
        else if (MathOp.isAssignMathOp(node.getText())) {
            source.addAll(compileBytecodeExpression(context, node.getChild(1), true));
            if (node.getChild(0).type == Token.OPERATOR) {
                source.addAll(compileBytecodeExpression(context, node.getChild(0), true));
            }
            else {
                Expression expression = context.getLocalSymbol(node.getChild(0).getText());
                if (expression instanceof Register) {
                    throw new RuntimeException("unhandled register expression");
                }
                else if ((expression instanceof Variable) || (expression instanceof LocalVariable)) {
                    source.add(new VariableOp(context, VariableOp.Op.Setup, (Variable) expression));
                }
                else {
                    throw new RuntimeException("unknown: " + node.getText());
                }
            }
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
        else if ("ABORT".equalsIgnoreCase(node.getText())) {
            if (argsNode.getChildCount() == 0) {
                source.add(new Bytecode(context, 0x06, node.getText().toUpperCase()));
            }
            else if (argsNode.getChildCount() == 1) {
                source.addAll(compileBytecodeExpression(context, argsNode.getChild(0), true));
                source.add(new Bytecode(context, 0x07, node.getText().toUpperCase()));
            }
            else {
                throw new RuntimeException("expected 0 or 1 argument(s), found " + node.getChildCount());
            }
        }
        else if ("RECV".equalsIgnoreCase(node.getText())) {
            if (argsNode.getChildCount() != 0) {
                throw new RuntimeException("expected " + 0 + " argument(s), found " + node.getChildCount());
            }
            source.add(new Bytecode(context, 0x0C, node.getText().toUpperCase()));
        }
        else if ("SEND".equalsIgnoreCase(node.getText())) {
            if (argsNode.getChildCount() == 0) {
                throw new RuntimeException("syntax error");
            }
            boolean bytes = true;
            for (Spin2StatementNode child : argsNode.getChilds()) {
                if (child.getType() != Token.NUMBER) {
                    bytes = false;
                    break;
                }
            }
            if (bytes) {
                byte[] code = new byte[argsNode.getChildCount() + 2];
                code[0] = 0x0E;
                code[1] = (byte) argsNode.getChildCount();
                for (int i = 0; i < argsNode.getChildCount(); i++) {
                    code[i + 2] = (byte) new NumberLiteral(argsNode.getChild(i).getText()).getNumber().intValue();
                }
                source.add(new Bytecode(context, code, node.getText().toUpperCase()));
            }
            else {
                for (Spin2StatementNode child : argsNode.getChilds()) {
                    source.addAll(compileBytecodeExpression(context, child, true));
                    source.add(new Bytecode(context, 0x0D, node.getText().toUpperCase()));
                }
            }
        }
        else if ("DEBUG".equalsIgnoreCase(node.getText())) {
            // Ignored
        }
        else {
            Expression expression = context.getLocalSymbol(node.getText());
            if (expression == null) {
                throw new RuntimeException("unknown " + node.getText());
            }
            if (expression instanceof Register) {
                throw new RuntimeException("unhandled register expression");
            }
            else if (expression instanceof Method) {
                int parameters = ((Method) expression).getArgumentsCount();
                if (argsNode.getChildCount() != parameters) {
                    throw new RuntimeException("expected " + parameters + " argument(s), found " + argsNode.getChildCount());
                }
                for (int i = 0; i < parameters; i++) {
                    source.addAll(compileBytecodeExpression(context, argsNode.getChild(i), true));
                }
                source.add(new Bytecode(context, new byte[] {
                    (byte) ((Method) expression).getReturnsCount(),
                    (byte) 0x0A,
                    (byte) ((Method) expression).getOffset()
                }, "CALL_SUB (" + ((Method) expression).getOffset() + ")"));
            }
            else if (node.getText().startsWith("@")) {
                MemoryOp.Size ss = MemoryOp.Size.Long;
                MemoryOp.Base bb = MemoryOp.Base.PBase;
                if (expression instanceof LocalVariable) {
                    bb = MemoryOp.Base.DBase;
                }
                else if (expression instanceof Variable) {
                    bb = MemoryOp.Base.VBase;
                }
                source.add(new MemoryOp(context, ss, bb, MemoryOp.Op.Address, expression));
            }
            else if ((expression instanceof Variable) || (expression instanceof LocalVariable)) {
                source.add(new VariableOp(context, VariableOp.Op.Read, (Variable) expression));
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
                source.add(new MemoryOp(context, ss, bb, MemoryOp.Op.Read, expression));
            }
            else {
                source.add(new Constant(context, expression));
            }
        }

        return source;
    }

    int getClockMode(int xinfreq, int clkfreq) {
        int divd;
        int zzzz = 11; // 0b10_11
        double e, post, mult, Fpfd, Fvco, Fout;
        double result_mult = 0;
        //double result_Fout = 0;
        int result_pppp = 0, result_divd = 0;
        double error = 1e9;

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

    Expression buildExpression(Node node, Spin2Context scope) {
        return buildExpression(node.getTokens(), scope);
    }

    Expression buildExpression(List<Token> tokens, Spin2Context scope) {
        int state = 0;
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
                    if ("!".equals(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.NOT);
                    }
                    else if ("!!".equals(token.getText()) || "NOT".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.LOGICAL_NOT);
                    }
                    else if ("+".equals(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.POSITIVE);
                    }
                    else if ("-".equals(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.NEGATIVE);
                    }
                    else if ("ABS".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.ABS);
                    }
                    else if ("ENCOD".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.ENCOD);
                    }
                    else if ("DECOD".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.DECOD);
                    }
                    else if ("BMASK".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.BMASK);
                    }
                    else if ("SQRT".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.SQRT);
                    }
                    else if ("ROUND".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.ROUND);
                    }
                    else if (token.type == Token.NUMBER) {
                        if ("$".equals(token.getText())) {
                            expressionBuilder.addValueToken(new Identifier(token.getText(), scope));
                        }
                        else {
                            expressionBuilder.addValueToken(new NumberLiteral(token.getText()));
                        }
                    }
                    else if (token.getText().startsWith("\"")) {
                        expressionBuilder.addValueToken(new CharacterLiteral(token.getText().charAt(1)));
                    }
                    else {
                        expressionBuilder.addValueToken(new Identifier(token.getText(), scope));
                    }
                    state = 1;
                    break;
                case 1:
                    if ("!".equals(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.NOT);
                    }
                    else if ("!!".equals(token.getText()) || "NOT".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.LOGICAL_NOT);
                    }
                    else if ("ABS".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.ABS);
                    }
                    else if ("ENCOD".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.ENCOD);
                    }
                    else if ("DECOD".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.DECOD);
                    }
                    else if ("BMASK".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.BMASK);
                    }
                    else if ("SQRT".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.SQRT);
                    }
                    else if (">>".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.SHIFT_RIGHT);
                    }
                    else if ("<<".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.SHIFT_LEFT);
                    }
                    else if ("&".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.AND);
                    }
                    else if ("^".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.XOR);
                    }
                    else if ("|".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.OR);
                    }
                    else if ("*".equals(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.MULTIPLY);
                    }
                    else if ("/".equals(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.DIVIDE);
                    }
                    else if ("//".equals(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.MODULO);
                    }
                    else if ("+/".equals(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.UNSIGNED_DIVIDE);
                    }
                    else if ("+//".equals(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.UNSIGNED_MODULO);
                    }
                    else if ("SCA".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.SCA);
                    }
                    else if ("SCAS".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.SCAS);
                    }
                    else if ("FRAC".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.FRAC);
                    }
                    else if ("+".equals(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.ADD);
                    }
                    else if ("-".equals(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.SUBTRACT);
                    }
                    else if ("ADDBITS".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.ADDBITS);
                    }
                    else if ("ADDPINS".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.ADDPINS);
                    }
                    else if ("&&".equalsIgnoreCase(token.getText()) || "AND".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.LOGICAL_AND);
                    }
                    else if ("^^".equalsIgnoreCase(token.getText()) || "XOR".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.LOGICAL_XOR);
                    }
                    else if ("||".equalsIgnoreCase(token.getText()) || "OR".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.LOGICAL_OR);
                    }
                    else if ("?".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.TERNARYIF);
                    }
                    else if (":".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.TERNARYELSE);
                    }
                    else if ("ROUND".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.ROUND);
                    }
                    else if (token.type == Token.NUMBER) {
                        if ("$".equals(token.getText())) {
                            expressionBuilder.addValueToken(new Identifier(token.getText(), scope));
                        }
                        else {
                            expressionBuilder.addValueToken(new NumberLiteral(token.getText()));
                        }
                    }
                    else if (token.getText().startsWith("\"")) {
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

    public static void main(String[] args) {
        String text1 = ""
            + "CON\n"
            + "    _clkfreq = 160_000_000\n"
            + "\n"
            + "PUB main() | ct\n"
            + "\n"
            + "    ct := getct()                   ' get current timer\n"
            + "    repeat\n"
            + "        pint(56)                    ' toggle pin 56\n"
            + "        waitct(ct += _clkfreq / 2)  ' wait half second\n"
            + "";
        String ifelse_text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    if a == 1\n"
            + "        a := 2\n"
            + "    elseif a == 3\n"
            + "        a := 4\n"
            + "    elseif a == 5\n"
            + "        a := 6\n"
            + "    else\n"
            + "        a := 7\n"
            + "\n"
            + "";
        String text3 = ""
            + "PUB main() | a\n"
            + "\n"
            + "    repeat while a > 1\n"
            + "        a := 2\n"
            + "    a := loop()\n"
            + "\n"
            + "\n"
            + "PUB loop() : r | a\n"
            + "\n"
            + "    repeat \n"
            + "        a := 2\n"
            + "    until a > 1\n"
            + "\n"
            + "";
        String text2 = ""
            + "PUB main() | a, b\n"
            + "\n"
            + "    'if a == 1\n"
            + "    '    a := 2\n"
            + "    '    if a == 5\n"
            + "    '        a := 6\n"
            + "    a := 1\n"
            + "    b := a + b * 3\n"
            + "\n"
            + "    if a == 1\n"
            + "        a := 2\n"
            + "    elseif a == 3\n"
            + "        a := 4\n"
            + "    elseif a == 5\n"
            + "        a := 6\n"
            + "    else\n"
            + "        a := 7\n"
            + "\n"
            + "    'case a\n"
            + "    '    1: a := 2\n"
            + "    '       if a == 5\n"
            + "    '           a := 6\n"
            + "    '    2: a := 3\n"
            + "    '    3: a := 4\n"
            + "    '    other: a := 5\n"
            + "\n"
            + "    'if a == 5\n"
            + "    '    a := 6\n"
            + "";
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    a := RECV()\n"
            + "    SEND(a)\n"
            + "    SEND(1,2,3)\n"
            + "    SEND(a,2,3)\n"
            + "\n"
            + "";

        try {
            Spin2TokenStream stream = new Spin2TokenStream(text);
            Spin2Parser subject = new Spin2Parser(stream);
            Node root = subject.parse();

            Spin2Compiler compiler = new Spin2Compiler();
            Spin2Object obj = compiler.compile(root);

            obj.generateListing(System.out);

            //FileOutputStream os = new FileOutputStream("test.binary");
            //binary.generateBinary(os);
            //os.close();

            //System.out.println(String.format("PBASE: $%05X", interpreter.getPBase()));
            //System.out.println(String.format("VBASE: $%05X", interpreter.getVBase()));
            //System.out.println(String.format("DBASE: $%05X", interpreter.getDBase()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void print(Node node, int indent) {
        if (indent != 0) {
            for (int i = 1; i < indent; i++) {
                System.out.print("|    ");
            }
            System.out.print("+--- ");
        }

        System.out.print(node.getClass().getSimpleName());
        //System.out.print(" [" + node.getText().replaceAll("\n", "\\\\n") + "]");
        for (Token token : node.getTokens()) {
            System.out.print(" [" + token.getText() + "]");
        }
        System.out.println();

        for (Node child : node.getChilds()) {
            print(child, indent + 1);
        }
    }

    static void print(Spin2StatementNode node, int indent) {
        if (indent != 0) {
            for (int i = 1; i < indent; i++) {
                System.out.print("|    ");
            }
            System.out.print("+--- ");
        }

        System.out.print(node.getClass().getSimpleName());
        System.out.print(" [" + node.getText().replaceAll("\n", "\\\\n") + "] " + node.getPropertiesText());
        if (node.getData("true") != null) {
            Spin2MethodLine line = (Spin2MethodLine) node.getData("true");
            System.out.print(" true -> " + line.getLabel());
        }
        if (node.getData("false") != null) {
            Spin2MethodLine line = (Spin2MethodLine) node.getData("false");
            System.out.print(" false -> " + line.getLabel());
        }
        System.out.println();

        for (Spin2StatementNode child : node.getChilds()) {
            print(child, indent + 1);
        }
    }

}
