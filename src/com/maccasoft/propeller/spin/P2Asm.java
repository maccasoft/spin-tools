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

import org.apache.commons.lang3.BitField;

public class P2Asm {

    public static final BitField e = new BitField(0b11110000000000000000000000000000); // condition
    public static final BitField o = new BitField(0b00001111111000000000000000000000); // instruction
    public static final BitField czi = new BitField(0b00000000000111000000000000000000); // czi
    public static final BitField d = new BitField(0b00000000000000111111111000000000); // destination
    public static final BitField s = new BitField(0b00000000000000000000000111111111); // source

    public static int encode(int IIIIIII, int CZI, int DDDDDDDD, int SSSSSSSS) {
        int value = e.setValue(0, 0b1111);
        value = o.setValue(value, IIIIIII);
        value = czi.setValue(value, CZI);
        value = d.setValue(value, DDDDDDDD);
        return s.setValue(value, SSSSSSSS);
    }
}
