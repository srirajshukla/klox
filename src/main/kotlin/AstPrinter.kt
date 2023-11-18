/**
 * We are implementing the Visitor interface as AstPrinter class.
 * When we get any expression, say a binary expr, and we call the
 * print method on it, it calls the accept method of the binary expr
 * (as `this` type is `Expr` and it is overloaded in each subclass.
 * Now, in the Expr abstract class and all its derived classes -
 * they implement call `visitor<theirownname>` class, and we are
 * implementing those classes here. So effectively, we are calling the
 * relevant expr derived class by these steps -
 * 1. Call accept method on expr type
 * 2. Expr derived class call relevant class of the interface
 * 3. We are implementing the interface and all its method here
 * 4. So we are calling the relevant function according to the type
 */

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

    override fun visitLogicalExpr(expr: Expr.Companion.Logical): String {
        return "${parenthesize("left: ", expr.left)} ${expr.operator.lexeme} ${parenthesize("right: ", expr.right)}"
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

    override fun visitVariableExpr(expr: Expr.Companion.Variable): String {
        return expr.name.lexeme
    }

    override fun visitAssignExpr(expr: Expr.Companion.Assign): String {
        return parenthesize("assignment", expr)
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

    val logicalExpression = Expr.Companion.Logical(
        Expr.Companion.Literal(false),
        Token(TokenType.OR, "or", "or", 1),
        Expr.Companion.Literal(true)
    )

    println(AstPrinter().print(logicalExpression))
}