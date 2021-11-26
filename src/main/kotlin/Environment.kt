class Environment {
     var values = hashMapOf<String, Any?>()
     var enclosing : Environment? = null

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



        if (enclosing!=null){
            return enclosing?.get(name)   // if the variable is not in this scope
                                          // try getting its value from parent scope
        }

        throw RuntimeError(name, "Undefined Variable ${name.lexeme}.")
    }

    fun assign(name: Token, value: Any?) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value)
            return
        }

        if (enclosing!=null){
            enclosing?.assign(name,value)   // if the variable is not in this scope
            return                          // try assigning it in the parent scope
        }

        throw RuntimeError(name, "Undefined Variable ${name.lexeme}.")
    }

    override fun toString(): String {
        val ans = values.toString()
        if (enclosing==null)
            return ans
        return "$ans {${enclosing.toString()}} "
    }
}

fun main(){
    val e1 = Environment()
    println(e1)
    e1.define("a", 2)
    println(e1)
    e1.define("b", 3)
    println(e1)
    e1.assign(Token(TokenType.VAR, "a", "a", 1), 1)
    println(e1)

    val e2 = Environment(e1)
    println(e2)

    println(e2.get(Token(TokenType.VAR, "a", "a", 1)))
    e2.assign(Token(TokenType.VAR, "a", "a", 1), 1)
    println(e2)
}