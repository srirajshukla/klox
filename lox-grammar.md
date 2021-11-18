The grammar for Lox

```
expression     → equality ;

equality       → comparison ( ( "!=" 
                    | "==" ) comparison )* ;

comparison     → term ( ( ">" 
                    | ">=" 
                    | "<" 
                    | "<=" ) term )* ;

term           → factor ( ( "-" 
                    | "+" ) factor )* ;

factor         → unary ( ( "/" 
                    | "*" ) unary )* ;

unary          → ( "!" | "-" ) unary
                    | primary ;

primary        → NUMBER 
                    | STRING 
                    | "true" 
                    | "false" 
                    | "nil"
                    | "(" expression ")" ;
```


Precedence and Associativity Rules

| Name | Operators | Associates |
| ---- | --------- | ---------- |
| Equality | `==` `!=` | Left |
| Comparison | `>` `>=` `<` `<=` | Left |
| Term | `-` `+` | Left |
| Factor | `/` `*` | Left |
| Unary | `!` `-` | Right |