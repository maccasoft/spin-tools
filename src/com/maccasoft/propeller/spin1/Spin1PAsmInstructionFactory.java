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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.spin1.instructions.Abs;
import com.maccasoft.propeller.spin1.instructions.Absneg;
import com.maccasoft.propeller.spin1.instructions.Add;
import com.maccasoft.propeller.spin1.instructions.Addabs;
import com.maccasoft.propeller.spin1.instructions.Adds;
import com.maccasoft.propeller.spin1.instructions.Addsx;
import com.maccasoft.propeller.spin1.instructions.Addx;
import com.maccasoft.propeller.spin1.instructions.And;
import com.maccasoft.propeller.spin1.instructions.Andn;
import com.maccasoft.propeller.spin1.instructions.Call;
import com.maccasoft.propeller.spin1.instructions.Clkset;
import com.maccasoft.propeller.spin1.instructions.Cmp;
import com.maccasoft.propeller.spin1.instructions.Cmps;
import com.maccasoft.propeller.spin1.instructions.Cmpsub;
import com.maccasoft.propeller.spin1.instructions.Cmpsx;
import com.maccasoft.propeller.spin1.instructions.Cmpx;
import com.maccasoft.propeller.spin1.instructions.Cogid;
import com.maccasoft.propeller.spin1.instructions.Coginit;
import com.maccasoft.propeller.spin1.instructions.Cogstop;
import com.maccasoft.propeller.spin1.instructions.Djnz;
import com.maccasoft.propeller.spin1.instructions.Enc;
import com.maccasoft.propeller.spin1.instructions.FileInc;
import com.maccasoft.propeller.spin1.instructions.Fit;
import com.maccasoft.propeller.spin1.instructions.Hubop;
import com.maccasoft.propeller.spin1.instructions.Jmp;
import com.maccasoft.propeller.spin1.instructions.Jmpret;
import com.maccasoft.propeller.spin1.instructions.Lockclr;
import com.maccasoft.propeller.spin1.instructions.Locknew;
import com.maccasoft.propeller.spin1.instructions.Lockret;
import com.maccasoft.propeller.spin1.instructions.Lockset;
import com.maccasoft.propeller.spin1.instructions.Max;
import com.maccasoft.propeller.spin1.instructions.Maxs;
import com.maccasoft.propeller.spin1.instructions.Min;
import com.maccasoft.propeller.spin1.instructions.Mins;
import com.maccasoft.propeller.spin1.instructions.Mov;
import com.maccasoft.propeller.spin1.instructions.Movd;
import com.maccasoft.propeller.spin1.instructions.Movi;
import com.maccasoft.propeller.spin1.instructions.Movs;
import com.maccasoft.propeller.spin1.instructions.Mul;
import com.maccasoft.propeller.spin1.instructions.Muls;
import com.maccasoft.propeller.spin1.instructions.Muxc;
import com.maccasoft.propeller.spin1.instructions.Muxnc;
import com.maccasoft.propeller.spin1.instructions.Muxnz;
import com.maccasoft.propeller.spin1.instructions.Muxz;
import com.maccasoft.propeller.spin1.instructions.Neg;
import com.maccasoft.propeller.spin1.instructions.Negc;
import com.maccasoft.propeller.spin1.instructions.Negnc;
import com.maccasoft.propeller.spin1.instructions.Negnz;
import com.maccasoft.propeller.spin1.instructions.Negz;
import com.maccasoft.propeller.spin1.instructions.Nop;
import com.maccasoft.propeller.spin1.instructions.Ones;
import com.maccasoft.propeller.spin1.instructions.Or;
import com.maccasoft.propeller.spin1.instructions.Org;
import com.maccasoft.propeller.spin1.instructions.Rcl;
import com.maccasoft.propeller.spin1.instructions.Rcr;
import com.maccasoft.propeller.spin1.instructions.Rdbyte;
import com.maccasoft.propeller.spin1.instructions.Rdlong;
import com.maccasoft.propeller.spin1.instructions.Rdword;
import com.maccasoft.propeller.spin1.instructions.Res;
import com.maccasoft.propeller.spin1.instructions.Ret;
import com.maccasoft.propeller.spin1.instructions.Rev;
import com.maccasoft.propeller.spin1.instructions.Rol;
import com.maccasoft.propeller.spin1.instructions.Ror;
import com.maccasoft.propeller.spin1.instructions.Sar;
import com.maccasoft.propeller.spin1.instructions.Shl;
import com.maccasoft.propeller.spin1.instructions.Shr;
import com.maccasoft.propeller.spin1.instructions.Sub;
import com.maccasoft.propeller.spin1.instructions.Subabs;
import com.maccasoft.propeller.spin1.instructions.Subs;
import com.maccasoft.propeller.spin1.instructions.Subsx;
import com.maccasoft.propeller.spin1.instructions.Subx;
import com.maccasoft.propeller.spin1.instructions.Sumc;
import com.maccasoft.propeller.spin1.instructions.Sumnc;
import com.maccasoft.propeller.spin1.instructions.Sumnz;
import com.maccasoft.propeller.spin1.instructions.Sumz;
import com.maccasoft.propeller.spin1.instructions.Test;
import com.maccasoft.propeller.spin1.instructions.Testn;
import com.maccasoft.propeller.spin1.instructions.Tjnz;
import com.maccasoft.propeller.spin1.instructions.Tjz;
import com.maccasoft.propeller.spin1.instructions.Waitcnt;
import com.maccasoft.propeller.spin1.instructions.Waitpeq;
import com.maccasoft.propeller.spin1.instructions.Waitpne;
import com.maccasoft.propeller.spin1.instructions.Waitvid;
import com.maccasoft.propeller.spin1.instructions.Word;
import com.maccasoft.propeller.spin1.instructions.Wrbyte;
import com.maccasoft.propeller.spin1.instructions.Wrlong;
import com.maccasoft.propeller.spin1.instructions.Wrword;
import com.maccasoft.propeller.spin1.instructions.Xor;

public abstract class Spin1PAsmInstructionFactory extends Expression {

    static Map<String, Spin1PAsmInstructionFactory> symbols = new HashMap<String, Spin1PAsmInstructionFactory>();
    static {
        symbols.put("ORG", new Org());
        symbols.put("FIT", new Fit());
        symbols.put("RES", new Res());
        symbols.put("BYTE", new com.maccasoft.propeller.spin1.instructions.Byte());
        symbols.put("WORD", new Word());
        symbols.put("LONG", new com.maccasoft.propeller.spin1.instructions.Long());
        symbols.put("FILE", new FileInc());

        symbols.put("ABS", new Abs());
        symbols.put("ABSNEG", new Absneg());
        symbols.put("ADD", new Add());
        symbols.put("ADDABS", new Addabs());
        symbols.put("ADDS", new Adds());
        symbols.put("ADDSX", new Addsx());
        symbols.put("ADDX", new Addx());
        symbols.put("AND", new And());
        symbols.put("ANDN", new Andn());
        symbols.put("CALL", new Call());
        symbols.put("CLKSET", new Clkset());
        symbols.put("CMP", new Cmp());
        symbols.put("CMPS", new Cmps());
        symbols.put("CMPSUB", new Cmpsub());
        symbols.put("CMPSX", new Cmpsx());
        symbols.put("CMPX", new Cmpx());
        symbols.put("COGID", new Cogid());
        symbols.put("COGINIT", new Coginit());
        symbols.put("COGSTOP", new Cogstop());
        symbols.put("DJNZ", new Djnz());
        symbols.put("ENC", new Enc());
        symbols.put("HUBOP", new Hubop());
        symbols.put("JMP", new Jmp());
        symbols.put("JMPRET", new Jmpret());
        symbols.put("LOCKCLR", new Lockclr());
        symbols.put("LOCKNEW", new Locknew());
        symbols.put("LOCKRET", new Lockret());
        symbols.put("LOCKSET", new Lockset());
        symbols.put("MAX", new Max());
        symbols.put("MAXS", new Maxs());
        symbols.put("MIN", new Min());
        symbols.put("MINS", new Mins());
        symbols.put("MOV", new Mov());
        symbols.put("MOVD", new Movd());
        symbols.put("MOVI", new Movi());
        symbols.put("MOVS", new Movs());
        symbols.put("MUL", new Mul());
        symbols.put("MULS", new Muls());
        symbols.put("MUXC", new Muxc());
        symbols.put("MUXNC", new Muxnc());
        symbols.put("MUXNZ", new Muxnz());
        symbols.put("MUXZ", new Muxz());
        symbols.put("NEG", new Neg());
        symbols.put("NEGC", new Negc());
        symbols.put("NEGNC", new Negnc());
        symbols.put("NEGNZ", new Negnz());
        symbols.put("NEGZ", new Negz());
        symbols.put("NOP", new Nop());
        symbols.put("ONES", new Ones());
        symbols.put("OR", new Or());
        symbols.put("RCL", new Rcl());
        symbols.put("RCR", new Rcr());
        symbols.put("RET", new Ret());
        symbols.put("REV", new Rev());
        symbols.put("ROL", new Rol());
        symbols.put("ROR", new Ror());
        symbols.put("RDBYTE", new Rdbyte());
        symbols.put("RDLONG", new Rdlong());
        symbols.put("RDWORD", new Rdword());
        symbols.put("WRBYTE", new Wrbyte());
        symbols.put("WRLONG", new Wrlong());
        symbols.put("WRWORD", new Wrword());
        symbols.put("SAR", new Sar());
        symbols.put("SHL", new Shl());
        symbols.put("SHR", new Shr());
        symbols.put("SUB", new Sub());
        symbols.put("SUBABS", new Subabs());
        symbols.put("SUBS", new Subs());
        symbols.put("SUBSX", new Subsx());
        symbols.put("SUBX", new Subx());
        symbols.put("SUMC", new Sumc());
        symbols.put("SUMNC", new Sumnc());
        symbols.put("SUMNZ", new Sumnz());
        symbols.put("SUMZ", new Sumz());
        symbols.put("TEST", new Test());
        symbols.put("TESTN", new Testn());
        symbols.put("TJNZ", new Tjnz());
        symbols.put("TJZ", new Tjz());
        symbols.put("WAITCNT", new Waitcnt());
        symbols.put("WAITPEQ", new Waitpeq());
        symbols.put("WAITPNE", new Waitpne());
        symbols.put("WAITVID", new Waitvid());
        symbols.put("XOR", new Xor());
    }

    public static Spin1PAsmInstructionFactory get(String mnemonic) {
        return symbols.get(mnemonic.toUpperCase());
    }

    public Spin1PAsmInstructionFactory() {
    }

    public List<Spin1PAsmLine> expand(Spin1PAsmLine line) {
        return Collections.singletonList(line);
    }

    public Spin1InstructionObject createObject(Spin1PAsmLine line) {
        return createObject(line.getScope(), line.getCondition(), line.getArguments(), line.getEffect());
    }

    public Spin1InstructionObject createObject(Spin1Context context, String condition, List<Spin1PAsmExpression> arguments, String modifiers) {
        return null;
    }

}
