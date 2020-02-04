class IDENTIFIER {}

class TYPE extends IDENTIFIER {}

class VARIABLE(_type: TYPE) extends IDENTIFIER {
  var this._type: TYPE = _type
}

class SCALAR(min: Int, max: Int) extends TYPE {
  var this.min: Int = min
  var this.max: Int = max
}

class ARRAY(_type: TYPE) extends TYPE {
  var this._type: TYPE = _type
}

class PAIR(type1: TYPE, type2: TYPE) extends TYPE{
  var this.type1: TYPE = type1
  var this.type2: TYPE = type2
}

class FUNCTION(returnType: TYPE) extends IDENTIFIER {
  var this.returnType: TYPE = returnType
}

class BASE_FUNCTION extends IDENTIFIER {}

class PARAM(_type:TYPE) extends IDENTIFIER {
  var this._type: TYPE = _type
}
