package ast

import scala.collection.immutable.HashMap

sealed class typeCheckVisitor(entryNode: ASTNode) extends Visitor(entryNode) {
  var topST: SymbolTable = SymbolTable.topLevelSymbolTable(entryNode)
  var currentSymbolTable: SymbolTable = new SymbolTable(new HashMap(), topST)

  override def visit(): Unit = entryNode match {

  }
}

