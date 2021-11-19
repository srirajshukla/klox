import kotlin.math.exp

class Interpreter : Expr.Visitor<Any?> {

    override fun visitBinaryExpr(expr: Expr.Companion.Binary): Any? {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)

        return when (expr.operator.type) {
            TokenType.GREATER -> left.toString().toDouble() > right.toString().toDouble()
            TokenType.GREATER_EQUAL -> left.toString().toDouble() >= right.toString().toDouble()
            TokenType.LESS -> left.toString().toDouble() < right.toString().toDouble()
            TokenType.LESS_EQUAL -> left.toString().toDouble() <= right.toString().toDouble()

            TokenType.MINUS -> left.toString().toDouble() - right.toString().toDouble()
            /**
             * We've overloaded the PLUS TokenType for number and string types
             */
            TokenType.PLUS -> {
                if (left is Double && right is Double) {
                    left.toString().toDouble() + right.toString().toDouble()
                } else if (left is String && right is String) {
                    left.toString() + right.toString()
                } else {
                    null
                }
            }
            TokenType.SLASH -> left.toString().toDouble() / right.toString().toDouble()
            TokenType.STAR  -> left.toString().toDouble() * right.toString().toDouble()

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
            TokenType.MINUS -> -right.toString().toDouble()
            else -> null
        }

    }

    private fun evaluate(expr: Expr) : Any? {
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
}