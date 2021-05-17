/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin.instructions;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.spin.P2Asm;
import com.maccasoft.propeller.spin.Spin2Context;
import com.maccasoft.propeller.spin.Spin2InstructionObject;
import com.maccasoft.propeller.spin.Spin2PAsmExpression;

class StdTest {

    @Test
    void testRegisterRegister() {
        Std_E factory = new Std_E(P2Asm.encode(0b0000111, 0b000, 0b000000000, 0b000000000), false);
        Spin2InstructionObject object = factory.createObject(new Spin2Context(), Arrays.asList(new Spin2PAsmExpression[] {
            new Spin2PAsmExpression(null, new NumberLiteral(1), null),
            new Spin2PAsmExpression(null, new NumberLiteral(2), null),
        }), null);
        Assertions.assertEquals(
            Spin2InstructionObject.decodeToString(Spin2InstructionObject.encode(0b0000111, false, false, false, 1, 2)),
            Spin2InstructionObject.decodeToString(object.getBytes()));
    }

    @Test
    void testRegisterLiteral() {
        Std_E factory = new Std_E(P2Asm.encode(0b0000111, 0b000, 0b000000000, 0b000000000), false);
        Spin2InstructionObject object = factory.createObject(new Spin2Context(), Arrays.asList(new Spin2PAsmExpression[] {
            new Spin2PAsmExpression(null, new NumberLiteral(1), null),
            new Spin2PAsmExpression("#", new NumberLiteral(2), null),
        }), null);
        Assertions.assertEquals(
            Spin2InstructionObject.decodeToString(Spin2InstructionObject.encode(0b0000111, false, false, true, 1, 2)),
            Spin2InstructionObject.decodeToString(object.getBytes()));
    }

    @Test
    void testLiteralRegister() {
        Std_E factory = new Std_E(P2Asm.encode(0b0000111, 0b000, 0b000000000, 0b000000000), false);
        try {
            factory.createObject(new Spin2Context(), Arrays.asList(new Spin2PAsmExpression[] {
                new Spin2PAsmExpression("#", new NumberLiteral(1), null),
                new Spin2PAsmExpression(null, new NumberLiteral(2), null),
            }), null);
            Assertions.fail("Expected exception");
        } catch (Exception e) {

        }
    }

    @Test
    void testSOptional() {
        Std_E factory = new Std_E(P2Asm.encode(0b0000111, 0b000, 0b000000000, 0b000000000), true);
        Spin2InstructionObject object = factory.createObject(new Spin2Context(), Arrays.asList(new Spin2PAsmExpression[] {
            new Spin2PAsmExpression(null, new NumberLiteral(1), null),
        }), null);
        Assertions.assertEquals(
            Spin2InstructionObject.decodeToString(Spin2InstructionObject.encode(0b0000111, false, false, false, 1, 1)),
            Spin2InstructionObject.decodeToString(object.getBytes()));
    }

    @Test
    void testRequireS() {
        Std_E factory = new Std_E(P2Asm.encode(0b0000111, 0b000, 0b000000000, 0b000000000), false);
        try {
            factory.createObject(new Spin2Context(), Arrays.asList(new Spin2PAsmExpression[] {
                new Spin2PAsmExpression(null, new NumberLiteral(1), null),
            }), null);
            Assertions.fail("Expected exception");
        } catch (Exception e) {

        }
    }

    @Test
    void testLiteralD_SOptional() {
        Std_E factory = new Std_E(P2Asm.encode(0b0000111, 0b000, 0b000000000, 0b000000000), true);
        try {
            factory.createObject(new Spin2Context(), Arrays.asList(new Spin2PAsmExpression[] {
                new Spin2PAsmExpression("#", new NumberLiteral(1), null),
            }), null);
            Assertions.fail("Expected exception");
        } catch (Exception e) {

        }
    }

}
