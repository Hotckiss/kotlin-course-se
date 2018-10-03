grammar Exp;

file
    : block
    ;

block
    : (statement)*
    ;

blockWithBraces
    : '{' block '}'
    ;

statement
    : function
    | variable
    | expression
    | whileLoop
    | conditional
    | assignment
    | returnStatement
    ;

function
    : 'fun' Identifier '(' parameterNames ')' blockWithBraces
    ;

variable
    : 'var' Identifier ('=' expression)?
    ;

parameterNames
    : (Identifier (',' Identifier)*)?
    ;

whileLoop
    : 'while' '(' expression ')' blockWithBraces
    ;

conditional
    : 'if' '(' expression ')' blockWithBraces ('else' blockWithBraces)?
    ;

assignment
    : Identifier '=' expression
    ;

returnStatement
    : 'return' expression
    ;

functionCall
    : Identifier '(' arguments ')'
    ;

arguments
    : (expression (',' expression)*)?
    ;

expression
    : binaryExpression
    | atomicExpression
    ;

binaryExpression
    : atomicExpression op = (MULT | DIV | MOD) expression
    | atomicExpression op = (PLUS | MINUS) expression
    | atomicExpression op = (GT | LT | GEQ | LEQ) expression
    | atomicExpression op = (EQ | NEQ) expression
    | atomicExpression op = AND expression
    | atomicExpression op = OR expression
    ;

atomicExpression
    : functionCall
    | Identifier
    | Number
    | '(' expression ')'
    ;

MULT : '*';
DIV : '/';
MOD : '%';
PLUS : '+';
MINUS : '-';

GT : '>';
LT : '<';
GEQ : '>=';
LEQ : '<=';
EQ : '==';
NEQ : '!=';

OR : '||';
AND : '&&';

Number
    : ([1-9] [0-9]*)
    | '0'
    ;

KEYWORDS
    : 'fun'
    | 'var'
    | 'while'
    | 'if'
    | 'else'
    | 'return'
    ;

Identifier
    : Literal (LiteralOrDigit)*
    ;

Literal
    : [a-z]
    | [A-Z]
    | '_'
    ;

LiteralOrDigit
    : Literal
    | [0-9]
    ;

COMMENT
    : '//' ~[\r\n]* -> skip
    ;

WS
    : (' ' | '\t' | '\r'| '\n') -> skip
    ;
