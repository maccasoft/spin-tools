/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.spin2.instructions.Empty.Empty_;
import com.maccasoft.propeller.spin2.instructions.Nop;

class Spin2InstructionObjectTest {

    @Test
    void testEncodeEffect() {
        Spin2InstructionObject obj = new Nop().createObject(new Context(), null, Collections.emptyList(), null);
        Assertions.assertEquals(0b01, obj.encodeEffect("wz"));
        Assertions.assertEquals(0b10, obj.encodeEffect("wc"));
        Assertions.assertEquals(0b11, obj.encodeEffect("wcz"));
        Assertions.assertEquals(0b01, obj.encodeEffect("Wz"));
        Assertions.assertEquals(0b10, obj.encodeEffect("Wc"));
        Assertions.assertEquals(0b11, obj.encodeEffect("Wcz"));
    }

    @Test
    void testEncodeBooleanEffect() {
        Spin2InstructionObject obj = new Nop().createObject(new Context(), null, Collections.emptyList(), null);

        Assertions.assertEquals(0b01, obj.encodeEffect("andz"));
        Assertions.assertEquals(0b10, obj.encodeEffect("andc"));
        Assertions.assertEquals(0b01, obj.encodeEffect("orz"));
        Assertions.assertEquals(0b10, obj.encodeEffect("orc"));
        Assertions.assertEquals(0b01, obj.encodeEffect("xorz"));
        Assertions.assertEquals(0b10, obj.encodeEffect("xorc"));

        Assertions.assertEquals(0b01, obj.encodeEffect("Andz"));
        Assertions.assertEquals(0b10, obj.encodeEffect("Andc"));
        Assertions.assertEquals(0b01, obj.encodeEffect("Orz"));
        Assertions.assertEquals(0b10, obj.encodeEffect("Orc"));
        Assertions.assertEquals(0b01, obj.encodeEffect("Xorz"));
        Assertions.assertEquals(0b10, obj.encodeEffect("Xorc"));
    }

    @Test
    void testEncodeEffectSpin1Compatible() {
        Spin2InstructionObject obj = new Nop().createObject(new Context(), null, Collections.emptyList(), null);
        Assertions.assertEquals(0b11, obj.encodeEffect("wc,wz"));
        Assertions.assertEquals(0b11, obj.encodeEffect("Wc,Wz"));
    }

    @Test
    void testDstRange() {
        Spin2InstructionObject subject = new Empty_(new Context());
        Spin2PAsmExpression src = new Spin2PAsmExpression(null, new NumberLiteral(0), null);
        Assertions.assertThrows(CompilerException.class, () -> {
            subject.encodeInstructionParameters(null, new Spin2PAsmExpression(null, new NumberLiteral(-1), null), src, null);
        });
        Assertions.assertThrows(CompilerException.class, () -> {
            subject.encodeInstructionParameters(null, new Spin2PAsmExpression(null, new NumberLiteral(512), null), src, null);
        });
    }

    @Test
    void testSrcRange() {
        Spin2InstructionObject subject = new Empty_(new Context());
        Spin2PAsmExpression dst = new Spin2PAsmExpression(null, new NumberLiteral(0), null);
        Assertions.assertThrows(CompilerException.class, () -> {
            subject.encodeInstructionParameters(null, dst, new Spin2PAsmExpression(null, new NumberLiteral(-1), null), null);
        });
        Assertions.assertThrows(CompilerException.class, () -> {
            subject.encodeInstructionParameters(null, dst, new Spin2PAsmExpression(null, new NumberLiteral(512), null), null);
        });
        Assertions.assertThrows(CompilerException.class, () -> {
            subject.encodeInstructionParameters(null, dst, new Spin2PAsmExpression("#", new NumberLiteral(-1), null), null);
        });
        Assertions.assertThrows(CompilerException.class, () -> {
            subject.encodeInstructionParameters(null, dst, new Spin2PAsmExpression("#", new NumberLiteral(512), null), null);
        });
    }

    @Test
    void testAltDstRange() {
        Spin2InstructionObject subject = new Empty_(new Context());
        Spin2PAsmExpression src = new Spin2PAsmExpression(null, new NumberLiteral(0), null);
        Assertions.assertThrows(CompilerException.class, () -> {
            subject.encodeInstructionParameters(null, new Spin2PAsmExpression(null, new NumberLiteral(-1), null), src);
        });
        Assertions.assertThrows(CompilerException.class, () -> {
            subject.encodeInstructionParameters(null, new Spin2PAsmExpression(null, new NumberLiteral(512), null), src);
        });
    }

    @Test
    void testAltSrcRange() {
        Spin2InstructionObject subject = new Empty_(new Context());
        Spin2PAsmExpression dst = new Spin2PAsmExpression(null, new NumberLiteral(0), null);
        Assertions.assertThrows(CompilerException.class, () -> {
            subject.encodeInstructionParameters(null, dst, new Spin2PAsmExpression(null, new NumberLiteral(-1), null));
        });
        Assertions.assertThrows(CompilerException.class, () -> {
            subject.encodeInstructionParameters(null, dst, new Spin2PAsmExpression(null, new NumberLiteral(512), null));
        });
        Assertions.assertThrows(CompilerException.class, () -> {
            subject.encodeInstructionParameters(null, dst, new Spin2PAsmExpression("#", new NumberLiteral(-1), null));
        });
        Assertions.assertThrows(CompilerException.class, () -> {
            subject.encodeInstructionParameters(null, dst, new Spin2PAsmExpression("#", new NumberLiteral(512), null));
        });
    }

}
