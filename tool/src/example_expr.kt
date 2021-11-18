package tool

import Token

abstract class Expr {
    interface Visitor<R> {
        fun visitBinaryExpr(expr: Binary): R
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

    }

    abstract fun <R> accept(visitor: Visitor<R>): R
}