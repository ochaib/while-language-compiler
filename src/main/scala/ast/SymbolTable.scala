package ast

import scala.collection.immutable.HashMap


class SymbolTable(var map: Map[String, IDENTIFIER], var funcMap: Map[String, FUNCTION], var encSymbolTable: SymbolTable) {

  def this(map: Map[String, IDENTIFIER]){
    this(map, null, null)
  }
  def this(encSymbolTable: SymbolTable) {
    this(null, null, encSymbolTable)
  }

  def add(name: String, function: FUNCTION): Unit = {
    funcMap = funcMap + (name -> function)
  }

  def add(name: String, identifier: IDENTIFIER): Unit = {
    this.map = this.map + (name -> identifier)
  }

  def lookupFun(name: String): Option[FUNCTION] = funcMap.get(name)

  def lookupFunAll(name: String): Option[FUNCTION] = {
    var s: Option[SymbolTable] = Option(this)
    while (s.isDefined) {
      val obj = s.get.lookupFun(name)
      if (obj.isDefined){
        return obj
      }
      s = Option(s.get.encSymbolTable)
    }
    None
  }

  def lookup(name: String): Option[IDENTIFIER] = {
    this.map.get(name)
  }

  def lookupAll(name: String): Option[IDENTIFIER] = {
    var s: Option[SymbolTable] = Option(this)
    while (s.isDefined) {
      val obj = s.get.lookup(name)
      if (obj.isDefined){
        return obj
      }
      s = Option(s.get.encSymbolTable)
    }
    None
  }
}

object SymbolTable {
  def topLevelSymbolTable(astTree: ASTNode): SymbolTable = {
    // return a symbol table with the map of all the standard types, global constants, global functions
    val charScalar: TYPE = new SCALAR(CharTypeNode.getKey, min=0, max=255)
    new SymbolTable(HashMap(IntTypeNode.getKey -> new SCALAR(IntTypeNode.getKey, min= Int.MinValue, max= Int.MaxValue),
      CharTypeNode.getKey -> charScalar,
      BoolTypeNode.getKey -> new SCALAR(BoolTypeNode.getKey, min=0, max=1),
      StringTypeNode.getKey -> new ARRAY(StringTypeNode.getKey, charScalar),
      GENERAL_PAIR.getKey -> GENERAL_PAIR,
      GENERAL_ARRAY.getKey -> GENERAL_ARRAY))

  }

  def newSymbolTable(encSymbolTable: SymbolTable): SymbolTable = new SymbolTable(new HashMap(),
    new HashMap(), encSymbolTable)
}