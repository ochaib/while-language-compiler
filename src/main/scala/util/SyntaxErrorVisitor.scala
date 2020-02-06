package util
import ast._

class SyntaxErrorVisitor {

  def SyntaxErrorVisitor(programNode: ProgramNode): Unit = {

    val functions: IndexedSeq[FuncNode] = programNode.functions
    val statNode: StatNode = programNode.stat

//    var accBool: Boolean = true

    if (functionReturnsOrExits(statNode)) {
      // Instead of printing, add to syntax error log.
      println("Syntax Error: Program statement does not return or exit.")
    }

    for (f <- functions) {
//      accBool &&= functionReturnsOrExits(f.stat)
      if (functionReturnsOrExits(f.stat)) {
        // Add to syntax error log.
        println(s"Syntax Error: Function ${f.identNode.toString} does not return or exit")
      }
    }

  }

  def functionReturnsOrExits(statNode: StatNode): Boolean = {
    statNode match {
      case exitNode: ExitNode =>
        true
      case returnNode: ReturnNode =>
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

  def intLiterOverflowCheck(intLiter: Int_literNode): Boolean = {
    intLiter.toString.toInt > Int.MaxValue
  }

}