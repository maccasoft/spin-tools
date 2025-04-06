/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.ObjectCompiler;
import com.maccasoft.propeller.expressions.Add;
import com.maccasoft.propeller.expressions.And;
import com.maccasoft.propeller.expressions.CharacterLiteral;
import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.ContextLiteral;
import com.maccasoft.propeller.expressions.DataVariable;
import com.maccasoft.propeller.expressions.Decod;
import com.maccasoft.propeller.expressions.Divide;
import com.maccasoft.propeller.expressions.Equals;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.GreaterOrEquals;
import com.maccasoft.propeller.expressions.GreaterThan;
import com.maccasoft.propeller.expressions.IfElse;
import com.maccasoft.propeller.expressions.LessOrEquals;
import com.maccasoft.propeller.expressions.LessThan;
import com.maccasoft.propeller.expressions.LimitMax;
import com.maccasoft.propeller.expressions.LimitMin;
import com.maccasoft.propeller.expressions.LocalVariable;
import com.maccasoft.propeller.expressions.LogicalAnd;
import com.maccasoft.propeller.expressions.LogicalNot;
import com.maccasoft.propeller.expressions.LogicalOr;
import com.maccasoft.propeller.expressions.Method;
import com.maccasoft.propeller.expressions.Modulo;
import com.maccasoft.propeller.expressions.Multiply;
import com.maccasoft.propeller.expressions.Negative;
import com.maccasoft.propeller.expressions.Not;
import com.maccasoft.propeller.expressions.NotEquals;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.expressions.ObjectContextLiteral;
import com.maccasoft.propeller.expressions.Or;
import com.maccasoft.propeller.expressions.Register;
import com.maccasoft.propeller.expressions.Rev;
import com.maccasoft.propeller.expressions.Rol;
import com.maccasoft.propeller.expressions.Ror;
import com.maccasoft.propeller.expressions.Round;
import com.maccasoft.propeller.expressions.Sar;
import com.maccasoft.propeller.expressions.Scl;
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

public abstract class Spin1BytecodeCompiler extends Spin1PAsmCompiler {

    List<Spin1Bytecode> stringData;

    public Spin1BytecodeCompiler(Context scope, Spin1Compiler compiler, File file) {
        this(scope, compiler, null, file);
    }

    public Spin1BytecodeCompiler(Context scope, Spin1Compiler compiler, ObjectCompiler parent, File file) {
        super(scope, compiler, parent, file);
        this.stringData = new ArrayList<>();
    }

    public List<Spin1Bytecode> compileBytecodeExpression(Context context, Spin1Method method, Spin1StatementNode node, boolean push) {
        List<Spin1Bytecode> source = new ArrayList<Spin1Bytecode>();

        if (push && compiler.isFoldConstants() && !("-".equals(node.getText()) && node.getChildCount() == 1)) {
            try {
                Expression expression = buildConstantExpression(context, node);
                if (expression.isConstant()) {
                    if (!push) {
                        logMessage(new CompilerException("expected assignment", node.getTokens()));
                    }
                    if (expression.isString()) {
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        int[] s = expression.getStringValues();
                        for (int i = 0; i < s.length; i++) {
                            os.write((byte) s[i]);
                        }
                        os.write(0x00);
                        Spin1Bytecode target = addStringData(new Bytecode(context, os.toByteArray(), "STRING".toUpperCase()));
                        return Collections.singletonList(new MemoryRef(context, MemoryRef.Size.Byte, false, MemoryRef.Base.PBase, MemoryRef.Op.Address, new ContextLiteral(target.getContext())));
                    }
                    return Collections.singletonList(new Constant(context, expression, compiler.isFastByteConstants()));
                }
            } catch (Exception e) {
                // Do nothing, fall-through
            }
        }

        try {
            Descriptor desc = Spin1Bytecode.getDescriptor(node.getText());
            if (desc != null) {
                try {
                    if (node.getChildCount() != desc.parameters) {
                        throw new RuntimeException("expected " + desc.parameters + " argument(s), found " + node.getChildCount());
                    }
                    for (int i = 0; i < desc.parameters; i++) {
                        source.addAll(compileBytecodeExpression(context, method, node.getChild(i), true));
                    }
                    if (push && desc.code_push != null) {
                        source.add(new Bytecode(context, desc.code_push, node.getText().toUpperCase()));
                    }
                    else {
                        source.add(new Bytecode(context, desc.code, node.getText().toUpperCase()));
                    }
                    if (push && desc.code_push == null) {
                        throw new CompilerException("method doesn't return a value", node.getToken());
                    }
                } catch (CompilerException e) {
                    logMessage(e);
                } catch (Exception e) {
                    logMessage(new CompilerException(e, node.getToken()));
                }
            }
            else if ("CONSTANT".equalsIgnoreCase(node.getText())) {
                try {
                    if (node.getChildCount() != 1) {
                        throw new CompilerException("expected " + 1 + " argument(s), found " + node.getChildCount(), node.getTokens());
                    }
                    try {
                        Expression expression = buildConstantExpression(context, node.getChild(0), true);
                        if (!expression.isConstant()) {
                            throw new CompilerException("expression is not constant", node.getChild(0).getToken());
                        }
                        source.add(new Constant(context, expression, compiler.isFastByteConstants()));
                        if (!push) {
                            logMessage(new CompilerException("expected assignment", node.getTokens()));
                        }
                    } catch (Exception e) {
                        throw new CompilerException("expression is not constant", node.getChild(0).getToken());
                    }
                } catch (CompilerException e) {
                    logMessage(e);
                } catch (Exception e) {
                    logMessage(new CompilerException(e, node.getToken()));
                }
            }
            else if ("FLOAT".equalsIgnoreCase(node.getText())) {
                try {
                    if (node.getChildCount() != 1) {
                        throw new RuntimeException("expected " + 1 + " argument(s), found " + node.getChildCount());
                    }
                    try {
                        Expression expression = buildConstantExpression(context, node.getChild(0));
                        if (!expression.isConstant()) {
                            throw new CompilerException("expression is not constant", node.getChild(0).getToken());
                        }
                        source.add(new Constant(context, new com.maccasoft.propeller.expressions.Float(expression), compiler.isFastByteConstants()));
                        if (!push) {
                            logMessage(new CompilerException("expected assignment", node.getTokens()));
                        }
                    } catch (Exception e) {
                        throw new CompilerException("expression is not constant", node.getChild(0).getToken());
                    }
                } catch (CompilerException e) {
                    logMessage(e);
                } catch (Exception e) {
                    logMessage(new CompilerException(e, node.getToken()));
                }
            }
            else if ("TRUNC".equalsIgnoreCase(node.getText())) {
                try {
                    if (node.getChildCount() != 1) {
                        throw new RuntimeException("expected " + 1 + " argument(s), found " + node.getChildCount());
                    }
                    try {
                        Expression expression = buildConstantExpression(context, node.getChild(0));
                        if (!expression.isConstant()) {
                            throw new CompilerException("expression is not constant", node.getChild(0).getToken());
                        }
                        source.add(new Constant(context, new Trunc(expression), compiler.isFastByteConstants()));
                        if (!push) {
                            logMessage(new CompilerException("expected assignment", node.getTokens()));
                        }
                    } catch (Exception e) {
                        throw new CompilerException("expression is not constant", node.getChild(0).getToken());
                    }
                } catch (CompilerException e) {
                    logMessage(e);
                } catch (Exception e) {
                    logMessage(new CompilerException(e, node.getToken()));
                }
            }
            else if ("ROUND".equalsIgnoreCase(node.getText())) {
                try {
                    if (node.getChildCount() != 1) {
                        throw new RuntimeException("expected " + 1 + " argument(s), found " + node.getChildCount());
                    }
                    try {
                        Expression expression = buildConstantExpression(context, node.getChild(0));
                        if (!expression.isConstant()) {
                            throw new CompilerException("expression is not constant", node.getChild(0).getToken());
                        }
                        source.add(new Constant(context, new Round(expression), compiler.isFastByteConstants()));
                        if (!push) {
                            logMessage(new CompilerException("expected assignment", node.getTokens()));
                        }
                    } catch (Exception e) {
                        throw new CompilerException("expression is not constant", node.getChild(0).getToken());
                    }
                } catch (CompilerException e) {
                    logMessage(e);
                } catch (Exception e) {
                    logMessage(new CompilerException(e, node.getToken()));
                }
            }
            else if ("CHIPVER".equalsIgnoreCase(node.getText())) {
                source.add(new Bytecode(context, new byte[] {
                    (byte) 0x34, (byte) 0x80
                }, "CHIPVER"));
                if (!push) {
                    logMessage(new CompilerException("expected assignment", node.getTokens()));
                }
            }
            else if ("CLKFREQ".equalsIgnoreCase(node.getText())) {
                source.add(new Constant(context, new NumberLiteral(0), compiler.isFastByteConstants()));
                source.add(new MemoryOp(context, MemoryOp.Size.Long, false, MemoryOp.Base.Pop, MemoryOp.Op.Read, null));
                if (!push) {
                    logMessage(new CompilerException("expected assignment", node.getTokens()));
                }
            }
            else if ("CLKMODE".equalsIgnoreCase(node.getText())) {
                source.add(new Address(context, new NumberLiteral(4)));
                source.add(new MemoryOp(context, MemoryOp.Size.Byte, false, MemoryOp.Base.Pop, MemoryOp.Op.Read, null));
                if (!push) {
                    logMessage(new CompilerException("expected assignment", node.getTokens()));
                }
            }
            else if ("COGID".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 0) {
                    throw new RuntimeException("expected " + 0 + " argument(s), found " + node.getChildCount());
                }
                source.add(new RegisterOp(context, RegisterOp.Op.Read, 0x1E9));
                if (!push) {
                    logMessage(new CompilerException("expected assignment", node.getTokens()));
                }
            }
            else if ("COGNEW".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 2) {
                    throw new RuntimeException("expected " + 2 + " argument(s), found " + node.getChildCount());
                }
                Expression expression = context.getLocalSymbol(node.getChild(0).getText());
                if (expression instanceof Method) {
                    Spin1StatementNode methodNode = node.getChild(0);
                    Spin1Method calledMethod = (Spin1Method) expression.getData(Spin1Method.class.getName());
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

                    }, compiler.isFastByteConstants()));
                    source.addAll(compileBytecodeExpression(context, method, node.getChild(1), true));
                    source.add(new Bytecode(context, 0x15, "MARK_INTERPRETED"));
                    calledMethod.setCalledBy(method);
                }
                else {
                    source.add(new Constant(context, new NumberLiteral(-1), compiler.isFastByteConstants()));
                    source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));
                    source.addAll(compileBytecodeExpression(context, method, node.getChild(1), true));
                }
                desc = Spin1Bytecode.getDescriptor("COGINIT");
                source.add(new Bytecode(context, push ? desc.code_push : desc.code, node.getText().toUpperCase()));
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

                source.add(new Constant(context, new NumberLiteral(node.getText().toUpperCase().endsWith("Z") ? 0 : 1), compiler.isFastByteConstants()));

                Spin1Bytecode end = new Spin1Bytecode(context);
                source.add(new Address(context, new ContextLiteral(end.getContext())));

                source.addAll(compileBytecodeExpression(context, method, argsNode.getChild(0), true));

                for (int i = 1; i < argsNode.getChildCount(); i++) {
                    Spin1StatementNode arg = argsNode.getChild(i);
                    if ("..".equals(arg.getText())) {
                        if (arg.getChildCount() != 2) {
                            throw new RuntimeException("expression syntax error");
                        }
                        source.addAll(compileBytecodeExpression(context, method, arg.getChild(0), true));
                        source.addAll(compileBytecodeExpression(context, method, arg.getChild(1), true));
                        source.add(new Bytecode(context, code_range, node.getText().toUpperCase()));
                    }
                    else if (arg.getType() == Token.STRING) {
                        String s = arg.getText().substring(1, arg.getText().length() - 1);
                        for (int x = 0; x < s.length(); x++) {
                            source.add(new Constant(context, new CharacterLiteral(s.substring(x, x + 1)), compiler.isFastByteConstants()));
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
                if (!push) {
                    logMessage(new CompilerException("expected assignment", node.getTokens()));
                }
            }
            else if ("STRING".equalsIgnoreCase(node.getText())) {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                for (Spin1StatementNode child : node.getChilds()) {
                    if (child.getType() == Token.STRING) {
                        String s = child.getText().substring(1);
                        os.write(s.substring(0, s.length() - 1).getBytes());
                    }
                    else if (child.getType() == Token.NUMBER) {
                        NumberLiteral expression = new NumberLiteral(child.getText());
                        os.write(expression.getNumber().intValue());
                    }
                    else {
                        try {
                            Expression expression = buildConstantExpression(context, child);
                            if (!expression.isConstant()) {
                                throw new CompilerException("expression is not constant", child.getToken());
                            }
                            if (expression.isString()) {
                                int[] s = expression.getStringValues();
                                for (int i = 0; i < s.length; i++) {
                                    os.write((byte) s[i]);
                                }
                            }
                            else {
                                if (expression.getNumber().intValue() < -0x80 || expression.getNumber().intValue() > 0xFF) {
                                    logMessage(new CompilerException(CompilerException.WARNING, "byte value range from -$80 to $FF", child.getTokens()));
                                }
                                os.write(expression.getByte());
                            }
                        } catch (Exception e) {
                            throw new CompilerException("expression is not constant", child.getToken());
                        }
                    }
                }
                os.write(0x00);
                Spin1Bytecode target = addStringData(new Bytecode(context, os.toByteArray(), "STRING".toUpperCase()));
                source.add(new MemoryRef(context, MemoryRef.Size.Byte, false, MemoryRef.Base.PBase, MemoryRef.Op.Address, new ContextLiteral(target.getContext())));
                if (!push) {
                    logMessage(new CompilerException("expected assignment", node.getTokens()));
                }
            }
            else if ("REBOOT".equalsIgnoreCase(node.getText())) {
                source.add(new Constant(context, new NumberLiteral("%10000000"), compiler.isFastByteConstants()));
                source.add(new Constant(context, new NumberLiteral(0), compiler.isFastByteConstants()));
                source.add(new Bytecode(context, new byte[] {
                    (byte) 0x20
                }, "CLKSET"));
                if (push) {
                    logMessage(new CompilerException("method doesn't return a value", node.getTokens()));
                }
            }
            else if ("BYTECODE".equalsIgnoreCase(node.getText())) {
                String text = node.getText().toUpperCase();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                for (int i = 0; i < node.getChildCount(); i++) {
                    if (node.getChild(i).getType() == Token.STRING && i == node.getChildCount() - 1) {
                        text = node.getChild(i).getText();
                        text = text.substring(1, text.length() - 1);
                        break;
                    }
                    try {
                        Expression expression = buildConstantExpression(context, node.getChild(i));
                        if (!expression.isConstant()) {
                            throw new CompilerException("expression is not constant", node.getChild(i).getTokens());
                        }
                        os.write(expression.getNumber().byteValue());
                    } catch (Exception e) {
                        throw new CompilerException("expression is not constant", node.getChild(i).getTokens());
                    }
                }
                source.add(new Bytecode(context, os.toByteArray(), text));
                if (push) {
                    logMessage(new CompilerException("method doesn't return a value", node.getTokens()));
                }
            }
            else if (node.getType() == Token.NUMBER) {
                Expression expression = new NumberLiteral(node.getText());
                source.add(new Constant(context, expression, compiler.isFastByteConstants()));
                if (!push) {
                    logMessage(new CompilerException("expected assignment", node.getTokens()));
                }
            }
            else if (node.getType() == Token.STRING) {
                String s = node.getText();
                if (s.startsWith("@")) {
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    os.write(s.substring(2, s.length() - 1).getBytes());
                    os.write(0x00);
                    Spin1Bytecode target = addStringData(new Bytecode(context, os.toByteArray(), "STRING".toUpperCase()));
                    source.add(new MemoryRef(context, MemoryRef.Size.Byte, false, MemoryRef.Base.PBase, MemoryRef.Op.Address, new ContextLiteral(target.getContext())));
                }
                else {
                    s = s.substring(1, s.length() - 1);
                    if (s.length() == 1) {
                        Expression expression = new CharacterLiteral(s);
                        source.add(new Constant(context, expression, compiler.isFastByteConstants()));
                    }
                    else {
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        os.write(s.getBytes());
                        os.write(0x00);
                        Spin1Bytecode target = addStringData(new Bytecode(context, os.toByteArray(), "STRING".toUpperCase()));
                        source.add(new MemoryRef(context, MemoryRef.Size.Byte, false, MemoryRef.Base.PBase, MemoryRef.Op.Address, new ContextLiteral(target.getContext())));
                    }
                }
                if (!push) {
                    logMessage(new CompilerException("expected assignment", node.getTokens()));
                }
            }
            else if ("-".equals(node.getText()) && node.getChildCount() == 1) {
                if (node.getChild(0).getToken().type == Token.NUMBER) {
                    source.add(new Constant(context, new Negative(new NumberLiteral(node.getChild(0).getText())), compiler.isFastByteConstants()));
                    if (!push) {
                        logMessage(new CompilerException("expected assignment", node.getTokens()));
                    }
                }
                else {
                    Expression expression = context.getLocalSymbol(node.getChild(0).getText());
                    if (expression != null && expression.isConstant() && push) {
                        source.add(new Constant(context, new Negative(expression), compiler.isFastByteConstants()));
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
            else if ("+".equals(node.getText()) && node.getChildCount() == 1) {
                source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));
                if (!push) {
                    logMessage(new CompilerException("expected assignment", node.getTokens()));
                }
            }
            else if (":=".equals(node.getText())) {
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
                source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));
                source.addAll(compileBytecodeExpression(context, method, node.getChild(1), true));
                source.add(new MathOp(context, node.getText(), push));
                if (!push) {
                    logMessage(new CompilerException("expected assignment", node.getTokens()));
                }
            }
            else if ("?".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() == 1) {
                    source.addAll(leftAssign(context, method, node.getChild(0), true));

                    int code = 0b0_00010_00;
                    if (push) {
                        code |= 0b10000000;
                    }
                    source.add(new Bytecode(context, code, "RANDOM_FORWARD"));
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

                    if (!push) {
                        logMessage(new CompilerException("expected assignment", node.getTokens()));
                    }
                }
            }
            else if ("(".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("expression syntax error");
                }
                source.addAll(compileBytecodeExpression(context, method, node.getChild(0), push));
            }
            else if (",".equalsIgnoreCase(node.getText())) {
                for (Spin1StatementNode child : node.getChilds()) {
                    source.addAll(compileBytecodeExpression(context, method, child, push));
                }
            }
            else if ("\\".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("expression syntax error");
                }
                Expression expression = context.getLocalSymbol(node.getChild(0).getText());

                if (expression instanceof Method) {
                    source.addAll(compileMethodCall(context, method, expression, node.getChild(0), push, true));
                }
                else if (expression instanceof SpinObject) {
                    source.addAll(compileMethodCall(context, method, expression, node.getChild(0), push, true));
                }
                else {
                    throw new CompilerException("not a method", node.getChild(0).getToken());
                }
            }
            else if ("++".equalsIgnoreCase(node.getText()) || "--".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 1) {
                    throw new CompilerException("expression syntax error", node.getToken());
                }
                source.addAll(leftAssign(context, method, node.getChild(0), true));

                Spin1Bytecode.Type type = Spin1Bytecode.Type.Long;
                Spin1StatementNode childNode = node.getChild(0);

                String[] s = childNode.getText().split("[\\.]");
                if (s.length == 2 && ("BYTE".equalsIgnoreCase(s[1]) || "WORD".equalsIgnoreCase(s[1]) || "LONG".equalsIgnoreCase(s[1]))) {
                    if ("BYTE".equalsIgnoreCase(s[1])) {
                        type = Spin1Bytecode.Type.Byte;
                    }
                    else if ("WORD".equalsIgnoreCase(s[1])) {
                        type = Spin1Bytecode.Type.Word;
                    }
                    else if ("LONG".equalsIgnoreCase(s[1])) {
                        type = Spin1Bytecode.Type.Long;
                    }
                }
                else if ("BYTE".equalsIgnoreCase(childNode.getText()) || "WORD".equalsIgnoreCase(childNode.getText()) || "LONG".equalsIgnoreCase(childNode.getText())) {
                    if ("BYTE".equalsIgnoreCase(childNode.getText())) {
                        type = Spin1Bytecode.Type.Byte;
                    }
                    else if ("WORD".equalsIgnoreCase(childNode.getText())) {
                        type = Spin1Bytecode.Type.Word;
                    }
                    else if ("LONG".equalsIgnoreCase(childNode.getText())) {
                        type = Spin1Bytecode.Type.Long;
                    }
                }
                else {
                    Expression expression = context.getLocalSymbol(childNode.getText());
                    if (expression instanceof Variable) {
                        type = Spin1Bytecode.Type.fromString(((Variable) expression).getType());
                    }
                    else if (expression instanceof ContextLiteral) {
                        if (expression instanceof DataVariable) {
                            switch (((DataVariable) expression).getType()) {
                                case "BYTE":
                                    type = Spin1Bytecode.Type.Byte;
                                    break;
                                case "WORD":
                                    type = Spin1Bytecode.Type.Word;
                                    break;
                            }
                        }
                    }
                    else if (!(expression instanceof Register)) {
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
                    source.add(compilePostEffect(context, postEffectNode, node.getText(), push));
                }
                else if (!push) {
                    logMessage(new CompilerException("expected assignment", node.getTokens()));
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

                if (!push) {
                    logMessage(new CompilerException("expected assignment", node.getTokens()));
                }
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

                if (!push) {
                    logMessage(new CompilerException("expected assignment", node.getTokens()));
                }
            }
            else {
                String[] s = node.getText().split("[\\.]");
                if (s.length == 2 && ("BYTE".equalsIgnoreCase(s[1]) || "WORD".equalsIgnoreCase(s[1]) || "LONG".equalsIgnoreCase(s[1]))) {
                    Spin1StatementNode postEffectNode = null;
                    boolean indexed = false;

                    Expression expression = context.getLocalSymbol(s[0]);
                    if (expression == null && isAbsoluteAddress(s[0])) {
                        expression = context.getLocalSymbol(s[0].substring(2));
                    }
                    if (expression == null && isAddress(s[0])) {
                        expression = context.getLocalSymbol(s[0].substring(1));
                    }
                    if (expression == null) {
                        throw new CompilerException("undefined symbol " + node.getText(), node.getToken());
                    }

                    if (node.getChildCount() != 0) {
                        indexed = true;
                        source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));
                        if (node.getChildCount() > 1) {
                            if (isPostEffect(node.getChild(1))) {
                                postEffectNode = node.getChild(1);
                            }
                        }
                    }

                    MemoryOp.Base bb = MemoryOp.Base.PBase;
                    if (expression instanceof Variable) {
                        bb = (expression instanceof LocalVariable) ? MemoryOp.Base.DBase : MemoryOp.Base.VBase;
                        ((Variable) expression).setCalledBy(method);
                    }

                    MemoryOp.Op op = push ? MemoryOp.Op.Read : MemoryOp.Op.Write;
                    if (postEffectNode != null) {
                        op = MemoryOp.Op.Assign;
                    }
                    if (isAddress(s[0])) {
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

                    if (postEffectNode != null) {
                        source.add(compilePostEffect(context, postEffectNode, s[1], push));
                    }
                    else if (!push) {
                        logMessage(new CompilerException("expected assignment", node.getTokens()));
                    }
                }
                else {
                    Expression expression = context.getLocalSymbol(node.getText());
                    if (expression == null && isAbsoluteAddress(node.getText())) {
                        expression = context.getLocalSymbol(node.getText().substring(2));
                    }
                    if (expression == null && isAddress(node.getText())) {
                        expression = context.getLocalSymbol(node.getText().substring(1));
                    }
                    if (expression instanceof ObjectContextLiteral) {
                        expression = context.getLocalSymbol(node.getText().substring(1));
                    }
                    if (expression == null) {
                        throw new CompilerException("undefined symbol " + node.getText(), node.getToken());
                    }
                    if (expression instanceof SpinObject) {
                        source.addAll(compileMethodCall(context, method, expression, node, push, false));
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
                                source.add(compilePostEffect(context, postEffectNode, ((Variable) expression).getType(), push));
                            }
                            else {
                                if (isAbsoluteAddress(node.getText())) {
                                    source.add(new VariableOp(context, VariableOp.Op.Read, popIndex, (Variable) expression));
                                    source.add(new MemoryOp(context, MemoryOp.Size.Byte, true, MemoryOp.Base.PBase, MemoryOp.Op.Address, new NumberLiteral(0)));
                                }
                                else {
                                    source.add(new VariableOp(context, VariableOp.Op.Address, popIndex, (Variable) expression));
                                }
                                if (!push) {
                                    logMessage(new CompilerException("expected assignment", node.getTokens()));
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
                            if (!push) {
                                logMessage(new CompilerException("expected assignment", node.getTokens()));
                            }
                        }
                        else if (expression instanceof ContextLiteral) {
                            String type = "LONG";
                            MemoryOp.Size ss = MemoryOp.Size.Long;
                            if (expression instanceof DataVariable) {
                                type = ((DataVariable) expression).getType();
                                switch (type) {
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
                                source.add(compilePostEffect(context, postEffectNode, type, push));
                            }
                            else {
                                source.add(new MemoryOp(context, ss, popIndex, MemoryOp.Base.PBase, MemoryOp.Op.Address, expression));
                                if (!push) {
                                    logMessage(new CompilerException("expected assignment", node.getTokens()));
                                }
                            }
                        }
                        else if (expression.isConstant()) {
                            if (node.getChildCount() != 0) {
                                throw new RuntimeException("syntax error");
                            }
                            source.add(new Constant(context, expression, compiler.isFastByteConstants()));
                            if (!push) {
                                logMessage(new CompilerException("expected assignment", node.getTokens()));
                            }
                        }
                        else {
                            throw new CompilerException("syntax error", node.getToken());
                        }
                    }
                    else if (expression instanceof Register) {
                        boolean popIndex = false;
                        boolean range = false;
                        Spin1StatementNode postEffectNode = null;

                        int n = 0;
                        if (n < node.getChildCount() && (node.getChild(n) instanceof Spin1StatementNode.Index)) {
                            if ("SPR".equalsIgnoreCase(node.getText())) {
                                source.addAll(compileBytecodeExpression(context, method, node.getChild(n++), true));
                            }
                            else {
                                if (range = "..".equals(node.getChild(n).getText())) {
                                    Spin1StatementNode rangeNode = node.getChild(n++);
                                    if (rangeNode.getChildCount() != 2) {
                                        throw new RuntimeException("expression syntax error");
                                    }
                                    source.addAll(compileBytecodeExpression(context, method, rangeNode.getChild(0), true));
                                    source.addAll(compileBytecodeExpression(context, method, rangeNode.getChild(1), true));
                                }
                                else {
                                    source.addAll(compileBytecodeExpression(context, method, node.getChild(n++), true));
                                }
                                popIndex = true;
                            }
                        }
                        if (n < node.getChildCount() && isPostEffect(node.getChild(n))) {
                            postEffectNode = node.getChild(n++);
                        }
                        if (n < node.getChildCount()) {
                            throw new CompilerException("unexpected " + node.getChild(n).getText(), node.getChild(n).getTokens());
                        }

                        if (range || popIndex) {
                            source.add(new RegisterBitOp(context, push ? RegisterBitOp.Op.Read : RegisterBitOp.Op.Assign, range, expression.getNumber().intValue()));
                        }
                        else if (postEffectNode != null) {
                            source.add(new RegisterOp(context, RegisterOp.Op.Assign, expression.getNumber().intValue()));
                        }
                        else {
                            source.add(new RegisterOp(context, RegisterOp.Op.Read, expression.getNumber().intValue()));
                        }

                        if (postEffectNode != null) {
                            source.add(compilePostEffect(context, postEffectNode, "LONG", push));
                        }
                        else if (!push) {
                            logMessage(new CompilerException("expected assignment", node.getTokens()));
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
                            source.add(compilePostEffect(context, postEffectNode, ((Variable) expression).getType(), push));
                        }
                        else {
                            source.add(new VariableOp(context, VariableOp.Op.Read, popIndex, (Variable) expression));
                            if (!push) {
                                logMessage(new CompilerException("expected assignment", node.getTokens()));
                            }
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

                        String type = "LONG";
                        MemoryOp.Size ss = MemoryOp.Size.Long;
                        if (expression instanceof DataVariable) {
                            type = ((DataVariable) expression).getType();
                            switch (type) {
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
                            source.add(compilePostEffect(context, postEffectNode, type, push));
                        }
                        else {
                            source.add(new MemoryOp(context, ss, popIndex, MemoryOp.Base.PBase, MemoryOp.Op.Read, expression));
                            if (!push) {
                                logMessage(new CompilerException("expected assignment", node.getTokens()));
                            }
                        }
                    }
                    else if (expression instanceof Method) {
                        source.addAll(compileMethodCall(context, method, expression, node, push, false));
                    }
                    else if (expression.isConstant()) {
                        if (node.getChildCount() != 0) {
                            throw new RuntimeException("syntax error");
                        }
                        source.add(new Constant(context, expression, compiler.isFastByteConstants()));
                        if (!push) {
                            logMessage(new CompilerException("expected assignment", node.getTokens()));
                        }
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

    List<Spin1Bytecode> compileMethodArguments(Context context, Spin1Method method, Spin1Method calledMethod, Spin1StatementNode argsNode) {
        List<Spin1Bytecode> source = new ArrayList<Spin1Bytecode>();

        int actual = 0;
        while (actual < argsNode.getChildCount()) {
            source.addAll(compileBytecodeExpression(context, method, argsNode.getChild(actual++), true));
        }
        while (actual < calledMethod.getParametersCount()) {
            Expression value = calledMethod.getParameters().get(actual).getValue();
            if (value == null) {
                break;
            }
            source.add(new Constant(context, value, compiler.isFastByteConstants()));
            actual++;
        }

        if (actual != calledMethod.getParametersCount()) {
            logMessage(new CompilerException("expected " + calledMethod.getParametersCount() + " argument(s), found " + actual, argsNode.getTokens()));
        }

        return source;
    }

    protected boolean isAddress(String text) {
        return text.startsWith("@");
    }

    protected boolean isAbsoluteAddress(String text) {
        return text.startsWith("@@");
    }

    Expression buildConstantExpression(Context context, Spin1StatementNode node) {
        return buildConstantExpression(context, node, false);
    }

    Expression buildConstantExpression(Context context, Spin1StatementNode node, boolean force) {
        if (node.getType() == Token.NUMBER) {
            return new NumberLiteral(node.getText());
        }
        else if (node.getType() == Token.STRING) {
            String s = node.getText();
            if (!s.startsWith("\"")) {
                throw new RuntimeException("not a constant (" + node.getText() + ")");
            }
            return new CharacterLiteral(s.substring(1, s.length() - 1));
        }

        String nodeText = node.getText();
        if ("CLKFREQ".equalsIgnoreCase(nodeText) || "CLKMODE".equalsIgnoreCase(nodeText)) {
            throw new RuntimeException("not a constant (" + nodeText + ")");
        }

        Expression expression = context.getLocalSymbol(nodeText);
        if ((expression instanceof ObjectContextLiteral) && force) {
            final Expression parent = expression;
            expression = new NumberLiteral(0) {

                @Override
                public Number getNumber() {
                    return parent.getNumber();
                }

            };
        }
        if (expression != null) {
            if (expression.isConstant()) {
                return expression;
            }
            throw new RuntimeException("not a constant (" + expression + ")");
        }

        switch (nodeText.toUpperCase()) {
            case ">>":
                return new ShiftRight(buildConstantExpression(context, node.getChild(0), force), buildConstantExpression(context, node.getChild(1), force));
            case "<<":
                return new ShiftLeft(buildConstantExpression(context, node.getChild(0), force), buildConstantExpression(context, node.getChild(1), force));
            case "~>":
                return new Sar(buildConstantExpression(context, node.getChild(0), force), buildConstantExpression(context, node.getChild(1), force));
            case "->":
                return new Ror(buildConstantExpression(context, node.getChild(0), force), buildConstantExpression(context, node.getChild(1), force));
            case "<-":
                return new Rol(buildConstantExpression(context, node.getChild(0), force), buildConstantExpression(context, node.getChild(1), force));
            case "><":
                return new Rev(buildConstantExpression(context, node.getChild(0), force), buildConstantExpression(context, node.getChild(1), force));

            case "&":
                return new And(buildConstantExpression(context, node.getChild(0), force), buildConstantExpression(context, node.getChild(1), force));
            case "^":
                return new Xor(buildConstantExpression(context, node.getChild(0), force), buildConstantExpression(context, node.getChild(1), force));
            case "|":
                return new Or(buildConstantExpression(context, node.getChild(0), force), buildConstantExpression(context, node.getChild(1), force));

            case "*":
                return new Multiply(buildConstantExpression(context, node.getChild(0), force), buildConstantExpression(context, node.getChild(1), force));
            case "**":
                return new Scl(buildConstantExpression(context, node.getChild(0), force), buildConstantExpression(context, node.getChild(1), force));
            case "/":
                return new Divide(buildConstantExpression(context, node.getChild(0), force), buildConstantExpression(context, node.getChild(1), force));
            case "//":
                return new Modulo(buildConstantExpression(context, node.getChild(0), force), buildConstantExpression(context, node.getChild(1), force));

            case "+":
                if (node.getChildCount() == 1) {
                    return buildConstantExpression(context, node.getChild(0), force);
                }
                return new Add(buildConstantExpression(context, node.getChild(0), force), buildConstantExpression(context, node.getChild(1), force));
            case "-":
                if (node.getChildCount() == 1) {
                    return new Negative(buildConstantExpression(context, node.getChild(0), force));
                }
                return new Subtract(buildConstantExpression(context, node.getChild(0), force), buildConstantExpression(context, node.getChild(1), force));
            case "!":
                if (node.getChildCount() == 1) {
                    return new Not(buildConstantExpression(context, node.getChild(0), force));
                }
                throw new RuntimeException("unary operator with " + node.getChildCount() + " arguments");
            case "|<":
                return new Decod(buildConstantExpression(context, node.getChild(0), force));

            case "#>":
                return new LimitMin(buildConstantExpression(context, node.getChild(0), force), buildConstantExpression(context, node.getChild(1), force));
            case "<#":
                return new LimitMax(buildConstantExpression(context, node.getChild(0), force), buildConstantExpression(context, node.getChild(1), force));

            case "<":
                return new LessThan(buildConstantExpression(context, node.getChild(0), force), buildConstantExpression(context, node.getChild(1), force));
            case "=<":
                return new LessOrEquals(buildConstantExpression(context, node.getChild(0), force), buildConstantExpression(context, node.getChild(1), force));
            case "==":
                return new Equals(buildConstantExpression(context, node.getChild(0), force), buildConstantExpression(context, node.getChild(1), force));
            case "<>":
                return new NotEquals(buildConstantExpression(context, node.getChild(0), force), buildConstantExpression(context, node.getChild(1), force));
            case "=>":
                return new GreaterOrEquals(buildConstantExpression(context, node.getChild(0), force), buildConstantExpression(context, node.getChild(1), force));
            case ">":
                return new GreaterThan(buildConstantExpression(context, node.getChild(0), force), buildConstantExpression(context, node.getChild(1), force));

            case "AND":
                return new LogicalAnd(buildConstantExpression(context, node.getChild(0), force), buildConstantExpression(context, node.getChild(1), force));
            case "OR":
                return new LogicalOr(buildConstantExpression(context, node.getChild(0), force), buildConstantExpression(context, node.getChild(1), force));
            case "NOT":
                if (node.getChildCount() == 1) {
                    return new LogicalNot(buildConstantExpression(context, node.getChild(0), force));
                }
                throw new RuntimeException("unary operator with " + node.getChildCount() + " arguments");

            case "?": {
                Expression left = buildConstantExpression(context, node.getChild(0), force);
                Expression right = buildConstantExpression(context, node.getChild(1), force);
                if (!(right instanceof IfElse)) {
                    throw new RuntimeException("invalid operator " + node.getText());
                }
                left = new IfElse(left, ((IfElse) right).getTrueTerm(), ((IfElse) right).getFalseTerm());
                break;
            }
            case ":":
                return new IfElse(null, buildConstantExpression(context, node.getChild(0), force), buildConstantExpression(context, node.getChild(1), force));

            case "FLOAT":
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("misplaced unary operator (" + node.getText() + ")");
                }
                return new com.maccasoft.propeller.expressions.Float(buildConstantExpression(context, node.getChild(0), force));
            case "TRUNC":
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("misplaced unary operator (" + node.getText() + ")");
                }
                return new Trunc(buildConstantExpression(context, node.getChild(0), force));
            case "ROUND":
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("misplaced unary operator (" + node.getText() + ")");
                }
                return new Round(buildConstantExpression(context, node.getChild(0), force));
        }

        throw new RuntimeException("unknown " + node.getText());
    }

    protected List<Spin1Bytecode> leftAssign(Context context, Spin1Method method, Spin1StatementNode node, boolean push) {
        List<Spin1Bytecode> source = new ArrayList<Spin1Bytecode>();

        String[] s = node.getText().split("[\\.]");
        if (s.length == 2 && ("BYTE".equalsIgnoreCase(s[1]) || "WORD".equalsIgnoreCase(s[1]) || "LONG".equalsIgnoreCase(s[1]))) {
            Spin1StatementNode postEffectNode = null;
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
                postEffectNode = node.getChild(n++);
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

            if (postEffectNode != null) {
                source.add(compilePostEffect(context, postEffectNode, s[1], push));
            }
        }
        else if (node.getType() != Token.KEYWORD) {
            throw new CompilerException("syntax error", node.getToken());
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
                boolean popIndex = false;
                boolean range = false;
                Spin1StatementNode postEffectNode = null;

                int n = 0;
                if (n < node.getChildCount() && (node.getChild(n) instanceof Spin1StatementNode.Index)) {
                    if ("SPR".equalsIgnoreCase(node.getText())) {
                        source.addAll(compileBytecodeExpression(context, method, node.getChild(n++), true));
                    }
                    else {
                        if (range = "..".equals(node.getChild(n).getText())) {
                            Spin1StatementNode rangeNode = node.getChild(n++);
                            if (rangeNode.getChildCount() != 2) {
                                throw new RuntimeException("expression syntax error");
                            }
                            source.addAll(compileBytecodeExpression(context, method, rangeNode.getChild(0), true));
                            source.addAll(compileBytecodeExpression(context, method, rangeNode.getChild(1), true));
                        }
                        else {
                            source.addAll(compileBytecodeExpression(context, method, node.getChild(n++), true));
                        }
                        popIndex = true;
                    }
                }
                if (n < node.getChildCount() && isPostEffect(node.getChild(n))) {
                    postEffectNode = node.getChild(n++);
                }
                if (n < node.getChildCount()) {
                    throw new CompilerException("unexpected " + node.getChild(n).getText(), node.getChild(n).getTokens());
                }

                if (range || popIndex) {
                    source.add(new RegisterBitOp(context, push ? RegisterBitOp.Op.Assign : RegisterBitOp.Op.Write, range, expression.getNumber().intValue()));
                }
                else if (postEffectNode != null) {
                    source.add(new RegisterOp(context, push ? RegisterOp.Op.Assign : RegisterOp.Op.Write, expression.getNumber().intValue()));
                }
                else {
                    source.add(new RegisterOp(context, push ? RegisterOp.Op.Assign : RegisterOp.Op.Write, expression.getNumber().intValue()));
                }

                if (postEffectNode != null) {
                    source.add(compilePostEffect(context, postEffectNode, "LONG", push));
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

    List<Spin1Bytecode> compileMethodCall(Context context, Spin1Method method, Expression expression, Spin1StatementNode node, boolean push, boolean trap) {
        List<Spin1Bytecode> source = new ArrayList<>();

        if (trap) {
            source.add(new Bytecode(context, push ? 0b00000010 : 0b00000011, "ANCHOR (TRY)"));
        }
        else {
            source.add(new Bytecode(context, push ? 0b00000000 : 0b00000001, "ANCHOR"));
        }

        if (expression instanceof Method) {
            Method methodExpression = (Method) expression;
            Spin1Method calledMethod = (Spin1Method) methodExpression.getData(Spin1Method.class.getName());

            source.addAll(compileMethodArguments(context, method, calledMethod, node));
            source.add(new CallSub(context, methodExpression));
            calledMethod.setCalledBy(method);

            if (push && !trap && calledMethod.getReturnsCount() == 0) {
                logMessage(new CompilerException("method doesn't return a value", node.getToken()));
            }
        }
        else if (expression instanceof SpinObject) {
            Spin1StatementNode indexNode = null;
            Spin1StatementNode methodNode = null;

            int n = 0;
            if (n < node.getChildCount()) {
                if (node.getChild(n) instanceof Spin1StatementNode.Index) {
                    indexNode = node.getChild(n++);
                }
            }
            if (n < node.getChildCount()) {
                methodNode = node.getChild(n++);
            }
            if (n < node.getChildCount() || methodNode == null) {
                throw new CompilerException("syntax error", node.getTokens());
            }

            String qualifiedName = node.getText() + methodNode.getText();

            expression = context.getLocalSymbol(qualifiedName);
            if (expression == null) {
                throw new CompilerException("undefined symbol " + methodNode.getText(), methodNode.getToken());
            }
            if (!(expression instanceof Method)) {
                throw new CompilerException(methodNode.getText() + " is not a method", methodNode.getToken());
            }
            Method methodExpression = (Method) expression;
            Spin1Method calledMethod = (Spin1Method) methodExpression.getData(Spin1Method.class.getName());

            source.addAll(compileMethodArguments(context, method, calledMethod, methodNode));
            if (indexNode != null) {
                source.addAll(compileBytecodeExpression(context, method, indexNode, true));
            }
            source.add(new CallSub(context, methodExpression, indexNode != null));
            calledMethod.setCalledBy(method);

            if (push && !trap && methodExpression.getReturnLongs() == 0) {
                logMessage(new CompilerException("method doesn't return a value", node.getToken()));
            }
        }
        else {
            logMessage(new CompilerException(node.getText() + " is not a method", node.getToken()));
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

    Spin1Bytecode compilePostEffect(Context context, Spin1StatementNode node, String type, boolean push) {
        int o = 3;
        if ("BYTE".equalsIgnoreCase(type)) {
            o = 1;
        }
        if ("WORD".equalsIgnoreCase(type)) {
            o = 2;
        }
        if ("++".equals(node.getText())) {
            int code = Spin1Bytecode.op_ss.setValue(0b0_0101_00_0, o);
            if (push) {
                code |= 0b10000000;
            }
            return new Bytecode(context, code, "POST_INC");
        }
        else if ("--".equals(node.getText())) {
            int code = Spin1Bytecode.op_ss.setValue(0b0_0111_00_0, o);
            if (push) {
                code |= 0b10000000;
            }
            return new Bytecode(context, code, "POST_DEC");
        }
        else if ("~".equals(node.getText())) {
            int code = 0b0_00110_00;
            if (push) {
                code |= 0b10000000;
            }
            return new Bytecode(context, code, "POST_CLEAR");
        }
        else if ("~~".equals(node.getText())) {
            int code = 0b0_00111_00;
            if (push) {
                code |= 0b10000000;
            }
            return new Bytecode(context, code, "POST_SET");
        }
        else if ("?".equals(node.getText())) {
            int code = 0b0_00011_00;
            if (push) {
                code |= 0b10000000;
            }
            return new Bytecode(context, code, "RANDOM_REVERSE");
        }
        throw new CompilerException("unhandled post effect " + node.getText(), node.getToken());
    }

    public Spin1Bytecode addStringData(Spin1Bytecode string) {
        if (compiler.isOptimizeStrings()) {
            for (Spin1Bytecode bc : stringData) {
                if (Arrays.equals(bc.getBytes(), string.getBytes())) {
                    return bc;
                }
            }

            byte[] b1 = string.getBytes();
            for (Spin1Bytecode bc : stringData) {
                byte[] b0 = bc.getBytes();
                if (b0.length > b1.length && Arrays.equals(b0, b0.length - b1.length, b0.length, b1, 0, b1.length)) {
                    Context sharedContext = new Context() {

                        @Override
                        public int getObjectAddress() {
                            return bc.getContext().getObjectAddress() + (b0.length - b1.length);
                        }

                    };
                    return new Spin1Bytecode(sharedContext) {

                        @Override
                        public Context getContext() {
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

}
