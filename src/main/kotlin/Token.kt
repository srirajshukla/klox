/**
 * The token class represents the individual tokens present.
 * This can be used by error handler to report on errors et cetera.
 */
class Token(var type: TokenType, var lexeme: String, var literal: Any?, var line: Int) {
    /**
     * type: The type of the token
     * lexeme: the actual value that is scanned in the code
     * literal: The literal value for literal and number types.
     * line: The line number on which the token is found.
     *      Used for error reporting.
     */
    override fun toString() : String {
        return "$type $lexeme $literal"
    }
}