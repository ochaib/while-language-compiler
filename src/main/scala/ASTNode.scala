class ASTNode {

}

class ProgramNode extends ASTNode {

}

class FuncNode extends ASTNode {

}

class AssignmentLeftNode extends ASTNode {

}

class AssignmentRightNode extends AstNode {

}

class NewPairNode extends AssignmentRightNode {

}

class CallNode extends AssignmentRightNode {

}

class ArgListNode extends ASTNode {

}

class PairElemNode extends AssignmentLeftNode with AssignmentRightNode {

}

class FstNode extends PairElemNode {

}

class SndNode extends PairElemNode {

}

class ExprNode extends AssignmentRightNode {

}

class IntLiteralNode extends ExprNode {

}

class BoolLiteralNode extends ExprNode {

}

class CharLiteralNode extends ExprNode {

}

class StringLiteralNode extends ExprNode {

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

}

class PairTypeNode extends TypeNode {

}

class PairElemTypeNode extends ASTNode {

}

class InnerPairTypeNode extends PairElemTypeNode {

}

class IdentNode extends ExprNode with AssignmentLeftNode {

}

class ArrayElemNode extends ExprNode with AssignmentLeftNode {

}

class ArrayLiteralNode extends AssignmentRightNode {

}

class BinaryOperationNode extends ExprNode {

}

class MulOperationNode extends BinaryOperationNode {

}

class DivOperationNode extends BinaryOperationNode {

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