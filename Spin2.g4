grammar Spin2;

prog
    : NL* (constants | objects | variables | method | data)* EOF
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
    : (',')? name=IDENTIFIER '=' exp=expression NL*
    | (',')? name=IDENTIFIER ('[' multiplier=expression ']')? NL*
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

reference: IDENTIFIER ;

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
    : type=TYPE IDENTIFIER ('[' expression ']')? (',' IDENTIFIER ('[' expression ']')? )* NL+ 
    ;

/* 

PUB go()
PUB SetupADC(pins)
PUB StartTx(pin, baud) : Okay
PRI RotateXY(X, Y, Angle) : NewX, NewY | p,q,r
PRI Shuffle() | i, j
PRI FFT1024(DataPtr) | a, b, x[1024], y[1024]
PRI ReMix() : Length, SampleRate | WORD Buff[20000], k
PRI StrCheck(StrPtrA, StrPtrB) : Pass | i, BYTE Str[64]

*/

method: ('PUB' | 'pub' | 'PRI' | 'pri') name=IDENTIFIER '(' (parameters)? ')' (':' result)? ('|' localvars)? NL+ (statement)* ;

parameters: IDENTIFIER (',' IDENTIFIER)* ;

result: IDENTIFIER (',' IDENTIFIER)* ;

localvars: localvar (',' localvar)* ;

localvar: align=('ALIGNW' | 'ALIGNL')? vartype=TYPE? name=IDENTIFIER ('[' count=expression ']')? ; 

statement
    : 'repeat' NL+
    | assignment
    | function
    ;

assignment
    : IDENTIFIER (':=' | '+=') (function | IDENTIFIER | expression) NL+
    ;

function
    : IDENTIFIER '(' (IDENTIFIER (',' IDENTIFIER)* )? ')' NL+
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

data: ('DAT' | 'dat')+ NL* dataLine* ;

dataLine
    : label NL+
    | label? directive=('ORG' | 'org' | 'ORGH' | 'orgh') (expression (',' expression)? )? NL+
    | label? directive=TYPE dataValue (',' dataValue)* NL+
    | label? directive=('RES' | 'res') dataValue NL+
    | label? condition? opcode argument ',' argument ',' argument effect? NL+
    | label? condition? opcode argument ',' argument effect? NL+
    | label? condition? opcode argument effect? NL+
    | label? condition? opcode effect? NL+
    ;

label
    : {_input.LT(1).getCharPositionInLine() == 0}? '.'? IDENTIFIER 
    ;

condition
    : {_input.LT(1).getCharPositionInLine() != 0}? CONDITION 
    ;

opcode
    : {_input.LT(1).getCharPositionInLine() != 0}? IDENTIFIER 
    ;

argument: prefix? expression ;

prefix: ('##' | '#') '\\'? ;

effect
    : {_input.LT(1).getCharPositionInLine() != 0}? '.'? IDENTIFIER 
    ;

dataValue: expression ('[' count=expression ']')? ;

expression
    : operator=('+' | '-' | '!!' | '!' | '~') exp=expression
    | left=expression operator=('>>' | '<<') right=expression
    | left=expression operator=('&' | '^' | '|') right=expression
    | left=expression operator=('*' | '/' | '+/' | '//' | '+//') right=expression
    | left=expression operator=('+=' | '+' | '-' | '#>' | '<#') right=expression
    | left=expression operator=('ADDBITS' | 'addbits' | 'ADDPINS' | 'addpins') right=expression
    | left=expression operator=('<' | '+<' | '<=' | '+<=' | '==' | '<>' | '>=' | '+>=' | '>' | '+>' | '<=>') right=expression
    | left=expression operator=('&&'|'^^'| '||') right=expression
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
    | '.'? IDENTIFIER
    ;

/* Lexer */

fragment LETTER: [a-zA-Z_] ;
fragment DIGIT : [0-9] ;

BLOCK_COMMENT: '{' .*? '}' -> channel(HIDDEN) ;

COMMENT: '\'' ~ [\r\n]* -> channel(HIDDEN) ;

TYPE: 'LONG' | 'long' | 'WORD' | 'word' | 'BYTE' | 'byte' ;

PTR: 'PTRA' | 'ptra' | 'PTRB' | 'ptrb' ;

CONDITION
    : ('IF_' | 'if_') ( LETTER | DIGIT )*
    | '_RET_' | '_ret_' 
    ;

IDENTIFIER: LETTER ( LETTER | DIGIT )* ;

STRING  : '"' .*? '"' ;
QUAD    : [%][%] [0123] [0123_]* ;
BIN     : [%] [01] [01_]* ;
HEX     : [$] [0-9a-fA-F] [0-9a-fA-F_]* ;
NUMBER  : [0-9.] [0-9._eE]* ;

NL      : [\r\n] ;
WS      : [ \t] -> channel(HIDDEN) ;
