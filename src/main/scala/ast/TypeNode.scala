package ast

import util.{ColoredConsole => console}


abstract class TypeNode extends ASTNode with Identifiable {

  override def toString: String = console.color("<TYPE>", fg=Console.RED)
}

abstract class BaseTypeNode extends TypeNode with PairElemTypeNode {

  override def initType(topST: SymbolTable, ST: SymbolTable): TYPE = {
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

  override def toString: String = console.color("<BASE TYPE>", fg=Console.RED)
}

case object IntTypeNode extends BaseTypeNode {

  override def toString: String = console.color("int", fg=Console.MAGENTA)
}

case object BoolTypeNode extends BaseTypeNode {

  override def toString: String = console.color("bool", fg=Console.MAGENTA)
}

case object CharTypeNode extends BaseTypeNode {

  override def toString: String = console.color("char", fg=Console.MAGENTA)
}

case object StringTypeNode extends BaseTypeNode {

  override def toString: String = console.color("string", fg=Console.MAGENTA)
}

case class ArrayTypeNode(val typeNode: TypeNode) extends TypeNode with PairElemTypeNode {

  override def initKey: String = typeNode.getKey + "[]"

  override def initType(topST: SymbolTable, ST: SymbolTable): _root_.ast.TYPE = {
    val T: Option[IDENTIFIER] = topST.lookup(getKey)
    if (T.isEmpty) {
      val arrayIdentifier = new ARRAY(getKey, typeNode.getType(topST, ST).asInstanceOf[TYPE])
      topST.add(toString, arrayIdentifier)
      arrayIdentifier
    } else {
      assert(T.get.isInstanceOf[ARRAY], s"Something went wrong... $getKey should be a type but isn't")
      T.get.asInstanceOf[ARRAY]
    }
  }

  override def toString: String = typeNode.toString + "[]"
}

case class PairTypeNode(val firstPairElem: PairElemTypeNode, val secondPairElem: PairElemTypeNode) extends TypeNode {

  override def initKey: String = "pair (" + firstPairElem.getKey + "," + secondPairElem.getKey + ")"

  override def initType(topST: SymbolTable, ST: SymbolTable): _root_.ast.TYPE = {
    val identifierLookupOption: Option[IDENTIFIER] = topST.lookup(getKey)
    if (identifierLookupOption.isEmpty) {
      val firstPairIdentifier: IDENTIFIER = firstPairElem.getType(topST, ST)
      val secondPairIdentifier: IDENTIFIER = secondPairElem.getType(topST, ST)
      assert(firstPairIdentifier.isInstanceOf[TYPE],
        "Something went wrong, the first pair identifier was not an instance of TYPE")
      assert(secondPairIdentifier.isInstanceOf[TYPE], "Something went wrong, the second pair identifier was not an instance of TYPE")
      new PAIR(getKey, firstPairIdentifier.asInstanceOf[TYPE], secondPairIdentifier.asInstanceOf[TYPE])
    } else {
      val pairIdentifier = identifierLookupOption.get
      assert(pairIdentifier.isInstanceOf[PAIR], s"Expected pair type but got $getKey instead")
      pairIdentifier.asInstanceOf[PAIR]
    }
  }

  override def toString: String = console.color("pair", fg=Console.BLUE) +
    s"(${firstPairElem.toString}, ${secondPairElem.toString})"
}

// <pair-elem-type> in the WACCLangSpec
trait PairElemTypeNode extends ASTNode with Identifiable {

  override def toString: String = console.color("<PAIR ELEM>", fg=Console.RED)
}

// 'pair' in the WACCLangSpec
class PairElemTypePairNode extends PairElemTypeNode {
  override def initKey: String = GENERAL_PAIR.getKey

  override def initType(topST: SymbolTable, ST: SymbolTable): _root_.ast.TYPE = {
    val generalPairIdentifierOption: Option[IDENTIFIER] = topST.lookup(getKey)
    assert(generalPairIdentifierOption.isDefined && generalPairIdentifierOption.get == GENERAL_PAIR,
      "Something went wrong, the general pair was not defined")
    generalPairIdentifierOption.asInstanceOf[TYPE]
  }

  override def toString: String = console.color("pair", fg=Console.MAGENTA)
}
