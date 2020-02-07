package ast.symboltable

// key denotes the unique name of the identifier in a symbol table
abstract class IDENTIFIER(key: String) {
  def getKey: String = key
}

class VARIABLE(key:String, val _type: TYPE) extends IDENTIFIER(key)


// Types
abstract class TYPE(key: String) extends IDENTIFIER(key)

class SCALAR(key: String, min: Int, max: Int) extends TYPE(key)
class ARRAY(key: String, val _type: TYPE) extends TYPE(key)
object GENERAL_ARRAY extends ARRAY("[]", _type = null)
class PAIR(key: String, val _type1: TYPE, val _type2: TYPE) extends TYPE(key)
object GENERAL_PAIR extends PAIR(key = "pair", null, null)

class FUNCTION(key: String, val returnType: TYPE, var paramTypes: IndexedSeq[TYPE]) extends IDENTIFIER(key)

class PARAM(key: String, val _type:TYPE) extends IDENTIFIER(key)
