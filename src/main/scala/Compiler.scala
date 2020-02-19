import antlr._
import asm.CodeGenerator
import asm.instructions.Instruction
import asm.instructionset.ARM11
import ast.nodes.{ASTNode, ProgramNode}
import ast.visitors.{ASTGenerator, TypeCheckVisitor, Visitor}
import java.io.{
  IOException,
  PrintWriter,
  File
}
import org.antlr.v4.runtime.{
  CharStream => ANTLRCharStream,
  CharStreams => ANTLRCharStreams,
  CommonTokenStream => ANTLRTokenStream
}
import util.{
  ErrorListener,
  SemanticErrorLog,
  SyntaxErrorLog,
  ColoredConsole => console
}

object Compiler extends App {
  def error(msg: String): Unit = {
    console.error(msg)
    System.exit(1)
  }

  if (args.length == 0) error("No filename provided")
  if (!args(0).endsWith(".wacc")) error("Source code must be a .wacc file")

  try {
    // Build the lexer and parse out tokens
    val file: ANTLRCharStream = ANTLRCharStreams.fromFileName(args(0))
    val lexer: WACCLexer = new WACCLexer(file)

    // Error listeners to highlight and return lexer errors
    val errorListener = new ErrorListener
    lexer.removeErrorListeners()
    lexer.addErrorListener(errorListener)

    // Get tokens using lexer
    val tokens: ANTLRTokenStream = new ANTLRTokenStream(lexer)
    // Build a parser
    val parser: WACCParser = new WACCParser(tokens)
    // Error listeners to highlight and return parser errors
    parser.removeErrorListeners()
    parser.addErrorListener(errorListener)

    // Fetch program context
    val program: WACCParser.ProgramContext = parser.program()
    // Check for syntax errors, exit with 100 if there are.
    if (SyntaxErrorLog.errorCheck) {
      SyntaxErrorLog.printAllErrors()
      System.exit(100)
    }

    // Build AST
    val visitor: ASTGenerator = new ASTGenerator() // TODO: this should be a singleton
    val tree: ProgramNode = visitor.visit(program).asInstanceOf[ProgramNode]

    // TODO: add flag to disable semantic analysis as in ref compiler
    // Run semantic analyzer
    val semanticVisitor: Visitor = new TypeCheckVisitor(tree) // TODO: this should be a singleton
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

    // Use ARM 11 instruction set
    CodeGenerator.useInstructionSet(ARM11)
    // Generate ASM instructions from AST
    val instructions: IndexedSeq[Instruction] = CodeGenerator.generate(tree)
    // Format using ARM11 syntax
    val compiled: String = ARM11.print(instructions)
    // Appropriately name output file, no prefix because it should go in root directory
    val baseFilename: String = args(0).split("/").last
    val outputFile: String = baseFilename.stripSuffix(".wacc") + ".s"
    // Write our compiled code to the assembly file
    val writer = new PrintWriter(new File(outputFile))
    writer.write(compiled)
    writer.close()

  } catch {
    case ioerror: IOException => error("File does not exist")
  }
}
