package ast.nodes

import org.antlr.v4.runtime.Token

import util.{ColoredConsole => console}

sealed abstract class ShortEffectNode(token: Token, ident: IdentNode) extends StatNode(token) {

  def getSymbol: String = this match {
    case _: IncrementNode => "++"
    case _: DecrementNode => "--"
  }

  override def toTreeString: String = console.color(s"${ident.toString} $getSymbol", fg=Console.RED)
}

case class IncrementNode(token: Token, ident: IdentNode) extends ShortEffectNode(token, ident)
case class DecrementNode(token: Token, ident: IdentNode) extends ShortEffectNode(token, ident)
