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

case class DeclarationNode(token: Token, _type: TypeNode, ident: IdentNode, rhs: AssignRHSNode)
  extends StatNode(token) {

  override def toTreeString: String = s"${_type.toString} ${ident.toString} = ${rhs.toString}"
}

case class AssignmentNode(token: Token, lhs: AssignLHSNode, rhs: AssignRHSNode) extends StatNode(token) {
  override def toTreeString: String = s"${lhs.toString} = ${rhs.toString}"
}

case class ReadNode(token: Token, lhs: AssignLHSNode) extends StatNode(token) {
  override def toTreeString: String = console.color("read ", fg=Console.BLUE) + lhs.toString
}

case class FreeNode(token: Token, expr: ExprNode) extends StatNode(token) {
  override def toTreeString: String = console.color("free ", fg=Console.BLUE) + expr.toString
}

case class ReturnNode(token: Token, expr: ExprNode) extends StatNode(token) {

  override def toTreeString: String = console.color("return ", fg=Console.BLUE) + expr.toString
}

case class ExitNode(token: Token, expr: ExprNode) extends StatNode(token) {

  override def toTreeString: String = console.color("exit ", fg=Console.BLUE) + expr.toString
}

case class PrintNode(token: Token, expr: ExprNode) extends StatNode(token) {

  override def toTreeString: String = console.color("print ", fg=Console.BLUE) + expr.toString
}

case class PrintlnNode(token: Token, expr: ExprNode) extends StatNode(token) {
  override def toTreeString: String = console.color("println ", fg=Console.BLUE) + expr.toString
}

case class IfNode(token: Token, conditionExpr: ExprNode, thenStat: StatNode, elseStat: StatNode) extends StatNode(token) {
  override def toTreeString: String = {
    val if_ : String = console.color("if", fg=Console.BLUE)
    val then_ : String = console.color("then", fg=Console.BLUE)
    val else_ : String = console.color("else", fg=Console.BLUE)
    s"$if_ ${conditionExpr.toString} $thenStat\n${thenStat.toString}\n$elseStat\n${elseStat.toString}\nfi"
  }
}

case class WhileNode(token: Token, expr: ExprNode, stat: StatNode) extends StatNode(token) {
  override def toTreeString: String = {
    val whileStr: String = console.color("while", fg=Console.BLUE)
    val doStr: String = console.color("do", fg=Console.BLUE)
    val doneStr: String = console.color("done", fg=Console.BLUE)
    s"$whileStr ${expr.toString} $doStr\n${stat.toString}\n$doneStr"
  }
}

case class DoWhileNode(token: Token, stat: StatNode, expr: ExprNode) extends StatNode(token) {
  override def toTreeString: String = {
    val doStr: String = console.color("do", fg=Console.BLUE)
    val whileStr: String = console.color("while", fg=Console.BLUE)
    val doneStr: String = console.color("done", fg=Console.BLUE)
    s"$doStr ${stat.toString} $whileStr\n${expr.toString}\n$doneStr"
  }
}

case class ForNode(token: Token, forCondition: ForConditionNode, stat: StatNode) extends StatNode(token) {
  override def toTreeString: String = {
    val forStr: String = console.color("for", fg=Console.BLUE)
    val doneStr: String = console.color("done", fg=Console.BLUE)
    s"$forStr ${forCondition.toString} \n${stat.toString}\n$doneStr"
  }
}

case class ForConditionNode(token: Token, decl: DeclarationNode, expr: ExprNode, assign: AssignmentNode)
  extends StatNode(token) {

  override def toTreeString: String = {
    s"(${decl.toString}; ${expr.toString}; ${assign.toString})"
  }
}

case class BreakNode(token: Token) extends StatNode(token) {
  override def toTreeString: String = console.color("break", fg=Console.BLUE)
}

case class ContinueNode(token: Token) extends StatNode(token) {
  override def toTreeString: String = console.color("continue", fg=Console.BLUE)
}

case class BeginNode(token: Token, stat: StatNode) extends StatNode(token) {
  override def toTreeString: String = {
    val begin: String = console.color("begin", fg=Console.BLUE)
    val end: String = console.color("end", fg=Console.BLUE)
    s"$begin\n${stat.toString}\n$end"
  }
}

case class SequenceNode(token: Token, statOne: StatNode, statTwo: StatNode) extends StatNode(token) {
  override def toTreeString: String = s"${statOne.toString}\n${statTwo.toString}"
}
