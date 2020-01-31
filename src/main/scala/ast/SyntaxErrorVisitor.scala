package main.scala.ast

import ast._

class SyntaxErrorVisitor {

  def SyntaxErrorVisitor(programNode: ProgramNode): Unit = {



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
