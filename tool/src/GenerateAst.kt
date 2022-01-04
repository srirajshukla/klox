package tool

import java.io.PrintWriter
import kotlin.system.exitProcess

/**
 * A Helper class that generates the AST for given expression
 *
 * Tip: Write a file by hand how you'd like things to be structured
 * and then follow  that file to see how you'd implement things here
 */

class GenerateAst {
    companion object{
        fun main(args: Array<String>){
            if (args.size != 1){
                System.err.println("Usage: generate_ast <output directory>")
                exitProcess(64)
            }
            val outputDir: String = args[0]

            defineAst(outputDir, "Expr", listOf(
                "Assign   - name: Token, value: Expr",
                "Binary   - left: Expr, operator: Token, right: Expr",
                "Grouping - expressions: Expr",
                "Literal  - value: Any?",
                "Unary    - operator: Token, right: Expr",
                "Variable - name: Token"
            )
            )

            defineAst(outputDir, "Stmt", listOf(
                "Block      - statements: List<Stmt>",
                "Expression - expression: Expr?",
                "If         - condition: Expr, thenBranch: Stmt, elseBranch: Stmt?",
                "Print      - expression: Expr",
                "Var        - name: Token, initializer: Expr?",
            ))
        }

        private fun defineAst(outputDir: String, baseName: String, types: List<String>) {
            val path = "$outputDir/$baseName.kt"
            println(path)
            val writer = PrintWriter(path, "UTF-8")

            writer.println("// ABSTRACT CLASS FOR $baseName")
            writer.println()
            writer.println("abstract class $baseName {")

            defineVisitor(writer, baseName, types)

            // the ast classes
            writer.println("  companion object {")

            for (type in types){
                val className = type.split('-')[0].trim()
                val fields = type.split('-')[1].trim()

                defineType(writer, baseName, className, fields)
            }

            // closing of the companion object
            writer.println("  }")

            writer.println()
            writer.println("abstract fun <R> accept(visitor: Visitor<R>) : R")

            // closing of the base class
            writer.println("}")
            writer.close()
        }

        private fun defineVisitor(writer: PrintWriter, baseName: String, types: List<String>) {
            writer.println("interface Visitor<R> {")

            for (type in types){
                val typeName = type.split("-")[0].trim()
                writer.println("fun visit$typeName$baseName (${baseName.lowercase()}: $typeName) : R")
            }

            writer.println("}")
        }

        private fun defineType(writer: PrintWriter, baseName: String, className: String, fieldsList: String) {
            writer.println(" class $className($fieldsList):$baseName() {")

            val fields = fieldsList.split(", ")
            for (field in fields){
                writer.println(" val $field")
            }

            writer.println("  init {")
            for (field in fields){
                val name = field.split(":")[0]
                writer.println("  this.$name = $name")
            }
            writer.println("  }")

            writer.println()
            writer.println("   override fun<R> accept(visitor: Visitor<R>) : R {")
            writer.println("      return visitor.visit$className$baseName(this)")
            writer.println("    }")

            // closing of the type class
            writer.println("}")
        }
    }
}

fun main(args: Array<String>){
    GenerateAst.main(args)
}