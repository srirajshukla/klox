class Environment {
    private var values = hashMapOf<String, Any?>()
    private var enclosing : Environment? = null

    constructor() {
        this.enclosing = null
    }

    constructor(enclosing: Environment) {
        this.enclosing = enclosing
    }

    fun define(name: String, value: Any?) {
        values.put(name, value)
    }

    fun get(name: Token) : Any? {
        if (values.containsKey(name.lexeme))
            return values.get(name.lexeme)

        enclosing?.get(name)    // If enclosing is not null then you can try
                                // getting value from the enclosing (parent) scope

        throw RuntimeError(name, "Undefined Variable ${name.lexeme}.")
    }

    fun assign(name: Token, value: Any?) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value)
            return
        }

        enclosing?.assign(name, value)  // if the variable is not in this scope
                                        // try assigning it in the parent scope

        throw RuntimeError(name, "Undefined Variable ${name.lexeme}.")
    }
}