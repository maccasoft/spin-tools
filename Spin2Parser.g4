parser grammar Spin2Parser;

options { tokenVocab=Spin2Lexer; }

prog
    : NL* (constantsSection | objectsSection | variablesSection | method | dataSection)* EOF
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
    : INDENT* name=IDENTIFIER EQUAL exp=constantExpression (NL | DEDENT)*
    ;

constantEnum
    : INDENT* LITERAL start=constantExpression (OPEN_BRACKET step=constantExpression CLOSE_BRACKET)? (NL | DEDENT)* (COMMA constantEnumName)*
    ;

constantEnumName: INDENT* name=IDENTIFIER (OPEN_BRACKET multiplier=constantExpression CLOSE_BRACKET)? (NL | DEDENT)*;

/*

OBJ  vga       : "VGA_Driver"     'instantiate "VGA_Driver.spin2" as "vga"

     mouse     : "USB_Mouse"      'instantiate "USB_Mouse.spin2" as "mouse"

     v[16]     : "VocalSynth"     'instantiate an array of 16 objects
                                  '..v[0] through v[15]

 */

objectsSection: OBJ_START+ NL* object* ;

object: INDENT* name=IDENTIFIER (OPEN_BRACKET count=constantExpression CLOSE_BRACKET)* COLON filename=STRING (NL | DEDENT)+ ;

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
    : INDENT* type=TYPE? name=IDENTIFIER (OPEN_BRACKET size=constantExpression CLOSE_BRACKET)? (NL | DEDENT)+ 
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

localvar: align=ALIGN? vartype=TYPE? name=IDENTIFIER (OPEN_BRACKET count=constantExpression CLOSE_BRACKET)? ; 

statement
    : spinExpression NL+
    | repeatLoop
    | conditional
    | caseConditional
    ;

function
    : BACKSLASH? name=IDENTIFIER OPEN_PAREN functionArgument? CLOSE_PAREN
    | BACKSLASH? obj=IDENTIFIER DOT name=IDENTIFIER OPEN_PAREN functionArgument? CLOSE_PAREN
    | BACKSLASH? obj=IDENTIFIER OPEN_BRACKET index=spinExpression CLOSE_BRACKET DOT name=IDENTIFIER OPEN_PAREN functionArgument? CLOSE_PAREN
    ;

functionArgument
    : spinExpression (COMMA spinExpression )*
    ;

identifier
    : AT name=IDENTIFIER
    | name=IDENTIFIER DOT IDENTIFIER OPEN_BRACKET spinExpression CLOSE_BRACKET
    | name=IDENTIFIER DOT OPEN_BRACKET spinExpression CLOSE_BRACKET
    | name=IDENTIFIER DOT IDENTIFIER
    | name=IDENTIFIER OPEN_BRACKET spinExpression CLOSE_BRACKET
    | name=IDENTIFIER
    ;

repeatLoop
    : REPEAT spinExpression? NL+ (INDENT statement* DEDENT? )?
    | REPEAT NL+ INDENT statement* DEDENT WHILE spinExpression NL+
    | REPEAT NL+ INDENT statement* DEDENT UNTIL spinExpression NL+
    | REPEAT identifier FROM spinExpression (TO spinExpression (STEP spinExpression)? )? NL+ (INDENT statement* DEDENT? )?
    | REPEAT WHILE spinExpression NL+ (INDENT statement* DEDENT? )?
    | REPEAT UNTIL spinExpression NL+ (INDENT statement* DEDENT? )?
    ;

conditional
    : IF NOT spinExpression NL+ INDENT statement* DEDENT elseConditional*
    | IFNOT spinExpression NL+ INDENT statement* DEDENT elseConditional*
    | IF spinExpression NL+ INDENT statement* DEDENT elseConditional*
    ;

elseConditional
    : ELSE NL+ INDENT statement* DEDENT
    | ELSEIF NOT spinExpression NL+ INDENT statement* DEDENT
    | ELSEIF spinExpression NL+ INDENT statement* DEDENT
    | ELSEIFNOT spinExpression NL+ INDENT statement* DEDENT
    ;

caseConditional
    : CASE spinExpression NL+ INDENT (caseConditionalMatch | caseConditionalOther)* DEDENT
    ;

caseConditionalMatch
    : spinExpression (ELLIPSIS spinExpression)? COLON spinExpression? NL+ (INDENT ( spinExpression NL+ )* DEDENT)?
    ;

caseConditionalOther
    : OTHER COLON spinExpression NL+ (INDENT statement* DEDENT)?
    | OTHER COLON NL+ (INDENT statement* DEDENT)?
    ;

spinExpression
    : identifier (COMMA IDENTIFIER)* ASSIGN spinExpression
    | TYPE OPEN_BRACKET spinExpression CLOSE_BRACKET ASSIGN spinExpression
    | operator=(PLUS | MINUS | TILDE | ENCOD | DECOD) exp=spinExpression
    | left=spinExpression operator=(LEFT_SHIFT | RIGHT_SHIFT) right=spinExpression
    | left=spinExpression operator=BIN_AND right=spinExpression
    | left=spinExpression operator=BIN_XOR right=spinExpression
    | left=spinExpression operator=BIN_OR right=spinExpression
    | left=spinExpression operator=(STAR | DIV) right=spinExpression
    | left=spinExpression operator=(PLUS | MINUS) right=spinExpression
    | left=spinExpression operator=FRAC right=spinExpression
    | left=spinExpression operator=(ADDPINS | ADDBITS) right=spinExpression
    | left=spinExpression operator=(EQUALS | NOT_EQUALS) right=spinExpression
    | left=spinExpression operator=(AND | LOGICAL_AND) right=spinExpression
    | left=spinExpression operator=(XOR | LOGICAL_XOR) right=spinExpression
    | left=spinExpression operator=(OR | LOGICAL_OR) right=spinExpression
    | left=spinExpression operator=QUESTION middle=spinExpression operator=COLON right=spinExpression
    | operator=FUNCTIONS OPEN_PAREN exp=spinExpression CLOSE_PAREN
    | OPEN_PAREN exp=spinExpression CLOSE_PAREN
    | expressionAtom
    ;

expressionAtom
    : NUMBER
    | HEX
    | BIN
    | QUAD
    | STRING
    | identifier
    | identifier (PLUS_PLUS | MINUS_MINUS)
    | (PLUS_PLUS | MINUS_MINUS) identifier
    | function
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

dataSection: DAT_START+ NL* (INDENT* dataLine (NL | DEDENT)* )* ;

dataLine
    : label NL+
    | directive NL+
    | assembler NL+
    | data NL+
    ;

directive
    : {_input.LT(1).getCharPositionInLine() != 0}? name=(ORG | ORGH | ORGF | ALIGN) (constantExpression (COMMA constantExpression)? )?
    | label? name=FIT constantExpression?
    | label? name=RES constantExpression
    ;

assembler
    : label condition=CONDITION? instruction=(OR | AND | NOT | XOR | ENCOD | DECOD | IDENTIFIER) argument COMMA argument COMMA argument modifier=MODIFIER?
    | label condition=CONDITION? instruction=(OR | AND | NOT | XOR | ENCOD | DECOD | IDENTIFIER) argument COMMA argument modifier=MODIFIER?
    | label condition=CONDITION? instruction=(OR | AND | NOT | XOR | ENCOD | DECOD | IDENTIFIER) argument modifier=MODIFIER?
    | label condition=CONDITION? instruction=(OR | AND | NOT | XOR | ENCOD | DECOD | IDENTIFIER) modifier=MODIFIER?
    | condition=CONDITION? instruction=(OR | AND | NOT | XOR | ENCOD | DECOD | IDENTIFIER) argument COMMA argument COMMA argument modifier=MODIFIER?
    | condition=CONDITION? instruction=(OR | AND | NOT | XOR | ENCOD | DECOD | IDENTIFIER) argument COMMA argument modifier=MODIFIER?
    | condition=CONDITION? instruction=(OR | AND | NOT | XOR | ENCOD | DECOD | IDENTIFIER) argument modifier=MODIFIER?
    | condition=CONDITION? instruction=(OR | AND | NOT | XOR | ENCOD | DECOD | IDENTIFIER) modifier=MODIFIER?
    ;

data
    : label? type=TYPE dataValue (COMMA dataValue)*
    ;

label
    : {_input.LT(1).getCharPositionInLine() == 0}? (DOT? IDENTIFIER) 
    ;

argument
    : prefix=(LITERAL | LITERAL_ABS | LONG_LITERAL | LONG_LITERAL_ABS)? constantExpression
    ;

dataValue: constantExpression (OPEN_BRACKET count=constantExpression CLOSE_BRACKET)? ;

constantExpression
    : operator=(PLUS | MINUS | TILDE) exp=constantExpression
    | left=constantExpression operator=(LEFT_SHIFT | RIGHT_SHIFT) right=constantExpression
    | left=constantExpression operator=BIN_AND right=constantExpression
    | left=constantExpression operator=BIN_XOR right=constantExpression
    | left=constantExpression operator=BIN_OR right=constantExpression
    | left=constantExpression operator=(STAR | DIV) right=constantExpression
    | left=constantExpression operator=(PLUS | MINUS) right=constantExpression
    | left=constantExpression operator=(ADDPINS | ADDBITS) right=constantExpression
    | left=constantExpression operator=(EQUALS | NOT_EQUALS) right=constantExpression
    | left=constantExpression operator=(AND | LOGICAL_AND) right=constantExpression
    | left=constantExpression operator=(XOR | LOGICAL_XOR) right=constantExpression
    | left=constantExpression operator=(OR | LOGICAL_OR) right=constantExpression
    | left=constantExpression operator=QUESTION middle=constantExpression operator=COLON right=constantExpression
    | operator=FUNCTIONS OPEN_PAREN exp=constantExpression CLOSE_PAREN
    | OPEN_PAREN exp=constantExpression CLOSE_PAREN
    | AT? atom
    ;

atom
    : NUMBER
    | HEX
    | BIN
    | QUAD
    | STRING
    | DOLLAR
    | IDENTIFIER (OPEN_BRACKET constantExpression CLOSE_BRACKET)?
    | IDENTIFIER (PLUS_PLUS | MINUS_MINUS) (OPEN_BRACKET constantExpression CLOSE_BRACKET)?
    | IDENTIFIER (OPEN_BRACKET constantExpression CLOSE_BRACKET)? (PLUS_PLUS | MINUS_MINUS)
    | (PLUS_PLUS | MINUS_MINUS) IDENTIFIER (OPEN_BRACKET constantExpression CLOSE_BRACKET)?
    | DOT? IDENTIFIER
    ;
