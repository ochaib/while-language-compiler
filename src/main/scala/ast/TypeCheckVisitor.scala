package ast

import util.SemanticErrorLog

import scala.collection.immutable.HashMap

sealed class TypeCheckVisitor(entryNode: ASTNode) extends Visitor(entryNode) {
  var topSymbolTable: SymbolTable = SymbolTable.topLevelSymbolTable(entryNode)
  var currentSymbolTable: SymbolTable = SymbolTable.newSymbolTable(topSymbolTable)

  override def visit(ASTNode: ASTNode): Unit = ASTNode match {

    // AST NODES

    case ProgramNode(functions, stat) =>
      for (functionNode <- functions) visit(functionNode)
      visit(stat)

    case FuncNode(funcType, identNode, paramList: Option[ParamListNode], stat: StatNode) =>
      visit(funcType)
      var functionIdentifier: FUNCTION = null
      // check identNode is already defined
      if (currentSymbolTable.lookupFun(identNode.getKey).isDefined)
        SemanticErrorLog.add(s"Tried to define function: ${identNode.getKey} but it was already declared.")
      else
        functionIdentifier = new FUNCTION(identNode.getKey, funcType.getType(topSymbolTable, currentSymbolTable).asInstanceOf[TYPE], paramTypes = null)
        currentSymbolTable.add(identNode.getKey, functionIdentifier)

      // Prepare to visit stat by creating new symbol table
      currentSymbolTable = SymbolTable.newSymbolTable(currentSymbolTable)
      // Missing: link symbol table to function?
      if (paramList.isDefined) {
        // implicitly adds identifiers to the symbol table
        functionIdentifier.paramTypes = paramList.get.getIdentifierList(topSymbolTable, currentSymbolTable)
        visit(paramList.get)
      }

      visit(stat)
      // Exit symbol table
      currentSymbolTable = currentSymbolTable.encSymbolTable

    case ParamListNode(paramNodeList) => for (paramNode <- paramNodeList) visit(paramNode)

    case ParamNode(paramType, identNode) =>
      visit(paramType)
      val paramIdentifier: Option[IDENTIFIER] = currentSymbolTable.lookup(identNode.getKey)
      if (! (paramIdentifier.isDefined && paramIdentifier.get.isInstanceOf[PARAM]) ) {
        SemanticErrorLog.add(s"Expected ${identNode.getKey} to refer to a parameter but it does not.")
      }

    case statNode: StatNode => statNode match {

      // STAT NODES

      case _:SkipNode =>

      case DeclarationNode(_type, ident, rhs) =>
        val typeIdentifier: IDENTIFIER = _type.getType(topSymbolTable, currentSymbolTable)
        visit(rhs)

        // If the type and the rhs don't match, throw exception
        if (typeIdentifier != rhs.getType(topSymbolTable, currentSymbolTable)) {
          SemanticErrorLog.add(s"Declaration failed, expected type ${typeIdentifier.getKey} " +
            s"but got type ${rhs.getType(topSymbolTable, currentSymbolTable).getKey}.")
        }
        if (currentSymbolTable.lookup(ident.getKey).isDefined) {
          // If variable is already defined log error
          SemanticErrorLog.add(s"Declaration failed, ${ident.getKey} has already been declared.")
        } else {
          currentSymbolTable.add(ident.getKey, new VARIABLE(ident.getKey, typeIdentifier.asInstanceOf[TYPE]))
        }

      case AssignmentNode(lhs, rhs) =>
        visit(lhs)
        visit(rhs)

        if (lhs.getType(topSymbolTable, currentSymbolTable) != rhs.getType(topSymbolTable, currentSymbolTable)) {
          SemanticErrorLog.add(s"Assignment failed, ${lhs.getKey} and ${rhs.getKey} have non-matching types.")
        }

      case ReadNode(lhs) =>
        visit(lhs)

        if (!(lhs.getType(topSymbolTable, currentSymbolTable) == IntTypeNode.getType(topSymbolTable, currentSymbolTable)
          || lhs.getType(topSymbolTable, currentSymbolTable) == CharTypeNode.getType(topSymbolTable, currentSymbolTable))) {
          SemanticErrorLog.add(s"Cannot read ${lhs.getKey}, it must be either a character or an integer.")
        }

      case FreeNode(expr) =>
        visit(expr)

        val exprIdentifier = expr.getType(topSymbolTable, currentSymbolTable)

        if (!(exprIdentifier.isInstanceOf[PAIR] || exprIdentifier == GENERAL_PAIR) ||
          !exprIdentifier.isInstanceOf[ARRAY]) {
          SemanticErrorLog.add(s"Cannot free ${expr.getKey}, it must be a pair or an array.")
        }

      // TODO: Check that return statement is present in body of non-main function.
      // TODO: Check that the type of expression given to the return statement must
      // TODO: match the return type of the expression.

      case ReturnNode(expr) => visit(expr)

      case ExitNode(expr) => visit(expr)

        val exprIdentifier = expr.getType(topSymbolTable, currentSymbolTable)

        if (!(exprIdentifier == IntTypeNode.getType(topSymbolTable, currentSymbolTable))) {
          SemanticErrorLog.add(s"Cannot exit with ${expr.getKey}, it must be an integer.")
        }

      case PrintNode(expr) => visit(expr)

      case PrintlnNode(expr) => visit(expr)

      case IfNode(conditionExpr, thenStat, elseStat) =>
        conditionCheckerHelper(conditionExpr)

        // Prepare to visit stat by creating new symbol table
        currentSymbolTable = SymbolTable.newSymbolTable(currentSymbolTable)
        visit(thenStat)
        // Exit symbol table
        currentSymbolTable = currentSymbolTable.encSymbolTable
        // Prepare to visit stat by creating new symbol table
        currentSymbolTable = SymbolTable.newSymbolTable(currentSymbolTable)
        visit(elseStat)
        // Exit symbol table
        currentSymbolTable = currentSymbolTable.encSymbolTable


      case WhileNode(expr, stat) =>
        conditionCheckerHelper(expr)
        // Prepare to visit stat by creating new symbol table
        currentSymbolTable = SymbolTable.newSymbolTable(currentSymbolTable)
        visit(stat)
        // Exit symbol table
        currentSymbolTable = currentSymbolTable.encSymbolTable

      case BeginNode(stat) => visit(stat)

      case SequenceNode(statOne, statTwo) =>
        // Prepare to visit stat by creating new symbol table
//        currentSymbolTable = SymbolTable.newSymbolTable(currentSymbolTable)
        visit(statOne)
        visit(statTwo)
        // Exit symbol table
//        currentSymbolTable = currentSymbolTable.encSymbolTable
    }

    // AssignLHSNodes

    case assignLHSNode: AssignLHSNode => assignLHSNode match {

      case IdentNode(ident) =>
        if (currentSymbolTable.lookupAll(assignLHSNode.getKey).isEmpty) {
          SemanticErrorLog.add(s"$ident has not been declared as an identifier.")
        }

      case ArrayElemNode(identNode, exprNodes) => arrayElemCheckerHelper(identNode, exprNodes)

      case pairElemNode: PairElemNode => pairElemCheckerHelper(pairElemNode)
    }

    // AssignRHSNodes

    case assignRHSNode: AssignRHSNode => assignRHSNode match {
      case exprNode: ExprNode => exprNodeCheckerHelper(exprNode)
      case ArrayLiteralNode(exprNodes) =>
        val firstIdentifier: IDENTIFIER = exprNodes.apply(0).getType(topSymbolTable, currentSymbolTable)
        for (expr <- exprNodes) {
          val exprIdentifier = expr.getType(topSymbolTable, currentSymbolTable)
          if (exprIdentifier != firstIdentifier) {
            SemanticErrorLog.add(s"Expected type ${firstIdentifier.getKey} but got ${exprIdentifier.getKey}.")
          }
        }
      case NewPairNode(fstElem, sndElem) =>
        visit(fstElem)
        visit(sndElem)
      case CallNode(identNode, argList) => // TODO
        val funcIdentifier: Option[FUNCTION] = currentSymbolTable.lookupFunAll(identNode.getKey)
        if (funcIdentifier.isEmpty)
          SemanticErrorLog.add(s"Function ${identNode.getKey} not declared.")
        else if (argList.isDefined && funcIdentifier.get.paramTypes.length != argList.get.exprNodes.length){
          SemanticErrorLog.add(s"Function: ${identNode.getKey} expected ${funcIdentifier.get.paramTypes.length} " +
            s"arguments but got ${argList.get.exprNodes.length} arguments instead.")
        } else if (argList.isDefined){
          visit(argList.get)
          for (argIndex <- argList.get.exprNodes.indices) {
            val argType: TYPE = argList.get.exprNodes.apply(argIndex).getType(topSymbolTable, currentSymbolTable)
            val paramType: TYPE = funcIdentifier.get.paramTypes.apply(argIndex)
            if (argType != paramType) {
              SemanticErrorLog.add(s"Expected type ${paramType.getKey} but got ${argType.getKey}.")
            }
          }
          // funcObj = F in slides???
        }
      case pairElemNode: PairElemNode => pairElemCheckerHelper(pairElemNode)
    }

    case ArgListNode(exprNodes) => for (exprNode <- exprNodes) visit(exprNode)

    case typeNode: TypeNode => typeNode match {
      case _: BaseTypeNode => // Always true
      case ArrayTypeNode(arrayTypeNode) => visit(arrayTypeNode)
      case PairTypeNode(firstPairElem, secondPairElem) =>
        visit(firstPairElem)
        visit(secondPairElem)
    }

    case pairElemType: PairElemTypeNode => pairElemType match {
      // case ArrayTypeNode(typeNode) =>
      // case node: BaseTypeNode =>
      case _: PairElemTypePairNode => // base pair always true
    }
  }

  def pairElemNodeVisit(expr: ExprNode): Unit = {
    val pairIdentifier: IDENTIFIER = expr.getType(topSymbolTable, currentSymbolTable)
    if (! pairIdentifier.isInstanceOf[PAIR]) {
      SemanticErrorLog.add(s"Expected pair type but got $pairIdentifier.")
    } else {
      visit(expr)
    }
  }

  // Unary Operator Helpers
  def checkHelper(expr: ExprNode, expectedIdentifier: String, topSymbolTable: SymbolTable, ST: SymbolTable): Unit = {
    val identifier: IDENTIFIER = expr.getType(topSymbolTable, currentSymbolTable)
    if (identifier != topSymbolTable.lookup(expectedIdentifier).get) {
      SemanticErrorLog.add(s"Expected $expectedIdentifier but got $identifier.")
    }
  }

  def lenHelper(expr: ExprNode, topSymbolTable: SymbolTable, ST: SymbolTable): Unit = {
    val identifier: IDENTIFIER = expr.getType(topSymbolTable, currentSymbolTable)
    if (!identifier.isInstanceOf[ARRAY]) {
      SemanticErrorLog.add(s"Expected an array but got $identifier.")
    }
  }

  // Binary Operator Helpers
  def comparatorsCheckerHelper(argOne: ExprNode, argTwo: ExprNode,
                               expectedIdentifier1: IDENTIFIER, expectedIdentifier2: IDENTIFIER, topSymbolTable: SymbolTable, ST: SymbolTable): Unit = {
    val argOneIdentifier: IDENTIFIER = argOne.getType(topSymbolTable, currentSymbolTable)
    val argTwoIdentifier: IDENTIFIER = argTwo.getType(topSymbolTable, currentSymbolTable)
    if (!((argOneIdentifier == expectedIdentifier1 || argOneIdentifier == expectedIdentifier2)
      && (argTwoIdentifier == expectedIdentifier1 || argTwoIdentifier == expectedIdentifier2))) {
      SemanticErrorLog.add(s"Expected input types ${expectedIdentifier1.getKey} or ${expectedIdentifier2.getKey}" +
        s" but got ${argOneIdentifier.getKey} and ${argTwoIdentifier.getKey} instead.")
    }
  }

  def binaryCheckerHelper(argOne: ExprNode, argTwo: ExprNode, expectedIdentifier1: IDENTIFIER,
                          expectedIdentifier2: IDENTIFIER, topSymbolTable: SymbolTable, ST: SymbolTable): Unit = {
    val argOneIdentifier: IDENTIFIER = argOne.getType(topSymbolTable, currentSymbolTable)
    val argTwoIdentifier: IDENTIFIER = argTwo.getType(topSymbolTable, currentSymbolTable)
    visit(argOne)
    visit(argTwo)
    if (!(argOneIdentifier == expectedIdentifier1 && argTwoIdentifier == expectedIdentifier2)) {
      SemanticErrorLog.add(s"Expected input types ${expectedIdentifier1.getKey} and ${expectedIdentifier2.getKey}" +
        s" but got ${argOneIdentifier.getKey} and ${argTwoIdentifier.getKey} instead.")
    }
  }

  def arrayElemCheckerHelper(identNode: IdentNode, exprNodes: IndexedSeq[ExprNode]): Unit = {
    visit(identNode)
    val identIdentifier: IDENTIFIER = identNode.getType(topSymbolTable, currentSymbolTable)
    for (expr <- exprNodes) visit(expr)
    if (!identIdentifier.isInstanceOf[ARRAY]) {
      SemanticErrorLog.add(s"Expected array type but got ${identIdentifier.getKey} instead.")
    } else {
      val identArrayType: IDENTIFIER = identIdentifier.asInstanceOf[ARRAY]._type
      for (expr <- exprNodes) {
        val exprIdentifier: IDENTIFIER = expr.getType(topSymbolTable, currentSymbolTable)
        if (exprIdentifier != identArrayType) {
          SemanticErrorLog.add(s"Expected ${identArrayType.getKey} but got ${exprIdentifier.getKey} instead.")
        }
      }
    }
  }

  def conditionCheckerHelper(conditionExpr: ExprNode): Unit = {
    visit(conditionExpr)
    val conditionIdentifier = conditionExpr.getType(topSymbolTable, currentSymbolTable)

    if (conditionIdentifier != BoolTypeNode.getType(topSymbolTable, currentSymbolTable)) {
      SemanticErrorLog.add(s"${conditionExpr.getKey} must evaluate to a boolean.")
    }
  }

  def pairElemCheckerHelper(pairElemNode: PairElemNode): Unit = pairElemNode match {
    case FstNode(expression) => pairElemNodeVisit(expression)
    case SndNode(expression) => pairElemNodeVisit(expression)
  }

  def exprNodeCheckerHelper(expr: ExprNode): Unit = expr match {
    case ident: IdentNode => visit(ident)
    case ArrayElemNode(identNode, exprNodes) => arrayElemCheckerHelper(identNode, exprNodes)

    case unary: UnaryOperationNode => unary match {

      case LogicalNotNode(expr: ExprNode) => checkHelper(expr, "bool", topSymbolTable, currentSymbolTable)
      case NegateNode(expr: ExprNode) => checkHelper(expr, "int", topSymbolTable, currentSymbolTable)
      case LenNode(expr: ExprNode) => lenHelper(expr, topSymbolTable, currentSymbolTable)
      case OrdNode(expr: ExprNode) => checkHelper(expr, "char", topSymbolTable, currentSymbolTable)
      case ChrNode(expr: ExprNode) => checkHelper(expr, "int", topSymbolTable, currentSymbolTable)

    }

    case binary: BinaryOperationNode =>

      val intIdentifier: IDENTIFIER = IntTypeNode.getType(topSymbolTable, currentSymbolTable)
      val boolIdentifier: IDENTIFIER = BoolTypeNode.getType(topSymbolTable, currentSymbolTable)
      val charIdentifier: IDENTIFIER = CharTypeNode.getType(topSymbolTable, currentSymbolTable)
      binary match {
        case MultiplyNode(argOne, argTwo) => binaryCheckerHelper(argOne, argTwo, intIdentifier, intIdentifier, topSymbolTable, currentSymbolTable)
        case DivideNode(argOne, argTwo) => binaryCheckerHelper(argOne, argTwo, intIdentifier, intIdentifier, topSymbolTable, currentSymbolTable)
        case ModNode(argOne, argTwo) => binaryCheckerHelper(argOne, argTwo, intIdentifier, intIdentifier, topSymbolTable, currentSymbolTable)
        case PlusNode(argOne, argTwo) => binaryCheckerHelper(argOne, argTwo, intIdentifier, intIdentifier, topSymbolTable, currentSymbolTable)
        case MinusNode(argOne, argTwo) => binaryCheckerHelper(argOne, argTwo, intIdentifier, intIdentifier, topSymbolTable, currentSymbolTable)
        case GreaterThanNode(argOne, argTwo) => comparatorsCheckerHelper(argOne, argTwo, intIdentifier, charIdentifier, topSymbolTable, currentSymbolTable)
        case GreaterEqualNode(argOne, argTwo) => comparatorsCheckerHelper(argOne, argTwo, intIdentifier, charIdentifier, topSymbolTable, currentSymbolTable)
        case LessThanNode(argOne, argTwo) => comparatorsCheckerHelper(argOne, argTwo, intIdentifier, charIdentifier, topSymbolTable, currentSymbolTable)
        case LessEqualNode(argOne, argTwo) => comparatorsCheckerHelper(argOne, argTwo, intIdentifier, charIdentifier, topSymbolTable, currentSymbolTable)
        case EqualToNode(argOne, argTwo) => binaryCheckerHelper(argOne, argTwo,
          argOne.getType(topSymbolTable, currentSymbolTable), argOne.getType(topSymbolTable, currentSymbolTable), topSymbolTable, currentSymbolTable)
        case NotEqualNode(argOne, argTwo) => binaryCheckerHelper(argOne, argTwo,
          argOne.getType(topSymbolTable, currentSymbolTable), argOne.getType(topSymbolTable, currentSymbolTable), topSymbolTable, currentSymbolTable)
        case LogicalAndNode(argOne, argTwo) => binaryCheckerHelper(argOne, argTwo, boolIdentifier, boolIdentifier, topSymbolTable, currentSymbolTable)
        case LogicalOrNode(argOne, argTwo) => binaryCheckerHelper(argOne, argTwo, boolIdentifier, boolIdentifier, topSymbolTable, currentSymbolTable)
      }
    // Literals
    case Int_literNode(_) =>
    case Bool_literNode(_) =>
    case Char_literNode(_) =>
    case Str_literNode(_) =>
    case Pair_literNode =>
  }
}

