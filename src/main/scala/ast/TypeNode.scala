package main.scala.ast

import ast.{ARRAY, GENERAL_PAIR, IDENTIFIER, PAIR, SymbolTable, TYPE}

abstract class TypeNode extends ASTNode with Identifiable

abstract class BaseTypeNode extends TypeNode with PairElemTypeNode {
  override def initIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = {
    val T: Option[IDENTIFIER] = topST.lookup(toString)
    assert(T.isDefined, "Base Type Nodes MUST be predefined in the top level symbol table")
    assert(T.get.isInstanceOf[TYPE], "Base type identifiers must be an instance of TYPE")
    T.get.asInstanceOf[TYPE]
  }

  override def initKey: String = this match {
    case IntTypeNode => "int"
    case BoolTypeNode => "bool"
    case CharTypeNode => "char"
    case StringTypeNode => "string"
  }
}

case object IntTypeNode extends BaseTypeNode
case object BoolTypeNode extends BaseTypeNode
case object CharTypeNode extends BaseTypeNode
case object StringTypeNode extends BaseTypeNode

class ArrayTypeNode(val _typeNode: TypeNode) extends TypeNode with PairElemTypeNode {
  override def initKey: String = _typeNode.getKey + "[]"

  override def initIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = {
    val T: Option[IDENTIFIER] = topST.lookup(getKey)
    if (T.isEmpty) {
      val arrayIdentifier = new ARRAY(getKey, _typeNode.getIdentifier(topST, ST).asInstanceOf[TYPE])
      topST.add(toString, arrayIdentifier)
      arrayIdentifier
    } else {
      assert(T.get.isInstanceOf[ARRAY], s"Something went wrong... $getKey should be a type but isn't")
      T.get
    }
  }
}

class PairTypeNode(val _firstPairElem: PairElemTypeNode, val _secondPairElem: PairElemTypeNode) extends TypeNode {
  override def initKey: String = "pair (" + _firstPairElem.getKey + "," + _secondPairElem.getKey + ")"
  override def initIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = {
    val identifierLookupOption: Option[IDENTIFIER] = topST.lookup(getKey)
    if (identifierLookupOption.isEmpty) {
      val firstPairIdentifier: IDENTIFIER = _firstPairElem.getIdentifier(topST, ST)
      val secondPairIdentifier: IDENTIFIER = _secondPairElem.getIdentifier(topST, ST)
      assert(firstPairIdentifier.isInstanceOf[TYPE],
        "Something went wrong, the first pair identifier was not an instance of TYPE")
      assert(secondPairIdentifier.isInstanceOf[TYPE], "Something went wrong, the second pair identifier was not an instance of TYPE")
      new PAIR(getKey, firstPairIdentifier.asInstanceOf[TYPE], secondPairIdentifier.asInstanceOf[TYPE])
    } else {
      val pairIdentifier = identifierLookupOption.get
      assert(pairIdentifier.isInstanceOf[PAIR], s"Expected pair type but got $getKey instead")
      pairIdentifier
    }
  }
}

// <pair-elem-type> in the WACCLangSpec
trait PairElemTypeNode extends ASTNode with Identifiable

// 'pair' in the WACCLangSpec
class PairElemTypePairNode extends PairElemTypeNode {
  override def initKey: String = GENERAL_PAIR.getKey

  override def initIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = {
    val generalPairIdentifierOption: Option[IDENTIFIER] = topST.lookup(getKey)
    assert(generalPairIdentifierOption.isDefined, "Something went wrong, the general pair was not defined")
    generalPairIdentifierOption.get
  }
}
