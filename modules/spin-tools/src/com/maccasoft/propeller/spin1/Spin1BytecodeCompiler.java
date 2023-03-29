/*
 * Copyright (c) 2023 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.expressions.Add;
import com.maccasoft.propeller.expressions.And;
import com.maccasoft.propeller.expressions.CharacterLiteral;
import com.maccasoft.propeller.expressions.ContextLiteral;
import com.maccasoft.propeller.expressions.DataVariable;
import com.maccasoft.propeller.expressions.Decod;
import com.maccasoft.propeller.expressions.Divide;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.LocalVariable;
import com.maccasoft.propeller.expressions.Method;
import com.maccasoft.propeller.expressions.Modulo;
import com.maccasoft.propeller.expressions.Multiply;
import com.maccasoft.propeller.expressions.Negative;
import com.maccasoft.propeller.expressions.Not;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.expressions.ObjectContextLiteral;
import com.maccasoft.propeller.expressions.Or;
import com.maccasoft.propeller.expressions.Register;
import com.maccasoft.propeller.expressions.ShiftLeft;
import com.maccasoft.propeller.expressions.ShiftRight;
import com.maccasoft.propeller.expressions.SpinObject;
import com.maccasoft.propeller.expressions.Subtract;
import com.maccasoft.propeller.expressions.Trunc;
import com.maccasoft.propeller.expressions.Variable;
import com.maccasoft.propeller.expressions.Xor;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.spin1.Spin1Bytecode.Descriptor;
import com.maccasoft.propeller.spin1.bytecode.Address;
import com.maccasoft.propeller.spin1.bytecode.Bytecode;
import com.maccasoft.propeller.spin1.bytecode.CallSub;
import com.maccasoft.propeller.spin1.bytecode.Constant;
import com.maccasoft.propeller.spin1.bytecode.Jmp;
import com.maccasoft.propeller.spin1.bytecode.Jz;
import com.maccasoft.propeller.spin1.bytecode.MathOp;
import com.maccasoft.propeller.spin1.bytecode.MemoryOp;
import com.maccasoft.propeller.spin1.bytecode.MemoryRef;
import com.maccasoft.propeller.spin1.bytecode.RegisterBitOp;
import com.maccasoft.propeller.spin1.bytecode.RegisterOp;
import com.maccasoft.propeller.spin1.bytecode.VariableOp;

public abstract class Spin1BytecodeCompiler {

    boolean openspinCompatible;

    List<Spin1Bytecode> stringData;

    Spin1BytecodeCompiler() {
        this.stringData = new ArrayList<>();
    }

    public Spin1BytecodeCompiler(boolean openspinCompatible) {
        this.openspinCompatible = openspinCompatible;
        this.stringData = new ArrayList<>();
    }

    public List<Spin1Bytecode> compileBytecodeExpression(Spin1Context context, Spin1Method method, Spin1StatementNode node) {
        return compileBytecodeExpression(context, method, node, false);
    }

    List<Spin1Bytecode> compileBytecodeExpression(Spin1Context context, Spin1Method method, Spin1StatementNode node, boolean push) {
        List<Spin1Bytecode> source = new ArrayList<Spin1Bytecode>();

        try {
            Descriptor desc = Spin1Bytecode.getDescriptor(node.getText());
            if (desc != null) {
                if (node.getChildCount() != desc.parameters) {
                    throw new RuntimeException("expected " + desc.parameters + " argument(s), found " + node.getChildCount());
                }
                if (push && desc.code_push == null) {
                    throw new RuntimeException("function " + node.getText() + " doesn't return a value");
                }
                for (int i = 0; i < desc.parameters; i++) {
                    source.addAll(compileBytecodeExpression(context, method, node.getChild(i), true));
                }
                source.add(new Bytecode(context, push ? desc.code_push : desc.code, node.getText()));
            }
            else if ("CONSTANT".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("expected " + 1 + " argument(s), found " + node.getChildCount());
                }
                try {
                    Expression expression = buildConstantExpression(context, node.getChild(0));
                    if (!expression.isConstant()) {
                        throw new CompilerException("expression is not constant", node.getChild(0).getToken());
                    }
                    source.add(new Constant(context, expression, openspinCompatible));
                } catch (Exception e) {
                    throw new CompilerException("expression is not constant", node.getChild(0).getToken());
                }
            }
            else if ("TRUNC".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("expected " + 1 + " argument(s), found " + node.getChildCount());
                }
                try {
                    Expression expression = buildConstantExpression(context, node.getChild(0));
                    if (!expression.isConstant()) {
                        throw new CompilerException("expression is not constant", node.getChild(0).getToken());
                    }
                    source.add(new Constant(context, new Trunc(expression), openspinCompatible));
                } catch (Exception e) {
                    throw new CompilerException("expression is not constant", node.getChild(0).getToken());
                }
            }
            else if ("CHIPVER".equalsIgnoreCase(node.getText())) {
                source.add(new Bytecode(context, new byte[] {
                    (byte) 0x34, (byte) 0x80
                }, "CHIPVER"));
            }
            else if ("CLKFREQ".equalsIgnoreCase(node.getText())) {
                source.add(new Constant(context, new NumberLiteral(0), openspinCompatible));
                source.add(new MemoryOp(context, MemoryOp.Size.Long, false, MemoryOp.Base.Pop, MemoryOp.Op.Read, null));
            }
            else if ("CLKMODE".equalsIgnoreCase(node.getText())) {
                source.add(new Address(context, new NumberLiteral(4)));
                source.add(new MemoryOp(context, MemoryOp.Size.Byte, false, MemoryOp.Base.Pop, MemoryOp.Op.Read, null));
            }
            else if ("COGID".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 0) {
                    throw new RuntimeException("expected " + 0 + " argument(s), found " + node.getChildCount());
                }
                source.add(new RegisterOp(context, RegisterOp.Op.Read, 0x1E9));
            }
            else if ("COGNEW".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 2) {
                    throw new RuntimeException("expected " + 2 + " argument(s), found " + node.getChildCount());
                }
                Expression expression = context.getLocalSymbol(node.getChild(0).getText());
                if (expression instanceof Method) {
                    Spin1StatementNode methodNode = node.getChild(0);
                    if (methodNode.getChildCount() != ((Method) expression).getArgumentsCount()) {
                        throw new RuntimeException("expected " + ((Method) expression).getArgumentsCount() + " argument(s), found " + methodNode.getChildCount());
                    }
                    for (int i = 0; i < methodNode.getChildCount(); i++) {
                        source.addAll(compileBytecodeExpression(context, method, methodNode.getChild(i), true));
                    }
                    source.add(new Constant(context, new NumberLiteral(0, 16) {

                        @Override
                        public Number getNumber() {
                            return (methodNode.getChildCount() << 8) | ((Method) expression).getIndex() + 1;
                        }

                    }, openspinCompatible));
                    source.addAll(compileBytecodeExpression(context, method, node.getChild(1), true));
                    source.add(new Bytecode(context, 0x15, "MARK_INTERPRETED"));
                }
                else {
                    source.add(new Constant(context, new NumberLiteral(-1), openspinCompatible));
                    source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));
                    source.addAll(compileBytecodeExpression(context, method, node.getChild(1), true));
                }
                desc = Spin1Bytecode.getDescriptor("COGINIT");
                source.add(new Bytecode(context, push ? desc.code_push : desc.code, node.getText()));
            }
            else if ("LOOKDOWN".equalsIgnoreCase(node.getText()) || "LOOKDOWNZ".equalsIgnoreCase(node.getText()) || "LOOKUP".equalsIgnoreCase(node.getText())
                || "LOOKUPZ".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() == 0) {
                    throw new RuntimeException("expected argument(s), found none");
                }
                Spin1StatementNode argsNode = node.getChild(0);
                if (!":".equalsIgnoreCase(argsNode.getText()) || argsNode.getChildCount() < 2) {
                    throw new RuntimeException("invalid argument(s)");
                }

                int code = 0b00010000;
                if ("LOOKDOWN".equalsIgnoreCase(node.getText()) || "LOOKDOWNZ".equalsIgnoreCase(node.getText())) {
                    code |= 0b00000001;
                }
                int code_range = code | 0b00000010;

                source.add(new Constant(context, new NumberLiteral(node.getText().toUpperCase().endsWith("Z") ? 0 : 1), openspinCompatible));

                Spin1Bytecode end = new Spin1Bytecode(context);
                source.add(new Address(context, new ContextLiteral(end.getContext())));

                source.addAll(compileBytecodeExpression(context, method, argsNode.getChild(0), true));

                for (int i = 1; i < argsNode.getChildCount(); i++) {
                    Spin1StatementNode arg = argsNode.getChild(i);
                    if ("..".equals(arg.getText())) {
                        source.addAll(compileBytecodeExpression(context, method, arg.getChild(0), true));
                        source.addAll(compileBytecodeExpression(context, method, arg.getChild(1), true));
                        source.add(new Bytecode(context, code_range, node.getText().toUpperCase()));
                    }
                    else if (arg.getType() == Token.STRING) {
                        String s = arg.getText().substring(1, arg.getText().length() - 1);
                        for (int x = 0; x < s.length(); x++) {
                            source.add(new Constant(context, new CharacterLiteral(s.substring(x, x + 1)), openspinCompatible));
                            source.add(new Bytecode(context, code, node.getText().toUpperCase()));
                        }
                    }
                    else {
                        source.addAll(compileBytecodeExpression(context, method, arg, true));
                        source.add(new Bytecode(context, code, node.getText().toUpperCase()));
                    }
                }

                source.add(new Bytecode(context, 0b00001111, "LOOKDONE"));
                source.add(end);
            }
            else if ("STRING".equalsIgnoreCase(node.getText())) {
                StringBuilder sb = new StringBuilder();
                for (Spin1StatementNode child : node.getChilds()) {
                    if (child.getType() == Token.STRING) {
                        String s = child.getText().substring(1);
                        sb.append(s.substring(0, s.length() - 1));
                    }
                    else if (child.getType() == Token.NUMBER) {
                        NumberLiteral expression = new NumberLiteral(child.getText());
                        sb.append((char) expression.getNumber().intValue());
                    }
                    else {
                        try {
                            Expression expression = buildConstantExpression(context, child);
                            if (!expression.isConstant()) {
                                throw new CompilerException("expression is not constant", child.getToken());
                            }
                            sb.append((char) expression.getNumber().intValue());
                        } catch (Exception e) {
                            throw new CompilerException("expression is not constant", child.getToken());
                        }
                    }
                }
                sb.append((char) 0x00);
                Spin1Bytecode target = addStringData(new Bytecode(context, sb.toString().getBytes(), "STRING".toUpperCase()));
                source.add(new MemoryRef(context, MemoryRef.Size.Byte, false, MemoryRef.Base.PBase, MemoryRef.Op.Address, new ContextLiteral(target.getContext())));
            }
            else if (node.getType() == Token.NUMBER) {
                Expression expression = new NumberLiteral(node.getText());
                source.add(new Constant(context, expression, openspinCompatible));
            }
            else if (node.getType() == Token.STRING) {
                String s = node.getText();
                if (s.startsWith("@")) {
                    s = s.substring(2, s.length() - 1);
                    s += (char) 0x00;
                    Spin1Bytecode target = addStringData(new Bytecode(context, s.getBytes(), "STRING".toUpperCase()));
                    source.add(new MemoryOp(context, MemoryOp.Size.Byte, false, MemoryOp.Base.PBase, MemoryOp.Op.Address, new ContextLiteral(target.getContext())));
                }
                else {
                    s = s.substring(1, s.length() - 1);
                    if (s.length() == 1) {
                        Expression expression = new CharacterLiteral(s);
                        source.add(new Constant(context, expression, openspinCompatible));
                    }
                    else {
                        s += (char) 0x00;
                        Spin1Bytecode target = addStringData(new Bytecode(context, s.getBytes(), "STRING".toUpperCase()));
                        source.add(new MemoryOp(context, MemoryOp.Size.Byte, false, MemoryOp.Base.PBase, MemoryOp.Op.Address, new ContextLiteral(target.getContext())));
                    }
                }
            }
            else if ("-".equals(node.getText()) && node.getChildCount() == 1) {
                if (node.getChild(0).getToken().type == Token.NUMBER) {
                    Spin1Bytecode bc1 = new Constant(context, new Negative(new NumberLiteral(node.getChild(0).getText())), openspinCompatible);
                    Spin1Bytecode bc2 = new Constant(context, new Subtract(new NumberLiteral(node.getChild(0).getText()), new NumberLiteral(1)), openspinCompatible);
                    if (bc1.getSize() <= (bc2.getSize() + 1)) {
                        source.add(bc1);
                    }
                    else {
                        source.add(bc2);
                        source.add(new Bytecode(context, 0b111_00111 | (push ? 0b10000000 : 0b00000000), "COMPLEMENT"));
                    }
                }
                else {
                    Expression expression = context.getLocalSymbol(node.getChild(0).getText());
                    if (expression != null && expression.isConstant() && push) {
                        source.add(new Constant(context, new Negative(expression), openspinCompatible));
                    }
                    else {
                        if (!push && (expression instanceof Variable)) {
                            source.addAll(leftAssign(context, method, node.getChild(0), true));
                        }
                        else {
                            source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));
                        }
                        source.add(new Bytecode(context, push ? 0b111_00110 : 0b010_00110, "NEGATE"));
                    }
                }
            }
            else if (isAssign(node.getText())) {
                source.addAll(compileBytecodeExpression(context, method, node.getChild(1), true));
                source.addAll(leftAssign(context, method, node.getChild(0), push));
                if (push) {
                    source.add(new Bytecode(context, 0x80, "WRITE"));
                }
            }
            else if (MathOp.isAssignMathOp(node.getText())) {
                source.addAll(compileBytecodeExpression(context, method, node.getChild(1), true));
                source.addAll(leftAssign(context, method, node.getChild(0), true));
                source.add(new MathOp(context, node.getText(), push));
            }
            else if ("||".equals(node.getText()) || "|<".equals(node.getText()) || ">|".equals(node.getText()) || "!".equals(node.getText()) || "^^".equals(node.getText())
                || "NOT".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("expression syntax error " + node.getText());
                }
                if (!push) {
                    source.addAll(leftAssign(context, method, node.getChild(0), true));
                    source.add(new MathOp(context, node.getText() + "=", false));
                }
                else {
                    source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));
                    source.add(new MathOp(context, node.getText(), push));
                }
            }
            else if (MathOp.isMathOp(node.getText())) {
                if (node.getChildCount() != 2) {
                    throw new RuntimeException("expression syntax error " + node.getText());
                }
                if (openspinCompatible) {
                    source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));
                    source.addAll(compileBytecodeExpression(context, method, node.getChild(1), true));
                    source.add(new MathOp(context, node.getText(), push));
                }
                else {
                    try {
                        Expression expression = buildConstantExpression(context, node);
                        source.add(new Constant(context, expression, openspinCompatible));
                    } catch (Exception e) {
                        source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));
                        source.addAll(compileBytecodeExpression(context, method, node.getChild(1), true));
                        source.add(new MathOp(context, node.getText(), push));
                    }
                }
            }
            else if ("?".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() == 1) {
                    Expression expression = context.getLocalSymbol(node.getChild(0).getText());
                    if (expression == null) {
                        throw new RuntimeException("undefined symbol " + node.getChild(0).getText());
                    }
                    if (expression instanceof Variable) {
                        int code = 0b0_00010_00;
                        if (push) {
                            code |= 0b10000000;
                        }
                        source.add(new VariableOp(context, VariableOp.Op.Assign, (Variable) expression));
                        source.add(new Bytecode(context, code, "RANDOM_FORWARD"));
                        ((Variable) expression).setCalledBy(method);
                    }
                }
                else {
                    if (node.getChildCount() != 2) {
                        throw new RuntimeException("expression syntax error " + node.getText());
                    }
                    if (!":".equals(node.getChild(1).getText())) {
                        throw new RuntimeException("expression syntax error " + node.getText());
                    }

                    source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));

                    List<Spin1Bytecode> falseSource = compileBytecodeExpression(context, method, node.getChild(1).getChild(1), true);
                    source.add(new Jz(context, new ContextLiteral(falseSource.get(0).getContext())));
                    source.addAll(compileBytecodeExpression(context, method, node.getChild(1).getChild(0), true));

                    Spin1Bytecode endSource = new Spin1Bytecode(context);
                    source.add(new Jmp(context, new ContextLiteral(endSource.getContext())));

                    source.addAll(falseSource);
                    source.add(endSource);
                }
            }
            else if ("(".equalsIgnoreCase(node.getText())) {
                source.addAll(compileBytecodeExpression(context, method, node.getChild(0), push));
            }
            else if (",".equalsIgnoreCase(node.getText())) {
                for (Spin1StatementNode child : node.getChilds()) {
                    source.addAll(compileBytecodeExpression(context, method, child, push));
                }
            }
            else if ("\\".equalsIgnoreCase(node.getText())) {
                Expression expression = context.getLocalSymbol(node.getChild(0).getText());
                if (!(expression instanceof Method)) {
                    throw new RuntimeException("invalid symbol " + node.getText());
                }

                Spin1StatementNode argsNode = node.getChild(0);
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
                    source.addAll(compileBytecodeExpression(context, method, argsNode.getChild(i), true));
                }
                Method methodExpression = (Method) expression;
                source.add(new CallSub(context, methodExpression));
                Spin1Method calledMethod = (Spin1Method) methodExpression.getData(Spin1Method.class.getName());
                calledMethod.setCalledBy(method);
            }
            else if ("++".equalsIgnoreCase(node.getText()) || "--".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 1) {
                    throw new CompilerException("expression syntax error", node.getToken());
                }
                Spin1Bytecode.Type type = Spin1Bytecode.Type.Long;
                Spin1StatementNode childNode = node.getChild(0);
                if ("BYTE".equalsIgnoreCase(childNode.getText()) || "WORD".equalsIgnoreCase(childNode.getText()) || "LONG".equalsIgnoreCase(childNode.getText())) {
                    boolean popIndex = false;

                    if (childNode.getChildCount() == 0) {
                        throw new RuntimeException("expected index expression");
                    }
                    source.addAll(compileBytecodeExpression(context, method, childNode.getChild(0), true));

                    int n = 1;
                    if (n < childNode.getChildCount()) {
                        source.addAll(compileBytecodeExpression(context, method, childNode.getChild(n++), true));
                        popIndex = true;
                    }
                    if (n < childNode.getChildCount()) {
                        throw new RuntimeException("unexpected expression");
                    }

                    if ("BYTE".equalsIgnoreCase(childNode.getText())) {
                        source.add(new MemoryOp(context, MemoryOp.Size.Byte, popIndex, MemoryOp.Base.Pop, MemoryOp.Op.Assign, null));
                        type = Spin1Bytecode.Type.Byte;
                    }
                    else if ("WORD".equalsIgnoreCase(childNode.getText())) {
                        source.add(new MemoryOp(context, MemoryOp.Size.Word, popIndex, MemoryOp.Base.Pop, MemoryOp.Op.Assign, null));
                        type = Spin1Bytecode.Type.Word;
                    }
                    else if ("LONG".equalsIgnoreCase(childNode.getText())) {
                        source.add(new MemoryOp(context, MemoryOp.Size.Long, popIndex, MemoryOp.Base.Pop, MemoryOp.Op.Assign, null));
                        type = Spin1Bytecode.Type.Long;
                    }
                }
                else {
                    Expression expression = context.getLocalSymbol(childNode.getText());
                    if (expression == null) {
                        throw new RuntimeException("undefined symbol " + childNode.getText());
                    }
                    if (expression instanceof Variable) {
                        type = Spin1Bytecode.Type.fromString(((Variable) expression).getType());
                        if (childNode.getChildCount() != 0) {
                            source.addAll(leftAssign(context, method, node.getChild(0), true));
                        }
                        else {
                            source.add(new VariableOp(context, VariableOp.Op.Assign, (Variable) expression));
                            ((Variable) expression).setCalledBy(method);
                        }
                    }
                    else if (expression instanceof ContextLiteral) {
                        MemoryOp.Size ss = MemoryOp.Size.Long;
                        if (expression instanceof DataVariable) {
                            switch (((DataVariable) expression).getType()) {
                                case "BYTE":
                                    ss = MemoryOp.Size.Byte;
                                    type = Spin1Bytecode.Type.Word;
                                    break;
                                case "WORD":
                                    ss = MemoryOp.Size.Word;
                                    type = Spin1Bytecode.Type.Byte;
                                    break;
                            }
                        }
                        if (childNode.getChildCount() != 0) {
                            source.addAll(leftAssign(context, method, node.getChild(0), true));
                        }
                        else {
                            source.add(new MemoryOp(context, ss, false, MemoryOp.Base.PBase, MemoryOp.Op.Assign, expression));
                        }
                    }
                    else {
                        throw new CompilerException("unsupported operation on " + node.getChild(0).getText(), node.getChild(0).getToken());
                    }
                }
                if ("++".equalsIgnoreCase(node.getText())) {
                    int code = Spin1Bytecode.op_ss.setValue(0b0_0100_000, type.ordinal() + 1);
                    source.add(new Bytecode(context, Spin1Bytecode.op_p.setBoolean(code, push), "PRE_INC"));
                }
                else if ("--".equalsIgnoreCase(node.getText())) {
                    int code = Spin1Bytecode.op_ss.setValue(0b0_0110_000, type.ordinal() + 1);
                    source.add(new Bytecode(context, Spin1Bytecode.op_p.setBoolean(code, push), "PRE_DEC"));
                }
            }
            else if ("~".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("expression syntax error");
                }
                source.addAll(leftAssign(context, method, node.getChild(0), true));
                int code = 0b0_00100_00;
                if (push) {
                    code |= 0b10000000;
                }
                source.add(new Bytecode(context, code, "SIGN_EXTEND_BYTE"));
            }
            else if ("~~".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("expression syntax error");
                }
                source.addAll(leftAssign(context, method, node.getChild(0), true));
                int code = 0b0_00101_00;
                if (push) {
                    code |= 0b10000000;
                }
                source.add(new Bytecode(context, code, "SIGN_EXTEND_WORD"));
            }
            else if ("..".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 2) {
                    throw new RuntimeException("expression syntax error");
                }
                source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));
                source.addAll(compileBytecodeExpression(context, method, node.getChild(1), true));
            }
            else if ("BYTE".equalsIgnoreCase(node.getText()) || "WORD".equalsIgnoreCase(node.getText()) || "LONG".equalsIgnoreCase(node.getText())) {
                boolean popIndex = false;
                Spin1StatementNode postEffectNode = null;

                if (node.getChildCount() == 0) {
                    throw new RuntimeException("expected index expression");
                }
                source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));

                int n = 1;
                if (n < node.getChildCount()) {
                    if (!isPostEffect(node.getChild(n))) {
                        source.addAll(compileBytecodeExpression(context, method, node.getChild(n++), true));
                        popIndex = true;
                    }
                }
                if (n < node.getChildCount()) {
                    if (isPostEffect(node.getChild(n))) {
                        postEffectNode = node.getChild(n++);
                    }
                }
                if (n < node.getChildCount()) {
                    throw new RuntimeException("unexpected expression");
                }

                MemoryOp.Op op = push ? MemoryOp.Op.Read : MemoryOp.Op.Write;
                if (postEffectNode != null) {
                    op = MemoryOp.Op.Assign;
                }
                if ("BYTE".equalsIgnoreCase(node.getText())) {
                    source.add(new MemoryOp(context, MemoryOp.Size.Byte, popIndex, MemoryOp.Base.Pop, op, null));
                }
                else if ("WORD".equalsIgnoreCase(node.getText())) {
                    source.add(new MemoryOp(context, MemoryOp.Size.Word, popIndex, MemoryOp.Base.Pop, op, null));
                }
                else if ("LONG".equalsIgnoreCase(node.getText())) {
                    source.add(new MemoryOp(context, MemoryOp.Size.Long, popIndex, MemoryOp.Base.Pop, op, null));
                }

                if (postEffectNode != null) {
                    if ("++".equals(postEffectNode.getText())) {
                        int code = 0b0_0101_11_0;
                        if (push) {
                            code |= 0b10000000;
                        }
                        source.add(new Bytecode(context, code, "POST_INC"));
                    }
                    else if ("--".equals(postEffectNode.getText())) {
                        int code = 0b0_0111_11_0;
                        if (push) {
                            code |= 0b10000000;
                        }
                        source.add(new Bytecode(context, code, "POST_DEC"));
                    }
                    else if ("~".equals(postEffectNode.getText())) {
                        int code = 0b0_00110_00;
                        if (push) {
                            code |= 0b10000000;
                        }
                        source.add(new Bytecode(context, code, "POST_CLEAR"));
                    }
                    else if ("~~".equals(postEffectNode.getText())) {
                        int code = 0b0_00111_00;
                        if (push) {
                            code |= 0b10000000;
                        }
                        source.add(new Bytecode(context, code, "POST_SET"));
                    }
                    else if ("?".equals(postEffectNode.getText())) {
                        int code = 0b0_00011_00;
                        if (push) {
                            code |= 0b10000000;
                        }
                        source.add(new Bytecode(context, code, "RANDOM_REVERSE"));
                    }
                }
            }
            else if ("@BYTE".equalsIgnoreCase(node.getText()) || "@WORD".equalsIgnoreCase(node.getText()) || "@LONG".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() == 0) {
                    throw new RuntimeException("expected index expression");
                }
                source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));

                int n = 1;
                boolean popIndex = false;
                if (n < node.getChildCount()) {
                    if (!isPostEffect(node.getChild(n))) {
                        source.addAll(compileBytecodeExpression(context, method, node.getChild(n++), true));
                        popIndex = true;
                    }
                }
                if (n < node.getChildCount()) {
                    throw new RuntimeException("unexpected expression");
                }

                MemoryOp.Size ss = MemoryOp.Size.Byte;
                if ("@WORD".equalsIgnoreCase(node.getText())) {
                    ss = MemoryOp.Size.Word;
                }
                else if ("@LONG".equalsIgnoreCase(node.getText())) {
                    ss = MemoryOp.Size.Long;
                }

                source.add(new MemoryOp(context, ss, popIndex, MemoryOp.Base.Pop, MemoryOp.Op.Address, new NumberLiteral(0)));
            }
            else if ("@@BYTE".equalsIgnoreCase(node.getText()) || "@@WORD".equalsIgnoreCase(node.getText()) || "@@LONG".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() == 0) {
                    throw new RuntimeException("expected index expression");
                }
                source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));

                int n = 1;
                boolean popIndex = false;
                if (n < node.getChildCount()) {
                    source.addAll(compileBytecodeExpression(context, method, node.getChild(n++), true));
                    popIndex = true;
                }
                if (n < node.getChildCount()) {
                    throw new RuntimeException("unexpected expression");
                }

                MemoryOp.Size ss = MemoryOp.Size.Byte;
                if ("@@WORD".equalsIgnoreCase(node.getText())) {
                    ss = MemoryOp.Size.Word;
                }
                else if ("@@LONG".equalsIgnoreCase(node.getText())) {
                    ss = MemoryOp.Size.Long;
                }

                source.add(new MemoryOp(context, ss, popIndex, MemoryOp.Base.Pop, MemoryOp.Op.Read, new NumberLiteral(0)));
                source.add(new MemoryOp(context, MemoryOp.Size.Byte, true, MemoryOp.Base.PBase, MemoryOp.Op.Address, new NumberLiteral(0)));
            }
            else {
                String[] s = node.getText().split("[\\.]");
                if (s.length == 2 && ("BYTE".equalsIgnoreCase(s[1]) || "WORD".equalsIgnoreCase(s[1]) || "LONG".equalsIgnoreCase(s[1]))) {
                    Spin1StatementNode postEffect = null;
                    boolean indexed = false;

                    Expression expression = context.getLocalSymbol(s[0]);
                    if (expression == null) {
                        throw new CompilerException("undefined symbol " + node.getText(), node.getToken());
                    }

                    if (node.getChildCount() != 0) {
                        indexed = true;
                        source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));
                        if (node.getChildCount() > 1) {
                            if (isPostEffect(node.getChild(1))) {
                                postEffect = node.getChild(1);
                            }
                        }
                    }

                    MemoryOp.Base bb = MemoryOp.Base.PBase;
                    if (expression instanceof Variable) {
                        bb = (expression instanceof LocalVariable) ? MemoryOp.Base.DBase : MemoryOp.Base.VBase;
                        ((Variable) expression).setCalledBy(method);
                    }

                    MemoryOp.Op op = push ? MemoryOp.Op.Read : MemoryOp.Op.Write;
                    if (postEffect != null) {
                        op = MemoryOp.Op.Assign;
                    }
                    if (s[0].startsWith("@")) {
                        op = MemoryOp.Op.Address;
                    }
                    if ("BYTE".equalsIgnoreCase(s[1])) {
                        source.add(new MemoryOp(context, MemoryOp.Size.Byte, indexed, bb, op, expression));
                    }
                    else if ("WORD".equalsIgnoreCase(s[1])) {
                        source.add(new MemoryOp(context, MemoryOp.Size.Word, indexed, bb, op, expression));
                    }
                    else if ("LONG".equalsIgnoreCase(s[1])) {
                        source.add(new MemoryOp(context, MemoryOp.Size.Long, indexed, bb, op, expression));
                    }

                    if (postEffect != null) {
                        if ("++".equals(postEffect.getText())) {
                            int code = 0b0_0101_11_0;
                            if (push) {
                                code |= 0b10000000;
                            }
                            source.add(new Bytecode(context, code, "POST_INC"));
                        }
                        else if ("--".equals(postEffect.getText())) {
                            int code = 0b0_0111_11_0;
                            if (push) {
                                code |= 0b10000000;
                            }
                            source.add(new Bytecode(context, code, "POST_DEC"));
                        }
                        else if ("~".equals(postEffect.getText())) {
                            int code = 0b0_00110_00;
                            if (push) {
                                code |= 0b10000000;
                            }
                            source.add(new Bytecode(context, code, "POST_CLEAR"));
                        }
                        else if ("~~".equals(postEffect.getText())) {
                            int code = 0b0_00111_00;
                            if (push) {
                                code |= 0b10000000;
                            }
                            source.add(new Bytecode(context, code, "POST_SET"));
                        }
                        else if ("?".equals(postEffect.getText())) {
                            int code = 0b0_00011_00;
                            if (push) {
                                code |= 0b10000000;
                            }
                            source.add(new Bytecode(context, code, "RANDOM_REVERSE"));
                        }
                    }
                }
                else {
                    Expression expression = context.getLocalSymbol(node.getText());
                    if (expression instanceof ObjectContextLiteral) {
                        expression = context.getLocalSymbol(node.getText().substring(1));
                    }
                    if (expression == null) {
                        throw new CompilerException("undefined symbol " + node.getText(), node.getToken());
                    }
                    if (expression instanceof SpinObject) {
                        if (node.getChildCount() != 2) {
                            throw new RuntimeException("syntax error" + node);
                        }
                        if (!(node.getChild(0) instanceof Spin1StatementNode.Index)) {
                            throw new CompilerException("syntax error", node.getChild(0).getToken());
                        }

                        String qualifiedName = node.getText() + node.getChild(1).getText();

                        expression = context.getLocalSymbol(qualifiedName);
                        if (expression != null) {
                            Method methodExpression = (Method) expression;
                            int parameters = methodExpression.getArgumentsCount();
                            if (node.getChild(1).getChildCount() != parameters) {
                                throw new CompilerException("expected " + parameters + " argument(s), found " + node.getChild(1).getChildCount(), node.getToken());
                            }
                            source.add(new Bytecode(context, new byte[] {
                                (byte) (push ? 0b00000000 : 0b00000001),
                            }, "ANCHOR"));
                            for (int i = 0; i < parameters; i++) {
                                source.addAll(compileBytecodeExpression(context, method, node.getChild(1).getChild(i), true));
                            }
                            source.addAll(compileConstantExpression(context, method, node.getChild(0)));
                            source.add(new CallSub(context, methodExpression, true));
                            Spin1Method calledMethod = (Spin1Method) methodExpression.getData(Spin1Method.class.getName());
                            calledMethod.setCalledBy(method);
                            return source;
                        }
                    }
                    else if (isAddress(node.getText())) {
                        boolean popIndex = false;
                        Spin1StatementNode postEffectNode = null;

                        int n = 0;
                        if (n < node.getChildCount()) {
                            if (!isPostEffect(node.getChild(n))) {
                                source.addAll(compileBytecodeExpression(context, method, node.getChild(n++), true));
                                popIndex = true;
                            }
                        }
                        if (n < node.getChildCount()) {
                            if (isPostEffect(node.getChild(n))) {
                                postEffectNode = node.getChild(n++);
                            }
                        }
                        if (n < node.getChildCount()) {
                            throw new RuntimeException("syntax error");
                        }
                        if (expression instanceof Variable) {
                            if (postEffectNode != null) {
                                source.add(new VariableOp(context, VariableOp.Op.Assign, popIndex, (Variable) expression));
                                if ("++".equals(postEffectNode.getText())) {
                                    Spin1Bytecode.Type type = Spin1Bytecode.Type.fromString(((Variable) expression).getType());
                                    int code = Spin1Bytecode.op_ss.setValue(0b0_0101_000, type.ordinal() + 1);
                                    source.add(new Bytecode(context, Spin1Bytecode.op_p.setBoolean(code, push), "POST_INC"));
                                }
                                else if ("--".equals(postEffectNode.getText())) {
                                    Spin1Bytecode.Type type = Spin1Bytecode.Type.fromString(((Variable) expression).getType());
                                    int code = Spin1Bytecode.op_ss.setValue(0b0_0111_000, type.ordinal() + 1);
                                    source.add(new Bytecode(context, Spin1Bytecode.op_p.setBoolean(code, push), "POST_DEC"));
                                }
                                else if ("~".equals(postEffectNode.getText())) {
                                    int code = 0b0_00110_00;
                                    if (push) {
                                        code |= 0b10000000;
                                    }
                                    source.add(new Bytecode(context, code, "POST_CLEAR"));
                                }
                                else if ("~~".equals(postEffectNode.getText())) {
                                    int code = 0b0_00111_00;
                                    if (push) {
                                        code |= 0b10000000;
                                    }
                                    source.add(new Bytecode(context, code, "POST_SET"));
                                }
                                else if ("?".equals(postEffectNode.getText())) {
                                    int code = 0b0_00011_00;
                                    if (push) {
                                        code |= 0b10000000;
                                    }
                                    source.add(new Bytecode(context, code, "RANDOM_REVERSE"));
                                }
                            }
                            else {
                                if (node.getText().startsWith("@@")) {
                                    source.add(new VariableOp(context, VariableOp.Op.Read, popIndex, (Variable) expression));
                                    source.add(new MemoryOp(context, MemoryOp.Size.Byte, true, MemoryOp.Base.PBase, MemoryOp.Op.Address, new NumberLiteral(0)));
                                }
                                else {
                                    source.add(new VariableOp(context, VariableOp.Op.Address, popIndex, (Variable) expression));
                                }
                            }
                            ((Variable) expression).setCalledBy(method);
                        }
                        else if (expression instanceof ObjectContextLiteral) {
                            MemoryOp.Size ss = MemoryOp.Size.Long;
                            switch (((ObjectContextLiteral) expression).getType()) {
                                case "BYTE":
                                    ss = MemoryOp.Size.Byte;
                                    break;
                                case "WORD":
                                    ss = MemoryOp.Size.Word;
                                    break;
                            }
                            source.add(new MemoryOp(context, ss, popIndex, MemoryOp.Base.PBase, MemoryOp.Op.Read, expression));
                            source.add(new MemoryOp(context, MemoryOp.Size.Byte, true, MemoryOp.Base.PBase, MemoryOp.Op.Address, new NumberLiteral(0)));
                        }
                        else if (expression instanceof ContextLiteral) {
                            MemoryOp.Size ss = MemoryOp.Size.Long;
                            if (expression instanceof DataVariable) {
                                switch (((DataVariable) expression).getType()) {
                                    case "BYTE":
                                        ss = MemoryOp.Size.Byte;
                                        break;
                                    case "WORD":
                                        ss = MemoryOp.Size.Word;
                                        break;
                                }
                            }
                            if (postEffectNode != null) {
                                source.add(new MemoryOp(context, ss, popIndex, MemoryOp.Base.PBase, MemoryOp.Op.Assign, expression));
                                if ("++".equals(postEffectNode.getText())) {
                                    int code = Spin1Bytecode.op_ss.setValue(0b0_0101_000, ss.ordinal() + 1);
                                    source.add(new Bytecode(context, Spin1Bytecode.op_p.setBoolean(code, push), "POST_INC"));
                                }
                                else if ("--".equals(postEffectNode.getText())) {
                                    int code = Spin1Bytecode.op_ss.setValue(0b0_0111_000, ss.ordinal() + 1);
                                    source.add(new Bytecode(context, Spin1Bytecode.op_p.setBoolean(code, push), "POST_DEC"));
                                }
                                else if ("~".equals(postEffectNode.getText())) {
                                    int code = 0b0_00110_00;
                                    if (push) {
                                        code |= 0b10000000;
                                    }
                                    source.add(new Bytecode(context, code, "POST_CLEAR"));
                                }
                                else if ("~~".equals(postEffectNode.getText())) {
                                    int code = 0b0_00111_00;
                                    if (push) {
                                        code |= 0b10000000;
                                    }
                                    source.add(new Bytecode(context, code, "POST_SET"));
                                }
                                else if ("?".equals(postEffectNode.getText())) {
                                    int code = 0b0_00011_00;
                                    if (push) {
                                        code |= 0b10000000;
                                    }
                                    source.add(new Bytecode(context, code, "RANDOM_REVERSE"));
                                }
                            }
                            else {
                                source.add(new MemoryOp(context, ss, popIndex, MemoryOp.Base.PBase, MemoryOp.Op.Address, expression));
                            }
                        }
                        else {
                            throw new CompilerException("syntax error", node.getToken());
                        }
                    }
                    else if (expression instanceof Register) {
                        if (node.getChildCount() != 0) {
                            boolean range = "..".equals(node.getChild(0).getText());
                            source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));
                            if (node.getChildCount() == 2) {
                                source.add(new RegisterBitOp(context, RegisterBitOp.Op.Assign, range, expression.getNumber().intValue()));
                                if ("~".equalsIgnoreCase(node.getChild(1).getText())) {
                                    source.add(new Bytecode(context, 0b0_00110_00, "POST_CLEAR"));
                                }
                                else if ("~~".equalsIgnoreCase(node.getChild(1).getText())) {
                                    source.add(new Bytecode(context, 0b0_00111_00, "POST_SET"));
                                }
                                else {
                                    throw new CompilerException("invalid operator", node.getToken());
                                }
                            }
                            else {
                                source.add(new RegisterBitOp(context, push ? RegisterBitOp.Op.Read : RegisterBitOp.Op.Write, range, expression.getNumber().intValue()));
                            }
                        }
                        else {
                            source.add(new RegisterOp(context, RegisterOp.Op.Read, expression.getNumber().intValue()));
                        }
                    }
                    else if (expression instanceof Variable) {
                        boolean popIndex = false;
                        Spin1StatementNode postEffectNode = null;

                        int n = 0;
                        if (n < node.getChildCount() && (node.getChild(n) instanceof Spin1StatementNode.Index)) {
                            source.addAll(compileBytecodeExpression(context, method, node.getChild(n++), true));
                            popIndex = true;
                        }
                        if (n < node.getChildCount() && isPostEffect(node.getChild(n))) {
                            postEffectNode = node.getChild(n++);
                        }
                        if (n < node.getChildCount()) {
                            throw new CompilerException("unexpected " + node.getChild(n).getText(), node.getChild(n).getToken());
                        }

                        if (postEffectNode != null) {
                            source.add(new VariableOp(context, VariableOp.Op.Assign, popIndex, (Variable) expression));
                            if ("++".equals(postEffectNode.getText())) {
                                Spin1Bytecode.Type type = Spin1Bytecode.Type.fromString(((Variable) expression).getType());
                                int code = Spin1Bytecode.op_ss.setValue(0b0_0101_000, type.ordinal() + 1);
                                source.add(new Bytecode(context, Spin1Bytecode.op_p.setBoolean(code, push), "POST_INC"));
                            }
                            else if ("--".equals(postEffectNode.getText())) {
                                Spin1Bytecode.Type type = Spin1Bytecode.Type.fromString(((Variable) expression).getType());
                                int code = Spin1Bytecode.op_ss.setValue(0b0_0111_000, type.ordinal() + 1);
                                source.add(new Bytecode(context, Spin1Bytecode.op_p.setBoolean(code, push), "POST_DEC"));
                            }
                            else if ("~".equals(postEffectNode.getText())) {
                                int code = 0b0_00110_00;
                                if (push) {
                                    code |= 0b10000000;
                                }
                                source.add(new Bytecode(context, code, "POST_CLEAR"));
                            }
                            else if ("~~".equals(postEffectNode.getText())) {
                                int code = 0b0_00111_00;
                                if (push) {
                                    code |= 0b10000000;
                                }
                                source.add(new Bytecode(context, code, "POST_SET"));
                            }
                            else if ("?".equals(postEffectNode.getText())) {
                                int code = 0b0_00011_00;
                                if (push) {
                                    code |= 0b10000000;
                                }
                                source.add(new Bytecode(context, code, "RANDOM_REVERSE"));
                            }
                        }
                        else {
                            source.add(new VariableOp(context, VariableOp.Op.Read, popIndex, (Variable) expression));
                        }
                        ((Variable) expression).setCalledBy(method);
                    }
                    else if (expression instanceof ContextLiteral) {
                        boolean popIndex = false;
                        Spin1StatementNode postEffectNode = null;

                        int n = 0;
                        if (n < node.getChildCount() && (node.getChild(n) instanceof Spin1StatementNode.Index)) {
                            source.addAll(compileBytecodeExpression(context, method, node.getChild(n++), true));
                            popIndex = true;
                        }
                        if (n < node.getChildCount() && isPostEffect(node.getChild(n))) {
                            postEffectNode = node.getChild(n++);
                        }
                        if (n < node.getChildCount()) {
                            throw new CompilerException("unexpected " + node.getChild(n).getText(), node.getChild(n).getToken());
                        }

                        MemoryOp.Size ss = MemoryOp.Size.Long;
                        if (expression instanceof DataVariable) {
                            switch (((DataVariable) expression).getType()) {
                                case "BYTE":
                                    ss = MemoryOp.Size.Byte;
                                    break;
                                case "WORD":
                                    ss = MemoryOp.Size.Word;
                                    break;
                            }
                        }

                        if (postEffectNode != null) {
                            source.add(new MemoryOp(context, ss, popIndex, MemoryOp.Base.PBase, MemoryOp.Op.Assign, expression));
                            if ("++".equals(postEffectNode.getText())) {
                                int code = Spin1Bytecode.op_ss.setValue(0b0_0101_000, ss.ordinal() + 1);
                                source.add(new Bytecode(context, Spin1Bytecode.op_p.setBoolean(code, push), "POST_INC"));
                            }
                            else if ("--".equals(postEffectNode.getText())) {
                                int code = Spin1Bytecode.op_ss.setValue(0b0_0111_000, ss.ordinal() + 1);
                                source.add(new Bytecode(context, Spin1Bytecode.op_p.setBoolean(code, push), "POST_DEC"));
                            }
                            else if ("~".equals(postEffectNode.getText())) {
                                int code = 0b0_00110_00;
                                if (push) {
                                    code |= 0b10000000;
                                }
                                source.add(new Bytecode(context, code, "POST_CLEAR"));
                            }
                            else if ("~~".equals(postEffectNode.getText())) {
                                int code = 0b0_00111_00;
                                if (push) {
                                    code |= 0b10000000;
                                }
                                source.add(new Bytecode(context, code, "POST_SET"));
                            }
                            else if ("?".equals(postEffectNode.getText())) {
                                int code = 0b0_00011_00;
                                if (push) {
                                    code |= 0b10000000;
                                }
                                source.add(new Bytecode(context, code, "RANDOM_REVERSE"));
                            }
                        }
                        else {
                            source.add(new MemoryOp(context, ss, popIndex, MemoryOp.Base.PBase, MemoryOp.Op.Read, expression));
                        }
                    }
                    else if (expression instanceof Method) {
                        Method methodExpression = (Method) expression;
                        int parameters = methodExpression.getArgumentsCount();
                        if (node.getChildCount() != parameters) {
                            throw new CompilerException("expected " + parameters + " argument(s), found " + node.getChildCount(), node.getToken());
                        }
                        source.add(new Bytecode(context, new byte[] {
                            (byte) (push ? 0b00000000 : 0b00000001),
                        }, "ANCHOR"));
                        for (int i = 0; i < parameters; i++) {
                            source.addAll(compileBytecodeExpression(context, method, node.getChild(i), true));
                        }
                        source.add(new CallSub(context, methodExpression));
                        Spin1Method calledMethod = (Spin1Method) methodExpression.getData(Spin1Method.class.getName());
                        calledMethod.setCalledBy(method);
                    }
                    else if (expression.isConstant()) {
                        if (node.getChildCount() != 0) {
                            throw new RuntimeException("syntax error");
                        }
                        source.add(new Constant(context, expression, openspinCompatible));
                    }
                    else {
                        if (node.getChildCount() != 0) {
                            throw new RuntimeException("syntax error");
                        }
                        throw new CompilerException("invalid operand " + node.getText(), node.getToken());
                    }
                }
            }
        } catch (CompilerException e) {
            logMessage(e);
        } catch (Exception e) {
            logMessage(new CompilerException(e, node.getToken()));
        }

        return source;
    }

    protected boolean isAddress(String text) {
        return text.startsWith("@");
    }

    protected boolean isAssign(String text) {
        return ":=".equals(text);
    }

    List<Spin1Bytecode> compileConstantExpression(Spin1Context context, Spin1Method method, Spin1StatementNode node) {
        if (!openspinCompatible) {
            try {
                Expression expression = buildConstantExpression(context, node);
                if (expression.isConstant()) {
                    return Collections.singletonList(new Constant(context, expression, openspinCompatible));
                }
            } catch (Exception e) {

            }
        }
        return compileBytecodeExpression(context, method, node, true);
    }

    Expression buildConstantExpression(Spin1Context context, Spin1StatementNode node) {
        if (node.getType() == Token.NUMBER) {
            return new NumberLiteral(node.getText());
        }
        else if (node.getType() == Token.STRING) {
            String s = node.getText().substring(1);
            s = s.substring(0, s.length() - 1);
            if (s.length() == 1) {
                return new CharacterLiteral(s);
            }
            throw new RuntimeException("string not allowed");
        }

        Expression expression = context.getLocalSymbol(node.getText());
        if (expression != null) {
            if (expression.isConstant()) {
                return expression;
            }
            throw new RuntimeException("not a constant (" + expression + ")");
        }

        if ("+".equals(node.getText())) {
            if (node.getChildCount() == 1) {
                return buildConstantExpression(context, node.getChild(0));
            }
            return new Add(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
        }
        if ("-".equals(node.getText())) {
            if (node.getChildCount() == 1) {
                return new Negative(buildConstantExpression(context, node.getChild(0)));
            }
            return new Subtract(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
        }
        if ("!".equals(node.getText())) {
            if (node.getChildCount() == 1) {
                return new Not(buildConstantExpression(context, node.getChild(0)));
            }
            throw new RuntimeException("unary operator with " + node.getChildCount() + " arguments");
        }
        if ("*".equals(node.getText())) {
            return new Multiply(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
        }
        if ("/".equals(node.getText())) {
            return new Divide(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
        }
        if ("//".equals(node.getText())) {
            return new Modulo(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
        }
        if ("|".equals(node.getText())) {
            return new Or(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
        }
        if ("&".equals(node.getText())) {
            return new And(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
        }
        if ("^".equals(node.getText())) {
            return new Xor(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
        }
        if ("<<".equals(node.getText())) {
            return new ShiftLeft(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
        }
        if (">>".equals(node.getText())) {
            return new ShiftRight(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
        }
        if ("TRUNC".equalsIgnoreCase(node.getText())) {
            if (node.getChildCount() != 1) {
                throw new RuntimeException("misplaced unary operator (" + node.getText() + ")");
            }
            return new Trunc(buildConstantExpression(context, node.getChild(0)));
        }
        if ("|<".equals(node.getText())) {
            return new Decod(buildConstantExpression(context, node.getChild(0)));
        }

        throw new RuntimeException("unknown " + node.getText());
    }

    List<Spin1Bytecode> leftAssign(Spin1Context context, Spin1Method method, Spin1StatementNode node, boolean push) {
        List<Spin1Bytecode> source = new ArrayList<Spin1Bytecode>();

        String[] s = node.getText().split("[\\.]");
        if (s.length == 2 && ("BYTE".equalsIgnoreCase(s[1]) || "WORD".equalsIgnoreCase(s[1]) || "LONG".equalsIgnoreCase(s[1]))) {
            Spin1StatementNode postEffect = null;
            boolean indexed = false;

            Expression expression = context.getLocalSymbol(s[0]);
            if (expression == null) {
                throw new CompilerException("undefined symbol " + node.getText(), node.getToken());
            }

            int n = 0;
            if (n < node.getChildCount() && (node.getChild(n) instanceof Spin1StatementNode.Index)) {
                indexed = true;
                source.addAll(compileBytecodeExpression(context, method, node.getChild(n++), true));
            }
            if (n < node.getChildCount() && isPostEffect(node.getChild(n))) {
                postEffect = node.getChild(n++);
            }
            if (n < node.getChildCount()) {
                throw new CompilerException("unexpected " + node.getChild(n).getText(), node.getChild(n).getToken());
            }

            MemoryOp.Base bb = MemoryOp.Base.PBase;
            if (expression instanceof Variable) {
                bb = (expression instanceof LocalVariable) ? MemoryOp.Base.DBase : MemoryOp.Base.VBase;
                ((Variable) expression).setCalledBy(method);
            }

            MemoryOp.Op op = push ? MemoryOp.Op.Assign : MemoryOp.Op.Write;
            if ("BYTE".equalsIgnoreCase(s[1])) {
                source.add(new MemoryOp(context, MemoryOp.Size.Byte, indexed, bb, op, expression));
            }
            else if ("WORD".equalsIgnoreCase(s[1])) {
                source.add(new MemoryOp(context, MemoryOp.Size.Word, indexed, bb, op, expression));
            }
            else if ("LONG".equalsIgnoreCase(s[1])) {
                source.add(new MemoryOp(context, MemoryOp.Size.Long, indexed, bb, op, expression));
            }

            if (postEffect != null) {
                if ("++".equals(postEffect.getText())) {
                    int code = 0b0_0101_11_0;
                    if (push) {
                        code |= 0b10000000;
                    }
                    source.add(new Bytecode(context, code, "POST_INC"));
                }
                else if ("--".equals(postEffect.getText())) {
                    int code = 0b0_0111_11_0;
                    if (push) {
                        code |= 0b10000000;
                    }
                    source.add(new Bytecode(context, code, "POST_DEC"));
                }
                else if ("~".equals(postEffect.getText())) {
                    int code = 0b0_00110_00;
                    if (push) {
                        code |= 0b10000000;
                    }
                    source.add(new Bytecode(context, code, "POST_CLEAR"));
                }
                else if ("~~".equals(postEffect.getText())) {
                    int code = 0b0_00111_00;
                    if (push) {
                        code |= 0b10000000;
                    }
                    source.add(new Bytecode(context, code, "POST_SET"));
                }
                else if ("?".equals(postEffect.getText())) {
                    int code = 0b0_00011_00;
                    if (push) {
                        code |= 0b10000000;
                    }
                    source.add(new Bytecode(context, code, "RANDOM_REVERSE"));
                }
            }
        }
        else if (node.getType() == Token.OPERATOR) {
            source.addAll(leftAssign(context, method, node.getChild(1), true));
            source.add(new Bytecode(context, 0x80, "WRITE"));
            source.addAll(leftAssign(context, method, node.getChild(0), node.getChild(0).getType() == Token.OPERATOR));
        }
        else if ("BYTE".equalsIgnoreCase(node.getText()) || "WORD".equalsIgnoreCase(node.getText()) || "LONG".equalsIgnoreCase(node.getText())) {
            source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));
            if (node.getChildCount() > 1) {
                source.addAll(compileBytecodeExpression(context, method, node.getChild(1), true));
            }

            MemoryOp.Op op = push ? MemoryOp.Op.Assign : MemoryOp.Op.Write;
            if ("BYTE".equalsIgnoreCase(node.getText())) {
                source.add(new MemoryOp(context, MemoryOp.Size.Byte, node.getChildCount() > 1, MemoryOp.Base.Pop, op, null));
            }
            else if ("WORD".equalsIgnoreCase(node.getText())) {
                source.add(new MemoryOp(context, MemoryOp.Size.Word, node.getChildCount() > 1, MemoryOp.Base.Pop, op, null));
            }
            else if ("LONG".equalsIgnoreCase(node.getText())) {
                source.add(new MemoryOp(context, MemoryOp.Size.Long, node.getChildCount() > 1, MemoryOp.Base.Pop, op, null));
            }
        }
        else {
            Expression expression = context.getLocalSymbol(node.getText());
            if (expression == null) {
                throw new CompilerException("undefined symbol " + node.getText(), node.getToken());
            }
            if (expression instanceof Register) {
                if (node.getChildCount() == 1) {
                    boolean range = "..".equals(node.getChild(0).getText());
                    source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));
                    source.add(new RegisterBitOp(context, push ? RegisterBitOp.Op.Assign : RegisterBitOp.Op.Write, range, expression.getNumber().intValue()));
                }
                else {
                    source.add(new RegisterOp(context, push ? RegisterOp.Op.Assign : RegisterOp.Op.Write, expression.getNumber().intValue()));
                }
            }
            else if (expression instanceof Variable) {
                boolean indexed = false;
                if (node.getChildCount() != 0) {
                    source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));
                    indexed = true;
                }
                source.add(new VariableOp(context, push ? VariableOp.Op.Assign : VariableOp.Op.Write, indexed, (Variable) expression));
                ((Variable) expression).setCalledBy(method);
            }
            else {
                MemoryOp.Size ss = MemoryOp.Size.Long;
                if (expression instanceof DataVariable) {
                    switch (((DataVariable) expression).getType()) {
                        case "BYTE":
                            ss = MemoryOp.Size.Byte;
                            break;
                        case "WORD":
                            ss = MemoryOp.Size.Word;
                            break;
                    }
                }

                boolean indexed = false;
                if (node.getChildCount() != 0) {
                    source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));
                    indexed = true;
                }
                source.add(new MemoryOp(context, ss, indexed, MemoryOp.Base.PBase, push ? MemoryOp.Op.Assign : MemoryOp.Op.Write, expression));
            }
        }

        return source;
    }

    boolean isPostEffect(Spin1StatementNode node) {
        if (node.getChildCount() != 0) {
            return false;
        }
        String s = node.getText();
        return "++".equals(s) || "--".equals(s) || "~".equals(s) || "~~".equals(s) || "?".equals(s);
    }

    public Spin1Bytecode addStringData(Spin1Bytecode string) {
        if (!openspinCompatible) {
            for (Spin1Bytecode bc : stringData) {
                if (Arrays.equals(bc.getBytes(), string.getBytes())) {
                    return bc;
                }
            }

            byte[] b1 = string.getBytes();
            for (Spin1Bytecode bc : stringData) {
                byte[] b0 = bc.getBytes();
                if (b0.length > b1.length && Arrays.equals(b0, b0.length - b1.length, b0.length, b1, 0, b1.length)) {
                    Spin1Context sharedContext = new Spin1Context() {

                        @Override
                        public int getObjectAddress() {
                            return bc.getContext().getObjectAddress() + (b0.length - b1.length);
                        }

                    };
                    return new Spin1Bytecode(sharedContext) {

                        @Override
                        public Spin1Context getContext() {
                            return sharedContext;
                        }

                    };
                }
            }
        }

        stringData.add(string);
        return string;
    }

    public List<Spin1Bytecode> getStringData() {
        return stringData;
    }

    protected abstract void logMessage(CompilerException message);

}
