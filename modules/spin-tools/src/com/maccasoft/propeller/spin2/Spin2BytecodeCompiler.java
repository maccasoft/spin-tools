/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.ObjectCompiler;
import com.maccasoft.propeller.expressions.Abs;
import com.maccasoft.propeller.expressions.Add;
import com.maccasoft.propeller.expressions.Addbits;
import com.maccasoft.propeller.expressions.Addpins;
import com.maccasoft.propeller.expressions.AddpinsRange;
import com.maccasoft.propeller.expressions.And;
import com.maccasoft.propeller.expressions.CharacterLiteral;
import com.maccasoft.propeller.expressions.Compare;
import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.ContextLiteral;
import com.maccasoft.propeller.expressions.DataVariable;
import com.maccasoft.propeller.expressions.Decod;
import com.maccasoft.propeller.expressions.Divide;
import com.maccasoft.propeller.expressions.Encod;
import com.maccasoft.propeller.expressions.Equals;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.GreaterOrEquals;
import com.maccasoft.propeller.expressions.GreaterOrEqualsUnsigned;
import com.maccasoft.propeller.expressions.GreaterThan;
import com.maccasoft.propeller.expressions.GreaterThanUnsigned;
import com.maccasoft.propeller.expressions.IfElse;
import com.maccasoft.propeller.expressions.LessOrEquals;
import com.maccasoft.propeller.expressions.LessOrEqualsUnsigned;
import com.maccasoft.propeller.expressions.LessThan;
import com.maccasoft.propeller.expressions.LessThanUnsigned;
import com.maccasoft.propeller.expressions.LocalVariable;
import com.maccasoft.propeller.expressions.LogicalAnd;
import com.maccasoft.propeller.expressions.LogicalNot;
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
import com.maccasoft.propeller.expressions.Rev;
import com.maccasoft.propeller.expressions.Rol;
import com.maccasoft.propeller.expressions.Ror;
import com.maccasoft.propeller.expressions.Round;
import com.maccasoft.propeller.expressions.Sar;
import com.maccasoft.propeller.expressions.Sca;
import com.maccasoft.propeller.expressions.Scas;
import com.maccasoft.propeller.expressions.ShiftLeft;
import com.maccasoft.propeller.expressions.ShiftRight;
import com.maccasoft.propeller.expressions.Signx;
import com.maccasoft.propeller.expressions.SpinObject;
import com.maccasoft.propeller.expressions.Sqrt;
import com.maccasoft.propeller.expressions.Subtract;
import com.maccasoft.propeller.expressions.Trunc;
import com.maccasoft.propeller.expressions.UnsignedDivide;
import com.maccasoft.propeller.expressions.UnsignedModulo;
import com.maccasoft.propeller.expressions.Variable;
import com.maccasoft.propeller.expressions.Xor;
import com.maccasoft.propeller.expressions.Zerox;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.spin2.Spin2Bytecode.Descriptor;
import com.maccasoft.propeller.spin2.Spin2Struct.Spin2StructMember;
import com.maccasoft.propeller.spin2.bytecode.Address;
import com.maccasoft.propeller.spin2.bytecode.BitField;
import com.maccasoft.propeller.spin2.bytecode.Bytecode;
import com.maccasoft.propeller.spin2.bytecode.CallSub;
import com.maccasoft.propeller.spin2.bytecode.Constant;
import com.maccasoft.propeller.spin2.bytecode.MathOp;
import com.maccasoft.propeller.spin2.bytecode.MemoryOp;
import com.maccasoft.propeller.spin2.bytecode.MemoryOp.Op;
import com.maccasoft.propeller.spin2.bytecode.RegisterOp;
import com.maccasoft.propeller.spin2.bytecode.SubAddress;
import com.maccasoft.propeller.spin2.bytecode.VariableOp;

public abstract class Spin2BytecodeCompiler extends Spin2PasmCompiler {

    public Spin2BytecodeCompiler(Context scope, Spin2Compiler compiler, File file) {
        super(scope, compiler, file);
    }

    public Spin2BytecodeCompiler(Context scope, Spin2Compiler compiler, ObjectCompiler parent, File file) {
        super(scope, compiler, parent, file);
    }

    public List<Spin2Bytecode> compileBytecodeExpression(Context context, Spin2Method method, Spin2StatementNode node, boolean push) {
        List<Spin2Bytecode> source = new ArrayList<Spin2Bytecode>();

        try {
            if (node.getType() == Token.NUMBER) {
                Expression expression = new NumberLiteral(node.getText());
                source.add(new Constant(context, expression));
                return source;
            }
            if (node.getType() == Token.STRING) {
                String s = node.getText();
                if (s.startsWith("@")) {
                    StringBuilder sb = new StringBuilder(s.substring(2, s.length() - 1));
                    sb.append((char) 0x00);

                    byte[] code = sb.toString().getBytes();
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    os.write(Spin2Bytecode.bc_string);
                    os.write(code.length);
                    os.writeBytes(code);
                    source.add(new Bytecode(context, os.toByteArray(), "STRING"));
                }
                else if (s.startsWith("%")) {
                    s = s.substring(2, s.length() - 1);
                    if (s.length() > 4) {
                        throw new CompilerException("no more than 4 characters can be packed into a long", node.getTokens());
                    }

                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    os.write(Spin2Bytecode.bc_con_rflong);

                    int i = 0;
                    while (i < s.length()) {
                        os.write(s.charAt(i++));
                    }
                    while (i < 4) {
                        os.write(0x00);
                        i++;
                    }
                    source.add(new Bytecode(context, os.toByteArray(), "CONSTANT (" + node.getText() + ")"));
                }
                else {
                    s = s.substring(1, s.length() - 1);
                    if (s.length() == 1) {
                        Expression expression = new CharacterLiteral(s);
                        source.add(new Constant(context, expression));
                    }
                    else {
                        StringBuilder sb = new StringBuilder(s);
                        sb.append((char) 0x00);

                        byte[] code = sb.toString().getBytes();
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        os.write(Spin2Bytecode.bc_string);
                        os.write(code.length);
                        os.writeBytes(code);
                        source.add(new Bytecode(context, os.toByteArray(), "STRING"));
                    }
                }
                return source;
            }

            if (node.isMethod()) {
                if ("RECV".equalsIgnoreCase(node.getText())) {
                    if (node.getChildCount() != 0) {
                        throw new RuntimeException("expected " + 0 + " argument(s), found " + node.getChildCount());
                    }
                    source.add(new Bytecode(context, Spin2Bytecode.bc_call_recv, node.getText().toUpperCase()));
                    return source;
                }

                if ("SEND".equalsIgnoreCase(node.getText())) {
                    if (node.getChildCount() == 0) {
                        throw new RuntimeException("syntax error");
                    }

                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    int i = 0;
                    while (i < node.getChildCount()) {
                        Spin2StatementNode child = node.getChild(i);

                        boolean isByte = false;
                        if (child.getType() == Token.STRING) {
                            String s = child.getText();
                            os.write(s.substring(1, s.length() - 1).getBytes());
                            isByte = true;
                        }
                        else {
                            try {
                                Expression expression = buildConstantExpression(context, child);
                                if (expression.isConstant()) {
                                    int value = expression.getNumber().intValue();
                                    if (value >= 0x00 && value <= 0xFF) {
                                        os.write(value);
                                        isByte = true;
                                    }
                                }
                            } catch (Exception e) {
                                // Do nothing
                            }
                        }
                        if (!isByte) {
                            if (os.size() == 1) {
                                source.addAll(compileConstantExpression(context, method, node.getChild(i - 1)));
                                source.add(new Bytecode(context, Spin2Bytecode.bc_call_send, node.getText().toUpperCase()));
                                os = new ByteArrayOutputStream();
                            }
                            else if (os.size() != 0) {
                                byte[] code = new byte[os.size() + 2];
                                code[0] = Spin2Bytecode.bc_call_send_bytes;
                                code[1] = (byte) os.size();
                                System.arraycopy(os.toByteArray(), 0, code, 2, os.size());
                                source.add(new Bytecode(context, code, node.getText().toUpperCase() + "_BYTES"));
                                os = new ByteArrayOutputStream();
                            }

                            boolean popValue = false;

                            Method methodExpression = getMethodExpression(context, child);
                            if (methodExpression != null) {
                                if (methodExpression.getReturnsCount() > 1) {
                                    throw new CompilerException("send parameter cannot return multiple values", child.getTokens());
                                }
                                popValue = methodExpression.getReturnsCount() != 0;
                                source.addAll(compileMethodCall(context, method, methodExpression, child, popValue, false));
                            }
                            else {
                                source.addAll(compileConstantExpression(context, method, child));
                                popValue = true;
                            }

                            if (popValue) {
                                source.add(new Bytecode(context, Spin2Bytecode.bc_call_send, node.getText().toUpperCase()));
                            }
                        }
                        i++;
                    }
                    if (os.size() == 1) {
                        source.addAll(compileConstantExpression(context, method, node.getChild(i - 1)));
                        source.add(new Bytecode(context, Spin2Bytecode.bc_call_send, node.getText().toUpperCase()));
                    }
                    else if (os.size() != 0) {
                        byte[] code = new byte[os.size() + 2];
                        code[0] = Spin2Bytecode.bc_call_send_bytes;
                        code[1] = (byte) os.size();
                        System.arraycopy(os.toByteArray(), 0, code, 2, os.size());
                        source.add(new Bytecode(context, code, node.getText().toUpperCase() + "_BYTES"));
                    }
                    return source;
                }
            }

            String[] ar = node.getText().split("[\\.]");
            if (ar.length >= 2 && ("BYTE".equalsIgnoreCase(ar[ar.length - 1]) || "WORD".equalsIgnoreCase(ar[ar.length - 1]) || "LONG".equalsIgnoreCase(ar[ar.length - 1]))) {
                int index = 0;
                boolean popIndex = false;
                Spin2StatementNode indexNode = null;
                Spin2StatementNode bitfieldNode = null;
                Spin2StatementNode postEffectNode = null;

                Expression expression = context.getLocalSymbol(ar[0]);
                if (expression == null && isAbsoluteAddress(ar[0])) {
                    expression = context.getLocalSymbol(ar[0].substring(2));
                }
                if (expression == null && isAddress(ar[0])) {
                    expression = context.getLocalSymbol(ar[0].substring(1));
                }
                if (expression instanceof ObjectContextLiteral) {
                    expression = context.getLocalSymbol(ar[0].substring(1));
                }
                if (expression != null) {
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
                    if ("BYTE".equalsIgnoreCase(ar[ar.length - 1])) {
                        ss = MemoryOp.Size.Byte;
                    }
                    else if ("WORD".equalsIgnoreCase(ar[ar.length - 1])) {
                        ss = MemoryOp.Size.Word;
                    }
                    MemoryOp.Base bb = MemoryOp.Base.PBase;
                    if (expression instanceof Variable) {
                        bb = expression instanceof LocalVariable ? MemoryOp.Base.DBase : MemoryOp.Base.VBase;
                        ((Variable) expression).setCalledBy(method);
                    }

                    int bitfield = -1;
                    if (bitfieldNode != null) {
                        bitfield = compileBitfield(context, method, bitfieldNode, source);
                    }

                    if (isPointer(expression)) {
                        source.add(new VariableOp(context, VariableOp.Op.Read, false, (Variable) expression, false, 0));
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
                            source.addAll(compileBytecodeExpression(context, method, indexNode, true));
                        }
                    }

                    if (ar[0].startsWith("@")) {
                        source.add(new MemoryOp(context, ss, bb, MemoryOp.Op.Address, popIndex, expression, index));
                    }
                    else if (isPointer(expression)) {
                        if (!popIndex) {
                            source.add(new Constant(context, new NumberLiteral(index)));
                            popIndex = true;
                        }
                        source.add(new MemoryOp(context, ss, MemoryOp.Base.Pop, push ? MemoryOp.Op.Read : MemoryOp.Op.Setup, popIndex, expression, 0));
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
                            source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_postinc_push : Spin2Bytecode.bc_var_inc, "POST_INC" + (push ? " (push)" : "")));
                        }
                        else if ("--".equalsIgnoreCase(postEffectNode.getText())) {
                            source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_postdec_push : Spin2Bytecode.bc_var_dec, "POST_DEC" + (push ? " (push)" : "")));
                        }
                        else if ("!!".equalsIgnoreCase(postEffectNode.getText())) {
                            source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_lognot_push : Spin2Bytecode.bc_var_lognot, "POST_LOGICAL_NOT" + (push ? " (push)" : "")));
                        }
                        else if ("!".equalsIgnoreCase(postEffectNode.getText())) {
                            source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_bitnot_push : Spin2Bytecode.bc_var_bitnot, "POST_NOT" + (push ? " (push)" : "")));
                        }
                        else {
                            throw new CompilerException("unhandled post effect " + postEffectNode.getText(), postEffectNode.getToken());
                        }
                    }

                    return source;
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
                if (expression == null && node.getText().startsWith("^@")) {
                    expression = context.getLocalSymbol(node.getText().substring(2));
                }
                if (expression instanceof ObjectContextLiteral) {
                    expression = context.getLocalSymbol(node.getText().substring(1));
                }

                if (expression == null && ar.length > 1) {
                    expression = context.getLocalSymbol(ar[0]);
                    if (expression == null && isAddress(ar[0])) {
                        expression = context.getLocalSymbol(ar[0].substring(1));
                    }
                }

                if (isStructure(context, expression)) {
                    source.addAll(compileVariableRead(context, method, expression, node, push));
                    return source;
                }

                if (expression != null) {
                    if (expression instanceof SpinObject) {
                        source.addAll(compileMethodCall(context, method, expression, node, push, false));
                        return source;
                    }
                    else if (expression instanceof Method) {
                        if (isAddress(node.getText())) {
                            Method methodExpression = (Method) expression;
                            source.add(new SubAddress(context, methodExpression));
                            Spin2Method calledMethod = (Spin2Method) methodExpression.getData(Spin2Method.class.getName());
                            calledMethod.setCalledBy(method);
                        }
                        else {
                            source.addAll(compileMethodCall(context, method, expression, node, push, false));
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
                                throw new CompilerException("unexpected " + node.getChild(n).getText(), node.getChild(n).getToken());
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
                                    source.addAll(compileBytecodeExpression(context, method, indexNode, true));
                                }
                            }

                            VariableOp.Op op = VariableOp.Op.Address;
                            if (node.getText().startsWith("@@")) {
                                op = VariableOp.Op.PBaseAddress;
                            }

                            source.add(new VariableOp(context, op, popIndex, (Variable) expression, hasIndex, index));
                        }
                        else {
                            if (node.isMethod()) {
                                source.addAll(compileMethodCall(context, method, expression, node, push, false));
                            }
                            else {
                                source.addAll(compileVariableRead(context, method, expression, node, push));
                            }
                        }
                        ((Variable) expression).setCalledBy(method);
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
                                    source.addAll(compileBytecodeExpression(context, method, indexNode, true));
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
                            if (isAbsoluteAddress(node.getText())) {
                                source.add(new MemoryOp(context, ss, bb, MemoryOp.Op.Read, popIndex, expression, index));
                                source.add(new Bytecode(context, Spin2Bytecode.bc_add_pbase, "ADD_PBASE"));
                            }
                            else {
                                source.add(new MemoryOp(context, ss, bb, MemoryOp.Op.Address, popIndex, expression, index));
                            }
                        }
                        else if (expression.isConstant()) {
                            if (node.getChildCount() != 0) {
                                throw new RuntimeException("syntax error");
                            }
                            source.add(new Constant(context, expression));
                        }
                        else if (expression instanceof Register) {
                            source.addAll(compileVariableRead(context, method, expression, node, push));
                        }
                        else if (expression instanceof DataVariable) {
                            if (node.isMethod()) {
                                source.addAll(compileMethodCall(context, method, expression, node, push, false));
                            }
                            else {
                                source.addAll(compileVariableRead(context, method, expression, node, push));
                            }
                        }
                        else if (expression instanceof ContextLiteral) {
                            source.addAll(compileVariableRead(context, method, expression, node, push));
                        }
                        else {
                            if (node.getChildCount() != 0) {
                                throw new RuntimeException("syntax error");
                            }
                            source.add(new MemoryOp(context, MemoryOp.Size.Long, MemoryOp.Base.PBase, MemoryOp.Op.Read, expression));
                        }
                    }

                    return source;
                }
            }

            if (node.isMethod()) {
                Descriptor desc = Spin2Bytecode.getDescriptor(node.getText());
                if (desc != null) {
                    int actual = getArgumentsCount(context, node);
                    if (actual != desc.getParameters()) {
                        throw new RuntimeException("expected " + desc.getParameters() + " argument(s), found " + actual);
                    }
                    for (int i = 0; i < node.getChildCount(); i++) {
                        source.addAll(compileConstantExpression(context, method, node.getChild(i)));
                    }
                    source.add(new Bytecode(context, desc.code, node.getText().toUpperCase()));
                    return source;
                }
                if ("ABORT".equalsIgnoreCase(node.getText())) {
                    int actual = getArgumentsCount(context, node);
                    if (actual == 0) {
                        source.add(new Bytecode(context, Spin2Bytecode.bc_abort_0, node.getText().toUpperCase()));
                        return source;
                    }
                    if (actual == 1) {
                        source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));
                        source.add(new Bytecode(context, Spin2Bytecode.bc_abort_arg, node.getText().toUpperCase()));
                        return source;
                    }
                    throw new RuntimeException("expected 0 or 1 argument(s), found " + actual);
                }
                if ("COGINIT".equalsIgnoreCase(node.getText())) {
                    if (node.getChildCount() != 3) {
                        throw new RuntimeException("expected " + 3 + " argument(s), found " + node.getChildCount());
                    }
                    for (int i = 0; i < node.getChildCount(); i++) {
                        source.addAll(compileBytecodeExpression(context, method, node.getChild(i), true));
                    }
                    source.add(new Bytecode(context, push ? Spin2Bytecode.bc_coginit_push : Spin2Bytecode.bc_coginit, node.getText().toUpperCase()));
                    return source;
                }
                if ("COGNEW".equalsIgnoreCase(node.getText())) {
                    if (node.getChildCount() != 2) {
                        throw new RuntimeException("expected " + 2 + " argument(s), found " + node.getChildCount());
                    }
                    source.add(new Constant(context, new NumberLiteral(16)));
                    for (int i = 0; i < node.getChildCount(); i++) {
                        source.addAll(compileBytecodeExpression(context, method, node.getChild(i), true));
                    }
                    source.add(new Bytecode(context, push ? Spin2Bytecode.bc_coginit_push : Spin2Bytecode.bc_coginit, node.getText().toUpperCase()));
                    return source;
                }
                if ("COGSPIN".equalsIgnoreCase(node.getText())) {
                    if (node.getChildCount() != 3) {
                        throw new RuntimeException("expected " + 3 + " argument(s), found " + node.getChildCount());
                    }

                    source.addAll(compileConstantExpression(context, method, node.getChild(0)));

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
                        source.addAll(compileConstantExpression(context, method, methodNode.getChild(i)));
                    }
                    source.add(new SubAddress(context, (Method) expression, false));
                    Spin2Method calledMethod = (Spin2Method) expression.getData(Spin2Method.class.getName());
                    calledMethod.setCalledBy(method);

                    source.addAll(compileConstantExpression(context, method, node.getChild(2)));

                    source.add(new Bytecode(context, new byte[] {
                        Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_cogspin,
                        (byte) methodNode.getChildCount(), (byte) (push ? Spin2Bytecode.bc_coginit_push : Spin2Bytecode.bc_coginit)
                    }, node.getText().toUpperCase()));

                    return source;
                }
                if ("LOOKDOWN".equalsIgnoreCase(node.getText()) || "LOOKDOWNZ".equalsIgnoreCase(node.getText()) || "LOOKUP".equalsIgnoreCase(node.getText())
                    || "LOOKUPZ".equalsIgnoreCase(node.getText())) {
                    if (node.getChildCount() == 0) {
                        throw new RuntimeException("expected argument(s), found none");
                    }
                    Spin2StatementNode argsNode = node.getChild(0);
                    if (!":".equalsIgnoreCase(argsNode.getText()) || argsNode.getChildCount() < 2) {
                        throw new RuntimeException("invalid argument(s)");
                    }

                    int code = Spin2Bytecode.bc_lookup_value;
                    int code_range = Spin2Bytecode.bc_lookup_range;
                    if ("LOOKDOWN".equalsIgnoreCase(node.getText()) || "LOOKDOWNZ".equalsIgnoreCase(node.getText())) {
                        code = Spin2Bytecode.bc_lookdown_value;
                        code_range = Spin2Bytecode.bc_lookdown_range;
                    }

                    Spin2Bytecode end = new Spin2Bytecode(context);
                    source.add(new Address(context, new ContextLiteral(end.getContext())));

                    source.addAll(compileBytecodeExpression(context, method, argsNode.getChild(0), true));

                    source.add(new Constant(context, new NumberLiteral(node.getText().toUpperCase().endsWith("Z") ? 0 : 1)));

                    for (int i = 1; i < argsNode.getChildCount(); i++) {
                        Spin2StatementNode arg = argsNode.getChild(i);
                        if ("..".equals(arg.getText())) {
                            source.addAll(compileBytecodeExpression(context, method, arg.getChild(0), true));
                            source.addAll(compileBytecodeExpression(context, method, arg.getChild(1), true));
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
                            source.addAll(compileBytecodeExpression(context, method, arg, true));
                            source.add(new Bytecode(context, code, node.getText().toUpperCase()));
                        }
                    }

                    source.add(new Bytecode(context, Spin2Bytecode.bc_look_done, "LOOKDONE"));
                    source.add(end);

                    return source;
                }
                if ("STRING".equalsIgnoreCase(node.getText()) || "LSTRING".equalsIgnoreCase(node.getText())) {
                    ByteArrayOutputStream sb = new ByteArrayOutputStream();
                    for (Spin2StatementNode child : node.getChilds()) {
                        if (child.getType() == Token.STRING) {
                            String s = child.getText().substring(1);
                            sb.write(s.substring(0, s.length() - 1).getBytes());
                        }
                        else {
                            Expression expression;
                            if (child.getType() == Token.NUMBER) {
                                expression = new NumberLiteral(child.getText());
                            }
                            else {
                                try {
                                    expression = buildConstantExpression(context, child);
                                    if (!expression.isConstant()) {
                                        throw new CompilerException("expression is not constant", child.getTokens());
                                    }
                                } catch (CompilerException e) {
                                    throw e;
                                } catch (Exception e) {
                                    throw new CompilerException("expression is not constant", child.getTokens());
                                }
                            }
                            if (expression.isString()) {
                                int[] s = expression.getStringValues();
                                for (int i = 0; i < s.length; i++) {
                                    sb.write((byte) s[i]);
                                }
                            }
                            else {
                                if (expression.getNumber().intValue() < -0x80 || expression.getNumber().intValue() > 0xFF) {
                                    logMessage(new CompilerException(CompilerException.WARNING, "byte value range from -$80 to $FF", child.getTokens()));
                                }
                                sb.write(expression.getByte());
                            }
                        }
                    }
                    if (sb.size() > 254) {
                        logMessage(new CompilerException(CompilerException.ERROR, "string data cannot exceed 254 bytes", node.getTokens()));
                    }
                    if ("STRING".equalsIgnoreCase(node.getText())) {
                        sb.write(0x00);
                    }
                    byte[] code = sb.toByteArray();

                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    os.write(Spin2Bytecode.bc_string);
                    if ("LSTRING".equalsIgnoreCase(node.getText())) {
                        os.write(code.length + 1);
                    }
                    os.write(code.length);
                    os.writeBytes(code);
                    source.add(new Bytecode(context, os.toByteArray(), node.getText().toUpperCase()));

                    return source;
                }
                if ("SIZEOF".equalsIgnoreCase(node.getText())) {
                    if (node.getChildCount() != 1) {
                        throw new RuntimeException("expected " + 1 + " argument(s), found " + node.getChildCount());
                    }
                    Expression expression = getSizeof(context, node.getChild(0).getText());
                    if (expression != null) {
                        source.add(new Constant(context, expression));
                    }
                    else {
                        logMessage(new CompilerException("expected type or variable", node.getChild(0).getTokens()));
                    }
                    return source;
                }
                if ("DEBUG".equalsIgnoreCase(node.getText())) {
                    int stack = 0;
                    for (Spin2StatementNode child : node.getChilds()) {
                        for (Spin2StatementNode param : child.getChilds()) {
                            source.addAll(compileBytecodeExpression(context, method, param, true));
                            stack += 4;
                        }
                    }
                    debug.compileDebugStatement(node);
                    node.setData("context", context);
                    if (isDebugEnabled()) {
                        method.debugNodes.add(node);
                        compiler.debugStatements.add(node);

                        int pop = stack;
                        source.add(new Bytecode(context, Spin2Bytecode.bc_debug, "") {

                            int index;

                            @Override
                            public int resolve(int address) {
                                index = compiler.debugStatements.indexOf(node) + 1;
                                if (index >= 255) {
                                    throw new CompilerException("too much debug statements", node);
                                }
                                return super.resolve(address);
                            }

                            @Override
                            public int getSize() {
                                return index == -1 ? 0 : 3;
                            }

                            @Override
                            public byte[] getBytes() {
                                if (index == -1) {
                                    return new byte[0];
                                }
                                return new byte[] {
                                    Spin2Bytecode.bc_debug, (byte) pop, (byte) index
                                };
                            }

                            @Override
                            public String toString() {
                                if (index == -1) {
                                    return "";
                                }
                                return node.getText().toUpperCase() + " #" + index;
                            }

                        });
                        return source;
                    }
                    return new ArrayList<Spin2Bytecode>();
                }
                if ("BYTECODE".equalsIgnoreCase(node.getText())) {
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
                    return source;
                }
            }

            if ("CLKMODE".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 0) {
                    throw new CompilerException("syntax error", node);
                }
                source.add(new Constant(context, new NumberLiteral(0x40, 16)));
                source.add(new MemoryOp(context, MemoryOp.Size.Long, MemoryOp.Base.Pop, MemoryOp.Op.Read, null));
                return source;
            }
            if ("CLKFREQ".equalsIgnoreCase(node.getText())) {
                source.add(new Bytecode(context, new byte[] {
                    Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_read_clkfreq
                }, node.getText().toUpperCase()));
                return source;
            }
            if ("END".equalsIgnoreCase(node.getText())) {
                // Ignored
                return source;
            }

            if ("-".equalsIgnoreCase(node.getText()) && node.getChildCount() == 1) {
                try {
                    Expression expression = buildConstantExpression(context, node);
                    source.add(new Constant(context, expression));
                } catch (Exception e) {
                    source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));
                    source.add(new Bytecode(context, Spin2Bytecode.bc_neg, "NEGATE"));
                }
                return source;
            }
            if ("-.".equalsIgnoreCase(node.getText()) && node.getChildCount() == 1) {
                try {
                    Expression expression = buildConstantExpression(context, node);
                    source.add(new Constant(context, expression));
                } catch (Exception e) {
                    source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));
                    source.add(new Bytecode(context, new byte[] {
                        Spin2Bytecode.bc_hub_bytecode, (byte) Spin2Bytecode.bc_fneg
                    }, "FLOAT_NEGATE"));
                }
                return source;
            }
            if (("+".equalsIgnoreCase(node.getText()) || "+.".equalsIgnoreCase(node.getText())) && node.getChildCount() == 1) {
                try {
                    Expression expression = buildConstantExpression(context, node);
                    source.add(new Constant(context, expression));
                } catch (Exception e) {
                    source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));
                }
                return source;
            }
            if ("-=".equalsIgnoreCase(node.getText()) && node.getChildCount() == 1) {
                source.addAll(leftAssign(context, method, node.getChild(0), true, false));
                if (push) {
                    source.add(new Bytecode(context, Spin2Bytecode.bc_neg_write_push, "NEGATE_ASSIGN (push)"));
                }
                else {
                    source.add(new Bytecode(context, Spin2Bytecode.bc_neg_write, "NEGATE_ASSIGN"));
                }
                return source;
            }
            if (":=".equals(node.getText())) {
                if (node.getChildCount() != 2) {
                    throw new RuntimeException("expression syntax error");
                }
                source.addAll(compileConstantExpression(context, method, node.getChild(1)));
                source.addAll(leftAssign(context, method, node.getChild(0), push, push));
                return source;
            }
            if (MathOp.isAssignMathOp(node.getText()) && node.getChildCount() == 1) {
                source.addAll(leftAssign(context, method, node.getChild(0), true, false));
                source.add(new MathOp(context, node.getText(), push));
                return source;
            }
            if (MathOp.isAssignMathOp(node.getText())) {
                source.addAll(compileConstantExpression(context, method, node.getChild(1)));
                source.addAll(leftAssign(context, method, node.getChild(0), true, false));
                source.add(new MathOp(context, node.getText(), push));
                return source;
            }
            if (MathOp.isUnaryMathOp(node.getText())) {
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("expression syntax error");
                }
                source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));
                source.add(new MathOp(context, node.getText(), push));
                return source;
            }
            if (MathOp.isMathOp(node.getText())) {
                if (node.getChildCount() != 2) {
                    throw new RuntimeException("expression syntax error");
                }
                source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));
                source.addAll(compileBytecodeExpression(context, method, node.getChild(1), true));
                source.add(new MathOp(context, node.getText(), false));
                return source;
            }
            if ("?".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 2) {
                    throw new RuntimeException("expression syntax error");
                }
                if (!":".equals(node.getChild(1).getText())) {
                    throw new RuntimeException("expression syntax error");
                }
                source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));
                source.addAll(compileBytecodeExpression(context, method, node.getChild(1).getChild(0), true));
                source.addAll(compileBytecodeExpression(context, method, node.getChild(1).getChild(1), true));
                source.add(new Bytecode(context, Spin2Bytecode.bc_ternary, "TERNARY_IF_ELSE"));
                return source;
            }
            if ("_".equalsIgnoreCase(node.getText())) {
                source.add(new Bytecode(context, Spin2Bytecode.bc_pop, "POP"));
                return source;
            }
            if ("(".equalsIgnoreCase(node.getText())) {
                source.addAll(compileBytecodeExpression(context, method, node.getChild(0), push));
                return source;
            }
            if (",".equalsIgnoreCase(node.getText())) {
                for (Spin2StatementNode child : node.getChilds()) {
                    source.addAll(compileBytecodeExpression(context, method, child, push));
                }
                return source;
            }
            if ("\\".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() == 2) {
                    source.addAll(compileConstantExpression(context, method, node.getChild(1)));
                    source.addAll(leftAssign(context, method, node.getChild(0), push, false));
                    source.add(new Bytecode(context, Spin2Bytecode.bc_var_swap, "SWAP"));
                }
                else {
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
                    else if (expression instanceof Variable) {
                        source.addAll(compileMethodCall(context, method, expression, node.getChild(0), push, true));
                    }
                    else if (expression instanceof DataVariable) {
                        source.addAll(compileMethodCall(context, method, expression, node.getChild(0), push, true));
                    }
                    else if ("BYTE".equalsIgnoreCase(node.getChild(0).getText()) || "WORD".equalsIgnoreCase(node.getChild(0).getText()) || "LONG".equalsIgnoreCase(node.getChild(0).getText())) {
                        source.addAll(compileBytecodeExpression(context, method, node.getChild(0), push));
                    }
                    else {
                        throw new CompilerException("symbol " + node.getChild(0).getText() + " is not a method", node.getChild(0).getToken());
                    }
                }
                return source;
            }
            if ("++".equalsIgnoreCase(node.getText()) || "--".equalsIgnoreCase(node.getText()) || "??".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("expression syntax error " + node.getText());
                }

                Spin2StatementNode childNode = node.getChild(0);

                String[] s = childNode.getText().split("[\\.]");
                if (s.length == 2 && ("BYTE".equalsIgnoreCase(s[1]) || "WORD".equalsIgnoreCase(s[1]) || "LONG".equalsIgnoreCase(s[1]))) {
                    Spin2StatementNode indexNode = null;
                    Spin2StatementNode bitfieldNode = null;

                    Expression expression = context.getLocalSymbol(s[0]);
                    if (expression == null) {
                        throw new CompilerException("undefined symbol " + node.getText(), node.getToken());
                    }

                    int n = 0;
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

                    if (indexNode != null) {
                        source.addAll(compileBytecodeExpression(context, method, indexNode, true));
                    }

                    int bitfield = -1;
                    if (bitfieldNode != null) {
                        bitfield = compileBitfield(context, method, bitfieldNode, source);
                    }

                    MemoryOp.Size ss = MemoryOp.Size.Long;
                    if ("BYTE".equalsIgnoreCase(s[1])) {
                        ss = MemoryOp.Size.Byte;
                    }
                    else if ("WORD".equalsIgnoreCase(s[1])) {
                        ss = MemoryOp.Size.Word;
                    }
                    MemoryOp.Base bb = MemoryOp.Base.PBase;
                    if (expression instanceof Variable) {
                        bb = expression instanceof LocalVariable ? MemoryOp.Base.DBase : MemoryOp.Base.VBase;
                        ((Variable) expression).setCalledBy(method);
                    }
                    source.add(new MemoryOp(context, ss, bb, MemoryOp.Op.Setup, indexNode != null, expression, 0));

                    if (bitfieldNode != null) {
                        source.add(new BitField(context, BitField.Op.Setup, bitfield));
                    }
                }
                else if ("BYTE".equalsIgnoreCase(childNode.getText()) || "WORD".equalsIgnoreCase(childNode.getText()) || "LONG".equalsIgnoreCase(childNode.getText())) {
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

                    source.addAll(compileBytecodeExpression(context, method, childNode.getChild(0), true));

                    if (indexNode != null) {
                        source.addAll(compileBytecodeExpression(context, method, indexNode, true));
                    }

                    int bitfield = -1;
                    if (bitfieldNode != null) {
                        bitfield = compileBitfield(context, method, bitfieldNode, source);
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
                    if (isPointer(expression)) {
                        throw new CompilerException("synax error", node.getToken());
                    }
                    if (expression == null) {
                        ar = childNode.getText().split("[\\.]");
                        expression = context.getLocalSymbol(ar[0]);
                    }
                    if (expression == null) {
                        throw new CompilerException("unsupported operation on " + childNode.getText(), childNode.getToken());
                    }
                    if (isStructure(context, expression)) {
                        source.addAll(compileStructure(context, method, node, expression, node, true, false));
                    }
                    else {
                        source.addAll(compileVariableSetup(context, method, expression, childNode));
                    }
                }
                if ("++".equalsIgnoreCase(node.getText())) {
                    source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_preinc_push : Spin2Bytecode.bc_var_inc, "PRE_INC" + (push ? " (push)" : "")));
                }
                else if ("--".equalsIgnoreCase(node.getText())) {
                    source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_predec_push : Spin2Bytecode.bc_var_dec, "PRE_DEC" + (push ? " (push)" : "")));
                }
                else if ("??".equalsIgnoreCase(node.getText())) {
                    source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_rnd_push : Spin2Bytecode.bc_var_rnd, "PRE_RND" + (push ? " (push)" : "")));
                }
                else if (!push) {
                    source.add(new Bytecode(context, Spin2Bytecode.bc_write_push, "WRITE"));
                }
                return source;
            }
            if ("BYTE".equalsIgnoreCase(node.getText()) || "WORD".equalsIgnoreCase(node.getText()) || "LONG".equalsIgnoreCase(node.getText())) {
                if (node.isMethod() && node.getChildCount() != 0 && !(node.getChild(0) instanceof Spin2StatementNode.Index)) {
                    byte[] code = compileTypes(context, node, node.getText());
                    if (code.length > 255) {
                        logMessage(new CompilerException(CompilerException.ERROR, "data cannot exceed 255 bytes", node.getTokens()));
                    }

                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    os.write(Spin2Bytecode.bc_string);
                    os.write(code.length);
                    os.writeBytes(code);
                    source.add(new Bytecode(context, os.toByteArray(), node.getText().toUpperCase() + "S"));

                    return source;
                }
            }
            if ("BYTE".equalsIgnoreCase(node.getText()) || "WORD".equalsIgnoreCase(node.getText()) || "LONG".equalsIgnoreCase(node.getText())
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

                if (node.isMethod()) {
                    if (node.getParent() != null && node.getParent().getText().startsWith("\\")) {
                        source.add(new Bytecode(context, push ? Spin2Bytecode.bc_drop_trap_push : Spin2Bytecode.bc_drop_trap, "ANCHOR_TRAP"));
                    }
                    else {
                        source.add(new Bytecode(context, push ? Spin2Bytecode.bc_drop_push : Spin2Bytecode.bc_drop, "ANCHOR"));
                    }

                    while (n < node.getChildCount()) {
                        if (!(node.getChild(n) instanceof Spin2StatementNode.Argument)) {
                            throw new CompilerException("syntax error", node.getChild(n));
                        }
                        source.addAll(compileConstantExpression(context, method, node.getChild(n++)));
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

                int bitfield = -1;
                if (bitfieldNode != null) {
                    bitfield = compileBitfield(context, method, bitfieldNode, source);
                }

                source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));

                if (indexNode != null) {
                    source.addAll(compileBytecodeExpression(context, method, indexNode, true));
                }

                MemoryOp.Size ss = MemoryOp.Size.Long;
                if ("BYTE".equalsIgnoreCase(node.getText()) || "@BYTE".equalsIgnoreCase(node.getText()) || "@@BYTE".equalsIgnoreCase(node.getText())) {
                    ss = MemoryOp.Size.Byte;
                }
                else if ("WORD".equalsIgnoreCase(node.getText()) || "@WORD".equalsIgnoreCase(node.getText()) || "@@WORD".equalsIgnoreCase(node.getText())) {
                    ss = MemoryOp.Size.Word;
                }

                if (node.isMethod()) {
                    if (ss != MemoryOp.Size.Long) {
                        throw new RuntimeException("method pointers must be long");
                    }
                    source.add(new MemoryOp(context, ss, MemoryOp.Base.Pop, MemoryOp.Op.Read, indexNode != null));
                    source.add(new Bytecode(context, new byte[] {
                        (byte) Spin2Bytecode.bc_call_ptr,
                    }, "CALL_PTR"));
                }
                else if (node.getText().startsWith("@@")) {
                    source.add(new MemoryOp(context, ss, MemoryOp.Base.Pop, MemoryOp.Op.Read, indexNode != null));
                    source.add(new Bytecode(context, Spin2Bytecode.bc_add_pbase, "ADD_PBASE"));
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
                return source;
            }
            if ("REG".equalsIgnoreCase(node.getText())) {
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
                    bitfield = compileBitfield(context, method, bitfieldNode, source);
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
                        source.addAll(compileBytecodeExpression(context, method, indexNode, true));
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
                return source;
            }
            if ("FIELD".equalsIgnoreCase(node.getText())) {
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

                source.addAll(compileConstantExpression(context, method, node.getChild(0)));

                if (indexNode != null) {
                    source.addAll(compileConstantExpression(context, method, indexNode));
                }

                if (postEffectNode == null) {
                    source.add(new Bytecode(context, new byte[] {
                        (byte) (indexNode == null ? Spin2Bytecode.bc_setup_field_p : Spin2Bytecode.bc_setup_field_pi),
                        (byte) (push ? Spin2Bytecode.bc_read : Spin2Bytecode.bc_write)
                    }, "FIELD_" + (push ? "READ" : "WRITE")));
                }
                else {
                    source.add(new Bytecode(context, new byte[] {
                        (byte) (indexNode == null ? Spin2Bytecode.bc_setup_field_p : Spin2Bytecode.bc_setup_field_pi)
                    }, "FIELD_SETUP"));
                }

                if (postEffectNode != null) {
                    compilePostEffect(context, postEffectNode, source, push);
                }

                return source;
            }
            if ("DEBUG".equalsIgnoreCase(node.getText())) {
                if (isDebugEnabled()) {
                    source.add(new Bytecode(context, Spin2Bytecode.bc_debug, "") {

                        @Override
                        public int getSize() {
                            return 3;
                        }

                        @Override
                        public byte[] getBytes() {
                            return new byte[] {
                                Spin2Bytecode.bc_debug, 0x00, 0x00
                            };
                        }

                        @Override
                        public String toString() {
                            return node.getText().toUpperCase() + " #" + 0;
                        }

                    });
                    return source;
                }
                return new ArrayList<Spin2Bytecode>();
            }
            throw new CompilerException("undefined symbol " + node.getText(), node.getToken());
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

    protected int getArgumentsCount(Context context, Spin2StatementNode childNode) {
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

    List<Spin2Bytecode> compileConstantExpression(Context context, Spin2Method method, Spin2StatementNode node) {
        try {
            Expression expression = buildConstantExpression(context, node);
            if (expression.isConstant()) {
                if (expression.isString()) {
                    int[] s = expression.getStringValues();
                    byte[] code = new byte[s.length + 3];
                    code[0] = (byte) Spin2Bytecode.bc_string;
                    code[1] = (byte) (s.length + 1);
                    for (int i = 0; i < s.length; i++) {
                        code[i + 2] = (byte) s[i];
                    }
                    return Collections.singletonList(new Bytecode(context, code, "STRING"));
                }
                return Collections.singletonList(new Constant(context, expression));
            }
        } catch (Exception e) {
            // Do nothing
        }
        return compileBytecodeExpression(context, method, node, true);
    }

    protected Expression buildConstantExpression(Context context, Spin2StatementNode node) {
        return buildConstantExpression(context, node, false);
    }

    protected Expression buildConstantExpression(Context context, Spin2StatementNode node, boolean registerConstant) {
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

        switch (nodeText.toUpperCase()) {
            case ">>":
                return new ShiftRight(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "<<":
                return new ShiftLeft(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "SAR":
                return new Sar(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "ROR":
                return new Ror(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "ROL":
                return new Rol(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "REV":
                return new Rev(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "ZEROX":
                return new Zerox(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "SIGNX":
                return new Signx(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));

            case "&":
                return new And(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "^":
                return new Xor(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "|":
                return new Or(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "!":
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("misplaced unary operator (" + node.getText() + ")");
                }
                return new Not(buildConstantExpression(context, node.getChild(0), registerConstant));

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
            case "..":
                return new AddpinsRange(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));

            case "&&":
            case "AND":
                return new LogicalAnd(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "||":
            case "OR":
                return new LogicalOr(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "^^":
            case "XOR":
                return new LogicalXor(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "!!":
            case "NOT":
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("misplaced unary operator (" + node.getText() + ")");
                }
                return new LogicalNot(buildConstantExpression(context, node.getChild(0), registerConstant));

            case "<":
            case "<.":
                return new LessThan(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "<=":
            case "<=.":
                return new LessOrEquals(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "==":
            case "==.":
                return new Equals(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "<>":
            case "<>.":
                return new NotEquals(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case ">=":
            case ">=.":
                return new GreaterOrEquals(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case ">":
            case ">.":
                return new GreaterThan(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));

            case "+<":
                return new LessThanUnsigned(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "+<=":
                return new LessOrEqualsUnsigned(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "+>=":
                return new GreaterOrEqualsUnsigned(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "+>":
                return new GreaterThanUnsigned(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));

            case "<=>":
                return new Compare(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));

            case "?": {
                Expression right = buildConstantExpression(context, node.getChild(1), registerConstant);
                if (!(right instanceof IfElse)) {
                    throw new RuntimeException("unsupported operator " + node.getText());
                }
                return new IfElse(buildConstantExpression(context, node.getChild(0), registerConstant), ((IfElse) right).getTrueTerm(), ((IfElse) right).getFalseTerm());
            }
            case ":":
                return new IfElse(null, buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));

            case "ENCOD":
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("misplaced unary operator (" + node.getText() + ")");
                }
                return new Encod(buildConstantExpression(context, node.getChild(0), registerConstant));
            case "DECOD":
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("misplaced unary operator (" + node.getText() + ")");
                }
                return new Decod(buildConstantExpression(context, node.getChild(0), registerConstant));

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
            case "SIZEOF":
                if (node.getChildCount() == 1) {
                    Expression exp = getSizeof(context, node.getChild(0).getText());
                    if (exp != null) {
                        return exp;
                    }
                }
                throw new RuntimeException("expected type or variable (" + node.getText() + ")");
        }

        throw new RuntimeException("unknown " + node.getText());
    }

    Expression getSizeof(Context context, String identifier) {
        Expression expression = context.getLocalSymbol(identifier);
        if (expression instanceof Variable) {
            Variable var = (Variable) expression;
            return new NumberLiteral(var.getTypeSize() * var.getSize());
        }
        else if (context.hasStructureDefinition(identifier)) {
            Spin2Struct struct = context.getStructureDefinition(identifier);
            return new NumberLiteral(getStructureSize(context, struct));
        }
        else if ("BYTE".equalsIgnoreCase(identifier)) {
            return new NumberLiteral(1);
        }
        else if ("WORD".equalsIgnoreCase(identifier)) {
            return new NumberLiteral(2);
        }
        else if ("LONG".equalsIgnoreCase(identifier)) {
            return new NumberLiteral(4);
        }
        else if ("^BYTE".equalsIgnoreCase(identifier) || "^WORD".equalsIgnoreCase(identifier) || "^LONG".equalsIgnoreCase(identifier)) {
            return new NumberLiteral(4);
        }
        return null;
    }

    protected List<Spin2Bytecode> leftAssign(Context context, Spin2Method method, Spin2StatementNode node, boolean push, boolean write) {
        Spin2StatementNode indexNode = null;
        Spin2StatementNode bitfieldNode = null;
        Spin2StatementNode postEffectNode = null;
        Spin2StatementNode pointerPostEffectNode = null;
        int index = 0;
        boolean hasIndex = false;
        boolean popIndex = false;
        List<Spin2Bytecode> source = new ArrayList<Spin2Bytecode>();

        if (node.getText().startsWith("^@")) {
            throw new CompilerException("syntax error", node.getToken());
        }

        String[] ar = node.getText().split("[\\.]");
        if (ar.length >= 2 && ("BYTE".equalsIgnoreCase(ar[ar.length - 1]) || "WORD".equalsIgnoreCase(ar[ar.length - 1]) || "LONG".equalsIgnoreCase(ar[ar.length - 1]))) {
            Expression expression = context.getLocalSymbol(ar[0]);
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
                bitfield = compileBitfield(context, method, bitfieldNode, source);
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
                    source.addAll(compileBytecodeExpression(context, method, indexNode, true));
                }
            }

            MemoryOp.Size ss = MemoryOp.Size.Long;
            if ("BYTE".equalsIgnoreCase(ar[1])) {
                ss = MemoryOp.Size.Byte;
            }
            else if ("WORD".equalsIgnoreCase(ar[1])) {
                ss = MemoryOp.Size.Word;
            }
            MemoryOp.Base bb = MemoryOp.Base.PBase;
            if (expression instanceof Variable) {
                bb = expression instanceof LocalVariable ? MemoryOp.Base.DBase : MemoryOp.Base.VBase;
                ((Variable) expression).setCalledBy(method);
            }

            MemoryOp.Op op = push || bitfieldNode != null ? MemoryOp.Op.Setup : MemoryOp.Op.Write;
            source.add(new MemoryOp(context, ss, bb, op, popIndex, expression, index));

            if (bitfieldNode != null) {
                source.add(new BitField(context, postEffectNode == null ? BitField.Op.Write : BitField.Op.Setup, bitfield));
            }

            if (postEffectNode != null) {
                if ("++".equalsIgnoreCase(postEffectNode.getText())) {
                    source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_postinc_push : Spin2Bytecode.bc_var_inc, "POST_INC" + (push ? " (push)" : "")));
                }
                else if ("--".equalsIgnoreCase(postEffectNode.getText())) {
                    source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_postdec_push : Spin2Bytecode.bc_var_dec, "POST_DEC" + (push ? " (push)" : "")));
                }
                else if ("!!".equalsIgnoreCase(postEffectNode.getText())) {
                    source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_lognot_push : Spin2Bytecode.bc_var_lognot, "POST_LOGICAL_NOT" + (push ? " (push)" : "")));
                }
                else if ("!".equalsIgnoreCase(postEffectNode.getText())) {
                    source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_bitnot_push : Spin2Bytecode.bc_var_bitnot, "POST_NOT" + (push ? " (push)" : "")));
                }
                else {
                    throw new CompilerException("unhandled post effect " + postEffectNode.getText(), postEffectNode.getToken());
                }
            }
        }
        else if ("_".equalsIgnoreCase(node.getText())) {
            source.add(new Bytecode(context, Spin2Bytecode.bc_pop, "POP"));
        }
        else if (",".equals(node.getText())) {
            for (int i = node.getChildCount() - 1; i >= 0; i--) {
                source.addAll(leftAssign(context, method, node.getChild(i), push, false));
            }
        }
        else if (node.getType() != 0 && node.getType() != Token.KEYWORD) {
            if (!node.toString().startsWith("[")) {
                throw new CompilerException("syntax error", node.getToken());
            }

            Expression expression = context.getLocalSymbol(node.getChild(0).getText());
            if (isPointer(expression)) {
                Variable var = (Variable) expression;

                MemoryOp.Size ss = MemoryOp.Size.Long;
                int typeSize = 4;
                if ("^BYTE".equalsIgnoreCase(var.getType())) {
                    ss = MemoryOp.Size.Byte;
                    typeSize = 1;
                }
                else if ("^WORD".equalsIgnoreCase(var.getType())) {
                    ss = MemoryOp.Size.Word;
                    typeSize = 2;
                }
                else if (!"^LONG".equalsIgnoreCase(var.getType())) {
                    Spin2Struct struct = context.getStructureDefinition(var.getType().substring(1));
                    if (struct == null) {
                        throw new CompilerException("syntax error", node.getTokens());
                    }
                    typeSize = getStructureSize(context, struct);
                }

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                try {
                    if (typeSize != 1) {
                        os.write(Spin2Bytecode.bc_set_incdec);
                        os.write(Constant.wrVar(typeSize));
                    }
                } catch (Exception e) {
                    // Do nothing
                }

                source.add(new VariableOp(context, VariableOp.Op.Setup, popIndex, var, hasIndex, index));
                if ("++".equalsIgnoreCase(node.getText())) {
                    os.write(Spin2Bytecode.bc_var_preinc_push);
                    source.add(new Bytecode(context, os.toByteArray(), "PRE_INC (push)"));
                }
                else if ("--".equalsIgnoreCase(node.getText())) {
                    os.write(Spin2Bytecode.bc_var_predec_push);
                    source.add(new Bytecode(context, os.toByteArray(), "PRE_DEC (push)"));
                }
                else {
                    throw new CompilerException("invalid post effect " + node.getText(), node.getTokens());
                }
                source.add(new MemoryOp(context, ss, MemoryOp.Base.Pop, MemoryOp.Op.Write, indexNode != null));

                var.setCalledBy(method);

            }
            else {
                throw new CompilerException("syntax error", node.getToken());
            }
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
                bitfield = compileBitfield(context, method, bitfieldNode, source);
            }

            source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));
            if (indexNode != null) {
                source.addAll(compileBytecodeExpression(context, method, indexNode, true));
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
            else if (write) {
                source.add(new Bytecode(context, Spin2Bytecode.bc_write_push, "WRITE"));
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
                bitfield = compileBitfield(context, method, bitfieldNode, source);
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
                    source.addAll(compileBytecodeExpression(context, method, indexNode, true));
                }
            }

            source.add(new RegisterOp(context, bitfieldNode != null || push ? RegisterOp.Op.Setup : RegisterOp.Op.Write, popIndex, expression, index));

            if (bitfieldNode != null) {
                source.add(new BitField(context, push && !write ? BitField.Op.Setup : BitField.Op.Write, push, bitfield));
            }
            else if (write) {
                source.add(new Bytecode(context, Spin2Bytecode.bc_write_push, "WRITE"));
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

            source.addAll(compileConstantExpression(context, method, node.getChild(0)));

            if (indexNode != null) {
                source.addAll(compileConstantExpression(context, method, indexNode));
            }

            if (push && !write) {
                source.add(new Bytecode(context, new byte[] {
                    (byte) (indexNode == null ? Spin2Bytecode.bc_setup_field_p : Spin2Bytecode.bc_setup_field_pi),
                }, "FIELD_SETUP"));
            }
            else {
                source.add(new Bytecode(context, new byte[] {
                    (byte) (indexNode == null ? Spin2Bytecode.bc_setup_field_p : Spin2Bytecode.bc_setup_field_pi),
                    (byte) (push ? Spin2Bytecode.bc_write_push : Spin2Bytecode.bc_write)
                }, push ? "FIELD_WRITE (push)" : "FIELD_WRITE"));
            }
        }
        else {
            Expression expression = context.getLocalSymbol(node.getText());
            if (expression == null && ar.length > 1) {
                expression = context.getLocalSymbol(ar[0]);
            }
            if (expression == null) {
                throw new CompilerException("undefined symbol " + node.getText(), node.getToken());
            }

            if (isStructure(context, expression)) {
                return compileStructure(context, method, node, expression, null, true, push);
            }
            else if (isPointer(expression)) {
                int n = 0;
                if (n < node.getChildCount() && (node.getChild(n) instanceof Spin2StatementNode.Index)) {
                    Spin2StatementNode child = node.getChild(n);
                    if (("++".equals(child.getText()) || "--".equals(child.getText())) && child.getChildCount() == 0) {
                        pointerPostEffectNode = node.getChild(n++);
                    }
                }
                if (n < node.getChildCount() && isPostEffect(node.getChild(n))) {
                    postEffectNode = node.getChild(n++);
                }
                if (n < node.getChildCount()) {
                    throw new CompilerException("unexpected: " + node.getChild(n).getText(), node.getChild(n).getTokens());
                }

                Variable var = (Variable) expression;

                if (node.toString().startsWith("[")) {
                    if (postEffectNode != null) {
                        source.add(new VariableOp(context, VariableOp.Op.Setup, popIndex, var, hasIndex, index));
                        compilePostEffect(context, postEffectNode, source, push);
                    }
                    else {
                        source.add(new VariableOp(context, VariableOp.Op.Write, popIndex, var, hasIndex, index));
                    }
                    if (pointerPostEffectNode != null) {
                        throw new CompilerException("unexpected: " + pointerPostEffectNode.getText(), pointerPostEffectNode.getTokens());
                    }
                }
                else {
                    MemoryOp.Size ss = MemoryOp.Size.Long;
                    int typeSize = 4;
                    if ("^BYTE".equalsIgnoreCase(var.getType())) {
                        ss = MemoryOp.Size.Byte;
                        typeSize = 1;
                    }
                    else if ("^WORD".equalsIgnoreCase(var.getType())) {
                        ss = MemoryOp.Size.Word;
                        typeSize = 2;
                    }
                    else if (!"^LONG".equalsIgnoreCase(var.getType())) {
                        Spin2Struct struct = context.getStructureDefinition(var.getType().substring(1));
                        if (struct == null) {
                            throw new CompilerException("syntax error", node.getTokens());
                        }
                        typeSize = getStructureSize(context, struct);
                    }
                    if (pointerPostEffectNode != null) {
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        try {
                            if (typeSize != 1) {
                                os.write(Spin2Bytecode.bc_set_incdec);
                                os.write(Constant.wrVar(typeSize));
                            }
                        } catch (Exception e) {
                            // Do nothing
                        }

                        source.add(new VariableOp(context, VariableOp.Op.Setup, popIndex, var, hasIndex, index));
                        if ("++".equalsIgnoreCase(pointerPostEffectNode.getText())) {
                            os.write(Spin2Bytecode.bc_var_postinc_push);
                            source.add(new Bytecode(context, os.toByteArray(), "POST_INC (push)"));
                        }
                        else if ("--".equalsIgnoreCase(pointerPostEffectNode.getText())) {
                            os.write(Spin2Bytecode.bc_var_postdec_push);
                            source.add(new Bytecode(context, os.toByteArray(), "POST_DEC (push)"));
                        }
                        else {
                            throw new CompilerException("invalid post effect " + pointerPostEffectNode.getText(), pointerPostEffectNode.getTokens());
                        }
                        source.add(new MemoryOp(context, ss, MemoryOp.Base.Pop, MemoryOp.Op.Write, indexNode != null));
                    }
                    else {
                        source.add(new VariableOp(context, VariableOp.Op.Read, popIndex, var, hasIndex, index));
                        if (postEffectNode != null) {
                            source.add(new MemoryOp(context, ss, MemoryOp.Base.Pop, MemoryOp.Op.Setup, indexNode != null));
                            compilePostEffect(context, postEffectNode, source, push);
                        }
                        else {
                            source.add(new MemoryOp(context, ss, MemoryOp.Base.Pop, push ? MemoryOp.Op.WritePush : MemoryOp.Op.Write, indexNode != null));
                        }
                    }
                }

                var.setCalledBy(method);
            }
            else {
                int n = 0;
                if (n < node.getChildCount() && (node.getChild(n) instanceof Spin2StatementNode.Index)) {
                    indexNode = node.getChild(n++);
                    popIndex = true;
                    try {
                        Expression exp = buildConstantExpression(context, indexNode);
                        if (exp.isConstant()) {
                            index = exp.getNumber().intValue();
                            if (isStructure(context, expression)) {
                                index = index * ((Variable) expression).getTypeSize();
                            }
                            hasIndex = true;
                            popIndex = false;
                        }
                    } catch (Exception e) {
                        // Do nothing
                    }
                }
                if (n < node.getChildCount()) {
                    if (".".equals(node.getChild(n).getText())) {
                        n++;
                        if (n >= node.getChildCount()) {
                            throw new CompilerException("expected bitfield expression", node.getChild(n));
                        }
                        if (!(node.getChild(n) instanceof Spin2StatementNode.Index)) {
                            throw new CompilerException("expected bitfield expression", node.getChild(n).getTokens());
                        }
                        bitfieldNode = node.getChild(n++);
                    }
                    else if (node.getChild(n).getText().startsWith(".")) {
                        ar = node.getChild(n).getText().split("[\\.]");
                        n++;
                    }
                }
                if (n < node.getChildCount()) {
                    throw new CompilerException("unexpected " + node.getChild(n).getText(), node.getChild(n).getToken());
                }

                int bitfield = -1;
                if (bitfieldNode != null) {
                    bitfield = compileBitfield(context, method, bitfieldNode, source);
                }

                if (popIndex) {
                    source.addAll(compileBytecodeExpression(context, method, indexNode, true));
                }

                if (expression instanceof Register) {
                    source.add(new RegisterOp(context, bitfieldNode != null || push ? RegisterOp.Op.Setup : RegisterOp.Op.Write, popIndex, expression, index));
                }
                else if (expression instanceof ContextLiteral) {
                    MemoryOp.Size ss = MemoryOp.Size.Long;
                    MemoryOp.Base bb = MemoryOp.Base.PBase;
                    if (expression instanceof Variable) {
                        bb = expression instanceof LocalVariable ? MemoryOp.Base.DBase : MemoryOp.Base.VBase;
                        ((Variable) expression).setCalledBy(method);
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
                    ((Variable) expression).setCalledBy(method);
                }

                if (bitfieldNode != null) {
                    source.add(new BitField(context, push && !write ? BitField.Op.Setup : BitField.Op.Write, push, bitfield));
                }
                else if (write) {
                    source.add(new Bytecode(context, Spin2Bytecode.bc_write_push, "WRITE"));
                }
            }
        }

        return source;
    }

    boolean isStructure(Context context, Expression expression) {
        if (expression instanceof Variable) {
            String varType = ((Variable) expression).getType();
            if (context.hasStructureDefinition(varType)) {
                return true;
            }
            if (varType.startsWith("^")) {
                if (context.hasStructureDefinition(varType.substring(1))) {
                    return true;
                }
            }
        }
        return false;
    }

    boolean isPointer(Expression expression) {
        if (expression instanceof Variable) {
            return ((Variable) expression).getType().startsWith("^");
        }
        return false;
    }

    Method getMethodExpression(Context context, Spin2StatementNode node) {
        Expression symbol = context.getLocalSymbol(node.getText());

        if (symbol instanceof SpinObject) {
            Spin2StatementNode methodNode = null;

            int n = 0;
            if (n < node.getChildCount()) {
                if (node.getChild(n) instanceof Spin2StatementNode.Index) {
                    node.getChild(n++);
                }
            }
            if (n < node.getChildCount()) {
                methodNode = node.getChild(n++);
            }
            if (n < node.getChildCount() || methodNode == null) {
                throw new CompilerException("syntax error", node.getTokens());
            }

            String qualifiedName = node.getText() + methodNode.getText();

            symbol = context.getLocalSymbol(qualifiedName);
            if (symbol == null && isAddress(qualifiedName)) {
                symbol = context.getLocalSymbol(qualifiedName.substring(1));
            }
            if (symbol == null) {
                throw new CompilerException("undefined symbol " + methodNode.getText(), methodNode.getToken());
            }
            if (!(symbol instanceof Method)) {
                throw new CompilerException(methodNode.getText() + " is not a method", methodNode.getToken());
            }
            return (Method) symbol;
        }
        if (symbol instanceof Method) {
            return (Method) symbol;
        }

        return null;
    }

    List<Spin2Bytecode> compileMethodCall(Context context, Spin2Method method, Expression symbol, Spin2StatementNode node, boolean push, boolean trap) {
        List<Spin2Bytecode> source = new ArrayList<Spin2Bytecode>();

        if (symbol instanceof SpinObject) {
            Spin2StatementNode indexNode = null;
            Spin2StatementNode methodNode = null;

            int n = 0;
            if (n < node.getChildCount()) {
                if (node.getChild(n) instanceof Spin2StatementNode.Index) {
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

            symbol = context.getLocalSymbol(qualifiedName);
            if (symbol == null && isAddress(qualifiedName)) {
                symbol = context.getLocalSymbol(qualifiedName.substring(1));
            }
            if (symbol == null) {
                throw new CompilerException("undefined symbol " + methodNode.getText(), methodNode.getToken());
            }
            if (!(symbol instanceof Method)) {
                throw new CompilerException(methodNode.getText() + " is not a method", methodNode.getToken());
            }
            Method methodExpression = (Method) symbol;
            Spin2Method calledMethod = (Spin2Method) methodExpression.getData(Spin2Method.class.getName());
            if (isAddress(node.getText())) {
                if (indexNode != null) {
                    source.addAll(compileConstantExpression(context, method, indexNode));
                }
                source.add(new SubAddress(context, methodExpression, indexNode != null));
                calledMethod.setCalledBy(method);
            }
            else {
                if (trap) {
                    source.add(new Bytecode(context, push ? Spin2Bytecode.bc_drop_trap_push : Spin2Bytecode.bc_drop_trap, "ANCHOR_TRAP"));
                }
                else {
                    source.add(new Bytecode(context, push ? Spin2Bytecode.bc_drop_push : Spin2Bytecode.bc_drop, "ANCHOR"));
                }

                source.addAll(compileMethodArguments(context, method, calledMethod, methodNode));
                if (indexNode != null) {
                    source.addAll(compileConstantExpression(context, method, indexNode));
                }
                source.add(new CallSub(context, methodExpression, indexNode != null));
                calledMethod.setCalledBy(method);

                if (push && methodExpression.getReturnsCount() == 0) {
                    throw new RuntimeException("method doesn't return any value");
                }
            }
        }
        else {
            if (trap) {
                source.add(new Bytecode(context, push ? Spin2Bytecode.bc_drop_trap_push : Spin2Bytecode.bc_drop_trap, "ANCHOR_TRAP"));
            }
            else {
                source.add(new Bytecode(context, push ? Spin2Bytecode.bc_drop_push : Spin2Bytecode.bc_drop, "ANCHOR"));
            }

            if (symbol instanceof Method) {
                Method methodExpression = (Method) symbol;
                Spin2Method calledMethod = (Spin2Method) methodExpression.getData(Spin2Method.class.getName());

                source.addAll(compileMethodArguments(context, method, calledMethod, node));
                source.add(new CallSub(context, methodExpression));
                calledMethod.setCalledBy(method);

                if (push && !trap && calledMethod.getReturnsCount() == 0) {
                    throw new RuntimeException("method doesn't return any value");
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
                        throw new CompilerException("syntax error", node.getChild(i).getTokens());
                    }
                    source.addAll(compileConstantExpression(context, method, node.getChild(i++)));
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
                        source.addAll(compileBytecodeExpression(context, method, indexNode, true));
                    }
                }

                if (symbol instanceof Variable) {
                    switch (((Variable) symbol).getType()) {
                        case "BYTE":
                        case "WORD":
                            throw new RuntimeException("method pointers must be long");
                    }
                    source.add(new VariableOp(context, VariableOp.Op.Read, popIndex, (Variable) symbol, hasIndex, index));
                    ((Variable) symbol).setCalledBy(method);
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
                    (byte) Spin2Bytecode.bc_call_ptr,
                }, "CALL_PTR"));
            }
        }

        return source;
    }

    List<Spin2Bytecode> compileMethodArguments(Context context, Spin2Method method, Spin2Method calledMethod, Spin2StatementNode argsNode) {
        List<Spin2Bytecode> source = new ArrayList<Spin2Bytecode>();

        int actual = 0;
        for (int i = 0; i < argsNode.getChildCount(); i++) {
            source.addAll(compileConstantExpression(context, method, argsNode.getChild(i)));
            Expression child = context.getLocalSymbol(argsNode.getChild(i).getText());
            if (child != null && (child instanceof Method) && !argsNode.getChild(i).getText().startsWith("@")) {
                actual += ((Method) child).getReturnsCount();
                continue;
            }
            Spin2Bytecode.Descriptor descriptor = Spin2Bytecode.getDescriptor(argsNode.getChild(i).getText().toUpperCase());
            if (descriptor != null) {
                actual += descriptor.getReturns();
                continue;
            }
            actual++;
        }
        while (actual < calledMethod.getParametersCount()) {
            Expression value = calledMethod.getParameters().get(actual).getValue();
            if (value == null) {
                break;
            }
            source.add(new Constant(context, value));
            actual++;
        }

        if (actual != calledMethod.getParametersCount()) {
            logMessage(new CompilerException("expected " + calledMethod.getParametersCount() + " argument(s), found " + actual, argsNode.getTokens()));
        }

        return source;
    }

    int compileBitfield(Context context, Spin2Method method, Spin2StatementNode node, List<Spin2Bytecode> source) {
        int bitfield = -1;

        if ("..".equals(node.getText())) {
            if (node.getChildCount() != 2) {
                throw new RuntimeException("syntax error");
            }
            try {
                Expression exp1 = buildConstantExpression(context, node.getChild(0));
                Expression exp2 = buildConstantExpression(context, node.getChild(1));
                if (exp1.isConstant() && exp2.isConstant()) {
                    int arg0 = exp1.getNumber().intValue();
                    int arg1 = exp2.getNumber().intValue();
                    bitfield = ((arg0 - arg1) & 0x1F) << 5 | arg1;
                }
            } catch (Exception e) {
                // Do nothing
            }

            if (bitfield == -1) {
                source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));
                source.addAll(compileBytecodeExpression(context, method, node.getChild(1), true));
                source.add(new Bytecode(context, new byte[] {
                    (byte) Spin2Bytecode.bc_bitrange, (byte) Spin2Bytecode.bc_addbits
                }, "BITRANGE"));
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
                source.addAll(compileBytecodeExpression(context, method, node, true));
            }
        }

        return bitfield;
    }

    List<Spin2Bytecode> compileVariableSetup(Context context, Spin2Method method, Expression expression, Spin2StatementNode node) {
        Spin2StatementNode indexNode = null;
        Spin2StatementNode bitfieldNode = null;
        List<Spin2Bytecode> source = new ArrayList<Spin2Bytecode>();

        int index = 0;
        boolean hasIndex = false;
        boolean popIndex = false;

        if (isPointer(expression)) {

        }
        else {
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
                int bitfield = compileBitfield(context, method, bitfieldNode, source);

                if (popIndex) {
                    source.addAll(compileBytecodeExpression(context, method, indexNode, true));
                }

                if (expression instanceof Register) {
                    source.add(new RegisterOp(context, RegisterOp.Op.Setup, popIndex, expression, index));
                }
                else if (expression instanceof ContextLiteral) {
                    source.add(new MemoryOp(context, ss, bb, MemoryOp.Op.Setup, popIndex, expression, index));
                }
                else {
                    source.add(new VariableOp(context, VariableOp.Op.Setup, popIndex, (Variable) expression, hasIndex, index));
                    ((Variable) expression).setCalledBy(method);
                }

                source.add(new BitField(context, BitField.Op.Setup, bitfield));
            }
            else {
                if (popIndex) {
                    source.addAll(compileBytecodeExpression(context, method, indexNode, true));
                }
                if (expression instanceof Register) {
                    source.add(new RegisterOp(context, RegisterOp.Op.Setup, popIndex, expression, index));
                }
                else if (expression instanceof ContextLiteral) {
                    source.add(new MemoryOp(context, ss, bb, MemoryOp.Op.Setup, popIndex, expression, index));
                }
                else {
                    source.add(new VariableOp(context, VariableOp.Op.Setup, popIndex, (Variable) expression, hasIndex, index));
                    ((Variable) expression).setCalledBy(method);
                }
            }
        }

        return source;
    }

    List<Spin2Bytecode> compileVariableRead(Context context, Spin2Method method, Expression expression, Spin2StatementNode node, boolean push) {
        Spin2StatementNode indexNode = null;
        Spin2StatementNode bitfieldNode = null;
        Spin2StatementNode postEffectNode = null;
        Spin2StatementNode pointerPostEffectNode = null;
        List<Spin2Bytecode> source = new ArrayList<Spin2Bytecode>();

        int index = 0;
        boolean hasIndex = false;
        boolean popIndex = false;
        boolean field = node.getText().startsWith("^@");

        if (isStructure(context, expression)) {
            return compileStructure(context, method, node, expression, null, false, push);
        }
        else if (isPointer(expression)) {
            int n = 0;
            if (n < node.getChildCount() && (node.getChild(n) instanceof Spin2StatementNode.Index)) {
                Spin2StatementNode child = node.getChild(n);
                if (("++".equals(child.getText()) || "--".equals(child.getText())) && child.getChildCount() == 0) {
                    pointerPostEffectNode = node.getChild(n++);
                }
            }
            if (n < node.getChildCount() && isPostEffect(node.getChild(n))) {
                postEffectNode = node.getChild(n++);
            }
            if (n < node.getChildCount()) {
                throw new CompilerException("unexpected: " + node.getChild(n).getText(), node.getChild(n).getTokens());
            }

            Variable var = (Variable) expression;

            if (node.toString().startsWith("[")) {
                if (postEffectNode != null) {
                    source.add(new VariableOp(context, VariableOp.Op.Setup, popIndex, var, hasIndex, index));
                    compilePostEffect(context, postEffectNode, source, push);
                }
                else {
                    source.add(new VariableOp(context, VariableOp.Op.Read, popIndex, var, hasIndex, index));
                }
                if (pointerPostEffectNode != null) {
                    throw new CompilerException("unexpected: " + pointerPostEffectNode.getText(), pointerPostEffectNode.getTokens());
                }
            }
            else {
                MemoryOp.Size ss = MemoryOp.Size.Long;
                int typeSize = 4;
                if ("^BYTE".equalsIgnoreCase(var.getType())) {
                    ss = MemoryOp.Size.Byte;
                    typeSize = 1;
                }
                else if ("^WORD".equalsIgnoreCase(var.getType())) {
                    ss = MemoryOp.Size.Word;
                    typeSize = 2;
                }
                else if (!"^LONG".equalsIgnoreCase(var.getType())) {
                    Spin2Struct struct = context.getStructureDefinition(var.getType().substring(1));
                    if (struct == null) {
                        throw new CompilerException("syntax error", node.getTokens());
                    }
                    typeSize = getStructureSize(context, struct);
                }
                if (pointerPostEffectNode != null) {
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    try {
                        if (typeSize != 1) {
                            os.write(Spin2Bytecode.bc_set_incdec);
                            os.write(Constant.wrVar(typeSize));
                        }
                    } catch (Exception e) {
                        // Do nothing
                    }

                    source.add(new VariableOp(context, VariableOp.Op.Setup, popIndex, var, hasIndex, index));
                    if ("++".equalsIgnoreCase(pointerPostEffectNode.getText())) {
                        os.write(Spin2Bytecode.bc_var_postinc_push);
                        source.add(new Bytecode(context, os.toByteArray(), "POST_INC (push)"));
                    }
                    else if ("--".equalsIgnoreCase(pointerPostEffectNode.getText())) {
                        os.write(Spin2Bytecode.bc_var_postdec_push);
                        source.add(new Bytecode(context, os.toByteArray(), "POST_DEC (push)"));
                    }
                    else {
                        throw new CompilerException("invalid post effect " + pointerPostEffectNode.getText(), pointerPostEffectNode.getTokens());
                    }
                    source.add(new MemoryOp(context, ss, MemoryOp.Base.Pop, MemoryOp.Op.Read, indexNode != null));
                }
                else {
                    source.add(new VariableOp(context, VariableOp.Op.Read, popIndex, var, hasIndex, index));
                    if (postEffectNode != null) {
                        source.add(new MemoryOp(context, ss, MemoryOp.Base.Pop, MemoryOp.Op.Setup, indexNode != null));
                        compilePostEffect(context, postEffectNode, source, push);
                    }
                    else {
                        source.add(new MemoryOp(context, ss, MemoryOp.Base.Pop, MemoryOp.Op.Read, indexNode != null));
                    }
                }
            }

            var.setCalledBy(method);
        }
        else {
            int n = 0;
            if (n < node.getChildCount() && (node.getChild(n) instanceof Spin2StatementNode.Index)) {
                indexNode = node.getChild(n++);
            }
            if (n < node.getChildCount() && ".".equals(node.getChild(n).getText())) {
                n++;
                if (n >= node.getChildCount()) {
                    throw new CompilerException("expected bitfield expression", node.getChild(n - 1).getTokens());
                }
                if (!(node.getChild(n) instanceof Spin2StatementNode.Index)) {
                    throw new CompilerException("invalid bitfield expression", node.getChild(n).getTokens());
                }
                bitfieldNode = node.getChild(n++);
            }
            if (n < node.getChildCount() && isPostEffect(node.getChild(n))) {
                postEffectNode = node.getChild(n++);
            }
            if (n < node.getChildCount()) {
                throw new CompilerException("unexpected " + node.getChild(n).getText(), node.getChild(n).getToken());
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
            }

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

            if (bitfieldNode != null) {
                int bitfield = compileBitfield(context, method, bitfieldNode, source);

                if (popIndex) {
                    source.addAll(compileBytecodeExpression(context, method, indexNode, true));
                }

                if (expression instanceof Register) {
                    source.add(new RegisterOp(context, RegisterOp.Op.Setup, popIndex, expression, index));
                }
                else if (expression instanceof ContextLiteral) {
                    source.add(new MemoryOp(context, ss, bb, MemoryOp.Op.Setup, popIndex, expression, index));
                }
                else {
                    source.add(new VariableOp(context, VariableOp.Op.Setup, popIndex, (Variable) expression, hasIndex, index));
                    ((Variable) expression).setCalledBy(method);
                }

                BitField.Op op = field ? BitField.Op.Field : (postEffectNode == null ? BitField.Op.Read : BitField.Op.Setup);
                source.add(new BitField(context, op, bitfield));

                if (postEffectNode != null) {
                    compilePostEffect(context, postEffectNode, source, push);
                }
            }
            else {
                if (postEffectNode != null && ("~".equalsIgnoreCase(postEffectNode.getText()) || "~~".equalsIgnoreCase(postEffectNode.getText()))) {
                    if ("~".equalsIgnoreCase(postEffectNode.getText())) {
                        source.add(new Constant(context, new NumberLiteral(0)));
                    }
                    else {
                        source.add(new Constant(context, new NumberLiteral(-1)));
                    }
                }
                if (popIndex) {
                    source.addAll(compileBytecodeExpression(context, method, indexNode, true));
                }
                if (postEffectNode != null) {
                    if ("~".equalsIgnoreCase(postEffectNode.getText()) || "~~".equalsIgnoreCase(postEffectNode.getText())) {
                        if (expression instanceof Register) {
                            source.add(new RegisterOp(context, push ? RegisterOp.Op.Setup : RegisterOp.Op.Write, popIndex, expression, index));
                        }
                        else if (expression instanceof ContextLiteral) {
                            source.add(new MemoryOp(context, ss, bb, push ? MemoryOp.Op.Setup : MemoryOp.Op.Write, popIndex, expression, index));
                        }
                        else {
                            source.add(new VariableOp(context, push ? VariableOp.Op.Setup : VariableOp.Op.Write, popIndex, (Variable) expression, hasIndex, index));
                            ((Variable) expression).setCalledBy(method);
                        }
                        if (push) {
                            source.add(new Bytecode(context, Spin2Bytecode.bc_var_swap, "SWAP"));
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
                            ((Variable) expression).setCalledBy(method);
                        }
                        if ("++".equalsIgnoreCase(postEffectNode.getText())) {
                            source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_postinc_push : Spin2Bytecode.bc_var_inc, "POST_INC" + (push ? " (push)" : "")));
                        }
                        else if ("--".equalsIgnoreCase(postEffectNode.getText())) {
                            source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_postdec_push : Spin2Bytecode.bc_var_dec, "POST_DEC" + (push ? " (push)" : "")));
                        }
                        else if ("!!".equalsIgnoreCase(postEffectNode.getText())) {
                            source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_lognot_push : Spin2Bytecode.bc_var_lognot, "POST_LOGICAL_NOT" + (push ? " (push)" : "")));
                        }
                        else if ("!".equalsIgnoreCase(postEffectNode.getText())) {
                            source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_bitnot_push : Spin2Bytecode.bc_var_bitnot, "POST_NOT" + (push ? " (push)" : "")));
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
                        ((Variable) expression).setCalledBy(method);
                    }
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

    void compilePostEffect(Context context, Spin2StatementNode node, List<Spin2Bytecode> source, boolean push) {
        if ("~".equalsIgnoreCase(node.getText())) {
            source.add(0, new Constant(context, new NumberLiteral(0)));
            source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_swap : Spin2Bytecode.bc_write, push ? "SWAP" : "WRITE"));
        }
        else if ("~~".equalsIgnoreCase(node.getText())) {
            source.add(0, new Constant(context, new NumberLiteral(-1)));
            source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_swap : Spin2Bytecode.bc_write, push ? "SWAP" : "WRITE"));
        }
        else if ("++".equalsIgnoreCase(node.getText())) {
            source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_postinc_push : Spin2Bytecode.bc_var_inc, "POST_INC" + (push ? " (push)" : "")));
        }
        else if ("--".equalsIgnoreCase(node.getText())) {
            source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_postdec_push : Spin2Bytecode.bc_var_dec, "POST_DEC" + (push ? " (push)" : "")));
        }
        else if ("!!".equalsIgnoreCase(node.getText())) {
            source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_lognot_push : Spin2Bytecode.bc_var_lognot, "POST_LOGICAL_NOT" + (push ? " (push)" : "")));
        }
        else if ("!".equalsIgnoreCase(node.getText())) {
            source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_bitnot_push : Spin2Bytecode.bc_var_bitnot, "POST_NOT" + (push ? " (push)" : "")));
        }
        else {
            throw new CompilerException("unhandled post effect " + node.getText(), node.getToken());
        }
    }

    byte[] compileTypes(Context context, Spin2StatementNode node, String type) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        Iterator<Spin2StatementNode> iter = node.getChilds().iterator();
        while (iter.hasNext()) {
            Spin2StatementNode child = iter.next();

            if (child.isMethod()) {
                if ("BYTE".equalsIgnoreCase(child.getText()) || "WORD".equalsIgnoreCase(child.getText()) || "LONG".equalsIgnoreCase(child.getText())) {
                    byte[] code = compileTypes(context, child, child.getText().substring(0, child.getText().length() - 1));
                    os.write(code);
                }
                else {
                    throw new CompilerException("expression is not constant", child.getTokens());
                }
            }
            else {
                String overrideType = type;
                if ("BYTE".equalsIgnoreCase(child.getText()) || "WORD".equalsIgnoreCase(child.getText()) || "LONG".equalsIgnoreCase(child.getText())) {
                    overrideType = child.getText();
                    if (child.getChildCount() != 1) {
                        throw new CompilerException("syntax error", child.getToken());
                    }
                    child = child.getChild(0);
                }

                if (child.getType() == Token.STRING) {
                    String s = child.getText().substring(1);
                    byte[] b = s.substring(0, s.length() - 1).getBytes();
                    for (int i = 0; i < b.length; i++) {
                        os.write(b[i]);
                        if ("WORD".equalsIgnoreCase(overrideType)) {
                            os.write(0x00);
                        }
                        else if ("LONG".equalsIgnoreCase(overrideType)) {
                            os.write(0x00);
                            os.write(0x00);
                            os.write(0x00);
                        }
                    }
                }
                else {
                    Expression expression;
                    if (child.getType() == Token.NUMBER) {
                        expression = new NumberLiteral(child.getText());
                    }
                    else {
                        try {
                            expression = buildConstantExpression(context, child);
                            if (!expression.isConstant()) {
                                throw new CompilerException("expression is not constant", child.getTokens());
                            }
                        } catch (CompilerException e) {
                            throw e;
                        } catch (Exception e) {
                            throw new CompilerException("expression is not constant", child.getTokens());
                        }
                    }
                    if ("BYTE".equalsIgnoreCase(overrideType)) {
                        if (expression.getNumber().intValue() < -0x80 || expression.getNumber().intValue() > 0xFF) {
                            logMessage(new CompilerException(CompilerException.WARNING, "byte value range from -$80 to $FF", child.getTokens()));
                        }
                        os.write(expression.getByte());
                    }
                    else if ("WORD".equalsIgnoreCase(overrideType)) {
                        if (expression.getNumber().intValue() < -0x8000 || expression.getNumber().intValue() > 0xFFFF) {
                            logMessage(new CompilerException(CompilerException.WARNING, "word value range from -$8000 to $FFFF", child.getTokens()));
                        }
                        os.write(expression.getWord());
                    }
                    else if ("LONG".equalsIgnoreCase(overrideType)) {
                        os.write(expression.getLong());
                    }
                }
            }
        }

        return os.toByteArray();
    }

    int getStructureSize(Context context, Spin2Struct struct) {
        int size = 0;

        for (Spin2StructMember member : struct.getMembers()) {
            int memberSize = 1;
            if (member.getSize() != null) {
                memberSize = member.getSize().getNumber().intValue();
            }

            String memberType = "LONG";
            if (member.getType() != null) {
                memberType = member.getType().getText();
            }
            if ("LONG".equalsIgnoreCase(memberType)) {
                size += 4 * memberSize;
            }
            else if ("WORD".equalsIgnoreCase(memberType)) {
                size += 2 * memberSize;
            }
            else if ("BYTE".equalsIgnoreCase(memberType)) {
                size += 1 * memberSize;
            }
            else {
                Spin2Struct memberStruct = context.getStructureDefinition(memberType);
                if (memberStruct != null) {
                    size += getStructureSize(context, memberStruct) * memberSize;
                }
            }
        }

        return size;
    }

    List<Spin2Bytecode> compileStructure(Context context, Spin2Method method, Spin2StatementNode node, Expression expression, Spin2StatementNode preEffectNode, boolean write, boolean push) {
        int offset = 0;
        int index = 0;

        List<Spin2StatementNode> indexNodes = new ArrayList<>();
        List<Integer> indexMultipliers = new ArrayList<>();
        Spin2StatementNode postEffectNode = null;

        List<Spin2Bytecode> source = new ArrayList<>();

        MemoryOp.Op op;
        if (isAddress(node.getText())) {
            op = MemoryOp.Op.Address;
        }
        else if (preEffectNode != null) {
            op = MemoryOp.Op.Setup;
        }
        else {
            op = write ? MemoryOp.Op.Write : MemoryOp.Op.Read;
        }

        Variable baseVariable = (Variable) expression;
        MemoryOp.Base bb = baseVariable instanceof LocalVariable ? MemoryOp.Base.DBase : MemoryOp.Base.VBase;
        if (baseVariable.isPointer()) {
            bb = MemoryOp.Base.Pop;
        }

        String varType = ((Variable) expression).getType();
        Spin2Struct struct = null;

        int n = 0;
        while (true) {
            struct = context.getStructureDefinition(varType);
            if (struct == null && varType.startsWith("^")) {
                struct = context.getStructureDefinition(varType.substring(1));
            }

            String[] ar = node.getText().split("[\\.]");
            for (int i = 1; i < ar.length; i++) {
                Spin2StructMember member = struct.getMember(ar[i]);
                if (member == null) {
                    throw new CompilerException("undefined symbol " + node.getText(), node.getToken());
                }

                offset += member.getOffset();

                varType = member.getType() != null ? member.getType().getText() : "LONG";
                struct = context.getStructureDefinition(varType);
                if (struct == null && varType.startsWith("^")) {
                    struct = context.getStructureDefinition(varType.substring(1));
                }
            }

            if (n < node.getChildCount() && (node.getChild(n) instanceof Spin2StatementNode.Index)) {
                boolean constantIndex = false;
                Spin2StatementNode indexNode = node.getChild(n++);
                try {
                    Expression exp = buildConstantExpression(context, indexNode);
                    if (exp.isConstant()) {
                        index += exp.getNumber().intValue() * struct.getTypeSize();
                        constantIndex = true;
                    }
                } catch (Exception e) {
                    // Do nothing
                }
                if (!constantIndex) {
                    indexNodes.add(indexNode);
                    indexMultipliers.add(0, struct.getTypeSize());
                }
            }

            if (n >= node.getChildCount()) {
                break;
            }
            if (isPostEffect(node.getChild(n))) {
                break;
            }

            if (struct == null) {
                break;
            }

            node = node.getChild(n);
            n = 0;
        }
        if (preEffectNode == null) {
            if (n < node.getChildCount() && isPostEffect(node.getChild(n))) {
                postEffectNode = node.getChild(n++);
            }
        }
        if (n < node.getChildCount()) {
            throw new CompilerException("unexpected '" + node.getChild(n).getText() + "'", node.getChild(n).getTokens());
        }

        if (preEffectNode != null && struct != null) {
            throw new CompilerException("syntax error", node.getTokens());
        }
        /*
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                try {
                    if (typeSize != 1) {
                        os.write(Spin2Bytecode.bc_set_incdec);
                        os.write(Constant.wrVar(typeSize));
                    }
                } catch (Exception e) {
                    // Do nothing
                }

                source.add(new VariableOp(context, VariableOp.Op.Setup, popIndex, var, hasIndex, index));
                if ("++".equalsIgnoreCase(node.getText())) {
                    os.write(Spin2Bytecode.bc_var_preinc_push);
                    source.add(new Bytecode(context, os.toByteArray(), "PRE_INC (push)"));
                }
                else if ("--".equalsIgnoreCase(node.getText())) {
                    os.write(Spin2Bytecode.bc_var_predec_push);
                    source.add(new Bytecode(context, os.toByteArray(), "PRE_DEC (push)"));
                }
                else {
                    throw new CompilerException("invalid post effect " + node.getText(), node.getTokens());
                }

         */
        if (node.toString().startsWith("[")) {
            source.add(new VariableOp(context, postEffectNode != null ? VariableOp.Op.Setup : VariableOp.Op.Write, false, (Variable) expression, false, 0));
            if (postEffectNode != null) {
                int structSize = struct.getTypeSize();

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                try {
                    if ("++".equalsIgnoreCase(postEffectNode.getText()) || "--".equalsIgnoreCase(postEffectNode.getText())) {
                        os.write(Spin2Bytecode.bc_set_incdec);
                        os.write(Constant.wrVar(structSize));
                        if ("++".equalsIgnoreCase(postEffectNode.getText())) {
                            os.write(Spin2Bytecode.bc_var_inc);
                            source.add(new Bytecode(context, os.toByteArray(), "POST_INC"));
                        }
                        else if ("--".equalsIgnoreCase(postEffectNode.getText())) {
                            os.write(Spin2Bytecode.bc_var_dec);
                            source.add(new Bytecode(context, os.toByteArray(), "POST_DEC"));
                        }
                    }
                    else {
                        os.write(Spin2Bytecode.bc_drop);
                        if ("~".equalsIgnoreCase(postEffectNode.getText())) {
                            os.write(Spin2Bytecode.bc_con_n + 1);
                        }
                        else if ("~~".equalsIgnoreCase(postEffectNode.getText())) {
                            os.write(Spin2Bytecode.bc_con_n);
                        }
                        else {
                            logMessage(new CompilerException("invalid post effect '" + postEffectNode.getText() + "'", postEffectNode.getToken()));
                        }
                        os.write(Constant.wrAuto(structSize));
                        os.write(Spin2Bytecode.bc_hub_bytecode);
                        os.write(Spin2Bytecode.bc_bytefill);
                        source.add(new Bytecode(context, os.toByteArray(), "BYTEFILL"));
                    }
                } catch (Exception e) {
                    // Do nothing
                }
            }
            return source;
        }

        MemoryOp.Size ss = MemoryOp.Size.Long;
        switch (varType.toUpperCase()) {
            case "BYTE":
                ss = MemoryOp.Size.Byte;
                break;
            case "WORD":
                ss = MemoryOp.Size.Word;
                break;
        }

        if (indexNodes.size() > 3) {
            throw new CompilerException("too many nested indexes", node.getTokens());
        }

        if (indexNodes.size() > 0 || postEffectNode != null || op == Op.Address || baseVariable.isPointer()) {
            for (Spin2StatementNode indexNode : indexNodes) {
                source.addAll(compileBytecodeExpression(context, method, indexNode, true));
            }

            if (baseVariable.isPointer()) {
                source.add(new VariableOp(context, VariableOp.Op.Read, false, baseVariable, false, 0));
            }

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                int packedOffset;
                if (baseVariable.isPointer()) {
                    packedOffset = ((index + offset) << 4) | indexNodes.size();
                    os.write(Spin2Bytecode.bc_setup_struct_pop);
                }
                else {
                    os.write(bb == MemoryOp.Base.DBase ? Spin2Bytecode.bc_setup_struct_dbase : Spin2Bytecode.bc_setup_struct_vbase);
                    packedOffset = ((baseVariable.getOffset() + (index + offset)) << 4) | indexNodes.size();
                }
                if (struct == null) {
                    packedOffset |= ((ss.ordinal() + 1) << 2);
                }
                os.write(Constant.wrVar(packedOffset));

                for (Integer multiplier : indexMultipliers) {
                    os.write(Constant.wrVar(multiplier.intValue()));
                }
                if (op == Op.Address) {
                    os.write(Constant.wrVar(0));
                }

                if (postEffectNode == null) {
                    if (op == Op.Read) {
                        os.write(Spin2Bytecode.bc_read);
                    }
                    else if (op == Op.Write) {
                        os.write(push ? Spin2Bytecode.bc_write_push : Spin2Bytecode.bc_write);
                    }
                }

                StringBuilder sb = new StringBuilder("STRUCT_");
                if (postEffectNode == null) {
                    if (op == Op.Read) {
                        sb.append("READ");
                    }
                    else if (op == Op.Write) {
                        sb.append("WRITE");
                    }
                    else if (op == Op.Setup) {
                        sb.append("SETUP");
                    }
                    else if (op == Op.Address) {
                        sb.append("ADDRESS");
                    }
                }
                else {
                    sb.append("SETUP");
                }
                switch (ss) {
                    case Byte:
                        sb.append(" BYTE");
                        break;
                    case Word:
                        sb.append(" WORD");
                        break;
                    case Long:
                        sb.append(" LONG");
                        break;
                }
                switch (bb) {
                    case PBase:
                        sb.append(" PBASE");
                        break;
                    case VBase:
                        sb.append(" VBASE");
                        break;
                    case DBase:
                        sb.append(" DBASE");
                        break;
                    case Pop:
                        sb.append(" POP");
                        break;
                }
                sb.append(String.format("+$%05X (indexed)", packedOffset >> 4));
                if (op == Op.Write && push) {
                    sb.append(" (push)");
                }

                source.add(new Bytecode(context, os.toByteArray(), sb.toString()));

            } catch (Exception e) {
                // Do nothing
            }
        }
        else {
            source.add(new MemoryOp(context, ss, bb, postEffectNode != null ? MemoryOp.Op.Setup : op, baseVariable, index + offset));
        }

        if (postEffectNode != null) {
            if (struct != null) {
                int structSize = struct.getTypeSize();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                try {
                    os.write(Spin2Bytecode.bc_drop);
                    if ("~".equalsIgnoreCase(postEffectNode.getText())) {
                        os.write(Spin2Bytecode.bc_con_n + 1);
                    }
                    else if ("~~".equalsIgnoreCase(postEffectNode.getText())) {
                        os.write(Spin2Bytecode.bc_con_n);
                    }
                    else {
                        logMessage(new CompilerException("invalid post effect '" + postEffectNode.getText() + "'", postEffectNode.getToken()));
                    }
                    os.write(Constant.wrAuto(structSize));
                    os.write(Spin2Bytecode.bc_hub_bytecode);
                    os.write(Spin2Bytecode.bc_bytefill);
                } catch (Exception e) {

                }
                source.add(new Bytecode(context, os.toByteArray(), "BYTEFILL"));
            }
            else {
                compilePostEffect(context, postEffectNode, source, push);
            }
        }

        baseVariable.setCalledBy(method);

        return source;
    }

}
