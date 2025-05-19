/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
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
import com.maccasoft.propeller.expressions.Exp;
import com.maccasoft.propeller.expressions.Exp10;
import com.maccasoft.propeller.expressions.Exp2;
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
import com.maccasoft.propeller.expressions.Log;
import com.maccasoft.propeller.expressions.Log10;
import com.maccasoft.propeller.expressions.Log2;
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
import com.maccasoft.propeller.expressions.Pow;
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
import com.maccasoft.propeller.spin2.bytecode.StructOp;
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
                if (!push) {
                    logMessage(new CompilerException("expected assignment", node.getTokens()));
                }
                node.setReturnLongs(1);
                return source;
            }
            if (node.getType() == Token.STRING) {
                source.addAll(compileString(context, node));
                if (!push) {
                    logMessage(new CompilerException("expected assignment", node.getTokens()));
                }
                node.setReturnLongs(1);
                return source;
            }

            if (node.isMethod()) {
                if ("RECV".equalsIgnoreCase(node.getText())) {
                    if (node.getChildCount() != 0) {
                        logMessage(new CompilerException("expected " + 0 + " argument(s), found " + node.getChildCount(), node.getTokens()));
                    }
                    source.add(new Bytecode(context, Spin2Bytecode.bc_call_recv, node.getText().toUpperCase()));
                    if (!push) {
                        logMessage(new CompilerException("expected assignment", node.getTokens()));
                    }
                    node.setReturnLongs(1);
                    return source;
                }

                if ("SEND".equalsIgnoreCase(node.getText())) {
                    if (node.getChildCount() == 0) {
                        logMessage(new CompilerException("expected argument(s)", node.getTokens()));
                    }

                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    int i = 0;
                    while (i < node.getChildCount()) {
                        Spin2StatementNode child = node.getChild(i);

                        boolean isByte = false;
                        if (child.getType() == Token.STRING && child.getText().startsWith("\"")) {
                            os.write(getString(child.getToken()).getBytes());
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

                            Expression methodExpression = getMethodExpression(context, child);
                            if (methodExpression != null) {
                                source.addAll(compileMethodCall(context, method, methodExpression, child, null, false));
                                if (child.getReturnLongs() > 1) {
                                    logMessage(new CompilerException("send parameter cannot return multiple values", child.getTokens()));
                                }
                                popValue = child.getReturnLongs() != 0;
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
                    if (push) {
                        logMessage(new CompilerException("method doesn't return a value", node.getTokens()));
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
                if (expression == null) {
                    if (expression == null) {
                        if ("CLKMODE".equalsIgnoreCase(ar[0])) {
                            expression = new NumberLiteral(0x40, 16);
                        }
                        else if ("CLKFREQ".equalsIgnoreCase(ar[0])) {
                            expression = new NumberLiteral(0x44, 16);
                        }
                    }
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
                    else if (expression instanceof NumberLiteral) {
                        bb = MemoryOp.Base.Pop;
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
                    else if (expression instanceof NumberLiteral) {
                        source.add(new Constant(context, expression));
                        if (indexNode != null && !popIndex) {
                            source.addAll(compileBytecodeExpression(context, method, indexNode, true));
                            popIndex = true;
                        }
                        MemoryOp.Op op = bitfieldNode == null && postEffectNode == null ? (push ? MemoryOp.Op.Read : MemoryOp.Op.Write) : MemoryOp.Op.Setup;
                        source.add(new MemoryOp(context, ss, bb, op, popIndex));
                    }
                    else {
                        MemoryOp.Op op = bitfieldNode == null && postEffectNode == null ? (push ? MemoryOp.Op.Read : MemoryOp.Op.Write) : MemoryOp.Op.Setup;
                        source.add(new MemoryOp(context, ss, bb, op, popIndex, expression, index));
                    }

                    if (bitfieldNode != null) {
                        source.add(new BitField(context, postEffectNode == null ? BitField.Op.Read : BitField.Op.Setup, bitfield));
                    }

                    if (postEffectNode != null) {
                        compilePostEffect(context, postEffectNode, source, push);
                        node.setReturnLongs(push ? 1 : 0);
                    }
                    else {
                        if (!push) {
                            logMessage(new CompilerException("expected assignment", node.getTokens()));
                        }
                        node.setReturnLongs(1);
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

                if (expression != null && isStructure(context, expression)) {
                    source.addAll(compileStructure(context, method, node, expression, null, MemoryOp.Op.Read, push));
                    return source;
                }
                if (expression == null && ar.length > 1) {
                    Expression structureExpression = context.getLocalSymbol(ar[0]);
                    if (structureExpression == null && isAddress(ar[0])) {
                        structureExpression = context.getLocalSymbol(ar[0].substring(1));
                    }
                    if (structureExpression != null && isStructure(context, structureExpression)) {
                        source.addAll(compileStructure(context, method, node, structureExpression, null, MemoryOp.Op.Read, push));
                        return source;
                    }
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
                            if (!push) {
                                logMessage(new CompilerException("expected assignment", node.getTokens()));
                            }
                            node.setReturnLongs(1);
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
                            if ((node.getText().startsWith("@[") || node.getText().startsWith("@@[")) && node.getText().endsWith("]")) {
                                if (n < node.getChildCount()) {
                                    throw new CompilerException("unexpected " + node.getChild(n).getText(), node.getChild(n).getToken());
                                }
                                VariableOp.Op op = VariableOp.Op.Address;
                                if (node.getText().startsWith("@@")) {
                                    op = VariableOp.Op.PBaseAddress;
                                }
                                source.add(new VariableOp(context, op, popIndex, (Variable) expression, hasIndex, index));
                            }
                            else {
                                if (n < node.getChildCount() && (node.getChild(n) instanceof Spin2StatementNode.Index)) {
                                    indexNode = node.getChild(n++);
                                }
                                if (n < node.getChildCount()) {
                                    throw new CompilerException("unexpected " + node.getChild(n).getText(), node.getChild(n).getToken());
                                }

                                if (isPointer(expression)) {
                                    Variable var = (Variable) expression;

                                    MemoryOp.Size ss = MemoryOp.Size.Long;
                                    if ("^BYTE".equalsIgnoreCase(var.getType())) {
                                        ss = MemoryOp.Size.Byte;
                                    }
                                    else if ("^WORD".equalsIgnoreCase(var.getType())) {
                                        ss = MemoryOp.Size.Word;
                                    }

                                    source.add(new VariableOp(context, VariableOp.Op.Read, false, var, false, 0));
                                    if (indexNode != null) {
                                        source.addAll(compileBytecodeExpression(context, method, indexNode, true));
                                    }
                                    source.add(new MemoryOp(context, ss, MemoryOp.Base.Pop, MemoryOp.Op.Address, indexNode != null));
                                }
                                else {
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
                            }

                            if (!push) {
                                logMessage(new CompilerException("expected assignment", node.getTokens()));
                            }
                            node.setReturnLongs(1);
                        }
                        else {
                            if (node.isMethod()) {
                                source.addAll(compileMethodCall(context, method, expression, node, null, false));
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

                            if (!push) {
                                logMessage(new CompilerException("expected assignment", node.getTokens()));
                            }
                            node.setReturnLongs(1);
                        }
                        else if (expression.isConstant()) {
                            if (node.getChildCount() != 0) {
                                throw new RuntimeException("syntax error");
                            }
                            source.add(new Constant(context, expression));
                            if (!push) {
                                logMessage(new CompilerException("expected assignment", node.getTokens()));
                            }
                            node.setReturnLongs(1);
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
                            if (!push) {
                                logMessage(new CompilerException("expected assignment", node.getTokens()));
                            }
                            node.setReturnLongs(1);
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
                    if (!push && desc.getReturns() != 0) {
                        logMessage(new CompilerException("expected assignment", node.getTokens()));
                    }
                    else if (push && desc.getReturns() == 0) {
                        logMessage(new CompilerException("method doesn't return a value", node.getTokens()));
                    }
                    node.setReturnLongs(desc.getReturns());
                }
                else if ("ABORT".equalsIgnoreCase(node.getText())) {
                    if (node.getChildCount() != 0 && node.getChildCount() != 1) {
                        throw new RuntimeException("expected 0 or 1 argument(s)");
                    }
                    if (node.getChildCount() == 0) {
                        source.add(new Bytecode(context, Spin2Bytecode.bc_abort_0, node.getText().toUpperCase()));
                    }
                    else {
                        source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));
                        source.add(new Bytecode(context, Spin2Bytecode.bc_abort_arg, node.getText().toUpperCase()));
                        if (node.getChild(0).getReturnLongs() > 1) {
                            logMessage(new CompilerException("expression returns more than 1 value", node.getChild(0).getTokens()));
                        }
                    }
                    if (push) {
                        logMessage(new CompilerException("abort doesn't return a value", node.getTokens()));
                    }
                    node.setReturnLongs(0);
                }
                else if ("COGINIT".equalsIgnoreCase(node.getText())) {
                    if (node.getChildCount() != 3) {
                        throw new RuntimeException("expected " + 3 + " argument(s), found " + node.getChildCount());
                    }
                    for (int i = 0; i < node.getChildCount(); i++) {
                        source.addAll(compileBytecodeExpression(context, method, node.getChild(i), true));
                    }
                    source.add(new Bytecode(context, push ? Spin2Bytecode.bc_coginit_push : Spin2Bytecode.bc_coginit, node.getText().toUpperCase()));
                    node.setReturnLongs(push ? 1 : 0);
                }
                else if ("COGNEW".equalsIgnoreCase(node.getText())) {
                    if (node.getChildCount() != 2) {
                        throw new RuntimeException("expected " + 2 + " argument(s), found " + node.getChildCount());
                    }
                    source.add(new Constant(context, new NumberLiteral(16)));
                    for (int i = 0; i < node.getChildCount(); i++) {
                        source.addAll(compileBytecodeExpression(context, method, node.getChild(i), true));
                    }
                    source.add(new Bytecode(context, push ? Spin2Bytecode.bc_coginit_push : Spin2Bytecode.bc_coginit, node.getText().toUpperCase()));
                    node.setReturnLongs(push ? 1 : 0);
                }
                else if ("COGSPIN".equalsIgnoreCase(node.getText())) {
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

                    node.setReturnLongs(push ? 1 : 0);
                }
                else if ("TASKSPIN".equalsIgnoreCase(node.getText())) {
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
                        Spin2Bytecode.bc_hub_bytecode, (byte) Spin2Bytecode.bc_taskspin, (byte) ((push ? 0x80 : 0x00) | actual)
                    }, node.getText().toUpperCase()));

                    node.setReturnLongs(push ? 1 : 0);
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

                    int code = Spin2Bytecode.bc_lookup_value;
                    int code_range = Spin2Bytecode.bc_lookup_range;
                    if ("LOOKDOWN".equalsIgnoreCase(node.getText()) || "LOOKDOWNZ".equalsIgnoreCase(node.getText())) {
                        code = Spin2Bytecode.bc_lookdown_value;
                        code_range = Spin2Bytecode.bc_lookdown_range;
                    }

                    Spin2Bytecode end = new Spin2Bytecode(context);
                    source.add(new Address(context, new ContextLiteral(end.getContext())));

                    Spin2StatementNode valueNode = argsNode.getChild(0);
                    source.addAll(compileBytecodeExpression(context, method, valueNode, true));
                    if (valueNode.getReturnLongs() > 1) {
                        if (valueNode.isMethod()) {
                            logMessage(new CompilerException("method return multiple values", valueNode.getToken()));
                        }
                        else {
                            logMessage(new CompilerException("expression return multiple values", valueNode.getTokens()));
                        }
                    }

                    source.add(new Constant(context, new NumberLiteral(node.getText().toUpperCase().endsWith("Z") ? 0 : 1)));

                    for (int i = 1; i < argsNode.getChildCount(); i++) {
                        Spin2StatementNode arg = argsNode.getChild(i);
                        if ("..".equals(arg.getText())) {
                            source.addAll(compileBytecodeExpression(context, method, arg.getChild(0), true));
                            source.addAll(compileBytecodeExpression(context, method, arg.getChild(1), true));
                            source.add(new Bytecode(context, code_range, node.getText().toUpperCase()));
                        }
                        else if (arg.getType() == Token.STRING) {
                            if (arg.getText().startsWith("\"")) {
                                String s = getString(arg.getToken());
                                for (int x = 0; x < s.length(); x++) {
                                    source.add(new Constant(context, new CharacterLiteral(s.substring(x, x + 1))));
                                    source.add(new Bytecode(context, code, node.getText().toUpperCase()));
                                }
                            }
                            else {
                                logMessage(new CompilerException("invalid argument", arg.getToken()));
                            }
                        }
                        else {
                            source.addAll(compileBytecodeExpression(context, method, arg, true));
                            source.add(new Bytecode(context, code, node.getText().toUpperCase()));
                        }
                    }

                    source.add(new Bytecode(context, Spin2Bytecode.bc_look_done, "LOOKDONE"));
                    source.add(end);

                    if (!push) {
                        logMessage(new CompilerException("expected assignment", node.getTokens()));
                    }
                    node.setReturnLongs(1);
                }
                else if ("STRING".equalsIgnoreCase(node.getText()) || "LSTRING".equalsIgnoreCase(node.getText())) {
                    ByteArrayOutputStream sb = new ByteArrayOutputStream();
                    for (Spin2StatementNode child : node.getChilds()) {
                        if (child.getType() == Token.STRING) {
                            if (child.getText().startsWith("\"")) {
                                sb.write(getString(child.getToken()).getBytes());
                            }
                            else {
                                logMessage(new CompilerException("invalid argument", child.getToken()));
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

                    if (!push) {
                        logMessage(new CompilerException("expected assignment", node.getTokens()));
                    }
                    node.setReturnLongs(1);
                }
                else if ("BYTE".equalsIgnoreCase(node.getText()) || "WORD".equalsIgnoreCase(node.getText()) || "LONG".equalsIgnoreCase(node.getText())) {
                    int n = 0;
                    if (n < node.getChildCount() && (node.getChild(n) instanceof Spin2StatementNode.Index)) {
                        Spin2StatementNode indexNode = null;

                        n++;
                        if (n < node.getChildCount() && (node.getChild(n) instanceof Spin2StatementNode.Index)) {
                            indexNode = node.getChild(n++);
                        }

                        if (!"LONG".equalsIgnoreCase(node.getText())) {
                            throw new RuntimeException("method pointers must be long");
                        }

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

                        source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));

                        if (indexNode != null) {
                            source.addAll(compileBytecodeExpression(context, method, indexNode, true));
                        }

                        source.add(new MemoryOp(context, MemoryOp.Size.Long, MemoryOp.Base.Pop, MemoryOp.Op.Read, indexNode != null));
                        source.add(new Bytecode(context, new byte[] {
                            (byte) Spin2Bytecode.bc_call_ptr,
                        }, "CALL_PTR"));

                        if (!push) { // node.returnLongs is set to the expected value in Spin2TreeBuilder
                            node.setReturnLongs(0);
                        }
                    }
                    else {
                        byte[] code = compileTypes(context, node, node.getText());
                        if (code.length > 255) {
                            logMessage(new CompilerException(CompilerException.ERROR, "data cannot exceed 255 bytes", node.getTokens()));
                        }

                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        os.write(Spin2Bytecode.bc_string);
                        os.write(code.length);
                        os.writeBytes(code);
                        source.add(new Bytecode(context, os.toByteArray(), node.getText().toUpperCase() + "S"));

                        if (!push) {
                            logMessage(new CompilerException("expected assignment", node.getTokens()));
                        }
                        node.setReturnLongs(1);
                    }
                }
                else if ("SIZEOF".equalsIgnoreCase(node.getText())) {
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
                    if (!push) {
                        logMessage(new CompilerException("expected assignment", node.getTokens()));
                    }
                    node.setReturnLongs(1);
                }
                else if ("DEBUG".equalsIgnoreCase(node.getText())) {
                    int stack = 0;
                    boolean debugLineEnabled = true;
                    List<Spin2Bytecode> debugSource = new ArrayList<Spin2Bytecode>();

                    int n = 0;
                    if (n < node.getChildCount()) {
                        if (node.getChild(n) instanceof Spin2StatementNode.Index) {
                            Spin2StatementNode bitmaskNode = node.getChild(n++);

                            Integer bit = null;
                            try {
                                Expression expression = buildConstantExpression(context, bitmaskNode);
                                if (expression.isConstant()) {
                                    bit = expression.getNumber().intValue();
                                }
                            } catch (Exception e) {
                                // Do nothing
                            }
                            if (bit == null) {
                                logMessage(new CompilerException("not a constant", bitmaskNode.getTokens()));
                            }
                            else {
                                if (bit < 0 || bit > 31) {
                                    logMessage(new CompilerException("debug mask bit number must be 0..31", bitmaskNode.getTokens()));
                                }
                                try {
                                    Expression debugMask = context.getLocalSymbol("DEBUG_MASK");
                                    if (debugMask != null) {
                                        long mask = debugMask.getNumber().longValue();
                                        debugLineEnabled = (mask & (1 << bit)) != 0;
                                    }
                                } catch (Exception e) {
                                    // Do nothing
                                }
                            }
                        }
                    }

                    while (n < node.getChildCount()) {
                        Spin2StatementNode child = node.getChild(n++);
                        if (child.getType() == Token.STRING) {
                            for (Spin2StatementNode param : child.getChilds()) {
                                debugSource.addAll(compileConstantExpression(context, method, param));
                                stack += param.getReturnLongs() * 4;
                            }
                        }
                        else if (Spin2Model.isDebugKeyword(child.getText())) {
                            for (Spin2StatementNode param : child.getChilds()) {
                                debugSource.addAll(compileConstantExpression(context, method, param));
                                stack += param.getReturnLongs() * 4;
                            }
                        }
                        else if (child.getText().startsWith("`")) {
                            for (Spin2StatementNode param : child.getChilds()) {
                                debugSource.addAll(compileConstantExpression(context, method, param));
                                stack += param.getReturnLongs() * 4;
                            }
                        }
                        else {
                            debugSource.addAll(compileConstantExpression(context, method, child));
                            stack += child.getReturnLongs() * 4;
                        }
                    }
                    debug.compileDebugStatement(node);

                    if (push) {
                        logMessage(new CompilerException("method doesn't return a value", node.getTokens()));
                    }

                    if (isDebugEnabled() && debugLineEnabled) {
                        method.debugNodes.add(node);
                        compiler.debugStatements.add(node);

                        int pop = stack;
                        debugSource.add(new Bytecode(context, Spin2Bytecode.bc_debug, "") {

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

                        return debugSource;
                    }

                    return Collections.emptyList();
                }
                else if ("BYTECODE".equalsIgnoreCase(node.getText())) {
                    String text = node.getText().toUpperCase();
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    for (int i = 0; i < node.getChildCount(); i++) {
                        if (node.getChild(i).getType() == Token.STRING && i == node.getChildCount() - 1) {
                            if (node.getChild(i).getText().startsWith("\"")) {
                                text = getString(node.getChild(i).getToken());
                            }
                            else {
                                logMessage(new CompilerException("invalid string argument", node.getChild(i).getToken()));
                            }
                            break;
                        }
                        try {
                            Expression expression = buildConstantExpression(context, node.getChild(i));
                            if (!expression.isConstant()) {
                                throw new CompilerException("expression is not constant", node.getChild(i).getTokens());
                            }
                            os.write(expression.getNumber().byteValue());
                        } catch (CompilerException e) {
                            logMessage(e);
                        } catch (Exception e) {
                            logMessage(new CompilerException("expression is not constant", node.getChild(i).getTokens()));
                        }
                    }
                    source.add(new Bytecode(context, os.toByteArray(), text));
                    if (push) {
                        logMessage(new CompilerException("method doesn't return a value", node.getTokens()));
                    }
                }
                else {
                    throw new CompilerException("undefined symbol " + node.getText(), node.getToken());
                }

                return source;
            }

            if ("CLKMODE".equalsIgnoreCase(node.getText()) || "CLKFREQ".equalsIgnoreCase(node.getText())) {
                Spin2StatementNode indexNode = null;
                Spin2StatementNode bitfieldNode = null;
                Spin2StatementNode postEffectNode = null;

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
                    throw new RuntimeException("syntax error");
                }

                int bitfield = -1;
                if (bitfieldNode != null) {
                    bitfield = compileBitfield(context, method, bitfieldNode, source);
                }

                if ("CLKMODE".equalsIgnoreCase(node.getText())) {
                    source.add(new Constant(context, new NumberLiteral(0x40, 16)));
                }
                if ("CLKFREQ".equalsIgnoreCase(node.getText())) {
                    source.add(new Constant(context, new NumberLiteral(0x44, 16)));
                }

                if (indexNode != null) {
                    source.addAll(compileBytecodeExpression(context, method, indexNode, true));
                }

                MemoryOp.Size ss = MemoryOp.Size.Long;
                MemoryOp.Op op = bitfieldNode == null && postEffectNode == null ? (push ? MemoryOp.Op.Read : MemoryOp.Op.Write) : MemoryOp.Op.Setup;
                source.add(new MemoryOp(context, ss, MemoryOp.Base.Pop, op, indexNode != null));

                if (bitfieldNode != null) {
                    source.add(new BitField(context, postEffectNode == null ? BitField.Op.Read : BitField.Op.Setup, bitfield));
                }

                if (postEffectNode != null) {
                    compilePostEffect(context, postEffectNode, source, push);
                    node.setReturnLongs(push ? 1 : 0);
                }
                else {
                    if (!push) {
                        logMessage(new CompilerException("expected assignment", node.getTokens()));
                    }
                    node.setReturnLongs(1);
                }

                return source;
            }
            if ("END".equalsIgnoreCase(node.getText())) {
                // Ignored
                if (push) {
                    logMessage(new CompilerException("syntax error", node.getTokens()));
                }
                node.setReturnLongs(0);
                return source;
            }

            if ("-".equalsIgnoreCase(node.getText()) && node.getChildCount() == 1) {
                try {
                    Expression expression = buildConstantExpression(context, node);
                    source.add(new Constant(context, expression));
                } catch (Exception e) {
                    source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));
                    source.add(new Bytecode(context, Spin2Bytecode.bc_neg, "NEGATE"));
                    if (node.getChild(0).getReturnLongs() > 1) {
                        logMessage(new CompilerException("expected 1 value", node.getChild(0).getTokens()));
                    }
                }
                if (!push) {
                    logMessage(new CompilerException("expected assignment", node.getTokens()));
                }
                node.setReturnLongs(1);
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
                    if (node.getChild(0).getReturnLongs() > 1) {
                        logMessage(new CompilerException("expected 1 value", node.getChild(0).getTokens()));
                    }
                }
                if (!push) {
                    logMessage(new CompilerException("expected assignment", node.getTokens()));
                }
                node.setReturnLongs(1);
                return source;
            }
            if (("+".equalsIgnoreCase(node.getText()) || "+.".equalsIgnoreCase(node.getText())) && node.getChildCount() == 1) {
                try {
                    Expression expression = buildConstantExpression(context, node);
                    source.add(new Constant(context, expression));
                } catch (Exception e) {
                    source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));
                    if (node.getChild(0).getReturnLongs() > 1) {
                        logMessage(new CompilerException("expected 1 value", node.getChild(0).getTokens()));
                    }
                }
                if (!push) {
                    logMessage(new CompilerException("expected assignment", node.getTokens()));
                }
                node.setReturnLongs(1);
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
                if (node.getChild(0).getReturnLongs() > 1) {
                    logMessage(new CompilerException("expected 1 value", node.getChild(0).getTokens()));
                }
                node.setReturnLongs(push ? 1 : 0);
                return source;
            }
            if (":=".equals(node.getText())) {
                if (node.getChildCount() != 2) {
                    throw new CompilerException("expression syntax error", node.getTokens());
                }

                if (node.getChild(0).getChildCount() == 0 && node.getChild(1).getChildCount() == 0) {
                    String[] ar0 = node.getChild(0).getText().split("[\\.]");
                    String[] ar1 = node.getChild(1).getText().split("[\\.]");
                    Expression left = context.getLocalSymbol(ar0[0]);
                    Expression right = context.getLocalSymbol(ar1[0]);
                    if (isStructure(context, left) && isStructure(context, right)) {
                        Spin2Struct leftStruct = null;
                        Spin2Struct rightStruct = null;

                        List<Spin2Bytecode> leftSource = compileStructure(context, method, node.getChild(0), left, null, MemoryOp.Op.Address, true);
                        if (leftSource.size() != 0 && leftSource.get(leftSource.size() - 1) instanceof StructOp) {
                            StructOp op = (StructOp) leftSource.get(leftSource.size() - 1);
                            leftStruct = op.getStruct();
                        }

                        List<Spin2Bytecode> rightSource = compileStructure(context, method, node.getChild(1), right, null, MemoryOp.Op.Address, true);
                        if (rightSource.size() != 0 && rightSource.get(rightSource.size() - 1) instanceof StructOp) {
                            StructOp op = (StructOp) rightSource.get(rightSource.size() - 1);
                            rightStruct = op.getStruct();
                        }

                        if (leftStruct != null && rightStruct != null) {
                            int leftSize = leftStruct.getTypeSize();
                            int rightSize = rightStruct.getTypeSize();
                            if (leftSize != rightSize || leftSize > (15 * 4) || rightSize > (15 * 4)) {
                                source.addAll(leftSource);
                                source.addAll(rightSource);

                                if (leftSize != rightSize) {
                                    logMessage(new CompilerException(CompilerException.WARNING, "structures are not same size", node.getTokens()));
                                }
                                source.add(new Constant(context, new NumberLiteral(Math.min(leftSize, rightSize))));

                                Descriptor desc = Spin2Bytecode.getDescriptor("BYTEMOVE");
                                source.add(new Bytecode(context, desc.code, "BYTEMOVE"));

                                node.setReturnLongs(0);
                                return source;
                            }
                        }

                        source.clear();
                    }
                }
                source.addAll(compileConstantExpression(context, method, node.getChild(1)));
                source.addAll(leftAssign(context, method, node.getChild(0), push, push));
                int expected = node.getChild(0).getReturnLongs();
                int actual = node.getChild(1).getReturnLongs();
                if (expected != actual) {
                    logMessage(new CompilerException("expression returns " + actual + " value(s), expecting " + expected, node.getChild(1).getTokens()));
                }
                node.setReturnLongs(push ? actual : 0);
                return source;
            }
            if (":=:".equals(node.getText())) {
                if (node.getChildCount() != 2) {
                    throw new RuntimeException("expression syntax error");
                }

                String[] ar0 = node.getChild(0).getText().split("[\\.]");
                String[] ar1 = node.getChild(1).getText().split("[\\.]");
                Expression left = context.getLocalSymbol(ar0[0]);
                Expression right = context.getLocalSymbol(ar1[0]);
                if (isStructure(context, left) && isStructure(context, right)) {
                    Spin2Struct leftStruct = null;
                    Spin2Struct rightStruct = null;

                    List<Spin2Bytecode> leftSource = compileStructure(context, method, node.getChild(0), left, null, MemoryOp.Op.Address, true);
                    if (leftSource.size() != 0 && leftSource.get(leftSource.size() - 1) instanceof StructOp) {
                        StructOp op = (StructOp) leftSource.get(leftSource.size() - 1);
                        leftStruct = op.getStruct();
                    }
                    if (leftStruct == null) {
                        logMessage(new CompilerException("not a structure", node.getChild(0).getTokens()));
                    }

                    List<Spin2Bytecode> rightSource = compileStructure(context, method, node.getChild(1), right, null, MemoryOp.Op.Address, true);
                    if (rightSource.size() != 0 && rightSource.get(rightSource.size() - 1) instanceof StructOp) {
                        StructOp op = (StructOp) rightSource.get(rightSource.size() - 1);
                        rightStruct = op.getStruct();
                    }
                    if (rightStruct == null) {
                        logMessage(new CompilerException("not a structure", node.getChild(1).getTokens()));
                    }

                    if (leftStruct != null && rightStruct != null) {
                        source.addAll(leftSource);
                        source.addAll(rightSource);

                        if (leftStruct.getTypeSize() != rightStruct.getTypeSize()) {
                            logMessage(new CompilerException(CompilerException.WARNING, "structures are not same size", node.getTokens()));
                        }
                        source.add(new Constant(context, new NumberLiteral(Math.min(leftStruct.getTypeSize(), rightStruct.getTypeSize()))));

                        Descriptor desc = Spin2Bytecode.getDescriptor("BYTESWAP");
                        source.add(new Bytecode(context, desc.code, "BYTESWAP"));
                    }
                }
                else {
                    if (!isStructure(context, left)) {
                        logMessage(new CompilerException("not a structure", node.getChild(0).getTokens()));
                    }
                    if (!isStructure(context, right)) {
                        logMessage(new CompilerException("not a structure", node.getChild(1).getTokens()));
                    }
                }

                if (push) {
                    logMessage(new CompilerException("expression doesn't return a value", node.getTokens()));
                }
                node.setReturnLongs(0);
                return source;
            }
            if ("==".equals(node.getText()) || "<>".equals(node.getText())) {
                if (node.getChildCount() != 2) {
                    throw new RuntimeException("expression syntax error");
                }
                String[] ar0 = node.getChild(0).getText().split("[\\.]");
                String[] ar1 = node.getChild(1).getText().split("[\\.]");
                Expression left = context.getLocalSymbol(ar0[0]);
                Expression right = context.getLocalSymbol(ar1[0]);
                if (isStructure(context, left) && isStructure(context, right)) {
                    Spin2Struct leftStruct = null;
                    Spin2Struct rightStruct = null;

                    List<Spin2Bytecode> leftSource = compileStructure(context, method, node.getChild(0), left, null, MemoryOp.Op.Address, true);
                    if (leftSource.size() != 0 && leftSource.get(leftSource.size() - 1) instanceof StructOp) {
                        StructOp op = (StructOp) leftSource.get(leftSource.size() - 1);
                        leftStruct = op.getStruct();
                    }

                    List<Spin2Bytecode> rightSource = compileStructure(context, method, node.getChild(1), right, null, MemoryOp.Op.Address, true);
                    if (rightSource.size() != 0 && rightSource.get(rightSource.size() - 1) instanceof StructOp) {
                        StructOp op = (StructOp) rightSource.get(rightSource.size() - 1);
                        rightStruct = op.getStruct();
                    }

                    if (leftStruct != null && rightStruct != null) {
                        source.addAll(leftSource);
                        source.addAll(rightSource);

                        if (leftStruct.getTypeSize() != rightStruct.getTypeSize()) {
                            logMessage(new CompilerException(CompilerException.WARNING, "structures are not same size", node.getTokens()));
                        }
                        source.add(new Constant(context, new NumberLiteral(Math.min(leftStruct.getTypeSize(), rightStruct.getTypeSize()))));

                        Descriptor desc = Spin2Bytecode.getDescriptor("BYTECOMP");
                        source.add(new Bytecode(context, desc.code, "BYTECOMP"));
                        if ("<>".equals(node.getText())) {
                            source.add(new MathOp(context, "NOT", true));
                        }

                        if (!push) {
                            logMessage(new CompilerException("expected assignment", node.getTokens()));
                        }
                        node.setReturnLongs(1);
                        return source;
                    }
                }
                // Fall-through
            }
            if (MathOp.isAssignMathOp(node.getText()) && node.getChildCount() == 1) {
                source.addAll(leftAssign(context, method, node.getChild(0), true, false));
                source.add(new MathOp(context, node.getText(), push));
                if (node.getChild(0).getReturnLongs() > 1) {
                    logMessage(new CompilerException("expected 1 value", node.getChild(0).getTokens()));
                }
                node.setReturnLongs(push ? 1 : 0);
                return source;
            }
            if (MathOp.isAssignMathOp(node.getText())) {
                source.addAll(compileConstantExpression(context, method, node.getChild(1)));
                source.addAll(leftAssign(context, method, node.getChild(0), true, false));
                source.add(new MathOp(context, node.getText(), push));
                if (node.getChild(0).getReturnLongs() > 1) {
                    logMessage(new CompilerException("expected 1 value", node.getChild(0).getTokens()));
                }
                if (node.getChild(1).getReturnLongs() > 1) {
                    logMessage(new CompilerException("expected 1 value", node.getChild(1).getTokens()));
                }
                node.setReturnLongs(push ? 1 : 0);
                return source;
            }
            if (MathOp.isUnaryMathOp(node.getText())) {
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("expression syntax error");
                }
                source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));
                source.add(new MathOp(context, node.getText(), push));
                if (node.getChild(0).getReturnLongs() > 1) {
                    logMessage(new CompilerException("expected 1 value", node.getChild(0).getTokens()));
                }
                node.setReturnLongs(push ? 1 : 0);
                return source;
            }
            if (MathOp.isMathOp(node.getText())) {
                if (node.getChildCount() != 2) {
                    throw new RuntimeException("expression syntax error");
                }
                source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));
                source.addAll(compileBytecodeExpression(context, method, node.getChild(1), true));
                source.add(new MathOp(context, node.getText(), false));
                if (node.getChild(0).getReturnLongs() > 1) {
                    logMessage(new CompilerException("expected 1 value", node.getChild(0).getTokens()));
                }
                if (node.getChild(1).getReturnLongs() > 1) {
                    logMessage(new CompilerException("expected 1 value", node.getChild(1).getTokens()));
                }
                if (!push) {
                    logMessage(new CompilerException("expected assignment", node.getTokens()));
                }
                node.setReturnLongs(1);
                return source;
            }
            if ("?".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 2) {
                    throw new RuntimeException("expression syntax error");
                }
                Spin2StatementNode ifNode = node.getChild(0);
                if (!":".equals(node.getChild(1).getText())) {
                    throw new RuntimeException("expression syntax error");
                }
                Spin2StatementNode thenNode = node.getChild(1).getChild(0);
                Spin2StatementNode elseNode = node.getChild(1).getChild(1);
                source.addAll(compileBytecodeExpression(context, method, ifNode, true));
                source.addAll(compileBytecodeExpression(context, method, thenNode, true));
                source.addAll(compileBytecodeExpression(context, method, elseNode, true));
                source.add(new Bytecode(context, Spin2Bytecode.bc_ternary, "TERNARY_IF_ELSE"));
                if (ifNode.getReturnLongs() > 1) {
                    logMessage(new CompilerException("expected 1 value", ifNode.getTokens()));
                }
                if (thenNode.getReturnLongs() > 1) {
                    logMessage(new CompilerException("expected 1 value", thenNode.getTokens()));
                }
                if (elseNode.getReturnLongs() > 1) {
                    logMessage(new CompilerException("expected 1 value", elseNode.getTokens()));
                }
                if (!push) {
                    logMessage(new CompilerException("expected assignment", node.getTokens()));
                }
                node.setReturnLongs(1);
                return source;
            }
            if ("(".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("expression syntax error");
                }
                source.addAll(compileBytecodeExpression(context, method, node.getChild(0), push));
                node.setReturnLongs(node.getChild(0).getReturnLongs());
                return source;
            }
            if (",".equalsIgnoreCase(node.getText())) {
                int pop = 0;
                for (Spin2StatementNode child : node.getChilds()) {
                    source.addAll(compileBytecodeExpression(context, method, child, push));
                    pop += child.getReturnLongs();
                }
                node.setReturnLongs(pop);
                return source;
            }
            if ("\\".equalsIgnoreCase(node.getText())) {
                if (node.getChildCount() == 2) {
                    source.addAll(compileConstantExpression(context, method, node.getChild(1)));
                    source.addAll(leftAssign(context, method, node.getChild(0), push, false));
                    source.add(new Bytecode(context, Spin2Bytecode.bc_var_swap, "SWAP"));
                    if (node.getChild(0).getReturnLongs() > 1) {
                        logMessage(new CompilerException("expected 1 value", node.getChild(0).getTokens()));
                    }
                    if (node.getChild(1).getReturnLongs() > 1) {
                        logMessage(new CompilerException("expected 1 value", node.getChild(1).getTokens()));
                    }
                    node.setReturnLongs(node.getChild(1).getReturnLongs());
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
                        logMessage(new CompilerException("symbol " + node.getChild(0).getText() + " is not a method", node.getChild(0).getToken()));
                    }
                    node.setReturnLongs(push ? 1 : 0);
                }
                return source;
            }
            if ("[++]".equals(node.getText()) || "[--]".equals(node.getText())) {
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("expression syntax error " + node.getText());
                }

                Spin2StatementNode childNode = node.getChild(0);
                if (childNode.getText().startsWith("[")) {
                    logMessage(new CompilerException("syntax error", childNode.getTokens()));
                }
                String[] s = childNode.getText().split("[\\.]");
                Expression expression = context.getLocalSymbol(s[0]);
                if (expression == null) {
                    throw new CompilerException("undefined symbol " + childNode.getText(), childNode.getToken());
                }
                if (!isPointer(expression)) {
                    logMessage(new CompilerException("not a pointer", childNode.getToken()));
                }

                if (isStructure(context, expression)) {
                    source.addAll(compileStructure(context, method, childNode, expression, node, Op.Read, push));
                    node.setReturnLongs(childNode.getReturnLongs());
                }
                else {
                    source.addAll(compileVariableRead(context, method, expression, childNode, node, push));
                    node.setReturnLongs(push ? 1 : 0);
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
                    Spin2StatementNode pointerPreEffectNode = null;
                    Spin2StatementNode expressionNode = childNode;

                    if (childNode.getChildCount() == 1) {
                        if ("[++]".equals(childNode.getText()) || "[--]".equals(childNode.getText())) {
                            pointerPreEffectNode = childNode;
                            if (pointerPreEffectNode.getChildCount() != 0) {
                                expressionNode = pointerPreEffectNode.getChild(0);
                            }
                        }
                    }

                    Expression expression = context.getLocalSymbol(expressionNode.getText());
                    if (expression == null) {
                        ar = expressionNode.getText().split("[\\.]");
                        expression = context.getLocalSymbol(ar[0]);
                    }
                    if (expression == null) {
                        throw new CompilerException("undefined symbol " + expressionNode.getText(), expressionNode.getToken());
                    }

                    if (isStructure(context, expression)) {
                        source.addAll(compileStructure(context, method, expressionNode, expression, pointerPreEffectNode, MemoryOp.Op.Setup, true));
                        if (expressionNode.getReturnLongs() != 1) {
                            logMessage(new CompilerException("invalid expression", node.getTokens()));
                        }
                    }
                    else {
                        source.addAll(compileVariableSetup(context, method, expression, expressionNode, pointerPreEffectNode));
                    }

                    if (expressionNode.getText().startsWith("[")) {
                        String varType;
                        if (expression instanceof Variable) {
                            varType = ((Variable) expression).getType();
                        }
                        else {
                            varType = ((DataVariable) expression).getType();
                        }

                        int typeSize = 1;
                        if ("^WORD".equalsIgnoreCase(varType)) {
                            typeSize = 2;
                        }
                        else if ("^LONG".equalsIgnoreCase(varType)) {
                            typeSize = 4;
                        }
                        else if (!"^BYTE".equalsIgnoreCase(varType)) {
                            Spin2Struct struct = context.getStructureDefinition(varType.substring(1));
                            if (struct != null) {
                                typeSize = struct.getTypeSize();
                            }
                        }

                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        try {
                            os.write(Spin2Bytecode.bc_set_incdec);
                            os.write(Constant.wrVar(typeSize));
                            if ("++".equalsIgnoreCase(node.getText())) {
                                os.write(push ? Spin2Bytecode.bc_var_preinc_push : Spin2Bytecode.bc_var_inc);
                                source.add(new Bytecode(context, os.toByteArray(), String.format("PRE_INC (%d)%s", typeSize, push ? " (push)" : "")));
                            }
                            else if ("--".equalsIgnoreCase(node.getText())) {
                                os.write(push ? Spin2Bytecode.bc_var_predec_push : Spin2Bytecode.bc_var_dec);
                                source.add(new Bytecode(context, os.toByteArray(), String.format("PRE_DEC (%d)%s", typeSize, push ? " (push)" : "")));
                            }
                            else {
                                logMessage(new CompilerException("invalid pointer pre effect " + node.getText(), node.getToken()));
                            }
                        } catch (Exception e) {
                            // Do nothing
                        }
                        node.setReturnLongs(push ? 1 : 0);
                        return source;
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
                node.setReturnLongs(push ? 1 : 0);
                return source;
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
                if (n < node.getChildCount() && node.getText().startsWith("@")) {
                    throw new CompilerException("syntax error", node.getChild(n).getToken());
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

                if (node.getText().startsWith("@@")) {
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
                    node.setReturnLongs(push ? 1 : 0);
                }
                else {
                    if (!push) {
                        logMessage(new CompilerException("expected assignment", node.getTokens()));
                    }
                    node.setReturnLongs(1);
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
                    node.setReturnLongs(push ? 1 : 0);
                }
                else {
                    if (!push) {
                        logMessage(new CompilerException("expected assignment", node.getTokens()));
                    }
                    node.setReturnLongs(1);
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
                    node.setReturnLongs(push ? 1 : 0);
                }
                else {
                    if (!push) {
                        logMessage(new CompilerException("expected assignment", node.getTokens()));
                    }
                    node.setReturnLongs(1);
                }

                return source;
            }
            if ("DEBUG".equalsIgnoreCase(node.getText())) {
                if (push) {
                    logMessage(new CompilerException("doesn't return a value", node.getTokens()));
                }
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
            node.setReturnLongs(push ? 1 : 0);
            if (node.getType() != Token.KEYWORD) {
                throw new CompilerException("unexpected operator '" + node.getText() + "'", node.getToken());
            }
            else {
                throw new CompilerException("undefined symbol " + node.getText(), node.getToken());
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

    protected int getArgumentsCount(Context context, Spin2StatementNode childNode) {
        int actual = 0;
        for (int i = 0; i < childNode.getChildCount(); i++) {
            Expression child = context.getLocalSymbol(childNode.getChild(i).getText());
            if (child != null && (child instanceof Method) && !childNode.getChild(i).getText().startsWith("@")) {
                actual += ((Method) child).getReturnLongs();
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
                node.setReturnLongs(1);
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
            if (!node.getText().startsWith("\"")) {
                throw new RuntimeException("not a constant (" + node.getText() + ")");
            }
            return new CharacterLiteral(getString(node.getToken()));
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

            case "POW":
                return new Pow(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));

            case "LOG":
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("misplaced unary operator (" + node.getText() + ")");
                }
                return new Log(buildConstantExpression(context, node.getChild(0), registerConstant));
            case "LOG2":
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("misplaced unary operator (" + node.getText() + ")");
                }
                return new Log2(buildConstantExpression(context, node.getChild(0), registerConstant));
            case "LOG10":
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("misplaced unary operator (" + node.getText() + ")");
                }
                return new Log10(buildConstantExpression(context, node.getChild(0), registerConstant));

            case "EXP":
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("misplaced unary operator (" + node.getText() + ")");
                }
                return new Exp(buildConstantExpression(context, node.getChild(0), registerConstant));
            case "EXP2":
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("misplaced unary operator (" + node.getText() + ")");
                }
                return new Exp2(buildConstantExpression(context, node.getChild(0), registerConstant));
            case "EXP10":
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("misplaced unary operator (" + node.getText() + ")");
                }
                return new Exp10(buildConstantExpression(context, node.getChild(0), registerConstant));

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

        node.setReturnLongs(1);

        String[] ar = node.getText().split("[\\.]");
        if (ar.length >= 2 && ("BYTE".equalsIgnoreCase(ar[ar.length - 1]) || "WORD".equalsIgnoreCase(ar[ar.length - 1]) || "LONG".equalsIgnoreCase(ar[ar.length - 1]))) {
            Expression expression = context.getLocalSymbol(ar[0]);
            if (expression == null) {
                if ("CLKMODE".equalsIgnoreCase(ar[0])) {
                    expression = new NumberLiteral(0x40, 16);
                }
                else if ("CLKFREQ".equalsIgnoreCase(ar[0])) {
                    expression = new NumberLiteral(0x44, 16);
                }
            }
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
            else if (expression instanceof NumberLiteral) {
                bb = MemoryOp.Base.Pop;
            }

            if (expression instanceof NumberLiteral) {
                source.add(new Constant(context, expression));
                if (indexNode != null && !popIndex) {
                    source.addAll(compileBytecodeExpression(context, method, indexNode, true));
                    popIndex = true;
                }
                MemoryOp.Op op = push ? MemoryOp.Op.WritePush : MemoryOp.Op.Write;
                source.add(new MemoryOp(context, ss, bb, op, popIndex));
            }
            else {
                MemoryOp.Op op = push || bitfieldNode != null ? MemoryOp.Op.Setup : MemoryOp.Op.Write;
                source.add(new MemoryOp(context, ss, bb, op, popIndex, expression, index));
            }

            if (bitfieldNode != null) {
                source.add(new BitField(context, postEffectNode == null ? BitField.Op.Write : BitField.Op.Setup, bitfield));
            }

            if (postEffectNode != null) {
                compilePostEffect(context, postEffectNode, source, push);
            }
        }
        else if ("_".equalsIgnoreCase(node.getText())) {
            if (node.getChildCount() == 0) {
                source.add(new Bytecode(context, Spin2Bytecode.bc_pop, "POP"));
            }
            else if (node.getChildCount() == 1) {
                int pop;
                Spin2Struct struct = context.getStructureDefinition(node.getChild(0).getText());
                if (struct != null) {
                    pop = getStructureSize(context, struct);
                    pop = (pop + 3) & ~3;
                }
                else {
                    try {
                        Expression expression = buildConstantExpression(context, node.getChild(0));
                        if (expression.isConstant()) {
                            pop = expression.getNumber().intValue() * 4;
                        }
                        else {
                            throw new CompilerException("not a constant (" + node.getChild(0).getText() + ")", node.getChild(0).getTokens());
                        }
                    } catch (CompilerException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new CompilerException("not a constant (" + node.getChild(0).getText() + ")", node.getChild(0).getTokens());
                    }
                }
                try {
                    if (pop == 4) {
                        source.add(new Bytecode(context, Spin2Bytecode.bc_pop, "POP"));
                    }
                    else {
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        os.write(Spin2Bytecode.bc_pop_rfvar);
                        os.write(Constant.wrVars(pop - 4));
                        source.add(new Bytecode(context, os.toByteArray(), String.format("POP %d", pop)));
                    }
                } catch (Exception e) {
                    // Do nothing
                }
                node.setReturnLongs(pop / 4);
            }
            else {
                throw new CompilerException("syntax error", node.getTokens());
            }
        }
        else if (",".equals(node.getText())) {
            int pop = 0;
            for (int i = node.getChildCount() - 1; i >= 0; i--) {
                source.addAll(leftAssign(context, method, node.getChild(i), push, false));
                pop += node.getChild(i).getReturnLongs();
            }
            node.setReturnLongs(pop);
        }
        else if ("[++]".equals(node.getText()) || "[--]".equals(node.getText())) {
            String[] s = node.getChild(0).getText().split("[\\.]");
            Expression expression = context.getLocalSymbol(s[0]);
            if (expression == null) {
                throw new CompilerException("undefined symbol " + node.getChild(0), node.getChild(0).getToken());
            }

            if (isStructure(context, expression)) {
                source.addAll(compileStructure(context, method, node.getChild(0), expression, node, push ? MemoryOp.Op.Setup : MemoryOp.Op.Write, push));
                node.setReturnLongs(node.getChild(0).getReturnLongs());
                if (write) {
                    if (node.getReturnLongs() != 1) {
                        List<Token> list = new ArrayList<>();
                        list.addAll(node.getTokens());
                        list.addAll(node.getChild(0).getTokens());
                        logMessage(new CompilerException("invalid operation", list));
                    }
                    source.add(new Bytecode(context, Spin2Bytecode.bc_write_push, "WRITE (push)"));
                }
                return source;
            }
            else if (isPointer(expression)) {
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
                if ("[++]".equals(node.getText())) {
                    os.write(Spin2Bytecode.bc_var_preinc_push);
                    source.add(new Bytecode(context, os.toByteArray(), String.format("PRE_INC (%d)%s", typeSize, " (push)")));
                }
                else if ("[--]".equals(node.getText())) {
                    os.write(Spin2Bytecode.bc_var_predec_push);
                    source.add(new Bytecode(context, os.toByteArray(), String.format("PRE_DEC (%d)%s", typeSize, " (push)")));
                }
                else {
                    throw new CompilerException("invalid post effect " + node.getText(), node.getTokens());
                }
                source.add(new MemoryOp(context, ss, MemoryOp.Base.Pop, push ? MemoryOp.Op.WritePush : MemoryOp.Op.Write, indexNode != null));

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
        else if ("CLKMODE".equalsIgnoreCase(node.getText()) || "CLKFREQ".equalsIgnoreCase(node.getText())) {
            indexNode = null;
            bitfieldNode = null;

            int n = 0;
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

            if ("CLKMODE".equalsIgnoreCase(node.getText())) {
                source.add(new Constant(context, new NumberLiteral(0x40, 16)));
            }
            if ("CLKFREQ".equalsIgnoreCase(node.getText())) {
                source.add(new Constant(context, new NumberLiteral(0x44, 16)));
            }

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
        else {
            Expression expression = context.getLocalSymbol(node.getText());
            if (expression == null && ar.length > 1) {
                expression = context.getLocalSymbol(ar[0]);
            }
            if (expression == null) {
                throw new CompilerException("undefined symbol " + node.getText(), node.getToken());
            }

            if (isStructure(context, expression)) {
                source.addAll(compileStructure(context, method, node, expression, null, push ? MemoryOp.Op.Setup : MemoryOp.Op.Write, push));
                if (write) {
                    if (node.getReturnLongs() != 1) {
                        logMessage(new CompilerException("invalid operation", node.getTokens()));
                    }
                    source.add(new Bytecode(context, Spin2Bytecode.bc_write_push, "WRITE (push)"));
                }
                return source;
            }
            else if (isPointer(expression)) {
                int n = 0;
                if (n < node.getChildCount()) {
                    Spin2StatementNode child = node.getChild(n);
                    if (("[++]".equals(child.getText()) || "[--]".equals(child.getText())) && child.getChildCount() == 0) {
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

                if (node.getText().startsWith("[")) {
                    if (postEffectNode != null) {
                        source.add(new VariableOp(context, VariableOp.Op.Setup, popIndex, var, hasIndex, index));
                        compilePostEffect(context, postEffectNode, source, push);
                        if (!push) {
                            node.setReturnLongs(0);
                        }
                    }
                    else {
                        source.add(new VariableOp(context, push ? VariableOp.Op.Setup : VariableOp.Op.Write, popIndex, var, hasIndex, index));
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
                        if ("[++]".equalsIgnoreCase(pointerPostEffectNode.getText())) {
                            os.write(Spin2Bytecode.bc_var_postinc_push);
                            source.add(new Bytecode(context, os.toByteArray(), String.format("POST_INC (%d)%s", typeSize, " (push)")));
                        }
                        else if ("[--]".equalsIgnoreCase(pointerPostEffectNode.getText())) {
                            os.write(Spin2Bytecode.bc_var_postdec_push);
                            source.add(new Bytecode(context, os.toByteArray(), String.format("POST_DEC (%d)%s", typeSize, " (push)")));
                        }
                        else {
                            throw new CompilerException("invalid post effect " + pointerPostEffectNode.getText(), pointerPostEffectNode.getTokens());
                        }
                        source.add(new MemoryOp(context, ss, MemoryOp.Base.Pop, write ? MemoryOp.Op.WritePush : MemoryOp.Op.Write, indexNode != null));
                    }
                    else {
                        source.add(new VariableOp(context, VariableOp.Op.Read, popIndex, var, hasIndex, index));
                        if (postEffectNode != null) {
                            source.add(new MemoryOp(context, ss, MemoryOp.Base.Pop, MemoryOp.Op.Setup, indexNode != null));
                            compilePostEffect(context, postEffectNode, source, push);
                            if (!push) {
                                node.setReturnLongs(0);
                            }
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
        String varType;

        if (expression instanceof Variable) {
            varType = ((Variable) expression).getType();
        }
        else if (expression instanceof DataVariable) {
            varType = ((DataVariable) expression).getType();
        }
        else {
            return false;
        }

        if (context.hasStructureDefinition(varType)) {
            return true;
        }
        if (varType.startsWith("^")) {
            if (context.hasStructureDefinition(varType.substring(1))) {
                return true;
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

    Expression getMethodExpression(Context context, Spin2StatementNode node) {
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
            return symbol;
        }
        if (symbol instanceof Method) {
            return symbol;
        }
        if (symbol instanceof Variable) {
            if (node.isMethod()) {
                return symbol;
            }
        }

        return null;
    }

    List<Spin2Bytecode> compileMethodCall(Context context, Spin2Method method, Expression symbol, Spin2StatementNode node, Boolean push, boolean trap) {
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
                node.setReturnLongs(1);
            }
            else {
                if (push == null) {
                    push = methodExpression.getReturnLongs() != 0;
                }
                if (push) {
                    if (trap) {
                        source.add(new Bytecode(context, Spin2Bytecode.bc_drop_trap_push, "ANCHOR_TRAP (push)"));
                    }
                    else {
                        source.add(new Bytecode(context, Spin2Bytecode.bc_drop_push, "ANCHOR (push)"));
                    }
                }
                else {
                    if (trap) {
                        source.add(new Bytecode(context, Spin2Bytecode.bc_drop_trap, "ANCHOR_TRAP"));
                    }
                    else {
                        source.add(new Bytecode(context, Spin2Bytecode.bc_drop, "ANCHOR"));
                    }
                }

                source.addAll(compileMethodArguments(context, method, calledMethod, methodNode));
                if (indexNode != null) {
                    source.addAll(compileConstantExpression(context, method, indexNode));
                }
                source.add(new CallSub(context, methodExpression, indexNode != null));
                calledMethod.setCalledBy(method);

                if (push && !trap && methodExpression.getReturnLongs() == 0) {
                    logMessage(new CompilerException("method doesn't return any value", node.getToken()));
                }
                node.setReturnLongs(push ? methodExpression.getReturnLongs() : 0);
            }
        }
        else {
            if (symbol instanceof Method) {
                Method methodExpression = (Method) symbol;
                Spin2Method calledMethod = (Spin2Method) methodExpression.getData(Spin2Method.class.getName());

                if (push == null) {
                    push = methodExpression.getReturnLongs() != 0;
                }
                if (push) {
                    if (trap) {
                        source.add(new Bytecode(context, Spin2Bytecode.bc_drop_trap_push, "ANCHOR_TRAP (push)"));
                    }
                    else {
                        source.add(new Bytecode(context, Spin2Bytecode.bc_drop_push, "ANCHOR (push)"));
                    }
                }
                else {
                    if (trap) {
                        source.add(new Bytecode(context, Spin2Bytecode.bc_drop_trap, "ANCHOR_TRAP"));
                    }
                    else {
                        source.add(new Bytecode(context, Spin2Bytecode.bc_drop, "ANCHOR"));
                    }
                }

                source.addAll(compileMethodArguments(context, method, calledMethod, node));
                source.add(new CallSub(context, methodExpression));
                calledMethod.setCalledBy(method);

                if (push && !trap && methodExpression.getReturnLongs() == 0) {
                    logMessage(new CompilerException("method doesn't return any value", node.getToken()));
                }
                node.setReturnLongs(push ? methodExpression.getReturnLongs() : 0);
            }
            else {
                if (push == null) {
                    push = node.getReturnLongs() != 0;
                }
                if (push) {
                    if (trap) {
                        source.add(new Bytecode(context, Spin2Bytecode.bc_drop_trap_push, "ANCHOR_TRAP (push)"));
                    }
                    else {
                        source.add(new Bytecode(context, Spin2Bytecode.bc_drop_push, "ANCHOR (push)"));
                    }
                }
                else {
                    if (trap) {
                        source.add(new Bytecode(context, Spin2Bytecode.bc_drop_trap, "ANCHOR_TRAP"));
                    }
                    else {
                        source.add(new Bytecode(context, Spin2Bytecode.bc_drop, "ANCHOR"));
                    }
                }

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

                if (isStructure(context, symbol)) {
                    int rc = node.getReturnLongs();
                    source.addAll(compileStructure(context, method, node, symbol, null, MemoryOp.Op.Read, true));
                    node.setReturnLongs(rc);
                }
                else if (symbol instanceof Variable) {
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

                if ((push && !trap) && node.getReturnLongs() == 0) {
                    logMessage(new CompilerException("method doesn't return any value", node.getToken()));
                }
                node.setReturnLongs(push ? node.getReturnLongs() : 0);
            }
        }

        return source;
    }

    List<Spin2Bytecode> compileMethodArguments(Context context, Spin2Method method, Spin2Method calledMethod, Spin2StatementNode argsNode) {
        List<Spin2Bytecode> source = new ArrayList<Spin2Bytecode>();

        int actual = 0;
        for (int i = 0; i < argsNode.getChildCount(); i++) {
            source.addAll(compileConstantExpression(context, method, argsNode.getChild(i)));
            actual += argsNode.getChild(i).getReturnLongs();
        }
        while (actual < calledMethod.getParameterLongs()) {
            Expression value = calledMethod.getParameters().get(actual).getValue();
            if (value == null) {
                break;
            }
            source.add(new Constant(context, value));
            actual++;
        }

        if (actual != calledMethod.getParameterLongs()) {
            logMessage(new CompilerException("expected " + calledMethod.getParameterLongs() + " argument(s), found " + actual, argsNode.getTokens()));
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

    List<Spin2Bytecode> compileVariableSetup(Context context, Spin2Method method, Expression expression, Spin2StatementNode node, Spin2StatementNode pointerPreEffectNode) {
        Spin2StatementNode indexNode = null;
        Spin2StatementNode bitfieldNode = null;
        Spin2StatementNode pointerPostEffectNode = null;
        List<Spin2Bytecode> source = new ArrayList<Spin2Bytecode>();

        int index = 0;
        boolean hasIndex = false;
        boolean popIndex = false;

        if (isPointer(expression)) {
            int n = 0;
            if (n < node.getChildCount()) {
                Spin2StatementNode child = node.getChild(n);
                if (("[++]".equals(child.getText()) || "[--]".equals(child.getText())) && child.getChildCount() == 0) {
                    pointerPostEffectNode = node.getChild(n++);
                    if (pointerPreEffectNode != null) {
                        logMessage(new CompilerException("syntax error", pointerPostEffectNode.getTokens()));
                    }
                }
            }
            if (n < node.getChildCount()) {
                throw new CompilerException("unexpected: " + node.getChild(n).getText(), node.getChild(n).getTokens());
            }

            Variable var = (Variable) expression;

            if (node.getText().startsWith("[")) {
                source.add(new VariableOp(context, VariableOp.Op.Setup, popIndex, var, hasIndex, index));
                if (pointerPostEffectNode != null) {
                    logMessage(new CompilerException("syntax error", pointerPostEffectNode.getTokens()));
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
                if (pointerPreEffectNode != null) {
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

                    if ("[++]".equalsIgnoreCase(pointerPreEffectNode.getText())) {
                        os.write(Spin2Bytecode.bc_var_preinc_push);
                        source.add(new Bytecode(context, os.toByteArray(), String.format("PRE_INC (%d)%s", typeSize, " (push)")));
                    }
                    else if ("[--]".equalsIgnoreCase(pointerPreEffectNode.getText())) {
                        os.write(Spin2Bytecode.bc_var_predec_push);
                        source.add(new Bytecode(context, os.toByteArray(), String.format("PRE_DEC (%d)%s", typeSize, " (push)")));
                    }
                    else {
                        throw new CompilerException("invalid pointer pre effect " + pointerPreEffectNode.getText(), pointerPreEffectNode.getTokens());
                    }
                    source.add(new MemoryOp(context, ss, MemoryOp.Base.Pop, MemoryOp.Op.Setup, indexNode != null));
                }
                else if (pointerPostEffectNode != null) {
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
                    if ("[++]".equalsIgnoreCase(pointerPostEffectNode.getText())) {
                        os.write(Spin2Bytecode.bc_var_postinc_push);
                        source.add(new Bytecode(context, os.toByteArray(), String.format("POST_INC (%d)%s", typeSize, " (push)")));
                    }
                    else if ("[--]".equalsIgnoreCase(pointerPostEffectNode.getText())) {
                        os.write(Spin2Bytecode.bc_var_postdec_push);
                        source.add(new Bytecode(context, os.toByteArray(), String.format("POST_DEC (%d)%s", typeSize, " (push)")));
                    }
                    else {
                        throw new CompilerException("invalid pointer post effect " + pointerPostEffectNode.getText(), pointerPostEffectNode.getTokens());
                    }
                    source.add(new MemoryOp(context, ss, MemoryOp.Base.Pop, MemoryOp.Op.Setup, indexNode != null));
                }
                else {
                    source.add(new VariableOp(context, VariableOp.Op.Read, popIndex, var, hasIndex, index));
                    source.add(new MemoryOp(context, ss, MemoryOp.Base.Pop, MemoryOp.Op.Setup, indexNode != null));
                }
            }

            var.setCalledBy(method);
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
        return compileVariableRead(context, method, expression, node, null, push);
    }

    List<Spin2Bytecode> compileVariableRead(Context context, Spin2Method method, Expression expression, Spin2StatementNode node, Spin2StatementNode pointerPreEffectNode, boolean push) {
        Spin2StatementNode indexNode = null;
        Spin2StatementNode bitfieldNode = null;
        Spin2StatementNode postEffectNode = null;
        Spin2StatementNode pointerPostEffectNode = null;
        List<Spin2Bytecode> source = new ArrayList<Spin2Bytecode>();

        int index = 0;
        boolean hasIndex = false;
        boolean popIndex = false;
        boolean field = node.getText().startsWith("^@");

        node.setReturnLongs(1);

        if (isStructure(context, expression)) {
            source.addAll(compileStructure(context, method, node, expression, pointerPreEffectNode, MemoryOp.Op.Read, push));
        }
        else if (isPointer(expression)) {
            int n = 0;
            if (n < node.getChildCount()) {
                Spin2StatementNode child = node.getChild(n);
                if (("[++]".equals(child.getText()) || "[--]".equals(child.getText())) && child.getChildCount() == 0) {
                    pointerPostEffectNode = node.getChild(n++);
                    if (pointerPreEffectNode != null) {
                        logMessage(new CompilerException("syntax error", pointerPostEffectNode.getTokens()));
                    }
                    if (pointerPostEffectNode.getChildCount() != 0) {
                        throw new CompilerException("syntax error", pointerPostEffectNode.getChild(0).getTokens());
                    }
                }
            }
            if (n < node.getChildCount() && isPostEffect(node.getChild(n))) {
                postEffectNode = node.getChild(n++);
            }
            if (n < node.getChildCount()) {
                throw new CompilerException("unexpected: " + node.getChild(n).getText(), node.getChild(n).getTokens());
            }

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

            if (node.getText().startsWith("[") && node.getText().endsWith("]")) {
                if (postEffectNode != null) {
                    if ("++".equalsIgnoreCase(postEffectNode.getText()) || "--".equalsIgnoreCase(postEffectNode.getText())) {
                        source.add(new VariableOp(context, VariableOp.Op.Setup, popIndex, var, hasIndex, index));
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        try {
                            if (typeSize != 1) {
                                os.write(Spin2Bytecode.bc_set_incdec);
                                os.write(Constant.wrVar(typeSize));
                            }
                            if ("++".equalsIgnoreCase(postEffectNode.getText())) {
                                os.write(push ? Spin2Bytecode.bc_var_postinc_push : Spin2Bytecode.bc_var_inc);
                                source.add(new Bytecode(context, os.toByteArray(), String.format("POST_INC (%d)%s", typeSize, push ? " (push)" : "")));
                            }
                            else if ("--".equalsIgnoreCase(postEffectNode.getText())) {
                                os.write(push ? Spin2Bytecode.bc_var_postdec_push : Spin2Bytecode.bc_var_dec);
                                source.add(new Bytecode(context, os.toByteArray(), String.format("POST_DEC (%d)%s", typeSize, push ? " (push)" : "")));
                            }
                        } catch (Exception e) {
                            // Do nothing
                        }
                    }
                    else if ("~".equalsIgnoreCase(postEffectNode.getText())) {
                        source.add(0, new Constant(context, new NumberLiteral(0)));
                        if (!push) {
                            source.add(new VariableOp(context, VariableOp.Op.Write, false, (Variable) expression, false, 0));
                        }
                        else {
                            source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_swap : Spin2Bytecode.bc_write, push ? "SWAP" : "WRITE"));
                        }
                    }
                    else if ("~~".equalsIgnoreCase(postEffectNode.getText())) {
                        source.add(0, new Constant(context, new NumberLiteral(-1)));
                        if (!push) {
                            source.add(new VariableOp(context, VariableOp.Op.Write, false, (Variable) expression, false, 0));
                        }
                        else {
                            source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_swap : Spin2Bytecode.bc_write, push ? "SWAP" : "WRITE"));
                        }
                    }
                    else if ("!!".equalsIgnoreCase(postEffectNode.getText())) {
                        source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_lognot_push : Spin2Bytecode.bc_var_lognot, "POST_LOGICAL_NOT" + (push ? " (push)" : "")));
                    }
                    else if ("!".equalsIgnoreCase(postEffectNode.getText())) {
                        source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_bitnot_push : Spin2Bytecode.bc_var_bitnot, "POST_NOT" + (push ? " (push)" : "")));
                    }
                    else {
                        logMessage(new CompilerException("unhandled pointer post effect", postEffectNode.getToken()));
                    }
                    node.setReturnLongs(push ? 1 : 0);
                }
                else {
                    source.add(new VariableOp(context, VariableOp.Op.Read, popIndex, var, hasIndex, index));
                    if (!push) {
                        logMessage(new CompilerException("expected assignment", node.getTokens()));
                    }
                }
                if (pointerPostEffectNode != null) {
                    logMessage(new CompilerException("syntax error", pointerPostEffectNode.getTokens()));
                }
                return source;
            }
            else {
                if (pointerPreEffectNode != null) {
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

                    if ("[++]".equalsIgnoreCase(pointerPreEffectNode.getText())) {
                        os.write(Spin2Bytecode.bc_var_preinc_push);
                        source.add(new Bytecode(context, os.toByteArray(), String.format("PRE_INC (%d)%s", typeSize, " (push)")));
                    }
                    else if ("[--]".equalsIgnoreCase(pointerPreEffectNode.getText())) {
                        os.write(Spin2Bytecode.bc_var_predec_push);
                        source.add(new Bytecode(context, os.toByteArray(), String.format("PRE_DEC (%d)%s", typeSize, " (push)")));
                    }
                    else {
                        throw new CompilerException("invalid pointer pre effect " + pointerPreEffectNode.getText(), pointerPreEffectNode.getTokens());
                    }
                    if (push && postEffectNode == null) {
                        source.add(new MemoryOp(context, ss, MemoryOp.Base.Pop, MemoryOp.Op.Read, indexNode != null));
                    }
                }
                else if (pointerPostEffectNode != null) {
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

                    if ("[++]".equalsIgnoreCase(pointerPostEffectNode.getText())) {
                        os.write(push ? Spin2Bytecode.bc_var_postinc_push : Spin2Bytecode.bc_var_inc);
                        source.add(new Bytecode(context, os.toByteArray(), String.format("POST_INC (%d)%s", typeSize, push ? " (push)" : "")));
                    }
                    else if ("[--]".equalsIgnoreCase(pointerPostEffectNode.getText())) {
                        os.write(push ? Spin2Bytecode.bc_var_postdec_push : Spin2Bytecode.bc_var_dec);
                        source.add(new Bytecode(context, os.toByteArray(), String.format("POST_DEC (%d)%s", typeSize, push ? " (push)" : "")));
                    }
                    else {
                        throw new CompilerException("invalid post effect " + pointerPostEffectNode.getText(), pointerPostEffectNode.getTokens());
                    }
                    if (push && postEffectNode == null) {
                        source.add(new MemoryOp(context, ss, MemoryOp.Base.Pop, MemoryOp.Op.Read, indexNode != null));
                    }
                }
                else {
                    source.add(new VariableOp(context, VariableOp.Op.Read, popIndex, var, hasIndex, index));
                    if (postEffectNode == null) {
                        source.add(new MemoryOp(context, ss, MemoryOp.Base.Pop, MemoryOp.Op.Read, indexNode != null));
                    }
                }
                if (postEffectNode != null) {
                    source.add(new MemoryOp(context, ss, MemoryOp.Base.Pop, MemoryOp.Op.Setup, indexNode != null));
                    compilePostEffect(context, postEffectNode, source, push);
                    if (!push) {
                        node.setReturnLongs(0);
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
                    if (!push) {
                        node.setReturnLongs(0);
                    }
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
                    if (!push) {
                        node.setReturnLongs(0);
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

        if (!push && node.getReturnLongs() != 0) {
            logMessage(new CompilerException("expected assignment", node.getTokens()));
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
                    if (child.getText().startsWith("\"")) {
                        byte[] b = getString(child.getToken()).getBytes();
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
                        logMessage(new CompilerException("invalid argument", child.getToken()));
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

    List<Spin2Bytecode> compileStructure(Context context, Spin2Method method, Spin2StatementNode node, Expression expression, Spin2StatementNode pointerPreEffectNode, MemoryOp.Op op, Boolean push) {
        int offset = 0;
        int index = 0;
        int lastMemberSize = 0;
        String varType;
        boolean pointer;
        Spin2Struct struct = null;
        Spin2StatementNode varNode = node;

        List<Spin2StatementNode> indexNodes = new ArrayList<>();
        List<Integer> indexMultipliers = new ArrayList<>();
        Spin2StatementNode bitfieldNode = null;
        Spin2StatementNode postEffectNode = null;
        Spin2StatementNode pointerPostEffectNode = null;

        List<Spin2Bytecode> source = new ArrayList<>();

        if (isAddress(node.getText())) {
            op = MemoryOp.Op.Address;
        }

        MemoryOp.Base bb;
        if (expression instanceof Variable) {
            pointer = ((Variable) expression).isPointer();
            if (pointer) {
                bb = MemoryOp.Base.Pop;
            }
            else {
                bb = expression instanceof LocalVariable ? MemoryOp.Base.DBase : MemoryOp.Base.VBase;
            }
            varType = ((Variable) expression).getType();
        }
        else {
            pointer = false;
            bb = MemoryOp.Base.PBase;
            varType = ((DataVariable) expression).getType();
        }

        struct = context.getStructureDefinition(varType);
        if (struct == null && varType.startsWith("^")) {
            struct = context.getStructureDefinition(varType.substring(1));
        }
        int structSize = struct.getTypeSize();

        if ((node.getText().startsWith("@[") || node.getText().startsWith("@@[")) && node.getText().endsWith("]")) {
            int n = 0;
            if (n < node.getChildCount()) {
                logMessage(new CompilerException("syntax error", node.getChild(n).getTokens()));
            }
            VariableOp.Op varOp = VariableOp.Op.Address;
            if (node.getText().startsWith("@@")) {
                varOp = VariableOp.Op.PBaseAddress;
            }
            source.add(new VariableOp(context, varOp, false, (Variable) expression, false, 0));
            node.setReturnLongs(1);
            if (!push) {
                logMessage(new CompilerException("expected assignment", node.getTokens()));
            }
            return source;
        }
        else if ((node.getText().startsWith("[") || node.getText().startsWith("@[")) && node.getText().endsWith("]")) {
            int n = 0;
            if (!node.getText().contains(".")) {
                if (n < node.getChildCount() && isPostEffect(node.getChild(n))) {
                    pointerPostEffectNode = node.getChild(n++);
                }
            }
            if (n < node.getChildCount()) {
                logMessage(new CompilerException("syntax error", node.getChild(n).getTokens()));
            }

            if (pointerPostEffectNode != null) {
                if (op == MemoryOp.Op.Setup) {
                    logMessage(new CompilerException("syntax error", pointerPostEffectNode.getToken()));
                }
                if ("++".equalsIgnoreCase(pointerPostEffectNode.getText()) || "--".equalsIgnoreCase(pointerPostEffectNode.getText())) {
                    source.add(new VariableOp(context, VariableOp.Op.Setup, false, (Variable) expression, false, 0));
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    try {
                        if (structSize > 1) {
                            os.write(Spin2Bytecode.bc_set_incdec);
                            os.write(Constant.wrVar(structSize));
                        }
                        if ("++".equalsIgnoreCase(pointerPostEffectNode.getText())) {
                            os.write(push ? Spin2Bytecode.bc_var_postinc_push : Spin2Bytecode.bc_var_inc);
                            source.add(new Bytecode(context, os.toByteArray(), String.format("POST_INC (%d)%s", structSize, push ? " (push)" : "")));
                        }
                        else if ("--".equalsIgnoreCase(pointerPostEffectNode.getText())) {
                            os.write(push ? Spin2Bytecode.bc_var_postdec_push : Spin2Bytecode.bc_var_dec);
                            source.add(new Bytecode(context, os.toByteArray(), String.format("POST_DEC (%d)%s", structSize, push ? " (push)" : "")));
                        }
                    } catch (Exception e) {
                        // Do nothing
                    }
                }
                else if ("~".equalsIgnoreCase(pointerPostEffectNode.getText())) {
                    source.add(0, new Constant(context, new NumberLiteral(0)));
                    if (!push) {
                        source.add(new VariableOp(context, VariableOp.Op.Write, false, (Variable) expression, false, 0));
                    }
                    else {
                        source.add(new VariableOp(context, VariableOp.Op.Setup, false, (Variable) expression, false, 0));
                        source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_swap : Spin2Bytecode.bc_write, push ? "SWAP" : "WRITE"));
                    }
                }
                else if ("~~".equalsIgnoreCase(pointerPostEffectNode.getText())) {
                    source.add(0, new Constant(context, new NumberLiteral(-1)));
                    if (!push) {
                        source.add(new VariableOp(context, VariableOp.Op.Write, false, (Variable) expression, false, 0));
                    }
                    else {
                        source.add(new VariableOp(context, VariableOp.Op.Setup, false, (Variable) expression, false, 0));
                        source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_swap : Spin2Bytecode.bc_write, push ? "SWAP" : "WRITE"));
                    }
                }
                else if ("!!".equalsIgnoreCase(pointerPostEffectNode.getText())) {
                    source.add(new VariableOp(context, VariableOp.Op.Setup, false, (Variable) expression, false, 0));
                    source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_lognot_push : Spin2Bytecode.bc_var_lognot, "POST_LOGICAL_NOT" + (push ? " (push)" : "")));
                }
                else if ("!".equalsIgnoreCase(pointerPostEffectNode.getText())) {
                    source.add(new VariableOp(context, VariableOp.Op.Setup, false, (Variable) expression, false, 0));
                    source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_bitnot_push : Spin2Bytecode.bc_var_bitnot, "POST_NOT" + (push ? " (push)" : "")));
                }
                else {
                    logMessage(new CompilerException("unhandled pointer post effect", pointerPostEffectNode.getToken()));
                }
                node.setReturnLongs(push ? 1 : 0);
            }
            else {
                VariableOp.Op varOp;
                if (op == MemoryOp.Op.Read) {
                    varOp = VariableOp.Op.Read;
                }
                else if (op == MemoryOp.Op.Write) {
                    varOp = VariableOp.Op.Write;
                }
                else if (op == MemoryOp.Op.Address) {
                    varOp = VariableOp.Op.Address;
                }
                else {
                    varOp = VariableOp.Op.Setup;
                }
                source.add(new VariableOp(context, varOp, false, (Variable) expression, false, 0));
                node.setReturnLongs(1);
                if (!push && op == MemoryOp.Op.Read) {
                    logMessage(new CompilerException("expected assignment", node.getTokens()));
                }
            }

            return source;
        }

        lastMemberSize = struct.getTypeSize();

        int n = 0;
        if (!varNode.getText().contains(".")) {
            if (n < varNode.getChildCount()) {
                if ("[++]".equals(varNode.getChild(n).getText()) || "[--]".equalsIgnoreCase(varNode.getChild(n).getText())) {
                    pointerPostEffectNode = varNode.getChild(n++);
                }
            }
        }

        while (true) {
            String[] ar = varNode.getText().split("[\\.]");
            for (int i = 1; i < ar.length; i++) {
                Spin2StructMember member = struct.getMember(ar[i]);
                if (member == null) {
                    throw new CompilerException("undefined symbol " + varNode.getText(), varNode.getToken());
                }

                offset += member.getOffset();

                varType = member.getType() != null ? member.getType().getText() : "LONG";
                struct = member.getStructureDefinition();

                if (struct != null) {
                    lastMemberSize = struct.getTypeSize();
                }
                else {
                    switch (varType.toUpperCase()) {
                        case "BYTE":
                            lastMemberSize = 1;
                            break;
                        case "WORD":
                            lastMemberSize = 2;
                            break;
                        default:
                            lastMemberSize = 4;
                            break;
                    }
                }
            }

            if (n < varNode.getChildCount() && (varNode.getChild(n) instanceof Spin2StatementNode.Index)) {
                boolean constantIndex = false;
                Spin2StatementNode idxNode = varNode.getChild(n++);
                try {
                    Expression exp = buildConstantExpression(context, idxNode);
                    if (exp.isConstant()) {
                        index += exp.getNumber().intValue() * lastMemberSize;
                        constantIndex = true;
                    }
                } catch (Exception e) {
                    // Do nothing
                }
                if (!constantIndex) {
                    indexNodes.add(idxNode);
                    indexMultipliers.add(0, lastMemberSize);
                }
            }

            if (n >= varNode.getChildCount()) {
                break;
            }
            if (isPostEffect(varNode.getChild(n))) {
                break;
            }
            if (".".equals(varNode.getChild(n).getText())) {
                if (node.getText().startsWith("@")) {
                    throw new CompilerException("bitfield expression not allowed", varNode.getChild(n).getToken());
                }
                n++;
                if (n >= varNode.getChildCount()) {
                    throw new RuntimeException("expected bitfield expression");
                }
                if (!(varNode.getChild(n) instanceof Spin2StatementNode.Index)) {
                    throw new RuntimeException("syntax error");
                }
                bitfieldNode = varNode.getChild(n++);
                break;
            }

            if (struct == null) {
                break;
            }

            varNode = varNode.getChild(n);
            n = 0;
        }

        if (varNode.isMethod()) {
            if (push == null) {
                push = varNode.getReturnLongs() != 0;
            }
            if (push) {
                source.add(new Bytecode(context, Spin2Bytecode.bc_drop_push, "ANCHOR (push)"));
            }
            else {
                source.add(new Bytecode(context, Spin2Bytecode.bc_drop, "ANCHOR"));
            }

            while (n < varNode.getChildCount()) {
                if (!(varNode.getChild(n) instanceof Spin2StatementNode.Argument)) {
                    throw new CompilerException("syntax error", varNode.getChild(n).getTokens());
                }
                source.addAll(compileConstantExpression(context, method, varNode.getChild(n++)));
            }
        }
        else {
            if (n < varNode.getChildCount() && isPostEffect(varNode.getChild(n))) {
                postEffectNode = varNode.getChild(n++);
            }
        }

        if (n < varNode.getChildCount()) {
            throw new CompilerException("unexpected '" + varNode.getChild(n).getText() + "'", varNode.getChild(n).getTokens());
        }

        int bitfield = -1;
        if (bitfieldNode != null) {
            if (struct != null) {
                throw new CompilerException("bitfield not allowed", bitfieldNode.getTokens());
            }
            bitfield = compileBitfield(context, method, bitfieldNode, source);
        }

        MemoryOp.Size ss;
        switch (varType.toUpperCase()) {
            case "BYTE":
                ss = MemoryOp.Size.Byte;
                break;
            case "WORD":
                ss = MemoryOp.Size.Word;
                break;
            default:
                ss = MemoryOp.Size.Long;
                break;
        }

        if (indexNodes.size() > 3) {
            throw new CompilerException("too many nested indexes", varNode.getTokens());
        }

        if (!varNode.isMethod()) {
            node.setReturnLongs(struct != null ? (struct.getTypeSize() + 3) / 4 : 1);
        }

        if (struct != null || indexNodes.size() > 0 || postEffectNode != null || pointerPostEffectNode != null || bb == MemoryOp.Base.Pop) {
            for (Spin2StatementNode idxNode : indexNodes) {
                source.addAll(compileBytecodeExpression(context, method, idxNode, true));
            }

            if (pointerPreEffectNode != null) {
                source.add(new VariableOp(context, VariableOp.Op.Setup, false, (Variable) expression, false, 0));

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                try {
                    if (structSize > 1) {
                        os.write(Spin2Bytecode.bc_set_incdec);
                        os.write(Constant.wrVar(structSize));
                    }
                    if ("[++]".equalsIgnoreCase(pointerPreEffectNode.getText())) {
                        os.write(Spin2Bytecode.bc_var_preinc_push);
                        source.add(new Bytecode(context, os.toByteArray(), String.format("PRE_INC (%d)%s", structSize, " (push)")));
                    }
                    else if ("[--]".equalsIgnoreCase(pointerPreEffectNode.getText())) {
                        os.write(Spin2Bytecode.bc_var_predec_push);
                        source.add(new Bytecode(context, os.toByteArray(), String.format("PRE_DEC (%d)%s", structSize, " (push)")));
                    }
                    else {
                        logMessage(new CompilerException("invalid pointer pre effect '" + postEffectNode.getText() + "'", postEffectNode.getToken()));
                    }
                } catch (Exception e) {
                    // Do nothing
                }
            }
            else if (pointerPostEffectNode != null) {
                source.add(new VariableOp(context, VariableOp.Op.Setup, false, (Variable) expression, false, 0));

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                try {
                    if (structSize > 1) {
                        os.write(Spin2Bytecode.bc_set_incdec);
                        os.write(Constant.wrVar(structSize));
                    }
                    if ("[++]".equalsIgnoreCase(pointerPostEffectNode.getText())) {
                        os.write(Spin2Bytecode.bc_var_postinc_push);
                        source.add(new Bytecode(context, os.toByteArray(), String.format("POST_INC (%d)%s", structSize, " (push)")));
                    }
                    else if ("[--]".equalsIgnoreCase(pointerPostEffectNode.getText())) {
                        os.write(Spin2Bytecode.bc_var_postdec_push);
                        source.add(new Bytecode(context, os.toByteArray(), String.format("POST_DEC (%d)%s", structSize, " (push)")));
                    }
                    else {
                        logMessage(new CompilerException("invalid pointer post effect '" + postEffectNode.getText() + "'", postEffectNode.getToken()));
                    }
                } catch (Exception e) {
                    // Do nothing
                }
            }
            else if (bb == MemoryOp.Base.Pop) {
                source.add(new VariableOp(context, VariableOp.Op.Read, false, (Variable) expression, false, 0));
            }

            if (push || (pointerPostEffectNode == null && pointerPostEffectNode == null) || op == MemoryOp.Op.Write) {
                MemoryOp.Op sOp = (bitfieldNode != null || postEffectNode != null) ? MemoryOp.Op.Setup : op;
                if (op == MemoryOp.Op.Address) {
                    switch (varType.toUpperCase()) {
                        case "BYTE":
                        case "WORD":
                        case "LONG":
                            sOp = MemoryOp.Op.Setup;
                            break;
                        default:
                            sOp = MemoryOp.Op.Address;
                            break;
                    }
                }
                source.add(new StructOp(context, sOp, bb, ss, struct, expression, index + offset, indexMultipliers, push));

                if (bitfieldNode != null) {
                    BitField.Op bfOp = op == MemoryOp.Op.Read ? BitField.Op.Read : BitField.Op.Write;
                    source.add(new BitField(context, postEffectNode == null ? bfOp : BitField.Op.Setup, bitfield));
                }

                if (op == MemoryOp.Op.Address) {
                    if (sOp == MemoryOp.Op.Setup) {
                        source.add(new Bytecode(context, Spin2Bytecode.bc_get_addr, "ADDRESS"));
                    }
                    node.setReturnLongs(1);
                }
            }
        }
        else if (expression instanceof Variable) {
            int address = ((Variable) expression).getOffset() + index + offset;
            if ("LONG".equalsIgnoreCase(varType) && (address % 4) == 0) {
                VariableOp.Op varOp;
                if (op == MemoryOp.Op.Read) {
                    varOp = VariableOp.Op.Read;
                }
                else if (op == MemoryOp.Op.Write) {
                    varOp = VariableOp.Op.Write;
                }
                else if (op == MemoryOp.Op.Address) {
                    varOp = VariableOp.Op.Address;
                }
                else {
                    varOp = VariableOp.Op.Setup;
                }
                source.add(new VariableOp(context, (bitfieldNode != null || postEffectNode != null) ? VariableOp.Op.Setup : varOp, false, (Variable) expression, false, (index + offset) / 4));
            }
            else {
                source.add(new MemoryOp(context, ss, bb, (bitfieldNode != null || postEffectNode != null) ? MemoryOp.Op.Setup : op, expression, index + offset));
            }
            if (bitfieldNode != null) {
                BitField.Op bfOp = op == MemoryOp.Op.Read ? BitField.Op.Read : BitField.Op.Write;
                source.add(new BitField(context, postEffectNode == null ? bfOp : BitField.Op.Setup, bitfield));
            }
        }
        else {
            source.add(new MemoryOp(context, ss, bb, (bitfieldNode != null || postEffectNode != null) ? MemoryOp.Op.Setup : op, expression, index + offset));
            if (bitfieldNode != null) {
                BitField.Op bfOp = op == MemoryOp.Op.Read ? BitField.Op.Read : BitField.Op.Write;
                source.add(new BitField(context, postEffectNode == null ? bfOp : BitField.Op.Setup, bitfield));
            }
        }

        if (varNode.isMethod()) {
            source.add(new Bytecode(context, new byte[] {
                (byte) Spin2Bytecode.bc_call_ptr,
            }, "CALL_PTR"));

            if (push && node.getReturnLongs() == 0) {
                logMessage(new CompilerException("method doesn't return any value", node.getToken()));
            }
            node.setReturnLongs(push ? node.getReturnLongs() : 0);
        }
        else {
            if (postEffectNode != null) {
                if (struct != null) {
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
                        source.add(new Bytecode(context, os.toByteArray(), "BYTEFILL"));
                    } catch (Exception e) {
                        // Do nothing
                    }
                    node.setReturnLongs(0);
                    if (push) {
                        logMessage(new CompilerException("expression doesn't return any value", node.getTokens()));
                    }
                }
                else {
                    compilePostEffect(context, postEffectNode, source, push);
                    node.setReturnLongs(push ? 1 : 0);
                }
            }
            else if (op == MemoryOp.Op.Read && !push && pointerPostEffectNode == null && pointerPostEffectNode == null) {
                logMessage(new CompilerException("expected assignment", node.getTokens()));
            }
        }

        if (expression instanceof Variable) {
            ((Variable) expression).setCalledBy(method);
        }

        return source;
    }

    public List<Spin2Bytecode> compileString(Context context, Spin2StatementNode node) {
        List<Spin2Bytecode> source = new ArrayList<Spin2Bytecode>();

        Token token = node.getToken();
        String s = getString(token);
        byte[] data = s.getBytes();

        if (token.getText().startsWith("@\\")) {
            int i = 0, ch;

            ByteArrayOutputStream ar = new ByteArrayOutputStream();
            while (i < data.length) {
                int c = data[i++] & 0xFF;
                if (c == '\\' && i < data.length) {
                    switch (data[i]) {
                        case 'a':
                        case 'A':
                            ar.write(7);
                            break;
                        case 'b':
                        case 'B':
                            ar.write(8);
                            break;
                        case 't':
                        case 'T':
                            ar.write(9);
                            break;
                        case 'n':
                        case 'N':
                            ar.write(10);
                            break;
                        case 'f':
                        case 'F':
                            ar.write(12);
                            break;
                        case 'r':
                        case 'R':
                            ar.write(13);
                            break;
                        case '\\':
                            ar.write('\\');
                            break;
                        case 'x':
                        case 'X':
                            ch = 0;
                            if (i + 1 < data.length) {
                                ch = hexVal(data[i + 1] & 0xFF);
                                if (ch != -1) {
                                    i++;
                                    if (i + 1 < data.length) {
                                        c = hexVal(data[i + 1] & 0xFF);
                                        if (c != -1) {
                                            i++;
                                            ch = (ch << 4) | c;
                                        }
                                    }
                                    ar.write(ch);
                                    break;
                                }
                                else if (data[i + 1] != '\\') {
                                    logMessage(new CompilerException(CompilerException.WARNING, "string contains an invalid hex number", node.getToken()));
                                }
                            }
                            // Fall-through
                        default:
                            ar.write('\\');
                            ar.write(data[i]);
                            break;
                    }
                    i++;
                }
                else {
                    ar.write(c);
                }
            }
            data = ar.toByteArray();

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            os.write(Spin2Bytecode.bc_string);
            os.write(data.length + 1);
            os.writeBytes(data);
            os.write(0x00);
            source.add(new Bytecode(context, os.toByteArray(), "STRING"));
        }
        else if (token.getText().startsWith("@")) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            os.write(Spin2Bytecode.bc_string);
            os.write(data.length + 1);
            os.writeBytes(data);
            os.write(0);
            source.add(new Bytecode(context, os.toByteArray(), "STRING"));
        }
        else if (token.getText().startsWith("%")) {
            if (data.length > 4) {
                logMessage(new CompilerException("no more than 4 characters can be packed into a long", node.getToken()));
            }
            long value = 0;
            int i = data.length - 1;
            while (i >= 0) {
                value <<= 8;
                value |= data[i] & 0xFF;
                i--;
            }
            source.add(new Bytecode(context, Constant.wrAuto(value), "CONSTANT (" + node.getText() + ")"));
        }
        else {
            if (s.length() == 1) {
                Expression expression = new CharacterLiteral(s);
                source.add(new Constant(context, expression));
            }
            else {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                os.write(Spin2Bytecode.bc_string);
                os.write(data.length + 1);
                os.writeBytes(data);
                os.write(0);
                source.add(new Bytecode(context, os.toByteArray(), "STRING"));
            }
        }

        return source;
    }

    String getString(Token token) {
        int i;
        String s = token.getText();

        if ((i = s.indexOf('"')) != -1) {
            s = s.substring(i + 1);
            if ((i = s.lastIndexOf('"')) != -1) {
                s = s.substring(0, i);
            }
            else {
                logMessage(new CompilerException("unterminated string", token));
            }
        }

        return s;
    }

    int hexVal(int c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        else if (c >= 'A' && c <= 'F') {
            return (c - 'A') + 10;
        }
        else if (c >= 'a' && c <= 'f') {
            return (c - 'a') + 10;
        }
        return -1;
    }

}
