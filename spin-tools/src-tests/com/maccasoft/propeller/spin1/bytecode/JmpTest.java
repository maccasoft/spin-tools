/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
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

import com.maccasoft.propeller.expressions.ContextLiteral;
import com.maccasoft.propeller.expressions.Context;

class JmpTest {

    @Test
    void testShortForward() {
        Context targetContext = new Context();

        Jmp op = new Jmp(new Context(), new ContextLiteral(targetContext));
        op.getContext().setAddress(0x001C);
        targetContext.setAddress(0x002F);

        Assertions.assertEquals("04 11", toString(op.getBytes()));
        Assertions.assertEquals("JMP $0002F (17)", op.toString());
    }

    @Test
    void testLongForward() {
        Context targetContext = new Context();

        Jmp op = new Jmp(new Context(), new ContextLiteral(targetContext));
        op.getContext().setAddress(0x001C);
        targetContext.setAddress(0x0065);

        Assertions.assertEquals("04 80 46", toString(op.getBytes()));
        Assertions.assertEquals("JMP $00065 (70)", op.toString());
    }

    @Test
    void testLongLongForward() {
        Context targetContext = new Context();

        Jmp op = new Jmp(new Context(), new ContextLiteral(targetContext));
        op.getContext().setAddress(0x001C);
        targetContext.setAddress(0x0139);

        Assertions.assertEquals("04 81 1A", toString(op.getBytes()));
        Assertions.assertEquals("JMP $00139 (282)", op.toString());
    }

    @Test
    void testShortBackward() {
        Context targetContext = new Context();

        Jmp op = new Jmp(new Context(), new ContextLiteral(targetContext));
        op.getContext().setAddress(0x0029);
        targetContext.setAddress(0x0018);

        Assertions.assertEquals("04 6D", toString(op.getBytes()));
        Assertions.assertEquals("JMP $00018 (-19)", op.toString());
    }

    @Test
    void testLongBackward() {
        Context targetContext = new Context();

        Jmp op = new Jmp(new Context(), new ContextLiteral(targetContext));
        op.getContext().setAddress(0x005C);
        targetContext.setAddress(0x0018);

        Assertions.assertEquals("04 FF B9", toString(op.getBytes()));
        Assertions.assertEquals("JMP $00018 (-71)", op.toString());
    }

    @Test
    void testLongLongBackward() {
        Context targetContext = new Context();

        Jmp op = new Jmp(new Context(), new ContextLiteral(targetContext));
        op.getContext().setAddress(0x0131);
        targetContext.setAddress(0x0018);

        Assertions.assertEquals("04 FE E4", toString(op.getBytes()));
        Assertions.assertEquals("JMP $00018 (-284)", op.toString());
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
