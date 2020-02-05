package ast

import ast._

// Every node necessary to generate AST. From the WACCLangSpec.

abstract class ASTNode {

}

class ProgramNode(val _stat: StatNode, val _functions: IndexedSeq[FuncNode]) extends ASTNode {

  // Functions in the program: <func>*.
  val functions: IndexedSeq[FuncNode] = _functions
  // Stat in the program: <stat>.
  val stat: StatNode = _stat

}

class FuncNode(val _funcType: TypeNode, val identNode: IdentNode, val _paramList: ParamListNode,
               val _stat: StatNode) extends ASTNode with Checkable {

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    val typeIdentifier: TYPE = _funcType.getIdentifier(topST, ST).asInstanceOf[TYPE]
    if (ST.lookup(identNode.identKey).isDefined){
      throw new TypeException("function " + identNode.identKey + " has already been defined")
    } else {
      ST.add(identNode.identKey, new FUNCTION(identNode.getKey, typeIdentifier, _paramList.getIdentifierList(ST)))
    }
  }
}

class ParamListNode(val _paramList: IndexedSeq[ParamNode]) extends ASTNode {

  val paramList: IndexedSeq[ParamNode] = _paramList

  // TODO loop through the _paramList and return a list of the TYPE IDENTIFIERS
  def getIdentifierList(ST: SymbolTable): IndexedSeq[TYPE] = ???
}

class ParamNode(val _paramType: TypeNode, val identNode: IdentNode) extends ASTNode with Identifiable {
  override def initIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = _paramType.getIdentifier(topST, ST)

  override def initKey: String = _paramType.getKey
}

trait AssignLHSNode extends ASTNode with Checkable with Identifiable

trait AssignRHSNode extends ASTNode with Checkable with Identifiable

class NewPairNode(val fstElem: ExprNode, val sndElem: ExprNode) extends AssignRHSNode {

  override def initIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = {
    val newPairIdentifierLookup: Option[IDENTIFIER] = topST.lookup(getKey)
    if (newPairIdentifierLookup.isDefined) {
      newPairIdentifierLookup.get
    } else {
      val newIdentifier = new PAIR(getKey, getElemIdentifier(fstElem, topST, ST).asInstanceOf[TYPE],
        getElemIdentifier(sndElem, topST, ST).asInstanceOf[TYPE])
      topST.add(getKey, newIdentifier)
      newIdentifier
    }
  }

  override def initKey: String = s"pair(${getElemKey(fstElem)},${getElemKey(sndElem)})}"

  private def getElemKey(elemNode: ExprNode): String = {
    val elemKey: String = elemNode.getKey
    if (elemKey.startsWith("pair")){
      "pair"
    } else {
      elemKey
    }
  }

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    fstElem.check(topST, ST)
    sndElem.check(topST, ST)
  }

  private def getElemIdentifier(elemNode: ExprNode, topST: SymbolTable, ST: SymbolTable): IDENTIFIER = {
    val elemIdentifier = elemNode.getIdentifier(topST, ST)
    if (elemIdentifier.isInstanceOf[PAIR] || elemIdentifier == GENERAL_PAIR) {
      GENERAL_PAIR
    } else {
      elemIdentifier
    }
  }
}

class CallNode(val identNode: IdentNode, val _argList: Option[ArgListNode]) extends AssignRHSNode {

  override def initIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = identNode.getIdentifier(topST, ST)

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    identNode.check(topST, ST)
    // TODO check through each of argList
  }
  override def initKey: String = identNode.identKey

}

class ArgListNode(val _exprNodes: IndexedSeq[ExprNode]) extends ASTNode

abstract class PairElemNode(val _expr: ExprNode) extends AssignLHSNode with AssignRHSNode {

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    val pairIdentifier: IDENTIFIER = _expr.getIdentifier(topST, ST)
    if (!pairIdentifier.isInstanceOf[PAIR]) {
      throw new TypeException("Expected pair type but got " + pairIdentifier)
    } else {
      _expr.check(topST, ST)
    }
  }
}

class FstNode(_expr: ExprNode) extends PairElemNode(_expr: ExprNode) {

  override def initIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = {
    val pairIdentifier: IDENTIFIER = _expr.getIdentifier(topST, ST)
    if (! pairIdentifier.isInstanceOf[PAIR]) {
      throw new TypeException(s"Expected pair type but got a non-pair type: ${_expr.getKey}}")
    } else {
      pairIdentifier.asInstanceOf[PAIR]._type1
    }
  }

  override def initKey: String = {
    val exprKey: String = _expr.getKey
    if (_expr == Pair_literNode) {
      // TODO in backend throw error
      throw new TypeException(s"Expected a pair type but got a null pair literal instead")
    } else if (exprKey.slice(0, 1) != "(" || ")" != exprKey.slice(exprKey.length() - 1, exprKey.length)) {
      throw new TypeException(s"Expected a pair type but got a non-pair type: ${_expr.getKey}")
    } else {
      exprKey.slice(1, exprKey.indexOf(','))
    }
  }
}

class SndNode(_expr: ExprNode) extends PairElemNode(_expr) {

  override def initIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = {
    val pairIdentifier: IDENTIFIER = _expr.getIdentifier(topST, ST)
    if (! pairIdentifier.isInstanceOf[PAIR]) {
      throw new TypeException("Expected pair type but got a non-pair type")
    } else {
      pairIdentifier.asInstanceOf[PAIR]._type2
    }
  }


  override def initKey: String = {
    val exprKey: String = _expr.getKey
    if (_expr == Pair_literNode) {
      // TODO in backend throw error
      throw new TypeException(s"Expected a pair type but got a null pair literal instead")
    } else if (exprKey.slice(0, 1) != "(" || ")" != exprKey.slice(exprKey.length() - 1, exprKey.length)) {
      throw new TypeException(s"Expected a pair type but got a non-pair type: ${_expr.getKey}")
    } else {
      exprKey.slice(exprKey.indexOf(',') + 1, exprKey.length)
    }
  }
}

class IdentNode(val identKey: String) extends ExprNode with AssignLHSNode {
  override def initKey: String = identKey

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    if (ST.lookupAll(toString).isEmpty){
      throw new TypeException(toString + " has not been declared")
    }
  }

  override def initIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = {
    val T: Option[IDENTIFIER] = ST.lookupAll(toString)
    if (T.isEmpty) {
      throw new TypeException(toString + " has not been declared")
    } else if (! T.get.isInstanceOf[VARIABLE]) {
      assert(assertion = false, "Something went wrong... " + toString + " should be a variable but isn't")
      null
    } else {
      T.get.asInstanceOf[VARIABLE]._type
    }
  }
}

class ArrayElemNode(val identNode: IdentNode, val _exprNodes: IndexedSeq[ExprNode]) extends ExprNode with AssignLHSNode {

  val ident: IdentNode = identNode
  val exprNodes: IndexedSeq[ExprNode] = _exprNodes

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = ???
}

class ArrayLiteralNode(val _exprNodes: IndexedSeq[ExprNode]) extends AssignRHSNode {

  val exprNodes: IndexedSeq[ExprNode] = _exprNodes

  // TODO check all exprNode identifiers against the first one
  override def check(topST: SymbolTable, ST: SymbolTable): Unit = ???

  override def initIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = {
    val arrayIdentifierOption: Option[IDENTIFIER] = topST.lookup(getKey)
    if (arrayIdentifierOption.isEmpty) {
      val arrayIdentifier = new ARRAY(getKey, _exprNodes.apply(0).getIdentifier(topST, ST).asInstanceOf[TYPE])
      topST.add(toString, arrayIdentifier)
      arrayIdentifier
    } else {
      assert(arrayIdentifierOption.get.isInstanceOf[ARRAY], s"Something went wrong... $getKey should be a an array type but isn't")
      arrayIdentifierOption.get
    }

  }

  override def initKey: String = _exprNodes.apply(0).getKey + "[]"
}

