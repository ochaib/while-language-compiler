package ast.nodes

import ast.symboltable._

// looks up the type identifier from all parent symbol tables and returns the appropriate identifier object
trait Identifiable {
  protected var key: String = _
  protected var _type: TYPE = _

  def getKey: String = {
    if (key == null) {
      key = initKey
    }
    key
  }

  def getType(topST: SymbolTable, ST: SymbolTable): TYPE = {
    // if null initialise
    if (_type == null) {
      _type = initType(topST, ST)
    }
    _type
  }

  def initKey: String
  def initType(topST: SymbolTable, ST: SymbolTable): TYPE
}
