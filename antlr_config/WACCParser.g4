parser grammar WACCParser;

options {
  tokenVocab=WACCLexer;
}

// EOF indicates that the program must consume to the end of the input.
program: BEGIN func* stat END EOF;

func: type ident OPEN_PARENTHESES param_list? CLOSE_PARENTHESES IS stat? (RETURN expr | EXIT expr) END ;

param_list: param (COMMA param)* ;

param: type ident ;

stat: SKIP_                                 #Skip
      | type ident EQUALS assign_rhs        #Declaration
      | assign_lhs EQUALS assign_rhs        #Assignment
      | READ assign_lhs                     #Read
      | FREE expr                           #Free
      | PRINT expr                          #Print
      | PRINTLN expr                        #Println
      | IF expr THEN stat ELSE stat FI      #If
      | WHILE expr DO stat DONE             #While
      | BEGIN stat END                      #Begin
      | stat SEMICOLON stat                 #Sequence;

assign_lhs: ident                           #AssignLHSIdent
          | array_elem                      #AssignLHSArrayElem
          | pair_elem                       #AssignLHSPairElem;

assign_rhs: expr                            #AssignRHSExpr
          | array_liter                     #AssignRHSLiteral
          | NEWPAIR OPEN_PARENTHESES expr COMMA expr CLOSE_PARENTHESES    #AssignRHSNewPair
          | pair_elem                       #AssignRHSPairElem
          | CALL ident OPEN_PARENTHESES arg_list? CLOSE_PARENTHESES       #AssignRHSCall;

arg_list: expr (COMMA expr)* ;

pair_elem: FST expr                         #PairFst
         | SND expr                         #PairSnd;

type: base_type                             #TypeBase_type
    | type OPEN_BRACKET CLOSE_BRACKET       #TypeArray_type
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

expr:
     OPEN_PARENTHESES expr CLOSE_PARENTHESES    #BracketExpr
    | expr binary_oper expr                 #ExprBinaryOper
    | unary_oper expr                       #ExprUnaryOper
    | int_liter                             #ExprIntLiter
    | bool_liter                            #ExprBoolLiter
    | char_liter                            #ExprCharLiter
    | str_liter                             #ExprStringLiter
    | pair_liter                            #ExprPairLiter
    | ident                                 #ExprIdent
    | array_elem                            #ExprArrayElem;

ident: IDENT;


binary_oper: MULTIPLY | DIVIDE | MODULO | PLUS | MINUS |
             GT | GTE | LT | LTE | EE | NE | AND | OR ;

unary_oper: NOT | MINUS | LEN | ORD | CHR;

array_elem: ident (OPEN_BRACKET expr CLOSE_BRACKET)+ ;

array_liter: OPEN_BRACKET (expr (COMMA expr)*)? CLOSE_BRACKET ;

sign: PLUS | MINUS;
int_liter: sign? DIGIT+;

bool_liter: BOOL_LIT;

char_liter: CHAR_LIT;

str_liter: STR_LIT;

pair_liter: PAIR_LIT;
