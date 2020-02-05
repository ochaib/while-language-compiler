package main.scala.ast

import ast._

abstract class ExprNode extends AssignRHSNode {
  override def check(topST: SymbolTable, ST: SymbolTable): Unit = this match {
    case Int_literNode(_, _) =>
    case Bool_literNode(_) =>
    case Char_literNode(_) =>
    case Str_literNode(_) =>
  }

  override def initIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = this match {
    case Int_literNode(_, _) => IntTypeNode.getIdentifier(topST, ST)
    case Bool_literNode(_) => BoolTypeNode.getIdentifier(topST, ST)
    case Char_literNode(_) => CharTypeNode.getIdentifier(topST, ST)
    case Str_literNode(_) => StringTypeNode.getIdentifier(topST, ST)
  }

  override def initKey: String = this match {
    case Int_literNode(_, _) => IntTypeNode.getKey
    case Bool_literNode(_) => BoolTypeNode.getKey
    case Char_literNode(_) => CharTypeNode.getKey
    case Str_literNode(_) => StringTypeNode.getKey
  }
}

case class Int_literNode(_intSign: Option[Char],  _digits: IndexedSeq[Int]) extends ExprNode
case class Bool_literNode(_value: Boolean) extends ExprNode
case class Char_literNode(_value: Char) extends ExprNode
case class Str_literNode(_characters: IndexedSeq[Char]) extends ExprNode

object Pair_literNode extends ExprNode {

  override def initIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = {
    val T: Option[IDENTIFIER] = topST.lookup(getKey)
    assert(T.isDefined, "Base or General Type Identifiers MUST be predefined in the top level symbol table")
    assert(T.get.isInstanceOf[TYPE], "Base type identifiers must be an instance of TYPE")
    T.get.asInstanceOf[TYPE]
  }
  override def initKey: String = GENERAL_PAIR.getKey
}

class ParenExprNode(_expr: ExprNode) extends ExprNode {
  override def check(topST: SymbolTable, ST: SymbolTable): Unit = _expr.check(topST, ST)

  override def getIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = _expr.getIdentifier(topST, ST)
}
