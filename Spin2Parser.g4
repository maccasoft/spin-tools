parser grammar Spin2Parser;

options { tokenVocab=Spin2Lexer; }

prog
    : NL* (constantsSection | objectsSection | variablesSection | method | data)* EOF
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

constantsSection: CON_START+ NL* ( (constantAssign (COMMA constantAssign)*) | constantEnum | (constantEnumName (COMMA constantEnumName)*) )* ;

constantAssign
    : INDENT* name=IDENTIFIER EQUAL exp=expression (NL | DEDENT)*
    ;

constantEnum
    : INDENT* POUND start=expression (OPEN_BRACKET step=expression CLOSE_BRACKET)? (NL | DEDENT)* (COMMA constantEnumName)*
    ;

constantEnumName: INDENT* name=IDENTIFIER (OPEN_BRACKET multiplier=expression CLOSE_BRACKET)? (NL | DEDENT)*;

/*

OBJ  vga       : "VGA_Driver"     'instantiate "VGA_Driver.spin2" as "vga"

     mouse     : "USB_Mouse"      'instantiate "USB_Mouse.spin2" as "mouse"

     v[16]     : "VocalSynth"     'instantiate an array of 16 objects
                                  '..v[0] through v[15]

 */

objectsSection: OBJ_START+ NL* object* ;

object: INDENT* name=IDENTIFIER (OPEN_BRACKET count=expression CLOSE_BRACKET)* COLON filename=STRING (NL | DEDENT)+ ;

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

variablesSection: VAR_START+ NL* (variable (COMMA variable)* )* ;

variable
    : INDENT* type=TYPE? name=IDENTIFIER (OPEN_BRACKET size=expression CLOSE_BRACKET)? (NL | DEDENT)+ 
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

method: (PUB_START | PRI_START) name=IDENTIFIER OPEN_PAREN (parameters)? CLOSE_PAREN (':' result)? ('|' localvars)? NL+ INDENT statement* DEDENT ;

parameters: IDENTIFIER (COMMA IDENTIFIER)* ;

result: IDENTIFIER (COMMA IDENTIFIER)* ;

localvars: localvar (COMMA localvar)* ;

localvar: align=ALIGN? vartype=TYPE? name=IDENTIFIER (OPEN_BRACKET count=expression CLOSE_BRACKET)? ; 

statement
    : assignment NL+
    | function NL+
    | repeatLoop
    | conditional
    | caseConditional
    ;

assignment
    : identifier (ASSIGN | ADD_ASSIGN) spinExpression ( (ASSIGN | ADD_ASSIGN) spinExpression )*
    | identifier (COMMA IDENTIFIER)+ (ASSIGN | ADD_ASSIGN) spinExpression
    ;

function
    : BACKSLASH? name=IDENTIFIER OPEN_PAREN functionArgument? CLOSE_PAREN
    | BACKSLASH? obj=IDENTIFIER DOT name=IDENTIFIER OPEN_PAREN functionArgument? CLOSE_PAREN
    | BACKSLASH? obj=IDENTIFIER OPEN_BRACKET index=spinExpression CLOSE_BRACKET DOT name=IDENTIFIER OPEN_PAREN functionArgument? CLOSE_PAREN
    ;

functionArgument
    : (assignment | spinExpression) (COMMA (assignment | spinExpression) )*
    ;

identifier
    : IDENTIFIER (OPEN_BRACKET spinExpression CLOSE_BRACKET)?
    | IDENTIFIER DOT IDENTIFIER
    | IDENTIFIER DOT OPEN_BRACKET spinExpression CLOSE_BRACKET
    | IDENTIFIER DOT IDENTIFIER OPEN_BRACKET spinExpression CLOSE_BRACKET
    ;

repeatLoop
    : REPEAT spinExpression? NL+ INDENT statement* DEDENT
    | REPEAT NL+ INDENT statement* DEDENT WHILE spinExpression NL+
    | REPEAT NL+ INDENT statement* DEDENT UNTIL spinExpression NL+
    | REPEAT FROM spinExpression (TO spinExpression (STEP spinExpression)? )? NL+ INDENT statement* DEDENT
    | REPEAT WHILE spinExpression NL+ (INDENT statement* DEDENT)?
    | REPEAT UNTIL spinExpression NL+ (INDENT statement* DEDENT)?
    ;

conditional
    : IF spinExpression NL+ INDENT statement* DEDENT elseConditional*
    | IFNOT spinExpression NL+ INDENT statement* DEDENT elseConditional*
    ;

elseConditional
    : ELSE NL+ INDENT statement* DEDENT
    | ELSEIF spinExpression NL+ INDENT statement* DEDENT
    | ELSEIFNOT spinExpression NL+ INDENT statement* DEDENT
    ;

caseConditional
    : CASE spinExpression NL+ INDENT (caseConditionalMatch | caseConditionalOther)* DEDENT
    ;

caseConditionalMatch
    : spinExpression (ELLIPSIS expression)? COLON (assignment | function)? NL+ (INDENT ( (assignment | function) NL+ )* DEDENT)?
    ;

caseConditionalOther
    : OTHER COLON (assignment | function) NL+ (INDENT statement* DEDENT)?
    | OTHER COLON NL+ (INDENT statement* DEDENT)?
    ;

spinExpression
    : operator=(PLUS | MINUS | TILDE) exp=spinExpression
    | left=spinExpression operator=(LEFT_SHIFT | RIGHT_SHIFT) right=spinExpression
    | left=spinExpression operator=BIN_AND right=spinExpression
    | left=spinExpression operator=BIN_XOR right=spinExpression
    | left=spinExpression operator=BIN_OR right=spinExpression
    | left=spinExpression operator=(STAR | DIV) right=spinExpression
    | left=spinExpression operator=(PLUS | MINUS) right=spinExpression
    | left=spinExpression operator=(ADDPINS | ADDBITS) right=spinExpression
    | left=spinExpression operator=(EQUALS | NOT_EQUALS) right=spinExpression
    | left=spinExpression operator=(AND | LOGICAL_AND) right=spinExpression
    | left=spinExpression operator=(XOR | LOGICAL_XOR) right=spinExpression
    | left=spinExpression operator=(OR | LOGICAL_OR) right=spinExpression
    | left=spinExpression operator=QUESTION middle=spinExpression operator=COLON right=spinExpression
    | operator=FUNCTIONS OPEN_PAREN exp=spinExpression CLOSE_PAREN
    | OPEN_PAREN exp=spinExpression CLOSE_PAREN
    | atom
    | identifier
    | function
    ;

experssionAtom
    : NUMBER
    | HEX
    | BIN
    | QUAD
    | STRING
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

data: DAT_START+ NL* dataLine* ;

dataLine
    : label NL+ (NL | DEDENT)*
    | INDENT* directive=(ORG | ORGH) (expression (COMMA expression)? )? NL+ (NL | DEDENT)*
    | INDENT* label? directive=TYPE dataValue (COMMA dataValue)* NL+ (NL | DEDENT)*
    | INDENT* label? directive=RES dataValue NL+ (NL | DEDENT)*
    | INDENT* label? condition? opcode argument COMMA argument COMMA argument effect? NL+ (NL | DEDENT)*
    | INDENT* label? condition? opcode argument COMMA argument effect? NL+ (NL | DEDENT)*
    | INDENT* label? condition? opcode argument effect? NL+ (NL | DEDENT)*
    | INDENT* label? condition? opcode effect? NL+ (NL | DEDENT)*
    ;

label
    : {_input.LT(1).getCharPositionInLine() == 0}? DOT? IDENTIFIER 
    ;

condition
    : {_input.LT(1).getCharPositionInLine() != 0}? CONDITION 
    ;

opcode
    : {_input.LT(1).getCharPositionInLine() != 0}? (OR | AND | XOR | IDENTIFIER) 
    ;

argument: prefix? expression ;

prefix: (POUND_POUND | POUND) BACKSLASH? ;

effect
    : {_input.LT(1).getCharPositionInLine() != 0}? IDENTIFIER 
    ;

dataValue: expression (OPEN_BRACKET count=expression CLOSE_BRACKET)? ;

expression
    : operator=(PLUS | MINUS | TILDE) exp=expression
    | left=expression operator=(LEFT_SHIFT | RIGHT_SHIFT) right=expression
    | left=expression operator=BIN_AND right=expression
    | left=expression operator=BIN_XOR right=expression
    | left=expression operator=BIN_OR right=expression
    | left=expression operator=(STAR | DIV) right=expression
    | left=expression operator=(PLUS | MINUS) right=expression
    | left=expression operator=(ADDPINS | ADDBITS) right=expression
    | left=expression operator=(EQUALS | NOT_EQUALS) right=expression
    | left=expression operator=(AND | LOGICAL_AND) right=expression
    | left=expression operator=(XOR | LOGICAL_XOR) right=expression
    | left=expression operator=(OR | LOGICAL_OR) right=expression
    | left=expression operator=QUESTION middle=expression operator=COLON right=expression
    | operator=FUNCTIONS OPEN_PAREN exp=expression CLOSE_PAREN
    | OPEN_PAREN exp=expression CLOSE_PAREN
    | AT? atom
    ;

atom
    : NUMBER
    | HEX
    | BIN
    | QUAD
    | STRING
    | DOLLAR
    | IDENTIFIER (OPEN_BRACKET expression CLOSE_BRACKET)?
    | IDENTIFIER (PLUS_PLUS | MINUS_MINUS) (OPEN_BRACKET expression CLOSE_BRACKET)?
    | IDENTIFIER (OPEN_BRACKET expression CLOSE_BRACKET)? (PLUS_PLUS | MINUS_MINUS)
    | (PLUS_PLUS | MINUS_MINUS) IDENTIFIER (OPEN_BRACKET expression CLOSE_BRACKET)?
    | DOT? IDENTIFIER
    ;
