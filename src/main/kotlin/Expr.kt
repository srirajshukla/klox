// ABSTRACT CLASS FOR Expr

abstract class Expr {
    interface Visitor<R> {
        fun visitBinaryExpr(expr: Binary): R
        fun visitGroupingExpr(expr: Grouping): R
        fun visitLiteralExpr(expr: Literal): R
        fun visitUnaryExpr(expr: Unary): R
    }

    companion object {
        class Binary(left: Expr, operator: Token, right: Expr) : Expr() {
            val left: Expr
            val operator: Token
            val right: Expr

            init {
                this.left = left
                this.operator = operator
                this.right = right
            }

            override fun <R> accept(visitor: Visitor<R>): R {
                return visitor.visitBinaryExpr(this)
            }
        }

        class Grouping(expressions: Expr) : Expr() {
            val expressions: Expr

            init {
                this.expressions = expressions
            }

            override fun <R> accept(visitor: Visitor<R>): R {
                return visitor.visitGroupingExpr(this)
            }
        }

        class Literal(value: Any?) : Expr() {
            val value: Any?

            init {
                this.value = value
            }

            override fun <R> accept(visitor: Visitor<R>): R {
                return visitor.visitLiteralExpr(this)
            }
        }

        class Unary(operator: Token, right: Expr) : Expr() {
            val operator: Token
            val right: Expr

            init {
                this.operator = operator
                this.right = right
            }

            override fun <R> accept(visitor: Visitor<R>): R {
                return visitor.visitUnaryExpr(this)
            }
        }
    }

    abstract fun <R> accept(visitor: Visitor<R>): R
}
