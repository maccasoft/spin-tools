
lexer grammar Spin2Lexer;

options { superClass=Spin2LexerBase; }

tokens { INDENT, DEDENT }

fragment LETTER: [a-zA-Z_] ;
fragment DIGIT : [0-9] ;

BLOCK_COMMENT: '{' .*? '}' -> channel(HIDDEN) ;

COMMENT: '\'' ~ [\r\n]* -> channel(HIDDEN) ;

NL      : [\r\n]+ { HandleNewLine(); } ;
WS      : [ \t]+ { HandleSpaces(); } -> channel(HIDDEN) ;

STRING  : '"' .*? '"' ;
QUAD    : '%%' DIGIT (DIGIT | UNDERSCORE)* ;
BIN     : '%' DIGIT (DIGIT | UNDERSCORE)* ;
HEX     : '$' (DIGIT | LETTER) (DIGIT | LETTER | UNDERSCORE)* ;
NUMBER  : (DIGIT) (DIGIT | DOT | LETTER | UNDERSCORE)* ;

LOGICAL_AND: '&&' ;
LOGICAL_OR : '||' ;
LOGICAL_XOR: '^^' ;

POUND_POUND: '##' ;
LEFT_SHIFT:  '<<' ;
RIGHT_SHIFT: '>>' ;
PLUS_PLUS:   '++' ;
MINUS_MINUS: '--' ;

ASSIGN:      ':=' ;
ADD_ASSIGN:  '+=' ;

BIN_AND:     '&' ;
BIN_OR :     '|' ;
BIN_XOR:     '^' ;

AT:          '@'  ;
POUND:       '#'  ;
DOLLAR:      '$'  ;
PERCENT:     '%'  ;
EQUAL:       '='  ;
DOT:         '.'  ; 
COMMA:       ','  ;
BACKSLASH:   '\\' ;
PLUS :       '+'  ;
MINUS:       '-'  ;
STAR:        '*'  ;
DIV:         '/'  ;
QUESTION:    '?'  ;
COLON :      ':'  ;
TILDE:       '~'  ;
UNDERSCORE:  '_'  ;

OPEN_BRACKET : '[' ;
CLOSE_BRACKET: ']' ;
OPEN_PAREN :   '(' ;
CLOSE_PAREN:   ')' ;

CON_START: 'CON' | 'con' ;
VAR_START: 'VAR' | 'var' ;
OBJ_START: 'OBJ' | 'obj' ;
PUB_START: 'PUB' | 'pub' ;
PRI_START: 'PRI' | 'pri' ;
DAT_START: 'DAT' | 'dat' ;

REPEAT: 'REPEAT' | 'repeat' ;

ADDPINS: 'ADDPINS' | 'addpins' ;
ADDBITS: 'ADDBITS' | 'addbits' ;

ORG: 'ORG' | 'org' ;
ORGH: 'ORGH' | 'orgh' ;
RES: 'RES' | 'res' ;

ALIGN:   'ALIGNW' | 'alignw' | 'ALIGNL' | 'alignl' ;

TYPE:    'LONG' | 'long' | 'WORD' | 'word' | 'BYTE' | 'byte' ;

CONDITION
    : ('IF_' | 'if_') ( LETTER | DIGIT )*
    | '_RET_' | '_ret_' 
    ;

IDENTIFIER: LETTER ( LETTER | DIGIT )* ;
