/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1.bytecode;

import java.util.HashMap;
import java.util.Map;

import com.maccasoft.propeller.spin1.Spin1Bytecode;
import com.maccasoft.propeller.spin1.Spin1Context;

public class MathOp extends Spin1Bytecode {

    public static class Descriptor {
        int value;
        String text;

        public Descriptor(int value, String text) {
            this.value = value;
            this.text = text;
        }
    }

    static Map<String, Descriptor> operations = new HashMap<String, Descriptor>();
    static {
        operations.put("->", new Descriptor(0b111_00000, "ROTATE_RIGHT")); //  rotate right
        operations.put("<-", new Descriptor(0b111_00001, "ROTATE_LEFT")); //  rotate left
        operations.put(">>", new Descriptor(0b111_00010, "SHIFT_RIGHT")); //  shift right
        operations.put("<<", new Descriptor(0b111_00011, "SHIFT_LEFT")); //  shift left
        operations.put("#>", new Descriptor(0b111_00100, "LIMIT_MINIMUM")); //  limit minimum (signed)
        operations.put("<#", new Descriptor(0b111_00101, "LIMIT_MAXIMUM")); //  limit maximum (signed)
        //operations.put("-", new Descriptor(0b111_00110, "NEGATE")); //   negate
        operations.put("!", new Descriptor(0b111_00111, "BITNOT")); //   bitwise not
        operations.put("&", new Descriptor(0b111_01000, "BITAND")); //   bitwise and
        operations.put("||", new Descriptor(0b111_01001, "ABS")); //  absolute
        operations.put("|", new Descriptor(0b111_01010, "BITOR")); //   bitwise or
        operations.put("^", new Descriptor(0b111_01011, "BITXOR")); //   bitwise xor
        operations.put("+", new Descriptor(0b111_01100, "ADD")); //   add
        operations.put("-", new Descriptor(0b111_01101, "SUBTRACT")); //   subtract
        operations.put("~>", new Descriptor(0b111_01110, "SAR")); //  shift arithmetic right
        operations.put("><", new Descriptor(0b111_01111, "REV")); //  reverse bits
        operations.put("AND", new Descriptor(0b111_10000, "BOOLEAN_AND")); // boolean and
        operations.put(">|", new Descriptor(0b111_10001, "ENCODE")); //  encode (0-32)
        operations.put("OR", new Descriptor(0b111_10010, "BOOLEAN_OR")); //  boolean or
        operations.put("|<", new Descriptor(0b111_10011, "DECODE")); //  decode
        operations.put("*", new Descriptor(0b111_10100, "MULTIPLY")); //   multiply, return lower half (signed)
        operations.put("**", new Descriptor(0b111_10101, "MULTIPLY_UPPER")); //  multiply, return upper half (signed)
        operations.put("/", new Descriptor(0b111_10110, "DIVIDE")); //   divide, return quotient (signed)
        operations.put("//", new Descriptor(0b111_10111, "REMAINDER")); //  divide, return remainder (signed)
        operations.put("^^", new Descriptor(0b111_11000, "SQR")); //  square root
        operations.put("<", new Descriptor(0b111_11001, "TEST_BELOW")); //   test below (signed)
        operations.put(">", new Descriptor(0b111_11010, "TEST_ABOVE")); //   test above (signed)
        operations.put("<>", new Descriptor(0b111_11011, "TEST_NOT_EQUAL")); //  test not equal
        operations.put("==", new Descriptor(0b111_11100, "TEST_EQUAL")); //  test equal
        operations.put("=<", new Descriptor(0b111_11101, "TEST_BELOW_OR_EQUAL")); //  test below or equal (signed)
        operations.put("=>", new Descriptor(0b111_11110, "TEST_ABOVE_OR_EQUAL")); //  test above or equal (signed)
        operations.put("NOT", new Descriptor(0b111_11111, "BOOLEAN_NOT")); // boolean not
    }

    public static boolean isMathOp(String s) {
        return operations.containsKey(s.toUpperCase());
    }

    static Map<String, Descriptor> assignOperations = new HashMap<String, Descriptor>();
    static {
        assignOperations.put("->=", new Descriptor(0b010_00000, "ROTATE_RIGHT")); //  rotate right
        assignOperations.put("<-=", new Descriptor(0b010_00001, "ROTATE_LEFT")); //  rotate left
        assignOperations.put(">>=", new Descriptor(0b010_00010, "SHIFT_RIGHT")); //  shift right
        assignOperations.put("<<=", new Descriptor(0b010_00011, "SHIFT_LEFT")); //  shift left
        assignOperations.put("#>=", new Descriptor(0b010_00100, "LIMIT_MINIMUM")); //  limit minimum (signed)
        assignOperations.put("<#=", new Descriptor(0b010_00101, "LIMIT_MAXIMUM")); //  limit maximum (signed)
        //assignOperations.put("-=", new Descriptor(0b010_00110, "NEGATE")); //   negate
        assignOperations.put("!=", new Descriptor(0b010_00111, "BITNOT")); //   bitwise not
        assignOperations.put("&=", new Descriptor(0b010_01000, "BITAND")); //   bitwise and
        assignOperations.put("||=", new Descriptor(0b010_01001, "ABS")); //  absolute
        assignOperations.put("|=", new Descriptor(0b010_01010, "BITOR")); //   bitwise or
        assignOperations.put("^=", new Descriptor(0b010_01011, "BITXOR")); //   bitwise xor
        assignOperations.put("+=", new Descriptor(0b010_01100, "ADD")); //   add
        assignOperations.put("-=", new Descriptor(0b010_01101, "SUBTRACT")); //   subtract
        assignOperations.put("~>=", new Descriptor(0b010_01110, "SAR")); //  shift arithmetic right
        assignOperations.put("><=", new Descriptor(0b010_01111, "REV")); //  reverse bits
        assignOperations.put("AND=", new Descriptor(0b010_10000, "BOOLEAN_AND")); // boolean and
        assignOperations.put(">|=", new Descriptor(0b010_10001, "ENCODE")); //  encode (0-32)
        assignOperations.put("OR=", new Descriptor(0b010_10010, "BOOLEAN_OR")); //  boolean or
        assignOperations.put("|<=", new Descriptor(0b010_10011, "DECODE")); //  decode
        assignOperations.put("*=", new Descriptor(0b010_10100, "MULTIPLY")); //   multiply, return lower half (signed)
        assignOperations.put("**=", new Descriptor(0b010_10101, "MULTIPLY_UPPER")); //  multiply, return upper half (signed)
        assignOperations.put("/=", new Descriptor(0b010_10110, "DIVIDE")); //   divide, return quotient (signed)
        assignOperations.put("//=", new Descriptor(0b010_10111, "REMAINDER")); //  divide, return remainder (signed)
        assignOperations.put("^^=", new Descriptor(0b010_11000, "SQR")); //  square root
        assignOperations.put("<=", new Descriptor(0b010_11001, "TEST_BELOW")); //   test below (signed)
        assignOperations.put(">=", new Descriptor(0b010_11010, "TEST_ABOVE")); //   test above (signed)
        assignOperations.put("<>=", new Descriptor(0b010_11011, "TEST_NOT_EQUAL")); //  test not equal
        assignOperations.put("===", new Descriptor(0b010_11100, "TEST_EQUAL")); //  test equal
        assignOperations.put("=<=", new Descriptor(0b010_11101, "TEST_BELOW_OR_EQUAL")); //  test below or equal (signed)
        assignOperations.put("=>=", new Descriptor(0b010_11110, "TEST_ABOVE_OR_EQUAL")); //  test above or equal (signed)
        assignOperations.put("NOT=", new Descriptor(0b010_11111, "BOOLEAN_NOT")); // boolean not
    }

    public static boolean isAssignMathOp(String s) {
        return assignOperations.containsKey(s.toUpperCase());
    }

    public static Descriptor getOperationDescriptor(String s) {
        Descriptor desc = operations.get(s.toUpperCase());
        if (desc == null) {
            desc = assignOperations.get(s.toUpperCase());
        }
        return desc;
    }

    Descriptor op;
    boolean push;

    public MathOp(Spin1Context context, String op, boolean push) {
        super(context);
        this.op = getOperationDescriptor(op);
        this.push = push;
    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public byte[] getBytes() {
        return new byte[] {
            (byte) (op.value | (push ? 0b10000000 : 0b00000000))
        };
    }

    @Override
    public String toString() {
        return op.text;
    }

}
