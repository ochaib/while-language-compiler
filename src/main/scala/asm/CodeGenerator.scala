package asm

import java.sql.Statement

import asm.instructions._
import asm.instructionset._
import asm.registers.RegisterManager
import ast.nodes._

import scala.collection.immutable.Stream.Empty

object CodeGenerator {

  var instructionSet: InstructionSet = _
  var RM: RegisterManager = _

  def useInstructionSet(_instructionSet: InstructionSet): Unit = {
    instructionSet = _instructionSet
    RM = new RegisterManager(instructionSet)
  }

  // Common instructions
  def pushLR: Instruction = Push(None, List(instructionSet.getLR))
  def popPC: Instruction = Pop(None, List(instructionSet.getPC))
  def zeroReturn: Instruction = new Load(
    condition = None,
    asmType = new SignedByte,
    dest = instructionSet.getReturn,
    loadable = new Immediate(0),
    label = None
  )

  def generateProgram(program: ProgramNode): IndexedSeq[Instruction] = {
    // Generated instructions to encompass everything generated.
    val generatedInstructions: IndexedSeq[Instruction] = IndexedSeq[Instruction]()

    // Generated code for functions
    val functions: IndexedSeq[Instruction] = program.functions.flatMap(generateFunction)

    // Generated code for stats
    val stats: IndexedSeq[Instruction] = IndexedSeq[Instruction]()

    generatedInstructions ++ functions ++ IndexedSeq[Instruction](
      Label("main"),
      pushLR,
      // TODO: generate stats
      zeroReturn,
      popPC,
      new EndFunction
    )
  }

  def generateFunction(func: FuncNode): IndexedSeq[Instruction] = {
    IndexedSeq[Instruction](
      Label(s"f_${func.identNode.ident}"),
      pushLR,
      // TODO: generate stats
      popPC,
      new EndFunction
    )
  }

  def generateStatement(statement: StatNode): IndexedSeq[Instruction] = {

    statement match {
      // Create/return empty instruction list for skip node.
      case _: SkipNode => IndexedSeq[Instruction]()
      case declaration: DeclarationNode => generateDeclaration(declaration)
      case assign: AssignmentNode => generateAssignment(assign)
      case ReadNode(_, lhs) => generateRead(lhs)
      case FreeNode(_, expr) => generateFree(expr)
      case ReturnNode(_, expr) => generateReturn(expr)
      case ExitNode(_, expr) => generateExit(expr)

      // Unsure as of what to do for the print generation.
      case PrintNode(_, expr) => IndexedSeq[Instruction]()
      case PrintlnNode(_, expr) => IndexedSeq[Instruction]()

      case ifNode: IfNode => generateIf(ifNode)
      case whileNode: WhileNode => generateWhile(whileNode)
      case begin: BeginNode => generateBegin(begin)
      case SequenceNode(_, statOne, statTwo) => generateStatement(statOne) ++ generateStatement(statTwo)
    }
  }

  def generateDeclaration(declaration: DeclarationNode): IndexedSeq[Instruction] = ???

  def generateAssignment(assignment: AssignmentNode): IndexedSeq[Instruction] = ???

  def generateRead(lhs: AssignLHSNode): IndexedSeq[Instruction] = ???

  def generateFree(expr: ExprNode): IndexedSeq[Instruction] = ???

  def generateReturn(expr: ExprNode): IndexedSeq[Instruction] = ???

  def generateExit(expr: ExprNode): IndexedSeq[Instruction] = {
    // Must generate the instructions necessary for the exit code,
    // then branch to exit.
    generateExpression(expr) :+ BranchLink(None, Label("exit"))
  }

  def generateIf(ifNode: IfNode): IndexedSeq[Instruction] = ???

  def generateWhile(whileNode: WhileNode): IndexedSeq[Instruction] = ???

  def generateBegin(begin: BeginNode): IndexedSeq[Instruction] = ???

  def generateExpression(expr: ExprNode): IndexedSeq[Instruction] = ???


}



