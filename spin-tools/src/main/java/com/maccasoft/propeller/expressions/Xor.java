/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
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

public class Xor extends BinaryOperator {

    public Xor(Expression term1, Expression term2) {
        super(term1, term2);
    }

    @Override
    protected Number internalGetNumber(Number term1, Number term2) {
        return term1.longValue() ^ term2.longValue();
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
                list.add(Integer.valueOf(value ^ (b[i++])));
            }
            while (i < b.length) {
                list.add(Integer.valueOf(b[i++]));
            }
        }
        else {
            list.add(Integer.valueOf(value ^ term2.getNumber().intValue()));
        }

        int[] r = new int[list.size()];
        for (i = 0; i < r.length; i++) {
            r[i] = list.get(i);
        }

        return r;
    }

    @Override
    public String getLexeme() {
        return "^";
    }

}
