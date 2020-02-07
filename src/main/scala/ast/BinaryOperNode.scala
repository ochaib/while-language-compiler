package ast
import util.{ColoredConsole => console}

sealed abstract class BinaryOperationNode(argOne: ExprNode, argTwo: ExprNode) extends ExprNode {
  override def initType(topST: SymbolTable, ST: SymbolTable): TYPE = this match {
    case MultiplyNode(_,_)
          | DivideNode(_, _)
          | ModNode(_, _)
          | PlusNode(_, _)
          | MinusNode(_, _) => IntTypeNode.getType(topST, ST)
    case GreaterThanNode(_, _)
          | GreaterEqualNode(_, _)
          | LessThanNode(_, _)
          | LessEqualNode(_, _)
          | EqualToNode(_, _)
          | NotEqualNode(_, _)
          | LogicalAndNode(_, _)
          | LogicalOrNode(_, _) => BoolTypeNode.getType(topST, ST)
  }

  override def initKey: String = this match {
    case MultiplyNode(_,_)
         | DivideNode(_, _)
         | ModNode(_, _)
         | PlusNode(_, _)
         | MinusNode(_, _) => IntTypeNode.getKey
    case GreaterThanNode(_, _)
         | GreaterEqualNode(_, _)
         | LessThanNode(_, _)
         | LessEqualNode(_, _)
         | EqualToNode(_, _)
         | NotEqualNode(_, _)
         | LogicalAndNode(_, _)
         | LogicalOrNode(_, _) => BoolTypeNode.getKey
  }

  def getOperator: String = this match {
    case _: MultiplyNode => "*"
    case _: DivideNode => "/"
    case _: ModNode => "%"
    case _: PlusNode => "+"
    case _: MinusNode => "-"
    case _: GreaterThanNode => ">"
    case _: GreaterEqualNode => ">="
    case _: LessThanNode => "<"
    case _: LessEqualNode => "<="
    case _: EqualToNode => "=="
    case _: NotEqualNode => "!="
    case _: LogicalAndNode => "&&"
    case _: LogicalOrNode => "||"
  }

  override def toTreeString: String = s"${argOne.toString} $getOperator ${argTwo.toString}"
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
