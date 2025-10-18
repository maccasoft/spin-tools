/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
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
import java.util.Set;

import com.maccasoft.propeller.model.Node;

public class Spin2Model extends SpinModel {

    public static Set<String> instructions = new HashSet<String>(Arrays.asList(new String[] {
        "ORG", "ORGH", "ORGF", "FIT", "NAMESP", "DITTO",
        "RES", "FILE", "INCLUDE", "ASMCLK", "ALIGNL", "ALIGNW",
        "NOP", "ROL", "ROR", "SHR", "SHL", "RCR", "RCL", "SAR", "SAL", "ADD", "ADDX", "ADDS", "ADDSX", "SUB", "SUBX", "SUBS",
        "SUBSX", "CMP", "CMPX", "CMPS", "CMPSX", "CMPR", "CMPM", "SUBR", "CMPSUB", "FGE", "FLE", "FGES", "FLES", "SUMC", "SUMNC",
        "SUMZ", "SUMNZ", "TESTB", "TESTBN", "BITL", "BITH", "BITC", "BITNC", "BITZ", "BITNZ", "BITNC", "BITRND", "BITNOT", "AND",
        "ANDN", "OR", "XOR", "MUXC", "MUXNC", "MUXZ", "MUXNZ", "MOV", "NOT", "ABS", "NEG", "NEGC", "NEGNC", "NEGZ", "NEGNZ",
        "INCMOD", "DECMOD", "ZEROX", "SIGNX", "ENCOD", "ONES", "TEST", "TESTN", "SETNIB", "GETNIB", "ROLNIB", "SETBYTE", "GETBYTE",
        "ROLBYTE", "SETWORD", "GETWORD", "ROLWORD", "ALTSN", "ALTGN", "ALTSB", "ALTGB", "ALTSW", "ALTGW", "ALTR", "ALTD", "ALTS",
        "ALTB", "ALTI", "SETR", "SETD", "SETS", "DECOD", "BMASK", "CRCBIT", "CRCNIB", "MUXNITS", "MUXNIBS", "MUXQ", "MOVBYTS",
        "MUL", "MULS", "SCA", "SCAS", "ADDPIX", "MULPIX", "BLNPIX", "MIXPIX", "ADDCT1", "ADDCT2", "ADDCT3", "WMLONG", "RQPIN",
        "RDPIN", "RDLUT", "RDBYTE", "RDWORD", "RDLONG", "POPA", "POPB", "CALLD", "RESI3", "RESI2", "RESI1", "RESI0", "RETI3",
        "RETI2", "RETI1", "RETI0", "CALLPA", "CALLPB", "DJZ", "DJNZ", "DJF", "DJNF", "IJZ", "IJNZ", "TJZ", "TJNZ", "TJF", "TJNF",
        "TJS", "TJNS", "TJV", "JINT", "JCT1", "JCT2", "JCT3", "JSE1", "JSE2", "JSE3", "JSE4", "JPAT", "JFBW", "JXMT", "JXFI", "JXRO",
        "JXRL", "JATN", "JQMT", "JNINT", "JNCT1", "JNCT2", "JNCT3", "JNSE1", "JNSE2", "JNSE3", "JNSE4", "JNPAT", "JNFBW", "JNXMT",
        "JNXFI", "JNXRO", "JNXRL", "JNATN", "JNQMT", "SETPAT", "AKPIN", "WRPIN", "WXPIN", "WYPIN", "WRLUT",
        "WRBYTE", "WRWORD", "WRLONG", "PUSHA", "PUSHB", "RDFAST", "WRFAST", "FBLOCK", "XINIT", "XSTOP", "XZERO", "XCONT", "REP",
        "COGINIT", "QMUL", "QDIV", "QFRAC", "QSQRT", "QROTATE", "QVECTOR", "HUBSET", "COGID", "COGSTOP", "LOCKNEW", "LOCKRET",
        "LOCKTRY", "LOCKREL", "QLOG", "QEXP", "RFBYTE", "RFWORD", "RFLONG", "RFVAR", "RFVARS", "WFBYTE", "WFWORD", "WFLONG",
        "GETQX", "GETQY", "GETCT", "GETRND", "SETDACS", "SETXFRQ", "GETXACC", "WAITX", "SETSE1", "SETSE2", "SETSE3", "SETSE4",
        "POLLINT", "POLLCT1", "POLLCT2", "POLLCT3", "POLLSE1", "POLLSE2", "POLLSE3", "POLLSE4", "POLLPAT", "POLLFBW", "POLLXMT",
        "POLLXFI", "POLLXRO", "POLLXRL", "POLLATN", "POLLQMT", "WAITINT", "WAITCT1", "WAITCT2", "WAITCT3", "WAITSE1", "WAITSE2",
        "WAITSE3", "WAITSE4", "WAITPAT", "WAITFBW", "WAITXMT", "WAITXFI", "WAITXRO", "WAITXRL", "WAITATN", "ALLOWI", "STALLI",
        "TRGINT1", "TRGINT2", "TRGINT3", "NIXINT1", "NIXINT2", "NIXINT3", "SETINT1", "SETINT2", "SETINT3", "SETQ", "SETQ2",
        "PUSH", "POP", "JMP", "CALL", "RET", "CALLA", "RETA", "CALLB", "RETB", "JMPREL", "SKIP", "SKIPF", "EXECF", "GETPTR",
        "GETBRK", "COGBRK", "BRK", "SETLUTS", "SETCY", "SETCI", "SETCQ", "SETCFRQ", "SETCMOD", "SETPIV", "SETPIX", "COGATN",
        "TESTP", "TESTPN", "DIRL", "DIRH", "DIRC", "DIRNC", "DIRZ", "DIRNZ", "DIRRND", "DIRNOT", "OUTL", "OUTH", "OUTC", "OUTNC",
        "OUTZ", "OUTNZ", "OUTRND", "OUTNOT", "FLTL", "FLTH", "FLTC", "FLTNC", "FLTZ", "FLTNZ", "FLTRND", "FLTNOT", "DRVL", "DRVH",
        "DRVC", "DRVNC", "DRVZ", "DRVNZ", "DRVRND", "DRVNOT", "SPLITB", "MERGEB", "SPLITW", "MERGEW", "SEUSSF", "SEUSSR", "RGBSQZ",
        "RGBEXP", "XORO32", "REV", "RCZR", "RCZL", "WRC", "WRNC", "WRZ", "WRNZ", "MODCZ", "MODC", "MODZ", "SETSCP", "GETSCP",
        "JMP", "CALL", "CALLA", "CALLB", "CALLD", "LOC", "AUGS", "AUGD",
        "LONG", "WORD", "BYTE", "WORDFIT", "BYTEFIT",
    }));

    public static Set<String> conditions = new HashSet<String>(Arrays.asList(new String[] {
        "_RET_",
        "IF_NC_AND_NZ", "IF_NZ_AND_NC", "IF_GT", "IF_A", "IF_00", "IF_NC_AND_Z", "IF_Z_AND_NC", "IF_01", "IF_NC", "IF_GE", "IF_AE",
        "IF_0X", "IF_C_AND_NZ", "IF_NZ_AND_C", "IF_10", "IF_NZ", "IF_NE", "IF_X0", "IF_C_NE_Z", "IF_Z_NE_C", "IF_DIFF",
        "IF_NC_OR_NZ", "IF_NZ_OR_NC", "IF_NOT_11", "IF_C_AND_Z", "IF_Z_AND_C", "IF_11", "IF_C_EQ_Z", "IF_Z_EQ_C", "IF_SAME",
        "IF_Z", "IF_E", "IF_X1", "IF_NC_OR_Z", "IF_Z_OR_NC", "IF_NOT_10", "IF_C", "IF_LT", "IF_B", "IF_1X", "IF_C_OR_NZ",
        "IF_NZ_OR_C", "IF_NOT_01", "IF_C_OR_Z", "IF_Z_OR_C", "IF_LE", "IF_BE", "IF_NOT_00", "IF_ALWAYS"
    }));

    public static Set<String> modifiers = new HashSet<String>(Arrays.asList(new String[] {
        "WC", "WZ", "WCZ",
        "ANDC", "ANDZ", "ORC", "ORZ", "XORC", "XORZ",
    }));

    public static Set<String> types = new HashSet<String>(Arrays.asList(new String[] {
        "LONG", "WORD", "BYTE",
    }));

    static Set<String> blockStart = new HashSet<String>(Arrays.asList(new String[] {
        "IF", "IFNOT", "ELSEIF", "ELSEIFNOT", "ELSE", "CASE", "CASE_FAST", "OTHER", "REPEAT",
    }));

    public static Set<String> debugKeywords = new HashSet<String>(Arrays.asList(new String[] {
        "FDEC",
        "FDEC_REG_ARRAY",
        "FDEC_ARRAY",

        "FDEC_",
        "FDEC_REG_ARRAY_",
        "FDEC_ARRAY_",

        "UDEC",
        "UDEC_BYTE",
        "UDEC_WORD",
        "UDEC_LONG",
        "SDEC",
        "SDEC_BYTE",
        "SDEC_WORD",
        "SDEC_LONG",
        "UHEX",
        "UHEX_BYTE",
        "UHEX_WORD",
        "UHEX_LONG",
        "SHEX",
        "SHEX_BYTE",
        "SHEX_WORD",
        "SHEX_LONG",
        "UBIN",
        "UBIN_BYTE",
        "UBIN_WORD",
        "UBIN_LONG",
        "SBIN",
        "SBIN_BYTE",
        "SBIN_WORD",
        "SBIN_LONG",

        "UDEC_",
        "UDEC_BYTE_",
        "UDEC_WORD_",
        "UDEC_LONG_",
        "SDEC_",
        "SDEC_BYTE_",
        "SDEC_WORD_",
        "SDEC_LONG_",
        "UHEX_",
        "UHEX_BYTE_",
        "UHEX_WORD_",
        "UHEX_LONG_",
        "SHEX_",
        "SHEX_BYTE_",
        "SHEX_WORD_",
        "SHEX_LONG_",
        "UBIN_",
        "UBIN_BYTE_",
        "UBIN_WORD_",
        "UBIN_LONG_",
        "SBIN_",
        "SBIN_BYTE_",
        "SBIN_WORD_",
        "SBIN_LONG_",

        "UDEC_REG_ARRAY",
        "UDEC_BYTE_ARRAY",
        "UDEC_WORD_ARRAY",
        "UDEC_LONG_ARRAY",
        "SDEC_REG_ARRAY",
        "SDEC_BYTE_ARRAY",
        "SDEC_WORD_ARRAY",
        "SDEC_LONG_ARRAY",
        "UHEX_REG_ARRAY",
        "UHEX_BYTE_ARRAY",
        "UHEX_WORD_ARRAY",
        "UHEX_LONG_ARRAY",
        "SHEX_REG_ARRAY",
        "SHEX_BYTE_ARRAY",
        "SHEX_WORD_ARRAY",
        "SHEX_LONG_ARRAY",
        "UBIN_REG_ARRAY",
        "UBIN_BYTE_ARRAY",
        "UBIN_WORD_ARRAY",
        "UBIN_LONG_ARRAY",
        "SBIN_REG_ARRAY",
        "SBIN_BYTE_ARRAY",
        "SBIN_WORD_ARRAY",
        "SBIN_LONG_ARRAY",

        "UDEC_REG_ARRAY_",
        "UDEC_BYTE_ARRAY_",
        "UDEC_WORD_ARRAY_",
        "UDEC_LONG_ARRAY_",
        "SDEC_REG_ARRAY_",
        "SDEC_BYTE_ARRAY_",
        "SDEC_WORD_ARRAY_",
        "SDEC_LONG_ARRAY_",
        "UHEX_REG_ARRAY_",
        "UHEX_BYTE_ARRAY_",
        "UHEX_WORD_ARRAY_",
        "UHEX_LONG_ARRAY_",
        "SHEX_REG_ARRAY_",
        "SHEX_BYTE_ARRAY_",
        "SHEX_WORD_ARRAY_",
        "SHEX_LONG_ARRAY_",
        "UBIN_REG_ARRAY_",
        "UBIN_BYTE_ARRAY_",
        "UBIN_WORD_ARRAY_",
        "UBIN_LONG_ARRAY_",
        "SBIN_REG_ARRAY_",
        "SBIN_BYTE_ARRAY_",
        "SBIN_WORD_ARRAY_",
        "SBIN_LONG_ARRAY_",

        "BOOL",
        "BOOL_",
        "C_Z",

        "DLY",
        "ZSTR",
        "ZSTR_",
        "LSTR",
        "LSTR_",

        "IF",
        "IFNOT",

        "PC_KEY",
        "PC_MOUSE",

        "SIZEOF",
    }));

    public static boolean isPAsmInstruction(String token) {
        return instructions.contains(token.toUpperCase());
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

    public static boolean isDebugKeyword(String token) {
        return debugKeywords.contains(token.toUpperCase());
    }

    public Spin2Model(Node root) {
        super(root);
    }

}
