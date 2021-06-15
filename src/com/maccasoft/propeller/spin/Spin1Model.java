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
import java.util.Set;

import com.maccasoft.propeller.model.Node;

public class Spin1Model extends SpinModel {

    static Set<String> instructions = new HashSet<String>(Arrays.asList(new String[] {
        "ORG", "FIT",
        "ABS", "ABSNEG", "ADD", "ADDABS", "ADDS", "ADDSX", "ADDX", "AND", "ANDN", "CALL", "CLKSET", "CMP", "CMPS", "CMPSUB",
        "CMPSX", "CMPX", "COGID", "COGINIT", "COGSTOP", "DJNZ", "HUBOP", "JMP", "JMPRET", "LOCKCLR", "LOCKNEW", "LOCKRET",
        "LOCKSET", "MAX", "MAXS", "MIN", "MINS", "MOV", "MOVD", "MOVI", "MOVS", "MUXC", "MUXNC", "MUXNZ", "MUXZ", "NEG", "NEGC",
        "NEGNC", "NEGNZ", "NEGZ", "NOP", "OR", "RCL", "RCR", "RDBYTE", "RDLONG", "RDWORD", "RET", "REV", "ROL", "ROR", "SAR",
        "SHL", "SHR", "SUB", "SUBABS", "SUBS", "SUBSX", "SUBX", "SUMC", "SUMNC", "SUMNZ", "SUMZ", "TEST", "TESTN", "TJNZ", "TJZ",
        "WAITCNT", "WAITPEQ", "WAITPNE", "WAITVID", "WRBYTE", "WRLONG", "WRWORD", "XOR",
    }));

    static Set<String> conditions = new HashSet<String>(Arrays.asList(new String[] {
        "IF_ALWAYS", "IF_NEVER", "IF_E", "IF_NE", "IF_A", "IF_B", "IF_AE", "IF_BE", "IF_C", "IF_NC", "IF_Z", "IF_NZ", "IF_C_EQ_Z",
        "IF_C_NE_Z", "IF_C_AND_Z", "IF_C_AND_NZ", "IF_NC_AND_Z", "IF_NC_AND_NZ", "IF_C_OR_Z", "IF_C_OR_NZ", "IF_NC_OR_Z",
        "IF_NC_OR_NZ", "IF_Z_EQ_C", "IF_Z_NE_C", "IF_Z_AND_C", "IF_Z_AND_NC", "IF_NZ_AND_C", "IF_NZ_AND_NC", "IF_Z_OR_C",
        "IF_Z_OR_NC", "IF_NZ_OR_C", "IF_NZ_OR_NC",
    }));

    static Set<String> modifiers = new HashSet<String>(Arrays.asList(new String[] {
        "WZ", "WC", "WR", "NR",
    }));

    static Set<String> types = new HashSet<String>(Arrays.asList(new String[] {
        "LONG", "WORD", "BYTE",
    }));

    static Set<String> blockStart = new HashSet<String>(Arrays.asList(new String[] {
        "IF", "IFNOT", "ELSEIF", "ELSEIFNOT", "ELSE", "CASE", "CASE_FAST", "OTHER", "REPEAT",
    }));

    public static boolean isInstruction(String token) {
        return instructions.contains(token.toUpperCase());
    }

    public static boolean isCondition(String token) {
        return conditions.contains(token.toUpperCase());
    }

    public static boolean isModifier(String token) {
        return modifiers.contains(token.toUpperCase());
    }

    public static boolean isType(String token) {
        return types.contains(token.toUpperCase());
    }

    public static boolean isBlockStart(String token) {
        return blockStart.contains(token.toUpperCase());
    }

    public Spin1Model(Node root) {
        super(root);
    }

}
