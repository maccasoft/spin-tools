
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

LONG_LITERAL:     '##'   ;
LONG_LITERAL_ABS: '##\\' ;
LITERAL_ABS:      '#\\'  ;
LITERAL:          '#'    ;

LOGICAL_AND:  '&&' ;
LOGICAL_OR:   '||' ;
LOGICAL_XOR:  '^^' ;

LEFT_SHIFT:   '<<' ;
RIGHT_SHIFT:  '>>' ;
PLUS_PLUS:    '++' ;
MINUS_MINUS:  '--' ;

ASSIGN:       ':=' | '+=' | '&=' | '|=' | '^=' ;

ELLIPSIS:     '..' ;

EQUALS:       '==' ;
NOT_EQUALS:   '<>' ;

BIN_AND:      '&' ;
BIN_OR :      '|' ;
BIN_XOR:      '^' ;

AT:           '@'  ;
DOLLAR:       '$'  ;
PERCENT:      '%'  ;
EQUAL:        '='  ;
DOT:          '.'  ; 
COMMA:        ','  ;
BACKSLASH:    '\\' ;
PLUS :        '+'  ;
MINUS:        '-'  ;
STAR:         '*'  ;
DIV:          '/'  ;
QUESTION:     '?'  ;
COLON :       ':'  ;
TILDE:        '~'  ;
UNDERSCORE:   '_'  ;

OPEN_BRACKET:  '[' ;
CLOSE_BRACKET: ']' ;
OPEN_PAREN:    '(' ;
CLOSE_PAREN:   ')' ;

CON_START: 'CON' | 'con' ;
VAR_START: 'VAR' | 'var' ;
OBJ_START: 'OBJ' | 'obj' ;
PUB_START: 'PUB' | 'pub' ;
PRI_START: 'PRI' | 'pri' ;
DAT_START: 'DAT' | 'dat' ;

REPEAT:    'REPEAT'    | 'repeat' ;
FROM:      'FROM'      | 'from' ;
TO:        'TO'        | 'to' ;
STEP:      'STEP'      | 'step' ;
WHILE:     'WHILE'     | 'while' ;
UNTIL:     'UNTIL'     | 'until' ;

ELSEIFNOT: 'ELSEIFNOT' | 'elseifnot' ;
ELSEIF:    'ELSEIF'    | 'elseif' ;
ELSE:      'ELSE'      | 'else' ;
IFNOT:     'IFNOT'     | 'ifnot' ;
IF:        'IF'        | 'if' ;

CASE:      'CASE'      | 'case' ;
OTHER:     'OTHER'     | 'other' ;

ADDPINS: 'ADDPINS' | 'addpins' ;
ADDBITS: 'ADDBITS' | 'addbits' ;
FRAC:    'FRAC'    | 'frac' ;
ENCOD:   'ENCOD'   | 'encod' ;
DECOD:   'DECOD'   | 'decod' ;

FUNCTIONS: 'ROUND' | 'round' | 'FLOAT' | 'float' | 'TRUNC' | 'trunc' ;

AND: 'AND' | 'and' ;
NOT: 'NOT' | 'not' ;
XOR: 'XOR' | 'xor' ;
OR:  'OR'  | 'or'  ;

ORG:  'ORG'  | 'org'  ;
ORGH: 'ORGH' | 'orgh' ;
ORGF: 'ORGF' | 'orgf' ;
FIT:  'FIT'  | 'fit'  ;
RES:  'RES'  | 'res'  ;

ALIGN:   'ALIGNW' | 'alignw' | 'ALIGNL' | 'alignl' ;

TYPE:    'LONG' | 'long' | 'WORD' | 'word' | 'BYTE' | 'byte' ;

CONDITION
    : '_RET_'        | '_ret_'
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
    | 'IF_NOT_00'    | 'if_not_00'
    ;

MODIFIER
    : 'WC'   | 'wc'
    | 'WZ'   | 'wz' 
    | 'WCZ'  | 'wcz' 
    | 'ANDC' | 'andc' 
    | 'ORC'  | 'orc' 
    | 'XORC' | 'xorc' 
    | 'ANDZ' | 'andz' 
    | 'ORZ'  | 'orz' 
    | 'XORZ' | 'xorz' 
    ;

IDENTIFIER: LETTER ( LETTER | DIGIT )* ;
