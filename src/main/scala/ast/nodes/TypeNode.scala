package ast.nodes

import ast.symboltable._
import org.antlr.v4.runtime.Token

import util.{ColoredConsole => console}

abstract class TypeNode(token: Token) extends ASTNode(token) with Identifiable {

  override def toTreeString: String = console.color("<TYPE>", fg=Console.RED)
}

abstract class BaseTypeNode(token: Token) extends TypeNode(token) with PairElemTypeNode {

  override def initType(topST: SymbolTable, ST: SymbolTable): TYPE = {
    val T: Option[IDENTIFIER] = topST.lookup(getKey)
    assert(T.isDefined, "Base Type Nodes MUST be predefined in the top level symbol table")
    assert(T.get.isInstanceOf[TYPE], "Base type identifiers must be an instance of TYPE")
    T.get.asInstanceOf[TYPE]
  }

  override def initKey: String = this match {
    case _: IntTypeNode => "int"
    case _: BoolTypeNode => "bool"
    case _: CharTypeNode => "char"
    case _: StringTypeNode => "string"
  }

  override def toTreeString: String = console.color("<BASE TYPE>", fg=Console.RED)
}

case class IntTypeNode(token: Token) extends BaseTypeNode(token) {

  override def toTreeString: String = console.color("int", fg=Console.MAGENTA)
}

case class BoolTypeNode(token: Token) extends BaseTypeNode(token) {

  override def toTreeString: String = console.color("bool", fg=Console.MAGENTA)
}

case class CharTypeNode(token: Token) extends BaseTypeNode(token) {

  override def toTreeString: String = console.color("char", fg=Console.MAGENTA)
}

case class StringTypeNode(token: Token) extends BaseTypeNode(token) {

  override def toTreeString: String = console.color("string", fg=Console.MAGENTA)
}

case class ArrayTypeNode(token: Token, typeNode: TypeNode) extends TypeNode(token) with PairElemTypeNode {

  override def initKey: String = typeNode.getKey + "[]"

  override def initType(topST: SymbolTable, ST: SymbolTable): TYPE = {
    val T: Option[IDENTIFIER] = topST.lookup(getKey)
    if (T.isEmpty) {
      val arrayIdentifier: TYPE = new ARRAY(getKey, typeNode.getType(topST, ST))
      topST.add(getKey, arrayIdentifier)
      arrayIdentifier
    } else {
      assert(T.get.isInstanceOf[ARRAY], s"Something went wrong... $getKey should be a type but isn't")
      T.get.asInstanceOf[ARRAY]
    }
  }

  override def toTreeString: String = typeNode.toString + "[]"
}

case class PairTypeNode(token: Token, firstPairElem: PairElemTypeNode, secondPairElem: PairElemTypeNode) extends TypeNode(token) {

  override def initKey: String = "pair(" + firstPairElem.getKey + "," + secondPairElem.getKey + ")"

  override def initType(topST: SymbolTable, ST: SymbolTable): TYPE = {
    val identifierLookupOption: Option[IDENTIFIER] = topST.lookup(getKey)
    if (identifierLookupOption.isEmpty) {
      val firstPairIdentifier: IDENTIFIER = firstPairElem.getType(topST, ST)
      val secondPairIdentifier: IDENTIFIER = secondPairElem.getType(topST, ST)
      assert(firstPairIdentifier.isInstanceOf[TYPE],
        "Something went wrong, the first pair identifier was not an instance of TYPE")
      assert(secondPairIdentifier.isInstanceOf[TYPE], "Something went wrong, the second pair identifier was not an instance of TYPE")
      val pairIdentifier: PAIR = new PAIR(getKey, firstPairIdentifier.asInstanceOf[TYPE], secondPairIdentifier.asInstanceOf[TYPE])
      topST.add(getKey, pairIdentifier)
      pairIdentifier
    } else {
      val pairIdentifier = identifierLookupOption.get
      assert(pairIdentifier.isInstanceOf[PAIR], s"Expected pair type but got $getKey instead")
      pairIdentifier.asInstanceOf[PAIR]
    }
  }

  override def toTreeString: String = console.color("pair", fg=Console.BLUE) +
    s"(${firstPairElem.toString}, ${secondPairElem.toString})"
}

// <pair-elem-type> in the WACCLangSpec
trait PairElemTypeNode extends ASTNode with Identifiable {

  override def toTreeString: String = console.color("<PAIR ELEM>", fg=Console.RED)
}

// 'pair' in the WACCLangSpec
class PairElemTypePairNode(token: Token) extends ASTNode(token) with PairElemTypeNode {
  override def initKey: String = GENERAL_PAIR.getKey

  override def initType(topST: SymbolTable, ST: SymbolTable): TYPE = {
    val generalPairIdentifierOption: Option[IDENTIFIER] = topST.lookup(getKey)
    assert(generalPairIdentifierOption.isDefined && generalPairIdentifierOption.get == GENERAL_PAIR,
      "Something went wrong, the general pair was not defined")
    generalPairIdentifierOption.get.asInstanceOf[TYPE]
  }

  override def toTreeString: String = console.color("pair", fg=Console.MAGENTA)
}
