package ast

import util.{ColoredConsole => console}

sealed abstract class UnaryOperationNode(expr: ExprNode) extends ExprNode {

  override def getType(topST: SymbolTable, ST: SymbolTable): TYPE = this match {
    case LogicalNotNode(_) | NegateNode(_) => BoolTypeNode.getType(topST, ST)
    case LenNode(_) | OrdNode(_) => IntTypeNode.getType(topST, ST)
    case ChrNode(_) => CharTypeNode.getType(topST, ST)
  }

  def getOperator: String = this match {
    case LogicalNotNode(_) => "!"
    case NegateNode(_) => "-"
    case LenNode(_) => "len "
    case OrdNode(_) => "ord "
    case ChrNode(_) => "chr "
  }

  override def toTreeString: String = console.color(s"$getOperator ${expr.toString}", fg=Console.RED)
}

case class LogicalNotNode(expr: ExprNode) extends UnaryOperationNode(expr)
case class NegateNode(expr: ExprNode) extends UnaryOperationNode(expr)
case class LenNode(expr: ExprNode) extends UnaryOperationNode(expr)
case class OrdNode(expr: ExprNode) extends UnaryOperationNode(expr)
case class ChrNode(expr: ExprNode) extends UnaryOperationNode(expr)
