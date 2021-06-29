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
import java.util.Iterator;
import java.util.List;

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
import com.maccasoft.propeller.model.DataNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.NodeVisitor;
import com.maccasoft.propeller.model.ParameterNode;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.VariableNode;
import com.maccasoft.propeller.model.VariablesNode;
import com.maccasoft.propeller.spin1.bytecode.Bytecode;
import com.maccasoft.propeller.spin1.bytecode.Constant;
import com.maccasoft.propeller.spin1.bytecode.MemoryOp;
import com.maccasoft.propeller.spin1.bytecode.RegisterOp;
import com.maccasoft.propeller.spin1.bytecode.VariableOp;

public class Spin1Compiler {

    Spin1Context scope = new Spin1GlobalContext();
    List<Spin1PAsmLine> source = new ArrayList<Spin1PAsmLine>();
    List<Spin1BytecodeInstructionObject> bytecode = new ArrayList<Spin1BytecodeInstructionObject>();

    List<Spin1Method> methods = new ArrayList<Spin1Method>();

    int pbase;
    int vbase;
    int dbase;

    int pcurr;
    int dcurr;

    public Spin1Compiler() {
        this.pbase = 0x0010;
        this.vbase = 0x0010;
        this.dbase = this.pbase + 8;

        this.pcurr = this.pbase;
        this.dcurr = this.dbase;
    }

    Spin1Object compile(Node root) {
        Spin1Object obj = new Spin1Object();

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

        determineClock();

        for (Node node : root.getChilds()) {
            if (node instanceof DataNode) {
                compileDatBlock(node);
            }
        }

        for (Node node : root.getChilds()) {
            if (node instanceof MethodNode) {
                compileMethodBlock((MethodNode) node);
            }
        }

        //vbase += 4 + 4 * methods.size();
        //dbase += 4 + 4 * methods.size();
        //pcurr += 4 + 4 * methods.size();
        //dcurr += 4 + 4 * methods.size();

        int address = 0;
        for (Spin1PAsmLine line : source) {
            try {
                address = line.resolve(address);
                if (address > 0x1F0) {
                    throw new RuntimeException("error: cog code limit exceeded by " + (address - 0x1F0) + " long(s)");
                }
            } catch (Exception e) {
                line.getAnnotations().add(e.getMessage());
            }
        }

        /*
        address = pcurr;
        for (Spin1Method method : methods) {
            method.getScope().setHubAddress(address);

            //vbase += method.getLocalSize();
            //dbase += method.getLocalSize();
            dcurr += method.getLocalSize();

            for (Spin1BytecodeLine line : method.source) {
                line.getScope().setHubAddress(address);
                address = line.resolve(address);
                Spin1BytecodeInstructionObject obj = line.getInstructionObject();
                if (obj != null) {
                    vbase += obj.getSize();
                    dbase += obj.getSize();
                    dcurr += obj.getSize();
                }
            }
        }

        while ((address % 4) != 0) {
            vbase++;
            dbase++;
            dcurr++;
            address++;
        }
        */

        obj.writeWord(0, "Object size");
        obj.writeByte(methods.size() + 1, "Method count + 1");
        obj.writeByte(0, "OBJ count");

        address = obj.getSize() + 4 * methods.size();
        for (Spin1PAsmLine line : source) {
            Spin1InstructionObject instructionObject = line.getInstructionObject();
            if (instructionObject != null) {
                address += instructionObject.getSize();
            }
        }

        int i = 0;
        for (Spin1Method method : methods) {
            obj.writeLong(method.getLocalSize() << 16 | address, "Function " + method.getName() + " @$" + String.format("%04X", address) + " (local size " + method.getLocalSize() + ")");
            compileBytecodeExpression(method.getScope(), method.root, false);
            while (i < bytecode.size()) {
                Spin1BytecodeInstructionObject instruction = bytecode.get(i++);
                address += instruction.getSize();
            }
        }

        for (Spin1PAsmLine line : source) {
            line.getScope().setHubAddress(obj.getSize());
            Spin1InstructionObject instructionObject = line.getInstructionObject();
            if (instructionObject != null) {
                obj.writeBytes(line.getScope().getAddress(), instructionObject.getBytes(), line.toString());
            }
        }

        for (Spin1BytecodeInstructionObject instruction : bytecode) {
            obj.writeBytes(instruction.getBytes(), instruction.toString());
        }

        obj.alignToLong();

        return obj;
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

    void compileConBlock(Node parent) {

        parent.accept(new NodeVisitor() {
            int enumValue = 0, enumIncrement = 1;

            @Override
            public void visitConstantAssign(ConstantAssignNode node) {
                String name = node.identifier.getText();
                Expression expression = buildExpression(node.expression.getTokens(), scope);
                scope.addSymbol(name, expression);
            }

            @Override
            public void visitConstantSetEnum(ConstantSetEnumNode node) {
                Expression expression = buildExpression(node.start.getTokens(), scope);
                enumValue = expression.getNumber().intValue();
                if (node.step != null) {
                    expression = buildExpression(node.step.getTokens(), scope);
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
                    Expression expression = buildExpression(node.multiplier.getTokens(), scope);
                    enumValue += enumIncrement * expression.getNumber().intValue();
                }
                else {
                    enumValue += enumIncrement;
                }
            }

        });
    }

    void compileVarBlock(Node parent) {

        parent.accept(new NodeVisitor() {

            int offset;

            @Override
            public void visitVariable(VariableNode node) {
                scope.addSymbol(node.identifier.getText(), new Variable(node.identifier.getText(), offset));
                scope.addSymbol("@" + node.identifier.getText(), new Variable(node.identifier.getText(), offset));
                offset += 4;
            }

        });
    }

    void compileDatBlock(Node parent) {
        for (Node child : parent.getChilds()) {
            DataLineNode node = (DataLineNode) child;

            String label = node.label != null ? node.label.getText() : null;
            String condition = node.condition != null ? node.condition.getText() : null;
            String mnemonic = node.instruction != null ? node.instruction.getText() : null;
            String modifier = node.modifier != null ? node.modifier.getText() : null;
            List<Spin1PAsmExpression> parameters = new ArrayList<Spin1PAsmExpression>();

            Spin1Context localScope = new Spin1Context(scope);

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
                        expression = buildExpression(param.getTokens().subList(index, param.getTokens().size()), localScope);
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
                        count = buildExpression(param.count.getTokens(), localScope);
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

            Spin1PAsmLine pasmLine = new Spin1PAsmLine(localScope, label, condition, mnemonic, parameters, modifier);
            if (pasmLine.getLabel() != null) {
                try {
                    if (!pasmLine.isLocalLabel() && scope.getParent() != null) {
                        scope = scope.getParent();
                    }
                    scope.addSymbol(pasmLine.getLabel(), new ContextLiteral(pasmLine.getScope()));
                    scope.addSymbol("@" + pasmLine.getLabel(), new HubContextLiteral(pasmLine.getScope()));
                    if (!pasmLine.isLocalLabel()) {
                        scope = new Spin1Context(scope);
                    }
                } catch (RuntimeException e) {
                    System.err.println(pasmLine);
                    e.printStackTrace();
                }
            }
            source.addAll(pasmLine.expand());
        }

        while (scope.getParent() != null) {
            scope = scope.getParent();
        }
    }

    public static Expression buildExpression(List<Token> tokens, Spin1Context scope) {
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
                    else if (token.getText().startsWith("\"")) {
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

    void compileMethodBlock(MethodNode node) {
        Spin1Method method = new Spin1Method(new Spin1Context(scope), node);
        print(method.root, 0);
        methods.add(method);
    }

    void compileBytecodeExpression(Spin1Context context, Spin1BytecodeExpression node, boolean push) {
        Spin1BytecodeInstructionObject obj = null;

        if (node.type == Token.NUMBER) {
            Expression expression = new NumberLiteral(node.getText());
            obj = new Constant(expression);
        }
        else if (":=".equals(node.getText())) {
            compileBytecodeExpression(context, node.childs.get(1), true);
            Expression expression = context.getLocalSymbol(node.childs.get(0).getText());
            if (expression instanceof Register) {
                obj = new RegisterOp(RegisterOp.Op.Write, expression.getNumber().intValue());
            }
            else if (expression instanceof Variable) {
                obj = new VariableOp(VariableOp.Base.VBase, VariableOp.Op.Write, expression);
            }
            else if (expression instanceof LocalVariable) {
                obj = new VariableOp(VariableOp.Base.DBase, VariableOp.Op.Write, expression);
            }
            else {
                obj = new MemoryOp(MemoryOp.Size.Long, !push, MemoryOp.Base.PBase, MemoryOp.Op.Write, expression);
            }
        }
        else if (Spin1BytecodeExpression.assignMathOps.containsKey(node.getText())) {
            if (node.childs.size() != 2) {
                throw new RuntimeException("error: expression syntax error " + node.getText());
            }
            compileBytecodeExpression(context, node.childs.get(1), true);
            if (node.childs.get(0).type == Token.OPERATOR) {
                compileBytecodeExpression(context, node.childs.get(0), true);
            }
            else {
                Expression expression = context.getLocalSymbol(node.childs.get(0).getText());
                if (expression instanceof Register) {
                    RegisterOp op = new RegisterOp(RegisterOp.Op.Assign, expression.getNumber().intValue(), Spin1BytecodeExpression.assignMathOps.get(node.getText()).intValue());
                    if (push) {
                        op.mathOp |= 0b100_00000;
                    }
                }
                else if (expression instanceof Variable) {
                    obj = new VariableOp(VariableOp.Base.VBase, VariableOp.Op.Assign, expression, Spin1BytecodeExpression.assignMathOps.get(node.getText()).intValue());
                    if (push) {
                        ((VariableOp) obj).mathOp |= 0b100_00000;
                    }
                }
                else if (expression instanceof LocalVariable) {
                    obj = new VariableOp(VariableOp.Base.DBase, VariableOp.Op.Assign, expression, Spin1BytecodeExpression.assignMathOps.get(node.getText()).intValue());
                    if (push) {
                        ((VariableOp) obj).mathOp |= 0b100_00000;
                    }
                }
                else {
                    throw new RuntimeException("error: unknown " + node.getText());
                }
            }
        }
        else if (Spin1BytecodeExpression.mathOps.containsKey(node.getText())) {
            if (node.childs.size() != 2) {
                throw new RuntimeException("error: expression syntax error " + node.getText());
            }
            compileBytecodeExpression(context, node.childs.get(0), push);
            compileBytecodeExpression(context, node.childs.get(1), push);
            int value = Spin1BytecodeExpression.mathOps.get(node.getText()).intValue();
            obj = new Bytecode(0b111_00000 | value, Spin1BytecodeExpression.mathOpsText.get(value & 0x1F));
        }
        else if ("ABORT".equalsIgnoreCase(node.getText())) {
            if (node.childs.size() == 0) {
                obj = new Bytecode(0b00110000, node.getText().toUpperCase());
            }
            else if (node.childs.size() == 1) {
                compileBytecodeExpression(context, node.childs.get(0), true);
                obj = new Bytecode(0b00110001, node.getText().toUpperCase());
            }
            else {
                throw new RuntimeException("error: expected 0 or 1 argument, found " + node.childs.size());
            }
        }
        else if ("BYTEFILL".equalsIgnoreCase(node.getText()) || "WORDFILL".equalsIgnoreCase(node.getText()) || "LONGFILL".equalsIgnoreCase(node.getText())
            || "WAITPEQ".equalsIgnoreCase(node.getText())) {
            if (push) {
                throw new RuntimeException("error: function " + node.getText() + " does not return a value");
            }
            List<Spin1BytecodeExpression> args = node.childs.get(0).childs;
            if (args.size() != 3) {
                throw new RuntimeException("error: expected 3 arguments, found " + node.childs.size());
            }
            compileBytecodeExpression(context, args.get(0), true);
            compileBytecodeExpression(context, args.get(1), true);
            compileBytecodeExpression(context, args.get(2), true);
            obj = new Bytecode(0b00011000, node.getText().toUpperCase());
            if ("WORDFILL".equalsIgnoreCase(node.getText())) {
                ((Bytecode) obj).value += 1;
            }
            else if ("LONGFILL".equalsIgnoreCase(node.getText())) {
                ((Bytecode) obj).value += 2;
            }
            else if ("WAITPEQ".equalsIgnoreCase(node.getText())) {
                ((Bytecode) obj).value += 3;
            }
        }
        else if ("BYTEMOVE".equalsIgnoreCase(node.getText()) || "WORDMOVE".equalsIgnoreCase(node.getText()) || "LONGMOVE".equalsIgnoreCase(node.getText())
            || "WAITPNE".equalsIgnoreCase(node.getText())) {
            if (push) {
                throw new RuntimeException("error: function " + node.getText() + " does not return a value");
            }
            List<Spin1BytecodeExpression> args = node.childs.get(0).childs;
            if (args.size() != 3) {
                throw new RuntimeException("error: expected 3 arguments, found " + node.childs.size());
            }
            compileBytecodeExpression(context, args.get(0), true);
            compileBytecodeExpression(context, args.get(1), true);
            compileBytecodeExpression(context, args.get(2), true);
            obj = new Bytecode(0b00011100, node.getText().toUpperCase());
            if ("WORDFILL".equalsIgnoreCase(node.getText())) {
                ((Bytecode) obj).value += 1;
            }
            else if ("LONGFILL".equalsIgnoreCase(node.getText())) {
                ((Bytecode) obj).value += 2;
            }
            else if ("WAITPEQ".equalsIgnoreCase(node.getText())) {
                ((Bytecode) obj).value += 3;
            }
        }
        else if ("COGID".equalsIgnoreCase(node.getText())) {
            if (!push) {
                throw new RuntimeException("error: invalid statement " + node.getText());
            }
            obj = new RegisterOp(RegisterOp.Op.Read, 0x1E9);
        }
        else if ("COGINIT".equalsIgnoreCase(node.getText())) {
            List<Spin1BytecodeExpression> args = node.childs.get(0).childs;
            if (args.size() != 3) {
                throw new RuntimeException("error: expected 3 arguments, found " + node.childs.size());
            }
            compileBytecodeExpression(context, args.get(0), true);
            compileBytecodeExpression(context, args.get(1), true);
            compileBytecodeExpression(context, args.get(2), true);
            obj = new Bytecode(push ? 0b00101000 : 0b00101100, node.getText().toUpperCase());
        }
        else if ("LOCKNEW".equalsIgnoreCase(node.getText())) {
            if (node.childs.size() != 0) {
                throw new RuntimeException("error: expected 0 arguments, found " + node.childs.size());
            }
            obj = new Bytecode(push ? 0b00101001 : 0b00101101, node.getText().toUpperCase());
        }
        else if ("RETURN".equalsIgnoreCase(node.getText())) {
            for (Spin1BytecodeExpression exp : node.childs) {
                compileBytecodeExpression(context, exp, false);
            }
            obj = new Bytecode(0b00110010, node.getText().toUpperCase());
        }
        else if ("CLKSET".equalsIgnoreCase(node.getText())) {
            List<Spin1BytecodeExpression> args = node.childs.get(0).childs;
            if (args.size() != 2) {
                throw new RuntimeException("error: expected 2 arguments, found " + node.childs.size());
            }
            compileBytecodeExpression(context, args.get(0), true);
            compileBytecodeExpression(context, args.get(1), true);
            obj = new Bytecode(0b00100000, node.getText().toUpperCase());
        }
        else if ("COGSTOP".equalsIgnoreCase(node.getText()) || "LOCKRET".equalsIgnoreCase(node.getText()) || "WAITCNT".equalsIgnoreCase(node.getText())) {
            if (push) {
                throw new RuntimeException("error: function " + node.getText() + " does not return a value");
            }
            if (node.childs.size() != 1) {
                throw new RuntimeException("error: expected 1 argument, found " + node.childs.size());
            }
            compileBytecodeExpression(context, node.childs.get(0), true);

            obj = new Bytecode(0b00100001, node.getText().toUpperCase());
            if ("LOCKRET".equalsIgnoreCase(node.getText())) {
                ((Bytecode) obj).value += 1;
            }
            else if ("WAITCNT".equalsIgnoreCase(node.getText())) {
                ((Bytecode) obj).value += 2;
            }
        }
        else if ("WAITVID".equalsIgnoreCase(node.getText())) {
            List<Spin1BytecodeExpression> args = node.childs.get(0).childs;
            if (args.size() != 2) {
                throw new RuntimeException("error: expected 2 arguments, found " + node.childs.size());
            }
            compileBytecodeExpression(context, args.get(0), true);
            compileBytecodeExpression(context, args.get(1), true);
            obj = new Bytecode(0b00100111, node.getText().toUpperCase());
        }
        else {
            Expression expression = context.getLocalSymbol(node.getText());
            if (expression instanceof Register) {
                obj = new RegisterOp(RegisterOp.Op.Read, expression.getNumber().intValue());
            }
            else if (expression instanceof Variable) {
                if (node.getText().startsWith("@")) {
                    obj = new VariableOp(VariableOp.Base.VBase, VariableOp.Op.Address, expression);
                }
                else {
                    obj = new VariableOp(VariableOp.Base.VBase, VariableOp.Op.Read, expression);
                }
            }
            else if (expression instanceof LocalVariable) {
                if (node.getText().startsWith("@")) {
                    obj = new VariableOp(VariableOp.Base.DBase, VariableOp.Op.Address, expression);
                }
                else {
                    obj = new VariableOp(VariableOp.Base.DBase, VariableOp.Op.Read, expression);
                }
            }
            else {
                if (node.getText().startsWith("@")) {
                    obj = new MemoryOp(MemoryOp.Size.Long, !push, MemoryOp.Base.PBase, MemoryOp.Op.Address, expression);
                }
                else {
                    obj = new MemoryOp(MemoryOp.Size.Long, !push, MemoryOp.Base.PBase, MemoryOp.Op.Read, expression);
                }
            }
        }

        if (obj != null) {
            bytecode.add(obj);
        }
    }

    void print(Spin1BytecodeExpression node, int indent) {
        if (indent != 0) {
            for (int i = 1; i < indent; i++) {
                System.out.print("|    ");
            }
            System.out.print("+--- ");
        }

        System.out.print(node.getClass().getSimpleName());
        System.out.println(" [" + node.getText().replaceAll("\n", "\\\\n") + "]");

        for (Spin1BytecodeExpression child : node.getChilds()) {
            print(child, indent + 1);
        }
    }

    /*
    public void generateObjectCode(OutputStream os, PrintStream out) throws Exception {
        int address = 0;
    
        int clkfreq = scope.getInteger("CLKFREQ");
        writeInt(os, clkfreq);
        out.print(String.format("%04X    ", address));
        out.print(String.format(" %02X %02X %02X %02X", clkfreq & 0xFF, (clkfreq >> 8) & 0xFF, (clkfreq >> 16) & 0xFF, (clkfreq >> 24) & 0xFF));
        out.println("    | " + "CLKFREQ");
        address += 4;
    
        int clkmode = scope.getInteger("CLKMODE");
        os.write(clkmode);
        out.print(String.format("%04X    ", address));
        out.print(String.format(" %02X         ", clkmode & 0xFF));
        out.println("    | " + "CLKMODE");
        address += 1;
    
        os.write(0x00);
        out.print(String.format("%04X    ", address));
        out.print(String.format(" %02X         ", 0x00));
        out.println("    | " + "Placeholder for checksum");
        address += 1;
    
        writeWord(os, pbase);
        out.print(String.format("%04X    ", address));
        out.print(String.format(" %02X %02X      ", pbase & 0xFF, (pbase >> 8) & 0xFF));
        out.println("    | " + "PBASE");
        address += 2;
    
        writeWord(os, vbase);
        out.print(String.format("%04X    ", address));
        out.print(String.format(" %02X %02X      ", vbase & 0xFF, (vbase >> 8) & 0xFF));
        out.println("    | " + "VBASE");
        address += 2;
    
        writeWord(os, dbase);
        out.print(String.format("%04X    ", address));
        out.print(String.format(" %02X %02X      ", dbase & 0xFF, (dbase >> 8) & 0xFF));
        out.println("    | " + "DBASE");
        address += 2;
    
        writeWord(os, pcurr);
        out.print(String.format("%04X    ", address));
        out.print(String.format(" %02X %02X      ", pcurr & 0xFF, (pcurr >> 8) & 0xFF));
        out.println("    | " + "PCURR");
        address += 2;
    
        writeWord(os, dcurr);
        out.print(String.format("%04X    ", address));
        out.print(String.format(" %02X %02X      ", dcurr & 0xFF, (dcurr >> 8) & 0xFF));
        out.println("    | " + "DCURR");
        address += 2;
    
        out.print("\n");
    
        int size = vbase - pbase;
        writeWord(os, size);
        out.print(String.format("%04X    ", address));
        out.print(String.format(" %02X %02X      ", size & 0xFF, (size >> 8) & 0xFF));
        out.print(("    | " + "Object size\n"));
        address += 2;
    
        int count = methods.size() + 1;
        os.write(count);
        out.print(String.format("%04X    ", address));
        out.print(String.format(" %02X         ", count & 0xFF));
        out.print(("    | " + "Method count + 1\n"));
        address += 1;
    
        int objects = 0;
        os.write(objects);
        out.print(String.format("%04X    ", address));
        out.print(String.format(" %02X         ", objects & 0xFF));
        out.print(("    | " + "OBJ count\n"));
        address += 1;
    
        for (Spin1Method method : methods) {
            int faddr = method.getScope().getHubAddress() - pbase;
            int lsize = method.getLocalSize() - 4;
            writeWord(os, faddr);
            writeWord(os, lsize);
            out.print(String.format("%04X    ", address));
            out.print(String.format(" %02X %02X %02X %02X", faddr & 0xFF, (faddr >> 8) & 0xFF, lsize & 0xFF, (lsize >> 8) & 0xFF));
            out.print(("    | " + "Function\n"));
            address += 4;
        }
    
        out.println();
    
        for (Spin1PAsmLine line : source) {
            while (address < line.getScope().getHubAddress()) {
                os.write(0x00);
                out.print(String.format("%02X", 0x00));
                address++;
            }
            Spin1InstructionObject obj = line.getInstructionObject();
            byte[] code = obj.getBytes();
    
            out.print(String.format("%04X %03X", line.getScope().getHubAddress(), line.getScope().getInteger("$")));
    
            int i = 0;
            if (code != null && code.length != 0) {
                while (i < code.length && i < 4) {
                    out.print(String.format(" %02X", code[i++]));
                }
            }
            while (i < 5) {
                out.print("   ");
                i++;
            }
            out.print((" | " + line.toString()));
    
            if (code != null) {
                os.write(code);
                while (i < code.length) {
                    if ((i % 5) == 0) {
                        out.print(String.format("\n%04X    ", address + i));
                    }
                    out.print(String.format(" %02X", code[i++]));
                }
                address += code.length;
                out.println();
            }
        }
    
        if (source.size() != 0) {
            out.println();
        }
    
        if (methods.size() != 0) {
            out.println();
        }
    
        out.print(String.format("%04X    ", address));
    
        if ((address % 4) != 0) {
            int i = 0;
            while ((address % 4) != 0) {
                os.write(0x00);
                out.print(String.format(" %02X", 0x00));
                i++;
                address++;
            }
            while (i < 5) {
                out.print("   ");
                i++;
            }
            out.println(" | (padding)");
        }
    }
    
    public void writeInt(OutputStream os, int value) throws IOException {
        os.write(value & 0xFF);
        os.write((value >> 8) & 0xFF);
        os.write((value >> 16) & 0xFF);
        os.write((value >> 24) & 0xFF);
    }
    
    public void writeWord(OutputStream os, int value) throws IOException {
        os.write(value & 0xFF);
        os.write((value >> 8) & 0xFF);
    }
    */

    public static void main(String[] args) {
        String text = ""
            + "VAR\n"
            + "\n"
            + "    long b\n"
            + "\n"
            + "PUB main\n"
            + "\n"
            + "    a := 1\n"
            + "    b := 2\n"
            + "    coginit(cogid, @start, 0)\n"
            + "\n"
            + "PUB loop | c\n"
            + "\n"
            + "    c := a + b * 3\n"
            + "\n"
            + "DAT             org     $000\n"
            + "\n"
            + "start           cogid   a\n"
            + "                cogstop a\n"
            + "\n"
            + "a               res     1\n"
            + "";

        try {
            Spin1TokenStream stream = new Spin1TokenStream(text);
            Spin1Parser subject = new Spin1Parser(stream);
            Node root = subject.parse();
            //print(root, 0);

            Spin1Compiler compiler = new Spin1Compiler();
            Spin1Object obj = compiler.compile(root);

            obj.generateListing(0x0000, System.out);

            /*
            for (Spin1PAsmLine line : compiler.source) {
                System.out.println(line);
            }

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            compiler.generateObjectCode(os, System.out);

            byte[] code = os.toByteArray();
            System.out.println();
            for (int i = 0; i < code.length; i++) {
                if (i > 0 && (i % 16) == 0) {
                    System.out.println();
                }
                System.out.print(String.format("%02X ", code[i]));
            }
            if ((code.length % 16) != 0) {
                System.out.println();
            }
            */

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
        //for (Token token : node.tokens) {
        //    System.out.print(" [" + token.getText().replaceAll("\n", "\\n") + "]");
        //}
        System.out.println(" [" + node.getText().replaceAll("\n", "\\\\n") + "]");

        for (Node child : node.getChilds()) {
            print(child, indent + 1);
        }
    }

}
