package ast;

// Every node necessary to generate AST. From the WACCLangSpec.

abstract class ASTNode {

}

trait Checkable {
  // Requires topST because of recursive types
  @throws(classOf[TypeException])
  def check(topST:SymbolTable, ST: SymbolTable): Unit
}

class ProgramNode(val _stat: StatNode, val _functions: IndexedSeq[FuncNode]) extends ASTNode {

  // Functions in the program: <func>*.
  val functions: IndexedSeq[FuncNode] = _functions
  // Stat in the program: <stat>.
  val stat: StatNode = _stat

}

class FuncNode(val _funcType: TypeNode, val _ident: IdentNode, val _paramList: ParamListNode,
               val _stat: StatNode) extends ASTNode with Checkable {
  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    val typeIdentifier: TYPE = _funcType.getTypeIdentifier(topST, ST)
    if (ST.lookup(_ident.toString).isDefined){
      throw new TypeException("function " + _ident.toString + " has already been defined")
    } else {
      ST.add(_ident.toString, new FUNCTION(typeIdentifier, _paramList.getTypeIdentifierList(ST)))
    }
  }
}

class ParamListNode(val _paramList: IndexedSeq[ParamNode]) extends ASTNode {

  val paramList: IndexedSeq[ParamNode] = _paramList

  // TODO loop through the _paramList and return a list of the TYPE IDENTIFIERS
  def getTypeIdentifierList(ST: SymbolTable): IndexedSeq[TYPE] = ???
}

class ParamNode(val _paramType: TypeNode, val _ident: IdentNode) extends ASTNode with Typeable {
  override def getTypeIdentifier(topST: SymbolTable, ST: SymbolTable): TYPE = ???
}

class StatNode extends ASTNode {

}

class SkipNode extends StatNode {

}

class DeclarationNode(val _type: TypeNode, val _ident: IdentNode, val _rhs: AssignRHSNode)
  extends StatNode with Checkable {
  override def check(topST:SymbolTable, ST: SymbolTable): Unit = {
    val typeIdentifier: TYPE = _type.getTypeIdentifier(topST, ST)
    _rhs.check(topST, ST)

    // If the type and the rhs dont match, throw exception
    if (typeIdentifier != _rhs.getTypeIdentifier(topST, ST)) {
      throw new TypeException(typeIdentifier.toString + " expected but got " + _rhs.getTypeIdentifier(topST, ST).toString)
    } else if (ST.lookup(_ident.toString).isDefined) {
      // If variable is already defined throw exception
      throw new TypeException(_ident.toString + " has already been declared")
    } else {
      ST.add(_ident.toString, new VARIABLE(typeIdentifier))
    }
  }
}

class AssignmentNode(val _lhs: AssignLHSNode, val _rhs: AssignRHSNode) extends StatNode with Checkable{

  val lhs: AssignLHSNode = _lhs
  val rhs: AssignRHSNode = _rhs

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    _lhs.check(topST, ST)
    _rhs.check(topST, ST)

    if (_lhs.getTypeIdentifier != _rhs.getTypeIdentifier){
      throw new TypeException(_lhs.toString + " and " + _rhs.toString + " have non-matching types")
    }
  }
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
trait AssignLHSNode extends ASTNode with Checkable with Typeable {

}

trait AssignRHSNode extends ASTNode with Checkable with Typeable{

}

class NewPairNode(val _fstElem: ExprNode, val _sndElem: ExprNode) extends AssignRHSNode {

  val fstElem: ExprNode = _fstElem
  val sndElem: ExprNode = _sndElem

  override def getTypeIdentifier(topST: SymbolTable, ST: SymbolTable): TYPE = ???

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    _fstElem.check(topST, ST)
    _sndElem.check(topST, ST)
  }
}

class CallNode(val _ident: IdentNode, val _argList: Option[ArgListNode]) extends AssignRHSNode {

  val ident: IdentNode = _ident
  // How do I make this optional?
  val argList: Option[ArgListNode] = _argList

  override def getTypeIdentifier(topST: SymbolTable, ST: SymbolTable): TYPE = ???

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    _ident.check(topST, ST)
    // TODO check through each of argList
  }
}

class ArgListNode(val _exprNodes: IndexedSeq[ExprNode]) extends ASTNode {

  val exprNodes: IndexedSeq[ExprNode] = _exprNodes

}

// Shouldn't be able to instantiate this.
trait PairElemNode extends AssignLHSNode with AssignRHSNode {

}

class FstNode(val _expr: ExprNode) extends PairElemNode {

  val expr: ExprNode = _expr

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = _expr.check(topST, ST)

  override def getTypeIdentifier(topST: SymbolTable, ST: SymbolTable): TYPE = _expr.getTypeIdentifier(topST, ST)
}

class SndNode(val _expr: ExprNode) extends PairElemNode {

  val expr: ExprNode = _expr

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = _expr.check(topST, ST)

  override def getTypeIdentifier(topST: SymbolTable, ST: SymbolTable): TYPE = _expr.getTypeIdentifier(topST, ST)
}

abstract class ExprNode extends AssignRHSNode {
  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    case Int_literNode(_, _) =>
    case Bool_literNode(_) =>
    case Char_literNode(_) =>
    case Str_literNode(_) =>
    case _ => assert(assertion = true, "Unaccounted for check case")
  }

  override def getTypeIdentifier(topST: SymbolTable, ST: SymbolTable): TYPE = {
    case Int_literNode(_, _) => IntTypeNode.getTypeIdentifier(topST, ST)
    case Bool_literNode(_) => BoolTypeNode.getTypeIdentifier(topST, ST)
    case Char_literNode(_) => CharTypeNode.getTypeIdentifier(topST, ST)
    case Str_literNode(_) => StringTypeNode.getTypeIdentifier(topST, ST)
    case _ => assert(assertion = true, "Unaccounted for getTypeIdentifier for expressions")
  }
}

case class Int_literNode(val _intSign: Option[Char], val _digits: IndexedSeq[Int]) extends ExprNode
case class Bool_literNode(val _value: Boolean) extends ExprNode
case class Char_literNode(val _value: Char) extends ExprNode
case class Str_literNode(val _characters: IndexedSeq[Char]) extends ExprNode

class Pair_literNode extends ExprNode {
  override def check(topST: SymbolTable, ST: SymbolTable): Unit = ???

  override def getTypeIdentifier(topST: SymbolTable, ST: SymbolTable): TYPE = ???
}

class ParenExprNode(_expr: ExprNode) extends ExprNode {
  override def check(topST: SymbolTable, ST: SymbolTable): Unit = _expr.check(topST, ST)

  override def getTypeIdentifier(topST: SymbolTable, ST: SymbolTable): TYPE = _expr.getTypeIdentifier(topST, ST)
}

// looks up the type identifier from all parent symbol tables and returns the appropriate identifier object
trait Typeable {
  @throws(classOf[TypeException])
  abstract def getTypeIdentifier(topST: SymbolTable, ST: SymbolTable): TYPE
}

abstract class TypeNode extends ASTNode with Typeable {
  def toKey: String
  override def toString: String = toKey
}

abstract class BaseTypeNode extends TypeNode with PairElemTypeNode {
  override def getTypeIdentifier(topST: SymbolTable, ST: SymbolTable): TYPE = {
    val T: Option[IDENTIFIER] = topST.lookup(toString)
    assert(T.isDefined, "Base Type Nodes MUST be predefined in the top level symbol table")
    assert(T.get.isInstanceOf[TYPE], "Base type identifiers must be an instance of TYPE")
    T.get.asInstanceOf[TYPE]
  }
}

object IntTypeNode extends BaseTypeNode {
  override def toKey: String = "int"
}

object BoolTypeNode extends BaseTypeNode {
  override def toKey: String = "bool"
}

object CharTypeNode extends BaseTypeNode {
  override def toKey: String = "char"
}

object StringTypeNode extends BaseTypeNode {
  override def toKey: String = "string"
}

class ArrayTypeNode(val _typeNode: TypeNode) extends TypeNode with PairElemTypeNode {
  override def toKey: String = _typeNode + "[]"

  override def getTypeIdentifier(topST: SymbolTable, ST: SymbolTable): TYPE = {
    val T: Option[IDENTIFIER] = topST.lookup(toString)
    if (T.isEmpty) {
      val arrayIdentifier = new ARRAY(_typeNode.getTypeIdentifier(topST, ST))
      topST.add(toString, arrayIdentifier)
      arrayIdentifier
    } else if (! T.get.isInstanceOf[TYPE]) {
      assert(assertion = true, "Something went wrong... " + toString + " should be a type but isn't")
      null
    } else {
      T.get.asInstanceOf[TYPE]
    }
  }
}

class PairTypeNode(val _firstPairElem: PairElemTypeNode, val _secondPairElem: PairElemTypeNode) extends TypeNode {

  val firstPairElem: PairElemTypeNode = _firstPairElem
  val secondPairElem: PairElemTypeNode = _secondPairElem

  override def toKey: String = "pair (" + _firstPairElem + "," + _secondPairElem + ")"

  override def getTypeIdentifier(topST: SymbolTable, ST: SymbolTable): TYPE = ???
}

// <pair-elem-type> in the WACCLangSpec
trait PairElemTypeNode extends ASTNode {
  override def toString: String = toKey
  abstract def toKey: String
}

// 'pair' in the WACCLangSpec
class PairElemTypePairNode extends PairElemTypeNode {
  override def toKey: String = "pair"
}

class IdentNode(val _ident: String) extends ExprNode with AssignLHSNode {
  override def toString: String = _ident

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = ???

  override def getTypeIdentifier(topST: SymbolTable, ST: SymbolTable): TYPE = {
    val T: Option[IDENTIFIER] = ST.lookupAll(toString)
    if (T.isEmpty) {
      throw new TypeException(toString + " has not been declared")
    } else if (! T.get.isInstanceOf[VARIABLE]) {
      assert(assertion = true, "Something went wrong... " + toString + " should be a variable but isn't")
      null
    } else {
      T.get.asInstanceOf[TYPE]
    }
  }
}

class ArrayElemNode(val _ident: IdentNode, val _exprNodes: IndexedSeq[ExprNode]) extends ExprNode with AssignLHSNode {

  val ident: IdentNode = _ident
  val exprNodes: IndexedSeq[ExprNode] = _exprNodes

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = ???
}

class ArrayLiteralNode(val _exprNodes: IndexedSeq[ExprNode]) extends AssignRHSNode {

  val exprNodes: IndexedSeq[ExprNode] = _exprNodes

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = ???

  override def getTypeIdentifier(topST: SymbolTable, ST: SymbolTable): TYPE = ???
}

trait BinaryOperationNode extends ExprNode {

  def argOne: ExprNode
  def argTwo: ExprNode

}

class MultiplyNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {
  override def argOne: ExprNode = _argOne

  override def argTwo: ExprNode = _argTwo

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = ???
}

class DivideNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {
  override def argOne: ExprNode = _argOne

  override def argTwo: ExprNode = _argTwo

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = ???
}

class ModNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {
  override def argOne: ExprNode = _argOne

  override def argTwo: ExprNode = _argTwo

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = ???
}

class PlusNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {
  override def argOne: ExprNode = _argOne

  override def argTwo: ExprNode = _argTwo

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = ???
}

class MinusNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {
  override def argOne: ExprNode = _argOne

  override def argTwo: ExprNode = _argTwo

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = ???
}

class GreaterThanNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {
  override def argOne: ExprNode = _argOne

  override def argTwo: ExprNode = _argTwo

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = ???
}

class GreaterEqualNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {
  override def argOne: ExprNode = _argOne

  override def argTwo: ExprNode = _argTwo

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = ???
}

class LessThanNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {
  override def argOne: ExprNode = _argOne

  override def argTwo: ExprNode = _argTwo

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = ???
}

class LessEqualNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {
  override def argOne: ExprNode = _argOne

  override def argTwo: ExprNode = _argTwo

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = ???
}

class EqualToNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {
  override def argOne: ExprNode = _argOne

  override def argTwo: ExprNode = _argTwo

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = ???
}

class NotEqualNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {
  override def argOne: ExprNode = _argOne

  override def argTwo: ExprNode = _argTwo

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = ???
}

class LogicalAndNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {
  override def argOne: ExprNode = _argOne

  override def argTwo: ExprNode = _argTwo

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = ???
}

class LogicalOrNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {
  override def argOne: ExprNode = _argOne

  override def argTwo: ExprNode = _argTwo

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = ???
}

trait UnaryOperationNode extends ExprNode {
  def expr: ExprNode
}

class LogicalNotNode(val _expr: ExprNode) extends UnaryOperationNode {
  override def expr: ExprNode = _expr

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = ???
}

class NegateNode(val _expr: ExprNode) extends UnaryOperationNode {
  override def expr: ExprNode = _expr

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = ???
}

class LenNode(val _expr: ExprNode) extends UnaryOperationNode {
  override def expr: ExprNode = _expr

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = ???
}

class OrdNode(val _expr: ExprNode) extends UnaryOperationNode {
  override def expr: ExprNode = _expr

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = ???
}

class ChrNode(val _expr: ExprNode) extends UnaryOperationNode {
  override def expr: ExprNode = _expr

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = ???
}