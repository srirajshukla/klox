import TokenType.*

class Scanner(private var source: String) {

    private var tokens = arrayListOf<Token>()
    /**
     * The start and current fields are offsets that index into the string.
     * The start field points to the first character in the lexeme
     * being scanned, and current points at the character currently
     * being considered. The line field tracks what source line
     * `current` is on, so we can produce tokens that know their location.
     */
    private var start = 0
    private var current = 0
    private var line = 1

    companion object{
        private var keywords = hashMapOf<String, TokenType>()
        init {
            keywords["and"] = AND
            keywords["class"] = CLASS
            keywords["else"] = ELSE
            keywords["false"] = FALSE
            keywords["for"] = FOR
            keywords["fun"] = FUN
            keywords["if"] = IF
            keywords["nil"] = NIL
            keywords["or"] = OR
            keywords["print"] = PRINT
            keywords["return"] = RETURN
            keywords["super"] = SUPER
            keywords["this"] = THIS
            keywords["true"] = TRUE
            keywords["var"] = VAR
            keywords["while"] = WHILE
        }
    }

    fun scanTokens(): ArrayList<Token> {
        while (!isAtEnd()) {
            // we are at the beginning of next lexeme.
            start = current
            scanToken()
        }
        // We add an `EOF` token after scanning all the characters.
        // It is not necessary, but it is a good practise.
        // We can also use it to detect EOF later.
        tokens.add(Token(EOF, "", null, line))
        return tokens
    }

    /**
     * The `scanToken()` function identifies the tokens and
     * calls the `addToken(type)` helper function to add
     * those tokens.
     *
     * This function also reports error if we've encountered an
     * unexpected character.
     */
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
                // This is a comment, it will go until the end of the line
                    while(peek() != '\n' && !isAtEnd()) advance()
                } else{
                    addToken(SLASH)
                }

            ' '  -> {}
            '\r' -> {}
            '\t' -> {}
            '\n' -> line++

            // Now string literals
            '"' -> string()


            else -> {
                if (isDigit(c)){
                    number()
                }
                // We assume that if the character is an alphabet or _,
                // it will be start of an identifier
                // todo: try changing the match to regex match like: "[A-Za-z_]".toRegex()
                else if (isAlpha(c)){
                    identifier()
                } else{
                    Lox.error(line, "Unexpected Character.")
                }
            }
        }
    }

    private fun identifier() {
        while(isAlphaNumeric(peek()) ) advance()

        val text = source.substring(start, current)
        val tokenType = keywords[text] ?: IDENTIFIERS

        addToken(tokenType)
    }

    private fun isAlphaNumeric(c: Char): Boolean {
        return isAlpha(c) || isDigit(c)
    }

    private fun isAlpha(c: Char): Boolean {
        return c in 'a'..'z' || c in 'A'..'Z' || c=='_'
    }

    private fun number() {
        while(isDigit(peek())) advance()

        /**
         * We are checking if this is a decimal number
         * or not, also since we are not allowing
         * numbers ending with . such as `123.`,
         * we are making sure char next to . is a digit.
         */
        if(peek()=='.' && isDigit(peekNext())){
            advance()
            while (isDigit(peek())) advance()
        }
        // todo: add error handling if the number is not a valid number

        this.addToken(NUMBER, source.substring(start, current).toDouble())
    }

    private fun peekNext(): Char {
        if (current+1>=source.length)
            return '\u0000'

        return source[current+1]
    }

    /**
     * Checks if the numbers are between 0 and 9
     * We are not using c.isDigit as it also returns
     * true for number from other script, like Devanagari,
     * or full width numbers etc.
     */
    private fun isDigit(c: Char): Boolean = c in '0'..'9'

    private fun string() {
        while(!isAtEnd() && peek()!= '"') {
            if (peek()=='\n')   line++
            advance()
        }

        if (isAtEnd()){
            Lox.error(line, "unterminated string")
            return
        }

        // move by the closing `"`
        advance()

        val valueOfStringLiteral = source.substring(start+1, current-1)
        addToken(STRING, valueOfStringLiteral)
    }

    /**
     * The `advance()` function reads and returns the current character
     * and moves the current pointer by one.
     * @return : Char -> The character at current position
     *
     * Side Effects: increases the current pointer by 1
     */
    private fun advance(): Char = source[current++]

    /**
     * Called by scanToken method to add tokens
     * Adds single and double character with the default value null
     */
    private fun addToken(type: TokenType) = addToken(type, null)

    private fun addToken(type: TokenType, literal: Any?) {
        val text = source.substring(start, current)
        tokens.add(Token(type, text, literal, line))
    }

    /**
     * The `peek()` function reads the current character.
     * If we are at the end, it returns a null character (\u0000)
     * Otherwise, it returns the current character.
     *
     * It **does not** move the current pointer.
     */
    private fun peek(): Char {
        if (isAtEnd())
            // Kotlin doesn't support normal null characters i.e. \0
            // So we are using unicode null characters
            return '\u0000'

        return source[current]
    }

    /**
     * The `match(Char)` function matches the parameter with
     * character with current pointer.
     *
     * @return If we are at end, it returns `False` otherwise,
     * if the characters match, it returns true and
     * increases the current pointer.
     * Otherwise, it returns false
     *
     * Side Effect: If characters match, the current pointer is
     * increased.
     */
    private fun match(expected: Char): Boolean {
        if (isAtEnd())
            return false

        if (source[current] != expected)
            return false

        current++
        return true
    }


    /**
     * This functions tells us if we've consumed all the characters
     */
    private fun isAtEnd(): Boolean {
        return current >= source.length
    }
}