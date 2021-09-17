import kotlin.math.exp

class AstPrinter : Expr.Visitor<String> {
    fun print(expr: Expr) : String {
        return expr.accept(this)
    }

    override fun visitBinaryExpr(expr: Expr.Companion.Binary): String {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right)
    }

    override fun visitGroupingExpr(expr: Expr.Companion.Grouping): String {
        return parenthesize("group", expr.expressions)
    }

    override fun visitLiteralExpr(expr: Expr.Companion.Literal): String {
        if (expr.value == null){
            return "nil"
        }
        return expr.value.toString()
    }

    override fun visitUnaryExpr(expr: Expr.Companion.Unary): String {
        return parenthesize(expr.operator.lexeme, expr.right)
    }


    private fun parenthesize(name: String, vararg exprs: Expr) : String {
        val builder = StringBuilder()
        builder.append("(").append(name)
        for (expr in exprs){
            builder.append(" ${expr.accept(this)}")
        }
        builder.append(")")
        return builder.toString()
    }
}

fun main(){
    val expression = Expr.Companion.Binary(
        Expr.Companion.Unary(
            Token(TokenType.MINUS, "-", null, 1),
            Expr.Companion.Literal(123)),
        Token(TokenType.STAR, "*", null, 1),
        Expr.Companion.Grouping(
            Expr.Companion.Literal(45.67)
        ))

    println(AstPrinter().print(expression))
}