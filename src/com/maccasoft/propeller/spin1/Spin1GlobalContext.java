/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1;

import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.NumberLiteral;

public class Spin1GlobalContext extends Spin1Context {

    public Spin1GlobalContext() {
        addBuiltinSymbol("PAR", new NumberLiteral("$1F0"));
        addBuiltinSymbol("CNT", new NumberLiteral("$1F1"));
        addBuiltinSymbol("INA", new NumberLiteral("$1F2"));
        addBuiltinSymbol("INB", new NumberLiteral("$1F3"));
        addBuiltinSymbol("OUTA", new NumberLiteral("$1F4"));
        addBuiltinSymbol("OUTB", new NumberLiteral("$1F5"));
        addBuiltinSymbol("DIRA", new NumberLiteral("$1F6"));
        addBuiltinSymbol("DIRB", new NumberLiteral("$1F7"));
        addBuiltinSymbol("CTRA", new NumberLiteral("$1F8"));
        addBuiltinSymbol("CTRB", new NumberLiteral("$1F9"));
        addBuiltinSymbol("FRQA", new NumberLiteral("$1FA"));
        addBuiltinSymbol("FRQB", new NumberLiteral("$1FB"));
        addBuiltinSymbol("PHSA", new NumberLiteral("$1FC"));
        addBuiltinSymbol("PHSB", new NumberLiteral("$1FD"));
        addBuiltinSymbol("VCFG", new NumberLiteral("$1FE"));
        addBuiltinSymbol("VSCL", new NumberLiteral("$1FF"));

        addBuiltinSymbol("TRUE", new NumberLiteral(-1));
        addBuiltinSymbol("FALSE", new NumberLiteral(0));
        addBuiltinSymbol("POSX", new NumberLiteral(2147483647));
        addBuiltinSymbol("NEGX", new NumberLiteral(-2147483648));
        addBuiltinSymbol("PI", new NumberLiteral(3.141593));

        addBuiltinSymbol("RCFAST", new NumberLiteral("%00000000001"));
        addBuiltinSymbol("RCSLOW", new NumberLiteral("%00000000010"));
        addBuiltinSymbol("XINPUT", new NumberLiteral("%00000000100"));

        addBuiltinSymbol("XTAL1", new NumberLiteral("%00000001000"));
        addBuiltinSymbol("XTAL2", new NumberLiteral("%00000010000"));
        addBuiltinSymbol("XTAL3", new NumberLiteral("%00000100000"));

        addBuiltinSymbol("PLL1X", new NumberLiteral("%00001000000"));
        addBuiltinSymbol("PLL2X", new NumberLiteral("%00010000000"));
        addBuiltinSymbol("PLL4X", new NumberLiteral("%00100000000"));
        addBuiltinSymbol("PLL8X", new NumberLiteral("%01000000000"));
        addBuiltinSymbol("PLL16X", new NumberLiteral("%10000000000"));
    }

    void addBuiltinSymbol(String name, int value) {
        symbols.put(name.toLowerCase(), new NumberLiteral(value));
        symbols.put(name.toUpperCase(), new NumberLiteral(value));
    }

    void addBuiltinSymbol(String name, Expression value) {
        symbols.put(name.toLowerCase(), value);
        symbols.put(name.toUpperCase(), value);
    }

}
