package ast

// looks up the type identifier from all parent symbol tables and returns the appropriate identifier object
trait Identifiable {
  protected var key: String = _
  protected var _type: TYPE = _

  def getKey: String = {
    if (key == null) {
      initKey
    }
    key
  }

  def getType(topST: SymbolTable, ST: SymbolTable): TYPE = {
    // if null initialise
    if (_type == null) {
      initType(topST, ST)
    }
    _type
  }

  def initKey: String
  def initType(topST: SymbolTable, ST: SymbolTable): TYPE
}
