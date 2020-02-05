package main.scala.ast

import ast._

sealed abstract class UnaryOperationNode extends ExprNode {
  override def getIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = this match {
    case LogicalNotNode(_) => BoolTypeNode.getIdentifier(topST, ST)
    case NegateNode(_) => BoolTypeNode.getIdentifier(topST, ST)
    case LenNode(_) => IntTypeNode.getIdentifier(topST, ST)
    case OrdNode(_) => IntTypeNode.getIdentifier(topST, ST)
    case ChrNode(_) => CharTypeNode.getIdentifier(topST, ST)
    case _ =>
      assert(assertion = false, "unaccounted for unary getIdentifier")
      null
  }
  override def check(topST: SymbolTable, ST: SymbolTable): Unit = this match {
    case LogicalNotNode(expr: ExprNode) => checkHelper(expr, "bool", topST, ST)
    case NegateNode(expr: ExprNode) => checkHelper(expr, "int", topST, ST)
    case LenNode(expr: ExprNode) => lenHelper(expr, topST, ST)
    case OrdNode(expr: ExprNode) => checkHelper(expr, "char", topST, ST)
    case ChrNode(expr: ExprNode) => checkHelper(expr, "int", topST, ST)
    case _ => assert(assertion = false, "unaccounted for unary check")
  }

  private def checkHelper(expr: ExprNode, expectedIdentifier: String, topST: SymbolTable, ST: SymbolTable): Unit = {
    val identifier: IDENTIFIER = expr.getIdentifier(topST, ST)
    if (identifier != topST.lookup(expectedIdentifier).get){
      throw new TypeException("Expected " + expectedIdentifier + " but got " + identifier)
    }
  }
  private def lenHelper(expr: ExprNode, topST: SymbolTable, ST: SymbolTable): Unit = {
    val identifier: IDENTIFIER = expr.getIdentifier(topST, ST)
    if (! identifier.isInstanceOf[ARRAY]) {
      throw new TypeException("Expected an array but got " + identifier)
    }
  }
}

case class LogicalNotNode(_expr: ExprNode) extends UnaryOperationNode
case class NegateNode(_expr: ExprNode) extends UnaryOperationNode
case class LenNode(_expr: ExprNode) extends UnaryOperationNode
case class OrdNode(_expr: ExprNode) extends UnaryOperationNode
case class ChrNode(_expr: ExprNode) extends UnaryOperationNode