package ast.nodes

import ast.symboltable._
import org.antlr.v4.runtime.Token
import util.{SemanticErrorLog, ColoredConsole => console}
import scala.util.control.Breaks._

// Every node necessary to generate AST. From the WACCLangSpec.

class ASTNode(token: Token) {
  def getPos(token: Token): String = s"at ${token.getLine}:${token.getCharPositionInLine}"
  def toTreeString: String = console.color("<NODE>", fg=Console.RED)
  override def toString: String = this.toTreeString
}

case class ProgramNode(token: Token, functions: IndexedSeq[FuncNode], stat: StatNode) extends ASTNode(token) {

  override def toTreeString: String = {
    val funcs : String = functions.map(_.toString).mkString("\n")
    val begin: String = console.color("begin", fg=Console.BLUE)
    val end: String = console.color("end", fg=Console.BLUE)
    s"$begin\n$funcs\n${stat.toString}\n$end"
  }
}

case class FuncNode(token: Token, funcType: TypeNode, identNode: IdentNode, paramList: Option[ParamListNode],
                    stat: StatNode) extends ASTNode(token) {

  override def toTreeString: String = paramList match {
    case Some(params) => s"${funcType.toString} ${identNode.toString} (${params.toString}) is\n${stat.toString}\nend"
    case None => s"${funcType.toString} ${identNode.toString} () is\n${stat.toString}\nend"
  }
}

case class ParamListNode(token: Token, paramList: IndexedSeq[ParamNode]) extends ASTNode(token) {

  def getIdentifierList(topST: SymbolTable, ST: SymbolTable): IndexedSeq[TYPE] = {
    assert(paramList.nonEmpty, "Parameter lists have to be at least size 1")
    var identifierList: IndexedSeq[IDENTIFIER] = Vector()
    for (param <- paramList) identifierList = {
      identifierList :+ param.getType(topST, ST)
    }
    identifierList.asInstanceOf[IndexedSeq[TYPE]]
  }

  override def toTreeString: String = paramList.map(_.toString).mkString(", ")
}

case class ParamNode(token: Token, paramType: TypeNode, identNode: IdentNode) extends ASTNode(token) with Identifiable {

  override def initType(topST: SymbolTable, ST: SymbolTable): TYPE = {
    paramType.getType(topST, ST)
    if (ST.lookup(identNode.getKey).isDefined) {
      // If variable is already defined throw exception
      // Can't change this exception a log because a TYPE would need to be returned.
      SemanticErrorLog.add(s"${getPos(token)} ${identNode.getKey} has already been declared")
      null
    } else {
      val paramIdentifier: PARAM = new PARAM(identNode.getKey, paramType.getType(topST, ST))
      // ST.add(identNode.getKey, paramIdentifier)
      paramIdentifier._type
    }
  }

  override def initKey: String = paramType.getKey

  override def toTreeString: String = s"${paramType.toString} ${identNode.toString}"
}

// Both of these need to be traits (abstract classes) in order to be extended later.
trait AssignLHSNode extends ASTNode with Identifiable {
  override def toTreeString: String = console.color("<LHS>", fg=Console.RED)
}

trait AssignRHSNode extends ASTNode with Identifiable {
  override def toTreeString: String = console.color("<RHS>", fg=Console.RED)
}

case class NewPairNode(token: Token, fstElem: ExprNode, sndElem: ExprNode) extends ASTNode(token) with AssignRHSNode {
  override def initType(topST: SymbolTable, ST: SymbolTable): TYPE = {
    val fstElemType: TYPE = getElemIdentifier(fstElem, topST, ST).asInstanceOf[TYPE]
    val sndElemType: TYPE = getElemIdentifier(sndElem, topST, ST).asInstanceOf[TYPE]
    val newPairIdentifierLookup: Option[IDENTIFIER] = topST.lookup(getKey)
    if (newPairIdentifierLookup.isDefined) {
      assert(newPairIdentifierLookup.get.isInstanceOf[PAIR],
        s"Expected instance of pair for ${newPairIdentifierLookup.get.getKey}.")
      newPairIdentifierLookup.get.asInstanceOf[PAIR]
    } else {
      val newIdentifier = new PAIR(getKey, fstElemType, sndElemType)
      topST.add(getKey, newIdentifier)
      newIdentifier
    }
  }

  private def getElemIdentifier(elemNode: ExprNode, topST: SymbolTable, ST: SymbolTable): IDENTIFIER = {
    val elemIdentifier = elemNode.getType(topST, ST)
    /*if (elemIdentifier.isInstanceOf[PAIR] || elemIdentifier == GENERAL_PAIR) {
      GENERAL_PAIR
    } else {*/
      elemIdentifier
    //}
  }

  override def initKey: String = s"pair(${getElemKey(fstElem)},${getElemKey(sndElem)})"

  private def getElemKey(elemNode: ExprNode): String = {
    var elemKey: String = elemNode.getKey
    elemNode match {
      case node: IdentNode =>
        elemKey = node.getTypeKey
      case _ =>
    }
    /*if (elemKey.startsWith("pair")) {
      "pair"
    } else {*/
      elemKey
    //}
  }

  override def toTreeString: String = console.color(s"newpair (${fstElem.toString}, ${sndElem.toString})", fg=Console.BLUE)
}

case class CallNode(token: Token, identNode: IdentNode, argList: Option[ArgListNode]) extends ASTNode(token) with AssignRHSNode {

  override def initType(topST: SymbolTable, ST: SymbolTable): TYPE = {
    val key = {
      if (argList.isDefined) {
        SymbolTable.makeFunctionKey(identNode, argList.get.exprNodes.map(_.getType(topST, ST)))
      } else SymbolTable.makeFunctionKey(identNode, IndexedSeq())
    }
    val F: Option[FUNCTION] = ST.lookupFunAll(key)
    if (F.isEmpty) {
      SemanticErrorLog.add(s"${getPos(token)} $getKey has not been declared as a function.")
      null
    } else if (! F.get.isInstanceOf[FUNCTION]) {
      assert(assertion = false, s"Something went wrong... $getKey should be a function but isn't.")
      null
    } else {
      F.get.returnType
    }
  }

  override def initKey: String = identNode.getKey

  override def toTreeString: String = argList match {
    case Some(args) => console.color(s"call ${identNode.toString} (${args.toString})", fg=Console.BLUE)
    case None => console.color(s"call ${identNode.toString} ()", fg=Console.BLUE)
  }
}

case class ArgListNode(token: Token, exprNodes: IndexedSeq[ExprNode]) extends ASTNode(token) {
  override def toTreeString: String = exprNodes.map(_.toString).mkString(", ")
}

abstract class PairElemNode(token: Token, expr: ExprNode) extends ASTNode(token) with AssignLHSNode with AssignRHSNode {

  override def toTreeString: String = console.color("<PAIR ELEM>", fg=Console.RED)
}

case class FstNode(token: Token, expression: ExprNode) extends PairElemNode(token, expression) {

  override def initType(topST: SymbolTable, ST: SymbolTable): TYPE = {
    val pairIdentifier: IDENTIFIER = expression.getType(topST, ST)
    if (! pairIdentifier.isInstanceOf[PAIR]) {
      SemanticErrorLog.add(s"${getPos(token)} expected pair type but got a non-pair type: ${expression.getKey}.")
      null
    } else {
      pairIdentifier.asInstanceOf[PAIR]._type1
    }
  }


  override def initKey: String = {
    var exprKey: String = expression.getKey
    expression match {
      case node: IdentNode =>
        exprKey = node.getTypeKey
        exprKey = exprKey.slice(4, exprKey.length)
      case _ =>
    }
    if (expression.isInstanceOf[Pair_literNode]) {
      // TODO in backend throw error
      SemanticErrorLog.add(s"${getPos(token)} expected a pair type but got a null pair literal instead.")
      "Semantic Error: Should not reach this."
    } else if (exprKey.slice(0, 1) != "(" || ")" != exprKey.slice(exprKey.length() - 1, exprKey.length)) {
      SemanticErrorLog.add(s"${getPos(token)} expected a pair type but got a non-pair type: ${expression.getKey}.")
      "Semantic Error: Should not reach this."
    } else {
      exprKey.slice(exprKey.indexOf(',') + 1, exprKey.length - 1)
    }
  }
  override def toTreeString: String = console.color(s"fst ${expression.toString}", fg=Console.BLUE)
}

case class SndNode(token: Token, expression: ExprNode) extends PairElemNode(token, expression) {

  override def initType(topST: SymbolTable, ST: SymbolTable): TYPE = {
    val pairIdentifier: IDENTIFIER = expression.getType(topST, ST)
    if (! pairIdentifier.isInstanceOf[PAIR]) {
      SemanticErrorLog.add(s"${getPos(token)} expected pair type but got a non-pair type.")
      null
    } else {
      pairIdentifier.asInstanceOf[PAIR]._type2
    }
  }


  override def initKey: String = {
    var exprKey: String = expression.getKey
    expression match {
      case node: IdentNode =>
        exprKey = node.getTypeKey
        exprKey = exprKey.slice(4, exprKey.length)
      case _ =>
    }
    if (expression.isInstanceOf[Pair_literNode]) {
      // TODO in backend throw error
      SemanticErrorLog.add(s"${getPos(token)} expected a pair type but got a null pair literal instead.")
      "Semantic Error: Should not reach this."
    } else if (exprKey.slice(0, 1) != "(" || ")" != exprKey.slice(exprKey.length() - 1, exprKey.length)) {
      SemanticErrorLog.add(s"${getPos(token)} expected a pair type but got a non-pair type: ${expression.getKey}.")
      "Semantic Error: Should not reach this."
    } else {
      exprKey.slice(exprKey.indexOf(',') + 1, exprKey.length - 1)
    }
  }

  override def toTreeString: String = console.color(s"snd ${expression.toString}", fg=Console.BLUE)
}

case class IdentNode(token: Token, ident: String) extends ExprNode(token) with AssignLHSNode {
  var typeKey: String = _
  def getTypeKey: String = typeKey

  override def initKey: String = ident

  override def initType(topST: SymbolTable, ST: SymbolTable): TYPE = {
    val T: Option[IDENTIFIER] = ST.lookupAll(getKey)
    if (T.isEmpty) {
      SemanticErrorLog.add(s"${getPos(token)} $getKey has not been declared.")
      null
    } else if (! (T.get.isInstanceOf[VARIABLE] || T.get.isInstanceOf[PARAM])) {
      assert(assertion = false, s"Something went wrong... $getKey should be a variable or parameter but isn't")
      null
    } else {
      T.get match {
        case variable: VARIABLE =>
          val variableType: TYPE = variable._type
          typeKey = variableType.getKey
          variableType
        case param: PARAM =>
          val paramType: TYPE = param._type
          typeKey = paramType.getKey
          paramType
        case _ =>
          assert(assertion = false, "Above if statement should prevent getting here")
          null
      }
    }
  }

  def resetType(topST: SymbolTable, ST: SymbolTable): TYPE = {
    _type = initType(topST, ST)
    _type
  }

  override def toTreeString: String = console.color(ident, fg=Console.GREEN)
}

case class ArrayElemNode(token: Token, identNode: IdentNode, exprNodes: IndexedSeq[ExprNode]) extends ExprNode(token) with AssignLHSNode {
  var innerMostKey: String = _
  override def initKey: String = {
    assert(innerMostKey != null, "Type of array elem node has to be initialised before a key can be found.")
    innerMostKey
  }

  override def initType(topST: SymbolTable, ST: SymbolTable): TYPE = {
    var innermostType: TYPE = identNode.getType(topST, ST)
    breakable {
      for (_ <- exprNodes.indices) {
        if (innermostType == STRING) {
          SemanticErrorLog.add(s"${getPos(token)} Array elem $toString is trying to access an element of a string")
          break
        }
        if (!innermostType.isInstanceOf[ARRAY]) SemanticErrorLog.add(s"${getPos(token)} array elem $toString refers to an undefined depth.")
        innermostType = innermostType.asInstanceOf[ARRAY]._type
      }
    }
    innerMostKey = innermostType.getKey
    innermostType
  }

  override def toTreeString: String = {
    val exprs : String = exprNodes.map("[" + _.toString + "]").mkString("")
    s"${identNode.toString}$exprs"
  }
}

case class ArrayLiteralNode(token: Token, exprNodes: IndexedSeq[ExprNode]) extends ASTNode(token) with AssignRHSNode {

  override def initType(topST: SymbolTable, ST: SymbolTable): TYPE = {
    val arrayIdentifierOption: Option[IDENTIFIER] = topST.lookup(getKey)
    if (arrayIdentifierOption.isEmpty) {
      assert(exprNodes.nonEmpty, "General Array needs to be defined in the top level symbol table.")
      val arrayIdentifier = new ARRAY(getKey, exprNodes.apply(0).getType(topST, ST))
      topST.add(getKey, arrayIdentifier)
      arrayIdentifier
    } else {
      assert(arrayIdentifierOption.get.isInstanceOf[ARRAY],
        s"Something went wrong... $getKey should be a an array type but isn't.")
      arrayIdentifierOption.get.asInstanceOf[ARRAY]
    }
  }

  override def toTreeString: String = "[" + exprNodes.map(_.toString).mkString(", ") + "]"

  override def initKey: String = {
    if (exprNodes.nonEmpty && exprNodes.apply(0).isInstanceOf[IdentNode]) {
      exprNodes.apply(0).asInstanceOf[IdentNode].getTypeKey + "[]"
    } else if (exprNodes.nonEmpty) {
      exprNodes.apply(0).getKey + "[]"
    } else "[]"
  }
}