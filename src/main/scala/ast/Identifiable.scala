package ast

import ast.{IDENTIFIER, SymbolTable}

// looks up the type identifier from all parent symbol tables and returns the appropriate identifier object
trait Identifiable {
  protected var key: String = _
  protected var identifier: IDENTIFIER = _

  def getKey: String = {
    if (key == null) {
      initKey
    }
    key
  }

  def getIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = {
    // if null initialise
    if (identifier == null) {
      initIdentifier(topST, ST)
    }
    identifier
  }

  def initKey: String
  def initIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER
}
