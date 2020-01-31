package main.scala.ast

import ast._

class SyntaxErrorVisitor {

  def SyntaxErrorVisitor(programNode: ProgramNode): Unit = {

    val functions: IndexedSeq[FuncNode] = programNode.functions
    val statNode: StatNode = programNode.stat

    var accBool: Boolean = true

    for (f <- functions) accBool &&= functionReturnsOrExits(f.stat)

    if (!(accBool && functionReturnsOrExits(statNode))) {
      println("Syntax Error: Statement does not return or exit.")
    }
  }

  def functionReturnsOrExits(statNode: StatNode): Boolean = {
    statNode match {
      case returnNode: ReturnNode | exitNode: ExitNode =>
        true
      case ifNode: IfNode =>
        functionReturnsOrExits(ifNode.thenStat) && functionReturnsOrExits(ifNode.elseStat)
      case whileNode: WhileNode =>
        functionReturnsOrExits(whileNode.stat)
      case beginNode: BeginNode =>
        functionReturnsOrExits(beginNode.stat)
      case sequenceNode: SequenceNode =>
        functionReturnsOrExits(sequenceNode.statOne) && functionReturnsOrExits(sequenceNode.statTwo)
      case _ =>
        false
    }
  }

}
