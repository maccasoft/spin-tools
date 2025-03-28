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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.spin2.instructions.*;
import com.maccasoft.propeller.spin2.instructions.Byte;
import com.maccasoft.propeller.spin2.instructions.Long;

public abstract class Spin2PAsmInstructionFactory extends Expression {

    static Map<String, Spin2PAsmInstructionFactory> symbols = new HashMap<String, Spin2PAsmInstructionFactory>();
    static {
        symbols.put("ASMCLK", new AsmClk());

        symbols.put("ORG", new Org());
        symbols.put("ORGH", new Orgh());
        symbols.put("ORGF", new Orgf());
        symbols.put("FIT", new Fit());
        symbols.put("RES", new Res());
        symbols.put("BYTE", new Byte());
        symbols.put("WORD", new Word());
        symbols.put("LONG", new Long());
        symbols.put("ALIGNW", new Alignw());
        symbols.put("ALIGNL", new Alignl());
        symbols.put("BYTEFIT", new Bytefit());
        symbols.put("WORDFIT", new Wordfit());

        symbols.put("NAMESP", new Empty());
        symbols.put("FILE", new Empty());
        symbols.put("INCLUDE", new Empty());
        symbols.put("DEBUG", new Debug());
        symbols.put("DITTO", new Empty());

        // Instructions
        symbols.put("NOP", new Nop());
        symbols.put("ROR", new Ror());
        symbols.put("ROL", new Rol());
        symbols.put("SHR", new Shr());
        symbols.put("SHL", new Shl());
        symbols.put("RCR", new Rcr());
        symbols.put("RCL", new Rcl());
        symbols.put("SAR", new Sar());
        symbols.put("SAL", new Sal());
        symbols.put("ADD", new Add());
        symbols.put("ADDX", new Addx());
        symbols.put("ADDS", new Adds());
        symbols.put("ADDSX", new Addsx());
        symbols.put("SUB", new Sub());
        symbols.put("SUBX", new Subx());
        symbols.put("SUBS", new Subs());
        symbols.put("SUBSX", new Subsx());
        symbols.put("CMP", new Cmp());
        symbols.put("CMPX", new Cmpx());
        symbols.put("CMPS", new Cmps());
        symbols.put("CMPSX", new Cmpsx());
        symbols.put("CMPR", new Cmpr());
        symbols.put("CMPM", new Cmpm());
        symbols.put("SUBR", new Subr());
        symbols.put("CMPSUB", new Cmpsub());
        symbols.put("FGE", new Fge());
        symbols.put("FLE", new Fle());
        symbols.put("FGES", new Fges());
        symbols.put("FLES", new Fles());
        symbols.put("SUMC", new Sumc());
        symbols.put("SUMNC", new Sumnc());
        symbols.put("SUMZ", new Sumz());
        symbols.put("SUMNZ", new Sumnz());
        symbols.put("TESTB", new Testb());
        symbols.put("TESTBN", new Testbn());
        symbols.put("BITL", new Bitl());
        symbols.put("BITH", new Bith());
        symbols.put("BITC", new Bitc());
        symbols.put("BITNC", new Bitnc());
        symbols.put("BITZ", new Bitz());
        symbols.put("BITNZ", new Bitnz());
        symbols.put("BITRND", new Bitrnd());
        symbols.put("BITNOT", new Bitnot());
        symbols.put("AND", new And());
        symbols.put("ANDN", new Andn());
        symbols.put("OR", new Or());
        symbols.put("XOR", new Xor());
        symbols.put("MUXC", new Muxc());
        symbols.put("MUXNC", new Muxnc());
        symbols.put("MUXZ", new Muxz());
        symbols.put("MUXNZ", new Muxnz());
        symbols.put("MOV", new Mov());
        symbols.put("NOT", new Not());
        symbols.put("ABS", new Abs());
        symbols.put("NEG", new Neg());
        symbols.put("NEGC", new Negc());
        symbols.put("NEGNC", new Negnc());
        symbols.put("NEGZ", new Negz());
        symbols.put("NEGNZ", new Negnz());
        symbols.put("INCMOD", new Incmod());
        symbols.put("DECMOD", new Decmod());
        symbols.put("ZEROX", new Zerox());
        symbols.put("SIGNX", new Signx());
        symbols.put("ENCOD", new Encod());
        symbols.put("ONES", new Ones());
        symbols.put("TEST", new Test());
        symbols.put("TESTN", new Testn());
        symbols.put("SETNIB", new Setnib());
        symbols.put("GETNIB", new Getnib());
        symbols.put("ROLNIB", new Rolnib());
        symbols.put("SETBYTE", new Setbyte());
        symbols.put("GETBYTE", new Getbyte());
        symbols.put("ROLBYTE", new Rolbyte());
        symbols.put("SETWORD", new Setword());
        symbols.put("GETWORD", new Getword());
        symbols.put("ROLWORD", new Rolword());
        symbols.put("ALTSN", new Altsn());
        symbols.put("ALTGN", new Altgn());
        symbols.put("ALTSB", new Altsb());
        symbols.put("ALTGB", new Altgb());
        symbols.put("ALTSW", new Altsw());
        symbols.put("ALTGW", new Altgw());
        symbols.put("ALTR", new Altr());
        symbols.put("ALTD", new Altd());
        symbols.put("ALTS", new Alts());
        symbols.put("ALTB", new Altb());
        symbols.put("ALTI", new Alti());
        symbols.put("SETR", new Setr());
        symbols.put("SETD", new Setd());
        symbols.put("SETS", new Sets());
        symbols.put("DECOD", new Decod());
        symbols.put("BMASK", new Bmask());
        symbols.put("CRCBIT", new Crcbit());
        symbols.put("CRCNIB", new Crcnib());
        symbols.put("MUXNITS", new Muxnits());
        symbols.put("MUXNIBS", new Muxnibs());
        symbols.put("MUXQ", new Muxq());
        symbols.put("MOVBYTS", new Movbyts());
        symbols.put("MUL", new Mul());
        symbols.put("MULS", new Muls());
        symbols.put("SCA", new Sca());
        symbols.put("SCAS", new Scas());
        symbols.put("ADDPIX", new Addpix());
        symbols.put("MULPIX", new Mulpix());
        symbols.put("BLNPIX", new Blnpix());
        symbols.put("MIXPIX", new Mixpix());
        symbols.put("ADDCT1", new Addct1());
        symbols.put("ADDCT2", new Addct2());
        symbols.put("ADDCT3", new Addct3());
        symbols.put("WMLONG", new Wmlong());
        symbols.put("RQPIN", new Rqpin());
        symbols.put("RDPIN", new Rdpin());
        symbols.put("RDLUT", new Rdlut());
        symbols.put("RDBYTE", new Rdbyte());
        symbols.put("RDWORD", new Rdword());
        symbols.put("RDLONG", new Rdlong());
        symbols.put("POPA", new Popa());
        symbols.put("POPB", new Popb());
        symbols.put("CALLD", new Calld());
        symbols.put("RESI3", new Resi3());
        symbols.put("RESI2", new Resi2());
        symbols.put("RESI1", new Resi1());
        symbols.put("RESI0", new Resi0());
        symbols.put("RETI3", new Reti3());
        symbols.put("RETI2", new Reti2());
        symbols.put("RETI1", new Reti1());
        symbols.put("RETI0", new Reti0());
        symbols.put("CALLPA", new Callpa());
        symbols.put("CALLPB", new Callpb());
        symbols.put("DJZ", new Djz());
        symbols.put("DJNZ", new Djnz());
        symbols.put("DJF", new Djf());
        symbols.put("DJNF", new Djnf());
        symbols.put("IJZ", new Ijz());
        symbols.put("IJNZ", new Ijnz());
        symbols.put("TJZ", new Tjz());
        symbols.put("TJNZ", new Tjnz());
        symbols.put("TJF", new Tjf());
        symbols.put("TJNF", new Tjnf());
        symbols.put("TJS", new Tjs());
        symbols.put("TJNS", new Tjns());
        symbols.put("TJV", new Tjv());
        symbols.put("JINT", new Jint());
        symbols.put("JCT1", new Jct1());
        symbols.put("JCT2", new Jct2());
        symbols.put("JCT3", new Jct3());
        symbols.put("JSE1", new Jse1());
        symbols.put("JSE2", new Jse2());
        symbols.put("JSE3", new Jse3());
        symbols.put("JSE4", new Jse4());
        symbols.put("JPAT", new Jpat());
        symbols.put("JFBW", new Jfbw());
        symbols.put("JXMT", new Jxmt());
        symbols.put("JXFI", new Jxfi());
        symbols.put("JXRO", new Jxro());
        symbols.put("JXRL", new Jxrl());
        symbols.put("JATN", new Jatn());
        symbols.put("JQMT", new Jqmt());
        symbols.put("JNINT", new Jnint());
        symbols.put("JNCT1", new Jnct1());
        symbols.put("JNCT2", new Jnct2());
        symbols.put("JNCT3", new Jnct3());
        symbols.put("JNSE1", new Jnse1());
        symbols.put("JNSE2", new Jnse2());
        symbols.put("JNSE3", new Jnse3());
        symbols.put("JNSE4", new Jnse4());
        symbols.put("JNPAT", new Jnpat());
        symbols.put("JNFBW", new Jnfbw());
        symbols.put("JNXMT", new Jnxmt());
        symbols.put("JNXFI", new Jnxfi());
        symbols.put("JNXRO", new Jnxro());
        symbols.put("JNXRL", new Jnxrl());
        symbols.put("JNATN", new Jnatn());
        symbols.put("JNQMT", new Jnqmt());
        symbols.put("SETPAT", new Setpat());
        symbols.put("AKPIN", new Akpin());
        symbols.put("WRPIN", new Wrpin());
        symbols.put("WXPIN", new Wxpin());
        symbols.put("WYPIN", new Wypin());
        symbols.put("WRLUT", new Wrlut());
        symbols.put("WRBYTE", new Wrbyte());
        symbols.put("WRWORD", new Wrword());
        symbols.put("WRLONG", new Wrlong());
        symbols.put("PUSHA", new Pusha());
        symbols.put("PUSHB", new Pushb());
        symbols.put("RDFAST", new Rdfast());
        symbols.put("WRFAST", new Wrfast());
        symbols.put("FBLOCK", new Fblock());
        symbols.put("XINIT", new Xinit());
        symbols.put("XSTOP", new Xstop());
        symbols.put("XZERO", new Xzero());
        symbols.put("XCONT", new Xcont());
        symbols.put("REP", new Rep());
        symbols.put("COGINIT", new Coginit());
        symbols.put("QMUL", new Qmul());
        symbols.put("QDIV", new Qdiv());
        symbols.put("QFRAC", new Qfrac());
        symbols.put("QSQRT", new Qsqrt());
        symbols.put("QROTATE", new Qrotate());
        symbols.put("QVECTOR", new Qvector());
        symbols.put("HUBSET", new Hubset());
        symbols.put("COGID", new Cogid());
        symbols.put("COGSTOP", new Cogstop());
        symbols.put("LOCKNEW", new Locknew());
        symbols.put("LOCKRET", new Lockret());
        symbols.put("LOCKTRY", new Locktry());
        symbols.put("LOCKREL", new Lockrel());
        symbols.put("QLOG", new Qlog());
        symbols.put("QEXP", new Qexp());
        symbols.put("RFBYTE", new Rfbyte());
        symbols.put("RFWORD", new Rfword());
        symbols.put("RFLONG", new Rflong());
        symbols.put("RFVAR", new Rfvar());
        symbols.put("RFVARS", new Rfvars());
        symbols.put("WFBYTE", new Wfbyte());
        symbols.put("WFWORD", new Wfword());
        symbols.put("WFLONG", new Wflong());
        symbols.put("GETQX", new Getqx());
        symbols.put("GETQY", new Getqy());
        symbols.put("GETCT", new Getct());
        symbols.put("GETRND", new Getrnd());
        symbols.put("SETDACS", new Setdacs());
        symbols.put("SETXFRQ", new Setxfrq());
        symbols.put("GETXACC", new Getxacc());
        symbols.put("WAITX", new Waitx());
        symbols.put("SETSE1", new Setse1());
        symbols.put("SETSE2", new Setse2());
        symbols.put("SETSE3", new Setse3());
        symbols.put("SETSE4", new Setse4());
        symbols.put("POLLINT", new Pollint());
        symbols.put("POLLCT1", new Pollct1());
        symbols.put("POLLCT2", new Pollct2());
        symbols.put("POLLCT3", new Pollct3());
        symbols.put("POLLSE1", new Pollse1());
        symbols.put("POLLSE2", new Pollse2());
        symbols.put("POLLSE3", new Pollse3());
        symbols.put("POLLSE4", new Pollse4());
        symbols.put("POLLPAT", new Pollpat());
        symbols.put("POLLFBW", new Pollfbw());
        symbols.put("POLLXMT", new Pollxmt());
        symbols.put("POLLXFI", new Pollxfi());
        symbols.put("POLLXRO", new Pollxro());
        symbols.put("POLLXRL", new Pollxrl());
        symbols.put("POLLATN", new Pollatn());
        symbols.put("POLLQMT", new Pollqmt());
        symbols.put("WAITINT", new Waitint());
        symbols.put("WAITCT1", new Waitct1());
        symbols.put("WAITCT2", new Waitct2());
        symbols.put("WAITCT3", new Waitct3());
        symbols.put("WAITSE1", new Waitse1());
        symbols.put("WAITSE2", new Waitse2());
        symbols.put("WAITSE3", new Waitse3());
        symbols.put("WAITSE4", new Waitse4());
        symbols.put("WAITPAT", new Waitpat());
        symbols.put("WAITFBW", new Waitfbw());
        symbols.put("WAITXMT", new Waitxmt());
        symbols.put("WAITXFI", new Waitxfi());
        symbols.put("WAITXRO", new Waitxro());
        symbols.put("WAITXRL", new Waitxrl());
        symbols.put("WAITATN", new Waitatn());
        symbols.put("ALLOWI", new Allowi());
        symbols.put("STALLI", new Stalli());
        symbols.put("TRGINT1", new Trgint1());
        symbols.put("TRGINT2", new Trgint2());
        symbols.put("TRGINT3", new Trgint3());
        symbols.put("NIXINT1", new Nixint1());
        symbols.put("NIXINT2", new Nixint2());
        symbols.put("NIXINT3", new Nixint3());
        symbols.put("SETINT1", new Setint1());
        symbols.put("SETINT2", new Setint2());
        symbols.put("SETINT3", new Setint3());
        symbols.put("SETQ", new Setq());
        symbols.put("SETQ2", new Setq2());
        symbols.put("PUSH", new Push());
        symbols.put("POP", new Pop());
        symbols.put("JMP", new Jmp());
        symbols.put("CALL", new Call());
        symbols.put("RET", new Ret());
        symbols.put("CALLA", new Calla());
        symbols.put("RETA", new Reta());
        symbols.put("CALLB", new Callb());
        symbols.put("RETB", new Retb());
        symbols.put("JMPREL", new Jmprel());
        symbols.put("SKIP", new Skip());
        symbols.put("SKIPF", new Skipf());
        symbols.put("EXECF", new Execf());
        symbols.put("GETPTR", new Getptr());
        symbols.put("GETBRK", new Getbrk());
        symbols.put("COGBRK", new Cogbrk());
        symbols.put("BRK", new Brk());
        symbols.put("SETLUTS", new Setluts());
        symbols.put("SETCY", new Setcy());
        symbols.put("SETCI", new Setci());
        symbols.put("SETCQ", new Setcq());
        symbols.put("SETCFRQ", new Setcfrq());
        symbols.put("SETCMOD", new Setcmod());
        symbols.put("SETPIV", new Setpiv());
        symbols.put("SETPIX", new Setpix());
        symbols.put("COGATN", new Cogatn());
        symbols.put("TESTP", new Testp());
        symbols.put("TESTPN", new Testpn());
        symbols.put("DIRL", new Dirl());
        symbols.put("DIRH", new Dirh());
        symbols.put("DIRC", new Dirc());
        symbols.put("DIRNC", new Dirnc());
        symbols.put("DIRZ", new Dirz());
        symbols.put("DIRNZ", new Dirnz());
        symbols.put("DIRRND", new Dirrnd());
        symbols.put("DIRNOT", new Dirnot());
        symbols.put("OUTL", new Outl());
        symbols.put("OUTH", new Outh());
        symbols.put("OUTC", new Outc());
        symbols.put("OUTNC", new Outnc());
        symbols.put("OUTZ", new Outz());
        symbols.put("OUTNZ", new Outnz());
        symbols.put("OUTRND", new Outrnd());
        symbols.put("OUTNOT", new Outnot());
        symbols.put("FLTL", new Fltl());
        symbols.put("FLTH", new Flth());
        symbols.put("FLTC", new Fltc());
        symbols.put("FLTNC", new Fltnc());
        symbols.put("FLTZ", new Fltz());
        symbols.put("FLTNZ", new Fltnz());
        symbols.put("FLTRND", new Fltrnd());
        symbols.put("FLTNOT", new Fltnot());
        symbols.put("DRVL", new Drvl());
        symbols.put("DRVH", new Drvh());
        symbols.put("DRVC", new Drvc());
        symbols.put("DRVNC", new Drvnc());
        symbols.put("DRVZ", new Drvz());
        symbols.put("DRVNZ", new Drvnz());
        symbols.put("DRVRND", new Drvrnd());
        symbols.put("DRVNOT", new Drvnot());
        symbols.put("SPLITB", new Splitb());
        symbols.put("MERGEB", new Mergeb());
        symbols.put("SPLITW", new Splitw());
        symbols.put("MERGEW", new Mergew());
        symbols.put("SEUSSF", new Seussf());
        symbols.put("SEUSSR", new Seussr());
        symbols.put("RGBSQZ", new Rgbsqz());
        symbols.put("RGBEXP", new Rgbexp());
        symbols.put("XORO32", new Xoro32());
        symbols.put("REV", new Rev());
        symbols.put("RCZR", new Rczr());
        symbols.put("RCZL", new Rczl());
        symbols.put("WRC", new Wrc());
        symbols.put("WRNC", new Wrnc());
        symbols.put("WRZ", new Wrz());
        symbols.put("WRNZ", new Wrnz());
        symbols.put("MODCZ", new Modcz());
        symbols.put("MODC", new Modc());
        symbols.put("MODZ", new Modz());
        symbols.put("SETSCP", new Setscp());
        symbols.put("GETSCP", new Getscp());
        symbols.put("LOC", new Loc());
        symbols.put("AUGS", new Augs());
        symbols.put("AUGD", new Augd());
    }

    public static Spin2PAsmInstructionFactory get(String mnemonic) {
        return symbols.get(mnemonic.toUpperCase());
    }

    public Spin2PAsmInstructionFactory() {
    }

    public List<Spin2PAsmLine> expand(Spin2PAsmLine line) {
        return Collections.singletonList(line);
    }

    public abstract Spin2InstructionObject createObject(Context context, String condition, List<Spin2PAsmExpression> arguments, String effect);

}
