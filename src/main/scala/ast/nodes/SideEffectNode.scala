package ast.nodes

import ast.symboltable._
import org.antlr.v4.runtime.Token

import util.{ColoredConsole => console}

sealed abstract class SideEffectNode(token: Token, ident: IdentNode, expr: ExprNode) extends StatNode(token) {

//  override def initType(topST: SymbolTable, ST: SymbolTable): TYPE = this match {
//    case LogicalNotNode(_, _) => new BoolTypeNode(null).getType(topST, ST)
//    case LenNode(_, _) | OrdNode(_, _) | NegateNode(_, _) => new IntTypeNode(null).getType(topST, ST)
//    case ChrNode(_, _) => new CharTypeNode(null).getType(topST, ST)
//  }
//
//  override def initKey: String = this match {
//    case LogicalNotNode(_, _) => BoolTypeNode(null).getKey
//    case LenNode(_, _) | OrdNode(_, _) | NegateNode(_, _) => IntTypeNode(null).getKey
//    case ChrNode(_, _) => CharTypeNode(null).getKey
//  }

  def getSymbol: String = this match {
    case _: AddAssign => "+="
    case _: SubAssign => "-="
    case _: MulAssign => "*="
    case _: DivAssign => "/="
    case _: ModAssign => "%="
  }

  override def toTreeString: String = console.color(s"${ident.toString} $getSymbol ${expr.toString}", fg=Console.RED)
}

case class AddAssign(token: Token, ident: IdentNode, expr: ExprNode) extends SideEffectNode(token, ident, expr)
case class SubAssign(token: Token, ident: IdentNode, expr: ExprNode) extends SideEffectNode(token, ident, expr)
case class MulAssign(token: Token, ident: IdentNode, expr: ExprNode) extends SideEffectNode(token, ident, expr)
case class DivAssign(token: Token, ident: IdentNode, expr: ExprNode) extends SideEffectNode(token, ident, expr)
case class ModAssign(token: Token, ident: IdentNode, expr: ExprNode) extends SideEffectNode(token, ident, expr)