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

import java.io.OutputStream;
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
import com.maccasoft.propeller.expressions.NumberLiteral;
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
import com.maccasoft.propeller.spin1.bytecode.Constant;

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

    static class Spin1Line {
        Spin1Context scope;
        Expression expression;

        Spin1BytecodeFactory instructionFactory;
        Spin1BytecodeObject instructionObject;

        public Spin1Line(Spin1Context scope, Spin1BytecodeFactory instructionFactory, Expression expression) {
            this.scope = scope;
            this.instructionFactory = instructionFactory;
            this.expression = expression;
        }

        public Spin1Context getScope() {
            return scope;
        }

        public List<Spin1Line> expand() {
            return Collections.singletonList(this);
        }

        public int resolve(int address) {
            return address;
        }

    }

    public Spin1Compiler() {
        this.pbase = 0x0010;
        this.vbase = 0x0010 + 4;
        this.dbase = this.vbase + 8;

        this.pcurr = this.pbase + 4;
        this.dcurr = this.dbase + 4;
    }

    void compile(Node root) {
        int address = 0;

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

        if (methods.size() == 0) {
            methods.add(new Spin1Method(scope));
        }

        vbase += methods.size() * 4;
        dbase += methods.size() * 4;
        pcurr += methods.size() * 4;
        dcurr += methods.size() * 4;

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

        if (methods.size() != 0) {
            dcurr += methods.get(0).getLocalSize();
        }

        address = pcurr;
        for (Spin1Method line : methods) {
            address = line.resolve(address);
            dcurr += line.getSize();
            vbase += line.getSize();
            dbase += line.getSize();
        }

        while ((address % 4) != 0) {
            vbase++;
            dbase++;
            dcurr++;
            address++;
        }
    }

    void determineClock() {
        Expression clkmode = scope.getLocalSymbol("_CLKMODE");
        if (clkmode == null) {
            clkmode = scope.getLocalSymbol("_clkmode");
        }

        Expression clkfreq = scope.getLocalSymbol("_CLKFREQ");
        if (clkfreq == null) {
            clkfreq = scope.getLocalSymbol("_clkfreq");
        }

        Expression xinfreq = scope.getLocalSymbol("_XINFREQ");
        if (xinfreq == null) {
            xinfreq = scope.getLocalSymbol("_xinfreq");
        }

        if (clkmode == null && clkfreq == null && xinfreq == null) {
            scope.addSymbol("_CLKMODE", new NumberLiteral(0));
            scope.addSymbol("_CLKFREQ", new NumberLiteral(12_000_000));
            return;
        }

        if (clkmode == null && (clkfreq != null || xinfreq != null)) {
            throw new RuntimeException("_CLKFREQ / _XINFREQ specified without _CLKMODE");
        }
        if (clkfreq != null && xinfreq != null) {
            throw new RuntimeException("Either _CLKFREQ or _XINFREQ must be specified, but not both");
        }

        int mode = clkmode.getNumber().intValue();
        if (mode == 0 || (mode & 0xFFFFF800) != 0 || (((mode & 0x03) != 0) && ((mode & 0x7FC) != 0))) {
            throw new RuntimeException("Invalid _CLKMODE specified");
        }
        if ((mode & 0x03) != 0) { // RCFAST or RCSLOW
            if (clkfreq != null || xinfreq != null) {
                throw new RuntimeException("_CLKFREQ / _XINFREQ not allowed with RCFAST / RCSLOW");
            }

            scope.addSymbol("_CLKMODE", new NumberLiteral(mode == 2 ? 1 : 0));
            scope.addSymbol("_CLKFREQ", new NumberLiteral(mode == 2 ? 20_000 : 12_000_000));
            return;
        }

        int freqshift = 0;
        if ((mode & 0x7C0) != 0) {
            freqshift = getBitPos(mode >> 6);
        }
        if (xinfreq != null) {
            scope.addSymbol("_CLKFREQ", new NumberLiteral(xinfreq.getNumber().intValue() << freqshift));
        }
    }

    int getBitPos(int value) {
        int bitPos = 0;

        for (int i = 32; i > 0; i--) {
            if ((value & 0x01) != 0) {
                bitPos = 32 - i;
            }
            value >>= 1;
        }

        return bitPos;
    }

    public void generateObjectCode(OutputStream os) throws Exception {
        int address = 0;

        for (Spin1PAsmLine line : source) {
            while (address < line.getScope().getHubAddress()) {
                os.write(0x00);
                address++;
            }
            Spin1InstructionObject obj = line.getInstructionObject();
            byte[] code = obj.getBytes();
            if (code != null && code.length != 0) {
                os.write(code);
                address += code.length;
            }
        }
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

            Spin1PAsmLine pasmLine = new Spin1PAsmLine(localScope, label, condition, mnemonic, parameters, modifier) {

                @Override
                public String toString() {
                    return node.getText();
                }

            };
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
        Spin1Context localScope = new Spin1Context(scope);

        for (Node child : node.getChilds()) {
            compileStatementBlock(localScope, child);
        }
    }

    void compileStatementBlock(Spin1Context scope, Node node) {
        Spin1ExpressionBuilder builder = new Spin1ExpressionBuilder();

        List<Token> tokens = node.getTokens();
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            if (Spin1ExpressionBuilder.operatorPrecedence.containsKey(token.getText().toUpperCase())) {
                builder.addOperatorToken(token);
            }
            else {
                if (i + 1 < tokens.size()) {
                    Token next = tokens.get(i + 1);
                    if (next.type != Token.EOF && next.type != Token.NL) {
                        if ("(".equals(next.getText())) {
                            builder.addFunctionOperatorToken(token, next);
                            i++;
                            continue;
                        }
                    }
                }
                builder.addValueToken(token);
            }
        }

        for (Node child : node.getChilds()) {
            compileStatementBlock(scope, child);
        }
    }

    void writeSource(Node node) {
        for (Node child : node.getChilds()) {
            writeSource(child);
        }

        Spin1BytecodeFactory factory = Spin1BytecodeFactory.symbols.get(node.getToken(0).getText().toUpperCase());
        if (factory == null) {
            factory = new Constant();
        }

        //source.add(new Spin1Line(null, factory, new NumberLiteral(node.getToken(0).getText())));
    }

    public void generateListing(OutputStream os) throws Exception {
        int address = 0;

        int clkfreq = scope.getInteger("_CLKFREQ");
        os.write(String.format("%04X    ", address).getBytes());
        os.write(String.format(" %02X %02X %02X %02X", clkfreq & 0xFF, (clkfreq >> 8) & 0xFF, (clkfreq >> 16) & 0xFF, (clkfreq >> 24) & 0xFF).getBytes());
        os.write((" | " + "_CLKFREQ\n").getBytes());
        address += 4;

        int clkmode = scope.getInteger("_CLKMODE");
        os.write(String.format("%04X    ", address).getBytes());
        os.write(String.format(" %02X         ", clkmode & 0xFF).getBytes());
        os.write((" | " + "_CLKMODE\n").getBytes());
        address += 1;

        os.write(String.format("%04X    ", address).getBytes());
        os.write(String.format(" %02X         ", 0x00).getBytes());
        os.write((" | " + "Placeholder for checksum\n").getBytes());
        address += 1;

        os.write(String.format("%04X    ", address).getBytes());
        os.write(String.format(" %02X %02X      ", pbase & 0xFF, (pbase >> 8) & 0xFF).getBytes());
        os.write((" | " + "PBASE\n").getBytes());
        address += 2;

        os.write(String.format("%04X    ", address).getBytes());
        os.write(String.format(" %02X %02X      ", vbase & 0xFF, (vbase >> 8) & 0xFF).getBytes());
        os.write((" | " + "VBASE\n").getBytes());
        address += 2;

        os.write(String.format("%04X    ", address).getBytes());
        os.write(String.format(" %02X %02X      ", dbase & 0xFF, (dbase >> 8) & 0xFF).getBytes());
        os.write((" | " + "DBASE\n").getBytes());
        address += 2;

        os.write(String.format("%04X    ", address).getBytes());
        os.write(String.format(" %02X %02X      ", pcurr & 0xFF, (pcurr >> 8) & 0xFF).getBytes());
        os.write((" | " + "PCURR\n").getBytes());
        address += 2;

        os.write(String.format("%04X    ", address).getBytes());
        os.write(String.format(" %02X %02X      ", dcurr & 0xFF, (dcurr >> 8) & 0xFF).getBytes());
        os.write((" | " + "DCURR\n").getBytes());
        address += 2;

        os.write("\n".getBytes());

        int size = vbase - pbase;
        os.write(String.format("%04X    ", address).getBytes());
        os.write(String.format(" %02X %02X      ", size & 0xFF, (size >> 8) & 0xFF).getBytes());
        os.write((" | " + "Object size\n").getBytes());
        address += 2;

        int count = methods.size() + 1;
        os.write(String.format("%04X    ", address).getBytes());
        os.write(String.format(" %02X         ", count & 0xFF).getBytes());
        os.write((" | " + "Method count + 1\n").getBytes());
        address += 1;

        int objects = 0;
        os.write(String.format("%04X    ", address).getBytes());
        os.write(String.format(" %02X         ", objects & 0xFF).getBytes());
        os.write((" | " + "OBJ count\n").getBytes());
        address += 1;

        for (Spin1Method line : methods) {
            int faddr = line.getScope().getAddress();
            os.write(String.format("%04X    ", address).getBytes());
            os.write(String.format(" %02X %02X %02X %02X", faddr & 0xFF, (faddr >> 8) & 0xFF, line.getLocalSize() & 0xFF, (line.getLocalSize() >> 8) & 0xFF).getBytes());
            os.write((" | " + "Function\n").getBytes());
            address += 4;
        }

        os.write("\n".getBytes());

        for (Spin1PAsmLine line : source) {
            while (address < line.getScope().getHubAddress()) {
                os.write(0x00);
                address++;
            }
            Spin1InstructionObject obj = line.getInstructionObject();
            byte[] code = obj.getBytes();

            os.write(String.format("%04X %03X", address, line.getScope().getInteger("$")).getBytes());

            int i = 0;
            if (code != null && code.length != 0) {
                while (i < code.length && i < 4) {
                    os.write(String.format(" %02X", code[i++]).getBytes());
                }
            }
            while (i < 4) {
                os.write("   ".getBytes());
                i++;
            }
            os.write((" | " + line.toString()).getBytes());

            if (code != null) {
                while (i < code.length) {
                    if ((i % 4) == 0) {
                        os.write(String.format("\n%04X    ", address + i).getBytes());
                    }
                    os.write(String.format(" %02X", code[i++]).getBytes());
                }
                address += code.length;
                os.write("\n".getBytes());
            }
        }

        os.write("\n".getBytes());

        for (Spin1Method line : methods) {
            byte[] code = line.getBytes();

            os.write(String.format("%04X    ", address).getBytes());

            int i = 0;
            if (code != null && code.length != 0) {
                while (i < code.length && i < 4) {
                    os.write(String.format(" %02X", code[i++]).getBytes());
                }
            }
            while (i < 4) {
                os.write("   ".getBytes());
                i++;
            }
            os.write((" | ").getBytes());

            if (code != null) {
                while (i < code.length) {
                    if ((i % 4) == 0) {
                        os.write(String.format("\n%04X    ", address + i).getBytes());
                    }
                    os.write(String.format(" %02X", code[i++]).getBytes());
                }
                address += code.length;
                while ((i % 4) != 0) {
                    os.write("   ".getBytes());
                    i++;
                }
                os.write(" |\n".getBytes());
            }
        }
    }

    public static void main(String[] args) {
        String text = ""
            + "PUB main\n"
            + "\n"
            + "    coginit(cogid, @start, 0)\n"
            + "\n"
            + "DAT             org     $000\n"
            + "\n"
            + "start           cogid   a\n"
            + "                cogstop a\n"
            + "\n"
            + "a               res     1";

        try {
            Spin1TokenStream stream = new Spin1TokenStream(text);
            Spin1Parser subject = new Spin1Parser(stream);
            Node root = subject.parse();
            print(root, 0);

            Spin1Compiler compiler = new Spin1Compiler();
            compiler.compile(root);

            //compiler.generateObjectCode(new ByteArrayOutputStream());
            compiler.generateListing(System.out);

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
