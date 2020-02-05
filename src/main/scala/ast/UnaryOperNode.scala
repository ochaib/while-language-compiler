package ast
import ast._
import util.{ColoredConsole => console}

class UnaryOperationNode(expr: ExprNode) extends ExprNode {

  override def getIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = this match {
    case LogicalNotNode(_) => BoolTypeNode.getIdentifier(topST, ST)
    case NegateNode(_) => BoolTypeNode.getIdentifier(topST, ST)
    case LenNode(_) => IntTypeNode.getIdentifier(topST, ST)
    case OrdNode(_) => IntTypeNode.getIdentifier(topST, ST)
    case ChrNode(_) => CharTypeNode.getIdentifier(topST, ST)
  }

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = this match {
    case LogicalNotNode(expr: ExprNode) => checkHelper(expr, "bool", topST, ST)
    case NegateNode(expr: ExprNode) => checkHelper(expr, "int", topST, ST)
    case LenNode(expr: ExprNode) => lenHelper(expr, topST, ST)
    case OrdNode(expr: ExprNode) => checkHelper(expr, "char", topST, ST)
    case ChrNode(expr: ExprNode) => checkHelper(expr, "int", topST, ST)
  }

  private def checkHelper(expr: ExprNode, expectedIdentifier: String, topST: SymbolTable, ST: SymbolTable): Unit = {
    val identifier: IDENTIFIER = expr.getIdentifier(topST, ST)
    if (identifier != topST.lookup(expectedIdentifier).get){
      throw new TypeException(s"Expected $expectedIdentifier but got $identifier")
    }
  }
  private def lenHelper(expr: ExprNode, topST: SymbolTable, ST: SymbolTable): Unit = {
    val identifier: IDENTIFIER = expr.getIdentifier(topST, ST)
    if (!identifier.isInstanceOf[ARRAY]) {
      throw new TypeException("Expected an array but got " + identifier)
    }
  }

  override def toString: String = console.color(s"<UNARY OPER> ${expr.toString}", fg=Console.RED)
}

case class LogicalNotNode(expr: ExprNode) extends UnaryOperationNode(expr) {

  override def toString: String = s"!${expr.toString}"
}

case class NegateNode(expr: ExprNode) extends UnaryOperationNode(expr) {

  override def toString: String = s"-${expr.toString}"
}

case class LenNode(expr: ExprNode) extends UnaryOperationNode(expr) {

  override def toString: String = s"len ${expr.toString}"
}

case class OrdNode(expr: ExprNode) extends UnaryOperationNode(expr) {

  override def toString: String = s"ord ${expr.toString}"
}

case class ChrNode(expr: ExprNode) extends UnaryOperationNode(expr) {

    override def toString: String = s"ord ${expr.toString}"
}
