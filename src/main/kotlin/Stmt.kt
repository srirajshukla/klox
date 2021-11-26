// ABSTRACT CLASS FOR Stmt

abstract class Stmt {
    interface Visitor<R> {
        fun visitExpressionStmt(stmt: Expression): R
        fun visitPrintStmt(stmt: Print): R
        fun visitVarStmt(stmt: Var): R
    }

    companion object {
        class Expression(expression: Expr?) : Stmt() {
            val expression: Expr?

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

        class Var(name: Token, initializer: Expr?) : Stmt() {
            val name: Token
            val initializer: Expr?

            init {
                this.name = name
                this.initializer = initializer
            }

            override fun <R> accept(visitor: Visitor<R>): R {
                return visitor.visitVarStmt(this)
            }
        }
    }

    abstract fun <R> accept(visitor: Visitor<R>): R
}
