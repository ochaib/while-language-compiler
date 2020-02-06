package ast
import ast._
import util.{ColoredConsole => console}

sealed abstract class UnaryOperationNode(expr: ExprNode) extends ExprNode {

  override def getIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = this match {
    case LogicalNotNode(_) => BoolTypeNode.getIdentifier(topST, ST)
    case NegateNode(_) => BoolTypeNode.getIdentifier(topST, ST)
    case LenNode(_) => IntTypeNode.getIdentifier(topST, ST)
    case OrdNode(_) => IntTypeNode.getIdentifier(topST, ST)
    case ChrNode(_) => CharTypeNode.getIdentifier(topST, ST)
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
