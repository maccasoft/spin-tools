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

import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.spin.Spin2Context;
import com.maccasoft.propeller.spin.Spin2PAsmExpression;

class GetbyteTest {

    @Test
    void testOk() {
        new Getbyte().createObject(new Spin2Context(), null, Arrays.asList(new Spin2PAsmExpression[] {
            new Spin2PAsmExpression(null, new NumberLiteral(1), null)
        }), null);
        new Getbyte().createObject(new Spin2Context(), null, Arrays.asList(new Spin2PAsmExpression[] {
            new Spin2PAsmExpression(null, new NumberLiteral(1), null),
            new Spin2PAsmExpression(null, new NumberLiteral(2), null),
            new Spin2PAsmExpression("#", new NumberLiteral(3), null)
        }), null);
        new Getbyte().createObject(new Spin2Context(), null, Arrays.asList(new Spin2PAsmExpression[] {
            new Spin2PAsmExpression(null, new NumberLiteral(1), null),
            new Spin2PAsmExpression("#", new NumberLiteral(2), null),
            new Spin2PAsmExpression("#", new NumberLiteral(3), null)
        }), null);
        new Getbyte().createObject(new Spin2Context(), null, Arrays.asList(new Spin2PAsmExpression[] {
            new Spin2PAsmExpression(null, new NumberLiteral(1), null),
            new Spin2PAsmExpression("##", new NumberLiteral(2), null),
            new Spin2PAsmExpression("#", new NumberLiteral(3), null)
        }), null);
    }

    @Test
    void testErrors() {
        try {
            new Getbyte().createObject(new Spin2Context(), null, Collections.emptyList(), null);
            fail("Must throw an exception");
        } catch (RuntimeException e) {

        }
        try {
            new Getbyte().createObject(new Spin2Context(), null, Arrays.asList(new Spin2PAsmExpression[] {
                new Spin2PAsmExpression("#", new NumberLiteral(1), null)
            }), null);
            fail("Must throw an exception");
        } catch (RuntimeException e) {

        }
        try {
            new Getbyte().createObject(new Spin2Context(), null, Arrays.asList(new Spin2PAsmExpression[] {
                new Spin2PAsmExpression(null, new NumberLiteral(1), null),
                new Spin2PAsmExpression("#", new NumberLiteral(2), null),
                new Spin2PAsmExpression(null, new NumberLiteral(3), null)
            }), null);
            fail("Must throw an exception");
        } catch (RuntimeException e) {

        }
    }

}
