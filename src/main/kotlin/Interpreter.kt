class Interpreter : Expr.Visitor<Any?>, Stmt.Visitor<Unit> {
    private var environment = Environment()

    override fun visitAssignExpr(expr: Expr.Companion.Assign): Any? {
        val value = evaluate(expr.value)
        environment.assign(expr.name, value)
        return value
    }

    override fun visitBinaryExpr(expr: Expr.Companion.Binary): Any? {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)

        return when (expr.operator.type) {
            TokenType.GREATER -> {
                checkNumberOperands(expr.operator, left, right)
                left.toString().toDouble() > right.toString().toDouble()
            }
            TokenType.GREATER_EQUAL -> {
                checkNumberOperands(expr.operator, left, right)
                left.toString().toDouble() >= right.toString().toDouble()
            }
            TokenType.LESS -> {
                checkNumberOperands(expr.operator, left, right)
                left.toString().toDouble() < right.toString().toDouble()
            }
            TokenType.LESS_EQUAL -> {
                checkNumberOperands(expr.operator, left, right)
                left.toString().toDouble() <= right.toString().toDouble()
            }

            TokenType.MINUS -> {
                checkNumberOperands(expr.operator, left, right)
                left.toString().toDouble() - right.toString().toDouble()
            }
            /**
             * We've overloaded the PLUS TokenType for number and string types
             */
            TokenType.PLUS -> {
                if (left is Double && right is Double) {
                    left.toString().toDouble() + right.toString().toDouble()
                } else if (left is String && right is String) {
                    left.toString() + right.toString()
                } else {
                    throw RuntimeError(expr.operator, "operands must be two numbers or two strings")
                }
            }
            TokenType.SLASH -> {
                checkNumberOperands(expr.operator, left, right)
                left.toString().toDouble() / right.toString().toDouble()
            }
            TokenType.STAR  -> {
                checkNumberOperands(expr.operator, left, right)
                left.toString().toDouble() * right.toString().toDouble()
            }

            TokenType.BANG_EQUAL -> !isEqual(left, right)
            TokenType.EQUAL_EQUAL -> isEqual(left, right)

            else -> null
        }
    }

    override fun visitGroupingExpr(expr: Expr.Companion.Grouping): Any? {
        return evaluate(expr.expressions)
    }

    override fun visitLiteralExpr(expr: Expr.Companion.Literal): Any? {
        return expr.value
    }

    override fun visitUnaryExpr(expr: Expr.Companion.Unary): Any? {
        val right = evaluate(expr.right)?:0

        return when (expr.operator.type) {
            TokenType.BANG -> !isTruthy(right)
            TokenType.MINUS -> {
                checkNumberOperand(expr.operator, right)
                -right.toString().toDouble()
            }
            else -> null
        }

    }

    /**
     * Visitor for IF-ELSE block
     */
    override fun visitIfStmt(stmt: Stmt.Companion.If) {
        if (isTruthy(evaluate(stmt.condition))){
            execute(stmt.thenBranch)
        } else if (stmt.elseBranch != null){
            execute(stmt.elseBranch)
        }
    }

    override fun visitBlockStmt(stmt: Stmt.Companion.Block) {
        executeBlock(stmt.statements, Environment(environment))
    }

    private fun executeBlock(statements: List<Stmt>, environment: Environment){
        val previous = this.environment
        try {
            this.environment = environment

            for (statement in statements){
                execute(statement)
            }
        } finally {
            this.environment = previous
        }
    }

    override fun visitExpressionStmt(stmt: Stmt.Companion.Expression) {
        evaluate(stmt.expression)
    }

    override fun visitPrintStmt(stmt: Stmt.Companion.Print) {
        val value = evaluate(stmt.expression)
        println(stringify(value))
    }


    private fun evaluate(expr: Expr?) : Any? {
        if (expr==null)
            return null
        return expr.accept(this)
    }

    private fun isTruthy(obj: Any?): Boolean {
        if (obj == null){
            return false
        }
        if (obj is Boolean) {
            return obj.toString().toBoolean()
        }

        return true
    }

    private fun isEqual(obj1: Any?, obj2: Any?) : Boolean {
        if (obj1==null && obj2==null)
            return true
        if (obj1==null)
            return false

        return obj1.equals(obj2)
    }

    override fun visitVarStmt(stmt: Stmt.Companion.Var) {
        var value : Any? =  null
        if (stmt.initializer != null){
            value = evaluate(stmt.initializer)
        }

        environment.define(stmt.name.lexeme, value)
    }

    override fun visitVariableExpr(expr: Expr.Companion.Variable): Any? {
        return environment.get(expr.name)
    }

    override fun visitLogicalExpr(expr: Expr.Companion.Logical): Any? {
        val left = evaluate(expr.left)

        if (expr.operator.type == TokenType.OR) {
            if (isTruthy(left)) return left
        } else {
            if (!isTruthy(left)) return left
        }

        return evaluate(expr.right)
    }

    override fun visitWhileStmt(stmt: Stmt.Companion.While) {
        while(isTruthy(evaluate(stmt.condition))) {
            execute(stmt.body)
        }
    }
    /**
     * begin error handling helper function
     */
    private fun checkNumberOperand(operator: Token, operand: Any?){
        if (operand is Double)
            return
        throw RuntimeError(operator, "Operand must be a number.")
    }

    private fun checkNumberOperands(operator: Token, left: Any?, right: Any?) {
        if (left is Double && right is Double)
            return
        throw RuntimeError(operator, "operands must be a number")
    }
    /**
     * end error handling helper function
     */
    private fun execute(statement: Stmt) = statement.accept(this)

    fun interpret(statements: List<Stmt>) {
        try {
            for (statement in statements){
                execute(statement)
            }
        } catch (error: RuntimeError) {
            Lox.runtimeError(error)
        }
    }

    private fun stringify(value: Any?) : String {
        if (value==null)
            return "nil"

        if (value is Double){
            var valueStr = value.toString()
            if (valueStr.endsWith(".0")) {
                valueStr = valueStr.substring(0, valueStr.length-2)
            }
            return valueStr
        }

        return value.toString()
    }
}