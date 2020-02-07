package ast
import org.antlr.v4.runtime.Token

import util.{ColoredConsole => console}

sealed abstract class UnaryOperationNode(token: Token, expr: ExprNode) extends ExprNode(token) {

  override def getType(topST: SymbolTable, ST: SymbolTable): TYPE = this match {
    case LogicalNotNode(_, _) | NegateNode(_, _) => new BoolTypeNode(null).getType(topST, ST)
    case LenNode(_, _) | OrdNode(_, _) => new IntTypeNode(null).getType(topST, ST)
    case ChrNode(_, _) => new CharTypeNode(null).getType(topST, ST)
  }

  def getOperator: String = this match {
    case _: LogicalNotNode => "!"
    case _: NegateNode => "-"
    case _: LenNode => "len "
    case _: OrdNode => "ord "
    case _: ChrNode => "chr "
  }

  override def toTreeString: String = console.color(s"$getOperator ${expr.toString}", fg=Console.RED)
}

case class LogicalNotNode(token: Token, expr: ExprNode) extends UnaryOperationNode(token, expr)
case class NegateNode(token: Token, expr: ExprNode) extends UnaryOperationNode(token, expr)
case class LenNode(token: Token, expr: ExprNode) extends UnaryOperationNode(token, expr)
case class OrdNode(token: Token, expr: ExprNode) extends UnaryOperationNode(token, expr)
case class ChrNode(token: Token, expr: ExprNode) extends UnaryOperationNode(token, expr)
