package ast

abstract class Visitor(entryNode: ASTNode) {
  abstract def visit(ASTNode: ASTNode)
  def begin(): Unit = visit(entryNode)
}

