package ast.visitors

import ast.nodes.ASTNode

abstract class Visitor(entryNode: ASTNode) {
  def visit(tree: ASTNode): Unit
  def begin(): Unit = visit(entryNode)
}

