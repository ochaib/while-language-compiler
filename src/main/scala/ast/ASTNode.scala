package ast

import util.{ColoredConsole => console}

// Every node necessary to generate AST. From the WACCLangSpec.

class ASTNode {
  def toTreeString: String = console.color("<NODE>", fg=Console.RED)
  override def toString: String = this.toTreeString
}

case class ProgramNode(functions: IndexedSeq[FuncNode], stat: StatNode) extends ASTNode {

  override def toTreeString: String = {
    val funcs : String = functions.map(_.toString).mkString("\n")
    val begin: String = console.color("begin", fg=Console.BLUE)
    val end: String = console.color("end", fg=Console.BLUE)
    s"$begin\n$funcs\n${stat.toString}\n$end"
  }
}

case class FuncNode(funcType: TypeNode, identNode: IdentNode, paramList: Option[ParamListNode],
                    stat: StatNode) extends ASTNode {

  override def toTreeString: String = paramList match {
    case Some(params) => s"${funcType.toString} ${identNode.toString} (${params.toString}) is\n${stat.toString}\nend"
    case None => s"${funcType.toString} ${identNode.toString} () is\n${stat.toString}\nend"
  }
}

case class ParamListNode(val paramList: IndexedSeq[ParamNode]) extends ASTNode {

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

case class ParamNode(paramType: TypeNode, identNode: IdentNode) extends ASTNode with Identifiable {

  override def initType(topST: SymbolTable, ST: SymbolTable): TYPE = {
    paramType.getType(topST, ST)
    if (ST.lookup(identNode.getKey).isDefined) {
      // If variable is already defined throw exception
      throw new TypeException(s"${identNode.getKey} has already been declared")
    } else {
      val paramIdentifier: PARAM = new PARAM(identNode.getKey, paramType.getType(topST, ST).asInstanceOf[TYPE])
      ST.add(identNode.getKey, paramIdentifier)
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

case class NewPairNode(fstElem: ExprNode, sndElem: ExprNode) extends AssignRHSNode {
  override def initType(topST: SymbolTable, ST: SymbolTable): TYPE = {
    val newPairIdentifierLookup: Option[IDENTIFIER] = topST.lookup(getKey)
    if (newPairIdentifierLookup.isDefined) {
      assert(newPairIdentifierLookup.get.isInstanceOf[PAIR],
        s"Expected instance of pair for ${newPairIdentifierLookup.get.getKey}")
      newPairIdentifierLookup.get.asInstanceOf[PAIR]
    } else {
      val newIdentifier = new PAIR(getKey, getElemIdentifier(fstElem, topST, ST).asInstanceOf[TYPE],
        getElemIdentifier(sndElem, topST, ST).asInstanceOf[TYPE])
      topST.add(getKey, newIdentifier)
      newIdentifier
    }
  }

  private def getElemIdentifier(elemNode: ExprNode, topST: SymbolTable, ST: SymbolTable): IDENTIFIER = {
    val elemIdentifier = elemNode.getType(topST, ST)
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

  override def toTreeString: String = console.color(s"newpair (${fstElem.toString}, ${sndElem.toString})", fg=Console.BLUE)
}

case class CallNode(identNode: IdentNode, argList: Option[ArgListNode]) extends AssignRHSNode {

  override def initType(topST: SymbolTable, ST: SymbolTable): TYPE = identNode.getType(topST, ST)

  override def initKey: String = identNode.getKey

  override def toTreeString: String = argList match {
    case Some(args) => console.color(s"call ${identNode.toString} (${args.toString})", fg=Console.BLUE)
    case None => console.color(s"call ${identNode.toString} ()", fg=Console.BLUE)
  }
}

case class ArgListNode(val exprNodes: IndexedSeq[ExprNode]) extends ASTNode {
  override def toTreeString: String = exprNodes.map(_.toString).mkString(", ")
}

abstract class PairElemNode(expr: ExprNode) extends ASTNode with AssignLHSNode with AssignRHSNode {

  override def toTreeString: String = console.color("<PAIR ELEM>", fg=Console.RED)
}

case class FstNode(expression: ExprNode) extends PairElemNode(expression) {

  override def initType(topST: SymbolTable, ST: SymbolTable): TYPE = {
    val pairIdentifier: IDENTIFIER = expression.getType(topST, ST)
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

  override def toTreeString: String = console.color(s"fst ${expression.toString}", fg=Console.BLUE)
}

case class SndNode(expression: ExprNode) extends PairElemNode(expression) {

  override def initType(topST: SymbolTable, ST: SymbolTable): TYPE = {
    val pairIdentifier: IDENTIFIER = expression.getType(topST, ST)
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

  override def toTreeString: String = console.color(s"snd ${expression.toString}", fg=Console.BLUE)
}

case class IdentNode(ident: String) extends ExprNode with AssignLHSNode {
  override def initKey: String = ident

  override def initType(topST: SymbolTable, ST: SymbolTable): TYPE = {
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

  override def toTreeString: String = console.color(ident, fg=Console.GREEN)
}

case class ArrayElemNode(identNode: IdentNode, exprNodes: IndexedSeq[ExprNode]) extends ExprNode with AssignLHSNode {

  override def toTreeString: String = {
    val exprs : String = exprNodes.map("[" + _.toString + "]").mkString("")
    s"${identNode.toString}$exprs"
  }
}

case class ArrayLiteralNode(exprNodes: IndexedSeq[ExprNode]) extends AssignRHSNode {

  override def initType(topST: SymbolTable, ST: SymbolTable): TYPE = {
    val arrayIdentifierOption: Option[IDENTIFIER] = topST.lookup(getKey)
    if (arrayIdentifierOption.isEmpty) {
      val arrayIdentifier = new ARRAY(getKey, exprNodes.apply(0).getType(topST, ST).asInstanceOf[TYPE])
      topST.add(toString, arrayIdentifier)
      arrayIdentifier
    } else {
      assert(arrayIdentifierOption.get.isInstanceOf[ARRAY], s"Something went wrong... $getKey should be a an array type but isn't")
      arrayIdentifierOption.get.asInstanceOf[ARRAY]
    }
  }

  override def toTreeString: String = "[" + exprNodes.map(_.toString).mkString(", ") + "]"

  override def initKey: String = exprNodes.apply(0).getKey + "[]"
}