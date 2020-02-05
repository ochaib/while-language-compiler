package main.scala.ast

import ast.{SymbolTable, TypeException}

trait Checkable {
  // Requires topST because of recursive types
  @throws(classOf[TypeException])
  def check(topST:SymbolTable, ST: SymbolTable): Unit
}
