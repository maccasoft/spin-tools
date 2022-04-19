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

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.spin2.Spin2PAsmDebugLine.Spin2DebugCommand;
import com.maccasoft.propeller.spin2.Spin2PAsmDebugLine.Spin2DebugExpression;

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

    public byte[] compileDebugStatement(Spin2StatementNode root) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        boolean conditionFirst = false;

        if (root.getChildCount() != 0) {
            String cmd = root.getChild(0).getText().toUpperCase();
            if ("IF".equals(cmd) || "IFNOT".equals(cmd)) {
                conditionFirst = true;
            }
        }

        if (!conditionFirst) {
            os.write(DBC_COGN);
        }

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
                    first = true;
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
                            compileSpinStatement(node, os, DBC_TYPE_FLP | DBC_SIZE_LONG);
                            break;
                        case "FDEC_ARRAY":
                            compileSpinArrayStatement(node, os, DBC_TYPE_FLP | DBC_SIZE_LONG | DBC_FLAG_ARRAY);
                            break;

                        case "UDEC":
                            compileSpinStatement(node, os, DBC_TYPE_DEC | flags);
                            break;
                        case "UDEC_BYTE":
                            compileSpinStatement(node, os, DBC_TYPE_DEC | DBC_SIZE_BYTE | flags);
                            break;
                        case "UDEC_WORD":
                            compileSpinStatement(node, os, DBC_TYPE_DEC | DBC_SIZE_WORD | flags);
                            break;
                        case "UDEC_LONG":
                            compileSpinStatement(node, os, DBC_TYPE_DEC | DBC_SIZE_LONG | flags);
                            break;
                        case "UDEC_BYTE_ARRAY":
                            compileSpinArrayStatement(node, os, DBC_TYPE_DEC | DBC_SIZE_BYTE | DBC_FLAG_ARRAY | flags);
                            break;
                        case "UDEC_WORD_ARRAY":
                            compileSpinArrayStatement(node, os, DBC_TYPE_DEC | DBC_SIZE_WORD | DBC_FLAG_ARRAY | flags);
                            break;
                        case "UDEC_LONG_ARRAY":
                            compileSpinArrayStatement(node, os, DBC_TYPE_DEC | DBC_SIZE_LONG | DBC_FLAG_ARRAY | flags);
                            break;

                        case "SDEC":
                            compileSpinStatement(node, os, DBC_TYPE_DEC | DBC_FLAG_SIGNED | flags);
                            break;
                        case "SDEC_BYTE":
                            compileSpinStatement(node, os, DBC_TYPE_DEC | DBC_SIZE_BYTE | DBC_FLAG_SIGNED | flags);
                            break;
                        case "SDEC_WORD":
                            compileSpinStatement(node, os, DBC_TYPE_DEC | DBC_SIZE_WORD | DBC_FLAG_SIGNED | flags);
                            break;
                        case "SDEC_LONG":
                            compileSpinStatement(node, os, DBC_TYPE_DEC | DBC_SIZE_LONG | DBC_FLAG_SIGNED | flags);
                            break;
                        case "SDEC_BYTE_ARRAY":
                            compileSpinArrayStatement(node, os, DBC_TYPE_DEC | DBC_SIZE_BYTE | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags);
                            break;
                        case "SDEC_WORD_ARRAY":
                            compileSpinArrayStatement(node, os, DBC_TYPE_DEC | DBC_SIZE_WORD | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags);
                            break;
                        case "SDEC_LONG_ARRAY":
                            compileSpinArrayStatement(node, os, DBC_TYPE_DEC | DBC_SIZE_LONG | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags);
                            break;

                        case "UHEX":
                            compileSpinStatement(node, os, DBC_TYPE_HEX | flags);
                            break;
                        case "UHEX_BYTE":
                            compileSpinStatement(node, os, DBC_TYPE_HEX | DBC_SIZE_BYTE | flags);
                            break;
                        case "UHEX_WORD":
                            compileSpinStatement(node, os, DBC_TYPE_HEX | DBC_SIZE_WORD | flags);
                            break;
                        case "UHEX_LONG":
                            compileSpinStatement(node, os, DBC_TYPE_HEX | DBC_SIZE_LONG | flags);
                            break;
                        case "UHEX_BYTE_ARRAY":
                            compileSpinArrayStatement(node, os, DBC_TYPE_HEX | DBC_SIZE_BYTE | DBC_FLAG_ARRAY | flags);
                            break;
                        case "UHEX_WORD_ARRAY":
                            compileSpinArrayStatement(node, os, DBC_TYPE_HEX | DBC_SIZE_WORD | DBC_FLAG_ARRAY | flags);
                            break;
                        case "UHEX_LONG_ARRAY":
                            compileSpinArrayStatement(node, os, DBC_TYPE_HEX | DBC_SIZE_LONG | DBC_FLAG_ARRAY | flags);
                            break;

                        case "SHEX":
                            compileSpinStatement(node, os, DBC_TYPE_HEX | DBC_FLAG_SIGNED | flags);
                            break;
                        case "SHEX_BYTE":
                            compileSpinStatement(node, os, DBC_TYPE_HEX | DBC_SIZE_BYTE | DBC_FLAG_SIGNED | flags);
                            break;
                        case "SHEX_WORD":
                            compileSpinStatement(node, os, DBC_TYPE_HEX | DBC_SIZE_WORD | DBC_FLAG_SIGNED | flags);
                            break;
                        case "SHEX_LONG":
                            compileSpinStatement(node, os, DBC_TYPE_HEX | DBC_SIZE_LONG | DBC_FLAG_SIGNED | flags);
                            break;
                        case "SHEX_BYTE_ARRAY":
                            compileSpinArrayStatement(node, os, DBC_TYPE_HEX | DBC_SIZE_BYTE | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags);
                            break;
                        case "SHEX_WORD_ARRAY":
                            compileSpinArrayStatement(node, os, DBC_TYPE_HEX | DBC_SIZE_WORD | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags);
                            break;
                        case "SHEX_LONG_ARRAY":
                            compileSpinArrayStatement(node, os, DBC_TYPE_HEX | DBC_SIZE_LONG | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags);
                            break;

                        case "UBIN":
                            compileSpinStatement(node, os, DBC_TYPE_BIN | flags);
                            break;
                        case "UBIN_BYTE":
                            compileSpinStatement(node, os, DBC_TYPE_BIN | DBC_SIZE_BYTE | flags);
                            break;
                        case "UBIN_WORD":
                            compileSpinStatement(node, os, DBC_TYPE_BIN | DBC_SIZE_WORD | flags);
                            break;
                        case "UBIN_LONG":
                            compileSpinStatement(node, os, DBC_TYPE_BIN | DBC_SIZE_LONG | flags);
                            break;
                        case "UBIN_BYTE_ARRAY":
                            compileSpinArrayStatement(node, os, DBC_TYPE_BIN | DBC_SIZE_BYTE | DBC_FLAG_ARRAY | flags);
                            break;
                        case "UBIN_WORD_ARRAY":
                            compileSpinArrayStatement(node, os, DBC_TYPE_BIN | DBC_SIZE_WORD | DBC_FLAG_ARRAY | flags);
                            break;
                        case "UBIN_LONG_ARRAY":
                            compileSpinArrayStatement(node, os, DBC_TYPE_BIN | DBC_SIZE_LONG | DBC_FLAG_ARRAY | flags);
                            break;

                        case "SBIN":
                            compileSpinStatement(node, os, DBC_TYPE_BIN | DBC_FLAG_SIGNED | flags);
                            break;
                        case "SBIN_BYTE":
                            compileSpinStatement(node, os, DBC_TYPE_BIN | DBC_SIZE_BYTE | DBC_FLAG_SIGNED | flags);
                            break;
                        case "SBIN_WORD":
                            compileSpinStatement(node, os, DBC_TYPE_BIN | DBC_SIZE_WORD | DBC_FLAG_SIGNED | flags);
                            break;
                        case "SBIN_LONG":
                            compileSpinStatement(node, os, DBC_TYPE_BIN | DBC_SIZE_LONG | DBC_FLAG_SIGNED | flags);
                            break;
                        case "SBIN_BYTE_ARRAY":
                            compileSpinArrayStatement(node, os, DBC_TYPE_BIN | DBC_SIZE_BYTE | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags);
                            break;
                        case "SBIN_WORD_ARRAY":
                            compileSpinArrayStatement(node, os, DBC_TYPE_BIN | DBC_SIZE_WORD | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags);
                            break;
                        case "SBIN_LONG_ARRAY":
                            compileSpinArrayStatement(node, os, DBC_TYPE_BIN | DBC_SIZE_LONG | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags);
                            break;

                        case "ZSTR":
                            compileSpinStatement(node, os, DBC_TYPE_STR | flags);
                            break;
                        case "LSTR":
                            compileSpinStatement(node, os, DBC_TYPE_STR | DBC_FLAG_ARRAY | flags);
                            break;

                        case "DLY":
                            os.write(DBC_DELAY);
                            break;

                        case "IF":
                            os.write(DBC_IF);
                            if (conditionFirst) {
                                os.write(DBC_COGN);
                                conditionFirst = false;
                            }
                            break;
                        case "IFNOT":
                            os.write(DBC_IFNOT);
                            if (conditionFirst) {
                                os.write(DBC_COGN);
                                conditionFirst = false;
                            }
                            break;

                        default:
                            throw new CompilerException("Unknown debug statement '" + node.getText() + "'", node.getToken());
                    }
                }
            } catch (IOException e) {
                // Do nothing
            }
        }

        os.write(DBC_DONE);

        return os.toByteArray();
    }

    void compileSpinStatement(Spin2StatementNode node, OutputStream os, int op) throws IOException {
        for (Spin2StatementNode child : node.getChilds()) {
            if (first) {
                os.write(op | DBC_FLAG_NOCOMMA);
                first = false;
            }
            else {
                os.write(op);
            }

            if ((op & DBC_FLAG_NOEXPR) == 0) {
                os.write(child.getText().getBytes());
                os.write(0x00);
            }
        }
    }

    void compileSpinArrayStatement(Spin2StatementNode node, OutputStream os, int op) throws IOException {
        for (int index = 0; index < node.getChildCount(); index += 2) {
            if (first) {
                os.write(op | DBC_FLAG_NOCOMMA);
                first = false;
            }
            else {
                os.write(op);
            }

            if ((op & DBC_FLAG_NOEXPR) == 0) {
                os.write(node.getChild(index).getText().getBytes());
                os.write(0x00);
            }
        }
    }

    public byte[] compilePAsmDebugStatement(Spin2PAsmDebugLine root) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        os.write(DBC_ASMMODE);

        boolean conditionFirst = false;

        if (root.getStatementsCount() != 0) {
            String cmd = root.getStatement(0).getText().toUpperCase();
            if ("IF".equals(cmd) || "IFNOT".equals(cmd)) {
                conditionFirst = true;
            }
        }

        if (!conditionFirst) {
            os.write(DBC_COGN);
        }

        first = true;

        for (Spin2DebugCommand node : root.getStatements()) {

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
                    first = true;
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
                            compileSimpleStatement(node, os, DBC_TYPE_FLP | DBC_SIZE_LONG);
                            break;
                        case "FDEC_REG_ARRAY":
                            compileArrayStatement(node, os, DBC_TYPE_FLP | DBC_FLAG_ARRAY);
                            break;
                        case "FDEC_ARRAY":
                            compileArrayStatement(node, os, DBC_TYPE_FLP | DBC_SIZE_LONG | DBC_FLAG_ARRAY);
                            break;

                        case "UDEC":
                            compileSimpleStatement(node, os, DBC_TYPE_DEC | flags);
                            break;
                        case "UDEC_BYTE":
                            compileSimpleStatement(node, os, DBC_TYPE_DEC | DBC_SIZE_BYTE | flags);
                            break;
                        case "UDEC_WORD":
                            compileSimpleStatement(node, os, DBC_TYPE_DEC | DBC_SIZE_WORD | flags);
                            break;
                        case "UDEC_LONG":
                            compileSimpleStatement(node, os, DBC_TYPE_DEC | DBC_SIZE_LONG | flags);
                            break;
                        case "UDEC_REG_ARRAY":
                            compileArrayStatement(node, os, DBC_TYPE_DEC | DBC_FLAG_ARRAY | flags);
                            break;
                        case "UDEC_BYTE_ARRAY":
                            compileArrayStatement(node, os, DBC_TYPE_DEC | DBC_SIZE_BYTE | DBC_FLAG_ARRAY | flags);
                            break;
                        case "UDEC_WORD_ARRAY":
                            compileArrayStatement(node, os, DBC_TYPE_DEC | DBC_SIZE_WORD | DBC_FLAG_ARRAY | flags);
                            break;
                        case "UDEC_LONG_ARRAY":
                            compileArrayStatement(node, os, DBC_TYPE_DEC | DBC_SIZE_LONG | DBC_FLAG_ARRAY | flags);
                            break;

                        case "SDEC":
                            compileSimpleStatement(node, os, DBC_TYPE_DEC | DBC_FLAG_SIGNED | flags);
                            break;
                        case "SDEC_BYTE":
                            compileSimpleStatement(node, os, DBC_TYPE_DEC | DBC_SIZE_BYTE | DBC_FLAG_SIGNED | flags);
                            break;
                        case "SDEC_WORD":
                            compileSimpleStatement(node, os, DBC_TYPE_DEC | DBC_SIZE_WORD | DBC_FLAG_SIGNED | flags);
                            break;
                        case "SDEC_LONG":
                            compileSimpleStatement(node, os, DBC_TYPE_DEC | DBC_SIZE_LONG | DBC_FLAG_SIGNED | flags);
                            break;
                        case "SDEC_REG_ARRAY":
                            compileArrayStatement(node, os, DBC_TYPE_DEC | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags);
                            break;
                        case "SDEC_BYTE_ARRAY":
                            compileArrayStatement(node, os, DBC_TYPE_DEC | DBC_SIZE_BYTE | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags);
                            break;
                        case "SDEC_WORD_ARRAY":
                            compileArrayStatement(node, os, DBC_TYPE_DEC | DBC_SIZE_WORD | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags);
                            break;
                        case "SDEC_LONG_ARRAY":
                            compileArrayStatement(node, os, DBC_TYPE_DEC | DBC_SIZE_LONG | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags);
                            break;

                        case "UHEX":
                            compileSimpleStatement(node, os, DBC_TYPE_HEX | flags);
                            break;
                        case "UHEX_BYTE":
                            compileSimpleStatement(node, os, DBC_TYPE_HEX | DBC_SIZE_BYTE | flags);
                            break;
                        case "UHEX_WORD":
                            compileSimpleStatement(node, os, DBC_TYPE_HEX | DBC_SIZE_WORD | flags);
                            break;
                        case "UHEX_LONG":
                            compileSimpleStatement(node, os, DBC_TYPE_HEX | DBC_SIZE_LONG | flags);
                            break;
                        case "UHEX_REG_ARRAY":
                            compileArrayStatement(node, os, DBC_TYPE_HEX | DBC_FLAG_ARRAY | flags);
                            break;
                        case "UHEX_BYTE_ARRAY":
                            compileArrayStatement(node, os, DBC_TYPE_HEX | DBC_SIZE_BYTE | DBC_FLAG_ARRAY | flags);
                            break;
                        case "UHEX_WORD_ARRAY":
                            compileArrayStatement(node, os, DBC_TYPE_HEX | DBC_SIZE_WORD | DBC_FLAG_ARRAY | flags);
                            break;
                        case "UHEX_LONG_ARRAY":
                            compileArrayStatement(node, os, DBC_TYPE_HEX | DBC_SIZE_LONG | DBC_FLAG_ARRAY | flags);
                            break;

                        case "SHEX":
                            compileSimpleStatement(node, os, DBC_TYPE_HEX | DBC_FLAG_SIGNED | flags);
                            break;
                        case "SHEX_BYTE":
                            compileSimpleStatement(node, os, DBC_TYPE_HEX | DBC_SIZE_BYTE | DBC_FLAG_SIGNED | flags);
                            break;
                        case "SHEX_WORD":
                            compileSimpleStatement(node, os, DBC_TYPE_HEX | DBC_SIZE_WORD | DBC_FLAG_SIGNED | flags);
                            break;
                        case "SHEX_LONG":
                            compileSimpleStatement(node, os, DBC_TYPE_HEX | DBC_SIZE_LONG | DBC_FLAG_SIGNED | flags);
                            break;
                        case "SHEX_REG_ARRAY":
                            compileArrayStatement(node, os, DBC_TYPE_HEX | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags);
                            break;
                        case "SHEX_BYTE_ARRAY":
                            compileArrayStatement(node, os, DBC_TYPE_HEX | DBC_SIZE_BYTE | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags);
                            break;
                        case "SHEX_WORD_ARRAY":
                            compileArrayStatement(node, os, DBC_TYPE_HEX | DBC_SIZE_WORD | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags);
                            break;
                        case "SHEX_LONG_ARRAY":
                            compileArrayStatement(node, os, DBC_TYPE_HEX | DBC_SIZE_LONG | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags);
                            break;

                        case "UBIN":
                            compileSimpleStatement(node, os, DBC_TYPE_BIN | flags);
                            break;
                        case "UBIN_BYTE":
                            compileSimpleStatement(node, os, DBC_TYPE_BIN | DBC_SIZE_BYTE | flags);
                            break;
                        case "UBIN_WORD":
                            compileSimpleStatement(node, os, DBC_TYPE_BIN | DBC_SIZE_WORD | flags);
                            break;
                        case "UBIN_LONG":
                            compileSimpleStatement(node, os, DBC_TYPE_BIN | DBC_SIZE_LONG | flags);
                            break;
                        case "UBIN_REG_ARRAY":
                            compileArrayStatement(node, os, DBC_TYPE_BIN | DBC_FLAG_ARRAY | flags);
                            break;
                        case "UBIN_BYTE_ARRAY":
                            compileArrayStatement(node, os, DBC_TYPE_BIN | DBC_SIZE_BYTE | DBC_FLAG_ARRAY | flags);
                            break;
                        case "UBIN_WORD_ARRAY":
                            compileArrayStatement(node, os, DBC_TYPE_BIN | DBC_SIZE_WORD | DBC_FLAG_ARRAY | flags);
                            break;
                        case "UBIN_LONG_ARRAY":
                            compileArrayStatement(node, os, DBC_TYPE_BIN | DBC_SIZE_LONG | DBC_FLAG_ARRAY | flags);
                            break;

                        case "SBIN":
                            compileSimpleStatement(node, os, DBC_TYPE_BIN | DBC_FLAG_SIGNED | flags);
                            break;
                        case "SBIN_BYTE":
                            compileSimpleStatement(node, os, DBC_TYPE_BIN | DBC_SIZE_BYTE | DBC_FLAG_SIGNED | flags);
                            break;
                        case "SBIN_WORD":
                            compileSimpleStatement(node, os, DBC_TYPE_BIN | DBC_SIZE_WORD | DBC_FLAG_SIGNED | flags);
                            break;
                        case "SBIN_LONG":
                            compileSimpleStatement(node, os, DBC_TYPE_BIN | DBC_SIZE_LONG | DBC_FLAG_SIGNED | flags);
                            break;
                        case "SBIN_REG_ARRAY":
                            compileArrayStatement(node, os, DBC_TYPE_BIN | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags);
                            break;
                        case "SBIN_BYTE_ARRAY":
                            compileArrayStatement(node, os, DBC_TYPE_BIN | DBC_SIZE_BYTE | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags);
                            break;
                        case "SBIN_WORD_ARRAY":
                            compileArrayStatement(node, os, DBC_TYPE_BIN | DBC_SIZE_WORD | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags);
                            break;
                        case "SBIN_LONG_ARRAY":
                            compileArrayStatement(node, os, DBC_TYPE_BIN | DBC_SIZE_LONG | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags);
                            break;

                        case "ZSTR":
                            compileSimpleStatement(node, os, DBC_TYPE_STR | flags);
                            break;
                        case "LSTR":
                            compileArrayStatement(node, os, DBC_TYPE_STR | DBC_FLAG_ARRAY | flags);
                            break;

                        case "DLY":
                            os.write(DBC_DELAY);
                            if (node.getArgumentsCount() != 1) {
                                throw new CompilerException("expecting one argument", node.getToken());
                            }
                            compileArgument(node.getArgument(0), os);
                            break;

                        case "IF":
                            os.write(DBC_IF);
                            if (node.getArgumentsCount() != 1) {
                                throw new CompilerException("expecting one argument", node.getToken());
                            }
                            compileArgument(node.getArgument(0), os);
                            if (conditionFirst) {
                                os.write(DBC_COGN);
                                conditionFirst = false;
                            }
                            break;
                        case "IFNOT":
                            os.write(DBC_IFNOT);
                            if (node.getArgumentsCount() != 1) {
                                throw new CompilerException("expecting one argument", node.getToken());
                            }
                            compileArgument(node.getArgument(0), os);
                            if (conditionFirst) {
                                os.write(DBC_COGN);
                                conditionFirst = false;
                            }
                            break;

                        default:
                            throw new CompilerException("unknown debug statement '" + node.getText() + "'", node.getToken());
                    }
                }
            } catch (IOException e) {
                // Do nothing
            }
        }

        os.write(DBC_DONE);

        return os.toByteArray();
    }

    void compileSimpleStatement(Spin2DebugCommand node, OutputStream os, int op) throws IOException {
        for (Spin2DebugExpression child : node.getArguments()) {
            if (first) {
                os.write(op | DBC_FLAG_NOCOMMA);
                first = false;
            }
            else {
                os.write(op);
            }

            if ((op & DBC_FLAG_NOEXPR) == 0) {
                os.write(child.toString().getBytes());
                os.write(0x00);
            }

            compileArgument(child, os);
        }
    }

    void compileArrayStatement(Spin2DebugCommand node, OutputStream os, int op) throws IOException {
        if ((node.getArgumentsCount() % 2) != 0) {
            throw new CompilerException("expecting argument pairs", node.getToken());
        }
        for (int index = 0; index < node.getArgumentsCount(); index += 2) {
            if (first) {
                os.write(op | DBC_FLAG_NOCOMMA);
                first = false;
            }
            else {
                os.write(op);
            }

            if ((op & DBC_FLAG_NOEXPR) == 0) {
                os.write(node.getArgument(index).toString().getBytes());
                os.write(0x00);
            }

            compileArgument(node.getArgument(index), os);
            compileArgument(node.getArgument(index + 1), os);
        }
    }

    void compileArgument(Spin2DebugExpression node, OutputStream os) throws IOException {
        int value = node.getExpression().getNumber().intValue();
        if (node.isImmediate()) {
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
        else {
            os.write(0x80 | (value >> 8));
            os.write(value);
        }
    }

}
