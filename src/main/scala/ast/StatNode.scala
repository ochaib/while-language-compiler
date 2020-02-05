package ast

import ast._

abstract class StatNode extends ASTNode with Checkable {

}

class SkipNode extends StatNode {
  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {}
}

class DeclarationNode(val _type: TypeNode, val identNode: IdentNode, val _rhs: AssignRHSNode)
  extends StatNode {
  override def check(topST:SymbolTable, ST: SymbolTable): Unit = {
    val typeIdentifier: IDENTIFIER = _type.getIdentifier(topST, ST)
    _rhs.check(topST, ST)

    // If the type and the rhs dont match, throw exception
    if (typeIdentifier != _rhs.getIdentifier(topST, ST)) {
      throw new TypeException(typeIdentifier.getKey + " expected but got " + _rhs.getIdentifier(topST, ST).getKey)
    } else if (ST.lookup(identNode.identKey).isDefined) {
      // If variable is already defined throw exception
      throw new TypeException(identNode.identKey + " has already been declared")
    } else {
      ST.add(identNode.identKey, new VARIABLE(identNode.getKey, typeIdentifier.asInstanceOf[TYPE]))
    }
  }
}

class AssignmentNode(val _lhs: AssignLHSNode, val _rhs: AssignRHSNode) extends StatNode {

  val lhs: AssignLHSNode = _lhs
  val rhs: AssignRHSNode = _rhs

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    _lhs.check(topST, ST)
    _rhs.check(topST, ST)

    if (_lhs.getIdentifier(topST, ST) != _rhs.getIdentifier(topST, ST)){
      throw new TypeException(_lhs.getKey + " and " + _rhs.getKey + " have non-matching types")
    }
  }
}

class ReadNode(val _lhs: AssignLHSNode) extends StatNode {
  // Ensure the read statement can only handle character or integer input.

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {

    _lhs.check(topST, ST)

    if (!(_lhs.getIdentifier(topST, ST) == IntTypeNode.getIdentifier(topST, ST)
      || _lhs.getIdentifier(topST, ST) == CharTypeNode.getIdentifier(topST, ST))) {
      throw new TypeException(s"Semantic Error: ${ _lhs.getKey} must be either a character or an integer.")
    }
  }
}

class FreeNode(val _expr: ExprNode) extends StatNode {
  // Call check on the freeNode which should check that the expression in the freeNode
  // is either a pair or an array.

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    _expr.check(topST, ST)

    val exprIdentifier = _expr.getIdentifier(topST, ST)

    if (!(exprIdentifier.isInstanceOf[PAIR] || exprIdentifier == GENERAL_PAIR) ||
      !exprIdentifier.isInstanceOf[ARRAY]) {
      throw new TypeException(s"Semantic Error: ${ _expr.getKey} must be a pair or an array.")
    }
  }
}

class ReturnNode(val _expr: ExprNode) extends StatNode {
  // TODO: Check that return statement is present in body of non-main function.
  // TODO: Check that the type of expression given to the return statement must
  // TODO: match the return type of the expression.

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    _expr.check(topST, ST)
  }
}

class ExitNode(val _expr: ExprNode) extends StatNode {

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    _expr.check(topST, ST)

    val exprIdentifier = _expr.getIdentifier(topST, ST)

    if (!(_expr.getIdentifier(topST, ST) == IntTypeNode.getIdentifier(topST, ST))) {
      throw new TypeException(s"Semantic Error: ${ _expr.getKey} must be an integer.")
    }
  }
}

class PrintNode(val _expr: ExprNode) extends StatNode {

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    _expr.check(topST, ST)
  }

}

class PrintlnNode(val _expr: ExprNode) extends StatNode {
  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    _expr.check(topST, ST)
  }
}

class IfNode(val _conditionExpr: ExprNode, val _thenStat: StatNode, val _elseStat: StatNode) extends StatNode {
  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    _conditionExpr.check(topST, ST)

    val conditionIdentifier = _conditionExpr.getIdentifier(topST, ST)

    if (!(conditionIdentifier == BoolTypeNode.getIdentifier(topST, ST))) {
      throw new TypeException(s"Semantic Error: ${ _conditionExpr.getKey} must evaluate to a boolean.")
    }
  }
}

class WhileNode(val _expr: ExprNode, val _stat: StatNode) extends StatNode {
  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    _expr.check(topST, ST)

    val conditionIdentifier = _expr.getIdentifier(topST, ST)

    if (!(conditionIdentifier == BoolTypeNode.getIdentifier(topST, ST))) {
      throw new TypeException(s"Semantic Error: ${ _expr.getKey} must evaluate to a boolean.")
    }
  }
}

class BeginNode(val _stat: StatNode) extends StatNode {
  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    // Check statement, new st needed as new scope is created.
    _stat.check(topST, ST)
  }
}

class SequenceNode(val _statOne: StatNode, val _statTwo: StatNode) extends StatNode {

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    _statOne.check(topST, ST)
    _statTwo.check(topST, ST)
  }
}
