/**
 * The token class represents the individual tokens present.
 * This can be used by error handler to report on errors et cetera.
 */
class Token(var type: TokenType, var lexeme: String, var literal: Any, var line: Int) {

    override fun toString() : String {
        return "$type $lexeme $literal"
    }
}