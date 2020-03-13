import antlr._
import asm.{CodeGenerator, Optimiser}
import asm.instructions.Instruction
import asm.instructionset.ARM11
import ast.nodes.{ASTNode, ProgramNode}
import ast.visitors.{ASTGenerator, TypeCheckVisitor, Visitor}
import java.io.{File, IOException, PrintWriter}

import ast.symboltable.SymbolTable
import org.antlr.v4.runtime.{CharStream => ANTLRCharStream, CharStreams => ANTLRCharStreams, CommonTokenStream => ANTLRTokenStream}
import util.{ErrorListener, SemanticErrorLog, SyntaxErrorLog, ColoredConsole => console}

object Compiler extends App {
  def error(msg: String): Unit = {
    console.error(msg)
    System.exit(1)
  }

  if (args.length == 0) error("No filename provided")
  if (!args(0).endsWith(".wacc")) error("Source code must be a .wacc file")

  def handleImport(charstream: ANTLRCharStream): ProgramNode = {
    val lexer: WACCLexer = new WACCLexer(charstream)
    val errorListener = new ErrorListener
    lexer.removeErrorListeners()
    lexer.addErrorListener(errorListener)
    val tokens: ANTLRTokenStream = new ANTLRTokenStream(lexer)
    val parser: WACCParser = new WACCParser(tokens)
    parser.removeErrorListeners()
    parser.addErrorListener(errorListener)
    val program: WACCParser.ProgramContext = parser.program()
    if (SyntaxErrorLog.errorCheck) {
      SyntaxErrorLog.printAllErrors()
      System.exit(100)
    }
    val visitor: ASTGenerator = new ASTGenerator()
    val tree: ProgramNode = visitor.visit(program).asInstanceOf[ProgramNode]
    tree
  }

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

    // first, build the standard library
    val stdlib: ProgramNode = handleImport(ANTLRCharStreams.fromString(StandardLib.STDLIB))

    // next, build any import files (.wacch) in same directory
    val directory: String = args(0).substring(0, args(0).lastIndexOf('/')+1) + "./"
    val importedFiles: IndexedSeq[String] = (new File(directory)).listFiles.map(_.getPath).filter(_.endsWith(".wach"))
    val imports: IndexedSeq[ProgramNode] = importedFiles
      .map(fn => ANTLRCharStreams.fromFileName(fn))
      .map(handleImport(_))
    // Build AST
    val visitor: ASTGenerator = new ASTGenerator(imports=(imports :+ stdlib))
    val tree: ProgramNode = visitor.visit(program).asInstanceOf[ProgramNode]

    // TODO: add flag to disable semantic analysis as in ref compiler
    // Run semantic analyzer
    val topSymbolTable: SymbolTable = SymbolTable.topLevelSymbolTable(tree)
    val semanticVisitor
        : Visitor = new TypeCheckVisitor(tree, topSymbolTable) // TODO: this should be a singleton
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

    if (args contains "--check") System.exit(0)

    // Use ARM 11 instruction set
    console.info("Using ARM 11 instruction set.")
    CodeGenerator.useInstructionSet(ARM11)
    CodeGenerator.useTopSymbolTable(topSymbolTable)
    // Generate ASM instructions from AST
    console.log("Compiling...")
    val instructions: IndexedSeq[Instruction] =
      CodeGenerator.generateProgram(tree)

    // PEEPHOLE OPTIMISATION
//    val optimisedInstructions: IndexedSeq[Instruction] = Optimiser.optimise(instructions)

    // Format using ARM11 syntax
    val compiled: String = ARM11.print(instructions)
    // Appropriately name output file, no prefix because it should go in root directory
    val baseFilename: String = args(0).split("/").last
    val outputFile: String =
      (if (args contains "--batch") "assembly/" else "") +
        baseFilename.stripSuffix(".wacc") + ".s"
    console.info("Writing assembly to " + outputFile)
    // Write our compiled code to the assembly file
    val writer = new PrintWriter(new File(outputFile))
    writer.write(compiled)
    writer.close()
    console.info("Compilation finished successfully.")
  } catch {
    case ioerror: IOException => error("File does not exist")
  }
}