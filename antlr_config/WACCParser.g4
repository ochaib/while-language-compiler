parser grammar BasicParser;

options {
  tokenVocab=BasicLexer;
}

// EOF indicates that the program must consume to the end of the input.
program: BEGIN FUNC* stat END EOF;

func: type IDENT OPEN_PARENTHESES param_list? CLOSE_PARENTHESES IS stat END ;

param_list: param (COMMA param)* ;

param: type IDENT ;

stat: SKIP
      | type IDENT EQUALS assign_rhs
      | assign_lhs EQUALS assign_rhs
      | READ assign_lhs
      | FREE expr
      | RETURN expr
      | EXIT expr
      | PRINT expr
      | PRINTLN expr
      | IF expr THEN stat ELSE stat FI
      | WHILE expr DO stat DONE
      | BEGIN stat END
      | stat SEMI_COLON stat ;

assign_lhs: IDENT | array_elem | pair_elem ;

assign_rhs: expr 
      | array_liter 
      | NEWPAIR OPEN_PARENTHESES expr COMMA expr CLOSE_PARENTHESES
      | pair_elem
      | CALL IDENT OPEN_PARENTHESES arg-list? CLOSE_PARENTHESES ;

arg_list: expr (COMMA expr)* ;

pair_elem: FST expr
      | SND expr ;

type: base_type
      | array_type
      | pair_type ;

base_type: INT
      | BOOL
      | CHAR
      | STRING ;

array_type: type OPEN_BRACKET CLOSE_BRACKET ;

pair_type: PAIR OPEN_PARENTHASES pair_elem_type COMMA pair_elem_type CLOSE_PARENTHASES ;

pair_elem_type: base_type
      | array_type
      | PAIR ;

expr: INT_LIT
      | BOOL_LIT
      | CHAR_LIT
      | STR_LIT
      | PAIR_LIT
      | IDENT
      | array_elem
      | unary_oper expr
      | expr binary_oper expr
      | OPEN_PARENTHESES expr CLOSE_PARENTHESES ;

unary_oper: NOT | MINUS | LEN | ORD | CHR;

binary_oper: MULTIPLY | DIVIDE | MODULO | PLUS | MINUS |
             GT | GTE | LT | LTE | EE | NE | AND | OR ;

array_elem: IDENT (OPEN_BRACKET expr CLOSE_BRACKET)+ ;

array_liter: OPEN_BRACKET (expr (COMMA expr)*)? CLOSE_BRACKET ;
