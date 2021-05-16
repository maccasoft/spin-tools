/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin;

import com.maccasoft.propeller.spin.instructions.Addct1;
import com.maccasoft.propeller.spin.instructions.AsmClk;
import com.maccasoft.propeller.spin.instructions.Augd;
import com.maccasoft.propeller.spin.instructions.Augs;
import com.maccasoft.propeller.spin.instructions.Drvnot;
import com.maccasoft.propeller.spin.instructions.Getct;
import com.maccasoft.propeller.spin.instructions.Hubset;
import com.maccasoft.propeller.spin.instructions.Jmp;
import com.maccasoft.propeller.spin.instructions.Nop;
import com.maccasoft.propeller.spin.instructions.Res;
import com.maccasoft.propeller.spin.instructions.Waitct1;
import com.maccasoft.propeller.spin.instructions.Waitx;

public class Spin2GlobalContext extends Spin2Context {

    public Spin2GlobalContext() {
        // Built-in macros
        addSymbol("asmclk", new AsmClk());
        addSymbol("res", new Res());

        // Branch
        addSymbol("jmp", new Jmp());

        // Hub control
        addSymbol("hubset", new Hubset());

        // Events configuration
        addSymbol("addct1", new Addct1());

        // Events wait
        addSymbol("waitct1", new Waitct1());

        // Pins
        addSymbol("drvnot", new Drvnot());

        // Misc.
        addSymbol("nop", new Nop());
        addSymbol("augs", new Augs());
        addSymbol("augd", new Augd());
        addSymbol("getct", new Getct());
        addSymbol("waitx", new Waitx());
    }

}
