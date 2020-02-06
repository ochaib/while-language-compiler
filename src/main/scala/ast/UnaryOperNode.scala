package ast
import ast._
import util.{ColoredConsole => console}

sealed abstract class UnaryOperationNode(expr: ExprNode) extends ExprNode {

  override def getType(topST: SymbolTable, ST: SymbolTable): TYPE = this match {
    case LogicalNotNode(_) => BoolTypeNode.getType(topST, ST)
    case NegateNode(_) => BoolTypeNode.getType(topST, ST)
    case LenNode(_) => IntTypeNode.getType(topST, ST)
    case OrdNode(_) => IntTypeNode.getType(topST, ST)
    case ChrNode(_) => CharTypeNode.getType(topST, ST)
  }

  override def toTreeString: String = console.color(s"<UNARY OPER> ${expr.toString}", fg=Console.RED)
}

case class LogicalNotNode(expr: ExprNode) extends UnaryOperationNode(expr) {

  override def toTreeString: String = s"!${expr.toString}"
}

case class NegateNode(expr: ExprNode) extends UnaryOperationNode(expr) {

  override def toTreeString: String = s"-${expr.toString}"
}

case class LenNode(expr: ExprNode) extends UnaryOperationNode(expr) {

  override def toTreeString: String = s"len ${expr.toString}"
}

case class OrdNode(expr: ExprNode) extends UnaryOperationNode(expr) {

  override def toTreeString: String = s"ord ${expr.toString}"
}

case class ChrNode(expr: ExprNode) extends UnaryOperationNode(expr) {

    override def toTreeString: String = s"ord ${expr.toString}"
}
