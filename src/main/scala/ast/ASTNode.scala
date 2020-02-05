package ast

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
  override def getIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = ???

  override def getKey: String = _paramType.getKey
}

abstract class StatNode extends ASTNode with Checkable {

}

class SkipNode extends StatNode {
  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {}
}

class DeclarationNode(val _type: TypeNode, val identNode: IdentNode, val _rhs: AssignRHSNode)
  extends StatNode {
  override def check(topST:SymbolTable, ST: SymbolTable): Unit = {
    val typeIdentifier: IDENTIFIER = _type.getIdentifier(topST, ST)
    _rhs.check(topST, ST)

    // If the type and the rhs dont match, throw exception
    if (typeIdentifier != _rhs.getIdentifier(topST, ST)) {
      throw new TypeException(typeIdentifier.toString + " expected but got " + _rhs.getIdentifier(topST, ST).toString)
    } else if (ST.lookup(identNode.identKey).isDefined) {
      // If variable is already defined throw exception
      throw new TypeException(identNode.identKey + " has already been declared")
    } else {
      ST.add(identNode.identKey, new VARIABLE(identNode.getKey, typeIdentifier.asInstanceOf[TYPE]))
    }
  }
}

class AssignmentNode(val _lhs: AssignLHSNode, val _rhs: AssignRHSNode) extends StatNode {

  val lhs: AssignLHSNode = _lhs
  val rhs: AssignRHSNode = _rhs

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    _lhs.check(topST, ST)
    _rhs.check(topST, ST)

    if (_lhs.getIdentifier(topST, ST) != _rhs.getIdentifier(topST, ST)){
      throw new TypeException(_lhs.getKey + " and " + _rhs.getKey + " have non-matching types")
    }
  }
}

class ReadNode(val _lhs: AssignLHSNode) extends StatNode {
  // Ensure the read statement can only handle character or integer input.

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {

    _lhs.check(topST, ST)

    if (!(_lhs.getIdentifier(topST, ST) == IntTypeNode.getIdentifier(topST, ST)
      || _lhs.getIdentifier(topST, ST) == CharTypeNode.getIdentifier(topST, ST))) {
      throw new TypeException(s"Semantic Error: ${ _lhs.getKey} must be either a character or an integer.")
    }
  }
}

class FreeNode(val _expr: ExprNode) extends StatNode {
  // Call check on the freeNode which should check that the expression in the freeNode
  // is either a pair or an array.

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    _expr.check(topST, ST)

    val exprIdentifier = _expr.getIdentifier(topST, ST)

    if (!(exprIdentifier.isInstanceOf[PAIR] || exprIdentifier == GENERAL_PAIR) ||
        !exprIdentifier.isInstanceOf[ARRAY]) {
      throw new TypeException(s"Semantic Error: ${ _expr.getKey} must be a pair or an array.")
    }
  }
}

class ReturnNode(val _expr: ExprNode) extends StatNode {
  // TODO: Check that return statement is present in body of non-main function.
  // TODO: Check that the type of expression given to the return statement must
  // TODO: match the return type of the expression.

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    _expr.check(topST, ST)
  }
}

class ExitNode(val _expr: ExprNode) extends StatNode {

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    _expr.check(topST, ST)

    val exprIdentifier = _expr.getIdentifier(topST, ST)

    if (!(_expr.getIdentifier(topST, ST) == IntTypeNode.getIdentifier(topST, ST))) {
      throw new TypeException(s"Semantic Error: ${ _expr.getKey} must be an integer.")
    }
  }
}

class PrintNode(val _expr: ExprNode) extends StatNode {

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    _expr.check(topST, ST)
  }

}

class PrintlnNode(val _expr: ExprNode) extends StatNode {
  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    _expr.check(topST, ST)
  }
}

class IfNode(val _conditionExpr: ExprNode, val _thenStat: StatNode, val _elseStat: StatNode) extends StatNode {
  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    _conditionExpr.check(topST, ST)

    val conditionIdentifier = _conditionExpr.getIdentifier(topST, ST)

    if (!(conditionIdentifier == BoolTypeNode.getIdentifier(topST, ST))) {
      throw new TypeException(s"Semantic Error: ${ _conditionExpr.getKey} must evaluate to a boolean.")
    }
  }
}

class WhileNode(val _expr: ExprNode, val _stat: StatNode) extends StatNode {
  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    _expr.check(topST, ST)

    val conditionIdentifier = _expr.getIdentifier(topST, ST)

    if (!(conditionIdentifier == BoolTypeNode.getIdentifier(topST, ST))) {
      throw new TypeException(s"Semantic Error: ${ _expr.getKey} must evaluate to a boolean.")
    }
  }
}

class BeginNode(val _stat: StatNode) extends StatNode {
  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    // Check statement, new st needed as new scope is created.
    _stat.check(topST, ST)
  }
}

class SequenceNode(val _statOne: StatNode, val _statTwo: StatNode) extends StatNode {

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    _statOne.check(topST, ST)
    _statTwo.check(topST, ST)
  }
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

  // TODO lookup and check if it's defined, if not exception, if it is return it
  override def getIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = ???

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    identNode.check(topST, ST)
    // TODO check through each of argList
  }

  override def getKey: String = identNode.identKey

  override def initKey = ???

  override def initIdentifier(topST: SymbolTable, ST: SymbolTable) = ???
}

class ArgListNode(val _exprNodes: IndexedSeq[ExprNode]) extends ASTNode

abstract class PairElemNode(val _expr: ExprNode) extends AssignLHSNode with AssignRHSNode {
  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    val pairIdentifier: IDENTIFIER = _expr.getIdentifier(topST, ST)
    if (! pairIdentifier.isInstanceOf[PAIR]) {
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

  // TODO
  override def initKey: String = {
    if (! _expr.isInstanceOf[Pair_literNode]) {
      throw new TypeException(s"Expected a pair type but got a non-pair type: ${_expr.getKey}")
    } else {
      _expr.asInstanceOf[Pair_literNode].getKey
    }
  }
}

class SndNode(_expr: ExprNode) extends PairElemNode(_expr) {

  override def getIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = {
    val pairIdentifier: IDENTIFIER = _expr.getIdentifier(topST, ST)
    if (! pairIdentifier.isInstanceOf[PAIR]) {
      throw new TypeException("Expected pair type but got a non-pair type")
    } else {
      pairIdentifier.asInstanceOf[PAIR]._type2
    }
  }

  override def getKey: String = ???

  override def initKey = ???

  override def initIdentifier(topST: SymbolTable, ST: SymbolTable) = ???
}

abstract class ExprNode extends AssignRHSNode {
  override def check(topST: SymbolTable, ST: SymbolTable): Unit = this match {
    case Int_literNode(_, _) =>
    case Bool_literNode(_) =>
    case Char_literNode(_) =>
    case Str_literNode(_) =>
  }

  override def initIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = this match {
    case Int_literNode(_, _) => IntTypeNode.getIdentifier(topST, ST)
    case Bool_literNode(_) => BoolTypeNode.getIdentifier(topST, ST)
    case Char_literNode(_) => CharTypeNode.getIdentifier(topST, ST)
    case Str_literNode(_) => StringTypeNode.getIdentifier(topST, ST)
  }

  override def initKey: String = this match {
    case Int_literNode(_, _) => IntTypeNode.getKey
    case Bool_literNode(_) => BoolTypeNode.getKey
    case Char_literNode(_) => CharTypeNode.getKey
    case Str_literNode(_) => StringTypeNode.getKey
  }
}

case class Int_literNode(_intSign: Option[Char],  _digits: IndexedSeq[Int]) extends ExprNode
case class Bool_literNode(_value: Boolean) extends ExprNode
case class Char_literNode(_value: Char) extends ExprNode
case class Str_literNode(_characters: IndexedSeq[Char]) extends ExprNode

class Pair_literNode extends ExprNode {
  override def initIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = {
    val T: Option[IDENTIFIER] = topST.lookup(getKey)
    assert(T.isDefined, "Base or General Type Identifiers MUST be predefined in the top level symbol table")
    assert(T.get.isInstanceOf[TYPE], "Base type identifiers must be an instance of TYPE")
    T.get.asInstanceOf[TYPE]
  }
  override def initKey: String = "pair"
}

class ParenExprNode(_expr: ExprNode) extends ExprNode {
  override def check(topST: SymbolTable, ST: SymbolTable): Unit = _expr.check(topST, ST)

  override def getIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = _expr.getIdentifier(topST, ST)
}

// looks up the type identifier from all parent symbol tables and returns the appropriate identifier object
trait Identifiable {
  protected var key: String = null
  protected var identifier: IDENTIFIER = null

  def getKey: String = {
    if (key == null) {
      initKey
    }
    key
  }

  def getIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = {
    // if null initialise
    if (identifier == null) {
      initIdentifier(topST, ST)
    }
    identifier
  }

  def initKey: String
  def initIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER
}

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
      val arrayIdentifier = {
        new ARRAY(_typeNode.getIdentifier(topST, ST).asInstanceOf[TYPE])
      }
      topST.add(toString, arrayIdentifier)
      arrayIdentifier
    } else {
      assert(T.get.isInstanceOf[ARRAY], "Something went wrong... " + toString + " should be a type but isn't")
      T.get
    }
  }
}

class PairTypeNode(val _firstPairElem: PairElemTypeNode, val _secondPairElem: PairElemTypeNode) extends TypeNode {
  override def initKey: String = "pair (" + _firstPairElem.getKey + "," + _secondPairElem.getKey + ")"
  override def initIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = {
    val identifierLookupOption: Option[IDENTIFIER]
  }
}

// <pair-elem-type> in the WACCLangSpec
trait PairElemTypeNode extends ASTNode with Identifiable


// 'pair' in the WACCLangSpec
class PairElemTypePairNode extends PairElemTypeNode {
  override def initKey: String = "pair"

  override def initIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = ???
}


class IdentNode(val identKey: String) extends ExprNode with AssignLHSNode {
  override def getKey: String = identKey

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    if (ST.lookupAll(toString).isEmpty){
      throw new TypeException(toString + " has not been declared")
    }
  }

  override def getIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = {
    val T: Option[IDENTIFIER] = ST.lookupAll(toString)
    if (T.isEmpty) {
      throw new TypeException(toString + " has not been declared")
    } else if (! T.get.isInstanceOf[VARIABLE]) {
      assert(assertion = false, "Something went wrong... " + toString + " should be a variable but isn't")
      null
    } else {
      T.get.asInstanceOf[VARIABLE]
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

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = ???

  override def getIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = ???

  override def initKey = ???

  override def initIdentifier(topST: SymbolTable, ST: SymbolTable) = ???
}

sealed abstract class BinaryOperationNode extends ExprNode {
  override def getIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = this match {
    case MultiplyNode(_, _) => IntTypeNode.getIdentifier(topST, ST)
    case DivideNode(_, _) => IntTypeNode.getIdentifier(topST, ST)
    case ModNode(_, _) => IntTypeNode.getIdentifier(topST, ST)
    case PlusNode(_, _) => IntTypeNode.getIdentifier(topST, ST)
    case MinusNode(_, _) => IntTypeNode.getIdentifier(topST, ST)
    case GreaterEqualNode(_, _) => BoolTypeNode.getIdentifier(topST, ST)
    case LessThanNode(_, _) => BoolTypeNode.getIdentifier(topST, ST)
    case LessEqualNode(_, _) => BoolTypeNode.getIdentifier(topST, ST)
    case EqualToNode(_, _) => BoolTypeNode.getIdentifier(topST, ST)
    case NotEqualNode(_, _) => BoolTypeNode.getIdentifier(topST, ST)
    case LogicalAndNode(_, _) => BoolTypeNode.getIdentifier(topST, ST)
    case LogicalOrNode(_, _) => BoolTypeNode.getIdentifier(topST, ST)
    case _ => {
      assert(assertion = false, "Binary identifier not defined")
      null
    }
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
  override def getIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = this match {
    case LogicalNotNode(_) => BoolTypeNode.getIdentifier(topST, ST)
    case NegateNode(_) => BoolTypeNode.getIdentifier(topST, ST)
    case LenNode(_) => IntTypeNode.getIdentifier(topST, ST)
    case OrdNode(_) => IntTypeNode.getIdentifier(topST, ST)
    case ChrNode(_) => CharTypeNode.getIdentifier(topST, ST)
    case _ => {
      assert(assertion = false, "unaccounted for unary getIdentifier")
      null
    }
  }
  override def check(topST: SymbolTable, ST: SymbolTable): Unit = this match {
    case LogicalNotNode(expr: ExprNode) => checkHelper(expr, "bool", topST, ST)
    case NegateNode(expr: ExprNode) => checkHelper(expr, "int", topST, ST)
    case LenNode(expr: ExprNode) => lenHelper(expr, topST, ST)
    case OrdNode(expr: ExprNode) => checkHelper(expr, "char", topST, ST)
    case ChrNode(expr: ExprNode) => checkHelper(expr, "int", topST, ST)
    case _ => assert(assertion = false, "unaccounted for unary check")
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