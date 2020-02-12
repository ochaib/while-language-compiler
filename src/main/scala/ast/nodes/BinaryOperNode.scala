package ast.nodes

import ast.symboltable._
import org.antlr.v4.runtime.Token

import util.{ColoredConsole => console}

sealed abstract class BinaryOperationNode(token: Token, argOne: ExprNode, argTwo: ExprNode) extends ExprNode(token) {
  override def initType(topST: SymbolTable, ST: SymbolTable): TYPE = this match {
    case MultiplyNode(_, _, _)
          | DivideNode(_, _, _)
          | ModNode(_, _, _)
          | PlusNode(_, _, _)
          | MinusNode(_, _, _) => IntTypeNode(null).getType(topST, ST)
    case GreaterThanNode(_, _, _)
          | GreaterEqualNode(_, _, _)
          | LessThanNode(_, _, _)
          | LessEqualNode(_, _, _)
          | EqualToNode(_, _, _)
          | NotEqualNode(_, _, _)
          | LogicalAndNode(_, _, _)
          | LogicalOrNode(_, _, _) => BoolTypeNode(null).getType(topST, ST)
  }

  override def initKey: String = this match {
    // TODO: refactor
    case MultiplyNode(_, _, _)
         | DivideNode(_, _, _)
         | ModNode(_, _, _)
         | PlusNode(_, _, _)
         | MinusNode(_, _, _) => IntTypeNode(null).getKey
    case GreaterThanNode(_, _, _)
         | GreaterEqualNode(_, _, _)
         | LessThanNode(_, _, _)
         | LessEqualNode(_, _, _)
         | EqualToNode(_, _, _)
         | NotEqualNode(_, _, _)
         | LogicalAndNode(_, _, _)
         | LogicalOrNode(_, _, _) => BoolTypeNode(null).getKey
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

case class MultiplyNode(token: Token, argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode(token, argOne, argTwo)
case class DivideNode(token: Token, argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode(token, argOne, argTwo)
case class ModNode(token: Token, argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode(token, argOne, argTwo)
case class PlusNode(token: Token, argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode(token, argOne, argTwo)
case class MinusNode(token: Token, argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode(token, argOne, argTwo)
case class GreaterThanNode(token: Token, argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode(token, argOne, argTwo)
case class GreaterEqualNode(token: Token, argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode(token, argOne, argTwo)
case class LessThanNode(token: Token, argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode(token, argOne, argTwo)
case class LessEqualNode(token: Token, argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode(token, argOne, argTwo)
case class EqualToNode(token: Token, argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode(token, argOne, argTwo)
case class NotEqualNode(token: Token, argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode(token, argOne, argTwo)
case class LogicalAndNode(token: Token, argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode(token, argOne, argTwo)
case class LogicalOrNode(token: Token, argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode(token, argOne, argTwo)
