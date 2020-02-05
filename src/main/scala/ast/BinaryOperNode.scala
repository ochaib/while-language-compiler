package ast

import ast.{IDENTIFIER, SymbolTable}

sealed trait BinaryOperationNode extends ExprNode {
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
  }

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = ???

  override def toString: String = argOne.toString + " " + console.color("<BINARY-OP>", fg=Console.RED) + " " + argTwo.toString
}

case class MultiplyNode(argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode {

  override def toString: String = s"${argOne.toString} * ${argTwo.toString}"
}

case class DivideNode(argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode {

  override def toString: String = s"${argOne.toString} / ${argTwo.toString}"
}

case class ModNode(argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode {

  override def toString: String = s"${argOne.toString} % ${argTwo.toString}"
}

case class PlusNode(argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode {

  override def toString: String = s"${argOne.toString} + ${argTwo.toString}"
}

case class MinusNode(argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode {

  override def toString: String = s"${argOne.toString} - ${argTwo.toString}"
}

case class GreaterThanNode(argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode {

  override def toString: String = s"${argOne.toString} > ${argTwo.toString}"
}

case class GreaterEqualNode(argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode {

  override def toString: String = s"${argOne.toString} >= ${argTwo.toString}"
}

case class LessThanNode(argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode {

  override def toString: String = s"${argOne.toString} < ${argTwo.toString}"
}

case class LessEqualNode(argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode {

  override def toString: String = s"${argOne.toString} <= ${argTwo.toString}"
}

case class EqualToNode(argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode {

  override def toString: String = s"${argOne.toString} == ${argTwo.toString}"
}

case class NotEqualNode(argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode {

  override def toString: String = s"${argOne.toString} != ${argTwo.toString}"
}

case class LogicalAndNode(argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode {

  override def toString: String = s"${argOne.toString} && ${argTwo.toString}"
}

case class LogicalOrNode(argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode {
  override def toString: String = s"${argOne.toString} || ${argTwo.toString}"
}
