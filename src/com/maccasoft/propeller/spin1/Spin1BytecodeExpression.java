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

import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.LocalVariable;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.expressions.Register;
import com.maccasoft.propeller.model.Token;

public class Spin1BytecodeExpression {

    Token token;
    List<Spin1BytecodeExpression> childs = new ArrayList<Spin1BytecodeExpression>();

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

    enum Size {
        Byte, Word, Long
    };

    enum Base {
        Pop, PBase, VBase, DBase
    };

    enum Op {
        Read, Write, Assign, Address
    };

    static final BitField memoryOp_ss = new BitField(0b0_11_0_00_00);
    static final BitField memoryOp_i = new BitField(0b0_00_1_00_00);
    static final BitField memoryOp_bb = new BitField(0b0_00_0_11_00);
    static final BitField memoryOp_oo = new BitField(0b0_00_0_00_11);

    class MemoryOp {
        Size ss;
        boolean i;
        Base bb;
        Op oo;

        int value;

        public byte[] getBytes() {
            int b0 = 0b1_00_0_00_00;
            b0 = memoryOp_ss.setValue(b0, ss.ordinal());
            b0 = memoryOp_i.setBoolean(b0, i);
            b0 = memoryOp_bb.setValue(b0, bb.ordinal());
            b0 = memoryOp_oo.setValue(b0, oo.ordinal());

            if (bb == Base.Pop) {
                return new byte[] {
                    (byte) b0,
                };
            }

            if (value < 127) {
                return new byte[] {
                    (byte) b0,
                    (byte) value,
                };
            }
            else {
                return new byte[] {
                    (byte) b0,
                    (byte) (0x80 | (value >> 8)),
                    (byte) value,
                };
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("MEM_");
            switch (oo) {
                case Read:
                    sb.append("READ");
                    break;
                case Write:
                    sb.append("WRITE");
                    break;
                case Assign:
                    sb.append("ASSIGN");
                    break;
                case Address:
                    sb.append("ADDRESS");
                    break;
            }
            sb.append(" ");
            switch (ss) {
                case Byte:
                    sb.append("BYTE");
                    break;
                case Word:
                    sb.append("WORD");
                    break;
                case Long:
                    sb.append("LONG");
                    break;
            }
            sb.append(" ");
            switch (bb) {
                case Pop:
                    sb.append("POP");
                    break;
                case PBase:
                    sb.append("PBASE");
                    break;
                case VBase:
                    sb.append("VBASE");
                    break;
                case DBase:
                    sb.append("DBASE");
                    break;
            }
            if (bb != Base.Pop) {
                sb.append("+");
                sb.append(String.format("$%04X", value));
            }
            return sb.toString();
        }
    }

    static final BitField variableOp_b = new BitField(0b00_1_000_00);
    static final BitField variableOp_oo = new BitField(0b00_0_000_11);
    static final BitField variableOp_xxx = new BitField(0b00_0_111_00);

    class VariableOp {
        Base b;
        Op oo;

        int value;

        public VariableOp() {
            b = Base.VBase;
            oo = Op.Read;
        }

        public byte getByte() {
            int b0 = 0b01_0_000_00;
            b0 = variableOp_b.setValue(b0, b.ordinal() - Base.VBase.ordinal());
            b0 = variableOp_oo.setValue(b0, oo.ordinal());
            b0 = variableOp_xxx.setValue(b0, value / 4);

            return (byte) b0;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("MEM_");
            switch (oo) {
                case Read:
                    sb.append("READ");
                    break;
                case Write:
                    sb.append("WRITE");
                    break;
                case Assign:
                    sb.append("ASSIGN");
                    break;
                case Address:
                    sb.append("ADDRESS");
                    break;
            }
            sb.append(" ");
            sb.append("LONG");
            sb.append(" ");
            switch (b) {
                case VBase:
                    sb.append("VBASE");
                    break;
                case DBase:
                    sb.append("DBASE");
                    break;
                default:
                    throw new RuntimeException("Invalid base");
            }
            sb.append("+");
            sb.append(String.format("$%04X", value));
            sb.append(" (short)");
            return sb.toString();
        }
    }

    static final BitField regOp_oo = new BitField(0b0_11_00000);
    static final BitField regOp_xxxxx = new BitField(0b0_00_11111);

    class RegOp {
        Op oo;
        int value;

        public byte[] getBytes() {
            int b1 = 0b1_00_00000;
            b1 = regOp_oo.setValue(b1, oo.ordinal());
            b1 = regOp_xxxxx.setValue(b1, value - 0x1E0);

            return new byte[] {
                0b00111111, (byte) b1
            };
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("REG_");
            switch (oo) {
                case Read:
                    sb.append("READ");
                    break;
                case Write:
                    sb.append("WRITE");
                    break;
                case Assign:
                    sb.append("ASSIGN");
                    break;
                default:
                    throw new RuntimeException("Invalid operation");
            }
            sb.append(" ");
            sb.append(String.format("$%03X", value));
            return sb.toString();
        }

    }

    public Spin1BytecodeExpression(Token token) {
        this.token = token;
    }

    public void addChild(Spin1BytecodeExpression node) {
        childs.add(node);
    }

    public List<Spin1BytecodeExpression> getChilds() {
        return childs;
    }

    public String getText() {
        return token.getText();
    }

    public void generateObjectCode(Spin1Context context, boolean push) {
        byte[] code = null;
        String text = "";

        if (token.type == Token.NUMBER) {
            Expression expression = new NumberLiteral(token.getText());
            code = compileConstant(expression.getNumber().intValue());
            text = "CONSTANT (" + String.valueOf(expression.getNumber()) + ")";
        }
        else if (":=".equals(getText())) {
            childs.get(1).generateObjectCode(context, true);
            Expression expression = context.getLocalSymbol(childs.get(0).getText());
            if (expression instanceof Register) {
                RegOp op = new RegOp();
                op.oo = Op.Write;
                op.value = expression.getNumber().intValue();
                code = op.getBytes();
                text = op.toString();
            }
            else {
                VariableOp op = new VariableOp();
                op.oo = Op.Write;
                if (expression instanceof LocalVariable) {
                    op.b = Base.DBase;
                }
                op.value = expression.getNumber().intValue();
                code = new byte[] {
                    op.getByte()
                };
                text = op.toString();
            }
        }
        else if (assignMathOps.containsKey(token.getText())) {
            childs.get(1).generateObjectCode(context, true);
            if (childs.get(0).token.type == Token.OPERATOR) {
                childs.get(0).generateObjectCode(context, true);
            }
            else {
                Expression expression = context.getLocalSymbol(childs.get(0).getText());
                if (expression instanceof Register) {
                    RegOp op = new RegOp();
                    op.oo = Op.Assign;
                    op.value = expression.getNumber().intValue();
                    byte[] b0 = op.getBytes();
                    code = new byte[] {
                        b0[0], b0[1],
                        (byte) assignMathOps.get(token.getText()).intValue()
                    };
                    if (push) {
                        code[2] |= 0b100_00000;
                    }
                    text = op.toString() + " " + mathOpsText.get(code[2] & 0x1F);
                }
                else {
                    VariableOp op = new VariableOp();
                    op.oo = Op.Assign;
                    if (expression instanceof LocalVariable) {
                        op.b = Base.DBase;
                    }
                    op.value = expression.getNumber().intValue();
                    code = new byte[] {
                        op.getByte(),
                        (byte) assignMathOps.get(token.getText()).intValue()
                    };
                    if (push) {
                        code[1] |= 0b100_00000;
                    }
                    text = op.toString() + " " + mathOpsText.get(code[1] & 0x1F);
                }
            }
        }
        else if (mathOps.containsKey(token.getText())) {
            childs.get(0).generateObjectCode(context, push);
            childs.get(1).generateObjectCode(context, push);
            code = new byte[] {
                (byte) (0b111_00000 | mathOps.get(token.getText()).intValue())
            };
            text = mathOpsText.get(code[0] & 0x1F);
        }
        else if ("ABORT".equalsIgnoreCase(getText())) {
            text = getText().toUpperCase();
            if (childs.size() == 0) {
                code = new byte[] {
                    (byte) 0b00110000
                };
            }
            else if (childs.size() == 1) {
                childs.get(0).generateObjectCode(context, true);
                code = new byte[] {
                    (byte) 0b00110001
                };
            }
            else {
                throw new RuntimeException("error: invalid arguments");
            }
        }
        else if ("BYTEFILL".equalsIgnoreCase(getText()) || "WORDFILL".equalsIgnoreCase(getText()) || "LONGFILL".equalsIgnoreCase(getText()) || "WAITPEQ".equalsIgnoreCase(getText())) {
            if (push) {
                throw new RuntimeException("error: function " + getText() + " does not return a value");
            }
            List<Spin1BytecodeExpression> args = childs.get(0).childs;
            if (args.size() != 3) {
                throw new RuntimeException("error: expected 3 arguments, found " + childs.size());
            }
            args.get(0).generateObjectCode(context, true);
            args.get(1).generateObjectCode(context, true);
            args.get(2).generateObjectCode(context, true);
            code = new byte[] {
                (byte) 0b00011000
            };
            if ("WORDFILL".equalsIgnoreCase(getText())) {
                code[0] += 1;
            }
            else if ("LONGFILL".equalsIgnoreCase(getText())) {
                code[0] += 2;
            }
            else if ("WAITPEQ".equalsIgnoreCase(getText())) {
                code[0] += 3;
            }
            text = getText().toUpperCase();
        }
        else if ("BYTEMOVE".equalsIgnoreCase(getText()) || "WORDMOVE".equalsIgnoreCase(getText()) || "LONGMOVE".equalsIgnoreCase(getText()) || "WAITPNE".equalsIgnoreCase(getText())) {
            if (push) {
                throw new RuntimeException("error: function " + getText() + " does not return a value");
            }
            List<Spin1BytecodeExpression> args = childs.get(0).childs;
            if (args.size() != 3) {
                throw new RuntimeException("error: expected 3 arguments, found " + childs.size());
            }
            args.get(0).generateObjectCode(context, true);
            args.get(1).generateObjectCode(context, true);
            args.get(2).generateObjectCode(context, true);
            code = new byte[] {
                (byte) 0b00011100
            };
            if ("WORDFILL".equalsIgnoreCase(getText())) {
                code[0] += 1;
            }
            else if ("LONGFILL".equalsIgnoreCase(getText())) {
                code[0] += 2;
            }
            else if ("WAITPEQ".equalsIgnoreCase(getText())) {
                code[0] += 3;
            }
            text = getText().toUpperCase();
        }
        else if ("COGID".equalsIgnoreCase(getText())) {
            if (!push) {
                throw new RuntimeException("error: invalid statement " + getText());
            }
            RegOp op = new RegOp();
            op.oo = Op.Read;
            op.value = 0x1E9;
            code = op.getBytes();
            text = op.toString();
        }
        else if ("COGINIT".equalsIgnoreCase(getText())) {
            List<Spin1BytecodeExpression> args = childs.get(0).childs;
            if (args.size() != 3) {
                throw new RuntimeException("error: expected 3 arguments, found " + childs.size());
            }
            args.get(0).generateObjectCode(context, true);
            args.get(1).generateObjectCode(context, true);
            args.get(2).generateObjectCode(context, true);
            code = new byte[] {
                (byte) (push ? 0b00101000 : 0b00101100)
            };
            text = getText().toUpperCase();
        }
        else if ("LOCKNEW".equalsIgnoreCase(getText())) {
            if (childs.size() != 0) {
                throw new RuntimeException("error: expected 0 arguments, found " + childs.size());
            }
            code = new byte[] {
                (byte) (push ? 0b00101001 : 0b00101101)
            };
            text = getText().toUpperCase();
        }
        else if ("RETURN".equalsIgnoreCase(getText())) {
            text = getText().toUpperCase();
            if (childs.size() == 0) {
                code = new byte[] {
                    (byte) 0b00110000
                };
            }
            else if (childs.size() == 1) {
                childs.get(0).generateObjectCode(context, true);
                code = new byte[] {
                    (byte) 0b00110001
                };
            }
            else {
                throw new RuntimeException("error: invalid arguments");
            }
        }
        else if ("CLKSET".equalsIgnoreCase(getText())) {
            List<Spin1BytecodeExpression> args = childs.get(0).childs;
            if (args.size() != 2) {
                throw new RuntimeException("error: expected 2 arguments, found " + childs.size());
            }
            args.get(0).generateObjectCode(context, true);
            args.get(1).generateObjectCode(context, true);
            code = new byte[] {
                (byte) 0b00100000
            };
            text = getText().toUpperCase();
        }
        else if ("COGSTOP".equalsIgnoreCase(getText()) || "LOCKRET".equalsIgnoreCase(getText()) || "WAITCNT".equalsIgnoreCase(getText())) {
            if (push) {
                throw new RuntimeException("error: function " + getText() + " does not return a value");
            }
            if (childs.size() != 1) {
                throw new RuntimeException("error: expected 1 argument, found " + childs.size());
            }
            childs.get(0).generateObjectCode(context, true);
            code = new byte[] {
                (byte) 0b00100001
            };
            if ("LOCKRET".equalsIgnoreCase(getText())) {
                code[0] += 1;
            }
            else if ("WAITCNT".equalsIgnoreCase(getText())) {
                code[0] += 2;
            }
            text = getText().toUpperCase();
        }
        else if ("WAITVID".equalsIgnoreCase(getText())) {
            List<Spin1BytecodeExpression> args = childs.get(0).childs;
            if (args.size() != 2) {
                throw new RuntimeException("error: expected 2 arguments, found " + childs.size());
            }
            args.get(0).generateObjectCode(context, true);
            args.get(1).generateObjectCode(context, true);
            code = new byte[] {
                (byte) 0b00100111
            };
            text = getText().toUpperCase();
        }
        else {
            Expression expression = context.getLocalSymbol(token.getText());
            if (expression instanceof Register) {
                RegOp op = new RegOp();
                op.oo = Op.Read;
                op.value = expression.getNumber().intValue();
                code = op.getBytes();
                text = op.toString();
            }
            else {
                VariableOp op = new VariableOp();
                if (token.getText().startsWith("@")) {
                    op.oo = Op.Address;
                }
                else {
                    op.oo = Op.Read;
                }
                if (expression instanceof LocalVariable) {
                    op.b = Base.DBase;
                }
                op.value = expression.getNumber().intValue();

                code = new byte[] {
                    op.getByte()
                };
                text = op.toString();
            }
        }

        if (code != null) {
            int i = 0;
            while (i < code.length && i < 4) {
                System.out.print(String.format(" %02X", code[i++]));
            }
            while (i < 5) {
                System.out.print("   ");
                i++;
            }
            System.out.println(" | " + text);
        }
    }

    byte[] compileConstant(int value) {

        if (value == -1 || value == 0 || value == 1) {
            return new byte[] {
                (byte) ((value + 1) | 0x34)
            };
        }

        /*for (int i = 0; i < 128; i++) {
            int testVal = 2;
            testVal <<= (i & 0x1F); // mask i, so that we only actually shift 0 to 31
        
            if ((i & 0x20) != 0) {// i in range 32 to 63 or 96 to 127
                testVal--;
            }
            if ((i & 0x40) != 0) {// i in range 64 to 127
                testVal = ~testVal;
            }
        
            if (testVal == value) {
                return new byte[] {
                    0x37, (byte) i
                };
            }
        }*/

        if ((value & 0xFFFFFF00) == 0xFFFFFF00) {
            return new byte[] {
                0x38, (byte) ~(value & 0xFF), (byte) 0xE7
            };
        }
        else if ((value & 0xFFFF0000) == 0xFFFF0000) {
            return new byte[] {
                0x39, (byte) ~((value >> 8) & 0xFF), (byte) ~(value & 0xFF), (byte) 0xE7
            };
        }

        // 1 to 4 byte constant
        if ((value & 0xFF000000) != 0) {
            return new byte[] {
                0x37 + 4, (byte) (value >> 24), (byte) (value >> 16), (byte) (value >> 8), (byte) value
            };
        }
        else if ((value & 0x00FF0000) != 0) {
            return new byte[] {
                0x37 + 3, (byte) (value >> 16), (byte) (value >> 8), (byte) value
            };
        }
        else if ((value & 0x0000FF00) != 0) {
            return new byte[] {
                0x37 + 2, (byte) (value >> 8), (byte) value
            };
        }
        else {
            return new byte[] {
                0x37 + 1, (byte) value
            };
        }

    }

    @Override
    public String toString() {
        return token.getText();
    }

}