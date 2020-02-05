package ast

import main.scala.ast.ASTNode
import scala.collection.immutable.HashMap

class SymbolTableFunctions {
  def topLevelSymbolTable(astTree: ASTNode): SymbolTable = {
    val charKey = "char"
    val boolKey = "bool"
    val stringKey = "string"
    val intKey = "int"
    // return a symbol table with the map of all the standard types, global constants, global functions
    val charScalar: TYPE = new SCALAR(charKey, min=0, max=255)
    new SymbolTable(HashMap(intKey -> new SCALAR(intKey, min= Int.MinValue, max= Int.MaxValue),
      charKey -> charScalar,
      boolKey -> new SCALAR(boolKey, min=0, max=1),
      stringKey -> new ARRAY(stringKey, charScalar),
      GENERAL_PAIR.getKey -> GENERAL_PAIR))

  }
}