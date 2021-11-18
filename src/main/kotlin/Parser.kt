/**
 * Implementing a Recursive Top-Down Parser based
 * on the grammar rules.
 */

class Parser(private var tokens: List<Token>) {
    var current : Int = 0


    private fun expression() : Expr {
        return equality()
    }

    private fun equality(): Expr {
        var expr : Expr = comparison()

        while(match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            val operator = previous()
            val right = comparison()
            expr =  Expr.Companion.Binary(expr, operator, right)
        }

        return expr
    }

    private fun comparison(): Expr {
        var expr = term()

        while(
            match(TokenType.GREATER
                , TokenType.EQUAL_EQUAL
                , TokenType.LESS
                , TokenType.LESS_EQUAL))
        {
            val operator = previous()
            val right = term()
            expr = Expr.Companion.Binary(expr, operator, right)
        }

        return expr
    }

    private fun term(): Expr {
        // todo: implement the term grammar rule
        return Expr.Companion.Literal(null)
    }

    private fun match(vararg types: TokenType) : Boolean{
        for(type in types){
            if (check(type)){
                advance()
                return true
            }
        }

        return false
    }

    private fun check(type: TokenType) : Boolean {
        if (isAtEnd())
            return false
        return peek().type == type
    }

    private fun advance() : Token {
        if (!isAtEnd())
            current++
        return previous()
    }

    private fun isAtEnd() : Boolean {
        return peek().type == TokenType.EOF
    }

    private fun peek() : Token {
        return tokens.get(current)
    }

    private fun previous() : Token {
        return tokens.get(current-1)
    }
}