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
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.SpinObject.DataObject;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.spin2.Spin2PAsmDebugLine.Spin2DebugCommand;
import com.maccasoft.propeller.spin2.Spin2PAsmDebugLine.Spin2DebugExpression;

public class Spin2Debug {

    public static final int DBC_DONE = 0;
    public static final int DBC_ASMMODE = 1; // Switches into PASM mode...
    public static final int DBC_IF = 2;
    public static final int DBC_IFNOT = 3;
    public static final int DBC_COGN = 4;
    public static final int DBC_CHAR = 5;
    public static final int DBC_STRING = 6;
    public static final int DBC_DELAY = 7;
    public static final int DBC_PC_KEY = 8;
    public static final int DBC_PC_MOUSE = 9;

    // Flags
    public static final int DBC_FLAG_NOCOMMA = 0x01;
    public static final int DBC_FLAG_NOEXPR = 0x02;
    public static final int DBC_FLAG_ARRAY = 0x10;
    public static final int DBC_FLAG_SIGNED = 0x20;
    // Numeric sizes
    public static final int DBC_SIZE_BYTE = 0x04;
    public static final int DBC_SIZE_WORD = 0x08;
    public static final int DBC_SIZE_LONG = 0x0C;
    // Output type
    public static final int DBC_TYPE_STR = 0x20 | DBC_SIZE_BYTE;
    public static final int DBC_TYPE_FLP = 0x20; // Note the overlap with the signed flag and the string type
    public static final int DBC_TYPE_DEC = 0x40;
    public static final int DBC_TYPE_HEX = 0x80;
    public static final int DBC_TYPE_BIN = 0xC0;

    boolean first;

    public List<DataObject> compileDebugStatement(Spin2StatementNode root) {
        boolean skipCogN = false;
        StringBuilder sb = new StringBuilder();
        List<DataObject> list = new ArrayList<>();

        if (root.getChildCount() != 0) {
            Spin2StatementNode child0 = root.getChild(0);
            String cmd = child0.getText();

            if (child0.getType() == Token.STRING) {
                if (cmd.startsWith("\"")) {
                    cmd = cmd.substring(1);
                }
                if (cmd.startsWith("`")) {
                    skipCogN = true;
                }
            }
            else if ("IF".equalsIgnoreCase(cmd) || "IFNOT".equalsIgnoreCase(cmd)) {
                skipCogN = true;
            }

            if (!skipCogN) {
                list.add(new DataObject(new byte[] {
                    DBC_COGN
                }, "COGN"));
            }

            first = true;

            for (Spin2StatementNode node : root.getChilds()) {
                try {
                    if (node.getType() == Token.STRING) {
                        String s = node.getText();
                        if (s.startsWith("\"")) {
                            s = s.substring(1, s.length() - 1);
                        }
                        sb.append(s);
                    }
                    else if (node.getType() == Token.NUMBER) {
                        NumberLiteral v = new NumberLiteral(node.getText());
                        sb.append((char) v.getNumber().intValue());
                    }
                    else {
                        int flags = 0;

                        if (sb.length() != 0) {
                            ByteArrayOutputStream os = new ByteArrayOutputStream();
                            os.write(DBC_STRING);
                            os.write(sb.toString().getBytes());
                            os.write(0x00);
                            list.add(new DataObject(os.toByteArray(), "STRING (" + sanitizeString(sb.toString().getBytes()) + ")"));
                            sb = new StringBuilder();
                            first = true;
                        }

                        cmd = node.getText();
                        if (cmd.startsWith("`")) {
                            flags |= DBC_FLAG_NOEXPR;
                            cmd = cmd.substring(1);
                        }
                        if (cmd.endsWith("_")) {
                            flags |= DBC_FLAG_NOEXPR;
                            cmd = cmd.substring(0, cmd.length() - "_".length());
                        }

                        switch (cmd.toUpperCase()) {
                            case "#":
                                for (int i = 0; i < node.getChildCount(); i++) {
                                    list.add(new DataObject(new byte[] {
                                        DBC_CHAR
                                    }, "CHAR"));
                                }
                                break;

                            case ".":
                            case "FDEC":
                                list.addAll(compileSpinStatement(node, DBC_TYPE_FLP | DBC_SIZE_LONG | flags, "FDEC"));
                                break;
                            case "FDEC_ARRAY":
                                list.addAll(compileSpinArrayStatement(node, DBC_TYPE_FLP | DBC_SIZE_LONG | DBC_FLAG_ARRAY | flags, cmd));
                                break;

                            case "UDEC":
                                list.addAll(compileSpinStatement(node, DBC_TYPE_DEC | flags, cmd));
                                break;
                            case "UDEC_BYTE":
                                list.addAll(compileSpinStatement(node, DBC_TYPE_DEC | DBC_SIZE_BYTE | flags, cmd));
                                break;
                            case "UDEC_WORD":
                                list.addAll(compileSpinStatement(node, DBC_TYPE_DEC | DBC_SIZE_WORD | flags, cmd));
                                break;
                            case "UDEC_LONG":
                                list.addAll(compileSpinStatement(node, DBC_TYPE_DEC | DBC_SIZE_LONG | flags, cmd));
                                break;
                            case "UDEC_BYTE_ARRAY":
                                list.addAll(compileSpinArrayStatement(node, DBC_TYPE_DEC | DBC_SIZE_BYTE | DBC_FLAG_ARRAY | flags, cmd));
                                break;
                            case "UDEC_WORD_ARRAY":
                                list.addAll(compileSpinArrayStatement(node, DBC_TYPE_DEC | DBC_SIZE_WORD | DBC_FLAG_ARRAY | flags, cmd));
                                break;
                            case "UDEC_LONG_ARRAY":
                                list.addAll(compileSpinArrayStatement(node, DBC_TYPE_DEC | DBC_SIZE_LONG | DBC_FLAG_ARRAY | flags, cmd));
                                break;

                            case "":
                            case "SDEC":
                                list.addAll(compileSpinStatement(node, DBC_TYPE_DEC | DBC_FLAG_SIGNED | flags, "SDEC"));
                                break;
                            case "SDEC_BYTE":
                                list.addAll(compileSpinStatement(node, DBC_TYPE_DEC | DBC_SIZE_BYTE | DBC_FLAG_SIGNED | flags, cmd));
                                break;
                            case "SDEC_WORD":
                                list.addAll(compileSpinStatement(node, DBC_TYPE_DEC | DBC_SIZE_WORD | DBC_FLAG_SIGNED | flags, cmd));
                                break;
                            case "SDEC_LONG":
                                list.addAll(compileSpinStatement(node, DBC_TYPE_DEC | DBC_SIZE_LONG | DBC_FLAG_SIGNED | flags, cmd));
                                break;
                            case "SDEC_BYTE_ARRAY":
                                list.addAll(compileSpinArrayStatement(node, DBC_TYPE_DEC | DBC_SIZE_BYTE | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags, cmd));
                                break;
                            case "SDEC_WORD_ARRAY":
                                list.addAll(compileSpinArrayStatement(node, DBC_TYPE_DEC | DBC_SIZE_WORD | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags, cmd));
                                break;
                            case "SDEC_LONG_ARRAY":
                                list.addAll(compileSpinArrayStatement(node, DBC_TYPE_DEC | DBC_SIZE_LONG | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags, cmd));
                                break;

                            case "$":
                            case "UHEX":
                                list.addAll(compileSpinStatement(node, DBC_TYPE_HEX | flags, "UHEX"));
                                break;
                            case "UHEX_BYTE":
                                list.addAll(compileSpinStatement(node, DBC_TYPE_HEX | DBC_SIZE_BYTE | flags, cmd));
                                break;
                            case "UHEX_WORD":
                                list.addAll(compileSpinStatement(node, DBC_TYPE_HEX | DBC_SIZE_WORD | flags, cmd));
                                break;
                            case "UHEX_LONG":
                                list.addAll(compileSpinStatement(node, DBC_TYPE_HEX | DBC_SIZE_LONG | flags, cmd));
                                break;
                            case "UHEX_BYTE_ARRAY":
                                list.addAll(compileSpinArrayStatement(node, DBC_TYPE_HEX | DBC_SIZE_BYTE | DBC_FLAG_ARRAY | flags, cmd));
                                break;
                            case "UHEX_WORD_ARRAY":
                                list.addAll(compileSpinArrayStatement(node, DBC_TYPE_HEX | DBC_SIZE_WORD | DBC_FLAG_ARRAY | flags, cmd));
                                break;
                            case "UHEX_LONG_ARRAY":
                                list.addAll(compileSpinArrayStatement(node, DBC_TYPE_HEX | DBC_SIZE_LONG | DBC_FLAG_ARRAY | flags, cmd));
                                break;

                            case "SHEX":
                                list.addAll(compileSpinStatement(node, DBC_TYPE_HEX | DBC_FLAG_SIGNED | flags, cmd));
                                break;
                            case "SHEX_BYTE":
                                list.addAll(compileSpinStatement(node, DBC_TYPE_HEX | DBC_SIZE_BYTE | DBC_FLAG_SIGNED | flags, cmd));
                                break;
                            case "SHEX_WORD":
                                list.addAll(compileSpinStatement(node, DBC_TYPE_HEX | DBC_SIZE_WORD | DBC_FLAG_SIGNED | flags, cmd));
                                break;
                            case "SHEX_LONG":
                                list.addAll(compileSpinStatement(node, DBC_TYPE_HEX | DBC_SIZE_LONG | DBC_FLAG_SIGNED | flags, cmd));
                                break;
                            case "SHEX_BYTE_ARRAY":
                                list.addAll(compileSpinArrayStatement(node, DBC_TYPE_HEX | DBC_SIZE_BYTE | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags, cmd));
                                break;
                            case "SHEX_WORD_ARRAY":
                                list.addAll(compileSpinArrayStatement(node, DBC_TYPE_HEX | DBC_SIZE_WORD | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags, cmd));
                                break;
                            case "SHEX_LONG_ARRAY":
                                list.addAll(compileSpinArrayStatement(node, DBC_TYPE_HEX | DBC_SIZE_LONG | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags, cmd));
                                break;

                            case "%":
                            case "UBIN":
                                list.addAll(compileSpinStatement(node, DBC_TYPE_BIN | flags, "UBIN"));
                                break;
                            case "UBIN_BYTE":
                                list.addAll(compileSpinStatement(node, DBC_TYPE_BIN | DBC_SIZE_BYTE | flags, cmd));
                                break;
                            case "UBIN_WORD":
                                list.addAll(compileSpinStatement(node, DBC_TYPE_BIN | DBC_SIZE_WORD | flags, cmd));
                                break;
                            case "UBIN_LONG":
                                list.addAll(compileSpinStatement(node, DBC_TYPE_BIN | DBC_SIZE_LONG | flags, cmd));
                                break;
                            case "UBIN_BYTE_ARRAY":
                                list.addAll(compileSpinArrayStatement(node, DBC_TYPE_BIN | DBC_SIZE_BYTE | DBC_FLAG_ARRAY | flags, cmd));
                                break;
                            case "UBIN_WORD_ARRAY":
                                list.addAll(compileSpinArrayStatement(node, DBC_TYPE_BIN | DBC_SIZE_WORD | DBC_FLAG_ARRAY | flags, cmd));
                                break;
                            case "UBIN_LONG_ARRAY":
                                list.addAll(compileSpinArrayStatement(node, DBC_TYPE_BIN | DBC_SIZE_LONG | DBC_FLAG_ARRAY | flags, cmd));
                                break;

                            case "SBIN":
                                list.addAll(compileSpinStatement(node, DBC_TYPE_BIN | DBC_FLAG_SIGNED | flags, cmd));
                                break;
                            case "SBIN_BYTE":
                                list.addAll(compileSpinStatement(node, DBC_TYPE_BIN | DBC_SIZE_BYTE | DBC_FLAG_SIGNED | flags, cmd));
                                break;
                            case "SBIN_WORD":
                                list.addAll(compileSpinStatement(node, DBC_TYPE_BIN | DBC_SIZE_WORD | DBC_FLAG_SIGNED | flags, cmd));
                                break;
                            case "SBIN_LONG":
                                list.addAll(compileSpinStatement(node, DBC_TYPE_BIN | DBC_SIZE_LONG | DBC_FLAG_SIGNED | flags, cmd));
                                break;
                            case "SBIN_BYTE_ARRAY":
                                list.addAll(compileSpinArrayStatement(node, DBC_TYPE_BIN | DBC_SIZE_BYTE | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags, cmd));
                                break;
                            case "SBIN_WORD_ARRAY":
                                list.addAll(compileSpinArrayStatement(node, DBC_TYPE_BIN | DBC_SIZE_WORD | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags, cmd));
                                break;
                            case "SBIN_LONG_ARRAY":
                                list.addAll(compileSpinArrayStatement(node, DBC_TYPE_BIN | DBC_SIZE_LONG | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags, cmd));
                                break;

                            case "ZSTR":
                                list.addAll(compileSpinStatement(node, DBC_TYPE_STR | flags, cmd));
                                break;
                            case "LSTR":
                                list.addAll(compileSpinStatement(node, DBC_TYPE_STR | DBC_FLAG_ARRAY | flags, cmd));
                                break;

                            case "DLY":
                                list.add(new DataObject(new byte[] {
                                    DBC_DELAY
                                }, cmd.toUpperCase()));
                                break;

                            case "IF":
                                list.add(new DataObject(new byte[] {
                                    DBC_IF
                                }, cmd.toUpperCase()));
                                if (skipCogN) {
                                    list.add(new DataObject(new byte[] {
                                        DBC_COGN
                                    }, "COGN"));
                                    skipCogN = false;
                                }
                                break;
                            case "IFNOT":
                                list.add(new DataObject(new byte[] {
                                    DBC_IFNOT
                                }, cmd.toUpperCase()));
                                if (skipCogN) {
                                    list.add(new DataObject(new byte[] {
                                        DBC_COGN
                                    }, "COGN"));
                                    skipCogN = false;
                                }
                                break;

                            case "PC_KEY":
                                for (int i = 0; i < node.getChildCount(); i++) {
                                    list.add(new DataObject(new byte[] {
                                        DBC_PC_KEY
                                    }, cmd.toUpperCase()));
                                }
                                break;
                            case "PC_MOUSE":
                                for (int i = 0; i < node.getChildCount(); i++) {
                                    list.add(new DataObject(new byte[] {
                                        DBC_PC_MOUSE
                                    }, cmd.toUpperCase()));
                                }
                                break;

                            default:
                                throw new CompilerException("Unknown debug statement '" + cmd + "'", node.getToken());
                        }
                    }
                } catch (IOException e) {
                    // Do nothing
                }
            }

            if (sb.length() != 0) {
                try {
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    os.write(DBC_STRING);
                    os.write(sb.toString().getBytes());
                    os.write(0x00);
                    list.add(new DataObject(os.toByteArray(), "STRING (" + sanitizeString(sb.toString().getBytes()) + ")"));
                } catch (Exception e) {
                    // Do nothing
                }
            }
        }

        list.add(new DataObject(new byte[] {
            DBC_DONE
        }, "DONE"));

        return list;
    }

    List<DataObject> compileSpinStatement(Spin2StatementNode node, int op, String cmd) throws IOException {
        List<DataObject> l = new ArrayList<>();

        for (Spin2StatementNode child : node.getChilds()) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            StringBuilder sb = new StringBuilder();

            if (first) {
                os.write(op | DBC_FLAG_NOCOMMA);
                first = false;
            }
            else {
                os.write(op);
            }
            sb.append(cmd.toUpperCase());

            if ((op & DBC_FLAG_NOEXPR) == 0) {
                byte[] b = child.toString().getBytes();

                os.write(b);
                os.write(0x00);

                sb.append("(");
                sb.append(sanitizeString(b));
                sb.append(")");
            }

            l.add(new DataObject(os.toByteArray(), sb.toString()));
        }

        return l;
    }

    List<DataObject> compileSpinArrayStatement(Spin2StatementNode node, int op, String cmd) throws IOException {
        List<DataObject> l = new ArrayList<>();

        if ((node.getChildCount() % 2) != 0) {
            throw new CompilerException("expecting argument pairs", node.getToken());
        }

        for (int index = 0; index < node.getChildCount(); index += 2) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            StringBuilder sb = new StringBuilder();

            if (first) {
                os.write(op | DBC_FLAG_NOCOMMA);
                first = false;
            }
            else {
                os.write(op);
            }
            sb.append(cmd.toUpperCase());

            if ((op & DBC_FLAG_NOEXPR) == 0) {
                byte[] b = node.getChild(index).getText().getBytes();

                os.write(b);
                os.write(0x00);

                sb.append("(");
                sb.append(sanitizeString(b));
                sb.append(")");
            }

            l.add(new DataObject(os.toByteArray(), sb.toString()));
        }

        return l;
    }

    public List<DataObject> compilePAsmDebugStatement(Spin2PAsmDebugLine root) {
        boolean skipCogN = false;
        StringBuilder sb = new StringBuilder();
        List<DataObject> list = new ArrayList<>();

        if (root.getStatementsCount() != 0) {
            Spin2DebugCommand child0 = root.getStatement(0);
            String cmd = child0.getText();

            if (child0.getType() == Token.STRING) {
                if (cmd.startsWith("\"")) {
                    cmd = cmd.substring(1);
                }
                if (cmd.startsWith("`")) {
                    skipCogN = true;
                }
            }
            else if ("IF".equalsIgnoreCase(cmd) || "IFNOT".equalsIgnoreCase(cmd)) {
                skipCogN = true;
            }

            list.add(new DataObject(new byte[] {
                DBC_ASMMODE
            }, "ASMMODE"));

            if (!skipCogN) {
                list.add(new DataObject(new byte[] {
                    DBC_COGN
                }, "COGN"));
            }

            first = true;

            for (Spin2DebugCommand node : root.getStatements()) {
                try {
                    if (node.getType() == Token.STRING) {
                        String s = node.getText();
                        if (s.startsWith("\"")) {
                            s = s.substring(1, s.length() - 1);
                        }
                        sb.append(s);
                    }
                    else if (node.getType() == Token.NUMBER) {
                        NumberLiteral v = new NumberLiteral(node.getText());
                        sb.append((char) v.getNumber().intValue());
                    }
                    else {
                        int flags = 0;

                        if (sb.length() != 0) {
                            ByteArrayOutputStream os = new ByteArrayOutputStream();
                            os.write(DBC_STRING);
                            os.write(sb.toString().getBytes());
                            os.write(0x00);
                            list.add(new DataObject(os.toByteArray(), "STRING (" + sanitizeString(sb.toString().getBytes()) + ")"));
                            sb = new StringBuilder();
                            first = true;
                        }

                        cmd = node.getText();
                        if (cmd.startsWith("`")) {
                            flags |= DBC_FLAG_NOEXPR;
                            cmd = cmd.substring(1);
                        }
                        if (cmd.endsWith("_")) {
                            flags |= DBC_FLAG_NOEXPR;
                            cmd = cmd.substring(0, cmd.length() - "_".length());
                        }

                        switch (cmd.toUpperCase()) {
                            case "#":
                                for (Spin2DebugExpression child : node.getArguments()) {
                                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                                    os.write(DBC_CHAR);
                                    compileArgument(child, os);
                                    list.add(new DataObject(os.toByteArray(), "CHAR"));
                                }
                                break;

                            case ".":
                            case "FDEC":
                                list.addAll(compileSimpleStatement(node, DBC_TYPE_FLP | DBC_SIZE_LONG | flags, "FDEC"));
                                break;
                            case "FDEC_REG_ARRAY":
                                list.addAll(compileArrayStatement(node, DBC_TYPE_FLP | DBC_FLAG_ARRAY | flags, cmd));
                                break;
                            case "FDEC_ARRAY":
                                list.addAll(compileArrayStatement(node, DBC_TYPE_FLP | DBC_SIZE_LONG | DBC_FLAG_ARRAY | flags, cmd));
                                break;

                            case "UDEC":
                                list.addAll(compileSimpleStatement(node, DBC_TYPE_DEC | flags, cmd));
                                break;
                            case "UDEC_BYTE":
                                list.addAll(compileSimpleStatement(node, DBC_TYPE_DEC | DBC_SIZE_BYTE | flags, cmd));
                                break;
                            case "UDEC_WORD":
                                list.addAll(compileSimpleStatement(node, DBC_TYPE_DEC | DBC_SIZE_WORD | flags, cmd));
                                break;
                            case "UDEC_LONG":
                                list.addAll(compileSimpleStatement(node, DBC_TYPE_DEC | DBC_SIZE_LONG | flags, cmd));
                                break;
                            case "UDEC_REG_ARRAY":
                                list.addAll(compileArrayStatement(node, DBC_TYPE_DEC | DBC_FLAG_ARRAY | flags, cmd));
                                break;
                            case "UDEC_BYTE_ARRAY":
                                list.addAll(compileArrayStatement(node, DBC_TYPE_DEC | DBC_SIZE_BYTE | DBC_FLAG_ARRAY | flags, cmd));
                                break;
                            case "UDEC_WORD_ARRAY":
                                list.addAll(compileArrayStatement(node, DBC_TYPE_DEC | DBC_SIZE_WORD | DBC_FLAG_ARRAY | flags, cmd));
                                break;
                            case "UDEC_LONG_ARRAY":
                                list.addAll(compileArrayStatement(node, DBC_TYPE_DEC | DBC_SIZE_LONG | DBC_FLAG_ARRAY | flags, cmd));
                                break;

                            case "":
                            case "SDEC":
                                list.addAll(compileSimpleStatement(node, DBC_TYPE_DEC | DBC_FLAG_SIGNED | flags, "SDEC"));
                                break;
                            case "SDEC_BYTE":
                                list.addAll(compileSimpleStatement(node, DBC_TYPE_DEC | DBC_SIZE_BYTE | DBC_FLAG_SIGNED | flags, cmd));
                                break;
                            case "SDEC_WORD":
                                list.addAll(compileSimpleStatement(node, DBC_TYPE_DEC | DBC_SIZE_WORD | DBC_FLAG_SIGNED | flags, cmd));
                                break;
                            case "SDEC_LONG":
                                list.addAll(compileSimpleStatement(node, DBC_TYPE_DEC | DBC_SIZE_LONG | DBC_FLAG_SIGNED | flags, cmd));
                                break;
                            case "SDEC_REG_ARRAY":
                                list.addAll(compileArrayStatement(node, DBC_TYPE_DEC | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags, cmd));
                                break;
                            case "SDEC_BYTE_ARRAY":
                                list.addAll(compileArrayStatement(node, DBC_TYPE_DEC | DBC_SIZE_BYTE | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags, cmd));
                                break;
                            case "SDEC_WORD_ARRAY":
                                list.addAll(compileArrayStatement(node, DBC_TYPE_DEC | DBC_SIZE_WORD | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags, cmd));
                                break;
                            case "SDEC_LONG_ARRAY":
                                list.addAll(compileArrayStatement(node, DBC_TYPE_DEC | DBC_SIZE_LONG | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags, cmd));
                                break;

                            case "$":
                            case "UHEX":
                                list.addAll(compileSimpleStatement(node, DBC_TYPE_HEX | flags, "UHEX"));
                                break;
                            case "UHEX_BYTE":
                                list.addAll(compileSimpleStatement(node, DBC_TYPE_HEX | DBC_SIZE_BYTE | flags, cmd));
                                break;
                            case "UHEX_WORD":
                                list.addAll(compileSimpleStatement(node, DBC_TYPE_HEX | DBC_SIZE_WORD | flags, cmd));
                                break;
                            case "UHEX_LONG":
                                list.addAll(compileSimpleStatement(node, DBC_TYPE_HEX | DBC_SIZE_LONG | flags, cmd));
                                break;
                            case "UHEX_REG_ARRAY":
                                list.addAll(compileArrayStatement(node, DBC_TYPE_HEX | DBC_FLAG_ARRAY | flags, cmd));
                                break;
                            case "UHEX_BYTE_ARRAY":
                                list.addAll(compileArrayStatement(node, DBC_TYPE_HEX | DBC_SIZE_BYTE | DBC_FLAG_ARRAY | flags, cmd));
                                break;
                            case "UHEX_WORD_ARRAY":
                                list.addAll(compileArrayStatement(node, DBC_TYPE_HEX | DBC_SIZE_WORD | DBC_FLAG_ARRAY | flags, cmd));
                                break;
                            case "UHEX_LONG_ARRAY":
                                list.addAll(compileArrayStatement(node, DBC_TYPE_HEX | DBC_SIZE_LONG | DBC_FLAG_ARRAY | flags, cmd));
                                break;

                            case "SHEX":
                                list.addAll(compileSimpleStatement(node, DBC_TYPE_HEX | DBC_FLAG_SIGNED | flags, cmd));
                                break;
                            case "SHEX_BYTE":
                                list.addAll(compileSimpleStatement(node, DBC_TYPE_HEX | DBC_SIZE_BYTE | DBC_FLAG_SIGNED | flags, cmd));
                                break;
                            case "SHEX_WORD":
                                list.addAll(compileSimpleStatement(node, DBC_TYPE_HEX | DBC_SIZE_WORD | DBC_FLAG_SIGNED | flags, cmd));
                                break;
                            case "SHEX_LONG":
                                list.addAll(compileSimpleStatement(node, DBC_TYPE_HEX | DBC_SIZE_LONG | DBC_FLAG_SIGNED | flags, cmd));
                                break;
                            case "SHEX_REG_ARRAY":
                                list.addAll(compileArrayStatement(node, DBC_TYPE_HEX | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags, cmd));
                                break;
                            case "SHEX_BYTE_ARRAY":
                                list.addAll(compileArrayStatement(node, DBC_TYPE_HEX | DBC_SIZE_BYTE | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags, cmd));
                                break;
                            case "SHEX_WORD_ARRAY":
                                list.addAll(compileArrayStatement(node, DBC_TYPE_HEX | DBC_SIZE_WORD | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags, cmd));
                                break;
                            case "SHEX_LONG_ARRAY":
                                list.addAll(compileArrayStatement(node, DBC_TYPE_HEX | DBC_SIZE_LONG | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags, cmd));
                                break;

                            case "%":
                            case "UBIN":
                                list.addAll(compileSimpleStatement(node, DBC_TYPE_BIN | flags, "UBIN"));
                                break;
                            case "UBIN_BYTE":
                                list.addAll(compileSimpleStatement(node, DBC_TYPE_BIN | DBC_SIZE_BYTE | flags, cmd));
                                break;
                            case "UBIN_WORD":
                                list.addAll(compileSimpleStatement(node, DBC_TYPE_BIN | DBC_SIZE_WORD | flags, cmd));
                                break;
                            case "UBIN_LONG":
                                list.addAll(compileSimpleStatement(node, DBC_TYPE_BIN | DBC_SIZE_LONG | flags, cmd));
                                break;
                            case "UBIN_REG_ARRAY":
                                list.addAll(compileArrayStatement(node, DBC_TYPE_BIN | DBC_FLAG_ARRAY | flags, cmd));
                                break;
                            case "UBIN_BYTE_ARRAY":
                                list.addAll(compileArrayStatement(node, DBC_TYPE_BIN | DBC_SIZE_BYTE | DBC_FLAG_ARRAY | flags, cmd));
                                break;
                            case "UBIN_WORD_ARRAY":
                                list.addAll(compileArrayStatement(node, DBC_TYPE_BIN | DBC_SIZE_WORD | DBC_FLAG_ARRAY | flags, cmd));
                                break;
                            case "UBIN_LONG_ARRAY":
                                list.addAll(compileArrayStatement(node, DBC_TYPE_BIN | DBC_SIZE_LONG | DBC_FLAG_ARRAY | flags, cmd));
                                break;

                            case "SBIN":
                                list.addAll(compileSimpleStatement(node, DBC_TYPE_BIN | DBC_FLAG_SIGNED | flags, cmd));
                                break;
                            case "SBIN_BYTE":
                                list.addAll(compileSimpleStatement(node, DBC_TYPE_BIN | DBC_SIZE_BYTE | DBC_FLAG_SIGNED | flags, cmd));
                                break;
                            case "SBIN_WORD":
                                list.addAll(compileSimpleStatement(node, DBC_TYPE_BIN | DBC_SIZE_WORD | DBC_FLAG_SIGNED | flags, cmd));
                                break;
                            case "SBIN_LONG":
                                list.addAll(compileSimpleStatement(node, DBC_TYPE_BIN | DBC_SIZE_LONG | DBC_FLAG_SIGNED | flags, cmd));
                                break;
                            case "SBIN_REG_ARRAY":
                                list.addAll(compileArrayStatement(node, DBC_TYPE_BIN | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags, cmd));
                                break;
                            case "SBIN_BYTE_ARRAY":
                                list.addAll(compileArrayStatement(node, DBC_TYPE_BIN | DBC_SIZE_BYTE | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags, cmd));
                                break;
                            case "SBIN_WORD_ARRAY":
                                list.addAll(compileArrayStatement(node, DBC_TYPE_BIN | DBC_SIZE_WORD | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags, cmd));
                                break;
                            case "SBIN_LONG_ARRAY":
                                list.addAll(compileArrayStatement(node, DBC_TYPE_BIN | DBC_SIZE_LONG | DBC_FLAG_SIGNED | DBC_FLAG_ARRAY | flags, cmd));
                                break;

                            case "ZSTR":
                                list.addAll(compileSimpleStatement(node, DBC_TYPE_STR | flags, cmd));
                                break;
                            case "LSTR":
                                list.addAll(compileArrayStatement(node, DBC_TYPE_STR | DBC_FLAG_ARRAY | flags, cmd));
                                break;

                            case "DLY": {
                                ByteArrayOutputStream os = new ByteArrayOutputStream();
                                os.write(DBC_DELAY);
                                if (node.getArgumentsCount() != 1) {
                                    throw new CompilerException("expecting one argument", node.getToken());
                                }
                                compileArgument(node.getArgument(0), os);
                                list.add(new DataObject(os.toByteArray(), cmd.toUpperCase()));
                                break;
                            }

                            case "IF": {
                                ByteArrayOutputStream os = new ByteArrayOutputStream();
                                os.write(DBC_IF);
                                if (node.getArgumentsCount() != 1) {
                                    throw new CompilerException("expecting one argument", node.getToken());
                                }
                                compileArgument(node.getArgument(0), os);
                                list.add(new DataObject(os.toByteArray(), cmd.toUpperCase()));
                                if (skipCogN) {
                                    list.add(new DataObject(new byte[] {
                                        DBC_COGN
                                    }, "COGN"));
                                    skipCogN = false;
                                }
                                break;
                            }
                            case "IFNOT": {
                                ByteArrayOutputStream os = new ByteArrayOutputStream();
                                os.write(DBC_IFNOT);
                                if (node.getArgumentsCount() != 1) {
                                    throw new CompilerException("expecting one argument", node.getToken());
                                }
                                compileArgument(node.getArgument(0), os);
                                list.add(new DataObject(os.toByteArray(), cmd.toUpperCase()));
                                if (skipCogN) {
                                    list.add(new DataObject(new byte[] {
                                        DBC_COGN
                                    }, "COGN"));
                                    skipCogN = false;
                                }
                                break;
                            }

                            case "PC_KEY":
                                for (Spin2DebugExpression child : node.getArguments()) {
                                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                                    os.write(DBC_PC_KEY);
                                    compileArgument(child, os);
                                    list.add(new DataObject(os.toByteArray(), cmd.toUpperCase()));
                                }
                                break;
                            case "PC_MOUSE":
                                for (Spin2DebugExpression child : node.getArguments()) {
                                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                                    os.write(DBC_PC_MOUSE);
                                    compileArgument(child, os);
                                    list.add(new DataObject(os.toByteArray(), cmd.toUpperCase()));
                                }
                                break;

                            default:
                                throw new CompilerException("Unknown debug statement '" + cmd + "'", node.getToken());
                        }
                    }
                } catch (IOException e) {
                    // Do nothing
                }
            }

            if (sb.length() != 0) {
                try {
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    os.write(DBC_STRING);
                    os.write(sb.toString().getBytes());
                    os.write(0x00);
                    list.add(new DataObject(os.toByteArray(), "STRING (" + sanitizeString(sb.toString().getBytes()) + ")"));
                } catch (Exception e) {
                    // Do nothing
                }
            }
        }

        list.add(new DataObject(new byte[] {
            DBC_DONE
        }, "DONE"));

        return list;
    }

    List<DataObject> compileSimpleStatement(Spin2DebugCommand node, int op, String cmd) throws IOException {
        List<DataObject> l = new ArrayList<>();

        for (Spin2DebugExpression child : node.getArguments()) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            StringBuilder sb = new StringBuilder();

            if (first) {
                os.write(op | DBC_FLAG_NOCOMMA);
                first = false;
            }
            else {
                os.write(op);
            }
            sb.append(cmd.toUpperCase());

            if ((op & DBC_FLAG_NOEXPR) == 0) {
                byte[] b = child.toString().getBytes();

                os.write(b);
                os.write(0x00);

                sb.append("(");
                sb.append(sanitizeString(b));
                sb.append(")");
            }

            compileArgument(child, os);

            l.add(new DataObject(os.toByteArray(), sb.toString()));
        }

        return l;
    }

    List<DataObject> compileArrayStatement(Spin2DebugCommand node, int op, String cmd) throws IOException {
        List<DataObject> l = new ArrayList<>();

        if ((node.getArgumentsCount() % 2) != 0) {
            throw new CompilerException("expecting argument pairs", node.getToken());
        }

        for (int index = 0; index < node.getArgumentsCount(); index += 2) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            StringBuilder sb = new StringBuilder();

            if (first) {
                os.write(op | DBC_FLAG_NOCOMMA);
                first = false;
            }
            else {
                os.write(op);
            }
            sb.append(cmd.toUpperCase());

            if ((op & DBC_FLAG_NOEXPR) == 0) {
                byte[] b = node.getArgument(index).toString().getBytes();

                os.write(b);
                os.write(0x00);

                sb.append("(");
                sb.append(sanitizeString(b));
                sb.append(")");
            }

            compileArgument(node.getArgument(index), os);
            compileArgument(node.getArgument(index + 1), os);

            l.add(new DataObject(os.toByteArray(), sb.toString()));
        }

        return l;
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

    String sanitizeString(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            sb.append((char) ((b[i] >= ' ' && b[i] <= 0x7F) ? b[i] : '.'));
        }
        return sb.toString();
    }

}
