/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2.bytecode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.expressions.LocalVariable;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.expressions.Variable;
import com.maccasoft.propeller.spin2.Spin2Context;

class VariableOpTest {

    @Test
    void testReadLocalVarLongShort() {
        Variable var = new LocalVariable("LONG", "a", new NumberLiteral(1), 4);

        VariableOp op = new VariableOp(new Spin2Context(), VariableOp.Op.Read, false, var);
        byte[] code = op.getBytes();

        Assertions.assertEquals("E1", toString(code));
        Assertions.assertEquals("VAR_READ LONG DBASE+$00001 (short)", op.toString());
        Assertions.assertEquals(code.length, op.getSize());
    }

    @Test
    void testReadLocalVarLong() {
        Variable var = new LocalVariable("LONG", "a", new NumberLiteral(1), 0x40);

        VariableOp op = new VariableOp(new Spin2Context(), VariableOp.Op.Read, false, var);
        byte[] code = op.getBytes();

        Assertions.assertEquals("5E 40 80", toString(code));
        Assertions.assertEquals("VAR_READ LONG DBASE+$00040", op.toString());
        Assertions.assertEquals(code.length, op.getSize());
    }

    @Test
    void testWriteLocalVarLongShort() {
        Variable var = new LocalVariable("LONG", "a", new NumberLiteral(1), 4);

        VariableOp op = new VariableOp(new Spin2Context(), VariableOp.Op.Write, false, var);
        byte[] code = op.getBytes();

        Assertions.assertEquals("F1", toString(code));
        Assertions.assertEquals("VAR_WRITE LONG DBASE+$00001 (short)", op.toString());
        Assertions.assertEquals(code.length, op.getSize());
    }

    @Test
    void testWriteLocalVarLong() {
        Variable var = new LocalVariable("LONG", "a", new NumberLiteral(1), 0x40);

        VariableOp op = new VariableOp(new Spin2Context(), VariableOp.Op.Write, false, var);
        byte[] code = op.getBytes();

        Assertions.assertEquals("5E 40 81", toString(code));
        Assertions.assertEquals("VAR_WRITE LONG DBASE+$00040", op.toString());
        Assertions.assertEquals(code.length, op.getSize());
    }

    @Test
    void testReadLocalVarByteShort() {
        Variable var = new LocalVariable("BYTE", "a", new NumberLiteral(1), 4);

        VariableOp op = new VariableOp(new Spin2Context(), VariableOp.Op.Read, false, var);
        byte[] code = op.getBytes();

        Assertions.assertEquals("52 04 80", toString(code));
        Assertions.assertEquals("VAR_READ BYTE DBASE+$00004", op.toString());
        Assertions.assertEquals(code.length, op.getSize());
    }

    @Test
    void testReadLocalVarWordShort() {
        Variable var = new LocalVariable("WORD", "a", new NumberLiteral(1), 4);

        VariableOp op = new VariableOp(new Spin2Context(), VariableOp.Op.Read, false, var);
        byte[] code = op.getBytes();

        Assertions.assertEquals("58 04 80", toString(code));
        Assertions.assertEquals("VAR_READ WORD DBASE+$00004", op.toString());
        Assertions.assertEquals(code.length, op.getSize());
    }

    @Test
    void testSetupLocalVarLongShort() {
        Variable var = new LocalVariable("LONG", "a", new NumberLiteral(1), 4);

        VariableOp op = new VariableOp(new Spin2Context(), VariableOp.Op.Setup, false, var);
        byte[] code = op.getBytes();

        Assertions.assertEquals("D1", toString(code));
        Assertions.assertEquals("VAR_SETUP LONG DBASE+$00001 (short)", op.toString());
        Assertions.assertEquals(code.length, op.getSize());
    }

    @Test
    void testSetupLocalVarLong() {
        Variable var = new LocalVariable("LONG", "a", new NumberLiteral(1), 0x40);

        VariableOp op = new VariableOp(new Spin2Context(), VariableOp.Op.Setup, false, var);
        byte[] code = op.getBytes();

        Assertions.assertEquals("5E 40", toString(code));
        Assertions.assertEquals("VAR_SETUP LONG DBASE+$00040", op.toString());
        Assertions.assertEquals(code.length, op.getSize());
    }

    @Test
    void testSetupLocalVarByteShort() {
        Variable var = new LocalVariable("BYTE", "a", new NumberLiteral(1), 4);

        VariableOp op = new VariableOp(new Spin2Context(), VariableOp.Op.Setup, false, var);
        byte[] code = op.getBytes();

        Assertions.assertEquals("52 04", toString(code));
        Assertions.assertEquals("VAR_SETUP BYTE DBASE+$00004", op.toString());
        Assertions.assertEquals(code.length, op.getSize());
    }

    @Test
    void testSetupLocalVarWordShort() {
        Variable var = new LocalVariable("WORD", "a", new NumberLiteral(1), 4);

        VariableOp op = new VariableOp(new Spin2Context(), VariableOp.Op.Setup, false, var);
        byte[] code = op.getBytes();

        Assertions.assertEquals("58 04", toString(code));
        Assertions.assertEquals("VAR_SETUP WORD DBASE+$00004", op.toString());
        Assertions.assertEquals(code.length, op.getSize());
    }

    @Test
    void testReadLocalVarLongIndexed() {
        Variable var = new LocalVariable("LONG", "a", new NumberLiteral(1), 0x4);

        VariableOp op = new VariableOp(new Spin2Context(), VariableOp.Op.Read, true, var);
        byte[] code = op.getBytes();

        Assertions.assertEquals("61 04 80", toString(code));
        Assertions.assertEquals("VAR_READ_INDEXED LONG DBASE+$00004", op.toString());
        Assertions.assertEquals(code.length, op.getSize());
    }

    @Test
    void testReadLocalVarWordIndexed() {
        Variable var = new LocalVariable("WORD", "a", new NumberLiteral(1), 0x4);

        VariableOp op = new VariableOp(new Spin2Context(), VariableOp.Op.Read, true, var);
        byte[] code = op.getBytes();

        Assertions.assertEquals("5B 04 80", toString(code));
        Assertions.assertEquals("VAR_READ_INDEXED WORD DBASE+$00004", op.toString());
        Assertions.assertEquals(code.length, op.getSize());
    }

    @Test
    void testReadLocalVarByteIndexed() {
        Variable var = new LocalVariable("BYTE", "a", new NumberLiteral(1), 0x4);

        VariableOp op = new VariableOp(new Spin2Context(), VariableOp.Op.Read, true, var);
        byte[] code = op.getBytes();

        Assertions.assertEquals("55 04 80", toString(code));
        Assertions.assertEquals("VAR_READ_INDEXED BYTE DBASE+$00004", op.toString());
        Assertions.assertEquals(code.length, op.getSize());
    }

    @Test
    void testReadVarLongShort() {
        Variable var = new Variable("LONG", "a", new NumberLiteral(1), 4);

        VariableOp op = new VariableOp(new Spin2Context(), VariableOp.Op.Read, false, var);
        byte[] code = op.getBytes();

        Assertions.assertEquals("C1 80", toString(code));
        Assertions.assertEquals("VAR_READ LONG VBASE+$00001 (short)", op.toString());
        Assertions.assertEquals(code.length, op.getSize());
    }

    @Test
    void testReadVarLong() {
        Variable var = new Variable("LONG", "a", new NumberLiteral(1), 0x40);

        VariableOp op = new VariableOp(new Spin2Context(), VariableOp.Op.Read, false, var);
        byte[] code = op.getBytes();

        Assertions.assertEquals("5D 40 80", toString(code));
        Assertions.assertEquals("VAR_READ LONG VBASE+$00040", op.toString());
        Assertions.assertEquals(code.length, op.getSize());
    }

    @Test
    void testWriteVarLongShort() {
        Variable var = new Variable("LONG", "a", new NumberLiteral(1), 4);

        VariableOp op = new VariableOp(new Spin2Context(), VariableOp.Op.Write, false, var);
        byte[] code = op.getBytes();

        Assertions.assertEquals("C1 81", toString(code));
        Assertions.assertEquals("VAR_WRITE LONG VBASE+$00001 (short)", op.toString());
        Assertions.assertEquals(code.length, op.getSize());
    }

    @Test
    void testWriteVarLong() {
        Variable var = new Variable("LONG", "a", new NumberLiteral(1), 0x40);

        VariableOp op = new VariableOp(new Spin2Context(), VariableOp.Op.Write, false, var);
        byte[] code = op.getBytes();

        Assertions.assertEquals("5D 40 81", toString(code));
        Assertions.assertEquals("VAR_WRITE LONG VBASE+$00040", op.toString());
        Assertions.assertEquals(code.length, op.getSize());
    }

    @Test
    void testAddressVarLongShort() {
        Variable var = new Variable("LONG", "a", new NumberLiteral(1), 4);

        VariableOp op = new VariableOp(new Spin2Context(), VariableOp.Op.Address, false, var);
        byte[] code = op.getBytes();

        Assertions.assertEquals("C1 7F", toString(code));
        Assertions.assertEquals("VAR_ADDRESS LONG VBASE+$00001 (short)", op.toString());
        Assertions.assertEquals(code.length, op.getSize());
    }

    @Test
    void testAddressVarLong() {
        Variable var = new Variable("LONG", "a", new NumberLiteral(1), 0x40);

        VariableOp op = new VariableOp(new Spin2Context(), VariableOp.Op.Address, false, var);
        byte[] code = op.getBytes();

        Assertions.assertEquals("5D 40 7F", toString(code));
        Assertions.assertEquals("VAR_ADDRESS LONG VBASE+$00040", op.toString());
        Assertions.assertEquals(code.length, op.getSize());
    }

    @Test
    void testAddressLocalVarLongShort() {
        Variable var = new LocalVariable("LONG", "a", new NumberLiteral(1), 4);

        VariableOp op = new VariableOp(new Spin2Context(), VariableOp.Op.Address, false, var);
        byte[] code = op.getBytes();

        Assertions.assertEquals("D1 7F", toString(code));
        Assertions.assertEquals("VAR_ADDRESS LONG DBASE+$00001 (short)", op.toString());
        Assertions.assertEquals(code.length, op.getSize());
    }

    @Test
    void testAddressLocalVarLong() {
        Variable var = new LocalVariable("LONG", "a", new NumberLiteral(1), 0x40);

        VariableOp op = new VariableOp(new Spin2Context(), VariableOp.Op.Address, false, var);
        byte[] code = op.getBytes();

        Assertions.assertEquals("5E 40 7F", toString(code));
        Assertions.assertEquals("VAR_ADDRESS LONG DBASE+$00040", op.toString());
        Assertions.assertEquals(code.length, op.getSize());
    }

    String toString(byte[] b) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < b.length; i++) {
            if (i != 0) {
                sb.append(" ");
            }
            sb.append(String.format("%02X", b[i] & 0xFF));
        }

        return sb.toString();
    }

}
