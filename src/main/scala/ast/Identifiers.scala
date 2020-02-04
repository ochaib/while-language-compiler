package ast

class IDENTIFIER

class VARIABLE(_type: TYPE) extends IDENTIFIER

// Types
abstract class TYPE extends IDENTIFIER
class SCALAR(min: Int, max: Int) extends TYPE
class ARRAY(_type: TYPE) extends TYPE
class GENERAL_PAIR extends TYPE
class PAIR(val _type1: TYPE, val _type2: TYPE) extends TYPE

class FUNCTION(returnType: TYPE, paramTypes: IndexedSeq[TYPE]) extends IDENTIFIER
// class BASE_FUNCTION extends ast.IDENTIFIER {}

class PARAM(_type:TYPE) extends IDENTIFIER
