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

import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.spin.instructions.AsmClk;
import com.maccasoft.propeller.spin.instructions.Augd;
import com.maccasoft.propeller.spin.instructions.Augs;
import com.maccasoft.propeller.spin.instructions.Branch_D_A;
import com.maccasoft.propeller.spin.instructions.Getct;
import com.maccasoft.propeller.spin.instructions.Hubset;
import com.maccasoft.propeller.spin.instructions.NoArg_E;
import com.maccasoft.propeller.spin.instructions.NoArg_NE;
import com.maccasoft.propeller.spin.instructions.Nop;
import com.maccasoft.propeller.spin.instructions.Org;
import com.maccasoft.propeller.spin.instructions.Pins;
import com.maccasoft.propeller.spin.instructions.Res;
import com.maccasoft.propeller.spin.instructions.Std_E;
import com.maccasoft.propeller.spin.instructions.Std_NE;
import com.maccasoft.propeller.spin.instructions.TriArg;
import com.maccasoft.propeller.spin.instructions.Waitx;

public class Spin2GlobalContext extends Spin2Context {

    public Spin2GlobalContext() {

        addSymbol("asmclk", new AsmClk());

        addSymbol("org", new Org());
        addSymbol("res", new Res());

        addSymbol("nop", new Nop());
        addSymbol("ror", new Std_E(P2Asm.encode(0b0000000, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("rol", new Std_E(P2Asm.encode(0b0000001, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("shr", new Std_E(P2Asm.encode(0b0000010, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("shl", new Std_E(P2Asm.encode(0b0000011, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("rcr", new Std_E(P2Asm.encode(0b0000100, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("rcl", new Std_E(P2Asm.encode(0b0000101, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("sar", new Std_E(P2Asm.encode(0b0000110, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("sal", new Std_E(P2Asm.encode(0b0000111, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("add", new Std_E(P2Asm.encode(0b0001000, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("addx", new Std_E(P2Asm.encode(0b0001001, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("adds", new Std_E(P2Asm.encode(0b0001010, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("addsx", new Std_E(P2Asm.encode(0b0001011, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("sub", new Std_E(P2Asm.encode(0b0001100, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("subx", new Std_E(P2Asm.encode(0b0001101, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("subs", new Std_E(P2Asm.encode(0b0001110, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("subsx", new Std_E(P2Asm.encode(0b0001111, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("cmp", new Std_E(P2Asm.encode(0b0010000, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("cmpx", new Std_E(P2Asm.encode(0b0010001, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("cmps", new Std_E(P2Asm.encode(0b0010010, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("cmpsx", new Std_E(P2Asm.encode(0b0010011, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("cmpr", new Std_E(P2Asm.encode(0b0010100, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("cmpm", new Std_E(P2Asm.encode(0b0010101, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("subr", new Std_E(P2Asm.encode(0b0010110, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("cmpsub", new Std_E(P2Asm.encode(0b0010111, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("fge", new Std_E(P2Asm.encode(0b0011000, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("fle", new Std_E(P2Asm.encode(0b0011001, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("fges", new Std_E(P2Asm.encode(0b0011010, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("fles", new Std_E(P2Asm.encode(0b0011011, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("sumc", new Std_E(P2Asm.encode(0b0011100, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("sumnc", new Std_E(P2Asm.encode(0b0011101, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("sumz", new Std_E(P2Asm.encode(0b0011110, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("sumnz", new Std_E(P2Asm.encode(0b0011111, 0b000, 0b000000000, 0b000000000), false));

        /*
        TESTB   D,{#}S         WC/WZ    Math and Logic          P2Asm.encode(0b0100000, 0b000, 0b000000000, 0b000000000)
        TESTBN  D,{#}S         WC/WZ    Math and Logic          P2Asm.encode(0b0100001, 0b000, 0b000000000, 0b000000000)
        TESTB   D,{#}S     ANDC/ANDZ    Math and Logic          P2Asm.encode(0b0100010, 0b000, 0b000000000, 0b000000000)
        TESTBN  D,{#}S     ANDC/ANDZ    Math and Logic          P2Asm.encode(0b0100011, 0b000, 0b000000000, 0b000000000)
        TESTB   D,{#}S       ORC/ORZ    Math and Logic          P2Asm.encode(0b0100100, 0b000, 0b000000000, 0b000000000)
        TESTBN  D,{#}S       ORC/ORZ    Math and Logic          P2Asm.encode(0b0100101, 0b000, 0b000000000, 0b000000000)
        TESTB   D,{#}S     XORC/XORZ    Math and Logic          P2Asm.encode(0b0100110, 0b000, 0b000000000, 0b000000000)
        TESTBN  D,{#}S     XORC/XORZ    Math and Logic          P2Asm.encode(0b0100111, 0b000, 0b000000000, 0b000000000)
        BITL    D,{#}S         {WCZ}    Math and Logic          P2Asm.encode(0b0100000, 0b000, 0b000000000, 0b000000000)
        BITH    D,{#}S         {WCZ}    Math and Logic          P2Asm.encode(0b0100001, 0b000, 0b000000000, 0b000000000)
        BITC    D,{#}S         {WCZ}    Math and Logic          P2Asm.encode(0b0100010, 0b000, 0b000000000, 0b000000000)
        BITNC   D,{#}S         {WCZ}    Math and Logic          P2Asm.encode(0b0100011, 0b000, 0b000000000, 0b000000000)
        BITZ    D,{#}S         {WCZ}    Math and Logic          P2Asm.encode(0b0100100, 0b000, 0b000000000, 0b000000000)
        BITNZ   D,{#}S         {WCZ}    Math and Logic          P2Asm.encode(0b0100101, 0b000, 0b000000000, 0b000000000)
        BITRND  D,{#}S         {WCZ}    Math and Logic          P2Asm.encode(0b0100110, 0b000, 0b000000000, 0b000000000)
        BITNOT  D,{#}S         {WCZ}    Math and Logic          P2Asm.encode(0b0100111, 0b000, 0b000000000, 0b000000000)
        */

        addSymbol("and", new Std_E(P2Asm.encode(0b0101000, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("andn", new Std_E(P2Asm.encode(0b0101001, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("or", new Std_E(P2Asm.encode(0b0101010, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("xor", new Std_E(P2Asm.encode(0b0101011, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("muxc", new Std_E(P2Asm.encode(0b0101100, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("muxnc", new Std_E(P2Asm.encode(0b0101101, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("muxz", new Std_E(P2Asm.encode(0b0101110, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("muxnz", new Std_E(P2Asm.encode(0b0101111, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("mov", new Std_E(P2Asm.encode(0b0110000, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("not", new Std_E(P2Asm.encode(0b0110001, 0b000, 0b000000000, 0b000000000), true));
        addSymbol("abs", new Std_E(P2Asm.encode(0b0110010, 0b000, 0b000000000, 0b000000000), true));
        addSymbol("neg", new Std_E(P2Asm.encode(0b0110011, 0b000, 0b000000000, 0b000000000), true));
        addSymbol("negc", new Std_E(P2Asm.encode(0b0110100, 0b000, 0b000000000, 0b000000000), true));
        addSymbol("negnc", new Std_E(P2Asm.encode(0b0110101, 0b000, 0b000000000, 0b000000000), true));
        addSymbol("negz", new Std_E(P2Asm.encode(0b0110110, 0b000, 0b000000000, 0b000000000), true));
        addSymbol("negnz", new Std_E(P2Asm.encode(0b0110111, 0b000, 0b000000000, 0b000000000), true));
        addSymbol("incmod", new Std_E(P2Asm.encode(0b0111000, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("decmod", new Std_E(P2Asm.encode(0b0111001, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("zerox", new Std_E(P2Asm.encode(0b0111010, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("signx", new Std_E(P2Asm.encode(0b0111011, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("encod", new Std_E(P2Asm.encode(0b0111100, 0b000, 0b000000000, 0b000000000), true));
        addSymbol("ones", new Std_E(P2Asm.encode(0b0111101, 0b000, 0b000000000, 0b000000000), true));
        addSymbol("test", new Std_E(P2Asm.encode(0b0111110, 0b000, 0b000000000, 0b000000000), true));
        addSymbol("testn", new Std_E(P2Asm.encode(0b0111111, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("setnib", new TriArg(P2Asm.encode(0b1000000, 0b000, 0b000000000, 0b000000000), true));
        addSymbol("getnib", new TriArg(P2Asm.encode(0b1000010, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("rolnib", new TriArg(P2Asm.encode(0b1000100, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("setbyte", new TriArg(P2Asm.encode(0b1000110, 0b000, 0b000000000, 0b000000000), true));
        addSymbol("getbyte", new TriArg(P2Asm.encode(0b1000111, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("rolbyte", new TriArg(P2Asm.encode(0b1001000, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("setword", new TriArg(P2Asm.encode(0b1001001, 0b000, 0b000000000, 0b000000000), true));
        addSymbol("getword", new TriArg(P2Asm.encode(0b1001001, 0b100, 0b000000000, 0b000000000), false));
        addSymbol("rolword", new TriArg(P2Asm.encode(0b1001010, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("altsn", new Std_NE(P2Asm.encode(0b1001010, 0b100, 0b000000000, 0b000000000), true));
        addSymbol("altgn", new Std_NE(P2Asm.encode(0b1001010, 0b110, 0b000000000, 0b000000000), true));
        addSymbol("altsb", new Std_NE(P2Asm.encode(0b1001011, 0b000, 0b000000000, 0b000000000), true));
        addSymbol("altgb", new Std_NE(P2Asm.encode(0b1001011, 0b010, 0b000000000, 0b000000000), true));
        addSymbol("altsw", new Std_NE(P2Asm.encode(0b1001011, 0b100, 0b000000000, 0b000000000), true));
        addSymbol("altgw", new Std_NE(P2Asm.encode(0b1001011, 0b110, 0b000000000, 0b000000000), true));
        addSymbol("altr", new Std_NE(P2Asm.encode(0b1001100, 0b000, 0b000000000, 0b000000000), true));
        addSymbol("altd", new Std_NE(P2Asm.encode(0b1001100, 0b010, 0b000000000, 0b000000000), true));
        addSymbol("alts", new Std_NE(P2Asm.encode(0b1001100, 0b100, 0b000000000, 0b000000000), true));
        addSymbol("altb", new Std_NE(P2Asm.encode(0b1001100, 0b110, 0b000000000, 0b000000000), true));
        addSymbol("alti", new Std_NE(P2Asm.encode(0b1001101, 0b000, 0b000000000, 0b000000000), true));
        addSymbol("setr", new Std_NE(P2Asm.encode(0b1001101, 0b010, 0b000000000, 0b000000000), false));
        addSymbol("setd", new Std_NE(P2Asm.encode(0b1001101, 0b100, 0b000000000, 0b000000000), false));
        addSymbol("sets", new Std_NE(P2Asm.encode(0b1001101, 0b110, 0b000000000, 0b000000000), false));
        addSymbol("decod", new Std_NE(P2Asm.encode(0b1001110, 0b000, 0b000000000, 0b000000000), true));
        addSymbol("bmask", new Std_NE(P2Asm.encode(0b1001110, 0b010, 0b000000000, 0b000000000), true));
        addSymbol("crcbit", new Std_NE(P2Asm.encode(0b1001110, 0b100, 0b000000000, 0b000000000), false));
        addSymbol("crcnib", new Std_NE(P2Asm.encode(0b1001110, 0b110, 0b000000000, 0b000000000), false));
        addSymbol("muxnits", new Std_NE(P2Asm.encode(0b1001111, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("muxnibs", new Std_NE(P2Asm.encode(0b1001111, 0b010, 0b000000000, 0b000000000), false));
        addSymbol("muxq", new Std_NE(P2Asm.encode(0b1001111, 0b100, 0b000000000, 0b000000000), false));
        addSymbol("movbyts", new Std_NE(P2Asm.encode(0b1001111, 0b110, 0b000000000, 0b000000000), false));

        /*
        MUL     D,{#}S          {WZ}    Math and Logic          P2Asm.encode(0b1010000, 0b000, 0b000000000, 0b000000000)
        MULS    D,{#}S          {WZ}    Math and Logic          P2Asm.encode(0b1010000, 0b100, 0b000000000, 0b000000000)
        SCA     D,{#}S          {WZ}    Math and Logic          P2Asm.encode(0b1010001, 0b000, 0b000000000, 0b000000000)
        SCAS    D,{#}S          {WZ}    Math and Logic          P2Asm.encode(0b1010001, 0b100, 0b000000000, 0b000000000)
        */

        addSymbol("addpix", new Std_NE(P2Asm.encode(0b1010010, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("mulpix", new Std_NE(P2Asm.encode(0b1010010, 0b010, 0b000000000, 0b000000000), false));
        addSymbol("blnpix", new Std_NE(P2Asm.encode(0b1010010, 0b100, 0b000000000, 0b000000000), false));
        addSymbol("mixpix", new Std_NE(P2Asm.encode(0b1010010, 0b110, 0b000000000, 0b000000000), false));
        addSymbol("addct1", new Std_NE(P2Asm.encode(0b1010011, 0b000, 0b000000000, 0b000000000), false));
        addSymbol("addct2", new Std_NE(P2Asm.encode(0b1010011, 0b010, 0b000000000, 0b000000000), false));
        addSymbol("addct3", new Std_NE(P2Asm.encode(0b1010011, 0b100, 0b000000000, 0b000000000), false));

        /*
        WMLONG  D,{#}S/P                Hub RAM - Write         P2Asm.encode(0b1010011, 0b110, 0b000000000, 0b000000000)
        RQPIN   D,{#}S          {WC}    Smart Pins              P2Asm.encode(0b1010100, 0b000, 0b000000000, 0b000000000)
        RDPIN   D,{#}S          {WC}    Smart Pins              P2Asm.encode(0b1010100, 0b010, 0b000000000, 0b000000000)
        RDLUT   D,{#}S/P {WC/WZ/WCZ}    Lookup Table            P2Asm.encode(0b1010101, 0b000, 0b000000000, 0b000000000)
        RDBYTE  D,{#}S/P {WC/WZ/WCZ}    Hub RAM - Read          P2Asm.encode(0b1010110, 0b000, 0b000000000, 0b000000000)
        RDWORD  D,{#}S/P {WC/WZ/WCZ}    Hub RAM - Read          P2Asm.encode(0b1010111, 0b000, 0b000000000, 0b000000000)
        RDLONG  D,{#}S/P {WC/WZ/WCZ}    Hub RAM - Read          P2Asm.encode(0b1011000, 0b000, 0b000000000, 0b000000000)
        POPA    D        {WC/WZ/WCZ}    Hub RAM - Read          P2Asm.encode(0b1011000, 0b001, 0b000000000, 0b101011111)
        POPB    D        {WC/WZ/WCZ}    Hub RAM - Read          P2Asm.encode(0b1011000, 0b001, 0b000000000, 0b111011111)
        CALLD   D,{#}S   {WC/WZ/WCZ}    Branch S - Call         P2Asm.encode(0b1011001, 0b000, 0b000000000, 0b000000000)
        */

        addSymbol("resi3", new NoArg_NE(P2Asm.encode(0b1011001, 0b110, 0b111110000, 0b111110001)));
        addSymbol("resi2", new NoArg_NE(P2Asm.encode(0b1011001, 0b110, 0b111110010, 0b111110011)));
        addSymbol("resi1", new NoArg_NE(P2Asm.encode(0b1011001, 0b110, 0b111110100, 0b111110101)));
        addSymbol("resi0", new NoArg_NE(P2Asm.encode(0b1011001, 0b110, 0b111111110, 0b111111111)));
        addSymbol("reti3", new NoArg_NE(P2Asm.encode(0b1011001, 0b110, 0b111111111, 0b111110001)));
        addSymbol("reti2", new NoArg_NE(P2Asm.encode(0b1011001, 0b110, 0b111111111, 0b111110011)));
        addSymbol("reti1", new NoArg_NE(P2Asm.encode(0b1011001, 0b110, 0b111111111, 0b111110101)));
        addSymbol("reti0", new NoArg_NE(P2Asm.encode(0b1011001, 0b110, 0b111111111, 0b111111111)));

        /*
        CALLPA  {#}D,{#}S               Branch S - Call         P2Asm.encode(0b1011010, 0b000, 0b000000000, 0b000000000)
        CALLPB  {#}D,{#}S               Branch S - Call         P2Asm.encode(0b1011010, 0b100, 0b000000000, 0b000000000)
        DJZ     D,{#}S                  Branch S - Mod & Test   P2Asm.encode(0b1011011, 0b000, 0b000000000, 0b000000000)
        DJNZ    D,{#}S                  Branch S - Mod & Test   P2Asm.encode(0b1011011, 0b010, 0b000000000, 0b000000000)
        DJF     D,{#}S                  Branch S - Mod & Test   P2Asm.encode(0b1011011, 0b100, 0b000000000, 0b000000000)
        DJNF    D,{#}S                  Branch S - Mod & Test   P2Asm.encode(0b1011011, 0b110, 0b000000000, 0b000000000)
        IJZ     D,{#}S                  Branch S - Mod & Test   P2Asm.encode(0b1011100, 0b000, 0b000000000, 0b000000000)
        IJNZ    D,{#}S                  Branch S - Mod & Test   P2Asm.encode(0b1011100, 0b010, 0b000000000, 0b000000000)
        TJZ     D,{#}S                  Branch S - Test         P2Asm.encode(0b1011100, 0b100, 0b000000000, 0b000000000)
        TJNZ    D,{#}S                  Branch S - Test         P2Asm.encode(0b1011100, 0b110, 0b000000000, 0b000000000)
        TJF     D,{#}S                  Branch S - Test         P2Asm.encode(0b1011101, 0b000, 0b000000000, 0b000000000)
        TJNF    D,{#}S                  Branch S - Test         P2Asm.encode(0b1011101, 0b010, 0b000000000, 0b000000000)
        TJS     D,{#}S                  Branch S - Test         P2Asm.encode(0b1011101, 0b100, 0b000000000, 0b000000000)
        TJNS    D,{#}S                  Branch S - Test         P2Asm.encode(0b1011101, 0b110, 0b000000000, 0b000000000)
        TJV     D,{#}S                  Branch S - Test         P2Asm.encode(0b1011110, 0b000, 0b000000000, 0b000000000)
        JINT    {#}S                    Events - Branch         P2Asm.encode(0b1011110, 0b010, 0b000000000, 0b000000000)
        JCT1    {#}S                    Events - Branch         P2Asm.encode(0b1011110, 0b010, 0b000000001, 0b000000000)
        JCT2    {#}S                    Events - Branch         P2Asm.encode(0b1011110, 0b010, 0b000000010, 0b000000000)
        JCT3    {#}S                    Events - Branch         P2Asm.encode(0b1011110, 0b010, 0b000000011, 0b000000000)
        JSE1    {#}S                    Events - Branch         P2Asm.encode(0b1011110, 0b010, 0b000000100, 0b000000000)
        JSE2    {#}S                    Events - Branch         P2Asm.encode(0b1011110, 0b010, 0b000000101, 0b000000000)
        JSE3    {#}S                    Events - Branch         P2Asm.encode(0b1011110, 0b010, 0b000000110, 0b000000000)
        JSE4    {#}S                    Events - Branch         P2Asm.encode(0b1011110, 0b010, 0b000000111, 0b000000000)
        JPAT    {#}S                    Events - Branch         P2Asm.encode(0b1011110, 0b010, 0b000001000, 0b000000000)
        JFBW    {#}S                    Events - Branch         P2Asm.encode(0b1011110, 0b010, 0b000001001, 0b000000000)
        JXMT    {#}S                    Events - Branch         P2Asm.encode(0b1011110, 0b010, 0b000001010, 0b000000000)
        JXFI    {#}S                    Events - Branch         P2Asm.encode(0b1011110, 0b010, 0b000001011, 0b000000000)
        JXRO    {#}S                    Events - Branch         P2Asm.encode(0b1011110, 0b010, 0b000001100, 0b000000000)
        JXRL    {#}S                    Events - Branch         P2Asm.encode(0b1011110, 0b010, 0b000001101, 0b000000000)
        JATN    {#}S                    Events - Branch         P2Asm.encode(0b1011110, 0b010, 0b000001110, 0b000000000)
        JQMT    {#}S                    Events - Branch         P2Asm.encode(0b1011110, 0b010, 0b000001111, 0b000000000)
        JNINT   {#}S                    Events - Branch         P2Asm.encode(0b1011110, 0b010, 0b000010000, 0b000000000)
        JNCT1   {#}S                    Events - Branch         P2Asm.encode(0b1011110, 0b010, 0b000010001, 0b000000000)
        JNCT2   {#}S                    Events - Branch         P2Asm.encode(0b1011110, 0b010, 0b000010010, 0b000000000)
        JNCT3   {#}S                    Events - Branch         P2Asm.encode(0b1011110, 0b010, 0b000010011, 0b000000000)
        JNSE1   {#}S                    Events - Branch         P2Asm.encode(0b1011110, 0b010, 0b000010100, 0b000000000)
        JNSE2   {#}S                    Events - Branch         P2Asm.encode(0b1011110, 0b010, 0b000010101, 0b000000000)
        JNSE3   {#}S                    Events - Branch         P2Asm.encode(0b1011110, 0b010, 0b000010110, 0b000000000)
        JNSE4   {#}S                    Events - Branch         P2Asm.encode(0b1011110, 0b010, 0b000010111, 0b000000000)
        JNPAT   {#}S                    Events - Branch         P2Asm.encode(0b1011110, 0b010, 0b000011000, 0b000000000)
        JNFBW   {#}S                    Events - Branch         P2Asm.encode(0b1011110, 0b010, 0b000011001, 0b000000000)
        JNXMT   {#}S                    Events - Branch         P2Asm.encode(0b1011110, 0b010, 0b000011010, 0b000000000)
        JNXFI   {#}S                    Events - Branch         P2Asm.encode(0b1011110, 0b010, 0b000011011, 0b000000000)
        JNXRO   {#}S                    Events - Branch         P2Asm.encode(0b1011110, 0b010, 0b000011100, 0b000000000)
        JNXRL   {#}S                    Events - Branch         P2Asm.encode(0b1011110, 0b010, 0b000011101, 0b000000000)
        JNATN   {#}S                    Events - Branch         P2Asm.encode(0b1011110, 0b010, 0b000011110, 0b000000000)
        JNQMT   {#}S                    Events - Branch         P2Asm.encode(0b1011110, 0b010, 0b000011111, 0b000000000)
        <empty> {#}D,{#}S               Miscellaneous           P2Asm.encode(0b1011110, 0b100, 0b000000000, 0b000000000)
        <empty> {#}D,{#}S               Miscellaneous           P2Asm.encode(0b1011111, 0b000, 0b000000000, 0b000000000)
        SETPAT  {#}D,{#}S               Events - Configuration  P2Asm.encode(0b1011111, 0b100, 0b000000000, 0b000000000)
        AKPIN   {#}S                    Smart Pins              P2Asm.encode(0b1100000, 0b010, 0b000000001, 0b000000000)
        WRPIN   {#}D,{#}S               Smart Pins              P2Asm.encode(0b1100000, 0b000, 0b000000000, 0b000000000)
        WXPIN   {#}D,{#}S               Smart Pins              P2Asm.encode(0b1100000, 0b100, 0b000000000, 0b000000000)
        WYPIN   {#}D,{#}S               Smart Pins              P2Asm.encode(0b1100001, 0b000, 0b000000000, 0b000000000)
        WRLUT   {#}D,{#}S/P             Lookup Table            P2Asm.encode(0b1100001, 0b100, 0b000000000, 0b000000000)
        WRBYTE  {#}D,{#}S/P             Hub RAM - Write         P2Asm.encode(0b1100010, 0b000, 0b000000000, 0b000000000)
        WRWORD  {#}D,{#}S/P             Hub RAM - Write         P2Asm.encode(0b1100010, 0b100, 0b000000000, 0b000000000)
        WRLONG  {#}D,{#}S/P             Hub RAM - Write         P2Asm.encode(0b1100011, 0b000, 0b000000000, 0b000000000)
        PUSHA   {#}D                    Hub RAM - Write         P2Asm.encode(0b1100011, 0b001, 0b000000000, 0b101100001)
        PUSHB   {#}D                    Hub RAM - Write         P2Asm.encode(0b1100011, 0b001, 0b000000000, 0b111100001)
        RDFAST  {#}D,{#}S               Hub FIFO - New Read     P2Asm.encode(0b1100011, 0b100, 0b000000000, 0b000000000)
        WRFAST  {#}D,{#}S               Hub FIFO - New Write    P2Asm.encode(0b1100100, 0b000, 0b000000000, 0b000000000)
        FBLOCK  {#}D,{#}S               Hub FIFO - New Block    P2Asm.encode(0b1100100, 0b100, 0b000000000, 0b000000000)
        XINIT   {#}D,{#}S               Streamer                P2Asm.encode(0b1100101, 0b000, 0b000000000, 0b000000000)
        */

        addSymbol("xstop", new NoArg_NE(P2Asm.encode(0b1100101, 0b011, 0b000000000, 0b000000000)));

        /*
        XZERO   {#}D,{#}S               Streamer                P2Asm.encode(0b1100101, 0b100, 0b000000000, 0b000000000)
        XCONT   {#}D,{#}S               Streamer                P2Asm.encode(0b1100110, 0b000, 0b000000000, 0b000000000)
        REP     {#}D,{#}S               Branch Repeat           P2Asm.encode(0b1100110, 0b100, 0b000000000, 0b000000000)
        COGINIT {#}D,{#}S       {WC}    Hub Control - Cogs      P2Asm.encode(0b1100111, 0b000, 0b000000000, 0b000000000)
        QMUL    {#}D,{#}S               CORDIC Solver           P2Asm.encode(0b1101000, 0b000, 0b000000000, 0b000000000)
        QDIV    {#}D,{#}S               CORDIC Solver           P2Asm.encode(0b1101000, 0b100, 0b000000000, 0b000000000)
        QFRAC   {#}D,{#}S               CORDIC Solver           P2Asm.encode(0b1101001, 0b000, 0b000000000, 0b000000000)
        QSQRT   {#}D,{#}S               CORDIC Solver           P2Asm.encode(0b1101001, 0b100, 0b000000000, 0b000000000)
        QROTATE {#}D,{#}S               CORDIC Solver           P2Asm.encode(0b1101010, 0b000, 0b000000000, 0b000000000)
        QVECTOR {#}D,{#}S               CORDIC Solver           P2Asm.encode(0b1101010, 0b100, 0b000000000, 0b000000000)
        HUBSET  {#}D                    Hub Control - Multi     P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000000000)
        COGID   {#}D            {WC}    Hub Control - Cogs      P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000000001)
        COGSTOP {#}D                    Hub Control - Cogs      P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000000011)
        LOCKNEW D               {WC}    Hub Control - Locks     P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000000100)
        LOCKRET {#}D                    Hub Control - Locks     P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000000101)
        LOCKTRY {#}D            {WC}    Hub Control - Locks     P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000000110)
        LOCKREL {#}D            {WC}    Hub Control - Locks     P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000000111)
        QLOG    {#}D                    CORDIC Solver           P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000001110)
        QEXP    {#}D                    CORDIC Solver           P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000001111)
        RFBYTE  D        {WC/WZ/WCZ}    Hub FIFO - Read         P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000010000)
        RFWORD  D        {WC/WZ/WCZ}    Hub FIFO - Read         P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000010001)
        RFLONG  D        {WC/WZ/WCZ}    Hub FIFO - Read         P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000010010)
        RFVAR   D        {WC/WZ/WCZ}    Hub FIFO - Read         P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000010011)
        RFVARS  D        {WC/WZ/WCZ}    Hub FIFO - Read         P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000010100)
        WFBYTE  {#}D                    Hub FIFO - Write        P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000010101)
        WFWORD  {#}D                    Hub FIFO - Write        P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000010110)
        WFLONG  {#}D                    Hub FIFO - Write        P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000010111)
        GETQX   D        {WC/WZ/WCZ}    CORDIC Solver           P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000011000)
        GETQY   D        {WC/WZ/WCZ}    CORDIC Solver           P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000011001)
        GETCT   D               {WC}    Miscellaneous           P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000011010)
        GETRND  D        {WC/WZ/WCZ}    Miscellaneous           P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000011011)
        GETRND            WC/WZ/WCZ     Miscellaneous           P2Asm.encode(0b1101011, 0b001, 0b000000000, 0b000011011)
        SETDACS {#}D                    Smart Pins              P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000011100)
        SETXFRQ {#}D                    Streamer                P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000011101)
        GETXACC D                       Streamer                P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000011110)
        WAITX   {#}D     {WC/WZ/WCZ}    Miscellaneous           P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000011111)
        SETSE1  {#}D                    Events - Configuration  P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000100000)
        SETSE2  {#}D                    Events - Configuration  P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000100001)
        SETSE3  {#}D                    Events - Configuration  P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000100010)
        SETSE4  {#}D                    Events - Configuration  P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000100011)
        */

        addSymbol("pollint", new NoArg_E(P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000100100)));
        addSymbol("pollct1", new NoArg_E(P2Asm.encode(0b1101011, 0b000, 0b000000001, 0b000100100)));
        addSymbol("pollct2", new NoArg_E(P2Asm.encode(0b1101011, 0b000, 0b000000010, 0b000100100)));
        addSymbol("pollct3", new NoArg_E(P2Asm.encode(0b1101011, 0b000, 0b000000011, 0b000100100)));
        addSymbol("pollse1", new NoArg_E(P2Asm.encode(0b1101011, 0b000, 0b000000100, 0b000100100)));
        addSymbol("pollse2", new NoArg_E(P2Asm.encode(0b1101011, 0b000, 0b000000101, 0b000100100)));
        addSymbol("pollse3", new NoArg_E(P2Asm.encode(0b1101011, 0b000, 0b000000110, 0b000100100)));
        addSymbol("pollse4", new NoArg_E(P2Asm.encode(0b1101011, 0b000, 0b000000111, 0b000100100)));
        addSymbol("pollpat", new NoArg_E(P2Asm.encode(0b1101011, 0b000, 0b000001000, 0b000100100)));
        addSymbol("pollfbw", new NoArg_E(P2Asm.encode(0b1101011, 0b000, 0b000001001, 0b000100100)));
        addSymbol("pollxmt", new NoArg_E(P2Asm.encode(0b1101011, 0b000, 0b000001010, 0b000100100)));
        addSymbol("pollxfi", new NoArg_E(P2Asm.encode(0b1101011, 0b000, 0b000001011, 0b000100100)));
        addSymbol("pollxro", new NoArg_E(P2Asm.encode(0b1101011, 0b000, 0b000001100, 0b000100100)));
        addSymbol("pollxrl", new NoArg_E(P2Asm.encode(0b1101011, 0b000, 0b000001101, 0b000100100)));
        addSymbol("pollatn", new NoArg_E(P2Asm.encode(0b1101011, 0b000, 0b000001110, 0b000100100)));
        addSymbol("pollqmt", new NoArg_E(P2Asm.encode(0b1101011, 0b000, 0b000001111, 0b000100100)));
        addSymbol("waitint", new NoArg_E(P2Asm.encode(0b1101011, 0b000, 0b000010000, 0b000100100)));
        addSymbol("waitct1", new NoArg_E(P2Asm.encode(0b1101011, 0b000, 0b000010001, 0b000100100)));
        addSymbol("waitct2", new NoArg_E(P2Asm.encode(0b1101011, 0b000, 0b000010010, 0b000100100)));
        addSymbol("waitct3", new NoArg_E(P2Asm.encode(0b1101011, 0b000, 0b000010011, 0b000100100)));
        addSymbol("waitse1", new NoArg_E(P2Asm.encode(0b1101011, 0b000, 0b000010100, 0b000100100)));
        addSymbol("waitse2", new NoArg_E(P2Asm.encode(0b1101011, 0b000, 0b000010101, 0b000100100)));
        addSymbol("waitse3", new NoArg_E(P2Asm.encode(0b1101011, 0b000, 0b000010110, 0b000100100)));
        addSymbol("waitse4", new NoArg_E(P2Asm.encode(0b1101011, 0b000, 0b000010111, 0b000100100)));
        addSymbol("waitpat", new NoArg_E(P2Asm.encode(0b1101011, 0b000, 0b000011000, 0b000100100)));
        addSymbol("waitfbw", new NoArg_E(P2Asm.encode(0b1101011, 0b000, 0b000011001, 0b000100100)));
        addSymbol("waitxmt", new NoArg_E(P2Asm.encode(0b1101011, 0b000, 0b000011010, 0b000100100)));
        addSymbol("waitxfi", new NoArg_E(P2Asm.encode(0b1101011, 0b000, 0b000011011, 0b000100100)));
        addSymbol("waitxro", new NoArg_E(P2Asm.encode(0b1101011, 0b000, 0b000011100, 0b000100100)));
        addSymbol("waitxrl", new NoArg_E(P2Asm.encode(0b1101011, 0b000, 0b000011101, 0b000100100)));
        addSymbol("waitatn", new NoArg_E(P2Asm.encode(0b1101011, 0b000, 0b000011110, 0b000100100)));

        addSymbol("allowi", new NoArg_NE(P2Asm.encode(0b1101011, 0b000, 0b000100000, 0b000100100)));
        addSymbol("stalli", new NoArg_NE(P2Asm.encode(0b1101011, 0b000, 0b000100001, 0b000100100)));
        addSymbol("trgint1", new NoArg_NE(P2Asm.encode(0b1101011, 0b000, 0b000100010, 0b000100100)));
        addSymbol("trgint2", new NoArg_NE(P2Asm.encode(0b1101011, 0b000, 0b000100011, 0b000100100)));
        addSymbol("trgint3", new NoArg_NE(P2Asm.encode(0b1101011, 0b000, 0b000100100, 0b000100100)));
        addSymbol("nixint1", new NoArg_NE(P2Asm.encode(0b1101011, 0b000, 0b000100101, 0b000100100)));
        addSymbol("nixint2", new NoArg_NE(P2Asm.encode(0b1101011, 0b000, 0b000100110, 0b000100100)));
        addSymbol("nixint3", new NoArg_NE(P2Asm.encode(0b1101011, 0b000, 0b000100111, 0b000100100)));

        /*
        SETINT1 {#}D                    Interrupts              P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000100101)
        SETINT2 {#}D                    Interrupts              P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000100110)
        SETINT3 {#}D                    Interrupts              P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000100111)
        SETQ    {#}D                    Miscellaneous           P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000101000)
        SETQ2   {#}D                    Miscellaneous           P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000101001)
        PUSH    {#}D                    Miscellaneous           P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000101010)
        POP     D        {WC/WZ/WCZ}    Miscellaneous           P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000101011)
        RET              {WC/WZ/WCZ}    Branch Return           P2Asm.encode(0b1101011, 0b001, 0b000000000, 0b000101101)
        RETA             {WC/WZ/WCZ}    Branch Return           P2Asm.encode(0b1101011, 0b001, 0b000000000, 0b000101110)
        RETB             {WC/WZ/WCZ}    Branch Return           P2Asm.encode(0b1101011, 0b001, 0b000000000, 0b000101111)
        JMPREL  {#}D                    Branch D - Jump         P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000110000)
        SKIP    {#}D                    Branch D - Skip         P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000110001)
        SKIPF   {#}D                    Branch D - Jump+Skip    P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000110010)
        EXECF   {#}D                    Branch D - Call+Skip    P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000110011)
        GETPTR  D                       Hub FIFO                P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000110100)
        GETBRK  D          WC/WZ/WCZ    Interrupts              P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000110101)
        COGBRK  {#}D                    Interrupts              P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000110101)
        BRK     {#}D                    Interrupts              P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000110110)
        SETLUTS {#}D                    Lookup Table            P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000110111)
        SETCY   {#}D                    Color Space Converter   P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000111000)
        SETCI   {#}D                    Color Space Converter   P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000111001)
        SETCQ   {#}D                    Color Space Converter   P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000111010)
        SETCFRQ {#}D                    Color Space Converter   P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000111011)
        SETCMOD {#}D                    Color Space Converter   P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000111100)
        SETPIV  {#}D                    Pixel Mixer             P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000111101)
        SETPIX  {#}D                    Pixel Mixer             P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000111110)
        COGATN  {#}D                    Events - Attention      P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000111111)
        TESTP   {#}D           WC/WZ    Pins                    P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001000000)
        TESTPN  {#}D           WC/WZ    Pins                    P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001000001)
        TESTP   {#}D       ANDC/ANDZ    Pins                    P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001000010)
        TESTPN  {#}D       ANDC/ANDZ    Pins                    P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001000011)
        TESTP   {#}D         ORC/ORZ    Pins                    P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001000100)
        TESTPN  {#}D         ORC/ORZ    Pins                    P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001000101)
        TESTP   {#}D       XORC/XORZ    Pins                    P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001000110)
        TESTPN  {#}D       XORC/XORZ    Pins                    P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001000111)
        */

        addSymbol("dirl", new Pins(P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001000000)));
        addSymbol("dirh", new Pins(P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001000001)));
        addSymbol("dirc", new Pins(P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001000010)));
        addSymbol("dirnc", new Pins(P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001000011)));
        addSymbol("dirz", new Pins(P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001000100)));
        addSymbol("dirnz", new Pins(P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001000101)));
        addSymbol("dirrnd", new Pins(P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001000110)));
        addSymbol("dirnot", new Pins(P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001000111)));
        addSymbol("outl", new Pins(P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001001000)));
        addSymbol("outh", new Pins(P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001001001)));
        addSymbol("outc", new Pins(P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001001010)));
        addSymbol("outnc", new Pins(P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001001011)));
        addSymbol("outz", new Pins(P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001001100)));
        addSymbol("outnz", new Pins(P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001001101)));
        addSymbol("outrnd", new Pins(P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001001110)));
        addSymbol("outnot", new Pins(P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001001111)));
        addSymbol("fltl", new Pins(P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001010000)));
        addSymbol("flth", new Pins(P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001010001)));
        addSymbol("fltc", new Pins(P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001010010)));
        addSymbol("fltnc", new Pins(P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001010011)));
        addSymbol("fltz", new Pins(P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001010100)));
        addSymbol("fltnz", new Pins(P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001010101)));
        addSymbol("fltrnd", new Pins(P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001010110)));
        addSymbol("fltnot", new Pins(P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001010111)));
        addSymbol("drvl", new Pins(P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001011000)));
        addSymbol("drvh", new Pins(P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001011001)));
        addSymbol("drvc", new Pins(P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001011010)));
        addSymbol("drvnc", new Pins(P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001011011)));
        addSymbol("drvz", new Pins(P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001011100)));
        addSymbol("drvnz", new Pins(P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001011101)));
        addSymbol("drvrnd", new Pins(P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001011110)));
        addSymbol("drvnot", new Pins(P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001011111)));

        /*
        SPLITB  D                       Math and Logic          P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001100000)
        MERGEB  D                       Math and Logic          P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001100001)
        SPLITW  D                       Math and Logic          P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001100010)
        MERGEW  D                       Math and Logic          P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001100011)
        SEUSSF  D                       Math and Logic          P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001100100)
        SEUSSR  D                       Math and Logic          P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001100101)
        RGBSQZ  D                       Math and Logic          P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001100110)
        RGBEXP  D                       Math and Logic          P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001100111)
        XORO32  D                       Math and Logic          P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001101000)
        REV     D                       Math and Logic          P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001101001)
        RCZR    D        {WC/WZ/WCZ}    Math and Logic          P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001101010)
        RCZL    D        {WC/WZ/WCZ}    Math and Logic          P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001101011)
        WRC     D                       Math and Logic          P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001101100)
        WRNC    D                       Math and Logic          P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001101101)
        WRZ     D                       Math and Logic          P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001101110)
        WRNZ    D                       Math and Logic          P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001101111)
        MODCZ   c,z      {WC/WZ/WCZ}    Math and Logic          P2Asm.encode(0b1101011, 0b001, 0b000000000, 0b001101111)
        MODC    c               {WC}    Math and Logic          P2Asm.encode(0b1101011, 0b001, 0b000000000, 0b001101111)
        MODZ    z               {WZ}    Math and Logic          P2Asm.encode(0b1101011, 0b001, 0b000000000, 0b001101111)
        SETSCP  {#}D                    Smart Pins              P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001110000)
        GETSCP  D                       Smart Pins              P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b001110001)
        */

        addSymbol("jmp", new Branch_D_A(P2Asm.encode(0b1101100, 0b000, 0b000000000, 0b000000000), P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000101100)));
        addSymbol("call", new Branch_D_A(P2Asm.encode(0b1101101, 0b000, 0b000000000, 0b000000000), P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000101101)));
        addSymbol("calla", new Branch_D_A(P2Asm.encode(0b1101110, 0b000, 0b000000000, 0b000000000), P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000101110)));
        addSymbol("callb", new Branch_D_A(P2Asm.encode(0b1101111, 0b000, 0b000000000, 0b000000000), P2Asm.encode(0b1101011, 0b000, 0b000000000, 0b000101111)));

        /*
        CALLD   PA/PB/PTRA/PTRB,#{\}A   Branch A - Call         P2Asm.encode(0b1110000, 0b000, 0b000000000, 0b000000000)
        LOC     PA/PB/PTRA/PTRB,#{\}A   Math and Logic          P2Asm.encode(0b1110100, 0b000, 0b000000000, 0b000000000)
         */

        addSymbol("augs", new Augs());
        addSymbol("augd", new Augd());

        // Hub control
        addSymbol("hubset", new Hubset());

        // Misc.
        addSymbol("getct", new Getct());
        addSymbol("waitx", new Waitx());

        // Conditions
        addSymbol("_ret_", 0b0000);
        addSymbol("if_nc_and_nz", 0b0001);
        addSymbol("if_nz_and_nc", 0b0001);
        addSymbol("if_gt", 0b0001);
        addSymbol("if_a", 0b0001);
        addSymbol("if_00", 0b0001);
        addSymbol("if_nc_and_z", 0b0010);
        addSymbol("if_z_and_nc", 0b0010);
        addSymbol("if_01", 0b0010);
        addSymbol("if_nc", 0b0011);
        addSymbol("if_ge", 0b0011);
        addSymbol("if_ae", 0b0011);
        addSymbol("if_0x", 0b0011);
        addSymbol("if_c_and_nz", 0b0100);
        addSymbol("if_nz_and_c", 0b0100);
        addSymbol("if_10", 0b0100);
        addSymbol("if_nz", 0b0101);
        addSymbol("if_ne", 0b0101);
        addSymbol("if_x0", 0b0101);
        addSymbol("if_c_ne_z", 0b0110);
        addSymbol("if_z_ne_c", 0b0110);
        addSymbol("if_diff", 0b0110);
        addSymbol("if_nc_or_nz", 0b0111);
        addSymbol("if_nz_or_nc", 0b0111);
        addSymbol("if_not_11", 0b0111);
        addSymbol("if_c_and_z", 0b1000);
        addSymbol("if_z_and_c", 0b1000);
        addSymbol("if_11", 0b1000);
        addSymbol("if_c_eq_z", 0b1001);
        addSymbol("if_z_eq_c", 0b1001);
        addSymbol("if_same", 0b1001);
        addSymbol("if_z", 0b1010);
        addSymbol("if_e", 0b1010);
        addSymbol("if_x1", 0b1010);
        addSymbol("if_nc_or_z", 0b1011);
        addSymbol("if_z_or_nc", 0b1011);
        addSymbol("if_not_10", 0b1011);
        addSymbol("if_c", 0b1100);
        addSymbol("if_lt", 0b1100);
        addSymbol("if_b", 0b1100);
        addSymbol("if_1x", 0b1100);
        addSymbol("if_c_or_nz", 0b1101);
        addSymbol("if_nz_or_c", 0b1101);
        addSymbol("if_not_01", 0b1101);
        addSymbol("if_c_or_z", 0b1110);
        addSymbol("if_z_or_c", 0b1110);
        addSymbol("if_le", 0b1110);
        addSymbol("if_be", 0b1110);
        addSymbol("if_not_00", 0b1110);
    }

    void addSymbol(String name, int value) {
        symbols.put(name.toLowerCase(), new NumberLiteral(value));
    }

}
