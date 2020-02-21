package asm

import asm.instructions._
import asm.instructionset._
import asm.registers.{Register, RegisterManager}
import ast.nodes._

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
    asmType = Some(new SignedByte),
    dest = instructionSet.getReturn,
    loadable = new Immediate(0)
  )

  def generateProgram(program: ProgramNode): IndexedSeq[Instruction] = {
    // Generated instructions to encompass everything generated.
    val generatedInstructions: IndexedSeq[Instruction] = IndexedSeq[Instruction]()

    // Generated code for functions
    val functions: IndexedSeq[Instruction] = program.functions.flatMap(generateFunction)

    // Generated code for stats
    val stats: IndexedSeq[Instruction] = generateStatement(program.stat)

    generatedInstructions ++ functions ++ IndexedSeq[Instruction](
      Label("main"),
      pushLR) ++ stats ++ IndexedSeq[Instruction](
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
      // Possibly do more for return and exit, moving used register into r0 (return reg)?
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

  def generateAssignment(assignment: AssignmentNode): IndexedSeq[Instruction] = {
    generateAssignLHS(assignment.lhs) ++ generateAssignRHS(assignment.rhs)
  }

  def generateAssignLHS(lhs: AssignLHSNode): IndexedSeq[Instruction] = {
    lhs match {
      case ident: IdentNode => generateIdent(ident)
      case arrayElem: ArrayElemNode => generateArrayElem(arrayElem)
      case pairElem: PairElemNode => generatePairElem(pairElem)
      case call: CallNode => generateCall(call)
    }
  }

  def generateIdent(ident: IdentNode): IndexedSeq[Instruction] = ???

  def generateArrayElem(arrayElem: ArrayElemNode): IndexedSeq[Instruction] = ???

  def generatePairElem(pairElem: PairElemNode): IndexedSeq[Instruction] = ???

  def generateCall(call: CallNode): IndexedSeq[Instruction] = ???

  def generateAssignRHS(rhs: AssignRHSNode): IndexedSeq[Instruction] = {
    rhs match {
      case expr: ExprNode => generateExpression(expr)
      case arrayLiteral: ArrayLiteralNode => IndexedSeq[Instruction]()
      case newPair: NewPairNode => IndexedSeq[Instruction]()
      case pairElem: PairElemNode => IndexedSeq[Instruction]()
    }
  }

  def generateRead(lhs: AssignLHSNode): IndexedSeq[Instruction] = ???

  def generateFree(expr: ExprNode): IndexedSeq[Instruction] = ???

  def generateReturn(expr: ExprNode): IndexedSeq[Instruction] = ???

  def generateExit(expr: ExprNode): IndexedSeq[Instruction] = {
    // Must generate the instructions necessary for the exit code,
    // then branch to exit.
    // Need next available register to move into r0, temporary fix below.
    // TODO: Implement following code better.
    val regUsedByGenExp: Register = RM.nextVariableRegister()
    // So that it can actually be used by generateExpression.
    RM.free(regUsedByGenExp)
    generateExpression(expr) ++ IndexedSeq[Instruction](
      Move(None, instructionSet.getReturn, new ShiftedRegister(regUsedByGenExp)),
      BranchLink(None, Label("exit")))
  }

  def generateIf(ifNode: IfNode): IndexedSeq[Instruction] = ???

  def generateWhile(whileNode: WhileNode): IndexedSeq[Instruction] = ???

  def generateBegin(begin: BeginNode): IndexedSeq[Instruction] = ???

  def generateExpression(expr: ExprNode): IndexedSeq[Instruction] = {
    expr match {
      case Int_literNode(_, str)
                  => IndexedSeq[Instruction](new Load(None, Some(new SignedByte),
                     RM.nextVariableRegister(), new Immediate(str.toInt)))
      case Bool_literNode(_, bool)
                  => IndexedSeq[Instruction](new Load(None, Some(new SignedByte),
                     RM.nextVariableRegister(), new Immediate(if (bool) 1 else 0)))
      case Char_literNode(_, char)
                  => IndexedSeq[Instruction](new Load(None, Some(new SignedByte),
                     RM.nextVariableRegister(), new Immediate(char)))
      case Str_literNode(_, str)
                  => IndexedSeq[Instruction](new Load(None, Some(new SignedByte),
                     RM.nextVariableRegister(), Label(str)))
      // May replace with zeroReturn.
      case Pair_literNode(_)
                  => IndexedSeq[Instruction](new Load(None, Some(new SignedByte),
                     RM.nextVariableRegister(), new Immediate(0)))
      case ident: IdentNode => IndexedSeq[Instruction]()
      case arrayElem: ArrayElemNode => IndexedSeq[Instruction]()
      case unaryOperation: UnaryOperationNode => generateUnary(unaryOperation)
      case binaryOperation: BinaryOperationNode => generateBinary(binaryOperation)
    }
  }

  def generateUnary(unaryOperation: UnaryOperationNode): IndexedSeq[Instruction] = {
    unaryOperation match {
      // More must be done for these according to the reference compiler.
      case LogicalNotNode(_, expr) => generateExpression(expr)
      // Negate is not currently adding sign to the immediate to be loaded.
      case NegateNode(_, expr) => generateExpression(expr)
      case LenNode(_, expr) => generateExpression(expr)
      // Finished implementation as nothing else must be done.
      case OrdNode(_, expr) => generateExpression(expr)
      case ChrNode(_, expr) => generateExpression(expr)
    }
  }

  def generateBinary(binaryOperation: BinaryOperationNode): IndexedSeq[Instruction] = {
    binaryOperation match {
      case MultiplyNode(_, argOne, argTwo) => IndexedSeq[Instruction]()
      case DivideNode(_, argOne, argTwo) => IndexedSeq[Instruction]()
      case ModNode(_, argOne, argTwo) => IndexedSeq[Instruction]()
      case PlusNode(_, argOne, argTwo) => IndexedSeq[Instruction]()
      case MinusNode(_, argOne, argTwo) => IndexedSeq[Instruction]()
      case GreaterThanNode(_, argOne, argTwo) => IndexedSeq[Instruction]()
      case GreaterEqualNode(_, argOne, argTwo) => IndexedSeq[Instruction]()
      case LessThanNode(_, argOne, argTwo) => IndexedSeq[Instruction]()
      case LessEqualNode(_, argOne, argTwo) => IndexedSeq[Instruction]()
      case EqualToNode(_, argOne, argTwo) => IndexedSeq[Instruction]()
      case NotEqualNode(_, argOne, argTwo) => IndexedSeq[Instruction]()
      case LogicalAndNode(_, argOne, argTwo) => IndexedSeq[Instruction]()
      case LogicalOrNode(_, argOne, argTwo) => IndexedSeq[Instruction]()
    }
  }

}



