import antlr.{WACCLexer, WACCParser}
import ast.{ASTGenerator, ASTNode}
import java.io.{IOException}
import org.antlr.v4.runtime.{
  CharStream => ANTLRCharStream,
  CharStreams => ANTLRCharStreams,
  CommonTokenStream => ANTLRTokenStream
}
import util.{ColoredConsole => console}

object Compiler extends App {
  def error(msg : String) {
    console.error(msg)
    System.exit(1)
  }

  if (args.length == 0) error("No filename provided")

  console.info("Compiling: " + args(0))
  try {
    // Build the lexer and parse out tokens
    val file : ANTLRCharStream = ANTLRCharStreams.fromFileName(args(0))
    val lexer : WACCLexer = new WACCLexer(file)
    val tokens : ANTLRTokenStream = new ANTLRTokenStream(lexer)
    // Build a parser and fetch the program context
    val parser : WACCParser  = new WACCParser(tokens)
    val program : WACCParser.ProgramContext = parser.program()
    // Build the AST
    val visitor : ASTGenerator = new ASTGenerator()
    val tree : ASTNode = visitor.visit(program)
    println(tree.toString)
  }
  catch {
    case ioerror : IOException => error("File does not exist")
  }
}