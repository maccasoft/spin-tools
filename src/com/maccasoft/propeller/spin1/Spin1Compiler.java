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

    ExpressionBuilder expressionBuilder = new ExpressionBuilder();

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

    }

    void compile(Node root) {
        int address = 0, hubAddress = 0;

        for (Node node : root.getChilds()) {
            if (node instanceof ConstantsNode) {
                compileConBlock(node);
            }
        }
        for (Node node : root.getChilds()) {
            if (node instanceof DataNode) {
                compileDatBlock(node);
            }
        }

        for (Spin1PAsmLine line : source) {
            try {
                line.getScope().setHubAddress(hubAddress);
                address = line.resolve(address);
                hubAddress += line.getInstructionObject().getSize();
            } catch (Exception e) {
                System.err.println(line);
                e.printStackTrace();
            }
        }
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

    public static void main(String[] args) {
        String text = ""
            + "DAT\n"
            + "\n"
            + "start           add     1,2\n"
            + "        if_nz   add     1,2\n"
            + "                add     1,2     wc\n"
            + "                add     1,2     wz\n"
            + "                add     1,2     wc,wz\n"
            + "                add     1,2     wc,wz,nr\n"
            + "";

        try {
            Spin1TokenStream stream = new Spin1TokenStream(text);
            Spin1Parser subject = new Spin1Parser(stream);
            Node root = subject.parse();
            print(root, 0);

            Spin1Compiler compiler = new Spin1Compiler();
            compiler.compile(root);
            compiler.generateObjectCode(new ByteArrayOutputStream());

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
