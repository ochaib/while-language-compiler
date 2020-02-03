import antlr.{WACCLexer, WACCParser}
import ast._
import java.io.IOException

import main.scala.ErrorListener
import org.antlr.v4.runtime.{CharStream => ANTLRCharStream, CharStreams => ANTLRCharStreams, CommonTokenStream => ANTLRTokenStream}

object Compiler extends App {
  def error(msg : String) {
    println(msg)
    System.exit(1)
  }

  if (args.length == 0) error("Error: No filename provided")

  println("Compiling: " + args(0))
  try {
    // Build the lexer and parse out tokens
    val file : ANTLRCharStream = ANTLRCharStreams.fromFileName(args(0))
    val lexer : WACCLexer = new WACCLexer(file)
    // Error listeners to highlight and return lexer errors
    val errorListener = new ErrorListener
    lexer.removeErrorListeners()
    lexer.addErrorListener(errorListener)

    val tokens : ANTLRTokenStream = new ANTLRTokenStream(lexer)
    // Build a parser and fetch the program context
    val parser : WACCParser  = new WACCParser(tokens)
    // Error listeners to highlight and return parser errors
    parser.removeErrorListeners()
    parser.addErrorListener(errorListener)

    val program : WACCParser.ProgramContext = parser.program()
    // Build the AST
    val visitor : ASTGenerator = new ASTGenerator()
    val tree : ASTNode = visitor.visitProgram(program)

    // Perform semantic analysis, generate symbolTable, and analyse tree.
  }
  catch {
    case ioerror : IOException => error("Error: File does not exist")
  }
}