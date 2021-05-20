grammar Spin2;

prog
    : NL* (constants | objects | variables | data)* EOF
    ;

/*

CON  EnableFlow = 8                'single assignments
     DisableFlow = 4
     ColorBurstFreq = 3_579_545

     PWM_base = 8
     PWM_pins = PWM_base ADDPINS 7

     x = 5, y = -5, z = 1          'comma-separated assignments

     HalfPi = 1.5707963268         'single-precision float values
     QuarPi = HalfPi / 2.0

     j = ROUND(4000.0 / QuarPi)    'float to integer

CON  #0,a,b,c,d           'a=0, b=1, c=2, d=3    (start=0, step=1)
     #1,e,f,g,h           'e=1, f=2, g=3, h=4    (start=1, step=1)

     #4[2],i,j,k,l        'i=4, j=6, k=8, l=10   (start=4, step=2)
     #-1[-1],m,n,p        'm=-1, n=-2, p=-3      (start=-1, step=-1)

     #16                  'start=16, step=1
     q                    'q=16
     r[0]                 'r=17   ([0] is a step multiplier)
     s                    's=17
     t                    't=18
     u[2]                 'u=19   ([2] is a step multiplier)
     v                    'v=21
     w                    'w=22

CON  e0,e1,e2             'e0=0, e1=1, e2=2      (start=0, step=1)
                          '..enumeration is reset at each CON

 */

constants: ('CON' | 'con')+ NL* constant* ;

constant
    : (',')? name=VARIABLE '=' exp=expression NL*
    | (',')? name=VARIABLE ('[' multiplier=expression ']')? NL*
    | '#' start=expression ('[' step=expression ']')? NL*
    ;

/*

OBJ  vga       : "VGA_Driver"     'instantiate "VGA_Driver.spin2" as "vga"

     mouse     : "USB_Mouse"      'instantiate "USB_Mouse.spin2" as "mouse"

     v[16]     : "VocalSynth"     'instantiate an array of 16 objects
                                  '..v[0] through v[15]

 */

objects: ('OBJ' | 'obj')+ NL* object* ;

object: reference ('[' expression ']')* ':' filename NL+ ;

reference: VARIABLE ;

filename: STRING ;

/*

VAR  CogNum                     'The default variable size is LONG (32 bits).
     CursorMode
     PosX                       'The first 15 longs have special bytecodes for faster/smaller code.
     Posy
     SendPtr                    'So, declare your most common variables first, as longs.

     BYTE StringChr             'byte variable (8 bits)
     BYTE StringBuff[64]        'byte variable array (64 bytes)
     BYTE a,b,c[1000],d         'comma-separated declarations

     WORD CurrentCycle          'word variable (16 bits)
     WORD Cycles[200]           'word variable array (200 words)

 */

variables: ('VAR' | 'var')+ NL* variable* ;

variable
    : type? VARIABLE ('[' expression ']')? (',' VARIABLE ('[' expression ']')? )* NL+ 
    ;

/*

DAT             ORG                   'begin a cog-exec program (no symbol allowed before ORG)
                                      'COGINIT(16, @IncPins, 0) will launch this program in a free cog
IncPins         MOV     DIRA,#$FF     'to Spin2 code, IncPins is the 'MOV' instruction (long)
Loop            ADD     OUTA,#1       'to Spin2 code, @IncPins is the hub address of the 'MOV' instruction
                JMP     #Loop         'to PASM code, Loop is the cog address ($001) of the 'ADD' instruction
                ORG                   'set cog-exec mode, cog address = $000, cog limit = $1F8 (reg, both defaults)
                ORG     $100          'set cog-exec mode, cog address = $100, cog limit = $1F8 (reg, default limit)
                ORG     $120,$140     'set cog-exec mode, cog address = $120, cog limit = $140 (reg)
                ORG     $200          'set cog-exec mode, cog address = $200, cog limit = $400 (LUT, default limit)
                ORG     $300,$380     'set cog-exec mode, cog address = $300, cog limit = $380 (LUT)
                ADD     reg,#1        'in cog-exec mode, instructions force alignment to cog/LUT registers
                ORGF    $040          'fill to cog address $040 with zeros (no symbol allowed before ORGF)
                FIT     $020          'test to make sure cog address has not exceeded $020
x               RES     1             'reserve 1 register, advance cog address by 1, don't advance hub address
y               RES     1             'reserve 1 register, advance cog address by 1, don't advance hub address
z               RES     1             'reserve 1 register, advance cog address by 1, don't advance hub address
buff            RES     16            'reserve 16 registers, advance cog address by 16, don't advance hub address

 */

data: ('DAT' | 'dat')+ NL* (directive | dataLine)* ;

dataLine
    : label NL+
    | label? condition? opcode argument ',' argument ',' argument effect? NL+
    | label? condition? opcode argument ',' argument effect? NL+
    | label? condition? opcode argument effect? NL+
    | label? condition? opcode effect? NL+
    | label? directive dataValue (',' dataValue)* NL+
    ;

typeValue: value=expression ('[' count=expression ']')? ;

dataValue: expression ('[' count=expression ']')? ;

directive
    : 'LONG'    | 'long'
    | 'WORD'    | 'word'
    | 'BYTE'    | 'byte'
    | 'RES'     | 'res'
    ;

label: {_input.LT(1).getCharPositionInLine() == 0}? '.'? VARIABLE ;

opcode
    : 'ORG'     | 'org'
    | 'ORGH'    | 'orgh'
    | 'ORGF'    | 'orgf'
    | 'FIT'     | 'fit'
    | 'NOP'     | 'nop'
    | 'ROR'     | 'ror'
    | 'ROL'     | 'rol'
    | 'SHR'     | 'shr'
    | 'SHL'     | 'shl'
    | 'RCR'     | 'rcr'
    | 'RCL'     | 'rcl'
    | 'SAR'     | 'sar'
    | 'SAL'     | 'sal'
    | 'ADD'     | 'add'
    | 'ADDX'    | 'addx'
    | 'ADDS'    | 'adds'
    | 'ADDSX'   | 'addsx'
    | 'SUB'     | 'sub'
    | 'SUBX'    | 'subx'
    | 'SUBS'    | 'subs'
    | 'SUBSX'   | 'subsx'
    | 'CMP'     | 'cmp'
    | 'CMPX'    | 'cmpx'
    | 'CMPS'    | 'cmps'
    | 'CMPSX'   | 'cmpsx'
    | 'CMPR'    | 'cmpr'
    | 'CMPM'    | 'cmpm'
    | 'SUBR'    | 'subr'
    | 'CMPSUB'  | 'cmpsub'
    | 'FGE'     | 'fge'
    | 'FLE'     | 'fle'
    | 'FGES'    | 'fges'
    | 'FLES'    | 'fles'
    | 'SUMC'    | 'sumc'
    | 'SUMNC'   | 'sumnc'
    | 'SUMZ'    | 'sumz'
    | 'SUMNZ'   | 'sumnz'
    | 'TESTB'   | 'testb'
    | 'TESTBN'  | 'testbn'
    | 'BITL'    | 'bitl'
    | 'BITH'    | 'bith'
    | 'BITC'    | 'bitc'
    | 'BITNC'   | 'bitnc'
    | 'BITZ'    | 'bitz'
    | 'BITNZ'   | 'bitnz'
    | 'BITRND'  | 'bitrnd'
    | 'BITNOT'  | 'bitnot'
    | 'AND'     | 'and'
    | 'ANDN'    | 'andn'
    | 'OR'      | 'or'
    | 'XOR'     | 'xor'
    | 'MUXC'    | 'muxc'
    | 'MUXNC'   | 'muxnc'
    | 'MUXZ'    | 'muxz'
    | 'MUXNZ'   | 'muxnz'
    | 'MOV'     | 'mov'
    | 'NOT'     | 'not'
    | 'ABS'     | 'abs'
    | 'NEG'     | 'neg'
    | 'NEGC'    | 'negc'
    | 'NEGNC'   | 'negnc'
    | 'NEGZ'    | 'negz'
    | 'NEGNZ'   | 'negnz'
    | 'INCMOD'  | 'incmod'
    | 'DECMOD'  | 'decmod'
    | 'ZEROX'   | 'zerox'
    | 'SIGNX'   | 'signx'
    | 'ENCOD'   | 'encod'
    | 'ONES'    | 'ones'
    | 'TEST'    | 'test'
    | 'TESTN'   | 'testn'
    | 'SETNIB'  | 'setnib'
    | 'GETNIB'  | 'getnib'
    | 'ROLNIB'  | 'rolnib'
    | 'SETBYTE' | 'setbyte'
    | 'GETBYTE' | 'getbyte'
    | 'ROLBYTE' | 'rolbyte'
    | 'SETWORD' | 'setword'
    | 'GETWORD' | 'getword'
    | 'ROLWORD' | 'rolword'
    | 'ALTSN'   | 'altsn'
    | 'ALTGN'   | 'altgn'
    | 'ALTSB'   | 'altsb'
    | 'ALTGB'   | 'altgb'
    | 'ALTSW'   | 'altsw'
    | 'ALTGW'   | 'altgw'
    | 'ALTR'    | 'altr'
    | 'ALTD'    | 'altd'
    | 'ALTS'    | 'alts'
    | 'ALTB'    | 'altb'
    | 'ALTI'    | 'alti'
    | 'SETR'    | 'setr'
    | 'SETD'    | 'setd'
    | 'SETS'    | 'sets'
    | 'DECOD'   | 'decod'
    | 'BMASK'   | 'bmask'
    | 'CRCBIT'  | 'crcbit'
    | 'CRCNIB'  | 'crcnib'
    | 'MUXNITS' | 'muxnits'
    | 'MUXNIBS' | 'muxnibs'
    | 'MUXQ'    | 'muxq'
    | 'MOVBYTS' | 'movbyts'
    | 'MUL'     | 'mul'
    | 'MULS'    | 'muls'
    | 'SCA'     | 'sca'
    | 'SCAS'    | 'scas'
    | 'ADDPIX'  | 'addpix'
    | 'MULPIX'  | 'mulpix'
    | 'BLNPIX'  | 'blnpix'
    | 'MIXPIX'  | 'mixpix'
    | 'ADDCT1'  | 'addct1'
    | 'ADDCT2'  | 'addct2'
    | 'ADDCT3'  | 'addct3'
    | 'WMLONG'  | 'wmlong'
    | 'RQPIN'   | 'rqpin'
    | 'RDPIN'   | 'rdpin'
    | 'RDLUT'   | 'rdlut'
    | 'RDBYTE'  | 'rdbyte'
    | 'RDWORD'  | 'rdword'
    | 'RDLONG'  | 'rdlong'
    | 'POPA'    | 'popa'
    | 'POPB'    | 'popb'
    | 'CALLD'   | 'calld'
    | 'RESI3'   | 'resi3'
    | 'RESI2'   | 'resi2'
    | 'RESI1'   | 'resi1'
    | 'RESI0'   | 'resi0'
    | 'RETI3'   | 'reti3'
    | 'RETI2'   | 'reti2'
    | 'RETI1'   | 'reti1'
    | 'RETI0'   | 'reti0'
    | 'CALLPA'  | 'callpa'
    | 'CALLPB'  | 'callpb'
    | 'DJZ'     | 'djz'
    | 'DJNZ'    | 'djnz'
    | 'DJF'     | 'djf'
    | 'DJNF'    | 'djnf'
    | 'IJZ'     | 'ijz'
    | 'IJNZ'    | 'ijnz'
    | 'TJZ'     | 'tjz'
    | 'TJNZ'    | 'tjnz'
    | 'TJF'     | 'tjf'
    | 'TJNF'    | 'tjnf'
    | 'TJS'     | 'tjs'
    | 'TJNS'    | 'tjns'
    | 'TJV'     | 'tjv'
    | 'JINT'    | 'jint'
    | 'JCT1'    | 'jct1'
    | 'JCT2'    | 'jct2'
    | 'JCT3'    | 'jct3'
    | 'JSE1'    | 'jse1'
    | 'JSE2'    | 'jse2'
    | 'JSE3'    | 'jse3'
    | 'JSE4'    | 'jse4'
    | 'JPAT'    | 'jpat'
    | 'JFBW'    | 'jfbw'
    | 'JXMT'    | 'jxmt'
    | 'JXFI'    | 'jxfi'
    | 'JXRO'    | 'jxro'
    | 'JXRL'    | 'jxrl'
    | 'JATN'    | 'jatn'
    | 'JQMT'    | 'jqmt'
    | 'JNINT'   | 'jnint'
    | 'JNCT1'   | 'jnct1'
    | 'JNCT2'   | 'jnct2'
    | 'JNCT3'   | 'jnct3'
    | 'JNSE1'   | 'jnse1'
    | 'JNSE2'   | 'jnse2'
    | 'JNSE3'   | 'jnse3'
    | 'JNSE4'   | 'jnse4'
    | 'JNPAT'   | 'jnpat'
    | 'JNFBW'   | 'jnfbw'
    | 'JNXMT'   | 'jnxmt'
    | 'JNXFI'   | 'jnxfi'
    | 'JNXRO'   | 'jnxro'
    | 'JNXRL'   | 'jnxrl'
    | 'JNATN'   | 'jnatn'
    | 'JNQMT'   | 'jnqmt'
    | 'SETPAT'  | 'setpat'
    | 'AKPIN'   | 'akpin'
    | 'WRPIN'   | 'wrpin'
    | 'WXPIN'   | 'wxpin'
    | 'WYPIN'   | 'wypin'
    | 'WRLUT'   | 'wrlut'
    | 'WRBYTE'  | 'wrbyte'
    | 'WRWORD'  | 'wrword'
    | 'WRLONG'  | 'wrlong'
    | 'PUSHA'   | 'pusha'
    | 'PUSHB'   | 'pushb'
    | 'RDFAST'  | 'rdfast'
    | 'WRFAST'  | 'wrfast'
    | 'FBLOCK'  | 'fblock'
    | 'XINIT'   | 'xinit'
    | 'XSTOP'   | 'xstop'
    | 'XZERO'   | 'xzero'
    | 'XCONT'   | 'xcont'
    | 'REP'     | 'rep'
    | 'COGINIT' | 'coginit'
    | 'QMUL'    | 'qmul'
    | 'QDIV'    | 'qdiv'
    | 'QFRAC'   | 'qfrac'
    | 'QSQRT'   | 'qsqrt'
    | 'QROTATE' | 'qrotate'
    | 'QVECTOR' | 'qvector'
    | 'HUBSET'  | 'hubset'
    | 'COGID'   | 'cogid'
    | 'COGSTOP' | 'cogstop'
    | 'LOCKNEW' | 'locknew'
    | 'LOCKRET' | 'lockret'
    | 'LOCKTRY' | 'locktry'
    | 'LOCKREL' | 'lockrel'
    | 'QLOG'    | 'qlog'
    | 'QEXP'    | 'qexp'
    | 'RFBYTE'  | 'rfbyte'
    | 'RFWORD'  | 'rfword'
    | 'RFLONG'  | 'rflong'
    | 'RFVAR'   | 'rfvar'
    | 'RFVARS'  | 'rfvars'
    | 'WFBYTE'  | 'wfbyte'
    | 'WFWORD'  | 'wfword'
    | 'WFLONG'  | 'wflong'
    | 'GETQX'   | 'getqx'
    | 'GETQY'   | 'getqy'
    | 'GETCT'   | 'getct'
    | 'GETRND'  | 'getrnd'
    | 'SETDACS' | 'setdacs'
    | 'SETXFRQ' | 'setxfrq'
    | 'GETXACC' | 'getxacc'
    | 'WAITX'   | 'waitx'
    | 'SETSE1'  | 'setse1'
    | 'SETSE2'  | 'setse2'
    | 'SETSE3'  | 'setse3'
    | 'SETSE4'  | 'setse4'
    | 'POLLINT' | 'pollint'
    | 'POLLCT1' | 'pollct1'
    | 'POLLCT2' | 'pollct2'
    | 'POLLCT3' | 'pollct3'
    | 'POLLSE1' | 'pollse1'
    | 'POLLSE2' | 'pollse2'
    | 'POLLSE3' | 'pollse3'
    | 'POLLSE4' | 'pollse4'
    | 'POLLPAT' | 'pollpat'
    | 'POLLFBW' | 'pollfbw'
    | 'POLLXMT' | 'pollxmt'
    | 'POLLXFI' | 'pollxfi'
    | 'POLLXRO' | 'pollxro'
    | 'POLLXRL' | 'pollxrl'
    | 'POLLATN' | 'pollatn'
    | 'POLLQMT' | 'pollqmt'
    | 'WAITINT' | 'waitint'
    | 'WAITCT1' | 'waitct1'
    | 'WAITCT2' | 'waitct2'
    | 'WAITCT3' | 'waitct3'
    | 'WAITSE1' | 'waitse1'
    | 'WAITSE2' | 'waitse2'
    | 'WAITSE3' | 'waitse3'
    | 'WAITSE4' | 'waitse4'
    | 'WAITPAT' | 'waitpat'
    | 'WAITFBW' | 'waitfbw'
    | 'WAITXMT' | 'waitxmt'
    | 'WAITXFI' | 'waitxfi'
    | 'WAITXRO' | 'waitxro'
    | 'WAITXRL' | 'waitxrl'
    | 'WAITATN' | 'waitatn'
    | 'ALLOWI'  | 'allowi'
    | 'STALLI'  | 'stalli'
    | 'TRGINT1' | 'trgint1'
    | 'TRGINT2' | 'trgint2'
    | 'TRGINT3' | 'trgint3'
    | 'NIXINT1' | 'nixint1'
    | 'NIXINT2' | 'nixint2'
    | 'NIXINT3' | 'nixint3'
    | 'SETINT1' | 'setint1'
    | 'SETINT2' | 'setint2'
    | 'SETINT3' | 'setint3'
    | 'SETQ'    | 'setq'
    | 'SETQ2'   | 'setq2'
    | 'PUSH'    | 'push'
    | 'POP'     | 'pop'
    | 'JMP'     | 'jmp'
    | 'CALL'    | 'call'
    | 'RET'     | 'ret'
    | 'CALLA'   | 'calla'
    | 'RETA'    | 'reta'
    | 'CALLB'   | 'callb'
    | 'RETB'    | 'retb'
    | 'JMPREL'  | 'jmprel'
    | 'SKIP'    | 'skip'
    | 'SKIPF'   | 'skipf'
    | 'EXECF'   | 'execf'
    | 'GETPTR'  | 'getptr'
    | 'GETBRK'  | 'getbrk'
    | 'COGBRK'  | 'cogbrk'
    | 'BRK'     | 'brk'
    | 'SETLUTS' | 'setluts'
    | 'SETCY'   | 'setcy'
    | 'SETCI'   | 'setci'
    | 'SETCQ'   | 'setcq'
    | 'SETCFRQ' | 'setcfrq'
    | 'SETCMOD' | 'setcmod'
    | 'SETPIV'  | 'setpiv'
    | 'SETPIX'  | 'setpix'
    | 'COGATN'  | 'cogatn'
    | 'TESTP'   | 'testp'
    | 'TESTPN'  | 'testpn'
    | 'DIRL'    | 'dirl'
    | 'DIRH'    | 'dirh'
    | 'DIRC'    | 'dirc'
    | 'DIRNC'   | 'dirnc'
    | 'DIRZ'    | 'dirz'
    | 'DIRNZ'   | 'dirnz'
    | 'DIRRND'  | 'dirrnd'
    | 'DIRNOT'  | 'dirnot'
    | 'OUTL'    | 'outl'
    | 'OUTH'    | 'outh'
    | 'OUTC'    | 'outc'
    | 'OUTNC'   | 'outnc'
    | 'OUTZ'    | 'outz'
    | 'OUTNZ'   | 'outnz'
    | 'OUTRND'  | 'outrnd'
    | 'OUTNOT'  | 'outnot'
    | 'FLTL'    | 'fltl'
    | 'FLTH'    | 'flth'
    | 'FLTC'    | 'fltc'
    | 'FLTNC'   | 'fltnc'
    | 'FLTZ'    | 'fltz'
    | 'FLTNZ'   | 'fltnz'
    | 'FLTRND'  | 'fltrnd'
    | 'FLTNOT'  | 'fltnot'
    | 'DRVL'    | 'drvl'
    | 'DRVH'    | 'drvh'
    | 'DRVC'    | 'drvc'
    | 'DRVNC'   | 'drvnc'
    | 'DRVZ'    | 'drvz'
    | 'DRVNZ'   | 'drvnz'
    | 'DRVRND'  | 'drvrnd'
    | 'DRVNOT'  | 'drvnot'
    | 'SPLITB'  | 'splitb'
    | 'MERGEB'  | 'mergeb'
    | 'SPLITW'  | 'splitw'
    | 'MERGEW'  | 'mergew'
    | 'SEUSSF'  | 'seussf'
    | 'SEUSSR'  | 'seussr'
    | 'RGBSQZ'  | 'rgbsqz'
    | 'RGBEXP'  | 'rgbexp'
    | 'XORO32'  | 'xoro32'
    | 'REV'     | 'rev'
    | 'RCZR'    | 'rczr'
    | 'RCZL'    | 'rczl'
    | 'WRC'     | 'wrc'
    | 'WRNC'    | 'wrnc'
    | 'WRZ'     | 'wrz'
    | 'WRNZ'    | 'wrnz'
    | 'MODCZ'   | 'modcz'
    | 'MODC'    | 'modc'
    | 'MODZ'    | 'modz'
    | 'SETSCP'  | 'setscp'
    | 'GETSCP'  | 'getscp'
    | 'LOC'     | 'loc'
    | 'AUGS'    | 'augs'
    | 'AUGD'    | 'augd'
    | 'ASMCLK'  | 'asmclk'
    ;

condition
    : {_input.LT(1).getCharPositionInLine() != 0}?
    ( '_RET_'        | '_ret_'
    | 'IF_NC_AND_NZ' | 'if_nc_and_nz'
    | 'IF_NZ_AND_NC' | 'if_nz_and_nc'
    | 'IF_GT'        | 'if_gt'
    | 'IF_A'         | 'if_a'
    | 'IF_00'        | 'if_00'
    | 'IF_NC_AND_Z'  | 'if_nc_and_z'
    | 'IF_Z_AND_NC'  | 'if_z_and_nc'
    | 'IF_01'        | 'if_01'
    | 'IF_NC'        | 'if_nc'
    | 'IF_GE'        | 'if_ge'
    | 'IF_AE'        | 'if_ae'
    | 'IF_0X'        | 'if_0x'
    | 'IF_C_AND_NZ'  | 'if_c_and_nz'
    | 'IF_NZ_AND_C'  | 'if_nz_and_c'
    | 'IF_10'        | 'if_10'
    | 'IF_NZ'        | 'if_nz'
    | 'IF_NE'        | 'if_ne'
    | 'IF_X0'        | 'if_x0'
    | 'IF_C_NE_Z'    | 'if_c_ne_z'
    | 'IF_Z_NE_C'    | 'if_z_ne_c'
    | 'IF_DIFF'      | 'if_diff'
    | 'IF_NC_OR_NZ'  | 'if_nc_or_nz'
    | 'IF_NZ_OR_NC'  | 'if_nz_or_nc'
    | 'IF_NOT_11'    | 'if_not_11'
    | 'IF_C_AND_Z'   | 'if_c_and_z'
    | 'IF_Z_AND_C'   | 'if_z_and_c'
    | 'IF_11'        | 'if_11'
    | 'IF_C_EQ_Z'    | 'if_c_eq_z'
    | 'IF_Z_EQ_C'    | 'if_z_eq_c'
    | 'IF_SAME'      | 'if_same'
    | 'IF_Z'         | 'if_z'
    | 'IF_E'         | 'if_e'
    | 'IF_X1'        | 'if_x1'
    | 'IF_NC_OR_Z'   | 'if_nc_or_z'
    | 'IF_Z_OR_NC'   | 'if_z_or_nc'
    | 'IF_NOT_10'    | 'if_not_10'
    | 'IF_C'         | 'if_c'
    | 'IF_LT'        | 'if_lt'
    | 'IF_B'         | 'if_b'
    | 'IF_1X'        | 'if_1x'
    | 'IF_C_OR_NZ'   | 'if_c_or_nz'
    | 'IF_NZ_OR_C'   | 'if_nz_or_c'
    | 'IF_NOT_01'    | 'if_not_01'
    | 'IF_C_OR_Z'    | 'if_c_or_z'
    | 'IF_Z_OR_C'    | 'if_z_or_c'
    | 'IF_LE'        | 'if_le'
    | 'IF_BE'        | 'if_be'
    | 'IF_NOT_00'    | 'if_not_00' )
    ;

effect
    : 'WC' (',' 'WZ')? | 'wc' (',' 'wz')? 
    | 'WZ' (',' 'WC')? | 'wz' (',' 'wc')? 
    | 'WCZ'            | 'wcz'
    | 'ANDC'           | 'andc' 
    | 'ANDZ'           | 'andz'
    | 'ORC'            | 'orc'
    | 'ORZ'            | 'orz'
    | 'XORC'           | 'xorc' 
    | 'XORZ'           | 'xorz'
    ;

argument: prefix? expression ;

prefix: ('##' | '#') '\\'? ;

type: ('LONG' | 'long' | 'WORD' | 'word' | 'BYTE' | 'byte') ; 

expression
    : operator=('+' | '-' | '!!' | '!' | '~' | 'ABS' | 'abs' | 'ENCOD' | 'encod' | 'DECOD' | 'decod' | 'RMASK' | 'rmask' | 'ONES' | 'ones' | 'SQRT' | 'sqrt' | 'QLOG' | 'qlog' | 'QEXP' | 'qexp') exp=expression
    | left=expression operator=('>>' | '<<' | 'sar' | 'ror' | 'rol' | 'rev' | 'zerox' | 'signx' ) right=expression
    | left=expression operator=('&' | '^' | '|') right=expression
    | left=expression operator=('*' | '/' | '+/' | '//' | '+//' | 'sca' | 'scas' | 'frac') right=expression
    | left=expression operator=('+' | '-' | '#>' | '<#') right=expression
    | left=expression operator=('ADDBITS' | 'addbits' | 'ADDPINS' | 'addpins') right=expression
    | left=expression operator=('<' | '+<' | '<=' | '+<=' | '==' | '<>' | '>=' | '+>=' | '>' | '+>' | '<=>') right=expression
    | left=expression operator=('&&'| 'and' | '^^' | 'xor' | '||' | 'or') right=expression
    | left=expression operator='?' middle=expression operator=':' right=expression
    | operator=('FLOAT' | 'float' | 'ROUND' | 'round' | 'TRUNC' | 'trunc') '(' exp=expression ')'
    | '(' exp=expression ')'
    | ('@')? atom
    ;

atom
    : NUMBER
    | HEX
    | BIN
    | QUAD
    | STRING
    | '$'
    | PTR
    | PTR ('[' expression ']')?
    | PTR ('++' | '--') ('[' expression ']')?
    | PTR ('[' expression ']')? ('++' | '--')
    | ('++' | '--') PTR ('[' expression ']')?
    | VARIABLE
    ;

BLOCK_COMMENT: '{' .*? '}' -> channel(HIDDEN) ;

COMMENT: '\'' ~ [\r\n]* -> channel(HIDDEN) ;

PTR: 'PTRA' | 'ptra' | 'PTRB' | 'ptrb' ;

VARIABLE: '.'? LETTER ( LETTER | DIGIT )* ;

STRING  : '"' .*? '"' ;
QUAD    : [%][%] [0123] [0123_]* ;
BIN     : [%] [01] [01_]* ;
HEX     : [$] [0-9a-fA-F] [0-9a-fA-F_]* ;
NUMBER  : [0-9.] [0-9._eE]* ;

NL      : [\r\n] ;
WS      : [ \t] -> channel(HIDDEN) ;

fragment LETTER: [a-zA-Z_] ;
fragment DIGIT : [0-9] ;
