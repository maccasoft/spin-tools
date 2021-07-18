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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Spin1PAsmSchema {

    public static Set<String> E_ALL = new HashSet<String>(Arrays.asList(new String[] {
        "wc",
        "wz",
        "wr",
        "nr",
    }));

    public static Set<String> E_WC_WZ = new HashSet<String>(Arrays.asList(new String[] {
        "wc",
        "wz",
    }));

    /**
     * OPCODE
     */
    public static Spin1PAsmSchema NONE = new Spin1PAsmSchema() {

        @Override
        public boolean check(List<Spin1PAsmExpression> arguments, String effect) {
            return arguments.size() == 0 && effect == null;
        }

    };

    /**
     * OPCODE  D,{#}S   {WC/WZ/NR/WR}
     */
    public static Spin1PAsmSchema D_S = new Spin1PAsmSchema() {

        @Override
        public boolean check(List<Spin1PAsmExpression> arguments, String effect) {
            if (arguments.size() != 2) {
                throw new RuntimeException("error: expected 2 operands, found " + arguments.size());
            }
            return arguments.size() == 2 && arguments.get(0).prefix == null;
        }

    };

    /**
     * OPCODE  D,{#}S   {WC/WZ}
     */
    public static Spin1PAsmSchema D_S_WC_WZ = new Spin1PAsmSchema() {

        @Override
        public boolean check(List<Spin1PAsmExpression> arguments, String effect) {
            if (arguments.size() != 2) {
                throw new RuntimeException("error: expected 2 operands, found " + arguments.size());
            }
            return arguments.size() == 2 && arguments.get(0).prefix == null;
        }

    };

    /**
     * OPCODE  D
     */
    public static Spin1PAsmSchema D = new Spin1PAsmSchema() {

        @Override
        public boolean check(List<Spin1PAsmExpression> arguments, String effect) {
            if (arguments.size() != 1) {
                throw new RuntimeException("error: expected 2 operands, found " + arguments.size());
            }
            return arguments.size() == 1 && arguments.get(0).prefix == null && effect == null;
        }

    };

    /**
     * OPCODE  D        {WC/WZ}
     */
    public static Spin1PAsmSchema D_WC_WZ = new Spin1PAsmSchema() {

        @Override
        public boolean check(List<Spin1PAsmExpression> arguments, String effect) {
            if (arguments.size() != 1) {
                throw new RuntimeException("error: expected 2 operands, found " + arguments.size());
            }
            return arguments.size() == 1 && arguments.get(0).prefix == null;
        }

    };

    /**
     * OPCODE  {#}S     {WC/WZ/NR/WR}
     */
    public static Spin1PAsmSchema S = new Spin1PAsmSchema() {

        @Override
        public boolean check(List<Spin1PAsmExpression> arguments, String effect) {
            if (arguments.size() != 1) {
                throw new RuntimeException("error: expected 2 operands, found " + arguments.size());
            }
            return arguments.size() == 1;
        }

    };

    public abstract boolean check(List<Spin1PAsmExpression> arguments, String effect);
}
