/*
 * Copyright (c) 2022 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.model.Token;

public class Spin2PAsmDebugLine {

    public static class Spin2DebugCommand {

        final Token token;
        final List<Spin2DebugExpression> arguments;

        public Spin2DebugCommand(Token token) {
            this.token = token;
            this.arguments = new ArrayList<Spin2DebugExpression>();
        }

        public Spin2DebugCommand(Token token, List<Spin2DebugExpression> arguments) {
            this.token = token;
            this.arguments = arguments;
        }

        public String getText() {
            return token.getText();
        }

        public int getType() {
            return token.type;
        }

        public Token getToken() {
            return token;
        }

        public void addArgument(Spin2DebugExpression arg) {
            arguments.add(arg);
        }

        public int getArgumentsCount() {
            return arguments.size();
        }

        public Spin2DebugExpression getArgument(int index) {
            return arguments.get(index);
        }

        public List<Spin2DebugExpression> getArguments() {
            return arguments;
        }

    }

    public static class Spin2DebugExpression {

        final String prefix;
        final Expression expression;

        public Spin2DebugExpression(String prefix, Expression expression) {
            this.prefix = prefix;
            this.expression = expression;
        }

        public String getPrefix() {
            return prefix;
        }

        public Expression getExpression() {
            return expression;
        }

        public boolean isImmediate() {
            return "#".equals(prefix);
        }

        @Override
        public String toString() {
            return (prefix != null ? prefix : "") + expression.toString();
        }

    }

    final Spin2Context context;
    final List<Spin2DebugCommand> statements = new ArrayList<Spin2DebugCommand>();

    public static Spin2PAsmDebugLine buildFrom(Spin2Context context, List<Token> tokens) {
        int index = 0;
        int state = 0;
        Spin2DebugCommand child = null;
        List<Token> list = null;

        Token token = tokens.get(index++);
        if (!"DEBUG".equalsIgnoreCase(token.getText())) {
            throw new CompilerException("invalid statement '" + token.getText() + "'", token);
        }
        Spin2PAsmDebugLine root = new Spin2PAsmDebugLine(context);

        token = tokens.get(index++);
        if (!"(".equals(token.getText())) {
            throw new CompilerException("expected '(' got '" + token.getText() + "'", token);
        }

        while (index < tokens.size() - 1) {
            token = tokens.get(index++);
            switch (state) {
                case 0:
                    if (token.type == Token.OPERATOR) {
                        throw new CompilerException("unexpected operator '" + token.getText() + "'", token);
                    }
                    if (token.type == Token.STRING) {
                        root.addStatement(new Spin2DebugCommand(token));
                        state = 3;
                        break;
                    }
                    if (token.type != 0) {
                        root.addStatement(new Spin2DebugCommand(token));
                        break;
                    }
                    root.addStatement(child = new Spin2DebugCommand(token));
                    state = 1;
                    break;
                case 1:
                    if (!"(".equals(token.getText())) {
                        throw new CompilerException("expected '(' got '" + token.getText() + "'", token);
                    }
                    list = new ArrayList<Token>();
                    state = 2;
                    break;
                case 2:
                    if (",".equals(token.getText()) || ")".equals(token.getText())) {
                        if (list.size() == 0) {
                            throw new CompilerException("expecting argument", token);
                        }

                        String prefix = null;
                        if ("#".equals(list.get(0).getText())) {
                            prefix = list.get(0).getText();
                            list = list.subList(1, list.size());
                        }
                        Spin2ExpressionBuilder expressionBuilder = new Spin2ExpressionBuilder(context, list);
                        child.addArgument(new Spin2DebugExpression(prefix, expressionBuilder.getExpression()));

                        list = new ArrayList<Token>();

                        if (")".equals(token.getText())) {
                            state = 3;
                        }
                        break;
                    }
                    list.add(token);
                    break;
                case 3:
                    if (",".equals(token.getText())) {
                        state = 0;
                        break;
                    }
                    throw new CompilerException("unexpected '" + token.getText() + "'", token);
            }
        }

        if (index >= tokens.size()) {
            throw new CompilerException("expected ')'", token);
        }
        token = tokens.get(index++);
        if (!")".equals(token.getText())) {
            throw new CompilerException("expected ')' got '" + token.getText() + "'", token);
        }

        return root;
    }

    public Spin2PAsmDebugLine(Spin2Context context) {
        this.context = context;
    }

    public void addStatement(Spin2DebugCommand statement) {
        statements.add(statement);
    }

    public int getStatementsCount() {
        return statements.size();
    }

    public Spin2DebugCommand getStatement(int index) {
        return statements.get(index);
    }

    public List<Spin2DebugCommand> getStatements() {
        return statements;
    }

    public static void main(String[] args) {
        String text;

        text = "debug(if(a>0), \"text\", udec(a,b,#c), udec_reg_array(@a,b), \"text\")";
        System.out.println(text);
        System.out.println(parse(text));
    }

    static String parse(String text) {
        List<Token> tokens = new ArrayList<Token>();

        Spin2TokenStream stream = new Spin2TokenStream(text);
        while (true) {
            Token token = stream.nextToken();
            if (token.type == Token.EOF) {
                break;
            }
            tokens.add(token);
        }

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(new Spin2Context(), tokens);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        print(new PrintStream(os), root, 0);
        return os.toString();
    }

    static void print(PrintStream out, Spin2PAsmDebugLine node, int indent) {
        for (Spin2DebugCommand child : node.getStatements()) {
            out.println(child.getText());
            for (Spin2DebugExpression arg : child.getArguments()) {
                printIndent(out, 1);
                out.println(arg);
            }
        }
    }

    static void printIndent(PrintStream out, int indent) {
        if (indent != 0) {
            for (int i = 1; i < indent; i++) {
                out.print("     ");
            }
            out.print(" +-- ");
        }
    }

}
