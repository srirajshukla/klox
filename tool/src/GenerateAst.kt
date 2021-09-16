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
                "Binary   - left: Expr, operator: Token, right: Expr",
                "Grouping - expressions: Expr",
                "Literal  - Value: Any",
                "Unary    - operator: Token, right: Expr"
            )
            )
        }

        private fun defineAst(outputDir: String, baseName: String, types: List<String>) {
            val path = "$outputDir/$baseName.kt"
            println(path)
            val writer = PrintWriter(path, "UTF-8")

            writer.println("// ABSTRACT CLASS FOR $baseName")
            writer.println()
            writer.println("abstract class $baseName {")
            writer.println("  companion object {")

            // the ast classes
            for (type in types){
                val className = type.split('-')[0].trim()
                val fields = type.split('-')[1].trim()

                defineType(writer, baseName, className, fields)
            }

            writer.println("  }")

            writer.println("}")
            writer.close()
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

            writer.println("}")
        }
    }
}

fun main(args: Array<String>){
    GenerateAst.main(args)
}