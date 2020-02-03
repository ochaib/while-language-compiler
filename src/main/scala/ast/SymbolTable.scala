package ast


class SymbolTable(var map: Map[String, IDENTIFIER], var encSymbolTable: SymbolTable) {

  def this(map: Map[String, IDENTIFIER]){
    this(map, null)
  }
  def this(encSymbolTable: SymbolTable) {
    this(null, encSymbolTable)
  }

  def add(name: String, identifier: IDENTIFIER): Map[String, IDENTIFIER] = {
    this.map = this.map + (name -> identifier)
    this.map
  }

  def lookup(name: String): Option[IDENTIFIER] = {
    this.map.get(name)
  }

  def lookupAll(name: String): Option[IDENTIFIER] = {
    var s: Option[SymbolTable] = Some(this)
    while (s.isDefined) {
      val obj = s.get.lookup(name)
      if (obj.isDefined){
        return obj
      }
      s = Some(s.get.encSymbolTable)
    }
    None
  }
}
