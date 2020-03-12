package ast.visitors

import ast.nodes._
import antlr.{WACCLexer, WACCParser, WACCParserBaseVisitor}
import org.antlr.v4.runtime._
import util.SyntaxErrorLog

// Class used to traverse the parse tree built by ANTLR
class ASTGenerator extends WACCParserBaseVisitor[ASTNode] {

  def debugCtx(ctx: ParserRuleContext) = {
    for (i<-0 until ctx.getChildCount) println(s"$i:: " + ctx.getChild(i).getClass + ": " + ctx.getChild(i).getText)
  }

  override def visitProgram(ctx: WACCParser.ProgramContext): ProgramNode = {
    // Need to retrieve program information from parser context,
    // Need to visit all the functions (as many as there are due to the *)
    // Need to visit the statement "node".
    // ‘begin’ ⟨func⟩* ⟨stat⟩ ‘end’
    //    0     1-n     n+1    n+2
    val childCount = ctx.getChildCount
    val stat: StatNode = visit(ctx.getChild(childCount - 3)).asInstanceOf[StatNode]

    val functions: IndexedSeq[FuncNode] = for (i<-1 until childCount - 3) yield visit(ctx.getChild(i)).asInstanceOf[FuncNode]
    // Then create program node from the two
    ProgramNode(ctx.start, functions, stat)
  }

  override def visitFunc(ctx: WACCParser.FuncContext): FuncNode = {
    // ⟨type⟩ ⟨ident⟩ ‘(’ ⟨param-list⟩? ‘)’ ‘is’ ⟨stat⟩ ‘end’
    val funcType: TypeNode = visit(ctx.getChild(0)).asInstanceOf[TypeNode]
    val ident: IdentNode = visit(ctx.getChild(1)).asInstanceOf[IdentNode]

    val paramList: Option[ParamListNode] = Option(visit(ctx.getChild(3)).asInstanceOf[ParamListNode])
    val statement: StatNode = paramList match {
      case Some(_) => visit(ctx.getChild(6)).asInstanceOf[StatNode]
      case None => visit(ctx.getChild(5)).asInstanceOf[StatNode]
    }

    FuncNode(ctx.start, funcType, ident, paramList, statement)
  }

  override def visitParam_list(ctx: WACCParser.Param_listContext): ParamListNode = {
    // ⟨param⟩ ( ‘,’ ⟨param⟩ )*
    // Multiple params... a param list
    val childCount = ctx.getChildCount

    val paramList: IndexedSeq[ParamNode] =
      for (i <- 0 until childCount; if !ctx.getChild(i).getText.charAt(0).equals(',')) yield
        visit(ctx.getChild(i)).asInstanceOf[ParamNode]

    ParamListNode(ctx.start, paramList)
  }

  override def visitParam(ctx: WACCParser.ParamContext): ParamNode = {
    // ⟨type⟩ ⟨ident⟩
    val paramType: TypeNode = visit(ctx.getChild(0)).asInstanceOf[TypeNode]
    val ident: IdentNode = visit(ctx.getChild(1)).asInstanceOf[IdentNode]

    ParamNode(ctx.start, paramType, ident)
  }

  // Individual components of stat that must be visited.

  override def visitSkip(ctx: WACCParser.SkipContext): SkipNode = {
    SkipNode(ctx.start)
  }

  override def visitDeclaration(ctx: WACCParser.DeclarationContext): DeclarationNode = {
    // Need to get each individual child of the declaration context as its own type.
    val typeNode: TypeNode = visit(ctx.getChild(0)).asInstanceOf[TypeNode]
    val ident: IdentNode = visit(ctx.getChild(1)).asInstanceOf[IdentNode]
    val assignRHS: AssignRHSNode = visit(ctx.getChild(3)).asInstanceOf[AssignRHSNode]

    // Then create DeclarationNode with the above as parameters for constructor
    DeclarationNode(ctx.start, typeNode, ident, assignRHS)
  }

  override def visitAssignment(ctx: WACCParser.AssignmentContext): AssignmentNode = {
    // Get assignLHS and assignRHS then construct AssignNode with them
    val lhs: AssignLHSNode = visit(ctx.getChild(0)).asInstanceOf[AssignLHSNode]
    // Have to consider the equals
    val rhs: AssignRHSNode = visit(ctx.getChild(2)).asInstanceOf[AssignRHSNode]

    AssignmentNode(ctx.start, lhs, rhs)
  }

  // SIDE-EFFECT EXTENSION:
  override def visitSideEffect(ctx: WACCParser.SideEffectContext): SideEffectNode = {
    val ident: IdentNode = visit(ctx.getChild(0)).asInstanceOf[IdentNode]
    val sideEffect: String = ctx.getChild(1).getText
    val expr: ExprNode = visit(ctx.getChild(2)).asInstanceOf[ExprNode]

    sideEffect match {
      case "+=" => AddAssign(ctx.start, ident, expr)
      case "-=" => SubAssign(ctx.start, ident, expr)
      case "*=" => MulAssign(ctx.start, ident, expr)
      case "/=" => DivAssign(ctx.start, ident, expr)
      case "%=" => ModAssign(ctx.start, ident, expr)
    }
  }

  // SHORT-EFFECT EXTENSION:
  override def visitShortEffect(ctx: WACCParser.ShortEffectContext): ShortEffectNode = {
    val ident: IdentNode = visit(ctx.getChild(0)).asInstanceOf[IdentNode]
    val shortEffect: String = ctx.getChild(1).getText

    shortEffect match {
      case "++" => IncrementNode(ctx.start, ident)
      case "--" => DecrementNode(ctx.start, ident)
    }
  }

  override def visitRead(ctx: WACCParser.ReadContext): ReadNode = {
    // ‘read’ ⟨assign-lhs⟩
    val lhs: AssignLHSNode = visit(ctx.getChild(1)).asInstanceOf[AssignLHSNode]

    ReadNode(ctx.start, lhs)
  }

  override def visitFree(ctx: WACCParser.FreeContext): FreeNode = {
    // ‘free’ ⟨expr⟩
    val freeExpr: ExprNode = visit(ctx.getChild(1)).asInstanceOf[ExprNode]

    FreeNode(ctx.start, freeExpr)
  }

  override def visitReturn(ctx: WACCParser.ReturnContext): ReturnNode = {
    // ‘return’ ⟨expr ⟩
    val returnExpr: ExprNode = visit(ctx.getChild(1)).asInstanceOf[ExprNode]

    ReturnNode(ctx.start, returnExpr)
  }

  override def visitExit(ctx: WACCParser.ExitContext): ExitNode = {
    //  ‘exit’ ⟨expr ⟩
    val exitExpr: ExprNode = visit(ctx.getChild(1)).asInstanceOf[ExprNode]

    ExitNode(ctx.start, exitExpr)
  }

  override def visitPrint(ctx: WACCParser.PrintContext): PrintNode = {
    // ‘print’ ⟨expr ⟩
    val printExpr: ExprNode = visit(ctx.getChild(1)).asInstanceOf[ExprNode]

    PrintNode(ctx.start, printExpr)
  }

  override def visitPrintln(ctx: WACCParser.PrintlnContext): PrintlnNode = {
    // ‘println’ ⟨expr ⟩
    val printlnExpr: ExprNode = visit(ctx.getChild(1)).asInstanceOf[ExprNode]

    PrintlnNode(ctx.start, printlnExpr)
  }

  override def visitIf(ctx: WACCParser.IfContext): IfNode = {
  // ‘if’ ⟨expr ⟩ ‘then’ ⟨stat ⟩ ‘else’ ⟨stat ⟩ ‘fi’
    val conditionExpr: ExprNode = visit(ctx.getChild(1)).asInstanceOf[ExprNode]
    val thenStat: StatNode = visit(ctx.getChild(3)).asInstanceOf[StatNode]
    val elseStat: StatNode = visit(ctx.getChild(5)).asInstanceOf[StatNode]

    IfNode(ctx.start, conditionExpr, thenStat, elseStat)
  }

  override def visitWhile(ctx: WACCParser.WhileContext): WhileNode = {
    // ‘while’ ⟨expr ⟩ ‘do’ ⟨stat ⟩ ‘done’
    val conditionExpr: ExprNode = visit(ctx.getChild(1)).asInstanceOf[ExprNode]
    val doStat: StatNode = visit(ctx.getChild(3)).asInstanceOf[StatNode]

    WhileNode(ctx.start, conditionExpr, doStat)
  }

  // DoWhile Extension.
  override def visitDoWhile(ctx: WACCParser.DoWhileContext): DoWhileNode = {
    // ‘do’ ⟨stat⟩ ‘while’ ⟨expr⟩ ‘done’
    val doStat: StatNode = visit(ctx.getChild(1)).asInstanceOf[StatNode]
    val conditionExpr: ExprNode = visit(ctx.getChild(3)).asInstanceOf[ExprNode]

    DoWhileNode(ctx.start, doStat, conditionExpr)
  }

  // FOR LOOP EXTENSION:
  override def visitFor(ctx: WACCParser.ForContext): ForNode = {
    // ‘for’ for_condition 'do' ⟨stat⟩ ‘done’
    val forCondition: ForConditionNode = visit(ctx.getChild(1)).asInstanceOf[ForConditionNode]
    val doStat: StatNode = visit(ctx.getChild(3)).asInstanceOf[StatNode]

    ForNode(ctx.start, forCondition, doStat)
  }

  override def visitFor_condition(ctx: WACCParser.For_conditionContext): ForConditionNode = {
    // Has to have the form (declaration, check, update) = (int i = __; i binOp __; i = __)
    val forDeclaration: DeclarationNode = visit(ctx.getChild(1)).asInstanceOf[DeclarationNode]
    val forExpression: ExprNode = visit(ctx.getChild(3)).asInstanceOf[ExprNode]
    val forAssign: AssignmentNode = visit(ctx.getChild(5)).asInstanceOf[AssignmentNode]

    ForConditionNode(ctx.start, forDeclaration, forExpression, forAssign)
  }

  // BREAK EXTENSION:
  override def visitBreak(ctx: WACCParser.BreakContext): BreakNode = {
    // ‘break'

    BreakNode(ctx.start)
  }

  // CONTINUE EXTENSION:
  override def visitContinue(ctx: WACCParser.ContinueContext): ContinueNode = {
    // ‘continue'

    ContinueNode(ctx.start)
  }

  override def visitBegin(ctx: WACCParser.BeginContext): BeginNode = {
    // ‘begin’ ⟨stat ⟩ ‘end’
    val beginStat: StatNode = visit(ctx.getChild(1)).asInstanceOf[StatNode]

    BeginNode(ctx.start, beginStat)
  }

  override def visitSequence(ctx: WACCParser.SequenceContext): SequenceNode = {
    // ⟨stat ⟩ ‘;’ ⟨stat ⟩
    val statOne: StatNode = visit(ctx.getChild(0)).asInstanceOf[StatNode]
    val statTwo: StatNode = visit(ctx.getChild(2)).asInstanceOf[StatNode]

    SequenceNode(ctx.start, statOne, statTwo)
  }

  // Need to traverse each possible option of assign-lhs
  override def visitAssignLHSIdent(ctx: WACCParser.AssignLHSIdentContext): AssignLHSNode = {
    visit(ctx.getChild(0)).asInstanceOf[IdentNode]
    // This works now
  }

  override def visitAssignLHSArrayElem(ctx: WACCParser.AssignLHSArrayElemContext): AssignLHSNode = {
    visit(ctx.getChild(0)).asInstanceOf[ArrayElemNode]
  }

  override def visitAssignLHSPairElem(ctx: WACCParser.AssignLHSPairElemContext): AssignLHSNode = {
    visit(ctx.getChild(0)).asInstanceOf[PairElemNode]
  }

  override def visitAssignRHSExpr(ctx: WACCParser.AssignRHSExprContext): AssignRHSNode = {
    visit(ctx.getChild(0)).asInstanceOf[ExprNode]
  }

  override def visitAssignRHSLiteral(ctx: WACCParser.AssignRHSLiteralContext): AssignRHSNode = {
    visit(ctx.getChild(0)).asInstanceOf[ArrayLiteralNode]
  }

  override def visitAssignRHSNewPair(ctx: WACCParser.AssignRHSNewPairContext): AssignRHSNode = {
    // ‘newpair’ ‘(’ ⟨expr ⟩ ‘,’ ⟨expr ⟩ ‘)’
    val newPairFst: ExprNode = visit(ctx.getChild(2)).asInstanceOf[ExprNode]
    val newPairSnd: ExprNode = visit(ctx.getChild(4)).asInstanceOf[ExprNode]

    NewPairNode(ctx.start, newPairFst, newPairSnd)
  }

  override def visitAssignRHSPairElem(ctx: WACCParser.AssignRHSPairElemContext): AssignRHSNode = {
    // ⟨pair-elem ⟩
    visit(ctx.getChild(0)).asInstanceOf[PairElemNode]
  }

  override def visitAssignRHSCall(ctx: WACCParser.AssignRHSCallContext): AssignRHSNode = {
    // ‘call’ ⟨ident⟩ ‘(’ ⟨arg-list⟩? ‘)’
    val ident: IdentNode = visit(ctx.getChild(1)).asInstanceOf[IdentNode]
    val argList: Option[ArgListNode] =
      if (ctx.getChildCount() == 5)
        Some(visit(ctx.getChild(3)).asInstanceOf[ArgListNode])
      else None

    CallNode(ctx.start, ident, argList)
  }

  override def visitArg_list(ctx: WACCParser.Arg_listContext): ArgListNode = {
    // ⟨expr ⟩ (‘,’ ⟨expr ⟩ )*
    val childCount = ctx.getChildCount

    val exprChildren: IndexedSeq[ExprNode] =
      for (i <- 0 until childCount; if !ctx.getChild(i).getText.charAt(0).equals(',')) yield
        visit(ctx.getChild(i)).asInstanceOf[ExprNode]

    ArgListNode(ctx.start, exprChildren)
  }

  override def visitPairFst(ctx: WACCParser.PairFstContext): PairElemNode = {
    // ‘fst’ ⟨expr ⟩
    val pairFstExpr: ExprNode = visit(ctx.getChild(1)).asInstanceOf[ExprNode]

    FstNode(ctx.start, pairFstExpr)
  }

  override def visitPairSnd(ctx: WACCParser.PairSndContext): PairElemNode = {
    // ‘snd’ ⟨expr ⟩
    val pairSndExpr: ExprNode = visit(ctx.getChild(1)).asInstanceOf[ExprNode]

    SndNode(ctx.start, pairSndExpr)
  }

  override def visitTypeBase_type(ctx: WACCParser.TypeBase_typeContext): TypeNode = {
    // ⟨base-type⟩
    visit(ctx.getChild(0)).asInstanceOf[BaseTypeNode]
  }

  override def visitTypeArray_type(ctx: WACCParser.TypeArray_typeContext): TypeNode = {
    // ⟨array-type⟩
    val arrayType: TypeNode = visit(ctx.getChild(0)).asInstanceOf[TypeNode]

    ArrayTypeNode(ctx.start, arrayType)
  }

  override def visitTypePair_type(ctx: WACCParser.TypePair_typeContext): TypeNode = {
    // ⟨pair-type⟩
    visit(ctx.getChild(0)).asInstanceOf[PairTypeNode]
  }

  override def visitIntBase_type(ctx: WACCParser.IntBase_typeContext): BaseTypeNode = {
    // 'int'
    IntTypeNode(ctx.start)
  }

  override def visitBoolBase_type(ctx: WACCParser.BoolBase_typeContext): BaseTypeNode = {
    // 'bool'
    BoolTypeNode(ctx.start)
  }

  override def visitCharBase_type(ctx: WACCParser.CharBase_typeContext): BaseTypeNode = {
    // 'char'
    CharTypeNode(ctx.start)
  }

  override def visitStringBase_type(ctx: WACCParser.StringBase_typeContext): BaseTypeNode = {
    // 'string'
    StringTypeNode(ctx.start)
  }

  override def visitArray_type(ctx: WACCParser.Array_typeContext): ArrayTypeNode = {
    // ⟨type ⟩ ‘[’ ‘]’
    val arrayType: TypeNode = visit(ctx.getChild(0)).asInstanceOf[TypeNode]

    ArrayTypeNode(ctx.start, arrayType)
  }

  override def visitPair_type(ctx: WACCParser.Pair_typeContext): PairTypeNode = {
    // ‘pair’ ‘(’ ⟨pair-elem-type ⟩ ‘,’ ⟨pair-elem-type ⟩ ‘)’

    val fstPairElem: PairElemTypeNode = visit(ctx.getChild(2)).asInstanceOf[PairElemTypeNode]
    val sndPairElem: PairElemTypeNode = visit(ctx.getChild(4)).asInstanceOf[PairElemTypeNode]

    PairTypeNode(ctx.start, fstPairElem, sndPairElem)
  }

  override def visitPETBaseType(ctx: WACCParser.PETBaseTypeContext): PairElemTypeNode = {
    // ⟨base-type⟩
    visit(ctx.getChild(0)).asInstanceOf[BaseTypeNode]
  }

  override def visitPETArrayType(ctx: WACCParser.PETArrayTypeContext): PairElemTypeNode = {
    // ⟨array-type⟩
    visit(ctx.getChild(0)).asInstanceOf[ArrayTypeNode]
  }

  // I doubt one is needed for the ⟨pair-elem-type⟩ 'pair'. Surely just a string?
  override def visitPETPair(ctx: WACCParser.PETPairContext): PairElemTypeNode = {
    // 'pair'
    new PairElemTypePairNode(ctx.start)
  }

  override def visitExprIntLiter(ctx: WACCParser.ExprIntLiterContext): ExprNode = {
    // ⟨int-liter⟩
    visit(ctx.getChild(0)).asInstanceOf[Int_literNode]
  }

  override def visitInt_liter(ctx: WACCParser.Int_literContext): Int_literNode = {
    val childCount = ctx.getChildCount

    // childCount - 1 in case it is signed +
    var num: String = ctx.getChild(childCount - 1).getText

    // check if it's negated
    // can't rely on child 0 as antlr prefers longest token sequence
    // and will prefer unary ops over signs (we need this behaviour as well)
    // and so we simply check if the parent is a unary operator of '-'
    val negated: Boolean = ctx.getParent().getParent() match {
      case parent: WACCParser.ExprUnaryOperContext =>
        parent.getChild(0).getText == "-"
      case _ => false
    }
    if (negated) num = "-" + num

    // parse the number -- if there's an overflow it'll be set to None
    val numVal: Option[Int] = num.toIntOption

    numVal match {
      case None =>
        SyntaxErrorLog.add("Invalid integer value.")
        Int_literNode(ctx.start, "")
      case Some(n) => Int_literNode(ctx.start, num)
    }

  }

  override def visitExprBoolLiter(ctx: WACCParser.ExprBoolLiterContext): ExprNode = {
    // ⟨bool-liter⟩
    visit(ctx.getChild(0)).asInstanceOf[Bool_literNode]
  }

  override def visitBool_liter(ctx: WACCParser.Bool_literContext): Bool_literNode = {
    // ‘true’ | ‘false’
    val boolValue: Boolean = ctx.getChild(0).getText.toBoolean

    Bool_literNode(ctx.start, boolValue)
  }

  override def visitExprCharLiter(ctx: WACCParser.ExprCharLiterContext): ExprNode = {
    // ⟨char-liter⟩
    visit(ctx.getChild(0)).asInstanceOf[Char_literNode]
  }

  override def visitChar_liter(ctx: WACCParser.Char_literContext): Char_literNode = {
    // ‘’’ ⟨character ⟩ ‘’’
    val charValue: Char = ctx.getChild(0).getText.charAt(1)
    if (charValue == '\\') Char_literNode(ctx.start, ctx.getChild(0).getText.charAt(2))
    else Char_literNode(ctx.start, charValue)
  }

  override def visitExprStringLiter(ctx: WACCParser.ExprStringLiterContext): ExprNode = {
    // ⟨string-liter⟩
    visit(ctx.getChild(0)).asInstanceOf[Str_literNode]
  }

  override def visitStr_liter(ctx: WACCParser.Str_literContext): Str_literNode = {
    // ‘"’ ⟨character⟩* ‘"’
    val str: String = ctx.getChild(0).getText

    Str_literNode(ctx.start, str)
  }

  override def visitExprPairLiter(ctx: WACCParser.ExprPairLiterContext): ExprNode = {
    // ⟨pair-liter⟩
    visit(ctx.getChild(0))
    Pair_literNode(ctx.start)
  }

  override def visitPair_liter(ctx: WACCParser.Pair_literContext): Pair_literNode =
    Pair_literNode(ctx.start)

  override def visitExprIdent(ctx: WACCParser.ExprIdentContext): ExprNode = {
    // ⟨ident⟩
    visit(ctx.getChild(0)).asInstanceOf[IdentNode]
  }

  override def visitExprArrayElem(ctx: WACCParser.ExprArrayElemContext): ExprNode = {
    // ⟨array-elem⟩
    visit(ctx.getChild(0)).asInstanceOf[ArrayElemNode]
  }

  override def visitExprUnaryOper(ctx: WACCParser.ExprUnaryOperContext): ExprNode = {
    // ⟨unary-oper⟩ ⟨expr⟩
    val unaryOperator: String = ctx.getChild(0).getText
    val expr: ExprNode = visit(ctx.getChild(1)).asInstanceOf[ExprNode]

    // Now I need to figure out which unary operator it is from the context
    // then construct the relevant node.

    unaryOperator match {
      case "!"   => LogicalNotNode(ctx.start, expr)
      case "-"   => NegateNode(ctx.start, expr)
      case "len" => LenNode(ctx.start, expr)
      case "ord" => OrdNode(ctx.start, expr)
      case "chr" => ChrNode(ctx.start, expr)
    }
  }

  override def visitExprBinaryMulDivModOp(ctx: WACCParser.ExprBinaryMulDivModOpContext): ExprNode = {
    val firstExpr: ExprNode = visit(ctx.getChild(0)).asInstanceOf[ExprNode]
    val binaryOperator: String = ctx.getChild(1).getText
    val secondExpr: ExprNode = visit(ctx.getChild(2)).asInstanceOf[ExprNode]

    binaryOperator match {
      case "*" => MultiplyNode(ctx.start, firstExpr, secondExpr)
      case "/" => DivideNode(ctx.start, firstExpr, secondExpr)
      case "%" => ModNode(ctx.start, firstExpr, secondExpr)
    }
  }

  override def visitExprBinaryAddSupOp(ctx: WACCParser.ExprBinaryAddSupOpContext): ExprNode = {
    val firstExpr: ExprNode = visit(ctx.getChild(0)).asInstanceOf[ExprNode]
    val binaryOperator: String = ctx.getChild(1).getText
    val secondExpr: ExprNode = visit(ctx.getChild(2)).asInstanceOf[ExprNode]

    binaryOperator match {
      case "+"  => PlusNode(ctx.start, firstExpr, secondExpr)
      case "-"  => MinusNode(ctx.start, firstExpr, secondExpr)
    }
  }

  override def visitExprBinaryComparatorOp(ctx: WACCParser.ExprBinaryComparatorOpContext): ExprNode = {
    val firstExpr: ExprNode = visit(ctx.getChild(0)).asInstanceOf[ExprNode]
    val binaryOperator: String = ctx.getChild(1).getText
    val secondExpr: ExprNode = visit(ctx.getChild(2)).asInstanceOf[ExprNode]

    binaryOperator match {
      case ">"  => GreaterThanNode(ctx.start, firstExpr, secondExpr)
      case ">=" => GreaterEqualNode(ctx.start, firstExpr, secondExpr)
      case "<"  => LessThanNode(ctx.start, firstExpr, secondExpr)
      case "<=" => LessEqualNode(ctx.start, firstExpr, secondExpr)
    }
  }

  override def visitExprBinaryEqualityOp(ctx: WACCParser.ExprBinaryEqualityOpContext): ExprNode = {
    val firstExpr: ExprNode = visit(ctx.getChild(0)).asInstanceOf[ExprNode]
    val binaryOperator: String = ctx.getChild(1).getText
    val secondExpr: ExprNode = visit(ctx.getChild(2)).asInstanceOf[ExprNode]

    binaryOperator match {
      case "==" => EqualToNode(ctx.start, firstExpr, secondExpr)
      case "!=" => NotEqualNode(ctx.start, firstExpr, secondExpr)
    }
  }

  override def visitExprBinaryLogicalAndOp(ctx: WACCParser.ExprBinaryLogicalAndOpContext): ExprNode = {
    val firstExpr: ExprNode = visit(ctx.getChild(0)).asInstanceOf[ExprNode]
    val binaryOperator: String = ctx.getChild(1).getText
    val secondExpr: ExprNode = visit(ctx.getChild(2)).asInstanceOf[ExprNode]

    binaryOperator match {
      case "&&" => LogicalAndNode(ctx.start, firstExpr, secondExpr)
    }
  }

  override def visitExprBinaryLogicalOrOp(ctx: WACCParser.ExprBinaryLogicalOrOpContext): ExprNode = {
    val firstExpr: ExprNode = visit(ctx.getChild(0)).asInstanceOf[ExprNode]
    val binaryOperator: String = ctx.getChild(1).getText
    val secondExpr: ExprNode = visit(ctx.getChild(2)).asInstanceOf[ExprNode]

    binaryOperator match {
      case "||" => LogicalOrNode(ctx.start, firstExpr, secondExpr)
    }
  }

  override def visitBracketExpr(ctx: WACCParser.BracketExprContext): ExprNode = {
    // ‘(’ ⟨expr⟩ ‘)’
    visit(ctx.getChild(1)).asInstanceOf[ExprNode]
  }

  override def visitIdent(ctx: WACCParser.IdentContext): IdentNode = {
    val string: String = ctx.getText

    IdentNode(ctx.start, string)
  }

  override def visitArray_elem(ctx: WACCParser.Array_elemContext): ArrayElemNode = {
    // ⟨ident⟩ (‘[’ ⟨expr⟩ ‘]’)+
    val ident: IdentNode = visit(ctx.getChild(0)).asInstanceOf[IdentNode]
    val childCount = ctx.getChildCount

    // i [ e ] [ e ] ...
    // 0 1 2 3 4 5 6...
    // first expr has index 2
    // index of next expr = index of previous expr + 3
    val exprList: IndexedSeq[ExprNode] =
      for (i <- 2 until childCount by 3) yield visit(ctx.getChild(i)).asInstanceOf[ExprNode]

    ArrayElemNode(ctx.start, ident, exprList)
  }

  override def visitArray_liter(ctx: WACCParser.Array_literContext): ArrayLiteralNode = {
    // ‘[’ ( ⟨expr⟩ (‘,’ ⟨expr⟩)* )? ‘]’
    val childCount = ctx.getChildCount

    val exprList: IndexedSeq[ExprNode] =
        for (i <- 1 until childCount - 1; if !ctx.getChild(i).getText.charAt(0).equals(',')) yield
          visit(ctx.getChild(i)).asInstanceOf[ExprNode]

    ArrayLiteralNode(ctx.start, exprList)
  }

}
