/**
 * Implementing a Recursive Top-Down Parser based
 * on the grammar rules.
 */

class Parser(private var tokens: List<Token>) {
    private var current : Int = 0

    companion object {
        private class ParseError : RuntimeException()
    }

    private fun declarations() : Stmt?{
        try {
            if (match(TokenType.VAR))
                return varDeclaration()
            return statement()
        } catch (error: ParseError) {
            synchronize()
            return null
        }
    }

    private fun varDeclaration() : Stmt {
        val name : Token = consume(TokenType.IDENTIFIERS, "Expected a variable name.")

        var initializer : Expr? = null
        if (match(TokenType.EQUAL)){
            initializer = expression()
        }

        consume(TokenType.SEMICOLON, "Expected ; after variable declaration")
        return Stmt.Companion.Var(name, initializer)
    }

    private fun statement() : Stmt {
        if (match(TokenType.IF))
            return ifStatement()
        if (match(TokenType.PRINT))
            return printStatement()
        if (match(TokenType.LEFT_BRACE))
            return Stmt.Companion.Block(block())
        return expressionStatement()
    }

    private fun ifStatement() : Stmt {
        consume(TokenType.LEFT_PAREN,"Expected ( after 'if'.")
        val condition = expression()
        consume(TokenType.RIGHT_PAREN, "Expected ) after 'if' condition.")

        val thenBranch = statement()
        var elseBranch : Stmt? = null

        if (match(TokenType.ELSE)){
            elseBranch = statement()
        }

        return Stmt.Companion.If(condition, thenBranch, elseBranch)
    }

    private fun block() : List<Stmt> {
        val statements = mutableListOf<Stmt>()

        while(!check(TokenType.RIGHT_BRACE) && !isAtEnd()){
            declarations()?.let { statements.add(it) }
            // if declarations are not null, then call
            // lambda and add it to statements list
        }

        consume(TokenType.RIGHT_BRACE, "Expected '}' after block.")
        return statements
    }

    private fun printStatement() : Stmt {
        val value : Expr = expression()
        consume(TokenType.SEMICOLON, "Expected ; after value")
        return Stmt.Companion.Print(value)
    }

    private fun expressionStatement() : Stmt {
        val expr : Expr = expression()
        consume(TokenType.SEMICOLON, "Expected ; after value")
        return Stmt.Companion.Expression(expr)
    }

    private fun expression() : Expr {
        return assignment()
    }

    private fun assignment() : Expr {
        val expr = equality()

        if (match(TokenType.EQUAL)) {
            val equals = previous()
            val value = assignment()

            if (expr is Expr.Companion.Variable) {
                val name = expr.name
                return Expr.Companion.Assign(name, value)
            }

            error(equals, "Invalid assignment target.")
        }

        return expr
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

        if (match(TokenType.IDENTIFIERS))
            return Expr.Companion.Variable(previous())

        if (match (TokenType.LEFT_PAREN)) {
            val expr = expression()
            consume(TokenType.RIGHT_PAREN, "Expected ')' after the expression")
            return Expr.Companion.Grouping(expr)
        }

        // If none of the primary grammar rules are matched,
        // it is an error
        throw error(peek(), "Expected an expression.")
    }

    private fun consume(type: TokenType, message: String) : Token {
        if (check(type))
            return advance()
        throw error(peek(), message)
    }

    /**
     * start error handling
     */
    private fun error(token: Token, message: String) : ParseError {
        Lox.error(token, message)
        return ParseError()
    }

    private fun synchronize() {
        advance()

        while(!isAtEnd()){
            if (previous().type == TokenType.SEMICOLON)
                return

            when(peek().type) {
                TokenType.CLASS -> return
                TokenType.FUN -> return
                TokenType.VAR -> return
                TokenType.FOR -> return
                TokenType.IF -> return
                TokenType.WHILE -> return
                TokenType.PRINT -> return
                TokenType.RETURN -> return
                else -> {}
            }

            advance()
        }
    }

    /**
     * end error handling
     */

    /**
     * start helper methods for parsing
     */

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

    /**
     * end helper functions
     */

    fun parse() : List<Stmt> {
        val statements = mutableListOf<Stmt>()
        while(!isAtEnd()){
            declarations()?.let { statements.add(it) }
        }
        return statements
    }
}