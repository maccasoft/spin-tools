/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.debug;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.model.RootNode;
import com.maccasoft.propeller.spin2.Spin2Compiler;
import com.maccasoft.propeller.spin2.Spin2Object;
import com.maccasoft.propeller.spin2.Spin2ObjectCompiler;
import com.maccasoft.propeller.spin2.Spin2Parser;

class P2DisassemblerTest {

    @Test
    void testMathAndLogic1() throws Exception {
        String text = ""
            + "DAT             org     $000\n"
            + "\n"
            + "                ror     1, 2\n"
            + "                rol     1, 2 wc\n"
            + "                shr     1, 2 wz\n"
            + "                shl     1, 2 wcz\n"
            + "                rcr     1, #2\n"
            + "                rcl     1, #2 wc\n"
            + "                sar     1, #2 wz\n"
            + "                sal     1, #2 wcz\n"
            + "                add     1, 2\n"
            + "                addx    1, #2\n"
            + "                adds    1, 2\n"
            + "                addsx   1, #2\n"
            + "                sub     1, 2\n"
            + "                subx    1, #2\n"
            + "                subs    1, 2\n"
            + "                subsx   1, #2\n"
            + "";

        Assertions.assertEquals(""
            + "F0000202              ror     $001, $002\n"
            + "F0300202              rol     $001, $002 wc\n"
            + "F0480202              shr     $001, $002 wz\n"
            + "F0780202              shl     $001, $002 wcz\n"
            + "F0840202              rcr     $001, #$002\n"
            + "F0B40202              rcl     $001, #$002 wc\n"
            + "F0CC0202              sar     $001, #$002 wz\n"
            + "F0FC0202              sal     $001, #$002 wcz\n"
            + "F1000202              add     $001, $002\n"
            + "F1240202              addx    $001, #$002\n"
            + "F1400202              adds    $001, $002\n"
            + "F1640202              addsx   $001, #$002\n"
            + "F1800202              sub     $001, $002\n"
            + "F1A40202              subx    $001, #$002\n"
            + "F1C00202              subs    $001, $002\n"
            + "F1E40202              subsx   $001, #$002\n"
            + "", compileAndDisassemble(text));
    }

    @Test
    void testMathAndLogic2() throws Exception {
        String text = ""
            + "DAT             org     $000\n"
            + "\n"
            + "                cmp     1, 2\n"
            + "                cmpx    1, 2 wc\n"
            + "                cmps    1, 2 wz\n"
            + "                cmpsx   1, 2 wcz\n"
            + "                cmpr    1, #2\n"
            + "                cmpm    1, #2 wc\n"
            + "                subr    1, #2 wz\n"
            + "                cmpsub  1, #2 wcz\n"
            + "                fge     1, 2\n"
            + "                fle     1, 2\n"
            + "                fges    1, 2\n"
            + "                fles    1, 2\n"
            + "                sumc    1, 2\n"
            + "                sumnc   1, 2\n"
            + "                sumz    1, 2\n"
            + "                sumnz   1, 2\n"
            + "";

        Assertions.assertEquals(""
            + "F2000202              cmp     $001, $002\n"
            + "F2300202              cmpx    $001, $002 wc\n"
            + "F2480202              cmps    $001, $002 wz\n"
            + "F2780202              cmpsx   $001, $002 wcz\n"
            + "F2840202              cmpr    $001, #$002\n"
            + "F2B40202              cmpm    $001, #$002 wc\n"
            + "F2CC0202              subr    $001, #$002 wz\n"
            + "F2FC0202              cmpsub  $001, #$002 wcz\n"
            + "F3000202              fge     $001, $002\n"
            + "F3200202              fle     $001, $002\n"
            + "F3400202              fges    $001, $002\n"
            + "F3600202              fles    $001, $002\n"
            + "F3800202              sumc    $001, $002\n"
            + "F3A00202              sumnc   $001, $002\n"
            + "F3C00202              sumz    $001, $002\n"
            + "F3E00202              sumnz   $001, $002\n"
            + "", compileAndDisassemble(text));
    }

    @Test
    void testMathAndLogic3() throws Exception {
        String text = ""
            + "DAT             org     $000\n"
            + "\n"
            + "                testb   1, 2 wc\n"
            + "                testbn  1, 2 wz\n"
            + "                testb   1, #2 andc\n"
            + "                testbn  1, #2 andc\n"
            + "                testb   1, 2 orc\n"
            + "                testbn  1, 2 orz\n"
            + "                testb   1, #2 xorc\n"
            + "                testbn  1, #2 xorz\n"
            + "                bitl    1, 2\n"
            + "                bith    1, 2 wcz\n"
            + "                bitc    1, #2\n"
            + "                bitnc   1, #2 wcz\n"
            + "                bitz    1, 2\n"
            + "                bitnz   1, 2 wcz\n"
            + "                bitrnd  1, #2\n"
            + "                bitnot  1, #2 wcz\n"
            + "";

        Assertions.assertEquals(""
            + "F4100202              testb   $001, $002 wc\n"
            + "F4280202              testbn  $001, $002 wz\n"
            + "F4540202              testb   $001, #$002 andc\n"
            + "F4740202              testbn  $001, #$002 andc\n"
            + "F4900202              testb   $001, $002 orc\n"
            + "F4A80202              testbn  $001, $002 orz\n"
            + "F4D40202              testb   $001, #$002 xorc\n"
            + "F4EC0202              testbn  $001, #$002 xorz\n"
            + "F4000202              bitl    $001, $002\n"
            + "F4380202              bith    $001, $002 wcz\n"
            + "F4440202              bitc    $001, #$002\n"
            + "F47C0202              bitnc   $001, #$002 wcz\n"
            + "F4800202              bitz    $001, $002\n"
            + "F4B80202              bitnz   $001, $002 wcz\n"
            + "F4C40202              bitrnd  $001, #$002\n"
            + "F4FC0202              bitnot  $001, #$002 wcz\n"
            + "", compileAndDisassemble(text));
    }

    @Test
    void testMathAndLogic4() throws Exception {
        String text = ""
            + "DAT             org     $000\n"
            + "\n"
            + "                and     1, 2\n"
            + "                andn    1, 2\n"
            + "                not     1, 2\n"
            + "                not     1 wc\n"
            + "                abs     1, #2\n"
            + "                abs     1\n"
            + "                neg     1, 2\n"
            + "                neg     1 wz\n"
            + "                negc    1, #2\n"
            + "                negc    1 wcz\n"
            + "                negz    1, 2\n"
            + "                negnz   1\n"
            + "                incmod  1, 2\n"
            + "                decmod  1, 2\n"
            + "                zerox   1, 2\n"
            + "                signx   1, 2\n"
            + "                encod   1, #2\n"
            + "                encod   1\n"
            + "                ones    1, 2\n"
            + "                ones    1\n"
            + "                test    1, 2\n"
            + "                test    1\n"
            + "                testn   1, 2\n"
            + "";

        Assertions.assertEquals(""
            + "F5000202              and     $001, $002\n"
            + "F5200202              andn    $001, $002\n"
            + "F6200202              not     $001, $002\n"
            + "F6300201              not     $001 wc\n"
            + "F6440202              abs     $001, #$002\n"
            + "F6400201              abs     $001\n"
            + "F6600202              neg     $001, $002\n"
            + "F6680201              neg     $001 wz\n"
            + "F6840202              negc    $001, #$002\n"
            + "F6980201              negc    $001 wcz\n"
            + "F6C00202              negz    $001, $002\n"
            + "F6E00201              negnz   $001\n"
            + "F7000202              incmod  $001, $002\n"
            + "F7200202              decmod  $001, $002\n"
            + "F7400202              zerox   $001, $002\n"
            + "F7600202              signx   $001, $002\n"
            + "F7840202              encod   $001, #$002\n"
            + "F7800201              encod   $001\n"
            + "F7A00202              ones    $001, $002\n"
            + "F7A00201              ones    $001\n"
            + "F7C00202              test    $001, $002\n"
            + "F7C00201              test    $001\n"
            + "F7E00202              testn   $001, $002\n"
            + "", compileAndDisassemble(text));
    }

    @Test
    void testMathAndLogic5() throws Exception {
        String text = ""
            + "DAT             org     $000\n"
            + "\n"
            + "                setnib  2, 3, #1\n"
            + "                setnib  1\n"
            + "                getnib  2, 3, #1\n"
            + "                getnib  1\n"
            + "                rolnib  2, 3, #1\n"
            + "                rolnib  1\n"
            + "                setbyte 2, 3, #1\n"
            + "                setbyte 1\n"
            + "                getbyte 2, 3, #1\n"
            + "                getbyte 1\n"
            + "                rolbyte 2, 3, #1\n"
            + "                rolbyte 1\n"
            + "                setword 2, 3, #1\n"
            + "                setword 1\n"
            + "                getword 2, 3, #1\n"
            + "                getword 1\n"
            + "                rolword 2, 3, #1\n"
            + "                rolword 1\n"
            + "";

        Assertions.assertEquals(""
            + "F8080403              setnib  $002, $003, #1\n"
            + "F8000001              setnib  $001\n"
            + "F8480403              getnib  $002, $003, #1\n"
            + "F8400200              getnib  $001\n"
            + "F8880403              rolnib  $002, $003, #1\n"
            + "F8800200              rolnib  $001\n"
            + "F8C80403              setbyte $002, $003, #1\n"
            + "F8C00001              setbyte $001\n"
            + "F8E80403              getbyte $002, $003, #1\n"
            + "F8E00200              getbyte $001\n"
            + "F9080403              rolbyte $002, $003, #1\n"
            + "F9000200              rolbyte $001\n"
            + "F9280403              setword $002, $003, #1\n"
            + "F9200001              setword $001\n"
            + "F9380403              getword $002, $003, #1\n"
            + "F9300200              getword $001\n"
            + "F9480403              rolword $002, $003, #1\n"
            + "F9400200              rolword $001\n"
            + "", compileAndDisassemble(text));
    }

    @Test
    void testRegisterIndirection() throws Exception {
        String text = ""
            + "DAT             org     $000\n"
            + "\n"
            + "                altsn   1, 2\n"
            + "                altsn   1\n"
            + "                altgn   1, 2\n"
            + "                altgn   1\n"
            + "                altsb   1, 2\n"
            + "                altsb   1\n"
            + "                altgb   1, 2\n"
            + "                altgb   1\n"
            + "                altsw   1, 2\n"
            + "                altsw   1\n"
            + "                altgw   1, 2\n"
            + "                altgw   1\n"
            + "                altr    1, 2\n"
            + "                altr    1\n"
            + "                altd    1, 2\n"
            + "                altd    1\n"
            + "                alts    1, 2\n"
            + "                alts    1\n"
            + "                altb    1, 2\n"
            + "                altb    1\n"
            + "                alti    1, 2\n"
            + "                alti    1\n"
            + "";

        Assertions.assertEquals(""
            + "F9500202              altsn   $001, $002\n"
            + "F9540200              altsn   $001\n"
            + "F9580202              altgn   $001, $002\n"
            + "F95C0200              altgn   $001\n"
            + "F9600202              altsb   $001, $002\n"
            + "F9640200              altsb   $001\n"
            + "F9680202              altgb   $001, $002\n"
            + "F96C0200              altgb   $001\n"
            + "F9700202              altsw   $001, $002\n"
            + "F9740200              altsw   $001\n"
            + "F9780202              altgw   $001, $002\n"
            + "F97C0200              altgw   $001\n"
            + "F9800202              altr    $001, $002\n"
            + "F9840200              altr    $001\n"
            + "F9880202              altd    $001, $002\n"
            + "F98C0200              altd    $001\n"
            + "F9900202              alts    $001, $002\n"
            + "F9940200              alts    $001\n"
            + "F9980202              altb    $001, $002\n"
            + "F99C0200              altb    $001\n"
            + "F9A00202              alti    $001, $002\n"
            + "F9A40364              alti    $001\n"
            + "", compileAndDisassemble(text));
    }

    @Test
    void testMathAndLogic6() throws Exception {
        String text = ""
            + "DAT             org     $000\n"
            + "\n"
            + "                setr    1, 2\n"
            + "                setd    1, 2\n"
            + "                sets    1, 2\n"
            + "                decod   1, 2\n"
            + "                decod   1\n"
            + "                bmask   1, 2\n"
            + "                bmask   1\n"
            + "                crcbit  1, 2\n"
            + "                crcnib  1, 2\n"
            + "                muxnits 1, 2\n"
            + "                muxnibs 1, 2\n"
            + "                muxq    1, 2\n"
            + "                movbyts 1, 2\n"
            + "                mul     1, 2\n"
            + "                muls    1, 2\n"
            + "                sca     1, 2\n"
            + "                scas    1, 2\n"
            + "";

        Assertions.assertEquals(""
            + "F9A80202              setr    $001, $002\n"
            + "F9B00202              setd    $001, $002\n"
            + "F9B80202              sets    $001, $002\n"
            + "F9C00202              decod   $001, $002\n"
            + "F9C00201              decod   $001, $001\n"
            + "F9C80202              bmask   $001, $002\n"
            + "F9C80201              bmask   $001, $001\n"
            + "F9D00202              crcbit  $001, $002\n"
            + "F9D80202              crcnib  $001, $002\n"
            + "F9E00202              muxnits $001, $002\n"
            + "F9E80202              muxnibs $001, $002\n"
            + "F9F00202              muxq    $001, $002\n"
            + "F9F80202              movbyts $001, $002\n"
            + "FA000202              mul     $001, $002\n"
            + "FA100202              muls    $001, $002\n"
            + "FA200202              sca     $001, $002\n"
            + "FA300202              scas    $001, $002\n"
            + "", compileAndDisassemble(text));
    }

    @Test
    void testMathAndLogic7() throws Exception {
        String text = ""
            + "DAT             org     $000\n"
            + "\n"
            + "                splitb  1\n"
            + "                mergeb  1\n"
            + "                splitw  1\n"
            + "                mergew  1\n"
            + "                seussf  1\n"
            + "                seussr  1\n"
            + "                rgbsqz  1\n"
            + "                rgbexp  1\n"
            + "                xoro32  1\n"
            + "                rev     1\n"
            + "                rczr    1\n"
            + "                rczl    1\n"
            + "                wrc     1\n"
            + "                wrnc    1\n"
            + "                wrz     1\n"
            + "                wrnz    1\n"
            + "                modc    _set wc\n"
            + "                modz    _set wz\n"
            + "                modcz   _set, _clr wcz\n"
            + "";

        Assertions.assertEquals(""
            + "FD600260              splitb  $001\n"
            + "FD600261              mergeb  $001\n"
            + "FD600262              splitw  $001\n"
            + "FD600263              mergew  $001\n"
            + "FD600264              seussf  $001\n"
            + "FD600265              seussr  $001\n"
            + "FD600266              rgbsqz  $001\n"
            + "FD600267              rgbexp  $001\n"
            + "FD600268              xoro32  $001\n"
            + "FD600269              rev     $001\n"
            + "FD60026A              rczr    $001\n"
            + "FD60026B              rczl    $001\n"
            + "FD60026C              wrc     $001\n"
            + "FD60026D              wrnc    $001\n"
            + "FD60026E              wrz     $001\n"
            + "FD60026F              wrnz    $001\n"
            + "FD75E06F              modc    _set wc\n"
            + "FD6C1E6F              modz    _set wz\n"
            + "FD7DE06F              modcz   _set, _clr wcz\n"
            + "", compileAndDisassemble(text));
    }

    @Test
    void testPixelMixer() throws Exception {
        String text = ""
            + "DAT             org     $000\n"
            + "\n"
            + "                addpix  1, 2\n"
            + "                mulpix  1, 2\n"
            + "                blnpix  1, 2\n"
            + "                mixpix  1, 2\n"
            + "                setpiv  1\n"
            + "                setpix  1\n"
            + "";

        Assertions.assertEquals(""
            + "FA400202              addpix  $001, $002\n"
            + "FA480202              mulpix  $001, $002\n"
            + "FA500202              blnpix  $001, $002\n"
            + "FA580202              mixpix  $001, $002\n"
            + "FD60023D              setpiv  $001\n"
            + "FD60023E              setpix  $001\n"
            + "", compileAndDisassemble(text));
    }

    @Test
    void testEventsConfig() throws Exception {
        String text = ""
            + "DAT             org     $000\n"
            + "\n"
            + "                addct1  1, 2\n"
            + "                addct2  1, 2\n"
            + "                addct3  1, 2\n"
            + "                cogatn  1\n"
            + "                setse1  1\n"
            + "                setse2  1\n"
            + "                setse3  1\n"
            + "                setse4  1\n"
            + "                setpat  1, 2\n"
            + "";

        Assertions.assertEquals(""
            + "FA600202              addct1  $001, $002\n"
            + "FA680202              addct2  $001, $002\n"
            + "FA700202              addct3  $001, $002\n"
            + "FD60023F              cogatn  $001\n"
            + "FD600220              setse1  $001\n"
            + "FD600221              setse2  $001\n"
            + "FD600222              setse3  $001\n"
            + "FD600223              setse4  $001\n"
            + "FBF00202              setpat  $001, $002\n"
            + "", compileAndDisassemble(text));
    }

    @Test
    void testHubRam() throws Exception {
        String text = ""
            + "DAT             org     $000\n"
            + "\n"
            + "                wmlong  1, 2\n"
            + "                rdbyte  1, 2 wz\n"
            + "                rdword  1, 2 wz\n"
            + "                rdlong  1, 2 wz\n"
            + "                popa    1\n"
            + "                popb    1\n"
            + "                wrbyte  1, 2\n"
            + "                wrword  1, 2\n"
            + "                wrlong  1, 2\n"
            + "                pusha   1\n"
            + "                pushb   1\n"
            + "";

        Assertions.assertEquals(""
            + "FA780202              wmlong  $001, $002\n"
            + "FAC80202              rdbyte  $001, $002 wz\n"
            + "FAE80202              rdword  $001, $002 wz\n"
            + "FB080202              rdlong  $001, $002 wz\n"
            + "FB04035F              rdlong  $001, --ptra\n"
            + "FB0403DF              rdlong  $001, --ptrb\n"
            + "FC400202              wrbyte  $001, $002\n"
            + "FC500202              wrword  $001, $002\n"
            + "FC600202              wrlong  $001, $002\n"
            + "FC640361              wrlong  $001, ptra++\n"
            + "FC6403E1              wrlong  $001, ptrb++\n"
            + "", compileAndDisassemble(text));
    }

    @Test
    void testBranch1() throws Exception {
        String text = ""
            + "DAT             org     $000\n"
            + "\n"
            + "                calld   1, #2\n"
            + "                resi3\n"
            + "                resi2\n"
            + "                resi1\n"
            + "                resi0\n"
            + "                reti3\n"
            + "                reti2\n"
            + "                reti1\n"
            + "                reti0\n"
            + "                callpa  1, #2\n"
            + "                callpb  1, #2\n"
            + "";

        Assertions.assertEquals(""
            + "FB240201              calld   $001, #$001\n"
            + "FB3BE1F1              resi3   \n"
            + "FB3BE5F3              resi2   \n"
            + "FB3BE9F5              resi1   \n"
            + "FB3BFDFF              resi0   \n"
            + "FB3BFFF1              reti3   \n"
            + "FB3BFFF3              reti2   \n"
            + "FB3BFFF5              reti1   \n"
            + "FB3BFFFF              reti0   \n"
            + "FB4403F8              callpa  $001, #$1F8\n"
            + "FB5403F7              callpb  $001, #$1F7\n"
            + "", compileAndDisassemble(text));
    }

    @Test
    void testBranch2() throws Exception {
        String text = ""
            + "DAT             org     $000\n"
            + "\n"
            + "                djz     1, #2\n"
            + "                djnz    1, #2\n"
            + "                djf     1, #2\n"
            + "                djnf    1, #2\n"
            + "                ijz     1, #2\n"
            + "                ijnz    1, #2\n"
            + "                tjz     1, #2\n"
            + "                tjnz    1, #2\n"
            + "                tjf     1, #2\n"
            + "                tjnf    1, #2\n"
            + "                tjs     1, #2\n"
            + "                tjns    1, #2\n"
            + "                tjv     1, #2\n"
            + "";

        Assertions.assertEquals(""
            + "FB640201              djz     $001, #$001\n"
            + "FB6C0200              djnz    $001, #$000\n"
            + "FB7403FF              djf     $001, #$1FF\n"
            + "FB7C03FE              djnf    $001, #$1FE\n"
            + "FB8403FD              ijz     $001, #$1FD\n"
            + "FB8C03FC              ijnz    $001, #$1FC\n"
            + "FB9403FB              tjz     $001, #$1FB\n"
            + "FB9C03FA              tjnz    $001, #$1FA\n"
            + "FBA403F9              tjf     $001, #$1F9\n"
            + "FBAC03F8              tjnf    $001, #$1F8\n"
            + "FBB403F7              tjs     $001, #$1F7\n"
            + "FBBC03F6              tjns    $001, #$1F6\n"
            + "FBC403F5              tjv     $001, #$1F5\n"
            + "", compileAndDisassemble(text));
    }

    @Test
    void testBranch3() throws Exception {
        String text = ""
            + "DAT             org     $000\n"
            + "\n"
            + "                jmp     1\n"
            + "                call    1\n"
            + "                ret\n"
            + "                calla   1\n"
            + "                reta\n"
            + "                callb   1\n"
            + "                retb\n"
            + "                jmprel  1\n"
            + "                skip    1\n"
            + "                skipf   1\n"
            + "                execf   1\n"
            + "";

        Assertions.assertEquals(""
            + "FD60022C              jmp     $001\n"
            + "FD60022D              call    $001\n"
            + "FD64002D              ret     $000\n"
            + "FD60022E              calla   $001\n"
            + "FD64002E              reta    $000\n"
            + "FD60022F              callb   $001\n"
            + "FD64002F              retb    $000\n"
            + "FD600230              jmprel  $001\n"
            + "FD600231              skip    $001\n"
            + "FD600232              skipf   $001\n"
            + "FD600233              execf   $001\n"
            + "", compileAndDisassemble(text));
    }

    @Test
    void testBranch4() throws Exception {
        String text = ""
            + "DAT             org     $000\n"
            + "\n"
            + "                jmp     #$123\n"
            + "                call    #$123\n"
            + "                calla   #$123\n"
            + "                callb   #$123\n"
            + "                calld   pa, #$12345\n"
            + "                loc     pa, #$12345\n"
            + "";

        Assertions.assertEquals(""
            + "FD900488              jmp     #$00488\n"
            + "FDB00484              call    #$00484\n"
            + "FDD00480              calla   #$00480\n"
            + "FDF0047C              callb   #$0047C\n"
            + "FE012345              calld   pa, #$00012345\n"
            + "FE812345              loc     pa, #$00012345\n"
            + "", compileAndDisassemble(text));
    }

    @Test
    void testEventsBranch1() throws Exception {
        String text = ""
            + "DAT             org     $000\n"
            + "\n"
            + "                jint    1\n"
            + "                jct1    1\n"
            + "                jct2    1\n"
            + "                jct3    1\n"
            + "                jse1    1\n"
            + "                jse2    1\n"
            + "                jse3    1\n"
            + "                jse4    1\n"
            + "                jpat    1\n"
            + "                jfbw    1\n"
            + "                jxmt    1\n"
            + "                jxfi    1\n"
            + "                jxro    1\n"
            + "                jxrl    1\n"
            + "                jatn    1\n"
            + "                jqmt    1\n"
            + "";

        Assertions.assertEquals(""
            + "FBC80001              jint    $001\n"
            + "FBC80201              jct1    $001\n"
            + "FBC80401              jct2    $001\n"
            + "FBC80601              jct3    $001\n"
            + "FBC80801              jse1    $001\n"
            + "FBC80A01              jse2    $001\n"
            + "FBC80C01              jse3    $001\n"
            + "FBC80E01              jse4    $001\n"
            + "FBC81001              jpat    $001\n"
            + "FBC81201              jfbw    $001\n"
            + "FBC81401              jxmt    $001\n"
            + "FBC81601              jxfi    $001\n"
            + "FBC81801              jxro    $001\n"
            + "FBC81A01              jxrl    $001\n"
            + "FBC81C01              jatn    $001\n"
            + "FBC81E01              jqmt    $001\n"
            + "", compileAndDisassemble(text));
    }

    @Test
    void testEventsBranch2() throws Exception {
        String text = ""
            + "DAT             org     $000\n"
            + "\n"
            + "                jnint   1\n"
            + "                jnct1   1\n"
            + "                jnct2   1\n"
            + "                jnct3   1\n"
            + "                jnse1   1\n"
            + "                jnse2   1\n"
            + "                jnse3   1\n"
            + "                jnse4   1\n"
            + "                jnpat   1\n"
            + "                jnfbw   1\n"
            + "                jnxmt   1\n"
            + "                jnxfi   1\n"
            + "                jnxro   1\n"
            + "                jnxrl   1\n"
            + "                jnatn   1\n"
            + "                jnqmt   1\n"
            + "";

        Assertions.assertEquals(""
            + "FBC82001              jnint   $001\n"
            + "FBC82201              jnct1   $001\n"
            + "FBC82401              jnct2   $001\n"
            + "FBC82601              jnct3   $001\n"
            + "FBC82801              jnse1   $001\n"
            + "FBC82A01              jnse2   $001\n"
            + "FBC82C01              jnse3   $001\n"
            + "FBC82E01              jnse4   $001\n"
            + "FBC83001              jnpat   $001\n"
            + "FBC83201              jnfbw   $001\n"
            + "FBC83401              jnxmt   $001\n"
            + "FBC83601              jnxfi   $001\n"
            + "FBC83801              jnxro   $001\n"
            + "FBC83A01              jnxrl   $001\n"
            + "FBC83C01              jnatn   $001\n"
            + "FBC83E01              jnqmt   $001\n"
            + "", compileAndDisassemble(text));
    }

    @Test
    void testEventsPoll() throws Exception {
        String text = ""
            + "DAT             org     $000\n"
            + "\n"
            + "                pollint\n"
            + "                pollct1\n"
            + "                pollct2\n"
            + "                pollct3\n"
            + "                pollse1\n"
            + "                pollse2\n"
            + "                pollse3\n"
            + "                pollse4\n"
            + "                pollpat\n"
            + "                pollfbw\n"
            + "                pollxmt\n"
            + "                pollxfi\n"
            + "                pollxro\n"
            + "                pollxrl\n"
            + "                pollatn\n"
            + "                pollqmt\n"
            + "                pollint wc\n"
            + "                pollint wz\n"
            + "                pollint wcz\n"
            + "";

        Assertions.assertEquals(""
            + "FD600024              pollint \n"
            + "FD600224              pollct1 \n"
            + "FD600424              pollct2 \n"
            + "FD600624              pollct3 \n"
            + "FD600824              pollse1 \n"
            + "FD600A24              pollse2 \n"
            + "FD600C24              pollse3 \n"
            + "FD600E24              pollse4 \n"
            + "FD601024              pollpat \n"
            + "FD601224              pollfbw \n"
            + "FD601424              pollxmt \n"
            + "FD601624              pollxfi \n"
            + "FD601824              pollxro \n"
            + "FD601A24              pollxrl \n"
            + "FD601C24              pollatn \n"
            + "FD601E24              pollqmt \n"
            + "FD700024              pollint  wc\n"
            + "FD680024              pollint  wz\n"
            + "FD780024              pollint  wcz\n"
            + "", compileAndDisassemble(text));
    }

    @Test
    void testEventsWait() throws Exception {
        String text = ""
            + "DAT             org     $000\n"
            + "\n"
            + "                waitint\n"
            + "                waitct1\n"
            + "                waitct2\n"
            + "                waitct3\n"
            + "                waitse1\n"
            + "                waitse2\n"
            + "                waitse3\n"
            + "                waitse4\n"
            + "                waitpat\n"
            + "                waitfbw\n"
            + "                waitxmt\n"
            + "                waitxfi\n"
            + "                waitxro\n"
            + "                waitxrl\n"
            + "                waitatn\n"
            + "                waitqmt\n"
            + "                waitint wc\n"
            + "                waitint wz\n"
            + "                waitint wcz\n"
            + "";

        Assertions.assertEquals(""
            + "FD602024              waitint \n"
            + "FD602224              waitct1 \n"
            + "FD602424              waitct2 \n"
            + "FD602624              waitct3 \n"
            + "FD602824              waitse1 \n"
            + "FD602A24              waitse2 \n"
            + "FD602C24              waitse3 \n"
            + "FD602E24              waitse4 \n"
            + "FD603024              waitpat \n"
            + "FD603224              waitfbw \n"
            + "FD603424              waitxmt \n"
            + "FD603624              waitxfi \n"
            + "FD603824              waitxro \n"
            + "FD603A24              waitxrl \n"
            + "FD603C24              waitatn \n"
            + "FD702024              waitint  wc\n"
            + "FD682024              waitint  wz\n"
            + "FD782024              waitint  wcz\n"
            + "", compileAndDisassemble(text));
    }

    @Test
    void testInterrupts() throws Exception {
        String text = ""
            + "DAT             org     $000\n"
            + "\n"
            + "                allowi\n"
            + "                stalli\n"
            + "                trgint1\n"
            + "                trgint2\n"
            + "                trgint3\n"
            + "                nixint1\n"
            + "                nixint2\n"
            + "                nixint3\n"
            + "                setint1 1\n"
            + "                setint2 1\n"
            + "                setint3 1\n"
            + "                getbrk  1 wc\n"
            + "                cogbrk  1\n"
            + "                brk     1\n"
            + "";

        Assertions.assertEquals(""
            + "FD604024              allowi  \n"
            + "FD604224              stalli  \n"
            + "FD604424              trgint1 \n"
            + "FD604624              trgint2 \n"
            + "FD604824              trgint3 \n"
            + "FD604A24              nixint1 \n"
            + "FD604C24              nixint2 \n"
            + "FD604E24              nixint3 \n"
            + "FD600225              setint1 $001\n"
            + "FD600226              setint2 $001\n"
            + "FD600227              setint3 $001\n"
            + "FD700235              cogbrk  $001 wc\n"
            + "FD600235              cogbrk  $001\n"
            + "FD600236              brk     $001\n"
            + "", compileAndDisassemble(text));
    }

    @Test
    void testMisc() throws Exception {
        String text = ""
            + "DAT             org     $000\n"
            + "\n"
            + "                nop\n"
            + "                setq    1\n"
            + "                setq2   1\n"
            + "                push    1\n"
            + "                pop     1\n"
            + "                pop     1 wc\n"
            + "                pop     1 wz\n"
            + "                pop     1 wcz\n"
            + "                getct   1\n"
            + "                getct   1 wc\n"
            + "                getrnd  1\n"
            + "                getrnd  1 wc\n"
            + "                getrnd  1 wz\n"
            + "                getrnd  1 wcz\n"
            + "                waitx   1\n"
            + "                waitx   1 wc\n"
            + "                waitx   1 wz\n"
            + "                waitx   1 wcz\n"
            + "                augs    #1\n"
            + "                augd    #1\n"
            + "";

        Assertions.assertEquals(""
            + "00000000              nop\n"
            + "FD600228              setq    $001\n"
            + "FD600229              setq2   $001\n"
            + "FD60022A              push    $001\n"
            + "FD60022B              pop     $001\n"
            + "FD70022B              pop     $001 wc\n"
            + "FD68022B              pop     $001 wz\n"
            + "FD78022B              pop     $001 wcz\n"
            + "FD60021A              getct   $001\n"
            + "FD70021A              getct   $001 wc\n"
            + "FD60021B              getrnd  $001\n"
            + "FD70021B              getrnd  $001 wc\n"
            + "FD68021B              getrnd  $001 wz\n"
            + "FD78021B              getrnd  $001 wcz\n"
            + "FD60021F              waitx   $001\n"
            + "FD70021F              waitx   $001 wc\n"
            + "FD68021F              waitx   $001 wz\n"
            + "FD78021F              waitx   $001 wcz\n"
            + "FF000000              augs    #$000000xx\n"
            + "FF800000              augd    #$000000xx\n"
            + "", compileAndDisassemble(text));
    }

    @Test
    void testPtr() throws Exception {
        String text = ""
            + "DAT    wrlong 1,ptra\n"
            + "       wrlong #1,ptra++\n"
            + "       wrlong #1,++ptra\n"
            + "       wrlong 1,ptra[3]\n"
            + "       wrlong 1,ptra--[3]\n"
            + "       wrlong 1,--ptra[3]\n"
            + "";

        Assertions.assertEquals(""
            + "FC640300              wrlong  $001, ptra\n"
            + "FC6C0361              wrlong  #$001, ptra++\n"
            + "FC6C0341              wrlong  #$001, ++ptra\n"
            + "FC640303              wrlong  $001, ptra[3]\n"
            + "FC64037D              wrlong  $001, ptra--[3]\n"
            + "FC64035D              wrlong  $001, --ptra[3]\n"
            + "", compileAndDisassemble(text));
    }

    @Test
    void testStreamer() throws Exception {
        String text = ""
            + "DAT             org     $000\n"
            + "\n"
            + "                xinit   1, 2\n"
            + "                xstop\n"
            + "                xzero   1, 2\n"
            + "                xcont   1, 2\n"
            + "                setxfrq 1\n"
            + "                getxacc 1\n"
            + "";

        Assertions.assertEquals(""
            + "FCA00202              xinit   $001, $002\n"
            + "FCAC0000              xstop   \n"
            + "FCB00202              xzero   $001, $002\n"
            + "FCC00202              xcont   $001, $002\n"
            + "FD60021D              setxfrq $001\n"
            + "FD60021E              getxacc $001\n"
            + "", compileAndDisassemble(text));
    }

    @Test
    void testBranchRepeat() throws Exception {
        String text = ""
            + "DAT             org     $000\n"
            + "\n"
            + "                rep     1, 2\n"
            + "                rep     1, #2\n"
            + "                rep     #1, 2\n"
            + "                rep     #1, #2\n"
            + "";

        Assertions.assertEquals(""
            + "FCD00202              rep     $001, $002\n"
            + "FCD40202              rep     $001, #$002\n"
            + "FCD80202              rep     #$001, $002\n"
            + "FCDC0202              rep     #$001, #$002\n"
            + "", compileAndDisassemble(text));
    }

    @Test
    void testPins() throws Exception {
        String text = ""
            + "DAT             org     $000\n"
            + "\n"
            + "                testp   1 wc\n"
            + "                testpn  1 wz\n"
            + "                testp   1 andc\n"
            + "                testpn  1 andz\n"
            + "                testp   1 orc\n"
            + "                testpn  1 orz\n"
            + "                testp   1 xorc\n"
            + "                testpn  1 xorz\n"
            + "                dirl    1\n"
            + "                dirh    1 wcz\n"
            + "                dirc    1\n"
            + "                dirnc   1 wcz\n"
            + "                dirz    1\n"
            + "                dirnz   1 wcz\n"
            + "                dirrnd  1\n"
            + "                dirnot  1 wcz\n"
            + "                outl    1\n"
            + "                outh    1 wcz\n"
            + "                outc    1\n"
            + "                outnc   1 wcz\n"
            + "                outz    1\n"
            + "                outnz   1 wcz\n"
            + "                outrnd  1\n"
            + "                outnot  1 wcz\n"
            + "                fltl    1\n"
            + "                flth    1 wcz\n"
            + "                fltc    1\n"
            + "                fltnc   1 wcz\n"
            + "                fltz    1\n"
            + "                fltnz   1 wcz\n"
            + "                fltrnd  1\n"
            + "                fltnot  1 wcz\n"
            + "                drvl    1\n"
            + "                drvh    1 wcz\n"
            + "                drvc    1\n"
            + "                drvnc   1 wcz\n"
            + "                drvz    1\n"
            + "                drvnz   1 wcz\n"
            + "                drvrnd  1\n"
            + "                drvnot  1 wcz\n"
            + "";

        Assertions.assertEquals(""
            + "FD700240              testp   $001 wc\n"
            + "FD680241              testpn  $001 wz\n"
            + "FD700242              testp   $001 andc\n"
            + "FD680243              testpn  $001 andz\n"
            + "FD700244              testp   $001 orc\n"
            + "FD680245              testpn  $001 orz\n"
            + "FD700246              testp   $001 xorc\n"
            + "FD680247              testpn  $001 xorz\n"
            + "FD600240              dirl    $001\n"
            + "FD780241              dirh    $001 wcz\n"
            + "FD600242              dirc    $001\n"
            + "FD780243              dirnc   $001 wcz\n"
            + "FD600244              dirz    $001\n"
            + "FD780245              dirnz   $001 wcz\n"
            + "FD600246              dirrnd  $001\n"
            + "FD780247              dirnot  $001 wcz\n"
            + "FD600248              outl    $001\n"
            + "FD780249              outh    $001 wcz\n"
            + "FD60024A              outc    $001\n"
            + "FD78024B              outnc   $001 wcz\n"
            + "FD60024C              outz    $001\n"
            + "FD78024D              outnz   $001 wcz\n"
            + "FD60024E              outrnd  $001\n"
            + "FD78024F              outnot  $001 wcz\n"
            + "FD600250              fltl    $001\n"
            + "FD780251              flth    $001 wcz\n"
            + "FD600252              fltc    $001\n"
            + "FD780253              fltnc   $001 wcz\n"
            + "FD600254              fltz    $001\n"
            + "FD780255              fltnz   $001 wcz\n"
            + "FD600256              fltrnd  $001\n"
            + "FD780257              fltnot  $001 wcz\n"
            + "FD600258              drvl    $001\n"
            + "FD780259              drvh    $001 wcz\n"
            + "FD60025A              drvc    $001\n"
            + "FD78025B              drvnc   $001 wcz\n"
            + "FD60025C              drvz    $001\n"
            + "FD78025D              drvnz   $001 wcz\n"
            + "FD60025E              drvrnd  $001\n"
            + "FD78025F              drvnot  $001 wcz\n"
            + "", compileAndDisassemble(text));
    }

    @Test
    void testInstructionPrefix() throws Exception {
        String text = ""
            + "DAT             org     $000\n"
            + "\n"
            + "  _ret_         mov 1, #2\n"
            + "  if_nc_and_nz  mov 1, #2\n"
            + "  if_nz_and_nc  mov 1, #2\n"
            + "  if_gt         mov 1, #2\n"
            + "  if_a          mov 1, #2\n"
            + "  if_00         mov 1, #2\n"
            + "  if_nc_and_z   mov 1, #2\n"
            + "  if_z_and_nc   mov 1, #2\n"
            + "  if_01         mov 1, #2\n"
            + "  if_nc         mov 1, #2\n"
            + "  if_ge         mov 1, #2\n"
            + "  if_ae         mov 1, #2\n"
            + "  if_0x         mov 1, #2\n"
            + "  if_c_and_nz   mov 1, #2\n"
            + "  if_nz_and_c   mov 1, #2\n"
            + "  if_10         mov 1, #2\n"
            + "  if_nz         mov 1, #2\n"
            + "  if_ne         mov 1, #2\n"
            + "  if_x0         mov 1, #2\n"
            + "  if_c_ne_z     mov 1, #2\n"
            + "  if_z_ne_c     mov 1, #2\n"
            + "  if_diff       mov 1, #2\n"
            + "  if_nc_or_nz   mov 1, #2\n"
            + "  if_nz_or_nc   mov 1, #2\n"
            + "  if_not_11     mov 1, #2\n"
            + "  if_c_and_z    mov 1, #2\n"
            + "  if_z_and_c    mov 1, #2\n"
            + "  if_11         mov 1, #2\n"
            + "  if_c_eq_z     mov 1, #2\n"
            + "  if_z_eq_c     mov 1, #2\n"
            + "  if_same       mov 1, #2\n"
            + "  if_z          mov 1, #2\n"
            + "  if_e          mov 1, #2\n"
            + "  if_x1         mov 1, #2\n"
            + "  if_nc_or_z    mov 1, #2\n"
            + "  if_z_or_nc    mov 1, #2\n"
            + "  if_not_10     mov 1, #2\n"
            + "  if_c          mov 1, #2\n"
            + "  if_lt         mov 1, #2\n"
            + "  if_b          mov 1, #2\n"
            + "  if_1x         mov 1, #2\n"
            + "  if_c_or_nz    mov 1, #2\n"
            + "  if_nz_or_c    mov 1, #2\n"
            + "  if_not_01     mov 1, #2\n"
            + "  if_c_or_z     mov 1, #2\n"
            + "  if_z_or_c     mov 1, #2\n"
            + "  if_le         mov 1, #2\n"
            + "  if_be         mov 1, #2\n"
            + "  if_not_00     mov 1, #2\n"
            + "  if_always     mov 1, #2\n"
            + "";

        Assertions.assertEquals(""
            + "06040202 _ret_        mov     $001, #$002\n"
            + "16040202 if_nc_and_nz mov     $001, #$002\n"
            + "16040202 if_nc_and_nz mov     $001, #$002\n"
            + "16040202 if_nc_and_nz mov     $001, #$002\n"
            + "16040202 if_nc_and_nz mov     $001, #$002\n"
            + "16040202 if_nc_and_nz mov     $001, #$002\n"
            + "26040202 if_nc_and_z  mov     $001, #$002\n"
            + "26040202 if_nc_and_z  mov     $001, #$002\n"
            + "26040202 if_nc_and_z  mov     $001, #$002\n"
            + "36040202 if_nc        mov     $001, #$002\n"
            + "36040202 if_nc        mov     $001, #$002\n"
            + "36040202 if_nc        mov     $001, #$002\n"
            + "36040202 if_nc        mov     $001, #$002\n"
            + "46040202 if_c_and_nz  mov     $001, #$002\n"
            + "46040202 if_c_and_nz  mov     $001, #$002\n"
            + "46040202 if_c_and_nz  mov     $001, #$002\n"
            + "56040202 if_nz        mov     $001, #$002\n"
            + "56040202 if_nz        mov     $001, #$002\n"
            + "56040202 if_nz        mov     $001, #$002\n"
            + "66040202 if_c_ne_z    mov     $001, #$002\n"
            + "66040202 if_c_ne_z    mov     $001, #$002\n"
            + "66040202 if_c_ne_z    mov     $001, #$002\n"
            + "76040202 if_nc_or_nz  mov     $001, #$002\n"
            + "76040202 if_nc_or_nz  mov     $001, #$002\n"
            + "76040202 if_nc_or_nz  mov     $001, #$002\n"
            + "86040202 if_c_and_z   mov     $001, #$002\n"
            + "86040202 if_c_and_z   mov     $001, #$002\n"
            + "86040202 if_c_and_z   mov     $001, #$002\n"
            + "96040202 if_c_eq_z    mov     $001, #$002\n"
            + "96040202 if_c_eq_z    mov     $001, #$002\n"
            + "96040202 if_c_eq_z    mov     $001, #$002\n"
            + "A6040202 if_z         mov     $001, #$002\n"
            + "A6040202 if_z         mov     $001, #$002\n"
            + "A6040202 if_z         mov     $001, #$002\n"
            + "B6040202 if_nc_or_z   mov     $001, #$002\n"
            + "B6040202 if_nc_or_z   mov     $001, #$002\n"
            + "B6040202 if_nc_or_z   mov     $001, #$002\n"
            + "C6040202 if_c         mov     $001, #$002\n"
            + "C6040202 if_c         mov     $001, #$002\n"
            + "C6040202 if_c         mov     $001, #$002\n"
            + "C6040202 if_c         mov     $001, #$002\n"
            + "D6040202 if_c_or_nz   mov     $001, #$002\n"
            + "D6040202 if_c_or_nz   mov     $001, #$002\n"
            + "D6040202 if_c_or_nz   mov     $001, #$002\n"
            + "E6040202 if_c_or_z    mov     $001, #$002\n"
            + "E6040202 if_c_or_z    mov     $001, #$002\n"
            + "E6040202 if_c_or_z    mov     $001, #$002\n"
            + "E6040202 if_c_or_z    mov     $001, #$002\n"
            + "E6040202 if_c_or_z    mov     $001, #$002\n"
            + "F6040202              mov     $001, #$002\n"
            + "", compileAndDisassemble(text));
    }

    @Test
    void testHubFifo() throws Exception {
        String text = ""
            + "DAT             org     $000\n"
            + "\n"
            + "                rdfast  1, 2\n"
            + "                wrfast  1, 2\n"
            + "                fblock  1, 2\n"
            + "                rfbyte  1\n"
            + "                rfword  1\n"
            + "                rflong  1\n"
            + "                rfvar   1\n"
            + "                rfvars  1\n"
            + "                wfbyte  1\n"
            + "                wfword  1\n"
            + "                wflong  1\n"
            + "                getptr  1\n"
            + "";

        Assertions.assertEquals(""
            + "FC700202              rdfast  $001, $002\n"
            + "FC800202              wrfast  $001, $002\n"
            + "FC900202              fblock  $001, $002\n"
            + "FD600210              rfbyte  $001\n"
            + "FD600211              rfword  $001\n"
            + "FD600212              rflong  $001\n"
            + "FD600213              rfvar   $001\n"
            + "FD600214              rfvars  $001\n"
            + "FD600215              wfbyte  $001\n"
            + "FD600216              wfword  $001\n"
            + "FD600217              wflong  $001\n"
            + "FD600234              getptr  $001\n"
            + "", compileAndDisassemble(text));
    }

    @Test
    void testCordic() throws Exception {
        String text = ""
            + "DAT             org     $000\n"
            + "\n"
            + "                qmul    1, 2\n"
            + "                qdiv    1, 2\n"
            + "                qfrac   1, 2\n"
            + "                qsqrt   1, 2\n"
            + "                qrotate 1, 2\n"
            + "                qvector 1, 2\n"
            + "                qlog    1\n"
            + "                qexp    1\n"
            + "                getqx   1\n"
            + "                getqy   1\n"
            + "";

        Assertions.assertEquals(""
            + "FD000202              qmul    $001, $002\n"
            + "FD100202              qdiv    $001, $002\n"
            + "FD200202              qfrac   $001, $002\n"
            + "FD300202              qsqrt   $001, $002\n"
            + "FD400202              qrotate $001, $002\n"
            + "FD500202              qvector $001, $002\n"
            + "FD60020E              qlog    $001\n"
            + "FD60020F              qexp    $001\n"
            + "FD600218              getqx   $001\n"
            + "FD600219              getqy   $001\n"
            + "", compileAndDisassemble(text));
    }

    @Test
    void testLut() throws Exception {
        String text = ""
            + "DAT             org     $000\n"
            + "\n"
            + "                rdlut   1, 2 wz\n"
            + "                wrlut   1, 2\n"
            + "                setluts 1\n"
            + "";

        Assertions.assertEquals(""
            + "FAA80202              rdlut   $001, $002 wz\n"
            + "FC300202              wrlut   $001, $002\n"
            + "FD600237              setluts $001\n"
            + "", compileAndDisassemble(text));
    }

    @Test
    void testSmartPins() throws Exception {
        String text = ""
            + "DAT             org     $000\n"
            + "\n"
            + "                rqpin   1, 2\n"
            + "                rdpin   1, 2\n"
            + "                akpin   1\n"
            + "                wrpin   1, 2\n"
            + "                wxpin   1, 2\n"
            + "                wypin   1, 2\n"
            + "                setdacs 1\n"
            + "                setscp  1\n"
            + "                getscp  1\n"
            + "";

        Assertions.assertEquals(""
            + "FA800202              rqpin   $001, $002\n"
            + "FA880202              rdpin   $001, $002\n"
            + "FC080201              akpin   $001\n"
            + "FC000202              wrpin   $001, $002\n"
            + "FC100202              wxpin   $001, $002\n"
            + "FC200202              wypin   $001, $002\n"
            + "FD60021C              setdacs $001\n"
            + "FD600270              setscp  $001\n"
            + "FD600271              getscp  $001\n"
            + "", compileAndDisassemble(text));
    }

    @Test
    void testHubControl() throws Exception {
        String text = ""
            + "DAT             org     $000\n"
            + "\n"
            + "                coginit 1, 2\n"
            + "                hubset  1\n"
            + "                cogid   1\n"
            + "                cogstop 1\n"
            + "                locknew 1\n"
            + "                lockret 1\n"
            + "                locktry 1\n"
            + "                lockrel 1\n"
            + "";

        Assertions.assertEquals(""
            + "FCE00202              coginit $001, $002\n"
            + "FD600200              hubset  $001\n"
            + "FD600201              cogid   $001\n"
            + "FD600203              cogstop $001\n"
            + "FD600204              locknew $001\n"
            + "FD600205              lockret $001\n"
            + "FD600206              locktry $001\n"
            + "FD600207              lockrel $001\n"
            + "", compileAndDisassemble(text));
    }

    @Test
    void testColorspaceConv() throws Exception {
        String text = ""
            + "DAT             org     $000\n"
            + "\n"
            + "                setcy   1\n"
            + "                setci   1\n"
            + "                setcq   1\n"
            + "                setcfrq 1\n"
            + "                setcmod 1\n"
            + "";

        Assertions.assertEquals(""
            + "FD600238              setcy   $001\n"
            + "FD600239              setci   $001\n"
            + "FD60023A              setcq   $001\n"
            + "FD60023B              setcfrq $001\n"
            + "FD60023C              setcmod $001\n"
            + "", compileAndDisassemble(text));
    }

    String compileAndDisassemble(String text) throws Exception {
        Spin2Parser parser = new Spin2Parser(text);
        RootNode root = parser.parse();

        Spin2Compiler compiler = new Spin2Compiler();
        compiler.setDebugEnabled(false);
        Spin2ObjectCompiler objectCompiler = new Spin2ObjectCompiler(compiler, new File("test.spin2"));
        Spin2Object obj = objectCompiler.compileObject(root);

        for (CompilerException msg : objectCompiler.getMessages()) {
            if (msg.type == CompilerException.ERROR) {
                throw msg;
            }
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        obj.generateBinary(os);
        byte[] b = os.toByteArray();

        StringBuilder sb = new StringBuilder();

        int i = 0;
        while (i < b.length) {
            int ins = (b[i] & 0xFF) | (b[i + 1] & 0xFF) << 8 | (b[i + 2] & 0xFF) << 16 | (b[i + 3] & 0xFF) << 24;
            sb.append(String.format("%08X ", ins));
            sb.append(P2Disassembler.disassemble(i / 4, ins));
            sb.append(System.lineSeparator());
            i += 4;
        }

        return sb.toString();
    }

}
