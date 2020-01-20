lexer grammar WACCLexer;

// ignore whitespace
IGNORE: [ \t\r\n]+ -> skip;

// PROGRAM/FUNC
BEGIN: 'begin';
END: 'end';
IS: 'is';

// STAT
SKIP: 'skip';
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

// ASSIGN RHS
NEWPAIR: 'newpair';
CALL: 'call';

// PAIR ELEM
FST: 'fst';
SND: 'snd';

// BASE TYPES
INT: 'int';
BOOL: 'bool';
CHAR: 'char';
STRING: 'string';

// PAIR TYPES
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

// IDENT
fragment UNDERSCORE: '_';
fragment DIGIT: '0'..'9';
fragment LOWERCASE: 'a'..'z';
fragment UPPERCASE: 'A'..'Z';

IDENT:
    (UNDERSCORE | LOWERCASE | UPPERCASE)
    (UNDERSCORE | LOWERCASE | UPPERCASE | DIGIT)*;

// LITERALS
SIGN: PLUS | MINUS;
INT_LIT: SIGN? DIGIT+;

TRUE: 'true';
FALSE: 'false';
BOOL_LIT: TRUE | FALSE;