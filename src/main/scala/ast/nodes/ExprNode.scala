package ast.nodes

import ast.symboltable._
import org.antlr.v4.runtime.Token

import util.{ColoredConsole => console}

abstract class ExprNode(token: Token) extends ASTNode(token) with AssignRHSNode {

  override def initType(topST: SymbolTable, ST: SymbolTable): TYPE = this match {
    case _: Int_literNode => new IntTypeNode(null).getType(topST, ST)
    case _: Bool_literNode => new BoolTypeNode(null).getType(topST, ST)
    case _: Char_literNode => new CharTypeNode(null).getType(topST, ST)
    case _: Str_literNode => new StringTypeNode(null).getType(topST, ST)
    case _ => assert(assertion = false, "No initType function provided")
      null
  }

  override def initKey: String = this match {
    case _: Int_literNode => new IntTypeNode(null).getKey
    case _: Bool_literNode => new BoolTypeNode(null).getKey
    case _: Char_literNode => new CharTypeNode(null).getKey
    case _: Str_literNode => new StringTypeNode(null).getKey
    case _ => assert(assertion = false, "No initKey function provided")
      null
  }

  override def toTreeString: String = console.color("<EXPR>", fg=Console.RED)
}

case class Int_literNode(token: Token, num: String) extends ExprNode(token) {

  override def toTreeString: String = console.color(num.toString, fg=Console.MAGENTA)
}

case class Bool_literNode(token: Token, value: Boolean) extends ExprNode(token) {

  override def toTreeString: String = if (value)
    console.color("true", fg=Console.MAGENTA)
    else console.color("false", fg=Console.MAGENTA)

}

case class Char_literNode(token: Token, value: Char) extends ExprNode(token) {

  override def toTreeString: String = console.color(s"'$value'", fg=Console.YELLOW)
}

case class Str_literNode(token: Token, str: String) extends ExprNode(token) {

  override def toTreeString: String = console.color(str, fg=Console.YELLOW)
}

case class Pair_literNode(token: Token) extends ExprNode(token) {

  override def initType(topST: SymbolTable, ST: SymbolTable): TYPE = {
    val T: Option[IDENTIFIER] = topST.lookup(getKey)
    assert(T.isDefined, "Base or General Type Identifiers MUST be predefined in the top level symbol table")
    assert(T.get.isInstanceOf[TYPE], "Base type identifiers must be an instance of TYPE")
    T.get.asInstanceOf[TYPE]
  }
  override def initKey: String = GENERAL_PAIR.getKey

  override def toTreeString: String = console.color("null", fg=Console.MAGENTA)
}

class ParenExprNode(token: Token, expr: ExprNode) extends ExprNode(token) {

  override def getType(topST: SymbolTable, ST: SymbolTable): TYPE = expr.getType(topST, ST)

  override def toTreeString: String = console.color("<PAREN EXPR>", fg=Console.RED)
}
