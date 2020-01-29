import antlr.{WACCLexer, WACCParser, WACCParserBaseVisitor}
import org.antlr.v4.runtime._

// Class used to traverse the parse tree built by ANTLR
class ASTGenerator extends WACCParserBaseVisitor[ASTNode] {

  override def visitProgram(ctx: WACCParser.ProgramContext): ASTNode = {
    // Need to retrieve program information from parser context,
    // Need to visit all the functions (as many as there are due to the *)
    // Need to visit the statement "node".
    // ‘begin’ ⟨func⟩* ⟨stat⟩ ‘end’
    //    0     1-n     n+1    n+2
    val childCount = ctx.getChildCount
    val functions: IndexedSeq[FuncNode] = IndexedSeq[FuncNode]()
    val stat: StatNode = visit(ctx.getChild(childCount - 2)).asInstanceOf[StatNode]

    for (i <- 1 to childCount - 2) {
      functions :+ visit(ctx.getChild(i)).asInstanceOf[FuncNode]
    }

    // Then create program node from the two
    new ProgramNode(stat, functions)
  }

  override def visitFunc(ctx: WACCParser.FuncContext): ASTNode = {
    // ⟨type⟩ ⟨ident⟩ ‘(’ ⟨param-list⟩? ‘)’ ‘is’ ⟨stat⟩ ‘end’
    val funcType: TypeNode = visit(ctx.getChild(0)).asInstanceOf[TypeNode]
    val ident: IdentNode = visit(ctx.getChild(1)).asInstanceOf[IdentNode]
    // Needs to be optional... so either an empty list or populated.
    val paramList: ParamListNode = visit(ctx.getChild(3)).asInstanceOf[ParamListNode]
    val statement: StatNode = visit(ctx.getChild(6)).asInstanceOf[StatNode]

    new FuncNode(funcType, ident, paramList, statement)
  }

  override def visitParam_list(ctx: WACCParser.Param_listContext): ASTNode = {
    // ⟨param⟩ ( ‘,’ ⟨param⟩ )*
    // Multiple params... a param list
    val childCount = ctx.getChildCount
    // Apparently IndexedSeq is much better than an array so I'll use it instead
    val paramList: IndexedSeq[ParamNode] = IndexedSeq[ParamNode]()

    // Hope this is how you do it.
    // Now takes into account the comma.
    for (i <- 0 until childCount) {
      if (!ctx.getChild(i).getText.charAt(0).equals(',')) {
        paramList :+ visit(ctx.getChild(i)).asInstanceOf[ParamNode]
      }
    }

    new ParamListNode(paramList)
  }

  override def visitParam(ctx: WACCParser.ParamContext): ASTNode = {
    // ⟨type⟩ ⟨ident⟩
    val paramType: TypeNode = visit(ctx.getChild(0)).asInstanceOf[TypeNode]
    val ident: IdentNode = visit(ctx.getChild(1)).asInstanceOf[IdentNode]

    new ParamNode(paramType, ident)
  }

//  override def visitStat(ctx: WACCParser.StatContext): ASTNode = {
//
//  }

  // Individual components of stat that must be visited.
  override def visitSkip(ctx: WACCParser.SkipContext): ASTNode = {
    println("Skip stat.")
    // Or something.
    new SkipNode()
  }

  override def visitDeclaration(ctx: WACCParser.DeclarationContext): ASTNode = {
    // Need to get each individual child of the declaration context as its own type.
    val typeNode: TypeNode = visit(ctx.getChild(0)).asInstanceOf[TypeNode]
    val ident: IdentNode = visit(ctx.getChild(1)).asInstanceOf[IdentNode]
    val assignRHS: AssignRHSNode = visit(ctx.getChild(3)).asInstanceOf[AssignRHSNode]

    // Then create DeclarationNode with the above as parameters for constructor
    new DeclarationNode(typeNode, ident, assignRHS)
  }

  override def visitAssignment(ctx: WACCParser.AssignmentContext): ASTNode = {
    // Get assignLHS and assignRHS then construct AssignNode with them
    val lhs: AssignLHSNode = visit(ctx.getChild(0)).asInstanceOf[AssignLHSNode]
    // Have to consider the equals
    val rhs: AssignRHSNode = visit(ctx.getChild(2)).asInstanceOf[AssignRHSNode]

    new AssignmentNode(lhs, rhs)
  }

  override def visitRead(ctx: WACCParser.ReadContext): ASTNode = {
    // ‘read’ ⟨assign-lhs⟩
    val lhs: AssignLHSNode = visit(ctx.getChild(1)).asInstanceOf[AssignLHSNode]

    new ReadNode(lhs)
  }

  override def visitFree(ctx: WACCParser.FreeContext): ASTNode = {
    // ‘free’ ⟨expr⟩
    val freeExpr: ExprNode = visit(ctx.getChild(1)).asInstanceOf[ExprNode]

    new FreeNode(freeExpr)
  }

  override def visitReturn(ctx: WACCParser.ReturnContext): ASTNode = {
    // ‘return’ ⟨expr ⟩
    val returnExpr: ExprNode = visit(ctx.getChild(1)).asInstanceOf[ExprNode]

    new ReturnNode(returnExpr)
  }

  override def visitExit(ctx: WACCParser.ExitContext): ASTNode = {
    //  ‘exit’ ⟨expr ⟩
    val exitExpr: ExprNode = visit(ctx.getChild(1)).asInstanceOf[ExprNode]

    new ExitNode(exitExpr)
  }

  override def visitPrint(ctx: WACCParser.PrintContext): ASTNode = {
    // ‘print’ ⟨expr ⟩
    val printExpr: ExprNode = visit(ctx.getChild(1)).asInstanceOf[ExprNode]

    new PrintNode(printExpr)
  }

  override def visitPrintln(ctx: WACCParser.PrintlnContext): ASTNode = {
    // ‘println’ ⟨expr ⟩
    val printlnExpr: ExprNode = visit(ctx.getChild(1)).asInstanceOf[ExprNode]

    new PrintlnNode(printlnExpr)
  }

  override def visitIf(ctx: WACCParser.IfContext): ASTNode = {
  // ‘if’ ⟨expr ⟩ ‘then’ ⟨stat ⟩ ‘else’ ⟨stat ⟩ ‘fi’
    val conditionExpr: ExprNode = visit(ctx.getChild(1)).asInstanceOf[ExprNode]
    val thenStat: StatNode = visit(ctx.getChild(3)).asInstanceOf[StatNode]
    val elseStat: StatNode = visit(ctx.getChild(5)).asInstanceOf[StatNode]

    new IfNode(conditionExpr, thenStat, elseStat)
  }

  override def visitWhile(ctx: WACCParser.WhileContext): ASTNode = {
    // ‘while’ ⟨expr ⟩ ‘do’ ⟨stat ⟩ ‘done’
    val conditionExpr: ExprNode = visit(ctx.getChild(1)).asInstanceOf[ExprNode]
    val doStat: StatNode = visit(ctx.getChild(3)).asInstanceOf[StatNode]

    new WhileNode(conditionExpr, doStat)
  }

  override def visitBegin(ctx: WACCParser.BeginContext): ASTNode = {
    // ‘begin’ ⟨stat ⟩ ‘end’
    val beginStat: StatNode = visit(ctx.getChild(1)).asInstanceOf[StatNode]

    new BeginNode(beginStat)
  }

  override def visitSequence(ctx: WACCParser.SequenceContext): ASTNode = {
    // ⟨stat ⟩ ‘;’ ⟨stat ⟩
    val statOne: StatNode = visit(ctx.getChild(0)).asInstanceOf[StatNode]
    val statTwo: StatNode = visit(ctx.getChild(2)).asInstanceOf[StatNode]

    new SequenceNode(statOne, statTwo)
  }

  // Need to traverse each possible option of assign-lhs
//  override def visitAssign_lhs(ctx: WACCParser.Assign_lhsContext): ASTNode = {
//
//  }

  override def visitAssignLHSIdent(ctx: WACCParser.AssignLHSIdentContext): ASTNode = {
    visit(ctx.getChild(0)).asInstanceOf[IdentNode]
    // This works now
  }

  override def visitAssignLHSArrayElem(ctx: WACCParser.AssignLHSArrayElemContext): ASTNode = {
    visit(ctx.getChild(0)).asInstanceOf[ArrayElemNode]
  }

  override def visitAssignLHSPairElem(ctx: WACCParser.AssignLHSPairElemContext): ASTNode = {
    visit(ctx.getChild(0)).asInstanceOf[PairElemNode]
  }

//  override def visitAssign_rhs(ctx: WACCParser.Assign_rhsContext): ASTNode = {
//
//  }

  override def visitAssignRHSExpr(ctx: WACCParser.AssignRHSExprContext): ASTNode = {
    visit(ctx.getChild(0)).asInstanceOf[ExprNode]
  }

  override def visitAssignRHSLiteral(ctx: WACCParser.AssignRHSLiteralContext): ASTNode = {
    visit(ctx.getChild(0)).asInstanceOf[ArrayLiteralNode]
  }

  override def visitAssignRHSNewPair(ctx: WACCParser.AssignRHSNewPairContext): ASTNode = {
    // ‘newpair’ ‘(’ ⟨expr ⟩ ‘,’ ⟨expr ⟩ ‘)’
    val newPairFst: ExprNode = visit(ctx.getChild(2)).asInstanceOf[ExprNode]
    val newPairSnd: ExprNode = visit(ctx.getChild(4)).asInstanceOf[ExprNode]

    new NewPairNode(newPairFst, newPairSnd)
  }

  override def visitAssignRHSPairElem(ctx: WACCParser.AssignRHSPairElemContext): ASTNode = {
    // ⟨pair-elem ⟩
    visit(ctx.getChild(0)).asInstanceOf[PairElemNode]
  }

  override def visitAssignRHSCall(ctx: WACCParser.AssignRHSCallContext): ASTNode = {
    // ‘call’ ⟨ident⟩ ‘(’ ⟨arg-list⟩? ‘)’
    val ident: IdentNode = visit(ctx.getChild(1)).asInstanceOf[IdentNode]
    // Have to make this optional.
    // Can tell if arglist is present by number of children present.
    if (ctx.getChildCount() == 5) {
      val argList: ArgListNode = visit(ctx.getChild(3)).asInstanceOf[ArgListNode]
      new CallNode(ident, argList)
    } else {
      new CallNode(ident, None.asInstanceOf[ArgListNode])
    }
  }
  
  override def visitArg_list(ctx: WACCParser.Arg_listContext): ASTNode = {
    // ⟨expr ⟩ (‘,’ ⟨expr ⟩ )*
    val childCount = ctx.getChildCount

    val exprChildren: IndexedSeq[ExprNode] = IndexedSeq[ExprNode]()

    // Change this to account for the comma...
    for (i <- 0 to childCount - 2) {
      exprChildren :+ visit(ctx.getChild(i)).asInstanceOf[ExprNode]
    }

    new ArgListNode(exprChildren)
  }

//  override def visitPair_elem(ctx: WACCParser.Pair_elemContext): ASTNode = {
//
//  }

  override def visitPairFst(ctx: WACCParser.PairFstContext): ASTNode = {
    // ‘fst’ ⟨expr ⟩
    val pairFstExpr: ExprNode = visit(ctx.getChild(1)).asInstanceOf[ExprNode]

    new FstNode(pairFstExpr)
  }

  override def visitPairSnd(ctx: WACCParser.PairSndContext): ASTNode = {
    // ‘snd’ ⟨expr ⟩
    val pairSndExpr: ExprNode = visit(ctx.getChild(1)).asInstanceOf[ExprNode]

    new SndNode(pairSndExpr)
  }

//  override def visitType(ctx: WACCParser.TypeContext): ASTNode = {
//    // ⟨type⟩
//  }

  override def visitTypeBase_type(ctx: WACCParser.TypeBase_typeContext): ASTNode = {
    // ⟨base-type⟩
    visit(ctx.getChild(0)).asInstanceOf[BaseTypeNode]
  }

  override def visitTypeArray_type(ctx: WACCParser.TypeArray_typeContext): ASTNode = {
    // ⟨array-type⟩
    visit(ctx.getChild(0)).asInstanceOf[ArrayTypeNode]
  }

  override def visitTypePair_type(ctx: WACCParser.TypePair_typeContext): ASTNode = {
    // ⟨pair-type⟩
    visit(ctx.getChild(0)).asInstanceOf[PairTypeNode]
  }

//  override def visitBase_type(ctx: WACCParser.Base_typeContext): ASTNode = {
//
//  }

  override def visitIntBase_type(ctx: WACCParser.IntBase_typeContext): ASTNode = {
    // 'int'
    new IntTypeNode()
  }

  override def visitBoolBase_type(ctx: WACCParser.BoolBase_typeContext): ASTNode = {
    // 'bool'
    new BoolTypeNode()
  }

  override def visitCharBase_type(ctx: WACCParser.CharBase_typeContext): ASTNode = {
    // 'char'
    new CharTypeNode()
  }

  override def visitStringBase_type(ctx: WACCParser.StringBase_typeContext): ASTNode = {
    // 'string'
    new StringTypeNode()
  }

  override def visitArray_type(ctx: WACCParser.Array_typeContext): ASTNode = {
    // ⟨type ⟩ ‘[’ ‘]’
    val arrayType: TypeNode = visit(ctx.getChild(0)).asInstanceOf[TypeNode]

    new ArrayTypeNode(arrayType)
  }

  override def visitPair_type(ctx: WACCParser.Pair_typeContext): ASTNode = {
    // ‘pair’ ‘(’ ⟨pair-elem-type ⟩ ‘,’ ⟨pair-elem-type ⟩ ‘)’

    val fstPairElem: PairElemTypeNode = visit(ctx.getChild(2)).asInstanceOf[PairElemTypeNode]
    val sndPairElem: PairElemTypeNode = visit(ctx.getChild(4)).asInstanceOf[PairElemTypeNode]

    new PairTypeNode(fstPairElem, sndPairElem)
  }

//  override def visitPair_elem_type(ctx: WACCParser.Pair_elemContext): ASTNode = {
//    // ⟨pair-elem-type⟩
//  }

  override def visitPETBaseType(ctx: WACCParser.PETBaseTypeContext): ASTNode = {
    // ⟨base-type⟩
    visit(ctx.getChild(0)).asInstanceOf[BaseTypeNode]
  }

  override def visitPETArrayType(ctx: WACCParser.PETArrayTypeContext): ASTNode = {
    // ⟨array-type⟩
    visit(ctx.getChild(0)).asInstanceOf[ArrayTypeNode]
  }

  // I doubt one is needed for the ⟨pair-elem-type⟩ 'pair'. Surely just a string?
  override def visitPETPair(ctx: WACCParser.PETPairContext): ASTNode = {
    // 'pair'
    new PairElemTypePairNode()
  }

//  override def visitExpr(ctx: WACCParser.ExprContext): ASTNode = {
//
//  }

  override def visitExprIntLiter(ctx: WACCParser.ExprIntLiterContext): ASTNode = {
    // ⟨int-liter⟩
    visit(ctx.getChild(0)).asInstanceOf[Int_literNode]
  }

  override def visitInt_liter(ctx: WACCParser.Int_literContext): ASTNode = {
    // ⟨int-sign⟩? ⟨digit⟩+
    val childCount = ctx.getChildCount
    var intSign: Char = None.asInstanceOf[Char]
    // Need to create node for sign, needs to be optional.
    if (ctx.getChildCount() == 2) {
      intSign = ctx.getChild(0).getText.charAt(0)
    }

    // Need to create list of digitNodes, or just chars...
//    val digits: IndexedSeq[DigitNode] = IndexedSeq[DigitNode]()
    val digits: IndexedSeq[Int] = IndexedSeq[Int]()


    for (i <- 1 to childCount - 1) {
      digits :+ visit(ctx.getChild(i))
    }

    new Int_literNode(intSign, digits)
  }

  override def visitExprBoolLiter(ctx: WACCParser.ExprBoolLiterContext): ASTNode = {
    // ⟨bool-liter⟩
    visit(ctx.getChild(0)).asInstanceOf[Bool_literNode]
  }

  override def visitBool_liter(ctx: WACCParser.Bool_literContext): ASTNode = {
    // ‘true’ | ‘false’
    val boolValue: Boolean = ctx.getChild(0).getText.toBoolean

    new Bool_literNode(boolValue)
  }

  override def visitExprCharLiter(ctx: WACCParser.ExprCharLiterContext): ASTNode = {
    // ⟨char-liter⟩
    visit(ctx.getChild(0)).asInstanceOf[Char_literNode]
  }

  override def visitChar_liter(ctx: WACCParser.Char_literContext): ASTNode = {
    // ‘’’ ⟨character ⟩ ‘’’
    val charValue: Char = ctx.getChild(1).getText.charAt(0)

    new Char_literNode(charValue)
  }

  override def visitExprStringLiter(ctx: WACCParser.ExprStringLiterContext): ASTNode = {
    // ⟨string-liter⟩
    visit(ctx.getChild(0)).asInstanceOf[Str_literNode]
  }

  override def visitStr_liter(ctx: WACCParser.Str_literContext): ASTNode = {
    // ‘"’ ⟨character⟩* ‘"’
    val childCount = ctx.getChildCount
    val charList: IndexedSeq[Char] = IndexedSeq[Char]()

    for (i <- 1 to childCount - 2) {
      charList :+ visit(ctx.getChild(i))
    }

    new Str_literNode(charList)
  }

  override def visitExprPairLiter(ctx: WACCParser.ExprPairLiterContext): ASTNode = {
    // ⟨pair-liter⟩
    visit(ctx.getChild(0)).asInstanceOf[Pair_literNode]
  }

  override def visitExprIdent(ctx: WACCParser.ExprIdentContext): ASTNode = {
    // ⟨ident⟩
    visit(ctx.getChild(0)).asInstanceOf[IdentNode]
  }

  override def visitExprArrayElem(ctx: WACCParser.ExprArrayElemContext): ASTNode = {
    // ⟨array-elem⟩
    visit(ctx.getChild(0)).asInstanceOf[ArrayElemNode]
  }

//  override def visitUnary_oper(ctx: WACCParser.Unary_operContext): ASTNode = {
//    // ⟨unary-oper⟩ ⟨expr⟩
//    val unaryOperator: Char = ctx.getChild(0).getText.charAt(0)
//    val expr: ExprNode = visit(ctx.getChild(1)).asInstanceOf[ExprNode]
//
//    // Now I need to figure out which unary operator it is from the context
//    // then construct the relevant node.
//  }
//
//  override def visitBinary_oper(ctx: WACCParser.Binary_operContext): ASTNode = {
//    // ⟨expr⟩ ⟨binary-oper⟩ ⟨expr⟩
//    val firstExpr: ExprNode = visit(ctx.getChild(0)).asInstanceOf[ExprNode]
//    val binaryOperator: String = ctx.getChild(1).getText
//    val secondExpr: ExprNode = visit(ctx.getChild(2)).asInstanceOf[ExprNode]
//
//    // Need to deduce binary operator from context and construct node.
//  }

  override def visitBracketExpr(ctx: WACCParser.BracketExprContext): ASTNode = {
    // ‘(’ ⟨expr⟩ ‘)’
    visit(ctx.getChild(1)).asInstanceOf[ExprNode]
  }

  override def visitIdent(ctx: WACCParser.IdentContext): ASTNode = {
    val string: String = ctx.getText

    new IdentNode(string)
  }

  override def visitArray_elem(ctx: WACCParser.Array_elemContext): ASTNode = {
    // ⟨ident⟩ (‘[’ ⟨expr⟩ ‘]’)+
    val ident: IdentNode = visit(ctx.getChild(0)).asInstanceOf[IdentNode]
    val childCount = ctx.getChildCount
    val exprList: IndexedSeq[ExprNode] = IndexedSeq[ExprNode]()

    // To get every expr in the exprList
    for (i <- 2 to childCount - 2) {
      exprList :+ visit(ctx.getChild(i)).asInstanceOf[ExprNode]
    }

    new ArrayElemNode(ident, exprList)
  }

  override def visitArray_liter(ctx: WACCParser.Array_literContext): ASTNode = {
    // ‘[’ ( ⟨expr⟩ (‘,’ ⟨expr⟩)* )? ‘]’
    val childCount = ctx.getChildCount
    val exprList: IndexedSeq[ExprNode] = IndexedSeq[ExprNode]()

    // To get every expr in the exprList but I don't think it works here because
    // it would be separated by commas, need to get every next expr after comma.
    for (i <- 0 to childCount - 2) {
      exprList :+ visit(ctx.getChild(i)).asInstanceOf[ExprNode]
    }

    new ArrayLiteralNode(exprList)
  }

}