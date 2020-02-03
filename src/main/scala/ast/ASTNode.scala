package ast;
import util.{ColoredConsole => console}

// Every node necessary to generate AST. From the WACCLangSpec.

class ASTNode {
  override def toString: String = console.color("<NODE>", fg=Console.RED)
}

class ProgramNode(val _stat: StatNode, val _functions: IndexedSeq[FuncNode]) extends ASTNode {

  // Functions in the program: <func>*.
  val functions: IndexedSeq[FuncNode] = _functions
  // Stat in the program: <stat>.
  // TODO: use option here as it could be null
  val stat: StatNode = _stat

  override def toString: String = {
    val funcs : String = functions.map(_.toString).mkString("\n  ")
    val begin: String = console.color("begin", fg=Console.BLUE)
    val end: String = console.color("end", fg=Console.BLUE)
    if (stat != null)
      s"$begin\n$funcs\n${stat.toString}\n$end"
    else
      s"$begin\n$funcs\n$end"
  }
}

class FuncNode(val _funcType: TypeNode, val _ident: IdentNode, val _paramList: ParamListNode,
               val _stat: StatNode) extends ASTNode {

  val funcType: TypeNode = _funcType
  val ident: IdentNode = _ident
  val paramList: ParamListNode = _paramList
  val stat: StatNode = _stat

  override def toString: String = {
    val params: String = if (paramList != null) paramList.toString else ""
    val stats: String = if (stat != null) stat.toString else ""
    s"${funcType.toString} ${ident.toString} (${params}) is\n${stats}\nend"
  }
}

class ParamListNode(val _paramList: IndexedSeq[ParamNode]) extends ASTNode {

  val paramList: IndexedSeq[ParamNode] = _paramList

  override def toString: String = paramList.map(_.toString).mkString(", ")
}

class ParamNode(val _paramType: TypeNode, val _ident: IdentNode) extends ASTNode {

  val paramType: TypeNode = _paramType
  val ident: IdentNode = _ident

  override def toString: String = s"${paramType.toString} ${ident.toString}"
}

class StatNode extends ASTNode {
  override def toString: String = console.color("<STATEMENT>", fg=Console.RED)
}

class SkipNode extends StatNode {
  override def toString: String = console.color("skip", fg=Console.BLUE)
}

class DeclarationNode(val _type: TypeNode, val _ident: IdentNode, val _rhs: AssignRHSNode) extends StatNode {

  val typeNode: TypeNode = _type
  val ident: IdentNode = _ident
  val rhs: AssignRHSNode = _rhs

  override def toString: String = s"${typeNode.toString} ${ident.toString} = ${rhs.toString}"
}

class AssignmentNode(val _lhs: AssignLHSNode, val _rhs: AssignRHSNode) extends StatNode {

  val lhs: AssignLHSNode = _lhs
  val rhs: AssignRHSNode = _rhs

  override def toString: String = s"${lhs.toString} = ${rhs.toString}"
}

class ReadNode(val _lhs: AssignLHSNode) extends StatNode {

  val lhs: AssignLHSNode = _lhs

  override def toString: String = console.color("read", fg=Console.BLUE) + s"(${lhs.toString})"
}

class FreeNode(val _expr: ExprNode) extends StatNode {

  val expr: ExprNode = _expr

  override def toString: String = console.color("free", fg=Console.BLUE) + s"(${expr.toString})"
}

class ReturnNode(val _expr: ExprNode) extends StatNode {

  val expr: ExprNode = _expr

  override def toString: String = console.color("return", fg=Console.BLUE) + s"(${expr.toString})"
}

class ExitNode(val _expr: ExprNode) extends StatNode {

  val expr: ExprNode = _expr

  override def toString: String = console.color("exit", fg=Console.BLUE) + s"(${expr.toString})"
}

class PrintNode(val _expr: ExprNode) extends StatNode {

  val expr: ExprNode = _expr

  override def toString: String = console.color("print", fg=Console.BLUE) + s"(${expr.toString})"
}

class PrintlnNode(val _expr: ExprNode) extends StatNode {

  val expr: ExprNode = _expr

  override def toString: String = console.color("println", fg=Console.BLUE) + s"(${expr.toString})"
}

class IfNode(val _conditionExpr: ExprNode, val _thenStat: StatNode, val _elseStat: StatNode)
  extends StatNode {

  val conditionExpr: ExprNode = _conditionExpr
  // Two stat nodes, one for then one for else.
  val thenStat: StatNode = _thenStat
  val elseStat: StatNode = _elseStat

  override def toString: String = {
    val if_ : String = console.color("if", fg=Console.BLUE)
    val then_ : String = console.color("then", fg=Console.BLUE)
    val else_ : String = console.color("else", fg=Console.BLUE)
    s"$if_ (${conditionExpr.toString}) $then_ {\n${thenStat.toString}\n} $else_ {\n${elseStat.toString}\n}"
  }
}

class WhileNode(val _expr: ExprNode, val _stat: StatNode) extends StatNode {

  val expr: ExprNode = _expr
  val stat: StatNode = _stat

  override def toString: String = console.color(s"while (${expr.toString}) {\n${stat.toString}\n}", fg=Console.YELLOW)
}

class BeginNode(val _stat: StatNode) extends StatNode {

  val stat: StatNode = _stat

  override def toString: String = {
    val begin: String = console.color("begin", fg=Console.BLUE)
    val end: String = console.color("end", fg=Console.BLUE)
    s"$begin\n${stat.toString}\n$end"
  }
}

class SequenceNode(val _statOne: StatNode, val _statTwo: StatNode) extends StatNode {

  val statOne: StatNode = _statOne
  val statTwo: StatNode = _statTwo

  override def toString: String = s"${statOne.toString}\n${statTwo.toString}"
}

// Both of these need to be traits (abstract classes) in order to be extended later.
trait AssignLHSNode extends ASTNode {
  override def toString: String = console.color("<LHS>", fg=Console.RED)
}

trait AssignRHSNode extends ASTNode {
  override def toString: String = console.color("<RHS>", fg=Console.RED)
}

class NewPairNode(val _fstElem: ExprNode, val _sndElem: ExprNode) extends AssignRHSNode {

  val fstElem: ExprNode = _fstElem
  val sndElem: ExprNode = _sndElem

  override def toString: String = console.color(s"newpair (${fstElem.toString}, ${sndElem.toString})", fg=Console.BLUE)
}

class CallNode(val _ident: IdentNode, val _argList: Option[ArgListNode]) extends AssignRHSNode {

  val ident: IdentNode = _ident
  // How do I make this optional?
  val argList: Option[ArgListNode] = _argList

  override def toString: String = argList match {
    case Some(args) => console.color(s"call ${ident.toString} (${args.toString})", fg=Console.BLUE)
    case None => console.color(s"call ${ident.toString} ()", fg=Console.BLUE)
  }
}

class ArgListNode(val _exprNodes: IndexedSeq[ExprNode]) extends ASTNode {

  val exprNodes: IndexedSeq[ExprNode] = _exprNodes

  override def toString: String = exprNodes.map(_.toString).mkString(", ")
}

// Shouldn't be able to instantiate this.
trait PairElemNode extends AssignLHSNode with AssignRHSNode {
  override def toString: String = console.color("<PAIR ELEM>", fg=Console.RED)
}

class FstNode(val _expr: ExprNode) extends PairElemNode {

  val expr: ExprNode = _expr

  override def toString: String = console.color(s"fst ${expr.toString}", fg=Console.BLUE)
}

class SndNode(val _expr: ExprNode) extends PairElemNode {

  val expr: ExprNode = _expr

  override def toString: String = console.color(s"snd ${expr.toString}", fg=Console.BLUE)
}

class ExprNode extends AssignRHSNode {
  override def toString: String = console.color("<EXPR>", fg=Console.RED)
}

class Int_literNode(val _intSign: Option[Char], val _digits: IndexedSeq[Int]) extends ExprNode {

  val intSign: Option[Char] = _intSign
  val digits: IndexedSeq[Int] = _digits

  override def toString: String = intSign match {
    case Some(s) => console.color(s"${intSign} ${digits.map(_.toString).mkString("")}", fg=Console.MAGENTA)
    case None => console.color(digits.map(_.toString).mkString(""), fg=Console.MAGENTA)
  }
}

class Bool_literNode(val _value: Boolean) extends ExprNode {

  val value: Boolean = _value

  override def toString: String = value match {
    case true => console.color("true", fg=Console.MAGENTA)
    case false => console.color("false", fg=Console.MAGENTA)
  }
}

class Char_literNode(val _value: Char) extends ExprNode {

  val value: Char = _value

  override def toString: String = console.color(s"'$value'", fg=Console.YELLOW)
}

class Str_literNode(val _characters: IndexedSeq[Char]) extends ExprNode {

  val characters: IndexedSeq[Char] = _characters

  override def toString: String = "\"" + console.color(characters.mkString(""), fg=Console.YELLOW) + "\""
}

class Pair_literNode extends ExprNode {
  override def toString: String = console.color("null", fg=Console.MAGENTA)
}

class ParenExprNode extends ExprNode {
  override def toString: String = console.color("<PAREN EXPR>", fg=Console.RED)
}

class TypeNode extends ASTNode {
  override def toString: String = console.color("<TYPE>", fg=Console.RED)
}

class BaseTypeNode extends TypeNode with PairElemTypeNode {
  override def toString: String = console.color("<BASE TYPE>", fg=Console.RED)
}

class IntTypeNode extends BaseTypeNode {
  override def toString: String = console.color("int", fg=Console.BLUE)
}

class BoolTypeNode extends BaseTypeNode {
  override def toString: String = console.color("bool", fg=Console.BLUE)
}

class CharTypeNode extends BaseTypeNode {
  override def toString: String = console.color("char", fg=Console.BLUE)
}

class StringTypeNode extends BaseTypeNode {
  override def toString: String = console.color("string", fg=Console.BLUE)
}

class ArrayTypeNode(val _typeNode: TypeNode) extends TypeNode with PairElemTypeNode {

  val typeNode: TypeNode = _typeNode

  override def toString: String = typeNode.toString
}

class PairTypeNode(val _firstPairElem: PairElemTypeNode, val _secondPairElem: PairElemTypeNode) extends TypeNode {

  val firstPairElem: PairElemTypeNode = _firstPairElem
  val secondPairElem: PairElemTypeNode = _secondPairElem

  override def toString: String = console.color("pair", fg=Console.BLUE) +
    s"(${firstPairElem.toString}, ${secondPairElem.toString})"
}

trait PairElemTypeNode extends ASTNode {
  override def toString: String = console.color("<PAIR ELEM>", fg=Console.RED)
}

class PairElemTypePairNode extends PairElemTypeNode {
  override def toString: String = console.color("<PAIR ELEM TYPE PAIR>", fg=Console.RED)
}

class IdentNode(val _ident: String) extends ExprNode with AssignLHSNode {
  val ident: String = _ident

  override def toString: String = console.color(ident, fg=Console.GREEN)
}

class ArrayElemNode(val _ident: IdentNode, val _exprNodes: IndexedSeq[ExprNode]) extends ExprNode with AssignLHSNode {

  val ident: IdentNode = _ident
  val exprNodes: IndexedSeq[ExprNode] = _exprNodes

  override def toString: String = {
    val exprs : String = exprNodes.map("[" + _.toString + "]").mkString("")
    s"${ident.toString}${exprs}"
  }
}

class ArrayLiteralNode(val _exprNodes: IndexedSeq[ExprNode]) extends AssignRHSNode {

  val exprNodes: IndexedSeq[ExprNode] = _exprNodes

  override def toString: String = "[" + exprNodes.map(_.toString).mkString(", ") + "]"
}

trait BinaryOperationNode extends ExprNode {

  def argOne: ExprNode
  def argTwo: ExprNode

  override def toString: String = argOne.toString + " " + console.color("<BINARY-OP>", fg=Console.RED) + " " + argTwo.toString
}

class MultiplyNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {
  override def argOne: ExprNode = _argOne

  override def argTwo: ExprNode = _argTwo

  override def toString: String = s"${argOne.toString} * ${argTwo.toString}"
}

class DivideNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {
  override def argOne: ExprNode = _argOne

  override def argTwo: ExprNode = _argTwo

  override def toString: String = s"${argOne.toString} / ${argTwo.toString}"
}

class ModNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {
  override def argOne: ExprNode = _argOne

  override def argTwo: ExprNode = _argTwo

  override def toString: String = s"${argOne.toString} % ${argTwo.toString}"
}

class PlusNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {
  override def argOne: ExprNode = _argOne

  override def argTwo: ExprNode = _argTwo

  override def toString: String = s"${argOne.toString} + ${argTwo.toString}"
}

class MinusNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {
  override def argOne: ExprNode = _argOne

  override def argTwo: ExprNode = _argTwo

  override def toString: String = s"${argOne.toString} - ${argTwo.toString}"
}

class GreaterThanNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {
  override def argOne: ExprNode = _argOne

  override def argTwo: ExprNode = _argTwo

  override def toString: String = s"${argOne.toString} > ${argTwo.toString}"
}

class GreaterEqualNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {
  override def argOne: ExprNode = _argOne

  override def argTwo: ExprNode = _argTwo

  override def toString: String = s"${argOne.toString} >= ${argTwo.toString}"
}

class LessThanNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {
  override def argOne: ExprNode = _argOne

  override def argTwo: ExprNode = _argTwo

  override def toString: String = s"${argOne.toString} < ${argTwo.toString}"
}

class LessEqualNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {
  override def argOne: ExprNode = _argOne

  override def argTwo: ExprNode = _argTwo

  override def toString: String = s"${argOne.toString} <= ${argTwo.toString}"
}

class EqualToNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {
  override def argOne: ExprNode = _argOne

  override def argTwo: ExprNode = _argTwo

  override def toString: String = s"${argOne.toString} = ${argTwo.toString}"
}

class NotEqualNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {
  override def argOne: ExprNode = _argOne

  override def argTwo: ExprNode = _argTwo

  override def toString: String = s"${argOne.toString} != ${argTwo.toString}"
}

class LogicalAndNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {
  override def argOne: ExprNode = _argOne

  override def argTwo: ExprNode = _argTwo

  override def toString: String = s"${argOne.toString} && ${argTwo.toString}"
}

class LogicalOrNode(val _argOne: ExprNode, val _argTwo: ExprNode) extends BinaryOperationNode {
  override def argOne: ExprNode = _argOne

  override def argTwo: ExprNode = _argTwo

  override def toString: String = s"${argOne.toString} || ${argTwo.toString}"
}

trait UnaryOperationNode extends ExprNode {
  def expr: ExprNode

  override def toString: String = console.color(s"<UNARY OPER> ${expr.toString}", fg=Console.RED)
}

class LogicalNotNode(val _expr: ExprNode) extends UnaryOperationNode {
  override def expr: ExprNode = _expr

  override def toString: String = s"!${expr.toString}"
}

class NegateNode(val _expr: ExprNode) extends UnaryOperationNode {
  override def expr: ExprNode = _expr

  override def toString: String = s"-${expr.toString}"
}

class LenNode(val _expr: ExprNode) extends UnaryOperationNode {
  override def expr: ExprNode = _expr

  override def toString: String = s"len ${expr.toString}"
}

class OrdNode(val _expr: ExprNode) extends UnaryOperationNode {
  override def expr: ExprNode = _expr

  override def toString: String = s"ord ${expr.toString}"
}

class ChrNode(val _expr: ExprNode) extends UnaryOperationNode {
  override def expr: ExprNode = _expr

  override def toString: String = s"chr ${expr.toString}"
}