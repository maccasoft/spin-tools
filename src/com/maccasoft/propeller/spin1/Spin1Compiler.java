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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
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

public class Spin1Compiler {

    Spin1Context scope = new Spin1GlobalContext();
    List<Spin1PAsmLine> source = new ArrayList<Spin1PAsmLine>();

    List<Spin1Method> methods = new ArrayList<Spin1Method>();

    ExpressionBuilder expressionBuilder = new ExpressionBuilder();

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

    void compile(Node root) {
        for (Node node : root.getChilds()) {
            if (node instanceof ConstantsNode) {
                compileConBlock(node);
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

        vbase += 4 + 4 * methods.size();
        dbase += 4 + 4 * methods.size();
        pcurr += 4 + 4 * methods.size();
        dcurr += 4 + 4 * methods.size();

        int address = 0;
        for (Spin1PAsmLine line : source) {
            line.getScope().setHubAddress(pcurr);
            try {
                address = line.resolve(address);
                if (address > 0x1F0) {
                    throw new RuntimeException("error: cog code limit exceeded by " + (address - 0x1F0) + " long(s)");
                }
                Spin1InstructionObject obj = line.getInstructionObject();
                if (obj != null) {
                    vbase += obj.getSize();
                    dbase += obj.getSize();
                    pcurr += obj.getSize();
                    dcurr += obj.getSize();
                }
            } catch (Exception e) {
                line.getAnnotations().add(e.getMessage());
            }
        }

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
    }

    Expression buildExpression(List<Token> tokens, Spin1Context scope) {
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
        Spin1Method method = new Spin1Method(new Spin1Context(scope));

        if (node.getReturnVariables().size() != 0) {
            Node child = node.getReturnVariable(0);
            method.getScope().addSymbol(child.getText(), new LocalVariable(child.getText(), 0));
            method.getScope().addSymbol("@" + child.getText(), new LocalVariable(child.getText(), 0));
        }

        int offset = 4;
        for (Node child : node.getParameters()) {
            method.getScope().addSymbol(child.getText(), new LocalVariable(child.getText(), offset));
            method.getScope().addSymbol("@" + child.getText(), new LocalVariable(child.getText(), offset));
            offset += 4;
        }
        for (LocalVariableNode child : node.getLocalVariables()) {
            method.getScope().addSymbol(child.getIdentifier().getText(), new LocalVariable(child.getIdentifier().getText(), offset));
            method.getScope().addSymbol("@" + child.getIdentifier().getText(), new LocalVariable(child.getIdentifier().getText(), offset));

            int size = 4;
            if (child.getType() != null) {
                if ("BYTE".equalsIgnoreCase(child.getType().getText())) {
                    size = 1;
                }
                if ("WORD".equalsIgnoreCase(child.getType().getText())) {
                    size = 2;
                }
            }
            if (child.getSize() != null) {
                Expression count = buildExpression(child.getSize().getTokens(), method.getScope());
                size *= count.getNumber().intValue();
            }
            offset += ((size + 3) / 4) * 4;
        }
        method.setLocalSize(offset);

        for (Node child : node.getChilds()) {
            if (child instanceof StatementNode) {
                compileStatementBlock(method, child);
            }
        }

        method.expand();

        methods.add(method);
    }

    void compileStatementBlock(Spin1Method method, Node node) {
        Spin1BytecodeExpressionCompiler compiler = new Spin1BytecodeExpressionCompiler();

        List<Token> tokens = node.getTokens();
        int state = 0;

        if (tokens.size() != 0) {
            if ("REPEAT".equalsIgnoreCase(tokens.get(0).getText())) {
                for (Node child : node.getChilds()) {
                    if (child instanceof StatementNode) {
                        compileStatementBlock(method, child);
                    }
                }
                return;
            }
        }

        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            switch (state) {
                case 0:
                    if (Spin1BytecodeExpressionCompiler.operatorPrecedence.containsKey(token.getText().toUpperCase())) {
                        compiler.addUnaryOperator(token);
                        break;
                    }
                    // fall through
                case 1:
                    if (Spin1BytecodeExpressionCompiler.operatorPrecedence.containsKey(token.getText().toUpperCase())) {
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

        Spin1BytecodeExpression expression = compiler.getExpression();
        print(expression, 0);
        expression.generateObjectCode(method.getScope(), false);
        //method.source.add(new Spin1BytecodeLine(new Spin1Context(method.getScope()), null, expression.getText(), expression.getChilds()));

        for (Node child : node.getChilds()) {
            if (child instanceof StatementNode) {
                compileStatementBlock(method, child);
            }
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

        for (Spin1Method method : methods) {
            for (Spin1BytecodeLine line : method.source) {
                byte[] code = line.getInstructionObject().getBytes();

                out.print(String.format("%04X    ", address));

                int i = 0;
                if (code != null && code.length != 0) {
                    os.write(code);
                    while (i < code.length && i < 5) {
                        out.print(String.format(" %02X", code[i++]));
                    }
                }
                while (i < 5) {
                    out.print("   ");
                    i++;
                }
                out.print(" | ");

                out.println(line.getInstructionObject());

                address += code.length;
            }
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

    public static void main(String[] args) {
        String text = ""
            + "PUB main | a\n"
            + "\n"
            + "    a := CNT\n"
            + "    repeat\n"
            + "        waitcnt(a += 1_000)\n"
            + "";

        try {
            Spin1TokenStream stream = new Spin1TokenStream(text);
            Spin1Parser subject = new Spin1Parser(stream);
            Node root = subject.parse();
            print(root, 0);

            Spin1Compiler compiler = new Spin1Compiler();
            compiler.compile(root);

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
