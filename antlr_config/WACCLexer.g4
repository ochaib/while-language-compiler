lexer grammar WACCLexer;

channels { COMMENTS }

// ignore whitespace
IGNORE: [ \t\r\n]+ -> skip;

// PROGRAM/FUNC
BEGIN: 'begin';
END: 'end';
IS: 'is';

// STAT
SKIP_: 'skip';
READ: 'read';
FREE: 'free';
RETURN: 'return';
EXIT: 'exit';
PRINT: 'print';
PRINTLN: 'println';

// BRANCHES
IF: 'if';
THEN: 'then';
ELSE: 'else';
FI: 'fi';
WHILE: 'while';
DO: 'do';
DONE: 'done';
FOR: 'for';
CONTINUE: 'continue';
BREAK: 'break';

// ASSIGN RHS
NEWPAIR: 'newpair';
CALL: 'call';

// ast.PAIR ELEM
FST: 'fst';
SND: 'snd';

// BASE TYPES
INT: 'int';
BOOL: 'bool';
CHAR: 'char';
STRING: 'string';

// ast.PAIR TYPES
PAIR: 'pair';

// SYMBOLS
OPEN_PARENTHESES: '(';
CLOSE_PARENTHESES: ')';
COMMA: ',';
EQUALS: '=';
SEMICOLON: ';';
OPEN_BRACKET: '[';
CLOSE_BRACKET: ']';

// UNARY OPERATORS
NOT: '!';
LEN: 'len';
ORD: 'ord';
CHR: 'chr';

// BINARY OPERATORS
MULTIPLY: '*';
DIVIDE: '/';
MODULO: '%';
PLUS: '+';
MINUS: '-';

// COMPARATORS
// shortening for convenience
GT: '>';
GTE: '>=';
LT: '<';
LTE: '<=';
EE: '==';
NE: '!=';
AND: '&&';
OR: '||';

// SIDE-EFFECT EXPRESSIONS
INC: '++';
DEC: '--';
ADDEQ: '+=';
SUBEQ: '-=';
DIVEQ: '/=';
MULTEQ: '*=';
MODEQ: '%=';

// IDENT
fragment UNDERSCORE: '_';
fragment DIGIT: '0'..'9';
fragment LOWERCASE: 'a'..'z';
fragment UPPERCASE: 'A'..'Z';

// LITERALS
INT_LIT: DIGIT+;

PAIR_LIT: 'null';

fragment TRUE: 'true';
fragment FALSE: 'false';
BOOL_LIT: TRUE | FALSE;

fragment SINGLEQUOTE: '\'' ;
fragment DOUBLEQUOTE: '"' ;
fragment SLASH: '\\';
fragment ESCAPED_CHAR: (
    '0' |
    'b' |
    't' |
    'n' |
    'f' |
    'r' |
    DOUBLEQUOTE |
    SINGLEQUOTE |
    SLASH) ;
fragment CHARACTER: (
    ~('\'' | '"' | '\\') |
    SLASH ESCAPED_CHAR ) ;
CHAR_LIT: SINGLEQUOTE CHARACTER SINGLEQUOTE;
STR_LIT: DOUBLEQUOTE CHARACTER* DOUBLEQUOTE;

IDENT:
    (UNDERSCORE | LOWERCASE | UPPERCASE)
    (UNDERSCORE | LOWERCASE | UPPERCASE | DIGIT)*;



// Comments are given a skip rule as they don't need to be parsed
fragment EOL: [\r\n];
COMMENT: '#' ~([\r\n])* EOL -> channel(COMMENTS);
