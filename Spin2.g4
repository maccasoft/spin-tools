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
    : ('LONG' | 'long')
    | ('WORD' | 'word')
    | ('BYTE' | 'byte')
    | ('RES' | 'res')
    ;

label: {_input.LT(1).getCharPositionInLine() == 0}? '.'? VARIABLE ;

opcode
    : ('ORG' | 'org')
    | ('ORGH' | 'orgh')
    | ('ORGF' | 'orgf')
    | ('FIT' | 'fit')
    | ('nop')
    | ('ror')
    | ('rol')
    | ('shr')
    | ('shl')
    | ('rcr')
    | ('rcl')
    | ('sar')
    | ('sal')
    | ('ADD' | 'add')
    | ('ADDX' | 'addx')
    | ('ADDS' | 'adds')
    | ('ADDSX' | 'addsx')
    | ('sub')
    | ('subx')
    | ('subs')
    | ('subsx')
    | ('cmp')
    | ('cmpx')
    | ('cmps')
    | ('cmpsx')
    | ('cmpr')
    | ('cmpm')
    | ('subr')
    | ('cmpsub')
    | ('fge')
    | ('fle')
    | ('fges')
    | ('fles')
    | ('sumc')
    | ('sumnc')
    | ('sumz')
    | ('sumnz')
    | ('testb')
    | ('testbn')
    | ('bitl')
    | ('bith')
    | ('bitc')
    | ('bitnc')
    | ('bitz')
    | ('bitnz')
    | ('bitrnd')
    | ('bitnot')
    | ('and')
    | ('andn')
    | ('or')
    | ('xor')
    | ('muxc')
    | ('muxnc')
    | ('muxz')
    | ('muxnz')
    | ('MOV' | 'mov')
    | ('NOT' | 'not')
    | ('ABS' | 'abs')
    | ('neg')
    | ('negc')
    | ('negnc')
    | ('negz')
    | ('negnz')
    | ('incmod')
    | ('decmod')
    | ('zerox')
    | ('signx')
    | ('encod')
    | ('ones')
    | ('test')
    | ('testn')
    | ('setnib')
    | ('getnib')
    | ('rolnib')
    | ('setbyte')
    | ('getbyte')
    | ('rolbyte')
    | ('setword')
    | ('getword')
    | ('rolword')
    | ('altsn')
    | ('altgn')
    | ('altsb')
    | ('altgb')
    | ('altsw')
    | ('altgw')
    | ('altr')
    | ('altd')
    | ('alts')
    | ('altb')
    | ('alti')
    | ('setr')
    | ('setd')
    | ('sets')
    | ('decod')
    | ('bmask')
    | ('crcbit')
    | ('crcnib')
    | ('muxnits')
    | ('muxnibs')
    | ('muxq')
    | ('movbyts')
    | ('mul')
    | ('muls')
    | ('sca')
    | ('scas')
    | ('addpix')
    | ('mulpix')
    | ('blnpix')
    | ('mixpix')
    | ('addct1')
    | ('addct2')
    | ('addct3')
    | ('wmlong')
    | ('rqpin')
    | ('rdpin')
    | ('rdlut')
    | ('rdbyte')
    | ('rdword')
    | ('rdlong')
    | ('popa')
    | ('popb')
    | ('calld')
    | ('resi3')
    | ('resi2')
    | ('resi1')
    | ('resi0')
    | ('reti3')
    | ('reti2')
    | ('reti1')
    | ('reti0')
    | ('callpa')
    | ('callpb')
    | ('djz')
    | ('djnz')
    | ('djf')
    | ('djnf')
    | ('ijz')
    | ('ijnz')
    | ('tjz')
    | ('tjnz')
    | ('tjf')
    | ('tjnf')
    | ('tjs')
    | ('tjns')
    | ('tjv')
    | ('jint')
    | ('jct1')
    | ('jct2')
    | ('jct3')
    | ('jse1')
    | ('jse2')
    | ('jse3')
    | ('jse4')
    | ('jpat')
    | ('jfbw')
    | ('jxmt')
    | ('jxfi')
    | ('jxro')
    | ('jxrl')
    | ('jatn')
    | ('jqmt')
    | ('jnint')
    | ('jnct1')
    | ('jnct2')
    | ('jnct3')
    | ('jnse1')
    | ('jnse2')
    | ('jnse3')
    | ('jnse4')
    | ('jnpat')
    | ('jnfbw')
    | ('jnxmt')
    | ('jnxfi')
    | ('jnxro')
    | ('jnxrl')
    | ('jnatn')
    | ('jnqmt')
    | ('setpat')
    | ('akpin')
    | ('wrpin')
    | ('wxpin')
    | ('wypin')
    | ('wrlut')
    | ('wrbyte')
    | ('wrword')
    | ('wrlong')
    | ('pusha')
    | ('pushb')
    | ('rdfast')
    | ('wrfast')
    | ('fblock')
    | ('xinit')
    | ('xstop')
    | ('xzero')
    | ('xcont')
    | ('rep')
    | ('coginit')
    | ('qmul')
    | ('qdiv')
    | ('qfrac')
    | ('qsqrt')
    | ('qrotate')
    | ('qvector')
    | ('hubset')
    | ('cogid')
    | ('cogstop')
    | ('locknew')
    | ('lockret')
    | ('locktry')
    | ('lockrel')
    | ('qlog')
    | ('qexp')
    | ('rfbyte')
    | ('rfword')
    | ('rflong')
    | ('rfvar')
    | ('rfvars')
    | ('wfbyte')
    | ('wfword')
    | ('wflong')
    | ('getqx')
    | ('getqy')
    | ('getct')
    | ('getrnd')
    | ('setdacs')
    | ('setxfrq')
    | ('getxacc')
    | ('waitx')
    | ('setse1')
    | ('setse2')
    | ('setse3')
    | ('setse4')
    | ('pollint')
    | ('pollct1')
    | ('pollct2')
    | ('pollct3')
    | ('pollse1')
    | ('pollse2')
    | ('pollse3')
    | ('pollse4')
    | ('pollpat')
    | ('pollfbw')
    | ('pollxmt')
    | ('pollxfi')
    | ('pollxro')
    | ('pollxrl')
    | ('pollatn')
    | ('pollqmt')
    | ('waitint')
    | ('waitct1')
    | ('waitct2')
    | ('waitct3')
    | ('waitse1')
    | ('waitse2')
    | ('waitse3')
    | ('waitse4')
    | ('waitpat')
    | ('waitfbw')
    | ('waitxmt')
    | ('waitxfi')
    | ('waitxro')
    | ('waitxrl')
    | ('waitatn')
    | ('allowi')
    | ('stalli')
    | ('trgint1')
    | ('trgint2')
    | ('trgint3')
    | ('nixint1')
    | ('nixint2')
    | ('nixint3')
    | ('setint1')
    | ('setint2')
    | ('setint3')
    | ('setq')
    | ('setq2')
    | ('push')
    | ('pop')
    | ('JMP' | 'jmp')
    | ('call')
    | ('ret')
    | ('calla')
    | ('reta')
    | ('callb')
    | ('retb')
    | ('jmprel')
    | ('skip')
    | ('skipf')
    | ('execf')
    | ('getptr')
    | ('getbrk')
    | ('cogbrk')
    | ('brk')
    | ('setluts')
    | ('setcy')
    | ('setci')
    | ('setcq')
    | ('setcfrq')
    | ('setcmod')
    | ('setpiv')
    | ('setpix')
    | ('cogatn')
    | ('testp')
    | ('testpn')
    | ('dirl')
    | ('dirh')
    | ('dirc')
    | ('dirnc')
    | ('dirz')
    | ('dirnz')
    | ('dirrnd')
    | ('dirnot')
    | ('outl')
    | ('outh')
    | ('outc')
    | ('outnc')
    | ('outz')
    | ('outnz')
    | ('outrnd')
    | ('outnot')
    | ('fltl')
    | ('flth')
    | ('fltc')
    | ('fltnc')
    | ('fltz')
    | ('fltnz')
    | ('fltrnd')
    | ('fltnot')
    | ('drvl')
    | ('drvh')
    | ('drvc')
    | ('drvnc')
    | ('drvz')
    | ('drvnz')
    | ('drvrnd')
    | ('drvnot')
    | ('splitb')
    | ('mergeb')
    | ('splitw')
    | ('mergew')
    | ('seussf')
    | ('seussr')
    | ('rgbsqz')
    | ('rgbexp')
    | ('xoro32')
    | ('rev')
    | ('rczr')
    | ('rczl')
    | ('wrc')
    | ('wrnc')
    | ('wrz')
    | ('wrnz')
    | ('modcz')
    | ('modc')
    | ('modz')
    | ('setscp')
    | ('getscp')
    | ('calld')
    | ('loc')
    | ('augs')
    | ('augd')
    | ('asmclk')
    ;

condition
    : {_input.LT(1).getCharPositionInLine() != 0}? (('_ret_')
    | ('if_nc_and_nz')
    | ('if_nz_and_nc')
    | ('if_gt')
    | ('if_a')
    | ('if_00')
    | ('if_nc_and_z')
    | ('if_z_and_nc')
    | ('if_01')
    | ('if_nc')
    | ('if_ge')
    | ('if_ae')
    | ('if_0x')
    | ('if_c_and_nz')
    | ('if_nz_and_c')
    | ('if_10')
    | ('if_nz')
    | ('if_ne')
    | ('if_x0')
    | ('if_c_ne_z')
    | ('if_z_ne_c')
    | ('if_diff')
    | ('if_nc_or_nz')
    | ('if_nz_or_nc')
    | ('if_not_11')
    | ('if_c_and_z')
    | ('if_z_and_c')
    | ('if_11')
    | ('if_c_eq_z')
    | ('if_z_eq_c')
    | ('if_same')
    | ('if_z')
    | ('if_e')
    | ('if_x1')
    | ('if_nc_or_z')
    | ('if_z_or_nc')
    | ('if_not_10')
    | ('if_c')
    | ('if_lt')
    | ('if_b')
    | ('if_1x')
    | ('if_c_or_nz')
    | ('if_nz_or_c')
    | ('if_not_01')
    | ('if_c_or_z')
    | ('if_z_or_c')
    | ('if_le')
    | ('if_be')
    | ('if_not_00'))
    ;

effect
    : ('wc' (',' 'wz')? ) | ('wz' (',' 'wc')? ) | ('wcz')
    | ('andc') | ('andz')
    | ('orc') | ('orz')
    | ('xorc') | ('xorz')
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
    | left=expression operator='?' expression operator=':' right=expression
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
    | VARIABLE
    | PTR
    | PTR ('[' expression ']')?
    | PTR ('++' | '--') ('[' expression ']')?
    | PTR ('[' expression ']')? ('++' | '--')
    | ('++' | '--') PTR ('[' expression ']')?
    ;

BLOCK_COMMENT: '{' .*? '}' -> channel(HIDDEN) ;

COMMENT: '\'' ~ [\r\n]* -> channel(HIDDEN) ;

PTR: 'ptra' | 'PTRA' | 'ptrb' | 'PTRB' ;

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
