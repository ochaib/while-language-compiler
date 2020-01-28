parser grammar WACCParser;

options {
  tokenVocab=WACCLexer;
}

// EOF indicates that the program must consume to the end of the input.
program: BEGIN func* stat END EOF;

func: type IDENT OPEN_PARENTHESES param_list? CLOSE_PARENTHESES IS stat END ;

param_list: param (COMMA param)* ;

param: type IDENT ;

stat: SKIP_                                 #Skip
      | type IDENT EQUALS assign_rhs        #Declaration
      | assign_lhs EQUALS assign_rhs        #Assignment
      | READ assign_lhs                     #Read
      | FREE expr                           #Free
      | RETURN expr                         #Return
      | EXIT expr                           #Exit
      | PRINT expr                          #Print
      | PRINTLN expr                        #Println
      | IF expr THEN stat ELSE stat FI      #If
      | WHILE expr DO stat DONE             #While
      | BEGIN stat END                      #Begin
      | stat SEMICOLON stat                 #Sequence;

assign_lhs: IDENT                           #LHSIdent
          | array_elem                      #LHSArrayElem
          | pair_elem                       #LHSPairElem;

assign_rhs: expr 
          | array_liter                     #RHSExpr
          | NEWPAIR OPEN_PARENTHESES expr COMMA expr CLOSE_PARENTHESES    #RHSNewPair
          | pair_elem                       #RHSPairElem
          | CALL IDENT OPEN_PARENTHESES arg_list? CLOSE_PARENTHESES       #RHSCall;

arg_list: expr (COMMA expr)* ;

pair_elem: FST expr                         #PairFst
         | SND expr                         #PairSnd;

type: base_type                             #TypeBase_type
    | array_type                            #TypeArray_type
    | pair_type                             #TypePair_type;

base_type: INT                              #IntBase_type
         | BOOL                             #BoolBase_type
         | CHAR                             #CharBase_type
         | STRING                           #StringBase_type;

array_type: type OPEN_BRACKET CLOSE_BRACKET ;

pair_type: PAIR OPEN_PARENTHESES pair_elem_type COMMA pair_elem_type CLOSE_PARENTHESES ;

pair_elem_type: base_type                   #PETBaseType
              | array_type                  #PETArrayType
              | PAIR                        #PETPair;

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
