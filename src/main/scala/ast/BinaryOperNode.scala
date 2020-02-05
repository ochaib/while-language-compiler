package ast
import ast.{IDENTIFIER, SymbolTable, TypeException}
import util.{ColoredConsole => console}

sealed abstract class BinaryOperationNode(argOne: ExprNode, argTwo: ExprNode) extends ExprNode {
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
  }

  override def check(topST: SymbolTable, ST: SymbolTable): Unit ={
    val intIdentifier: IDENTIFIER = IntTypeNode.getIdentifier(topST, ST)
    val boolIdentifier: IDENTIFIER = BoolTypeNode.getIdentifier(topST, ST)
    val charIdentifier: IDENTIFIER = CharTypeNode.getIdentifier(topST, ST)
    this match {
      case MultiplyNode(argOne, argTwo) => binaryCheckerHelper(argOne, argTwo, intIdentifier, intIdentifier, topST, ST)
      case DivideNode(argOne, argTwo) => binaryCheckerHelper(argOne, argTwo, intIdentifier, intIdentifier, topST, ST)
      case ModNode(argOne, argTwo) => binaryCheckerHelper(argOne, argTwo, intIdentifier, intIdentifier, topST, ST)
      case PlusNode(argOne, argTwo) => binaryCheckerHelper(argOne, argTwo, intIdentifier, intIdentifier, topST, ST)
      case MinusNode(argOne, argTwo) => binaryCheckerHelper(argOne, argTwo, intIdentifier, intIdentifier, topST, ST)
      case GreaterThanNode(argOne, argTwo) => comparatorsCheckerHelper(argOne, argTwo, intIdentifier, charIdentifier, topST, ST)
      case GreaterEqualNode(argOne, argTwo) => comparatorsCheckerHelper(argOne, argTwo, intIdentifier, charIdentifier, topST, ST)
      case LessThanNode(argOne, argTwo) => comparatorsCheckerHelper(argOne, argTwo, intIdentifier, charIdentifier, topST, ST)
      case LessEqualNode(argOne, argTwo) => comparatorsCheckerHelper(argOne, argTwo, intIdentifier, charIdentifier, topST, ST)
      case EqualToNode(argOne, argTwo) => binaryCheckerHelper(argOne, argTwo,
      argOne.getIdentifier(topST, ST), argOne.getIdentifier(topST, ST), topST, ST)
      case NotEqualNode(argOne, argTwo) => binaryCheckerHelper(argOne, argTwo,
        argOne.getIdentifier(topST, ST), argOne.getIdentifier(topST, ST), topST, ST)
      case LogicalAndNode(argOne, argTwo) => binaryCheckerHelper(argOne, argTwo, boolIdentifier, boolIdentifier, topST, ST)
      case LogicalOrNode(argOne, argTwo) => binaryCheckerHelper(argOne, argTwo, boolIdentifier, boolIdentifier, topST, ST)
    }
  }

  override def toString: String = this match {
    case MultiplyNode(argOne, argTwo) => s"${argOne.toString} * ${argTwo.toString}"
    case DivideNode(argOne, argTwo) => s"${argOne.toString} / ${argTwo.toString}"
    case ModNode(argOne, argTwo) => s"${argOne.toString} % ${argTwo.toString}"
    case PlusNode(argOne, argTwo) => s"${argOne.toString} + ${argTwo.toString}"
    case MinusNode(argOne, argTwo) => s"${argOne.toString} - ${argTwo.toString}"
    case GreaterThanNode(argOne, argTwo) => s"${argOne.toString} > ${argTwo.toString}"
    case GreaterEqualNode(argOne, argTwo) =>  s"${argOne.toString} >= ${argTwo.toString}"
    case LessThanNode(argOne, argTwo) => s"${argOne.toString} < ${argTwo.toString}"
    case LessEqualNode(argOne, argTwo) => s"${argOne.toString} <= ${argTwo.toString}"
    case EqualToNode(argOne, argTwo) => s"${argOne.toString} == ${argTwo.toString}"
    case NotEqualNode(argOne, argTwo) => s"${argOne.toString} != ${argTwo.toString}"
    case LogicalAndNode(argOne, argTwo) => s"${argOne.toString} && ${argTwo.toString}"
    case LogicalOrNode(argOne, argTwo) => s"${argOne.toString} || ${argTwo.toString}"
  }
  private def comparatorsCheckerHelper(argOne: ExprNode, argTwo: ExprNode,
                                      expectedIdentifier1: IDENTIFIER, expectedIdentifier2: IDENTIFIER, topST: SymbolTable, ST: SymbolTable): Unit = {
    val argOneIdentifier: IDENTIFIER = argOne.getIdentifier(topST, ST)
    val argTwoIdentifier: IDENTIFIER = argTwo.getIdentifier(topST, ST)
    if (! ((argOneIdentifier == expectedIdentifier1 || argOneIdentifier == expectedIdentifier2)
      && (argTwoIdentifier == expectedIdentifier1 || argTwoIdentifier == expectedIdentifier2))) {
      throw new TypeException(s"Expected input types ${expectedIdentifier1.getKey} or ${expectedIdentifier2.getKey}" +
        s" but got ${argOneIdentifier.getKey} and ${argTwoIdentifier.getKey} instead")
    }
  }

  private def binaryCheckerHelper(argOne: ExprNode, argTwo: ExprNode, expectedIdentifier1: IDENTIFIER,
                                  expectedIdentifier2: IDENTIFIER, topST: SymbolTable, ST: SymbolTable): Unit = {
    val argOneIdentifier: IDENTIFIER = argOne.getIdentifier(topST, ST)
    val argTwoIdentifier: IDENTIFIER = argTwo.getIdentifier(topST, ST)
    if (! (argOneIdentifier == expectedIdentifier1 && argTwoIdentifier == expectedIdentifier2)) {
      throw new TypeException(s"Expected input types ${expectedIdentifier1.getKey} and ${expectedIdentifier2.getKey}" +
        s" but got ${argOneIdentifier.getKey} and ${argTwoIdentifier.getKey} instead")
    }
  }
}

case class MultiplyNode(argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode(argOne, argTwo)
case class DivideNode(argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode(argOne, argTwo)
case class ModNode(argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode(argOne, argTwo)
case class PlusNode(argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode(argOne, argTwo)
case class MinusNode(argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode(argOne, argTwo)
case class GreaterThanNode(argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode(argOne, argTwo)
case class GreaterEqualNode(argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode(argOne, argTwo)
case class LessThanNode(argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode(argOne, argTwo)
case class LessEqualNode(argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode(argOne, argTwo)
case class EqualToNode(argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode(argOne, argTwo)
case class NotEqualNode(argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode(argOne, argTwo)
case class LogicalAndNode(argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode(argOne, argTwo)
case class LogicalOrNode(argOne: ExprNode, argTwo: ExprNode) extends BinaryOperationNode(argOne, argTwo)
