package ast;

// Every node necessary to generate AST. From the WACCLangSpec.

abstract class ASTNode {

}

trait Checkable {
  def check(TopST:SymbolTable, ST: SymbolTable): Unit
}

class ProgramNode(val _stat: StatNode, val _functions: IndexedSeq[FuncNode]) extends ASTNode {

  // Functions in the program: <func>*.
  val functions: IndexedSeq[FuncNode] = _functions
  // Stat in the program: <stat>.
  val stat: StatNode = _stat

}

class FuncNode(val _funcType: TypeNode, val _ident: IdentNode, val _paramList: ParamListNode,
               val _stat: StatNode) extends ASTNode with Checkable {

  val funcType: TypeNode = _funcType
  val ident: IdentNode = _ident
  val paramList: ParamListNode = _paramList
  val stat: StatNode = _stat

  override def check(TopST: SymbolTable, ST: SymbolTable): Unit = ???
}

class ParamListNode(val _paramList: IndexedSeq[ParamNode]) extends ASTNode {

  val paramList: IndexedSeq[ParamNode] = _paramList
}

class ParamNode(val _paramType: TypeNode, val _ident: IdentNode) extends ASTNode {

}

class StatNode extends ASTNode {

}

class SkipNode extends StatNode {

}

class DeclarationNode(val _type: TypeNode, val _ident: IdentNode, val _rhs: AssignRHSNode)
  extends StatNode with Checkable {
  override def check(TopST:SymbolTable, ST: SymbolTable): Unit = {
    var T = ST.lookupAll(_type.toString)
    val V = ST.lookup(_ident.toString)
    // WACC language grammar does not account for user-generated types or classes
    if (T.isEmpty){
      val typeIdentifier: TYPE = _type.getType
      TopST.add(T.toString, typeIdentifier)
      ST.add(T.toString, new VARIABLE(typeIdentifier))
    } else if (! T.get.isInstanceOf[TYPE]) {
      throw new TypeException(_type.toString + " is not a type");
    } else if (V.isDefined) {
      ST.add(_ident.toString, new VARIABLE(T.get.asInstanceOf[TYPE]))
    }
  }
}

class AssignmentNode(val _lhs: AssignLHSNode, val _rhs: AssignRHSNode) extends StatNode with Checkable{

  val lhs: AssignLHSNode = _lhs
  val rhs: AssignRHSNode = _rhs

  override def check(TopST: SymbolTable, ST: SymbolTable): Unit = ???
}

class ReadNode(val _lhs: AssignLHSNode) extends StatNode {

  val lhs: AssignLHSNode = _lhs

}

class FreeNode(val _expr: ExprNode) extends StatNode {

  val expr: ExprNode = _expr

}

class ReturnNode(val _expr: ExprNode) extends StatNode {

  val expr: ExprNode = _expr

}

class ExitNode(val _expr: ExprNode) extends StatNode {

  val expr: ExprNode = _expr

}

class PrintNode(val _expr: ExprNode) extends StatNode {

  val expr: ExprNode = _expr

}

class PrintlnNode(val _expr: ExprNode) extends StatNode {

  val expr: ExprNode = _expr

}

class IfNode(val _conditionExpr: ExprNode, val _thenStat: StatNode, val _elseStat: StatNode)
  extends StatNode {

  val conditionExpr: ExprNode = _conditionExpr
  // Two stat nodes, one for then one for else.
  val thenStat: StatNode = _thenStat
  val elseStat: StatNode = _elseStat

}

class WhileNode(val _expr: ExprNode, val _stat: StatNode) extends StatNode {

  val expr: ExprNode = _expr
  val stat: StatNode = _stat

}

class BeginNode(val _stat: StatNode) extends StatNode {

  val stat: StatNode = _stat

}

class SequenceNode(val _statOne: StatNode, val _statTwo: StatNode) extends StatNode {

  val statOne: StatNode = _statOne
  val statTwo: StatNode = _statTwo

}

// Both of these need to be traits (abstract classes) in order to be extended later.
trait AssignLHSNode extends ASTNode {

}

trait AssignRHSNode extends ASTNode {

}

class NewPairNode(val _fstElem: ExprNode, val _sndElem: ExprNode) extends AssignRHSNode {

  val fstElem: ExprNode = _fstElem
  val sndElem: ExprNode = _sndElem

}

class CallNode(val _ident: IdentNode, val _argList: Option[ArgListNode]) extends AssignRHSNode {

  val ident: IdentNode = _ident
  // How do I make this optional?
  val argList: Option[ArgListNode] = _argList

}

class ArgListNode(val _exprNodes: IndexedSeq[ExprNode]) extends ASTNode {

  val exprNodes: IndexedSeq[ExprNode] = _exprNodes

}

// Shouldn't be able to instantiate this.
trait PairElemNode extends AssignLHSNode with AssignRHSNode {

}

class FstNode(val _expr: ExprNode) extends PairElemNode {

  val expr: ExprNode = _expr

}

class SndNode(val _expr: ExprNode) extends PairElemNode {

  val expr: ExprNode = _expr

}

abstract class ExprNode extends AssignRHSNode with Checkable {

}

class Int_literNode(val _intSign: Option[Char], val _digits: IndexedSeq[Int]) extends ExprNode {

  val intSign: Option[Char] = _intSign
  val digits: IndexedSeq[Int] = _digits

  override def check(TopST: SymbolTable, ST: SymbolTable): Unit = ???
}

class Bool_literNode(val _value: Boolean) extends ExprNode {

  val value: Boolean = _value

  override def check(TopST: SymbolTable, ST: SymbolTable): Unit = ???
}

class Char_literNode(val _value: Char) extends ExprNode {

  val value: Char = _value

  override def check(TopST: SymbolTable, ST: SymbolTable): Unit = ???
}

class Str_literNode(val _characters: IndexedSeq[Char]) extends ExprNode {

  val characters: IndexedSeq[Char] = _characters

  override def check(TopST: SymbolTable, ST: SymbolTable): Unit = ???
}

class Pair_literNode extends ExprNode {
  override def check(TopST: SymbolTable, ST: SymbolTable): Unit = ???
}

class ParenExprNode extends ExprNode {
  override def check(TopST: SymbolTable, ST: SymbolTable): Unit = ???
}

abstract class TypeNode extends ASTNode {
  def getType: TYPE
  def toKey: String
  override def toString: String = toKey
}

abstract class BaseTypeNode extends TypeNode with PairElemTypeNode {

}

class IntTypeNode extends BaseTypeNode {
  override def getType: TYPE = new SCALAR(Int.MinValue, Int.MaxValue)
  override def toKey(): String = "int"
}

class BoolTypeNode extends BaseTypeNode {
  override def getType: TYPE = ???
  override def toKey: String = ???
}

class CharTypeNode extends BaseTypeNode {
  override def toKey(): String = "char"

  override def getType: TYPE = ???
}

class StringTypeNode extends BaseTypeNode {
  override def toKey(): String = "string"

  override def getType: TYPE = ???
}

class ArrayTypeNode(val _typeNode: TypeNode) extends TypeNode with PairElemTypeNode {

  val typeNode: TypeNode = _typeNode

  override def toKey(): String = _typeNode + "[]"

  override def getType: TYPE = ???
}

class PairTypeNode(val _firstPairElem: PairElemTypeNode, val _secondPairElem: PairElemTypeNode) extends TypeNode {

  val firstPairElem: PairElemTypeNode = _firstPairElem
  val secondPairElem: PairElemTypeNode = _secondPairElem

  override def toKey(): String = "pair (" + _firstPairElem + "," + _secondPairElem + ")"

  override def getType: TYPE = ???
}

// <pair-elem-type> in the WACCLangSpec
trait PairElemTypeNode extends ASTNode {
  override def toString: String = toKey
  def toKey: String
}

// 'pair' in the WACCLangSpec
class PairElemTypePairNode extends PairElemTypeNode {
  override def toKey: String = "pair"
}

class IdentNode(val _ident: String) extends ExprNode with AssignLHSNode {
  override def toString: String = _ident

  override def check(TopST: SymbolTable, ST: SymbolTable): Unit = ???
}

class ArrayElemNode(val _ident: IdentNode, val _exprNodes: IndexedSeq[ExprNode]) extends ExprNode with AssignLHSNode {

  val ident: IdentNode = _ident
  val exprNodes: IndexedSeq[ExprNode] = _exprNodes

  override def check(TopST: SymbolTable, ST: SymbolTable): Unit = ???
}

class ArrayLiteralNode(val _exprNodes: IndexedSeq[ExprNode]) extends AssignRHSNode {

  val exprNodes: IndexedSeq[ExprNode] = _exprNodes

}

trait BinaryOperationNode extends ExprNode {

  def argOne: ExprNode
  def argTwo: ExprNode

}

class MultiplyNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {
  override def argOne: ExprNode = _argOne

  override def argTwo: ExprNode = _argTwo

  override def check(TopST: SymbolTable, ST: SymbolTable): Unit = ???
}

class DivideNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {
  override def argOne: ExprNode = _argOne

  override def argTwo: ExprNode = _argTwo

  override def check(TopST: SymbolTable, ST: SymbolTable): Unit = ???
}

class ModNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {
  override def argOne: ExprNode = _argOne

  override def argTwo: ExprNode = _argTwo

  override def check(TopST: SymbolTable, ST: SymbolTable): Unit = ???
}

class PlusNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {
  override def argOne: ExprNode = _argOne

  override def argTwo: ExprNode = _argTwo

  override def check(TopST: SymbolTable, ST: SymbolTable): Unit = ???
}

class MinusNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {
  override def argOne: ExprNode = _argOne

  override def argTwo: ExprNode = _argTwo

  override def check(TopST: SymbolTable, ST: SymbolTable): Unit = ???
}

class GreaterThanNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {
  override def argOne: ExprNode = _argOne

  override def argTwo: ExprNode = _argTwo

  override def check(TopST: SymbolTable, ST: SymbolTable): Unit = ???
}

class GreaterEqualNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {
  override def argOne: ExprNode = _argOne

  override def argTwo: ExprNode = _argTwo

  override def check(TopST: SymbolTable, ST: SymbolTable): Unit = ???
}

class LessThanNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {
  override def argOne: ExprNode = _argOne

  override def argTwo: ExprNode = _argTwo

  override def check(TopST: SymbolTable, ST: SymbolTable): Unit = ???
}

class LessEqualNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {
  override def argOne: ExprNode = _argOne

  override def argTwo: ExprNode = _argTwo

  override def check(TopST: SymbolTable, ST: SymbolTable): Unit = ???
}

class EqualToNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {
  override def argOne: ExprNode = _argOne

  override def argTwo: ExprNode = _argTwo

  override def check(TopST: SymbolTable, ST: SymbolTable): Unit = ???
}

class NotEqualNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {
  override def argOne: ExprNode = _argOne

  override def argTwo: ExprNode = _argTwo

  override def check(TopST: SymbolTable, ST: SymbolTable): Unit = ???
}

class LogicalAndNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {
  override def argOne: ExprNode = _argOne

  override def argTwo: ExprNode = _argTwo

  override def check(TopST: SymbolTable, ST: SymbolTable): Unit = ???
}

class LogicalOrNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {
  override def argOne: ExprNode = _argOne

  override def argTwo: ExprNode = _argTwo

  override def check(TopST: SymbolTable, ST: SymbolTable): Unit = ???
}

trait UnaryOperationNode extends ExprNode {
  def expr: ExprNode
}

class LogicalNotNode(val _expr: ExprNode) extends UnaryOperationNode {
  override def expr: ExprNode = _expr

  override def check(TopST: SymbolTable, ST: SymbolTable): Unit = ???
}

class NegateNode(val _expr: ExprNode) extends UnaryOperationNode {
  override def expr: ExprNode = _expr

  override def check(TopST: SymbolTable, ST: SymbolTable): Unit = ???
}

class LenNode(val _expr: ExprNode) extends UnaryOperationNode {
  override def expr: ExprNode = _expr

  override def check(TopST: SymbolTable, ST: SymbolTable): Unit = ???
}

class OrdNode(val _expr: ExprNode) extends UnaryOperationNode {
  override def expr: ExprNode = _expr

  override def check(TopST: SymbolTable, ST: SymbolTable): Unit = ???
}

class ChrNode(val _expr: ExprNode) extends UnaryOperationNode {
  override def expr: ExprNode = _expr

  override def check(TopST: SymbolTable, ST: SymbolTable): Unit = ???
}