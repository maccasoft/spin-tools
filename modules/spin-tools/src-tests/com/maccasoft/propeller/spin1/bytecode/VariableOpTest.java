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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.LocalVariable;

class VariableOpTest {

    @Test
    void testReadLocalVarLongShort() {
        LocalVariable var = new LocalVariable("LONG", "a", 1, 4);

        VariableOp op = new VariableOp(new Context(), VariableOp.Op.Read, var);

        Assertions.assertEquals("64", toString(op.getBytes()));
        Assertions.assertEquals("VAR_READ LONG DBASE+$0004 (short)", op.toString());
    }

    @Test
    void testReadLocalVarLong() {
        LocalVariable var = new LocalVariable("LONG", "a", 1, 64);

        VariableOp op = new VariableOp(new Context(), VariableOp.Op.Read, var);

        Assertions.assertEquals("CC 40", toString(op.getBytes()));
        Assertions.assertEquals("VAR_READ LONG DBASE+$0040", op.toString());
    }

    @Test
    void testReadLocalVarWord() {
        LocalVariable var = new LocalVariable("WORD", "a", 1, 4);

        VariableOp op = new VariableOp(new Context(), VariableOp.Op.Read, var);

        Assertions.assertEquals("AC 04", toString(op.getBytes()));
        Assertions.assertEquals("VAR_READ WORD DBASE+$0004", op.toString());
    }

    @Test
    void testReadLocalVarByte() {
        LocalVariable var = new LocalVariable("BYTE", "a", 1, 4);

        VariableOp op = new VariableOp(new Context(), VariableOp.Op.Read, var);

        Assertions.assertEquals("8C 04", toString(op.getBytes()));
        Assertions.assertEquals("VAR_READ BYTE DBASE+$0004", op.toString());
    }

    @Test
    void testWriteLocalVarLong() {
        LocalVariable var = new LocalVariable("LONG", "a", 1, 4);

        VariableOp op = new VariableOp(new Context(), VariableOp.Op.Write, var);

        Assertions.assertEquals("65", toString(op.getBytes()));
        Assertions.assertEquals("VAR_WRITE LONG DBASE+$0004 (short)", op.toString());
    }

    @Test
    void testWriteLocalVarWord() {
        LocalVariable var = new LocalVariable("WORD", "a", 1, 4);

        VariableOp op = new VariableOp(new Context(), VariableOp.Op.Write, var);

        Assertions.assertEquals("AD 04", toString(op.getBytes()));
        Assertions.assertEquals("VAR_WRITE WORD DBASE+$0004", op.toString());
    }

    @Test
    void testWriteLocalVarByte() {
        LocalVariable var = new LocalVariable("BYTE", "a", 1, 4);

        VariableOp op = new VariableOp(new Context(), VariableOp.Op.Write, var);

        Assertions.assertEquals("8D 04", toString(op.getBytes()));
        Assertions.assertEquals("VAR_WRITE BYTE DBASE+$0004", op.toString());
    }

    @Test
    void testModifyLocalVarLong() {
        LocalVariable var = new LocalVariable("LONG", "a", 1, 4);

        VariableOp op = new VariableOp(new Context(), VariableOp.Op.Assign, var);

        Assertions.assertEquals("66", toString(op.getBytes()));
        Assertions.assertEquals("VAR_MODIFY LONG DBASE+$0004 (short)", op.toString());
    }

    @Test
    void testModifyLocalVarWord() {
        LocalVariable var = new LocalVariable("WORD", "a", 1, 4);

        VariableOp op = new VariableOp(new Context(), VariableOp.Op.Assign, var);

        Assertions.assertEquals("AE 04", toString(op.getBytes()));
        Assertions.assertEquals("VAR_MODIFY WORD DBASE+$0004", op.toString());
    }

    @Test
    void testModifyLocalVarByte() {
        LocalVariable var = new LocalVariable("BYTE", "a", 1, 4);

        VariableOp op = new VariableOp(new Context(), VariableOp.Op.Assign, var);

        Assertions.assertEquals("8E 04", toString(op.getBytes()));
        Assertions.assertEquals("VAR_MODIFY BYTE DBASE+$0004", op.toString());
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
