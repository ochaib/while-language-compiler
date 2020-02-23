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

  def generateDeclaration(declaration: DeclarationNode): IndexedSeq[Instruction] = {
    generateAssignRHS(declaration.rhs) ++ generateIdent(declaration.ident)
  }

  def generateAssignment(assignment: AssignmentNode): IndexedSeq[Instruction] = {
    generateAssignRHS(assignment.rhs) ++ generateAssignLHS(assignment.lhs)
  }

  def generateAssignLHS(lhs: AssignLHSNode): IndexedSeq[Instruction] = {
    lhs match {
      case ident: IdentNode => generateIdent(ident)
      case arrayElem: ArrayElemNode => generateArrayElem(arrayElem)
      case pairElem: PairElemNode => generatePairElem(pairElem)
    }
  }

  def generateIdent(ident: IdentNode): IndexedSeq[Instruction] = {
    IndexedSeq[Instruction](
      new Store(None, Some(new ByteType), RM.peekVariableRegister(), instructionSet.getSP)
    )
  }

  def generateArrayElem(arrayElem: ArrayElemNode): IndexedSeq[Instruction] = {
    generateIdent(arrayElem.identNode) ++
      arrayElem.exprNodes.flatMap(generateExpression) ++ IndexedSeq[Instruction]()
  }

  def generatePairElem(pairElem: PairElemNode): IndexedSeq[Instruction] = IndexedSeq[Instruction]()

  def generateCall(call: CallNode): IndexedSeq[Instruction] = IndexedSeq[Instruction]()

  def generateAssignRHS(rhs: AssignRHSNode): IndexedSeq[Instruction] = {
    rhs match {
      case expr: ExprNode => generateExpression(expr)
      case arrayLiteral: ArrayLiteralNode => generateArrayLiteral(arrayLiteral)
      case newPair: NewPairNode => generateNewPair(newPair)
      case pairElem: PairElemNode => generatePairElem(pairElem)
      case call: CallNode => generateCall(call)
    }
  }

  def generateArrayLiteral(arrayLiteral: ArrayLiteralNode): IndexedSeq[Instruction] = {
    val varReg1 = RM.nextVariableRegister()

    // Need to load size of array into r0, this is a temporary hardcode below.
    val preExprInstructions = IndexedSeq[Instruction](
      new Load(None, Some(new SignedByte), instructionSet.getReturn, new Immediate(8)),
      BranchLink(None, Label("malloc")),
      Move(None, varReg1, new ShiftedRegister(instructionSet.getReturn))
    )

    var generatedExpressions: IndexedSeq[Instruction] = IndexedSeq[Instruction]()

    arrayLiteral.exprNodes.foreach(expr => generatedExpressions
      ++= generateExpression(expr) :+
      new Store(None, Some(new ByteType), RM.peekVariableRegister(), varReg1, new Immediate(4)))

    val varReg2 = RM.nextVariableRegister()

    // However above once expression in arrayLiteral is generated we must store it.
    val postExprInstructions = generatedExpressions ++ IndexedSeq[Instruction](
      // Store number of elements in array in next available variable register.
      // Temporary hardcode below.
      new Load(None, Some(new SignedByte), varReg2, new Immediate(1)),
      new Store(None, Some(new ByteType), varReg2, varReg1)
    )
    // Since we are done with varReg1 above we can free it back to available registers.
    RM.freeVariableRegister(varReg1)

    preExprInstructions ++ postExprInstructions
  }

  def generateNewPair(newPair: NewPairNode): IndexedSeq[Instruction] = IndexedSeq[Instruction]()

  def generateRead(lhs: AssignLHSNode): IndexedSeq[Instruction] = IndexedSeq[Instruction]()

  def generateFree(expr: ExprNode): IndexedSeq[Instruction] = IndexedSeq[Instruction]()

  def generateReturn(expr: ExprNode): IndexedSeq[Instruction] = IndexedSeq[Instruction]()

  def generateExit(expr: ExprNode): IndexedSeq[Instruction] = {
    // Must generate the instructions necessary for the exit code,
    // then branch to exit.
    // Need next available register to move into r0, temporary fix below.
    // TODO: Implement following code better.
    val regUsedByGenExp: Register = RM.peekVariableRegister()
    // So that it can actually be used by generateExpression.
    RM.freeVariableRegister(regUsedByGenExp)
    generateExpression(expr) ++ IndexedSeq[Instruction](
      Move(None, instructionSet.getReturn, new ShiftedRegister(regUsedByGenExp)),
      BranchLink(None, Label("exit")))
  }

  def generateIf(ifNode: IfNode): IndexedSeq[Instruction] = IndexedSeq[Instruction]()

  def generateWhile(whileNode: WhileNode): IndexedSeq[Instruction] = IndexedSeq[Instruction]()

  def generateBegin(begin: BeginNode): IndexedSeq[Instruction] = IndexedSeq[Instruction]()

  def generateExpression(expr: ExprNode): IndexedSeq[Instruction] = {
    expr match {
      case Int_literNode(_, str)
                  => IndexedSeq[Instruction](new Load(None, Some(new SignedByte),
                     RM.peekVariableRegister(), new Immediate(str.toInt)))
      case Bool_literNode(_, bool)
                  => IndexedSeq[Instruction](Move(None, RM.peekVariableRegister(),
                     new Immediate(if (bool) 1 else 0)))
      case Char_literNode(_, char)
                  => IndexedSeq[Instruction](Move(None, RM.peekVariableRegister(),
                     new ImmediateChar(char)))
      // Need to produce message instead of Label(str).
      case Str_literNode(_, str)
                  => IndexedSeq[Instruction](new Load(None, Some(new SignedByte),
                     RM.peekVariableRegister(), Label(str)))
      // May replace with zeroReturn.
      case Pair_literNode(_)
                  => IndexedSeq[Instruction](new Load(None, Some(new SignedByte),
                     RM.peekVariableRegister(), new Immediate(0)))
      case ident: IdentNode => generateIdent(ident)
      case arrayElem: ArrayElemNode => generateArrayElem(arrayElem)
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
    val varReg1 = RM.nextVariableRegister()
    val varReg2 = RM.peekVariableRegister()
    RM.freeVariableRegister(varReg1)

    binaryOperation match {
      case MultiplyNode(_, argOne, argTwo) =>
        generateExpression(argTwo) ++ generateExpression(argOne) ++
        IndexedSeq[Instruction](SMull(None, conditionFlag = false, varReg1,
                                      varReg2, varReg1, varReg2))
      case DivideNode(_, argOne, argTwo) => IndexedSeq[Instruction]()
      case ModNode(_, argOne, argTwo) => IndexedSeq[Instruction]()
      case PlusNode(_, argOne, argTwo) =>
        generateExpression(argTwo) ++ generateExpression(argOne) ++
        IndexedSeq[Instruction](Add(None, conditionFlag = false, varReg1,
                                    varReg1, new ShiftedRegister(varReg2)))
      case MinusNode(_, argOne, argTwo) =>
        generateExpression(argTwo) ++ generateExpression(argOne) ++
        IndexedSeq[Instruction](Subtract(None, conditionFlag = false, varReg1,
                                         varReg1, new ShiftedRegister(varReg2)))

      case GreaterThanNode(_, argOne, argTwo) =>
        generateExpression(argTwo) ++ generateExpression(argOne) ++
        IndexedSeq[Instruction](
          Compare(None, varReg1, new ShiftedRegister(varReg2)),
          Move(Some(GreaterThan), varReg1, new Immediate(1)),
          Move(Some(LessEqual), varReg1, new Immediate(0)))
      case GreaterEqualNode(_, argOne, argTwo) =>
        generateExpression(argTwo) ++ generateExpression(argOne) ++
        IndexedSeq[Instruction](
          Compare(None, varReg1, new ShiftedRegister(varReg2)),
          Move(Some(GreaterEqual), varReg1, new Immediate(1)),
          Move(Some(LessThan), varReg1, new Immediate(0)))
      case LessThanNode(_, argOne, argTwo) =>
        generateExpression(argTwo) ++ generateExpression(argOne) ++
        IndexedSeq[Instruction](
          Compare(None, varReg1, new ShiftedRegister(varReg2)),
          Move(Some(LessThan), varReg1, new Immediate(1)),
          Move(Some(GreaterEqual), varReg1, new Immediate(0)))
      case LessEqualNode(_, argOne, argTwo) =>
        generateExpression(argTwo) ++ generateExpression(argOne) ++
        IndexedSeq[Instruction](
          Compare(None, varReg1, new ShiftedRegister(varReg2)),
          Move(Some(LessEqual), varReg1, new Immediate(1)),
          Move(Some(GreaterThan), varReg1, new Immediate(0)))
      case EqualToNode(_, argOne, argTwo) =>
        generateExpression(argTwo) ++ generateExpression(argOne) ++
        IndexedSeq[Instruction](
          Compare(None, varReg1, new ShiftedRegister(varReg2)),
          Move(Some(Equal), varReg1, new Immediate(1)),
          Move(Some(NotEqual), varReg1, new Immediate(0)))
      case NotEqualNode(_, argOne, argTwo) =>
        generateExpression(argTwo) ++ generateExpression(argOne) ++
        IndexedSeq[Instruction](
          Compare(None, varReg1, new ShiftedRegister(varReg2)),
          Move(Some(NotEqual), varReg1, new Immediate(1)),
          Move(Some(Equal), varReg1, new Immediate(0)))

      case LogicalAndNode(_, argOne, argTwo) =>
        generateExpression(argTwo) ++ generateExpression(argOne) ++
        IndexedSeq[Instruction](And(None, conditionFlag = false, varReg1,
                                    varReg1, new ShiftedRegister(varReg2)))
      case LogicalOrNode(_, argOne, argTwo) =>
        generateExpression(argTwo) ++ generateExpression(argOne) ++
        IndexedSeq[Instruction](Or(None, conditionFlag = false, varReg1,
                                   varReg1, new ShiftedRegister(varReg2)))
    }
  }

}



