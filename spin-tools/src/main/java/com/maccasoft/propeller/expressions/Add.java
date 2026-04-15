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

import com.maccasoft.propeller.CompilerException;

public class Add extends BinaryOperator {

    public Add(Expression augend, Expression addend) {
        super(augend, addend);
    }

    public Expression getAugend() {
        return term1;
    }

    public Expression getAddend() {
        return term2;
    }

    @Override
    protected Number internalGetNumber(Number term1, Number term2) {
        if ((term1 instanceof Long) && (term2 instanceof Long)) {
            return term1.longValue() + term2.longValue();
        }
        return term1.doubleValue() + term2.doubleValue();
    }

    @Override
    public int[] getStringValues() {
        int i;
        int value = 0;
        List<Integer> list = new ArrayList<>();
        CompilerException errors = new CompilerException();

        try {
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
        } catch (CompilerException e) {
            errors.addMessage(e);
        } catch (Exception e) {
            errors.addMessage((new CompilerException(e, term1.getData())));
        }

        try {
            if (term2.isString()) {
                int[] b = term2.getStringValues();

                i = 0;
                if (i < b.length) {
                    list.add(Integer.valueOf(value + (b[i++])));
                }
                while (i < b.length) {
                    list.add(Integer.valueOf(b[i++]));
                }
            }
            else {
                list.add(Integer.valueOf(value + term2.getNumber().intValue()));
            }
        } catch (CompilerException e) {
            errors.addMessage(e);
        } catch (Exception e) {
            errors.addMessage((new CompilerException(e, term1.getData())));
        }

        if (errors.hasChilds()) {
            throw errors;
        }

        int[] r = new int[list.size()];
        for (i = 0; i < r.length; i++) {
            r[i] = list.get(i);
        }

        return r;
    }

    @Override
    public String getLexeme() {
        return "+";
    }

}
