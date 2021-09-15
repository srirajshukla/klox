import TokenType.*

class Scanner(private var source: String) {
    private var tokens = arrayListOf<Token>()
    private var start = 0
    private var current = 0
    private var line = 1

    fun scanTokens(): ArrayList<Token> {
        while (!isAtEnd()) {
            // we are at the beginning of next lexeme.
            start = current
            scanToken()
        }

        tokens.add(Token(EOF, "", null, line))
        return tokens
    }

    private fun scanToken() {
        val c: Char = advance()
        when (c) {
            '(' -> addToken(LEFT_PAREN)
            ')' -> addToken(RIGHT_PAREN)
            '{' -> addToken(LEFT_BRACE)
            '}' -> addToken(RIGHT_BRACE)
            ',' -> addToken(COMMA)
            '.' -> addToken(DOT)
            '-' -> addToken(MINUS)
            '+' -> addToken(PLUS)
            ';' -> addToken(SEMICOLON)
            '*' -> addToken(STAR)
            '!' -> addToken(
                if (match('=')) {
                    BANG_EQUAL
                } else {
                    BANG
                })

            '=' -> addToken(
                if (match('=')) {
                    EQUAL_EQUAL
                } else {
                    EQUAL
                })

            '<' -> addToken(
                if (match('=')) {
                    LESS_EQUAL
                } else {
                    LESS
                })

            '>' -> addToken(
                if (match('=')) {
                    GREATER_EQUAL
                } else {
                    GREATER
                })

            '/' -> if (match('/')){
                    while(peek() != '\n' && !isAtEnd()) advance()
                } else{
                    addToken(SLASH)
                }

            ' '  -> {}
            '\r' -> {}
            '\t' -> {}
            '\n' -> line++
            
            else -> Lox.error(line, "Unexpected Character.")
        }
    }

    private fun peek(): Char {
        if (isAtEnd())
            return '\u0000'

        return source[current]
    }

    private fun match(expected: Char): Boolean {
        if (isAtEnd())
            return false

        if (source[current] != expected)
            return false

        current++
        return true
    }

    private fun advance(): Char = source[current++]

    private fun addToken(type: TokenType) = addToken(type, null)

    private fun addToken(type: TokenType, literal: Any?) {
        val text = source.substring(start, current)
        tokens.add(Token(type, text, literal, line))
    }

    private fun isAtEnd(): Boolean {
        return current >= source.length
    }
}