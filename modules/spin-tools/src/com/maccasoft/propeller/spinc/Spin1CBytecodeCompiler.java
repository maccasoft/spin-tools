/*
 * Copyright (c) 2023 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spinc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.expressions.Abs;
import com.maccasoft.propeller.expressions.Add;
import com.maccasoft.propeller.expressions.And;
import com.maccasoft.propeller.expressions.CharacterLiteral;
import com.maccasoft.propeller.expressions.ContextLiteral;
import com.maccasoft.propeller.expressions.DataVariable;
import com.maccasoft.propeller.expressions.Divide;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.LimitMax;
import com.maccasoft.propeller.expressions.LimitMin;
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
import com.maccasoft.propeller.expressions.Rev;
import com.maccasoft.propeller.expressions.Rol;
import com.maccasoft.propeller.expressions.Ror;
import com.maccasoft.propeller.expressions.Sar;
import com.maccasoft.propeller.expressions.ShiftLeft;
import com.maccasoft.propeller.expressions.SpinObject;
import com.maccasoft.propeller.expressions.Subtract;
import com.maccasoft.propeller.expressions.Trunc;
import com.maccasoft.propeller.expressions.Variable;
import com.maccasoft.propeller.expressions.Xor;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.spin1.Spin1Bytecode;
import com.maccasoft.propeller.spin1.Spin1Context;
import com.maccasoft.propeller.spin1.Spin1Method;
import com.maccasoft.propeller.spin1.Spin1StatementNode;
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

public abstract class Spin1CBytecodeCompiler {

    static class FunctionDescriptor {
        public byte[] code;
        public byte[] code_push;
        public int parameters;

        public FunctionDescriptor(int b, int parameters) {
            this.code = new byte[] {
                (byte) b
            };
            this.parameters = parameters;
        }

        public FunctionDescriptor(int b, int b_push, int parameters) {
            this.code = new byte[] {
                (byte) b
            };
            this.code_push = new byte[] {
                (byte) b_push
            };
            this.parameters = parameters;
        }
    }

    static Map<String, FunctionDescriptor> descriptors = new HashMap<String, FunctionDescriptor>();
    static {
        descriptors.put("strsize", new FunctionDescriptor(0b00010110, 0b00010110, 1));
        descriptors.put("strcomp", new FunctionDescriptor(0b00010111, 0b00010111, 2));

        descriptors.put("bytefill", new FunctionDescriptor(0b00011000, 3));
        descriptors.put("wordfill", new FunctionDescriptor(0b00011001, 3));
        descriptors.put("longfill", new FunctionDescriptor(0b00011010, 3));
        descriptors.put("waitpeq", new FunctionDescriptor(0b00011011, 3));

        descriptors.put("bytemove", new FunctionDescriptor(0b00011100, 3));
        descriptors.put("wordmove", new FunctionDescriptor(0b00011101, 3));
        descriptors.put("longmove", new FunctionDescriptor(0b00011110, 3));
        descriptors.put("waitpne", new FunctionDescriptor(0b00011111, 3));

        descriptors.put("clkset", new FunctionDescriptor(0b00100000, 2));
        descriptors.put("cogstop", new FunctionDescriptor(0b00100001, 1));
        descriptors.put("lockret", new FunctionDescriptor(0b00100010, 1));
        descriptors.put("waitcnt", new FunctionDescriptor(0b00100011, 1));

        descriptors.put("waitvid", new FunctionDescriptor(0b00100111, 2));

        descriptors.put("coginit", new FunctionDescriptor(0b00101100, 0b00101000, 3));
        descriptors.put("locknew", new FunctionDescriptor(0b00101101, 0b00101001, 0));
        descriptors.put("lockset", new FunctionDescriptor(0b00101110, 0b00101010, 1));
        descriptors.put("lockclr", new FunctionDescriptor(0b00101111, 0b00101011, 1));

        descriptors.put("ror", new FunctionDescriptor(0b111_00000, 0b111_00000, 2));
        descriptors.put("rol", new FunctionDescriptor(0b111_00001, 0b111_00001, 2));
        descriptors.put("max", new FunctionDescriptor(0b111_00100, 0b111_00100, 2));
        descriptors.put("min", new FunctionDescriptor(0b111_00101, 0b111_00101, 2));
        descriptors.put("abs", new FunctionDescriptor(0b111_01001, 0b111_01001, 1));
        descriptors.put("rev", new FunctionDescriptor(0b111_01111, 0b111_01111, 2));
        descriptors.put("encod", new FunctionDescriptor(0b111_10001, 0b111_10001, 1));
        descriptors.put("decod", new FunctionDescriptor(0b111_10011, 0b111_10011, 1));
        descriptors.put("sqrt", new FunctionDescriptor(0b111_11000, 0b111_11000, 1));
    }

    public static class OperatorDescriptor {
        int value;
        String text;

        public OperatorDescriptor(int value, String text) {
            this.value = value;
            this.text = text;
        }
    }

    static Map<String, OperatorDescriptor> operators = new HashMap<String, OperatorDescriptor>();
    static {
        //operators.put("->", new OperatorDescriptor(0b111_00000, "ROTATE_RIGHT")); //  rotate right
        //operators.put("<-", new OperatorDescriptor(0b111_00001, "ROTATE_LEFT")); //  rotate left
        //operators.put(">>", new OperatorDescriptor(0b111_00010, "SHIFT_RIGHT")); //  shift right
        operators.put("<<", new OperatorDescriptor(0b111_00011, "SHIFT_LEFT")); //  shift left
        //operators.put("#>", new OperatorDescriptor(0b111_00100, "LIMIT_MINIMUM")); //  limit minimum (signed)
        //operators.put("<#", new OperatorDescriptor(0b111_00101, "LIMIT_MAXIMUM")); //  limit maximum (signed)
        ////operations.put("-", new Descriptor(0b111_00110, "NEGATE")); //   negate
        //operators.put("!", new OperatorDescriptor(0b111_00111, "BITNOT")); //   bitwise not
        operators.put("&", new OperatorDescriptor(0b111_01000, "BITAND")); //   bitwise and
        //operators.put("||", new OperatorDescriptor(0b111_01001, "ABS")); //  absolute
        operators.put("|", new OperatorDescriptor(0b111_01010, "BITOR")); //   bitwise or
        operators.put("^", new OperatorDescriptor(0b111_01011, "BITXOR")); //   bitwise xor
        operators.put("+", new OperatorDescriptor(0b111_01100, "ADD")); //   add
        operators.put("-", new OperatorDescriptor(0b111_01101, "SUBTRACT")); //   subtract
        operators.put(">>", new OperatorDescriptor(0b111_01110, "SAR")); //  shift arithmetic right
        //operators.put("><", new OperatorDescriptor(0b111_01111, "REV")); //  reverse bits
        operators.put("&&", new OperatorDescriptor(0b111_10000, "BOOLEAN_AND")); // boolean and
        //operators.put(">|", new OperatorDescriptor(0b111_10001, "ENCODE")); //  encode (0-32)
        operators.put("||", new OperatorDescriptor(0b111_10010, "BOOLEAN_OR")); //  boolean or
        //operators.put("|<", new OperatorDescriptor(0b111_10011, "DECODE")); //  decode
        operators.put("*", new OperatorDescriptor(0b111_10100, "MULTIPLY")); //   multiply, return lower half (signed)
        //operators.put("**", new OperatorDescriptor(0b111_10101, "MULTIPLY_UPPER")); //  multiply, return upper half (signed)
        operators.put("/", new OperatorDescriptor(0b111_10110, "DIVIDE")); //   divide, return quotient (signed)
        operators.put("%", new OperatorDescriptor(0b111_10111, "REMAINDER")); //  divide, return remainder (signed)
        //operators.put("^^", new OperatorDescriptor(0b111_11000, "SQR")); //  square root
        operators.put("<", new OperatorDescriptor(0b111_11001, "TEST_BELOW")); //   test below (signed)
        operators.put(">", new OperatorDescriptor(0b111_11010, "TEST_ABOVE")); //   test above (signed)
        operators.put("!=", new OperatorDescriptor(0b111_11011, "TEST_NOT_EQUAL")); //  test not equal
        operators.put("==", new OperatorDescriptor(0b111_11100, "TEST_EQUAL")); //  test equal
        operators.put("<=", new OperatorDescriptor(0b111_11101, "TEST_BELOW_OR_EQUAL")); //  test below or equal (signed)
        operators.put(">=", new OperatorDescriptor(0b111_11110, "TEST_ABOVE_OR_EQUAL")); //  test above or equal (signed)
        operators.put("!", new OperatorDescriptor(0b111_11111, "BOOLEAN_NOT")); // boolean not
    }

    static Map<String, OperatorDescriptor> assignOperators = new HashMap<String, OperatorDescriptor>();
    static {
        //assignOperators.put("->=", new OperatorDescriptor(0b010_00000, "ROTATE_RIGHT")); //  rotate right
        //assignOperators.put("<-=", new OperatorDescriptor(0b010_00001, "ROTATE_LEFT")); //  rotate left
        //assignOperators.put(">>=", new OperatorDescriptor(0b010_00010, "SHIFT_RIGHT")); //  shift right
        assignOperators.put("<<=", new OperatorDescriptor(0b010_00011, "SHIFT_LEFT")); //  shift left
        //assignOperators.put("#>=", new OperatorDescriptor(0b010_00100, "LIMIT_MINIMUM")); //  limit minimum (signed)
        //assignOperators.put("<#=", new OperatorDescriptor(0b010_00101, "LIMIT_MAXIMUM")); //  limit maximum (signed)
        ////assignOperations.put("-=", new Descriptor(0b010_00110, "NEGATE")); //   negate
        //assignOperators.put("!=", new OperatorDescriptor(0b010_00111, "BITNOT")); //   bitwise not
        assignOperators.put("&=", new OperatorDescriptor(0b010_01000, "BITAND")); //   bitwise and
        //assignOperators.put("||=", new OperatorDescriptor(0b010_01001, "ABS")); //  absolute
        assignOperators.put("|=", new OperatorDescriptor(0b010_01010, "BITOR")); //   bitwise or
        assignOperators.put("^=", new OperatorDescriptor(0b010_01011, "BITXOR")); //   bitwise xor
        assignOperators.put("+=", new OperatorDescriptor(0b010_01100, "ADD")); //   add
        assignOperators.put("-=", new OperatorDescriptor(0b010_01101, "SUBTRACT")); //   subtract
        assignOperators.put(">>=", new OperatorDescriptor(0b010_01110, "SAR")); //  shift arithmetic right
        //assignOperators.put("><=", new OperatorDescriptor(0b010_01111, "REV")); //  reverse bits
        //assignOperators.put("AND=", new OperatorDescriptor(0b010_10000, "BOOLEAN_AND")); // boolean and
        //assignOperators.put(">|=", new OperatorDescriptor(0b010_10001, "ENCODE")); //  encode (0-32)
        //assignOperators.put("OR=", new OperatorDescriptor(0b010_10010, "BOOLEAN_OR")); //  boolean or
        //assignOperators.put("|<=", new OperatorDescriptor(0b010_10011, "DECODE")); //  decode
        assignOperators.put("*=", new OperatorDescriptor(0b010_10100, "MULTIPLY")); //   multiply, return lower half (signed)
        //assignOperators.put("**=", new OperatorDescriptor(0b010_10101, "MULTIPLY_UPPER")); //  multiply, return upper half (signed)
        assignOperators.put("/=", new OperatorDescriptor(0b010_10110, "DIVIDE")); //   divide, return quotient (signed)
        assignOperators.put("%=", new OperatorDescriptor(0b010_10111, "REMAINDER")); //  divide, return remainder (signed)
        //assignOperators.put("^^=", new OperatorDescriptor(0b010_11000, "SQR")); //  square root
        //assignOperators.put("<=", new OperatorDescriptor(0b010_11001, "TEST_BELOW")); //   test below (signed)
        //assignOperators.put(">=", new OperatorDescriptor(0b010_11010, "TEST_ABOVE")); //   test above (signed)
        //assignOperators.put("<>=", new OperatorDescriptor(0b010_11011, "TEST_NOT_EQUAL")); //  test not equal
        //assignOperators.put("===", new OperatorDescriptor(0b010_11100, "TEST_EQUAL")); //  test equal
        //assignOperators.put("=<=", new OperatorDescriptor(0b010_11101, "TEST_BELOW_OR_EQUAL")); //  test below or equal (signed)
        //assignOperators.put("=>=", new OperatorDescriptor(0b010_11110, "TEST_ABOVE_OR_EQUAL")); //  test above or equal (signed)
        //assignOperators.put("NOT=", new OperatorDescriptor(0b010_11111, "BOOLEAN_NOT")); // boolean not
    }

    List<Spin1Bytecode> stringData;

    public Spin1CBytecodeCompiler() {
        this.stringData = new ArrayList<>();
    }

    public List<Spin1Bytecode> compileBytecodeExpression(Spin1Context context, Spin1Method method, Spin1StatementNode node, boolean push) {
        List<Spin1Bytecode> source = new ArrayList<Spin1Bytecode>();

        try {
            if (node instanceof Spin1StatementNode.Method) {
                FunctionDescriptor desc = descriptors.get(node.getText());
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
                else if ("trunc".equals(node.getText())) {
                    if (node.getChildCount() != 1) {
                        throw new RuntimeException("expected " + 1 + " argument(s), found " + node.getChildCount());
                    }
                    try {
                        Expression expression = buildConstantExpression(context, node.getChild(0));
                        if (!expression.isConstant()) {
                            throw new CompilerException("expression is not constant", node.getChild(0).getToken());
                        }
                        source.add(new Constant(context, new Trunc(expression), false));
                    } catch (Exception e) {
                        throw new CompilerException("expression is not constant", node.getChild(0).getToken());
                    }
                }
                else if ("chipver".equals(node.getText())) {
                    source.add(new Bytecode(context, new byte[] {
                        (byte) 0x34, (byte) 0x80
                    }, "CHIPVER"));
                }
                else if ("clkfreq".equals(node.getText())) {
                    source.add(new Constant(context, new NumberLiteral(0), false));
                    source.add(new MemoryOp(context, MemoryOp.Size.Long, false, MemoryOp.Base.Pop, MemoryOp.Op.Read, null));
                }
                else if ("clkmode".equals(node.getText())) {
                    source.add(new Address(context, new NumberLiteral(4)));
                    source.add(new MemoryOp(context, MemoryOp.Size.Byte, false, MemoryOp.Base.Pop, MemoryOp.Op.Read, null));
                }
                else if ("cogid".equals(node.getText())) {
                    if (node.getChildCount() != 0) {
                        throw new RuntimeException("expected " + 0 + " argument(s), found " + node.getChildCount());
                    }
                    source.add(new RegisterOp(context, RegisterOp.Op.Read, 0x1E9));
                }
                else if ("cognew".equals(node.getText())) {
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

                        }, false));
                        source.addAll(compileBytecodeExpression(context, method, node.getChild(1), true));
                        source.add(new Bytecode(context, 0x15, "MARK_INTERPRETED"));
                    }
                    else {
                        source.add(new Constant(context, new NumberLiteral(-1), false));
                        source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));
                        source.addAll(compileBytecodeExpression(context, method, node.getChild(1), true));
                    }
                    desc = descriptors.get("coginit");
                    source.add(new Bytecode(context, push ? desc.code_push : desc.code, node.getText()));
                }
                else if ("lookdown".equals(node.getText()) || "lookdownz".equals(node.getText()) || "lookup".equals(node.getText()) || "lookupz".equals(node.getText())) {
                    if (node.getChildCount() == 0) {
                        throw new RuntimeException("expected argument(s), found none");
                    }
                    Spin1StatementNode argsNode = node.getChild(0);
                    if (!":".equalsIgnoreCase(argsNode.getText()) || argsNode.getChildCount() < 2) {
                        throw new RuntimeException("invalid argument(s)");
                    }

                    int code = 0b00010000;
                    if ("lookdown".equalsIgnoreCase(node.getText()) || "lookdownz".equalsIgnoreCase(node.getText())) {
                        code |= 0b00000001;
                    }
                    int code_range = code | 0b00000010;

                    source.add(new Constant(context, new NumberLiteral(node.getText().toUpperCase().endsWith("z") ? 0 : 1), false));

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
                                source.add(new Constant(context, new CharacterLiteral(s.substring(x, x + 1)), false));
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
                else if ("string".equals(node.getText())) {
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
                else {
                    Expression expression = context.getLocalSymbol(node.getText());
                    if (expression == null && !(expression instanceof Method)) {
                        throw new CompilerException("unknown function " + node.getText(), node.getToken());
                    }
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
            }
            else if (node.getType() == Token.NUMBER) {
                Expression expression = new NumberLiteral(node.getText());
                source.add(new Constant(context, expression, false));
            }
            else if (node.getType() == Token.CHAR) {
                String s = node.getText().substring(1, node.getText().length() - 1);
                if (s.length() != 1) {
                    throw new CompilerException("invalid character constant", node.getToken());
                }
                source.add(new Constant(context, new CharacterLiteral(s), false));
            }
            else if (node.getType() == Token.STRING) {
                String s = node.getText().substring(1, node.getText().length() - 1) + (char) 0x00;
                Spin1Bytecode target = addStringData(new Bytecode(context, s.getBytes(), "STRING".toUpperCase()));
                source.add(new MemoryOp(context, MemoryOp.Size.Byte, false, MemoryOp.Base.PBase, MemoryOp.Op.Address, new ContextLiteral(target.getContext())));
            }
            else if ("-".equals(node.getText()) && node.getChildCount() == 1) {
                if (node.getChild(0).getToken().type == Token.NUMBER) {
                    Spin1Bytecode bc1 = new Constant(context, new Negative(new NumberLiteral(node.getChild(0).getText())), false);
                    Spin1Bytecode bc2 = new Constant(context, new Subtract(new NumberLiteral(node.getChild(0).getText()), new NumberLiteral(1)), false);
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
                        source.add(new Constant(context, new Negative(expression), false));
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
            }
            else if ("=".equals(node.getText())) {
                if (node.getChildCount() != 2) {
                    throw new CompilerException("expression syntax error", node.getToken());
                }
                source.addAll(compileBytecodeExpression(context, method, node.getChild(1), true));
                source.addAll(leftAssign(context, method, node.getChild(0), push));
                if (push) {
                    source.add(new Bytecode(context, 0x80, "WRITE"));
                }
            }
            else if (assignOperators.containsKey(node.getText())) {
                OperatorDescriptor desc = assignOperators.get(node.getText());
                if (node.getChildCount() != 2) {
                    throw new CompilerException("expression syntax error", node.getToken());
                }
                source.addAll(compileBytecodeExpression(context, method, node.getChild(1), true));
                source.addAll(leftAssign(context, method, node.getChild(0), true));
                source.add(new Bytecode(context, desc.value | (push ? 0b10000000 : 0b00000000), desc.text));
            }
            else if ("!".equals(node.getText())) {
                if (node.getChildCount() != 1) {
                    throw new CompilerException("expression syntax error", node.getToken());
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
            else if (operators.containsKey(node.getText())) {
                OperatorDescriptor desc = operators.get(node.getText());
                if (node.getChildCount() != 2) {
                    throw new CompilerException("expression syntax error", node.getToken());
                }
                try {
                    Expression expression = buildConstantExpression(context, node);
                    source.add(new Constant(context, expression, false));
                } catch (Exception e) {
                    source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));
                    source.addAll(compileBytecodeExpression(context, method, node.getChild(1), true));
                    source.add(new Bytecode(context, desc.value | (push ? 0b10000000 : 0b00000000), desc.text));
                }
            }
            else if ("?".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 2) {
                    throw new CompilerException("expression syntax error", node.getToken());
                }
                if (!":".equals(node.getChild(1).getText())) {
                    throw new CompilerException("expression syntax error", node.getChild(1).getToken());
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
            else if ("..".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 2) {
                    throw new CompilerException("expression syntax error", node.getToken());
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
                            }
                            else {
                                if (isAbsoluteAddress(node.getText())) {
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
                        }
                        else {
                            source.add(new MemoryOp(context, ss, popIndex, MemoryOp.Base.PBase, MemoryOp.Op.Read, expression));
                        }
                    }
                    else if (expression.isConstant()) {
                        if (node.getChildCount() != 0) {
                            throw new RuntimeException("syntax error");
                        }
                        source.add(new Constant(context, expression, false));
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

    protected boolean isAbsoluteAddress(String text) {
        return text.startsWith("@@");
    }

    List<Spin1Bytecode> compileConstantExpression(Spin1Context context, Spin1Method method, Spin1StatementNode node) {
        if (!false) {
            try {
                Expression expression = buildConstantExpression(context, node);
                if (expression.isConstant()) {
                    return Collections.singletonList(new Constant(context, expression, false));
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
            throw new RuntimeException("string not allowed");
        }
        else if (node.getType() == Token.CHAR) {
            String s = node.getText().substring(1, node.getText().length() - 1);
            if (s.length() != 1) {
                throw new RuntimeException("invalid character constant");
            }
            return new CharacterLiteral(s);
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
        if ("%".equals(node.getText())) {
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
            return new Sar(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
        }
        if ("ABS".equalsIgnoreCase(node.getText())) {
            if (node.getChildCount() != 1) {
                throw new RuntimeException("misplaced unary operator (" + node.getText() + ")");
            }
            return new Abs(buildConstantExpression(context, node.getChild(0)));
        }
        if ("MIN".equalsIgnoreCase(node.getText())) {
            return new LimitMax(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
        }
        if ("MAX".equalsIgnoreCase(node.getText())) {
            return new LimitMin(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
        }
        if ("ROR".equalsIgnoreCase(node.getText())) {
            return new Ror(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
        }
        if ("ROL".equalsIgnoreCase(node.getText())) {
            return new Rol(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
        }
        if ("REV".equalsIgnoreCase(node.getText())) {
            return new Rev(buildConstantExpression(context, node.getChild(0)), buildConstantExpression(context, node.getChild(1)));
        }
        if ("TRUNC".equalsIgnoreCase(node.getText())) {
            if (node.getChildCount() != 1) {
                throw new RuntimeException("misplaced unary operator (" + node.getText() + ")");
            }
            return new Trunc(buildConstantExpression(context, node.getChild(0)));
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
        return "++".equals(s) || "--".equals(s);
    }

    public Spin1Bytecode addStringData(Spin1Bytecode string) {
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
        stringData.add(string);

        return string;
    }

    public List<Spin1Bytecode> getStringData() {
        return stringData;
    }

    protected abstract void logMessage(CompilerException message);

}
