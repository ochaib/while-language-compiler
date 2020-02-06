package ast

import util.{ColoredConsole => console}

abstract class ExprNode extends AssignRHSNode {

  override def initType(topST: SymbolTable, ST: SymbolTable): TYPE = this match {
    case Int_literNode(_) => IntTypeNode.getType(topST, ST)
    case Bool_literNode(_) => BoolTypeNode.getType(topST, ST)
    case Char_literNode(_) => CharTypeNode.getType(topST, ST)
    case Str_literNode(_) => StringTypeNode.getType(topST, ST)
  }

  override def initKey: String = this match {
    case Int_literNode(_) => IntTypeNode.getKey
    case Bool_literNode(_) => BoolTypeNode.getKey
    case Char_literNode(_) => CharTypeNode.getKey
    case Str_literNode(_) => StringTypeNode.getKey
  }

  override def toTreeString: String = console.color("<EXPR>", fg=Console.RED)
}

case class Int_literNode(num: Int) extends ExprNode {

  override def toTreeString: String = console.color(num.toString, fg=Console.MAGENTA)
}

case class Bool_literNode(value: Boolean) extends ExprNode {

  override def toTreeString: String = if (value)
    console.color("true", fg=Console.MAGENTA)
    else console.color("false", fg=Console.MAGENTA)

}

case class Char_literNode(value: Char) extends ExprNode {

  override def toTreeString: String = console.color(s"'$value'", fg=Console.YELLOW)
}

case class Str_literNode(str: String) extends ExprNode {

  override def toTreeString: String = console.color(str, fg=Console.YELLOW)
}

object Pair_literNode extends ExprNode {

  override def initType(topST: SymbolTable, ST: SymbolTable): TYPE = {
    val T: Option[IDENTIFIER] = topST.lookup(getKey)
    assert(T.isDefined, "Base or General Type Identifiers MUST be predefined in the top level symbol table")
    assert(T.get.isInstanceOf[TYPE], "Base type identifiers must be an instance of TYPE")
    T.get.asInstanceOf[TYPE]
  }
  override def initKey: String = GENERAL_PAIR.getKey

  override def toTreeString: String = console.color("null", fg=Console.MAGENTA)
}

class ParenExprNode(expr: ExprNode) extends ExprNode {

  override def getType(topST: SymbolTable, ST: SymbolTable): TYPE = expr.getType(topST, ST)

  override def toTreeString: String = console.color("<PAREN EXPR>", fg=Console.RED)
}
