// Every node necessary to generate AST. From the WACCLangSpec.

class ASTNode {

}

class ProgramNode(val _stat: StatNode, val _functions: IndexedSeq[FuncNode]) extends ASTNode {

  // Functions in the program: <func>*.
  val functions: IndexedSeq[FuncNode] = _functions
  // Stat in the program: <stat>.
  val stat: StatNode = _stat

}

class FuncNode(val _funcType: TypeNode, val _ident: IdentNode, val _paramList: ParamListNode,
               val _stat: StatNode) extends ASTNode {

  val funcType: TypeNode = _funcType
  val ident: IdentNode = _ident
  val paramList: ParamListNode = _paramList
  val stat: StatNode = _stat

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

class DeclarationNode(val _type: TypeNode, val _ident: IdentNode, val _rhs: AssignRHSNode) extends StatNode {

  val typeNode: TypeNode = _type
  val ident: IdentNode = _ident
  val rhs: AssignRHSNode = _rhs

}

class AssignmentNode(val _lhs: AssignLHSNode, val _rhs: AssignRHSNode) extends StatNode {

  val lhs: AssignLHSNode = _lhs
  val rhs: AssignRHSNode = _rhs

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

class CallNode(val _ident: IdentNode, val _argList: ArgListNode) extends AssignRHSNode {

  val ident: IdentNode = _ident
  // How do I make this optional?
  val argList: ArgListNode = _argList

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

class ExprNode extends AssignRHSNode {

}

class Int_literNode(val _intSign: Char, val _digits: IndexedSeq[Int]) extends ExprNode {

  val intSign: Char = _intSign
  val digits: IndexedSeq[Int] = _digits

}

class Bool_literNode(val _value: Boolean) extends ExprNode {

  val value: Boolean = _value
}

class Char_literNode(val _value: Char) extends ExprNode {

  val value: Char = _value

}

class Str_literNode(val _characters: IndexedSeq[Char]) extends ExprNode {

  val characters: IndexedSeq[Char] = _characters

}

class Pair_literNode extends ExprNode {

}

class ParenExprNode extends ExprNode {

}

class TypeNode extends ASTNode {

}

class BaseTypeNode extends TypeNode with PairElemTypeNode {

}

class IntTypeNode extends BaseTypeNode {

}

class BoolTypeNode extends BaseTypeNode {

}

class CharTypeNode extends BaseTypeNode {

}

class StringTypeNode extends BaseTypeNode {

}

class ArrayTypeNode(val _typeNode: TypeNode) extends TypeNode with PairElemTypeNode {

  val typeNode: TypeNode = _typeNode

}

class PairTypeNode(val _firstPairElem: PairElemTypeNode, val _secondPairElem: PairElemTypeNode) extends TypeNode {

  val firstPairElem: PairElemTypeNode = _firstPairElem
  val secondPairElem: PairElemTypeNode = _secondPairElem

}

trait PairElemTypeNode extends ASTNode {

}

class PairElemTypePairNode extends PairElemTypeNode {

}

class IdentNode extends ExprNode with AssignLHSNode {

}

class ArrayElemNode(val _ident: IdentNode, val _exprNodes: IndexedSeq[ExprNode]) extends ExprNode with AssignLHSNode {

  val ident: IdentNode = _ident
  val exprNodes: IndexedSeq[ExprNode] = _exprNodes

}

class ArrayLiteralNode(val _exprNodes: IndexedSeq[ExprNode]) extends AssignRHSNode {

  val exprNodes: IndexedSeq[ExprNode] = _exprNodes

}

trait BinaryOperationNode extends ExprNode {

  def argOne: ExprNode
  def argTwo: ExprNode

}

class MultiplyNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {

}

class DivideNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {

}

class ModNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {

}

class PlusNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {

}

class MinusNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {

}

class GreaterThanNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {

}

class GreaterEqualNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {

}

class LessThanNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {

}

class LessEqualNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {

}

class DoubleEqualNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {

}

class NotEqualNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {

}

class LogicalAndNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {

}

class LogicalOrNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {

}

trait UnaryOperationNode extends ExprNode {

  def arg: ExprNode

}

class LogicalNotNode(val _arg: ExprNode) extends UnaryOperationNode {

}

class NegateNode(val _arg: ExprNode) extends UnaryOperationNode {

}

class LenNode(val _arg: ExprNode) extends UnaryOperationNode {

}

class OrdNode(val _arg: ExprNode) extends UnaryOperationNode {

}

class ChrNode(val _arg: ExprNode) extends UnaryOperationNode {

}