package ast

import org.antlr.v4.runtime.Token
import util.{SemanticErrorLog, SyntaxErrorLog}
import scala.collection.immutable.HashMap

sealed class TypeCheckVisitor(entryNode: ASTNode) extends Visitor(entryNode) {
  var topSymbolTable: SymbolTable = SymbolTable.topLevelSymbolTable(entryNode)
  var currentSymbolTable: SymbolTable = topSymbolTable
  var currentFuncReturnType: TYPE = null

  def getPos(token: Token): String = s"at ${token.getLine}:${token.getCharPositionInLine()}"

  override def visit(ASTNode: ASTNode): Unit = ASTNode match {

    // AST NODES

    case ProgramNode(token: Token, functions, stat) =>
      for (functionNode <- functions) visit(functionNode)
/*      for (functionNode <- functions) {
        if (functionReturnsOrExits(functionNode.stat)) {
          // Add to syntax error log.
          SyntaxErrorLog.add(s"Function ${functionNode.identNode.getKey} does not return or exit")
        }
        visit(functionNode)
      }
      if (functionReturnsOrExits(stat)) {
        SyntaxErrorLog.add(s"${getPos(token)} Program statement does not return or exit.")
      }*/
      symbolTableCreatorWrapper(_ => visit(stat))

    case FuncNode(token: Token, funcType, identNode, paramList: Option[ParamListNode], stat: StatNode) =>
      visit(funcType)
      var functionIdentifier: FUNCTION = null
      // check identNode is already defined
      if (currentSymbolTable.lookupFun(identNode.getKey).isDefined)
        SemanticErrorLog.add(s"${getPos(token)} tried to define function: ${identNode.getKey} but it was already declared.")
      else {
        // Save the func return type for current scope
        var saveFuncReturnType: TYPE = currentFuncReturnType
        // Set new func return type for new scope
        currentFuncReturnType = funcType.getType(topSymbolTable, currentSymbolTable)
        functionIdentifier = new FUNCTION(identNode.getKey, funcType.getType(topSymbolTable, currentSymbolTable).asInstanceOf[TYPE], paramTypes = null)
        currentSymbolTable.add(identNode.getKey, functionIdentifier)

        symbolTableCreatorWrapper(_ => {
          // Missing: link symbol table to function?
          if (paramList.isDefined) {
            // implicitly adds identifiers to the symbol table
            functionIdentifier.paramTypes = paramList.get.getIdentifierList(topSymbolTable, currentSymbolTable)
            visit(paramList.get)
          }
          visit(stat)
        })
        // Restore func return type of scope
        currentFuncReturnType = saveFuncReturnType
      }

    case ParamListNode(token: Token, paramNodeList) => for (paramNode <- paramNodeList) visit(paramNode)

    case ParamNode(token: Token, paramType, identNode) =>
      visit(paramType)
      val paramIdentifier: Option[IDENTIFIER] = currentSymbolTable.lookup(identNode.getKey)
      if (! (paramIdentifier.isDefined && paramIdentifier.get.isInstanceOf[PARAM]) ) {
        SemanticErrorLog.add(s"${getPos(token)} expected ${identNode.getKey} to refer to a parameter but it does not.")
      }

    case statNode: StatNode => statNode match {

      // STAT NODES

      case _: SkipNode =>

      case DeclarationNode(token: Token, _type, ident, rhs) =>
        val typeIdentifier: IDENTIFIER = _type.getType(topSymbolTable, currentSymbolTable)
        visit(rhs)

        // If the type and the rhs don't match, throw exception
        val rhsType = rhs.getType(topSymbolTable, currentSymbolTable)
        // If the rhs could not have its type evaluated, do not attempt to compare them
        if (rhsType == null) {
        // If types are not the same, or the rhs is not a general identifier for pair and array respectively
        } else if (! (typeIdentifier == rhsType ||
          typeIdentifier.isInstanceOf[PAIR] && rhsType == GENERAL_PAIR ||
          typeIdentifier.isInstanceOf[ARRAY] && rhsType == GENERAL_ARRAY)) {

          SemanticErrorLog.add(s"${getPos(token)} declaration for ${ident.getKey} failed, expected type ${typeIdentifier.getKey} " +
            s"but got type ${rhs.getType(topSymbolTable, currentSymbolTable).getKey} instead.")
        }
        if (currentSymbolTable.lookup(ident.getKey).isDefined) {
          // If variable is already defined log error
          SemanticErrorLog.add(s"${getPos(token)} declaration failed, ${ident.getKey} has already been declared.")
        } else {
          currentSymbolTable.add(ident.getKey, new VARIABLE(ident.getKey, typeIdentifier.asInstanceOf[TYPE]))
        }

      case AssignmentNode(token: Token, lhs, rhs) =>
        visit(lhs)
        visit(rhs)
        val lhsType = lhs.getType(topSymbolTable, currentSymbolTable)
        val rhsType = rhs.getType(topSymbolTable, currentSymbolTable)

        // If either side evaluated to an incorrect expression, stop checking
        if (lhsType == null || rhsType == null) {

        } else if (! (lhsType == rhsType ||
          lhsType.isInstanceOf[PAIR] && rhsType == GENERAL_PAIR ||
          lhsType.isInstanceOf[ARRAY] && rhsType == GENERAL_ARRAY)) {

          SemanticErrorLog.add(s"${getPos(token)} Assignment for ${lhs.getKey} to ${rhs.getKey} failed, " +
            s"expected type ${lhsType.getKey} "
            + s"but got type ${rhsType.getKey} instead.")
        }

      case ReadNode(token: Token, lhs) =>
        visit(lhs)

        if (!(lhs.getType(topSymbolTable, currentSymbolTable) == new IntTypeNode(null).getType(topSymbolTable, currentSymbolTable)
          || lhs.getType(topSymbolTable, currentSymbolTable) == new CharTypeNode(null).getType(topSymbolTable, currentSymbolTable))) {
          SemanticErrorLog.add(s"${getPos(token)} cannot read ${lhs.getKey}, it must be either a character or an integer.")
        }

      case FreeNode(token: Token, expr) =>
        visit(expr)

        val exprIdentifier = expr.getType(topSymbolTable, currentSymbolTable)

        if (!(exprIdentifier.isInstanceOf[PAIR] || exprIdentifier == GENERAL_PAIR ||
          exprIdentifier.isInstanceOf[ARRAY])) {
          SemanticErrorLog.add(s"${getPos(token)} cannot free ${expr.getKey}, it must be a pair or an array.")
        }

      case ReturnNode(token: Token, expr) => {
        visit(expr)
        val exprType = expr.getType(topSymbolTable, currentSymbolTable)
        if (currentFuncReturnType == null) {
          SemanticErrorLog.add(s"${getPos(token)} trying to global return on ${expr.toString}")
        } else if (exprType != null && exprType != currentFuncReturnType) {
          SemanticErrorLog.add(s"${getPos(token)} expected retun type ${currentFuncReturnType.getKey} but got ${exprType.getKey}")
        }
      }

      case ExitNode(token: Token, expr) => visit(expr)

        val exprIdentifier = expr.getType(topSymbolTable, currentSymbolTable)

        if (!(exprIdentifier == new IntTypeNode(null).getType(topSymbolTable, currentSymbolTable))) {
          SemanticErrorLog.add(s"${getPos(token)} cannot exit with ${exprIdentifier.getKey}, it must be an integer.")
        }

      case PrintNode(token: Token, expr) => visit(expr)

      case PrintlnNode(token: Token, expr) => visit(expr)

      case IfNode(token: Token, conditionExpr, thenStat, elseStat) =>
        conditionCheckerHelper(token, conditionExpr)

        symbolTableCreatorWrapper(_ => visit(thenStat))
        symbolTableCreatorWrapper(_ => visit(elseStat))

      case WhileNode(token: Token, expr, stat) =>
        conditionCheckerHelper(token, expr)
        // Prepare to visit stat by creating new symbol table
        symbolTableCreatorWrapper(_ => visit(stat))

      case BeginNode(token: Token, stat) => symbolTableCreatorWrapper(_ => visit(stat))

      case SequenceNode(token: Token, statOne, statTwo) =>
        // TODO optimise to halve visits
        visit(statOne)
        visit(statTwo)
        if (statOne.isInstanceOf[ReturnNode]) {
          SemanticErrorLog.add(s"${getPos(token)} return and exit statements may only be the last statement in a block")
        }
    }

    // AssignLHSNodes

    case assignLHSNode: AssignLHSNode => assignLHSNode match {

      case IdentNode(token: Token, ident) =>
        if (currentSymbolTable.lookupAll(assignLHSNode.getKey).isEmpty) {
          SemanticErrorLog.add(s"${getPos(token)} $ident has not been declared as an identifier.")
        }

      case ArrayElemNode(token: Token, identNode, exprNodes) => arrayElemCheckerHelper(token, identNode, exprNodes)

      case pairElemNode: PairElemNode => pairElemCheckerHelper(pairElemNode)
    }

    // AssignRHSNodes

    case assignRHSNode: AssignRHSNode => assignRHSNode match {
      case exprNode: ExprNode => exprNodeCheckerHelper(exprNode)
      case ArrayLiteralNode(token: Token, exprNodes) =>
        if (exprNodes.nonEmpty) {
          val firstIdentifier: IDENTIFIER = exprNodes.apply(0).getType(topSymbolTable, currentSymbolTable)
          for (expr <- exprNodes) {
            val exprIdentifier = expr.getType(topSymbolTable, currentSymbolTable)
            if (exprIdentifier != firstIdentifier) {
              SemanticErrorLog.add(s"${getPos(token)} expected type ${firstIdentifier.getKey} but got ${exprIdentifier.getKey}.")
            }
          }
        }
      case NewPairNode(token: Token, fstElem, sndElem) =>
        visit(fstElem)
        visit(sndElem)
      case CallNode(token: Token, identNode, argList) =>
        val funcIdentifier: Option[FUNCTION] = currentSymbolTable.lookupFunAll(identNode.getKey)
        if (funcIdentifier.isEmpty)
          SemanticErrorLog.add(s"${getPos(token)} function ${identNode.getKey} not declared.")
        else if (argList.isDefined && funcIdentifier.get.paramTypes.length != argList.get.exprNodes.length){
          SemanticErrorLog.add(s"${getPos(token)} function: ${identNode.getKey} expected ${funcIdentifier.get.paramTypes.length} " +
            s"arguments but got ${argList.get.exprNodes.length} arguments instead.")
        } else if (argList.isDefined){
          visit(argList.get)
          for (argIndex <- argList.get.exprNodes.indices) {
            val argType: TYPE = argList.get.exprNodes.apply(argIndex).getType(topSymbolTable, currentSymbolTable)
            val paramType: TYPE = funcIdentifier.get.paramTypes.apply(argIndex)
            if (argType != paramType) {
              SemanticErrorLog.add(s"${getPos(token)} expected type ${paramType.getKey} but got ${argType.getKey}.")
            }
          }
          // funcObj = F in slides???
        }
      case pairElemNode: PairElemNode => pairElemCheckerHelper(pairElemNode)
    }

    case ArgListNode(token: Token, exprNodes) => for (exprNode <- exprNodes) visit(exprNode)

    case typeNode: TypeNode => typeNode match {
      case _: BaseTypeNode => // Always true
      case ArrayTypeNode(token: Token, arrayTypeNode) => visit(arrayTypeNode)
      case PairTypeNode(token: Token, firstPairElem, secondPairElem) =>
        visit(firstPairElem)
        visit(secondPairElem)
    }

    case pairElemType: PairElemTypeNode => pairElemType match {
      // case ArrayTypeNode(typeNode) =>
      // case node: BaseTypeNode =>
      case _: PairElemTypePairNode => // base pair always true
    }
  }

  def pairElemNodeVisit(token: Token, expr: ExprNode): Unit = {
    val pairIdentifier: IDENTIFIER = expr.getType(topSymbolTable, currentSymbolTable)
    if (! pairIdentifier.isInstanceOf[PAIR]) {
      SemanticErrorLog.add(s"${getPos(token)} expected pair type but got $pairIdentifier.")
    } else if (pairIdentifier == GENERAL_PAIR) {
      SemanticErrorLog.add(s"${getPos(token)} expected pair type but got null.")
    } else {
      visit(expr)
    }
  }

  // Unary Operator Helpers
  def unaryCheckerHelper(token: Token, expr: ExprNode, expectedIdentifier: IDENTIFIER, topSymbolTable: SymbolTable, ST: SymbolTable): Unit = {
    visit(expr)
    val identifier: IDENTIFIER = expr.getType(topSymbolTable, currentSymbolTable)
    if (identifier != expectedIdentifier) {
      SemanticErrorLog.add(s"${getPos(token)} Expected $expectedIdentifier but got $identifier.")
    }
  }

  def lenHelper(token: Token,expr: ExprNode, topSymbolTable: SymbolTable, ST: SymbolTable): Unit = {
    val identifier: IDENTIFIER = expr.getType(topSymbolTable, currentSymbolTable)
    if (!identifier.isInstanceOf[ARRAY]) {
      SemanticErrorLog.add(s"${getPos(token)} expected an array but got $identifier.")
    }
  }

  // Binary Operator Helpers
  def comparatorsCheckerHelper(token: Token, argOne: ExprNode, argTwo: ExprNode,
                               expectedIdentifier1: IDENTIFIER, expectedIdentifier2: IDENTIFIER, topSymbolTable: SymbolTable, ST: SymbolTable): Unit = {
    val argOneIdentifier: IDENTIFIER = argOne.getType(topSymbolTable, currentSymbolTable)
    val argTwoIdentifier: IDENTIFIER = argTwo.getType(topSymbolTable, currentSymbolTable)
    if (!((argOneIdentifier == expectedIdentifier1 || argOneIdentifier == expectedIdentifier2)
      && (argTwoIdentifier == expectedIdentifier1 || argTwoIdentifier == expectedIdentifier2))) {
      SemanticErrorLog.add(s"${getPos(token)} expected input types ${expectedIdentifier1.getKey} or ${expectedIdentifier2.getKey}" +
        s" but got ${argOneIdentifier.getKey} and ${argTwoIdentifier.getKey} instead.")
    }
  }

  def binaryCheckerHelper(token: Token, argOne: ExprNode, argTwo: ExprNode, expectedIdentifier1: IDENTIFIER,
                          expectedIdentifier2: IDENTIFIER, topSymbolTable: SymbolTable, ST: SymbolTable): Unit = {
    visit(argOne)
    visit(argTwo)
    val argOneIdentifier: IDENTIFIER = argOne.getType(topSymbolTable, currentSymbolTable)
    val argTwoIdentifier: IDENTIFIER = argTwo.getType(topSymbolTable, currentSymbolTable)
    if (argOneIdentifier == null || argTwoIdentifier == null){
      // If either identifier is null, dont check if they're equal to expected
    } else if (expectedIdentifier1.isInstanceOf[PAIR] && expectedIdentifier2.isInstanceOf[PAIR]
    && argOneIdentifier.isInstanceOf[PAIR] && argTwoIdentifier.isInstanceOf[PAIR]) {
      // If all required areguments are of type pair then it's ok
    } else if (expectedIdentifier1.isInstanceOf[ARRAY] && expectedIdentifier2.isInstanceOf[ARRAY]
      && argOneIdentifier.isInstanceOf[ARRAY] && argTwoIdentifier.isInstanceOf[ARRAY]) {
      // If all required arguments are of type array then it's ok
    } else if (!(argOneIdentifier == expectedIdentifier1 && argTwoIdentifier == expectedIdentifier2)) {
      SemanticErrorLog.add(s"${getPos(token)} expected input types ${expectedIdentifier1.getKey} and ${expectedIdentifier2.getKey}" +
        s" but got ${argOneIdentifier.getKey} and ${argTwoIdentifier.getKey} instead.")
    }
  }

  def arrayElemCheckerHelper(token: Token, identNode: IdentNode, exprNodes: IndexedSeq[ExprNode]): Unit = {
    // Check identifier has been defined
    visit(identNode)
    val identIdentifier: TYPE = identNode.getType(topSymbolTable, currentSymbolTable)
    // Check all indices evaluate to any type
    for (expr <- exprNodes) visit(expr)
    // Check ident type is an array
    if (!identIdentifier.isInstanceOf[ARRAY]) {
      SemanticErrorLog.add(s"${getPos(token)} expected array type for ${identNode.toString} but got ${identIdentifier.getKey} instead.")
    } else {
      // Check that number of depths is valid
      var currentDepthType: TYPE = identIdentifier
      for (_ <- exprNodes.indices) {
        if (! currentDepthType.isInstanceOf[ARRAY]){
          SemanticErrorLog.add(s"Trying to access lower dimensions but ${identNode.toString}" +
            s" has less than ${exprNodes.length} dimensions")
        }
        currentDepthType = currentDepthType.asInstanceOf[ARRAY]._type
      }
      // Check if all expressions evaluate to an int
      for (expr <- exprNodes) {
        val exprIdentifier: TYPE = expr.getType(topSymbolTable, currentSymbolTable)
        // TODO: refactor
        if (exprIdentifier != new IntTypeNode(null).getType(topSymbolTable, currentSymbolTable)) {
          SemanticErrorLog.add(s"${getPos(token)} expected index value but got ${exprIdentifier.getKey} instead.")
        }
      }
    }
  }

  def conditionCheckerHelper(token: Token, conditionExpr: ExprNode): Unit = {
    visit(conditionExpr)
    val conditionIdentifier = conditionExpr.getType(topSymbolTable, currentSymbolTable)

    // TODO: refactor
    if (conditionIdentifier != new BoolTypeNode(null).getType(topSymbolTable, currentSymbolTable)) {
      SemanticErrorLog.add(s"${getPos(token)} ${conditionExpr.getKey} must evaluate to a boolean.")
    }
  }

  def pairElemCheckerHelper(pairElemNode: PairElemNode): Unit = pairElemNode match {
    case FstNode(token, expression) => pairElemNodeVisit(token, expression)
    case SndNode(token, expression) => pairElemNodeVisit(token, expression)
  }

  def exprNodeCheckerHelper(expr: ExprNode): Unit = expr match {
    case ident: IdentNode => visit(ident)
    case ArrayElemNode(token: Token, identNode, exprNodes) => arrayElemCheckerHelper(token, identNode, exprNodes)

    case unary: UnaryOperationNode => unary match {

      case LogicalNotNode(token: Token, expr: ExprNode) => unaryCheckerHelper(token, expr, new BoolTypeNode(null).getType(topSymbolTable, currentSymbolTable), topSymbolTable, currentSymbolTable)
      case NegateNode(token: Token, expr: ExprNode) => unaryCheckerHelper(token, expr, new IntTypeNode(null).getType(topSymbolTable, currentSymbolTable), topSymbolTable, currentSymbolTable)
      case LenNode(token: Token, expr: ExprNode) => lenHelper(token, expr, topSymbolTable, currentSymbolTable)
      case OrdNode(token: Token, expr: ExprNode) => unaryCheckerHelper(token, expr, new CharTypeNode(null).getType(topSymbolTable, currentSymbolTable), topSymbolTable, currentSymbolTable)
      case ChrNode(token: Token, expr: ExprNode) => unaryCheckerHelper(token, expr, new IntTypeNode(null).getType(topSymbolTable, currentSymbolTable), topSymbolTable, currentSymbolTable)

    }

    case binary: BinaryOperationNode =>

      val intIdentifier: IDENTIFIER = new IntTypeNode(null).getType(topSymbolTable, currentSymbolTable)
      val boolIdentifier: IDENTIFIER = new BoolTypeNode(null).getType(topSymbolTable, currentSymbolTable)
      val charIdentifier: IDENTIFIER = new CharTypeNode(null).getType(topSymbolTable, currentSymbolTable)
      binary match {
        case MultiplyNode(token: Token, argOne, argTwo) => binaryCheckerHelper(token, argOne, argTwo, intIdentifier, intIdentifier, topSymbolTable, currentSymbolTable)
        case DivideNode(token: Token, argOne, argTwo) => binaryCheckerHelper(token, argOne, argTwo, intIdentifier, intIdentifier, topSymbolTable, currentSymbolTable)
        case ModNode(token: Token, argOne, argTwo) => binaryCheckerHelper(token, argOne, argTwo, intIdentifier, intIdentifier, topSymbolTable, currentSymbolTable)
        case PlusNode(token: Token, argOne, argTwo) => binaryCheckerHelper(token, argOne, argTwo, intIdentifier, intIdentifier, topSymbolTable, currentSymbolTable)
        case MinusNode(token: Token, argOne, argTwo) => binaryCheckerHelper(token, argOne, argTwo, intIdentifier, intIdentifier, topSymbolTable, currentSymbolTable)
        case GreaterThanNode(token: Token, argOne, argTwo) => comparatorsCheckerHelper(token, argOne, argTwo, intIdentifier, charIdentifier, topSymbolTable, currentSymbolTable)
        case GreaterEqualNode(token: Token, argOne, argTwo) => comparatorsCheckerHelper(token, argOne, argTwo, intIdentifier, charIdentifier, topSymbolTable, currentSymbolTable)
        case LessThanNode(token: Token, argOne, argTwo) => comparatorsCheckerHelper(token, argOne, argTwo, intIdentifier, charIdentifier, topSymbolTable, currentSymbolTable)
        case LessEqualNode(token: Token, argOne, argTwo) => comparatorsCheckerHelper(token, argOne, argTwo, intIdentifier, charIdentifier, topSymbolTable, currentSymbolTable)
        case EqualToNode(token: Token, argOne, argTwo) => binaryCheckerHelper(token, argOne, argTwo,
          argOne.getType(topSymbolTable, currentSymbolTable), argOne.getType(topSymbolTable, currentSymbolTable), topSymbolTable, currentSymbolTable)
        case NotEqualNode(token: Token, argOne, argTwo) => binaryCheckerHelper(token, argOne, argTwo,
          argOne.getType(topSymbolTable, currentSymbolTable), argOne.getType(topSymbolTable, currentSymbolTable), topSymbolTable, currentSymbolTable)
        case LogicalAndNode(token: Token, argOne, argTwo) => binaryCheckerHelper(token, argOne, argTwo, boolIdentifier, boolIdentifier, topSymbolTable, currentSymbolTable)
        case LogicalOrNode(token: Token, argOne, argTwo) => binaryCheckerHelper(token, argOne, argTwo, boolIdentifier, boolIdentifier, topSymbolTable, currentSymbolTable)
      }
    // Literals
    case Int_literNode(_, _) =>
    case Bool_literNode(_, _) =>
    case Char_literNode(_, _) =>
    case Str_literNode(_, _) =>
    case Pair_literNode(_) =>
  }

  def symbolTableCreatorWrapper(contents: Unit => Unit): Unit = {
    // Prepare to visit stat by creating new symbol table
    currentSymbolTable = SymbolTable.newSymbolTable(currentSymbolTable)
    contents.apply()
    // Exit symbol table
    currentSymbolTable = currentSymbolTable.encSymbolTable

  }

  // Helper function to check that function has a return or an exit, otherwise syntax error.
  def functionReturnsOrExits(statNode: StatNode): Boolean = {
    statNode match {
      case exitNode: ExitNode =>
        true
      case returnNode: ReturnNode =>
        true
      case ifNode: IfNode =>
        functionReturnsOrExits(ifNode.thenStat) && functionReturnsOrExits(ifNode.elseStat)
      case whileNode: WhileNode =>
        functionReturnsOrExits(whileNode.stat)
      case beginNode: BeginNode =>
        functionReturnsOrExits(beginNode.stat)
      case sequenceNode: SequenceNode =>
        functionReturnsOrExits(sequenceNode.statOne) && functionReturnsOrExits(sequenceNode.statTwo)
      case _ =>
        false
    }
  }

}

