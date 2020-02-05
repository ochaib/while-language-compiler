package ast
import ast._
import util.{ColoredConsole => console}

abstract class StatNode extends ASTNode with Checkable {
  override def toString: String = console.color("<STATEMENT>", fg=Console.RED)
}

class SkipNode extends StatNode {
  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {}

  override def toString: String = console.color("skip", fg=Console.BLUE)
}

class DeclarationNode(val _type: TypeNode, val identNode: IdentNode, val rhs: AssignRHSNode)
  extends StatNode {
  override def check(topST:SymbolTable, ST: SymbolTable): Unit = {
    val typeIdentifier: IDENTIFIER = _type.getIdentifier(topST, ST)
    rhs.check(topST, ST)

    // If the type and the rhs dont match, throw exception
    if (typeIdentifier != rhs.getIdentifier(topST, ST)) {
      throw new TypeException(typeIdentifier.getKey + " expected but got " + rhs.getIdentifier(topST, ST).getKey)
    } else if (ST.lookup(identNode.identKey).isDefined) {
      // If variable is already defined throw exception
      throw new TypeException(s"${identNode.identKey} has already been declared")
    } else {
      ST.add(identNode.identKey, new VARIABLE(identNode.getKey, typeIdentifier.asInstanceOf[TYPE]))
    }
  }

  override def toString: String = s"${typeNode.toString} ${ident.toString} = ${rhs.toString}"
}

class AssignmentNode(val lhs: AssignLHSNode, val rhs: AssignRHSNode) extends StatNode {

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    lhs.check(topST, ST)
    rhs.check(topST, ST)

    if (lhs.getIdentifier(topST, ST) != rhs.getIdentifier(topST, ST)){
      throw new TypeException(lhs.getKey + " and " + rhs.getKey + " have non-matching types")
    }
  }

  override def toString: String = s"${lhs.toString} = ${rhs.toString}"
}

class ReadNode(val lhs: AssignLHSNode) extends StatNode {
  // Ensure the read statement can only handle character or integer input.

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {

    lhs.check(topST, ST)

    if (!(lhs.getIdentifier(topST, ST) == IntTypeNode.getIdentifier(topST, ST)
      || lhs.getIdentifier(topST, ST) == CharTypeNode.getIdentifier(topST, ST))) {
      throw new TypeException(s"Semantic Error: ${ lhs.getKey} must be either a character or an integer.")
    }
  }

  override def toString: String = console.color("read ", fg=Console.BLUE) + lhs.toString
}

class FreeNode(val expr: ExprNode) extends StatNode {
  // Call check on the freeNode which should check that the expression in the freeNode
  // is either a pair or an array.

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    expr.check(topST, ST)

    val exprIdentifier = expr.getIdentifier(topST, ST)

    if (!(exprIdentifier.isInstanceOf[PAIR] || exprIdentifier == GENERAL_PAIR) ||
      !exprIdentifier.isInstanceOf[ARRAY]) {
      throw new TypeException(s"Semantic Error: ${expr.getKey} must be a pair or an array.")
    }
  }

  override def toString: String = console.color("free ", fg=Console.BLUE) + expr.toString
}

class ReturnNode(val expr: ExprNode) extends StatNode {
  // TODO: Check that return statement is present in body of non-main function.
  // TODO: Check that the type of expression given to the return statement must
  // TODO: match the return type of the expression.

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    expr.check(topST, ST)
  }

  override def toString: String = console.color("return ", fg=Console.BLUE) + expr.toString
}

class ExitNode(val expr: ExprNode) extends StatNode {

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    expr.check(topST, ST)

    val exprIdentifier = expr.getIdentifier(topST, ST)

    if (!(expr.getIdentifier(topST, ST) == IntTypeNode.getIdentifier(topST, ST))) {
      throw new TypeException(s"Semantic Error: ${ expr.getKey} must be an integer.")
    }
  }

  override def toString: String = console.color("exit ", fg=Console.BLUE) + expr.toString
}

class PrintNode(val expr: ExprNode) extends StatNode {

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    expr.check(topST, ST)
  }

  override def toString: String = console.color("print ", fg=Console.BLUE) + expr.toString
}

class PrintlnNode(val expr: ExprNode) extends StatNode {
  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    expr.check(topST, ST)
  }

  override def toString: String = console.color("println ", fg=Console.BLUE) + expr.toString
}

class IfNode(val conditionExpr: ExprNode, val thenStat: StatNode, val elseStat: StatNode) extends StatNode {
  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    conditionExpr.check(topST, ST)

    val conditionIdentifier = conditionExpr.getIdentifier(topST, ST)

    if (!(conditionIdentifier == BoolTypeNode.getIdentifier(topST, ST))) {
      throw new TypeException(s"Semantic Error: ${ conditionExpr.getKey} must evaluate to a boolean.")
    }
  }

  override def toString: String = {
    val if_ : String = console.color("if", fg=Console.BLUE)
    val then_ : String = console.color("then", fg=Console.BLUE)
    val else_ : String = console.color("else", fg=Console.BLUE)
    s"$if_ ${conditionExpr.toString} $then_\n${thenStat.toString}\n$else_\n${elseStat.toString}\nfi"
  }
}

class WhileNode(val expr: ExprNode, val stat: StatNode) extends StatNode {
  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    expr.check(topST, ST)

    val conditionIdentifier = expr.getIdentifier(topST, ST)

    if (!(conditionIdentifier == BoolTypeNode.getIdentifier(topST, ST))) {
      throw new TypeException(s"Semantic Error: ${ expr.getKey} must evaluate to a boolean.")
    }
  }

  override def toString: String = console.color(s"while ${expr.toString} do\n${stat.toString}\ndone", fg=Console.YELLOW)
}

class BeginNode(val stat: StatNode) extends StatNode {
  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    // Check statement, new st needed as new scope is created.
    stat.check(topST, ST)
  }

  override def toString: String = {
    val begin: String = console.color("begin", fg=Console.BLUE)
    val end: String = console.color("end", fg=Console.BLUE)
    s"$begin\n${stat.toString}\n$end"
  }
}

class SequenceNode(val statOne: StatNode, val statTwo: StatNode) extends StatNode {

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    statOne.check(topST, ST)
    statTwo.check(topST, ST)
  }

  override def toString: String = s"${statOne.toString}\n${statTwo.toString}"
}
