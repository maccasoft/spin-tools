/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2.instructions;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.spin2.Spin2PAsmExpression;

class MovTest {

    @Test
    void testOk() {
        new Mov().createObject(new Context(), null, Arrays.asList(new Spin2PAsmExpression[] {
            new Spin2PAsmExpression(null, new NumberLiteral(1), null),
            new Spin2PAsmExpression(null, new NumberLiteral(2), null),
        }), null);
        new Mov().createObject(new Context(), null, Arrays.asList(new Spin2PAsmExpression[] {
            new Spin2PAsmExpression(null, new NumberLiteral(1), null),
            new Spin2PAsmExpression("#", new NumberLiteral(2), null),
        }), null);
        new Mov().createObject(new Context(), null, Arrays.asList(new Spin2PAsmExpression[] {
            new Spin2PAsmExpression(null, new NumberLiteral(1), null),
            new Spin2PAsmExpression("##", new NumberLiteral(2), null),
        }), null);
        new Mov().createObject(new Context(), null, Arrays.asList(new Spin2PAsmExpression[] {
            new Spin2PAsmExpression(null, new NumberLiteral(1), null),
            new Spin2PAsmExpression("##", new NumberLiteral(2), null),
        }), "wc");
        new Mov().createObject(new Context(), null, Arrays.asList(new Spin2PAsmExpression[] {
            new Spin2PAsmExpression(null, new NumberLiteral(1), null),
            new Spin2PAsmExpression("##", new NumberLiteral(2), null),
        }), "wz");
        new Mov().createObject(new Context(), null, Arrays.asList(new Spin2PAsmExpression[] {
            new Spin2PAsmExpression(null, new NumberLiteral(1), null),
            new Spin2PAsmExpression("##", new NumberLiteral(2), null),
        }), "wcz");
    }

    @Test
    void testErrors() {
        try {
            new Mov().createObject(new Context(), null, Collections.emptyList(), null);
            fail("Must throw an exception");
        } catch (RuntimeException e) {

        }
        try {
            new Mov().createObject(new Context(), null, Arrays.asList(new Spin2PAsmExpression[] {
                new Spin2PAsmExpression("#", new NumberLiteral(1), null)
            }), null);
            fail("Must throw an exception");
        } catch (RuntimeException e) {

        }
        try {
            new Mov().createObject(new Context(), null, Arrays.asList(new Spin2PAsmExpression[] {
                new Spin2PAsmExpression("#", new NumberLiteral(1), null),
                new Spin2PAsmExpression("#", new NumberLiteral(2), null),
            }), null);
            fail("Must throw an exception");
        } catch (RuntimeException e) {

        }
        try {
            new Mov().createObject(new Context(), null, Arrays.asList(new Spin2PAsmExpression[] {
                new Spin2PAsmExpression(null, new NumberLiteral(1), null),
                new Spin2PAsmExpression("#", new NumberLiteral(2), null),
            }), "wca");
            fail("Must throw an exception");
        } catch (RuntimeException e) {

        }
    }

}
