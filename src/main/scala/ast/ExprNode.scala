package ast
import ast._
import util.{ColoredConsole => console}

abstract class ExprNode extends AssignRHSNode {
  override def check(topST: SymbolTable, ST: SymbolTable): Unit = this match {
    case Int_literNode(_) =>
    case Bool_literNode(_) =>
    case Char_literNode(_) =>
    case Str_literNode(_) =>
  }

  override def initIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = this match {
    case Int_literNode(_) => IntTypeNode.getIdentifier(topST, ST)
    case Bool_literNode(_) => BoolTypeNode.getIdentifier(topST, ST)
    case Char_literNode(_) => CharTypeNode.getIdentifier(topST, ST)
    case Str_literNode(_) => StringTypeNode.getIdentifier(topST, ST)
  }

  override def initKey: String = this match {
    case Int_literNode(_) => IntTypeNode.getKey
    case Bool_literNode(_) => BoolTypeNode.getKey
    case Char_literNode(_) => CharTypeNode.getKey
    case Str_literNode(_) => StringTypeNode.getKey
  }

  override def toString: String = console.color("<EXPR>", fg=Console.RED)
}

case class Int_literNode(num: Int) extends ExprNode {

  override def toString: String = console.color(num.toString, fg=Console.MAGENTA)
}

case class Bool_literNode(value: Boolean) extends ExprNode {

  override def toString: String = value match {
    case true => console.color("true", fg=Console.MAGENTA)
    case false => console.color("false", fg=Console.MAGENTA)
  }
}

case class Char_literNode(value: Char) extends ExprNode {

  override def toString: String = console.color(s"'$value'", fg=Console.YELLOW)
}

case class Str_literNode(str: String) extends ExprNode {

  override def toString: String = console.color(str, fg=Console.YELLOW)
}

object Pair_literNode extends ExprNode {

  override def initIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = {
    val T: Option[IDENTIFIER] = topST.lookup(getKey)
    assert(T.isDefined, "Base or General Type Identifiers MUST be predefined in the top level symbol table")
    assert(T.get.isInstanceOf[TYPE], "Base type identifiers must be an instance of TYPE")
    T.get.asInstanceOf[TYPE]
  }
  override def initKey: String = GENERAL_PAIR.getKey

  override def toString: String = console.color("null", fg=Console.MAGENTA)
}

class ParenExprNode(expr: ExprNode) extends ExprNode {
  override def check(topST: SymbolTable, ST: SymbolTable): Unit = expr.check(topST, ST)

  override def getIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = expr.getIdentifier(topST, ST)

  override def toString: String = console.color("<PAREN EXPR>", fg=Console.RED)
}
