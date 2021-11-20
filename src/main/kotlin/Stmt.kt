// ABSTRACT CLASS FOR Stmt

abstract class Stmt {
    interface Visitor<R> {
        fun visitExpressionStmt(stmt: Expression): R
        fun visitPrintStmt(stmt: Print): R
    }

    companion object {
        class Expression(expression: Expr) : Stmt() {
            val expression: Expr

            init {
                this.expression = expression
            }

            override fun <R> accept(visitor: Visitor<R>): R {
                return visitor.visitExpressionStmt(this)
            }
        }

        class Print(expression: Expr) : Stmt() {
            val expression: Expr

            init {
                this.expression = expression
            }

            override fun <R> accept(visitor: Visitor<R>): R {
                return visitor.visitPrintStmt(this)
            }
        }
    }

    abstract fun <R> accept(visitor: Visitor<R>): R
}
