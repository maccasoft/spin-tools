/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.expressions;

import java.util.ArrayList;
import java.util.List;

public class Subtract extends BinaryOperator {

    public Subtract(Expression minuend, Expression subtrahend) {
        super(minuend, subtrahend);
    }

    public Expression getMinuend() {
        return term1;
    }

    public Expression getSubtrahend() {
        return term2;
    }

    @Override
    public Number getNumber() {
        if ((term1.getNumber() instanceof Long) && (term2.getNumber() instanceof Long)) {
            return term1.getNumber().longValue() - term2.getNumber().longValue();
        }
        return term1.getNumber().doubleValue() - term2.getNumber().doubleValue();
    }

    @Override
    public int[] getStringValues() {
        int i;
        int value = 0;
        List<Integer> list = new ArrayList<>();

        if (term1.isString()) {
            int[] b = term1.getStringValues();
            i = 0;
            while (i < b.length - 1) {
                list.add(Integer.valueOf(b[i++]));
            }
            if (i < b.length) {
                value = b[i++];
            }
        }
        else {
            value = term1.getNumber().intValue();
        }

        if (term2.isString()) {
            int[] b = term2.getStringValues();

            i = 0;
            if (i < b.length) {
                list.add(Integer.valueOf(value - (b[i++])));
            }
            while (i < b.length) {
                list.add(Integer.valueOf(b[i++]));
            }
        }
        else {
            list.add(Integer.valueOf(value - term2.getNumber().intValue()));
        }

        int[] r = new int[list.size()];
        for (i = 0; i < r.length; i++) {
            r[i] = list.get(i);
        }

        return r;
    }

    @Override
    public String getLexeme() {
        return "-";
    }

}
