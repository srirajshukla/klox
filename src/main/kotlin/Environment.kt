class Environment {
    private var values = hashMapOf<String, Any?>()


    fun define(name: String, value: Any?) {
        values.put(name, value)
    }

    fun get(name: Token) : Any? {
        if (values.containsKey(name.lexeme))
            return values.get(name.lexeme)
        throw RuntimeError(name, "Undefined Variable ${name.lexeme}.")
    }

    fun assign(name: Token, value: Any?) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value)
            return
        }

        throw RuntimeError(name, "Undefined Variable ${name.lexeme}.")
    }
}