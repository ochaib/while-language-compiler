package ast

class IDENTIFIER

class VARIABLE(_type: TYPE) extends IDENTIFIER

// Types
abstract class TYPE extends IDENTIFIER
class SCALAR(min: Int, max: Int) extends TYPE
class ARRAY(_type: TYPE) extends TYPE
class PAIR(type1: TYPE, type2: TYPE) extends TYPE

class FUNCTION(returnType: TYPE, paramTypes: IndexedSeq[TYPE]) extends IDENTIFIER
// class BASE_FUNCTION extends ast.IDENTIFIER {}

class PARAM(_type:TYPE) extends IDENTIFIER
