import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.system.exitProcess

class Lox {
    /**
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
        } while (true)
    }

    /**
     * The actual class that implements the source
     * @param: source (String)
     */
    private fun run(source: String){
        val scanner = Scanner(source)
        val tokens = scanner.tokens().toList()

        for(token in tokens){
            println(token)
        }
    }

}