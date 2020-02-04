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
    val typeIdentifier: TYPE = _funcType.getIdentifier(topST, ST).asInstanceOf[TYPE]
    if (ST.lookup(_ident.toString).isDefined){
      throw new TypeException("function " + _ident.toString + " has already been defined")
    } else {
      ST.add(_ident.toString, new FUNCTION(typeIdentifier, _paramList.getIdentifierList(ST)))
    }
  }
}

class ParamListNode(val _paramList: IndexedSeq[ParamNode]) extends ASTNode {

  val paramList: IndexedSeq[ParamNode] = _paramList

  // TODO loop through the _paramList and return a list of the TYPE IDENTIFIERS
  def getIdentifierList(ST: SymbolTable): IndexedSeq[TYPE] = ???
}

class ParamNode(val _paramType: TypeNode, val _ident: IdentNode) extends ASTNode with Identifiable {
  override def getIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = ???
}

class StatNode extends ASTNode {

}

class SkipNode extends StatNode {

}

class DeclarationNode(val _type: TypeNode, val _ident: IdentNode, val _rhs: AssignRHSNode)
  extends StatNode with Checkable {
  override def check(topST:SymbolTable, ST: SymbolTable): Unit = {
    val typeIdentifier: IDENTIFIER = _type.getIdentifier(topST, ST)
    _rhs.check(topST, ST)

    // If the type and the rhs dont match, throw exception
    if (typeIdentifier != _rhs.getIdentifier(topST, ST)) {
      throw new TypeException(typeIdentifier.toString + " expected but got " + _rhs.getIdentifier(topST, ST).toString)
    } else if (ST.lookup(_ident.toString).isDefined) {
      // If variable is already defined throw exception
      throw new TypeException(_ident.toString + " has already been declared")
    } else {
      ST.add(_ident.toString, new VARIABLE(typeIdentifier.asInstanceOf[TYPE]))
    }
  }
}

class AssignmentNode(val _lhs: AssignLHSNode, val _rhs: AssignRHSNode) extends StatNode with Checkable{

  val lhs: AssignLHSNode = _lhs
  val rhs: AssignRHSNode = _rhs

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    _lhs.check(topST, ST)
    _rhs.check(topST, ST)

    if (_lhs.getIdentifier != _rhs.getIdentifier){
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
trait AssignLHSNode extends ASTNode with Checkable with Identifiable {

}

trait AssignRHSNode extends ASTNode with Checkable with Identifiable{

}

class NewPairNode(val _fstElem: ExprNode, val _sndElem: ExprNode) extends AssignRHSNode {

  val fstElem: ExprNode = _fstElem
  val sndElem: ExprNode = _sndElem

  override def getIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = ???

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    _fstElem.check(topST, ST)
    _sndElem.check(topST, ST)
  }
}

class CallNode(val _ident: IdentNode, val _argList: Option[ArgListNode]) extends AssignRHSNode {

  val ident: IdentNode = _ident
  // How do I make this optional?
  val argList: Option[ArgListNode] = _argList

  override def getIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = ???

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

  override def getIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = _expr.getIdentifier(topST, ST)
}

class SndNode(val _expr: ExprNode) extends PairElemNode {

  val expr: ExprNode = _expr

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = _expr.check(topST, ST)

  override def getIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = _expr.getIdentifier(topST, ST)
}

abstract class ExprNode extends AssignRHSNode {
  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    case Int_literNode(_, _) =>
    case Bool_literNode(_) =>
    case Char_literNode(_) =>
    case Str_literNode(_) =>
    case _ => assert(assertion = true, "Unaccounted for check case")
  }

  override def getIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = {
    case Int_literNode(_, _) => IntTypeNode.getIdentifier(topST, ST)
    case Bool_literNode(_) => BoolTypeNode.getIdentifier(topST, ST)
    case Char_literNode(_) => CharTypeNode.getIdentifier(topST, ST)
    case Str_literNode(_) => StringTypeNode.getIdentifier(topST, ST)
    case _ => assert(assertion = true, "Unaccounted for getIdentifier for expressions")
  }
}

case class Int_literNode(_intSign: Option[Char],  _digits: IndexedSeq[Int]) extends ExprNode
case class Bool_literNode(_value: Boolean) extends ExprNode
case class Char_literNode(_value: Char) extends ExprNode
case class Str_literNode(_characters: IndexedSeq[Char]) extends ExprNode

class Pair_literNode extends ExprNode {
  override def check(topST: SymbolTable, ST: SymbolTable): Unit = ???

  override def getIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = ???
}

class ParenExprNode(_expr: ExprNode) extends ExprNode {
  override def check(topST: SymbolTable, ST: SymbolTable): Unit = _expr.check(topST, ST)

  override def getIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = _expr.getIdentifier(topST, ST)
}

// looks up the type identifier from all parent symbol tables and returns the appropriate identifier object
trait Identifiable {
  @throws(classOf[TypeException])
  abstract def getIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER
}

abstract class TypeNode extends ASTNode with Identifiable {
  def toKey: String
  override def toString: String = toKey
}

abstract class BaseTypeNode extends TypeNode with PairElemTypeNode {
  override def getIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = {
    val T: Option[IDENTIFIER] = topST.lookup(toString)
    assert(T.isDefined, "Base Type Nodes MUST be predefined in the top level symbol table")
    assert(T.get.isInstanceOf[TYPE], "Base type identifiers must be an instance of TYPE")
    T.get.asInstanceOf[TYPE]
  }

  override def toKey: String = {
    case IntTypeNode => "int"
    case BoolTypeNode => "bool"
    case CharTypeNode => "char"
    case StringTypeNode => "string"
    case _ => assert(assertion = true, "undefined case for toKey")
  }
}

case object IntTypeNode extends BaseTypeNode
case object BoolTypeNode extends BaseTypeNode
case object CharTypeNode extends BaseTypeNode
case object StringTypeNode extends BaseTypeNode

class ArrayTypeNode(val _typeNode: TypeNode) extends TypeNode with PairElemTypeNode {
  override def toKey: String = _typeNode + "[]"

  override def getIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = {
    val T: Option[IDENTIFIER] = topST.lookup(toString)
    if (T.isEmpty) {
      val arrayIdentifier = new ARRAY(_typeNode.getIdentifier(topST, ST).asInstanceOf[TYPE])
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

  override def getIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = ???
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

  override def getIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = {
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

  override def getIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = ???
}

sealed abstract class BinaryOperationNode extends ExprNode {
  override def getIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = {
    case MultiplyNode(_, _) => IntTypeNode.getIdentifier(topST, ST)
    case DivideNode(_, _) => IntTypeNode.getIdentifier(topST, ST)
    case ModNode(_, _) => IntTypeNode.getIdentifier(topST, ST)
    case PlusNode(_, _) => IntTypeNode.getIdentifier(topST, ST)
    case MinusNode(_, _) => IntTypeNode.getIdentifier(topST, ST)
    case GreaterEqualNode(_, _) => BoolTypeNode.getIdentifier(topST, ST)
    case GreaterEqualNode(_, _) => BoolTypeNode.getIdentifier(topST, ST)
    case LessThanNode(_, _) => BoolTypeNode.getIdentifier(topST, ST)
    case LessEqualNode(_, _) => BoolTypeNode.getIdentifier(topST, ST)
    case EqualToNode(_, _) => BoolTypeNode.getIdentifier(topST, ST)
    case NotEqualNode(_, _) => BoolTypeNode.getIdentifier(topST, ST)
    case LogicalAndNode(_, _) => BoolTypeNode.getIdentifier(topST, ST)
    case LogicalOrNode(_, _) => BoolTypeNode.getIdentifier(topST, ST)
  }

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = ???
}

case class MultiplyNode(_argOne: ExprNode, _argTwo: ExprNode) extends BinaryOperationNode
case class DivideNode(_argOne: ExprNode, _argTwo: ExprNode) extends BinaryOperationNode
case class ModNode(_argOne: ExprNode, _argTwo: ExprNode) extends BinaryOperationNode
case class PlusNode(_argOne: ExprNode, _argTwo: ExprNode) extends BinaryOperationNode
case class MinusNode(_argOne: ExprNode, _argTwo: ExprNode) extends BinaryOperationNode
case class GreaterThanNode(_argOne: ExprNode, _argTwo: ExprNode) extends BinaryOperationNode
case class GreaterEqualNode(_argOne: ExprNode, _argTwo: ExprNode) extends BinaryOperationNode
case class LessThanNode(_argOne: ExprNode, _argTwo: ExprNode) extends BinaryOperationNode
case class LessEqualNode(_argOne: ExprNode, _argTwo: ExprNode) extends BinaryOperationNode
case class EqualToNode(_argOne: ExprNode, _argTwo: ExprNode) extends BinaryOperationNode
case class NotEqualNode(_argOne: ExprNode, _argTwo: ExprNode) extends BinaryOperationNode
case class LogicalAndNode(_argOne: ExprNode, _argTwo: ExprNode) extends BinaryOperationNode
case class LogicalOrNode(_argOne: ExprNode, _argTwo: ExprNode) extends BinaryOperationNode

sealed abstract class UnaryOperationNode extends ExprNode {
  override def getIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = {
    case LogicalNotNode(_) => BoolTypeNode.getIdentifier(topST, _)
    case NegateNode(_) => BoolTypeNode.getIdentifier(topST, _)
    case LenNode(_) => IntTypeNode.getIdentifier(topST, _)
    case OrdNode(_) => IntTypeNode.getIdentifier(topST, _)
    case ChrNode(_) => CharTypeNode.getIdentifier(topST, _)
  }
  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    case LogicalNotNode(expr) => checkHelper(expr, "bool", topST, ST)
    case NegateNode(expr) => checkHelper(expr, "int", topST, ST)
    case LenNode(expr) => lenHelper(expr, topST, ST)
    case OrdNode(expr) => checkHelper(expr, "char", topST, ST)
    case ChrNode(expr) => checkHelper(expr, "int", topST, ST)
  }

  private def checkHelper(expr: ExprNode, expected: String, topST: SymbolTable, ST: SymbolTable): Unit = {
    val identifier: IDENTIFIER = expr.getIdentifier(topST, ST)
    if (identifier != topST.lookup(expected).get){
      throw new TypeException("Expected " + expected + " but got " + identifier)
    }
  }
  private def lenHelper(expr: ExprNode, topST: SymbolTable, ST: SymbolTable): Unit = {
    val identifier: IDENTIFIER = expr.getIdentifier(topST, ST)
    if (! identifier.isInstanceOf[ARRAY]) {
      throw new TypeException("Expected an array but got " + identifier)
    }
  }
}

case class LogicalNotNode(_expr: ExprNode) extends UnaryOperationNode
case class NegateNode(_expr: ExprNode) extends UnaryOperationNode
case class LenNode(_expr: ExprNode) extends UnaryOperationNode
case class OrdNode(_expr: ExprNode) extends UnaryOperationNode
case class ChrNode(_expr: ExprNode) extends UnaryOperationNode