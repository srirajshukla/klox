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
        var expr = factor()

        while (match(TokenType.MINUS, TokenType.PLUS)) {
            val operator = previous()
            val right = factor()
            expr = Expr.Companion.Binary(expr, operator, right)
        }

        return expr
    }

    private fun factor(): Expr {
        var expr = unary()

        while(match(TokenType.SLASH, TokenType.STAR)) {
            val operator = previous()
            val right = unary()
            expr = Expr.Companion.Binary(expr, operator, right)
        }

        return expr
    }

    private fun unary(): Expr {
        if (match(TokenType.BANG, TokenType.MINUS)) {
            val operator = previous()
            val right = unary()
            return Expr.Companion.Unary(operator, right)
        }

        return primary()
    }

    private fun primary(): Expr {
        if (match(TokenType.FALSE))
            return Expr.Companion.Literal(false)
        if (match(TokenType.TRUE))
            return Expr.Companion.Literal(true)
        if (match(TokenType.NIL))
            return Expr.Companion.Literal(null)

        if (match(TokenType.NUMBER, TokenType.STRING))
            return Expr.Companion.Literal(previous().literal)

        if (match (TokenType.LEFT_PAREN)) {
            val expr = expression()
            consume(TokenType.RIGHT_PAREN, "Expected ')' after the expression")
            return Expr.Companion.Grouping(expr)
        }

        // create an error here
    }

    private fun consume(type: TokenType, message: String) : Token {
        if (check(type))
            return advance()
        throw error(peek(), message)
    }

    private fun error(token: Token, message: String) : ParseError {

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