package ast;
import ast._
import util.{ColoredConsole => console}

// Every node necessary to generate AST. From the WACCLangSpec.

class ASTNode {
  override def toString: String = console.color("<NODE>", fg=Console.RED)
}

class ProgramNode(val _stat: StatNode, val _functions: IndexedSeq[FuncNode]) extends ASTNode {

  // Functions in the program: <func>*.
  val functions: IndexedSeq[FuncNode] = _functions
  // Stat in the program: <stat>.
  val stat: StatNode = _stat

  override def toString: String = {
    val funcs : String = functions.map(_.toString).mkString("\n")
    val begin: String = console.color("begin", fg=Console.BLUE)
    val end: String = console.color("end", fg=Console.BLUE)
    s"$begin\n$funcs\n${stat.toString}\n$end"
  }
}

class FuncNode(val funcType: TypeNode, val identNode: IdentNode, val paramList: Option[ParamListNode],
               val stat: StatNode) extends ASTNode with Checkable {

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    val typeIdentifier: TYPE = funcType.getIdentifier(topST, ST).asInstanceOf[TYPE]
    if (ST.lookup(identNode.getKey).isDefined){
      throw new TypeException("function " + identNode.getKey + " has already been defined")
    } else {
      paramList match {
        case Some(params) => ST.add(identNode.getKey, new FUNCTION(identNode.getKey, typeIdentifier, params.getIdentifierList(ST)))
        case None => ST.add(identNode.getKey, new FUNCTION(identNode.getKey, typeIdentifier, IndexedSeq[TYPE]()))
      }
    }
  }

  override def toString: String = paramList match {
    case Some(params) => s"${funcType.toString} ${identNode.toString} (${params.toString}) is\n${stat.toString}\nend"
    case None => s"${funcType.toString} ${identNode.toString} () is\n${stat.toString}\nend"
  }
}

class ParamListNode(val paramList: IndexedSeq[ParamNode]) extends ASTNode {

  // TODO: loop through the _paramList and return a list of the TYPE IDENTIFIERS
  def getIdentifierList(ST: SymbolTable): IndexedSeq[TYPE] = null

  override def toString: String = paramList.map(_.toString).mkString(", ")
}

class ParamNode(val paramType: TypeNode, val identNode: IdentNode) extends ASTNode with Identifiable {

  override def initIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = paramType.getIdentifier(topST, ST)

  override def initKey: String = paramType.getKey

  override def toString: String = s"${paramType.toString} ${identNode.toString}"
}

// Both of these need to be traits (abstract classes) in order to be extended later.
trait AssignLHSNode extends ASTNode with Checkable with Identifiable {
  override def toString: String = console.color("<LHS>", fg=Console.RED)
}

trait AssignRHSNode extends ASTNode with Checkable with Identifiable {
  override def toString: String = console.color("<RHS>", fg=Console.RED)
}

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

  override def toString: String = console.color(s"newpair (${fstElem.toString}, ${sndElem.toString})", fg=Console.BLUE)
}

class CallNode(val identNode: IdentNode, val argList: Option[ArgListNode]) extends AssignRHSNode {

  // TODO lookup and check if it's defined, if not exception, if it is return it
  override def initIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = identNode.getIdentifier(topST, ST)

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    identNode.check(topST, ST)
    // TODO check through each of argList
  }
  override def initKey: String = identNode.getKey

  override def toString: String = argList match {
    case Some(args) => console.color(s"call ${identNode.toString} (${args.toString})", fg=Console.BLUE)
    case None => console.color(s"call ${identNode.toString} ()", fg=Console.BLUE)
  }
}

class ArgListNode(val exprNodes: IndexedSeq[ExprNode]) extends ASTNode {
  override def toString: String = exprNodes.map(_.toString).mkString(", ")
}

abstract class PairElemNode(val expr: ExprNode) extends AssignLHSNode with AssignRHSNode {
  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    val pairIdentifier: IDENTIFIER = expr.getIdentifier(topST, ST)
    if (! pairIdentifier.isInstanceOf[PAIR]) {
      throw new TypeException("Expected pair type but got " + pairIdentifier)
    } else {
      expr.check(topST, ST)
    }
  }

  override def toString: String = console.color("<PAIR ELEM>", fg=Console.RED)
}

class FstNode(expr: ExprNode) extends PairElemNode(expr: ExprNode) {

  override def initIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = {
    val pairIdentifier: IDENTIFIER = expr.getIdentifier(topST, ST)
    if (! pairIdentifier.isInstanceOf[PAIR]) {
      throw new TypeException(s"Expected pair type but got a non-pair type: ${expr.getKey}}")
    } else {
      pairIdentifier.asInstanceOf[PAIR]._type1
    }
  }

  override def initKey: String = {
    val exprKey: String = expr.getKey
    if (expr == Pair_literNode) {
      // TODO in backend throw error
      throw new TypeException(s"Expected a pair type but got a null pair literal instead")
    } else if (exprKey.slice(0, 1) != "(" || ")" != exprKey.slice(exprKey.length() - 1, exprKey.length)) {
      throw new TypeException(s"Expected a pair type but got a non-pair type: ${expr.getKey}")
    } else {
      exprKey.slice(1, exprKey.indexOf(','))
    }
  }

  override def toString: String = console.color(s"fst ${expr.toString}", fg=Console.BLUE)
}

class SndNode(expr: ExprNode) extends PairElemNode(expr) {

  override def initIdentifier(topST: SymbolTable, ST: SymbolTable): IDENTIFIER = {
    val pairIdentifier: IDENTIFIER = expr.getIdentifier(topST, ST)
    if (! pairIdentifier.isInstanceOf[PAIR]) {
      throw new TypeException("Expected pair type but got a non-pair type")
    } else {
      pairIdentifier.asInstanceOf[PAIR]._type2
    }
  }


  override def initKey: String = {
    val exprKey: String = expr.getKey
    if (expr == Pair_literNode) {
      // TODO in backend throw error
      throw new TypeException(s"Expected a pair type but got a null pair literal instead")
    } else if (exprKey.slice(0, 1) != "(" || ")" != exprKey.slice(exprKey.length() - 1, exprKey.length)) {
      throw new TypeException(s"Expected a pair type but got a non-pair type: ${expr.getKey}")
    } else {
      exprKey.slice(exprKey.indexOf(',') + 1, exprKey.length)
    }
  }

  override def toString: String = console.color(s"snd ${expr.toString}", fg=Console.BLUE)
}

class IdentNode(val ident: String) extends ExprNode with AssignLHSNode {
  override def initKey: String = ident

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    if (ST.lookupAll(toString).isEmpty){
      throw new TypeException(s"$toString has not been declared")
    }
  }

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

class ArrayElemNode(val identNode: IdentNode, val exprNodes: IndexedSeq[ExprNode]) extends ExprNode with AssignLHSNode {

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    identNode.check(topST, ST)
    val identIdentifier: IDENTIFIER = identNode.getIdentifier(topST, ST)
    for (expr <- exprNodes) expr.check(topST, ST)
    if (! identIdentifier.isInstanceOf[ARRAY]) {
      throw new TypeException(s"Expected array type but got ${identIdentifier.getKey} instead")
    } else {
      val identArrayType: IDENTIFIER = identIdentifier.asInstanceOf[ARRAY]._type
      for (expr <- exprNodes) {
        val exprIdentifier: IDENTIFIER = expr.getIdentifier(topST, ST)
        if (exprIdentifier != identArrayType) {
          throw new TypeException(s"Expected ${identArrayType.getKey} but got ${exprIdentifier.getKey} instead")
        }
      }
    }
  }

  override def toString: String = {
    val exprs : String = exprNodes.map("[" + _.toString + "]").mkString("")
    s"${identNode.toString}${exprs}"
  }
}

class ArrayLiteralNode(val exprNodes: IndexedSeq[ExprNode]) extends AssignRHSNode {

  override def check(topST: SymbolTable, ST: SymbolTable): Unit = {
    val firstIdentifier: IDENTIFIER = exprNodes.apply(0).getIdentifier(topST, ST)
    for (expr <- exprNodes) {
      val exprIdentifier = expr.getIdentifier(topST, ST)
      if (exprIdentifier != firstIdentifier) {
        throw new TypeException(s"Expected type ${firstIdentifier.getKey} but got ${exprIdentifier.getKey}")
      }
    }
  }

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