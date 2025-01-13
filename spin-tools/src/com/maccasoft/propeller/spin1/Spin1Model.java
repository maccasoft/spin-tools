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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.spin2.SpinModel;

public class Spin1Model extends SpinModel {

    static Set<String> pasmInstructions = new HashSet<>(Arrays.asList(new String[] {
        "ORG", "FIT", "RES", "FILE", "INCLUDE",
        "ABS", "ABSNEG", "ADD", "ADDABS", "ADDS", "ADDSX", "ADDX", "AND", "ANDN", "CALL", "CLKSET", "CMP", "CMPS", "CMPSUB",
        "CMPSX", "CMPX", "COGID", "COGINIT", "COGSTOP", "DEC", "DJNZ", "ENC", "HUBOP", "JMP", "JMPRET", "LOCKCLR", "LOCKNEW",
        "LOCKRET", "LOCKSET", "MAX", "MAXS", "MIN", "MINS", "MOV", "MOVD", "MOVI", "MOVS", "MUL", "MULS", "MUXC", "MUXNC", "MUXNZ",
        "MUXZ", "NEG", "NEGC", "NEGNC", "NEGNZ", "NEGZ", "NOP", "ONES", "OR", "RCL", "RCR", "RDBYTE", "RDLONG", "RDWORD", "RET",
        "REV", "ROL", "ROR", "SAR", "SHL", "SHR", "SUB", "SUBABS", "SUBS", "SUBSX", "SUBX", "SUMC", "SUMNC", "SUMNZ", "SUMZ",
        "TEST", "TESTN", "TJNZ", "TJZ", "WAITCNT", "WAITPEQ", "WAITPNE", "WAITVID", "WRBYTE", "WRLONG", "WRWORD", "XOR",
        "LONG", "WORD", "BYTE", "WORDFIT", "BYTEFIT"
    }));

    static Set<String> conditions = new HashSet<>(Arrays.asList(new String[] {
        "IF_ALWAYS", "IF_NEVER", "IF_E", "IF_NE", "IF_A", "IF_B", "IF_AE", "IF_BE", "IF_C", "IF_NC", "IF_Z", "IF_NZ", "IF_C_EQ_Z",
        "IF_C_NE_Z", "IF_C_AND_Z", "IF_C_AND_NZ", "IF_NC_AND_Z", "IF_NC_AND_NZ", "IF_C_OR_Z", "IF_C_OR_NZ", "IF_NC_OR_Z",
        "IF_NC_OR_NZ", "IF_Z_EQ_C", "IF_Z_NE_C", "IF_Z_AND_C", "IF_Z_AND_NC", "IF_NZ_AND_C", "IF_NZ_AND_NC", "IF_Z_OR_C",
        "IF_Z_OR_NC", "IF_NZ_OR_C", "IF_NZ_OR_NC"
    }));

    static Set<String> modifiers = new HashSet<>(Arrays.asList(new String[] {
        "WZ", "WC", "WR", "NR"
    }));

    static Set<String> types = new HashSet<>(Arrays.asList(new String[] {
        "LONG", "WORD", "BYTE"
    }));

    static Set<String> blockStart = new HashSet<>(Arrays.asList(new String[] {
        "IF", "IFNOT", "ELSEIF", "ELSEIFNOT", "ELSE", "CASE", "CASE_FAST", "OTHER", "REPEAT"
    }));

    public static boolean isPAsmInstruction(String token) {
        return pasmInstructions.contains(token.toUpperCase());
    }

    public static boolean isPAsmCondition(String token) {
        return conditions.contains(token.toUpperCase());
    }

    public static boolean isPAsmModifier(String token) {
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
