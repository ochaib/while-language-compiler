package ast.symboltable

import ast.nodes.{ASTNode, BoolTypeNode, CharTypeNode, IdentNode, IntTypeNode, StringTypeNode}

import scala.collection.immutable.HashMap


class SymbolTable(var map: Map[String, IDENTIFIER], var funcMap: Map[String, FUNCTION], var encSymbolTable: SymbolTable, var children: IndexedSeq[SymbolTable]) {

  def this(map: Map[String, IDENTIFIER], funcMap: Map[String, FUNCTION]){
    this(map, funcMap, null, IndexedSeq())
  }
  def this(encSymbolTable: SymbolTable) {
    this(null, null, encSymbolTable, IndexedSeq())
  }

  def addChild(child: SymbolTable): Unit = {
    children = children :+ child
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
    // TODO: refactor
    new SymbolTable(HashMap(IntTypeNode(null).getKey -> new SCALAR(IntTypeNode(null).getKey, min= Int.MinValue, max= Int.MaxValue),
      CharTypeNode(null).getKey -> new SCALAR(CharTypeNode(null).getKey, min=0, max=255),
      BoolTypeNode(null).getKey -> new SCALAR(BoolTypeNode(null).getKey, min=0, max=1),
      STRING.getKey -> STRING,
      GENERAL_PAIR.getKey -> GENERAL_PAIR,
      GENERAL_ARRAY.getKey -> GENERAL_ARRAY), new HashMap())

  }

  def newSymbolTable(encSymbolTable: SymbolTable): SymbolTable = {
    val newSymbolTable: SymbolTable = new SymbolTable(new HashMap(), new HashMap(), encSymbolTable, IndexedSeq())
    encSymbolTable.addChild(newSymbolTable)
    newSymbolTable
  }

  def makeFunctionKey(identNode: IdentNode, paramTypeList: IndexedSeq[TYPE]): String = {
    var key = s"${identNode.ident}_p_"
    for (_type <- paramTypeList) {
      _type match {
        case _: ARRAY =>
          key = key + "array_"
        case _: PAIR =>
          key = key + s"pair_"
        case _ =>
          key = key + s"${_type.getKey}_"
      }
    }
    key
  }
}