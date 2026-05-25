/*
 * Copyright (c) 26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.spin1.instructions.Empty.Empty_;

class Spin1InstructionObjectTest {

    @Test
    void testDstRange() {
        Spin1InstructionObject subject = new Empty_(new Context());
        Spin1PAsmExpression src = new Spin1PAsmExpression(null, new NumberLiteral(0), null);
        Assertions.assertThrows(CompilerException.class, () -> {
            subject.encodeInstructionParameters(null, new Spin1PAsmExpression(null, new NumberLiteral(-1), null), src, null);
        });
        Assertions.assertThrows(CompilerException.class, () -> {
            subject.encodeInstructionParameters(null, new Spin1PAsmExpression(null, new NumberLiteral(512), null), src, null);
        });
    }

    @Test
    void testSrcRange() {
        Spin1InstructionObject subject = new Empty_(new Context());
        Spin1PAsmExpression dst = new Spin1PAsmExpression(null, new NumberLiteral(0), null);
        Assertions.assertThrows(CompilerException.class, () -> {
            subject.encodeInstructionParameters(null, dst, new Spin1PAsmExpression(null, new NumberLiteral(-1), null), null);
        });
        Assertions.assertThrows(CompilerException.class, () -> {
            subject.encodeInstructionParameters(null, dst, new Spin1PAsmExpression(null, new NumberLiteral(512), null), null);
        });
        Assertions.assertThrows(CompilerException.class, () -> {
            subject.encodeInstructionParameters(null, dst, new Spin1PAsmExpression("#", new NumberLiteral(-1), null), null);
        });
        Assertions.assertThrows(CompilerException.class, () -> {
            subject.encodeInstructionParameters(null, dst, new Spin1PAsmExpression("#", new NumberLiteral(512), null), null);
        });
    }

}
