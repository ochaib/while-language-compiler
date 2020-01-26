// Every node necessary to generate AST. From the WACCLangSpec.

class ASTNode {

}

class ProgramNode extends ASTNode {

  // Functions in the program: <func>*.
  val functions: Array[FuncNode]
  // Stat in the program: <stat>.
  val stat: StatNode

}

class FuncNode extends ASTNode {

  val funcType: TypeNode
  val ident: IdentNode
  val paramListElems: Array[Param]
  val stat: StatNode

}

class StatNode extends ASTNode {

}

class SkipNode extends StatNode {

}

class DeclarationNode extends StatNode {

}

class AssignmentNode extends StatNode {

  val assignLeftNode: AssignmentLeftNode
  val assignRightNode: AssignmentRightNode

}

class ReadNode extends StatNode {

  val assignLeftNode: AssignmentLeftNode

}

class FreeNode extends StatNode {

  val expr: ExprNode

}

class ReturnNode extends StatNode {

  val expr: ExprNode

}

class ExitNode extends StatNode {

  val expr: ExprNode

}

class PrintNode extends StatNode {

  val expr: ExprNode

}

class PrintlnNode extends StatNode {

  val expr: ExprNode

}

class IfNode extends StatNode {

  val expr: ExprNode
  // Two stat nodes, one for then one for else.
  val statNodes: Array[StatNode]

}

class WhileNode extends StatNode {

  val expr: ExprNode
  val stat: StatNode

}

class BeginNode extends StatNode {

  val stat: StatNode

}

class SequenceNode extends StatNode {

  val statNodes: Array[StatNode]

}

class AssignmentLeftNode extends ASTNode {

}

class AssignmentRightNode extends ASTNode {

}

class NewPairNode extends AssignmentRightNode {

  val exprNodes: Array[ExprNode]

}

class CallNode extends AssignmentRightNode {

  val ident: IdentNode
  // How do I make this optional?
  val argList: ArgListNode

}

class ArgListNode extends ASTNode {

  val exprNodes: Array[ExprNode]

}

// Shouldn't be able to instantiate this.
class PairElemNode extends AssignmentLeftNode with AssignmentRightNode {

}

class FstNode extends PairElemNode {

  val expr: ExprNode

}

class SndNode extends PairElemNode {

  val expr: ExprNode

}

class ExprNode extends AssignmentRightNode {

}

class IntLiteralNode extends ExprNode {

  val value: Int

}

class BoolLiteralNode extends ExprNode {

  val value: Boolean
}

class CharLiteralNode extends ExprNode {

  val value: Char

}

class StringLiteralNode extends ExprNode {

  val value: String

}

class PairLiteralNode extends ExprNode {

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

class ArrayTypeNode extends TypeNode with PairElemTypeNode {

  val typeNode: TypeNode

}

class PairTypeNode extends TypeNode {

  val firstPairElem: PairElemTypeNode
  val secondPairElem: PairElemTypeNode

}

class PairElemTypeNode extends ASTNode {



}

class IdentNode extends ExprNode with AssignmentLeftNode {

}

class ArrayElemNode extends ExprNode with AssignmentLeftNode {

  val ident: IdentNode
  val expr: ExprNode

}

class ArrayLiteralNode extends AssignmentRightNode {

  val exprNodes: Array[ExprNode]

}

class BinaryOperationNode extends ExprNode {

  val argOne: ExprNode
  val argTwo: ExprNode

}

class MultiplyNode extends BinaryOperationNode {

}

class DivideNode extends BinaryOperationNode {

}

class ModNode extends BinaryOperationNode {

}

class PlusNode extends BinaryOperationNode {

}

class MinusNode extends BinaryOperationNode {

}

class GreaterThanNode extends BinaryOperationNode {

}

class GreaterEqualNode extends BinaryOperationNode {

}

class LessThanNode extends BinaryOperationNode {

}

class LessEqualNode extends BinaryOperationNode {

}

class DoubleEqualNode extends BinaryOperationNode {

}

class NotEqualNode extends BinaryOperationNode {

}

class LogicalAndNode extends BinaryOperationNode {

}

class LogicalOrNode extends BinaryOperationNode {

}

class UnaryOperationNode extends ExprNode {

  val arg: ExprNode

}

class LogicalNotNode extends UnaryOperationNode {

}

class NegateNode extends UnaryOperationNode {

}

class LenNode extends UnaryOperationNode {

}

class OrdNode extends UnaryOperationNode {

}

class ChrNode extends UnaryOperationNode {

}