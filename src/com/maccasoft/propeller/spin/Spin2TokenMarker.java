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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.maccasoft.propeller.spin.Spin2Parser.AtomContext;
import com.maccasoft.propeller.spin.Spin2Parser.ConstantAssignContext;
import com.maccasoft.propeller.spin.Spin2Parser.ConstantsSectionContext;
import com.maccasoft.propeller.spin.Spin2Parser.DataContext;
import com.maccasoft.propeller.spin.Spin2Parser.DataLineContext;
import com.maccasoft.propeller.spin.Spin2Parser.FunctionContext;
import com.maccasoft.propeller.spin.Spin2Parser.IdentifierContext;
import com.maccasoft.propeller.spin.Spin2Parser.LabelContext;
import com.maccasoft.propeller.spin.Spin2Parser.LocalvarContext;
import com.maccasoft.propeller.spin.Spin2Parser.MethodContext;
import com.maccasoft.propeller.spin.Spin2Parser.ObjectContext;
import com.maccasoft.propeller.spin.Spin2Parser.ObjectsSectionContext;
import com.maccasoft.propeller.spin.Spin2Parser.OpcodeContext;
import com.maccasoft.propeller.spin.Spin2Parser.ParametersContext;
import com.maccasoft.propeller.spin.Spin2Parser.ProgContext;
import com.maccasoft.propeller.spin.Spin2Parser.ResultContext;
import com.maccasoft.propeller.spin.Spin2Parser.VariableContext;
import com.maccasoft.propeller.spin.Spin2Parser.VariablesSectionContext;

@SuppressWarnings({
    "unchecked", "rawtypes"
})
public class Spin2TokenMarker {

    public static enum TokenId {
        NULL,
        COMMENT,
        SECTION,
        NUMBER,
        STRING,
        KEYWORD,
        FUNCTION,
        OPERATOR,
        TYPE,

        CONSTANT,
        VARIABLE,
        OBJECT,

        METHOD_PUB,
        METHOD_PRI,
        METHOD_LOCAL,
        METHOD_RETURN,
        METHOD_PARAMETER,

        PASM_LABEL,
        PASM_CONDITION,
        PASM_INSTRUCTION,
        PASM_MODIFIER,

        WARNING,
        ERROR
    }

    static Set<Integer> commentSet = new HashSet<Integer>(Arrays.asList(new Integer[] {
        Spin2Lexer.COMMENT, Spin2Lexer.BLOCK_COMMENT
    }));

    static Map<Integer, TokenId> typeMap = new HashMap<Integer, TokenId>();
    static {
        typeMap.put(Spin2Lexer.COMMENT, TokenId.COMMENT);
        typeMap.put(Spin2Lexer.BLOCK_COMMENT, TokenId.COMMENT);

        typeMap.put(Spin2Lexer.BIN, TokenId.NUMBER);
        typeMap.put(Spin2Lexer.HEX, TokenId.NUMBER);
        typeMap.put(Spin2Lexer.QUAD, TokenId.NUMBER);
        typeMap.put(Spin2Lexer.NUMBER, TokenId.NUMBER);
        typeMap.put(Spin2Lexer.STRING, TokenId.STRING);

        typeMap.put(Spin2Lexer.REPEAT, TokenId.KEYWORD);
        typeMap.put(Spin2Lexer.WHILE, TokenId.KEYWORD);
        typeMap.put(Spin2Lexer.UNTIL, TokenId.KEYWORD);
        typeMap.put(Spin2Lexer.FROM, TokenId.KEYWORD);
        typeMap.put(Spin2Lexer.TO, TokenId.KEYWORD);
        typeMap.put(Spin2Lexer.STEP, TokenId.KEYWORD);

        typeMap.put(Spin2Lexer.IF, TokenId.KEYWORD);
        typeMap.put(Spin2Lexer.IFNOT, TokenId.KEYWORD);
        typeMap.put(Spin2Lexer.ELSE, TokenId.KEYWORD);
        typeMap.put(Spin2Lexer.ELSEIF, TokenId.KEYWORD);
        typeMap.put(Spin2Lexer.ELSEIFNOT, TokenId.KEYWORD);

        typeMap.put(Spin2Lexer.CASE, TokenId.KEYWORD);
        typeMap.put(Spin2Lexer.OTHER, TokenId.KEYWORD);
    }

    static Map<String, TokenId> keywords = new HashMap<String, TokenId>();
    static {
        keywords.put("CON", TokenId.SECTION);
        keywords.put("VAR", TokenId.SECTION);
        keywords.put("OBJ", TokenId.SECTION);
        keywords.put("PUB", TokenId.SECTION);
        keywords.put("PRI", TokenId.SECTION);
        keywords.put("DAT", TokenId.SECTION);

        keywords.put("BYTE", TokenId.TYPE);
        keywords.put("WORD", TokenId.TYPE);
        keywords.put("LONG", TokenId.TYPE);

        keywords.put("HUBSET", TokenId.FUNCTION);
        keywords.put("CLKSET", TokenId.FUNCTION);
        keywords.put("COGSPIN", TokenId.FUNCTION);
        keywords.put("COGINIT", TokenId.FUNCTION);
        keywords.put("COGSTOP", TokenId.FUNCTION);
        keywords.put("COGID", TokenId.FUNCTION);
        keywords.put("COGCHK", TokenId.FUNCTION);
        keywords.put("LOCKNEW", TokenId.FUNCTION);
        keywords.put("LOCKRET", TokenId.FUNCTION);
        keywords.put("LOCKTRY", TokenId.FUNCTION);
        keywords.put("LOCKREL", TokenId.FUNCTION);
        keywords.put("LOCKCHK", TokenId.FUNCTION);
        keywords.put("COGATN", TokenId.FUNCTION);
        keywords.put("POLLATN", TokenId.FUNCTION);
        keywords.put("WAITATN", TokenId.FUNCTION);

        keywords.put("PINW", TokenId.FUNCTION);
        keywords.put("PINWRITE", TokenId.FUNCTION);
        keywords.put("PINL", TokenId.FUNCTION);
        keywords.put("PINLOW", TokenId.FUNCTION);
        keywords.put("PINH", TokenId.FUNCTION);
        keywords.put("PINHIGH", TokenId.FUNCTION);
        keywords.put("PINT", TokenId.FUNCTION);
        keywords.put("PINTOGGLE", TokenId.FUNCTION);
        keywords.put("PINF", TokenId.FUNCTION);
        keywords.put("PINFLOAT", TokenId.FUNCTION);
        keywords.put("PINR", TokenId.FUNCTION);
        keywords.put("PINREAD", TokenId.FUNCTION);
        keywords.put("PINSTART", TokenId.FUNCTION);
        keywords.put("PINCLEAR", TokenId.FUNCTION);
        keywords.put("WRPIN", TokenId.FUNCTION);
        keywords.put("WXPIN", TokenId.FUNCTION);
        keywords.put("WYPIN", TokenId.FUNCTION);
        keywords.put("AKPIN", TokenId.FUNCTION);
        keywords.put("RDPIN", TokenId.FUNCTION);
        keywords.put("RQPIN", TokenId.FUNCTION);

        keywords.put("GETCT", TokenId.FUNCTION);
        keywords.put("POLLCT", TokenId.FUNCTION);
        keywords.put("WAITCT", TokenId.FUNCTION);
        keywords.put("WAITUS", TokenId.FUNCTION);
        keywords.put("WAITMS", TokenId.FUNCTION);
        keywords.put("GETSEC", TokenId.FUNCTION);
        keywords.put("GETMS", TokenId.FUNCTION);

        keywords.put("CALL", TokenId.FUNCTION);
        keywords.put("REGEXEC", TokenId.FUNCTION);
        keywords.put("REGLOAD", TokenId.FUNCTION);

        keywords.put("ROTXY", TokenId.FUNCTION);
        keywords.put("POLXY", TokenId.FUNCTION);
        keywords.put("XYPOL", TokenId.FUNCTION);
        keywords.put("QSIN", TokenId.FUNCTION);
        keywords.put("QCOS", TokenId.FUNCTION);
        keywords.put("MULDIV64", TokenId.FUNCTION);

        keywords.put("GETREGS", TokenId.FUNCTION);
        keywords.put("SETREGS", TokenId.FUNCTION);
        keywords.put("BYTEMOVE", TokenId.FUNCTION);
        keywords.put("WORDMOVE", TokenId.FUNCTION);
        keywords.put("LONGMOVE", TokenId.FUNCTION);
        keywords.put("BYTEFILL", TokenId.FUNCTION);
        keywords.put("WORDFILL", TokenId.FUNCTION);
        keywords.put("LONGFILL", TokenId.FUNCTION);

        keywords.put("STRSIZE", TokenId.FUNCTION);
        keywords.put("STRCOMP", TokenId.FUNCTION);
        keywords.put("STRING", TokenId.FUNCTION);

        keywords.put("LOOKUP", TokenId.FUNCTION);
        keywords.put("LOOKUPZ", TokenId.FUNCTION);
        keywords.put("LOOKDOWN", TokenId.FUNCTION);
        keywords.put("LOOKDOWNZ", TokenId.FUNCTION);

        keywords.put("ABORT", TokenId.KEYWORD);
        keywords.put("SEND", TokenId.KEYWORD);
        keywords.put("RECV", TokenId.KEYWORD);
        keywords.put("IF", TokenId.KEYWORD);
        keywords.put("IFNOT", TokenId.KEYWORD);
        keywords.put("ELSEIF", TokenId.KEYWORD);
        keywords.put("ELSEIFNOT", TokenId.KEYWORD);
        keywords.put("ELSE", TokenId.KEYWORD);
        keywords.put("CASE", TokenId.KEYWORD);
        keywords.put("CASE_FAST", TokenId.KEYWORD);
        keywords.put("OTHER", TokenId.KEYWORD);
        keywords.put("REPEAT", TokenId.KEYWORD);
        keywords.put("FROM", TokenId.KEYWORD);
        keywords.put("TO", TokenId.KEYWORD);
        keywords.put("STEP", TokenId.KEYWORD);
        keywords.put("WHILE", TokenId.KEYWORD);
        keywords.put("UNTIL", TokenId.KEYWORD);
        keywords.put("NEXT", TokenId.KEYWORD);
        keywords.put("QUIT", TokenId.KEYWORD);

        keywords.put("END", TokenId.KEYWORD);

        keywords.put("_CLKFREQ", TokenId.CONSTANT);
        keywords.put("_CLKMODE", TokenId.CONSTANT);
        keywords.put("CLKFREQ", TokenId.CONSTANT);
        keywords.put("CLKMODE", TokenId.CONSTANT);
        keywords.put("VARBASE", TokenId.CONSTANT);

        keywords.put("TRUE", TokenId.CONSTANT);
        keywords.put("FALSE", TokenId.CONSTANT);
        keywords.put("POSX", TokenId.CONSTANT);
        keywords.put("NEGX", TokenId.CONSTANT);
        keywords.put("PI", TokenId.CONSTANT);
    }

    static Map<String, TokenId> pasmKeywords = new HashMap<String, TokenId>();
    static {
        pasmKeywords.put("ORG", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("ORGH", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("ORGF", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("FIT", TokenId.PASM_INSTRUCTION);

        pasmKeywords.put("BYTE", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("WORD", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("LONG", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("RES", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("FILE", TokenId.PASM_INSTRUCTION);

        pasmKeywords.put("ASMCLK", TokenId.PASM_INSTRUCTION);

        pasmKeywords.put("NOP", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("ROL", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("ROR", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SHR", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SHL", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("RCR", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("RCL", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SAR", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SAL", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("ADD", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("ADDX", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("ADDS", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("ADDSX", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SUB", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SUBX", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SUBS", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SUBSX", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("CMP", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("CMPX", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("CMPS", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("CMPSX", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("CMPR", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("CMPM", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SUBR", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("CMPSUB", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("FGE", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("FLE", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("FGES", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("FLES", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SUMC", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SUMNC", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SUMZ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SUMNZ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("TESTB", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("TESTBN", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("BITL", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("BITH", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("BITC", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("BITNC", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("BITZ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("BITNZ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("BITNC", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("BITRND", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("BITNOT", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("AND", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("ANDN", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("OR", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("XOR", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("MUXC", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("MUXNC", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("MUXZ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("MUXNZ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("MOV", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("NOT", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("ABS", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("NEG", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("NEGC", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("NEGNC", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("NEGZ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("NEGNZ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("INCMOD", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("DECMOD", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("ZEROX", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SIGNX", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("ENCOD", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("ONES", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("TEST", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("TESTN", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SETNIB", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("GETNIB", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("ROLNIB", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SETBYTE", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("GETBYTE", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("ROLBYTE", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SETWORD", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("GETWORD", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("ROLWORD", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("ALTSN", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("ALTGN", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("ALTSB", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("ALTGB", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("ALTSW", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("ALTGW", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("ALTR", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("ALTD", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("ALTS", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("ALTB", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("ALTI", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SETR", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SETD", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SETS", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("DECOD", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("BMASK", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("CRCBIT", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("CRCNIB", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("MUXNITS", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("MUXNIBS", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("MUXQ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("MOVBYTS", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("MUL", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("MULS", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SCA", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SCAS", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("ADDPIX", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("MULPIX", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("BLNPIX", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("MIXPIX", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("ADDCT1", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("ADDCT2", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("ADDCT3", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("WMLONG", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("RQPIN", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("RDPIN", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("RDLUT", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("RDBYTE", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("RDWORD", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("RDLONG", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("POPA", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("POPB", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("CALLD", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("RESI3", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("RESI2", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("RESI1", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("RESI0", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("REST3", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("REST2", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("REST1", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("REST0", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("CALLPA", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("CALLPB", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("DJZ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("DJNZ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("DJF", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("DJNF", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("IJZ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("IJNZ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("TJZ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("TJNZ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("TJF", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("TJNF", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("TJS", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("TJNS", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("JINT", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("JCT1", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("JCT2", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("JCT3", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("JSE1", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("JSE2", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("JSE3", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("JSE4", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("JPAT", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("JFBW", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("JXMT", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("JXFI", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("JXRO", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("JXRL", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("JATN", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("JQMT", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("JNINT", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("JNCT1", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("JNCT2", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("JNCT3", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("JNSE1", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("JNSE2", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("JNSE3", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("JNSE4", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("JNPAT", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("JNFBW", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("JNXMT", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("JNXFI", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("JNXRO", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("JNXRL", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("JNATN", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("JNQMT", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SETPAT", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("AKPIN", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("WRPIN", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("WXPIN", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("WYPIN", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("WRLUT", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("WRBYTE", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("WRWORD", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("WRLONG", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("PUSHA", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("PUSHB", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("RDFAST", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("WRFAST", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("FBLOCK", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("XINIT", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("XSTOP", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("XZERO", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("XCONT", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("REP", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("COGINIT", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("QMUL", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("QDIV", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("QFRAC", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("QSQRT", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("QROTATE", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("QVECTOR", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("HUBSET", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("COGID", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("COGSTOP", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("LOCKNEW", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("LOCKRET", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("LOCKTRY", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("LOCKREL", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("QLOG", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("QEXP", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("RFBYTE", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("RFWORD", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("RFLONG", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("RFVAR", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("RFVARS", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("WFBYTE", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("WFWORD", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("WFLONG", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("GETQX", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("GETQY", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("GETCT", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("GETRND", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SETDACS", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SETXFRQ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("GETXACC", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("WAITX", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SETSE1", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SETSE2", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SETSE3", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SETSE4", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("POLLINT", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("POLLCT1", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("POLLCT2", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("POLLCT3", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("POLLSE1", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("POLLSE2", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("POLLSE3", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("POLLSE4", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("POLLPAT", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("POLLFBW", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("POLLXMT", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("POLLXFI", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("POLLXRO", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("POLLXRL", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("POLLATN", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("POLLQMT", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("WAITINT", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("WAITCT1", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("WAITCT2", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("WAITCT3", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("WAITSE1", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("WAITSE2", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("WAITSE3", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("WAITSE4", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("WAITPAT", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("WAITFBW", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("WAITXMT", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("WAITXFI", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("WAITXRO", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("WAITXRL", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("WAITATN", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("ALLOWI", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("STALLI", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("TRIGINT1", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("TRIGINT2", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("TRIGINT3", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("NIXINT1", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("NIXINT2", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("NIXINT3", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SETINT1", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SETINT2", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SETINT3", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SETQ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SETQ2", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("PUSH", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("POP", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("JMP", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("CALL", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("RET", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("CALLA", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("RETA", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("CALLB", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("RETB", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("JMPREL", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SKIP", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SKIPF", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("EXECF", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("GETPTR", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("GETBRK", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("COGBRK", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("BRK", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SETLUTS", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SETCY", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SETCI", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SETCQ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SETCFRQ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SETCMOD", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SETPIV", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SETPIX", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("COGATN", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("TESTP", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("TESTPN", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("DIRL", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("DIRH", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("DIRC", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("DIRNC", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("DIRZ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("DIRNZ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("DIRRND", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("DIRNOT", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("OUTL", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("OUTH", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("OUTC", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("OUTNC", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("OUTZ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("OUTNZ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("OUTRND", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("OUTNOT", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("FLTL", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("FLTH", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("FLTC", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("FLTNC", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("FLTZ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("FLTNZ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("FLTRND", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("FLTNOT", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("DRVL", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("DRVH", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("DRVC", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("DRVNC", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("DRVZ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("DRVNZ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("DRVRND", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("DRVNOT", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SPLITB", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("MERGEB", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SPLITW", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("MERGEW", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SEUSSF", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SEUSSR", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("RGBSQZ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("RGBEXP", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("XORO32", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("REV", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("RCZR", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("RCZL", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("WRC", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("WRNC", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("WRZ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("WRNZ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("MODCZ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("MODC", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("MODZ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SETSCP", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("GETSCP", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("JMP", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("CALL", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("CALLA", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("CALLB", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("CALLD", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("LOC", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("AUGS", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("AUGD", TokenId.PASM_INSTRUCTION);

        pasmKeywords.put("PR0", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("PR1", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("PR2", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("PR3", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("PR4", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("PR5", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("PR6", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("PR7", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("IJMP3", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("IRET3", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("IJMP2", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("IRET2", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("IJMP1", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("IRET1", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("PA", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("PB", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("PTRA", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("PTRB", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("DIRA", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("DIRB", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("OUTA", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("OUTB", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("INA", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("INB", TokenId.PASM_INSTRUCTION);

        pasmKeywords.put("WC", TokenId.PASM_MODIFIER);
        pasmKeywords.put("WZ", TokenId.PASM_MODIFIER);
        pasmKeywords.put("WCZ", TokenId.PASM_MODIFIER);
        pasmKeywords.put("ANDC", TokenId.PASM_MODIFIER);
        pasmKeywords.put("ANDZ", TokenId.PASM_MODIFIER);
        pasmKeywords.put("ORC", TokenId.PASM_MODIFIER);
        pasmKeywords.put("ORZ", TokenId.PASM_MODIFIER);
        pasmKeywords.put("XORC", TokenId.PASM_MODIFIER);
        pasmKeywords.put("XORZ", TokenId.PASM_MODIFIER);

        pasmKeywords.put("_RET_", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_NC_AND_NZ", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_NZ_AND_NC", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_GT", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_A", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_00", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_NC_AND_Z", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_Z_AND_NC", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_01", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_NC", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_GE", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_AE", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_0X", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_C_AND_NZ", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_NZ_AND_C", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_10", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_NZ", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_NE", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_X0", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_C_NE_Z", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_Z_NE_C", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_DIFF", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_NC_OR_NZ", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_NZ_OR_NC", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_NOT_11", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_C_AND_Z", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_Z_AND_C", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_11", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_C_EQ_Z", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_Z_EQ_C", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_SAME", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_Z", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_E", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_X1", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_NC_OR_Z", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_Z_OR_NC", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_NOT_10", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_C", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_LT", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_B", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_1X", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_C_OR_NZ", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_NZ_OR_C", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_NOT_01", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_C_OR_Z", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_Z_OR_C", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_LE", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_BE", TokenId.PASM_CONDITION);
        pasmKeywords.put("IF_NOT_00", TokenId.PASM_CONDITION);

        pasmKeywords.put("_CLR", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_NC_AND_Z", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_Z_AND_NC", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_GT", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_NC_AND_Z", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_Z_AND_NC", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_NC", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_GE", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_C_AND_NZ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_NZ_AND_C", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_NZ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_NE", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_C_NE_NZ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_NZ_NE_C", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_C_OR_NZ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_NZ_OR_C", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_C_AND_Z", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_Z_AND_C", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_C_EQ_Z", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_Z_EQ_C", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_Z", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_E", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_NC_OR_Z", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_Z_OR_NC", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_C", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_LT", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_C_OR_NZ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_NZ_OR_C", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_C_OR_Z", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_Z_OR_C", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_LE", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_SET", TokenId.PASM_INSTRUCTION);
    }

    public static class TokenMarker implements Comparable<TokenMarker> {

        int start;
        int stop;
        TokenId id;

        public TokenMarker(Token token, TokenId id) {
            this.start = token.getStartIndex();
            this.stop = token.getStopIndex();
            this.id = id;
        }

        public TokenMarker(Token startToken, Token stopToken, TokenId id) {
            this.start = startToken.getStartIndex();
            this.stop = stopToken.getStopIndex();
            this.id = id;
        }

        public TokenMarker(int start, int stop, TokenId id) {
            this.start = start;
            this.stop = stop;
            this.id = id;
        }

        public TokenMarker(int start) {
            this.start = start;
            this.stop = start;
        }

        public int getStart() {
            return start;
        }

        public int getStop() {
            return stop;
        }

        public TokenId getId() {
            return id;
        }

        @Override
        public int compareTo(TokenMarker o) {
            return Integer.compare(start, o.start);
        }

    }

    TreeSet<TokenMarker> tokens = new TreeSet<TokenMarker>();

    Map<String, TokenId> symbols = new HashMap<String, TokenId>();

    final Spin2ParserBaseListener parserListener = new Spin2ParserBaseListener() {

        @Override
        public void enterConstantsSection(ConstantsSectionContext ctx) {
            tokens.add(new TokenMarker(ctx.getStart(), TokenId.SECTION));
        }

        @Override
        public void exitConstantAssign(ConstantAssignContext ctx) {
            if (ctx.name != null) {
                if (!symbols.containsKey(ctx.name.getText())) {
                    symbols.put(ctx.name.getText(), TokenId.CONSTANT);
                    tokens.add(new TokenMarker(ctx.name, TokenId.CONSTANT));
                }
                else {
                    tokens.add(new TokenMarker(ctx.name, TokenId.ERROR));
                }
            }
        }

        @Override
        public void enterVariablesSection(VariablesSectionContext ctx) {
            tokens.add(new TokenMarker(ctx.getStart(), TokenId.SECTION));
        }

        @Override
        public void exitVariable(VariableContext ctx) {
            if (ctx.name != null) {
                if (!symbols.containsKey(ctx.name.getText())) {
                    symbols.put(ctx.name.getText(), TokenId.VARIABLE);
                    tokens.add(new TokenMarker(ctx.name, TokenId.VARIABLE));
                }
                else {
                    tokens.add(new TokenMarker(ctx.name, TokenId.ERROR));
                }
            }
        }

        @Override
        public void enterObjectsSection(ObjectsSectionContext ctx) {
            tokens.add(new TokenMarker(ctx.getStart(), TokenId.SECTION));
        }

        @Override
        public void exitObject(ObjectContext ctx) {
            if (ctx.name != null) {
                if (!symbols.containsKey(ctx.name.getText())) {
                    symbols.put(ctx.name.getText(), TokenId.OBJECT);
                    tokens.add(new TokenMarker(ctx.name, TokenId.OBJECT));
                }
                else {
                    tokens.add(new TokenMarker(ctx.name, TokenId.ERROR));
                }
            }
        }

        @Override
        public void exitMethod(MethodContext ctx) {
            tokens.add(new TokenMarker(ctx.getStart(), TokenId.SECTION));
            if (ctx.name != null) {
                if (symbols.containsKey(ctx.name.getText())) {
                    tokens.add(new TokenMarker(ctx.name, TokenId.ERROR));
                }
                else if (ctx.PRI_START() != null) {
                    symbols.put(ctx.name.getText(), TokenId.METHOD_PRI);
                    tokens.add(new TokenMarker(ctx.name, TokenId.METHOD_PRI));
                }
                else {
                    symbols.put(ctx.name.getText(), TokenId.METHOD_PUB);
                    tokens.add(new TokenMarker(ctx.name, TokenId.METHOD_PUB));
                }
            }
        }

        @Override
        public void enterData(DataContext ctx) {
            tokens.add(new TokenMarker(ctx.getStart(), TokenId.SECTION));
        }

        String lastPasmLabel = "";

        @Override
        public void exitDataLine(DataLineContext ctx) {
            if (ctx.label() != null) {
                LabelContext labelCtx = ctx.label();

                String text = labelCtx.getText();
                if (text.startsWith(".")) {
                    text = lastPasmLabel + text;
                }
                else {
                    lastPasmLabel = text;
                }

                if (!symbols.containsKey(text)) {
                    symbols.put(text, TokenId.PASM_LABEL);
                    tokens.add(new TokenMarker(labelCtx.getStart(), TokenId.PASM_LABEL));
                    tokens.add(new TokenMarker(labelCtx.getStop(), TokenId.PASM_LABEL));
                }
                else {
                    tokens.add(new TokenMarker(labelCtx.getStart(), TokenId.ERROR));
                    tokens.add(new TokenMarker(labelCtx.getStop(), TokenId.ERROR));
                }
            }
            if (ctx.condition != null) {
                TokenId id = pasmKeywords.get(ctx.condition.getText().toUpperCase());
                if (id != TokenId.PASM_CONDITION) {
                    id = TokenId.ERROR;
                }
                tokens.add(new TokenMarker(ctx.condition, id));
            }
            if (ctx.directive != null) {
                TokenId id = pasmKeywords.get(ctx.directive.getText().toUpperCase());
                if (id != TokenId.PASM_INSTRUCTION) {
                    id = TokenId.ERROR;
                }
                tokens.add(new TokenMarker(ctx.directive, id));
            }
            if (ctx.opcode() != null) {
                OpcodeContext opcodeCtx = ctx.opcode();
                TokenId id = pasmKeywords.get(opcodeCtx.getText().toUpperCase());
                if (id != TokenId.PASM_INSTRUCTION) {
                    id = TokenId.ERROR;
                }
                tokens.add(new TokenMarker(opcodeCtx.getStart(), id));
            }
            if (ctx.modifier != null) {
                TokenId id = pasmKeywords.get(ctx.modifier.getText().toUpperCase());
                if (id != TokenId.PASM_MODIFIER) {
                    id = TokenId.ERROR;
                }
                tokens.add(new TokenMarker(ctx.modifier, id));
            }
        }

        @Override
        public void visitTerminal(TerminalNode node) {
            TokenId id = typeMap.get(node.getSymbol().getType());
            if (id != null) {
                tokens.add(new TokenMarker(node.getSymbol(), id));
            }
        }

        @Override
        public void visitErrorNode(ErrorNode node) {
            tokens.add(new TokenMarker(node.getSymbol(), TokenId.WARNING));
        }

    };

    final Spin2ParserBaseVisitor parserVisitor = new Spin2ParserBaseVisitor() {

        @Override
        public Object visitMethod(MethodContext ctx) {
            ctx.accept(new Spin2ParserBaseVisitor() {

                Map<String, TokenId> locals = new HashMap<String, TokenId>();

                @Override
                public Object visitParameters(ParametersContext ctx) {
                    for (TerminalNode node : ctx.IDENTIFIER()) {
                        String text = node.getSymbol().getText();
                        if (!symbols.containsKey(text) && !locals.containsKey(text)) {
                            locals.put(text, TokenId.METHOD_PARAMETER);
                            tokens.add(new TokenMarker(node.getSymbol(), TokenId.METHOD_PARAMETER));
                        }
                        else {
                            tokens.add(new TokenMarker(node.getSymbol(), TokenId.ERROR));
                        }
                    }
                    return null;
                }

                @Override
                public Object visitResult(ResultContext ctx) {
                    for (TerminalNode node : ctx.IDENTIFIER()) {
                        String text = node.getSymbol().getText();
                        if (!symbols.containsKey(text) && !locals.containsKey(text)) {
                            locals.put(text, TokenId.METHOD_RETURN);
                            tokens.add(new TokenMarker(node.getSymbol(), TokenId.METHOD_RETURN));
                        }
                        else {
                            tokens.add(new TokenMarker(node.getSymbol(), TokenId.ERROR));
                        }
                    }
                    return null;
                }

                @Override
                public Object visitLocalvar(LocalvarContext ctx) {
                    if (ctx.name != null) {
                        String text = ctx.name.getText();
                        if (!symbols.containsKey(text) && !locals.containsKey(text)) {
                            locals.put(ctx.name.getText(), TokenId.METHOD_LOCAL);
                            tokens.add(new TokenMarker(ctx.name, TokenId.METHOD_LOCAL));
                        }
                        else {
                            tokens.add(new TokenMarker(ctx.name, TokenId.ERROR));
                        }
                    }
                    return null;
                }

                @Override
                public Object visitIdentifier(IdentifierContext ctx) {
                    if (ctx.name != null) {
                        TokenId id = symbols.get(ctx.name.getText());
                        if (id == null) {
                            id = locals.get(ctx.name.getText());
                        }
                        if (id == null) {
                            id = TokenId.ERROR;
                        }
                        tokens.add(new TokenMarker(ctx.name, id));
                    }
                    return null;
                }

                @Override
                public Object visitFunction(FunctionContext ctx) {
                    if (ctx.obj != null) {
                        TokenId id = symbols.get(ctx.obj.getText());
                        if (id == null) {
                            id = TokenId.ERROR;
                        }
                        tokens.add(new TokenMarker(ctx.obj, id));
                    }
                    if (ctx.name != null) {
                        TokenId id = keywords.get(ctx.name.getText().toUpperCase());
                        if (id == null) {
                            id = symbols.get(ctx.name.getText());
                        }
                        if (id == null) {
                            id = TokenId.ERROR;
                        }
                        tokens.add(new TokenMarker(ctx.name, id));
                    }
                    return super.visitFunction(ctx);
                }

            });
            return null;
        }

        @Override
        public Object visitIdentifier(IdentifierContext ctx) {
            if (ctx.name != null) {
                TokenId id = symbols.get(ctx.name.getText());
                if (id == null) {
                    id = TokenId.ERROR;
                }
                tokens.add(new TokenMarker(ctx.name, id));
            }
            return null;
        }

        String lastPasmLabel = "";

        @Override
        public Object visitDataLine(DataLineContext ctx) {
            if (ctx.label() != null) {
                String text = ctx.label().getText();
                if (text.startsWith(".")) {
                    text = lastPasmLabel + text;
                }
                else {
                    lastPasmLabel = text;
                }
            }
            return super.visitDataLine(ctx);
        }

        @Override
        public Object visitAtom(AtomContext ctx) {
            if (ctx.IDENTIFIER() != null) {
                String text = ctx.getText();
                if (text.startsWith(".")) {
                    text = lastPasmLabel + text;
                }
                TokenId id = pasmKeywords.get(text.toUpperCase());
                if (id == null) {
                    id = symbols.get(text);
                }
                if (id == null) {
                    id = TokenId.ERROR;
                }
                tokens.add(new TokenMarker(ctx.getStart(), id));
                tokens.add(new TokenMarker(ctx.getStop(), id));
            }
            return null;
        }

    };

    public Spin2TokenMarker() {

    }

    public void refreshTokens(String text) {
        tokens.clear();
        symbols.clear();

        Spin2Lexer lexer = new Spin2Lexer(CharStreams.fromString(text));
        lexer.removeErrorListeners();

        CommonTokenStream stream = new CommonTokenStream(lexer);
        Spin2Parser parser = new Spin2Parser(stream);
        parser.removeErrorListeners();
        parser.addParseListener(parserListener);

        ProgContext context = parser.prog();
        context.accept(parserVisitor);

        List<Token> list = stream.getTokens(0, stream.size() - 1, commentSet);
        if (list != null) {
            for (Token token : list) {
                tokens.add(new TokenMarker(token, TokenId.COMMENT));
            }
        }
    }

    public Set<TokenMarker> getLineTokens(int lineStart, String lineText) {
        return getLineTokens(lineStart, lineStart + lineText.length());
    }

    public Set<TokenMarker> getLineTokens(int lineStart, int lineStop) {
        Set<TokenMarker> result = new TreeSet<TokenMarker>();

        TokenMarker firstMarker = tokens.floor(new TokenMarker(lineStart, lineStop, null));
        if (firstMarker == null) {
            return new TreeSet<TokenMarker>();
        }

        for (TokenMarker entry : tokens.tailSet(firstMarker)) {
            int start = entry.getStart();
            int stop = entry.getStop();
            if ((lineStart >= start && lineStart <= stop) || (lineStop >= start && lineStop <= stop)) {
                result.add(entry);
            }
            else if (stop >= lineStart && stop <= lineStop) {
                result.add(entry);
            }
            if (start >= lineStop) {
                break;
            }
        }

        return result;
    }

}
