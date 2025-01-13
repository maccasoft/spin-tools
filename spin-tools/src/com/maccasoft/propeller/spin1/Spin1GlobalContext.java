/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1;

import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.expressions.Register;

public class Spin1GlobalContext extends Context {

    public Spin1GlobalContext() {
        this(false);
    }

    public Spin1GlobalContext(boolean caseSensitive) {
        super(caseSensitive);

        addBuiltinSymbol("SPR", new Register(0x000));
        addBuiltinSymbol("PAR", new Register(0x1F0));
        addBuiltinSymbol("CNT", new Register(0x1F1));
        addBuiltinSymbol("INA", new Register(0x1F2));
        addBuiltinSymbol("INB", new Register(0x1F3));
        addBuiltinSymbol("OUTA", new Register(0x1F4));
        addBuiltinSymbol("OUTB", new Register(0x1F5));
        addBuiltinSymbol("DIRA", new Register(0x1F6));
        addBuiltinSymbol("DIRB", new Register(0x1F7));
        addBuiltinSymbol("CTRA", new Register(0x1F8));
        addBuiltinSymbol("CTRB", new Register(0x1F9));
        addBuiltinSymbol("FRQA", new Register(0x1FA));
        addBuiltinSymbol("FRQB", new Register(0x1FB));
        addBuiltinSymbol("PHSA", new Register(0x1FC));
        addBuiltinSymbol("PHSB", new Register(0x1FD));
        addBuiltinSymbol("VCFG", new Register(0x1FE));
        addBuiltinSymbol("VSCL", new Register(0x1FF));

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

}
