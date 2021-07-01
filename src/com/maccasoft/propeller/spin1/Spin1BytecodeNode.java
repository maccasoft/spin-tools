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

import com.maccasoft.propeller.model.Token;

public class Spin1BytecodeNode {

    int type;
    String text;
    List<Spin1BytecodeNode> childs = new ArrayList<Spin1BytecodeNode>();

    static Map<String, Integer> mathOps = new HashMap<String, Integer>();
    static {
        mathOps.put("->", 0b111_00000); //  rotate right
        mathOps.put("<-", 0b111_00001); //  rotate left
        mathOps.put(">>", 0b111_00010); //  shift right
        mathOps.put("<<", 0b111_00011); //  shift left
        mathOps.put("|>", 0b111_00100); //  limit minimum (signed)
        mathOps.put("<|", 0b111_00101); //  limit maximum (signed)
        //mathOps.put("-", 0b111_00110); //   negate
        mathOps.put("!", 0b111_00111); //   bitwise not
        mathOps.put("&", 0b111_01000); //   bitwise and
        mathOps.put("||", 0b111_01001); //  absolute
        mathOps.put("|", 0b111_01010); //   bitwise or
        mathOps.put("^", 0b111_01011); //   bitwise xor
        mathOps.put("+", 0b111_01100); //   add
        mathOps.put("-", 0b111_01101); //   subtract
        mathOps.put("~>", 0b111_01110); //  shift arithmetic right
        mathOps.put("><", 0b111_01111); //  reverse bits
        mathOps.put("AND", 0b111_10000); // boolean and
        mathOps.put(">|", 0b111_10001); //  encode (0-32)
        mathOps.put("OR", 0b111_10010); //  boolean or
        mathOps.put("|<", 0b111_10011); //  decode
        mathOps.put("*", 0b111_10100); //   multiply, return lower half (signed)
        mathOps.put("**", 0b111_10101); //  multiply, return upper half (signed)
        mathOps.put("/", 0b111_10110); //   divide, return quotient (signed)
        mathOps.put("//", 0b111_10111); //  divide, return remainder (signed)
        mathOps.put("^^", 0b111_11000); //  square root
        mathOps.put("<", 0b111_11001); //   test below (signed)
        mathOps.put(">", 0b111_11010); //   test above (signed)
        mathOps.put("<>", 0b111_11011); //  test not equal
        mathOps.put("==", 0b111_11100); //  test equal
        mathOps.put("=<", 0b111_11101); //  test below or equal (signed)
        mathOps.put("=>", 0b111_11110); //  test above or equal (signed)
        mathOps.put("NOT", 0b111_11111); // boolean not
    }

    static Map<String, Integer> assignMathOps = new HashMap<String, Integer>();
    static {
        assignMathOps.put("->=", 0b010_00000); //  rotate right
        assignMathOps.put("<-=", 0b010_00001); //  rotate left
        assignMathOps.put(">>=", 0b010_00010); //  shift right
        assignMathOps.put("<<=", 0b010_00011); //  shift left
        assignMathOps.put("|>=", 0b010_00100); //  limit minimum (signed)
        assignMathOps.put("<|=", 0b010_00101); //  limit maximum (signed)
        //assignMathOps.put("-=", 0b010_00110); //   negate
        assignMathOps.put("!=", 0b010_00111); //   bitwise not
        assignMathOps.put("&=", 0b010_01000); //   bitwise and
        assignMathOps.put("||=", 0b010_01001); //  absolute
        assignMathOps.put("|=", 0b010_01010); //   bitwise or
        assignMathOps.put("^=", 0b010_01011); //   bitwise xor
        assignMathOps.put("+=", 0b010_01100); //   add
        assignMathOps.put("-=", 0b010_01101); //   subtract
        assignMathOps.put("~>=", 0b010_01110); //  shift arithmetic right
        assignMathOps.put("><=", 0b010_01111); //  reverse bits
        assignMathOps.put("AND=", 0b010_10000); // boolean and
        assignMathOps.put(">|=", 0b010_10001); //  encode (0-32)
        assignMathOps.put("OR=", 0b010_10010); //  boolean or
        assignMathOps.put("|<=", 0b010_10011); //  decode
        assignMathOps.put("*=", 0b010_10100); //   multiply, return lower half (signed)
        assignMathOps.put("**=", 0b010_10101); //  multiply, return upper half (signed)
        assignMathOps.put("/=", 0b010_10110); //   divide, return quotient (signed)
        assignMathOps.put("//=", 0b010_10111); //  divide, return remainder (signed)
        assignMathOps.put("^^=", 0b010_11000); //  square root
        assignMathOps.put("<=", 0b010_11001); //   test below (signed)
        assignMathOps.put(">=", 0b010_11010); //   test above (signed)
        assignMathOps.put("<>=", 0b010_11011); //  test not equal
        assignMathOps.put("===", 0b010_11100); //  test equal
        assignMathOps.put("=<=", 0b010_11101); //  test below or equal (signed)
        assignMathOps.put("=>=", 0b010_11110); //  test above or equal (signed)
        assignMathOps.put("NOT=", 0b010_11111); // boolean not
    }

    static Map<Integer, String> mathOpsText = new HashMap<Integer, String>();
    static {
        mathOpsText.put(0b000_00000, "ROTATE_RIGHT"); //  rotate right
        mathOpsText.put(0b000_00001, "ROTATE_LEFT"); //  rotate left
        mathOpsText.put(0b000_00010, "SHIFT_RIGHT"); //  shift right
        mathOpsText.put(0b000_00011, "SHIFT_LEFT"); //  shift left
        mathOpsText.put(0b000_00100, "LIMIT_MINIMUM"); //  limit minimum (signed)
        mathOpsText.put(0b000_00101, "LIMIT_MAXIMUM"); //  limit maximum (signed)
        mathOpsText.put(0b000_00110, "NEGATE"); //   negate
        mathOpsText.put(0b000_00111, "BITNOT"); //   bitwise not
        mathOpsText.put(0b000_01000, "BITAND"); //   bitwise and
        mathOpsText.put(0b000_01001, "ABS"); //  absolute
        mathOpsText.put(0b000_01010, "BITOR"); //   bitwise or
        mathOpsText.put(0b000_01011, "BITXOR"); //   bitwise xor
        mathOpsText.put(0b000_01100, "ADD"); //   add
        mathOpsText.put(0b000_01101, "SUBTRACT"); //   subtract
        mathOpsText.put(0b000_01110, "SAR"); //  shift arithmetic right
        mathOpsText.put(0b000_01111, "REV"); //  reverse bits
        mathOpsText.put(0b000_10000, "BOOLEAN_AND"); // boolean and
        mathOpsText.put(0b000_10001, "ENCODE"); //  encode (0-32)
        mathOpsText.put(0b000_10010, "BOOLEAN_OR"); //  boolean or
        mathOpsText.put(0b000_10011, "DECODE"); //  decode
        mathOpsText.put(0b000_10100, "MULTIPLY"); //   multiply, return lower half (signed)
        mathOpsText.put(0b000_10101, "MULTIPLY_UPPER"); //  multiply, return upper half (signed)
        mathOpsText.put(0b000_10110, "DIVIDE"); //   divide, return quotient (signed)
        mathOpsText.put(0b000_10111, "REMAINDER"); //  divide, return remainder (signed)
        mathOpsText.put(0b000_11000, "SQR"); //  square root
        mathOpsText.put(0b000_11001, "TEST_BELOW"); //   test below (signed)
        mathOpsText.put(0b000_11010, "TEST_ABOVE"); //   test above (signed)
        mathOpsText.put(0b000_11011, "TEST_NOT_EQUAL"); //  test not equal
        mathOpsText.put(0b000_11100, "TEST_EQUAL"); //  test equal
        mathOpsText.put(0b000_11101, "TEST_BELOW_OR_EQUAL"); //  test below or equal (signed)
        mathOpsText.put(0b000_11110, "TEST_ABOVE_OR_EQUAL"); //  test above or equal (signed)
        mathOpsText.put(0b000_11111, "BOOLEAN_NOT"); // boolean not
    }

    public Spin1BytecodeNode(String text) {
        this.text = text;
    }

    public Spin1BytecodeNode(Token token) {
        this.type = token.type;
        this.text = token.getText();
    }

    public void addChild(Spin1BytecodeNode node) {
        childs.add(node);
    }

    public List<Spin1BytecodeNode> getChilds() {
        return childs;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }

}