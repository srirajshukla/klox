The grammar for Lox

```
program         → declaration* EOF ;

declaration     → varDecl
                    | statemnt ;

varDecl         → "var" IDENTIFIER ( "=" expression )? ";" ;

statement       → exprStmt
                    | printStmt ;

exprStmt        → expression ";" ;

printStmt       → "print" expression ";" ;

expression      → assignment ;

assignment      → IDENTIFIER "=" assignment
                    | equality ;

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