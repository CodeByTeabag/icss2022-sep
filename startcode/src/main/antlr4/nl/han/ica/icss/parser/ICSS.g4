grammar ICSS;

//--- LEXER: ---

// IF support:
IF: 'if';
ELSE: 'else';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';


//Literals
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;


//Color value takes precedence over id idents
COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f];

//Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

//General identifiers
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

//
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';


//--- PARSER: ---
// Level 3
stylesheet: variableAssignment* stylerule+;
variableAssignment: variableReference ASSIGNMENT_OPERATOR expression SEMICOLON;
stylerule: selector OPEN_BRACE statement* CLOSE_BRACE;
statement: declaration | condition;
selector: ID_IDENT | CLASS_IDENT | LOWER_IDENT;
declaration: property COLON expression SEMICOLON;
variableReference : CAPITAL_IDENT;
property: LOWER_IDENT;
expression: value (operator value)*;
value: COLOR | PIXELSIZE | PERCENTAGE | SCALAR | TRUE | FALSE | LOWER_IDENT | CAPITAL_IDENT;
operator: PLUS | MIN | MUL;
ifClause: IF BOX_BRACKET_OPEN variableReference BOX_BRACKET_CLOSE OPEN_BRACE statement* CLOSE_BRACE;
elseClause: ELSE OPEN_BRACE statement* CLOSE_BRACE;
condition: ifClause (elseClause)?;





