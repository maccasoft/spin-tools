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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.BitField;

import com.maccasoft.propeller.spin1.bytecode.Abs;
import com.maccasoft.propeller.spin1.bytecode.Add;
import com.maccasoft.propeller.spin1.bytecode.AddAssign;
import com.maccasoft.propeller.spin1.bytecode.And;
import com.maccasoft.propeller.spin1.bytecode.BooleanAnd;
import com.maccasoft.propeller.spin1.bytecode.BooleanNot;
import com.maccasoft.propeller.spin1.bytecode.BooleanOr;
import com.maccasoft.propeller.spin1.bytecode.Cogid;
import com.maccasoft.propeller.spin1.bytecode.Coginit;
import com.maccasoft.propeller.spin1.bytecode.Decode;
import com.maccasoft.propeller.spin1.bytecode.Divide;
import com.maccasoft.propeller.spin1.bytecode.Encode;
import com.maccasoft.propeller.spin1.bytecode.LimitMax;
import com.maccasoft.propeller.spin1.bytecode.LimitMin;
import com.maccasoft.propeller.spin1.bytecode.Modulo;
import com.maccasoft.propeller.spin1.bytecode.Multiply;
import com.maccasoft.propeller.spin1.bytecode.MultiplyUpper;
import com.maccasoft.propeller.spin1.bytecode.Negate;
import com.maccasoft.propeller.spin1.bytecode.Not;
import com.maccasoft.propeller.spin1.bytecode.Or;
import com.maccasoft.propeller.spin1.bytecode.Return;
import com.maccasoft.propeller.spin1.bytecode.Rev;
import com.maccasoft.propeller.spin1.bytecode.Sar;
import com.maccasoft.propeller.spin1.bytecode.Sqr;
import com.maccasoft.propeller.spin1.bytecode.Subtract;
import com.maccasoft.propeller.spin1.bytecode.TestAbove;
import com.maccasoft.propeller.spin1.bytecode.TestAboveOrEqual;
import com.maccasoft.propeller.spin1.bytecode.TestBelow;
import com.maccasoft.propeller.spin1.bytecode.TestBelowOrEqual;
import com.maccasoft.propeller.spin1.bytecode.TestEqual;
import com.maccasoft.propeller.spin1.bytecode.TestNotEqual;
import com.maccasoft.propeller.spin1.bytecode.VarWrite;
import com.maccasoft.propeller.spin1.bytecode.Xor;

public abstract class Spin1BytecodeInstructionFactory {

    public static final BitField xxx = new BitField(0b000_111_00);

    static Map<String, Spin1BytecodeInstructionFactory> symbols = new HashMap<String, Spin1BytecodeInstructionFactory>();
    static {
        symbols.put("|>", new LimitMin()); //  limit minimum (signed)
        symbols.put("<|", new LimitMax()); //  limit maximum (signed)
        symbols.put("-", new Negate()); //   negate
        symbols.put("!", new Not()); //   bitwise not
        symbols.put("&", new And()); //   bitwise and
        symbols.put("||", new Abs()); //  absolute
        symbols.put("|", new Or()); //   bitwise or
        symbols.put("^", new Xor()); //   bitwise xor
        symbols.put("+", new Add()); //   add
        symbols.put("-", new Subtract()); //   subtract
        symbols.put("~>", new Sar()); //  shift arithmetic right
        symbols.put("><", new Rev()); //  reverse bits
        symbols.put("AND", new BooleanAnd()); // boolean and
        symbols.put(">|", new Encode()); //  encode (0-32)
        symbols.put("OR", new BooleanOr()); //  boolean or
        symbols.put("|<", new Decode()); //  decode
        symbols.put("*", new Multiply()); //   multiply, return lower half (signed)
        symbols.put("**", new MultiplyUpper()); //  multiply, return upper half (signed)
        symbols.put("/", new Divide()); //   divide, return quotient (signed)
        symbols.put("//", new Modulo()); //  divide, return remainder (signed)
        symbols.put("^^", new Sqr()); //  square root
        symbols.put("<", new TestBelow()); //   test below (signed)
        symbols.put(">", new TestAbove()); //   test above (signed)
        symbols.put("<>", new TestNotEqual()); //  test not equal
        symbols.put("==", new TestEqual()); //  test equal
        symbols.put("=<", new TestBelowOrEqual()); //  test below or equal (signed)
        symbols.put("=>", new TestAboveOrEqual()); //  test above or equal (signed)
        symbols.put("NOT", new BooleanNot()); // boolean not

        symbols.put(":=", new VarWrite());
        symbols.put("+=", new AddAssign());

        symbols.put("COGID", new Cogid());
        symbols.put("COGINIT", new Coginit());
        symbols.put("RETURN", new Return());
    }

    public static Spin1BytecodeInstructionFactory get(String mnemonic) {
        return symbols.get(mnemonic.toUpperCase());
    }

    public Spin1BytecodeInstructionFactory() {

    }

    public List<Spin1BytecodeLine> expand(Spin1BytecodeLine line) {
        List<Spin1BytecodeLine> list = new ArrayList<Spin1BytecodeLine>();

        List<Spin1BytecodeExpression> arguments = new ArrayList<>(line.getArguments());
        line.arguments.clear();
        for (Spin1BytecodeExpression exp : arguments) {
            if (",".equals(exp.getText()) || "(".equals(exp.getText())) {
                line.arguments.addAll(exp.getChilds());
            }
            else {
                line.arguments.add(exp);
            }
        }

        for (int i = 0; i < line.getArgumentCount(); i++) {
            Spin1BytecodeExpression expression = line.getArgument(i);
            Spin1BytecodeLine newLine = new Spin1BytecodeLine(line.getScope(), null, expression.getText(), expression.getChilds());
            list.addAll(newLine.expand());
        }
        if (!",".equals(line.getMnemonic()) && !"(".equals(line.getMnemonic())) {
            list.add(line);
        }

        return list;
    }

    public List<Spin1BytecodeLine> expand1(Spin1BytecodeLine line) {
        List<Spin1BytecodeLine> list = new ArrayList<Spin1BytecodeLine>();

        if (line.getArgumentCount() == 1 && ",".equals(line.getMnemonic())) {
            line.arguments = new ArrayList<Spin1BytecodeExpression>(line.getArgument(0).getChilds());
        }

        if (line.getArgumentCount() != 0) {
            for (int i = 0; i < line.getArgumentCount(); i++) {
                Spin1BytecodeExpression expression = line.getArgument(i);
                Spin1BytecodeLine newLine = new Spin1BytecodeLine(new Spin1Context(line.getScope()), null, expression.getText(), expression.getChilds());
                list.addAll(newLine.expand());
            }

            List<Spin1BytecodeExpression> arguments = new ArrayList<Spin1BytecodeExpression>();
            arguments.add(line.getArgument(0));
            arguments.add(line.getArgument(1));
            list.add(new Spin1BytecodeLine(line.getScope(), line.getLabel(), line.getMnemonic(), arguments));
        }
        else {
            list.add(line);
        }

        return list;
    }

    public Spin1BytecodeInstructionObject createObject(Spin1BytecodeLine line) {
        return null;
    }

}
