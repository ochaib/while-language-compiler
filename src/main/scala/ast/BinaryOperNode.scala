package ast

sealed abstract class BinaryOperationNode(argOne: ExprNode, argTwo: ExprNode) extends ExprNode {
  override def getType(topST: SymbolTable, ST: SymbolTable): TYPE = this match {
    case MultiplyNode(_, _) => IntTypeNode.getType(topST, ST)
    case DivideNode(_, _) => IntTypeNode.getType(topST, ST)
    case ModNode(_, _) => IntTypeNode.getType(topST, ST)
    case PlusNode(_, _) => IntTypeNode.getType(topST, ST)
    case MinusNode(_, _) => IntTypeNode.getType(topST, ST)
    case GreaterThanNode(_, _) => BoolTypeNode.getType(topST, ST)
    case GreaterEqualNode(_, _) => BoolTypeNode.getType(topST, ST)
    case LessThanNode(_, _) => BoolTypeNode.getType(topST, ST)
    case LessEqualNode(_, _) => BoolTypeNode.getType(topST, ST)
    case EqualToNode(_, _) => BoolTypeNode.getType(topST, ST)
    case NotEqualNode(_, _) => BoolTypeNode.getType(topST, ST)
    case LogicalAndNode(_, _) => BoolTypeNode.getType(topST, ST)
    case LogicalOrNode(_, _) => BoolTypeNode.getType(topST, ST)
  }

  override def toTreeString: String = this match {
    case MultiplyNode(argOne, argTwo) => s"${argOne.toString} * ${argTwo.toString}"
    case DivideNode(argOne, argTwo) => s"${argOne.toString} / ${argTwo.toString}"
    case ModNode(argOne, argTwo) => s"${argOne.toString} % ${argTwo.toString}"
    case PlusNode(argOne, argTwo) => s"${argOne.toString} + ${argTwo.toString}"
    case MinusNode(argOne, argTwo) => s"${argOne.toString} - ${argTwo.toString}"
    case GreaterThanNode(argOne, argTwo) => s"${argOne.toString} > ${argTwo.toString}"
    case GreaterEqualNode(argOne, argTwo) => s"${argOne.toString} >= ${argTwo.toString}"
    case LessThanNode(argOne, argTwo) => s"${argOne.toString} < ${argTwo.toString}"
    case LessEqualNode(argOne, argTwo) => s"${argOne.toString} <= ${argTwo.toString}"
    case EqualToNode(argOne, argTwo) => s"${argOne.toString} == ${argTwo.toString}"
    case NotEqualNode(argOne, argTwo) => s"${argOne.toString} != ${argTwo.toString}"
    case LogicalAndNode(argOne, argTwo) => s"${argOne.toString} && ${argTwo.toString}"
    case LogicalOrNode(argOne, argTwo) => s"${argOne.toString} || ${argTwo.toString}"
  }
}

case class MultiplyNode(argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode(argOne, argTwo)
case class DivideNode(argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode(argOne, argTwo)
case class ModNode(argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode(argOne, argTwo)
case class PlusNode(argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode(argOne, argTwo)
case class MinusNode(argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode(argOne, argTwo)
case class GreaterThanNode(argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode(argOne, argTwo)
case class GreaterEqualNode(argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode(argOne, argTwo)
case class LessThanNode(argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode(argOne, argTwo)
case class LessEqualNode(argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode(argOne, argTwo)
case class EqualToNode(argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode(argOne, argTwo)
case class NotEqualNode(argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode(argOne, argTwo)
case class LogicalAndNode(argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode(argOne, argTwo)
case class LogicalOrNode(argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode(argOne, argTwo)
