import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

class Lox {
    /**
     * The main class Lox
     * @param: The array of strings the program was initialized with
     */
    fun main(args: Array<String>){
        if (args.size > 1){
            println("Usage: klox [script]")
            exitProcess(64)
        } else if(args.size == 1){
            runFile(args[0])
        } else{
            runPrompt()
        }
    }

    /**
     * @param : The file which has to be run
     */
    private fun runFile(path: String) {
        val bytes =  Files.readAllBytes(Paths.get(path))
        run(String(bytes, Charset.defaultCharset()))

        if (hadError)
            exitProcess(65)
        if (hadRuntimeError)
            exitProcess(70)
    }

    /**
     * Starts the Lox prompt
     */
    private fun runPrompt(){
        val input = InputStreamReader(System.`in`)
        val reader = BufferedReader(input)

        do {
            print("> ")
            val line = readLine() ?: break
            run(line)
            hadError = false
        } while (true)
    }

    /**
     * The actual class that implements the source
     * @param: source (String)
     */
    private fun run(source: String){
        val scanner = Scanner(source)
        val tokens = scanner.scanTokens()

        val parser = Parser(tokens)
        val statements : List<Stmt> = parser.parse()

        if (hadError)
            return

        if (statements != null) {
            interpreter.interpret(statements)
        } else{
            println("null?")
        }
        // todo: Allow null expressions
    }

    /*
        Kotlin doesn't allow java static functions.
        Instead, it encourages either package level functions,
        or companion objects.
        https://kotlinlang.org/docs/classes.html#companion-objects
        https://stackoverflow.com/questions/40352684/what-is-the-equivalent-of-java-static-methods-in-kotlin
     */
    companion object{
        var interpreter: Interpreter = Interpreter()
        var hadError : Boolean = false
        var hadRuntimeError: Boolean = false

        fun error(line: Int, message: String) {
            report(line, "", message)
        }
        private fun report(line: Int, where: String, message: String) {
            println("[line $line] Error $where : $message")
            hadError = true
        }
        fun error(token: Token, message: String) {
            if (token.type == TokenType.EOF){
                report(token.line, "at end", message)
            } else{
                report(token.line, "at '${token.lexeme}'", message)
            }
        }

        fun runtimeError(error: RuntimeError) {
            // System.err.println prints the error after > so, using println
            println(error.message + "\n[Line " + error.token.line + "]")
            hadRuntimeError = true
        }
    }


}