package ast.visitors

import ast.nodes.ASTNode

// FIXME: entryNode, begin are redundant
abstract class Visitor(entryNode: ASTNode) {
  def visit(tree: ASTNode): Unit
  def begin(): Unit = visit(entryNode)
}

