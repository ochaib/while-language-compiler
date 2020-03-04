package asm

import asm.instructions._
import asm.instructionset._
import asm.registers._
import asm.utilities._
import ast.nodes._
import ast.symboltable._
import scala.util.control.Breaks._
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

    // Main directive and pushing LR
    val mainHeaderInstructions: IndexedSeq[Instruction] = IndexedSeq[Instruction](Label("main"), pushLR)

    // Generated code for functions
    val functionInstructions: IndexedSeq[Instruction] = program.functions.flatMap(generateFunction)

    // Update the current symbol table for main method
    currentSymbolTable = symbolTableManager.nextScope()

    // Enter Scope
    val allocateInstructions = enterScopeAndAllocateStack()

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

    // Common Functions
    // note: sets are not ordered, so if you remove toList here the output is out of order!
    val commonFunctions = Utilities.commonFunctions.toList.flatMap(generateCommonFunction)

    // Program Instructions = Function Instructions + Main Instructions
    functionInstructions ++ mainInstructions ++ commonFunctions
  }

  var paramOffsetMap: Map[String, Int] = Map[String, Int]()
  var currentParamOffset: Int = 0

  def addParamToMap(param: ParamNode): Unit = {
    //if (param.getType(topSymbolTable, currentSymbolTable) != STRING) {
      paramOffsetMap = paramOffsetMap + (param.identNode.getKey -> currentParamOffset)
      currentParamOffset += getSize(param.getType(topSymbolTable, currentSymbolTable))
    //}
  }

  def createParamMap(paramListNode: ParamListNode): Unit = {
    paramOffsetMap = Map[String, Int]()
    currentParamOffset = 4
    for (param: ParamNode <- paramListNode.paramList) {
      addParamToMap(param)
    }
  }

  def getParamOffset(key: String): Int = paramOffsetMap(key)

  def generateFunction(func: FuncNode): IndexedSeq[Instruction] = {

    if (func.paramList.isDefined) {
      createParamMap(func.paramList.get)
    }
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

    val popEndInstruction = IndexedSeq[Instruction](popPC, new EndFunction)

    // TODO: HAVE TO GO BACK TO MAIN
    currentParamOffset = 0
    paramOffsetMap = Map()

    labelPushLR ++ allocateInstructions ++ statInstructions ++ deallocateInstructions ++ popEndInstruction
  }

  def getOffset(key: String): Int = {
    if (paramOffsetMap.get(key).isDefined || currentSymbolTable.lookup(key).isDefined && currentSymbolTable.lookup(key).get.isInstanceOf[PARAM])
      getParamOffset(key) + bytesAllocatedSoFar
    else
      symbolTableManager.lookupOffset(key)// + bytesAllocatedSoFar - getScopeStackSize(currentSymbolTable)
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
        => var asmType: Option[ASMType] = None
           if (checkSingleByte(ident)) asmType = Some(ByteType)
           IndexedSeq[Instruction](new Store(None, asmType, RM.peekVariableRegister(),
             instructionSet.getSP, new Immediate(symbolTableManager.lookupOffset(ident.getKey)),
             registerWriteBack = false))
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

    if (checkSingleByte(ident))
      asmType = Some(ByteType)

    IndexedSeq[Instruction](new Store(None, asmType,
      RM.peekVariableRegister(), instructionSet.getSP,
      new Immediate(symbolTableManager.setAndGetOffset(ident.getKey)), registerWriteBack = false))
  }

  // THIS, COMES FROM EXPR
  def generateArrayElem(arrayElem: ArrayElemNode): IndexedSeq[Instruction] = {
    val varReg = RM.nextVariableRegister()
    val varReg2 = RM.peekVariableRegister()

    // Must now retrieve elements in array corresponding to each
    val arrayElemInstructions = retrieveArrayElements(arrayElem, varReg, varReg2)

    val loadRes: IndexedSeq[Instruction] = IndexedSeq[Instruction](new Load(None, None, varReg, varReg))

    // Now free register, push it back onto the register stack.
    RM.freeVariableRegister(varReg)

    arrayElemInstructions ++ loadRes
  }

  // THIS COMES FROM ASSIGNLHS
  def generateArrayElemLHS(arrayElem: ArrayElemNode): IndexedSeq[Instruction] = {
    val varReg1 = RM.nextVariableRegister()
    val varReg2 = RM.nextVariableRegister()

    // Must now retrieve elements in array corresponding to each
    val arrayElemInstructions = retrieveArrayElements(arrayElem, varReg2, varReg1)

    // Check if B is necessary for load, store etc.
    // May need to be ByteType
    var asmType: Option[ASMType] = None
    if (checkSingleByte(arrayElem)) asmType = Some(ByteType)

    val storeResult = IndexedSeq[Instruction](
      new Store(None, asmType, varReg1, varReg2)
    )

    // Since we are done with variable registers above we can free them.
    RM.freeVariableRegister(varReg2)
    RM.freeVariableRegister(varReg1)

    // Return generated instructions.
    arrayElemInstructions ++ storeResult
  }

  def retrieveArrayElements(arrayElem: ArrayElemNode, varReg: Register, varReg2: Register): IndexedSeq[Instruction] = {
    val preExpr = IndexedSeq[Instruction](
      Add(None, conditionFlag = false, varReg, instructionSet.getSP,
          new Immediate(getOffset(arrayElem.identNode.getKey)))
    )

    // Produce following instructions for every expression in the array.
    val exprInstructions: IndexedSeq[Instruction] = {
      arrayElem.exprNodes.flatMap(e => generateExpression(e) ++
        IndexedSeq[Instruction](
          new Load(None, None, varReg, varReg),
          Move(None, instructionSet.getReturn, new ShiftedRegister(varReg2)),
          Move(None, instructionSet.getArgumentRegisters(1), new ShiftedRegister(varReg)),
        ) ++ Utilities.printCheckArrayBounds ++ IndexedSeq[Instruction](
          Add(None, conditionFlag = false, varReg, varReg, new Immediate(4)),
          Add(None, conditionFlag = false, varReg, varReg, new ShiftedRegister(varReg2, "LSL", 2))
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
    val arrayLength = arrayLiteral.exprNodes.length
    var exprElemSize = {
      if (arrayLength != 0)
        getSize(arrayLiteral.exprNodes.head.getType(topSymbolTable, currentSymbolTable))
      else 0
    }
    val intSize = 4

    // Calculations necessary to retrieve size of array for loading into return.
    val arraySize = intSize + arrayLength * exprElemSize

    val preExprInstructions = IndexedSeq[Instruction](
      new Load(None, None, instructionSet.getReturn, new LoadableExpression(arraySize)),
      BranchLink(None, Malloc.label),
      Move(None, varReg1, new ShiftedRegister(instructionSet.getReturn))
    )

    var generatedExpressions: IndexedSeq[Instruction] = IndexedSeq[Instruction]()

    var acc = exprElemSize
    // Generate expression instructions for each expression node in the array.
    arrayLiteral.exprNodes.foreach(expr => { generatedExpressions ++= generateExpression(expr) :+
      new Store(None, None, RM.peekVariableRegister(), varReg1,
        // Replaced hardcoded 4 with actual expression type.
        new Immediate(acc), registerWriteBack = false);  acc = acc + getSize(expr.getType(topSymbolTable, currentSymbolTable))})

    val varReg2 = RM.nextVariableRegister()

    // However above once expression in arrayLiteral is generated we must store it.
    val postExprInstructions = generatedExpressions ++ IndexedSeq[Instruction](
      // Store number of elements in array in next available variable register.
      new Load(None, None, varReg2, new LoadableExpression(arrayLength)),
      new Store(None, None, varReg2, varReg1)
    )
    // Since we are done with varReg1 and varReg2 above we can free it back to available registers.
    RM.freeVariableRegister(varReg2)
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
      BranchLink(None, Malloc.label),
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
    var pairSizeOffset = 0
    if (isSnd) pairSizeOffset = 4

    val exprInstructions = generateExpression(expr)

    var coreInstructions = IndexedSeq[Instruction](
        // Load the size of the type into a variable register.
        new Load(None, None, instructionSet.getReturn, new LoadableExpression(exprSize)),
        BranchLink(None, Malloc.label))

    // Check if B suffix is necessary (ByteType).
    if (checkSingleByte(expr)) {
      coreInstructions = coreInstructions :+ new Store(None, Some(ByteType), RM.peekVariableRegister(),
                                                       instructionSet.getReturn)
    } else {
      coreInstructions = coreInstructions :+ new Store(None, None, RM.peekVariableRegister(), instructionSet.getReturn)
    }

    RM.freeVariableRegister(varReg)
    val varReg2 = RM.peekVariableRegister()

    // Once we are on the second element it will be at an offset that we must retrieve.
    val finalStore = new Store(None, None, instructionSet.getReturn, varReg2, new Immediate(pairSizeOffset),
                               registerWriteBack = false)

    exprInstructions ++ coreInstructions :+ finalStore
  }

  def generatePairElemLHS(pairElem: PairElemNode): IndexedSeq[Instruction] = {
    // Register than had rhs evaluated instructions.
    val varReg = RM.nextVariableRegister()
    val peekedReg = RM.peekVariableRegister()

    // Check if loadOffset below can be replaced with:
    //     val loadOffset: IndexedSeq[Instruction] = pairElem match {
    //        case fst: FstNode => generateExpression(fst.expression)
    //        case snd: SndNode => generateExpression(snd.expression)
    //     }

//    val immOffset: Int = pairElem match {
//      case fst: FstNode => symbolTableManager.getOffset(fst.getKey)
//      case snd: SndNode => symbolTableManager.getOffset(snd.getKey)
//    }

    val loadOffset = IndexedSeq[Instruction](
      // Current offset of identifier related to pair.
      new Load(None, None, peekedReg, instructionSet.getSP,
               new Immediate(getSize(pairElem.getType(topSymbolTable, currentSymbolTable))), registerWriteBack = false)
    )

    val nullPtrIns = Move(None, instructionSet.getReturn,
                          new ShiftedRegister(RM.peekVariableRegister())) +: Utilities.printCheckNullPointer

    var asmType: Option[ASMType] = None

    // Check if B is necessary for load, store etc.
    // May need to be ByteType
    if (checkSingleByte(pairElem)) asmType = Some(ByteType)

    val offset: Int = pairElem match {
      case fst: FstNode => 0
      case snd: SndNode => 4
    }

    val loadStore: IndexedSeq[Instruction] = pairElem match {
      case fst: FstNode =>
        IndexedSeq[Instruction](
          new Load(None, None, peekedReg, peekedReg, new Immediate(offset), registerWriteBack = false),
          new Store(None, asmType, varReg, peekedReg, new Immediate(symbolTableManager.lookupOffset(fst.expression.getKey)),
            registerWriteBack = false)
        )
      case snd: SndNode =>
        IndexedSeq[Instruction](
          new Load(None, None, peekedReg, peekedReg, new Immediate(offset), registerWriteBack = false),
          new Store(None, asmType, varReg, peekedReg, new Immediate(symbolTableManager.lookupOffset(snd.expression.getKey)),
            registerWriteBack = false)
        )

    }
    // Free register now.
    RM.freeVariableRegister(varReg)

    loadOffset ++ nullPtrIns ++ loadStore
  }

  def generatePairElem(pairElem: PairElemNode): IndexedSeq[Instruction] = {
    pairElem match {
      case fst: FstNode =>
        IndexedSeq[Instruction](
          new Load(None, None, RM.peekVariableRegister(), instructionSet.getSP,
          new Immediate(symbolTableManager.lookupOffset(fst.expression.getKey)),
          registerWriteBack = false)
        ) ++ generatePEHelper(fst, isSnd = false)
      case snd: SndNode => IndexedSeq[Instruction](
        new Load(None, None, RM.peekVariableRegister(), instructionSet.getSP,
          new Immediate(symbolTableManager.lookupOffset(snd.expression.getKey)),
        registerWriteBack = false)
      ) ++ generatePEHelper(snd, isSnd = true)
    }
  }

  def generatePEHelper(pairElemNode: PairElemNode, isSnd: Boolean): IndexedSeq[Instruction] = {
    val peInstructions =
      Move(None, instructionSet.getReturn,
           new ShiftedRegister(RM.peekVariableRegister())) +: Utilities.printCheckNullPointer

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
          new Immediate(4), registerWriteBack = false),
    //            new Immediate(symbolTableManager.getOffset(pairElemNode.getKey)), registerWriteBack = false),
        new Load(None, asmType, RM.peekVariableRegister(), RM.peekVariableRegister())
      )

    // Should set a flag that triggers checkNullPointer at top level.
    peInstructions ++ loads
  }

  def generateCall(call: CallNode): IndexedSeq[Instruction] = {
    // Must store every argument on the stack, negative intervals, backwards.
    var argInstructions = IndexedSeq[Instruction]()
    var totalArgOffset: Int = 0
    // First check if there are arguments in the arglist.
    if (call.argList.isDefined)
      argInstructions = call.argList.get.exprNodes.reverse.flatMap(e =>
      { val exprSize = getSize(e.getType(topSymbolTable, currentSymbolTable))
        totalArgOffset += exprSize
        // Distinguish between STR and STRB.
        var asmType: Option[ASMType] = None
        if (checkSingleByte(e)) asmType = Some(ByteType)
        generateExpression(e) :+
        // Register write back should be allowed, hence the true.
          new Store(None, asmType, RM.peekVariableRegister(), instructionSet.getSP,
                    new Immediate(-exprSize), registerWriteBack = true)
      })

    var labelAndBranch = IndexedSeq[Instruction](
      BranchLink(None, Label(s"f_${call.identNode.ident}"))
    )

    // TODO May need to do this multiple times if stack exceeds 1024 (max stack size).
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
        // CONFIRMED THAT I SHOULD BE GETTING OFFSET HERE
        instructionSet.getSP, new Immediate(getOffset(ident.getKey))))
        //        instructionSet.getSP, new Immediate(getSize(ident.getType(topSymbolTable, currentSymbolTable)))))
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
        => generatedReadInstructions ++ Utilities.printReadInt
      case scalar if scalar == CharTypeNode(null).getType(topSymbolTable, currentSymbolTable)
      => generatedReadInstructions ++ Utilities.printReadChar
      case _ => assert(assertion = false, "Undefined type for read."); null
    }
  }

  def generateFree(expr: ExprNode): IndexedSeq[Instruction] = {
    // Need to generate p_free_pair here.
    generateExpression(expr) ++ (
      Move(None, instructionSet.getReturn, new ShiftedRegister(RM.peekVariableRegister())) +: Utilities.printFreePair
    )
  }

  def generateReturn(expr: ExprNode): IndexedSeq[Instruction] = {
    if (bytesAllocatedSoFar == 0)
      generateExpression(expr) ++
        IndexedSeq[Instruction](Move(None, instructionSet.getReturn, new ShiftedRegister(RM.peekVariableRegister())),
        popPC)
    else
      generateExpression(expr) ++
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

    generateExpression(expr) ++ IndexedSeq[Instruction](
      Move(None, instructionSet.getReturn, new ShiftedRegister(regUsedByGenExp)),
      BranchLink(None, Exit.label)
    )
  }

  def generatePrint(expr: ExprNode, printLn: Boolean): IndexedSeq[Instruction] = {
    val instr = expr match {
      case Int_literNode(_, n) => Utilities.printInt(n.toInt)
      case Bool_literNode(_, b) => Utilities.printBool(b)
      case Char_literNode(_, c) => Utilities.printChar(c)
      case Str_literNode(_, s) => Utilities.printString(s)
      case Pair_literNode(_) => IndexedSeq[Instruction](
        new Load(condition=None, asmType=None, dest=RM.peekVariableRegister, loadable=new LoadableExpression(0)),
        Move(condition=None, dest=instructionSet.getReturn, src=new ShiftedRegister(RM.peekVariableRegister))
      ) ++ Utilities.printReference
      case ArrayElemNode(_, _, _) => Utilities.printReference
      case i: IdentNode =>
        var asmType: Option[ASMType] = None
        if (checkSingleByte(i)) asmType = Some(SignedByte)
        IndexedSeq[Instruction](
          new Load(None, asmType, RM.peekVariableRegister(), instructionSet.getSP,
          new Immediate(getOffset(i.getKey)),
          registerWriteBack=false)) ++ (i.getType(topSymbolTable, currentSymbolTable) match {
            case scalar: SCALAR =>
              if (scalar == IntTypeNode(null).getType(topSymbolTable, currentSymbolTable)) {
                Utilities.printInt(0) // doesn't matter just need to trigger add printInt
                IndexedSeq[Instruction](
                  Move(condition = None, dest = instructionSet.getReturn,
                    src = new ShiftedRegister(RM.peekVariableRegister())),
                  BranchLink(None, PrintInt.label)
                )
              }
              else if (scalar == BoolTypeNode(null).getType(topSymbolTable, currentSymbolTable)) {
                Utilities.printBool(true)
                IndexedSeq[Instruction](
                  Move(condition = None, dest = instructionSet.getReturn,
                    src = new ShiftedRegister(RM.peekVariableRegister())),
                  BranchLink(None, PrintBool.label)
                )
              }
              else if (scalar == CharTypeNode(null).getType(topSymbolTable, currentSymbolTable)) {
                Utilities.printChar('c')
                IndexedSeq[Instruction](
                  Move(condition = None, dest = instructionSet.getReturn,
                    src = new ShiftedRegister(RM.peekVariableRegister())),
                  BranchLink(condition = None, label = PutChar.label)
                )
              }
              else
                IndexedSeq[Instruction](
                  BranchLink(None, Label("UNIMPLEMENTED PRINT !!! OH NO !!! THIS IS AN ISSUE !!! PLEASE IMPLEMENT FOR PAREN EXPR NODE !!!"))
                )
            case STRING => Utilities.printString(""); IndexedSeq[Instruction](
              Move(condition=None, dest=instructionSet.getReturn, src=new ShiftedRegister(RM.peekVariableRegister)),
              BranchLink(None, PrintString.label)
            )
            case _: ARRAY | _: PAIR => IndexedSeq[Instruction](Move(None, instructionSet.getReturn,
              new ShiftedRegister(RM.peekVariableRegister()))) ++ Utilities.printReference
          })
      case i: ParenExprNode =>
        new Load(
          condition=None, asmType=None,
          RM.peekVariableRegister, instructionSet.getSP,
          new Immediate(getOffset(i.getKey)),
          registerWriteBack = false) +: (i.getType(topSymbolTable, currentSymbolTable) match {
            case scalar: SCALAR =>
              if (scalar == IntTypeNode(null).getType(topSymbolTable, currentSymbolTable)) {
                Utilities.printInt(0) // doesn't matter just need to trigger add printInt
                IndexedSeq[Instruction](
                  Move(None, instructionSet.getReturn, new ShiftedRegister(RM.peekVariableRegister())),
                  BranchLink(None, PrintInt.label)
                )
              }
              else if (scalar == BoolTypeNode(null).getType(topSymbolTable, currentSymbolTable)) {
                Utilities.printBool(true)
                IndexedSeq[Instruction](
                  Move(None, instructionSet.getReturn, new ShiftedRegister(RM.peekVariableRegister())),
                  BranchLink(None, PrintBool.label)
                )
              }
              else if (scalar == CharTypeNode(null).getType(topSymbolTable, currentSymbolTable)) {
                Utilities.printChar('c')
                IndexedSeq[Instruction](
                  Move(None, instructionSet.getReturn, new ShiftedRegister(RM.peekVariableRegister())),
                  BranchLink(condition = None, label = PutChar.label)
                )
              }
              else
                IndexedSeq[Instruction](
                  BranchLink(None, Label("UNIMPLEMENTED PRINT !!! OH NO !!! THIS IS AN ISSUE !!! PLEASE IMPLEMENT FOR PAREN EXPR NODE !!!"))
                )
            case STRING => Utilities.printString(""); IndexedSeq[Instruction](
              Move(condition=None, dest=instructionSet.getReturn, src=new ShiftedRegister(RM.peekVariableRegister)),
              BranchLink(None, PrintString.label)
            )
            case _: ARRAY | _: PAIR => Utilities.printReference
          })
      case i: BinaryOperationNode =>
//        (new Load(
//          condition=None, asmType=None,
//          RM.peekVariableRegister(), instructionSet.getSP,
//          new Immediate(symbolTableManager.getOffset(i.getKey)),
//          registerWriteBack = false
//        ) +:
          generateBinary(i) ++
            (i.getType(topSymbolTable, currentSymbolTable) match {
            case scalar: SCALAR =>
              if (scalar == IntTypeNode(null).getType(topSymbolTable, currentSymbolTable)) {
                Utilities.printInt(0) // doesn't matter just need to trigger add printInt
                IndexedSeq[Instruction](
                  Move(None, instructionSet.getReturn, new ShiftedRegister(RM.peekVariableRegister())),
                  BranchLink(None, PrintInt.label)
                )
              }
              else if (scalar == BoolTypeNode(null).getType(topSymbolTable, currentSymbolTable)) {
                Utilities.printBool(true)
                IndexedSeq[Instruction](
                  Move(None, instructionSet.getReturn, new ShiftedRegister(RM.peekVariableRegister())),
                  BranchLink(None, PrintBool.label)
                )
              }
              else if (scalar == CharTypeNode(null).getType(topSymbolTable, currentSymbolTable)) {
                Utilities.printChar('c')
                IndexedSeq[Instruction](
                  Move(None, instructionSet.getReturn, new ShiftedRegister(RM.peekVariableRegister())),
                  BranchLink(condition = None, label = PutChar.label)
                )
              }
              else
                IndexedSeq[Instruction](
                  BranchLink(None, Label("UNIMPLEMENTED PRINT !!! OH NO !!! THIS IS AN ISSUE !!! PLEASE IMPLEMENT FOR PAREN EXPR NODE !!!"))
                )
            case STRING => Utilities.printString(""); IndexedSeq[Instruction](
              Move(condition=None, dest=instructionSet.getReturn, src=new ShiftedRegister(RM.peekVariableRegister)),
              BranchLink(None, PrintString.label)
            )
            case _: ARRAY | _: PAIR => Utilities.printReference
          })
      case i: UnaryOperationNode =>
//        (new Load(
//          condition=None, asmType=None,
//          RM.peekVariableRegister(), instructionSet.getSP,
//          new Immediate(symbolTableManager.getOffset(i.getKey)),
//          registerWriteBack = false
//        ) +:
          generateUnary(i) ++ (i.getType(topSymbolTable, currentSymbolTable) match {
            case scalar: SCALAR =>
              if (scalar == IntTypeNode(null).getType(topSymbolTable, currentSymbolTable)) {
                Utilities.printInt(0) // doesn't matter just need to trigger add printInt
                IndexedSeq[Instruction](
                  Move(None, instructionSet.getReturn, new ShiftedRegister(RM.peekVariableRegister())),
                  BranchLink(None, PrintInt.label)
                )
              }
              else if (scalar == BoolTypeNode(null).getType(topSymbolTable, currentSymbolTable)) {
                Utilities.printBool(true)
                IndexedSeq[Instruction](
                  Move(None, instructionSet.getReturn, new ShiftedRegister(RM.peekVariableRegister())),
                  BranchLink(None, PrintBool.label)
                )
              }
              else if (scalar == CharTypeNode(null).getType(topSymbolTable, currentSymbolTable)) {
                Utilities.printChar('c')
                IndexedSeq[Instruction](
                  Move(None, instructionSet.getReturn, new ShiftedRegister(RM.peekVariableRegister())),
                  BranchLink(condition = None, label = PutChar.label)
                )
              }
              else
                IndexedSeq[Instruction](
                  BranchLink(None, Label("UNIMPLEMENTED PRINT !!! OH NO !!! THIS IS AN ISSUE !!! PLEASE IMPLEMENT FOR PAREN EXPR NODE !!!"))
                )
            case STRING => Utilities.printString(""); IndexedSeq[Instruction](
              Move(condition=None, dest=instructionSet.getReturn, src=new ShiftedRegister(RM.peekVariableRegister)),
              BranchLink(None, PrintString.label)
            )
            case _: ARRAY | _: PAIR => Utilities.printReference
          })
    }

    if (printLn) instr ++ Utilities.printNewline
    else instr
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
    val elseInstructions = generateStatement(ifNode.elseStat)

    // Leave Scope
    val deallocateElseInstruction: IndexedSeq[Instruction] = leaveScopeAndDeallocateStack()

    // ***********

    // *** SUMMARY ***

    val totalThenInstructions = allocateThenInstruction ++ thenInstructions ++ deallocateThenInstruction

    val totalElseInstructions = elseLabel +: (allocateElseInstruction ++ elseInstructions ++ deallocateElseInstruction)

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

    val totalConditionInstructions = conditionLabel +: condInstructions :+ bodyBranch

    initConditionBranch +: (totalBodyInstructions ++ totalConditionInstructions)
  }

  def generateBegin(begin: BeginNode): IndexedSeq[Instruction] = {
    // We must first enter the new scope, then generate the statements inside the scope,
    // then finally close the scope.

    // Update the current symbol table to the begin scope
    currentSymbolTable = symbolTableManager.nextScope()

    // Enter Scope
    val allocateInstructions: IndexedSeq[Instruction] = enterScopeAndAllocateStack()

    // Scope Instructions
    val statInstructions: IndexedSeq[Instruction] = generateStatement(begin.stat)

    // Leave Scope
    val deallocateInstructions: IndexedSeq[Instruction] = leaveScopeAndDeallocateStack()

    allocateInstructions ++ statInstructions ++ deallocateInstructions
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
                     RM.peekVariableRegister(), Utilities.addString(str)))
      // All that is necessary for Pair_liter expression generation.
      case Pair_literNode(_)
                  => IndexedSeq[Instruction](new Load(None, None,
                     RM.peekVariableRegister(), new LoadableExpression(0)))
      case ident: IdentNode
      // Load identifier into first available variable register.
          => if (checkSingleByte(ident)) {
              IndexedSeq[Instruction](new Load(None, Some(SignedByte),
                RM.peekVariableRegister(), instructionSet.getSP,
                new Immediate(getOffset(ident.getKey)),
                registerWriteBack = false))
        //                        new Immediate(getSize(
        //                          ident.getType(topSymbolTable, currentSymbolTable)))))
          } else {
              IndexedSeq[Instruction](new Load(None, None,
                RM.peekVariableRegister(), instructionSet.getSP,
                new Immediate(getOffset(ident.getKey)),
                registerWriteBack = false))}
      case arrayElem: ArrayElemNode => generateArrayElem(arrayElem)
      case unaryOperation: UnaryOperationNode => generateUnary(unaryOperation)
      case binaryOperation: BinaryOperationNode => generateBinary(binaryOperation)
      case parenExpr: ParenExprNode => generateExpression(parenExpr.expr)
    }
  }

  def generateUnary(unaryOperation: UnaryOperationNode): IndexedSeq[Instruction] = {
    unaryOperation match {
      // Logical not node according to the reference compiler.
      case LogicalNotNode(_, expr) =>
        generateExpression(expr) ++ IndexedSeq[Instruction](
          ExclusiveOr(None, conditionFlag = false, RM.peekVariableRegister(),
                      RM.peekVariableRegister(), new Immediate(1)))
      // Negate according to reference compiler.
      case NegateNode(_, expr) =>
        expr match {
          case Int_literNode(_, str) => IndexedSeq[Instruction](
            new Load(None, None, RM.peekVariableRegister(), new LoadableExpression(str.toInt)))
          case _ => generateExpression(expr) ++ IndexedSeq[Instruction](
            RSBS(None, conditionFlag = false, RM.peekVariableRegister(),
            RM.peekVariableRegister(), new Immediate(0)),
          ) ++ Utilities.printOverflowError(Some(Overflow))
        }
      case LenNode(_, expr) =>
        generateExpression(expr) ++ IndexedSeq[Instruction](
          new Load(None, None, RM.peekVariableRegister(), RM.peekVariableRegister())
        )
      // Finished implementation as nothing else must be done.
      case OrdNode(_, expr) => generateExpression(expr)
      case ChrNode(_, expr) => generateExpression(expr)
    }
  }

  def generateBinary(binaryOperation: BinaryOperationNode): IndexedSeq[Instruction] = {
    val varReg1 = RM.nextVariableRegister()
    val varReg2 = RM.peekVariableRegister()
    val r1 = instructionSet.getArgumentRegisters(1)
    RM.freeVariableRegister(varReg1)

    // Necessary to insure register correctness
    var firstArgument: ExprNode = Int_literNode(null, null)
    var secondArgument: ExprNode = Int_literNode(null, null)

    val binOpInstructions: IndexedSeq[Instruction] = binaryOperation match {
      case MultiplyNode(_, argOne, argTwo) =>
        firstArgument = argOne
        secondArgument = argTwo
          IndexedSeq[Instruction](
            SMull(None, conditionFlag = false, varReg1, varReg2, varReg1, varReg2),
            // Need to be shifted by 31, ASR 31
            Compare(None, varReg2, new ShiftedRegister(varReg1, "ASR", 31))) ++ Utilities.printOverflowError(Some(NotEqual))
      case DivideNode(_, argOne, argTwo) =>
        firstArgument = argOne
        secondArgument = argTwo
          IndexedSeq[Instruction](
            Move(None, instructionSet.getReturn, new ShiftedRegister(varReg1)),
            Move(None, r1, new ShiftedRegister(varReg2))
          ) ++ Utilities.printDivideByZero ++ IndexedSeq[Instruction](
          BranchLink(None, Div.label),
          Move(None, varReg1, new ShiftedRegister(instructionSet.getReturn)))
      case ModNode(_, argOne, argTwo) =>
        firstArgument = argOne
        secondArgument = argTwo
          IndexedSeq[Instruction](
            Move(None, instructionSet.getReturn, new ShiftedRegister(varReg1)),
            Move(None, r1, new ShiftedRegister(varReg2))
          ) ++ Utilities.printDivideByZero ++ IndexedSeq[Instruction](
          BranchLink(None, DivMod.label),
          Move(None, varReg1, new ShiftedRegister(r1)))
      case PlusNode(_, argOne, argTwo) =>
        // Should be ADDS, conditionFlag set to true.
        firstArgument = argOne
        secondArgument = argTwo
          IndexedSeq[Instruction](
            Add(None, conditionFlag = true, varReg1, varReg1, new ShiftedRegister(varReg2))
          ) ++ Utilities.printOverflowError(Some(Overflow))
      case MinusNode(_, argOne, argTwo) =>
        firstArgument = argOne
        secondArgument = argTwo
          IndexedSeq[Instruction](
            Subtract(None, conditionFlag = true, varReg1, varReg1, new ShiftedRegister(varReg2))
          ) ++ Utilities.printOverflowError(Some(Overflow))
      case GreaterThanNode(_, argOne, argTwo) =>
        firstArgument = argOne
        secondArgument = argTwo
          IndexedSeq[Instruction](
            Compare(None, varReg1, new ShiftedRegister(varReg2)),
            Move(Some(GreaterThan), varReg1, new Immediate(1)),
            Move(Some(LessEqual), varReg1, new Immediate(0)))
      case GreaterEqualNode(_, argOne, argTwo) =>
        firstArgument = argOne
        secondArgument = argTwo
          IndexedSeq[Instruction](
            Compare(None, varReg1, new ShiftedRegister(varReg2)),
            Move(Some(GreaterEqual), varReg1, new Immediate(1)),
            Move(Some(LessThan), varReg1, new Immediate(0)))
      case LessThanNode(_, argOne, argTwo) =>
        firstArgument = argOne
        secondArgument = argTwo
          IndexedSeq[Instruction](
            Compare(None, varReg1, new ShiftedRegister(varReg2)),
            Move(Some(LessThan), varReg1, new Immediate(1)),
            Move(Some(GreaterEqual), varReg1, new Immediate(0)))
      case LessEqualNode(_, argOne, argTwo) =>
        firstArgument = argOne
        secondArgument = argTwo
          IndexedSeq[Instruction](
            Compare(None, varReg1, new ShiftedRegister(varReg2)),
            Move(Some(LessEqual), varReg1, new Immediate(1)),
            Move(Some(GreaterThan), varReg1, new Immediate(0)))
      case EqualToNode(_, argOne, argTwo) =>
        firstArgument = argOne
        secondArgument = argTwo
        IndexedSeq[Instruction](
            Compare(None, varReg1, new ShiftedRegister(varReg2)),
            Move(Some(Equal), varReg1, new Immediate(1)),
            Move(Some(NotEqual), varReg1, new Immediate(0)))
      case NotEqualNode(_, argOne, argTwo) =>
        firstArgument = argOne
        secondArgument = argTwo
          IndexedSeq[Instruction](
            Compare(None, varReg1, new ShiftedRegister(varReg2)),
            Move(Some(NotEqual), varReg1, new Immediate(1)),
            Move(Some(Equal), varReg1, new Immediate(0)))

      case LogicalAndNode(_, argOne, argTwo) =>
        firstArgument = argOne
        secondArgument = argTwo
          IndexedSeq[Instruction](And(None, conditionFlag = false, varReg1,
            varReg1, new ShiftedRegister(varReg2)))
      case LogicalOrNode(_, argOne, argTwo) =>
        firstArgument = argOne
        secondArgument = argTwo
          IndexedSeq[Instruction](Or(None, conditionFlag = false, varReg1,
            varReg1, new ShiftedRegister(varReg2)))
    }

    // Generate the instructions for the first argument.
    val firstArgInstructions: IndexedSeq[Instruction] = generateExpression(firstArgument)

    // Call next register as previous variable register is storing first arg instructions.
    val varReg3: Register = RM.nextVariableRegister()
    // Generate the instructions for the second argument.
    val secondArgInstructions: IndexedSeq[Instruction] = generateExpression(secondArgument)
    // Free register used by generateExpression to allow other generators to use it.
    RM.freeVariableRegister(varReg3)

    // Return all instructions in the correct order.
    firstArgInstructions ++ secondArgInstructions ++ binOpInstructions
  }

  def generateCommonFunction(func: CommonFunction): IndexedSeq[Instruction] = func match {
    case PrintLn => IndexedSeq[Instruction](
      PrintLn.label,
      pushLR,
      new Load(condition=None, asmType=None, dest=instructionSet.getReturn, loadable=Label("msg_print_ln")),
      Add(condition=None, conditionFlag=false, dest=instructionSet.getReturn,
          src1=instructionSet.getReturn, src2=new Immediate(4)),
      BranchLink(condition=None, label=Puts.label),
      Move(condition=None, dest=instructionSet.getReturn, src=new Immediate(0)),
      BranchLink(condition=None, label=Flush.label),
      popPC
    )
    case PrintRuntimeError => IndexedSeq[Instruction](
      PrintRuntimeError.label,
      BranchLink(condition=None, PrintString.label),
      Move(condition=None, dest=instructionSet.getReturn, src=new Immediate(-1)),
      BranchLink(None, Exit.label)
    )
    case PrintString => IndexedSeq[Instruction](
      PrintString.label,
      pushLR,
      new Load(condition=None, asmType=None, dest=instructionSet.getArgumentRegisters(1),
               src=instructionSet.getReturn),
      Add(condition=None, conditionFlag=false, dest=instructionSet.getArgumentRegisters(2),
          src1=instructionSet.getReturn, src2=new Immediate(4)),
      new Load(condition=None, asmType=None, dest=instructionSet.getReturn, loadable=Label("msg_print_string")),
      Add(condition=None, conditionFlag=false, dest=instructionSet.getReturn,
          src1=instructionSet.getReturn, src2=new Immediate(4)),
      BranchLink(condition=None, label=Printf.label),
      Move(condition=None, dest=instructionSet.getReturn, src=new Immediate(0)),
      BranchLink(condition=None, label=Flush.label),
      popPC
    )
    case PrintFreePair => IndexedSeq[Instruction](
      PrintFreePair.label,
      pushLR,
      Compare(condition=None, operand1=instructionSet.getReturn, operand2=new Immediate(0)),
      new Load(condition=Some(Equal), asmType=None, dest=instructionSet.getReturn, loadable=Label("msg_free_pair")),
      Branch(condition=Some(Equal), label=PrintRuntimeError.label),
      Push(condition=None, List(instructionSet.getReturn)),
      new Load(condition=None, asmType=None, dest=instructionSet.getReturn, src=instructionSet.getReturn),
      BranchLink(condition=None, label=Free.label),
      new Load(condition=None, asmType=None, dest=instructionSet.getReturn, src=instructionSet.getSP),
      new Load(condition=None, asmType=None, dest=instructionSet.getReturn, src=instructionSet.getReturn,
               flexOffset=new Immediate(4), registerWriteBack = false),
      BranchLink(condition=None, label=Free.label),
      Pop(condition=None, List(instructionSet.getReturn)),
      BranchLink(condition=None, label=Free.label),
      popPC
    )
    case PrintReference => IndexedSeq[Instruction](
      PrintReference.label,
      pushLR,
      Move(condition=None, dest=instructionSet.getArgumentRegisters(1),
           src=new ShiftedRegister(instructionSet.getReturn)),
      new Load(condition=None, asmType=None, dest=instructionSet.getReturn, loadable=Label("msg_print_reference")),
      Add(condition = None, conditionFlag = false, dest = instructionSet.getReturn,
          src1 = instructionSet.getReturn, src2 = new Immediate(4)),
      BranchLink(condition=None, label=Printf.label),
      Move(condition=None, dest=instructionSet.getReturn, src=new Immediate(0)),
      BranchLink(condition=None, label=Flush.label),
      popPC
    )
    case PrintReadChar => IndexedSeq[Instruction](
      PrintReadChar.label,
      pushLR,
      Move(condition=None, dest=instructionSet.getArgumentRegisters(1),
           src=new ShiftedRegister(instructionSet.getReturn)),
      new Load(condition=None, asmType=None, dest=instructionSet.getReturn, loadable=Label("msg_read_char")),
      Add(condition = None, conditionFlag = false, dest = instructionSet.getReturn,
          src1 = instructionSet.getReturn, src2 = new Immediate(4)),
      BranchLink(condition=None, label=Scanf.label),
      popPC
    )
    case PrintReadInt => IndexedSeq[Instruction](
      PrintReadInt.label,
      pushLR,
      Move(condition=None, dest=instructionSet.getArgumentRegisters(1),
           src=new ShiftedRegister(instructionSet.getReturn)),
      new Load(condition=None, asmType=None, dest=instructionSet.getReturn, loadable=Label("msg_read_int")),
      Add(condition = None, conditionFlag = false, dest = instructionSet.getReturn,
          src1 = instructionSet.getReturn, src2 = new Immediate(4)),
      BranchLink(condition=None, label=Scanf.label),
      popPC
    )
    case PrintCheckNullPointer => IndexedSeq[Instruction](
      PrintCheckNullPointer.label,
      pushLR,
      Compare(condition=None, operand1=instructionSet.getReturn, operand2=new Immediate(0)),
      new Load(condition=Some(Equal), asmType=None, dest=instructionSet.getReturn,
               loadable=Label("msg_check_null_pointer")),
      BranchLink(condition=Some(Equal), PrintRuntimeError.label),
      popPC
    )
    case PrintBool => IndexedSeq[Instruction](
      PrintBool.label,
      pushLR,
      Compare(condition=None, operand1=instructionSet.getReturn, operand2=new Immediate(0)),
      new Load(condition=Some(NotEqual), asmType=None, dest=instructionSet.getReturn,
               loadable=Label("msg_print_bool_true")),
      new Load(condition=Some(Equal), asmType=None, dest=instructionSet.getReturn,
               loadable=Label("msg_print_bool_false")),
      Add(condition = None, conditionFlag = false, dest = instructionSet.getReturn,
          src1 = instructionSet.getReturn, src2 = new Immediate(4)),
      BranchLink(condition=None, label=Printf.label),
      Move(condition=None, dest=instructionSet.getReturn, src=new Immediate(0)),
      BranchLink(condition=None, label=Flush.label),
      popPC
    )
    case PrintCheckArrayBounds => IndexedSeq[Instruction](
      PrintCheckArrayBounds.label,
      pushLR,
      Compare(condition=None, operand1=instructionSet.getReturn, operand2=new Immediate(0)),
      new Load(condition=Some(LessThan), asmType=None, dest=instructionSet.getReturn,
               loadable=Label("msg_negative_index")),
      BranchLink(condition=Some(LessThan), label=PrintRuntimeError.label),
      new Load(condition=None, asmType=None, dest=instructionSet.getArgumentRegisters(1),
               src=instructionSet.getArgumentRegisters(1)),
      Compare(condition=None, operand1=instructionSet.getReturn,
              operand2=new ShiftedRegister(instructionSet.getArgumentRegisters(1))),
      new Load(condition=Some(HigherSame), asmType=None, dest=instructionSet.getReturn,
               loadable=Label("msg_index_too_large")),
      BranchLink(condition=Some(HigherSame), PrintRuntimeError.label),
      popPC
    )
    case PrintInt => IndexedSeq[Instruction](
      PrintInt.label,
      pushLR,
      Move(condition=None, dest=instructionSet.getArgumentRegisters(1),
           src=new ShiftedRegister(instructionSet.getReturn)),
      new Load(condition=None, asmType=None, dest=instructionSet.getReturn, loadable=Label("msg_print_int")),
      Add(condition = None, conditionFlag = false, dest = instructionSet.getReturn,
          src1 = instructionSet.getReturn, src2 = new Immediate(4)),
      BranchLink(condition=None, Printf.label),
      Move(condition=None, dest=instructionSet.getReturn, src=new Immediate(0)),
      BranchLink(condition=None, Flush.label),
      popPC
    )
    case PrintOverflowError => IndexedSeq[Instruction](
      PrintOverflowError.label,
      new Load(condition=None, asmType=None, dest=instructionSet.getReturn,
               loadable=Label("msg_throw_overflow_error")),
      BranchLink(condition=None, label=PrintRuntimeError.label)
    )
    case PrintDivideByZero => IndexedSeq[Instruction](
      PrintDivideByZero.label,
      pushLR, Compare(None, instructionSet.getArgumentRegisters(1), new Immediate(0)),
      new Load(Some(Equal), None, instructionSet.getReturn, loadable = Label("msg_divide_by_zero")),
      BranchLink(Some(Equal), PrintRuntimeError.label), popPC
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
    symbolTable.map.values.map(getIDStackSize).sum
  }

  def getIDStackSize(identifier: IDENTIFIER): Int = {
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
      var allocateInstructions: IndexedSeq[Instruction] = IndexedSeq()
      var bytesToAllocate = getScopeStackSize(currentSymbolTable)
      while (bytesToAllocate > 0) {
        if (bytesToAllocate >= instructionSet.getMaxOffset) {
          bytesToAllocate -= instructionSet.getMaxOffset
          allocateInstructions = allocateInstructions :+ Subtract(None, conditionFlag = false, instructionSet.getSP, instructionSet.getSP, new Immediate(instructionSet.getMaxOffset))
          bytesAllocatedSoFar += instructionSet.getMaxOffset
        } else {
          allocateInstructions = allocateInstructions :+ Subtract(None, conditionFlag = false, instructionSet.getSP, instructionSet.getSP, new Immediate(bytesToAllocate))
          bytesAllocatedSoFar += bytesToAllocate
          bytesToAllocate = 0
        }
      }
      allocateInstructions
    }
  }

  def leaveScopeAndDeallocateStack(): IndexedSeq[Instruction] = {
    currentSymbolTable = symbolTableManager.leaveScope()
    if (getScopeStackSize(currentSymbolTable) == 0) IndexedSeq()
    // If all the bytes allocated so far have been freed, a return must have already taken place
    else if (bytesAllocatedSoFar != 0) {
      var deallocateInstructions: IndexedSeq[Instruction] = IndexedSeq()
      while (bytesAllocatedSoFar > 0) {
        if (bytesAllocatedSoFar >= instructionSet.getMaxOffset) {
          deallocateInstructions = deallocateInstructions :+ Add(None, conditionFlag = false, instructionSet.getSP, instructionSet.getSP, new Immediate(instructionSet.getMaxOffset))
          bytesAllocatedSoFar -= instructionSet.getMaxOffset
        } else {
          deallocateInstructions = deallocateInstructions :+ Add(None, conditionFlag = false, instructionSet.getSP, instructionSet.getSP, new Immediate(bytesAllocatedSoFar))
          bytesAllocatedSoFar -= bytesAllocatedSoFar
        }
      }
      deallocateInstructions
    }
    else IndexedSeq()
  }

  case class SymbolTableInfo(symbolTable: SymbolTable, scopeIndex: Int) {
    var offsetMap: Map[String, Int] = Map.empty
    val symbolTableSize: Int = if (scopeIndex == -1) 0 else getScopeStackSize(symbolTable)
    private var offsetSoFar: Int = symbolTableSize

    def setAndGetOffset(key: String): Int = {
      val currentIdOption = symbolTable.lookup(key)
      assert (currentIdOption.isDefined, "key must be defined in the scope")
      val offsetSize: Int = currentIdOption.get match {
        case value: TYPE => getSize(value)
        case variable: VARIABLE => getSize(variable._type)
        case _ =>
          assert(assertion = false, "key ID must be a variable or type")
          -1
      }
      offsetSoFar -= offsetSize
      // Add offset for IDENTIFIER to current scope map.
      offsetMap = offsetMap + (key -> offsetSoFar)
      offsetSoFar
    }
  }

  case class SymbolTableManager(private val initScope: SymbolTable) {
    // Info of the current symbol table
    private var currentInfo: SymbolTableInfo = SymbolTableInfo(initScope, -1)

    // Stack keeping track of the symbolTable, index, byteSize, offsetSoFar and offsetMap of
    // the symbol tables as we enter scope
    // (symbolTable, index, symbolTableSize, offsetSoFar, offsetMap)
    private var infoStack: List[SymbolTableInfo] = List.empty

    // Current scope parent
    var currentScopeParent: SymbolTable = _

    // Returns the next scope under the current scope level
    def nextScope(): SymbolTable = {
      // Check there are children
      assert(currentScopeParent.children != null, "Scope parent must have children to get next scope")
      var newIndex = 0
      // If we are already iterating through the scope then increment, otherwise reset to 0
      if (currentInfo.symbolTable.encSymbolTable == currentScopeParent) {
        // Check you can go to next scope
        assert(currentInfo.scopeIndex + 1 < currentScopeParent.children.length, s"Scope parent has no more scopes left")
        newIndex = currentInfo.scopeIndex + 1
      }
      // Update current scope
      currentInfo = SymbolTableInfo(currentScopeParent.children.apply(newIndex), newIndex)
      currentInfo.symbolTable
    }

    // Enters the current scope
    def enterScope(): Unit = {
      currentScopeParent = currentInfo.symbolTable
      // Push
      infoStack = currentInfo :: infoStack
      //currentInfo = null
    }

    // Leaves the current scope
    def leaveScope(): SymbolTable = {
      assert(infoStack.nonEmpty, "Scope is at the top level already")
      // Reset currentScopeParent
      currentScopeParent = currentScopeParent.encSymbolTable
      // Pop
      currentInfo = infoStack.head
      infoStack = infoStack.tail

      // Return table
      currentInfo.symbolTable
    }

    // Called on declarations for idents to set the map address
    def setAndGetOffset(key: String): Int = {
      currentInfo.setAndGetOffset(key)
    }

    // Returns current identifier offset
    def lookupOffset(key: String): Int = {
      var offset: Option[Int] = currentInfo.offsetMap.get(key)
      var additionalBytes = 0
      // If the offset is not in the current offsetMap iterate through all parent maps
      if (offset.isEmpty) {
        // Add the byte allocation for the current scope
        additionalBytes += currentInfo.symbolTableSize
        breakable {
          for (iteratingInfo <- infoStack) {
            // Lookup offset
            offset = iteratingInfo.offsetMap.get(key)
            // If it is defined, break
            if (offset.isDefined) break
            // Add scope bytes to additional bytes
            additionalBytes += iteratingInfo.symbolTableSize
          }
        }
      }
      // If defined, return offset + additional bytes
      if (offset.isDefined) {
        offset.get + additionalBytes
      } else {
        // Otherwise it's an error
        assert(assertion = false, s"$key does not exist in this symbol table or all parent tables")
        -1
      }
    }

    def returnToTopScope(): Unit = {
      while (infoStack.size > 1)
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



