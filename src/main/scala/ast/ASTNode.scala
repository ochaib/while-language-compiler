package ast

import ast._
import util.{ColoredConsole => console}

// Every node necessary to generate AST. From the WACCLangSpec.

abstract class ASTNode {
  override def toString: String = super.toString
}

case class ProgramNode(functions: IndexedSeq[FuncNode], val stat: StatNode) extends ASTNode {

  override def toString: String = {
    val funcs : String = functions.map(_.toString).mkString("\n")
    val begin: String = console.color("begin", fg=Console.BLUE)
    val end: String = console.color("end", fg=Console.BLUE)
    s"$begin\n$funcs\n${stat.toString}\n$end"
  }
}

case class FuncNode(funcType: TypeNode, identNode: IdentNode, paramList: Option[ParamListNode],
                    stat: StatNode) extends ASTNode {

  override def toString: String = paramList match {
    case Some(params) => s"${funcType.toString} ${identNode.toString} (${params.toString}) is\n${stat.toString}\nend"
    case None => s"${funcType.toString} ${identNode.toString} () is\n${stat.toString}\nend"
  }
}

case class ParamListNode(paramList: IndexedSeq[ParamNode]) extends ASTNode {

  def getIdentifierList(topST: SymbolTable, ST: SymbolTable): IndexedSeq[TYPE] = {
    assert(paramList.nonEmpty, "Parameter lists have to be at least size 1")
    var identifierList: IndexedSeq[IDENTIFIER] = Vector()
    for (param <- paramList) identifierList = {
      identifierList :+ param.getIdentifier(topST, ST)
    }
    identifierList.asInstanceOf[IndexedSeq[TYPE]]
  }

  override def toString: String = paramList.map(_.toString).mkString(", ")
}

case class ParamNode(paramType: TypeNode, identNode: IdentNode) extends ASTNode with Identifiable {

  override def initIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = paramType.getIdentifier(topST, ST)

  override def initKey: String = paramType.getKey

  override def toString: String = s"${paramType.toString} ${identNode.toString}"
}

// Both of these need to be traits (abstract classes) in order to be extended later.
trait AssignLHSNode extends ASTNode with Identifiable {
  override abstract def toString: String = console.color("<LHS>", fg=Console.RED)
}

trait AssignRHSNode extends ASTNode with Identifiable {
  override abstract def toString: String = console.color("<RHS>", fg=Console.RED)
}

case class NewPairNode(fstElem: ExprNode, val sndElem: ExprNode) extends AssignRHSNode {
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

  private def getElemIdentifier(elemNode: ExprNode, topST: SymbolTable, ST: SymbolTable): IDENTIFIER = {
    val elemIdentifier = elemNode.getIdentifier(topST, ST)
    if (elemIdentifier.isInstanceOf[PAIR] || elemIdentifier == GENERAL_PAIR) {
      GENERAL_PAIR
    } else {
      elemIdentifier
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

  override def toString: String = console.color(s"newpair (${fstElem.toString}, ${sndElem.toString})", fg=Console.BLUE)
}

case class CallNode(identNode: IdentNode, argList: Option[ArgListNode]) extends AssignRHSNode {

  override def initIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = identNode.getIdentifier(topST, ST)

  override def initKey: String = identNode.getKey

  override def toString: String = argList match {
    case Some(args) => console.color(s"call ${identNode.toString} (${args.toString})", fg=Console.BLUE)
    case None => console.color(s"call ${identNode.toString} ()", fg=Console.BLUE)
  }
}

case class ArgListNode(exprNodes: IndexedSeq[ExprNode]) extends ASTNode {
  override def toString: String = exprNodes.map(_.toString).mkString(", ")
}

abstract class PairElemNode(expr: ExprNode) extends ASTNode with AssignLHSNode with AssignRHSNode {

  override def toString: String = console.color("<PAIR ELEM>", fg=Console.RED)
}

case class FstNode(expression: ExprNode) extends PairElemNode(expression) {

  override def initIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = {
    val pairIdentifier: IDENTIFIER = expression.getIdentifier(topST, ST)
    if (! pairIdentifier.isInstanceOf[PAIR]) {
      throw new TypeException(s"Expected pair type but got a non-pair type: ${expression.getKey}}")
    } else {
      pairIdentifier.asInstanceOf[PAIR]._type1
    }
  }

  override def initKey: String = {
    val exprKey: String = expression.getKey
    if (expression == Pair_literNode) {
      // TODO in backend throw error
      throw new TypeException(s"Expected a pair type but got a null pair literal instead")
    } else if (exprKey.slice(0, 1) != "(" || ")" != exprKey.slice(exprKey.length() - 1, exprKey.length)) {
      throw new TypeException(s"Expected a pair type but got a non-pair type: ${expression.getKey}")
    } else {
      exprKey.slice(1, exprKey.indexOf(','))
    }
  }

  override def toString: String = console.color(s"fst ${expression.toString}", fg=Console.BLUE)
}

case class SndNode(expression: ExprNode) extends PairElemNode(expression) {

  override def initIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = {
    val pairIdentifier: IDENTIFIER = expression.getIdentifier(topST, ST)
    if (! pairIdentifier.isInstanceOf[PAIR]) {
      throw new TypeException("Expected pair type but got a non-pair type")
    } else {
      pairIdentifier.asInstanceOf[PAIR]._type2
    }
  }


  override def initKey: String = {
    val exprKey: String = expression.getKey
    if (expression == Pair_literNode) {
      // TODO in backend throw error
      throw new TypeException(s"Expected a pair type but got a null pair literal instead")
    } else if (exprKey.slice(0, 1) != "(" || ")" != exprKey.slice(exprKey.length() - 1, exprKey.length)) {
      throw new TypeException(s"Expected a pair type but got a non-pair type: ${expression.getKey}")
    } else {
      exprKey.slice(exprKey.indexOf(',') + 1, exprKey.length)
    }
  }

  override def toString: String = console.color(s"snd ${expression.toString}", fg=Console.BLUE)
}

case class IdentNode(ident: String) extends ExprNode with AssignLHSNode {
  override def initKey: String = ident

  override def initIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = {
    val T: Option[IDENTIFIER] = ST.lookupAll(toString)
    if (T.isEmpty) {
      throw new TypeException(s"$toString has not been declared")
    } else if (! T.get.isInstanceOf[VARIABLE]) {
      assert(assertion = false, s"Something went wrong... $toString should be a variable but isn't")
      null
    } else {
      T.get.asInstanceOf[VARIABLE]._type
    }
  }

  override def toString: String = console.color(ident, fg=Console.GREEN)
}

case class ArrayElemNode(identNode: IdentNode, exprNodes: IndexedSeq[ExprNode]) extends ExprNode with AssignLHSNode {

  override def toString: String = {
    val exprs : String = exprNodes.map("[" + _.toString + "]").mkString("")
    s"${identNode.toString}$exprs"
  }
}

case class ArrayLiteralNode(exprNodes: IndexedSeq[ExprNode]) extends AssignRHSNode {

  override def initIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = {
    val arrayIdentifierOption: Option[IDENTIFIER] = topST.lookup(getKey)
    if (arrayIdentifierOption.isEmpty) {
      val arrayIdentifier = new ARRAY(getKey, exprNodes.apply(0).getIdentifier(topST, ST).asInstanceOf[TYPE])
      topST.add(toString, arrayIdentifier)
      arrayIdentifier
    } else {
      assert(arrayIdentifierOption.get.isInstanceOf[ARRAY], s"Something went wrong... $getKey should be a an array type but isn't")
      arrayIdentifierOption.get
    }
  }

  override def toString: String = "[" + exprNodes.map(_.toString).mkString(", ") + "]"

  override def initKey: String = exprNodes.apply(0).getKey + "[]"
}