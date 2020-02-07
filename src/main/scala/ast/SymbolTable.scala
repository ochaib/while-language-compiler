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
    var s: Option[SymbolTable] = Some(this)
    while (s.isDefined) {
      val obj = s.get.lookupFun(name)
      if (obj.isDefined){
        return obj
      }
      s = Some(s.get.encSymbolTable)
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

  def newSymbolTable(encSymbolTable: SymbolTable): SymbolTable = new SymbolTable(new HashMap(),
    new HashMap(), encSymbolTable)
}