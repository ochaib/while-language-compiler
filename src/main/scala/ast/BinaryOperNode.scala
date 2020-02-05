package main.scala.ast

import ast.{IDENTIFIER, SymbolTable, TypeException}

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

  override def check(topST: SymbolTable, ST: SymbolTable): Unit ={
    val intIdentifier: IDENTIFIER = IntTypeNode.getIdentifier(topST, ST)
    val boolIdentifier: IDENTIFIER = BoolTypeNode.getIdentifier(topST, ST)
    val charIdentifier: IDENTIFIER = CharTypeNode.getIdentifier(topST, ST)
    this match {
      case MultiplyNode(_argOne, _argTwo) => binaryCheckerHelper(_argOne, _argTwo, intIdentifier, intIdentifier, topST, ST)
      case DivideNode(_argOne, _argTwo) => binaryCheckerHelper(_argOne, _argTwo, intIdentifier, intIdentifier, topST, ST)
      case ModNode(_argOne, _argTwo) => binaryCheckerHelper(_argOne, _argTwo, intIdentifier, intIdentifier, topST, ST)
      case PlusNode(_argOne, _argTwo) => binaryCheckerHelper(_argOne, _argTwo, intIdentifier, intIdentifier, topST, ST)
      case MinusNode(_argOne, _argTwo) => binaryCheckerHelper(_argOne, _argTwo, intIdentifier, intIdentifier, topST, ST)
      case GreaterThanNode(_argOne, _argTwo) => comparatorsCheckerHelper(_argOne, _argTwo, intIdentifier, charIdentifier, topST, ST)
      case GreaterEqualNode(_argOne, _argTwo) => comparatorsCheckerHelper(_argOne, _argTwo, intIdentifier, charIdentifier, topST, ST)
      case LessThanNode(_argOne, _argTwo) => comparatorsCheckerHelper(_argOne, _argTwo, intIdentifier, charIdentifier, topST, ST)
      case LessEqualNode(_argOne, _argTwo) => comparatorsCheckerHelper(_argOne, _argTwo, intIdentifier, charIdentifier, topST, ST)
      case EqualToNode(_argOne, _argTwo) => binaryCheckerHelper(_argOne, _argTwo,
      _argOne.getIdentifier(topST, ST), _argOne.getIdentifier(topST, ST), topST, ST)
      case NotEqualNode(_argOne, _argTwo) => binaryCheckerHelper(_argOne, _argTwo,
        _argOne.getIdentifier(topST, ST), _argOne.getIdentifier(topST, ST), topST, ST)
      case LogicalAndNode(_argOne, _argTwo) => binaryCheckerHelper(_argOne, _argTwo, boolIdentifier, boolIdentifier, topST, ST)
      case LogicalOrNode(_argOne, _argTwo) => binaryCheckerHelper(_argOne, _argTwo, boolIdentifier, boolIdentifier, topST, ST)
    }
  }

  private def comparatorsCheckerHelper(argOne: ExprNode, argTwo: ExprNode,
                                      expectedIdentifier1: IDENTIFIER, expectedIdentifier2: IDENTIFIER, topST: SymbolTable, ST: SymbolTable): Unit = {
    val argOneIdentifier: IDENTIFIER = argOne.getIdentifier(topST, ST)
    val argTwoIdentifier: IDENTIFIER = argTwo.getIdentifier(topST, ST)
    if (! ((argOneIdentifier == expectedIdentifier1 || argOneIdentifier == expectedIdentifier2)
      && (argTwoIdentifier == expectedIdentifier2 || argTwoIdentifier == expectedIdentifier2))) {
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
