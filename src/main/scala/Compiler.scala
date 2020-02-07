import antlr._
import ast.nodes.ASTNode
import ast.visitors.{
  ASTGenerator,
  TypeCheckVisitor,
  Visitor
}
import java.io.IOException

import org.antlr.v4.runtime.{
  CharStream => ANTLRCharStream,
  CharStreams => ANTLRCharStreams,
  CommonTokenStream => ANTLRTokenStream
}
import util.{
  ErrorListener, SemanticErrorLog, SyntaxErrorLog,
  ColoredConsole => console
}


object Compiler extends App {
  def error(msg : String): Unit = {
    console.error(msg)
    System.exit(1)
  }

  if (args.length == 0) error("No filename provided")

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


    // Build the AST
    val program : WACCParser.ProgramContext = parser.program()
    // Check for syntax errors, exit with 100 if there are.
    if (SyntaxErrorLog.errorCheck) {
      SyntaxErrorLog.printAllErrors()
      System.exit(100)
    }

    val visitor : ASTGenerator = new ASTGenerator()
    val tree : ASTNode = visitor.visit(program)

    // Check the AST for semantic errors
    val semanticVisitor : Visitor = new TypeCheckVisitor(tree)
    semanticVisitor.visit(tree)
    // Check for syntax errors, exit with 100 if there are, new ones could've appeared here.
    if (SyntaxErrorLog.errorCheck) {
      SyntaxErrorLog.printAllErrors()
      System.exit(100)
    }
    // Check for semantic errors, exit with 200 if there are.
    if (SemanticErrorLog.errorCheck) {
      SemanticErrorLog.printAllErrors()
      System.exit(200)
    }

    println(tree.toString)
  }
  catch {
    case ioerror : IOException => error("File does not exist")
  }
}