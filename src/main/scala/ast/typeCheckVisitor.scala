package ast

import org.stringtemplate.v4.ST

import scala.collection.immutable.HashMap

sealed class typeCheckVisitor(entryNode: ASTNode) extends Visitor(entryNode) {
  var topSymbolTable: SymbolTable = SymbolTable.topLevelSymbolTable(entryNode)
  var currentSymbolTable: SymbolTable = new SymbolTable(new HashMap(), topSymbolTable)

  override def visit(ASTNode: ASTNode): Unit = ASTNode match {

    // AST NODES

    case ProgramNode(functions, stat) =>
      for (functionNode <- functions) visit(functionNode)
      visit(stat)

    case FuncNode(funcType, identNode, paramList, stat) =>
      val typeIdentifier: TYPE = funcType.getIdentifier(topSymbolTable, currentSymbolTable).asInstanceOf[TYPE]
      if (currentSymbolTable.lookup(identNode.getKey).isDefined) {
        throw new TypeException("function " + identNode.getKey + " has already been defined")
      } else {
        paramList match {
          case Some(params) => currentSymbolTable.add(identNode.getKey,
            new FUNCTION(identNode.getKey, typeIdentifier, params.getIdentifierList(topSymbolTable, currentSymbolTable)))
          case None => currentSymbolTable.add(identNode.getKey, new FUNCTION(identNode.getKey, typeIdentifier, IndexedSeq[TYPE]()))
        }
      }

    case ParamListNode(paramLicurrentSymbolTable) => for (paramNode <- paramLicurrentSymbolTable) visit(paramNode)
    // TODO not sure if this needs to visit the idents
    case ParamNode(paramType, _) => visit(paramType)

    case node: StatNode => node match {

      // STAT NODES

      case DeclarationNode(_type, ident, rhs) =>
        val typeIdentifier: IDENTIFIER = _type.getIdentifier(topSymbolTable, currentSymbolTable)
        visit(rhs)

        // If the type and the rhs dont match, throw exception
        if (typeIdentifier != rhs.getIdentifier(topSymbolTable, currentSymbolTable)) {
          throw new TypeException(typeIdentifier.getKey + " expected but got " + rhs.getIdentifier(topSymbolTable, currentSymbolTable).getKey)
        } else if (currentSymbolTable.lookup(ident.getKey).isDefined) {
          // If variable is already defined throw exception
          throw new TypeException(s"${ident.getKey} has already been declared")
        } else {
          currentSymbolTable.add(ident.getKey, new VARIABLE(ident.getKey, typeIdentifier.asInstanceOf[TYPE]))
        }

      case AssignmentNode(lhs, rhs) => {
        visit(lhs)
        visit(rhs)

        if (lhs.getIdentifier(topSymbolTable, currentSymbolTable) != rhs.getIdentifier(topSymbolTable, currentSymbolTable)) {
          throw new TypeException(lhs.getKey + " and " + rhs.getKey + " have non-matching types")
        }
      }

      case ReadNode(lhs) =>
        visit(lhs)

        if (!(lhs.getIdentifier(topSymbolTable, currentSymbolTable) == IntTypeNode.getIdentifier(topSymbolTable, currentSymbolTable)
          || lhs.getIdentifier(topSymbolTable, currentSymbolTable) == CharTypeNode.getIdentifier(topSymbolTable, currentSymbolTable))) {
          throw new TypeException(s"Semantic Error: ${lhs.getKey} must be either a character or an integer.")
        }

      case FreeNode(expr) =>
        visit(expr)

        val exprIdentifier = expr.getIdentifier(topSymbolTable, currentSymbolTable)

        if (!(exprIdentifier.isInstanceOf[PAIR] || exprIdentifier == GENERAL_PAIR) ||
          !exprIdentifier.isInstanceOf[ARRAY]) {
          throw new TypeException(s"Semantic Error: ${expr.getKey} must be a pair or an array.")
        }

      // TODO: Check that return statement is present in body of non-main function.
      // TODO: Check that the type of expression given to the return statement must
      // TODO: match the return type of the expression.

      case ReturnNode(expr) => visit(expr)

      case ExitNode(expr) => visit(expr)

        val exprIdentifier = expr.getIdentifier(topSymbolTable, currentSymbolTable)

        if (!(expr.getIdentifier(topSymbolTable, currentSymbolTable) == IntTypeNode.getIdentifier(topSymbolTable, currentSymbolTable))) {
          throw new TypeException(s"Semantic Error: ${expr.getKey} must be an integer.")
        }

      case PrintNode(expr) => visit(expr)

      case PrintlnNode(expr) => visit(expr)

      case IfNode(conditionExpr, thenStat, elseStat) =>
        visit(conditionExpr)

        val conditionIdentifier = conditionExpr.getIdentifier(topSymbolTable, currentSymbolTable)

        if (!(conditionIdentifier == BoolTypeNode.getIdentifier(topSymbolTable, currentSymbolTable))) {
          throw new TypeException(s"Semantic Error: ${conditionExpr.getKey} must evaluate to a boolean.")
        }

      case WhileNode(expr, stat) =>
        visit(expr)

        val conditionIdentifier = expr.getIdentifier(topSymbolTable, currentSymbolTable)

        if (!(conditionIdentifier == BoolTypeNode.getIdentifier(topSymbolTable, currentSymbolTable))) {
          throw new TypeException(s"Semantic Error: ${expr.getKey} must evaluate to a boolean.")
        }

      case BeginNode(stat) => visit(stat)

      case SequenceNode(statOne, statTwo) =>
        visit(statOne)
        visit(statTwo)
    }

    // AssignLHSNodes

    case assignLHSNode: AssignLHSNode => assignLHSNode match {

      case IdentNode(ident) =>
        if (currentSymbolTable.lookupAll(assignLHSNode.getKey).isEmpty) {
          throw new TypeException(s"$toString has not been declared")
        }

      case ArrayElemNode(identNode, exprNodes) =>
        visit(identNode)
        val identIdentifier: IDENTIFIER = identNode.getIdentifier(topSymbolTable, currentSymbolTable)
        for (expr <- exprNodes) visit(expr)
        if (!identIdentifier.isInstanceOf[ARRAY]) {
          throw new TypeException(s"Expected array type but got ${identIdentifier.getKey} instead.")
        } else {
          val identArrayType: IDENTIFIER = identIdentifier.asInstanceOf[ARRAY]._type
          for (expr <- exprNodes) {
            val exprIdentifier: IDENTIFIER = expr.getIdentifier(topSymbolTable, currentSymbolTable)
            if (exprIdentifier != identArrayType) {
              throw new TypeException(s"Expected ${identArrayType.getKey} but got ${exprIdentifier.getKey} instead.")
            }
          }
        }

      case pairElemNode: PairElemNode => visit(pairElemNode.asInstanceOf[ASTNode])
    }

    // AssignRHSNodes

    case assignRHSNode: AssignRHSNode => assignRHSNode match {
      // case exprNode: ExprNode =>
      case ArrayLiteralNode(exprNodes) =>
        val firstIdentifier: IDENTIFIER = exprNodes.apply(0).getIdentifier(topSymbolTable, currentSymbolTable)
        for (expr <- exprNodes) {
          val exprIdentifier = expr.getIdentifier(topSymbolTable, currentSymbolTable)
          if (exprIdentifier != firstIdentifier) {
            throw new TypeException(s"Expected type ${firstIdentifier.getKey} but got ${exprIdentifier.getKey}")
          }
        }
      case NewPairNode(fstElem, sndElem) =>
        visit(fstElem)
        visit(sndElem)
      case CallNode(identNode, argList) => // TODO
      // case pairElemNode: PairElemNode =>
    }

    case ArgListNode(exprNodes) => for (exprNode <- exprNodes) visit(exprNode)

    case pairElemNode: PairElemNode => pairElemNode match {
      case FstNode(expression) => pairElemNodeVisit(expression)
      case SndNode(expression) => pairElemNodeVisit(expression)
      case _ =>
    }

    case typeNode: TypeNode => typeNode match {
      case _: BaseTypeNode => // Always true
      case ArrayTypeNode(typeNode) => visit(typeNode)
      case PairTypeNode(firstPairElem, secondPairElem) =>
        visit(firstPairElem)
        visit(secondPairElem)
    }

    case pairElemType: PairElemTypeNode => pairElemType match {
      // case ArrayTypeNode(typeNode) =>
      // case node: BaseTypeNode =>
      case _: PairElemTypePairNode => // base pair always true
    }

    case expr: ExprNode => expr match {

      case ident: IdentNode => visit(ident)
      case ArrayElemNode(identNode, exprNodes) =>

        visit(identNode)
        val identIdentifier: IDENTIFIER = identNode.getIdentifier(topSymbolTable, currentSymbolTable)
        for (expr <- exprNodes) visit(expr)
        if (!identIdentifier.isInstanceOf[ARRAY]) {
          throw new TypeException(s"Expected array type but got ${identIdentifier.getKey} instead")
        } else {
          val identArrayType: IDENTIFIER = identIdentifier.asInstanceOf[ARRAY]._type
          for (expr <- exprNodes) {
            val exprIdentifier: IDENTIFIER = expr.getIdentifier(topSymbolTable, currentSymbolTable)
            if (exprIdentifier != identArrayType) {
              throw new TypeException(s"Expected ${identArrayType.getKey} but got ${exprIdentifier.getKey} instead")
            }
          }
        }

      case _: UnaryOperationNode =>

        case LogicalNotNode(expr: ExprNode) => checkHelper(expr, "bool", topSymbolTable, currentSymbolTable)
        case NegateNode(expr: ExprNode) => checkHelper(expr, "int", topSymbolTable, currentSymbolTable)
        case LenNode(expr: ExprNode) => lenHelper(expr, topSymbolTable, currentSymbolTable)
        case OrdNode(expr: ExprNode) => checkHelper(expr, "char", topSymbolTable, currentSymbolTable)
        case ChrNode(expr: ExprNode) => checkHelper(expr, "int", topSymbolTable, currentSymbolTable)

      case binary: BinaryOperationNode =>

        val intIdentifier: IDENTIFIER = IntTypeNode.getIdentifier(topSymbolTable, currentSymbolTable)
        val boolIdentifier: IDENTIFIER = BoolTypeNode.getIdentifier(topSymbolTable, currentSymbolTable)
        val charIdentifier: IDENTIFIER = CharTypeNode.getIdentifier(topSymbolTable, currentSymbolTable)
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
            argOne.getIdentifier(topSymbolTable, currentSymbolTable), argOne.getIdentifier(topSymbolTable, currentSymbolTable), topSymbolTable, currentSymbolTable)
          case NotEqualNode(argOne, argTwo) => binaryCheckerHelper(argOne, argTwo,
            argOne.getIdentifier(topSymbolTable, currentSymbolTable), argOne.getIdentifier(topSymbolTable, currentSymbolTable), topSymbolTable, currentSymbolTable)
          case LogicalAndNode(argOne, argTwo) => binaryCheckerHelper(argOne, argTwo, boolIdentifier, boolIdentifier, topSymbolTable, currentSymbolTable)
          case LogicalOrNode(argOne, argTwo) => binaryCheckerHelper(argOne, argTwo, boolIdentifier, boolIdentifier, topSymbolTable, currentSymbolTable)
        }

      case Int_literNode(num) =>
      case Bool_literNode(value) =>
      case Char_literNode(value) =>
      case Str_literNode(str) =>
      case Pair_literNode =>

      }
    }

    def pairElemNodeVisit(expr: ExprNode): Unit = {
      val pairIdentifier: IDENTIFIER = expr.getIdentifier(topSymbolTable, currentSymbolTable)
      if (! pairIdentifier.isInstanceOf[PAIR]) {
        throw new TypeException("Expected pair type but got " + pairIdentifier)
      } else {
        visit(expr)
      }
    }

    // Unary Operator Helpers
    def checkHelper(expr: ExprNode, expectedIdentifier: String, topSymbolTable: SymbolTable, ST: SymbolTable): Unit = {
      val identifier: IDENTIFIER = expr.getIdentifier(topSymbolTable, currentSymbolTable)
      if (identifier != topSymbolTable.lookup(expectedIdentifier).get) {
        throw new TypeException(s"Expected $expectedIdentifier but got $identifier")
      }
    }

    def lenHelper(expr: ExprNode, topSymbolTable: SymbolTable, ST: SymbolTable): Unit = {
      val identifier: IDENTIFIER = expr.getIdentifier(topSymbolTable, currentSymbolTable)
      if (!identifier.isInstanceOf[ARRAY]) {
        throw new TypeException("Expected an array but got " + identifier)
      }
    }

    // Binary Operator Helpers
    def comparatorsCheckerHelper(argOne: ExprNode, argTwo: ExprNode,
                                 expectedIdentifier1: IDENTIFIER, expectedIdentifier2: IDENTIFIER, topSymbolTable: SymbolTable, ST: SymbolTable): Unit = {
      val argOneIdentifier: IDENTIFIER = argOne.getIdentifier(topSymbolTable, currentSymbolTable)
      val argTwoIdentifier: IDENTIFIER = argTwo.getIdentifier(topSymbolTable, currentSymbolTable)
      if (!((argOneIdentifier == expectedIdentifier1 || argOneIdentifier == expectedIdentifier2)
        && (argTwoIdentifier == expectedIdentifier1 || argTwoIdentifier == expectedIdentifier2))) {
        throw new TypeException(s"Expected input types ${expectedIdentifier1.getKey} or ${expectedIdentifier2.getKey}" +
          s" but got ${argOneIdentifier.getKey} and ${argTwoIdentifier.getKey} instead")
      }
    }

    def binaryCheckerHelper(argOne: ExprNode, argTwo: ExprNode, expectedIdentifier1: IDENTIFIER,
                            expectedIdentifier2: IDENTIFIER, topSymbolTable: SymbolTable, ST: SymbolTable): Unit = {
      val argOneIdentifier: IDENTIFIER = argOne.getIdentifier(topSymbolTable, currentSymbolTable)
      val argTwoIdentifier: IDENTIFIER = argTwo.getIdentifier(topSymbolTable, currentSymbolTable)
      if (!(argOneIdentifier == expectedIdentifier1 && argTwoIdentifier == expectedIdentifier2)) {
        throw new TypeException(s"Expected input types ${expectedIdentifier1.getKey} and ${expectedIdentifier2.getKey}" +
          s" but got ${argOneIdentifier.getKey} and ${argTwoIdentifier.getKey} instead")
      }
    }
}

