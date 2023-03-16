/*
 * Copyright (c) 2023 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.ListOrderedMap;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.expressions.Abs;
import com.maccasoft.propeller.expressions.Add;
import com.maccasoft.propeller.expressions.Addbits;
import com.maccasoft.propeller.expressions.Addpins;
import com.maccasoft.propeller.expressions.And;
import com.maccasoft.propeller.expressions.CharacterLiteral;
import com.maccasoft.propeller.expressions.ContextLiteral;
import com.maccasoft.propeller.expressions.DataVariable;
import com.maccasoft.propeller.expressions.Divide;
import com.maccasoft.propeller.expressions.Equals;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.GreaterOrEquals;
import com.maccasoft.propeller.expressions.GreaterThan;
import com.maccasoft.propeller.expressions.IfElse;
import com.maccasoft.propeller.expressions.LessOrEquals;
import com.maccasoft.propeller.expressions.LessThan;
import com.maccasoft.propeller.expressions.LocalVariable;
import com.maccasoft.propeller.expressions.LogicalAnd;
import com.maccasoft.propeller.expressions.LogicalOr;
import com.maccasoft.propeller.expressions.LogicalXor;
import com.maccasoft.propeller.expressions.MemoryContextLiteral;
import com.maccasoft.propeller.expressions.Method;
import com.maccasoft.propeller.expressions.Modulo;
import com.maccasoft.propeller.expressions.Multiply;
import com.maccasoft.propeller.expressions.Nan;
import com.maccasoft.propeller.expressions.Negative;
import com.maccasoft.propeller.expressions.Not;
import com.maccasoft.propeller.expressions.NotEquals;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.expressions.ObjectContextLiteral;
import com.maccasoft.propeller.expressions.Or;
import com.maccasoft.propeller.expressions.Register;
import com.maccasoft.propeller.expressions.Round;
import com.maccasoft.propeller.expressions.Sca;
import com.maccasoft.propeller.expressions.Scas;
import com.maccasoft.propeller.expressions.ShiftLeft;
import com.maccasoft.propeller.expressions.ShiftRight;
import com.maccasoft.propeller.expressions.SpinObject;
import com.maccasoft.propeller.expressions.Sqrt;
import com.maccasoft.propeller.expressions.Subtract;
import com.maccasoft.propeller.expressions.Trunc;
import com.maccasoft.propeller.expressions.UnsignedDivide;
import com.maccasoft.propeller.expressions.UnsignedModulo;
import com.maccasoft.propeller.expressions.Variable;
import com.maccasoft.propeller.expressions.Xor;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.spin2.Spin2Bytecode.Descriptor;
import com.maccasoft.propeller.spin2.Spin2ObjectCompiler.ObjectInfo;
import com.maccasoft.propeller.spin2.bytecode.Address;
import com.maccasoft.propeller.spin2.bytecode.BitField;
import com.maccasoft.propeller.spin2.bytecode.Bytecode;
import com.maccasoft.propeller.spin2.bytecode.Constant;
import com.maccasoft.propeller.spin2.bytecode.MathOp;
import com.maccasoft.propeller.spin2.bytecode.MemoryOp;
import com.maccasoft.propeller.spin2.bytecode.RegisterOp;
import com.maccasoft.propeller.spin2.bytecode.VariableOp;

public abstract class Spin2BytecodeCompiler {

    Map<String, ObjectInfo> objects;

    List<Object> debugStatements;

    Spin2BytecodeCompiler() {
        this.objects = ListOrderedMap.listOrderedMap(new HashMap<String, ObjectInfo>());
        this.debugStatements = new ArrayList<Object>();
    }

    public Spin2BytecodeCompiler(Map<String, ObjectInfo> objects, List<Object> debugStatements) {
        this.objects = objects;
        this.debugStatements = debugStatements;
    }

    public List<Spin2Bytecode> compileBytecodeExpression(Spin2Context context, Spin2StatementNode node) {
        return compileBytecodeExpression(context, node, false);
    }

    public List<Spin2Bytecode> compileBytecodeExpression(Spin2Context context, Spin2StatementNode node, boolean push) {
        List<Spin2Bytecode> source = new ArrayList<Spin2Bytecode>();

        try {
            Descriptor desc = Spin2Bytecode.getDescriptor(node.getText());
            if (desc != null) {
                int actual = getArgumentsCount(context, node);
                if (actual != desc.getParameters()) {
                    throw new RuntimeException("expected " + desc.getParameters() + " argument(s), found " + actual);
                }
                for (int i = 0; i < node.getChildCount(); i++) {
                    source.addAll(compileConstantExpression(context, node.getChild(i)));
                }
                source.add(new Bytecode(context, desc.code, node.getText().toUpperCase()));
            }
            else if ("ABORT".equalsIgnoreCase(node.getText())) {
                int actual = getArgumentsCount(context, node);
                if (actual == 0) {
                    source.add(new Bytecode(context, 0x06, node.getText().toUpperCase()));
                }
                else if (actual == 1) {
                    source.addAll(compileBytecodeExpression(context, node.getChild(0), true));
                    source.add(new Bytecode(context, 0x07, node.getText().toUpperCase()));
                }
                else {
                    throw new RuntimeException("expected 0 or 1 argument(s), found " + actual);
                }
            }
            else if ("COGINIT".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 3) {
                    throw new RuntimeException("expected " + 3 + " argument(s), found " + node.getChildCount());
                }
                for (int i = 0; i < node.getChildCount(); i++) {
                    source.addAll(compileBytecodeExpression(context, node.getChild(i), true));
                }
                source.add(new Bytecode(context, push ? 0x26 : 0x25, node.getText().toUpperCase()));
            }
            else if ("COGNEW".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 2) {
                    throw new RuntimeException("expected " + 2 + " argument(s), found " + node.getChildCount());
                }
                source.add(new Constant(context, new NumberLiteral(16)));
                for (int i = 0; i < node.getChildCount(); i++) {
                    source.addAll(compileBytecodeExpression(context, node.getChild(i), true));
                }
                source.add(new Bytecode(context, push ? 0x26 : 0x25, node.getText().toUpperCase()));
            }
            else if ("COGSPIN".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 3) {
                    throw new RuntimeException("expected " + 3 + " argument(s), found " + node.getChildCount());
                }

                source.addAll(compileConstantExpression(context, node.getChild(0)));

                Spin2StatementNode methodNode = node.getChild(1);
                Expression expression = context.getLocalSymbol(methodNode.getText());
                if (!(expression instanceof Method)) {
                    throw new CompilerException("invalid method " + methodNode.getText(), methodNode.getToken());
                }
                int actual = getArgumentsCount(context, methodNode);
                if (actual != ((Method) expression).getArgumentsCount()) {
                    throw new CompilerException("expected " + ((Method) expression).getArgumentsCount() + " argument(s), found " + actual, methodNode.getToken());
                }
                for (int i = 0; i < methodNode.getChildCount(); i++) {
                    source.addAll(compileConstantExpression(context, methodNode.getChild(i)));
                }
                source.add(new Bytecode(context, new byte[] {
                    (byte) 0x11,
                    (byte) ((Method) expression).getOffset()
                }, "SUB_ADDRESS (" + ((Method) expression).getOffset() + ")"));

                source.addAll(compileConstantExpression(context, node.getChild(2)));

                source.add(new Bytecode(context, new byte[] {
                    0x19, 0x5A
                }, node.getText().toUpperCase()));

                source.add(new Bytecode(context, new byte[] {
                    (byte) methodNode.getChildCount(), (byte) (push ? 0x26 : 0x25)
                }, "POP_RETURN (???)"));
            }
            else if ("RECV".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 0) {
                    throw new RuntimeException("expected " + 0 + " argument(s), found " + node.getChildCount());
                }
                source.add(new Bytecode(context, 0x0C, node.getText().toUpperCase()));
            }
            else if ("SEND".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() == 0) {
                    throw new RuntimeException("syntax error");
                }
                boolean bytes = true;
                for (Spin2StatementNode child : node.getChilds()) {
                    if (child.getType() != Token.NUMBER) {
                        bytes = false;
                        break;
                    }
                }
                if (bytes) {
                    byte[] code = new byte[node.getChildCount() + 2];
                    code[0] = 0x0E;
                    code[1] = (byte) node.getChildCount();
                    for (int i = 0; i < node.getChildCount(); i++) {
                        code[i + 2] = (byte) new NumberLiteral(node.getChild(i).getText()).getNumber().intValue();
                    }
                    source.add(new Bytecode(context, code, node.getText().toUpperCase()));
                }
                else {
                    for (Spin2StatementNode child : node.getChilds()) {
                        source.addAll(compileBytecodeExpression(context, child, true));
                        source.add(new Bytecode(context, 0x0D, node.getText().toUpperCase()));
                    }
                }
            }
            else if ("DEBUG".equalsIgnoreCase(node.getText())) {
                int debugIndex = debugStatements.size() + 1;
                if (debugIndex >= 255) {
                    throw new RuntimeException("too much debug statements");
                }

                int pop = 0;
                for (Spin2StatementNode child : node.getChilds()) {
                    for (Spin2StatementNode param : child.getChilds()) {
                        source.addAll(compileBytecodeExpression(context, param, true));
                        pop += 4;
                    }
                }
                node.setData("context", context);
                debugStatements.add(node);
                source.add(new Bytecode(context, new byte[] {
                    0x43, (byte) pop, (byte) debugIndex
                }, node.getText().toUpperCase() + " #" + debugIndex));
            }
            else if ("END".equalsIgnoreCase(node.getText())) {
                // Ignored
            }
            else if ("LOOKDOWN".equalsIgnoreCase(node.getText()) || "LOOKDOWNZ".equalsIgnoreCase(node.getText()) || "LOOKUP".equalsIgnoreCase(node.getText())
                || "LOOKUPZ".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() == 0) {
                    throw new RuntimeException("expected argument(s), found none");
                }
                Spin2StatementNode argsNode = node.getChild(0);
                if (!":".equalsIgnoreCase(argsNode.getText()) || argsNode.getChildCount() < 2) {
                    throw new RuntimeException("invalid argument(s)");
                }

                int code = 0x1F;
                int code_range = 0x21;
                if ("LOOKDOWN".equalsIgnoreCase(node.getText()) || "LOOKDOWNZ".equalsIgnoreCase(node.getText())) {
                    code = 0x20;
                    code_range = 0x22;
                }

                Spin2Bytecode end = new Spin2Bytecode(context);
                source.add(new Address(context, new ContextLiteral(end.getContext())));

                source.addAll(compileBytecodeExpression(context, argsNode.getChild(0), true));

                source.add(new Constant(context, new NumberLiteral(node.getText().toUpperCase().endsWith("Z") ? 0 : 1)));

                for (int i = 1; i < argsNode.getChildCount(); i++) {
                    Spin2StatementNode arg = argsNode.getChild(i);
                    if ("..".equals(arg.getText())) {
                        source.addAll(compileBytecodeExpression(context, arg.getChild(0), true));
                        source.addAll(compileBytecodeExpression(context, arg.getChild(1), true));
                        source.add(new Bytecode(context, code_range, node.getText().toUpperCase()));
                    }
                    else if (arg.getType() == Token.STRING) {
                        String s = arg.getText().substring(1, arg.getText().length() - 1);
                        for (int x = 0; x < s.length(); x++) {
                            source.add(new Constant(context, new CharacterLiteral(s.substring(x, x + 1))));
                            source.add(new Bytecode(context, code, node.getText().toUpperCase()));
                        }
                    }
                    else {
                        source.addAll(compileBytecodeExpression(context, arg, true));
                        source.add(new Bytecode(context, code, node.getText().toUpperCase()));
                    }
                }

                source.add(new Bytecode(context, 0x23, "LOOKDONE"));
                source.add(end);
            }
            else if ("STRING".equalsIgnoreCase(node.getText())) {
                StringBuilder sb = new StringBuilder();
                for (Spin2StatementNode child : node.getChilds()) {
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
                byte[] code = new byte[sb.length() + 3];
                int index = 0;
                code[index++] = (byte) 0x9E;
                code[index++] = (byte) (sb.length() + 1);
                for (int i = 0; i < sb.length(); i++) {
                    code[index++] = (byte) sb.charAt(i);
                }
                code[index++] = (byte) 0x00;
                source.add(new Bytecode(context, code, node.getText().toUpperCase()));
            }
            else if (node.getType() == Token.NUMBER) {
                Expression expression = new NumberLiteral(node.getText());
                source.add(new Constant(context, expression));
            }
            else if (node.getType() == Token.STRING) {
                String s = node.getText();
                if (s.startsWith("@")) {
                    s = s.substring(2, s.length() - 1);
                    byte[] code = new byte[s.length() + 3];
                    int index = 0;
                    code[index++] = (byte) 0x9E;
                    code[index++] = (byte) (s.length() + 1);
                    for (int i = 0; i < s.length(); i++) {
                        code[index++] = (byte) s.charAt(i);
                    }
                    code[index++] = (byte) 0x00;
                    source.add(new Bytecode(context, code, "STRING"));
                }
                else {
                    s = s.substring(1, s.length() - 1);
                    if (s.length() == 1) {
                        Expression expression = new CharacterLiteral(s);
                        source.add(new Constant(context, expression));
                    }
                    else {
                        byte[] code = new byte[s.length() + 3];
                        int index = 0;
                        code[index++] = (byte) 0x9E;
                        code[index++] = (byte) (s.length() + 1);
                        for (int i = 0; i < s.length(); i++) {
                            code[index++] = (byte) s.charAt(i);
                        }
                        code[index++] = (byte) 0x00;
                        source.add(new Bytecode(context, code, "STRING"));
                    }
                }
            }
            else if ("-".equalsIgnoreCase(node.getText()) && node.getChildCount() == 1) {
                try {
                    Expression expression = buildConstantExpression(context, node);
                    source.add(new Constant(context, expression));
                } catch (Exception e) {
                    source.addAll(compileBytecodeExpression(context, node.getChild(0), true));
                    source.add(new Bytecode(context, 0x79, "NEGATE"));
                }
            }
            else if ("-=".equalsIgnoreCase(node.getText()) && node.getChildCount() == 1) {
                source.addAll(leftAssign(context, node.getChild(0), true, false));
                if (push) {
                    source.add(new Bytecode(context, 0xB9, "NEGATE_ASSIGN (push)"));
                }
                else {
                    source.add(new Bytecode(context, 0x92, "NEGATE_ASSIGN"));
                }
            }
            else if (isAssign(node.getText())) {
                if (node.getChildCount() != 2) {
                    throw new RuntimeException("expression syntax error");
                }
                source.addAll(compileConstantExpression(context, node.getChild(1)));
                source.addAll(leftAssign(context, node.getChild(0), push, push));
            }
            else if (MathOp.isAssignMathOp(node.getText()) && node.getChildCount() == 1) {
                source.addAll(leftAssign(context, node.getChild(0), true, false));
                source.add(new MathOp(context, node.getText(), push));
            }
            else if (MathOp.isAssignMathOp(node.getText())) {
                source.addAll(compileConstantExpression(context, node.getChild(1)));
                source.addAll(leftAssign(context, node.getChild(0), true, false));
                source.add(new MathOp(context, node.getText(), push));
            }
            else if (MathOp.isUnaryMathOp(node.getText())) {
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("expression syntax error");
                }
                source.addAll(compileBytecodeExpression(context, node.getChild(0), true));
                source.add(new MathOp(context, node.getText(), push));
            }
            else if (MathOp.isMathOp(node.getText())) {
                if (node.getChildCount() != 2) {
                    throw new RuntimeException("expression syntax error");
                }
                source.addAll(compileBytecodeExpression(context, node.getChild(0), true));
                source.addAll(compileBytecodeExpression(context, node.getChild(1), true));
                source.add(new MathOp(context, node.getText(), false));
            }
            else if ("?".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 2) {
                    throw new RuntimeException("expression syntax error");
                }
                if (!":".equals(node.getChild(1).getText())) {
                    throw new RuntimeException("expression syntax error");
                }
                source.addAll(compileBytecodeExpression(context, node.getChild(0), true));
                source.addAll(compileBytecodeExpression(context, node.getChild(1).getChild(0), true));
                source.addAll(compileBytecodeExpression(context, node.getChild(1).getChild(1), true));
                source.add(new Bytecode(context, 0x6B, "TERNARY_IF_ELSE"));
            }
            else if ("_".equalsIgnoreCase(node.getText())) {
                source.add(new Bytecode(context, 0x17, "POP"));
            }
            else if ("(".equalsIgnoreCase(node.getText())) {
                source.addAll(compileBytecodeExpression(context, node.getChild(0), push));
            }
            else if (",".equalsIgnoreCase(node.getText())) {
                for (Spin2StatementNode child : node.getChilds()) {
                    source.addAll(compileBytecodeExpression(context, child, push));
                }
            }
            else if ("\\".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() == 2) {
                    source.addAll(compileConstantExpression(context, node.getChild(1)));
                    source.addAll(leftAssign(context, node.getChild(0), push, false));
                    source.add(new Bytecode(context, 0x8D, "SWAP"));
                }
                else {
                    if (node.getChildCount() != 1) {
                        throw new RuntimeException("expression syntax error");
                    }
                    Expression expression = context.getLocalSymbol(node.getChild(0).getText());

                    if (expression instanceof Method) {
                        source.addAll(compileMethodCall(context, expression, node.getChild(0), push, true));
                    }
                    else if (expression instanceof Variable) {
                        source.addAll(compileMethodCall(context, expression, node.getChild(0), push, true));
                    }
                    else if (expression instanceof DataVariable) {
                        source.addAll(compileMethodCall(context, expression, node.getChild(0), push, true));
                    }
                    else if ("BYTE".equalsIgnoreCase(node.getChild(0).getText()) || "WORD".equalsIgnoreCase(node.getChild(0).getText()) || "LONG".equalsIgnoreCase(node.getChild(0).getText())) {
                        source.addAll(compileBytecodeExpression(context, node.getChild(0), push));
                    }
                    else {
                        throw new CompilerException("symbol " + node.getChild(0).getText() + " is not a method", node.getChild(0).getToken());
                    }
                }
            }
            else if ("++".equalsIgnoreCase(node.getText()) || "--".equalsIgnoreCase(node.getText()) || "??".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("expression syntax error " + node.getText());
                }

                Spin2StatementNode childNode = node.getChild(0);
                if ("BYTE".equalsIgnoreCase(childNode.getText()) || "WORD".equalsIgnoreCase(childNode.getText()) || "LONG".equalsIgnoreCase(childNode.getText())) {
                    Spin2StatementNode indexNode = null;
                    Spin2StatementNode bitfieldNode = null;

                    int n = 1;
                    if (n < childNode.getChildCount() && (childNode.getChild(n) instanceof Spin2StatementNode.Index)) {
                        indexNode = childNode.getChild(n++);
                    }
                    if (n < childNode.getChildCount() && ".".equals(childNode.getChild(n).getText())) {
                        n++;
                        if (n >= childNode.getChildCount()) {
                            throw new CompilerException("expected bitfield expression", childNode.getToken());
                        }
                        if (!(childNode.getChild(n) instanceof Spin2StatementNode.Index)) {
                            throw new RuntimeException("syntax error");
                        }
                        bitfieldNode = childNode.getChild(n++);
                    }
                    if (n < childNode.getChildCount()) {
                        throw new RuntimeException("syntax error");
                    }

                    source.addAll(compileBytecodeExpression(context, childNode.getChild(0), true));

                    if (indexNode != null) {
                        source.addAll(compileBytecodeExpression(context, indexNode, true));
                    }

                    int bitfield = -1;
                    if (bitfieldNode != null) {
                        bitfield = compileBitfield(context, bitfieldNode, source);
                    }

                    MemoryOp.Size ss = MemoryOp.Size.Long;
                    if ("BYTE".equalsIgnoreCase(childNode.getText())) {
                        ss = MemoryOp.Size.Byte;
                    }
                    else if ("WORD".equalsIgnoreCase(childNode.getText())) {
                        ss = MemoryOp.Size.Word;
                    }
                    source.add(new MemoryOp(context, ss, MemoryOp.Base.Pop, MemoryOp.Op.Setup, indexNode != null));

                    if (bitfieldNode != null) {
                        source.add(new BitField(context, BitField.Op.Setup, bitfield));
                    }
                }
                else {
                    Expression expression = context.getLocalSymbol(childNode.getText());
                    if (expression == null) {
                        throw new CompilerException("unsupported operation on " + childNode.getText(), childNode.getToken());
                    }
                    source.addAll(compileVariableSetup(context, expression, childNode));
                }
                if ("++".equalsIgnoreCase(node.getText())) {
                    source.add(new Bytecode(context, push ? 0x85 : 0x83, "PRE_INC" + (push ? " (push)" : "")));
                }
                else if ("--".equalsIgnoreCase(node.getText())) {
                    source.add(new Bytecode(context, push ? 0x86 : 0x84, "PRE_DEC" + (push ? " (push)" : "")));
                }
                else if ("??".equalsIgnoreCase(node.getText())) {
                    source.add(new Bytecode(context, push ? 0x8F : 0x8E, "PRE_RND" + (push ? " (push)" : "")));
                }
            }
            else if ("BYTE".equalsIgnoreCase(node.getText()) || "WORD".equalsIgnoreCase(node.getText()) || "LONG".equalsIgnoreCase(node.getText())
                || "@BYTE".equalsIgnoreCase(node.getText()) || "@WORD".equalsIgnoreCase(node.getText()) || "@LONG".equalsIgnoreCase(node.getText())
                || "@@BYTE".equalsIgnoreCase(node.getText()) || "@@WORD".equalsIgnoreCase(node.getText()) || "@@LONG".equalsIgnoreCase(node.getText())) {
                Spin2StatementNode indexNode = null;
                Spin2StatementNode bitfieldNode = null;
                Spin2StatementNode postEffectNode = null;

                int n = 1;
                if (n < node.getChildCount() && (node.getChild(n) instanceof Spin2StatementNode.Index)) {
                    indexNode = node.getChild(n++);
                }
                if (n < node.getChildCount() && ".".equals(node.getChild(n).getText())) {
                    if (node.getText().startsWith("@")) {
                        throw new CompilerException("bitfield expression not allowed", node.getChild(n).getToken());
                    }
                    n++;
                    if (n >= node.getChildCount()) {
                        throw new RuntimeException("expected bitfield expression");
                    }
                    if (!(node.getChild(n) instanceof Spin2StatementNode.Index)) {
                        throw new RuntimeException("syntax error");
                    }
                    bitfieldNode = node.getChild(n++);
                }

                if (node instanceof Spin2StatementNode.Method) {
                    if (node.getParent() != null && node.getParent().getText().startsWith("\\")) {
                        source.add(new Bytecode(context, push ? 0x03 : 0x02, "ANCHOR_TRAP"));
                    }
                    else {
                        source.add(new Bytecode(context, push ? 0x01 : 0x00, "ANCHOR"));
                    }

                    while (n < node.getChildCount()) {
                        if (!(node.getChild(n) instanceof Spin2StatementNode.Argument)) {
                            throw new CompilerException("syntax error", node.getChild(n));
                        }
                        source.addAll(compileConstantExpression(context, node.getChild(n++)));
                    }
                }
                else if (node.getText().startsWith("@")) {
                    if (n < node.getChildCount()) {
                        throw new CompilerException("syntax error", node.getChild(n).getToken());
                    }
                }

                if (n < node.getChildCount() && isPostEffect(node.getChild(n))) {
                    postEffectNode = node.getChild(n++);
                }
                if (n < node.getChildCount()) {
                    throw new RuntimeException("syntax error");
                }

                source.addAll(compileBytecodeExpression(context, node.getChild(0), true));

                if (indexNode != null) {
                    source.addAll(compileBytecodeExpression(context, indexNode, true));
                }

                int bitfield = -1;
                if (bitfieldNode != null) {
                    bitfield = compileBitfield(context, bitfieldNode, source);
                }

                MemoryOp.Size ss = MemoryOp.Size.Long;
                if ("BYTE".equalsIgnoreCase(node.getText()) || "@BYTE".equalsIgnoreCase(node.getText()) || "@@BYTE".equalsIgnoreCase(node.getText())) {
                    ss = MemoryOp.Size.Byte;
                }
                else if ("WORD".equalsIgnoreCase(node.getText()) || "@WORD".equalsIgnoreCase(node.getText()) || "@@WORD".equalsIgnoreCase(node.getText())) {
                    ss = MemoryOp.Size.Word;
                }

                if (node instanceof Spin2StatementNode.Method) {
                    if (ss != MemoryOp.Size.Long) {
                        throw new RuntimeException("method pointers must be long");
                    }
                    source.add(new MemoryOp(context, ss, MemoryOp.Base.Pop, MemoryOp.Op.Read, indexNode != null));
                    source.add(new Bytecode(context, new byte[] {
                        (byte) 0x0B,
                    }, "CALL_PTR"));
                }
                else if (node.getText().startsWith("@@")) {
                    source.add(new MemoryOp(context, ss, MemoryOp.Base.Pop, MemoryOp.Op.Read, indexNode != null));
                    source.add(new Bytecode(context, 0x24, "ADD_PBASE"));
                }
                else if (node.getText().startsWith("@")) {
                    source.add(new MemoryOp(context, ss, MemoryOp.Base.Pop, MemoryOp.Op.Address, indexNode != null));
                }
                else {
                    MemoryOp.Op op = bitfieldNode == null && postEffectNode == null ? (push ? MemoryOp.Op.Read : MemoryOp.Op.Write) : MemoryOp.Op.Setup;
                    source.add(new MemoryOp(context, ss, MemoryOp.Base.Pop, op, indexNode != null));
                }

                if (bitfieldNode != null) {
                    source.add(new BitField(context, postEffectNode == null ? BitField.Op.Read : BitField.Op.Setup, bitfield));
                }

                if (postEffectNode != null) {
                    compilePostEffect(context, postEffectNode, source, push);
                }
            }
            else if ("REG".equalsIgnoreCase(node.getText())) {
                Spin2StatementNode indexNode = null;
                Spin2StatementNode bitfieldNode = null;
                Spin2StatementNode postEffectNode = null;

                int n = 1;
                if (n < node.getChildCount() && (node.getChild(n) instanceof Spin2StatementNode.Index)) {
                    indexNode = node.getChild(n++);
                }
                if (n < node.getChildCount() && ".".equals(node.getChild(n).getText())) {
                    n++;
                    if (n >= node.getChildCount()) {
                        throw new RuntimeException("expected bitfield expression");
                    }
                    if (!(node.getChild(n) instanceof Spin2StatementNode.Index)) {
                        throw new RuntimeException("syntax error");
                    }
                    bitfieldNode = node.getChild(n++);
                }

                if (n < node.getChildCount() && isPostEffect(node.getChild(n))) {
                    postEffectNode = node.getChild(n++);
                }
                if (n < node.getChildCount()) {
                    throw new RuntimeException("syntax error");
                }

                int bitfield = -1;
                if (bitfieldNode != null) {
                    bitfield = compileBitfield(context, bitfieldNode, source);
                }

                Expression expression = null;
                try {
                    expression = buildConstantExpression(context, node.getChild(0), true);
                } catch (Exception e) {
                    // Do nothing
                }
                if (expression == null) {
                    throw new CompilerException("expected constant expression", node.getChild(0).getToken());
                }

                int index = 0;
                boolean popIndex = indexNode != null;
                if (indexNode != null) {
                    try {
                        Expression exp = buildConstantExpression(context, indexNode);
                        if (exp.isConstant()) {
                            index = exp.getNumber().intValue();
                            popIndex = false;
                        }
                    } catch (Exception e) {
                        // Do nothing
                    }
                    if (popIndex) {
                        source.addAll(compileBytecodeExpression(context, indexNode, true));
                    }
                }

                RegisterOp.Op op = bitfieldNode == null && postEffectNode == null ? (push ? RegisterOp.Op.Read : RegisterOp.Op.Write) : RegisterOp.Op.Setup;
                source.add(new RegisterOp(context, op, popIndex, expression, index));

                if (bitfieldNode != null) {
                    source.add(new BitField(context, postEffectNode == null ? BitField.Op.Read : BitField.Op.Setup, bitfield));
                }

                if (postEffectNode != null) {
                    compilePostEffect(context, postEffectNode, source, push);
                }
            }
            else if ("FIELD".equalsIgnoreCase(node.getText()) && node.getChildCount() != 0) {
                Spin2StatementNode indexNode = null;
                Spin2StatementNode postEffectNode = null;

                int n = 1;
                if (n < node.getChildCount() && (node.getChild(n) instanceof Spin2StatementNode.Index)) {
                    indexNode = node.getChild(n++);
                }
                if (n < node.getChildCount() && isPostEffect(node.getChild(n))) {
                    postEffectNode = node.getChild(n++);
                }
                if (n < node.getChildCount()) {
                    throw new RuntimeException("syntax error");
                }

                source.addAll(compileConstantExpression(context, node.getChild(0)));

                if (indexNode != null) {
                    source.addAll(compileConstantExpression(context, indexNode));
                }

                if (postEffectNode == null) {
                    source.add(new Bytecode(context, new byte[] {
                        (byte) (indexNode == null ? 0x4D : 0x4E),
                        (byte) (push ? 0x80 : 0x81)
                    }, "FIELD_" + (push ? "READ" : "WRITE")));
                }
                else {
                    source.add(new Bytecode(context, new byte[] {
                        (byte) (indexNode == null ? 0x4D : 0x4E)
                    }, "FIELD_SETUP"));
                }

                if (postEffectNode != null) {
                    compilePostEffect(context, postEffectNode, source, push);
                }
            }
            else {
                String[] s = node.getText().split("[\\.]");
                if (s.length == 2 && ("BYTE".equalsIgnoreCase(s[1]) || "WORD".equalsIgnoreCase(s[1]) || "LONG".equalsIgnoreCase(s[1]))) {
                    int index = 0;
                    boolean popIndex = false;
                    Spin2StatementNode indexNode = null;
                    Spin2StatementNode bitfieldNode = null;
                    Spin2StatementNode postEffectNode = null;

                    Expression expression = context.getLocalSymbol(s[0]);
                    if (expression instanceof ObjectContextLiteral) {
                        expression = context.getLocalSymbol(s[0].substring(1));
                    }
                    if (expression == null) {
                        throw new CompilerException("undefined symbol " + node.getText(), node.getToken());
                    }

                    int n = 0;
                    if (n < node.getChildCount() && (node.getChild(n) instanceof Spin2StatementNode.Index)) {
                        indexNode = node.getChild(n++);
                    }
                    if (n < node.getChildCount() && ".".equals(node.getChild(n).getText())) {
                        if (node.getText().startsWith("@")) {
                            throw new CompilerException("bitfield expression not allowed", node.getChild(n).getToken());
                        }
                        n++;
                        if (n >= node.getChildCount()) {
                            throw new RuntimeException("expected bitfield expression");
                        }
                        if (!(node.getChild(n) instanceof Spin2StatementNode.Index)) {
                            throw new RuntimeException("syntax error");
                        }
                        bitfieldNode = node.getChild(n++);
                    }
                    if (n < node.getChildCount() && isPostEffect(node.getChild(n))) {
                        postEffectNode = node.getChild(n++);
                    }
                    if (n < node.getChildCount()) {
                        throw new CompilerException("syntax error", node.getChild(n).getToken());
                    }

                    MemoryOp.Size ss = MemoryOp.Size.Long;
                    if ("BYTE".equalsIgnoreCase(s[1])) {
                        ss = MemoryOp.Size.Byte;
                    }
                    else if ("WORD".equalsIgnoreCase(s[1])) {
                        ss = MemoryOp.Size.Word;
                    }
                    MemoryOp.Base bb = MemoryOp.Base.PBase;
                    if (expression instanceof LocalVariable) {
                        bb = MemoryOp.Base.DBase;
                    }
                    else if (expression instanceof Variable) {
                        bb = MemoryOp.Base.VBase;
                    }

                    int bitfield = -1;
                    if (bitfieldNode != null) {
                        bitfield = compileBitfield(context, bitfieldNode, source);
                    }

                    if (indexNode != null) {
                        popIndex = true;
                        try {
                            Expression exp = buildConstantExpression(context, indexNode);
                            if (exp.isConstant()) {
                                index = exp.getNumber().intValue();
                                popIndex = false;
                            }
                        } catch (Exception e) {
                            // Do nothing
                        }
                        if (popIndex) {
                            source.addAll(compileBytecodeExpression(context, indexNode, true));
                        }
                    }

                    if (s[0].startsWith("@")) {
                        source.add(new MemoryOp(context, ss, bb, MemoryOp.Op.Address, popIndex, expression, index));
                    }
                    else {
                        MemoryOp.Op op = bitfieldNode == null && postEffectNode == null ? (push ? MemoryOp.Op.Read : MemoryOp.Op.Write) : MemoryOp.Op.Setup;
                        source.add(new MemoryOp(context, ss, bb, op, popIndex, expression, index));
                    }

                    if (bitfieldNode != null) {
                        source.add(new BitField(context, postEffectNode == null ? BitField.Op.Read : BitField.Op.Setup, bitfield));
                    }

                    if (postEffectNode != null) {
                        if ("++".equalsIgnoreCase(postEffectNode.getText())) {
                            source.add(new Bytecode(context, push ? 0x87 : 0x83, "POST_INC" + (push ? " (push)" : "")));
                        }
                        else if ("--".equalsIgnoreCase(postEffectNode.getText())) {
                            source.add(new Bytecode(context, push ? 0x88 : 0x84, "POST_DEC" + (push ? " (push)" : "")));
                        }
                        else if ("!!".equalsIgnoreCase(postEffectNode.getText())) {
                            source.add(new Bytecode(context, push ? 0x8A : 0x89, "POST_LOGICAL_NOT" + (push ? " (push)" : "")));
                        }
                        else if ("!".equalsIgnoreCase(postEffectNode.getText())) {
                            source.add(new Bytecode(context, push ? 0x8C : 0x8B, "POST_NOT" + (push ? " (push)" : "")));
                        }
                        else {
                            throw new CompilerException("unhandled post effect " + postEffectNode.getText(), postEffectNode.getToken());
                        }
                    }
                }
                else {
                    Expression expression = context.getLocalSymbol(node.getText());
                    if (expression == null && isAddress(node.getText())) {
                        expression = context.getLocalSymbol(node.getText().substring(1));
                    }
                    if (expression == null && node.getText().startsWith("^@")) {
                        expression = context.getLocalSymbol(node.getText().substring(2));
                    }
                    if (expression instanceof ObjectContextLiteral) {
                        expression = context.getLocalSymbol(node.getText().substring(1));
                    }
                    if (expression == null) {
                        throw new CompilerException("undefined symbol " + node.getText(), node.getToken());
                    }
                    if (expression instanceof SpinObject) {
                        if (node.getChildCount() != 2) {
                            throw new CompilerException("syntax error", node.getToken());
                        }
                        if (!(node.getChild(0) instanceof Spin2StatementNode.Index)) {
                            throw new CompilerException("syntax error", node.getChild(0).getToken());
                        }

                        String qualifiedName = node.getText() + node.getChild(1).getText();

                        expression = context.getLocalSymbol(qualifiedName);
                        if (expression == null && qualifiedName.startsWith("@")) {
                            expression = context.getLocalSymbol(qualifiedName.substring(1));
                        }
                        if (expression != null) {
                            Method method = (Method) expression;
                            if (isAddress(node.getText())) {
                                source.addAll(compileConstantExpression(context, node.getChild(0)));
                                source.add(new Bytecode(context, new byte[] {
                                    (byte) 0x10,
                                    (byte) (method.getObject() - 1),
                                    (byte) ((Method) expression).getOffset()
                                }, "OBJ_SUB_ADDRESS (" + (method.getObject() - 1) + "." + method.getOffset() + ")"));
                            }
                            else {
                                Spin2StatementNode childNode = node.getChild(1);

                                int expected = method.getArgumentsCount();
                                int actual = getArgumentsCount(context, childNode);
                                if (expected != actual) {
                                    throw new CompilerException("expected " + expected + " argument(s), found " + actual, node.getToken());
                                }
                                if (push && method.getReturnsCount() == 0) {
                                    throw new RuntimeException("method doesn't return any value");
                                }

                                source.add(new Bytecode(context, push ? 0x01 : 0x00, "ANCHOR"));
                                for (int i = 0; i < childNode.getChildCount(); i++) {
                                    source.addAll(compileConstantExpression(context, childNode.getChild(i)));
                                }
                                source.addAll(compileConstantExpression(context, node.getChild(0)));
                                source.add(new Bytecode(context, new byte[] {
                                    (byte) 0x09,
                                    (byte) (method.getObject() - 1),
                                    (byte) method.getOffset()
                                }, "CALL_OBJ_SUB (" + (method.getObject() - 1) + "." + method.getOffset() + ")"));
                            }
                            return source;
                        }
                    }
                    else if (expression instanceof Method) {
                        if (isAddress(node.getText())) {
                            Method method = (Method) expression;
                            if (method.getObject() != 0) {
                                source.add(new Bytecode(context, new byte[] {
                                    (byte) 0x0F,
                                    (byte) (method.getObject() - 1),
                                    (byte) ((Method) expression).getOffset()
                                }, "OBJ_SUB_ADDRESS (" + (method.getObject() - 1) + "." + method.getOffset() + ")"));
                            }
                            else {
                                source.add(new Bytecode(context, new byte[] {
                                    (byte) 0x11,
                                    (byte) method.getOffset()
                                }, "SUB_ADDRESS (" + method.getOffset() + ")"));
                            }
                        }
                        else {
                            source.addAll(compileMethodCall(context, expression, node, push, false));
                        }
                    }
                    else if (expression instanceof Variable) {
                        if (isAddress(node.getText())) {
                            int index = 0;
                            boolean popIndex = false;
                            boolean hasIndex = false;
                            Spin2StatementNode indexNode = null;

                            int n = 0;
                            if (n < node.getChildCount() && (node.getChild(n) instanceof Spin2StatementNode.Index)) {
                                indexNode = node.getChild(n++);
                            }
                            if (n < node.getChildCount()) {
                                throw new CompilerException("unexpected " + node.getChild(n).getText(), node.getChild(n));
                            }

                            if (indexNode != null) {
                                popIndex = true;
                                try {
                                    Expression exp = buildConstantExpression(context, indexNode);
                                    if (exp.isConstant()) {
                                        index = exp.getNumber().intValue();
                                        hasIndex = true;
                                        popIndex = false;
                                    }
                                } catch (Exception e) {
                                    // Do nothing
                                }
                                if (popIndex) {
                                    source.addAll(compileBytecodeExpression(context, indexNode, true));
                                }
                            }

                            VariableOp.Op op = VariableOp.Op.Address;
                            if (node.getText().startsWith("@@")) {
                                op = VariableOp.Op.PBaseAddress;
                            }

                            source.add(new VariableOp(context, op, popIndex, (Variable) expression, hasIndex, index));
                        }
                        else {
                            if (node instanceof Spin2StatementNode.Method) {
                                source.addAll(compileMethodCall(context, expression, node, push, false));
                            }
                            else {
                                source.addAll(compileVariableRead(context, expression, node, push));
                            }
                        }
                    }
                    else {
                        if (isAddress(node.getText())) {
                            int index = 0;
                            boolean popIndex = false;
                            Spin2StatementNode indexNode = null;

                            int n = 0;
                            if (n < node.getChildCount() && (node.getChild(n) instanceof Spin2StatementNode.Index)) {
                                indexNode = node.getChild(n++);
                            }
                            if (n < node.getChildCount()) {
                                throw new CompilerException("unexpected " + node.getChild(n).getText(), node.getChild(n));
                            }

                            if (indexNode != null) {
                                popIndex = true;
                                try {
                                    Expression exp = buildConstantExpression(context, indexNode);
                                    if (exp.isConstant()) {
                                        index = exp.getNumber().intValue();
                                        popIndex = false;
                                    }
                                } catch (Exception e) {
                                    // Do nothing
                                }
                                if (popIndex) {
                                    source.addAll(compileBytecodeExpression(context, indexNode, true));
                                }
                            }

                            MemoryOp.Size ss = MemoryOp.Size.Long;
                            MemoryOp.Base bb = MemoryOp.Base.PBase;

                            if ((expression instanceof MemoryContextLiteral) && node.getText().startsWith("@@")) {
                                expression = context.getLocalSymbol(node.getText().substring(1));
                            }
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
                            else if (expression instanceof ObjectContextLiteral) {
                                switch (((ObjectContextLiteral) expression).getType()) {
                                    case "BYTE":
                                        ss = MemoryOp.Size.Byte;
                                        break;
                                    case "WORD":
                                        ss = MemoryOp.Size.Word;
                                        break;
                                }
                            }
                            if (node.getText().startsWith("@@")) {
                                source.add(new MemoryOp(context, ss, bb, MemoryOp.Op.Read, popIndex, expression, index));
                                source.add(new Bytecode(context, 0x24, "ADD_PBASE"));
                            }
                            else {
                                source.add(new MemoryOp(context, ss, bb, MemoryOp.Op.Address, popIndex, expression, index));
                            }
                        }
                        else if (expression instanceof Register) {
                            source.addAll(compileVariableRead(context, expression, node, push));
                        }
                        else if (expression instanceof DataVariable) {
                            if (node instanceof Spin2StatementNode.Method) {
                                source.addAll(compileMethodCall(context, expression, node, push, false));
                            }
                            else {
                                source.addAll(compileVariableRead(context, expression, node, push));
                            }
                        }
                        else if (expression instanceof ContextLiteral) {
                            source.addAll(compileVariableRead(context, expression, node, push));
                        }
                        else if (expression.isConstant()) {
                            if (node.getChildCount() != 0) {
                                throw new RuntimeException("syntax error");
                            }
                            source.add(new Constant(context, expression));
                        }
                        else {
                            if (node.getChildCount() != 0) {
                                throw new RuntimeException("syntax error");
                            }
                            source.add(new MemoryOp(context, MemoryOp.Size.Long, MemoryOp.Base.PBase, MemoryOp.Op.Read, expression));
                        }
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

    protected int getArgumentsCount(Spin2Context context, Spin2StatementNode childNode) {
        int actual = 0;
        for (int i = 0; i < childNode.getChildCount(); i++) {
            Expression child = context.getLocalSymbol(childNode.getChild(i).getText());
            if (child != null && (child instanceof Method) && !childNode.getChild(i).getText().startsWith("@")) {
                actual += ((Method) child).getReturnsCount();
                continue;
            }
            Spin2Bytecode.Descriptor descriptor = Spin2Bytecode.getDescriptor(childNode.getChild(i).getText().toUpperCase());
            if (descriptor != null) {
                actual += descriptor.getReturns();
                continue;
            }
            actual++;
        }
        return actual;
    }

    List<Spin2Bytecode> compileConstantExpression(Spin2Context context, Spin2StatementNode node) {
        try {
            Expression expression = buildConstantExpression(context, node);
            if (expression.isConstant()) {
                return Collections.singletonList(new Constant(context, expression));
            }
        } catch (Exception e) {
            // Do nothing
        }
        return compileBytecodeExpression(context, node, true);
    }

    Expression buildConstantExpression(Spin2Context context, Spin2StatementNode node) {
        return buildConstantExpression(context, node, false);
    }

    Expression buildConstantExpression(Spin2Context context, Spin2StatementNode node, boolean registerConstant) {
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
            if (node.getChildCount() == 0) {
                if (expression.isConstant()) {
                    return expression;
                }
                if (registerConstant && (expression instanceof Register) || (expression instanceof DataVariable)) {
                    return expression;
                }
            }
            throw new RuntimeException("not a constant (" + expression + ")");
        }

        switch (node.getText().toUpperCase()) {
            case ">>":
                return new ShiftRight(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "<<":
                return new ShiftLeft(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));

            case "&":
                return new And(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "^":
                return new Xor(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "|":
                return new Or(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));

            case "*":
            case "*.":
                return new Multiply(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "/":
            case "/.":
                return new Divide(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "//":
                return new Modulo(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "+/":
                return new UnsignedDivide(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "+//":
                return new UnsignedModulo(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "SCA":
                return new Sca(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "SCAS":
                return new Scas(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));

            case "+":
            case "+.":
                if (node.getChildCount() == 1) {
                    return buildConstantExpression(context, node.getChild(0), registerConstant);
                }
                return new Add(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "-":
            case "-.":
                if (node.getChildCount() == 1) {
                    return new Negative(buildConstantExpression(context, node.getChild(0), registerConstant));
                }
                return new Subtract(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));

            case "ADDBITS":
                return new Addbits(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "ADDPINS":
                return new Addpins(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));

            case "&&":
            case "AND":
                return new LogicalAnd(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "||":
            case "OR":
                return new LogicalOr(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "^^":
            case "XOR":
                return new LogicalXor(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));

            case "<":
                return new LessThan(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "<=":
                return new LessOrEquals(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "==":
                return new Equals(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "<>":
                return new NotEquals(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case ">=":
                return new GreaterOrEquals(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case ">":
                return new GreaterThan(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));

            case "!":
                if (node.getChildCount() == 1) {
                    return new Not(buildConstantExpression(context, node.getChild(0), registerConstant));
                }
                throw new RuntimeException("unary operator with " + node.getChildCount() + " arguments");

            case "?": {
                Expression right = buildConstantExpression(context, node.getChild(1), registerConstant);
                if (!(right instanceof IfElse)) {
                    throw new RuntimeException("unsupported operator " + node.getText());
                }
                return new IfElse(buildConstantExpression(context, node.getChild(0), registerConstant), ((IfElse) right).getTrueTerm(), ((IfElse) right).getFalseTerm());
            }
            case ":":
                return new IfElse(null, buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));

            case "TRUNC":
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("misplaced unary operator (" + node.getText() + ")");
                }
                return new Trunc(buildConstantExpression(context, node.getChild(0), registerConstant));
            case "FSQRT":
            case "SQRT":
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("misplaced unary operator (" + node.getText() + ")");
                }
                return new Sqrt(buildConstantExpression(context, node.getChild(0), registerConstant));
            case "ROUND":
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("misplaced unary operator (" + node.getText() + ")");
                }
                return new Round(buildConstantExpression(context, node.getChild(0), registerConstant));
            case "FLOAT":
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("misplaced unary operator (" + node.getText() + ")");
                }
                return new NumberLiteral(buildConstantExpression(context, node.getChild(0), registerConstant).getNumber().doubleValue());
            case "ABS":
            case "FABS":
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("misplaced unary operator (" + node.getText() + ")");
                }
                return new Abs(buildConstantExpression(context, node.getChild(0), registerConstant));
            case "NAN":
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("misplaced unary operator (" + node.getText() + ")");
                }
                return new Nan(buildConstantExpression(context, node.getChild(0), registerConstant));
        }

        throw new RuntimeException("unknown " + node.getText());
    }

    List<Spin2Bytecode> leftAssign(Spin2Context context, Spin2StatementNode node, boolean push, boolean write) {
        Spin2StatementNode indexNode = null;
        Spin2StatementNode bitfieldNode = null;
        Spin2StatementNode postEffectNode = null;
        int index = 0;
        boolean popIndex = false;
        List<Spin2Bytecode> source = new ArrayList<Spin2Bytecode>();

        if (node.getText().startsWith("^@")) {
            throw new CompilerException("syntax error", node.getToken());
        }

        String[] s = node.getText().split("[\\.]");
        if (s.length == 2 && ("BYTE".equalsIgnoreCase(s[1]) || "WORD".equalsIgnoreCase(s[1]) || "LONG".equalsIgnoreCase(s[1]))) {
            Expression expression = context.getLocalSymbol(s[0]);
            if (expression == null) {
                throw new CompilerException("undefined symbol " + node.getText(), node.getToken());
            }

            int n = 0;
            if (n < node.getChildCount() && (node.getChild(n) instanceof Spin2StatementNode.Index)) {
                indexNode = node.getChild(n++);
            }
            if (n < node.getChildCount() && ".".equals(node.getChild(n).getText())) {
                n++;
                if (n >= node.getChildCount()) {
                    throw new RuntimeException("expected bitfield expression");
                }
                if (!(node.getChild(n) instanceof Spin2StatementNode.Index)) {
                    throw new RuntimeException("syntax error");
                }
                bitfieldNode = node.getChild(n++);
            }
            if (n < node.getChildCount() && isPostEffect(node.getChild(n))) {
                postEffectNode = node.getChild(n++);
            }
            if (n < node.getChildCount()) {
                throw new CompilerException("unexpected " + node.getChild(n).getText(), node.getChild(n).getToken());
            }

            int bitfield = -1;
            if (bitfieldNode != null) {
                bitfield = compileBitfield(context, bitfieldNode, source);
            }

            if (indexNode != null) {
                popIndex = true;
                try {
                    Expression exp = buildConstantExpression(context, indexNode);
                    if (exp.isConstant()) {
                        index = exp.getNumber().intValue();
                        popIndex = false;
                    }
                } catch (Exception e) {
                    // Do nothing
                }
                if (popIndex) {
                    source.addAll(compileBytecodeExpression(context, indexNode, true));
                }
            }

            MemoryOp.Size ss = MemoryOp.Size.Long;
            if ("BYTE".equalsIgnoreCase(s[1])) {
                ss = MemoryOp.Size.Byte;
            }
            else if ("WORD".equalsIgnoreCase(s[1])) {
                ss = MemoryOp.Size.Word;
            }
            MemoryOp.Base bb = MemoryOp.Base.PBase;
            if (expression instanceof LocalVariable) {
                bb = MemoryOp.Base.DBase;
            }
            else if (expression instanceof Variable) {
                bb = MemoryOp.Base.VBase;
            }

            MemoryOp.Op op = push || bitfieldNode != null ? MemoryOp.Op.Setup : MemoryOp.Op.Write;
            source.add(new MemoryOp(context, ss, bb, op, popIndex, expression, index));

            if (bitfieldNode != null) {
                source.add(new BitField(context, postEffectNode == null ? BitField.Op.Write : BitField.Op.Setup, bitfield));
            }

            if (postEffectNode != null) {
                if ("++".equalsIgnoreCase(postEffectNode.getText())) {
                    source.add(new Bytecode(context, push ? 0x87 : 0x83, "POST_INC" + (push ? " (push)" : "")));
                }
                else if ("--".equalsIgnoreCase(postEffectNode.getText())) {
                    source.add(new Bytecode(context, push ? 0x88 : 0x84, "POST_DEC" + (push ? " (push)" : "")));
                }
                else if ("!!".equalsIgnoreCase(postEffectNode.getText())) {
                    source.add(new Bytecode(context, push ? 0x8A : 0x89, "POST_LOGICAL_NOT" + (push ? " (push)" : "")));
                }
                else if ("!".equalsIgnoreCase(postEffectNode.getText())) {
                    source.add(new Bytecode(context, push ? 0x8C : 0x8B, "POST_NOT" + (push ? " (push)" : "")));
                }
                else {
                    throw new CompilerException("unhandled post effect " + postEffectNode.getText(), postEffectNode.getToken());
                }
            }
        }
        else if ("_".equalsIgnoreCase(node.getText())) {
            source.add(new Bytecode(context, 0x17, "POP"));
        }
        else if (",".equals(node.getText())) {
            for (int i = node.getChildCount() - 1; i >= 0; i--) {
                source.addAll(leftAssign(context, node.getChild(i), push, false));
            }
        }
        else if (node.getType() == Token.OPERATOR) {
            source.addAll(leftAssign(context, node.getChild(1), true, true));
            source.addAll(leftAssign(context, node.getChild(0), node.getChild(0).getType() == Token.OPERATOR, false));
        }
        else if ("BYTE".equalsIgnoreCase(node.getText()) || "WORD".equalsIgnoreCase(node.getText()) || "LONG".equalsIgnoreCase(node.getText())) {
            indexNode = null;
            bitfieldNode = null;

            int n = 1;
            if (n < node.getChildCount()) {
                if (".".equals(node.getChild(n).getText())) {
                    n++;
                    if (n >= node.getChildCount()) {
                        throw new CompilerException("expected bitfield expression", node.getToken());
                    }
                    bitfieldNode = node.getChild(n++);
                }
                else if (!isPostEffect(node.getChild(n))) {
                    indexNode = node.getChild(n++);
                }
            }
            if (n < node.getChildCount()) {
                if (".".equals(node.getChild(n).getText())) {
                    if (bitfieldNode != null) {
                        throw new CompilerException("syntax error", node.getToken());
                    }
                    n++;
                    if (n >= node.getChildCount()) {
                        throw new CompilerException("expected bitfield expression", node.getToken());
                    }
                    bitfieldNode = node.getChild(n++);
                }
            }
            if (n < node.getChildCount()) {
                if (!isPostEffect(node.getChild(n))) {
                    if (indexNode != null) {
                        throw new CompilerException("syntax error", node.getToken());
                    }
                    indexNode = node.getChild(n++);
                }
            }
            if (n < node.getChildCount()) {
                throw new RuntimeException("syntax error " + node.getText());
            }

            int bitfield = -1;
            if (bitfieldNode != null) {
                bitfield = compileBitfield(context, bitfieldNode, source);
            }

            source.addAll(compileBytecodeExpression(context, node.getChild(0), true));
            if (indexNode != null) {
                source.addAll(compileBytecodeExpression(context, indexNode, true));
            }

            MemoryOp.Size ss = MemoryOp.Size.Long;
            if ("BYTE".equalsIgnoreCase(node.getText())) {
                ss = MemoryOp.Size.Byte;
            }
            else if ("WORD".equalsIgnoreCase(node.getText())) {
                ss = MemoryOp.Size.Word;
            }
            MemoryOp.Op op = push || bitfieldNode != null ? MemoryOp.Op.Setup : MemoryOp.Op.Write;
            source.add(new MemoryOp(context, ss, MemoryOp.Base.Pop, op, indexNode != null));

            if (bitfieldNode != null) {
                source.add(new BitField(context, push && !write ? BitField.Op.Setup : BitField.Op.Write, push, bitfield));
            }
        }
        else if ("REG".equalsIgnoreCase(node.getText())) {
            indexNode = null;
            bitfieldNode = null;

            int n = 1;
            if (n < node.getChildCount()) {
                if (".".equals(node.getChild(n).getText())) {
                    n++;
                    if (n >= node.getChildCount()) {
                        throw new CompilerException("expected bitfield expression", node.getToken());
                    }
                    bitfieldNode = node.getChild(n++);
                }
                else if (!isPostEffect(node.getChild(n))) {
                    indexNode = node.getChild(n++);
                }
            }
            if (n < node.getChildCount()) {
                if (".".equals(node.getChild(n).getText())) {
                    if (bitfieldNode != null) {
                        throw new CompilerException("syntax error", node.getToken());
                    }
                    n++;
                    if (n >= node.getChildCount()) {
                        throw new CompilerException("expected bitfield expression", node.getToken());
                    }
                    bitfieldNode = node.getChild(n++);
                }
            }
            if (n < node.getChildCount()) {
                if (!isPostEffect(node.getChild(n))) {
                    if (indexNode != null) {
                        throw new CompilerException("syntax error", node.getToken());
                    }
                    indexNode = node.getChild(n++);
                }
            }
            if (n < node.getChildCount()) {
                throw new RuntimeException("syntax error " + node.getText());
            }

            int bitfield = -1;
            if (bitfieldNode != null) {
                bitfield = compileBitfield(context, bitfieldNode, source);
            }

            Expression expression = null;
            try {
                expression = buildConstantExpression(context, node.getChild(0), true);
            } catch (Exception e) {
                // Do nothing
            }
            if (expression == null) {
                throw new CompilerException("expected constant expression", node.getChild(0).getToken());
            }

            index = 0;
            popIndex = indexNode != null;
            if (indexNode != null) {
                try {
                    Expression exp = buildConstantExpression(context, indexNode);
                    if (exp.isConstant()) {
                        index = exp.getNumber().intValue();
                        popIndex = false;
                    }
                } catch (Exception e) {
                    // Do nothing
                }
                if (popIndex) {
                    source.addAll(compileBytecodeExpression(context, indexNode, true));
                }
            }

            source.add(new RegisterOp(context, bitfieldNode != null || push ? RegisterOp.Op.Setup : RegisterOp.Op.Write, popIndex, expression, index));

            if (bitfieldNode != null) {
                source.add(new BitField(context, push && !write ? BitField.Op.Setup : BitField.Op.Write, push, bitfield));
            }
        }
        else if ("FIELD".equalsIgnoreCase(node.getText()) && node.getChildCount() != 0) {
            indexNode = null;

            int n = 1;
            if (n < node.getChildCount()) {
                indexNode = node.getChild(n++);
            }
            if (n < node.getChildCount()) {
                throw new RuntimeException("syntax error " + node.getText());
            }

            source.addAll(compileConstantExpression(context, node.getChild(0)));

            if (indexNode != null) {
                source.addAll(compileConstantExpression(context, indexNode));
            }

            source.add(new Bytecode(context, new byte[] {
                (byte) (indexNode == null ? 0x4D : 0x4E),
                (byte) 0x81
            }, "FIELD_WRITE"));
        }
        else {
            Expression expression = context.getLocalSymbol(node.getText());
            if (expression == null) {
                throw new CompilerException("undefined symbol " + node.getText(), node.getToken());
            }

            boolean hasIndex = false;

            int n = 0;
            if (n < node.getChildCount()) {
                if (".".equals(node.getChild(n).getText())) {
                    n++;
                    if (n >= node.getChildCount()) {
                        throw new CompilerException("expected bitfield expression", node.getToken());
                    }
                    bitfieldNode = node.getChild(n++);
                }
                else {
                    indexNode = node.getChild(n++);
                    popIndex = true;
                    try {
                        Expression exp = buildConstantExpression(context, indexNode);
                        if (exp.isConstant()) {
                            index = exp.getNumber().intValue();
                            hasIndex = true;
                            popIndex = false;
                        }
                    } catch (Exception e) {
                        // Do nothing
                    }
                    if (n < node.getChildCount()) {
                        if (".".equals(node.getChild(n).getText())) {
                            n++;
                            if (n >= node.getChildCount()) {
                                throw new CompilerException("expected bitfield expression", node.getToken());
                            }
                            bitfieldNode = node.getChild(n++);
                        }
                    }
                }
            }
            if (n < node.getChildCount()) {
                throw new CompilerException("unexpected " + node.getChild(n).getText(), node.getChild(n).getToken());
            }

            int bitfield = -1;
            if (bitfieldNode != null) {
                bitfield = compileBitfield(context, bitfieldNode, source);
            }

            if (popIndex) {
                source.addAll(compileBytecodeExpression(context, indexNode, true));
            }

            if (expression instanceof Register) {
                source.add(new RegisterOp(context, bitfieldNode != null || push ? RegisterOp.Op.Setup : RegisterOp.Op.Write, popIndex, expression, index));
            }
            else if (expression instanceof ContextLiteral) {
                MemoryOp.Size ss = MemoryOp.Size.Long;
                MemoryOp.Base bb = MemoryOp.Base.PBase;
                if (expression instanceof LocalVariable) {
                    bb = MemoryOp.Base.DBase;
                }
                else if (expression instanceof Variable) {
                    bb = MemoryOp.Base.VBase;
                }
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

                source.add(new MemoryOp(context, ss, bb, bitfieldNode != null || push ? MemoryOp.Op.Setup : MemoryOp.Op.Write, popIndex, expression, index));
            }
            else {
                source.add(new VariableOp(context, bitfieldNode != null || push ? VariableOp.Op.Setup : VariableOp.Op.Write, popIndex, (Variable) expression, hasIndex, index));
            }

            if (bitfieldNode != null) {
                source.add(new BitField(context, push && !write ? BitField.Op.Setup : BitField.Op.Write, push, bitfield));
            }
            else if (write) {
                source.add(new Bytecode(context, 0x82, "WRITE"));
            }
        }

        return source;
    }

    List<Spin2Bytecode> compileMethodCall(Spin2Context context, Expression symbol, Spin2StatementNode node, boolean push, boolean trap) {
        List<Spin2Bytecode> source = new ArrayList<Spin2Bytecode>();

        if (trap) {
            source.add(new Bytecode(context, push ? 0x03 : 0x02, "ANCHOR_TRAP"));
        }
        else {
            source.add(new Bytecode(context, push ? 0x01 : 0x00, "ANCHOR"));
        }

        if (symbol instanceof Method) {
            Method method = (Method) symbol;

            int expected = method.getArgumentsCount();
            int actual = getArgumentsCount(context, node);
            if (expected != actual) {
                throw new RuntimeException("expected " + expected + " argument(s), found " + actual);
            }
            if (push && !trap && method.getReturnsCount() == 0) {
                throw new RuntimeException("method doesn't return any value");
            }
            for (int i = 0; i < node.getChildCount(); i++) {
                source.addAll(compileConstantExpression(context, node.getChild(i)));
            }
            if (method.getObject() != 0) {
                source.add(new Bytecode(context, new byte[] {
                    (byte) 0x08,
                    (byte) (method.getObject() - 1),
                    (byte) method.getOffset()
                }, "CALL_OBJ_SUB (" + (method.getObject() - 1) + "." + method.getOffset() + ")"));
            }
            else {
                source.add(new Bytecode(context, new byte[] {
                    (byte) 0x0A,
                    (byte) method.getOffset()
                }, "CALL_SUB (" + method.getOffset() + ")"));
            }
        }
        else {
            int i = 0;
            Spin2StatementNode indexNode = null;
            if (i < node.getChildCount()) {
                if (node.getChild(i) instanceof Spin2StatementNode.Index) {
                    indexNode = node.getChild(i++);
                }
            }
            while (i < node.getChildCount()) {
                if (!(node.getChild(i) instanceof Spin2StatementNode.Argument)) {
                    throw new CompilerException("syntax error", node.getChild(i));
                }
                source.addAll(compileConstantExpression(context, node.getChild(i++)));
            }

            int index = 0;
            boolean hasIndex = false;
            boolean popIndex = indexNode != null;

            if (indexNode != null) {
                try {
                    Expression exp = buildConstantExpression(context, indexNode);
                    if (exp.isConstant()) {
                        index = exp.getNumber().intValue();
                        hasIndex = true;
                        popIndex = false;
                    }
                } catch (Exception e) {
                    // Do nothing
                }
                if (popIndex) {
                    source.addAll(compileBytecodeExpression(context, indexNode, true));
                }
            }

            if (symbol instanceof Variable) {
                switch (((Variable) symbol).getType()) {
                    case "BYTE":
                    case "WORD":
                        throw new RuntimeException("method pointers must be long");
                }
                source.add(new VariableOp(context, VariableOp.Op.Read, popIndex, (Variable) symbol, hasIndex, index));
            }
            else if (symbol instanceof DataVariable) {
                switch (((DataVariable) symbol).getType()) {
                    case "BYTE":
                    case "WORD":
                        throw new RuntimeException("method pointers must be long");
                }
                source.add(new MemoryOp(context, MemoryOp.Size.Long, MemoryOp.Base.PBase, MemoryOp.Op.Read, popIndex, symbol, index));
            }
            else {
                throw new CompilerException("unsupported operation on " + node.getText(), node.getToken());
            }

            source.add(new Bytecode(context, new byte[] {
                (byte) 0x0B,
            }, "CALL_PTR"));
        }

        return source;
    }

    int compileBitfield(Spin2Context context, Spin2StatementNode node, List<Spin2Bytecode> source) {
        int bitfield = -1;

        if ("..".equals(node.getText())) {
            if (node.getChildCount() != 2) {
                throw new RuntimeException("syntax error");
            }
            try {
                Expression exp1 = buildConstantExpression(context, node.getChild(0));
                Expression exp2 = buildConstantExpression(context, node.getChild(1));
                if (exp1.isConstant() && exp2.isConstant()) {
                    int lowest = Math.min(exp1.getNumber().intValue(), exp2.getNumber().intValue());
                    int highest = Math.max(exp1.getNumber().intValue(), exp2.getNumber().intValue());
                    Expression exp = new Addbits(new NumberLiteral(lowest), new NumberLiteral(highest - lowest));
                    bitfield = exp.getNumber().intValue();
                }
            } catch (Exception e) {
                // Do nothing
            }

            if (bitfield == -1) {
                source.addAll(compileBytecodeExpression(context, node.getChild(0), true));
                source.addAll(compileBytecodeExpression(context, node.getChild(1), true));
                source.add(new Bytecode(context, new byte[] {
                    (byte) 0x9F, (byte) 0x94
                }, "ADDBITS"));
            }
        }
        else {
            try {
                Expression exp = buildConstantExpression(context, node);
                if (exp.isConstant()) {
                    bitfield = exp.getNumber().intValue();
                }
            } catch (Exception e) {
                // Do nothing
            }

            if (bitfield == -1) {
                source.addAll(compileBytecodeExpression(context, node, true));
            }
        }

        return bitfield;
    }

    List<Spin2Bytecode> compileVariableSetup(Spin2Context context, Expression expression, Spin2StatementNode node) {
        Spin2StatementNode indexNode = null;
        Spin2StatementNode bitfieldNode = null;
        List<Spin2Bytecode> source = new ArrayList<Spin2Bytecode>();

        MemoryOp.Size ss = MemoryOp.Size.Long;
        MemoryOp.Base bb = MemoryOp.Base.PBase;
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

        int n = 0;
        if (n < node.getChildCount()) {
            if (!".".equals(node.getChild(n).getText()) && !isPostEffect(node.getChild(n))) {
                indexNode = node.getChild(n++);
                if ("..".equals(indexNode.getText())) {
                    bitfieldNode = indexNode;
                    indexNode = null;
                }
            }
        }
        if (n < node.getChildCount()) {
            if (".".equals(node.getChild(n).getText())) {
                if (bitfieldNode != null) {
                    throw new CompilerException("invalid bitfield expression", node.getToken());
                }
                n++;
                if (n >= node.getChildCount()) {
                    throw new CompilerException("expected bitfield expression", node.getToken());
                }
                bitfieldNode = node.getChild(n++);
            }
        }
        if (n < node.getChildCount()) {
            throw new CompilerException("unexpected " + node.getChild(n).getText(), node.getChild(n).getToken());
        }

        int index = 0;
        boolean hasIndex = false;
        boolean popIndex = false;

        if (indexNode != null) {
            popIndex = true;
            try {
                Expression exp = buildConstantExpression(context, indexNode);
                if (exp.isConstant()) {
                    index = exp.getNumber().intValue();
                    hasIndex = true;
                    popIndex = false;
                }
            } catch (Exception e) {
                // Do nothing
            }
        }

        if (bitfieldNode != null) {
            int bitfield = compileBitfield(context, bitfieldNode, source);

            if (popIndex) {
                source.addAll(compileBytecodeExpression(context, indexNode, true));
            }

            if (expression instanceof Register) {
                source.add(new RegisterOp(context, RegisterOp.Op.Setup, popIndex, expression, index));
            }
            else if (expression instanceof ContextLiteral) {
                source.add(new MemoryOp(context, ss, bb, MemoryOp.Op.Setup, popIndex, expression, index));
            }
            else {
                source.add(new VariableOp(context, VariableOp.Op.Setup, popIndex, (Variable) expression, hasIndex, index));
            }

            source.add(new BitField(context, BitField.Op.Setup, bitfield));
        }
        else {
            if (popIndex) {
                source.addAll(compileBytecodeExpression(context, indexNode, true));
            }
            if (expression instanceof Register) {
                source.add(new RegisterOp(context, RegisterOp.Op.Setup, popIndex, expression, index));
            }
            else if (expression instanceof ContextLiteral) {
                source.add(new MemoryOp(context, ss, bb, MemoryOp.Op.Setup, popIndex, expression, index));
            }
            else {
                source.add(new VariableOp(context, VariableOp.Op.Setup, popIndex, (Variable) expression, hasIndex, index));
            }
        }

        return source;
    }

    List<Spin2Bytecode> compileVariableRead(Spin2Context context, Expression expression, Spin2StatementNode node, boolean push) {
        Spin2StatementNode indexNode = null;
        Spin2StatementNode bitfieldNode = null;
        Spin2StatementNode postEffectNode = null;
        List<Spin2Bytecode> source = new ArrayList<Spin2Bytecode>();

        MemoryOp.Size ss = MemoryOp.Size.Long;
        MemoryOp.Base bb = MemoryOp.Base.PBase;
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

        boolean field = node.getText().startsWith("^@");

        int n = 0;
        if (n < node.getChildCount() && (node.getChild(n) instanceof Spin2StatementNode.Index)) {
            indexNode = node.getChild(n++);
        }
        if (n < node.getChildCount() && ".".equals(node.getChild(n).getText())) {
            n++;
            if (n >= node.getChildCount()) {
                throw new CompilerException("expected bitfield expression", node.getChild(n - 1));
            }
            if (!(node.getChild(n) instanceof Spin2StatementNode.Index)) {
                throw new CompilerException("invalid bitfield expression", node.getChild(n));
            }
            bitfieldNode = node.getChild(n++);
        }
        if (n < node.getChildCount() && isPostEffect(node.getChild(n))) {
            postEffectNode = node.getChild(n++);
        }
        if (n < node.getChildCount()) {
            throw new CompilerException("unexpected " + node.getChild(n).getText(), node.getChild(n));
        }

        int index = 0;
        boolean hasIndex = false;
        boolean popIndex = false;

        if (indexNode != null) {
            popIndex = true;
            try {
                Expression exp = buildConstantExpression(context, indexNode);
                if (exp.isConstant()) {
                    index = exp.getNumber().intValue();
                    hasIndex = true;
                    popIndex = false;
                }
            } catch (Exception e) {
                // Do nothing
            }
        }

        if (bitfieldNode != null) {
            int bitfield = compileBitfield(context, bitfieldNode, source);

            if (popIndex) {
                source.addAll(compileBytecodeExpression(context, indexNode, true));
            }

            if (expression instanceof Register) {
                source.add(new RegisterOp(context, RegisterOp.Op.Setup, popIndex, expression, index));
            }
            else if (expression instanceof ContextLiteral) {
                source.add(new MemoryOp(context, ss, bb, MemoryOp.Op.Setup, popIndex, expression, index));
            }
            else {
                source.add(new VariableOp(context, VariableOp.Op.Setup, popIndex, (Variable) expression, hasIndex, index));
            }

            BitField.Op op = field ? BitField.Op.Field : (postEffectNode == null ? BitField.Op.Read : BitField.Op.Setup);
            source.add(new BitField(context, op, bitfield));

            if (postEffectNode != null) {
                compilePostEffect(context, postEffectNode, source, push);
            }
        }
        else {
            if (popIndex) {
                source.addAll(compileBytecodeExpression(context, indexNode, true));
            }
            if (postEffectNode != null) {
                if ("~".equalsIgnoreCase(postEffectNode.getText()) || "~~".equalsIgnoreCase(postEffectNode.getText())) {
                    if ("~".equalsIgnoreCase(postEffectNode.getText())) {
                        source.add(new Constant(context, new NumberLiteral(0)));
                    }
                    else {
                        source.add(new Constant(context, new NumberLiteral(-1)));
                    }
                    if (expression instanceof Register) {
                        source.add(new RegisterOp(context, push ? RegisterOp.Op.Setup : RegisterOp.Op.Write, popIndex, expression, index));
                    }
                    else if (expression instanceof ContextLiteral) {
                        source.add(new MemoryOp(context, ss, bb, push ? MemoryOp.Op.Setup : MemoryOp.Op.Write, popIndex, expression, index));
                    }
                    else {
                        source.add(new VariableOp(context, push ? VariableOp.Op.Setup : VariableOp.Op.Write, popIndex, (Variable) expression, hasIndex, index));
                    }
                    if (push) {
                        source.add(new Bytecode(context, 0x8D, "SWAP"));
                    }
                }
                else {
                    if (expression instanceof Register) {
                        source.add(new RegisterOp(context, RegisterOp.Op.Setup, popIndex, expression, index));
                    }
                    else if (expression instanceof ContextLiteral) {
                        source.add(new MemoryOp(context, ss, bb, MemoryOp.Op.Setup, popIndex, expression, index));
                    }
                    else {
                        source.add(new VariableOp(context, VariableOp.Op.Setup, popIndex, (Variable) expression, hasIndex, index));
                    }
                    if ("++".equalsIgnoreCase(postEffectNode.getText())) {
                        source.add(new Bytecode(context, push ? 0x87 : 0x83, "POST_INC" + (push ? " (push)" : "")));
                    }
                    else if ("--".equalsIgnoreCase(postEffectNode.getText())) {
                        source.add(new Bytecode(context, push ? 0x88 : 0x84, "POST_DEC" + (push ? " (push)" : "")));
                    }
                    else if ("!!".equalsIgnoreCase(postEffectNode.getText())) {
                        source.add(new Bytecode(context, push ? 0x8A : 0x89, "POST_LOGICAL_NOT" + (push ? " (push)" : "")));
                    }
                    else if ("!".equalsIgnoreCase(postEffectNode.getText())) {
                        source.add(new Bytecode(context, push ? 0x8C : 0x8B, "POST_NOT" + (push ? " (push)" : "")));
                    }
                    else {
                        throw new CompilerException("unsupported post effect " + postEffectNode.getText(), postEffectNode.getToken());
                    }
                }
            }
            else {
                if (expression instanceof Register) {
                    source.add(new RegisterOp(context, field ? RegisterOp.Op.Field : RegisterOp.Op.Read, popIndex, expression, index));
                }
                else if (expression instanceof ContextLiteral) {
                    source.add(new MemoryOp(context, ss, bb, field ? MemoryOp.Op.Field : MemoryOp.Op.Read, popIndex, expression, index));
                }
                else {
                    source.add(new VariableOp(context, field ? VariableOp.Op.Field : VariableOp.Op.Read, popIndex, (Variable) expression, hasIndex, index));
                }
            }
        }

        return source;
    }

    boolean isPostEffect(Spin2StatementNode node) {
        if (node.getChildCount() != 0) {
            return false;
        }
        String s = node.getText();
        return "++".equals(s) || "--".equals(s) || "!!".equals(s) || "!".equals(s) || "~".equals(s) || "~~".equals(s);
    }

    void compilePostEffect(Spin2Context context, Spin2StatementNode node, List<Spin2Bytecode> source, boolean push) {
        if ("~".equalsIgnoreCase(node.getText())) {
            source.add(0, new Constant(context, new NumberLiteral(0)));
            source.add(new Bytecode(context, push ? 0x8D : 0x81, push ? "SWAP" : "WRITE"));
        }
        else if ("~~".equalsIgnoreCase(node.getText())) {
            source.add(0, new Constant(context, new NumberLiteral(-1)));
            source.add(new Bytecode(context, push ? 0x8D : 0x81, push ? "SWAP" : "WRITE"));
        }
        else if ("++".equalsIgnoreCase(node.getText())) {
            source.add(new Bytecode(context, push ? 0x87 : 0x83, "POST_INC" + (push ? " (push)" : "")));
        }
        else if ("--".equalsIgnoreCase(node.getText())) {
            source.add(new Bytecode(context, push ? 0x88 : 0x84, "POST_DEC" + (push ? " (push)" : "")));
        }
        else if ("!!".equalsIgnoreCase(node.getText())) {
            source.add(new Bytecode(context, push ? 0x8A : 0x89, "POST_LOGICAL_NOT" + (push ? " (push)" : "")));
        }
        else if ("!".equalsIgnoreCase(node.getText())) {
            source.add(new Bytecode(context, push ? 0x8C : 0x8B, "POST_NOT" + (push ? " (push)" : "")));
        }
        else {
            throw new CompilerException("unhandled post effect " + node.getText(), node.getToken());
        }
    }

    protected abstract void logMessage(CompilerException message);

}
