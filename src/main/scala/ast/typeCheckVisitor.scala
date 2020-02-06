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
      if (currentSymbolTable.lookup(identNode.getKey).isDefined){
        throw new TypeException("function " + identNode.getKey + " has already been defined")
      } else {
        paramList match {
          case Some(params) => currentSymbolTable.add(identNode.getKey,
            new FUNCTION(identNode.getKey, typeIdentifier, params.getIdentifierList(topSymbolTable, currentSymbolTable)))
          case None => currentSymbolTable.add(identNode.getKey, new FUNCTION(identNode.getKey, typeIdentifier, IndexedSeq[TYPE]()))
        }
      }

    case ParamListNode(paramList) => for (paramNode <- paramList) visit(paramNode)
      // TODO not sure if this needs to visit the idents
    case ParamNode(paramType, identNode) => visit(paramType)

    case node: StatNode => node match {

        // STAT NODES

      case DeclarationNode(_type, ident, rhs) =>
        val typeIdentifier: IDENTIFIER = _type.getIdentifier(topSymbolTable, currentSymbolTable)
        rhs.check(topSymbolTable, currentSymbolTable)

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

        if (lhs.getIdentifier(topSymbolTable, currentSymbolTable) != rhs.getIdentifier(topSymbolTable, currentSymbolTable)){
          throw new TypeException(lhs.getKey + " and " + rhs.getKey + " have non-matching types")
        }
      }

      case ReadNode(lhs) =>
        visit(lhs)

        if (!(lhs.getIdentifier(topSymbolTable, currentSymbolTable) == IntTypeNode.getIdentifier(topSymbolTable, currentSymbolTable)
          || lhs.getIdentifier(topSymbolTable, currentSymbolTable) == CharTypeNode.getIdentifier(topSymbolTable, currentSymbolTable))) {
          throw new TypeException(s"Semantic Error: ${ lhs.getKey} must be either a character or an integer.")
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
          throw new TypeException(s"Semantic Error: ${ expr.getKey} must be an integer.")
        }

      case PrintNode(expr) => visit(expr)

      case PrintlnNode(expr) => visit(expr)

      case IfNode(conditionExpr, thenStat, elseStat) =>
        visit(conditionExpr)

        val conditionIdentifier = conditionExpr.getIdentifier(topSymbolTable, currentSymbolTable)

        if (!(conditionIdentifier == BoolTypeNode.getIdentifier(topSymbolTable, currentSymbolTable))) {
          throw new TypeException(s"Semantic Error: ${ conditionExpr.getKey} must evaluate to a boolean.")
        }

      case WhileNode(expr, stat) =>
        visit(expr)

        val conditionIdentifier = expr.getIdentifier(topSymbolTable, currentSymbolTable)

        if (!(conditionIdentifier == BoolTypeNode.getIdentifier(topSymbolTable, currentSymbolTable))) {
          throw new TypeException(s"Semantic Error: ${ expr.getKey} must evaluate to a boolean.")
        }

      case BeginNode(stat) => visit(stat)

      case SequenceNode(statOne, statTwo) =>
        visit(statOne)
        visit(statTwo)
    }

      // AssignLHSNodes

    case assignLHSNode: AssignLHSNode => assignLHSNode match {

      case IdentNode(ident) =>
        if (currentSymbolTable.lookupAll(assignLHSNode.getKey).isEmpty){
          throw new TypeException(s"$toString has not been declared")
        }

      case ArrayElemNode(identNode, exprNodes) =>
        visit(identNode)
        val identIdentifier: IDENTIFIER = identNode.getIdentifier(topSymbolTable, currentSymbolTable)
        for (expr <- exprNodes) visit(expr)
        if (! identIdentifier.isInstanceOf[ARRAY]) {
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

      case pairElemNode: PairElemNode => pairElemNode match {
        case FstNode(expr) => // TODO
        case SndNode(expr) => // TODO
      }
    }

      // AssignRHSNodes

    case assignRHSNode: AssignRHSNode => assignRHSNode match {
      case exprNode: ExprNode => visit(exprNode)
      case NewPairNode(fstElem, sndElem) =>
      case CallNode(identNode, argList) =>
      case pairElemNode: PairElemNode =>
      case ArrayLiteralNode(exprNodes) =>
    }
  }
}

