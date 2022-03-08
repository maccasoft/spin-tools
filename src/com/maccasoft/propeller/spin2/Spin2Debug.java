/*
 * Copyright (c) 2022 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.maccasoft.propeller.CompilerMessage;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.model.Token;

public class Spin2Debug {

    private static final int DBC_DONE = 0;
    private static final int DBC_ASMMODE = 1; // Switches into PASM mode...
    private static final int DBC_IF = 2;
    private static final int DBC_IFNOT = 3;
    private static final int DBC_COGN = 4;
    private static final int DBC_CHAR = 5;
    private static final int DBC_STRING = 6;
    private static final int DBC_DELAY = 7;

    // Flags
    private static final int DBC_FLAG_NOCOMMA = 0x01;
    private static final int DBC_FLAG_NOEXPR = 0x02;
    private static final int DBC_FLAG_ARRAY = 0x10;
    private static final int DBC_FLAG_SIGNED = 0x20;
    // Numeric sizes
    private static final int DBC_SIZE_BYTE = 0x04;
    private static final int DBC_SIZE_WORD = 0x08;
    private static final int DBC_SIZE_LONG = 0x0C;
    // Output type
    private static final int DBC_TYPE_STR = 0x20 | DBC_SIZE_BYTE;
    private static final int DBC_TYPE_FLP = 0x20; // Note the overlap with the signed flag and the string type
    private static final int DBC_TYPE_DEC = 0x40;
    private static final int DBC_TYPE_HEX = 0x80;
    private static final int DBC_TYPE_BIN = 0xC0;

    boolean first;

    public byte[] compilePAsmDebugStatement(Spin2Context context, Spin2StatementNode root) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        os.write(DBC_ASMMODE);
        os.write(DBC_COGN);

        first = true;

        for (Spin2StatementNode node : root.getChilds()) {

            try {
                if (node.getType() == Token.STRING) {
                    try {
                        os.write(DBC_STRING);
                        String s = node.getText();
                        if (s.startsWith("\"")) {
                            s = s.substring(1, s.length() - 1);
                        }
                        os.write(s.getBytes());
                        os.write(0x00);
                    } catch (IOException e) {

                    }
                }
                else {
                    int flags = 0;

                    String cmd = node.getText().toUpperCase();
                    if (cmd.endsWith("_")) {
                        flags |= DBC_FLAG_NOEXPR;
                        cmd = cmd.substring(0, cmd.length() - "_".length());
                    }

                    switch (cmd) {
                        case "FDEC":
                            compileSimpleStatement(context, node, os, DBC_TYPE_FLP | DBC_SIZE_LONG);
                            break;
                        case "FDEC_REG_ARRAY":
                            compileArrayStatement(context, node, os, DBC_TYPE_FLP | DBC_FLAG_ARRAY);
                            break;
                        case "FDEC_ARRAY":
                            compileArrayStatement(context, node, os, DBC_TYPE_FLP | DBC_SIZE_LONG | DBC_FLAG_ARRAY);
                            break;

                        case "UDEC":
                            compileSimpleStatement(context, node, os, DBC_TYPE_DEC | flags);
                            break;
                        case "UDEC_BYTE":
                            compileSimpleStatement(context, node, os, DBC_TYPE_DEC | DBC_SIZE_BYTE | flags);
                            break;
                        case "UDEC_WORD":
                            compileSimpleStatement(context, node, os, DBC_TYPE_DEC | DBC_SIZE_WORD | flags);
                            break;
                        case "UDEC_LONG":
                            compileSimpleStatement(context, node, os, DBC_TYPE_DEC | DBC_SIZE_LONG | flags);
                            break;
                        case "UDEC_REG_ARRAY":
                            compileArrayStatement(context, node, os, DBC_TYPE_DEC | DBC_FLAG_ARRAY | flags);
                            break;
                        case "UDEC_BYTE_ARRAY":
                            compileArrayStatement(context, node, os, DBC_TYPE_DEC | DBC_SIZE_BYTE | DBC_FLAG_ARRAY | flags);
                            break;
                        case "UDEC_WORD_ARRAY":
                            compileArrayStatement(context, node, os, DBC_TYPE_DEC | DBC_SIZE_WORD | DBC_FLAG_ARRAY | flags);
                            break;
                        case "UDEC_LONG_ARRAY":
                            compileArrayStatement(context, node, os, DBC_TYPE_DEC | DBC_SIZE_LONG | DBC_FLAG_ARRAY | flags);
                            break;

                        case "SDEC":
                            compileSimpleStatement(context, node, os, DBC_TYPE_DEC | DBC_FLAG_SIGNED | flags);
                            break;
                        case "SDEC_BYTE":
                            compileSimpleStatement(context, node, os, DBC_TYPE_DEC | DBC_SIZE_BYTE | DBC_FLAG_SIGNED | flags);
                            break;
                        case "SDEC_WORD":
                            compileSimpleStatement(context, node, os, DBC_TYPE_DEC | DBC_SIZE_WORD | DBC_FLAG_SIGNED | flags);
                            break;
                        case "SDEC_LONG":
                            compileSimpleStatement(context, node, os, DBC_TYPE_DEC | DBC_SIZE_LONG | DBC_FLAG_SIGNED | flags);
                            break;
                        case "SDEC_REG_ARRAY":
                            compileArrayStatement(context, node, os, DBC_TYPE_DEC | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags);
                            break;
                        case "SDEC_BYTE_ARRAY":
                            compileArrayStatement(context, node, os, DBC_TYPE_DEC | DBC_SIZE_BYTE | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags);
                            break;
                        case "SDEC_WORD_ARRAY":
                            compileArrayStatement(context, node, os, DBC_TYPE_DEC | DBC_SIZE_WORD | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags);
                            break;
                        case "SDEC_LONG_ARRAY":
                            compileArrayStatement(context, node, os, DBC_TYPE_DEC | DBC_SIZE_LONG | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags);
                            break;

                        case "UHEX":
                            compileSimpleStatement(context, node, os, DBC_TYPE_HEX | flags);
                            break;
                        case "UHEX_BYTE":
                            compileSimpleStatement(context, node, os, DBC_TYPE_HEX | DBC_SIZE_BYTE | flags);
                            break;
                        case "UHEX_WORD":
                            compileSimpleStatement(context, node, os, DBC_TYPE_HEX | DBC_SIZE_WORD | flags);
                            break;
                        case "UHEX_LONG":
                            compileSimpleStatement(context, node, os, DBC_TYPE_HEX | DBC_SIZE_LONG | flags);
                            break;
                        case "UHEX_REG_ARRAY":
                            compileArrayStatement(context, node, os, DBC_TYPE_HEX | DBC_FLAG_ARRAY | flags);
                            break;
                        case "UHEX_BYTE_ARRAY":
                            compileArrayStatement(context, node, os, DBC_TYPE_HEX | DBC_SIZE_BYTE | DBC_FLAG_ARRAY | flags);
                            break;
                        case "UHEX_WORD_ARRAY":
                            compileArrayStatement(context, node, os, DBC_TYPE_HEX | DBC_SIZE_WORD | DBC_FLAG_ARRAY | flags);
                            break;
                        case "UHEX_LONG_ARRAY":
                            compileArrayStatement(context, node, os, DBC_TYPE_HEX | DBC_SIZE_LONG | DBC_FLAG_ARRAY | flags);
                            break;

                        case "SHEX":
                            compileSimpleStatement(context, node, os, DBC_TYPE_HEX | DBC_FLAG_SIGNED | flags);
                            break;
                        case "SHEX_BYTE":
                            compileSimpleStatement(context, node, os, DBC_TYPE_HEX | DBC_SIZE_BYTE | DBC_FLAG_SIGNED | flags);
                            break;
                        case "SHEX_WORD":
                            compileSimpleStatement(context, node, os, DBC_TYPE_HEX | DBC_SIZE_WORD | DBC_FLAG_SIGNED | flags);
                            break;
                        case "SHEX_LONG":
                            compileSimpleStatement(context, node, os, DBC_TYPE_HEX | DBC_SIZE_LONG | DBC_FLAG_SIGNED | flags);
                            break;
                        case "SHEX_REG_ARRAY":
                            compileArrayStatement(context, node, os, DBC_TYPE_HEX | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags);
                            break;
                        case "SHEX_BYTE_ARRAY":
                            compileArrayStatement(context, node, os, DBC_TYPE_HEX | DBC_SIZE_BYTE | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags);
                            break;
                        case "SHEX_WORD_ARRAY":
                            compileArrayStatement(context, node, os, DBC_TYPE_HEX | DBC_SIZE_WORD | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags);
                            break;
                        case "SHEX_LONG_ARRAY":
                            compileArrayStatement(context, node, os, DBC_TYPE_HEX | DBC_SIZE_LONG | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags);
                            break;

                        case "UBIN":
                            compileSimpleStatement(context, node, os, DBC_TYPE_BIN | flags);
                            break;
                        case "UBIN_BYTE":
                            compileSimpleStatement(context, node, os, DBC_TYPE_BIN | DBC_SIZE_BYTE | flags);
                            break;
                        case "UBIN_WORD":
                            compileSimpleStatement(context, node, os, DBC_TYPE_BIN | DBC_SIZE_WORD | flags);
                            break;
                        case "UBIN_LONG":
                            compileSimpleStatement(context, node, os, DBC_TYPE_BIN | DBC_SIZE_LONG | flags);
                            break;
                        case "UBIN_REG_ARRAY":
                            compileArrayStatement(context, node, os, DBC_TYPE_BIN | DBC_FLAG_ARRAY | flags);
                            break;
                        case "UBIN_BYTE_ARRAY":
                            compileArrayStatement(context, node, os, DBC_TYPE_BIN | DBC_SIZE_BYTE | DBC_FLAG_ARRAY | flags);
                            break;
                        case "UBIN_WORD_ARRAY":
                            compileArrayStatement(context, node, os, DBC_TYPE_BIN | DBC_SIZE_WORD | DBC_FLAG_ARRAY | flags);
                            break;
                        case "UBIN_LONG_ARRAY":
                            compileArrayStatement(context, node, os, DBC_TYPE_BIN | DBC_SIZE_LONG | DBC_FLAG_ARRAY | flags);
                            break;

                        case "SBIN":
                            compileSimpleStatement(context, node, os, DBC_TYPE_BIN | DBC_FLAG_SIGNED | flags);
                            break;
                        case "SBIN_BYTE":
                            compileSimpleStatement(context, node, os, DBC_TYPE_BIN | DBC_SIZE_BYTE | DBC_FLAG_SIGNED | flags);
                            break;
                        case "SBIN_WORD":
                            compileSimpleStatement(context, node, os, DBC_TYPE_BIN | DBC_SIZE_WORD | DBC_FLAG_SIGNED | flags);
                            break;
                        case "SBIN_LONG":
                            compileSimpleStatement(context, node, os, DBC_TYPE_BIN | DBC_SIZE_LONG | DBC_FLAG_SIGNED | flags);
                            break;
                        case "SBIN_REG_ARRAY":
                            compileArrayStatement(context, node, os, DBC_TYPE_BIN | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags);
                            break;
                        case "SBIN_BYTE_ARRAY":
                            compileArrayStatement(context, node, os, DBC_TYPE_BIN | DBC_SIZE_BYTE | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags);
                            break;
                        case "SBIN_WORD_ARRAY":
                            compileArrayStatement(context, node, os, DBC_TYPE_BIN | DBC_SIZE_WORD | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags);
                            break;
                        case "SBIN_LONG_ARRAY":
                            compileArrayStatement(context, node, os, DBC_TYPE_BIN | DBC_SIZE_LONG | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags);
                            break;

                        case "ZSTR":
                            compileSimpleStatement(context, node, os, DBC_TYPE_STR | flags);
                            break;
                        case "LSTR":
                            compileArrayStatement(context, node, os, DBC_TYPE_STR | DBC_FLAG_ARRAY | flags);
                            break;

                        case "DLY": {
                            os.write(DBC_DELAY);
                            int index = 0;
                            if ("#".equals(node.getChild(index).getText())) {
                                os.write('#');
                                index++;
                            }
                            Expression exp = new NumberLiteral(node.getChild(index).getText());
                            compileConstant(os, exp.getNumber().intValue());
                            break;
                        }

                        default:
                            throw new CompilerMessage("Unknown debug statement '" + node.getText() + "'", node.getData());
                    }
                }
            } catch (IOException e) {
                // Do nothing
            }
        }

        os.write(DBC_DONE);

        return os.toByteArray();
    }

    void compileSimpleStatement(Spin2Context context, Spin2StatementNode node, OutputStream os, int op) throws IOException {
        for (Spin2StatementNode child : node.getChilds()) {
            if (first) {
                os.write(op | DBC_FLAG_NOCOMMA);
                first = false;
            }
            else {
                os.write(op);
            }

            String arg = child.getText();

            if ((op & DBC_FLAG_NOEXPR) == 0) {
                os.write(arg.getBytes());
                os.write(0x00);
            }

            Expression expression = context.getLocalSymbol(arg);
            if (expression != null) {
                int value = expression.getNumber().intValue();
                os.write(0x80 | (value >> 8));
                os.write(value);
            }
        }
    }

    void compileArrayStatement(Spin2Context context, Spin2StatementNode node, OutputStream os, int op) throws IOException {
        if (first) {
            os.write(op | DBC_FLAG_NOCOMMA);
            first = false;
        }
        else {
            os.write(op);
        }

        Spin2StatementNode child = node.getChild(0);
        String arg = child.getText();
        os.write(arg.getBytes());
        os.write(0x00);

        Expression expression = context.getLocalSymbol(arg);
        if (expression != null) {
            int value = expression.getNumber().intValue();
            os.write(0x80 | (value >> 8));
            os.write(value);
        }

        child = node.getChild(1);
        int index = 1;
        if ("#".equals(node.getChild(index).getText())) {
            os.write('#');
            index++;
        }
        if (child.getType() == Token.NUMBER) {
            expression = new NumberLiteral(child.getText());
            compileConstant(os, expression.getNumber().intValue());
        }
        else {
            expression = context.getLocalSymbol(arg);
            int value = expression.getNumber().intValue();
            os.write(0x80 | (value >> 8));
            os.write(value);
        }
    }

    void compileConstant(OutputStream os, int value) throws IOException {
        if (value < 0x4000) {
            os.write(new byte[] {
                (byte) (value >> 8),
                (byte) value
            });
            return;
        }
        os.write(new byte[] {
            0b01000000,
            (byte) value,
            (byte) (value >> 8),
            (byte) (value >> 16),
            (byte) (value >> 24)
        });
    }
}
