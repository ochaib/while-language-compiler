import ast.ASTNode

import scala.collection.immutable.HashMap

class SymbolTableFunctions {
  def topLevelSymbolTable(astTree: ASTNode): SymbolTable = {
    // return a symbol table with the map of all the standard types, global constants, global functions
    val charScalar: TYPE = new SCALAR(min=0, max=255)
    new SymbolTable(HashMap("int" -> new SCALAR(min= -2147483647, max= 2147483647),
      "char" -> charScalar,
      "bool" -> new SCALAR(min=0, max=1),
      "string" -> new ARRAY(charScalar),
      "read" -> new BASE_FUNCTION,
      "free" -> new BASE_FUNCTION,
      "return" -> new BASE_FUNCTION,
      "exit" -> new BASE_FUNCTION))

  }
}