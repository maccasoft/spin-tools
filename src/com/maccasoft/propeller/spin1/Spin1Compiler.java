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
import com.maccasoft.propeller.model.LocalVariableNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.NodeVisitor;
import com.maccasoft.propeller.model.ParameterNode;
import com.maccasoft.propeller.model.StatementNode;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.VariableNode;
import com.maccasoft.propeller.model.VariablesNode;
import com.maccasoft.propeller.spin1.Spin1Object.WordDataObject;
import com.maccasoft.propeller.spin1.bytecode.Bytecode;
import com.maccasoft.propeller.spin1.bytecode.Constant;
import com.maccasoft.propeller.spin1.bytecode.Jmp;
import com.maccasoft.propeller.spin1.bytecode.MemoryOp;
import com.maccasoft.propeller.spin1.bytecode.RegisterBitOp;
import com.maccasoft.propeller.spin1.bytecode.RegisterOp;
import com.maccasoft.propeller.spin1.bytecode.VariableOp;

public class Spin1Compiler {

    Spin1Context scope = new Spin1GlobalContext();
    List<Spin1PAsmLine> source = new ArrayList<Spin1PAsmLine>();

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

        WordDataObject objectSize = obj.writeWord(0, "Object size");
        obj.writeByte(methods.size() + 1, "Method count + 1");
        obj.writeByte(0, "OBJ count");

        address = obj.getSize() + 4 * methods.size();
        for (Spin1PAsmLine line : source) {
            Spin1InstructionObject instructionObject = line.getInstructionObject();
            if (instructionObject != null) {
                address += instructionObject.getSize();
            }
        }

        for (Spin1Method method : methods) {
            obj.writeLong(method.getLocalSize() << 16 | address, "Function " + method.getLabel() + " @$" + String.format("%04X", address) + " (local size " + method.getLocalSize() + ")");
            for (Spin1BytecodeInstruction instruction : method.source) {
                address = instruction.resolve(address);
            }
        }

        for (Spin1PAsmLine line : source) {
            line.getScope().setHubAddress(obj.getSize());
            Spin1InstructionObject instructionObject = line.getInstructionObject();
            if (instructionObject != null) {
                obj.writeBytes(line.getScope().getAddress(), instructionObject.getBytes(), line.toString());
            }
        }

        for (Spin1Method method : methods) {
            for (Spin1BytecodeInstruction instruction : method.source) {
                obj.writeBytes(instruction.getBytes(), instruction.toString());
            }
        }

        obj.alignToLong();
        objectSize.setValue(obj.getSize());

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
                String type = "LONG";
                if (node.type != null) {
                    type = node.type.getText().toUpperCase();
                }
                Expression size = null;
                if (node.size != null) {
                    size = buildExpression(node.size.getTokens(), scope);
                }
                scope.addSymbol(node.identifier.getText(), new Variable(type, node.identifier.getText(), size, offset));
                scope.addSymbol("@" + node.identifier.getText(), new Variable(type, node.identifier.getText(), size, offset));
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
        Spin1Method method = new Spin1Method(new Spin1Context(scope), node.name.getText());

        if (node.getReturnVariables().size() != 0) {
            Node child = node.getReturnVariable(0);
            method.getScope().addSymbol(child.getText(), new LocalVariable("LONG", child.getText(), new NumberLiteral(1), 0));
            method.getScope().addSymbol("@" + child.getText(), new LocalVariable("LONG", child.getText(), new NumberLiteral(1), 0));
        }

        int offset = 4;
        for (Node child : node.getParameters()) {
            method.getScope().addSymbol(child.getText(), new LocalVariable("LONG", child.getText(), new NumberLiteral(1), offset));
            method.getScope().addSymbol("@" + child.getText(), new LocalVariable("LONG", child.getText(), new NumberLiteral(1), offset));
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
            method.getScope().addSymbol(child.getIdentifier().getText(), new LocalVariable(type, child.getText(), size, offset));
            method.getScope().addSymbol("@" + child.getIdentifier().getText(), new LocalVariable(type, child.getText(), size, offset));

            int count = 4;
            if ("BYTE".equalsIgnoreCase(type)) {
                count = 1;
            }
            if ("WORD".equalsIgnoreCase(type)) {
                count = 2;
            }
            count = count * size.getNumber().intValue();
            offset += ((count + 3) / 4) * 4;
        }
        method.setLocalSize(offset - 4);

        Spin1BytecodeNode root = new Spin1BytecodeNode("RETURN");
        for (Node child : node.getChilds()) {
            compileStatementBlock(root, child);
        }

        print(root, 0);
        compileBytecodeExpression(method, root, false);

        methods.add(method);
    }

    Spin1BytecodeNode compileRepeat(List<Token> tokens, Spin1BytecodeNode parent) {
        int index = 1;

        List<Token> list = new ArrayList<Token>();
        while (index < tokens.size()) {
            Token token = tokens.get(index++);
            if (token.type == Token.NL || token.type == Token.EOF) {
                break;
            }
            list.add(token);
        }

        if (list.size() != 0) {
            Expression expression = buildExpression(list, scope);
            if (expression.isConstant()) {
                Token token = new Token(Token.NUMBER, String.valueOf(expression.getNumber()));
                Spin1BytecodeNode exp = new Spin1BytecodeNode(token);
                parent.childs.add(exp);
            }
            else {
                Spin1BytecodeNode exp = compileBytecodeTree(list);
                parent.childs.add(exp);
            }
        }

        return new Spin1BytecodeNode(tokens.get(0));
    }

    void compileStatementBlock(Spin1BytecodeNode parent, Node node) {
        List<Token> tokens = node.getTokens();

        Spin1BytecodeNode expression;
        if ("REPEAT".equalsIgnoreCase(tokens.get(0).getText())) {
            expression = compileRepeat(tokens, parent);
        }
        else {
            expression = compileBytecodeTree(tokens);
        }

        parent.childs.add(expression);

        for (Node child : node.getChilds()) {
            if (child instanceof StatementNode) {
                compileStatementBlock(expression, child);
            }
        }
    }

    Spin1BytecodeNode compileBytecodeTree(List<Token> tokens) {
        int state = 0;
        Spin1BytecodeParser compiler = new Spin1BytecodeParser();

        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            if (token.type == Token.NL || token.type == Token.EOF) {
                break;
            }
            switch (state) {
                case 0:
                    if (Spin1BytecodeParser.operatorPrecedence.containsKey(token.getText().toUpperCase())) {
                        compiler.addUnaryOperator(token);
                        break;
                    }
                    // fall through
                case 1:
                    if (Spin1BytecodeParser.operatorPrecedence.containsKey(token.getText().toUpperCase())) {
                        compiler.addOperatorToken(token);
                        break;
                    }
                    if (i + 1 < tokens.size()) {
                        Token next = tokens.get(i + 1);
                        if (next.type != Token.EOF && next.type != Token.NL) {
                            if ("(".equals(next.getText())) {
                                compiler.addFunctionOperatorToken(token, next);
                                i++;
                                state = 0;
                                break;
                            }
                        }
                    }
                    compiler.addValueToken(token);
                    state = 2;
                    break;
                case 2:
                    compiler.addOperatorToken(token);
                    state = 1;
                    break;
            }
        }

        return compiler.getExpression();
    }

    void compileBytecodeExpression(Spin1Method method, Spin1BytecodeNode node, boolean push) {
        compileBytecodeExpression(method, node, push, null);
    }

    void compileBytecodeExpression(Spin1Method method, Spin1BytecodeNode node, boolean push, String mathOp) {
        Spin1BytecodeInstruction obj = null;

        if (node.type == Token.NUMBER) {
            Expression expression = new NumberLiteral(node.getText());
            obj = new Constant(new Spin1Context(method.getScope()), expression);
        }
        else if (":=".equals(node.getText())) {
            compileBytecodeExpression(method, node.childs.get(1), true);
            if (node.childs.get(0).type == Token.OPERATOR) {
                compileBytecodeExpression(method, node.childs.get(0), true);
            }
            else {
                Expression expression = method.getScope().getLocalSymbol(node.childs.get(0).getText());
                if (expression instanceof Register) {
                    obj = new RegisterOp(new Spin1Context(method.getScope()), RegisterOp.Op.Write, expression.getNumber().intValue());
                }
                else if (expression instanceof Variable) {
                    obj = new VariableOp(new Spin1Context(method.getScope()), VariableOp.Base.VBase, VariableOp.Op.Write, expression);
                }
                else if (expression instanceof LocalVariable) {
                    obj = new VariableOp(new Spin1Context(method.getScope()), VariableOp.Base.DBase, VariableOp.Op.Write, expression);
                }
                else {
                    obj = new MemoryOp(new Spin1Context(method.getScope()), MemoryOp.Size.Long, !push, MemoryOp.Base.PBase, MemoryOp.Op.Write, expression);
                }
            }
        }
        else if (Spin1BytecodeNode.assignMathOps.containsKey(node.getText())) {
            if (node.childs.size() != 2) {
                throw new RuntimeException("error: expression syntax error " + node.getText());
            }
            compileBytecodeExpression(method, node.childs.get(1), true);
            if (node.childs.get(0).type == Token.OPERATOR) {
                compileBytecodeExpression(method, node.childs.get(0), false, node.getText());
            }
            else {
                Expression expression = method.getScope().getLocalSymbol(node.childs.get(0).getText());
                if (expression instanceof Register) {
                    obj = new RegisterOp(new Spin1Context(method.getScope()), RegisterOp.Op.Assign, expression.getNumber().intValue(),
                        Spin1BytecodeNode.assignMathOps.get(node.getText()).intValue());
                    if (push) {
                        ((RegisterOp) obj).mathOp |= 0b100_00000;
                    }
                }
                else if (expression instanceof Variable) {
                    obj = new VariableOp(new Spin1Context(method.getScope()), VariableOp.Base.VBase, VariableOp.Op.Assign, expression,
                        Spin1BytecodeNode.assignMathOps.get(node.getText()).intValue());
                    if (push) {
                        ((VariableOp) obj).mathOp |= 0b100_00000;
                    }
                }
                else if (expression instanceof LocalVariable) {
                    obj = new VariableOp(new Spin1Context(method.getScope()), VariableOp.Base.DBase, VariableOp.Op.Assign, expression,
                        Spin1BytecodeNode.assignMathOps.get(node.getText()).intValue());
                    if (push) {
                        ((VariableOp) obj).mathOp |= 0b100_00000;
                    }
                }
                else {
                    throw new RuntimeException("error: unknown " + node.getText());
                }
            }
        }
        else if (Spin1BytecodeNode.mathOps.containsKey(node.getText())) {
            if (node.childs.size() != 2) {
                throw new RuntimeException("error: expression syntax error " + node.getText());
            }
            compileBytecodeExpression(method, node.childs.get(0), push);
            compileBytecodeExpression(method, node.childs.get(1), push);
            int value = Spin1BytecodeNode.mathOps.get(node.getText()).intValue();
            obj = new Bytecode(new Spin1Context(method.getScope()), 0b111_00000 | value, Spin1BytecodeNode.mathOpsText.get(value & 0x1F));
        }
        else if ("[".equalsIgnoreCase(node.getText())) {
            if (node.childs.size() != 2) {
                throw new RuntimeException("error: expression syntax error " + node.getText());
            }
            compileBytecodeExpression(method, node.childs.get(1), true);
            Expression expression = method.getScope().getLocalSymbol(node.childs.get(0).getText());
            if (expression instanceof Register) {
                if (mathOp != null) {
                    Integer i = Spin1BytecodeNode.assignMathOps.get(mathOp);
                    obj = new RegisterBitOp(new Spin1Context(method.getScope()), RegisterBitOp.Op.Assign, expression.getNumber().intValue(), i);
                }
                else {
                    obj = new RegisterBitOp(new Spin1Context(method.getScope()), RegisterBitOp.Op.Write, expression.getNumber().intValue());
                }
                if (push) {
                    ((RegisterBitOp) obj).mathOp |= 0b100_00000;
                }
            }
            else {
                throw new RuntimeException("error: unknown " + node.getText());
            }
        }
        else if ("ABORT".equalsIgnoreCase(node.getText())) {
            if (node.childs.size() == 0) {
                obj = new Bytecode(new Spin1Context(method.getScope()), 0b00110000, node.getText().toUpperCase());
            }
            else if (node.childs.size() == 1) {
                compileBytecodeExpression(method, node.childs.get(0), true);
                obj = new Bytecode(new Spin1Context(method.getScope()), 0b00110001, node.getText().toUpperCase());
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
            List<Spin1BytecodeNode> args = node.childs.get(0).childs;
            if (args.size() != 3) {
                throw new RuntimeException("error: expected 3 arguments, found " + node.childs.size());
            }
            compileBytecodeExpression(method, args.get(0), true);
            compileBytecodeExpression(method, args.get(1), true);
            compileBytecodeExpression(method, args.get(2), true);
            obj = new Bytecode(new Spin1Context(method.getScope()), 0b00011000, node.getText().toUpperCase());
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
            List<Spin1BytecodeNode> args = node.childs.get(0).childs;
            if (args.size() != 3) {
                throw new RuntimeException("error: expected 3 arguments, found " + node.childs.size());
            }
            compileBytecodeExpression(method, args.get(0), true);
            compileBytecodeExpression(method, args.get(1), true);
            compileBytecodeExpression(method, args.get(2), true);
            obj = new Bytecode(new Spin1Context(method.getScope()), 0b00011100, node.getText().toUpperCase());
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
            obj = new RegisterOp(new Spin1Context(method.getScope()), RegisterOp.Op.Read, 0x1E9);
        }
        else if ("COGINIT".equalsIgnoreCase(node.getText())) {
            List<Spin1BytecodeNode> args = node.childs.get(0).childs;
            if (args.size() != 3) {
                throw new RuntimeException("error: expected 3 arguments, found " + node.childs.size());
            }
            compileBytecodeExpression(method, args.get(0), true);
            compileBytecodeExpression(method, args.get(1), true);
            compileBytecodeExpression(method, args.get(2), true);
            obj = new Bytecode(new Spin1Context(method.getScope()), push ? 0b00101000 : 0b00101100, node.getText().toUpperCase());
        }
        else if ("LOCKNEW".equalsIgnoreCase(node.getText())) {
            if (node.childs.size() != 0) {
                throw new RuntimeException("error: expected 0 arguments, found " + node.childs.size());
            }
            obj = new Bytecode(new Spin1Context(method.getScope()), push ? 0b00101001 : 0b00101101, node.getText().toUpperCase());
        }
        else if ("REPEAT".equalsIgnoreCase(node.getText())) {
            Spin1Context context = new Spin1Context(method.getScope());
            Spin1BytecodeInstruction label = new Spin1BytecodeInstruction(context, "label");
            context.addSymbol("label", new ContextLiteral(context));
            method.source.add(label);
            for (Spin1BytecodeNode exp : node.childs) {
                compileBytecodeExpression(method, exp, false);
            }
            obj = new Jmp(new Spin1Context(context), new Identifier("label", label.getContext()));
        }
        else if ("RETURN".equalsIgnoreCase(node.getText())) {
            for (Spin1BytecodeNode exp : node.childs) {
                compileBytecodeExpression(method, exp, false);
            }
            obj = new Bytecode(new Spin1Context(method.getScope()), 0b00110010, node.getText().toUpperCase());
        }
        else if ("CLKSET".equalsIgnoreCase(node.getText())) {
            List<Spin1BytecodeNode> args = node.childs.get(0).childs;
            if (args.size() != 2) {
                throw new RuntimeException("error: expected 2 arguments, found " + node.childs.size());
            }
            compileBytecodeExpression(method, args.get(0), true);
            compileBytecodeExpression(method, args.get(1), true);
            obj = new Bytecode(new Spin1Context(method.getScope()), 0b00100000, node.getText().toUpperCase());
        }
        else if ("COGSTOP".equalsIgnoreCase(node.getText()) || "LOCKRET".equalsIgnoreCase(node.getText()) || "WAITCNT".equalsIgnoreCase(node.getText())) {
            if (push) {
                throw new RuntimeException("error: function " + node.getText() + " does not return a value");
            }
            if (node.childs.size() != 1) {
                throw new RuntimeException("error: expected 1 argument, found " + node.childs.size());
            }
            compileBytecodeExpression(method, node.childs.get(0), true);

            obj = new Bytecode(new Spin1Context(method.getScope()), 0b00100001, node.getText().toUpperCase());
            if ("LOCKRET".equalsIgnoreCase(node.getText())) {
                ((Bytecode) obj).value += 1;
            }
            else if ("WAITCNT".equalsIgnoreCase(node.getText())) {
                ((Bytecode) obj).value += 2;
            }
        }
        else if ("WAITVID".equalsIgnoreCase(node.getText())) {
            List<Spin1BytecodeNode> args = node.childs.get(0).childs;
            if (args.size() != 2) {
                throw new RuntimeException("error: expected 2 arguments, found " + node.childs.size());
            }
            compileBytecodeExpression(method, args.get(0), true);
            compileBytecodeExpression(method, args.get(1), true);
            obj = new Bytecode(new Spin1Context(method.getScope()), 0b00100111, node.getText().toUpperCase());
        }
        else {
            Expression expression = method.getScope().getLocalSymbol(node.getText());
            if (expression instanceof Register) {
                obj = new RegisterOp(new Spin1Context(method.getScope()), RegisterOp.Op.Read, expression.getNumber().intValue());
            }
            else if (expression instanceof Variable) {
                if (node.getText().startsWith("@")) {
                    obj = new VariableOp(new Spin1Context(method.getScope()), VariableOp.Base.VBase, VariableOp.Op.Address, expression);
                }
                else {
                    obj = new VariableOp(new Spin1Context(method.getScope()), VariableOp.Base.VBase, VariableOp.Op.Read, expression);
                }
            }
            else if (expression instanceof LocalVariable) {
                if (node.getText().startsWith("@")) {
                    obj = new VariableOp(new Spin1Context(method.getScope()), VariableOp.Base.DBase, VariableOp.Op.Address, expression);
                }
                else {
                    obj = new VariableOp(new Spin1Context(method.getScope()), VariableOp.Base.DBase, VariableOp.Op.Read, expression);
                }
            }
            else {
                if (node.getText().startsWith("@")) {
                    obj = new MemoryOp(new Spin1Context(method.getScope()), MemoryOp.Size.Long, !push, MemoryOp.Base.PBase, MemoryOp.Op.Address, expression);
                }
                else {
                    obj = new MemoryOp(new Spin1Context(method.getScope()), MemoryOp.Size.Long, !push, MemoryOp.Base.PBase, MemoryOp.Op.Read, expression);
                }
            }
        }

        if (obj != null) {
            method.addBytecodeInstruction(obj);
        }
    }

    void print(Spin1BytecodeNode node, int indent) {
        if (indent != 0) {
            for (int i = 1; i < indent; i++) {
                System.out.print("|    ");
            }
            System.out.print("+--- ");
        }

        System.out.print(node.getClass().getSimpleName());
        System.out.print(" [" + node.getText().replaceAll("\n", "\\\\n") + "]");
        System.out.println();

        for (Spin1BytecodeNode child : node.getChilds()) {
            print(child, indent + 1);
        }
    }

    public static void main(String[] args) {
        String text1 = ""
            + "VAR\n"
            + "\n"
            + "    long a, b\n"
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
            + "start           cogid   id\n"
            + "                cogstop id\n"
            + "\n"
            + "id              res     1\n"
            + "";
        String text = ""
            + "CON\n"
            + "\n"
            + "    _XINFREQ = 5_000_000\n"
            + "    _CLKMODE = XTAL1 + PLL16X\n"
            + "\n"
            + "OBJ\n"
            + "\n"
            + "PUB start | a\n"
            + "\n"
            + "    ' initialization\n"
            + "    DIRA[0] := 1\n"
            + "    a := CNT\n"
            + "\n"
            + "    repeat\n"
            + "        ' loop\n"
            + "        OUTA[0] ^= 1\n"
            + "        waitcnt(a += CNT / 2)\n"
            + "";

        try {
            Spin1TokenStream stream = new Spin1TokenStream(text);
            Spin1Parser subject = new Spin1Parser(stream);
            Node root = subject.parse();
            print(root, 0);

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
