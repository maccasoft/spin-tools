/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Spin2PAsmSchema {

    public static Set<String> E_WC_WZ_WCZ = new HashSet<String>(Arrays.asList(new String[] {
        "wc", "wz", "wcz",
        "wc,wz",
        "wz,wc"
    }));

    public static Set<String> E_WC_WZ = new HashSet<String>(Arrays.asList(new String[] {
        "wc", "wz"
    }));

    public static Set<String> E_WCZ = new HashSet<String>(Arrays.asList(new String[] {
        "wcz",
    }));

    public static Set<String> E_TEST_OP = new HashSet<String>(Arrays.asList(new String[] {
        "wc", "wz",
        "andc", "andz",
        "orc", "orz",
        "xorc", "xorz"
    }));

    /**
     * OPCODE
     */
    public static Spin2PAsmSchema NONE = new Spin2PAsmSchema() {

        @Override
        public boolean check(List<Spin2PAsmExpression> arguments, String effect) {
            return arguments.size() == 0 && effect == null;
        }

    };

    /**
     * OPCODE  D,{#}S   {WC/WZ/WCZ}
     */
    public static Spin2PAsmSchema D_S_WC_WZ_WCZ = new Spin2PAsmSchema() {

        @Override
        public boolean check(List<Spin2PAsmExpression> arguments, String effect) {
            return arguments.size() == 2 && arguments.get(0).prefix == null && (effect == null || E_WC_WZ_WCZ.contains(effect.toLowerCase()));
        }

    };

    /**
     * OPCODE  D,{#}S   {WCZ}
     */
    public static Spin2PAsmSchema D_S_WCZ = new Spin2PAsmSchema() {

        @Override
        public boolean check(List<Spin2PAsmExpression> arguments, String effect) {
            return arguments.size() == 2 && arguments.get(0).prefix == null && (effect == null || E_WCZ.contains(effect.toLowerCase()));
        }

    };

    /**
     * OPCODE  D,{#}S    {WC}
     */
    public static Spin2PAsmSchema D_S_WC = new Spin2PAsmSchema() {

        @Override
        public boolean check(List<Spin2PAsmExpression> arguments, String effect) {
            return arguments.size() == 2 && arguments.get(0).prefix == null && (effect == null || "wc".equalsIgnoreCase(effect));
        }

    };

    /**
     * OPCODE  D,{#}S    {WZ}
     */
    public static Spin2PAsmSchema D_S_WZ = new Spin2PAsmSchema() {

        @Override
        public boolean check(List<Spin2PAsmExpression> arguments, String effect) {
            return arguments.size() == 2 && arguments.get(0).prefix == null && (effect == null || "wz".equalsIgnoreCase(effect));
        }

    };

    /**
     * OPCODE  D        {WC/WZ/WCZ}
     */
    public static Spin2PAsmSchema D_WC_WZ_WCZ = new Spin2PAsmSchema() {

        @Override
        public boolean check(List<Spin2PAsmExpression> arguments, String effect) {
            return arguments.size() == 1 && arguments.get(0).prefix == null && (effect == null || E_WC_WZ_WCZ.contains(effect.toLowerCase()));
        }

    };

    /**
     * OPCODE  D               {WC}
     */
    public static Spin2PAsmSchema D_WC = new Spin2PAsmSchema() {

        @Override
        public boolean check(List<Spin2PAsmExpression> arguments, String effect) {
            return arguments.size() == 1 && arguments.get(0).prefix == null && (effect == null || "wc".equalsIgnoreCase(effect));
        }

    };

    /**
     * OPCODE  D               {WZ}
     */
    public static Spin2PAsmSchema D_WZ = new Spin2PAsmSchema() {

        @Override
        public boolean check(List<Spin2PAsmExpression> arguments, String effect) {
            return arguments.size() == 1 && arguments.get(0).prefix == null && (effect == null || "wz".equalsIgnoreCase(effect));
        }

    };

    /**
     * OPCODE  {#}D            {WC}
     */
    public static Spin2PAsmSchema LD_WC = new Spin2PAsmSchema() {

        @Override
        public boolean check(List<Spin2PAsmExpression> arguments, String effect) {
            return arguments.size() == 1 && (effect == null || "wc".equalsIgnoreCase(effect));
        }

    };

    /**
     * OPCODE           {WC/WZ/WCZ}
     */
    public static Spin2PAsmSchema WC_WZ_WCZ = new Spin2PAsmSchema() {

        @Override
        public boolean check(List<Spin2PAsmExpression> arguments, String effect) {
            return arguments.size() == 0 && (effect == null || E_WC_WZ_WCZ.contains(effect.toLowerCase()));
        }

    };

    /**
     * OPCODE  D,{#}S
     */
    public static Spin2PAsmSchema D_S = new Spin2PAsmSchema() {

        @Override
        public boolean check(List<Spin2PAsmExpression> arguments, String effect) {
            return arguments.size() == 2 && arguments.get(0).prefix == null && effect == null;
        }

    };

    /**
     * OPCODE  {#}S
     */
    public static Spin2PAsmSchema S = new Spin2PAsmSchema() {

        @Override
        public boolean check(List<Spin2PAsmExpression> arguments, String effect) {
            return arguments.size() == 1 && effect == null;
        }

    };

    /**
     * OPCODE  D
     */
    public static Spin2PAsmSchema D = new Spin2PAsmSchema() {

        @Override
        public boolean check(List<Spin2PAsmExpression> arguments, String effect) {
            return arguments.size() == 1 && arguments.get(0).prefix == null && effect == null;
        }

    };

    /**
     * OPCODE  {#}D
     */
    public static Spin2PAsmSchema LD = new Spin2PAsmSchema() {

        @Override
        public boolean check(List<Spin2PAsmExpression> arguments, String effect) {
            return arguments.size() == 1 && effect == null;
        }

    };

    /**
     * OPCODE  {#}D           {WCZ}
     */
    public static Spin2PAsmSchema LD_WCZ = new Spin2PAsmSchema() {

        @Override
        public boolean check(List<Spin2PAsmExpression> arguments, String effect) {
            return arguments.size() == 1 && (effect == null || "wcz".equalsIgnoreCase(effect));
        }

    };

    /**
     * OPCODE  D,{#}S,#N
     */
    public static Spin2PAsmSchema D_S_N = new Spin2PAsmSchema() {

        @Override
        public boolean check(List<Spin2PAsmExpression> arguments, String effect) {
            return arguments.size() == 3 && arguments.get(0).prefix == null && "#".equals(arguments.get(2).prefix) && effect == null;
        }

    };

    /**
     * OPCODE  {#}D,{#}S       {WC}
     */
    public static Spin2PAsmSchema LD_IS_WC = new Spin2PAsmSchema() {

        @Override
        public boolean check(List<Spin2PAsmExpression> arguments, String effect) {
            return arguments.size() == 2 && (effect == null || "wc".equalsIgnoreCase(effect));
        }

    };

    /**
     * OPCODE  {#}D,{#}S
     */
    public static Spin2PAsmSchema LD_S = new Spin2PAsmSchema() {

        @Override
        public boolean check(List<Spin2PAsmExpression> arguments, String effect) {
            return arguments.size() == 2 && effect == null;
        }

    };

    /**
     * OPCODE  D,{#}S         WC/WZ
     * OPCODE  D,{#}S     ANDC/ANDZ
     * OPCODE  D,{#}S       ORC/ORZ
     * OPCODE  D,{#}S     XORC/XORZ
     */
    public static Spin2PAsmSchema TEST_OP = new Spin2PAsmSchema() {

        @Override
        public boolean check(List<Spin2PAsmExpression> arguments, String effect) {
            return arguments.size() == 2 && arguments.get(0).prefix == null && (effect == null || E_TEST_OP.contains(effect.toLowerCase()));
        }

    };

    /**
     * OPCODE  {#]D           WC/WZ
     * OPCODE  {#]D       ANDC/ANDZ
     * OPCODE  {#]D         ORC/ORZ
     * OPCODE  {#]D       XORC/XORZ
     */
    public static Spin2PAsmSchema TEST_OP2 = new Spin2PAsmSchema() {

        @Override
        public boolean check(List<Spin2PAsmExpression> arguments, String effect) {
            return arguments.size() == 1 && (effect == null || E_TEST_OP.contains(effect.toLowerCase()));
        }

    };

    public abstract boolean check(List<Spin2PAsmExpression> arguments, String effect);
}
