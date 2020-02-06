package ast

abstract class Visitor(entryNode: ASTNode) {
  abstract def visit(ASTNode: ASTNode)
}

