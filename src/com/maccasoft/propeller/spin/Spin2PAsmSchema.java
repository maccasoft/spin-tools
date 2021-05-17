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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.BitField;

public abstract class Spin2PAsmSchema {

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

    public static Set<String> E_WC_WZ_WCZ = new HashSet<String>(Arrays.asList(new String[] {
        "wc", "wz", "wcz",
        "wc,wz",
        "wz,wc"
    }));

    static Set<String> E_WC_WZ = new HashSet<String>(Arrays.asList(new String[] {
        "wc", "wz"
    }));

    static Set<String> E_ANDC_ANDZ = new HashSet<String>(Arrays.asList(new String[] {
        "andc", "andz"
    }));

    static Set<String> E_ORC_ORZ = new HashSet<String>(Arrays.asList(new String[] {
        "orc", "orz"
    }));

    static Set<String> E_XORC_XORZ = new HashSet<String>(Arrays.asList(new String[] {
        "xorc", "xorz"
    }));

    public static Set<String> E_WCZ = new HashSet<String>(Arrays.asList(new String[] {
        "wcz",
    }));

    public static Spin2PAsmSchema D_S_WC_WZ_WCZ = new Spin2PAsmSchema() {

        @Override
        public boolean check(List<Spin2PAsmExpression> arguments, String effect) {
            return arguments.size() == 2 && arguments.get(0).prefix == null && (effect == null || E_WC_WZ_WCZ.contains(effect.toLowerCase()));
        }

    };

    public static Spin2PAsmSchema D_WC_WZ_WCZ = new Spin2PAsmSchema() {

        @Override
        public boolean check(List<Spin2PAsmExpression> arguments, String effect) {
            return arguments.size() == 1 && arguments.get(0).prefix == null && (effect == null || E_WC_WZ_WCZ.contains(effect.toLowerCase()));
        }

    };

    public static Spin2PAsmSchema D_S = new Spin2PAsmSchema() {

        @Override
        public boolean check(List<Spin2PAsmExpression> arguments, String effect) {
            return arguments.size() == 2 && arguments.get(0).prefix == null && effect == null;
        }

    };

    public static Spin2PAsmSchema D = new Spin2PAsmSchema() {

        @Override
        public boolean check(List<Spin2PAsmExpression> arguments, String effect) {
            return arguments.size() == 1 && arguments.get(0).prefix == null && effect == null;
        }

    };

    public static Spin2PAsmSchema WC_WZ_WCZ = new Spin2PAsmSchema() {

        @Override
        public boolean check(List<Spin2PAsmExpression> arguments, String effect) {
            return arguments.size() == 0 && (effect == null || E_WC_WZ_WCZ.contains(effect.toLowerCase()));
        }

    };

    public abstract boolean check(List<Spin2PAsmExpression> arguments, String effect);
}
