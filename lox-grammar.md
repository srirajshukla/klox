The grammar for Lox

```
program         → declaration* EOF ;

declaration     → varDecl
                    | statemnt ;

varDecl         → "var" IDENTIFIER ( "=" expression )? ";" ;

statement       → exprStmt
                    | ifStmt
                    | printStmt 
                    | whileStmt
                    | block;

whileStmt       → "while" "(" expression ")" statement ;

ifStmt          → "if" "(" expression ")" statement
                    ("else"  statement)?

blocks          → "{"   declaration* "}" ;

exprStmt        → expression ";" ;

printStmt       → "print" expression ";" ;

expression      → assignment ;

assignment      → IDENTIFIER "=" assignment
                    | logical_or ;

logical_or      → logical_and ( "or" logical_and)* ;

logical_and     → equality ( "and" equality))* ;

equality        → comparison ( ( "!=" 
                    | "==" ) comparison )* ;

comparison      → term ( ( ">" 
                    | ">=" 
                    | "<" 
                    | "<=" ) term )* ;

term            → factor ( ( "-" 
                    | "+" ) factor )* ;

factor          → unary ( ( "/" 
                    | "*" ) unary )* ;

unary           → ( "!" | "-" ) unary
                    | primary ;

primary         → NUMBER 
                    | STRING 
                    | "true" 
                    | "false" 
                    | "nil"
                    | "(" expression ")" 
                    | IDENTIFIER;
```


Precedence and Associativity Rules

| Name | Operators | Associates |
| ---- | --------- | ---------- |
| Equality | `==` `!=` | Left |
| Comparison | `>` `>=` `<` `<=` | Left |
| Term | `-` `+` | Left |
| Factor | `/` `*` | Left |
| Unary | `!` `-` | Right |