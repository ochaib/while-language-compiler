package asm

import asm.instructions._
import asm.instructionset._
import asm.registers._
import asm.utilities._
import ast.nodes._
import ast.symboltable._

object CodeGenerator {

  var symbolTableManager: SymbolTableManager = _
  var instructionSet: InstructionSet = _
  var RM: RegisterManager = _
  var topSymbolTable: SymbolTable = _
  var currentSymbolTable: SymbolTable = _

  // Keep track of number of branches.
  var n_branches = 0

  def useInstructionSet(_instructionSet: InstructionSet): Unit = {
    instructionSet = _instructionSet
    RM = new RegisterManager(instructionSet)
    Utilities.useRegisterManager(RM)
  }

  def useTopSymbolTable(symbolTable: SymbolTable): Unit = {
    symbolTableManager = SymbolTableManager(symbolTable)
    topSymbolTable = symbolTable
  }

  // Common instructions
  def pushLR: Instruction = Push(None, List(instructionSet.getLR))
  def popPC: Instruction = Pop(None, List(instructionSet.getPC))
  def zeroReturn: Instruction = new Load(
    condition = None,
    asmType = None,
    dest = instructionSet.getReturn,
    loadable = new LoadableExpression(0)
  )

  def generateProgram(program: ProgramNode): IndexedSeq[Instruction] = {
    assert(symbolTableManager != null, "Top level symbol table needs to be defined.")
    assert(instructionSet != null, "Instruction set needs to be defined.")
    assert(RM != null, "Register manager needs to be defined.")

    // Generated code for functions
    val functions: IndexedSeq[Instruction] = program.functions.flatMap(generateFunction)

    // Update the current symbol table for main method
    currentSymbolTable = symbolTableManager.nextScope()

    // Generated code for stats
    val stats: IndexedSeq[Instruction] = generateStatement(program.stat)

    // Generated instructions to encompass everything generated.
    val generatedInstructions: IndexedSeq[Instruction] = (functions
      ++ IndexedSeq[Instruction](Label("main"), pushLR,
      Subtract(None, conditionFlag = false,
      instructionSet.getSP, instructionSet.getSP, new Immediate(getScopeStackSize(currentSymbolTable)))
    ) ++ stats)

    val endInstructions = IndexedSeq[Instruction](
      Add(None, conditionFlag = false, instructionSet.getSP,
        instructionSet.getSP, new Immediate(getScopeStackSize(currentSymbolTable))),
      zeroReturn, popPC, new EndFunction)

    // Leave the current scope
    // symbolTableManager.leaveScope()

    generatedInstructions ++ endInstructions
  }

  def generateFunction(func: FuncNode): IndexedSeq[Instruction] = {

    // Update the current symbol table to function block
    currentSymbolTable = symbolTableManager.nextScope()

    var labelPushLR = IndexedSeq[Instruction](Label(s"f_${func.identNode.ident}"), pushLR)
    if (func.paramList.isDefined)
      labelPushLR ++= func.paramList.get.paramList.flatMap(generateParam)
    // Otherwise nothing?

    // Generate instructions for statement.
    val statInstructions = generateStatement(func.stat)

    var popEndInstruction = IndexedSeq[Instruction](popPC, new EndFunction)

    labelPushLR ++ statInstructions ++ popEndInstruction
  }

//  def generateParamList(paramList: ParamListNode): IndexedSeq[Instruction] = IndexedSeq[Instruction]()

  def generateParam(param: ParamNode): IndexedSeq[Instruction] = IndexedSeq[Instruction]()

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
      case PrintNode(_, expr) => generatePrint(expr)
      case PrintlnNode(_, expr) => generatePrint(expr)

      case ifNode: IfNode => generateIf(ifNode)
      case whileNode: WhileNode => generateWhile(whileNode)
      case begin: BeginNode => generateBegin(begin)
      case SequenceNode(_, statOne, statTwo) => generateStatement(statOne) ++ generateStatement(statTwo)
    }
  }

  def generateDeclaration(declaration: DeclarationNode): IndexedSeq[Instruction] = {
    // TODO
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
    // Retrieve actual size for ident from symbol table.
    val identSize = getSize(ident.getType(topSymbolTable, currentSymbolTable))

    // ASMType to classify if ident is of type bool and if so should be loaded
    // with ByteType triggering STRB instead of the usual STR.
    var asmType: Option[ASMType] = None

    if (ident.getType(topSymbolTable, currentSymbolTable)
              == BoolTypeNode(null).getType(topSymbolTable, currentSymbolTable))
      asmType = Some(ByteType)

    IndexedSeq[Instruction](new Store(None, asmType,
      RM.peekVariableRegister(), instructionSet.getSP))
//      new Immediate(identSize)))
  }

  def generateArrayElem(arrayElem: ArrayElemNode): IndexedSeq[Instruction] = {
    generateIdent(arrayElem.identNode) ++
      arrayElem.exprNodes.flatMap(generateExpression) ++ IndexedSeq[Instruction]()
  }

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
    // Because we assume every expr in the array is of the same type.
    var exprElemSize = getSize(arrayLiteral.exprNodes.head.getType(topSymbolTable, currentSymbolTable))
    val arrayLength = arrayLiteral.exprNodes.length
    var intSize = 4

    // Calculations necessary to retrieve size of array for loading into return.
    val arraySize = intSize + arrayLength * exprElemSize

    val preExprInstructions = IndexedSeq[Instruction](
      new Load(None, None, instructionSet.getReturn, new LoadableExpression(arraySize)),
      BranchLink(None, Label("malloc")),
      Move(None, varReg1, new ShiftedRegister(instructionSet.getReturn))
    )

    var generatedExpressions: IndexedSeq[Instruction] = IndexedSeq[Instruction]()

    var acc = exprElemSize
    // Generate expression instructions for each expression node in the array.
    arrayLiteral.exprNodes.foreach(expr => { generatedExpressions ++= generateExpression(expr) :+
      new Store(None, None, RM.peekVariableRegister(), varReg1,
        // Replaced hardcoded 4 with actual expression type.
        new Immediate(acc));  acc = acc + getSize(expr.getType(topSymbolTable, currentSymbolTable))})

    val varReg2 = RM.nextVariableRegister()

    // However above once expression in arrayLiteral is generated we must store it.
    val postExprInstructions = generatedExpressions ++ IndexedSeq[Instruction](
      // Store number of elements in array in next available variable register.
      new Load(None, None, varReg2, new LoadableExpression(arrayLength)),
      new Store(None, None, varReg2, varReg1)
    )
    // Since we are done with varReg1 above we can free it back to available registers.
    RM.freeVariableRegister(varReg1)

    preExprInstructions ++ postExprInstructions
  }

  def generateNewPair(newPair: NewPairNode): IndexedSeq[Instruction] = {
    val varReg1 = RM.nextVariableRegister()

    // Generate instructions for the new pair.
    val preExprInstructions: IndexedSeq[Instruction] = IndexedSeq[Instruction](
      // TODO: REPLACE MAGIC NUMBER FOR PAIR SIZE BELOW
      new Load(None, None, instructionSet.getReturn, new LoadableExpression(8)),
      BranchLink(None, Label("malloc")),
      Move(None, varReg1, new ShiftedRegister(instructionSet.getReturn))
    )

    // Generate instructions for the expressions and those necessary to allocate space for them on the stack.
    val fstInstructions = generateNPElem(newPair.fstElem, varReg1, isSnd = false)
    val varReg2 = RM.nextVariableRegister()
    val sndInstructions = generateNPElem(newPair.sndElem, varReg2, isSnd = true)

    preExprInstructions ++ fstInstructions ++ sndInstructions
  }

  def generateNPElem(expr: ExprNode, varReg: Register, isSnd: Boolean): IndexedSeq[Instruction] = {
    // Size of type of expression.
    val exprSize = getSize(expr.getType(topSymbolTable, currentSymbolTable))
    val pairSizeOffset = 4

    val exprInstructions = generateExpression(expr)

    var coreInstructions = IndexedSeq[Instruction](
        // Load the size of the type into a variable register.
        new Load(None, None, instructionSet.getReturn, new LoadableExpression(exprSize)),
        BranchLink(None, Label("malloc")))

    // Check if B suffix is necessary (ByteType).
    if ((expr.getType(topSymbolTable, currentSymbolTable)
         == BoolTypeNode(null).getType(topSymbolTable, currentSymbolTable)) ||
        (expr.getType(topSymbolTable, currentSymbolTable)
         == CharTypeNode(null).getType(topSymbolTable, currentSymbolTable))) {
      coreInstructions = coreInstructions :+ new Store(None, Some(ByteType), RM.peekVariableRegister(), instructionSet.getReturn)
    } else {
      coreInstructions = coreInstructions :+ new Store(None, None, RM.peekVariableRegister(), instructionSet.getReturn)
    }

    RM.freeVariableRegister(varReg)
    val varReg2 = RM.peekVariableRegister()

    // Once we are on the second element it will be at an offset that we must retrieve.
    var finalStore = new Store(None, None, instructionSet.getReturn, varReg2)
    // Where 4 is the pair size offset and this refers to the instruction where we store the
    // variable register in the return one.
    if (isSnd) finalStore =
      new Store(None, None, instructionSet.getReturn, varReg2, new Immediate(pairSizeOffset))

    exprInstructions ++ coreInstructions :+ finalStore
  }

  def generatePairElem(pairElem: PairElemNode): IndexedSeq[Instruction] = {
    IndexedSeq[Instruction]()
  }

  def generateCall(call: CallNode): IndexedSeq[Instruction] = {
    // Must store every argument on the stack, negative intervals, backwards.
    var argInstructions = IndexedSeq[Instruction]()
    var totalArgOffset: Int = 0
    // First check if there are arguments in the arglist.
    if (call.argList.isDefined)
      argInstructions = call.argList.get.exprNodes.flatMap(e =>
      { val exprSize = getSize(e.getType(topSymbolTable, currentSymbolTable))
        totalArgOffset += exprSize
        generateExpression(e) :+
        // May need to distinguish between STR and STRB.
        // Register write back should be allowed, hence the true.
          new Store(None, None, RM.peekVariableRegister(), instructionSet.getSP,
            new Immediate(-exprSize), true)
      })

    var labelAndBranch = IndexedSeq[Instruction](
      BranchLink(None, Label(s"f_${call.identNode.ident}"))
    )

    // May need to do this multiple times if stack exceeds 1024 (max stack size).
    labelAndBranch = labelAndBranch :+ Add(None, conditionFlag = false, instructionSet.getSP,
                                           instructionSet.getSP, new Immediate(totalArgOffset))

    val finalMove = IndexedSeq[Instruction](
      Move(None, RM.peekVariableRegister(), new ShiftedRegister(instructionSet.getReturn))
    )

    argInstructions ++ labelAndBranch ++ finalMove
  }

  def generateRead(lhs: AssignLHSNode): IndexedSeq[Instruction] = {
    var generatedReadInstructions = IndexedSeq[Instruction]()

    // Peek for now doesn't seem like I would need to pop the register.
    val varReg1 = RM.peekVariableRegister()

    val addInstruction: IndexedSeq[Instruction] = lhs match {
      // Temporary hardcode for ident replace 4 with offset from symbol table.
      case ident: IdentNode => IndexedSeq[Instruction](Add(None, conditionFlag = false, varReg1,
        instructionSet.getSP, new Immediate(getSize(ident.getType(topSymbolTable, currentSymbolTable)))))
      // No offset if not reading variable.
      case _ => IndexedSeq[Instruction](Add(None, conditionFlag = false, varReg1,
        instructionSet.getSP, new Immediate(0)))
    }

    generatedReadInstructions = addInstruction :+
      Move(None, instructionSet.getReturn, new ShiftedRegister(varReg1))

    // Now must generate BL depending on the type, however only for character and int as
    // they are the only types that can be read.

    val lhsType = lhs.getType(topSymbolTable, currentSymbolTable)

    lhsType match {
      case scalar if scalar == IntTypeNode(null).getType(topSymbolTable, currentSymbolTable)
        // Generate top level msg with " %c\\0" and with "p_read_int"
        => generatedReadInstructions :+ BranchLink(None, Label("p_read_int"))
      case scalar if scalar == CharTypeNode(null).getType(topSymbolTable, currentSymbolTable)
        // Generate top level msg with " %c\\0" and with "p_read_char"
      => generatedReadInstructions :+ BranchLink(None, Label("p_read_char"))
      case _ => assert(assertion = false, "Undefined type for read.")
    }

    generatedReadInstructions
  }

  def generateFree(expr: ExprNode): IndexedSeq[Instruction] = {
    generateExpression(expr) ++ IndexedSeq[Instruction](BranchLink(None, Label("p_free_pair")))
  }

  def generateReturn(expr: ExprNode): IndexedSeq[Instruction] = {
    IndexedSeq[Instruction](
      Move(None, instructionSet.getReturn, new ShiftedRegister(RM.peekVariableRegister())),
      popPC)
  }

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

  def generatePrint(expr: ExprNode): IndexedSeq[Instruction] = {
    // Need to distinguish between print branch links.

    generateExpression(expr)
  }

  def generateIf(ifNode: IfNode): IndexedSeq[Instruction] = {
    // Instructions generated for condition expression.
    val condInstructions = generateExpression(ifNode.conditionExpr) :+
      Compare(None, RM.peekVariableRegister(), new Immediate(0)) :+
      Branch(Some(Equal), Label(s"L$n_branches"))

    n_branches += 1

    // Enter Scope
    val allocateInstruction: Instruction = enterScopeAndAllocateStack()
    // First if block
    currentSymbolTable = symbolTableManager.nextScope()

    val thenInstructions = generateStatement(ifNode.thenStat) :+
      Branch(None, Label(s"L$n_branches"))

    n_branches += 1

    // Second if block
    currentSymbolTable = symbolTableManager.nextScope()

    val elseInstructions = generateStatement(ifNode.elseStat)

    // Leave Scope
    val deallocateInstruction: Instruction = leaveScopeAndDeallocateStack()

    allocateInstruction +: (condInstructions ++ thenInstructions ++ elseInstructions :+ deallocateInstruction)
  }

  def generateWhile(whileNode: WhileNode): IndexedSeq[Instruction] = {
    var instructions: IndexedSeq[Instruction] = IndexedSeq[Instruction]()


    // Enter Scope
    val allocateInstruction: Instruction = enterScopeAndAllocateStack()

    // Update Scope to while block
    currentSymbolTable = symbolTableManager.nextScope()
    // TODO generate instructions

    // Leave Scope
    val deallocateInstruction: Instruction = leaveScopeAndDeallocateStack()

    allocateInstruction +: instructions :+ deallocateInstruction
  }

  def generateBegin(begin: BeginNode): IndexedSeq[Instruction] = {
    // We must first enter the new scope, then generate the statements inside the scope,
    // then finally close the scope.
//    symbolTableManager.enterScope()

    val generatedInstructions = generateStatement(begin.stat)

//    symbolTableManager.leaveScope()

    generatedInstructions
  }

  def generateExpression(expr: ExprNode): IndexedSeq[Instruction] = {
    expr match {
      case Int_literNode(_, str)
                  => IndexedSeq[Instruction](new Load(None, None,
                     RM.peekVariableRegister(), new LoadableExpression(str.toInt)))
      case Bool_literNode(_, bool)
                  => IndexedSeq[Instruction](Move(None, RM.peekVariableRegister(),
                     new Immediate(if (bool) 1 else 0)))
      case Char_literNode(_, char)
        // This was using next not sure it should be so I changed it to peek.
                  => IndexedSeq[Instruction](Move(None, RM.peekVariableRegister(),
                     new Immediate(char)))
      case Str_literNode(_, str)
                  => IndexedSeq[Instruction](new Load(None, None,
                     RM.peekVariableRegister(), Label(str)))
      // May replace with zeroReturn.
      case Pair_literNode(_)
                  => IndexedSeq[Instruction](new Load(None, None,
                     RM.peekVariableRegister(), new LoadableExpression(0)))
      case ident: IdentNode
      // Get offset from symbol table for the ident and replace it in the immediate.
                  => IndexedSeq[Instruction](new Load(None, None,
                     RM.peekVariableRegister(),
                     new LoadableExpression(getSize(
                         ident.getType(topSymbolTable, currentSymbolTable)))))
      case arrayElem: ArrayElemNode => generateArrayElem(arrayElem)
      case unaryOperation: UnaryOperationNode => generateUnary(unaryOperation)
      case binaryOperation: BinaryOperationNode => generateBinary(binaryOperation)
    }
  }

  def generateUnary(unaryOperation: UnaryOperationNode): IndexedSeq[Instruction] = {
    unaryOperation match {
      // More must be done for these according to the reference compiler.
      case LogicalNotNode(_, expr) =>
        generateExpression(expr) ++ IndexedSeq[Instruction](
          ExclusiveOr(None, conditionFlag = false, RM.peekVariableRegister(),
                      RM.peekVariableRegister(), new Immediate(1)))

      // Negate is not currently adding sign to the immediate to be loaded.
      case NegateNode(_, expr) =>
        generateExpression(expr) ++
        IndexedSeq[Instruction](
          RSBS(None, conditionFlag = false, RM.peekVariableRegister(),
               RM.peekVariableRegister(), new Immediate(0)),
          BranchLink(Some(Overflow), Label("p_throw_overflow_error")))
      case LenNode(_, expr) =>
        val varReg = RM.peekVariableRegister()
        generateExpression(expr) ++ IndexedSeq[Instruction](
          new Load(None, None, varReg, varReg)
        )
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
        generateExpression(argOne) ++ generateExpression(argTwo) ++
        IndexedSeq[Instruction](SMull(None, conditionFlag = false, varReg1,
                                      varReg2, varReg1, varReg2))
      case DivideNode(_, argOne, argTwo) =>
        generateExpression(argOne) ++ generateExpression(argTwo) ++
        IndexedSeq[Instruction](BranchLink(None, Label("p_check_divide_by_zero")),
                                BranchLink(None, Label("__aeabi_idiv")))
      case ModNode(_, argOne, argTwo) =>
        generateExpression(argOne) ++ generateExpression(argTwo) ++
        IndexedSeq[Instruction](BranchLink(None, Label("p_check_divide_by_zero")),
                                BranchLink(None, Label("__aeabi_idiv")))
      case PlusNode(_, argOne, argTwo) =>
        generateExpression(argOne) ++ generateExpression(argTwo) ++
        IndexedSeq[Instruction](Add(None, conditionFlag = false, varReg1,
                                    varReg1, new ShiftedRegister(varReg2)))
      case MinusNode(_, argOne, argTwo) =>
        generateExpression(argOne) ++ generateExpression(argTwo) ++
        IndexedSeq[Instruction](Subtract(None, conditionFlag = false, varReg1,
                                         varReg1, new ShiftedRegister(varReg2)))

      case GreaterThanNode(_, argOne, argTwo) =>
        generateExpression(argOne) ++ generateExpression(argTwo) ++
        IndexedSeq[Instruction](
          Compare(None, varReg1, new ShiftedRegister(varReg2)),
          Move(Some(GreaterThan), varReg1, new Immediate(1)),
          Move(Some(LessEqual), varReg1, new Immediate(0)))
      case GreaterEqualNode(_, argOne, argTwo) =>
        generateExpression(argOne) ++ generateExpression(argTwo) ++
        IndexedSeq[Instruction](
          Compare(None, varReg1, new ShiftedRegister(varReg2)),
          Move(Some(GreaterEqual), varReg1, new Immediate(1)),
          Move(Some(LessThan), varReg1, new Immediate(0)))
      case LessThanNode(_, argOne, argTwo) =>
        generateExpression(argOne) ++ generateExpression(argTwo) ++
        IndexedSeq[Instruction](
          Compare(None, varReg1, new ShiftedRegister(varReg2)),
          Move(Some(LessThan), varReg1, new Immediate(1)),
          Move(Some(GreaterEqual), varReg1, new Immediate(0)))
      case LessEqualNode(_, argOne, argTwo) =>
        generateExpression(argOne) ++ generateExpression(argTwo) ++
        IndexedSeq[Instruction](
          Compare(None, varReg1, new ShiftedRegister(varReg2)),
          Move(Some(LessEqual), varReg1, new Immediate(1)),
          Move(Some(GreaterThan), varReg1, new Immediate(0)))
      case EqualToNode(_, argOne, argTwo) =>
        generateExpression(argOne) ++ generateExpression(argTwo) ++
        IndexedSeq[Instruction](
          Compare(None, varReg1, new ShiftedRegister(varReg2)),
          Move(Some(Equal), varReg1, new Immediate(1)),
          Move(Some(NotEqual), varReg1, new Immediate(0)))
      case NotEqualNode(_, argOne, argTwo) =>
        generateExpression(argOne) ++ generateExpression(argTwo) ++
        IndexedSeq[Instruction](
          Compare(None, varReg1, new ShiftedRegister(varReg2)),
          Move(Some(NotEqual), varReg1, new Immediate(1)),
          Move(Some(Equal), varReg1, new Immediate(0)))

      case LogicalAndNode(_, argOne, argTwo) =>
        generateExpression(argOne) ++ generateExpression(argTwo) ++
        IndexedSeq[Instruction](And(None, conditionFlag = false, varReg1,
                                    varReg1, new ShiftedRegister(varReg2)))
      case LogicalOrNode(_, argOne, argTwo) =>
        generateExpression(argOne) ++ generateExpression(argTwo) ++
        IndexedSeq[Instruction](Or(None, conditionFlag = false, varReg1,
                                   varReg1, new ShiftedRegister(varReg2)))
    }
  }

  def getSize(_type: TYPE): Int = {
    _type match {
      case _: ARRAY => 4
      case _: PAIR => 4
      case scalar: SCALAR =>
        if (scalar == IntTypeNode(null).getType(topSymbolTable, currentSymbolTable)) 4
        else if (scalar == BoolTypeNode(null).getType(topSymbolTable, currentSymbolTable)) 1
        else if (scalar == CharTypeNode(null).getType(topSymbolTable, currentSymbolTable)) 1
        else {
          assert(assertion = false, "Size undefined for scalar")
          -1
        }
      case STRING => 4
      case _ =>
        assert(assertion = false, s"Size for type is undefined")
        -1
    }
  }

  def getScopeStackSize(symbolTable: SymbolTable): Int = {
    symbolTable.map.values.map(getSTStackSize).sum
  }

  def getSTStackSize(identifier: IDENTIFIER): Int = {
    identifier match {
      case _: PARAM => 0
      case value: TYPE => getSize(value)
      case variable: VARIABLE => getSize(variable._type)
      case _ =>
        assert(assertion = false, "ST should not have non type or param identifiers")
        -1
    }
  }

  def enterScopeAndAllocateStack(): Instruction = {
    symbolTableManager.enterScope()
    Subtract(None, conditionFlag = false,
      instructionSet.getSP, instructionSet.getSP, new Immediate(getScopeStackSize(currentSymbolTable)))
  }

  def leaveScopeAndDeallocateStack(): Instruction = {
    symbolTableManager.enterScope()
    Add(None, conditionFlag = false,
      instructionSet.getSP, instructionSet.getSP, new Immediate(getScopeStackSize(currentSymbolTable)))
  }

  case class SymbolTableManager(private val topLevelTable: SymbolTable) {
    // Scope information
    private var currentScopeParent: SymbolTable = topLevelTable
    private var currentScopeIndex: Int = -1
    private var indexStack: List[Int] = List[Int]()
    private var currentScope: SymbolTable = _

    // Variable offset information
    private var currentOffset: Int = -1
    // Returns the next scope under the current scope level
    def nextScope(): SymbolTable = {
      // Check you can go to next scope
      assert(currentScopeIndex + 1 < currentScopeParent.children.length, s"Cannot go to next scope.")
      // Increment Scope
      currentScopeIndex += 1
      // Update current scope
      currentScope = currentScopeParent.children.apply(currentScopeIndex)
      // Update currentOffset
      currentOffset = getScopeStackSize(currentScope)
      currentScope
    }

    private def idIsVariableOrType(id: IDENTIFIER): Boolean = id.isInstanceOf[VARIABLE] || id.isInstanceOf[TYPE]

    def getNextOffset(key: String): Int = {
      val currentIdOption = currentScope.lookup(key)
      assert (currentIdOption.isDefined, "key must be defined in the scope")
      assert (idIsVariableOrType(currentIdOption.get), "key ID must be a variable or type")
      val offsetSize: Int = currentIdOption.get match {
        case value: TYPE => getSize(value)
        case variable: VARIABLE => getSize(variable._type)
        case _ =>
          assert(assertion = false, "key must refer to a type or variable")
          -1
      }
      currentOffset -= offsetSize
      currentOffset
    }

    // Enters the current scope
    def enterScope(): Unit = {
      assert(currentScopeParent.children.isDefinedAt(currentScopeIndex))
      currentScopeParent = currentScopeParent.children.apply(currentScopeIndex)
      // Push
      indexStack = currentScopeIndex :: indexStack
      currentScopeIndex = -1
    }
    // Leaves the current scope
    def leaveScope(): Unit = {
      assert(indexStack.nonEmpty, "Scope is at the top level already")
      currentScopeParent = currentScopeParent.encSymbolTable
      // Pop
      currentScopeIndex = indexStack.head
      indexStack = indexStack.tail
    }
  }

  case class LabelGenerator() {
    private var labelNum = 0
    def generate(): Label = {
      val label = Label(s"L$labelNum")
      labelNum += 1
      label
    }
  }
}



