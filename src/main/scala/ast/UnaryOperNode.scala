package ast

import util.{ColoredConsole => console}

sealed abstract class UnaryOperationNode(expr: ExprNode) extends ExprNode {

  override def initType(topST: SymbolTable, ST: SymbolTable): TYPE = this match {
    case LogicalNotNode(_) => BoolTypeNode.getType(topST, ST)
    case LenNode(_) | OrdNode(_) | NegateNode(_) => IntTypeNode.getType(topST, ST)
    case ChrNode(_) => CharTypeNode.getType(topST, ST)
  }

  override def initKey: String = this match {
    case LogicalNotNode(_) => BoolTypeNode.getKey
    case LenNode(_) | OrdNode(_) | NegateNode(_) => IntTypeNode.getKey
    case ChrNode(_) => CharTypeNode.getKey
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
