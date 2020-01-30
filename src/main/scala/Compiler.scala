import antlr.{WACCLexer, WACCParser}
import ast.{ASTGenerator, ASTNode}
import java.io.{IOException}
import org.antlr.v4.runtime.{
  CharStream => ANTLRCharStream,
  CharStreams => ANTLRCharStreams,
  CommonTokenStream => ANTLRTokenStream
}

object Compiler extends App {
  def error(msg : String) {
    println(msg)
    System.exit(1)
  }

  if (args.length == 0) error("Error: No filename provided")

  println("Compiling: " + args(0))
  try {
    val file : ANTLRCharStream = ANTLRCharStreams.fromFileName(args(0))
    val lexer : WACCLexer = new WACCLexer(file)
    val tokens : ANTLRTokenStream = new ANTLRTokenStream(lexer)
    val parser : WACCParser  = new WACCParser(tokens)
    val program : WACCParser.ProgramContext = parser.program()
    val visitor : ASTGenerator = new ASTGenerator()
  }
  catch {
    case ioerror : IOException => error("Error: File does not exist")
  }
}