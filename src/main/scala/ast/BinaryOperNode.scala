package main.scala.ast

import ast.{IDENTIFIER, SymbolTable}

sealed abstract class BinaryOperationNode extends ExprNode {
  override def getIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = this match {
    case MultiplyNode(_, _) => IntTypeNode.getIdentifier(topST, ST)
    case DivideNode(_, _) => IntTypeNode.getIdentifier(topST, ST)
    case ModNode(_, _) => IntTypeNode.getIdentifier(topST, ST)
    case PlusNode(_, _) => IntTypeNode.getIdentifier(topST, ST)
    case MinusNode(_, _) => IntTypeNode.getIdentifier(topST, ST)
    case GreaterEqualNode(_, _) => BoolTypeNode.getIdentifier(topST, ST)
    case LessThanNode(_, _) => BoolTypeNode.getIdentifier(topST, ST)
    case LessEqualNode(_, _) => BoolTypeNode.getIdentifier(topST, ST)
    case EqualToNode(_, _) => BoolTypeNode.getIdentifier(topST, ST)
    case NotEqualNode(_, _) => BoolTypeNode.getIdentifier(topST, ST)
    case LogicalAndNode(_, _) => BoolTypeNode.getIdentifier(topST, ST)
    case LogicalOrNode(_, _) => BoolTypeNode.getIdentifier(topST, ST)
    case _ => {
      assert(assertion = false, "Binary identifier not defined")
      null
    }
  }

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = ???
}

case class MultiplyNode(_argOne: ExprNode, _argTwo: ExprNode) extends BinaryOperationNode
case class DivideNode(_argOne: ExprNode, _argTwo: ExprNode) extends BinaryOperationNode
case class ModNode(_argOne: ExprNode, _argTwo: ExprNode) extends BinaryOperationNode
case class PlusNode(_argOne: ExprNode, _argTwo: ExprNode) extends BinaryOperationNode
case class MinusNode(_argOne: ExprNode, _argTwo: ExprNode) extends BinaryOperationNode
case class GreaterThanNode(_argOne: ExprNode, _argTwo: ExprNode) extends BinaryOperationNode
case class GreaterEqualNode(_argOne: ExprNode, _argTwo: ExprNode) extends BinaryOperationNode
case class LessThanNode(_argOne: ExprNode, _argTwo: ExprNode) extends BinaryOperationNode
case class LessEqualNode(_argOne: ExprNode, _argTwo: ExprNode) extends BinaryOperationNode
case class EqualToNode(_argOne: ExprNode, _argTwo: ExprNode) extends BinaryOperationNode
case class NotEqualNode(_argOne: ExprNode, _argTwo: ExprNode) extends BinaryOperationNode
case class LogicalAndNode(_argOne: ExprNode, _argTwo: ExprNode) extends BinaryOperationNode
case class LogicalOrNode(_argOne: ExprNode, _argTwo: ExprNode) extends BinaryOperationNode
