package ast

abstract class Visitor(entryNode: ASTNode) {
  def visit(ASTNode: ASTNode)
  def begin(): Unit = visit(entryNode)
}

