package asm

import asm.instructions._
import asm.instructionset._
import asm.registers._
import asm.utilities._
import ast.nodes._
import ast.symboltable._

import scala.collection.mutable

object CodeGenerator {

  var symbolTableManager: SymbolTableManager = _
  var instructionSet: InstructionSet = _
  var RM: RegisterManager = _
  var topSymbolTable: SymbolTable = _
  var currentSymbolTable: SymbolTable = _
  var bytesAllocatedSoFar: Int = 0

  // Keep track of number of branches.
  val labelGenerator: LabelGenerator = LabelGenerator()

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

    // Enter the top symbol table
    symbolTableManager.enterScope()

    // Generated code for functions
    val functionInstructions: IndexedSeq[Instruction] = program.functions.flatMap(generateFunction)

    // Update the current symbol table for main method
    currentSymbolTable = symbolTableManager.nextScope()

    // Enter Scope
    val allocateInstructions = enterScopeAndAllocateStack()

    // Main directive and pushing LR
    val mainHeaderInstructions: IndexedSeq[Instruction] = IndexedSeq[Instruction](Label("main"), pushLR)

    // Generated code for stats
    val statInstructions: IndexedSeq[Instruction] = generateStatement(program.stat)

    // Leave Scope
    val deallocateInstructions: Seq[Instruction] = leaveScopeAndDeallocateStack()

    val mainEndInstructions = IndexedSeq(zeroReturn, popPC, new EndFunction)

    // Leave top symbol table
    symbolTableManager.leaveScope()

    // Total Main Instructions
    val mainInstructions = mainHeaderInstructions ++ allocateInstructions ++
      statInstructions ++ deallocateInstructions ++ mainEndInstructions

    // Program Instructions = Function Instructions + Main Instructions
    functionInstructions ++ mainInstructions
  }

  def generateFunction(func: FuncNode): IndexedSeq[Instruction] = {

    // Update the current symbol table to function block
    currentSymbolTable = symbolTableManager.nextScope()

    // Enter function scope
    val allocateInstructions = enterScopeAndAllocateStack()

    var labelPushLR = IndexedSeq[Instruction](Label(s"f_${func.identNode.ident}"), pushLR)
    if (func.paramList.isDefined)
      // May need to fetch parameters in reverse.
      labelPushLR ++= func.paramList.get.paramList.flatMap(generateParam)
    // Otherwise nothing?

    // Generate instructions for statement.
    val statInstructions = generateStatement(func.stat)

    // Leave function scope
    val deallocateInstructions = leaveScopeAndDeallocateStack()

    var popEndInstruction = IndexedSeq[Instruction](popPC, new EndFunction)

    labelPushLR ++ allocateInstructions ++ statInstructions ++ deallocateInstructions ++ popEndInstruction
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
      case PrintNode(_, expr) => generatePrint(expr, printLn = false)
      case PrintlnNode(_, expr) => generatePrint(expr, printLn = true)

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
      case ident: IdentNode
        // Need to pass offset here too.
        => IndexedSeq[Instruction](new Store(None, None, RM.peekVariableRegister(), instructionSet.getSP))
      case arrayElem: ArrayElemNode => generateArrayElemLHS(arrayElem)
      case pairElem: PairElemNode => generatePairElemLHS(pairElem)
    }
  }

  def generateIdent(ident: IdentNode): IndexedSeq[Instruction] = {
    // Retrieve actual size for ident from symbol table.
//    val identSize = getSize(ident.getType(topSymbolTable, currentSymbolTable))

    // ASMType to classify if ident is of type bool and if so should be loaded
    // with ByteType triggering STRB instead of the usual STR.
    var asmType: Option[ASMType] = None

    if (ident.getType(topSymbolTable, currentSymbolTable)
              == BoolTypeNode(null).getType(topSymbolTable, currentSymbolTable))
      asmType = Some(ByteType)

    IndexedSeq[Instruction](new Store(None, asmType,
      RM.peekVariableRegister(), instructionSet.getSP,
      new Immediate(symbolTableManager.getNextOffset(ident.getKey))))
  }

  // THIS, COMES FROM EXPR
  def generateArrayElem(arrayElem: ArrayElemNode): IndexedSeq[Instruction] = {
    val varReg = RM.nextVariableRegister()

    // Must now retrieve elements in array corresponding to each
    val arrayElemInstructions = retrieveArrayElements(arrayElem, varReg)

    val loadRes: IndexedSeq[Instruction] = IndexedSeq[Instruction](new Load(None, None, varReg, varReg))

    RM.freeVariableRegister(varReg)

    arrayElemInstructions ++ loadRes
  }

  // THIS COMES FROM ASSIGNLHS
  def generateArrayElemLHS(arrayElem: ArrayElemNode): IndexedSeq[Instruction] = {
    val varReg1 = RM.nextVariableRegister()
    val varReg2 = RM.nextVariableRegister()

    // Must now retrieve elements in array corresponding to each
    val arrayElemInstructions = retrieveArrayElements(arrayElem, varReg2)

    var asmType: Option[ASMType] = None

    // Check if B is necessary for load, store etc.
    // May need to be ByteType
    if (checkSingleByte(arrayElem)) asmType = Some(SignedByte)

    val storeResult = IndexedSeq[Instruction](
      new Store(None, asmType, varReg1, varReg2)
    )

    // Since we are done with variable registers above we can free them.
    RM.freeVariableRegister(varReg2)
    RM.freeVariableRegister(varReg1)

    // Return generated instructions.
    arrayElemInstructions ++ storeResult
  }

  def retrieveArrayElements(arrayElem: ArrayElemNode, varReg: Register): IndexedSeq[Instruction] = {
    val preExpr = IndexedSeq[Instruction](
      Add(None, conditionFlag = false, varReg, instructionSet.getSP,
          new Immediate(getSize(arrayElem.identNode.getType(topSymbolTable, currentSymbolTable))))
    )

    // Produce following instructions for every expression in the array.
    val exprInstructions: IndexedSeq[Instruction] = {
      arrayElem.exprNodes.flatMap(e => generateExpression(e) ++
        IndexedSeq[Instruction](
          new Load(None, None, varReg, varReg),
          Move(None, instructionSet.getReturn, new ShiftedRegister(RM.peekVariableRegister())),
          Move(None, instructionSet.getArgumentRegisters(1), new ShiftedRegister(varReg)),
          // TODO: Add label for p_check_array_bounds
          BranchLink(None, Label("p_check_array_bounds")),
          Add(None, conditionFlag = false, varReg, varReg, new Immediate(4)),
          Add(None, conditionFlag = false, varReg, varReg, new ShiftedRegister(RM.peekVariableRegister(), "LSL", 2))
        )
      )
    }

    // Return generated instructions.
    preExpr ++ exprInstructions
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
    // Should be added to stack size for subbing.
    val pairElemSize = getSize(newPair.fstElem.getType(topSymbolTable, currentSymbolTable)) +
                       getSize(newPair.sndElem.getType(topSymbolTable, currentSymbolTable))
    // Pair size
    val pairSize = 2 * getSize(newPair.getType(topSymbolTable, currentSymbolTable))

    // Generate instructions for the new pair.
    val preExprInstructions: IndexedSeq[Instruction] = IndexedSeq[Instruction](
      new Load(None, None, instructionSet.getReturn, new LoadableExpression(pairSize)),
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
    if (checkSingleByte(expr)) {
      coreInstructions = coreInstructions :+ new Store(None, Some(ByteType), RM.peekVariableRegister(), instructionSet.getReturn)
    } else {
      coreInstructions = coreInstructions :+ new Store(None, None, RM.peekVariableRegister(), instructionSet.getReturn)
    }

    RM.freeVariableRegister(varReg)
    val varReg2 = RM.peekVariableRegister()

    // Once we are on the second element it will be at an offset that we must retrieve.
    val finalStore = new Store(None, None, instructionSet.getReturn, varReg2, new Immediate(pairSizeOffset))

    exprInstructions ++ coreInstructions :+ finalStore
  }

  def generatePairElemLHS(pairElem: PairElemNode): IndexedSeq[Instruction] = {
    // Register than had rhs evaluated instructions.
    val varReg = RM.nextVariableRegister()
    val peekedReg = RM.peekVariableRegister()

    // TODO: Check if loadOffset below can be replaced with:
    // TODO: val offset: Int = pairElem match {
    // TODO:  case fst: FstNode => generateExpression(fst.expr)
    // TODO:  case snd: SndNode => generateExpression(snd.expr) }

    val loadOffset = IndexedSeq[Instruction](
      // Current offset of identifier related to pair.
      new Load(None, None, peekedReg, instructionSet.getSP,
               // TODO: ANOTHER CHANGE FOR GETOFFSET HERE
               new Immediate(symbolTableManager.getOffset(pairElem.getKey)))
    )

    var asmType: Option[ASMType] = None

    // Check if B is necessary for load, store etc.
    // May need to be ByteType
    if (checkSingleByte(pairElem)) asmType = Some(ByteType)

    // TODO: Add Null pointer check here.
    val nullPtrIns = genNullPointerInstructions

    val offset: Int = pairElem match {
      case fst: FstNode => 0
      case snd: SndNode => 4
    }

    val loadStore = IndexedSeq[Instruction](
      new Load(None, None, peekedReg, peekedReg, new Immediate(offset)),
      new Store(None, asmType, varReg, peekedReg)
    )
    // Free register now.
    RM.freeVariableRegister(varReg)

    loadOffset ++ nullPtrIns ++ loadStore
  }

  def generatePairElem(pairElem: PairElemNode): IndexedSeq[Instruction] = {
    pairElem match {
      case fst: FstNode => generateExpression(fst.expression) ++ generatePEHelper(fst, isSnd = false)
      case snd: SndNode => generateExpression(snd.expression) ++ generatePEHelper(snd, isSnd = true)
    }
  }

  def generatePEHelper(pairElemNode: PairElemNode, isSnd: Boolean): IndexedSeq[Instruction] = {
    val peInstructions = genNullPointerInstructions

    var asmType: Option[ASMType] = None

    // Check if B is necessary for load, store etc.
    // May need to be ByteType
    if (checkSingleByte(pairElemNode)) asmType = Some(SignedByte)

    var loads = IndexedSeq[Instruction](
      new Load(None, None, RM.peekVariableRegister(), RM.peekVariableRegister()),
      new Load(None, asmType, RM.peekVariableRegister(), RM.peekVariableRegister())
    )

    if (isSnd)
      loads = IndexedSeq[Instruction](
        new Load(None, None, RM.peekVariableRegister(), RM.peekVariableRegister(),
          new Immediate(getSize(pairElemNode.getType(topSymbolTable, currentSymbolTable))), registerWriteBack = false),
        new Load(None, asmType, RM.peekVariableRegister(), RM.peekVariableRegister())
      )

    // Should set a flag that triggers checkNullPointer at top level.
    peInstructions ++ loads
  }

  def genNullPointerInstructions: IndexedSeq[Instruction] = {
    IndexedSeq[Instruction](
      Move(None, instructionSet.getReturn, new ShiftedRegister(RM.peekVariableRegister())),
      // Create label here and trigger the checkNullPointer at the bottom.
      BranchLink(None, Label("p_check_null_pointer")))
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
      // Offset from symbol table for ident.
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
    // Need to generate p_free_pair here.
    generateExpression(expr) ++ IndexedSeq[Instruction](
      Move(None, instructionSet.getReturn, new ShiftedRegister(RM.peekVariableRegister())),
      // TODO: Call printFreePair or something
      BranchLink(None, Label("p_free_pair")))
  }

  def generateReturn(expr: ExprNode): IndexedSeq[Instruction] = {
    if (bytesAllocatedSoFar == 0)
      IndexedSeq[Instruction](
        Move(None, instructionSet.getReturn, new ShiftedRegister(RM.peekVariableRegister())),
        popPC)
    else
      IndexedSeq[Instruction](
        Move(None, instructionSet.getReturn, new ShiftedRegister(RM.peekVariableRegister())),
        Add(None, conditionFlag = false, instructionSet.getSP,
          instructionSet.getSP, new Immediate(bytesAllocatedSoFar)),
        popPC)
  }

  def generateExit(expr: ExprNode): IndexedSeq[Instruction] = {
    // Must generate the instructions necessary for the exit code,
    // then branch to exit.
    // Need next available register to move into r0, temporary fix below.
    val regUsedByGenExp: Register = RM.peekVariableRegister()
    // So that it can actually be used by generateExpression.
    RM.freeVariableRegister(regUsedByGenExp)

    var int = 0
    val intLoad: IndexedSeq[Instruction] = expr match {
      // Check if the expression is negate node, i.e. int to be exited with is negative.
      case NegateNode(_, intExpr) =>
        intExpr match {
          // Check if negate node expression is an int.
          case Int_literNode(_, str) => int = 0 - str.toInt
            IndexedSeq[Instruction](new Load(None, None,
            RM.peekVariableRegister(), new LoadableExpression(int)))
        }
      case _ => generateExpression(expr)
    }

    intLoad ++ IndexedSeq[Instruction](
      Move(None, instructionSet.getReturn, new ShiftedRegister(regUsedByGenExp)),
      BranchLink(None, Label("exit")))
  }

  // TODO: replace
  def generatePrint(expr: ExprNode, printLn: Boolean): IndexedSeq[Instruction] = {
    // Generate instruction then add necessary move.
    val preLabelInstructions = generateExpression(expr) ++ IndexedSeq[Instruction](
      Move(None, instructionSet.getReturn, new ShiftedRegister(RM.peekVariableRegister()))
    )

    // TODO: Check if can be replaced with matching on Int_liternode... etc.
    val printBranchType: IndexedSeq[Instruction]
    = expr.getType(topSymbolTable, currentSymbolTable) match {
      case scalar: SCALAR =>
        if (scalar == IntTypeNode(null).getType(topSymbolTable, currentSymbolTable)) {
          IndexedSeq[Instruction](
            // TODO: Call printInt or something
            BranchLink(None, Label("p_print_int"))
          )
        } else if (scalar == BoolTypeNode(null).getType(topSymbolTable, currentSymbolTable)) {
          IndexedSeq[Instruction](
            // TODO: Call printBool or something
            BranchLink(None, Label("p_print_bool"))
          )
        }
        else if (scalar == CharTypeNode(null).getType(topSymbolTable, currentSymbolTable)) {
          IndexedSeq[Instruction](
            // TODO: Call printChar or something
            BranchLink(None, Label("p_print_char"))
          )
        }
        // Return empty list, or assert error or something.
        // TODO: Check this
        else IndexedSeq[Instruction]()
      case STRING =>
        IndexedSeq[Instruction](
          // TODO: Call printString or something
          BranchLink(None, Label("p_print_string"))
        )
      // Do same thing for arrays and pair prints.
      case _:ARRAY | _:PAIR =>
        IndexedSeq[Instruction](
          // TODO: Call something that generates relevant label.
          BranchLink(None, Label("p_print_reference"))
        )
    }

    // Extra printLn label necessary for printLn obviously.
    var printLineBranch = IndexedSeq[Instruction]()
    if (printLn) printLineBranch = IndexedSeq[Instruction](BranchLink(None, Label("p_print_ln")))

    preLabelInstructions ++ printBranchType ++ printLineBranch
  }

  def generateIf(ifNode: IfNode): IndexedSeq[Instruction] = {
    // Instructions generated for condition expression.
    val elseLabel: Label = labelGenerator.generate()
    val fiLabel: Label = labelGenerator.generate()

    // Condition
    val condInstructions: IndexedSeq[Instruction] = generateExpression(ifNode.conditionExpr) :+
      Compare(None, RM.peekVariableRegister(), new Immediate(0))

    // elseBranch
    val elseBranchInstructions: IndexedSeq[Instruction] = IndexedSeq(Branch(Some(Equal), elseLabel))

    // *** THEN ***

    // Then Scope
    currentSymbolTable = symbolTableManager.nextScope()

    // Enter Then Scope
    val allocateThenInstruction: IndexedSeq[Instruction] = enterScopeAndAllocateStack()

    // Then
    val thenInstructions = generateStatement(ifNode.thenStat)

    // Leave Then Scope
    val deallocateThenInstruction: IndexedSeq[Instruction] = leaveScopeAndDeallocateStack()

    // ***********

    // fiBranch
    val fiBranchInstructions: IndexedSeq[Instruction] = IndexedSeq(Branch(None, fiLabel))

    // *** ELSE ***

    // Second if block
    currentSymbolTable = symbolTableManager.nextScope()

    // Enter Else Scope
    val allocateElseInstruction: IndexedSeq[Instruction] = enterScopeAndAllocateStack()

    // Else
    val elseInstructions = elseLabel +: generateStatement(ifNode.elseStat)

    // Leave Scope
    val deallocateElseInstruction: IndexedSeq[Instruction] = leaveScopeAndDeallocateStack()

    // ***********

    // *** SUMMARY ***

    val totalThenInstructions = allocateThenInstruction ++ thenInstructions ++ deallocateThenInstruction

    val totalElseInstructions = allocateElseInstruction ++ elseInstructions ++ deallocateElseInstruction

    condInstructions ++ elseBranchInstructions ++ totalThenInstructions ++
      fiBranchInstructions ++ totalElseInstructions :+ fiLabel
  }

  def generateWhile(whileNode: WhileNode): IndexedSeq[Instruction] = {
    // Labels
    val conditionLabel: Label = labelGenerator.generate()
    val bodyLabel: Label = labelGenerator.generate()


    // *** CONDITION ***

    // Initial condition check
    val initConditionBranch: Instruction = Branch(None, conditionLabel)

    // Condition
    val condInstructions: IndexedSeq[Instruction] = generateExpression(whileNode.expr) :+
      Compare(None, RM.peekVariableRegister(), new Immediate(1))

    // Branch to start of body
    val bodyBranch: Instruction = Branch(Some(Equal), bodyLabel)

    // ************


    // *** BODY ***

    // Update Scope to While Body
    currentSymbolTable = symbolTableManager.nextScope()

    // Enter Scope
    val allocateWhileBody: IndexedSeq[Instruction] = enterScopeAndAllocateStack()

    // Body Instruction list
    val bodyInstructions: IndexedSeq[Instruction] = generateStatement(whileNode.stat)

    // Leave Scope
    val deallocateWhileBody: IndexedSeq[Instruction] = leaveScopeAndDeallocateStack()

    // ************


    // *** SUMMARY ***

    val totalBodyInstructions: IndexedSeq[Instruction] = bodyLabel +: (allocateWhileBody ++ bodyInstructions ++ deallocateWhileBody)

    val totalConditionInstructions = (conditionLabel +: condInstructions :+ bodyBranch)

    initConditionBranch +: (totalBodyInstructions ++ totalConditionInstructions)
  }

  def generateBegin(begin: BeginNode): IndexedSeq[Instruction] = {
    // We must first enter the new scope, then generate the statements inside the scope,
    // then finally close the scope.

    // TODO NO TOUCHING SCOPES WITHOUT ASKING DANIEL
    val generatedInstructions = generateStatement(begin.stat)

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
      // All that is necessary for Pair_liter expression generation.
      case Pair_literNode(_)
                  => IndexedSeq[Instruction](new Load(None, None,
                     RM.peekVariableRegister(), new LoadableExpression(0)))
      case ident: IdentNode
      // Load identifier into first available variable register.
        // TODO: Daniel Check as loading immediate instead of loadable expression.
                  => if (checkSingleByte(ident)) {
                      IndexedSeq[Instruction](new Load(None, Some(ByteType),
                        RM.peekVariableRegister(), instructionSet.getSP,
                        new Immediate(getSize(
                          ident.getType(topSymbolTable, currentSymbolTable)))))
                  } else {
                      IndexedSeq[Instruction](new Load(None, None,
                        RM.peekVariableRegister(), instructionSet.getSP,
                        // TODO: HERE IS THE CHANGE FOR SYMBOLTABLE STACK
                        new Immediate(symbolTableManager.getOffset(ident.getKey))))}
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

      // Negate according to reference compiler.
      case NegateNode(_, expr) =>
        generateExpression(expr) ++
        IndexedSeq[Instruction](
          RSBS(None, conditionFlag = false, RM.peekVariableRegister(),
               RM.peekVariableRegister(), new Immediate(0)),
        ) ++ Utilities.printOverflowError
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
    val r1 = instructionSet.getArgumentRegisters(1)

    binaryOperation match {
      case MultiplyNode(_, argOne, argTwo) =>
        generateExpression(argOne) ++ generateExpression(argTwo) ++
        IndexedSeq[Instruction](
          SMull(None, conditionFlag = false, varReg1, varReg2, varReg1, varReg2),
          // Need to be shifted by 31, ASR 31
          Compare(None, varReg2, new ShiftedRegister(varReg1, "ASR", 31)),
          // TODO: Trigger p_throw_overflow_error
          BranchLink(Some(NotEqual), Label("p_throw_overflow_error")))
      case DivideNode(_, argOne, argTwo) =>
        generateExpression(argOne) ++ generateExpression(argTwo) ++
          // TODO: Call function to generate labels below.
          IndexedSeq[Instruction](
            Move(None, instructionSet.getReturn, new ShiftedRegister(varReg1)),
            Move(None, r1, new ShiftedRegister(varReg2)),
            BranchLink(None, Label("p_check_divide_by_zero")),
            BranchLink(None, Label("__aeabi_idiv")),
            Move(None, varReg1, new ShiftedRegister(instructionSet.getReturn)))
      case ModNode(_, argOne, argTwo) =>
        generateExpression(argOne) ++ generateExpression(argTwo) ++
        // TODO: Call function to generate labels below.
        IndexedSeq[Instruction](
          Move(None, instructionSet.getReturn, new ShiftedRegister(varReg1)),
          Move(None, r1, new ShiftedRegister(varReg2)),
          BranchLink(None, Label("p_check_divide_by_zero")),
          BranchLink(None, Label("__aeabi_idiv")),
          Move(None, varReg1, new ShiftedRegister(r1)))
      case PlusNode(_, argOne, argTwo) =>
        // Should be ADDS, conditionFlag set to true.
      generateExpression(argOne) ++ generateExpression(argTwo) ++
        IndexedSeq[Instruction](
          Add(None, conditionFlag = true, varReg1, varReg1, new ShiftedRegister(varReg2)),
          // TODO: Call function to generate overflow error label.
          BranchLink(Some(Overflow), Label("p_throw_overflow_error")))
      case MinusNode(_, argOne, argTwo) =>
        generateExpression(argOne) ++ generateExpression(argTwo) ++
        IndexedSeq[Instruction](
          Subtract(None, conditionFlag = true, varReg1, varReg1, new ShiftedRegister(varReg2)),
          // TODO: Call function to generate overflow error label.
          BranchLink(Some(Overflow), Label("p_throw_overflow_error")))

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

  def generateCommonFunction(func: CommonFunction): IndexedSeq[Instruction] = func match {
    case PrintLn => IndexedSeq[Instruction](
      pushLR,
      Add(condition=None, conditionFlag=false, dest=instructionSet.getReturn, src1=instructionSet.getReturn, src2=new Immediate(4)),
      BranchLink(condition=None, label=Puts.label),
      Move(condition=None, dest=instructionSet.getReturn, src=new Immediate(0)),
      BranchLink(condition=None, label=Flush.label),
      popPC
    )
    case PrintRuntimeError => IndexedSeq[Instruction](
      BranchLink(condition=None, PrintString.label),
      Move(condition=None, dest=instructionSet.getReturn, src=new Immediate(-1)),
      BranchLink(None, Exit.label)
    )
    case PrintString => IndexedSeq[Instruction](
      pushLR,
      new Load(condition=None, asmType=None, dest=instructionSet.getArgumentRegisters(1), src=instructionSet.getReturn),
      Add(condition=None, conditionFlag=false, dest=instructionSet.getArgumentRegisters(2), src1=instructionSet.getReturn, src2=new Immediate(4)),
      BranchLink(condition=None, label=Printf.label),
      Move(condition=None, dest=instructionSet.getReturn, src=new Immediate(0)),
      BranchLink(condition=None, label=Flush.label),
      popPC
    )
    case PrintFreePair => IndexedSeq[Instruction](
      pushLR,
      Compare(condition=None, operand1=instructionSet.getReturn, operand2=new Immediate(0)),
      new Load(condition=Some(Equal), asmType=None, dest=instructionSet.getReturn, loadable=Label("msg_free_pair")),
      Branch(condition=Some(Equal), label=PrintRuntimeError.label),
      Push(condition=None, List(instructionSet.getReturn)),
      new Load(condition=None, asmType=None, dest=instructionSet.getReturn, src=instructionSet.getReturn),
      BranchLink(condition=None, label=Free.label),
      new Load(condition=None, asmType=None, dest=instructionSet.getReturn, src=instructionSet.getSP),
      new Load(condition=None, asmType=None, dest=instructionSet.getReturn, src=instructionSet.getReturn, flexOffset=new Immediate(4)),
      BranchLink(condition=None, label=Free.label),
      Pop(condition=None, List(instructionSet.getReturn)),
      BranchLink(None, label=Free.label),
      popPC
    )
    case PrintReadChar => IndexedSeq[Instruction](
      pushLR,
      Move(condition=None, dest=instructionSet.getArgumentRegisters(1), src=new ShiftedRegister(instructionSet.getReturn)),
      new Load(condition=None, asmType=None, dest=instructionSet.getReturn, loadable=Label("msg_read_char")),
      new Add(condition=None, conditionFlag=false, dest=instructionSet.getReturn, src1=instructionSet.getReturn, src2=new Immediate(4)),
      BranchLink(condition=None, label=Scanf.label),
      popPC
    )
    case PrintCheckNullPointer => IndexedSeq[Instruction](
      pushLR,
      Compare(condition=None, operand1=instructionSet.getReturn, operand2=new Immediate(0)),
      new Load(condition=Some(Equal), asmType=None, dest=instructionSet.getReturn, loadable=Label("msg_check_null_pointer")),
      BranchLink(condition=Some(Equal), PrintRuntimeError.label),
      popPC
    )
    case PrintBool => IndexedSeq[Instruction](
      pushLR,
      Compare(condition=None, operand1=instructionSet.getReturn, operand2=new Immediate(0)),
      new Load(condition=Some(NotEqual), asmType=None, dest=instructionSet.getReturn, loadable=new Label("msg_print_bool_true")),
      new Load(condition=Some(Equal), asmType=None, dest=instructionSet.getReturn, loadable=new Label("msg_print_bool_false")),
      new Add(condition=None, conditionFlag=false, dest=instructionSet.getReturn, src1=instructionSet.getReturn, src2=new Immediate(4)),
      BranchLink(condition=None, label=Printf.label),
      Move(condition=None, dest=instructionSet.getReturn, src=new Immediate(0)),
      BranchLink(condition=None, label=Flush.label),
      popPC
    )
  }

  def checkSingleByte(expr: ExprNode): Boolean = {
    (expr.getType(topSymbolTable, currentSymbolTable)
      == BoolTypeNode(null).getType(topSymbolTable, currentSymbolTable)) ||
      (expr.getType(topSymbolTable, currentSymbolTable)
        == CharTypeNode(null).getType(topSymbolTable, currentSymbolTable))
  }

  def checkSingleByte(expr: PairElemNode): Boolean = {
    (expr.getType(topSymbolTable, currentSymbolTable)
      == BoolTypeNode(null).getType(topSymbolTable, currentSymbolTable)) ||
      (expr.getType(topSymbolTable, currentSymbolTable)
        == CharTypeNode(null).getType(topSymbolTable, currentSymbolTable))
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

  def enterScopeAndAllocateStack(): IndexedSeq[Instruction] = {
    symbolTableManager.enterScope()
    if (getScopeStackSize(currentSymbolTable) == 0) IndexedSeq()
    else {
      bytesAllocatedSoFar += getScopeStackSize(currentSymbolTable)
      IndexedSeq(Subtract(None, conditionFlag = false,
        instructionSet.getSP, instructionSet.getSP, new Immediate(getScopeStackSize(currentSymbolTable))))
    }
  }

  def leaveScopeAndDeallocateStack(): IndexedSeq[Instruction] = {
    currentSymbolTable = symbolTableManager.leaveScope()
    if (getScopeStackSize(currentSymbolTable) == 0) IndexedSeq()
    // If all the bytes allocated so far have been freed, a return must have already taken place
    else if (bytesAllocatedSoFar != 0) {
      bytesAllocatedSoFar -= getScopeStackSize(currentSymbolTable)
      IndexedSeq(Add(None, conditionFlag = false,
        instructionSet.getSP, instructionSet.getSP, new Immediate(getScopeStackSize(currentSymbolTable))))
    }
    else IndexedSeq()
  }

  case class SymbolTableManager(private val initScope: SymbolTable) {
    // Scope information
    private var currentScopeParent: SymbolTable = _
    private var currentScope: SymbolTable = initScope
    private var currentScopeIndex: Int = -1
    private var indexStack: List[Int] = List[Int]()
    private var scopeStack: List[SymbolTable] = List[SymbolTable]()

    // Variable offset information
    private var currentOffset: Int = -1
    // Map that maps offset to respective ident, is modified when getNextOffset is called.
    private var identOffsetMap: Map[String, Int] = Map[String, Int]()
    // Stack that keeps track of identMap, on ENTRY (NOT NEXT) to a new scope the current identOffsetMap is pushed to
    // the stack, an empty identOffsetMap replaces it. When LEAVING a scope the identOffsetMap is discarded and
    // the one for the next scope is popped.
    var identMapStack: mutable.Stack[Map[String, Int]] = mutable.Stack[Map[String, Int]]()

    // Returns the next scope under the current scope level
    def nextScope(): SymbolTable = {
      // Check you can go to next scope
      assert(currentScopeParent.children != null && currentScopeIndex + 1 < currentScopeParent.children.length, s"Cannot go to next scope.")
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
      val currentIdOption = currentScopeParent.lookup(key)
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
      // Add offset for IDENTIFIER to current scope map.
      identOffsetMap + (key -> currentOffset)
      currentOffset
    }

    // Returns current identifier offset
    def getOffset(key: String): Int = {
      // Retrieve offset for current identifier or return 0.
      identOffsetMap getOrElse(key, 0)
    }

    // Enters the current scope
    def enterScope(): Unit = {
      currentScopeParent = currentScope
      // Push
      indexStack = currentScopeIndex :: indexStack
      // Push identOffsetMap to identMapStack:
      identMapStack.push(identOffsetMap)
      // Reset identOffsetMap for next stack.
      identOffsetMap = Map[String, Int]()
      scopeStack = currentScope :: scopeStack
      currentScopeIndex = -1
      currentScope = null
    }

    // Leaves the current scope
    def leaveScope(): SymbolTable = {
      assert(indexStack.nonEmpty, "Scope is at the top level already")
      assert(scopeStack.nonEmpty, "Scope is at the top level already")
      currentScopeParent = currentScopeParent.encSymbolTable
      // Pop
      currentScopeIndex = indexStack.head
      // Pop identOffsetMap off identMapStack and set it to current identOffsetMap
      identOffsetMap = identMapStack.pop()
      indexStack = indexStack.tail
      currentScope = scopeStack.head
      scopeStack = scopeStack.tail
      currentScope
    }

    def returnToTopScope(): Unit = {
      while (scopeStack.size > 1)
        leaveScope()
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



