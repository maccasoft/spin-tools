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

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.maccasoft.propeller.expressions.CharacterLiteral;
import com.maccasoft.propeller.expressions.ContextLiteral;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.ExpressionBuilder;
import com.maccasoft.propeller.expressions.HubContextLiteral;
import com.maccasoft.propeller.expressions.Identifier;
import com.maccasoft.propeller.expressions.LocalVariable;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.expressions.Register;
import com.maccasoft.propeller.expressions.Variable;
import com.maccasoft.propeller.model.ConstantAssignEnumNode;
import com.maccasoft.propeller.model.ConstantAssignNode;
import com.maccasoft.propeller.model.ConstantSetEnumNode;
import com.maccasoft.propeller.model.ConstantsNode;
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.LocalVariableNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.NodeVisitor;
import com.maccasoft.propeller.model.ParameterNode;
import com.maccasoft.propeller.model.StatementNode;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.VariableNode;
import com.maccasoft.propeller.model.VariablesNode;
import com.maccasoft.propeller.spin2.Spin2Object.LongDataObject;
import com.maccasoft.propeller.spin2.bytecode.Bytecode;
import com.maccasoft.propeller.spin2.bytecode.Constant;
import com.maccasoft.propeller.spin2.bytecode.Djnz;
import com.maccasoft.propeller.spin2.bytecode.Jmp;
import com.maccasoft.propeller.spin2.bytecode.Jz;
import com.maccasoft.propeller.spin2.bytecode.MathOp;
import com.maccasoft.propeller.spin2.bytecode.Tjz;
import com.maccasoft.propeller.spin2.bytecode.VariableOp;
import com.maccasoft.propeller.spin2.instructions.Org;
import com.maccasoft.propeller.spin2.instructions.Orgh;

public class Spin2Compiler {

    Spin2Context scope = new Spin2GlobalContext();
    List<Spin2PAsmLine> source = new ArrayList<Spin2PAsmLine>();

    List<Spin2Method> methods = new ArrayList<Spin2Method>();

    ExpressionBuilder expressionBuilder = new ExpressionBuilder();

    int varOffset = 4;

    final NodeVisitor compilerVisitor = new NodeVisitor() {

        int enumValue = 0, enumIncrement = 1;

        @Override
        public void visitConstants(ConstantsNode node) {
            while (scope.getParent() != null) {
                scope = scope.getParent();
            }
            enumValue = 0;
            enumIncrement = 1;
        }

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

        @Override
        public void visitDataLine(DataLineNode node) {
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
            source.addAll(pasmLine.expand());
        }

    };

    public Spin2Compiler() {

    }

    public void generateObjectCode(OutputStream os) throws Exception {
        int address = 0;

        for (Spin2PAsmLine line : source) {
            while (address < line.getScope().getHubAddress()) {
                os.write(0x00);
                address++;
            }
            Spin2InstructionObject obj = line.getInstructionObject();
            byte[] code = obj.getBytes();
            if (code != null && code.length != 0) {
                os.write(code);
                address += code.length;
            }
        }
    }

    public Spin2Object compile(Node root) {
        boolean hubMode = false;
        int address = 0, hubAddress = 0;
        Spin2Object object = new Spin2Object();

        root.accept(compilerVisitor);

        for (Node node : root.getChilds()) {
            if (node instanceof VariablesNode) {
                compileVarBlock(node);
            }
        }

        while (scope.getParent() != null) {
            scope = scope.getParent();
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

        for (Node node : root.getChilds()) {
            if (node instanceof MethodNode) {
                compileMethodBlock((MethodNode) node);
            }
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

        int index = 0;
        for (Spin2Method method : methods) {
            ld[index] = object.writeLong(0, "Method " + method.getLabel());
            index++;
        }
        ld[index] = object.writeLong(0, "End");

        for (Spin2PAsmLine line : source) {
            hubAddress = line.getScope().getHubAddress();
            if (object.getSize() < hubAddress) {
                object.writeBytes(new byte[hubAddress - object.getSize()], "(filler)");
            }
            object.writeBytes(line.getScope().getAddress(), line.getInstructionObject().getBytes(), line.toString());
        }
        object.alignToLong();

        boolean repeat = true;
        while (repeat) {
            repeat = false;
            address = object.getSize();

            for (Spin2Method method : methods) {
                address++;
                for (Spin2Bytecode bc : method.getSource()) {
                    if (bc.getContext().isAddressSet()) {
                        if (address != bc.getContext().getAddress()) {
                            repeat = true;
                        }
                    }
                    bc.resolve(address);
                    if (!bc.isFixedSize()) {
                        repeat = true;
                    }
                    address += bc.getSize();
                }
            }
        }

        index = 0;
        for (Spin2Method method : methods) {
            int value = 0;

            value = Spin2Method.address_bit.setValue(value, object.getSize());
            value = Spin2Method.returns_bit.setValue(value, method.getReturnsCount());
            value = Spin2Method.parameters_bit.setValue(value, method.getParametersCount());

            ld[index].setValue(value | 0x80000000L);
            ld[index].setText(String.format("Method %s @ $%05X (%d parameters, %d returns)", method.getLabel(), object.getSize(), method.getParametersCount(), method.getReturnsCount()));
            object.writeBytes(new byte[0], "Method " + method.getLabel());
            method.writeTo(object);
            index++;
        }
        ld[index].setValue(object.getSize());

        object.alignToLong();

        return object;
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

    void compileMethodBlock(MethodNode node) {
        Spin2Context localScope = new Spin2Context(scope);
        List<LocalVariable> parameters = new ArrayList<LocalVariable>();
        List<LocalVariable> returns = new ArrayList<LocalVariable>();
        List<LocalVariable> localVariables = new ArrayList<LocalVariable>();

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
                    throw new RuntimeException("error: expression is not constant");
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

        Spin2StatementNode root = new Spin2StatementNode(0, "RETURN");
        for (Node child : node.getChilds()) {
            if (child instanceof StatementNode) {
                compileStatementBlock(root, child);
            }
        }
        print(root, 0);

        compileBytecodeStatement(method, root, false);

        methods.add(method);
    }

    void compileStatementBlock(Spin2StatementNode parent, Node node) {
        Spin2TreeBuilder builder = new Spin2TreeBuilder();

        int index = 0;
        List<Token> tokens = node.getTokens();

        Spin2StatementNode statementNode;
        if ("IF".equalsIgnoreCase(tokens.get(index).getText())) {
            statementNode = new Spin2StatementNode(0, tokens.get(index++).getText());

            builder.setState(2);
            while (index < tokens.size()) {
                Token token = tokens.get(index++);
                builder.addToken(token);
            }
            statementNode.addChild(builder.getRoot());
            parent.addChild(statementNode);
        }
        else if ("REPEAT".equalsIgnoreCase(tokens.get(index).getText())) {
            statementNode = new Spin2StatementNode(0, tokens.get(index++).getText());
            while (index < tokens.size()) {
                Token token = tokens.get(index++);
                builder.addToken(token);
            }
            if (index > 1) {
                statementNode.setProperty("counter", builder.getRoot());
            }
            parent.addChild(statementNode);
        }
        else {
            while (index < tokens.size()) {
                Token token = tokens.get(index++);
                builder.addToken(token);
            }

            statementNode = builder.getRoot();
            parent.addChild(statementNode);
        }

        for (Node child : node.getChilds()) {
            if (child instanceof StatementNode) {
                compileStatementBlock(statementNode, child);
            }
        }
    }

    static class BytecodeDescriptor {
        int code;
        int parameters;

        public BytecodeDescriptor(int code, int parameters) {
            this.code = code;
            this.parameters = parameters;
        }
    }
    static Map<String, BytecodeDescriptor> descriptors = new HashMap<String, BytecodeDescriptor>();
    static {
        descriptors.put("HUBSET", new BytecodeDescriptor(0x54, 1));
        descriptors.put("CLKSET", new BytecodeDescriptor(0x56, 2));
        descriptors.put("CLKFREQ", new BytecodeDescriptor(0x58, 0));
        descriptors.put("COGSPIN", new BytecodeDescriptor(0x5A, 3));
        descriptors.put("REGEXEC", new BytecodeDescriptor(0x5E, 1));
        descriptors.put("REGLOAD", new BytecodeDescriptor(0x60, 1));
        descriptors.put("CALL", new BytecodeDescriptor(0x62, 1));
        descriptors.put("GETREGS", new BytecodeDescriptor(0x64, 3));
        descriptors.put("SETREGS", new BytecodeDescriptor(0x66, 3));
        descriptors.put("BYTEMOVE", new BytecodeDescriptor(0x68, 3));
        descriptors.put("BYTEFILL", new BytecodeDescriptor(0x6A, 3));
        descriptors.put("WORDMOVE", new BytecodeDescriptor(0x6C, 3));
        descriptors.put("WORDFILL", new BytecodeDescriptor(0x6E, 3));
        descriptors.put("LONGMOVE", new BytecodeDescriptor(0x70, 3));
        descriptors.put("LONGFILL", new BytecodeDescriptor(0x72, 3));
        descriptors.put("STRSIZE", new BytecodeDescriptor(0x74, 1));
        descriptors.put("STRCOMP", new BytecodeDescriptor(0x76, 2));
        descriptors.put("WAITUS", new BytecodeDescriptor(0x78, 1));
        descriptors.put("WAITMS", new BytecodeDescriptor(0x7A, 1));
        descriptors.put("GETMS", new BytecodeDescriptor(0x7C, 0));
        descriptors.put("GETSEC", new BytecodeDescriptor(0x7E, 0));
        descriptors.put("MULDIV64", new BytecodeDescriptor(0x80, 3));
        descriptors.put("QSIN", new BytecodeDescriptor(0x82, 3));
        descriptors.put("QCOS", new BytecodeDescriptor(0x84, 3));

        descriptors.put("COGINIT", new BytecodeDescriptor(0x25, 3));
        descriptors.put("COGSTOP", new BytecodeDescriptor(0x27, 1));
        descriptors.put("COGID", new BytecodeDescriptor(0x28, 0));
        descriptors.put("COGCHK", new BytecodeDescriptor(0x29, 1));

        descriptors.put("LOCKNEW", new BytecodeDescriptor(0x2A, 0));
        descriptors.put("LOCKRET", new BytecodeDescriptor(0x2B, 1));
        descriptors.put("LOCKTRY", new BytecodeDescriptor(0x2C, 1));
        descriptors.put("LOCKREL", new BytecodeDescriptor(0x2D, 1));
        descriptors.put("LOCKCHK", new BytecodeDescriptor(0x2E, 1));

        descriptors.put("COGATN", new BytecodeDescriptor(0x2F, 1));
        descriptors.put("POLLATN", new BytecodeDescriptor(0x30, 0));
        descriptors.put("WAITATN", new BytecodeDescriptor(0x31, 0));

        descriptors.put("GETRND", new BytecodeDescriptor(0x32, 0));
        descriptors.put("GETCT", new BytecodeDescriptor(0x33, 0));
        descriptors.put("POLLCT", new BytecodeDescriptor(0x34, 1));
        descriptors.put("WAITCT", new BytecodeDescriptor(0x35, 1));

        descriptors.put("PINW", new BytecodeDescriptor(0x36, 1));
        descriptors.put("PINWRITE", new BytecodeDescriptor(0x36, 1));
        descriptors.put("PINL", new BytecodeDescriptor(0x37, 1));
        descriptors.put("PINLOW", new BytecodeDescriptor(0x37, 1));
        descriptors.put("PINH", new BytecodeDescriptor(0x38, 1));
        descriptors.put("PINHIGH", new BytecodeDescriptor(0x38, 1));
        descriptors.put("PINT", new BytecodeDescriptor(0x39, 1));
        descriptors.put("PINTOGGLE", new BytecodeDescriptor(0x39, 1));
        descriptors.put("PINF", new BytecodeDescriptor(0x3A, 1));
        descriptors.put("PINFLOAT", new BytecodeDescriptor(0x3A, 1));
        descriptors.put("PINR", new BytecodeDescriptor(0x3B, 1));
        descriptors.put("PINREAD", new BytecodeDescriptor(0x3B, 1));

        descriptors.put("PINSTART", new BytecodeDescriptor(0x3C, 4));
        descriptors.put("PINCLEAR", new BytecodeDescriptor(0x3D, 1));

        descriptors.put("WRPIN", new BytecodeDescriptor(0x3E, 2));
        descriptors.put("WXPIN", new BytecodeDescriptor(0x3F, 2));
        descriptors.put("WYPIN", new BytecodeDescriptor(0x40, 2));
        descriptors.put("AKPIN", new BytecodeDescriptor(0x41, 1));
        descriptors.put("RDPIN", new BytecodeDescriptor(0x42, 1));
        descriptors.put("RQPIN", new BytecodeDescriptor(0x43, 1));

        descriptors.put("ROTXY", new BytecodeDescriptor(0x68, 3));
        descriptors.put("POLXY", new BytecodeDescriptor(0x69, 2));
        descriptors.put("XYPOL", new BytecodeDescriptor(0x6A, 2));
    }

    void compileBytecodeStatement(Spin2Method method, Spin2StatementNode node, boolean push) {
        BytecodeDescriptor desc = descriptors.get(node.getText().toUpperCase());
        if (desc != null) {
            if (node.getChildCount() == 1 && ",".equals(node.getChild(0).getText())) {
                Spin2StatementNode argsNode = node.getChild(0);
                if (argsNode.getChildCount() != desc.parameters) {
                    throw new RuntimeException("error: expected " + desc.parameters + " argument(s), found " + argsNode.getChildCount());
                }
                for (int i = 0; i < desc.parameters; i++) {
                    compileBytecodeStatement(method, argsNode.getChild(i), true);
                }
            }
            else {
                if (node.getChildCount() != desc.parameters) {
                    throw new RuntimeException("error: expected " + desc.parameters + " argument(s), found " + node.getChildCount());
                }
                for (int i = 0; i < desc.parameters; i++) {
                    compileBytecodeStatement(method, node.getChild(i), true);
                }
            }
            method.source.add(new Bytecode(method.getScope(), null, desc.code, node.getText()));
        }
        else if (node.type == Token.NUMBER) {
            Expression expression = new NumberLiteral(node.getText());
            method.source.add(new Constant(method.getScope(), null, expression));
        }
        else if (":=".equals(node.getText())) {
            compileBytecodeStatement(method, node.getChild(1), push);
            if (node.getChild(0).type == Token.OPERATOR) {
                compileBytecodeStatement(method, node.getChild(0), push);
            }
            else {
                Expression expression = method.getScope().getLocalSymbol(node.getChild(0).getText());
                if (expression instanceof Register) {
                    throw new RuntimeException("error: unhandled register expression");
                }
                else if ((expression instanceof Variable) || (expression instanceof LocalVariable)) {
                    method.source.add(new VariableOp(method.getScope(), null, VariableOp.Op.Write, (Variable) expression));
                }
                else {
                    throw new RuntimeException("error: unknown: " + node.getText());
                }
            }
        }
        else if (MathOp.isAssignMathOp(node.getText())) {
            compileBytecodeStatement(method, node.getChild(1), true);
            if (node.getChild(0).type == Token.OPERATOR) {
                compileBytecodeStatement(method, node.getChild(0), true);
            }
            else {
                Expression expression = method.getScope().getLocalSymbol(node.getChild(0).getText());
                if (expression instanceof Register) {
                    throw new RuntimeException("error: unhandled register expression");
                }
                else if ((expression instanceof Variable) || (expression instanceof LocalVariable)) {
                    method.addSource(new VariableOp(method.getScope(), null, VariableOp.Op.Setup, (Variable) expression));
                }
                else {
                    throw new RuntimeException("error: unknown: " + node.getText());
                }
            }
            method.source.add(new MathOp(method.getScope(), null, node.getText(), push));
        }
        else if (MathOp.isMathOp(node.getText())) {
            if (node.childs.size() != 2) {
                throw new RuntimeException("error: expression syntax error " + node.getText());
            }
            compileBytecodeStatement(method, node.getChild(0), true);
            compileBytecodeStatement(method, node.getChild(1), true);
            method.source.add(new MathOp(method.getScope(), null, node.getText(), false));
        }
        else if ("ABORT".equalsIgnoreCase(node.getText())) {
            if (node.childs.size() == 0) {
                method.source.add(new Bytecode(method.getScope(), null, 0x06, node.getText()));
            }
            else if (node.childs.size() == 1) {
                compileBytecodeStatement(method, node.getChild(0), true);
                method.source.add(new Bytecode(method.getScope(), null, 0x07, node.getText()));
            }
            else {
                throw new RuntimeException("error: expected 0 or 1 argument, found " + node.childs.size());
            }
        }
        else if ("RETURN".equalsIgnoreCase(node.getText())) {
            for (Spin2StatementNode child : node.getChilds()) {
                compileBytecodeStatement(method, child, push);
            }
            method.source.add(new Bytecode(method.getScope(), null, 0x04, node.getText()));
        }
        else if ("IF".equalsIgnoreCase(node.getText())) {
            compileBytecodeStatement(method, node.getChild(0), true);

            Spin2Bytecode exitLabel = new Spin2Bytecode(method.getScope(), "LABEL_" + String.valueOf(method.source.size()));
            method.getScope().addSymbol(exitLabel.getLabel(), new ContextLiteral(exitLabel.getContext()));
            method.addSource(new Jz(method.getScope(), null, new Identifier(exitLabel.getLabel(), exitLabel.getContext())));

            for (int i = 1; i < node.getChildCount(); i++) {
                compileBytecodeStatement(method, node.getChild(i), false);
            }
            method.source.add(exitLabel);
        }
        else if ("REPEAT".equalsIgnoreCase(node.getText())) {
            Spin2Bytecode exitLabel = new Spin2Bytecode(method.getScope(), "LABEL_" + String.valueOf(method.source.size()));

            Spin2StatementNode property = node.getProperty("counter");
            if (property != null) {
                compileBytecodeStatement(method, property, true);
                if (!(method.source.get(method.source.size() - 1) instanceof Constant)) {
                    method.addSource(new Tjz(method.getScope(), null, new Identifier(exitLabel.getLabel(), exitLabel.getContext())));
                }
            }

            Spin2Bytecode loopLabel = new Spin2Bytecode(method.getScope(), "LABEL_" + String.valueOf(method.source.size()));
            method.getScope().addSymbol(loopLabel.getLabel(), new ContextLiteral(loopLabel.getContext()));
            method.source.add(loopLabel);

            for (Spin2StatementNode child : node.getChilds()) {
                compileBytecodeStatement(method, child, push);
            }

            if (property != null) {
                method.source.add(new Djnz(method.getScope(), null, new Identifier(loopLabel.getLabel(), loopLabel.getContext())));
                method.getScope().addSymbol(exitLabel.getLabel(), new ContextLiteral(exitLabel.getContext()));
            }
            else {
                method.source.add(new Jmp(method.getScope(), null, new Identifier(loopLabel.getLabel(), loopLabel.getContext())));
            }
            method.source.add(exitLabel);
        }
        else {
            Expression expression = method.getScope().getLocalSymbol(node.getText());
            if (expression == null) {
                throw new RuntimeException("error: unknown " + node.getText());
            }
            if (expression instanceof Register) {
                throw new RuntimeException("error: unhandled register expression");
            }
            else if ((expression instanceof Variable) || (expression instanceof LocalVariable)) {
                method.source.add(new VariableOp(method.getScope(), null, VariableOp.Op.Read, (Variable) expression));
            }
            else {
                method.source.add(new Constant(method.getScope(), null, expression));
            }
        }
    }

    static void print(Spin2StatementNode node, int indent) {
        for (Spin2StatementNode child : node.getChilds()) {
            print(child, indent + 1);
        }

        if (indent != 0) {
            for (int i = 1; i < indent; i++) {
                System.out.print("|    ");
            }
            System.out.print("+--- ");
        }

        System.out.print(node.getClass().getSimpleName());
        System.out.print(" [" + node.getText().replaceAll("\n", "\\\\n") + "]");
        System.out.println();
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
            + "    DELAY    = _CLKFREQ / 2\n"
            + "\n"
            + "PUB main() | ct\n"
            + "\n"
            + "    ct := muldiv64(1, 2, 3)\n"
            + "\n"
            + "    ct := getct()                   ' get current timer\n"
            + "    repeat\n"
            + "        pint(56)                    ' toggle pin 56\n"
            + "        waitct(ct += DELAY)         ' wait half second"
            + "\n"
            + "DAT\n"
            + "\n"
            + "                org   $000\n"
            + "\n"
            + "start\n"
            + "                getct   ct                  ' get current timer\n"
            + ".loop           drvnot  #56                 ' toggle output\n"
            + "                addct1  ct, ##DELAY         ' set delay to timer 1\n"
            + "                waitct1                     ' wait for timer 1 expire\n"
            + "                jmp     #\\.loop\n"
            + "\n"
            + "ct              res     1\n"
            + "";
        String text = ""
            + "PUB main(a, b) : c | d, e\n"
            + "\n"
            + "    if a == 1\n"
            + "        a := 2\n"
            + "        b := 3\n"
            + "\n"
            + "";

        try {
            Spin2TokenStream stream = new Spin2TokenStream(text);
            Spin2Parser subject = new Spin2Parser(stream);
            Node root = subject.parse();

            Spin2Compiler compiler = new Spin2Compiler();
            Spin2Object obj = compiler.compile(root);

            obj.generateListing(System.out);

            Spin2Interpreter interpreter = new Spin2Interpreter();
            Spin2Object binary = new Spin2Object();
            binary.writeBytes(interpreter.code, "Interpreter");
            binary.writeObject(obj);

            FileOutputStream os = new FileOutputStream("test.binary");
            binary.generateBinary(os);
            os.close();

            System.out.println(String.format("PBASE: $%05X", interpreter.getPBase()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
