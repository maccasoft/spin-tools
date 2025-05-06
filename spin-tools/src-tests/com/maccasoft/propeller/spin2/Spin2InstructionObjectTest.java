/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.expressions.Context;
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

}
