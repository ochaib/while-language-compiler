package ast

import util.{ColoredConsole => console}

abstract class StatNode extends ASTNode {
  override def toTreeString: String = console.color("<STATEMENT>", fg=Console.RED)
}

case class SkipNode() extends StatNode {
  override def toTreeString: String = console.color("skip", fg=Console.BLUE)
}

case class DeclarationNode(val _type: TypeNode, val ident: IdentNode, val rhs: AssignRHSNode)
  extends StatNode {

  override def toTreeString: String = s"${_type.toString} ${ident.toString} = ${rhs.toString}"
}

case class AssignmentNode(val lhs: AssignLHSNode, val rhs: AssignRHSNode) extends StatNode {
  override def toTreeString: String = s"${lhs.toString} = ${rhs.toString}"
}

case class ReadNode(val lhs: AssignLHSNode) extends StatNode {
  override def toTreeString: String = console.color("read ", fg=Console.BLUE) + lhs.toString
}

case class FreeNode(val expr: ExprNode) extends StatNode {
  override def toTreeString: String = console.color("free ", fg=Console.BLUE) + expr.toString
}

case class ReturnNode(val expr: ExprNode) extends StatNode {

  override def toTreeString: String = console.color("return ", fg=Console.BLUE) + expr.toString
}

case class ExitNode(val expr: ExprNode) extends StatNode {

  override def toTreeString: String = console.color("exit ", fg=Console.BLUE) + expr.toString
}

case class PrintNode(val expr: ExprNode) extends StatNode {

  override def toTreeString: String = console.color("print ", fg=Console.BLUE) + expr.toString
}

case class PrintlnNode(val expr: ExprNode) extends StatNode {
  override def toTreeString: String = console.color("println ", fg=Console.BLUE) + expr.toString
}

case class IfNode(val conditionExpr: ExprNode, val thenStat: StatNode, val elseStat: StatNode) extends StatNode {
  override def toTreeString: String = {
    val if_ : String = console.color("if", fg=Console.BLUE)
    val then_ : String = console.color("then", fg=Console.BLUE)
    val else_ : String = console.color("else", fg=Console.BLUE)
    s"$if_ ${conditionExpr.toString} $thenStat\n${thenStat.toString}\n$elseStat\n${elseStat.toString}\nfi"
  }
}

case class WhileNode(val expr: ExprNode, val stat: StatNode) extends StatNode {
  override def toTreeString: String = {
    val whileStr: String = console.color("while", fg=Console.BLUE)
    val doStr: String = console.color("do", fg=Console.BLUE)
    val doneStr: String = console.color("done", fg=Console.BLUE)
    s"$whileStr ${expr.toString} $doStr\n${stat.toString}\n${doneStr}"
  }
}

case class BeginNode(val stat: StatNode) extends StatNode {
  override def toTreeString: String = {
    val begin: String = console.color("begin", fg=Console.BLUE)
    val end: String = console.color("end", fg=Console.BLUE)
    s"$begin\n${stat.toString}\n$end"
  }
}

case class SequenceNode(val statOne: StatNode, val statTwo: StatNode) extends StatNode {
  override def toTreeString: String = s"${statOne.toString}\n${statTwo.toString}"
}
