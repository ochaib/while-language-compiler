package ast.nodes

import ast.symboltable._
import org.antlr.v4.runtime.Token

import util.{ColoredConsole => console}

abstract class StatNode(token: Token) extends ASTNode(token) {
  override def toTreeString: String = console.color("<STATEMENT>", fg=Console.RED)
}

case class SkipNode(token: Token) extends StatNode(token) {
  override def toTreeString: String = console.color("skip", fg=Console.BLUE)
}

case class DeclarationNode(token: Token, val _type: TypeNode, val ident: IdentNode, val rhs: AssignRHSNode)
  extends StatNode(token) {

  override def toTreeString: String = s"${_type.toString} ${ident.toString} = ${rhs.toString}"
}

case class AssignmentNode(token: Token, val lhs: AssignLHSNode, val rhs: AssignRHSNode) extends StatNode(token) {
  override def toTreeString: String = s"${lhs.toString} = ${rhs.toString}"
}

case class ReadNode(token: Token, val lhs: AssignLHSNode) extends StatNode(token) {
  override def toTreeString: String = console.color("read ", fg=Console.BLUE) + lhs.toString
}

case class FreeNode(token: Token, val expr: ExprNode) extends StatNode(token) {
  override def toTreeString: String = console.color("free ", fg=Console.BLUE) + expr.toString
}

case class ReturnNode(token: Token, val expr: ExprNode) extends StatNode(token) {

  override def toTreeString: String = console.color("return ", fg=Console.BLUE) + expr.toString
}

case class ExitNode(token: Token, val expr: ExprNode) extends StatNode(token) {

  override def toTreeString: String = console.color("exit ", fg=Console.BLUE) + expr.toString
}

case class PrintNode(token: Token, val expr: ExprNode) extends StatNode(token) {

  override def toTreeString: String = console.color("print ", fg=Console.BLUE) + expr.toString
}

case class PrintlnNode(token: Token, val expr: ExprNode) extends StatNode(token) {
  override def toTreeString: String = console.color("println ", fg=Console.BLUE) + expr.toString
}

case class IfNode(token: Token, val conditionExpr: ExprNode, val thenStat: StatNode, val elseStat: StatNode) extends StatNode(token) {
  override def toTreeString: String = {
    val if_ : String = console.color("if", fg=Console.BLUE)
    val then_ : String = console.color("then", fg=Console.BLUE)
    val else_ : String = console.color("else", fg=Console.BLUE)
    s"$if_ ${conditionExpr.toString} $thenStat\n${thenStat.toString}\n$elseStat\n${elseStat.toString}\nfi"
  }
}

case class WhileNode(token: Token, val expr: ExprNode, val stat: StatNode) extends StatNode(token) {
  override def toTreeString: String = {
    val whileStr: String = console.color("while", fg=Console.BLUE)
    val doStr: String = console.color("do", fg=Console.BLUE)
    val doneStr: String = console.color("done", fg=Console.BLUE)
    s"$whileStr ${expr.toString} $doStr\n${stat.toString}\n${doneStr}"
  }
}

case class BeginNode(token: Token, val stat: StatNode) extends StatNode(token) {
  override def toTreeString: String = {
    val begin: String = console.color("begin", fg=Console.BLUE)
    val end: String = console.color("end", fg=Console.BLUE)
    s"$begin\n${stat.toString}\n$end"
  }
}

case class SequenceNode(token: Token, val statOne: StatNode, val statTwo: StatNode) extends StatNode(token) {
  override def toTreeString: String = s"${statOne.toString}\n${statTwo.toString}"
}
